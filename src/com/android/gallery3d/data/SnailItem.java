package com.android.gallery3d.data;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import com.android.gallery3d.ui.ScreenNail;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;

public class SnailItem extends MediaItem
{
  private ScreenNail mScreenNail;

  public SnailItem(Path paramPath)
  {
    super(paramPath, nextVersionNumber());
  }

  public int getHeight()
  {
    return 0;
  }

  public String getMimeType()
  {
    return "";
  }

  public ScreenNail getScreenNail()
  {
    return this.mScreenNail;
  }

  public int getWidth()
  {
    return 0;
  }

  public ThreadPool.Job<Bitmap> requestImage(int paramInt)
  {
    return new ThreadPool.Job()
    {
      public Bitmap run(ThreadPool.JobContext paramJobContext)
      {
        return null;
      }
    };
  }

  public ThreadPool.Job<BitmapRegionDecoder> requestLargeImage()
  {
    return new ThreadPool.Job()
    {
      public BitmapRegionDecoder run(ThreadPool.JobContext paramJobContext)
      {
        return null;
      }
    };
  }

  public void setScreenNail(ScreenNail paramScreenNail)
  {
    this.mScreenNail = paramScreenNail;
  }

  public void updateVersion()
  {
    this.mDataVersion = nextVersionNumber();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.SnailItem
 * JD-Core Version:    0.5.4
 */