package com.google.android.apps.lightcycle.panorama;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.google.android.apps.lightcycle.storage.LocalSessionStorage;
import com.google.android.apps.lightcycle.util.LG;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class StitchingServiceManager
{
  private static StitchingServiceManager stitchingServiceManager;
  private final Context appContext;
  private int notificationId = 2;
  private boolean serviceRunning = false;
  private Queue<StitchSession> stitchQueue = new LinkedList();
  private List<ProgressUpdateCallback> stitchingProgressCallbacks = new ArrayList();
  private List<StitchingQueuedCallback> stitchingQueuedCallbacks = new ArrayList();
  private List<StitchingResultCallback> stitchingResultCallbacks = new ArrayList();

  private StitchingServiceManager(Context paramContext)
  {
    this.appContext = paramContext;
  }

  public static StitchingServiceManager getStitchingServiceManager(Context paramContext)
  {
    if (stitchingServiceManager == null)
      stitchingServiceManager = new StitchingServiceManager(paramContext);
    return stitchingServiceManager;
  }

  public void addStitchingQueuedCallback(StitchingQueuedCallback paramStitchingQueuedCallback)
  {
    this.stitchingQueuedCallbacks.add(paramStitchingQueuedCallback);
  }

  public void addStitchingResultCallback(StitchingResultCallback paramStitchingResultCallback)
  {
    this.stitchingResultCallbacks.add(paramStitchingResultCallback);
  }

  public void newTask(LocalSessionStorage paramLocalSessionStorage)
  {
    monitorenter;
    try
    {
      Queue localQueue = this.stitchQueue;
      int i = this.notificationId;
      this.notificationId = (i + 1);
      localQueue.add(new StitchSession(paramLocalSessionStorage, i));
      onStitchingQueued(paramLocalSessionStorage);
      LG.d("Added to queue. Size is now " + this.stitchQueue.size());
      if (!this.serviceRunning)
        this.appContext.startService(new Intent(this.appContext, StitchingService.class));
      this.serviceRunning = true;
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  public void onStitchingProgress(String paramString, Uri paramUri, int paramInt)
  {
    monitorenter;
    Iterator localIterator;
    try
    {
      localIterator = this.stitchingProgressCallbacks.iterator();
      if (!localIterator.hasNext())
        break label51;
    }
    finally
    {
      monitorexit;
    }
    label51: monitorexit;
  }

  public void onStitchingQueued(LocalSessionStorage paramLocalSessionStorage)
  {
    monitorenter;
    Iterator localIterator;
    try
    {
      localIterator = this.stitchingQueuedCallbacks.iterator();
      if (!localIterator.hasNext())
        break label51;
    }
    finally
    {
      monitorexit;
    }
    label51: monitorexit;
  }

  public void onStitchingResult(String paramString, Uri paramUri)
  {
    monitorenter;
    Iterator localIterator;
    try
    {
      localIterator = this.stitchingResultCallbacks.iterator();
      if (!localIterator.hasNext())
        break label48;
    }
    finally
    {
      monitorexit;
    }
    label48: monitorexit;
  }

  // ERROR //
  public StitchSession popNextSession()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 43	com/google/android/apps/lightcycle/panorama/StitchingServiceManager:stitchQueue	Ljava/util/Queue;
    //   6: invokeinterface 164 1 0
    //   11: checkcast 67	com/google/android/apps/lightcycle/panorama/StitchingServiceManager$StitchSession
    //   14: astore_3
    //   15: aload_0
    //   16: monitorexit
    //   17: aload_3
    //   18: areturn
    //   19: astore_2
    //   20: aconst_null
    //   21: astore_3
    //   22: goto -7 -> 15
    //   25: astore_1
    //   26: aload_0
    //   27: monitorexit
    //   28: aload_1
    //   29: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   2	15	19	java/util/NoSuchElementException
    //   2	15	25	finally
  }

  public void setStitchingProgressCallback(ProgressUpdateCallback paramProgressUpdateCallback)
  {
    this.stitchingProgressCallbacks.add(paramProgressUpdateCallback);
  }

  public void stitchingFinished()
  {
    monitorenter;
    try
    {
      this.serviceRunning = false;
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

  public static abstract interface ProgressUpdateCallback
  {
    public abstract void onProgress(String paramString, Uri paramUri, int paramInt);
  }

  public static class StitchSession
  {
    public final int notificationId;
    public final LocalSessionStorage storage;

    StitchSession(LocalSessionStorage paramLocalSessionStorage, int paramInt)
    {
      this.storage = paramLocalSessionStorage;
      this.notificationId = paramInt;
    }
  }

  public static abstract interface StitchingQueuedCallback
  {
    public abstract void onStitchingQueued(String paramString, Uri paramUri);
  }

  public static abstract interface StitchingResultCallback
  {
    public abstract void onResult(String paramString, Uri paramUri);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.StitchingServiceManager
 * JD-Core Version:    0.5.4
 */