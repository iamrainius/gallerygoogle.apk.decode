package com.android.gallery3d.ui;

import android.graphics.Bitmap;
import android.os.Message;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.app.AlbumDataLoader;
import com.android.gallery3d.app.AlbumDataLoader.DataListener;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.BitmapPool;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaObject.PanoramaSupportCallback;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.JobLimiter;

public class AlbumSlidingWindow
  implements AlbumDataLoader.DataListener
{
  private int mActiveEnd = 0;
  private int mActiveRequestCount = 0;
  private int mActiveStart = 0;
  private int mContentEnd = 0;
  private int mContentStart = 0;
  private final AlbumEntry[] mData;
  private final SynchronizedHandler mHandler;
  private boolean mIsActive = false;
  private Listener mListener;
  private int mSize;
  private final AlbumDataLoader mSource;
  private final JobLimiter mThreadPool;
  private final TiledTexture.Uploader mTileUploader;

  public AlbumSlidingWindow(AbstractGalleryActivity paramAbstractGalleryActivity, AlbumDataLoader paramAlbumDataLoader, int paramInt)
  {
    paramAlbumDataLoader.setDataListener(this);
    this.mSource = paramAlbumDataLoader;
    this.mData = new AlbumEntry[paramInt];
    this.mSize = paramAlbumDataLoader.size();
    this.mHandler = new SynchronizedHandler(paramAbstractGalleryActivity.getGLRoot())
    {
      public void handleMessage(Message paramMessage)
      {
        if (paramMessage.what == 0);
        for (boolean bool = true; ; bool = false)
        {
          Utils.assertTrue(bool);
          ((AlbumSlidingWindow.ThumbnailLoader)paramMessage.obj).updateEntry();
          return;
        }
      }
    };
    this.mThreadPool = new JobLimiter(paramAbstractGalleryActivity.getThreadPool(), 2);
    this.mTileUploader = new TiledTexture.Uploader(paramAbstractGalleryActivity.getGLRoot());
  }

  private void cancelNonactiveImages()
  {
    int i = Math.max(this.mContentEnd - this.mActiveEnd, this.mActiveStart - this.mContentStart);
    for (int j = 0; j < i; ++j)
    {
      cancelSlotImage(j + this.mActiveEnd);
      cancelSlotImage(-1 + this.mActiveStart - j);
    }
  }

  private void cancelSlotImage(int paramInt)
  {
    if ((paramInt < this.mContentStart) || (paramInt >= this.mContentEnd));
    AlbumEntry localAlbumEntry;
    do
    {
      return;
      localAlbumEntry = this.mData[(paramInt % this.mData.length)];
    }
    while (localAlbumEntry.contentLoader == null);
    localAlbumEntry.contentLoader.cancelLoad();
  }

  private void freeSlotContent(int paramInt)
  {
    AlbumEntry[] arrayOfAlbumEntry = this.mData;
    int i = paramInt % arrayOfAlbumEntry.length;
    AlbumEntry localAlbumEntry = arrayOfAlbumEntry[i];
    if (localAlbumEntry.contentLoader != null)
      localAlbumEntry.contentLoader.recycle();
    if (localAlbumEntry.bitmapTexture != null)
      localAlbumEntry.bitmapTexture.recycle();
    arrayOfAlbumEntry[i] = null;
  }

  private void prepareSlotContent(int paramInt)
  {
    AlbumEntry localAlbumEntry = new AlbumEntry();
    MediaItem localMediaItem = this.mSource.get(paramInt);
    localAlbumEntry.item = localMediaItem;
    int i;
    label29: Path localPath;
    if (localMediaItem == null)
    {
      i = 1;
      localAlbumEntry.mediaType = i;
      if (localMediaItem != null)
        break label105;
      localPath = null;
      label42: localAlbumEntry.path = localPath;
      if (localMediaItem != null)
        break label114;
    }
    for (int j = 0; ; j = localMediaItem.getRotation())
    {
      localAlbumEntry.rotation = j;
      AlbumEntry.access$102(localAlbumEntry, new ThumbnailLoader(paramInt, localAlbumEntry.item));
      this.mData[(paramInt % this.mData.length)] = localAlbumEntry;
      return;
      i = localAlbumEntry.item.getMediaType();
      break label29:
      label105: localPath = localMediaItem.getPath();
      label114: break label42:
    }
  }

  private void requestNonactiveImages()
  {
    int i = Math.max(this.mContentEnd - this.mActiveEnd, this.mActiveStart - this.mContentStart);
    for (int j = 0; j < i; ++j)
    {
      requestSlotImage(j + this.mActiveEnd);
      requestSlotImage(-1 + this.mActiveStart - j);
    }
  }

  private boolean requestSlotImage(int paramInt)
  {
    if ((paramInt < this.mContentStart) || (paramInt >= this.mContentEnd));
    AlbumEntry localAlbumEntry;
    do
    {
      return false;
      localAlbumEntry = this.mData[(paramInt % this.mData.length)];
    }
    while ((localAlbumEntry.content != null) || (localAlbumEntry.item == null));
    AlbumEntry.access$002(localAlbumEntry, new PanoSupportListener(localAlbumEntry));
    localAlbumEntry.item.getPanoramaSupport(localAlbumEntry.mPanoSupportListener);
    localAlbumEntry.contentLoader.startLoad();
    return localAlbumEntry.contentLoader.isRequestInProgress();
  }

  private void setContentWindow(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == this.mContentStart) && (paramInt2 == this.mContentEnd))
      return;
    if (!this.mIsActive)
    {
      this.mContentStart = paramInt1;
      this.mContentEnd = paramInt2;
      this.mSource.setActiveWindow(paramInt1, paramInt2);
      return;
    }
    if ((paramInt1 >= this.mContentEnd) || (this.mContentStart >= paramInt2))
    {
      int i = this.mContentStart;
      int j = this.mContentEnd;
      while (i < j)
      {
        freeSlotContent(i);
        ++i;
      }
      this.mSource.setActiveWindow(paramInt1, paramInt2);
      for (int k = paramInt1; ; ++k)
      {
        if (k >= paramInt2)
          break label231;
        prepareSlotContent(k);
      }
    }
    for (int l = this.mContentStart; l < paramInt1; ++l)
      freeSlotContent(l);
    int i1 = paramInt2;
    int i2 = this.mContentEnd;
    while (i1 < i2)
    {
      freeSlotContent(i1);
      ++i1;
    }
    this.mSource.setActiveWindow(paramInt1, paramInt2);
    int i3 = paramInt1;
    int i4 = this.mContentStart;
    while (i3 < i4)
    {
      prepareSlotContent(i3);
      ++i3;
    }
    for (int i5 = this.mContentEnd; i5 < paramInt2; ++i5)
      prepareSlotContent(i5);
    label231: this.mContentStart = paramInt1;
    this.mContentEnd = paramInt2;
  }

  private void updateAllImageRequests()
  {
    this.mActiveRequestCount = 0;
    int i = this.mActiveStart;
    int j = this.mActiveEnd;
    while (i < j)
    {
      if (requestSlotImage(i))
        this.mActiveRequestCount = (1 + this.mActiveRequestCount);
      ++i;
    }
    if (this.mActiveRequestCount == 0)
    {
      requestNonactiveImages();
      return;
    }
    cancelNonactiveImages();
  }

  private void updateTextureUploadQueue()
  {
    if (!this.mIsActive)
      return;
    this.mTileUploader.clear();
    int i = this.mActiveStart;
    int j = this.mActiveEnd;
    while (i < j)
    {
      AlbumEntry localAlbumEntry = this.mData[(i % this.mData.length)];
      if (localAlbumEntry.bitmapTexture != null)
        this.mTileUploader.addTexture(localAlbumEntry.bitmapTexture);
      ++i;
    }
    int k = Math.max(this.mContentEnd - this.mActiveEnd, this.mActiveStart - this.mContentStart);
    for (int l = 0; ; ++l)
    {
      if (l < k);
      uploadBgTextureInSlot(l + this.mActiveEnd);
      uploadBgTextureInSlot(-1 + (this.mActiveStart - l));
    }
  }

  private void uploadBgTextureInSlot(int paramInt)
  {
    if ((paramInt >= this.mContentEnd) || (paramInt < this.mContentStart))
      return;
    AlbumEntry localAlbumEntry = this.mData[(paramInt % this.mData.length)];
    if (localAlbumEntry.bitmapTexture == null)
      return;
    this.mTileUploader.addTexture(localAlbumEntry.bitmapTexture);
  }

  public AlbumEntry get(int paramInt)
  {
    if (!isActiveSlot(paramInt))
    {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = Integer.valueOf(paramInt);
      arrayOfObject[1] = Integer.valueOf(this.mActiveStart);
      arrayOfObject[2] = Integer.valueOf(this.mActiveEnd);
      Utils.fail("invalid slot: %s outsides (%s, %s)", arrayOfObject);
    }
    return this.mData[(paramInt % this.mData.length)];
  }

  public boolean isActiveSlot(int paramInt)
  {
    return (paramInt >= this.mActiveStart) && (paramInt < this.mActiveEnd);
  }

  public void onContentChanged(int paramInt)
  {
    if ((paramInt < this.mContentStart) || (paramInt >= this.mContentEnd) || (!this.mIsActive))
      return;
    freeSlotContent(paramInt);
    prepareSlotContent(paramInt);
    updateAllImageRequests();
    if ((this.mListener == null) || (!isActiveSlot(paramInt)))
      return;
    this.mListener.onContentChanged();
  }

  public void onSizeChanged(int paramInt)
  {
    if (this.mSize == paramInt)
      return;
    this.mSize = paramInt;
    if (this.mListener != null)
      this.mListener.onSizeChanged(this.mSize);
    if (this.mContentEnd > this.mSize)
      this.mContentEnd = this.mSize;
    if (this.mActiveEnd <= this.mSize)
      return;
    this.mActiveEnd = this.mSize;
  }

  public void pause()
  {
    this.mIsActive = false;
    this.mTileUploader.clear();
    TiledTexture.freeResources();
    int i = this.mContentStart;
    int j = this.mContentEnd;
    while (i < j)
    {
      freeSlotContent(i);
      ++i;
    }
  }

  public void resume()
  {
    this.mIsActive = true;
    TiledTexture.prepareResources();
    int i = this.mContentStart;
    int j = this.mContentEnd;
    while (i < j)
    {
      prepareSlotContent(i);
      ++i;
    }
    updateAllImageRequests();
  }

  public void setActiveWindow(int paramInt1, int paramInt2)
  {
    if ((paramInt1 > paramInt2) || (paramInt2 - paramInt1 > this.mData.length) || (paramInt2 > this.mSize))
    {
      Object[] arrayOfObject = new Object[4];
      arrayOfObject[0] = Integer.valueOf(paramInt1);
      arrayOfObject[1] = Integer.valueOf(paramInt2);
      arrayOfObject[2] = Integer.valueOf(this.mData.length);
      arrayOfObject[3] = Integer.valueOf(this.mSize);
      Utils.fail("%s, %s, %s, %s", arrayOfObject);
    }
    AlbumEntry[] arrayOfAlbumEntry = this.mData;
    this.mActiveStart = paramInt1;
    this.mActiveEnd = paramInt2;
    int i = Utils.clamp((paramInt1 + paramInt2) / 2 - arrayOfAlbumEntry.length / 2, 0, Math.max(0, this.mSize - arrayOfAlbumEntry.length));
    setContentWindow(i, Math.min(i + arrayOfAlbumEntry.length, this.mSize));
    updateTextureUploadQueue();
    if (!this.mIsActive)
      return;
    updateAllImageRequests();
  }

  public void setListener(Listener paramListener)
  {
    this.mListener = paramListener;
  }

  public static class AlbumEntry
  {
    public TiledTexture bitmapTexture;
    public Texture content;
    private BitmapLoader contentLoader;
    public boolean isPanorama;
    public boolean isWaitDisplayed;
    public MediaItem item;
    private AlbumSlidingWindow.PanoSupportListener mPanoSupportListener;
    public int mediaType;
    public Path path;
    public int rotation;
  }

  public static abstract interface Listener
  {
    public abstract void onContentChanged();

    public abstract void onSizeChanged(int paramInt);
  }

  private class PanoSupportListener
    implements MediaObject.PanoramaSupportCallback
  {
    public final AlbumSlidingWindow.AlbumEntry mEntry;

    public PanoSupportListener(AlbumSlidingWindow.AlbumEntry arg2)
    {
      Object localObject;
      this.mEntry = localObject;
    }

    public void panoramaInfoAvailable(MediaObject paramMediaObject, boolean paramBoolean1, boolean paramBoolean2)
    {
      if (this.mEntry == null)
        return;
      this.mEntry.isPanorama = paramBoolean1;
    }
  }

  private class ThumbnailLoader extends BitmapLoader
  {
    private final MediaItem mItem;
    private final int mSlotIndex;

    public ThumbnailLoader(int paramMediaItem, MediaItem arg3)
    {
      this.mSlotIndex = paramMediaItem;
      Object localObject;
      this.mItem = localObject;
    }

    protected void onLoadComplete(Bitmap paramBitmap)
    {
      AlbumSlidingWindow.this.mHandler.obtainMessage(0, this).sendToTarget();
    }

    protected void recycleBitmap(Bitmap paramBitmap)
    {
      BitmapPool localBitmapPool = MediaItem.getMicroThumbPool();
      if (localBitmapPool == null)
        return;
      localBitmapPool.recycle(paramBitmap);
    }

    protected Future<Bitmap> submitBitmapTask(FutureListener<Bitmap> paramFutureListener)
    {
      return AlbumSlidingWindow.this.mThreadPool.submit(this.mItem.requestImage(2), this);
    }

    public void updateEntry()
    {
      Bitmap localBitmap = getBitmap();
      if (localBitmap == null);
      AlbumSlidingWindow.AlbumEntry localAlbumEntry;
      do
      {
        return;
        localAlbumEntry = AlbumSlidingWindow.this.mData[(this.mSlotIndex % AlbumSlidingWindow.this.mData.length)];
        localAlbumEntry.bitmapTexture = new TiledTexture(localBitmap);
        localAlbumEntry.content = localAlbumEntry.bitmapTexture;
        if (!AlbumSlidingWindow.this.isActiveSlot(this.mSlotIndex))
          break label128;
        AlbumSlidingWindow.this.mTileUploader.addTexture(localAlbumEntry.bitmapTexture);
        AlbumSlidingWindow.access$606(AlbumSlidingWindow.this);
        if (AlbumSlidingWindow.this.mActiveRequestCount != 0)
          continue;
        AlbumSlidingWindow.this.requestNonactiveImages();
      }
      while (AlbumSlidingWindow.this.mListener == null);
      AlbumSlidingWindow.this.mListener.onContentChanged();
      return;
      label128: AlbumSlidingWindow.this.mTileUploader.addTexture(localAlbumEntry.bitmapTexture);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.AlbumSlidingWindow
 * JD-Core Version:    0.5.4
 */