package com.android.gallery3d.data;

import android.content.Context;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.GalleryUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TimeClustering extends Clustering
{
  private static int CLUSTER_SPLIT_MULTIPLIER = 3;
  private static final Comparator<SmallItem> sDateComparator = new DateComparator(null);
  private long mClusterSplitTime = 3630000L;
  private ArrayList<Cluster> mClusters;
  private Context mContext;
  private Cluster mCurrCluster;
  private long mLargeClusterSplitTime = this.mClusterSplitTime / 2L;
  private int mMaxClusterSize = 35;
  private int mMinClusterSize = 11;
  private String[] mNames;

  public TimeClustering(Context paramContext)
  {
    this.mContext = paramContext;
    this.mClusters = new ArrayList();
    this.mCurrCluster = new Cluster();
  }

  private void compute(SmallItem paramSmallItem)
  {
    int k;
    int l;
    int i1;
    if (paramSmallItem != null)
    {
      k = this.mClusters.size();
      l = this.mCurrCluster.size();
      i1 = 0;
      if (l == 0)
        this.mCurrCluster.addItem(paramSmallItem);
    }
    do
    {
      return;
      SmallItem localSmallItem = this.mCurrCluster.getLastItem();
      int i2;
      if (isGeographicallySeparated(localSmallItem, paramSmallItem))
      {
        this.mClusters.add(this.mCurrCluster);
        i2 = 1;
      }
      while (true)
      {
        if (i1 == 0);
        this.mCurrCluster = new Cluster();
        if (i2 != 0)
          this.mCurrCluster.mGeographicallySeparatedFromPrevCluster = true;
        this.mCurrCluster.addItem(paramSmallItem);
        return;
        if (l > this.mMaxClusterSize)
        {
          splitAndAddCurrentCluster();
          i2 = 0;
          i1 = 0;
        }
        if (timeDistance(localSmallItem, paramSmallItem) < this.mClusterSplitTime)
        {
          this.mCurrCluster.addItem(paramSmallItem);
          i1 = 1;
          i2 = 0;
        }
        if ((k > 0) && (l < this.mMinClusterSize) && (!this.mCurrCluster.mGeographicallySeparatedFromPrevCluster))
        {
          mergeAndAddCurrentCluster();
          i2 = 0;
          i1 = 0;
        }
        this.mClusters.add(this.mCurrCluster);
        i2 = 0;
        i1 = 0;
      }
    }
    while (this.mCurrCluster.size() <= 0);
    int i = this.mClusters.size();
    int j = this.mCurrCluster.size();
    if (j > this.mMaxClusterSize)
      splitAndAddCurrentCluster();
    while (true)
    {
      this.mCurrCluster = new Cluster();
      return;
      if ((i > 0) && (j < this.mMinClusterSize) && (!this.mCurrCluster.mGeographicallySeparatedFromPrevCluster))
        mergeAndAddCurrentCluster();
      this.mClusters.add(this.mCurrCluster);
    }
  }

  private int getPartitionIndexForCurrentCluster()
  {
    int i = -1;
    float f1 = 2.0F;
    ArrayList localArrayList = this.mCurrCluster.getItems();
    int j = this.mCurrCluster.size();
    int k = this.mMinClusterSize;
    if (j > k + 1)
    {
      int l = k;
      if (l < j - k)
      {
        label40: SmallItem localSmallItem1 = (SmallItem)localArrayList.get(l - 1);
        SmallItem localSmallItem2 = (SmallItem)localArrayList.get(l);
        SmallItem localSmallItem3 = (SmallItem)localArrayList.get(l + 1);
        long l1 = localSmallItem3.dateInMs;
        long l2 = localSmallItem2.dateInMs;
        long l3 = localSmallItem1.dateInMs;
        if ((l1 == 0L) || (l2 == 0L) || (l3 == 0L));
        while (true)
        {
          ++l;
          break label40:
          long l4 = Math.abs(l1 - l2);
          long l5 = Math.abs(l2 - l3);
          float f2 = Math.max((float)l4 / (0.01F + (float)l5), (float)l5 / (0.01F + (float)l4));
          if (f2 <= f1)
            continue;
          if (timeDistance(localSmallItem2, localSmallItem1) > this.mLargeClusterSplitTime)
          {
            i = l;
            f1 = f2;
          }
          if (timeDistance(localSmallItem3, localSmallItem2) <= this.mLargeClusterSplitTime)
            continue;
          i = l + 1;
          f1 = f2;
        }
      }
    }
    return i;
  }

  private static boolean isGeographicallySeparated(SmallItem paramSmallItem1, SmallItem paramSmallItem2)
  {
    if ((!GalleryUtils.isValidLocation(paramSmallItem1.lat, paramSmallItem1.lng)) || (!GalleryUtils.isValidLocation(paramSmallItem2.lat, paramSmallItem2.lng)))
      return false;
    return GalleryUtils.toMile(GalleryUtils.fastDistanceMeters(Math.toRadians(paramSmallItem1.lat), Math.toRadians(paramSmallItem1.lng), Math.toRadians(paramSmallItem2.lat), Math.toRadians(paramSmallItem2.lng))) > 20.0D;
  }

  private void mergeAndAddCurrentCluster()
  {
    int i = this.mClusters.size();
    Cluster localCluster = (Cluster)this.mClusters.get(i - 1);
    ArrayList localArrayList = this.mCurrCluster.getItems();
    int j = this.mCurrCluster.size();
    if (localCluster.size() < this.mMinClusterSize)
    {
      for (int k = 0; k < j; ++k)
        localCluster.addItem((SmallItem)localArrayList.get(k));
      this.mClusters.set(i - 1, localCluster);
      return;
    }
    this.mClusters.add(this.mCurrCluster);
  }

  private void setTimeRange(long paramLong, int paramInt)
  {
    if (paramInt != 0)
    {
      int i = paramInt / 9;
      this.mMinClusterSize = (i / 2);
      this.mMaxClusterSize = (i * 2);
      this.mClusterSplitTime = (paramLong / paramInt * CLUSTER_SPLIT_MULTIPLIER);
    }
    this.mClusterSplitTime = Utils.clamp(this.mClusterSplitTime, 60000L, 7200000L);
    this.mLargeClusterSplitTime = (this.mClusterSplitTime / 2L);
    this.mMinClusterSize = Utils.clamp(this.mMinClusterSize, 8, 15);
    this.mMaxClusterSize = Utils.clamp(this.mMaxClusterSize, 20, 50);
  }

  private void splitAndAddCurrentCluster()
  {
    ArrayList localArrayList = this.mCurrCluster.getItems();
    int i = this.mCurrCluster.size();
    int j = getPartitionIndexForCurrentCluster();
    if (j != -1)
    {
      Cluster localCluster1 = new Cluster();
      for (int k = 0; k < j; ++k)
        localCluster1.addItem((SmallItem)localArrayList.get(k));
      this.mClusters.add(localCluster1);
      Cluster localCluster2 = new Cluster();
      for (int l = j; l < i; ++l)
        localCluster2.addItem((SmallItem)localArrayList.get(l));
      this.mClusters.add(localCluster2);
      return;
    }
    this.mClusters.add(this.mCurrCluster);
  }

  private static long timeDistance(SmallItem paramSmallItem1, SmallItem paramSmallItem2)
  {
    return Math.abs(paramSmallItem1.dateInMs - paramSmallItem2.dateInMs);
  }

  public ArrayList<Path> getCluster(int paramInt)
  {
    ArrayList localArrayList1 = ((Cluster)this.mClusters.get(paramInt)).getItems();
    ArrayList localArrayList2 = new ArrayList(localArrayList1.size());
    int i = 0;
    int j = localArrayList1.size();
    while (i < j)
    {
      localArrayList2.add(((SmallItem)localArrayList1.get(i)).path);
      ++i;
    }
    return localArrayList2;
  }

  public String getClusterName(int paramInt)
  {
    return this.mNames[paramInt];
  }

  public int getNumberOfClusters()
  {
    return this.mClusters.size();
  }

  public void run(MediaSet paramMediaSet)
  {
    int i = paramMediaSet.getTotalMediaItemCount();
    SmallItem[] arrayOfSmallItem = new SmallItem[i];
    paramMediaSet.enumerateTotalMediaItems(new MediaSet.ItemConsumer(i, new double[2], arrayOfSmallItem)
    {
      public void consume(int paramInt, MediaItem paramMediaItem)
      {
        if ((paramInt < 0) || (paramInt >= this.val$total))
          return;
        SmallItem localSmallItem = new SmallItem();
        localSmallItem.path = paramMediaItem.getPath();
        localSmallItem.dateInMs = paramMediaItem.getDateInMs();
        paramMediaItem.getLatLong(this.val$latLng);
        localSmallItem.lat = this.val$latLng[0];
        localSmallItem.lng = this.val$latLng[1];
        this.val$buf[paramInt] = localSmallItem;
      }
    });
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; ++j)
    {
      if (arrayOfSmallItem[j] == null)
        continue;
      localArrayList.add(arrayOfSmallItem[j]);
    }
    Collections.sort(localArrayList, sDateComparator);
    int k = localArrayList.size();
    long l1 = 0L;
    long l2 = 0L;
    int l = 0;
    if (l < k)
    {
      label93: long l3 = ((SmallItem)localArrayList.get(l)).dateInMs;
      if (l3 == 0L);
      while (true)
      {
        ++l;
        break label93:
        if (l1 == 0L)
        {
          l2 = l3;
          l1 = l3;
        }
        l1 = Math.min(l1, l3);
        l2 = Math.max(l2, l3);
      }
    }
    setTimeRange(l2 - l1, k);
    for (int i1 = 0; i1 < k; ++i1)
      compute((SmallItem)localArrayList.get(i1));
    compute(null);
    int i2 = this.mClusters.size();
    this.mNames = new String[i2];
    for (int i3 = 0; i3 < i2; ++i3)
      this.mNames[i3] = ((Cluster)this.mClusters.get(i3)).generateCaption(this.mContext);
  }

  private static class DateComparator
    implements Comparator<SmallItem>
  {
    public int compare(SmallItem paramSmallItem1, SmallItem paramSmallItem2)
    {
      return -Utils.compare(paramSmallItem1.dateInMs, paramSmallItem2.dateInMs);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.TimeClustering
 * JD-Core Version:    0.5.4
 */