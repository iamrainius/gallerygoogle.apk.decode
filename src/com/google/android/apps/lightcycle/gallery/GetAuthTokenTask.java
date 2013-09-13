package com.google.android.apps.lightcycle.gallery;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.google.android.apps.lightcycle.util.Callback;
import java.io.IOException;

public class GetAuthTokenTask extends AsyncTask<Account, Integer, Bundle>
{
  private static final String TAG = GetAuthTokenTask.class.getSimpleName();
  private final AccountManager accountManager;
  private final Callback<Bundle> bundleCallback;

  public GetAuthTokenTask(AccountManager paramAccountManager, Callback<Bundle> paramCallback)
  {
    this.accountManager = paramAccountManager;
    this.bundleCallback = paramCallback;
  }

  protected Bundle doInBackground(Account[] paramArrayOfAccount)
  {
    try
    {
      Bundle localBundle = (Bundle)this.accountManager.getAuthToken(paramArrayOfAccount[0], "lh2", null, true, null, null).getResult();
      return localBundle;
    }
    catch (OperationCanceledException localOperationCanceledException)
    {
      Log.e(TAG, localOperationCanceledException.getMessage(), localOperationCanceledException);
      return null;
    }
    catch (AuthenticatorException localAuthenticatorException)
    {
      Log.e(TAG, localAuthenticatorException.getMessage(), localAuthenticatorException);
    }
    catch (IOException localIOException)
    {
      Log.e(TAG, localIOException.getMessage(), localIOException);
    }
  }

  protected void onPostExecute(Bundle paramBundle)
  {
    this.bundleCallback.onCallback(paramBundle);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.gallery.GetAuthTokenTask
 * JD-Core Version:    0.5.4
 */