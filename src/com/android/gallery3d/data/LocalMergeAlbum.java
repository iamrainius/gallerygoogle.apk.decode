package com.android.gallery3d.data;

import I;
import android.net.Uri;
import android.net.Uri.Builder;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images.Media;
import com.android.gallery3d.common.ApiHelper;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

public class LocalMergeAlbum extends MediaSet
  implements ContentListener
{
  private int mBucketId;
  private final Comparator<MediaItem> mComparator;
  private FetchCache[] mFetcher;
  private TreeMap<Integer, int[]> mIndex = new TreeMap();
  private String mName;
  private final MediaSet[] mSources;
  private int mSupportedOperation;

  public LocalMergeAlbum(Path paramPath, Comparator<MediaItem> paramComparator, MediaSet[] paramArrayOfMediaSet, int paramInt)
  {
    super(paramPath, -1L);
    this.mComparator = paramComparator;
    this.mSources = paramArrayOfMediaSet;
    if (paramArrayOfMediaSet.length == 0);
    for (String str = ""; ; str = paramArrayOfMediaSet[0].getName())
    {
      this.mName = str;
      this.mBucketId = paramInt;
      MediaSet[] arrayOfMediaSet = this.mSources;
      int i = arrayOfMediaSet.length;
      for (int j = 0; ; ++j)
      {
        if (j >= i)
          break label97;
        arrayOfMediaSet[j].addContentListener(this);
      }
    }
    label97: reload();
  }

  private void invalidateCache()
  {
    int i = 0;
    int j = this.mSources.length;
    while (i < j)
    {
      this.mFetcher[i].invalidate();
      ++i;
    }
    this.mIndex.clear();
    this.mIndex.put(Integer.valueOf(0), new int[this.mSources.length]);
  }

  private void updateData()
  {
    new ArrayList();
    if (this.mSources.length == 0);
    for (int i = 0; ; i = -1)
    {
      this.mFetcher = new FetchCache[this.mSources.length];
      int j = 0;
      int k = this.mSources.length;
      while (true)
      {
        if (j >= k)
          break label87;
        this.mFetcher[j] = new FetchCache(this.mSources[j]);
        i &= this.mSources[j].getSupportedOperations();
        ++j;
      }
    }
    label87: this.mSupportedOperation = i;
    this.mIndex.clear();
    this.mIndex.put(Integer.valueOf(0), new int[this.mSources.length]);
    if (this.mSources.length == 0);
    for (String str = ""; ; str = this.mSources[0].getName())
    {
      this.mName = str;
      return;
    }
  }

  public void delete()
  {
    MediaSet[] arrayOfMediaSet = this.mSources;
    int i = arrayOfMediaSet.length;
    for (int j = 0; j < i; ++j)
      arrayOfMediaSet[j].delete();
  }

  public Uri getContentUri()
  {
    String str = String.valueOf(this.mBucketId);
    if (ApiHelper.HAS_MEDIA_PROVIDER_FILES_TABLE)
      return MediaStore.Files.getContentUri("external").buildUpon().appendQueryParameter("bucketId", str).build();
    return MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendQueryParameter("bucketId", str).build();
  }

  public ArrayList<MediaItem> getMediaItem(int paramInt1, int paramInt2)
  {
    SortedMap localSortedMap = this.mIndex.headMap(Integer.valueOf(paramInt1 + 1));
    int i = ((Integer)localSortedMap.lastKey()).intValue();
    int[] arrayOfInt = (int[])((int[])localSortedMap.get(Integer.valueOf(i))).clone();
    MediaItem[] arrayOfMediaItem = new MediaItem[this.mSources.length];
    int j = this.mSources.length;
    for (int k = 0; k < j; ++k)
      arrayOfMediaItem[k] = this.mFetcher[k].getItem(arrayOfInt[k]);
    ArrayList localArrayList = new ArrayList();
    for (int l = i; ; ++l)
    {
      int i1;
      if (l < paramInt1 + paramInt2)
      {
        i1 = -1;
        for (int i2 = 0; i2 < j; ++i2)
        {
          if ((arrayOfMediaItem[i2] == null) || ((i1 != -1) && (this.mComparator.compare(arrayOfMediaItem[i2], arrayOfMediaItem[i1]) >= 0)))
            continue;
          i1 = i2;
        }
        if (i1 != -1)
          break label192;
      }
      return localArrayList;
      label192: arrayOfInt[i1] = (1 + arrayOfInt[i1]);
      if (l >= paramInt1)
        localArrayList.add(arrayOfMediaItem[i1]);
      arrayOfMediaItem[i1] = this.mFetcher[i1].getItem(arrayOfInt[i1]);
      if ((l + 1) % 64 != 0)
        continue;
      this.mIndex.put(Integer.valueOf(l + 1), arrayOfInt.clone());
    }
  }

  public int getMediaItemCount()
  {
    return getTotalMediaItemCount();
  }

  public String getName()
  {
    return this.mName;
  }

  public int getSupportedOperations()
  {
    return this.mSupportedOperation;
  }

  public int getTotalMediaItemCount()
  {
    int i = 0;
    MediaSet[] arrayOfMediaSet = this.mSources;
    int j = arrayOfMediaSet.length;
    for (int k = 0; k < j; ++k)
      i += arrayOfMediaSet[k].getTotalMediaItemCount();
    return i;
  }

  public boolean isCameraRoll()
  {
    if (this.mSources.length == 0)
      return false;
    MediaSet[] arrayOfMediaSet = this.mSources;
    int i = arrayOfMediaSet.length;
    for (int j = 0; j < i; ++j)
      if (!arrayOfMediaSet[j].isCameraRoll());
    return true;
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
    int k = this.mSources.length;
    while (j < k)
    {
      if (this.mSources[j].reload() > this.mDataVersion)
        i = 1;
      ++j;
    }
    if (i != 0)
    {
      this.mDataVersion = nextVersionNumber();
      updateData();
      invalidateCache();
    }
    return this.mDataVersion;
  }

  public void rotate(int paramInt)
  {
    MediaSet[] arrayOfMediaSet = this.mSources;
    int i = arrayOfMediaSet.length;
    for (int j = 0; j < i; ++j)
      arrayOfMediaSet[j].rotate(paramInt);
  }

  private static class FetchCache
  {
    private MediaSet mBaseSet;
    private SoftReference<ArrayList<MediaItem>> mCacheRef;
    private int mStartPos;

    public FetchCache(MediaSet paramMediaSet)
    {
      this.mBaseSet = paramMediaSet;
    }

    public MediaItem getItem(int paramInt)
    {
      ArrayList localArrayList = null;
      if ((this.mCacheRef == null) || (paramInt < this.mStartPos) || (paramInt >= 64 + this.mStartPos));
      for (int i = 1; ; i = 1)
        do
        {
          if (i != 0)
          {
            localArrayList = this.mBaseSet.getMediaItem(paramInt, 64);
            this.mCacheRef = new SoftReference(localArrayList);
            this.mStartPos = paramInt;
          }
          if ((paramInt >= this.mStartPos) && (paramInt < this.mStartPos + localArrayList.size()))
            break label107;
          return null;
          localArrayList = (ArrayList)this.mCacheRef.get();
          i = 0;
        }
        while (localArrayList != null);
      label107: return (MediaItem)localArrayList.get(paramInt - this.mStartPos);
    }

    public void invalidate()
    {
      this.mCacheRef = null;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.LocalMergeAlbum
 * JD-Core Version:    0.5.4
 */