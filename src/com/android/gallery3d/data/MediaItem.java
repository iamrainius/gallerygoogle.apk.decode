package com.android.gallery3d.data;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.ui.ScreenNail;
import com.android.gallery3d.util.ThreadPool.Job;

public abstract class MediaItem extends MediaObject
{
  private static final BytesBufferPool sMicroThumbBufferPool;
  private static BitmapPool sMicroThumbPool;
  private static int sMicrothumbnailTargetSize = 200;
  private static final BitmapPool sThumbPool;
  private static int sThumbnailTargetSize;

  static
  {
    sMicroThumbBufferPool = new BytesBufferPool(4, 204800);
    sThumbnailTargetSize = 640;
    if (ApiHelper.HAS_REUSING_BITMAP_IN_BITMAP_FACTORY);
    for (BitmapPool localBitmapPool = new BitmapPool(4); ; localBitmapPool = null)
    {
      sThumbPool = localBitmapPool;
      return;
    }
  }

  public MediaItem(Path paramPath, long paramLong)
  {
    super(paramPath, paramLong);
  }

  public static BytesBufferPool getBytesBufferPool()
  {
    return sMicroThumbBufferPool;
  }

  public static BitmapPool getMicroThumbPool()
  {
    if ((ApiHelper.HAS_REUSING_BITMAP_IN_BITMAP_FACTORY) && (sMicroThumbPool == null))
      initializeMicroThumbPool();
    return sMicroThumbPool;
  }

  public static int getTargetSize(int paramInt)
  {
    switch (paramInt)
    {
    default:
      throw new RuntimeException("should only request thumb/microthumb from cache");
    case 1:
      return sThumbnailTargetSize;
    case 2:
    }
    return sMicrothumbnailTargetSize;
  }

  public static BitmapPool getThumbPool()
  {
    return sThumbPool;
  }

  private static void initializeMicroThumbPool()
  {
    if (ApiHelper.HAS_REUSING_BITMAP_IN_BITMAP_FACTORY);
    for (BitmapPool localBitmapPool = new BitmapPool(sMicrothumbnailTargetSize, sMicrothumbnailTargetSize, 16); ; localBitmapPool = null)
    {
      sMicroThumbPool = localBitmapPool;
      return;
    }
  }

  public static void setThumbnailSizes(int paramInt1, int paramInt2)
  {
    sThumbnailTargetSize = paramInt1;
    if (sMicrothumbnailTargetSize == paramInt2)
      return;
    sMicrothumbnailTargetSize = paramInt2;
    initializeMicroThumbPool();
  }

  public long getDateInMs()
  {
    return 0L;
  }

  public Face[] getFaces()
  {
    return null;
  }

  public String getFilePath()
  {
    return "";
  }

  public int getFullImageRotation()
  {
    return getRotation();
  }

  public abstract int getHeight();

  public void getLatLong(double[] paramArrayOfDouble)
  {
    paramArrayOfDouble[0] = 0.0D;
    paramArrayOfDouble[1] = 0.0D;
  }

  public abstract String getMimeType();

  public String getName()
  {
    return null;
  }

  public int getRotation()
  {
    return 0;
  }

  public ScreenNail getScreenNail()
  {
    return null;
  }

  public long getSize()
  {
    return 0L;
  }

  public String[] getTags()
  {
    return null;
  }

  public abstract int getWidth();

  public abstract ThreadPool.Job<Bitmap> requestImage(int paramInt);

  public abstract ThreadPool.Job<BitmapRegionDecoder> requestLargeImage();
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.MediaItem
 * JD-Core Version:    0.5.4
 */