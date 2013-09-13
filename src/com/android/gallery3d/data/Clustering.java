package com.android.gallery3d.data;

import java.util.ArrayList;

public abstract class Clustering
{
  public abstract ArrayList<Path> getCluster(int paramInt);

  public MediaItem getClusterCover(int paramInt)
  {
    return null;
  }

  public abstract String getClusterName(int paramInt);

  public abstract int getNumberOfClusters();

  public abstract void run(MediaSet paramMediaSet);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.Clustering
 * JD-Core Version:    0.5.4
 */