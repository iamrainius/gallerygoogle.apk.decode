package com.android.gallery3d.app;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Looper;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.DownloadCache;
import com.android.gallery3d.data.ImageCacheService;
import com.android.gallery3d.util.ThreadPool;

public abstract interface GalleryApp
{
  public abstract Context getAndroidContext();

  public abstract ContentResolver getContentResolver();

  public abstract DataManager getDataManager();

  public abstract DownloadCache getDownloadCache();

  public abstract ImageCacheService getImageCacheService();

  public abstract Looper getMainLooper();

  public abstract Resources getResources();

  public abstract StitchingProgressManager getStitchingProgressManager();

  public abstract ThreadPool getThreadPool();
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.GalleryApp
 * JD-Core Version:    0.5.4
 */