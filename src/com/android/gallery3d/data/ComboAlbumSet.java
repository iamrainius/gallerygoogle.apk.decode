package com.android.gallery3d.data;

import android.content.res.Resources;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.util.Future;

public class ComboAlbumSet extends MediaSet
  implements ContentListener
{
  private final String mName;
  private final MediaSet[] mSets;

  public ComboAlbumSet(Path paramPath, GalleryApp paramGalleryApp, MediaSet[] paramArrayOfMediaSet)
  {
    super(paramPath, nextVersionNumber());
    this.mSets = paramArrayOfMediaSet;
    MediaSet[] arrayOfMediaSet = this.mSets;
    int i = arrayOfMediaSet.length;
    for (int j = 0; j < i; ++j)
      arrayOfMediaSet[j].addContentListener(this);
    this.mName = paramGalleryApp.getResources().getString(2131362293);
  }

  public String getName()
  {
    return this.mName;
  }

  public MediaSet getSubMediaSet(int paramInt)
  {
    for (MediaSet localMediaSet : this.mSets)
    {
      int k = localMediaSet.getSubMediaSetCount();
      if (paramInt < k)
        return localMediaSet.getSubMediaSet(paramInt);
      paramInt -= k;
    }
    return null;
  }

  public int getSubMediaSetCount()
  {
    int i = 0;
    MediaSet[] arrayOfMediaSet = this.mSets;
    int j = arrayOfMediaSet.length;
    for (int k = 0; k < j; ++k)
      i += arrayOfMediaSet[k].getSubMediaSetCount();
    return i;
  }

  public boolean isLoading()
  {
    int i = 0;
    int j = this.mSets.length;
    while (i < j)
    {
      if (this.mSets[i].isLoading())
        return true;
      ++i;
    }
    return false;
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
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.ComboAlbumSet
 * JD-Core Version:    0.5.4
 */