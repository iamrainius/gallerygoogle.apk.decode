package com.android.gallery3d.ui;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.gallery3d.common.Utils;

class EdgeAnimation
{
  private long mDuration;
  private final Interpolator mInterpolator = new DecelerateInterpolator();
  private long mStartTime;
  private int mState = 0;
  private float mValue;
  private float mValueFinish;
  private float mValueStart;

  private long now()
  {
    return AnimationTime.get();
  }

  private void startAnimation(float paramFloat1, float paramFloat2, long paramLong, int paramInt)
  {
    this.mValueStart = paramFloat1;
    this.mValueFinish = paramFloat2;
    this.mDuration = paramLong;
    this.mStartTime = now();
    this.mState = paramInt;
  }

  public float getValue()
  {
    return this.mValue;
  }

  public void onAbsorb(float paramFloat)
  {
    float f = Utils.clamp(this.mValue + 0.1F * paramFloat, -1.0F, 1.0F);
    startAnimation(this.mValue, f, 200L, 2);
  }

  public void onPull(float paramFloat)
  {
    if (this.mState == 2)
      return;
    this.mValue = Utils.clamp(paramFloat + this.mValue, -1.0F, 1.0F);
    this.mState = 1;
  }

  public void onRelease()
  {
    if ((this.mState == 0) || (this.mState == 2))
      return;
    startAnimation(this.mValue, 0.0F, 500L, 3);
  }

  public boolean update()
  {
    if (this.mState == 0)
      return false;
    if (this.mState == 1)
      return true;
    float f1 = Utils.clamp((float)(now() - this.mStartTime) / (float)this.mDuration, 0.0F, 1.0F);
    float f2;
    if (this.mState == 2)
    {
      f2 = f1;
      label51: this.mValue = (this.mValueStart + f2 * (this.mValueFinish - this.mValueStart));
      if (f1 >= 1.0F)
        switch (this.mState)
        {
        default:
        case 2:
        case 3:
        }
    }
    while (true)
    {
      return true;
      f2 = this.mInterpolator.getInterpolation(f1);
      break label51:
      startAnimation(this.mValue, 0.0F, 500L, 3);
      continue;
      this.mState = 0;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.EdgeAnimation
 * JD-Core Version:    0.5.4
 */