package com.android.gallery3d.data;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.util.FloatMath;
import android.widget.Toast;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.ReverseGeocoder;
import com.android.gallery3d.util.ReverseGeocoder.SetLatLong;
import java.util.ArrayList;
import java.util.Iterator;

class LocationClustering extends Clustering
{
  private ArrayList<ArrayList<SmallItem>> mClusters;
  private Context mContext;
  private Handler mHandler;
  private ArrayList<String> mNames;
  private String mNoLocationString;

  public LocationClustering(Context paramContext)
  {
    this.mContext = paramContext;
    this.mNoLocationString = this.mContext.getResources().getString(2131362243);
    this.mHandler = new Handler(Looper.getMainLooper());
  }

  private static String generateName(ArrayList<SmallItem> paramArrayList, ReverseGeocoder paramReverseGeocoder)
  {
    ReverseGeocoder.SetLatLong localSetLatLong = new ReverseGeocoder.SetLatLong();
    int i = paramArrayList.size();
    for (int j = 0; j < i; ++j)
    {
      SmallItem localSmallItem = (SmallItem)paramArrayList.get(j);
      double d1 = localSmallItem.lat;
      double d2 = localSmallItem.lng;
      if (localSetLatLong.mMinLatLatitude > d1)
      {
        localSetLatLong.mMinLatLatitude = d1;
        localSetLatLong.mMinLatLongitude = d2;
      }
      if (localSetLatLong.mMaxLatLatitude < d1)
      {
        localSetLatLong.mMaxLatLatitude = d1;
        localSetLatLong.mMaxLatLongitude = d2;
      }
      if (localSetLatLong.mMinLonLongitude > d2)
      {
        localSetLatLong.mMinLonLatitude = d1;
        localSetLatLong.mMinLonLongitude = d2;
      }
      if (localSetLatLong.mMaxLonLongitude >= d2)
        continue;
      localSetLatLong.mMaxLonLatitude = d1;
      localSetLatLong.mMaxLonLongitude = d2;
    }
    return paramReverseGeocoder.computeAddress(localSetLatLong);
  }

  private static int[] kMeans(Point[] paramArrayOfPoint, int[] paramArrayOfInt)
  {
    int i = paramArrayOfPoint.length;
    int j = Math.min(i, 1);
    int k = Math.min(i, 20);
    Point[] arrayOfPoint1 = new Point[k];
    Point[] arrayOfPoint2 = new Point[k];
    int[] arrayOfInt1 = new int[k];
    int[] arrayOfInt2 = new int[i];
    for (int l = 0; l < k; ++l)
    {
      arrayOfPoint1[l] = new Point();
      arrayOfPoint2[l] = new Point();
    }
    float f1 = 3.4028235E+38F;
    int[] arrayOfInt3 = new int[i];
    paramArrayOfInt[0] = 1;
    float f2 = 0.0F;
    float f3 = 0.0F;
    int i1 = j;
    label104: int i4;
    label171: int[] arrayOfInt4;
    int i5;
    int i6;
    label501: int i8;
    if (i1 <= k)
    {
      int i2 = i / i1;
      for (int i3 = 0; i3 < i1; ++i3)
      {
        Point localPoint4 = paramArrayOfPoint[(i3 * i2)];
        arrayOfPoint1[i3].latRad = localPoint4.latRad;
        arrayOfPoint1[i3].lngRad = localPoint4.lngRad;
      }
      i4 = 0;
      if (i4 < 30)
      {
        for (int i9 = 0; i9 < i1; ++i9)
        {
          arrayOfPoint2[i9].latRad = 0.0D;
          arrayOfPoint2[i9].lngRad = 0.0D;
          arrayOfInt1[i9] = 0;
        }
        f3 = 0.0F;
        for (int i10 = 0; i10 < i; ++i10)
        {
          Point localPoint1 = paramArrayOfPoint[i10];
          float f5 = 3.4028235E+38F;
          int i12 = 0;
          for (int i13 = 0; i13 < i1; ++i13)
          {
            float f6 = (float)GalleryUtils.fastDistanceMeters(localPoint1.latRad, localPoint1.lngRad, arrayOfPoint1[i13].latRad, arrayOfPoint1[i13].lngRad);
            if (f6 < 1.0F)
              f6 = 0.0F;
            if (f6 >= f5)
              continue;
            f5 = f6;
            i12 = i13;
          }
          arrayOfInt2[i10] = i12;
          arrayOfInt1[i12] = (1 + arrayOfInt1[i12]);
          Point localPoint2 = arrayOfPoint2[i12];
          localPoint2.latRad += localPoint1.latRad;
          Point localPoint3 = arrayOfPoint2[i12];
          localPoint3.lngRad += localPoint1.lngRad;
          f3 += f5;
        }
        for (int i11 = 0; i11 < i1; ++i11)
        {
          if (arrayOfInt1[i11] <= 0)
            continue;
          arrayOfPoint1[i11].latRad = (arrayOfPoint2[i11].latRad / arrayOfInt1[i11]);
          arrayOfPoint1[i11].lngRad = (arrayOfPoint2[i11].lngRad / arrayOfInt1[i11]);
        }
        if ((f3 != 0.0F) && (Math.abs(f2 - f3) / f3 >= 0.01F))
          break label539;
      }
      arrayOfInt4 = new int[i1];
      i5 = 0;
      i6 = 0;
      if (i5 < i1)
      {
        if (arrayOfInt1[i5] <= 0)
          break label621;
        i8 = i6 + 1;
        arrayOfInt4[i5] = i6;
      }
    }
    while (true)
    {
      ++i5;
      i6 = i8;
      break label501:
      label539: f2 = f3;
      ++i4;
      break label171:
      float f4 = f3 * FloatMath.sqrt(i6);
      if (f4 < f1)
      {
        f1 = f4;
        paramArrayOfInt[0] = i6;
        for (int i7 = 0; i7 < i; ++i7)
          arrayOfInt3[i7] = arrayOfInt4[arrayOfInt2[i7]];
        if (f4 == 0.0F)
          return arrayOfInt3;
      }
      ++i1;
      break label104:
      label621: i8 = i6;
    }
  }

