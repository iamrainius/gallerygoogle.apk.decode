package com.android.gallery3d.anim;

import android.view.animation.Interpolator;
import com.android.gallery3d.common.Utils;

public abstract class Animation
{
  private int mDuration;
  private Interpolator mInterpolator;
  private long mStartTime = -2L;

  public boolean calculate(long paramLong)
  {
    if (this.mStartTime == -2L);
    do
    {
      return false;
      if (this.mStartTime == -1L)
        this.mStartTime = paramLong;
      int i = (int)(paramLong - this.mStartTime);
      float f = Utils.clamp(i / this.mDuration, 0.0F, 1.0F);
      Interpolator localInterpolator = this.mInterpolator;
      if (localInterpolator != null)
        f = localInterpolator.getInterpolation(f);
      onCalculate(f);
      if (i < this.mDuration)
        continue;
      this.mStartTime = -2L;
    }
    while (this.mStartTime == -2L);
    return true;
  }

  public void forceStop()
  {
    this.mStartTime = -2L;
  }

  public boolean isActive()
  {
    return this.mStartTime != -2L;
  }

  protected abstract void onCalculate(float paramFloat);

  public void setDuration(int paramInt)
  {
    this.mDuration = paramInt;
  }

  public void setInterpolator(Interpolator paramInterpolator)
  {
    this.mInterpolator = paramInterpolator;
  }

  public void setStartTime(long paramLong)
  {
    this.mStartTime = paramLong;
  }

  public void start()
  {
    this.mStartTime = -1L;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.anim.Animation
 * JD-Core Version:    0.5.4
 */