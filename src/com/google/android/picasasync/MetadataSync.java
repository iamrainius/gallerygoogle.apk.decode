package com.google.android.picasasync;

import android.content.Context;
import android.content.SyncResult;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.android.gallery3d.common.Utils;
import com.google.android.picasastore.MetricsUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

class MetadataSync
  implements SyncTaskProvider
{
  private final Context mContext;
  private final boolean mIsManual;

  public MetadataSync(Context paramContext, boolean paramBoolean)
  {
    this.mContext = paramContext;
    this.mIsManual = paramBoolean;
  }

  public void collectTasks(Collection<SyncTask> paramCollection)
  {
    PicasaSyncHelper localPicasaSyncHelper = PicasaSyncHelper.getInstance(this.mContext);
    SQLiteDatabase localSQLiteDatabase = localPicasaSyncHelper.getWritableDatabase();
    if (this.mIsManual);
    for (SyncState localSyncState = SyncState.METADATA_MANUAL; ; localSyncState = SyncState.METADATA)
    {
      Iterator localIterator = localPicasaSyncHelper.getUsers().iterator();
      while (true)
      {
        if (!localIterator.hasNext())
          return;
        UserEntry localUserEntry = (UserEntry)localIterator.next();
        if (!localSyncState.isRequested(localSQLiteDatabase, localUserEntry.account))
          continue;
        paramCollection.add(new MetadataSyncTask(localUserEntry.account, this.mIsManual));
      }
    }
  }

  public void onSyncStart()
  {
  }

  public void resetSyncStates()
  {
    PicasaSyncHelper localPicasaSyncHelper = PicasaSyncHelper.getInstance(this.mContext);
    SQLiteDatabase localSQLiteDatabase = localPicasaSyncHelper.getWritableDatabase();
    if (this.mIsManual);
    for (SyncState localSyncState = SyncState.METADATA_MANUAL; ; localSyncState = SyncState.METADATA)
    {
      Iterator localIterator = localPicasaSyncHelper.getUsers().iterator();
      while (true)
      {
        if (!localIterator.hasNext())
          return;
        localSyncState.resetSyncToDirty(localSQLiteDatabase, ((UserEntry)localIterator.next()).account);
      }
    }
  }

  private class MetadataSyncTask extends SyncTask
  {
    private boolean mSyncCancelled = false;
    private PicasaSyncHelper.SyncContext mSyncContext;

    public MetadataSyncTask(String paramBoolean, boolean arg3)
    {
      super(paramBoolean);
    }

    private void performSyncInternal(SyncResult paramSyncResult)
    {
      PicasaSyncHelper localPicasaSyncHelper = PicasaSyncHelper.getInstance(MetadataSync.this.mContext);
      SQLiteDatabase localSQLiteDatabase = localPicasaSyncHelper.getWritableDatabase();
      if (MetadataSync.this.mIsManual)
      {
        if (!SyncState.METADATA_MANUAL.onSyncStart(localSQLiteDatabase, this.syncAccount))
          return;
        SyncState.METADATA.onSyncStart(localSQLiteDatabase, this.syncAccount);
      }
      while (true)
      {
        monitorenter;
        try
        {
          if (!this.mSyncCancelled)
            break label101;
          return;
        }
        finally
        {
          monitorexit;
        }
        if (!((false | SyncState.METADATA.onSyncStart(localSQLiteDatabase, this.syncAccount) | SyncState.METADATA_MANUAL.onSyncStart(localSQLiteDatabase, this.syncAccount))))
          return;
      }
      label101: this.mSyncContext = localPicasaSyncHelper.createSyncContext(paramSyncResult, Thread.currentThread());
      this.mSyncContext.setAccount(this.syncAccount);
      monitorexit;
      UserEntry localUserEntry = localPicasaSyncHelper.findUser(this.syncAccount);
      if (localUserEntry == null)
      {
        Object[] arrayOfObject2 = new Object[1];
        arrayOfObject2[0] = Utils.maskDebugInfo(this.syncAccount);
        Log.w("MetadataSyncProvider", String.format("user: %s not found, sync abort", arrayOfObject2));
        return;
      }
      boolean bool1;
      try
      {
        boolean bool2 = localPicasaSyncHelper.isPicasaAccount(localUserEntry.account);
        bool1 = bool2;
        label188: if (!bool1)
          break label292;
        localPicasaSyncHelper.syncAlbumsForUser(this.mSyncContext, localUserEntry);
        localPicasaSyncHelper.syncPhotosForUser(this.mSyncContext, localUserEntry);
        label213: if (this.mSyncContext.syncInterrupted())
          break label326;
        SyncState.METADATA.onSyncFinish(localSQLiteDatabase, localUserEntry.account);
        SyncState.METADATA_MANUAL.onSyncFinish(localSQLiteDatabase, localUserEntry.account);
        PicasaSyncManager.get(MetadataSync.this.mContext).requestPrefetchSync();
        PicasaSyncManager.get(MetadataSync.this.mContext).requestAccountSync();
        label292: label326: return;
      }
      catch (Exception localException)
      {
        Log.w("MetadataSyncProvider", "check picasa account failed", localException);
        bool1 = false;
        break label188:
        Object[] arrayOfObject1 = new Object[1];
        arrayOfObject1[0] = Utils.maskDebugInfo(localUserEntry.account);
        Log.w("MetadataSyncProvider", String.format("%s has not been enabled for Picasa service, just ignore", arrayOfObject1));
        break label213:
        SyncState.METADATA.resetSyncToDirty(localSQLiteDatabase, localUserEntry.account);
        SyncState.METADATA_MANUAL.resetSyncToDirty(localSQLiteDatabase, localUserEntry.account);
      }
    }

    public void cancelSync()
    {
      monitorenter;
      try
      {
        this.mSyncCancelled = true;
        if (this.mSyncContext != null)
          this.mSyncContext.stopSync();
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

    public boolean isBackgroundSync()
    {
      return !MetadataSync.this.mIsManual;
    }

    public boolean isSyncOnBattery()
    {
      return isSyncOnBattery(MetadataSync.this.mContext);
    }

    public boolean isSyncOnWifiOnly()
    {
      if (MetadataSync.this.mIsManual)
        return false;
      return isSyncPicasaOnWifiOnly(MetadataSync.this.mContext);
    }

    public void performSync(SyncResult paramSyncResult)
    {
      int i = MetricsUtils.begin("MetadataSync");
      try
      {
        performSyncInternal(paramSyncResult);
        return;
      }
      finally
      {
        MetricsUtils.endWithReport(i, "picasa.sync.metadata");
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.MetadataSync
 * JD-Core Version:    0.5.4
 */