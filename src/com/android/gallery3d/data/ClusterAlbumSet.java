package com.android.gallery3d.data;

import android.content.Context;
import android.net.Uri;
import com.android.gallery3d.app.GalleryApp;
import java.util.ArrayList;
import java.util.HashSet;

public class ClusterAlbumSet extends MediaSet
  implements ContentListener
{
  private ArrayList<ClusterAlbum> mAlbums = new ArrayList();
  private GalleryApp mApplication;
  private MediaSet mBaseSet;
  private boolean mFirstReloadDone;
  private int mKind;

  public ClusterAlbumSet(Path paramPath, GalleryApp paramGalleryApp, MediaSet paramMediaSet, int paramInt)
  {
    super(paramPath, -1L);
    this.mApplication = paramGalleryApp;
    this.mBaseSet = paramMediaSet;
    this.mKind = paramInt;
    paramMediaSet.addContentListener(this);
  }

  private void updateClusters()
  {
    this.mAlbums.clear();
    Context localContext = this.mApplication.getAndroidContext();
    Object localObject1;
    label65: DataManager localDataManager;
    int j;
    label92: String str;
    Path localPath;
    switch (this.mKind)
    {
    case 3:
    default:
      localObject1 = new SizeClustering(localContext);
      ((Clustering)localObject1).run(this.mBaseSet);
      int i = ((Clustering)localObject1).getNumberOfClusters();
      localDataManager = this.mApplication.getDataManager();
      j = 0;
      if (j >= i)
        return;
      str = ((Clustering)localObject1).getClusterName(j);
      if (this.mKind == 2)
        localPath = this.mPath.getChild(Uri.encode(str));
    case 0:
    case 1:
    case 2:
    case 4:
    }
    synchronized (DataManager.LOCK)
    {
      ClusterAlbum localClusterAlbum = (ClusterAlbum)localDataManager.peekMediaObject(localPath);
      if (localClusterAlbum == null)
        localClusterAlbum = new ClusterAlbum(localPath, localDataManager, this);
      localClusterAlbum.setMediaItems(((Clustering)localObject1).getCluster(j));
      localClusterAlbum.setName(str);
      localClusterAlbum.setCoverMediaItem(((Clustering)localObject1).getClusterCover(j));
      this.mAlbums.add(localClusterAlbum);
      ++j;
      break label92:
      localObject1 = new TimeClustering(localContext);
      break label65:
      localObject1 = new LocationClustering(localContext);
      break label65:
      localObject1 = new TagClustering(localContext);
      break label65:
      localObject1 = new FaceClustering(localContext);
      break label65:
      if (this.mKind == 3)
      {
        long l = ((SizeClustering)localObject1).getMinSize(j);
        localPath = this.mPath.getChild(l);
      }
      localPath = this.mPath.getChild(j);
    }
  }

  private void updateClustersContents()
  {
    HashSet localHashSet = new HashSet();
    this.mBaseSet.enumerateTotalMediaItems(new MediaSet.ItemConsumer(localHashSet)
    {
      public void consume(int paramInt, MediaItem paramMediaItem)
      {
        this.val$existing.add(paramMediaItem.getPath());
      }
    });
    for (int i = -1 + this.mAlbums.size(); i >= 0; --i)
    {
      ArrayList localArrayList1 = ((ClusterAlbum)this.mAlbums.get(i)).getMediaItems();
      ArrayList localArrayList2 = new ArrayList();
      int j = localArrayList1.size();
      for (int k = 0; k < j; ++k)
      {
        Path localPath = (Path)localArrayList1.get(k);
        if (!localHashSet.contains(localPath))
          continue;
        localArrayList2.add(localPath);
      }
      ((ClusterAlbum)this.mAlbums.get(i)).setMediaItems(localArrayList2);
      if (!localArrayList2.isEmpty())
        continue;
      this.mAlbums.remove(i);
    }
  }

  public String getName()
  {
    return this.mBaseSet.getName();
  }

  public MediaSet getSubMediaSet(int paramInt)
  {
    return (MediaSet)this.mAlbums.get(paramInt);
  }

  public int getSubMediaSetCount()
  {
    return this.mAlbums.size();
  }

  public void onContentDirty()
  {
    notifyContentChanged();
  }

  public long reload()
  {
    if (this.mBaseSet.reload() > this.mDataVersion)
    {
      if (!this.mFirstReloadDone)
        break label38;
      updateClustersContents();
    }
    while (true)
    {
      this.mDataVersion = nextVersionNumber();
      return this.mDataVersion;
      label38: updateClusters();
      this.mFirstReloadDone = true;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.ClusterAlbumSet
 * JD-Core Version:    0.5.4
 */