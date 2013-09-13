package com.google.android.picasastore;

import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;

public class MetricsUtils
{
  private static final long LOG_DURATION_LIMIT;
  static Metrics sFreeMetrics = null;
  private static final ThreadLocal<ArrayList<Metrics>> sMetricsStack;

  static
  {
    LOG_DURATION_LIMIT = SystemProperties.getLong("picasasync.metrics.time", 100L);
    sMetricsStack = new ThreadLocal()
    {
      protected ArrayList<MetricsUtils.Metrics> initialValue()
      {
        return new ArrayList(8);
      }
    };
  }

  public static int begin(String paramString)
  {
    ArrayList localArrayList = (ArrayList)sMetricsStack.get();
    localArrayList.add(Metrics.obtain(paramString));
    return localArrayList.size();
  }

  public static void end(int paramInt)
  {
    endWithReport(paramInt, null);
  }

  public static void endWithReport(int paramInt, String paramString)
  {
    ArrayList localArrayList = (ArrayList)sMetricsStack.get();
    if ((paramInt > localArrayList.size()) || (paramInt <= 0))
    {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = Integer.valueOf(localArrayList.size());
      arrayOfObject[1] = Integer.valueOf(paramInt);
      throw new IllegalArgumentException(String.format("size: %s, id: %s", arrayOfObject));
    }
    while (paramInt < localArrayList.size())
    {
      Metrics localMetrics2 = (Metrics)localArrayList.remove(-1 + localArrayList.size());
      Log.w("MetricsUtils", "WARNING: unclosed metrics: " + localMetrics2.toString());
      if (!localArrayList.isEmpty())
        ((Metrics)localArrayList.get(-1 + localArrayList.size())).merge(localMetrics2);
      localMetrics2.recycle();
    }
    Metrics localMetrics1 = (Metrics)localArrayList.remove(-1 + localArrayList.size());
    localMetrics1.endTimestamp = SystemClock.elapsedRealtime();
    if ((Log.isLoggable("MetricsUtils", 3)) && (LOG_DURATION_LIMIT >= 0L) && (localMetrics1.endTimestamp - localMetrics1.startTimestamp >= LOG_DURATION_LIMIT))
      Log.d("MetricsUtils", localMetrics1.toString(paramString));
    if (!localArrayList.isEmpty())
      ((Metrics)localArrayList.get(-1 + localArrayList.size())).merge(localMetrics1);
    if ((paramString != null) && (localMetrics1.networkOpCount > 0))
      PicasaStoreFacade.broadcastOperationReport(paramString, localMetrics1.endTimestamp - localMetrics1.startTimestamp, localMetrics1.networkOpDuration, localMetrics1.networkOpCount, localMetrics1.outBytes, localMetrics1.inBytes);
    localMetrics1.recycle();
  }

  public static void incrementInBytes(long paramLong)
  {
    ArrayList localArrayList = (ArrayList)sMetricsStack.get();
    int i = localArrayList.size();
    if (i <= 0)
      return;
    Metrics localMetrics = (Metrics)localArrayList.get(i - 1);
    localMetrics.inBytes = (paramLong + localMetrics.inBytes);
  }

  public static void incrementNetworkOpCount(long paramLong)
  {
    ArrayList localArrayList = (ArrayList)sMetricsStack.get();
    int i = localArrayList.size();
    if (i <= 0)
      return;
    Metrics localMetrics = (Metrics)localArrayList.get(i - 1);
    localMetrics.networkOpCount = (int)(paramLong + localMetrics.networkOpCount);
  }

  public static void incrementNetworkOpDuration(long paramLong)
  {
    ArrayList localArrayList = (ArrayList)sMetricsStack.get();
    int i = localArrayList.size();
    if (i <= 0)
      return;
    Metrics localMetrics = (Metrics)localArrayList.get(i - 1);
    localMetrics.networkOpDuration = (paramLong + localMetrics.networkOpDuration);
  }

