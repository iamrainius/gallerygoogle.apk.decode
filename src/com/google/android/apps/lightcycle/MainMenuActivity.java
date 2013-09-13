package com.google.android.apps.lightcycle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import com.google.android.apps.lightcycle.gallery.GalleryActivity;
import com.google.android.apps.lightcycle.glass.GlassMainMenuActivity;
import com.google.android.apps.lightcycle.panorama.DeviceManager;
import com.google.android.apps.lightcycle.storage.StorageManager;
import com.google.android.apps.lightcycle.storage.StorageManagerFactory;
import com.google.android.apps.lightcycle.util.AnalyticsHelper;
import com.google.android.apps.lightcycle.util.AnalyticsHelper.Page;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.FeedbackUtil;
import com.google.android.apps.lightcycle.util.FontUtil;
import com.google.android.apps.lightcycle.util.LG;

public class MainMenuActivity extends Activity
{
  private static final String TAG = MainMenuActivity.class.getSimpleName();
  private FeedbackUtil feedbackUtil = new FeedbackUtil();
  private FontUtil fontUtil;
  private AnalyticsHelper mAnalyticsHelper;

  private void initializeLocalStorageOnFirstStart()
  {
    StorageManager localStorageManager = StorageManagerFactory.getStorageManager();
    localStorageManager.init(this);
    ProgressDialog localProgressDialog = new ProgressDialog(this);
    localProgressDialog.setProgressStyle(1);
    localProgressDialog.setCancelable(false);
    localProgressDialog.setTitle(2131361851);
    localProgressDialog.setIndeterminate(false);
    PowerManager.WakeLock localWakeLock = ((PowerManager)getSystemService("power")).newWakeLock(536870918, TAG);
    localWakeLock.acquire();
    localStorageManager.addExistingPanoramaSessions(new Callback(localWakeLock, localProgressDialog)
    {
      public void onCallback(Void paramVoid)
      {
        this.val$wakeLock.release();
        this.val$progressDialog.dismiss();
      }
    }
    , localProgressDialog);
  }

  private void setUpFeedbackButton(int paramInt)
  {
    View localView = findViewById(paramInt);
    if (localView instanceof Button)
      ((Button)localView).setTypeface(this.fontUtil.getMainMenuFont());
    localView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        MainMenuActivity.this.feedbackUtil.startFeedback(MainMenuActivity.this, MainMenuActivity.this.getWindow().getDecorView());
      }
    });
  }

  private void setUpMenuButton(int paramInt, Class<? extends Activity> paramClass)
  {
    View localView = findViewById(paramInt);
    if (localView instanceof Button)
      ((Button)localView).setTypeface(this.fontUtil.getMainMenuFont());
    localView.setOnClickListener(new View.OnClickListener(paramClass)
    {
      public void onClick(View paramView)
      {
        MainMenuActivity.this.startActivity(new Intent(MainMenuActivity.this, this.val$activityClass));
      }
    });
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (DeviceManager.isWingman())
    {
      LG.d("Wingman detected.");
      startActivity(new Intent(this, GlassMainMenuActivity.class));
      finish();
      return;
    }
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    setContentView(2130968612);
    this.fontUtil = new FontUtil(this);
    setUpMenuButton(2131558513, PanoramaCaptureActivity.class);
    setUpMenuButton(2131558514, GalleryActivity.class);
    setUpMenuButton(2131558517, LightCyclePreferenceActivity.class);
    setUpMenuButton(2131558515, HelpAndTipsActivity.class);
    setUpFeedbackButton(2131558516);
    if (localSharedPreferences.getBoolean("showHelpOnStartUp", true))
    {
      localSharedPreferences.edit().putBoolean("showHelpOnStartUp", false).apply();
      startActivity(new Intent(this, HelpAndTipsActivity.class));
    }
    if (!localSharedPreferences.getBoolean("dbInitialized", false))
    {
      localSharedPreferences.edit().putBoolean("dbInitialized", true).apply();
      initializeLocalStorageOnFirstStart();
    }
    this.mAnalyticsHelper = AnalyticsHelper.getInstance(this);
  }

  protected void onResume()
  {
    super.onResume();
    this.mAnalyticsHelper.trackPage(AnalyticsHelper.Page.MAIN_MENU);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.MainMenuActivity
 * JD-Core Version:    0.5.4
 */