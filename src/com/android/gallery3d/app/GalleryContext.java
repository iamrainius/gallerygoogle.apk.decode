package com.android.gallery3d.app;

import android.content.Context;
import com.android.gallery3d.util.ThreadPool;

public abstract interface GalleryContext
{
  public abstract Context getAndroidContext();

  public abstract ThreadPool getThreadPool();
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.GalleryContext
 * JD-Core Version:    0.5.4
 */