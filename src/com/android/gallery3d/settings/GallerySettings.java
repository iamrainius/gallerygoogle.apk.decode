package com.android.gallery3d.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.app.LicensesActivity;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import com.google.android.picasasync.PicasaFacade;

public class GallerySettings extends PreferenceActivity
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener
{
  private static final String[] PROJECTION_SETTINGS = { "sync_picasa_on_wifi_only" };
  private final long DELAY_SHOW_PROGRESS = 300L;
  private final int MSG_DISMISS_PROGRESS_DIALOG = 2;
  private final int MSG_SHOW_PROGRESS_DIALOG = 1;
  private final int MSG_UPDATE_ACCOUNTS = 3;
  private PicasaFacade mFacade;
  private Handler mHandler;
  private boolean mIsResumed = false;
  private Future<Void> mLoadAccountTask;
  private CheckBoxPreference mPrefsSyncOnWifiOnly;
  private ProgressDialog mProgressDialog;

  public static void addAccount(Activity paramActivity, boolean paramBoolean)
  {
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(paramActivity);
    if ((paramBoolean) && (((AccountManager.get(paramActivity).getAccountsByType("com.google").length > 0) || (!localSharedPreferences.getBoolean("showSigninReminder", true)))))
      return;
    Bundle localBundle = new Bundle();
    localBundle.putCharSequence("introMessage", paramActivity.getString(2131362077));
    localBundle.putBoolean("allowSkip", true);
    AccountManager.get(paramActivity).addAccount("com.google", "service_lh2", null, localBundle, paramActivity, new AccountManagerCallback(localSharedPreferences)
    {
      public void run(AccountManagerFuture<Bundle> paramAccountManagerFuture)
      {
        if (paramAccountManagerFuture.isCancelled())
          return;
        try
        {
          if (((Bundle)paramAccountManagerFuture.getResult()).getBoolean("setupSkipped"));
          this.val$prefs.edit().putBoolean("showSigninReminder", false).commit();
          return;
        }
        catch (Throwable localThrowable)
        {
          Log.w("GallerySettings", "fail to add acount", localThrowable);
        }
      }
    }
    , null);
  }

  private void changePicasaSyncSettings(ContentValues paramContentValues)
  {
    getContentResolver().update(this.mFacade.getSettingsUri(), paramContentValues, null, null);
  }

  private static Account[] getGoogleAccounts(ThreadPool.JobContext paramJobContext, Context paramContext)
  {
    try
    {
      Account[] arrayOfAccount = AccountManager.get(paramContext).getAccountsByType("com.google");
      return arrayOfAccount;
    }
    catch (Exception localException)
    {
      Log.e("GallerySettings", "cannot get accounts", localException);
    }
    return new Account[0];
  }

  private void updateAccountInfo(Account[] paramArrayOfAccount)
  {
    if (!this.mIsResumed)
      return;
    if (this.mProgressDialog != null)
    {
      this.mProgressDialog.dismiss();
      this.mProgressDialog = null;
    }
    this.mHandler.removeMessages(1);
    PreferenceGroup localPreferenceGroup = (PreferenceGroup)findPreference("prefs_account_settings");
    localPreferenceGroup.removeAll();
    localPreferenceGroup.setEnabled(true);
    String str = this.mFacade.getAuthority();
    Intent localIntent = new Intent("android.settings.SYNC_SETTINGS");
    localIntent.putExtra("authorities", new String[] { str });
    boolean bool1 = ContentResolver.getMasterSyncAutomatically();
    int i = paramArrayOfAccount.length;
    int j = 0;
    if (j < i)
    {
      label101: Account localAccount = paramArrayOfAccount[j];
      AccountHeaderPreference localAccountHeaderPreference = new AccountHeaderPreference(this);
      localAccountHeaderPreference.setAccountName(localAccount.name);
      localPreferenceGroup.addPreference(localAccountHeaderPreference);
      AccountSyncPreference localAccountSyncPreference = new AccountSyncPreference(this);
      if ((bool1) && (ContentResolver.getSyncAutomatically(localAccount, str)));
      for (boolean bool4 = true; ; bool4 = false)
      {
        localAccountSyncPreference.setSyncEnabled(bool4);
        localAccountSyncPreference.setIntent(localIntent);
        localPreferenceGroup.addPreference(localAccountSyncPreference);
        ++j;
        break label101:
      }
    }
    Cursor localCursor = getContentResolver().query(this.mFacade.getSettingsUri(), PROJECTION_SETTINGS, null, null, null);
    if (localCursor != null);
    boolean bool3;
    try
    {
      if (localCursor.moveToNext());
      for (boolean bool2 = true; ; bool2 = false)
      {
        Utils.assertTrue(bool2);
        if (localCursor.getInt(0) == 0)
          break;
        bool3 = true;
        this.mPrefsSyncOnWifiOnly.setChecked(bool3);
        return;
      }
    }
    finally
    {
      localCursor.close();
    }
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    getActionBar().setDisplayOptions(4, 4);
    this.mFacade = PicasaFacade.get(this);
    addPreferencesFromResource(2131165186);
    this.mPrefsSyncOnWifiOnly = ((CheckBoxPreference)findPreference("prefs_sync_on_wifi_only"));
    this.mPrefsSyncOnWifiOnly.setOnPreferenceChangeListener(this);
    findPreference("prefs_open_source_licenses").setOnPreferenceClickListener(this);
    this.mHandler = new Handler()
    {
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default:
          throw new IllegalArgumentException();
        case 1:
          GallerySettings.access$002(GallerySettings.this, ProgressDialog.show(GallerySettings.this, GallerySettings.this.getText(2131362189), GallerySettings.this.getText(2131362185), true, false));
        case 2:
          do
            return;
          while (GallerySettings.this.mProgressDialog == null);
          GallerySettings.this.mProgressDialog.dismiss();
          GallerySettings.access$002(GallerySettings.this, null);
          return;
        case 3:
        }
        GallerySettings.this.updateAccountInfo((Account[])(Account[])paramMessage.obj);
      }
    };
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    super.onCreateOptionsMenu(paramMenu);
    getMenuInflater().inflate(2131886094, paramMenu);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return false;
    case 2131558678:
      showAddAccount();
      return true;
    case 16908332:
    }
    finish();
    return true;
  }

  public void onPause()
  {
    super.onPause();
    this.mIsResumed = false;
    if (this.mLoadAccountTask != null)
    {
      this.mLoadAccountTask.cancel();
      this.mLoadAccountTask = null;
    }
    ((PreferenceGroup)findPreference("prefs_account_settings")).setEnabled(false);
    this.mHandler.removeMessages(1);
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    String str = paramPreference.getKey();
    if ("prefs_sync_on_wifi_only".equals(str))
    {
      ContentValues localContentValues = new ContentValues();
      if (((Boolean)paramObject).booleanValue());
      for (int i = 1; ; i = 0)
      {
        localContentValues.put("sync_picasa_on_wifi_only", Integer.valueOf(i));
        changePicasaSyncSettings(localContentValues);
        return true;
      }
    }
    throw new AssertionError("unknown prefs: " + str);
  }

  public boolean onPreferenceClick(Preference paramPreference)
  {
    if ("prefs_open_source_licenses".equals(paramPreference.getKey()))
      startActivity(new Intent(this, LicensesActivity.class));
    return false;
  }

  public void onResume()
  {
    super.onResume();
    this.mIsResumed = true;
    this.mLoadAccountTask = ((GalleryApp)getApplication()).getThreadPool().submit(new ThreadPool.Job()
    {
      public Void run(ThreadPool.JobContext paramJobContext)
      {
        Account[] arrayOfAccount = GallerySettings.access$200(paramJobContext, GallerySettings.this);
        if (!paramJobContext.isCancelled())
          GallerySettings.this.mHandler.sendMessage(GallerySettings.this.mHandler.obtainMessage(3, arrayOfAccount));
        return null;
      }
    }
    , null);
    this.mHandler.removeMessages(1);
    this.mHandler.sendEmptyMessageDelayed(1, 300L);
    this.mPrefsSyncOnWifiOnly.setChecked(true);
  }

  public void showAddAccount()
  {
    addAccount(this, false);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.settings.GallerySettings
 * JD-Core Version:    0.5.4
 */