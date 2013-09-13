package com.android.gallery3d.data;

import java.util.ArrayList;

public class ClusterAlbum extends MediaSet
  implements ContentListener
{
  private MediaSet mClusterAlbumSet;
  private MediaItem mCover;
  private DataManager mDataManager;
  private String mName = "";
  private ArrayList<Path> mPaths = new ArrayList();

  public ClusterAlbum(Path paramPath, DataManager paramDataManager, MediaSet paramMediaSet)
  {
    super(paramPath, nextVersionNumber());
    this.mDataManager = paramDataManager;
    this.mClusterAlbumSet = paramMediaSet;
    this.mClusterAlbumSet.addContentListener(this);
  }

  public static ArrayList<MediaItem> getMediaItemFromPath(ArrayList<Path> paramArrayList, int paramInt1, int paramInt2, DataManager paramDataManager)
  {
    if (paramInt1 >= paramArrayList.size())
    {
      localArrayList1 = new ArrayList();
      return localArrayList1;
    }
    int i = Math.min(paramInt1 + paramInt2, paramArrayList.size());
    ArrayList localArrayList2 = new ArrayList(paramArrayList.subList(paramInt1, i));
    MediaItem[] arrayOfMediaItem = new MediaItem[i - paramInt1];
    paramDataManager.mapMediaItems(localArrayList2, new MediaSet.ItemConsumer(arrayOfMediaItem)
    {
      public void consume(int paramInt, MediaItem paramMediaItem)
      {
        this.val$buf[paramInt] = paramMediaItem;
      }
    }
    , 0);
    ArrayList localArrayList1 = new ArrayList(i - paramInt1);
    for (int j = 0; ; ++j)
    {
      if (j < arrayOfMediaItem.length);
      localArrayList1.add(arrayOfMediaItem[j]);
    }
  }

  public void delete()
  {
    2 local2 = new MediaSet.ItemConsumer()
    {
      public void consume(int paramInt, MediaItem paramMediaItem)
      {
        if ((0x1 & paramMediaItem.getSupportedOperations()) == 0)
          return;
        paramMediaItem.delete();
      }
    };
    this.mDataManager.mapMediaItems(this.mPaths, local2, 0);
  }

  protected int enumerateMediaItems(MediaSet.ItemConsumer paramItemConsumer, int paramInt)
  {
    this.mDataManager.mapMediaItems(this.mPaths, paramItemConsumer, paramInt);
    return this.mPaths.size();
  }

  public MediaItem getCoverMediaItem()
  {
    if (this.mCover != null)
      return this.mCover;
    return super.getCoverMediaItem();
  }

  public ArrayList<MediaItem> getMediaItem(int paramInt1, int paramInt2)
  {
    return getMediaItemFromPath(this.mPaths, paramInt1, paramInt2, this.mDataManager);
  }

  public int getMediaItemCount()
  {
    return this.mPaths.size();
  }

  ArrayList<Path> getMediaItems()
  {
    return this.mPaths;
  }

  public String getName()
  {
    return this.mName;
  }

  public int getSupportedOperations()
  {
    return 1029;
  }

  public int getTotalMediaItemCount()
  {
    return this.mPaths.size();
  }

  public boolean isLeafAlbum()
  {
    return true;
  }

  public void onContentDirty()
  {
    notifyContentChanged();
  }

  public long reload()
  {
    if (this.mClusterAlbumSet.reload() > this.mDataVersion)
      this.mDataVersion = nextVersionNumber();
    return this.mDataVersion;
  }

  public void setCoverMediaItem(MediaItem paramMediaItem)
  {
    this.mCover = paramMediaItem;
  }

  void setMediaItems(ArrayList<Path> paramArrayList)
  {
    this.mPaths = paramArrayList;
  }

  public void setName(String paramString)
  {
    this.mName = paramString;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.ClusterAlbum
 * JD-Core Version:    0.5.4
 */