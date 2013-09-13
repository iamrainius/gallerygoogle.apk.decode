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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class AlbumDataLoader
{
  private int mActiveEnd = 0;
  private int mActiveStart = 0;
  private int mContentEnd = 0;
  private int mContentStart = 0;
  private final MediaItem[] mData;
  private DataListener mDataListener;
  private final long[] mItemVersion;
  private LoadingListener mLoadingListener;
  private final Handler mMainHandler;
  private ReloadTask mReloadTask;
  private final long[] mSetVersion;
  private int mSize = 0;
  private final MediaSet mSource;
  private MySourceListener mSourceListener = new MySourceListener(null);
  private long mSourceVersion = -1L;

  public AlbumDataLoader(AbstractGalleryActivity paramAbstractGalleryActivity, MediaSet paramMediaSet)
  {
    this.mSource = paramMediaSet;
    this.mData = new MediaItem[1000];
    this.mItemVersion = new long[1000];
    this.mSetVersion = new long[1000];
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
          while (AlbumDataLoader.this.mLoadingListener == null);
          AlbumDataLoader.this.mLoadingListener.onLoadingStarted();
          return;
        }
        while (AlbumDataLoader.this.mLoadingListener == null);
        AlbumDataLoader.this.mLoadingListener.onLoadingFinished();
      }
    };
  }

  private void clearSlot(int paramInt)
  {
    this.mData[paramInt] = null;
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
    if ((paramInt1 == this.mContentStart) && (paramInt2 == this.mContentEnd));
    do
    {
      return;
      int i = this.mContentEnd;
      int j = this.mContentStart;
      monitorenter;
      int k;
      try
      {
        this.mContentStart = paramInt1;
        this.mContentEnd = paramInt2;
        monitorexit;
        if ((paramInt1 < i) && (j < paramInt2))
          break label96;
        k = j;
        if (k >= i)
          break label147;
        clearSlot(k % 1000);
      }
      finally
      {
        monitorexit;
      }
      for (int l = j; l < paramInt1; ++l)
        label96: clearSlot(l % 1000);
      label147: for (int i1 = paramInt2; i1 < i; ++i1)
        clearSlot(i1 % 1000);
    }
    while (this.mReloadTask == null);
    this.mReloadTask.notifyDirty();
  }

  public int findItem(Path paramPath)
  {
    for (int i = this.mContentStart; i < this.mContentEnd; ++i)
    {
      MediaItem localMediaItem = this.mData[(i % 1000)];
      if ((localMediaItem != null) && (paramPath == localMediaItem.getPath()))
        return i;
    }
    return -1;
  }

  public MediaItem get(int paramInt)
  {
    if (!isActive(paramInt))
    {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = Integer.valueOf(paramInt);
      arrayOfObject[1] = Integer.valueOf(this.mActiveStart);
      arrayOfObject[2] = Integer.valueOf(this.mActiveEnd);
      throw new IllegalArgumentException(String.format("%s not in (%s, %s)", arrayOfObject));
    }
    return this.mData[(paramInt % this.mData.length)];
  }

  public boolean isActive(int paramInt)
  {
    return (paramInt >= this.mActiveStart) && (paramInt < this.mActiveEnd);
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
    if ((paramInt1 <= paramInt2) && (paramInt2 - paramInt1 <= this.mData.length) && (paramInt2 <= this.mSize));
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      int i = this.mData.length;
      this.mActiveStart = paramInt1;
      this.mActiveEnd = paramInt2;
      if (paramInt1 != paramInt2);
      int j = Utils.clamp((paramInt1 + paramInt2) / 2 - i / 2, 0, Math.max(0, this.mSize - i));
      int k = Math.min(j + i, this.mSize);
      if ((this.mContentStart > paramInt1) || (this.mContentEnd < paramInt2) || (Math.abs(j - this.mContentStart) > 32));
      setContentWindow(j, k);
      return;
    }
  }

  public void setDataListener(DataListener paramDataListener)
  {
    this.mDataListener = paramDataListener;
  }

  public void setLoadingListener(LoadingListener paramLoadingListener)
  {
    this.mLoadingListener = paramLoadingListener;
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
    implements Callable<AlbumDataLoader.UpdateInfo>
  {
    private final long mVersion;

    public GetUpdateInfo(long arg2)
    {
      Object localObject;
      this.mVersion = localObject;
    }

    public AlbumDataLoader.UpdateInfo call()
      throws Exception
    {
      AlbumDataLoader.UpdateInfo localUpdateInfo = new AlbumDataLoader.UpdateInfo(null);
      long l = this.mVersion;
      localUpdateInfo.version = AlbumDataLoader.this.mSourceVersion;
      localUpdateInfo.size = AlbumDataLoader.this.mSize;
      long[] arrayOfLong = AlbumDataLoader.this.mSetVersion;
      int i = AlbumDataLoader.this.mContentStart;
      int j = AlbumDataLoader.this.mContentEnd;
      if (i < j)
        if (arrayOfLong[(i % 1000)] != l)
        {
          label63: localUpdateInfo.reloadStart = i;
          localUpdateInfo.reloadCount = Math.min(64, j - i);
        }
      do
      {
        return localUpdateInfo;
        ++i;
        break label63:
      }
      while (AlbumDataLoader.this.mSourceVersion != this.mVersion);
      return null;
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
      if (AlbumDataLoader.this.mReloadTask == null)
        return;
      AlbumDataLoader.this.mReloadTask.notifyDirty();
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

    private void updateLoading(boolean paramBoolean)
    {
      if (this.mIsLoading == paramBoolean)
        return;
      this.mIsLoading = paramBoolean;
      Handler localHandler = AlbumDataLoader.this.mMainHandler;
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
          break label204;
        monitorenter;
        try
        {
          if ((!this.mActive) || (this.mDirty) || (i == 0))
            break label53;
          updateLoading(false);
          Utils.waitWithoutInterrupt(this);
        }
        finally
        {
          monitorexit;
        }
      }
      label53: monitorexit;
      this.mDirty = false;
      updateLoading(true);
      long l = AlbumDataLoader.this.mSource.reload();
      AlbumDataLoader.UpdateInfo localUpdateInfo = (AlbumDataLoader.UpdateInfo)AlbumDataLoader.this.executeAndWait(new AlbumDataLoader.GetUpdateInfo(AlbumDataLoader.this, l));
      if (localUpdateInfo == null);
      for (i = 1; ; i = 0)
      {
        if (i == 0);
        if (localUpdateInfo.version != l)
        {
          localUpdateInfo.size = AlbumDataLoader.this.mSource.getMediaItemCount();
          localUpdateInfo.version = l;
        }
        if (localUpdateInfo.reloadCount > 0)
          localUpdateInfo.items = AlbumDataLoader.this.mSource.getMediaItem(localUpdateInfo.reloadStart, localUpdateInfo.reloadCount);
        AlbumDataLoader.this.executeAndWait(new AlbumDataLoader.UpdateContent(AlbumDataLoader.this, localUpdateInfo));
        break label7:
      }
      label204: updateLoading(false);
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
    private AlbumDataLoader.UpdateInfo mUpdateInfo;

    public UpdateContent(AlbumDataLoader.UpdateInfo arg2)
    {
      Object localObject;
      this.mUpdateInfo = localObject;
    }

    public Void call()
      throws Exception
    {
      AlbumDataLoader.UpdateInfo localUpdateInfo = this.mUpdateInfo;
      AlbumDataLoader.access$502(AlbumDataLoader.this, localUpdateInfo.version);
      if (AlbumDataLoader.this.mSize != localUpdateInfo.size)
      {
        AlbumDataLoader.access$602(AlbumDataLoader.this, localUpdateInfo.size);
        if (AlbumDataLoader.this.mDataListener != null)
          AlbumDataLoader.this.mDataListener.onSizeChanged(AlbumDataLoader.this.mSize);
        if (AlbumDataLoader.this.mContentEnd > AlbumDataLoader.this.mSize)
          AlbumDataLoader.access$902(AlbumDataLoader.this, AlbumDataLoader.this.mSize);
        if (AlbumDataLoader.this.mActiveEnd > AlbumDataLoader.this.mSize)
          AlbumDataLoader.access$1102(AlbumDataLoader.this, AlbumDataLoader.this.mSize);
      }
      ArrayList localArrayList = localUpdateInfo.items;
      if (localArrayList == null)
        return null;
      int i = Math.max(localUpdateInfo.reloadStart, AlbumDataLoader.this.mContentStart);
      int j = Math.min(localUpdateInfo.reloadStart + localArrayList.size(), AlbumDataLoader.this.mContentEnd);
      for (int k = i; ; ++k)
      {
        if (k < j);
        int l = k % 1000;
        AlbumDataLoader.this.mSetVersion[l] = localUpdateInfo.version;
        MediaItem localMediaItem = (MediaItem)localArrayList.get(k - localUpdateInfo.reloadStart);
        long l1 = localMediaItem.getDataVersion();
        if (AlbumDataLoader.this.mItemVersion[l] == l1)
          continue;
        AlbumDataLoader.this.mItemVersion[l] = l1;
        AlbumDataLoader.this.mData[l] = localMediaItem;
        if ((AlbumDataLoader.this.mDataListener == null) || (k < AlbumDataLoader.this.mActiveStart) || (k >= AlbumDataLoader.this.mActiveEnd))
          continue;
        AlbumDataLoader.this.mDataListener.onContentChanged(k);
      }
    }
  }

  private static class UpdateInfo
  {
    public ArrayList<MediaItem> items;
    public int reloadCount;
    public int reloadStart;
    public int size;
    public long version;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.AlbumDataLoader
 * JD-Core Version:    0.5.4
 */