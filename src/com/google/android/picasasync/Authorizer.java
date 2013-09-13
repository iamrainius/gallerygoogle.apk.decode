package com.google.android.picasasync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.android.gallery3d.common.Utils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

class Authorizer
{
  private AccountManager mAccountManager;
  private final String mAuthTokenType;

  public Authorizer(Context paramContext, String paramString)
  {
    this.mAccountManager = AccountManager.get(paramContext);
    this.mAuthTokenType = paramString;
  }

  private Account getAccount(String paramString)
  {
    for (Account localAccount : this.mAccountManager.getAccountsByType("com.google"))
      if (localAccount.name.equals(paramString))
        return localAccount;
    return null;
  }

  public String getAuthToken(String paramString)
    throws OperationCanceledException, IOException, AuthenticatorException
  {
    Account localAccount = getAccount(paramString);
    if (localAccount != null)
    {
      Bundle localBundle = (Bundle)this.mAccountManager.getAuthToken(localAccount, this.mAuthTokenType, true, null, null).getResult(30000L, TimeUnit.MILLISECONDS);
      if (localBundle == null)
        return null;
      return localBundle.getString("authtoken");
    }
    throw new AuthenticatorException("account doesn't exist");
  }

  public String getFreshAuthToken(String paramString1, String paramString2)
    throws OperationCanceledException, IOException, AuthenticatorException
  {
    Log.d("UploaderAuthorizer", "Refreshing authToken for " + Utils.maskDebugInfo(paramString1));
    this.mAccountManager.invalidateAuthToken("com.google", paramString2);
    return getAuthToken(paramString1);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.Authorizer
 * JD-Core Version:    0.5.4
 */