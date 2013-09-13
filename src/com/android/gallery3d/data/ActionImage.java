package com.android.gallery3d.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;

public class ActionImage extends MediaItem
{
  private GalleryApp mApplication;
  private int mResourceId;

  public ActionImage(Path paramPath, GalleryApp paramGalleryApp, int paramInt)
  {
    super(paramPath, nextVersionNumber());
    this.mApplication = ((GalleryApp)Utils.checkNotNull(paramGalleryApp));
    this.mResourceId = paramInt;
  }

  public Uri getContentUri()
  {
    return null;
  }

  public int getHeight()
  {
    return 0;
  }

  public int getMediaType()
  {
    return 1;
  }

  public String getMimeType()
  {
    return "";
  }

  public int getSupportedOperations()
  {
    return 32768;
  }

  public int getWidth()
  {
    return 0;
  }

  public ThreadPool.Job<Bitmap> requestImage(int paramInt)
  {
    return new BitmapJob(paramInt);
  }

  public ThreadPool.Job<BitmapRegionDecoder> requestLargeImage()
  {
    return null;
  }

  private class BitmapJob
    implements ThreadPool.Job<Bitmap>
  {
    private int mType;

    protected BitmapJob(int arg2)
    {
      int i;
      this.mType = i;
    }

    public Bitmap run(ThreadPool.JobContext paramJobContext)
    {
      int i = MediaItem.getTargetSize(this.mType);
      Bitmap localBitmap = BitmapFactory.decodeResource(ActionImage.this.mApplication.getResources(), ActionImage.this.mResourceId);
      if (this.mType == 2)
        return BitmapUtils.resizeAndCropCenter(localBitmap, i, true);
      return BitmapUtils.resizeDownBySideLength(localBitmap, i, true);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.ActionImage
 * JD-Core Version:    0.5.4
 */