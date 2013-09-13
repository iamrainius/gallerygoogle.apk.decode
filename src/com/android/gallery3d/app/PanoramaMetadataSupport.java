package com.android.gallery3d.app;

import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaObject.PanoramaSupportCallback;
import com.android.gallery3d.data.PanoramaMetadataJob;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.LightCycleHelper;
import com.android.gallery3d.util.LightCycleHelper.PanoramaMetadata;
import com.android.gallery3d.util.ThreadPool;
import java.util.ArrayList;
import java.util.Iterator;

public class PanoramaMetadataSupport
  implements FutureListener<LightCycleHelper.PanoramaMetadata>
{
  private ArrayList<MediaObject.PanoramaSupportCallback> mCallbacksWaiting;
  private Future<LightCycleHelper.PanoramaMetadata> mGetPanoMetadataTask;
  private Object mLock = new Object();
  private MediaObject mMediaObject;
  private LightCycleHelper.PanoramaMetadata mPanoramaMetadata;

  public PanoramaMetadataSupport(MediaObject paramMediaObject)
  {
    this.mMediaObject = paramMediaObject;
  }

  public void clearCachedValues()
  {
    while (true)
    {
      synchronized (this.mLock)
      {
        if (this.mPanoramaMetadata != null)
          this.mPanoramaMetadata = null;
        do
          return;
        while (this.mGetPanoMetadataTask == null);
        this.mGetPanoMetadataTask.cancel();
        Iterator localIterator = this.mCallbacksWaiting.iterator();
        if (localIterator.hasNext())
          ((MediaObject.PanoramaSupportCallback)localIterator.next()).panoramaInfoAvailable(this.mMediaObject, false, false);
      }
      this.mGetPanoMetadataTask = null;
      this.mCallbacksWaiting = null;
    }
  }

  public void getPanoramaSupport(GalleryApp paramGalleryApp, MediaObject.PanoramaSupportCallback paramPanoramaSupportCallback)
  {
    synchronized (this.mLock)
    {
      if (this.mPanoramaMetadata != null)
      {
        paramPanoramaSupportCallback.panoramaInfoAvailable(this.mMediaObject, this.mPanoramaMetadata.mUsePanoramaViewer, this.mPanoramaMetadata.mIsPanorama360);
        return;
      }
      if (this.mCallbacksWaiting == null)
      {
        this.mCallbacksWaiting = new ArrayList();
        this.mGetPanoMetadataTask = paramGalleryApp.getThreadPool().submit(new PanoramaMetadataJob(paramGalleryApp.getAndroidContext(), this.mMediaObject.getContentUri()), this);
      }
      this.mCallbacksWaiting.add(paramPanoramaSupportCallback);
    }
  }

  public void onFutureDone(Future<LightCycleHelper.PanoramaMetadata> paramFuture)
  {
    synchronized (this.mLock)
    {
      this.mPanoramaMetadata = ((LightCycleHelper.PanoramaMetadata)paramFuture.get());
      if (this.mPanoramaMetadata == null)
        this.mPanoramaMetadata = LightCycleHelper.NOT_PANORAMA;
      Iterator localIterator = this.mCallbacksWaiting.iterator();
      if (localIterator.hasNext())
        ((MediaObject.PanoramaSupportCallback)localIterator.next()).panoramaInfoAvailable(this.mMediaObject, this.mPanoramaMetadata.mUsePanoramaViewer, this.mPanoramaMetadata.mIsPanorama360);
    }
    this.mGetPanoMetadataTask = null;
    this.mCallbacksWaiting = null;
    monitorexit;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.PanoramaMetadataSupport
 * JD-Core Version:    0.5.4
 */