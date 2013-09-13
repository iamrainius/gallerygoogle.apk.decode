package com.google.android.picasasync;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.content.SyncStats;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.android.gallery3d.common.Utils;

public final class PicasaSyncService extends Service
{
  private static PicasaSyncAdapter sSyncAdapter;

  private static void carryOverSyncAutomatically(Context paramContext, Account paramAccount, String paramString)
  {
    monitorenter;
    try
    {
      if (ContentResolver.getSyncAutomatically(paramAccount, "com.cooliris.picasa.contentprovider"))
      {
        Log.d("PicasaSyncService", "carry over syncAutomatically for " + Utils.maskDebugInfo(paramAccount.name));
        ContentResolver.setSyncAutomatically(paramAccount, "com.cooliris.picasa.contentprovider", false);
        ContentResolver.setSyncAutomatically(paramAccount, paramString, true);
      }
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  private static PicasaSyncAdapter getSyncAdapter(Context paramContext)
  {
    monitorenter;
    try
    {
      if (sSyncAdapter == null)
        sSyncAdapter = new PicasaSyncAdapter(paramContext);
      PicasaSyncAdapter localPicasaSyncAdapter = sSyncAdapter;
      return localPicasaSyncAdapter;
    }
    finally
    {
      monitorexit;
    }
  }

  public IBinder onBind(Intent paramIntent)
  {
    return getSyncAdapter(this).getSyncAdapterBinder();
  }

  private static class PicasaSyncAdapter extends AbstractThreadedSyncAdapter
  {
    private PicasaSyncManager.SyncSession mSession;

    public PicasaSyncAdapter(Context paramContext)
    {
      super(paramContext, false);
    }

    private boolean isGoogleAccount(Account paramAccount)
    {
      return "com.google".equals(paramAccount.type);
    }

    public void onPerformSync(Account paramAccount, Bundle paramBundle, String paramString, ContentProviderClient paramContentProviderClient, SyncResult paramSyncResult)
    {
      PicasaSyncManager localPicasaSyncManager = PicasaSyncManager.get(getContext());
      if (paramBundle.getBoolean("initialize"))
      {
        Log.d("PicasaSyncService", "initialize account: " + Utils.maskDebugInfo(paramAccount.name));
        try
        {
          if ((PicasaFacade.get(getContext()).isMaster()) && (isGoogleAccount(paramAccount)))
          {
            ContentResolver.setIsSyncable(paramAccount, paramString, 1);
            PicasaSyncService.access$000(getContext(), paramAccount, paramString);
            localPicasaSyncManager.onAccountInitialized(paramAccount.name);
            return;
          }
          ContentResolver.setIsSyncable(paramAccount, paramString, 0);
          return;
        }
        catch (Exception localException2)
        {
          Log.e("PicasaSyncService", "cannot do sync", localException2);
          return;
        }
      }
      monitorenter;
      try
      {
        if (Thread.currentThread().isInterrupted())
        {
          Log.d("PicasaSyncService", "sync is cancelled");
          return;
        }
      }
      finally
      {
        monitorexit;
      }
      this.mSession = localPicasaSyncManager.createSession(paramAccount.name, paramSyncResult);
      monitorexit;
      if ((!paramBundle.getBoolean("upload", false)) && (!paramBundle.getBoolean("picasa-sync-manager-requested", false)))
      {
        localPicasaSyncManager.resetSyncStates();
        SyncState.METADATA.onSyncRequested(PicasaDatabaseHelper.get(getContext()).getWritableDatabase(), paramAccount.name);
      }
      Log.d("PicasaSyncService", "start sync on " + Utils.maskDebugInfo(paramAccount.name));
      try
      {
        return;
      }
      catch (Exception localException1)
      {
        Log.e("PicasaSyncService", "performSync error", localException1);
        SyncStats localSyncStats = paramSyncResult.stats;
        return;
      }
      finally
      {
        if (this.mSession.isSyncCancelled())
          Log.d("PicasaSyncService", "sync cancelled");
        while (true)
        {
          throw localObject2;
          Log.d("PicasaSyncService", "sync finished");
        }
      }
    }

    public void onSyncCanceled()
    {
      monitorenter;
      try
      {
        Log.d("PicasaSyncService", "receive cancel request");
        super.onSyncCanceled();
        if (this.mSession != null)
          this.mSession.cancelSync();
        monitorexit;
        return;
      }
      finally
      {
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.PicasaSyncService
 * JD-Core Version:    0.5.4
 */