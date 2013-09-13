package com.android.gallery3d.data;

import java.util.ArrayList;

public class FilterEmptyPromptSet extends MediaSet
  implements ContentListener
{
  private MediaSet mBaseSet;
  private ArrayList<MediaItem> mEmptyItem = new ArrayList(1);

  public FilterEmptyPromptSet(Path paramPath, MediaSet paramMediaSet, MediaItem paramMediaItem)
  {
    super(paramPath, -1L);
    this.mEmptyItem.add(paramMediaItem);
    this.mBaseSet = paramMediaSet;
    this.mBaseSet.addContentListener(this);
  }

  public ArrayList<MediaItem> getMediaItem(int paramInt1, int paramInt2)
  {
    if (this.mBaseSet.getMediaItemCount() > 0)
      return this.mBaseSet.getMediaItem(paramInt1, paramInt2);
    if ((paramInt1 == 0) && (paramInt2 == 1))
      return this.mEmptyItem;
    throw new ArrayIndexOutOfBoundsException();
  }

  public int getMediaItemCount()
  {
    int i = this.mBaseSet.getMediaItemCount();
    if (i > 0)
      return i;
    return 1;
  }

  public String getName()
  {
    return this.mBaseSet.getName();
  }

  public boolean isCameraRoll()
  {
    return this.mBaseSet.isCameraRoll();
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
    return this.mBaseSet.reload();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.FilterEmptyPromptSet
 * JD-Core Version:    0.5.4
 */