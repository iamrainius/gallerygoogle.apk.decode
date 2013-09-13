package com.google.android.apps.lightcycle.viewer;

import android.graphics.PointF;

public class ThrowController
{
  private ThrowEvent mActiveThrowEvent = null;
  private PointF mDragVelocity = new PointF(0.0F, 0.0F);
  private long mLastDragVelocityTime = 0L;
  private PointF mLastUserInputPosition = new PointF(0.0F, 0.0F);
  private PointF mMotionLast = new PointF(0.0F, 0.0F);

  private static double easeOut(double paramDouble)
  {
    return 1.0D - Math.pow(1.0D - paramDouble, 3.0D);
  }

  private void startThrow()
  {
    monitorenter;
    try
    {
      if (Math.hypot(this.mDragVelocity.x, this.mDragVelocity.y) >= 100.0D)
      {
        PointF localPointF = new PointF(this.mLastUserInputPosition.x, this.mLastUserInputPosition.y);
        this.mActiveThrowEvent = new ThrowEvent(localPointF, new PointF(0.125F * this.mDragVelocity.x, 0.125F * this.mDragVelocity.y));
        this.mMotionLast.x = localPointF.x;
        this.mMotionLast.y = localPointF.y;
      }
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

  public boolean getThrowDelta(PointF paramPointF)
  {
    monitorenter;
    while (true)
    {
      double d;
      try
      {
        ThrowEvent localThrowEvent = this.mActiveThrowEvent;
        i = 0;
        if (localThrowEvent == null)
          return i;
        d = easeOut((System.nanoTime() - this.mActiveThrowEvent.startTime) / 500000000.0D);
        if (d < 1.0D)
          break label63;
        this.mActiveThrowEvent = null;
      }
      finally
      {
        monitorexit;
      }
      label63: float f1 = (float)(this.mActiveThrowEvent.from.x + d * this.mActiveThrowEvent.throwVector.x);
      float f2 = (float)(this.mActiveThrowEvent.from.y + d * this.mActiveThrowEvent.throwVector.y);
      paramPointF.x = (this.mMotionLast.x - f1);
      paramPointF.y = (this.mMotionLast.y - f2);
      this.mMotionLast.x = f1;
      this.mMotionLast.y = f2;
      int i = 1;
    }
  }

  public void onPointerDown(float paramFloat1, float paramFloat2, long paramLong)
  {
    this.mLastDragVelocityTime = paramLong;
    this.mLastUserInputPosition = new PointF(paramFloat1, paramFloat2);
  }

  public void onPointerMove(float paramFloat1, float paramFloat2, long paramLong)
  {
    long l = paramLong - this.mLastDragVelocityTime;
    if (l < 50L)
      return;
    this.mDragVelocity.x = ((paramFloat1 - this.mLastUserInputPosition.x) * (1000.0F / (float)l));
    this.mDragVelocity.y = ((paramFloat2 - this.mLastUserInputPosition.y) * (1000.0F / (float)l));
    this.mLastDragVelocityTime = paramLong;
    this.mLastUserInputPosition.x = paramFloat1;
    this.mLastUserInputPosition.y = paramFloat2;
  }

  public void onPointerUp(float paramFloat1, float paramFloat2, long paramLong)
  {
    if (paramLong - this.mLastDragVelocityTime < 200L)
      startThrow();
    this.mLastDragVelocityTime = 0L;
    this.mLastUserInputPosition = new PointF(0.0F, 0.0F);
    this.mDragVelocity = new PointF(0.0F, 0.0F);
  }

  public void stopThrow()
  {
    monitorenter;
    try
    {
      this.mActiveThrowEvent = null;
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

  public static class ThrowEvent
  {
    public final PointF from;
    public final long startTime;
    public final PointF throwVector;

    public ThrowEvent(PointF paramPointF1, PointF paramPointF2)
    {
      this.from = paramPointF1;
      this.throwVector = paramPointF2;
      this.startTime = System.nanoTime();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.viewer.ThrowController
 * JD-Core Version:    0.5.4
 */