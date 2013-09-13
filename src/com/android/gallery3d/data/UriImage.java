package com.android.gallery3d.data;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.app.PanoramaMetadataSupport;
import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.ThreadPool.CancelListener;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class UriImage extends MediaItem
{
  private GalleryApp mApplication;
  private DownloadCache.Entry mCacheEntry;
  private final String mContentType;
  private ParcelFileDescriptor mFileDescriptor;
  private int mHeight;
  private PanoramaMetadataSupport mPanoramaMetadata = new PanoramaMetadataSupport(this);
  private int mRotation;
  private int mState = 0;
  private final Uri mUri;
  private int mWidth;

  public UriImage(GalleryApp paramGalleryApp, Path paramPath, Uri paramUri, String paramString)
  {
    super(paramPath, nextVersionNumber());
    this.mUri = paramUri;
    this.mApplication = ((GalleryApp)Utils.checkNotNull(paramGalleryApp));
    this.mContentType = paramString;
  }

  private boolean isSharable()
  {
    return "file".equals(this.mUri.getScheme());
  }

  private void openFileOrDownloadTempFile(ThreadPool.JobContext paramJobContext)
  {
    int i = openOrDownloadInner(paramJobContext);
    monitorenter;
    try
    {
      this.mState = i;
      if ((this.mState != 2) && (this.mFileDescriptor != null))
      {
        Utils.closeSilently(this.mFileDescriptor);
        this.mFileDescriptor = null;
      }
      super.notifyAll();
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  private int openOrDownloadInner(ThreadPool.JobContext paramJobContext)
  {
    String str = this.mUri.getScheme();
    if (("content".equals(str)) || ("android.resource".equals(str)) || ("file".equals(str)))
      try
      {
        if ("image/jpeg".equalsIgnoreCase(this.mContentType))
        {
          InputStream localInputStream = this.mApplication.getContentResolver().openInputStream(this.mUri);
          this.mRotation = Exif.getOrientation(localInputStream);
          Utils.closeSilently(localInputStream);
        }
        this.mFileDescriptor = this.mApplication.getContentResolver().openFileDescriptor(this.mUri, "r");
        boolean bool = paramJobContext.isCancelled();
        if (bool)
          return 0;
        return 2;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        Log.w("UriImage", "fail to open: " + this.mUri, localFileNotFoundException);
        return -1;
      }
    try
    {
      URL localURL = new URI(this.mUri.toString()).toURL();
      this.mCacheEntry = this.mApplication.getDownloadCache().download(paramJobContext, localURL);
      if (!paramJobContext.isCancelled());
      if (this.mCacheEntry == null)
      {
        Log.w("UriImage", "download failed " + localURL);
        return -1;
      }
      if ("image/jpeg".equalsIgnoreCase(this.mContentType))
      {
        FileInputStream localFileInputStream = new FileInputStream(this.mCacheEntry.cacheFile);
        this.mRotation = Exif.getOrientation(localFileInputStream);
        Utils.closeSilently(localFileInputStream);
      }
      this.mFileDescriptor = ParcelFileDescriptor.open(this.mCacheEntry.cacheFile, 268435456);
      return 2;
    }
    catch (Throwable localThrowable)
    {
      Log.w("UriImage", "download error", localThrowable);
    }
    return -1;
  }

  private boolean prepareInputFile(ThreadPool.JobContext paramJobContext)
  {
    paramJobContext.setCancelListener(new ThreadPool.CancelListener()
    {
      public void onCancel()
      {
        monitorenter;
        try
        {
          super.notifyAll();
          return;
        }
        finally
        {
          monitorexit;
        }
      }
    });
    while (true)
    {
      monitorenter;
      try
      {
        if (paramJobContext.isCancelled())
          return false;
        if (this.mState == 0)
        {
          this.mState = 1;
          monitorexit;
          openFileOrDownloadTempFile(paramJobContext);
        }
        if (this.mState == -1)
          return false;
      }
      finally
      {
        monitorexit;
      }
      if (this.mState == 2)
      {
        monitorexit;
        return true;
      }
      try
      {
        super.wait();
        label84: monitorexit;
      }
      catch (InterruptedException localInterruptedException)
      {
        break label84:
      }
    }
  }

  public void clearCachedPanoramaSupport()
  {
    this.mPanoramaMetadata.clearCachedValues();
  }

  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mFileDescriptor != null)
        Utils.closeSilently(this.mFileDescriptor);
      return;
    }
    finally
    {
      super.finalize();
    }
  }

  public Uri getContentUri()
  {
    return this.mUri;
  }

  public MediaDetails getDetails()
  {
    MediaDetails localMediaDetails = super.getDetails();
    if ((this.mWidth != 0) && (this.mHeight != 0))
    {
      localMediaDetails.addDetail(5, Integer.valueOf(this.mWidth));
      localMediaDetails.addDetail(6, Integer.valueOf(this.mHeight));
    }
    if (this.mContentType != null)
      localMediaDetails.addDetail(9, this.mContentType);
    if ("file".equals(this.mUri.getScheme()))
    {
      String str = this.mUri.getPath();
      localMediaDetails.addDetail(200, str);
      MediaDetails.extractExifInfo(localMediaDetails, str);
    }
    return localMediaDetails;
  }

  public int getHeight()
  {
    return 0;
  }

  public int getMediaType()
  {
    return 2;
  }

  public String getMimeType()
  {
    return this.mContentType;
  }

  public void getPanoramaSupport(MediaObject.PanoramaSupportCallback paramPanoramaSupportCallback)
  {
    this.mPanoramaMetadata.getPanoramaSupport(this.mApplication, paramPanoramaSupportCallback);
  }

  public int getRotation()
  {
    return this.mRotation;
  }

  public int getSupportedOperations()
  {
    int i = 544;
    if (isSharable())
      i |= 4;
    if (BitmapUtils.isSupportedByRegionDecoder(this.mContentType))
      i |= 64;
    return i;
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
    return new RegionDecoderJob(null);
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
      if (UriImage.this.prepareInputFile(paramJobContext) == 0)
        return null;
      int i = MediaItem.getTargetSize(this.mType);
      BitmapFactory.Options localOptions = new BitmapFactory.Options();
      localOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
      Bitmap localBitmap = DecodeUtils.decodeThumbnail(paramJobContext, UriImage.this.mFileDescriptor.getFileDescriptor(), localOptions, i, this.mType);
      if ((paramJobContext.isCancelled()) || (localBitmap == null))
        return null;
      if (this.mType == 2)
        return BitmapUtils.resizeAndCropCenter(localBitmap, i, true);
      return BitmapUtils.resizeDownBySideLength(localBitmap, i, true);
    }
  }

  private class RegionDecoderJob
    implements ThreadPool.Job<BitmapRegionDecoder>
  {
    private RegionDecoderJob()
    {
    }

    public BitmapRegionDecoder run(ThreadPool.JobContext paramJobContext)
    {
      if (UriImage.this.prepareInputFile(paramJobContext) == 0)
        return null;
      BitmapRegionDecoder localBitmapRegionDecoder = DecodeUtils.createBitmapRegionDecoder(paramJobContext, UriImage.this.mFileDescriptor.getFileDescriptor(), false);
      UriImage.access$302(UriImage.this, localBitmapRegionDecoder.getWidth());
      UriImage.access$402(UriImage.this, localBitmapRegionDecoder.getHeight());
      return localBitmapRegionDecoder;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.UriImage
 * JD-Core Version:    0.5.4
 */