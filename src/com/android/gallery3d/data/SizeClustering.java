package com.android.gallery3d.data;

import android.content.Context;
import android.content.res.Resources;
import java.util.ArrayList;

public class SizeClustering extends Clustering
{
  private static final long[] SIZE_LEVELS = { 0L, 1048576L, 10485760L, 104857600L, 1073741824L, 2147483648L, 4294967296L };
  private ArrayList<Path>[] mClusters;
  private Context mContext;
  private long[] mMinSizes;
  private String[] mNames;

  public SizeClustering(Context paramContext)
  {
    this.mContext = paramContext;
  }

  private String getSizeString(int paramInt)
  {
    long l = SIZE_LEVELS[paramInt];
    if (l >= 1073741824L)
      return l / 1073741824L + "GB";
    return l / 1048576L + "MB";
  }

  public ArrayList<Path> getCluster(int paramInt)
  {
    return this.mClusters[paramInt];
  }

  public String getClusterName(int paramInt)
  {
    return this.mNames[paramInt];
  }

  public long getMinSize(int paramInt)
  {
    return this.mMinSizes[paramInt];
  }

  public int getNumberOfClusters()
  {
    return this.mClusters.length;
  }

  public void run(MediaSet paramMediaSet)
  {
    ArrayList[] arrayOfArrayList = new ArrayList[SIZE_LEVELS.length];
    paramMediaSet.enumerateTotalMediaItems(new MediaSet.ItemConsumer(arrayOfArrayList)
    {
      public void consume(int paramInt, MediaItem paramMediaItem)
      {
        long l = paramMediaItem.getSize();
        for (int i = 0; ; ++i)
        {
          if ((i < -1 + SizeClustering.SIZE_LEVELS.length) && (l >= SizeClustering.SIZE_LEVELS[(i + 1)]))
            continue;
          ArrayList localArrayList = this.val$group[i];
          if (localArrayList == null)
          {
            localArrayList = new ArrayList();
            this.val$group[i] = localArrayList;
          }
          localArrayList.add(paramMediaItem.getPath());
          return;
        }
      }
    });
    int i = 0;
    for (int j = 0; j < arrayOfArrayList.length; ++j)
    {
      if (arrayOfArrayList[j] == null)
        continue;
      ++i;
    }
    this.mClusters = new ArrayList[i];
    this.mNames = new String[i];
    this.mMinSizes = new long[i];
    Resources localResources = this.mContext.getResources();
    int k = 0;
    for (int l = -1 + arrayOfArrayList.length; ; --l)
    {
      if (l < 0)
        return;
      label102: if (arrayOfArrayList[l] != null)
        break;
    }
    this.mClusters[k] = arrayOfArrayList[l];
    if (l == 0)
    {
      String[] arrayOfString2 = this.mNames;
      String str4 = localResources.getString(2131362298);
      Object[] arrayOfObject2 = new Object[1];
      arrayOfObject2[0] = getSizeString(l + 1);
      arrayOfString2[k] = String.format(str4, arrayOfObject2);
    }
    while (true)
    {
      this.mMinSizes[k] = SIZE_LEVELS[l];
      ++k;
      break label102:
      if (l == -1 + arrayOfArrayList.length)
      {
        String[] arrayOfString1 = this.mNames;
        String str3 = localResources.getString(2131362299);
        Object[] arrayOfObject1 = new Object[1];
        arrayOfObject1[0] = getSizeString(l);
        arrayOfString1[k] = String.format(str3, arrayOfObject1);
      }
      String str1 = getSizeString(l);
      String str2 = getSizeString(l + 1);
      this.mNames[k] = String.format(localResources.getString(2131362300), new Object[] { str1, str2 });
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.SizeClustering
 * JD-Core Version:    0.5.4
 */