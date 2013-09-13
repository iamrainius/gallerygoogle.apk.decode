package com.android.gallery3d.data;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;

abstract class ImageCacheRequest
  implements ThreadPool.Job<Bitmap>
{
  protected GalleryApp mApplication;
  private Path mPath;
  private int mTargetSize;
  private int mType;

  public ImageCacheRequest(GalleryApp paramGalleryApp, Path paramPath, int paramInt1, int paramInt2)
  {
    this.mApplication = paramGalleryApp;
    this.mPath = paramPath;
    this.mType = paramInt1;
    this.mTargetSize = paramInt2;
  }

  private String debugTag()
  {
    StringBuilder localStringBuilder = new StringBuilder().append(this.mPath).append(",");
    if (this.mType == 1);
    for (String str = "THUMB"; ; str = "?")
      while (true)
      {
        return str;
        if (this.mType != 2)
          break;
        str = "MICROTHUMB";
      }
  }

  public abstract Bitmap onDecodeOriginal(ThreadPool.JobContext paramJobContext, int paramInt);

  public Bitmap run(ThreadPool.JobContext paramJobContext)
  {
    ImageCacheService localImageCacheService = this.mApplication.getImageCacheService();
    BytesBufferPool.BytesBuffer localBytesBuffer = MediaItem.getBytesBufferPool().get();
    Bitmap localBitmap1;
    try
    {
      boolean bool1 = localImageCacheService.getImageData(this.mPath, this.mType, localBytesBuffer);
      boolean bool2 = paramJobContext.isCancelled();
      if (bool2)
        return null;
      if (bool1)
      {
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        if (this.mType == 2);
        Bitmap localBitmap3;
        for (Object localObject2 = DecodeUtils.decode(paramJobContext, localBytesBuffer.data, localBytesBuffer.offset, localBytesBuffer.length, localOptions, MediaItem.getMicroThumbPool()); ; localObject2 = localBitmap3)
        {
          if ((localObject2 == null) && (!paramJobContext.isCancelled()))
            Log.w("ImageCacheRequest", "decode cached failed " + debugTag());
          return localObject2;
          localBitmap3 = DecodeUtils.decode(paramJobContext, localBytesBuffer.data, localBytesBuffer.offset, localBytesBuffer.length, localOptions, MediaItem.getThumbPool());
        }
      }
      MediaItem.getBytesBufferPool().recycle(localBytesBuffer);
      localBitmap1 = onDecodeOriginal(paramJobContext, this.mType);
      return null;
    }
    finally
    {
      MediaItem.getBytesBufferPool().recycle(localBytesBuffer);
    }
    if (localBitmap1 == null)
    {
      Log.w("ImageCacheRequest", "decode orig failed " + debugTag());
      return null;
    }
    if (this.mType == 2);
    for (Bitmap localBitmap2 = BitmapUtils.resizeAndCropCenter(localBitmap1, this.mTargetSize, true); paramJobContext.isCancelled(); localBitmap2 = BitmapUtils.resizeDownBySideLength(localBitmap1, this.mTargetSize, true))
      return null;
    byte[] arrayOfByte = BitmapUtils.compressToBytes(localBitmap2);
    if (paramJobContext.isCancelled())
      return null;
    localImageCacheService.putImageData(this.mPath, this.mType, arrayOfByte);
    return (Bitmap)localBitmap2;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.ImageCacheRequest
 * JD-Core Version:    0.5.4
 */