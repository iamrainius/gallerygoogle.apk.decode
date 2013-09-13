package com.android.gallery3d.data;

import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.Future;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class MediaSet extends MediaObject
{
  private static final Future<Integer> FUTURE_STUB = new Future()
  {
    public void cancel()
    {
    }

    public Integer get()
    {
      return Integer.valueOf(0);
    }

    public boolean isCancelled()
    {
      return false;
    }

    public boolean isDone()
    {
      return true;
    }

    public void waitDone()
    {
    }
  };
  private WeakHashMap<ContentListener, Object> mListeners = new WeakHashMap();

  public MediaSet(Path paramPath, long paramLong)
  {
    super(paramPath, paramLong);
  }

  public void addContentListener(ContentListener paramContentListener)
  {
    if (this.mListeners.containsKey(paramContentListener))
      throw new IllegalArgumentException();
    this.mListeners.put(paramContentListener, null);
  }

  protected int enumerateMediaItems(ItemConsumer paramItemConsumer, int paramInt)
  {
    int i = getMediaItemCount();
    int j = 0;
    while (j < i)
    {
      int k = Math.min(500, i - j);
      ArrayList localArrayList = getMediaItem(j, k);
      int l = 0;
      int i1 = localArrayList.size();
      while (l < i1)
      {
        MediaItem localMediaItem = (MediaItem)localArrayList.get(l);
        paramItemConsumer.consume(l + (paramInt + j), localMediaItem);
        ++l;
      }
      j += k;
    }
    return i;
  }

  public void enumerateMediaItems(ItemConsumer paramItemConsumer)
  {
    enumerateMediaItems(paramItemConsumer, 0);
  }

  protected int enumerateTotalMediaItems(ItemConsumer paramItemConsumer, int paramInt)
  {
    int i = 0 + enumerateMediaItems(paramItemConsumer, paramInt);
    int j = getSubMediaSetCount();
    for (int k = 0; k < j; ++k)
      i += getSubMediaSet(k).enumerateTotalMediaItems(paramItemConsumer, paramInt + i);
    return i;
  }

  public void enumerateTotalMediaItems(ItemConsumer paramItemConsumer)
  {
    enumerateTotalMediaItems(paramItemConsumer, 0);
  }

  public MediaItem getCoverMediaItem()
  {
    ArrayList localArrayList = getMediaItem(0, 1);
    if (localArrayList.size() > 0)
      return (MediaItem)localArrayList.get(0);
    int i = 0;
    int j = getSubMediaSetCount();
    while (i < j)
    {
      MediaItem localMediaItem = getSubMediaSet(i).getCoverMediaItem();
      if (localMediaItem != null)
        return localMediaItem;
      ++i;
    }
    return null;
  }

  public MediaDetails getDetails()
  {
    MediaDetails localMediaDetails = super.getDetails();
    localMediaDetails.addDetail(1, getName());
    return localMediaDetails;
  }

  protected int getIndexOf(Path paramPath, ArrayList<MediaItem> paramArrayList)
  {
    int i = 0;
    int j = paramArrayList.size();
    while (i < j)
    {
      MediaObject localMediaObject = (MediaObject)paramArrayList.get(i);
      if ((localMediaObject != null) && (localMediaObject.mPath == paramPath))
        return i;
      ++i;
    }
    return -1;
  }

  public int getIndexOfItem(Path paramPath, int paramInt)
  {
    int i = Math.max(0, paramInt - 250);
    int j = getIndexOf(paramPath, getMediaItem(i, 500));
    if (j != -1)
      return i + j;
    int k;
    if (i == 0)
      k = 500;
    for (ArrayList localArrayList = getMediaItem(k, 500); ; localArrayList = getMediaItem(k += 500, 500))
    {
      int l = getIndexOf(paramPath, localArrayList);
      if (l != -1)
      {
        return k + l;
        k = 0;
      }
      if (localArrayList.size() < 500)
        return -1;
    }
  }

  public ArrayList<MediaItem> getMediaItem(int paramInt1, int paramInt2)
  {
    return new ArrayList();
  }

  public int getMediaItemCount()
  {
    return 0;
  }

  public abstract String getName();

  public MediaSet getSubMediaSet(int paramInt)
  {
    throw new IndexOutOfBoundsException();
  }

  public int getSubMediaSetCount()
  {
    return 0;
  }

  public int getTotalMediaItemCount()
  {
    int i = getMediaItemCount();
    int j = 0;
    int k = getSubMediaSetCount();
    while (j < k)
    {
      i += getSubMediaSet(j).getTotalMediaItemCount();
      ++j;
    }
    return i;
  }

  public boolean isCameraRoll()
  {
    return false;
  }

  public boolean isLeafAlbum()
  {
    return false;
  }

  public boolean isLoading()
  {
    return false;
  }

  public void notifyContentChanged()
  {
    Iterator localIterator = this.mListeners.keySet().iterator();
    while (localIterator.hasNext())
      ((ContentListener)localIterator.next()).onContentDirty();
  }

  public abstract long reload();

  public void removeContentListener(ContentListener paramContentListener)
  {
    if (!this.mListeners.containsKey(paramContentListener))
      throw new IllegalArgumentException();
    this.mListeners.remove(paramContentListener);
  }

  public Future<Integer> requestSync(SyncListener paramSyncListener)
  {
    paramSyncListener.onSyncDone(this, 0);
    return FUTURE_STUB;
  }

  protected Future<Integer> requestSyncOnMultipleSets(MediaSet[] paramArrayOfMediaSet, SyncListener paramSyncListener)
  {
    return new MultiSetSyncFuture(paramArrayOfMediaSet, paramSyncListener);
  }

  public static abstract interface ItemConsumer
  {
    public abstract void consume(int paramInt, MediaItem paramMediaItem);
  }

  private class MultiSetSyncFuture
    implements Future<Integer>, MediaSet.SyncListener
  {
    private final Future<Integer>[] mFutures;
    private boolean mIsCancelled = false;
    private final MediaSet.SyncListener mListener;
    private int mPendingCount;
    private int mResult = -1;

    MultiSetSyncFuture(MediaSet[] paramSyncListener, MediaSet.SyncListener arg3)
    {
      Object localObject1;
      this.mListener = localObject1;
      this.mPendingCount = paramSyncListener.length;
      this.mFutures = new Future[paramSyncListener.length];
      monitorenter;
      int i = 0;
      try
      {
        int j = paramSyncListener.length;
        while (i < j)
        {
          this.mFutures[i] = paramSyncListener[i].requestSync(this);
          Log.d("Gallery.MultiSetSync", "  request sync: " + Utils.maskDebugInfo(paramSyncListener[i].getName()));
          ++i;
        }
        return;
      }
      finally
      {
        monitorexit;
      }
    }

    public void cancel()
    {
      monitorenter;
      try
      {
        boolean bool = this.mIsCancelled;
        if (bool);
        do
        {
          return;
          this.mIsCancelled = true;
          Future[] arrayOfFuture = this.mFutures;
          int i = arrayOfFuture.length;
          for (int j = 0; j < i; ++j)
            arrayOfFuture[j].cancel();
        }
        while (this.mResult >= 0);
      }
      finally
      {
        monitorexit;
      }
    }

    public Integer get()
    {
      monitorenter;
      try
      {
        waitDone();
        Integer localInteger = Integer.valueOf(this.mResult);
        monitorexit;
        return localInteger;
      }
      finally
      {
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }

    public boolean isCancelled()
    {
      monitorenter;
      try
      {
        boolean bool = this.mIsCancelled;
        monitorexit;
        return bool;
      }
      finally
      {
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }

    public boolean isDone()
    {
      monitorenter;
      try
      {
        int i = this.mPendingCount;
        if (i == 0)
        {
          j = 1;
          return j;
        }
        int j = 0;
      }
      finally
      {
        monitorexit;
      }
    }

    public void onSyncDone(MediaSet paramMediaSet, int paramInt)
    {
      monitorenter;
      if (paramInt == 2);
      try
      {
        this.mResult = 2;
        this.mPendingCount = (-1 + this.mPendingCount);
        int i = this.mPendingCount;
        MediaSet.SyncListener localSyncListener = null;
        if (i == 0)
        {
          localSyncListener = this.mListener;
          super.notifyAll();
        }
        Log.d("Gallery.MultiSetSync", "onSyncDone: " + Utils.maskDebugInfo(paramMediaSet.getName()) + " #pending=" + this.mPendingCount);
        monitorexit;
        if (localSyncListener != null);
        return;
      }
      finally
      {
        monitorexit;
      }
    }

    public void waitDone()
    {
      monitorenter;
      while (true)
        try
        {
          if (isDone())
            break label25;
        }
        catch (InterruptedException localInterruptedException)
        {
          Log.d("Gallery.MultiSetSync", "waitDone() interrupted");
          label25: return;
        }
        finally
        {
          monitorexit;
        }
    }
  }

  public static abstract interface SyncListener
  {
    public abstract void onSyncDone(MediaSet paramMediaSet, int paramInt);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.MediaSet
 * JD-Core Version:    0.5.4
 */