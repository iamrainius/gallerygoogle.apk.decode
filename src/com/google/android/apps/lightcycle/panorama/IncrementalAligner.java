package com.google.android.apps.lightcycle.panorama;

import android.util.Log;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.LG;
import com.google.android.apps.lightcycle.util.Size;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

public class IncrementalAligner extends Thread
{
  public static final String TAG = IncrementalAligner.class.getSimpleName();
  private Callback<Void> doneCallback = null;
  boolean extractFeaturesAndThumbnail = true;
  private final ArrayBlockingQueue<ImageData> imagesToProcess = new ArrayBlockingQueue(20);
  private Size photoSize;
  private boolean processingImages = false;
  private final boolean useRealtimeAlignment;

  public IncrementalAligner(boolean paramBoolean)
  {
    this.useRealtimeAlignment = paramBoolean;
  }

  public void addImage(String paramString, float[] paramArrayOfFloat, int paramInt)
  {
    try
    {
      this.imagesToProcess.put(new ImageData(paramString, paramArrayOfFloat, paramInt));
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Unexpected interruption");
    }
  }

  public void interrupt()
  {
    this.imagesToProcess.add(new ImageData("Poison Pill", new float[9], 0));
  }

  public boolean isExtractFeaturesAndThumbnailEnabled()
  {
    return this.extractFeaturesAndThumbnail;
  }

  public boolean isProcessingImages()
  {
    return this.processingImages;
  }

  public boolean isRealtimeAlignmentEnabled()
  {
    return this.useRealtimeAlignment;
  }

  public void run()
  {
    ArrayList localArrayList;
    if (!isInterrupted())
      localArrayList = new ArrayList();
    while (true)
    {
      label65: int i;
      ImageData localImageData1;
      try
      {
        ImageData localImageData2 = (ImageData)this.imagesToProcess.take();
        this.processingImages = true;
        localArrayList.add(localImageData2);
        if (this.imagesToProcess.isEmpty())
          break label65;
        localArrayList.add(this.imagesToProcess.take());
      }
      catch (InterruptedException localInterruptedException)
      {
        Iterator localIterator = localArrayList.iterator();
        boolean bool1 = localIterator.hasNext();
        i = 0;
        if (bool1)
        {
          localImageData1 = (ImageData)localIterator.next();
          if (!"Poison Pill".equals(localImageData1.filename))
            break label176;
          i = 1;
        }
        if ((this.useRealtimeAlignment) && (this.extractFeaturesAndThumbnail))
          LightCycleNative.ComputeAlignment();
        this.processingImages = false;
      }
      if (i != 0);
      Log.d(TAG, "Incremental aligner shutting down. Firing callback ...");
      if (this.doneCallback != null)
        this.doneCallback.onCallback(null);
      Log.d(TAG, "Incremental aligner thread shut down. Bye.");
      return;
      label176: boolean bool2 = this.useRealtimeAlignment;
      LG.d("Processing file " + localImageData1.filename);
      LightCycleNative.AddImage(localImageData1.filename, localImageData1.thumbnailTextureId, this.photoSize.width, this.photoSize.height, localImageData1.rotation, this.extractFeaturesAndThumbnail, bool2);
    }
  }

  // ERROR //
  public void shutdown(Callback<Void> paramCallback)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokevirtual 87	com/google/android/apps/lightcycle/panorama/IncrementalAligner:isInterrupted	()Z
    //   6: ifne +10 -> 16
    //   9: aload_0
    //   10: invokevirtual 181	com/google/android/apps/lightcycle/panorama/IncrementalAligner:isAlive	()Z
    //   13: ifne +18 -> 31
    //   16: new 68	java/lang/RuntimeException
    //   19: dup
    //   20: ldc 183
    //   22: invokespecial 73	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   25: athrow
    //   26: astore_2
    //   27: aload_0
    //   28: monitorexit
    //   29: aload_2
    //   30: athrow
    //   31: aload_0
    //   32: aload_1
    //   33: putfield 43	com/google/android/apps/lightcycle/panorama/IncrementalAligner:doneCallback	Lcom/google/android/apps/lightcycle/util/Callback;
    //   36: aload_0
    //   37: invokevirtual 184	com/google/android/apps/lightcycle/panorama/IncrementalAligner:interrupt	()V
    //   40: aload_0
    //   41: monitorexit
    //   42: return
    //
    // Exception table:
    //   from	to	target	type
    //   2	16	26	finally
    //   16	26	26	finally
    //   31	40	26	finally
  }

  public void start(Size paramSize)
  {
    this.photoSize = paramSize;
    super.start();
    LG.d("Aligner start");
  }

  private static class ImageData
  {
    public final String filename;
    public final float[] rotation;
    public final int thumbnailTextureId;

    ImageData(String paramString, float[] paramArrayOfFloat, int paramInt)
    {
      this.filename = paramString;
      this.rotation = paramArrayOfFloat;
      this.thumbnailTextureId = paramInt;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.IncrementalAligner
 * JD-Core Version:    0.5.4
 */