  public ArrayList<Path> getCluster(int paramInt)
  {
    ArrayList localArrayList1 = (ArrayList)this.mClusters.get(paramInt);
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
    return (String)this.mNames.get(paramInt);
  }

  public int getNumberOfClusters()
  {
    return this.mClusters.size();
  }

  public void run(MediaSet paramMediaSet)
  {
    int i = paramMediaSet.getTotalMediaItemCount();
    SmallItem[] arrayOfSmallItem = new SmallItem[i];
    double[] arrayOfDouble = new double[2];
    1 local1 = new MediaSet.ItemConsumer(i, arrayOfDouble, arrayOfSmallItem)
    {
      public void consume(int paramInt, MediaItem paramMediaItem)
      {
        if ((paramInt < 0) || (paramInt >= this.val$total))
          return;
        LocationClustering.SmallItem localSmallItem = new LocationClustering.SmallItem(null);
        localSmallItem.path = paramMediaItem.getPath();
        paramMediaItem.getLatLong(this.val$latLong);
        localSmallItem.lat = this.val$latLong[0];
        localSmallItem.lng = this.val$latLong[1];
        this.val$buf[paramInt] = localSmallItem;
      }
    };
    paramMediaSet.enumerateTotalMediaItems(local1);
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    int j = 0;
    if (j < i)
    {
      label65: SmallItem localSmallItem = arrayOfSmallItem[j];
      if (localSmallItem == null);
      while (true)
      {
        ++j;
        break label65:
        if (GalleryUtils.isValidLocation(localSmallItem.lat, localSmallItem.lng))
        {
          localArrayList1.add(localSmallItem);
          localArrayList3.add(new Point(localSmallItem.lat, localSmallItem.lng));
        }
        localArrayList2.add(localSmallItem);
      }
    }
    ArrayList localArrayList4 = new ArrayList();
    int k = localArrayList1.size();
    if (k > 0)
    {
      Point[] arrayOfPoint = (Point[])localArrayList3.toArray(new Point[k]);
      int[] arrayOfInt1 = new int[1];
      int[] arrayOfInt2 = kMeans(arrayOfPoint, arrayOfInt1);
      for (int i1 = 0; i1 < arrayOfInt1[0]; ++i1)
        localArrayList4.add(new ArrayList());
      for (int i2 = 0; i2 < k; ++i2)
        ((ArrayList)localArrayList4.get(arrayOfInt2[i2])).add(localArrayList1.get(i2));
    }
    ReverseGeocoder localReverseGeocoder = new ReverseGeocoder(this.mContext);
    this.mNames = new ArrayList();
    int l = 0;
    this.mClusters = new ArrayList();
    Iterator localIterator = localArrayList4.iterator();
    while (localIterator.hasNext())
    {
      ArrayList localArrayList5 = (ArrayList)localIterator.next();
      String str = generateName(localArrayList5, localReverseGeocoder);
      if (str != null)
      {
        this.mNames.add(str);
        this.mClusters.add(localArrayList5);
      }
      localArrayList2.addAll(localArrayList5);
      l = 1;
    }
    if (localArrayList2.size() > 0)
    {
      this.mNames.add(this.mNoLocationString);
      this.mClusters.add(localArrayList2);
    }
    if (l == 0)
      return;
    Handler localHandler = this.mHandler;
    2 local2 = new Runnable()
    {
      public void run()
      {
        Toast.makeText(LocationClustering.this.mContext, 2131362244, 1).show();
      }
    };
    localHandler.post(local2);
  }

  private static class Point
  {
    public double latRad;
    public double lngRad;

    public Point()
    {
    }

    public Point(double paramDouble1, double paramDouble2)
    {
      this.latRad = Math.toRadians(paramDouble1);
      this.lngRad = Math.toRadians(paramDouble2);
    }
  }

  private static class SmallItem
  {
    double lat;
    double lng;
    Path path;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.LocationClustering
 * JD-Core Version:    0.5.4
 */