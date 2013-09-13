package com.android.gallery3d.onetimeinitializer;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import com.android.gallery3d.common.Utils;

public class AutoSyncCarryOverFromICSReceiver extends BroadcastReceiver
{
  private static void carryOverSyncAutomatically(Context paramContext)
  {
    if (paramContext.getPackageManager().resolveContentProvider("com.google.android.apps.plus.content.EsGooglePhotoProvider", 0) == null)
    {
      Log.d("AutoSyncCarryOver", "plus not installed; skip auto sync carryover");
      return;
    }
    Account[] arrayOfAccount = AccountManager.get(paramContext).getAccountsByType("com.google");
    for (int i = 0; ; ++i)
    {
      if (i < arrayOfAccount.length);
      Account localAccount = arrayOfAccount[i];
      boolean bool = ContentResolver.getSyncAutomatically(localAccount, "com.google.android.apps.plus.content.EsGooglePhotoProvider");
      Log.d("AutoSyncCarryOver", "carry over syncAutomatically " + bool + " for " + Utils.maskDebugInfo(localAccount.name));
      ContentResolver.setSyncAutomatically(localAccount, "com.google.android.gallery3d.GooglePhotoProvider", bool);
    }
  }

  private static void disableMyself(Context paramContext)
  {
    paramContext.getPackageManager().setComponentEnabledSetting(new ComponentName(paramContext, AutoSyncCarryOverFromICSReceiver.class), 2, 1);
  }

  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext.startService(new Intent(paramContext, AsyncService.class));
  }

  public static class AsyncService extends IntentService
  {
    public AsyncService()
    {
      super("AutoSyncCarryOverFromICSAsync");
    }

    protected void onHandleIntent(Intent paramIntent)
    {
      try
      {
        AutoSyncCarryOverFromICSReceiver.access$000(this);
        return;
      }
      catch (Throwable localThrowable)
      {
        Log.e("AutoSyncCarryOver", "onHandleIntent", localThrowable);
        return;
      }
      finally
      {
        AutoSyncCarryOverFromICSReceiver.access$100(this);
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.onetimeinitializer.AutoSyncCarryOverFromICSReceiver
 * JD-Core Version:    0.5.4
 */