  public static void incrementNetworkOpDurationAndCount(long paramLong)
  {
    ArrayList localArrayList = (ArrayList)sMetricsStack.get();
    int i = localArrayList.size();
    if (i <= 0)
      return;
    Metrics localMetrics = (Metrics)localArrayList.get(i - 1);
    localMetrics.networkOpDuration = (paramLong + localMetrics.networkOpDuration);
    localMetrics.networkOpCount = (1 + localMetrics.networkOpCount);
  }

  public static void incrementOutBytes(long paramLong)
  {
    ArrayList localArrayList = (ArrayList)sMetricsStack.get();
    int i = localArrayList.size();
    if (i <= 0)
      return;
    Metrics localMetrics = (Metrics)localArrayList.get(i - 1);
    localMetrics.outBytes = (paramLong + localMetrics.outBytes);
  }

  public static void incrementQueryResultCount(int paramInt)
  {
    ArrayList localArrayList = (ArrayList)sMetricsStack.get();
    int i = localArrayList.size();
    if (i <= 0)
      return;
    Metrics localMetrics = (Metrics)localArrayList.get(i - 1);
    localMetrics.queryResultCount = (paramInt + localMetrics.queryResultCount);
  }

  private static class Metrics
  {
    public long endTimestamp;
    public long inBytes;
    public String name;
    public int networkOpCount;
    public long networkOpDuration;
    public Metrics nextFree;
    public long outBytes;
    public int queryResultCount;
    public long startTimestamp;
    public int updateCount;

    public static Metrics obtain(String paramString)
    {
      monitorenter;
      Metrics localMetrics;
      try
      {
        localMetrics = MetricsUtils.sFreeMetrics;
        if (localMetrics == null)
        {
          localMetrics = new Metrics();
          localMetrics.name = paramString;
          localMetrics.startTimestamp = SystemClock.elapsedRealtime();
          return localMetrics;
        }
      }
      finally
      {
        monitorexit;
      }
    }

    public static void recycle(Metrics paramMetrics)
    {
      monitorenter;
      try
      {
        paramMetrics.nextFree = MetricsUtils.sFreeMetrics;
        MetricsUtils.sFreeMetrics = paramMetrics;
        monitorexit;
        return;
      }
      finally
      {
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }

    public void merge(Metrics paramMetrics)
    {
      this.queryResultCount += paramMetrics.queryResultCount;
      this.updateCount += paramMetrics.updateCount;
      this.inBytes += paramMetrics.inBytes;
      this.outBytes += paramMetrics.outBytes;
      this.networkOpDuration += paramMetrics.networkOpDuration;
      this.networkOpCount += paramMetrics.networkOpCount;
    }

    public void recycle()
    {
      this.name = null;
      this.queryResultCount = 0;
      this.updateCount = 0;
      this.inBytes = 0L;
      this.outBytes = 0L;
      this.networkOpDuration = 0L;
      this.networkOpCount = 0;
      recycle(this);
    }

    public String toString(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[").append(this.name);
      if (this.queryResultCount != 0)
        localStringBuilder.append(" query-result:").append(this.queryResultCount);
      if (this.updateCount != 0)
        localStringBuilder.append(" update:").append(this.updateCount);
      if (this.inBytes != 0L)
        localStringBuilder.append(" in:").append(this.inBytes);
      if (this.outBytes != 0L)
        localStringBuilder.append(" out:").append(this.outBytes);
      if (this.networkOpDuration > 0L)
        localStringBuilder.append(" net-time:").append(this.networkOpDuration);
      if (this.networkOpCount > 1)
        localStringBuilder.append(" net-op:").append(this.networkOpCount);
      long l = this.endTimestamp - this.startTimestamp;
      if (l > 0L)
        localStringBuilder.append(" time:").append(l);
      if (paramString != null)
        localStringBuilder.append(" report:" + paramString);
      return ']';
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasastore.MetricsUtils
 * JD-Core Version:    0.5.4
 */