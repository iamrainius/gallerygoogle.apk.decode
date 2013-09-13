package com.android.gallery3d.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.app.AlbumSetDataLoader;
import com.android.gallery3d.app.AlbumSetDataLoader.DataListener;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.BitmapPool;
import com.android.gallery3d.data.DataSourceType;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.ThreadPool;

public class AlbumSetSlidingWindow
  implements AlbumSetDataLoader.DataListener
{
  private int mActiveEnd = 0;
  private int mActiveRequestCount = 0;
  private int mActiveStart = 0;
  private int mContentEnd = 0;
  private int mContentStart = 0;
  private final TiledTexture.Uploader mContentUploader;
  private final AlbumSetEntry[] mData;
  private final SynchronizedHandler mHandler;
  private boolean mIsActive = false;
  private final AlbumLabelMaker mLabelMaker;
  private final TextureUploader mLabelUploader;
  private Listener mListener;
  private BitmapTexture mLoadingLabel;
  private final String mLoadingText;
  private int mSize;
  private int mSlotWidth;
  private final AlbumSetDataLoader mSource;
  private final ThreadPool mThreadPool;

  public AlbumSetSlidingWindow(AbstractGalleryActivity paramAbstractGalleryActivity, AlbumSetDataLoader paramAlbumSetDataLoader, AlbumSetSlotRenderer.LabelSpec paramLabelSpec, int paramInt)
  {
    paramAlbumSetDataLoader.setModelListener(this);
    this.mSource = paramAlbumSetDataLoader;
    this.mData = new AlbumSetEntry[paramInt];
    this.mSize = paramAlbumSetDataLoader.size();
    this.mThreadPool = paramAbstractGalleryActivity.getThreadPool();
    this.mLabelMaker = new AlbumLabelMaker(paramAbstractGalleryActivity.getAndroidContext(), paramLabelSpec);
    this.mLoadingText = paramAbstractGalleryActivity.getAndroidContext().getString(2131362189);
    this.mContentUploader = new TiledTexture.Uploader(paramAbstractGalleryActivity.getGLRoot());
    this.mLabelUploader = new TextureUploader(paramAbstractGalleryActivity.getGLRoot());
    this.mHandler = new SynchronizedHandler(paramAbstractGalleryActivity.getGLRoot())
    {
      public void handleMessage(Message paramMessage)
      {
        int i = 1;
        if (paramMessage.what == i);
        while (true)
        {
          Utils.assertTrue(i);
          ((AlbumSetSlidingWindow.EntryUpdater)paramMessage.obj).updateEntry();
          return;
          int j = 0;
        }
      }
    };
  }

  private void cancelImagesInSlot(int paramInt)
  {
    if ((paramInt < this.mContentStart) || (paramInt >= this.mContentEnd));
    AlbumSetEntry localAlbumSetEntry;
    do
    {
      return;
      localAlbumSetEntry = this.mData[(paramInt % this.mData.length)];
      if (localAlbumSetEntry.coverLoader == null)
        continue;
      localAlbumSetEntry.coverLoader.cancelLoad();
    }
    while (localAlbumSetEntry.labelLoader == null);
    localAlbumSetEntry.labelLoader.cancelLoad();
  }

  private void cancelNonactiveImages()
  {
    int i = Math.max(this.mContentEnd - this.mActiveEnd, this.mActiveStart - this.mContentStart);
    for (int j = 0; j < i; ++j)
    {
      cancelImagesInSlot(j + this.mActiveEnd);
      cancelImagesInSlot(-1 + this.mActiveStart - j);
    }
  }

  private void freeSlotContent(int paramInt)
  {
    AlbumSetEntry localAlbumSetEntry = this.mData[(paramInt % this.mData.length)];
    if (localAlbumSetEntry.coverLoader != null)
      localAlbumSetEntry.coverLoader.recycle();
    if (localAlbumSetEntry.labelLoader != null)
      localAlbumSetEntry.labelLoader.recycle();
    if (localAlbumSetEntry.labelTexture != null)
      localAlbumSetEntry.labelTexture.recycle();
    if (localAlbumSetEntry.bitmapTexture != null)
      localAlbumSetEntry.bitmapTexture.recycle();
    this.mData[(paramInt % this.mData.length)] = null;
  }

  private static long getDataVersion(MediaObject paramMediaObject)
  {
    if (paramMediaObject == null)
      return -1L;
    return paramMediaObject.getDataVersion();
  }

  private static int identifyCacheFlag(MediaSet paramMediaSet)
  {
    if ((paramMediaSet == null) || ((0x100 & paramMediaSet.getSupportedOperations()) == 0))
      return 0;
    return paramMediaSet.getCacheFlag();
  }

  private static int identifyCacheStatus(MediaSet paramMediaSet)
  {
    if ((paramMediaSet == null) || ((0x100 & paramMediaSet.getSupportedOperations()) == 0))
      return 0;
    return paramMediaSet.getCacheStatus();
  }

  private boolean isLabelChanged(AlbumSetEntry paramAlbumSetEntry, String paramString, int paramInt1, int paramInt2)
  {
    return (!Utils.equals(paramAlbumSetEntry.title, paramString)) || (paramAlbumSetEntry.totalCount != paramInt1) || (paramAlbumSetEntry.sourceType != paramInt2);
  }

  private void prepareSlotContent(int paramInt)
  {
    AlbumSetEntry localAlbumSetEntry = new AlbumSetEntry();
    updateAlbumSetEntry(localAlbumSetEntry, paramInt);
    this.mData[(paramInt % this.mData.length)] = localAlbumSetEntry;
  }

  private void requestImagesInSlot(int paramInt)
  {
    if ((paramInt < this.mContentStart) || (paramInt >= this.mContentEnd));
    AlbumSetEntry localAlbumSetEntry;
    do
    {
      return;
      localAlbumSetEntry = this.mData[(paramInt % this.mData.length)];
      if (localAlbumSetEntry.coverLoader == null)
        continue;
      localAlbumSetEntry.coverLoader.startLoad();
    }
    while (localAlbumSetEntry.labelLoader == null);
    localAlbumSetEntry.labelLoader.startLoad();
  }

  private void requestNonactiveImages()
  {
    int i = Math.max(this.mContentEnd - this.mActiveEnd, this.mActiveStart - this.mContentStart);
    for (int j = 0; j < i; ++j)
    {
      requestImagesInSlot(j + this.mActiveEnd);
      requestImagesInSlot(-1 + this.mActiveStart - j);
    }
  }

  private void setContentWindow(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == this.mContentStart) && (paramInt2 == this.mContentEnd))
      return;
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
          break label204;
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
    label204: this.mContentStart = paramInt1;
    this.mContentEnd = paramInt2;
  }

  private static boolean startLoadBitmap(BitmapLoader paramBitmapLoader)
  {
    if (paramBitmapLoader == null)
      return false;
    paramBitmapLoader.startLoad();
    return paramBitmapLoader.isRequestInProgress();
  }

  private void updateAlbumSetEntry(AlbumSetEntry paramAlbumSetEntry, int paramInt)
  {
    MediaSet localMediaSet = this.mSource.getMediaSet(paramInt);
    MediaItem localMediaItem = this.mSource.getCoverItem(paramInt);
    int i = this.mSource.getTotalCount(paramInt);
    paramAlbumSetEntry.album = localMediaSet;
    paramAlbumSetEntry.setDataVersion = getDataVersion(localMediaSet);
    paramAlbumSetEntry.cacheFlag = identifyCacheFlag(localMediaSet);
    paramAlbumSetEntry.cacheStatus = identifyCacheStatus(localMediaSet);
    Path localPath;
    label65: String str;
    if (localMediaSet == null)
    {
      localPath = null;
      paramAlbumSetEntry.setPath = localPath;
      if (localMediaSet != null)
        break label270;
      str = "";
      label80: int j = DataSourceType.identifySourceType(localMediaSet);
      if (isLabelChanged(paramAlbumSetEntry, str, i, j))
      {
        paramAlbumSetEntry.title = str;
        paramAlbumSetEntry.totalCount = i;
        paramAlbumSetEntry.sourceType = j;
        if (paramAlbumSetEntry.labelLoader != null)
        {
          paramAlbumSetEntry.labelLoader.recycle();
          AlbumSetEntry.access$102(paramAlbumSetEntry, null);
          paramAlbumSetEntry.labelTexture = null;
        }
        if (localMediaSet != null)
          AlbumSetEntry.access$102(paramAlbumSetEntry, new AlbumLabelLoader(paramInt, str, i, j));
      }
      paramAlbumSetEntry.coverItem = localMediaItem;
      if (getDataVersion(localMediaItem) != paramAlbumSetEntry.coverDataVersion)
      {
        paramAlbumSetEntry.coverDataVersion = getDataVersion(localMediaItem);
        if (localMediaItem != null)
          break label282;
      }
    }
    for (int k = 0; ; k = localMediaItem.getRotation())
    {
      paramAlbumSetEntry.rotation = k;
      if (paramAlbumSetEntry.coverLoader != null)
      {
        paramAlbumSetEntry.coverLoader.recycle();
        AlbumSetEntry.access$002(paramAlbumSetEntry, null);
        paramAlbumSetEntry.bitmapTexture = null;
        paramAlbumSetEntry.content = null;
      }
      if (localMediaItem != null)
        AlbumSetEntry.access$002(paramAlbumSetEntry, new AlbumCoverLoader(paramInt, localMediaItem));
      return;
      localPath = localMediaSet.getPath();
      break label65:
      label270: str = Utils.ensureNotNull(localMediaSet.getName());
      label282: break label80:
    }
  }

  private void updateAllImageRequests()
  {
    this.mActiveRequestCount = 0;
    int i = this.mActiveStart;
    int j = this.mActiveEnd;
    while (i < j)
    {
      AlbumSetEntry localAlbumSetEntry = this.mData[(i % this.mData.length)];
      if (startLoadBitmap(localAlbumSetEntry.coverLoader))
        this.mActiveRequestCount = (1 + this.mActiveRequestCount);
      if (startLoadBitmap(localAlbumSetEntry.labelLoader))
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
    this.mContentUploader.clear();
    this.mLabelUploader.clear();
    int i = this.mActiveStart;
    int j = this.mActiveEnd;
    while (i < j)
    {
      AlbumSetEntry localAlbumSetEntry = this.mData[(i % this.mData.length)];
      if (localAlbumSetEntry.bitmapTexture != null)
        this.mContentUploader.addTexture(localAlbumSetEntry.bitmapTexture);
      if (localAlbumSetEntry.labelTexture != null)
        this.mLabelUploader.addFgTexture(localAlbumSetEntry.labelTexture);
      ++i;
    }
    int k = Math.max(this.mContentEnd - this.mActiveEnd, this.mActiveStart - this.mContentStart);
    for (int l = 0; ; ++l)
    {
      if (l < k);
      uploadBackgroundTextureInSlot(l + this.mActiveEnd);
      uploadBackgroundTextureInSlot(-1 + (this.mActiveStart - l));
    }
  }

  private void uploadBackgroundTextureInSlot(int paramInt)
  {
    if ((paramInt < this.mContentStart) || (paramInt >= this.mContentEnd));
    AlbumSetEntry localAlbumSetEntry;
    do
    {
      return;
      localAlbumSetEntry = this.mData[(paramInt % this.mData.length)];
      if (localAlbumSetEntry.bitmapTexture == null)
        continue;
      this.mContentUploader.addTexture(localAlbumSetEntry.bitmapTexture);
    }
    while (localAlbumSetEntry.labelTexture == null);
    this.mLabelUploader.addBgTexture(localAlbumSetEntry.labelTexture);
  }

  public AlbumSetEntry get(int paramInt)
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
    if (!this.mIsActive);
    do
    {
      return;
      if ((paramInt < this.mContentStart) || (paramInt >= this.mContentEnd))
      {
        Object[] arrayOfObject = new Object[3];
        arrayOfObject[0] = Integer.valueOf(paramInt);
        arrayOfObject[1] = Integer.valueOf(this.mContentStart);
        arrayOfObject[2] = Integer.valueOf(this.mContentEnd);
        Log.w("AlbumSetSlidingWindow", String.format("invalid update: %s is outside (%s, %s)", arrayOfObject));
        return;
      }
      updateAlbumSetEntry(this.mData[(paramInt % this.mData.length)], paramInt);
      updateAllImageRequests();
      updateTextureUploadQueue();
    }
    while ((this.mListener == null) || (!isActiveSlot(paramInt)));
    this.mListener.onContentChanged();
  }

  public void onSizeChanged(int paramInt)
  {
    if ((!this.mIsActive) || (this.mSize == paramInt))
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

  public void onSlotSizeChanged(int paramInt1, int paramInt2)
  {
    if (this.mSlotWidth == paramInt1);
    do
    {
      return;
      this.mSlotWidth = paramInt1;
      this.mLoadingLabel = null;
      this.mLabelMaker.setLabelWidth(this.mSlotWidth);
    }
    while (!this.mIsActive);
    int i = this.mContentStart;
    int j = this.mContentEnd;
    while (i < j)
    {
      AlbumSetEntry localAlbumSetEntry = this.mData[(i % this.mData.length)];
      if (localAlbumSetEntry.labelLoader != null)
      {
        localAlbumSetEntry.labelLoader.recycle();
        AlbumSetEntry.access$102(localAlbumSetEntry, null);
        localAlbumSetEntry.labelTexture = null;
      }
      if (localAlbumSetEntry.album != null)
        AlbumSetEntry.access$102(localAlbumSetEntry, new AlbumLabelLoader(i, localAlbumSetEntry.title, localAlbumSetEntry.totalCount, localAlbumSetEntry.sourceType));
      ++i;
    }
    updateAllImageRequests();
    updateTextureUploadQueue();
  }

  public void pause()
  {
    this.mIsActive = false;
    this.mLabelUploader.clear();
    this.mContentUploader.clear();
    TiledTexture.freeResources();
    int i = this.mContentStart;
    int j = this.mContentEnd;
    while (i < j)
    {
      freeSlotContent(i);
      ++i;
    }
    this.mLabelMaker.clearRecycledLabels();
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
      Utils.fail("start = %s, end = %s, length = %s, size = %s", arrayOfObject);
    }
    AlbumSetEntry[] arrayOfAlbumSetEntry = this.mData;
    this.mActiveStart = paramInt1;
    this.mActiveEnd = paramInt2;
    int i = Utils.clamp((paramInt1 + paramInt2) / 2 - arrayOfAlbumSetEntry.length / 2, 0, Math.max(0, this.mSize - arrayOfAlbumSetEntry.length));
    setContentWindow(i, Math.min(i + arrayOfAlbumSetEntry.length, this.mSize));
    if (!this.mIsActive)
      return;
    updateTextureUploadQueue();
    updateAllImageRequests();
  }

  public void setListener(Listener paramListener)
  {
    this.mListener = paramListener;
  }

  public int size()
  {
    return this.mSize;
  }

  private class AlbumCoverLoader extends BitmapLoader
    implements AlbumSetSlidingWindow.EntryUpdater
  {
    private MediaItem mMediaItem;
    private final int mSlotIndex;

    public AlbumCoverLoader(int paramMediaItem, MediaItem arg3)
    {
      this.mSlotIndex = paramMediaItem;
      Object localObject;
      this.mMediaItem = localObject;
    }

    protected void onLoadComplete(Bitmap paramBitmap)
    {
      AlbumSetSlidingWindow.this.mHandler.obtainMessage(1, this).sendToTarget();
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
      return AlbumSetSlidingWindow.this.mThreadPool.submit(this.mMediaItem.requestImage(2), paramFutureListener);
    }

    public void updateEntry()
    {
      Bitmap localBitmap = getBitmap();
      if (localBitmap == null);
      TiledTexture localTiledTexture;
      do
      {
        return;
        AlbumSetSlidingWindow.AlbumSetEntry localAlbumSetEntry = AlbumSetSlidingWindow.this.mData[(this.mSlotIndex % AlbumSetSlidingWindow.this.mData.length)];
        localTiledTexture = new TiledTexture(localBitmap);
        localAlbumSetEntry.bitmapTexture = localTiledTexture;
        localAlbumSetEntry.content = localTiledTexture;
        if (!AlbumSetSlidingWindow.this.isActiveSlot(this.mSlotIndex))
          break label124;
        AlbumSetSlidingWindow.this.mContentUploader.addTexture(localTiledTexture);
        AlbumSetSlidingWindow.access$606(AlbumSetSlidingWindow.this);
        if (AlbumSetSlidingWindow.this.mActiveRequestCount != 0)
          continue;
        AlbumSetSlidingWindow.this.requestNonactiveImages();
      }
      while (AlbumSetSlidingWindow.this.mListener == null);
      AlbumSetSlidingWindow.this.mListener.onContentChanged();
      return;
      label124: AlbumSetSlidingWindow.this.mContentUploader.addTexture(localTiledTexture);
    }
  }

  private class AlbumLabelLoader extends BitmapLoader
    implements AlbumSetSlidingWindow.EntryUpdater
  {
    private final int mSlotIndex;
    private final int mSourceType;
    private final String mTitle;
    private final int mTotalCount;

    public AlbumLabelLoader(int paramString, String paramInt1, int paramInt2, int arg5)
    {
      this.mSlotIndex = paramString;
      this.mTitle = paramInt1;
      this.mTotalCount = paramInt2;
      int i;
      this.mSourceType = i;
    }

    protected void onLoadComplete(Bitmap paramBitmap)
    {
      AlbumSetSlidingWindow.this.mHandler.obtainMessage(1, this).sendToTarget();
    }

    protected void recycleBitmap(Bitmap paramBitmap)
    {
      AlbumSetSlidingWindow.this.mLabelMaker.recycleLabel(paramBitmap);
    }

    protected Future<Bitmap> submitBitmapTask(FutureListener<Bitmap> paramFutureListener)
    {
      return AlbumSetSlidingWindow.this.mThreadPool.submit(AlbumSetSlidingWindow.this.mLabelMaker.requestLabel(this.mTitle, String.valueOf(this.mTotalCount), this.mSourceType), paramFutureListener);
    }

    public void updateEntry()
    {
      Bitmap localBitmap = getBitmap();
      if (localBitmap == null);
      BitmapTexture localBitmapTexture;
      do
      {
        return;
        AlbumSetSlidingWindow.AlbumSetEntry localAlbumSetEntry = AlbumSetSlidingWindow.this.mData[(this.mSlotIndex % AlbumSetSlidingWindow.this.mData.length)];
        localBitmapTexture = new BitmapTexture(localBitmap);
        localBitmapTexture.setOpaque(false);
        localAlbumSetEntry.labelTexture = localBitmapTexture;
        if (!AlbumSetSlidingWindow.this.isActiveSlot(this.mSlotIndex))
          break label124;
        AlbumSetSlidingWindow.this.mLabelUploader.addFgTexture(localBitmapTexture);
        AlbumSetSlidingWindow.access$606(AlbumSetSlidingWindow.this);
        if (AlbumSetSlidingWindow.this.mActiveRequestCount != 0)
          continue;
        AlbumSetSlidingWindow.this.requestNonactiveImages();
      }
      while (AlbumSetSlidingWindow.this.mListener == null);
      AlbumSetSlidingWindow.this.mListener.onContentChanged();
      return;
      label124: AlbumSetSlidingWindow.this.mLabelUploader.addBgTexture(localBitmapTexture);
    }
  }

  public static class AlbumSetEntry
  {
    public MediaSet album;
    public TiledTexture bitmapTexture;
    public int cacheFlag;
    public int cacheStatus;
    public Texture content;
    public long coverDataVersion;
    public MediaItem coverItem;
    private BitmapLoader coverLoader;
    public boolean isWaitLoadingDisplayed;
    private BitmapLoader labelLoader;
    public BitmapTexture labelTexture;
    public int rotation;
    public long setDataVersion;
    public Path setPath;
    public int sourceType;
    public String title;
    public int totalCount;
  }

  private static abstract interface EntryUpdater
  {
    public abstract void updateEntry();
  }

  public static abstract interface Listener
  {
    public abstract void onContentChanged();

    public abstract void onSizeChanged(int paramInt);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.AlbumSetSlidingWindow
 * JD-Core Version:    0.5.4
 */