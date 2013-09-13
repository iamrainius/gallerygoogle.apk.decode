package com.android.gallery3d.app;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import com.android.camera.Util;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.DownloadCache;
import com.android.gallery3d.data.ImageCacheService;
import com.android.gallery3d.gadget.WidgetUtils;
import com.android.gallery3d.picasasource.PicasaSource;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.LightCycleHelper;
import com.android.gallery3d.util.ThreadPool;
import java.io.File;

public class GalleryAppImpl extends Application
  implements GalleryApp
{
  private DataManager mDataManager;
  private DownloadCache mDownloadCache;
  private ImageCacheService mImageCacheService;
  private Object mLock = new Object();
  private StitchingProgressManager mStitchingProgressManager;
  private ThreadPool mThreadPool;

  private void initializeAsyncTask()
  {
    try
    {
      Class.forName(AsyncTask.class.getName());
      return;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
    }
  }

  public Context getAndroidContext()
  {
    return this;
  }

  public DataManager getDataManager()
  {
    monitorenter;
    try
    {
      if (this.mDataManager == null)
      {
        this.mDataManager = new DataManager(this);
        this.mDataManager.initializeSourceMap();
      }
      DataManager localDataManager = this.mDataManager;
      return localDataManager;
    }
    finally
    {
      monitorexit;
    }
  }

  public DownloadCache getDownloadCache()
  {
    monitorenter;
    File localFile;
    try
    {
      if (this.mDownloadCache != null)
        break label93;
      localFile = new File(getExternalCacheDir(), "download");
      if (!localFile.isDirectory())
        localFile.mkdirs();
      throw new RuntimeException("fail to create: " + localFile.getAbsolutePath());
    }
    finally
    {
      monitorexit;
    }
    this.mDownloadCache = new DownloadCache(this, localFile, 67108864L);
    label93: DownloadCache localDownloadCache = this.mDownloadCache;
    monitorexit;
    return localDownloadCache;
  }

  public ImageCacheService getImageCacheService()
  {
    synchronized (this.mLock)
    {
      if (this.mImageCacheService == null)
        this.mImageCacheService = new ImageCacheService(getAndroidContext());
      ImageCacheService localImageCacheService = this.mImageCacheService;
      return localImageCacheService;
    }
  }

  public StitchingProgressManager getStitchingProgressManager()
  {
    return this.mStitchingProgressManager;
  }

  public ThreadPool getThreadPool()
  {
    monitorenter;
    try
    {
      if (this.mThreadPool == null)
        this.mThreadPool = new ThreadPool();
      ThreadPool localThreadPool = this.mThreadPool;
      return localThreadPool;
    }
    finally
    {
      monitorexit;
    }
  }

  public void onCreate()
  {
    super.onCreate();
    Util.initialize(this);
    initializeAsyncTask();
    GalleryUtils.initialize(this);
    WidgetUtils.initialize(this);
    PicasaSource.initialize(this);
    this.mStitchingProgressManager = LightCycleHelper.createStitchingManagerInstance(this);
    if (this.mStitchingProgressManager == null)
      return;
    this.mStitchingProgressManager.addChangeListener(getDataManager());
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.GalleryAppImpl
 * JD-Core Version:    0.5.4
 */