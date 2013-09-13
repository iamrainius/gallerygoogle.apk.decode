package com.android.gallery3d.app;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.os.Handler;
import android.os.Message;
import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.BitmapPool;
import com.android.gallery3d.data.ContentListener;
import com.android.gallery3d.data.LocalMediaItem;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.PhotoView;
import com.android.gallery3d.ui.PhotoView.Size;
import com.android.gallery3d.ui.ScreenNail;
import com.android.gallery3d.ui.SynchronizedHandler;
import com.android.gallery3d.ui.TileImageViewAdapter;
import com.android.gallery3d.ui.TiledScreenNail;
import com.android.gallery3d.ui.TiledTexture;
import com.android.gallery3d.ui.TiledTexture.Uploader;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.MediaSetUtils;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class PhotoDataAdapter
  implements PhotoPage.Model
{
  private static ImageFetch[] sImageFetchSeq = new ImageFetch[16];
  private int mActiveEnd = 0;
  private int mActiveStart = 0;
  private int mCameraIndex;
  private final long[] mChanges = new long[7];
  private int mContentEnd = 0;
  private int mContentStart = 0;
  private int mCurrentIndex;
  private final MediaItem[] mData = new MediaItem[256];
  private DataListener mDataListener;
  private int mFocusHintDirection = 0;
  private Path mFocusHintPath = null;
  private HashMap<Path, ImageEntry> mImageCache = new HashMap();
  private boolean mIsActive;
  private boolean mIsPanorama;
  private boolean mIsStaticCamera;
  private Path mItemPath;
  private final Handler mMainHandler;
  private boolean mNeedFullImage;
  private final Path[] mPaths = new Path[7];
  private final PhotoView mPhotoView;
  private ReloadTask mReloadTask;
  private int mSize = 0;
  private final MediaSet mSource;
  private final SourceListener mSourceListener = new SourceListener(null);
  private long mSourceVersion = -1L;
  private final ThreadPool mThreadPool;
  private final TileImageViewAdapter mTileProvider = new TileImageViewAdapter();
  private final TiledTexture.Uploader mUploader;

  static
  {
    ImageFetch[] arrayOfImageFetch1 = sImageFetchSeq;
    int i = 0 + 1;
    arrayOfImageFetch1[0] = new ImageFetch(0, 1);
    for (int j = 1; j < 7; ++j)
    {
      ImageFetch[] arrayOfImageFetch5 = sImageFetchSeq;
      int i1 = i + 1;
      arrayOfImageFetch5[i] = new ImageFetch(j, 1);
      ImageFetch[] arrayOfImageFetch6 = sImageFetchSeq;
      i = i1 + 1;
      arrayOfImageFetch6[i1] = new ImageFetch(-j, 1);
    }
    ImageFetch[] arrayOfImageFetch2 = sImageFetchSeq;
    int k = i + 1;
    arrayOfImageFetch2[i] = new ImageFetch(0, 2);
    ImageFetch[] arrayOfImageFetch3 = sImageFetchSeq;
    int l = k + 1;
    arrayOfImageFetch3[k] = new ImageFetch(1, 2);
    ImageFetch[] arrayOfImageFetch4 = sImageFetchSeq;
    (l + 1);
    arrayOfImageFetch4[l] = new ImageFetch(-1, 2);
  }

  public PhotoDataAdapter(AbstractGalleryActivity paramAbstractGalleryActivity, PhotoView paramPhotoView, MediaSet paramMediaSet, Path paramPath, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mSource = ((MediaSet)Utils.checkNotNull(paramMediaSet));
    this.mPhotoView = ((PhotoView)Utils.checkNotNull(paramPhotoView));
    this.mItemPath = ((Path)Utils.checkNotNull(paramPath));
    this.mCurrentIndex = paramInt1;
    this.mCameraIndex = paramInt2;
    this.mIsPanorama = paramBoolean1;
    this.mIsStaticCamera = paramBoolean2;
    this.mThreadPool = paramAbstractGalleryActivity.getThreadPool();
    this.mNeedFullImage = true;
    Arrays.fill(this.mChanges, -1L);
    this.mUploader = new TiledTexture.Uploader(paramAbstractGalleryActivity.getGLRoot());
    this.mMainHandler = new SynchronizedHandler(paramAbstractGalleryActivity.getGLRoot())
    {
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default:
          throw new AssertionError();
        case 3:
          ((Runnable)paramMessage.obj).run();
        case 1:
        case 2:
          do
          {
            do
              return;
            while (PhotoDataAdapter.this.mDataListener == null);
            PhotoDataAdapter.this.mDataListener.onLoadingStarted();
            return;
          }
          while (PhotoDataAdapter.this.mDataListener == null);
          PhotoDataAdapter.this.mDataListener.onLoadingFinished();
          return;
        case 4:
        }
        PhotoDataAdapter.this.updateImageRequests();
      }
    };
    updateSlidingWindow();
  }

  private <T> T executeAndWait(Callable<T> paramCallable)
  {
    FutureTask localFutureTask = new FutureTask(paramCallable);
    this.mMainHandler.sendMessage(this.mMainHandler.obtainMessage(3, localFutureTask));
    try
    {
      Object localObject = localFutureTask.get();
      return localObject;
    }
    catch (InterruptedException localInterruptedException)
    {
      return null;
    }
    catch (ExecutionException localExecutionException)
    {
      throw new RuntimeException(localExecutionException);
    }
  }

  private void fireDataChange()
  {
    int i = 0;
    for (int j = -3; j <= 3; ++j)
    {
      long l1 = getVersion(j + this.mCurrentIndex);
      if (this.mChanges[(j + 3)] == l1)
        continue;
      this.mChanges[(j + 3)] = l1;
      i = 1;
    }
    if (i == 0)
      return;
    int[] arrayOfInt = new int[7];
    Path[] arrayOfPath = new Path[7];
    System.arraycopy(this.mPaths, 0, arrayOfPath, 0, 7);
    for (int k = 0; k < 7; ++k)
      this.mPaths[k] = getPath(-3 + (k + this.mCurrentIndex));
    Path localPath;
    for (int l = 0; ; ++l)
    {
      if (l >= 7)
        break label214;
      localPath = this.mPaths[l];
      if (localPath != null)
        break;
      label152: arrayOfInt[l] = 2147483647;
    }
    int i1 = 0;
    if ((i1 >= 7) || (arrayOfPath[i1] == localPath))
      label161: if (i1 >= 7)
        break label206;
    for (int i2 = i1 - 3; ; i2 = 2147483647)
    {
      arrayOfInt[l] = i2;
      break label152:
      ++i1;
      label206: break label161:
    }
    label214: this.mPhotoView.notifyDataChange(arrayOfInt, -this.mCurrentIndex, -1 + this.mSize - this.mCurrentIndex);
  }

  private MediaItem getItem(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mSize) || (!this.mIsActive))
      return null;
    if ((paramInt >= this.mActiveStart) && (paramInt < this.mActiveEnd));
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      if ((paramInt < this.mContentStart) || (paramInt >= this.mContentEnd))
        break;
      return this.mData[(paramInt % 256)];
    }
    return null;
  }

  private MediaItem getItemInternal(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mSize));
    do
      return null;
    while ((paramInt < this.mContentStart) || (paramInt >= this.mContentEnd));
    return this.mData[(paramInt % 256)];
  }

  private Path getPath(int paramInt)
  {
    MediaItem localMediaItem = getItemInternal(paramInt);
    if (localMediaItem == null)
      return null;
    return localMediaItem.getPath();
  }

  private long getVersion(int paramInt)
  {
    MediaItem localMediaItem = getItemInternal(paramInt);
    if (localMediaItem == null)
      return -1L;
    return localMediaItem.getDataVersion();
  }

  private boolean isTemporaryItem(MediaItem paramMediaItem)
  {
    if (this.mCameraIndex < 0);
    LocalMediaItem localLocalMediaItem;
    do
    {
      do
        return false;
      while (!paramMediaItem instanceof LocalMediaItem);
      localLocalMediaItem = (LocalMediaItem)paramMediaItem;
    }
    while ((localLocalMediaItem.getBucketId() != MediaSetUtils.CAMERA_BUCKET_ID) || (localLocalMediaItem.getSize() != 0L) || (localLocalMediaItem.getWidth() == 0) || (localLocalMediaItem.getHeight() == 0) || (localLocalMediaItem.getDateInMs() - System.currentTimeMillis() > 10000L));
    return true;
  }

  private ScreenNail newPlaceholderScreenNail(MediaItem paramMediaItem)
  {
    return new TiledScreenNail(paramMediaItem.getWidth(), paramMediaItem.getHeight());
  }

  private Future<?> startTaskIfNeeded(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < this.mActiveStart) || (paramInt1 >= this.mActiveEnd))
      return null;
    ImageEntry localImageEntry = (ImageEntry)this.mImageCache.get(getPath(paramInt1));
    if (localImageEntry == null)
      return null;
    MediaItem localMediaItem = this.mData[(paramInt1 % 256)];
    if (localMediaItem != null);
    long l;
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      l = localMediaItem.getDataVersion();
      if ((paramInt2 != 1) || (localImageEntry.screenNailTask == null) || (localImageEntry.requestedScreenNail != l))
        break;
      return localImageEntry.screenNailTask;
    }
    if ((paramInt2 == 2) && (localImageEntry.fullImageTask != null) && (localImageEntry.requestedFullImage == l))
      return localImageEntry.fullImageTask;
    if ((paramInt2 == 1) && (localImageEntry.requestedScreenNail != l))
    {
      localImageEntry.requestedScreenNail = l;
      localImageEntry.screenNailTask = this.mThreadPool.submit(new ScreenNailJob(localMediaItem), new ScreenNailListener(localMediaItem));
      return localImageEntry.screenNailTask;
    }
    if ((paramInt2 == 2) && (localImageEntry.requestedFullImage != l) && ((0x40 & localMediaItem.getSupportedOperations()) != 0))
    {
      localImageEntry.requestedFullImage = l;
      localImageEntry.fullImageTask = this.mThreadPool.submit(new FullImageJob(localMediaItem), new FullImageListener(localMediaItem));
      return localImageEntry.fullImageTask;
    }
    return null;
  }

  private void updateCurrentIndex(int paramInt)
  {
    if (this.mCurrentIndex == paramInt)
      return;
    this.mCurrentIndex = paramInt;
    updateSlidingWindow();
    MediaItem localMediaItem = this.mData[(paramInt % 256)];
    if (localMediaItem == null);
    for (Path localPath = null; ; localPath = localMediaItem.getPath())
    {
      this.mItemPath = localPath;
      updateImageCache();
      updateImageRequests();
      updateTileProvider();
      if (this.mDataListener != null)
        this.mDataListener.onPhotoChanged(paramInt, this.mItemPath);
      fireDataChange();
      return;
    }
  }

  private void updateFullImage(Path paramPath, Future<BitmapRegionDecoder> paramFuture)
  {
    ImageEntry localImageEntry = (ImageEntry)this.mImageCache.get(paramPath);
    if ((localImageEntry == null) || (localImageEntry.fullImageTask != paramFuture))
    {
      BitmapRegionDecoder localBitmapRegionDecoder = (BitmapRegionDecoder)paramFuture.get();
      if (localBitmapRegionDecoder != null)
        localBitmapRegionDecoder.recycle();
      return;
    }
    localImageEntry.fullImageTask = null;
    localImageEntry.fullImage = ((BitmapRegionDecoder)paramFuture.get());
    if ((localImageEntry.fullImage != null) && (paramPath == getPath(this.mCurrentIndex)))
    {
      updateTileProvider(localImageEntry);
      this.mPhotoView.notifyImageChange(0);
    }
    updateImageRequests();
  }

  private void updateImageCache()
  {
    HashSet localHashSet = new HashSet(this.mImageCache.keySet());
    int i = this.mActiveStart;
    if (i < this.mActiveEnd)
    {
      label20: MediaItem localMediaItem = this.mData[(i % 256)];
      if (localMediaItem == null);
      while (true)
      {
        ++i;
        break label20:
        Path localPath2 = localMediaItem.getPath();
        ImageEntry localImageEntry2 = (ImageEntry)this.mImageCache.get(localPath2);
        localHashSet.remove(localPath2);
        if (localImageEntry2 != null)
        {
          if (Math.abs(i - this.mCurrentIndex) > 1)
          {
            if (localImageEntry2.fullImageTask != null)
            {
              localImageEntry2.fullImageTask.cancel();
              localImageEntry2.fullImageTask = null;
            }
            localImageEntry2.fullImage = null;
            localImageEntry2.requestedFullImage = -1L;
          }
          if ((localImageEntry2.requestedScreenNail == localMediaItem.getDataVersion()) || (!localImageEntry2.screenNail instanceof TiledScreenNail))
            continue;
          ((TiledScreenNail)localImageEntry2.screenNail).updatePlaceholderSize(localMediaItem.getWidth(), localMediaItem.getHeight());
        }
        ImageEntry localImageEntry3 = new ImageEntry(null);
        this.mImageCache.put(localPath2, localImageEntry3);
      }
    }
    Iterator localIterator = localHashSet.iterator();
    while (localIterator.hasNext())
    {
      Path localPath1 = (Path)localIterator.next();
      ImageEntry localImageEntry1 = (ImageEntry)this.mImageCache.remove(localPath1);
      if (localImageEntry1.fullImageTask != null)
        localImageEntry1.fullImageTask.cancel();
      if (localImageEntry1.screenNailTask != null)
        localImageEntry1.screenNailTask.cancel();
      if (localImageEntry1.screenNail == null)
        continue;
      localImageEntry1.screenNail.recycle();
    }
    updateScreenNailUploadQueue();
  }

  private void updateImageRequests()
  {
    if (!this.mIsActive);
    int i;
    MediaItem localMediaItem;
    do
    {
      return;
      i = this.mCurrentIndex;
      localMediaItem = this.mData[(i % 256)];
    }
    while ((localMediaItem == null) || (localMediaItem.getPath() != this.mItemPath));
    Future localFuture = null;
    int j = 0;
    if (j < sImageFetchSeq.length)
    {
      label44: int k = sImageFetchSeq[j].indexOffset;
      int l = sImageFetchSeq[j].imageBit;
      if ((l == 2) && (!this.mNeedFullImage));
      do
      {
        ++j;
        break label44:
        localFuture = startTaskIfNeeded(i + k, l);
      }
      while (localFuture == null);
    }
    Iterator localIterator = this.mImageCache.values().iterator();
    while (true)
    {
      if (localIterator.hasNext());
      ImageEntry localImageEntry = (ImageEntry)localIterator.next();
      if ((localImageEntry.screenNailTask != null) && (localImageEntry.screenNailTask != localFuture))
      {
        localImageEntry.screenNailTask.cancel();
        localImageEntry.screenNailTask = null;
        localImageEntry.requestedScreenNail = -1L;
      }
      if ((localImageEntry.fullImageTask == null) || (localImageEntry.fullImageTask == localFuture))
        continue;
      localImageEntry.fullImageTask.cancel();
      localImageEntry.fullImageTask = null;
      localImageEntry.requestedFullImage = -1L;
    }
  }

  private void updateScreenNail(Path paramPath, Future<ScreenNail> paramFuture)
  {
    ImageEntry localImageEntry = (ImageEntry)this.mImageCache.get(paramPath);
    ScreenNail localScreenNail = (ScreenNail)paramFuture.get();
    if ((localImageEntry == null) || (localImageEntry.screenNailTask != paramFuture))
    {
      if (localScreenNail != null)
        localScreenNail.recycle();
      return;
    }
    localImageEntry.screenNailTask = null;
    if (localImageEntry.screenNail instanceof TiledScreenNail)
      localScreenNail = ((TiledScreenNail)localImageEntry.screenNail).combine(localScreenNail);
    if (localScreenNail == null)
      localImageEntry.failToLoad = true;
    for (int i = -3; ; ++i)
    {
      if (i <= 3)
      {
        label87: if (paramPath != getPath(i + this.mCurrentIndex))
          continue;
        if (i == 0)
          updateTileProvider(localImageEntry);
        this.mPhotoView.notifyImageChange(i);
      }
      updateImageRequests();
      updateScreenNailUploadQueue();
      return;
      localImageEntry.failToLoad = false;
      localImageEntry.screenNail = localScreenNail;
      break label87:
    }
  }

  private void updateScreenNailUploadQueue()
  {
    this.mUploader.clear();
    uploadScreenNail(0);
    for (int i = 1; i < 7; ++i)
    {
      uploadScreenNail(i);
      uploadScreenNail(-i);
    }
  }

  private void updateSlidingWindow()
  {
    int i = Utils.clamp(-3 + this.mCurrentIndex, 0, Math.max(0, -7 + this.mSize));
    int j = Math.min(this.mSize, i + 7);
    if ((this.mActiveStart == i) && (this.mActiveEnd == j));
    do
    {
      int k;
      int l;
      do
      {
        return;
        this.mActiveStart = i;
        this.mActiveEnd = j;
        k = Utils.clamp(-128 + this.mCurrentIndex, 0, Math.max(0, -256 + this.mSize));
        l = Math.min(this.mSize, k + 256);
      }
      while ((this.mContentStart <= this.mActiveStart) && (this.mContentEnd >= this.mActiveEnd) && (Math.abs(k - this.mContentStart) <= 16));
      for (int i1 = this.mContentStart; i1 < this.mContentEnd; ++i1)
      {
        if ((i1 >= k) && (i1 < l))
          continue;
        this.mData[(i1 % 256)] = null;
      }
      this.mContentStart = k;
      this.mContentEnd = l;
    }
    while (this.mReloadTask == null);
    this.mReloadTask.notifyDirty();
  }

  private void updateTileProvider()
  {
    ImageEntry localImageEntry = (ImageEntry)this.mImageCache.get(getPath(this.mCurrentIndex));
    if (localImageEntry == null)
    {
      this.mTileProvider.clear();
      return;
    }
    updateTileProvider(localImageEntry);
  }

  private void updateTileProvider(ImageEntry paramImageEntry)
  {
    ScreenNail localScreenNail = paramImageEntry.screenNail;
    BitmapRegionDecoder localBitmapRegionDecoder = paramImageEntry.fullImage;
    if (localScreenNail != null)
    {
      if (localBitmapRegionDecoder != null)
      {
        this.mTileProvider.setScreenNail(localScreenNail, localBitmapRegionDecoder.getWidth(), localBitmapRegionDecoder.getHeight());
        this.mTileProvider.setRegionDecoder(localBitmapRegionDecoder);
        return;
      }
      int i = localScreenNail.getWidth();
      int j = localScreenNail.getHeight();
      this.mTileProvider.setScreenNail(localScreenNail, i, j);
      return;
    }
    this.mTileProvider.clear();
  }

  private void uploadScreenNail(int paramInt)
  {
    int i = paramInt + this.mCurrentIndex;
    if ((i < this.mActiveStart) || (i >= this.mActiveEnd));
    TiledTexture localTiledTexture;
    do
    {
      ScreenNail localScreenNail;
      do
      {
        ImageEntry localImageEntry;
        do
        {
          MediaItem localMediaItem;
          do
          {
            return;
            localMediaItem = getItem(i);
          }
          while (localMediaItem == null);
          localImageEntry = (ImageEntry)this.mImageCache.get(localMediaItem.getPath());
        }
        while (localImageEntry == null);
        localScreenNail = localImageEntry.screenNail;
      }
      while (!localScreenNail instanceof TiledScreenNail);
      localTiledTexture = ((TiledScreenNail)localScreenNail).getTexture();
    }
    while ((localTiledTexture == null) || (localTiledTexture.isReady()));
    this.mUploader.addTexture(localTiledTexture);
  }

  public int getCurrentIndex()
  {
    return this.mCurrentIndex;
  }

  public int getImageHeight()
  {
    return this.mTileProvider.getImageHeight();
  }

  public int getImageRotation(int paramInt)
  {
    MediaItem localMediaItem = getItem(paramInt + this.mCurrentIndex);
    if (localMediaItem == null)
      return 0;
    return localMediaItem.getFullImageRotation();
  }

  public void getImageSize(int paramInt, PhotoView.Size paramSize)
  {
    MediaItem localMediaItem = getItem(paramInt + this.mCurrentIndex);
    if (localMediaItem == null)
    {
      paramSize.width = 0;
      paramSize.height = 0;
      return;
    }
    paramSize.width = localMediaItem.getWidth();
    paramSize.height = localMediaItem.getHeight();
  }

  public int getImageWidth()
  {
    return this.mTileProvider.getImageWidth();
  }

  public int getLevelCount()
  {
    return this.mTileProvider.getLevelCount();
  }

  public int getLoadingState(int paramInt)
  {
    ImageEntry localImageEntry = (ImageEntry)this.mImageCache.get(getPath(paramInt + this.mCurrentIndex));
    if (localImageEntry == null);
    do
    {
      return 0;
      if (localImageEntry.failToLoad)
        return 2;
    }
    while (localImageEntry.screenNail == null);
    return 1;
  }

  public MediaItem getMediaItem(int paramInt)
  {
    int i = paramInt + this.mCurrentIndex;
    if ((i >= this.mContentStart) && (i < this.mContentEnd))
      return this.mData[(i % 256)];
    return null;
  }

  public ScreenNail getScreenNail()
  {
    return getScreenNail(0);
  }

  public ScreenNail getScreenNail(int paramInt)
  {
    int i = paramInt + this.mCurrentIndex;
    if ((i < 0) || (i >= this.mSize) || (!this.mIsActive))
      return null;
    if ((i >= this.mActiveStart) && (i < this.mActiveEnd));
    MediaItem localMediaItem;
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      localMediaItem = getItem(i);
      if (localMediaItem != null)
        break;
      return null;
    }
    ImageEntry localImageEntry = (ImageEntry)this.mImageCache.get(localMediaItem.getPath());
    if (localImageEntry == null)
      return null;
    if ((localImageEntry.screenNail == null) && (!isCamera(paramInt)))
    {
      localImageEntry.screenNail = newPlaceholderScreenNail(localMediaItem);
      if (paramInt == 0)
        updateTileProvider(localImageEntry);
    }
    return localImageEntry.screenNail;
  }

  public Bitmap getTile(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, BitmapPool paramBitmapPool)
  {
    return this.mTileProvider.getTile(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramBitmapPool);
  }

  public boolean isCamera(int paramInt)
  {
    return paramInt + this.mCurrentIndex == this.mCameraIndex;
  }

  public boolean isDeletable(int paramInt)
  {
    MediaItem localMediaItem = getItem(paramInt + this.mCurrentIndex);
    if (localMediaItem == null);
    do
      return false;
    while ((0x1 & localMediaItem.getSupportedOperations()) == 0);
    return true;
  }

  public boolean isEmpty()
  {
    return this.mSize == 0;
  }

  public boolean isPanorama(int paramInt)
  {
    return (isCamera(paramInt)) && (this.mIsPanorama);
  }

  public boolean isStaticCamera(int paramInt)
  {
    return (isCamera(paramInt)) && (this.mIsStaticCamera);
  }

  public boolean isVideo(int paramInt)
  {
    MediaItem localMediaItem = getItem(paramInt + this.mCurrentIndex);
    if (localMediaItem == null);
    do
      return false;
    while (localMediaItem.getMediaType() != 4);
    return true;
  }

  public void moveTo(int paramInt)
  {
    updateCurrentIndex(paramInt);
  }

  public void pause()
  {
    this.mIsActive = false;
    this.mReloadTask.terminate();
    this.mReloadTask = null;
    this.mSource.removeContentListener(this.mSourceListener);
    Iterator localIterator = this.mImageCache.values().iterator();
    while (localIterator.hasNext())
    {
      ImageEntry localImageEntry = (ImageEntry)localIterator.next();
      if (localImageEntry.fullImageTask != null)
        localImageEntry.fullImageTask.cancel();
      if (localImageEntry.screenNailTask != null)
        localImageEntry.screenNailTask.cancel();
      if (localImageEntry.screenNail == null)
        continue;
      localImageEntry.screenNail.recycle();
    }
    this.mImageCache.clear();
    this.mTileProvider.clear();
    this.mUploader.clear();
    TiledTexture.freeResources();
  }

  public void resume()
  {
    this.mIsActive = true;
    TiledTexture.prepareResources();
    this.mSource.addContentListener(this.mSourceListener);
    updateImageCache();
    updateImageRequests();
    this.mReloadTask = new ReloadTask(null);
    this.mReloadTask.start();
    fireDataChange();
  }

  public void setCurrentPhoto(Path paramPath, int paramInt)
  {
    if (this.mItemPath == paramPath);
    MediaItem localMediaItem;
    do
    {
      return;
      this.mItemPath = paramPath;
      this.mCurrentIndex = paramInt;
      updateSlidingWindow();
      updateImageCache();
      fireDataChange();
      localMediaItem = getMediaItem(0);
    }
    while ((localMediaItem == null) || (localMediaItem.getPath() == paramPath) || (this.mReloadTask == null));
    this.mReloadTask.notifyDirty();
  }

  public void setDataListener(DataListener paramDataListener)
  {
    this.mDataListener = paramDataListener;
  }

  public void setFocusHintDirection(int paramInt)
  {
    this.mFocusHintDirection = paramInt;
  }

  public void setFocusHintPath(Path paramPath)
  {
    this.mFocusHintPath = paramPath;
  }

  public void setNeedFullImage(boolean paramBoolean)
  {
    this.mNeedFullImage = paramBoolean;
    this.mMainHandler.sendEmptyMessage(4);
  }

  public static abstract interface DataListener extends LoadingListener
  {
    public abstract void onPhotoChanged(int paramInt, Path paramPath);
  }

  private class FullImageJob
    implements ThreadPool.Job<BitmapRegionDecoder>
  {
    private MediaItem mItem;

    public FullImageJob(MediaItem arg2)
    {
      Object localObject;
      this.mItem = localObject;
    }

    public BitmapRegionDecoder run(ThreadPool.JobContext paramJobContext)
    {
      if (PhotoDataAdapter.this.isTemporaryItem(this.mItem) != 0)
        return null;
      return (BitmapRegionDecoder)this.mItem.requestLargeImage().run(paramJobContext);
    }
  }

  private class FullImageListener
    implements Runnable, FutureListener<BitmapRegionDecoder>
  {
    private Future<BitmapRegionDecoder> mFuture;
    private final Path mPath;

    public FullImageListener(MediaItem arg2)
    {
      Object localObject;
      this.mPath = localObject.getPath();
    }

    public void onFutureDone(Future<BitmapRegionDecoder> paramFuture)
    {
      this.mFuture = paramFuture;
      PhotoDataAdapter.this.mMainHandler.sendMessage(PhotoDataAdapter.this.mMainHandler.obtainMessage(3, this));
    }

    public void run()
    {
      PhotoDataAdapter.this.updateFullImage(this.mPath, this.mFuture);
    }
  }

  private class GetUpdateInfo
    implements Callable<PhotoDataAdapter.UpdateInfo>
  {
    private GetUpdateInfo()
    {
    }

    private boolean needContentReload()
    {
      int i = PhotoDataAdapter.this.mContentStart;
      int j = PhotoDataAdapter.this.mContentEnd;
      if (i < j)
        label16: if (PhotoDataAdapter.this.mData[(i % 256)] != null);
      MediaItem localMediaItem;
      do
      {
        return true;
        ++i;
        break label16:
        localMediaItem = PhotoDataAdapter.this.mData[(PhotoDataAdapter.this.mCurrentIndex % 256)];
      }
      while ((localMediaItem == null) || (localMediaItem.getPath() != PhotoDataAdapter.this.mItemPath));
      return false;
    }

    public PhotoDataAdapter.UpdateInfo call()
      throws Exception
    {
      PhotoDataAdapter.UpdateInfo localUpdateInfo = new PhotoDataAdapter.UpdateInfo(null);
      localUpdateInfo.version = PhotoDataAdapter.this.mSourceVersion;
      localUpdateInfo.reloadContent = needContentReload();
      localUpdateInfo.target = PhotoDataAdapter.this.mItemPath;
      localUpdateInfo.indexHint = PhotoDataAdapter.this.mCurrentIndex;
      localUpdateInfo.contentStart = PhotoDataAdapter.this.mContentStart;
      localUpdateInfo.contentEnd = PhotoDataAdapter.this.mContentEnd;
      localUpdateInfo.size = PhotoDataAdapter.this.mSize;
      return localUpdateInfo;
    }
  }

  private static class ImageEntry
  {
    public boolean failToLoad = false;
    public BitmapRegionDecoder fullImage;
    public Future<BitmapRegionDecoder> fullImageTask;
    public long requestedFullImage = -1L;
    public long requestedScreenNail = -1L;
    public ScreenNail screenNail;
    public Future<ScreenNail> screenNailTask;
  }

  private static class ImageFetch
  {
    int imageBit;
    int indexOffset;

    public ImageFetch(int paramInt1, int paramInt2)
    {
      this.indexOffset = paramInt1;
      this.imageBit = paramInt2;
    }
  }

  private class ReloadTask extends Thread
  {
    private volatile boolean mActive = true;
    private volatile boolean mDirty = true;
    private boolean mIsLoading = false;

    private ReloadTask()
    {
    }

    private MediaItem findCurrentMediaItem(PhotoDataAdapter.UpdateInfo paramUpdateInfo)
    {
      ArrayList localArrayList = paramUpdateInfo.items;
      int i = paramUpdateInfo.indexHint - paramUpdateInfo.contentStart;
      if ((i < 0) || (i >= localArrayList.size()))
        return null;
      return (MediaItem)localArrayList.get(i);
    }

    private int findIndexOfPathInCache(PhotoDataAdapter.UpdateInfo paramUpdateInfo, Path paramPath)
    {
      ArrayList localArrayList = paramUpdateInfo.items;
      int i = 0;
      int j = localArrayList.size();
      while (i < j)
      {
        MediaItem localMediaItem = (MediaItem)localArrayList.get(i);
        if ((localMediaItem != null) && (localMediaItem.getPath() == paramPath))
          return i + paramUpdateInfo.contentStart;
        ++i;
      }
      return -1;
    }

    private int findIndexOfTarget(PhotoDataAdapter.UpdateInfo paramUpdateInfo)
    {
      int i;
      if (paramUpdateInfo.target == null)
        i = paramUpdateInfo.indexHint;
      do
      {
        return i;
        if (paramUpdateInfo.items == null)
          break;
        i = findIndexOfPathInCache(paramUpdateInfo, paramUpdateInfo.target);
      }
      while (i != -1);
      return PhotoDataAdapter.this.mSource.getIndexOfItem(paramUpdateInfo.target, paramUpdateInfo.indexHint);
    }

    private void updateLoading(boolean paramBoolean)
    {
      if (this.mIsLoading == paramBoolean)
        return;
      this.mIsLoading = paramBoolean;
      Handler localHandler = PhotoDataAdapter.this.mMainHandler;
      if (paramBoolean);
      for (int i = 1; ; i = 2)
      {
        localHandler.sendEmptyMessage(i);
        return;
      }
    }

    public void notifyDirty()
    {
      monitorenter;
      try
      {
        this.mDirty = true;
        super.notifyAll();
        monitorexit;
        return;
      }
      finally
      {
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }

    public void run()
    {
      label0: label42: PhotoDataAdapter.UpdateInfo localUpdateInfo;
      do
      {
        while (true)
        {
          if (!this.mActive)
            return;
          monitorenter;
          try
          {
            if ((this.mDirty) || (!this.mActive))
              break label42;
            updateLoading(false);
            Utils.waitWithoutInterrupt(this);
          }
          finally
          {
            monitorexit;
          }
        }
        monitorexit;
        this.mDirty = false;
        localUpdateInfo = (PhotoDataAdapter.UpdateInfo)PhotoDataAdapter.this.executeAndWait(new PhotoDataAdapter.GetUpdateInfo(PhotoDataAdapter.this, null));
        updateLoading(true);
        long l = PhotoDataAdapter.this.mSource.reload();
        if (localUpdateInfo.version == l)
          continue;
        localUpdateInfo.reloadContent = true;
        localUpdateInfo.size = PhotoDataAdapter.this.mSource.getMediaItemCount();
      }
      while (!localUpdateInfo.reloadContent);
      localUpdateInfo.items = PhotoDataAdapter.this.mSource.getMediaItem(localUpdateInfo.contentStart, localUpdateInfo.contentEnd);
      int i = -1;
      if (PhotoDataAdapter.this.mFocusHintPath != null)
      {
        i = findIndexOfPathInCache(localUpdateInfo, PhotoDataAdapter.this.mFocusHintPath);
        PhotoDataAdapter.access$2702(PhotoDataAdapter.this, null);
      }
      if (i == -1)
      {
        MediaItem localMediaItem = findCurrentMediaItem(localUpdateInfo);
        if ((localMediaItem == null) || (localMediaItem.getPath() != localUpdateInfo.target))
          break label331;
      }
      for (i = localUpdateInfo.indexHint; ; i = findIndexOfTarget(localUpdateInfo))
      {
        if (i == -1)
        {
          i = localUpdateInfo.indexHint;
          int j = PhotoDataAdapter.this.mFocusHintDirection;
          if (i == 1 + PhotoDataAdapter.this.mCameraIndex)
            j = 0;
          if ((j == 1) && (i > 0))
            --i;
        }
        if ((PhotoDataAdapter.this.mSize > 0) && (i >= PhotoDataAdapter.this.mSize))
          i = -1 + PhotoDataAdapter.this.mSize;
        localUpdateInfo.indexHint = i;
        PhotoDataAdapter.this.executeAndWait(new PhotoDataAdapter.UpdateContent(PhotoDataAdapter.this, localUpdateInfo));
        label331: break label0:
      }
    }

    public void terminate()
    {
      monitorenter;
      try
      {
        this.mActive = false;
        super.notifyAll();
        monitorexit;
        return;
      }
      finally
      {
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }
  }

  private class ScreenNailJob
    implements ThreadPool.Job<ScreenNail>
  {
    private MediaItem mItem;

    public ScreenNailJob(MediaItem arg2)
    {
      Object localObject;
      this.mItem = localObject;
    }

    public ScreenNail run(ThreadPool.JobContext paramJobContext)
    {
      ScreenNail localScreenNail1 = this.mItem.getScreenNail();
      ScreenNail localScreenNail2;
      if (localScreenNail1 != null)
        localScreenNail2 = localScreenNail1;
      Bitmap localBitmap;
      do
      {
        boolean bool;
        do
        {
          return localScreenNail2;
          if (PhotoDataAdapter.this.isTemporaryItem(this.mItem) != 0)
            return PhotoDataAdapter.this.newPlaceholderScreenNail(this.mItem);
          localBitmap = (Bitmap)this.mItem.requestImage(1).run(paramJobContext);
          bool = paramJobContext.isCancelled();
          localScreenNail2 = null;
        }
        while (bool);
        if (localBitmap != null)
          localBitmap = BitmapUtils.rotateBitmap(localBitmap, this.mItem.getRotation() - this.mItem.getFullImageRotation(), true);
        localScreenNail2 = null;
      }
      while (localBitmap == null);
      return new TiledScreenNail(localBitmap);
    }
  }

  private class ScreenNailListener
    implements Runnable, FutureListener<ScreenNail>
  {
    private Future<ScreenNail> mFuture;
    private final Path mPath;

    public ScreenNailListener(MediaItem arg2)
    {
      Object localObject;
      this.mPath = localObject.getPath();
    }

    public void onFutureDone(Future<ScreenNail> paramFuture)
    {
      this.mFuture = paramFuture;
      PhotoDataAdapter.this.mMainHandler.sendMessage(PhotoDataAdapter.this.mMainHandler.obtainMessage(3, this));
    }

    public void run()
    {
      PhotoDataAdapter.this.updateScreenNail(this.mPath, this.mFuture);
    }
  }

  private class SourceListener
    implements ContentListener
  {
    private SourceListener()
    {
    }

    public void onContentDirty()
    {
      if (PhotoDataAdapter.this.mReloadTask == null)
        return;
      PhotoDataAdapter.this.mReloadTask.notifyDirty();
    }
  }

  private class UpdateContent
    implements Callable<Void>
  {
    PhotoDataAdapter.UpdateInfo mUpdateInfo;

    public UpdateContent(PhotoDataAdapter.UpdateInfo arg2)
    {
      Object localObject;
      this.mUpdateInfo = localObject;
    }

    public Void call()
      throws Exception
    {
      PhotoDataAdapter.UpdateInfo localUpdateInfo = this.mUpdateInfo;
      PhotoDataAdapter.access$1702(PhotoDataAdapter.this, localUpdateInfo.version);
      if (localUpdateInfo.size != PhotoDataAdapter.this.mSize)
      {
        PhotoDataAdapter.access$1802(PhotoDataAdapter.this, localUpdateInfo.size);
        if (PhotoDataAdapter.this.mContentEnd > PhotoDataAdapter.this.mSize)
          PhotoDataAdapter.access$1202(PhotoDataAdapter.this, PhotoDataAdapter.this.mSize);
        if (PhotoDataAdapter.this.mActiveEnd > PhotoDataAdapter.this.mSize)
          PhotoDataAdapter.access$1902(PhotoDataAdapter.this, PhotoDataAdapter.this.mSize);
      }
      PhotoDataAdapter.access$1402(PhotoDataAdapter.this, localUpdateInfo.indexHint);
      PhotoDataAdapter.this.updateSlidingWindow();
      if (localUpdateInfo.items != null)
      {
        int i = Math.max(localUpdateInfo.contentStart, PhotoDataAdapter.this.mContentStart);
        int j = Math.min(localUpdateInfo.contentStart + localUpdateInfo.items.size(), PhotoDataAdapter.this.mContentEnd);
        int k = i % 256;
        for (int l = i; l < j; ++l)
        {
          PhotoDataAdapter.this.mData[k] = ((MediaItem)localUpdateInfo.items.get(l - localUpdateInfo.contentStart));
          if (++k != 256)
            continue;
          k = 0;
        }
      }
      MediaItem localMediaItem = PhotoDataAdapter.this.mData[(PhotoDataAdapter.this.mCurrentIndex % 256)];
      PhotoDataAdapter localPhotoDataAdapter = PhotoDataAdapter.this;
      if (localMediaItem == null);
      for (Path localPath = null; ; localPath = localMediaItem.getPath())
      {
        PhotoDataAdapter.access$1502(localPhotoDataAdapter, localPath);
        PhotoDataAdapter.this.updateImageCache();
        PhotoDataAdapter.this.updateTileProvider();
        PhotoDataAdapter.this.updateImageRequests();
        if (PhotoDataAdapter.this.mDataListener != null)
          PhotoDataAdapter.this.mDataListener.onPhotoChanged(PhotoDataAdapter.this.mCurrentIndex, PhotoDataAdapter.this.mItemPath);
        PhotoDataAdapter.this.fireDataChange();
        return null;
      }
    }
  }

  private static class UpdateInfo
  {
    public int contentEnd;
    public int contentStart;
    public int indexHint;
    public ArrayList<MediaItem> items;
    public boolean reloadContent;
    public int size;
    public Path target;
    public long version;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.PhotoDataAdapter
 * JD-Core Version:    0.5.4
 */