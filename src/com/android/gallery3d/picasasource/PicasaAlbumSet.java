package com.android.gallery3d.picasasource;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.LongSparseArray;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.ChangeNotifier;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.MediaSet.SyncListener;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import com.google.android.picasasync.AlbumEntry;
import com.google.android.picasasync.PicasaFacade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

class PicasaAlbumSet extends MediaSet
  implements FutureListener<ArrayList<MediaSet>>
{
  private static final EntrySchema ALBUM_SCHEMA = AlbumData.SCHEMA;
  private static final String[] SUM_SIZE_PROJECTION = { "sum(size)" };
  private ArrayList<MediaSet> mAlbums = new ArrayList();
  private final Handler mHandler;
  private ArrayList<MediaSet> mLoadBuffer;
  private Future<ArrayList<MediaSet>> mLoadTask;
  private final String mName;
  private final ChangeNotifier mNotifier;
  private final PicasaSource mSource;
  private final int mType;

  public PicasaAlbumSet(Path paramPath, PicasaSource paramPicasaSource)
  {
    super(paramPath, nextVersionNumber());
    this.mSource = paramPicasaSource;
    this.mNotifier = new ChangeNotifier(this, this.mSource.getPicasaFacade().getAlbumsUri(), this.mSource.getApplication());
    this.mType = getTypeFromPath(paramPath);
    this.mName = paramPicasaSource.getApplication().getResources().getString(2131362296);
    this.mHandler = new Handler(paramPicasaSource.getApplication().getMainLooper());
  }

  private static long getAlbumCacheSize(PicasaSource paramPicasaSource, long paramLong)
  {
    Uri localUri = paramPicasaSource.getPicasaFacade().getPhotosUri();
    String[] arrayOfString1 = SUM_SIZE_PROJECTION;
    String[] arrayOfString2 = new String[1];
    arrayOfString2[0] = String.valueOf(paramLong);
    Cursor localCursor = paramPicasaSource.query(localUri, arrayOfString1, "album_id=?", arrayOfString2, null);
    if (localCursor == null)
      return 0L;
    try
    {
      Utils.assertTrue(localCursor.moveToNext());
      long l = localCursor.getLong(0);
      return l;
    }
    finally
    {
      localCursor.close();
    }
  }

  public static AlbumData getAlbumData(PicasaSource paramPicasaSource, long paramLong)
  {
    Uri localUri = paramPicasaSource.getPicasaFacade().getAlbumsUri();
    String[] arrayOfString1 = ALBUM_SCHEMA.getProjection();
    String[] arrayOfString2 = new String[1];
    arrayOfString2[0] = String.valueOf(paramLong);
    Cursor localCursor = paramPicasaSource.query(localUri, arrayOfString1, "_id=?", arrayOfString2, null);
    if (localCursor == null)
      return null;
    try
    {
      if (localCursor.moveToNext())
      {
        AlbumData localAlbumData = new AlbumData();
        ALBUM_SCHEMA.cursorToObject(localCursor, localAlbumData);
        localAlbumData.cacheSize = getAlbumCacheSize(paramPicasaSource, localAlbumData.id);
        return localAlbumData;
      }
      return null;
    }
    finally
    {
      localCursor.close();
    }
  }

  public static long getTotalTargetCacheSize(PicasaSource paramPicasaSource)
  {
    Cursor localCursor = paramPicasaSource.query(paramPicasaSource.getPicasaFacade().getAlbumsUri(), new String[] { "_id" }, "cache_flag=2", null, null);
    long l1 = 0L;
    try
    {
      while (localCursor.moveToNext())
      {
        long l2 = getAlbumCacheSize(paramPicasaSource, localCursor.getLong(0));
        l1 += l2;
      }
      return l1;
    }
    finally
    {
      localCursor.close();
    }
  }

  public static long getTotalUsedCacheSize(Context paramContext)
  {
    Cursor localCursor = paramContext.getContentResolver().query(PicasaFacade.get(paramContext).getPhotosUri(), SUM_SIZE_PROJECTION, "cache_status=3", null, null);
    try
    {
      Utils.assertTrue(localCursor.moveToNext());
      long l = localCursor.getLong(0);
      return l;
    }
    finally
    {
      localCursor.close();
    }
  }

  private int getTypeFromPath(Path paramPath)
  {
    String[] arrayOfString = paramPath.split();
    if (arrayOfString.length < 2)
      throw new IllegalArgumentException(paramPath.toString());
    return getTypeFromString(arrayOfString[1]);
  }

  public String getName()
  {
    return this.mName;
  }

  public MediaSet getSubMediaSet(int paramInt)
  {
    return (MediaSet)this.mAlbums.get(paramInt);
  }

  public int getSubMediaSetCount()
  {
    return this.mAlbums.size();
  }

  public void onFutureDone(Future<ArrayList<MediaSet>> paramFuture)
  {
    monitorenter;
    try
    {
      Future localFuture = this.mLoadTask;
      if (localFuture != paramFuture)
        return;
      this.mLoadBuffer = ((ArrayList)paramFuture.get());
      if (this.mLoadBuffer == null)
        this.mLoadBuffer = new ArrayList();
    }
    finally
    {
      monitorexit;
    }
  }

  public long reload()
  {
    monitorenter;
    Iterator localIterator;
    try
    {
      if (this.mNotifier.isDirty())
      {
        if (this.mLoadTask != null)
          this.mLoadTask.cancel();
        this.mLoadTask = this.mSource.getApplication().getThreadPool().submit(new AlbumsLoader(null), this);
      }
      if (this.mLoadBuffer == null)
        break label125;
      this.mAlbums = this.mLoadBuffer;
      this.mLoadBuffer = null;
      localIterator = this.mAlbums.iterator();
      if (!localIterator.hasNext())
        break label118;
    }
    finally
    {
      monitorexit;
    }
    label118: this.mDataVersion = nextVersionNumber();
    label125: long l = this.mDataVersion;
    monitorexit;
    return l;
  }

  public Future<Integer> requestSync(MediaSet.SyncListener paramSyncListener)
  {
    monitorenter;
    while (true)
    {
      Object localObject2;
      try
      {
        if (!PicasaSource.checkPlusOneVersion(this.mSource.getApplication().getAndroidContext()))
        {
          Future localFuture = super.requestSync(paramSyncListener);
          localObject2 = localFuture;
          return localObject2;
        }
        localObject2 = new PicasaSyncTaskFuture(this.mSource, this, paramSyncListener);
      }
      finally
      {
        monitorexit;
      }
    }
  }

  private static class AlbumInfo
    implements Comparable<AlbumInfo>
  {
    public long date;
    public MediaSet set;

    public AlbumInfo(long paramLong, MediaSet paramMediaSet)
    {
      this.date = paramLong;
      this.set = paramMediaSet;
    }

    public int compareTo(AlbumInfo paramAlbumInfo)
    {
      if (paramAlbumInfo.date < this.date)
        return -1;
      if (paramAlbumInfo.date > this.date)
        return 1;
      return 0;
    }
  }

  private class AlbumsLoader
    implements ThreadPool.Job<ArrayList<MediaSet>>
  {
    private AlbumsLoader()
    {
    }

    private PicasaPostAlbum getPostAlbum(DataManager paramDataManager, AlbumData paramAlbumData)
    {
      synchronized (DataManager.LOCK)
      {
        Path localPath = Path.fromString("/picasa/post/" + PicasaAlbumSet.access$500(PicasaAlbumSet.this).getSuffix() + '/' + paramAlbumData.userId);
        PicasaPostAlbum localPicasaPostAlbum = (PicasaPostAlbum)paramDataManager.peekMediaObject(localPath);
        if (localPicasaPostAlbum == null)
          localPicasaPostAlbum = new PicasaPostAlbum(localPath, PicasaAlbumSet.this.mSource, paramAlbumData.userId, PicasaAlbumSet.this.mType);
        return localPicasaPostAlbum;
      }
    }

    public ArrayList<MediaSet> run(ThreadPool.JobContext paramJobContext)
    {
      if (!PicasaSource.checkPlusOneVersion(PicasaAlbumSet.this.mSource.getApplication().getAndroidContext()))
      {
        localArrayList3 = null;
        return localArrayList3;
      }
      Uri localUri = PicasaAlbumSet.this.mSource.getPicasaFacade().getAlbumsUri().buildUpon().appendQueryParameter("type", MediaObject.getTypeString(PicasaAlbumSet.this.mType)).build();
      ArrayList localArrayList1 = new ArrayList();
      Cursor localCursor = PicasaAlbumSet.this.mSource.query(localUri, PicasaAlbumSet.ALBUM_SCHEMA.getProjection(), null, null, null);
      if (localCursor == null)
      {
        Log.w("PicasaAlbumSet", "cannot open picasa database");
        return null;
      }
      label186: ArrayList localArrayList2;
      DataManager localDataManager;
      AlbumData localAlbumData1;
      while (true)
      {
        PicasaAlbumSet.AlbumInfo localAlbumInfo1;
        try
        {
          boolean bool;
          do
          {
            if (!localCursor.moveToNext())
              break label186;
            AlbumData localAlbumData2 = (AlbumData)PicasaAlbumSet.ALBUM_SCHEMA.cursorToObject(localCursor, new AlbumData());
            localAlbumData2.cacheSize = PicasaAlbumSet.access$300(PicasaAlbumSet.this.mSource, localAlbumData2.id);
            localArrayList1.add(localAlbumData2);
            bool = paramJobContext.isCancelled();
          }
          while (!bool);
          return null;
          localCursor.close();
          localArrayList2 = new ArrayList();
          localDataManager = PicasaAlbumSet.this.mSource.getApplication().getDataManager();
          LongSparseArray localLongSparseArray = new LongSparseArray();
          int i = 0;
          int j = localArrayList1.size();
          if (i >= j)
            break label486;
          localAlbumData1 = (AlbumData)localArrayList1.get(i);
          if (!"Buzz".equals(localAlbumData1.albumType))
            break label373;
          localAlbumInfo1 = (PicasaAlbumSet.AlbumInfo)localLongSparseArray.get(localAlbumData1.userId);
          if (localAlbumInfo1 != null)
            break label340;
          PicasaPostAlbum localPicasaPostAlbum = getPostAlbum(localDataManager, localAlbumData1);
          PicasaAlbumSet.AlbumInfo localAlbumInfo2 = new PicasaAlbumSet.AlbumInfo(localAlbumData1.datePublished, localPicasaPostAlbum);
          localLongSparseArray.put(localAlbumData1.userId, localAlbumInfo2);
          localArrayList2.add(localAlbumInfo2);
          label340: ++i;
        }
        finally
        {
          localCursor.close();
        }
      }
      label373: Path localPath = PicasaAlbumSet.access$400(PicasaAlbumSet.this).getChild(localAlbumData1.id);
      while (true)
      {
        PicasaAlbum localPicasaAlbum;
        synchronized (DataManager.LOCK)
        {
          localPicasaAlbum = (PicasaAlbum)localDataManager.peekMediaObject(localPath);
          if (localPicasaAlbum != null)
            break label476;
          localPicasaAlbum = new PicasaAlbum(localPath, PicasaAlbumSet.this.mSource, localAlbumData1, PicasaAlbumSet.this.mType);
          localArrayList2.add(new PicasaAlbumSet.AlbumInfo(localAlbumData1.datePublished, localPicasaAlbum));
        }
        label476: localPicasaAlbum.updateContent(localAlbumData1);
      }
      label486: Collections.sort(localArrayList2);
      int k = localArrayList2.size();
      ArrayList localArrayList3 = new ArrayList(k);
      int l = 0;
      int i1 = localArrayList2.size();
      while (true)
      {
        if (l < i1);
        MediaSet localMediaSet = ((PicasaAlbumSet.AlbumInfo)localArrayList2.get(l)).set;
        localArrayList3.add(localMediaSet);
        ++l;
      }
    }
  }

  static class PicasaSyncTaskFuture extends ContentObserver
    implements Future<Integer>
  {
    private boolean mIsCancelled = false;
    protected MediaSet.SyncListener mListener;
    protected final MediaSet mMediaSet;
    protected int mResult = -1;
    protected final PicasaSource mSource;
    protected Uri mUri;

    protected PicasaSyncTaskFuture(PicasaSource paramPicasaSource, MediaSet paramMediaSet, MediaSet.SyncListener paramSyncListener)
    {
      super(new Handler());
      this.mSource = paramPicasaSource;
      this.mMediaSet = paramMediaSet;
      this.mListener = paramSyncListener;
    }

    private void cancelInternal()
    {
      monitorenter;
      while (true)
      {
        try
        {
          boolean bool = this.mIsCancelled;
          if (bool)
            return;
          this.mIsCancelled = true;
          if (this.mUri != null)
            break label39;
        }
        finally
        {
          monitorexit;
        }
        label39: this.mSource.getContentResolver().unregisterContentObserver(this);
        try
        {
          this.mSource.getContentProvider().delete(this.mUri, null, null);
          if (this.mResult < 0);
          this.mResult = 1;
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("PicasaAlbumSet", "delete fail", localRemoteException);
        }
      }
    }

    public void cancel()
    {
      this.mSource.getApplication().getThreadPool().submit(new ThreadPool.Job()
      {
        public Void run(ThreadPool.JobContext paramJobContext)
        {
          PicasaAlbumSet.PicasaSyncTaskFuture.this.cancelInternal();
          return null;
        }
      });
    }

    public Integer get()
    {
      waitDone();
      return Integer.valueOf(this.mResult);
    }

    protected int getSyncResult()
    {
      Cursor localCursor = this.mSource.query(this.mUri, null, null, null, null);
      if (localCursor == null)
        return -1;
      try
      {
        boolean bool = localCursor.moveToFirst();
        if (!bool)
          return -1;
        int i = localCursor.getInt(0);
        switch (i)
        {
        default:
          return -1;
        case 0:
          return 0;
        case 2:
          return 2;
        case 1:
        }
        return 1;
      }
      finally
      {
        localCursor.close();
      }
    }

    public boolean isCancelled()
    {
      monitorenter;
      try
      {
        boolean bool = this.mIsCancelled;
        monitorexit;
        return bool;
      }
      finally
      {
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }

    public boolean isDone()
    {
      monitorenter;
      try
      {
        int i = this.mResult;
        if (i >= 0)
        {
          j = 1;
          return j;
        }
        int j = 0;
      }
      finally
      {
        monitorexit;
      }
    }

    public void onChange(boolean paramBoolean)
    {
      monitorenter;
      try
      {
        this.mResult = getSyncResult();
        if (this.mResult < 0)
        {
          Log.w("PicasaAlbumSet", "bad sync result: " + this.mUri.getLastPathSegment() + ": " + this.mResult);
          super.notifyAll();
          MediaSet.SyncListener localSyncListener = this.mListener;
          this.mListener = null;
          this.mSource.getContentResolver().unregisterContentObserver(this);
          monitorexit;
          if (localSyncListener != null);
          return;
        }
      }
      finally
      {
        monitorexit;
      }
    }

    void startSync(String paramString)
    {
      ContentResolver localContentResolver = this.mSource.getContentResolver();
      monitorenter;
      try
      {
        if (this.mIsCancelled)
          return;
      }
      finally
      {
        try
        {
          this.mUri = this.mSource.getPicasaFacade().requestImmediateSyncOnAlbumList(paramString);
          localContentResolver.registerContentObserver(this.mUri, false, this);
          this.mResult = getSyncResult();
          label53: int i = this.mResult;
          MediaSet.SyncListener localSyncListener = null;
          if (i >= 0)
          {
            localSyncListener = this.mListener;
            this.mListener = null;
            localContentResolver.unregisterContentObserver(this);
          }
          monitorexit;
          if (localSyncListener != null);
          localSyncListener.onSyncDone(this.mMediaSet, this.mResult);
          return;
        }
        catch (Throwable localThrowable)
        {
          Log.e("PicasaAlbumSet", "requestImmediateSyncOnAlbum: " + localThrowable);
          this.mResult = 2;
          break label53:
          localObject = finally;
          monitorexit;
          throw localObject;
        }
      }
    }

    public void waitDone()
    {
      monitorenter;
      while (true)
        try
        {
          if (isDone())
            break label45;
        }
        catch (InterruptedException localInterruptedException)
        {
          Log.d("PicasaAlbumSet", "waitDone() interrupted: " + this.mUri);
          label45: return;
        }
        finally
        {
          monitorexit;
        }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.picasasource.PicasaAlbumSet
 * JD-Core Version:    0.5.4
 */