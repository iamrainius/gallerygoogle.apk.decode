package com.android.gallery3d.data;

import android.content.Context;
import android.net.Uri;
import com.android.gallery3d.util.LightCycleHelper;
import com.android.gallery3d.util.LightCycleHelper.PanoramaMetadata;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;

public class PanoramaMetadataJob
  implements ThreadPool.Job<LightCycleHelper.PanoramaMetadata>
{
  Context mContext;
  Uri mUri;

  public PanoramaMetadataJob(Context paramContext, Uri paramUri)
  {
    this.mContext = paramContext;
    this.mUri = paramUri;
  }

  public LightCycleHelper.PanoramaMetadata run(ThreadPool.JobContext paramJobContext)
  {
    return LightCycleHelper.getPanoramaMetadata(this.mContext, this.mUri);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.PanoramaMetadataJob
 * JD-Core Version:    0.5.4
 */