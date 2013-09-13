package android.support.v4.net;

import android.os.Build.VERSION;

public class TrafficStatsCompat
{
  private static final TrafficStatsCompatImpl IMPL;

  static
  {
    if (Build.VERSION.SDK_INT >= 14)
    {
      IMPL = new IcsTrafficStatsCompatImpl();
      return;
    }
    IMPL = new BaseTrafficStatsCompatImpl();
  }

  public static void clearThreadStatsTag()
  {
    IMPL.clearThreadStatsTag();
  }

  public static void setThreadStatsTag(int paramInt)
  {
    IMPL.setThreadStatsTag(paramInt);
  }

  static class BaseTrafficStatsCompatImpl
    implements TrafficStatsCompat.TrafficStatsCompatImpl
  {
    private ThreadLocal<SocketTags> mThreadSocketTags = new ThreadLocal()
    {
      protected TrafficStatsCompat.BaseTrafficStatsCompatImpl.SocketTags initialValue()
      {
        return new TrafficStatsCompat.BaseTrafficStatsCompatImpl.SocketTags(null);
      }
    };

    public void clearThreadStatsTag()
    {
      ((SocketTags)this.mThreadSocketTags.get()).statsTag = -1;
    }

    public void setThreadStatsTag(int paramInt)
    {
      ((SocketTags)this.mThreadSocketTags.get()).statsTag = paramInt;
    }

    private static class SocketTags
    {
      public int statsTag = -1;
    }
  }

  static class IcsTrafficStatsCompatImpl
    implements TrafficStatsCompat.TrafficStatsCompatImpl
  {
    public void clearThreadStatsTag()
    {
      TrafficStatsCompatIcs.clearThreadStatsTag();
    }

    public void setThreadStatsTag(int paramInt)
    {
      TrafficStatsCompatIcs.setThreadStatsTag(paramInt);
    }
  }

  static abstract interface TrafficStatsCompatImpl
  {
    public abstract void clearThreadStatsTag();

    public abstract void setThreadStatsTag(int paramInt);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     android.support.v4.net.TrafficStatsCompat
 * JD-Core Version:    0.5.4
 */