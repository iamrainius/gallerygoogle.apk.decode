package com.android.gallery3d.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.ui.BitmapScreenNail;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.PhotoView;
import com.android.gallery3d.ui.PhotoView.Size;
import com.android.gallery3d.ui.ScreenNail;
import com.android.gallery3d.ui.SynchronizedHandler;
import com.android.gallery3d.ui.TileImageViewAdapter;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.ThreadPool;

public class SinglePhotoDataAdapter extends TileImageViewAdapter
  implements PhotoPage.Model
{
  private BitmapScreenNail mBitmapScreenNail;
  private Handler mHandler;
  private boolean mHasFullImage;
  private MediaItem mItem;
  private FutureListener<BitmapRegionDecoder> mLargeListener = new FutureListener()
  {
    public void onFutureDone(Future<BitmapRegionDecoder> paramFuture)
    {
      BitmapRegionDecoder localBitmapRegionDecoder = (BitmapRegionDecoder)paramFuture.get();
      if (localBitmapRegionDecoder == null)
        return;
      int i = localBitmapRegionDecoder.getWidth();
      int j = localBitmapRegionDecoder.getHeight();
      BitmapFactory.Options localOptions = new BitmapFactory.Options();
      localOptions.inSampleSize = BitmapUtils.computeSampleSize(1024.0F / Math.max(i, j));
      Bitmap localBitmap = localBitmapRegionDecoder.decodeRegion(new Rect(0, 0, i, j), localOptions);
      SinglePhotoDataAdapter.this.mHandler.sendMessage(SinglePhotoDataAdapter.this.mHandler.obtainMessage(1, new SinglePhotoDataAdapter.ImageBundle(localBitmapRegionDecoder, localBitmap)));
    }
  };
  private int mLoadingState = 0;
  private PhotoView mPhotoView;
  private Future<?> mTask;
  private ThreadPool mThreadPool;
  private FutureListener<Bitmap> mThumbListener = new FutureListener()
  {
    public void onFutureDone(Future<Bitmap> paramFuture)
    {
      SinglePhotoDataAdapter.this.mHandler.sendMessage(SinglePhotoDataAdapter.this.mHandler.obtainMessage(1, paramFuture));
    }
  };

  public SinglePhotoDataAdapter(AbstractGalleryActivity paramAbstractGalleryActivity, PhotoView paramPhotoView, MediaItem paramMediaItem)
  {
    this.mItem = ((MediaItem)Utils.checkNotNull(paramMediaItem));
    if ((0x40 & paramMediaItem.getSupportedOperations()) != 0);
    for (int i = 1; ; i = 0)
    {
      this.mHasFullImage = i;
      this.mPhotoView = ((PhotoView)Utils.checkNotNull(paramPhotoView));
      this.mHandler = new SynchronizedHandler(paramAbstractGalleryActivity.getGLRoot())
      {
        public void handleMessage(Message paramMessage)
        {
          int i = 1;
          if (paramMessage.what == i);
          while (true)
          {
            Utils.assertTrue(i);
            if (!SinglePhotoDataAdapter.this.mHasFullImage)
              break;
            SinglePhotoDataAdapter.this.onDecodeLargeComplete((SinglePhotoDataAdapter.ImageBundle)paramMessage.obj);
            return;
            int j = 0;
          }
          SinglePhotoDataAdapter.this.onDecodeThumbComplete((Future)paramMessage.obj);
        }
      };
      this.mThreadPool = paramAbstractGalleryActivity.getThreadPool();
      return;
    }
  }

  private void onDecodeLargeComplete(ImageBundle paramImageBundle)
  {
    try
    {
      setScreenNail(paramImageBundle.backupImage, paramImageBundle.decoder.getWidth(), paramImageBundle.decoder.getHeight());
      setRegionDecoder(paramImageBundle.decoder);
      this.mPhotoView.notifyImageChange(0);
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("SinglePhotoDataAdapter", "fail to decode large", localThrowable);
    }
  }

  private void onDecodeThumbComplete(Future<Bitmap> paramFuture)
  {
    try
    {
      Bitmap localBitmap = (Bitmap)paramFuture.get();
      if (localBitmap == null)
      {
        this.mLoadingState = 2;
        return;
      }
      this.mLoadingState = 1;
      setScreenNail(localBitmap, localBitmap.getWidth(), localBitmap.getHeight());
      this.mPhotoView.notifyImageChange(0);
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("SinglePhotoDataAdapter", "fail to decode thumb", localThrowable);
    }
  }

  private void setScreenNail(Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    this.mBitmapScreenNail = new BitmapScreenNail(paramBitmap);
    setScreenNail(this.mBitmapScreenNail, paramInt1, paramInt2);
  }

  public int getCurrentIndex()
  {
    return 0;
  }

  public int getImageRotation(int paramInt)
  {
    if (paramInt == 0)
      return this.mItem.getFullImageRotation();
    return 0;
  }

  public void getImageSize(int paramInt, PhotoView.Size paramSize)
  {
    if (paramInt == 0)
    {
      paramSize.width = this.mItem.getWidth();
      paramSize.height = this.mItem.getHeight();
      return;
    }
    paramSize.width = 0;
    paramSize.height = 0;
  }

  public int getLoadingState(int paramInt)
  {
    return this.mLoadingState;
  }

  public MediaItem getMediaItem(int paramInt)
  {
    if (paramInt == 0)
      return this.mItem;
    return null;
  }

  public ScreenNail getScreenNail(int paramInt)
  {
    if (paramInt == 0)
      return getScreenNail();
    return null;
  }

  public boolean isCamera(int paramInt)
  {
    return false;
  }

  public boolean isDeletable(int paramInt)
  {
    return (0x1 & this.mItem.getSupportedOperations()) != 0;
  }

  public boolean isEmpty()
  {
    return false;
  }

  public boolean isPanorama(int paramInt)
  {
    return false;
  }

  public boolean isStaticCamera(int paramInt)
  {
    return false;
  }

  public boolean isVideo(int paramInt)
  {
    return this.mItem.getMediaType() == 4;
  }

  public void moveTo(int paramInt)
  {
    throw new UnsupportedOperationException();
  }

  public void pause()
  {
    Future localFuture = this.mTask;
    localFuture.cancel();
    localFuture.waitDone();
    if (localFuture.get() == null)
      this.mTask = null;
    if (this.mBitmapScreenNail == null)
      return;
    this.mBitmapScreenNail.recycle();
    this.mBitmapScreenNail = null;
  }

  public void resume()
  {
    if (this.mTask == null)
    {
      if (!this.mHasFullImage)
        break label37;
      this.mTask = this.mThreadPool.submit(this.mItem.requestLargeImage(), this.mLargeListener);
    }
    return;
    label37: this.mTask = this.mThreadPool.submit(this.mItem.requestImage(1), this.mThumbListener);
  }

  public void setCurrentPhoto(Path paramPath, int paramInt)
  {
  }

  public void setFocusHintDirection(int paramInt)
  {
  }

  public void setFocusHintPath(Path paramPath)
  {
  }

  public void setNeedFullImage(boolean paramBoolean)
  {
  }

  private static class ImageBundle
  {
    public final Bitmap backupImage;
    public final BitmapRegionDecoder decoder;

    public ImageBundle(BitmapRegionDecoder paramBitmapRegionDecoder, Bitmap paramBitmap)
    {
      this.decoder = paramBitmapRegionDecoder;
      this.backupImage = paramBitmap;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.SinglePhotoDataAdapter
 * JD-Core Version:    0.5.4
 */