package com.google.android.picasasync;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.support.v4.net.TrafficStatsCompat;
import android.util.Log;
import android.widget.RemoteViews;
import com.android.gallery3d.app.Gallery;
import com.android.gallery3d.common.Utils;
import com.google.android.picasastore.MetricsUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

class PhotoPrefetch
  implements SyncTaskProvider
{
  private final Context mContext;
  private final int mImageType;
  private final SharedPreferences mPrefs;

  public PhotoPrefetch(Context paramContext, int paramInt)
  {
    this.mContext = paramContext;
    this.mImageType = paramInt;
    this.mPrefs = PreferenceManager.getDefaultSharedPreferences(this.mContext);
  }

  private void cleanCache(PrefetchHelper.PrefetchContext paramPrefetchContext)
    throws IOException
  {
    int i = 2;
    if (!compareAndSetCleanBit(this.mPrefs, i, 1))
      return;
    PrefetchHelper.get(this.mContext).cleanCache(paramPrefetchContext);
    SharedPreferences localSharedPreferences = this.mPrefs;
    if (paramPrefetchContext.syncInterrupted());
    while (true)
    {
      compareAndSetCleanBit(localSharedPreferences, 1, i);
      return;
      i = 0;
    }
  }

  private static boolean compareAndSetCleanBit(SharedPreferences paramSharedPreferences, int paramInt1, int paramInt2)
  {
    monitorenter;
    int j;
    try
    {
      int i = paramSharedPreferences.getInt("picasasync.prefetch.clean-cache", 0);
      j = 0;
      if (i != paramInt1)
        return j;
      paramSharedPreferences.edit().putInt("picasasync.prefetch.clean-cache", paramInt2).commit();
    }
    finally
    {
      monitorexit;
    }
  }

  private static SyncState getSyncState(int paramInt)
  {
    switch (paramInt)
    {
    default:
      throw new AssertionError();
    case 2:
      return SyncState.PREFETCH_FULL_IMAGE;
    case 1:
      return SyncState.PREFETCH_SCREEN_NAIL;
    case 3:
    }
    return SyncState.PREFETCH_ALBUM_COVER;
  }

  static void onRequestSync(Context paramContext)
  {
    compareAndSetCleanBit(PreferenceManager.getDefaultSharedPreferences(paramContext), 0, 2);
  }

  public void collectTasks(Collection<SyncTask> paramCollection)
  {
    PicasaSyncHelper localPicasaSyncHelper = PicasaSyncHelper.getInstance(this.mContext);
    SQLiteDatabase localSQLiteDatabase = localPicasaSyncHelper.getWritableDatabase();
    switch (this.mImageType)
    {
    default:
      throw new AssertionError();
    case 2:
      Iterator localIterator3 = localPicasaSyncHelper.getUsers().iterator();
      while (true)
      {
        if (!localIterator3.hasNext())
          return;
        UserEntry localUserEntry3 = (UserEntry)localIterator3.next();
        if (!SyncState.PREFETCH_FULL_IMAGE.isRequested(localSQLiteDatabase, localUserEntry3.account))
          continue;
        paramCollection.add(new PrefetchFullImage(localUserEntry3.account));
      }
    case 1:
      Iterator localIterator2 = localPicasaSyncHelper.getUsers().iterator();
      while (true)
      {
        if (!localIterator2.hasNext())
          return;
        UserEntry localUserEntry2 = (UserEntry)localIterator2.next();
        if (!SyncState.PREFETCH_SCREEN_NAIL.isRequested(localSQLiteDatabase, localUserEntry2.account))
          continue;
        paramCollection.add(new PrefetchScreenNail(localUserEntry2.account));
      }
    case 3:
    }
    Iterator localIterator1 = localPicasaSyncHelper.getUsers().iterator();
    while (localIterator1.hasNext())
    {
      UserEntry localUserEntry1 = (UserEntry)localIterator1.next();
      if (!SyncState.PREFETCH_ALBUM_COVER.isRequested(localSQLiteDatabase, localUserEntry1.account))
        continue;
      paramCollection.add(new PrefetchAlbumCover(localUserEntry1.account));
    }
  }

  public void onSyncStart()
  {
  }

  public void resetSyncStates()
  {
    PicasaSyncHelper localPicasaSyncHelper = PicasaSyncHelper.getInstance(this.mContext);
    SQLiteDatabase localSQLiteDatabase = localPicasaSyncHelper.getWritableDatabase();
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);
    monitorenter;
    SyncState localSyncState;
    Iterator localIterator;
    try
    {
      if (localSharedPreferences.getInt("picasasync.prefetch.clean-cache", 0) == 1)
        localSharedPreferences.edit().putInt("picasasync.prefetch.clean-cache", 2).commit();
      monitorexit;
      localSyncState = getSyncState(this.mImageType);
      localIterator = localPicasaSyncHelper.getUsers().iterator();
      if (!localIterator.hasNext())
        return;
    }
    finally
    {
      monitorexit;
    }
  }

  @TargetApi(14)
  void showPrefetchCompleteNotification(int paramInt)
  {
    Resources localResources = this.mContext.getResources();
    String str1 = localResources.getString(R.string.ps_cache_done_title);
    String str2 = localResources.getString(R.string.ps_cache_done);
    Notification localNotification1;
    if (Build.VERSION.SDK_INT < 14)
    {
      localNotification1 = new Notification(17301634, str1, System.currentTimeMillis());
      localNotification1.setLatestEventInfo(this.mContext, str1, str2, null);
      localNotification1.flags = (0x10 | localNotification1.flags);
      Intent localIntent = new Intent(this.mContext, Gallery.class);
      localNotification1.contentIntent = PendingIntent.getActivity(this.mContext, 0, localIntent, 134217728);
    }
    for (Notification localNotification2 = localNotification1; ; localNotification2 = new Notification.Builder(this.mContext).setSmallIcon(17301634).setAutoCancel(true).setOngoing(false).setContentTitle(str1).setContentText(str2).setTicker(str2).getNotification())
    {
      ((NotificationManager)this.mContext.getSystemService("notification")).notify(2, localNotification2);
      return;
    }
  }

  @TargetApi(14)
  void updateOngoingNotification(int paramInt1, int paramInt2)
  {
    Resources localResources = this.mContext.getResources();
    int i = R.string.ps_cache_status;
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = Integer.valueOf(paramInt1);
    arrayOfObject[1] = Integer.valueOf(paramInt2);
    String str = localResources.getString(i, arrayOfObject);
    Notification localNotification1;
    if (Build.VERSION.SDK_INT < 14)
    {
      RemoteViews localRemoteViews = new RemoteViews(this.mContext.getPackageName(), R.layout.ps_cache_notification);
      localRemoteViews.setTextViewText(R.id.ps_status, str);
      localRemoteViews.setImageViewResource(R.id.icon, 17301633);
      localRemoteViews.setProgressBar(R.id.ps_progress, paramInt2, paramInt1, false);
      localNotification1 = new Notification();
      localNotification1.icon = 17301633;
      localNotification1.contentView = localRemoteViews;
      localNotification1.when = System.currentTimeMillis();
      localNotification1.flags = (0x2 | localNotification1.flags);
      Intent localIntent = new Intent(this.mContext, Gallery.class);
      localNotification1.contentIntent = PendingIntent.getActivity(this.mContext, 0, localIntent, 134217728);
    }
    for (Notification localNotification2 = localNotification1; ; localNotification2 = new Notification.Builder(this.mContext).setSmallIcon(17301633).setOngoing(true).setContentTitle(localResources.getString(R.string.ps_cache_status_title)).setContentText(str).setProgress(paramInt2, paramInt1, false).getNotification())
    {
      ((NotificationManager)this.mContext.getSystemService("notification")).notify(1, localNotification2);
      return;
    }
  }

  private class PrefetchAlbumCover extends PhotoPrefetch.PrefetchScreenNail
  {
    public PrefetchAlbumCover(String arg2)
    {
      super(PhotoPrefetch.this, str, SyncState.PREFETCH_ALBUM_COVER);
    }

    public void performSync(SyncResult paramSyncResult)
      throws IOException
    {
      int i = MetricsUtils.begin("PrefetchAlbumCover");
      TrafficStatsCompat.setThreadStatsTag(2);
      try
      {
        performSyncCommon(paramSyncResult);
        return;
      }
      finally
      {
        TrafficStatsCompat.clearThreadStatsTag();
        MetricsUtils.endWithReport(i, "picasa.prefetch.thumbnail");
      }
    }

    protected boolean performSyncInternal(UserEntry paramUserEntry, PrefetchHelper paramPrefetchHelper, SQLiteDatabase paramSQLiteDatabase)
      throws IOException
    {
      if (PhotoPrefetch.this.mContext.getExternalCacheDir() == null)
        Log.w("PhotoPrefetch", "no external storage, skip album cover prefetching");
      do
      {
        return true;
        PhotoPrefetch.this.cleanCache(this.mSyncContext);
        paramPrefetchHelper.syncAlbumCoversForUser(this.mSyncContext, paramUserEntry);
      }
      while (!this.mSyncContext.syncInterrupted());
      return false;
    }
  }

  private class PrefetchFullImage extends PhotoPrefetch.PrefetchScreenNail
    implements PrefetchHelper.PrefetchListener
  {
    private PrefetchHelper.CacheStats mCacheStats;

    public PrefetchFullImage(String arg2)
    {
      super(PhotoPrefetch.this, str, SyncState.PREFETCH_FULL_IMAGE);
    }

    public boolean isBackgroundSync()
    {
      return false;
    }

    public void onDownloadFinish(long paramLong, boolean paramBoolean)
    {
      PrefetchHelper.CacheStats localCacheStats = this.mCacheStats;
      localCacheStats.pendingCount = (-1 + localCacheStats.pendingCount);
      if (!paramBoolean)
        localCacheStats.failedCount = (1 + localCacheStats.failedCount);
      int i = localCacheStats.totalCount - localCacheStats.pendingCount;
      PhotoPrefetch.this.updateOngoingNotification(i, localCacheStats.totalCount);
    }

    public void performSync(SyncResult paramSyncResult)
      throws IOException
    {
      int i = MetricsUtils.begin("PrefetchFullImage");
      TrafficStatsCompat.setThreadStatsTag(4);
      try
      {
        performSyncCommon(paramSyncResult);
        return;
      }
      finally
      {
        TrafficStatsCompat.clearThreadStatsTag();
        MetricsUtils.endWithReport(i, "picasa.prefetch.full_image");
      }
    }

    protected boolean performSyncInternal(UserEntry paramUserEntry, PrefetchHelper paramPrefetchHelper, SQLiteDatabase paramSQLiteDatabase)
      throws IOException
    {
      this.mSyncContext.setCacheDownloadListener(this);
      PhotoPrefetch.this.cleanCache(this.mSyncContext);
      this.mCacheStats = paramPrefetchHelper.getCacheStatistics(2);
      if (this.mCacheStats.pendingCount == 0)
        return true;
      while (true)
      {
        try
        {
          paramPrefetchHelper.syncFullImagesForUser(this.mSyncContext, paramUserEntry);
          ((NotificationManager)PhotoPrefetch.this.mContext.getSystemService("notification")).cancel(1);
          if ((this.mCacheStats.pendingCount == 0) && (this.mCacheStats.failedCount == 0))
            PhotoPrefetch.this.showPrefetchCompleteNotification(this.mCacheStats.totalCount);
          if (!this.mSyncContext.syncInterrupted())
            return i;
        }
        finally
        {
          ((NotificationManager)PhotoPrefetch.this.mContext.getSystemService("notification")).cancel(1);
        }
        int i = 0;
      }
    }
  }

  private class PrefetchScreenNail extends SyncTask
  {
    protected boolean mSyncCancelled = false;
    protected PrefetchHelper.PrefetchContext mSyncContext;
    private SyncState mSyncState;

    public PrefetchScreenNail(String arg2)
    {
      this(PhotoPrefetch.this, str, SyncState.PREFETCH_SCREEN_NAIL);
    }

    public PrefetchScreenNail(String paramSyncState, SyncState arg3)
    {
      super(paramSyncState);
      Object localObject;
      this.mSyncState = localObject;
    }

    public void cancelSync()
    {
      this.mSyncCancelled = true;
      if (this.mSyncContext == null)
        return;
      this.mSyncContext.stopSync();
    }

    public boolean isBackgroundSync()
    {
      return true;
    }

    public boolean isSyncOnBattery()
    {
      return isSyncOnBattery(PhotoPrefetch.this.mContext);
    }

    public boolean isSyncOnExternalStorageOnly()
    {
      return true;
    }

    public boolean isSyncOnRoaming()
    {
      return isSyncOnRoaming(PhotoPrefetch.this.mContext);
    }

    public boolean isSyncOnWifiOnly()
    {
      return isSyncPicasaOnWifiOnly(PhotoPrefetch.this.mContext);
    }

    public void performSync(SyncResult paramSyncResult)
      throws IOException
    {
      int i = MetricsUtils.begin("PrefetchScreenNail");
      TrafficStatsCompat.setThreadStatsTag(3);
      try
      {
        performSyncCommon(paramSyncResult);
        return;
      }
      finally
      {
        TrafficStatsCompat.clearThreadStatsTag();
        MetricsUtils.endWithReport(i, "picasa.prefetch.screennail");
      }
    }

    protected final void performSyncCommon(SyncResult paramSyncResult)
      throws IOException
    {
      PrefetchHelper localPrefetchHelper = PrefetchHelper.get(PhotoPrefetch.this.mContext);
      PicasaSyncHelper localPicasaSyncHelper = PicasaSyncHelper.getInstance(PhotoPrefetch.this.mContext);
      SQLiteDatabase localSQLiteDatabase = localPicasaSyncHelper.getWritableDatabase();
      monitorenter;
      try
      {
        if (this.mSyncCancelled)
          return;
        this.mSyncContext = localPrefetchHelper.createPrefetchContext(paramSyncResult, Thread.currentThread());
        monitorexit;
        this.mSyncContext.updateCacheConfigVersion();
        if (!this.mSyncState.onSyncStart(localSQLiteDatabase, this.syncAccount))
          return;
      }
      finally
      {
        monitorexit;
      }
      UserEntry localUserEntry = localPicasaSyncHelper.findUser(this.syncAccount);
      if (localUserEntry == null)
      {
        Log.w("PhotoPrefetch", "cannot find user: " + Utils.maskDebugInfo(this.syncAccount));
        return;
      }
      if (performSyncInternal(localUserEntry, localPrefetchHelper, localSQLiteDatabase))
      {
        this.mSyncState.onSyncFinish(localSQLiteDatabase, this.syncAccount);
        return;
      }
      this.mSyncState.resetSyncToDirty(localSQLiteDatabase, this.syncAccount);
    }

    protected boolean performSyncInternal(UserEntry paramUserEntry, PrefetchHelper paramPrefetchHelper, SQLiteDatabase paramSQLiteDatabase)
      throws IOException
    {
      if (PhotoPrefetch.this.mContext.getExternalCacheDir() == null)
        Log.w("PhotoPrefetch", "no external storage, skip screenail prefetching");
      do
      {
        return true;
        PhotoPrefetch.this.cleanCache(this.mSyncContext);
        paramPrefetchHelper.syncScreenNailsForUser(this.mSyncContext, paramUserEntry);
      }
      while (!this.mSyncContext.syncInterrupted());
      return false;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.PhotoPrefetch
 * JD-Core Version:    0.5.4
 */