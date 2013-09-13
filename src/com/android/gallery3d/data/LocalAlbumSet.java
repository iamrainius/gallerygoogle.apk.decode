package com.android.gallery3d.data;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.MediaSetUtils;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class LocalAlbumSet extends MediaSet
  implements FutureListener<ArrayList<MediaSet>>
{
  public static final Path PATH_ALL = Path.fromString("/local/all");
  public static final Path PATH_IMAGE = Path.fromString("/local/image");
  public static final Path PATH_VIDEO = Path.fromString("/local/video");
  private static final Uri[] mWatchUris;
  private ArrayList<MediaSet> mAlbums = new ArrayList();
  private final GalleryApp mApplication;
  private final Handler mHandler;
  private boolean mIsLoading;
  private ArrayList<MediaSet> mLoadBuffer;
  private Future<ArrayList<MediaSet>> mLoadTask;
  private final String mName;
  private final ChangeNotifier mNotifier;
  private final int mType;

  static
  {
    Uri[] arrayOfUri = new Uri[2];
    arrayOfUri[0] = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    arrayOfUri[1] = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    mWatchUris = arrayOfUri;
  }

  public LocalAlbumSet(Path paramPath, GalleryApp paramGalleryApp)
  {
    super(paramPath, nextVersionNumber());
    this.mApplication = paramGalleryApp;
    this.mHandler = new Handler(paramGalleryApp.getMainLooper());
    this.mType = getTypeFromPath(paramPath);
    this.mNotifier = new ChangeNotifier(this, mWatchUris, paramGalleryApp);
    this.mName = paramGalleryApp.getResources().getString(2131362294);
  }

  private static <T> void circularShiftRight(T[] paramArrayOfT, int paramInt1, int paramInt2)
  {
    T ? = paramArrayOfT[paramInt2];
    for (int i = paramInt2; i > paramInt1; --i)
      paramArrayOfT[i] = paramArrayOfT[(i - 1)];
    paramArrayOfT[paramInt1] = ?;
  }

  private static int findBucket(BucketHelper.BucketEntry[] paramArrayOfBucketEntry, int paramInt)
  {
    int i = 0;
    int j = paramArrayOfBucketEntry.length;
    while (i < j)
    {
      if (paramArrayOfBucketEntry[i].bucketId == paramInt)
        return i;
      ++i;
    }
    return -1;
  }

  private MediaSet getLocalAlbum(DataManager paramDataManager, int paramInt1, Path paramPath, int paramInt2, String paramString)
  {
    Path localPath;
    synchronized (DataManager.LOCK)
    {
      localPath = paramPath.getChild(paramInt2);
      MediaObject localMediaObject = paramDataManager.peekMediaObject(localPath);
      if (localMediaObject == null)
        break label182;
      MediaSet localMediaSet = (MediaSet)localMediaObject;
      return localMediaSet;
      throw new IllegalArgumentException(String.valueOf(paramInt1));
    }
    LocalAlbum localLocalAlbum2 = new LocalAlbum(localPath, this.mApplication, paramInt2, true, paramString);
    monitorexit;
    return localLocalAlbum2;
    LocalAlbum localLocalAlbum1 = new LocalAlbum(localPath, this.mApplication, paramInt2, false, paramString);
    monitorexit;
    return localLocalAlbum1;
    Comparator localComparator = DataManager.sDateTakenComparator;
    MediaSet[] arrayOfMediaSet = new MediaSet[2];
    arrayOfMediaSet[0] = getLocalAlbum(paramDataManager, 2, PATH_IMAGE, paramInt2, paramString);
    arrayOfMediaSet[1] = getLocalAlbum(paramDataManager, 4, PATH_VIDEO, paramInt2, paramString);
    LocalMergeAlbum localLocalMergeAlbum = new LocalMergeAlbum(localPath, localComparator, arrayOfMediaSet, paramInt2);
    monitorexit;
    return localLocalMergeAlbum;
    label182: switch (paramInt1)
    {
    case 3:
    case 5:
    default:
    case 2:
    case 4:
    case 6:
    }
  }

  private static int getTypeFromPath(Path paramPath)
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

  public boolean isLoading()
  {
    monitorenter;
    try
    {
      boolean bool = this.mIsLoading;
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

  public void onFutureDone(Future<ArrayList<MediaSet>> paramFuture)
  {
    monitorenter;
    try
    {
      Future localFuture = this.mLoadTask;
      if (localFuture != paramFuture)
        return;
      this.mLoadBuffer = ((ArrayList)paramFuture.get());
      this.mIsLoading = false;
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
        this.mIsLoading = true;
        this.mLoadTask = this.mApplication.getThreadPool().submit(new AlbumsLoader(null), this);
      }
      if (this.mLoadBuffer == null)
        break label127;
      this.mAlbums = this.mLoadBuffer;
      this.mLoadBuffer = null;
      localIterator = this.mAlbums.iterator();
      if (!localIterator.hasNext())
        break label120;
    }
    finally
    {
      monitorexit;
    }
    label120: this.mDataVersion = nextVersionNumber();
    label127: long l = this.mDataVersion;
    monitorexit;
    return l;
  }

  private class AlbumsLoader
    implements ThreadPool.Job<ArrayList<MediaSet>>
  {
    private AlbumsLoader()
    {
    }

    public ArrayList<MediaSet> run(ThreadPool.JobContext paramJobContext)
    {
      BucketHelper.BucketEntry[] arrayOfBucketEntry = BucketHelper.loadBucketEntries(paramJobContext, LocalAlbumSet.this.mApplication.getContentResolver(), LocalAlbumSet.this.mType);
      if (paramJobContext.isCancelled())
      {
        localArrayList = null;
        return localArrayList;
      }
      int i = LocalAlbumSet.access$200(arrayOfBucketEntry, MediaSetUtils.CAMERA_BUCKET_ID);
      int j = 0;
      if (i != -1)
      {
        int i2 = 0 + 1;
        LocalAlbumSet.access$300(arrayOfBucketEntry, 0, i);
        j = i2;
      }
      int k = LocalAlbumSet.access$200(arrayOfBucketEntry, MediaSetUtils.DOWNLOAD_BUCKET_ID);
      if (k != -1)
      {
        (j + 1);
        LocalAlbumSet.access$300(arrayOfBucketEntry, j, k);
      }
      ArrayList localArrayList = new ArrayList();
      DataManager localDataManager = LocalAlbumSet.this.mApplication.getDataManager();
      int l = arrayOfBucketEntry.length;
      for (int i1 = 0; ; ++i1)
      {
        if (i1 < l);
        BucketHelper.BucketEntry localBucketEntry = arrayOfBucketEntry[i1];
        localArrayList.add(LocalAlbumSet.this.getLocalAlbum(localDataManager, LocalAlbumSet.this.mType, LocalAlbumSet.this.mPath, localBucketEntry.bucketId, localBucketEntry.bucketName));
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.LocalAlbumSet
 * JD-Core Version:    0.5.4
 */