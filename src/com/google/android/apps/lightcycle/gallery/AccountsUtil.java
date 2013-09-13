package com.google.android.apps.lightcycle.gallery;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.apps.lightcycle.panorama.DeviceManager;
import com.google.android.apps.lightcycle.util.Callback;
import java.util.ArrayList;
import java.util.List;

public class AccountsUtil
{
  private static final String TAG = AccountsUtil.class.getSimpleName();
  private final AccountManager accountManager;
  private Account activeAccount;
  private final Context context;
  private List<Callback<String>> pendingCallbacks = new ArrayList();

  public AccountsUtil(Context paramContext)
  {
    this.context = paramContext;
    this.accountManager = AccountManager.get(paramContext);
  }

  private Account[] filterAccounts(Account[] paramArrayOfAccount, String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    int i = paramArrayOfAccount.length;
    for (int j = 0; j < i; ++j)
    {
      Account localAccount = paramArrayOfAccount[j];
      if (!localAccount.name.endsWith(paramString))
        continue;
      localArrayList.add(localAccount);
    }
    return (Account[])localArrayList.toArray(new Account[0]);
  }

  private void getAuthTokenForActiveAccount(Callback<String> paramCallback, boolean paramBoolean)
  {
    Log.d(TAG, "GetAuthTokenForActiveAccount " + paramBoolean);
    if (this.activeAccount == null)
    {
      Log.w(TAG, "No active account");
      paramCallback.onCallback("");
      return;
    }
    5 local5 = new Callback(paramBoolean, paramCallback)
    {
      public void onCallback(Bundle paramBundle)
      {
        if (paramBundle != null)
        {
          AccountsUtil.this.onAuthBundleReceived(paramBundle, new Callback()
          {
            public void onCallback(String paramString)
            {
              Log.d(AccountsUtil.TAG, "Received authToken: " + paramString);
              if (AccountsUtil.5.this.val$invalidate)
              {
                if ((paramString != null) && (!paramString.isEmpty()))
                {
                  Log.d(AccountsUtil.TAG, "Invalidating authToken: " + paramString);
                  AccountsUtil.this.accountManager.invalidateAuthToken("com.google", paramString);
                  AccountsUtil.this.getAuthTokenForActiveAccount(AccountsUtil.5.this.val$callback, false);
                  return;
                }
                AccountsUtil.5.this.val$callback.onCallback("");
                return;
              }
              AccountsUtil.5.this.val$callback.onCallback(paramString);
            }
          });
          return;
        }
        this.val$callback.onCallback("");
      }
    };
    GetAuthTokenTask localGetAuthTokenTask = new GetAuthTokenTask(this.accountManager, local5);
    Account[] arrayOfAccount = new Account[1];
    arrayOfAccount[0] = this.activeAccount;
    localGetAuthTokenTask.execute(arrayOfAccount);
  }

  private void onAuthBundleReceived(Bundle paramBundle, Callback<String> paramCallback)
  {
    if (paramBundle.containsKey("intent"));
    do
      synchronized (this.pendingCallbacks)
      {
        this.pendingCallbacks.add(paramCallback);
        Intent localIntent = (Intent)paramBundle.getParcelable("intent");
        localIntent.setFlags(0xEFFFFFFF & localIntent.getFlags());
        ((Activity)this.context).startActivityForResult(localIntent, 0);
        return;
      }
    while (!paramBundle.containsKey("authtoken"));
    paramCallback.onCallback(paramBundle.getString("authtoken"));
  }

  private void showGoogleAccountSelectionDialog(Callback<Account> paramCallback)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.context);
    localBuilder.setTitle(2131361839);
    Account[] arrayOfAccount1 = this.accountManager.getAccountsByType("com.google");
    if ((DeviceManager.isWingman()) && (arrayOfAccount1.length > 0))
    {
      paramCallback.onCallback(arrayOfAccount1[0]);
      return;
    }
    Account[] arrayOfAccount2 = filterAccounts(arrayOfAccount1, "@google.com");
    int i = arrayOfAccount2.length;
    String[] arrayOfString = new String[i];
    for (int j = 0; j < i; ++j)
      arrayOfString[j] = arrayOfAccount2[j].name;
    if (arrayOfString.length == 1)
    {
      paramCallback.onCallback(arrayOfAccount2[0]);
      return;
    }
    if (arrayOfString.length > 0)
      localBuilder.setItems(arrayOfString, new DialogInterface.OnClickListener(paramCallback, arrayOfAccount2)
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          this.val$callback.onCallback(this.val$accounts[paramInt]);
        }
      });
    while (true)
    {
      AlertDialog localAlertDialog = localBuilder.create();
      localAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener(paramCallback)
      {
        public void onCancel(DialogInterface paramDialogInterface)
        {
          this.val$callback.onCallback(null);
        }
      });
      localAlertDialog.show();
      return;
      localBuilder.setMessage(2131361849);
    }
  }

  public String getActiveAccountName()
  {
    if (this.activeAccount == null)
      return null;
    return this.activeAccount.name;
  }

  public void getAuthToken(Callback<String> paramCallback)
  {
    showGoogleAccountSelectionDialog(new Callback(paramCallback)
    {
      public void onCallback(Account paramAccount)
      {
        AccountsUtil.access$002(AccountsUtil.this, paramAccount);
        if (AccountsUtil.this.activeAccount != null)
        {
          AccountsUtil.this.getAuthTokenForActiveAccount(this.val$callback, true);
          return;
        }
        this.val$callback.onCallback(null);
      }
    });
  }

  public void onAuthenticationActivityResult(boolean paramBoolean)
  {
    Log.d(TAG, "Request Code: " + paramBoolean);
    if (!paramBoolean)
    {
      synchronized (this.pendingCallbacks)
      {
        if (this.pendingCallbacks.size() > 0)
          ((Callback)this.pendingCallbacks.remove(0)).onCallback(null);
      }
      monitorexit;
      return;
    }
    getAuthTokenForActiveAccount(new Callback()
    {
      public void onCallback(String paramString)
      {
        synchronized (AccountsUtil.this.pendingCallbacks)
        {
          if (AccountsUtil.this.pendingCallbacks.size() > 0)
            ((Callback)AccountsUtil.this.pendingCallbacks.remove(0)).onCallback(paramString);
        }
        monitorexit;
      }
    }
    , true);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.gallery.AccountsUtil
 * JD-Core Version:    0.5.4
 */