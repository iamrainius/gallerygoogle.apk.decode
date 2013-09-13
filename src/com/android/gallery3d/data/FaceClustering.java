package com.android.gallery3d.data;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import com.android.gallery3d.picasasource.PicasaSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

public class FaceClustering extends Clustering
{
  private FaceCluster[] mClusters;
  private Context mContext;
  private String mUntaggedString;

  public FaceClustering(Context paramContext)
  {
    this.mUntaggedString = paramContext.getResources().getString(2131362242);
    this.mContext = paramContext;
  }

  public ArrayList<Path> getCluster(int paramInt)
  {
    return this.mClusters[paramInt].mPaths;
  }

  public MediaItem getClusterCover(int paramInt)
  {
    return this.mClusters[paramInt].getCover();
  }

  public String getClusterName(int paramInt)
  {
    return this.mClusters[paramInt].mName;
  }

  public int getNumberOfClusters()
  {
    return this.mClusters.length;
  }

  public void run(MediaSet paramMediaSet)
  {
    TreeMap localTreeMap = new TreeMap();
    FaceCluster localFaceCluster = new FaceCluster(this.mUntaggedString);
    paramMediaSet.enumerateTotalMediaItems(new MediaSet.ItemConsumer(localFaceCluster, localTreeMap)
    {
      public void consume(int paramInt, MediaItem paramMediaItem)
      {
        Face[] arrayOfFace = paramMediaItem.getFaces();
        if ((arrayOfFace == null) || (arrayOfFace.length == 0))
        {
          this.val$untagged.add(paramMediaItem, -1);
          return;
        }
        for (int i = 0; ; ++i)
        {
          if (i < arrayOfFace.length);
          Face localFace = arrayOfFace[i];
          FaceClustering.FaceCluster localFaceCluster = (FaceClustering.FaceCluster)this.val$map.get(localFace);
          if (localFaceCluster == null)
          {
            localFaceCluster = new FaceClustering.FaceCluster(FaceClustering.this, localFace.getName());
            this.val$map.put(localFace, localFaceCluster);
          }
          localFaceCluster.add(paramMediaItem, i);
        }
      }
    });
    int i = localTreeMap.size();
    Collection localCollection = localTreeMap.values();
    if (localFaceCluster.size() > 0);
    for (int j = 1; ; j = 0)
    {
      this.mClusters = ((FaceCluster[])localCollection.toArray(new FaceCluster[j + i]));
      if (localFaceCluster.size() > 0)
        this.mClusters[i] = localFaceCluster;
      return;
    }
  }

  private class FaceCluster
  {
    int mCoverFaceIndex;
    MediaItem mCoverItem;
    Rect mCoverRegion;
    String mName;
    ArrayList<Path> mPaths = new ArrayList();

    public FaceCluster(String arg2)
    {
      Object localObject;
      this.mName = localObject;
    }

    public void add(MediaItem paramMediaItem, int paramInt)
    {
      Path localPath = paramMediaItem.getPath();
      this.mPaths.add(localPath);
      Face[] arrayOfFace = paramMediaItem.getFaces();
      Face localFace;
      if (arrayOfFace != null)
      {
        localFace = arrayOfFace[paramInt];
        if (this.mCoverItem != null)
          break label58;
        this.mCoverItem = paramMediaItem;
        this.mCoverRegion = localFace.getPosition();
        this.mCoverFaceIndex = paramInt;
      }
      label58: Rect localRect;
      do
      {
        return;
        localRect = localFace.getPosition();
      }
      while ((this.mCoverRegion.width() >= localRect.width()) || (this.mCoverRegion.height() >= localRect.height()));
      this.mCoverItem = paramMediaItem;
      this.mCoverRegion = localFace.getPosition();
      this.mCoverFaceIndex = paramInt;
    }

    public MediaItem getCover()
    {
      if (this.mCoverItem != null)
      {
        if (PicasaSource.isPicasaImage(this.mCoverItem))
          return PicasaSource.getFaceItem(FaceClustering.this.mContext, this.mCoverItem, this.mCoverFaceIndex);
        return this.mCoverItem;
      }
      return null;
    }

    public int size()
    {
      return this.mPaths.size();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.FaceClustering
 * JD-Core Version:    0.5.4
 */