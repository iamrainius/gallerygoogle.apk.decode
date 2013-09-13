package com.android.gallery3d.app;

import android.graphics.Bitmap;
import com.android.gallery3d.data.ContentListener;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class SlideshowDataAdapter
  implements SlideshowPage.Model
{
  private boolean mDataReady;
  private long mDataVersion = -1L;
  private final LinkedList<SlideshowPage.Slide> mImageQueue = new LinkedList();
  private Path mInitialPath;
  private boolean mIsActive = false;
  private int mLoadIndex = 0;
  private final AtomicBoolean mNeedReload = new AtomicBoolean(false);
  private boolean mNeedReset;
  private int mNextOutput = 0;
  private Future<Void> mReloadTask;
  private final SlideshowSource mSource;
  private final SourceListener mSourceListener = new SourceListener(null);
  private final ThreadPool mThreadPool;

  public SlideshowDataAdapter(GalleryContext paramGalleryContext, SlideshowSource paramSlideshowSource, int paramInt, Path paramPath)
  {
    this.mSource = paramSlideshowSource;
    this.mInitialPath = paramPath;
    this.mLoadIndex = paramInt;
    this.mNextOutput = paramInt;
    this.mThreadPool = paramGalleryContext.getThreadPool();
  }

  private SlideshowPage.Slide innerNextBitmap()
  {
    monitorenter;
    while (true)
      try
      {
        while (true)
        {
          if ((!this.mIsActive) || (!this.mDataReady))
            break label52;
          boolean bool2 = this.mImageQueue.isEmpty();
          try
          {
            super.wait();
          }
          catch (InterruptedException localInterruptedException)
          {
            throw new AssertionError();
          }
        }
      }
      finally
      {
        monitorexit;
      }
    label52: boolean bool1 = this.mImageQueue.isEmpty();
    if (bool1);
    for (SlideshowPage.Slide localSlide = null; ; localSlide = (SlideshowPage.Slide)this.mImageQueue.removeFirst())
    {
      monitorexit;
      return localSlide;
      this.mNextOutput = (1 + this.mNextOutput);
      super.notifyAll();
    }
  }

  private MediaItem loadItem()
  {
    if (this.mNeedReload.compareAndSet(true, false))
    {
      long l = this.mSource.reload();
      if (l != this.mDataVersion)
      {
        this.mDataVersion = l;
        this.mNeedReset = true;
        return null;
      }
    }
    int i = this.mLoadIndex;
    if (this.mInitialPath != null)
    {
      i = this.mSource.findItemIndex(this.mInitialPath, i);
      this.mInitialPath = null;
    }
    return this.mSource.getMediaItem(i);
  }

  public Future<SlideshowPage.Slide> nextSlide(FutureListener<SlideshowPage.Slide> paramFutureListener)
  {
    return this.mThreadPool.submit(new ThreadPool.Job()
    {
      public SlideshowPage.Slide run(ThreadPool.JobContext paramJobContext)
      {
        paramJobContext.setMode(0);
        return SlideshowDataAdapter.this.innerNextBitmap();
      }
    }
    , paramFutureListener);
  }

  public void pause()
  {
    monitorenter;
    try
    {
      this.mIsActive = false;
      super.notifyAll();
      monitorexit;
      this.mSource.removeContentListener(this.mSourceListener);
      this.mReloadTask.cancel();
      this.mReloadTask.waitDone();
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  public void resume()
  {
    monitorenter;
    try
    {
      this.mIsActive = true;
      this.mSource.addContentListener(this.mSourceListener);
      this.mNeedReload.set(true);
      this.mDataReady = true;
      this.mReloadTask = this.mThreadPool.submit(new ReloadTask(null));
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

  private class ReloadTask
    implements ThreadPool.Job<Void>
  {
    private ReloadTask()
    {
    }

    public Void run(ThreadPool.JobContext paramJobContext)
    {
      synchronized (SlideshowDataAdapter.this)
      {
        while (SlideshowDataAdapter.this.mIsActive)
        {
          if (SlideshowDataAdapter.this.mDataReady)
          {
            int i = SlideshowDataAdapter.this.mImageQueue.size();
            if (i < 3)
              break;
          }
          try
          {
            SlideshowDataAdapter.this.wait();
          }
          catch (InterruptedException localInterruptedException)
          {
          }
        }
        if (!SlideshowDataAdapter.this.mIsActive)
          return null;
      }
      SlideshowDataAdapter.access$402(SlideshowDataAdapter.this, false);
      MediaItem localMediaItem = SlideshowDataAdapter.this.loadItem();
      if (SlideshowDataAdapter.this.mNeedReset)
        synchronized (SlideshowDataAdapter.this)
        {
          SlideshowDataAdapter.this.mImageQueue.clear();
          SlideshowDataAdapter.access$602(SlideshowDataAdapter.this, SlideshowDataAdapter.this.mNextOutput);
        }
      if (localMediaItem == null)
        synchronized (SlideshowDataAdapter.this)
        {
          if (!SlideshowDataAdapter.this.mNeedReload.get())
            SlideshowDataAdapter.access$202(SlideshowDataAdapter.this, false);
          SlideshowDataAdapter.this.notifyAll();
        }
      Bitmap localBitmap = (Bitmap)localMediaItem.requestImage(1).run(paramJobContext);
      if (localBitmap != null);
      synchronized (SlideshowDataAdapter.this)
      {
        SlideshowDataAdapter.this.mImageQueue.addLast(new SlideshowPage.Slide(localMediaItem, SlideshowDataAdapter.this.mLoadIndex, localBitmap));
        if (SlideshowDataAdapter.this.mImageQueue.size() == 1)
          SlideshowDataAdapter.this.notifyAll();
        SlideshowDataAdapter.access$604(SlideshowDataAdapter.this);
      }
    }
  }

  public static abstract interface SlideshowSource
  {
    public abstract void addContentListener(ContentListener paramContentListener);

    public abstract int findItemIndex(Path paramPath, int paramInt);

    public abstract MediaItem getMediaItem(int paramInt);

    public abstract long reload();

    public abstract void removeContentListener(ContentListener paramContentListener);
  }

  private class SourceListener
    implements ContentListener
  {
    private SourceListener()
    {
    }

    public void onContentDirty()
    {
      synchronized (SlideshowDataAdapter.this)
      {
        SlideshowDataAdapter.this.mNeedReload.set(true);
        SlideshowDataAdapter.access$202(SlideshowDataAdapter.this, true);
        SlideshowDataAdapter.this.notifyAll();
        return;
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.SlideshowDataAdapter
 * JD-Core Version:    0.5.4
 */