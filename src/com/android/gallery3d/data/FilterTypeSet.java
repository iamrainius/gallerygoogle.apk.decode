package com.android.gallery3d.data;

import java.util.ArrayList;

public class FilterTypeSet extends MediaSet
  implements ContentListener
{
  private final ArrayList<MediaSet> mAlbums = new ArrayList();
  private final MediaSet mBaseSet;
  private final DataManager mDataManager;
  private final int mMediaType;
  private final ArrayList<Path> mPaths = new ArrayList();

  public FilterTypeSet(Path paramPath, DataManager paramDataManager, MediaSet paramMediaSet, int paramInt)
  {
    super(paramPath, -1L);
    this.mDataManager = paramDataManager;
    this.mBaseSet = paramMediaSet;
    this.mMediaType = paramInt;
    this.mBaseSet.addContentListener(this);
  }

  private void updateData()
  {
    this.mAlbums.clear();
    String str1 = "/filter/mediatype/" + this.mMediaType;
    int i = 0;
    int j = this.mBaseSet.getSubMediaSetCount();
    while (i < j)
    {
      MediaSet localMediaSet1 = this.mBaseSet.getSubMediaSet(i);
      String str2 = str1 + "/{" + localMediaSet1.getPath().toString() + "}";
      MediaSet localMediaSet2 = this.mDataManager.getMediaSet(str2);
      localMediaSet2.reload();
      if ((localMediaSet2.getMediaItemCount() > 0) || (localMediaSet2.getSubMediaSetCount() > 0))
        this.mAlbums.add(localMediaSet2);
      ++i;
    }
    this.mPaths.clear();
    int k = this.mBaseSet.getMediaItemCount();
    Path[] arrayOfPath = new Path[k];
    this.mBaseSet.enumerateMediaItems(new MediaSet.ItemConsumer(k, arrayOfPath)
    {
      public void consume(int paramInt, MediaItem paramMediaItem)
      {
        if ((paramMediaItem.getMediaType() != FilterTypeSet.this.mMediaType) || (paramInt < 0) || (paramInt >= this.val$total))
          return;
        Path localPath = paramMediaItem.getPath();
        this.val$buf[paramInt] = localPath;
      }
    });
    for (int l = 0; l < k; ++l)
    {
      if (arrayOfPath[l] == null)
        continue;
      this.mPaths.add(arrayOfPath[l]);
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

  public ArrayList<MediaItem> getMediaItem(int paramInt1, int paramInt2)
  {
    return ClusterAlbum.getMediaItemFromPath(this.mPaths, paramInt1, paramInt2, this.mDataManager);
  }

  public int getMediaItemCount()
  {
    return this.mPaths.size();
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

  public int getSupportedOperations()
  {
    return 5;
  }

  public void onContentDirty()
  {
    notifyContentChanged();
  }

  public long reload()
  {
    if (this.mBaseSet.reload() > this.mDataVersion)
    {
      updateData();
      this.mDataVersion = nextVersionNumber();
    }
    return this.mDataVersion;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.FilterTypeSet
 * JD-Core Version:    0.5.4
 */