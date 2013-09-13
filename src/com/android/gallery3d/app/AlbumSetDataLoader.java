package com.android.gallery3d.app;

import android.os.Handler;
import android.os.Message;
import android.os.Process;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.ContentListener;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.SynchronizedHandler;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class AlbumSetDataLoader
{
  private int mActiveEnd = 0;
  private int mActiveStart = 0;
  private int mContentEnd = 0;
  private int mContentStart = 0;
  private final MediaItem[] mCoverItem;
  private final MediaSet[] mData;
  private DataListener mDataListener;
  private final long[] mItemVersion;
  private LoadingListener mLoadingListener;
  private final Handler mMainHandler;
  private ReloadTask mReloadTask;
  private final long[] mSetVersion;
  private int mSize;
  private final MediaSet mSource;
  private final MySourceListener mSourceListener = new MySourceListener(null);
  private long mSourceVersion = -1L;
  private final int[] mTotalCount;

  public AlbumSetDataLoader(AbstractGalleryActivity paramAbstractGalleryActivity, MediaSet paramMediaSet, int paramInt)
  {
    this.mSource = ((MediaSet)Utils.checkNotNull(paramMediaSet));
    this.mCoverItem = new MediaItem[paramInt];
    this.mData = new MediaSet[paramInt];
    this.mTotalCount = new int[paramInt];
    this.mItemVersion = new long[paramInt];
    this.mSetVersion = new long[paramInt];
    Arrays.fill(this.mItemVersion, -1L);
    Arrays.fill(this.mSetVersion, -1L);
    this.mMainHandler = new SynchronizedHandler(paramAbstractGalleryActivity.getGLRoot())
    {
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default:
        case 3:
        case 1:
        case 2:
        }
        do
        {
          do
          {
            return;
            ((Runnable)paramMessage.obj).run();
            return;
          }
          while (AlbumSetDataLoader.this.mLoadingListener == null);
          AlbumSetDataLoader.this.mLoadingListener.onLoadingStarted();
          return;
        }
        while (AlbumSetDataLoader.this.mLoadingListener == null);
        AlbumSetDataLoader.this.mLoadingListener.onLoadingFinished();
      }
    };
  }

  private void assertIsActive(int paramInt)
  {
    if ((paramInt >= this.mActiveStart) || (paramInt < this.mActiveEnd))
      return;
    Object[] arrayOfObject = new Object[3];
    arrayOfObject[0] = Integer.valueOf(paramInt);
    arrayOfObject[1] = Integer.valueOf(this.mActiveStart);
    arrayOfObject[2] = Integer.valueOf(this.mActiveEnd);
    throw new IllegalArgumentException(String.format("%s not in (%s, %s)", arrayOfObject));
  }

  private void clearSlot(int paramInt)
  {
    this.mData[paramInt] = null;
    this.mCoverItem[paramInt] = null;
    this.mTotalCount[paramInt] = 0;
    this.mItemVersion[paramInt] = -1L;
    this.mSetVersion[paramInt] = -1L;
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

  private void setContentWindow(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == this.mContentStart) && (paramInt2 == this.mContentEnd))
      return;
    int i = this.mCoverItem.length;
    int j = this.mContentStart;
    int k = this.mContentEnd;
    this.mContentStart = paramInt1;
    this.mContentEnd = paramInt2;
    if ((paramInt1 >= k) || (j >= paramInt2))
      for (int l = j; ; ++l)
      {
        if (l >= k)
          break label130;
        clearSlot(l % i);
      }
    for (int i1 = j; i1 < paramInt1; ++i1)
      clearSlot(i1 % i);
    for (int i2 = paramInt2; i2 < k; ++i2)
      clearSlot(i2 % i);
    label130: this.mReloadTask.notifyDirty();
  }

  public int findSet(Path paramPath)
  {
    int i = this.mData.length;
    for (int j = this.mContentStart; j < this.mContentEnd; ++j)
    {
      MediaSet localMediaSet = this.mData[(j % i)];
      if ((localMediaSet != null) && (paramPath == localMediaSet.getPath()))
        return j;
    }
    return -1;
  }

  public MediaItem getCoverItem(int paramInt)
  {
    assertIsActive(paramInt);
    return this.mCoverItem[(paramInt % this.mCoverItem.length)];
  }

  public MediaSet getMediaSet(int paramInt)
  {
    assertIsActive(paramInt);
    return this.mData[(paramInt % this.mData.length)];
  }

  public int getTotalCount(int paramInt)
  {
    assertIsActive(paramInt);
    return this.mTotalCount[(paramInt % this.mTotalCount.length)];
  }

  public void pause()
  {
    this.mReloadTask.terminate();
    this.mReloadTask = null;
    this.mSource.removeContentListener(this.mSourceListener);
  }

  public void resume()
  {
    this.mSource.addContentListener(this.mSourceListener);
    this.mReloadTask = new ReloadTask(null);
    this.mReloadTask.start();
  }

  public void setActiveWindow(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == this.mActiveStart) && (paramInt2 == this.mActiveEnd))
      return;
    if ((paramInt1 <= paramInt2) && (paramInt2 - paramInt1 <= this.mCoverItem.length) && (paramInt2 <= this.mSize));
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      this.mActiveStart = paramInt1;
      this.mActiveEnd = paramInt2;
      int i = this.mCoverItem.length;
      if (paramInt1 != paramInt2);
      int j = Utils.clamp((paramInt1 + paramInt2) / 2 - i / 2, 0, Math.max(0, this.mSize - i));
      int k = Math.min(j + i, this.mSize);
      if ((this.mContentStart > paramInt1) || (this.mContentEnd < paramInt2) || (Math.abs(j - this.mContentStart) > 4));
      setContentWindow(j, k);
      return;
    }
  }

  public void setLoadingListener(LoadingListener paramLoadingListener)
  {
    this.mLoadingListener = paramLoadingListener;
  }

  public void setModelListener(DataListener paramDataListener)
  {
    this.mDataListener = paramDataListener;
  }

  public int size()
  {
    return this.mSize;
  }

  public static abstract interface DataListener
  {
    public abstract void onContentChanged(int paramInt);

    public abstract void onSizeChanged(int paramInt);
  }

  private class GetUpdateInfo
    implements Callable<AlbumSetDataLoader.UpdateInfo>
  {
    private final long mVersion;

    public GetUpdateInfo(long arg2)
    {
      Object localObject;
      this.mVersion = localObject;
    }

    private int getInvalidIndex(long paramLong)
    {
      long[] arrayOfLong = AlbumSetDataLoader.this.mSetVersion;
      int i = arrayOfLong.length;
      int j = AlbumSetDataLoader.this.mContentStart;
      int k = AlbumSetDataLoader.this.mContentEnd;
      while (j < k)
      {
        (j % i);
        if (arrayOfLong[(j % i)] != paramLong)
          return j;
        ++j;
      }
      return -1;
    }

    public AlbumSetDataLoader.UpdateInfo call()
      throws Exception
    {
      int i = getInvalidIndex(this.mVersion);
      if ((i == -1) && (AlbumSetDataLoader.this.mSourceVersion == this.mVersion))
        return null;
      AlbumSetDataLoader.UpdateInfo localUpdateInfo = new AlbumSetDataLoader.UpdateInfo(null);
      localUpdateInfo.version = AlbumSetDataLoader.this.mSourceVersion;
      localUpdateInfo.index = i;
      localUpdateInfo.size = AlbumSetDataLoader.this.mSize;
      return localUpdateInfo;
    }
  }

  private class MySourceListener
    implements ContentListener
  {
    private MySourceListener()
    {
    }

    public void onContentDirty()
    {
      AlbumSetDataLoader.this.mReloadTask.notifyDirty();
    }
  }

  private class ReloadTask extends Thread
  {
    private volatile boolean mActive = true;
    private volatile boolean mDirty = true;
    private volatile boolean mIsLoading = false;

    private ReloadTask()
    {
    }

    private void updateLoading(boolean paramBoolean)
    {
      if (this.mIsLoading == paramBoolean)
        return;
      this.mIsLoading = paramBoolean;
      Handler localHandler = AlbumSetDataLoader.this.mMainHandler;
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
      Process.setThreadPriority(10);
      int i = 0;
      while (true)
      {
        label7: if (!this.mActive)
          break label266;
        monitorenter;
        try
        {
          if ((!this.mActive) || (this.mDirty) || (i == 0))
            break label66;
          if (!AlbumSetDataLoader.this.mSource.isLoading())
            updateLoading(false);
          Utils.waitWithoutInterrupt(this);
        }
        finally
        {
          monitorexit;
        }
      }
      label66: monitorexit;
      this.mDirty = false;
      updateLoading(true);
      long l = AlbumSetDataLoader.this.mSource.reload();
      AlbumSetDataLoader.UpdateInfo localUpdateInfo = (AlbumSetDataLoader.UpdateInfo)AlbumSetDataLoader.this.executeAndWait(new AlbumSetDataLoader.GetUpdateInfo(AlbumSetDataLoader.this, l));
      if (localUpdateInfo == null);
      for (i = 1; ; i = 0)
      {
        if (i == 0);
        if (localUpdateInfo.version != l)
        {
          localUpdateInfo.version = l;
          localUpdateInfo.size = AlbumSetDataLoader.this.mSource.getSubMediaSetCount();
          if (localUpdateInfo.index >= localUpdateInfo.size)
            localUpdateInfo.index = -1;
        }
        if (localUpdateInfo.index != -1)
        {
          localUpdateInfo.item = AlbumSetDataLoader.this.mSource.getSubMediaSet(localUpdateInfo.index);
          if (localUpdateInfo.item != null);
          localUpdateInfo.cover = localUpdateInfo.item.getCoverMediaItem();
          localUpdateInfo.totalCount = localUpdateInfo.item.getTotalMediaItemCount();
        }
        AlbumSetDataLoader.this.executeAndWait(new AlbumSetDataLoader.UpdateContent(AlbumSetDataLoader.this, localUpdateInfo));
        break label7:
      }
      label266: updateLoading(false);
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

  private class UpdateContent
    implements Callable<Void>
  {
    private final AlbumSetDataLoader.UpdateInfo mUpdateInfo;

    public UpdateContent(AlbumSetDataLoader.UpdateInfo arg2)
    {
      Object localObject;
      this.mUpdateInfo = localObject;
    }

    public Void call()
    {
      if (AlbumSetDataLoader.this.mReloadTask == null);
      AlbumSetDataLoader.UpdateInfo localUpdateInfo;
      do
      {
        int i;
        long l;
        do
        {
          do
          {
            return null;
            localUpdateInfo = this.mUpdateInfo;
            AlbumSetDataLoader.access$702(AlbumSetDataLoader.this, localUpdateInfo.version);
            if (AlbumSetDataLoader.this.mSize == localUpdateInfo.size)
              continue;
            AlbumSetDataLoader.access$902(AlbumSetDataLoader.this, localUpdateInfo.size);
            if (AlbumSetDataLoader.this.mDataListener != null)
              AlbumSetDataLoader.this.mDataListener.onSizeChanged(AlbumSetDataLoader.this.mSize);
            if (AlbumSetDataLoader.this.mContentEnd > AlbumSetDataLoader.this.mSize)
              AlbumSetDataLoader.access$602(AlbumSetDataLoader.this, AlbumSetDataLoader.this.mSize);
            if (AlbumSetDataLoader.this.mActiveEnd <= AlbumSetDataLoader.this.mSize)
              continue;
            AlbumSetDataLoader.access$1102(AlbumSetDataLoader.this, AlbumSetDataLoader.this.mSize);
          }
          while ((localUpdateInfo.index < AlbumSetDataLoader.this.mContentStart) || (localUpdateInfo.index >= AlbumSetDataLoader.this.mContentEnd));
          i = localUpdateInfo.index % AlbumSetDataLoader.this.mCoverItem.length;
          AlbumSetDataLoader.this.mSetVersion[i] = localUpdateInfo.version;
          l = localUpdateInfo.item.getDataVersion();
        }
        while (AlbumSetDataLoader.this.mItemVersion[i] == l);
        AlbumSetDataLoader.this.mItemVersion[i] = l;
        AlbumSetDataLoader.this.mData[i] = localUpdateInfo.item;
        AlbumSetDataLoader.this.mCoverItem[i] = localUpdateInfo.cover;
        AlbumSetDataLoader.this.mTotalCount[i] = localUpdateInfo.totalCount;
      }
      while ((AlbumSetDataLoader.this.mDataListener == null) || (localUpdateInfo.index < AlbumSetDataLoader.this.mActiveStart) || (localUpdateInfo.index >= AlbumSetDataLoader.this.mActiveEnd));
      AlbumSetDataLoader.this.mDataListener.onContentChanged(localUpdateInfo.index);
      return null;
    }
  }

  private static class UpdateInfo
  {
    public MediaItem cover;
    public int index;
    public MediaSet item;
    public int size;
    public int totalCount;
    public long version;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.AlbumSetDataLoader
 * JD-Core Version:    0.5.4
 */