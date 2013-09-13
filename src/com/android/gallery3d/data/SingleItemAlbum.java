package com.android.gallery3d.data;

import java.util.ArrayList;

public class SingleItemAlbum extends MediaSet
{
  private final MediaItem mItem;
  private final String mName;

  public SingleItemAlbum(Path paramPath, MediaItem paramMediaItem)
  {
    super(paramPath, nextVersionNumber());
    this.mItem = paramMediaItem;
    this.mName = ("SingleItemAlbum(" + this.mItem.getClass().getSimpleName() + ")");
  }

  public MediaItem getItem()
  {
    return this.mItem;
  }

  public ArrayList<MediaItem> getMediaItem(int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = new ArrayList();
    if ((paramInt1 <= 0) && (paramInt1 + paramInt2 > 0))
      localArrayList.add(this.mItem);
    return localArrayList;
  }

  public int getMediaItemCount()
  {
    return 1;
  }

  public String getName()
  {
    return this.mName;
  }

  public boolean isLeafAlbum()
  {
    return true;
  }

  public long reload()
  {
    return this.mDataVersion;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.SingleItemAlbum
 * JD-Core Version:    0.5.4
 */