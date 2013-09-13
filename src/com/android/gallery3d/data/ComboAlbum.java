package com.android.gallery3d.data;

import com.android.gallery3d.util.Future;
import java.util.ArrayList;

public class ComboAlbum extends MediaSet
  implements ContentListener
{
  private String mName;
  private final MediaSet[] mSets;

  public ComboAlbum(Path paramPath, MediaSet[] paramArrayOfMediaSet, String paramString)
  {
    super(paramPath, nextVersionNumber());
    this.mSets = paramArrayOfMediaSet;
    MediaSet[] arrayOfMediaSet = this.mSets;
    int i = arrayOfMediaSet.length;
    for (int j = 0; j < i; ++j)
      arrayOfMediaSet[j].addContentListener(this);
    this.mName = paramString;
  }

  public ArrayList<MediaItem> getMediaItem(int paramInt1, int paramInt2)
  {
    ArrayList localArrayList1 = new ArrayList();
    MediaSet[] arrayOfMediaSet = this.mSets;
    int i = arrayOfMediaSet.length;
    int j = 0;
    label22: MediaSet localMediaSet;
    int k;
    if (j < i)
    {
      localMediaSet = arrayOfMediaSet[j];
      k = localMediaSet.getMediaItemCount();
      if (paramInt2 >= 1)
        break label50;
    }
    return localArrayList1;
    label50: int l;
    if (paramInt1 < k)
      if (paramInt1 + paramInt2 <= k)
      {
        l = paramInt2;
        label67: ArrayList localArrayList2 = localMediaSet.getMediaItem(paramInt1, l);
        localArrayList1.addAll(localArrayList2);
        paramInt2 -= localArrayList2.size();
        paramInt1 = 0;
      }
    while (true)
    {
      ++j;
      break label22:
      l = k - paramInt1;
      break label67:
      paramInt1 -= k;
    }
  }

  public int getMediaItemCount()
  {
    int i = 0;
    MediaSet[] arrayOfMediaSet = this.mSets;
    int j = arrayOfMediaSet.length;
    for (int k = 0; k < j; ++k)
      i += arrayOfMediaSet[k].getMediaItemCount();
    return i;
  }

  public String getName()
  {
    return this.mName;
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
    int i = 0;
    int j = 0;
    int k = this.mSets.length;
    while (j < k)
    {
      if (this.mSets[j].reload() > this.mDataVersion)
        i = 1;
      ++j;
    }
    if (i != 0)
      this.mDataVersion = nextVersionNumber();
    return this.mDataVersion;
  }

  public Future<Integer> requestSync(MediaSet.SyncListener paramSyncListener)
  {
    return requestSyncOnMultipleSets(this.mSets, paramSyncListener);
  }

  public void useNameOfChild(int paramInt)
  {
    if (paramInt >= this.mSets.length)
      return;
    this.mName = this.mSets[paramInt].getName();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.ComboAlbum
 * JD-Core Version:    0.5.4
 */