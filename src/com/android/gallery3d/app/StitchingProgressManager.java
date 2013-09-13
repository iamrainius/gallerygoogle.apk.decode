package com.android.gallery3d.app;

import android.net.Uri;
import com.android.camera.Util;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.ImageCacheService;
import com.android.gallery3d.data.Path;
import com.google.android.apps.lightcycle.panorama.StitchingServiceManager;
import com.google.android.apps.lightcycle.panorama.StitchingServiceManager.ProgressUpdateCallback;
import com.google.android.apps.lightcycle.panorama.StitchingServiceManager.StitchingQueuedCallback;
import com.google.android.apps.lightcycle.panorama.StitchingServiceManager.StitchingResultCallback;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class StitchingProgressManager
{
  private GalleryApp mApplication;
  private final HashMap<Uri, Integer> mItems;
  private ArrayList<WeakReference<StitchingChangeListener>> mListenerRefs;

  public StitchingProgressManager(GalleryApp paramGalleryApp)
  {
    this.mApplication = paramGalleryApp;
    this.mItems = new HashMap();
    this.mListenerRefs = new ArrayList();
    StitchingServiceManager localStitchingServiceManager = StitchingServiceManager.getStitchingServiceManager(this.mApplication.getAndroidContext());
    localStitchingServiceManager.addStitchingResultCallback(new ProgressCompleteCallback(null));
    localStitchingServiceManager.addStitchingQueuedCallback(new AddItemCallback(null));
    localStitchingServiceManager.setStitchingProgressCallback(new UpdateProgressCallback(null));
  }

  private int findListener(StitchingChangeListener paramStitchingChangeListener)
  {
    int i = -1;
    for (int j = 0; ; ++j)
    {
      if (j < this.mListenerRefs.size())
      {
        StitchingChangeListener localStitchingChangeListener = (StitchingChangeListener)((WeakReference)this.mListenerRefs.get(j)).get();
        if ((localStitchingChangeListener == null) || (localStitchingChangeListener != paramStitchingChangeListener))
          continue;
        i = j;
      }
      return i;
    }
  }

  private ListenerIterable getListeners()
  {
    return new ListenerIterable(null);
  }

  public void addChangeListener(StitchingChangeListener paramStitchingChangeListener)
  {
    synchronized (this.mListenerRefs)
    {
      if (findListener(paramStitchingChangeListener) == -1)
        this.mListenerRefs.add(new WeakReference(paramStitchingChangeListener));
      return;
    }
  }

  public void clearCachedThumbnails(Uri paramUri)
  {
    Path localPath = this.mApplication.getDataManager().findPathByUri(paramUri, null);
    ImageCacheService localImageCacheService = this.mApplication.getImageCacheService();
    localImageCacheService.clearImageData(localPath, 1);
    localImageCacheService.clearImageData(localPath, 2);
  }

  public Integer getProgress(Uri paramUri)
  {
    synchronized (this.mItems)
    {
      Integer localInteger = (Integer)this.mItems.get(paramUri);
      return localInteger;
    }
  }

  private class AddItemCallback
    implements StitchingServiceManager.StitchingQueuedCallback
  {
    private AddItemCallback()
    {
    }

    public void onStitchingQueued(String paramString, Uri paramUri)
    {
      synchronized (StitchingProgressManager.this.mItems)
      {
        StitchingProgressManager.this.mItems.put(paramUri, Integer.valueOf(0));
        synchronized (StitchingProgressManager.this.mListenerRefs)
        {
          Iterator localIterator = StitchingProgressManager.this.getListeners().iterator();
          if (!localIterator.hasNext())
            break label94;
          ((StitchingChangeListener)localIterator.next()).onStitchingQueued(paramUri);
        }
      }
      label94: monitorexit;
      monitorexit;
    }
  }

  private class ListenerIterable
    implements Iterable<StitchingChangeListener>
  {
    private ListenerIterable()
    {
    }

    public Iterator<StitchingChangeListener> iterator()
    {
      return new StitchingProgressManager.ListenerIterator(StitchingProgressManager.this, null);
    }
  }

  private class ListenerIterator
    implements Iterator<StitchingChangeListener>
  {
    private int mIndex = 0;
    private StitchingChangeListener mNext = null;

    private ListenerIterator()
    {
    }

    public boolean hasNext()
    {
      while ((this.mNext == null) && (this.mIndex < StitchingProgressManager.this.mListenerRefs.size()))
      {
        do
          this.mNext = ((StitchingChangeListener)((WeakReference)StitchingProgressManager.this.mListenerRefs.get(this.mIndex)).get());
        while (this.mNext != null);
        StitchingProgressManager.this.mListenerRefs.remove(this.mIndex);
      }
      return this.mNext != null;
    }

    public StitchingChangeListener next()
    {
      hasNext();
      this.mIndex = (1 + this.mIndex);
      StitchingChangeListener localStitchingChangeListener = this.mNext;
      this.mNext = null;
      return localStitchingChangeListener;
    }

    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

  private class ProgressCompleteCallback
    implements StitchingServiceManager.StitchingResultCallback
  {
    private ProgressCompleteCallback()
    {
    }

    public void onResult(String paramString, Uri paramUri)
    {
      synchronized (StitchingProgressManager.this.mItems)
      {
        if ((Integer)StitchingProgressManager.this.mItems.get(paramUri) == null)
          return;
        StitchingProgressManager.this.mItems.remove(paramUri);
        StitchingProgressManager.this.clearCachedThumbnails(paramUri);
        synchronized (StitchingProgressManager.this.mListenerRefs)
        {
          Iterator localIterator = StitchingProgressManager.this.getListeners().iterator();
          if (!localIterator.hasNext())
            break label118;
          ((StitchingChangeListener)localIterator.next()).onStitchingResult(paramUri);
        }
      }
      label118: monitorexit;
      Util.broadcastNewPicture(StitchingProgressManager.this.mApplication.getAndroidContext(), paramUri);
      monitorexit;
    }
  }

  private class UpdateProgressCallback
    implements StitchingServiceManager.ProgressUpdateCallback
  {
    private UpdateProgressCallback()
    {
    }

    public void onProgress(String paramString, Uri paramUri, int paramInt)
    {
      synchronized (StitchingProgressManager.this.mItems)
      {
        if ((Integer)StitchingProgressManager.this.mItems.get(paramUri) == null)
          return;
        StitchingProgressManager.this.mItems.put(paramUri, Integer.valueOf(paramInt));
        synchronized (StitchingProgressManager.this.mListenerRefs)
        {
          Iterator localIterator = StitchingProgressManager.this.getListeners().iterator();
          if (!localIterator.hasNext())
            break label119;
          ((StitchingChangeListener)localIterator.next()).onStitchingProgress(paramUri, paramInt);
        }
      }
      label119: monitorexit;
      monitorexit;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.StitchingProgressManager
 * JD-Core Version:    0.5.4
 */