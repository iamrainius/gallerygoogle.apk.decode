package com.android.gallery3d.ui;

import android.os.SystemClock;

public class AnimationTime
{
  private static volatile long sTime;

  public static long get()
  {
    return sTime;
  }

  public static long startTime()
  {
    sTime = SystemClock.uptimeMillis();
    return sTime;
  }

  public static void update()
  {
    sTime = SystemClock.uptimeMillis();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.AnimationTime
 * JD-Core Version:    0.5.4
 */