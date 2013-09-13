package com.android.gallery3d.common;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public class OverScroller
{
  private final boolean mFlywheel;
  private Interpolator mInterpolator;
  private int mMode;
  private final SplineOverScroller mScrollerX;
  private final SplineOverScroller mScrollerY;

  public OverScroller(Context paramContext)
  {
    this(paramContext, null);
  }

  public OverScroller(Context paramContext, Interpolator paramInterpolator)
  {
    this(paramContext, paramInterpolator, true);
  }

  public OverScroller(Context paramContext, Interpolator paramInterpolator, boolean paramBoolean)
  {
    this.mInterpolator = paramInterpolator;
    this.mFlywheel = paramBoolean;
    this.mScrollerX = new SplineOverScroller();
    this.mScrollerY = new SplineOverScroller();
    SplineOverScroller.initFromContext(paramContext);
  }

  public void abortAnimation()
  {
    this.mScrollerX.finish();
    this.mScrollerY.finish();
  }

  public boolean computeScrollOffset()
  {
    if (isFinished())
      return false;
    switch (this.mMode)
    {
    default:
    case 0:
    case 1:
    }
    while (true)
    {
      label36: return true;
      long l = AnimationUtils.currentAnimationTimeMillis() - this.mScrollerX.mStartTime;
      int i = this.mScrollerX.mDuration;
      if (l < i)
      {
        float f1 = (float)l / i;
        float f2;
        if (this.mInterpolator == null)
          f2 = Scroller.viscousFluid(f1);
        while (true)
        {
          this.mScrollerX.updateScroll(f2);
          this.mScrollerY.updateScroll(f2);
          break label36:
          f2 = this.mInterpolator.getInterpolation(f1);
        }
      }
      abortAnimation();
      continue;
      if ((!this.mScrollerX.mFinished) && (!this.mScrollerX.update()) && (!this.mScrollerX.continueWhenFinished()))
        this.mScrollerX.finish();
      if ((this.mScrollerY.mFinished) || (this.mScrollerY.update()) || (this.mScrollerY.continueWhenFinished()))
        continue;
      this.mScrollerY.finish();
    }
  }

  public void fling(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10)
  {
    if ((this.mFlywheel) && (!isFinished()))
    {
      float f1 = this.mScrollerX.mCurrVelocity;
      float f2 = this.mScrollerY.mCurrVelocity;
      if ((Math.signum(paramInt3) == Math.signum(f1)) && (Math.signum(paramInt4) == Math.signum(f2)))
      {
        paramInt3 = (int)(f1 + paramInt3);
        paramInt4 = (int)(f2 + paramInt4);
      }
    }
    this.mMode = 1;
    this.mScrollerX.fling(paramInt1, paramInt3, paramInt5, paramInt6, paramInt9);
    this.mScrollerY.fling(paramInt2, paramInt4, paramInt7, paramInt8, paramInt10);
  }

  public final void forceFinished(boolean paramBoolean)
  {
    SplineOverScroller.access$002(this.mScrollerX, SplineOverScroller.access$002(this.mScrollerY, paramBoolean));
  }

  public float getCurrVelocity()
  {
    return FloatMath.sqrt(this.mScrollerX.mCurrVelocity * this.mScrollerX.mCurrVelocity + this.mScrollerY.mCurrVelocity * this.mScrollerY.mCurrVelocity);
  }

  public final int getCurrX()
  {
    return this.mScrollerX.mCurrentPosition;
  }

  public final int getFinalX()
  {
    return this.mScrollerX.mFinal;
  }

  public final boolean isFinished()
  {
    return (this.mScrollerX.mFinished) && (this.mScrollerY.mFinished);
  }

  public void startScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    this.mMode = 0;
    this.mScrollerX.startScroll(paramInt1, paramInt3, paramInt5);
    this.mScrollerY.startScroll(paramInt2, paramInt4, paramInt5);
  }

  static class SplineOverScroller
  {
    private static float DECELERATION_RATE = (float)(Math.log(0.78D) / Math.log(0.9D));
    private static float PHYSICAL_COEF;
    private static final float[] SPLINE_POSITION = new float[101];
    private static final float[] SPLINE_TIME = new float[101];
    private float mCurrVelocity;
    private int mCurrentPosition;
    private float mDeceleration;
    private int mDuration;
    private int mFinal;
    private boolean mFinished = true;
    private float mFlingFriction = ViewConfiguration.getScrollFriction();
    private int mOver;
    private int mSplineDistance;
    private int mSplineDuration;
    private int mStart;
    private long mStartTime;
    private int mState = 0;
    private int mVelocity;

    static
    {
      float f1 = 0.0F;
      float f2 = 0.0F;
      int i = 0;
      if (i < 100)
      {
        label37: float f3 = i / 100.0F;
        float f4 = 1.0F;
        float f5 = f1 + (f4 - f1) / 2.0F;
        float f6 = 3.0F * f5 * (1.0F - f5);
        float f7 = f6 * (0.175F * (1.0F - f5) + 0.35F * f5) + f5 * (f5 * f5);
        float f8;
        if (Math.abs(f7 - f3) < 1.E-005D)
        {
          SPLINE_POSITION[i] = (f6 * (f5 + 0.5F * (1.0F - f5)) + f5 * (f5 * f5));
          f8 = 1.0F;
        }
        while (true)
        {
          float f9 = f2 + (f8 - f2) / 2.0F;
          float f10 = 3.0F * f9 * (1.0F - f9);
          float f11 = f10 * (f9 + 0.5F * (1.0F - f9)) + f9 * (f9 * f9);
          if (Math.abs(f11 - f3) < 1.E-005D)
          {
            SPLINE_TIME[i] = (f10 * (0.175F * (1.0F - f9) + 0.35F * f9) + f9 * (f9 * f9));
            ++i;
            break label37:
            if (f7 > f3)
              f4 = f5;
            f1 = f5;
          }
          if (f11 > f3)
            f8 = f9;
          f2 = f9;
        }
      }
      float[] arrayOfFloat = SPLINE_POSITION;
      SPLINE_TIME[100] = 1.0F;
      arrayOfFloat[100] = 1.0F;
    }

    private void adjustDuration(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = paramInt2 - paramInt1;
      float f1 = Math.abs((paramInt3 - paramInt1) / i);
      int j = (int)(100.0F * f1);
      if (j >= 100)
        return;
      float f2 = j / 100.0F;
      float f3 = (j + 1) / 100.0F;
      float f4 = SPLINE_TIME[j];
      float f5 = SPLINE_TIME[(j + 1)];
      this.mDuration = (int)((f4 + (f1 - f2) / (f3 - f2) * (f5 - f4)) * this.mDuration);
    }

    private void fitOnBounceCurve(int paramInt1, int paramInt2, int paramInt3)
    {
      float f1 = -paramInt3 / this.mDeceleration;
      float f2 = (float)Math.sqrt(2.0D * (paramInt3 * paramInt3 / 2.0F / Math.abs(this.mDeceleration) + Math.abs(paramInt2 - paramInt1)) / Math.abs(this.mDeceleration));
      this.mStartTime -= (int)(1000.0F * (f2 - f1));
      this.mStart = paramInt2;
      this.mVelocity = (int)(f2 * -this.mDeceleration);
    }

    private static float getDeceleration(int paramInt)
    {
      if (paramInt > 0)
        return -2000.0F;
      return 2000.0F;
    }

    private double getSplineDeceleration(int paramInt)
    {
      return Math.log(0.35F * Math.abs(paramInt) / (this.mFlingFriction * PHYSICAL_COEF));
    }

    private double getSplineFlingDistance(int paramInt)
    {
      double d1 = getSplineDeceleration(paramInt);
      double d2 = DECELERATION_RATE - 1.0D;
      return this.mFlingFriction * PHYSICAL_COEF * Math.exp(d1 * (DECELERATION_RATE / d2));
    }

    private int getSplineFlingDuration(int paramInt)
    {
      return (int)(1000.0D * Math.exp(getSplineDeceleration(paramInt) / (DECELERATION_RATE - 1.0D)));
    }

    static void initFromContext(Context paramContext)
    {
      PHYSICAL_COEF = 0.84F * (386.0878F * (160.0F * paramContext.getResources().getDisplayMetrics().density));
    }

    private void onEdgeReached()
    {
      float f1 = this.mVelocity * this.mVelocity / (2.0F * Math.abs(this.mDeceleration));
      float f2 = Math.signum(this.mVelocity);
      if (f1 > this.mOver)
      {
        this.mDeceleration = (-f2 * this.mVelocity * this.mVelocity / (2.0F * this.mOver));
        f1 = this.mOver;
      }
      this.mOver = (int)f1;
      this.mState = 2;
      int i = this.mStart;
      if (this.mVelocity > 0);
      while (true)
      {
        this.mFinal = (i + (int)f1);
        this.mDuration = (-(int)(1000.0F * this.mVelocity / this.mDeceleration));
        return;
        f1 = -f1;
      }
    }

    private void startAfterEdge(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if ((paramInt1 > paramInt2) && (paramInt1 < paramInt3))
      {
        Log.e("OverScroller", "startAfterEdge called from a valid position");
        this.mFinished = true;
        return;
      }
      int i;
      label32: int j;
      label40: int k;
      if (paramInt1 > paramInt3)
      {
        i = 1;
        if (i == 0)
          break label78;
        j = paramInt3;
        k = paramInt1 - j;
        if (k * paramInt4 < 0)
          break label84;
      }
      for (int l = 1; l != 0; l = 0)
      {
        startBounceAfterEdge(paramInt1, j, paramInt4);
        return;
        i = 0;
        break label32:
        label78: j = paramInt2;
        label84: break label40:
      }
      if (getSplineFlingDistance(paramInt4) > Math.abs(k))
      {
        int i1;
        if (i != 0)
        {
          i1 = paramInt2;
          label114: if (i == 0)
            break label144;
        }
        for (int i2 = paramInt1; ; i2 = paramInt3)
        {
          fling(paramInt1, paramInt4, i1, i2, this.mOver);
          return;
          i1 = paramInt1;
          label144: break label114:
        }
      }
      startSpringback(paramInt1, j, paramInt4);
    }

    private void startBounceAfterEdge(int paramInt1, int paramInt2, int paramInt3)
    {
      if (paramInt3 == 0);
      for (int i = paramInt1 - paramInt2; ; i = paramInt3)
      {
        this.mDeceleration = getDeceleration(i);
        fitOnBounceCurve(paramInt1, paramInt2, paramInt3);
        onEdgeReached();
        return;
      }
    }

    private void startSpringback(int paramInt1, int paramInt2, int paramInt3)
    {
      this.mFinished = false;
      this.mState = 1;
      this.mStart = paramInt1;
      this.mFinal = paramInt2;
      int i = paramInt1 - paramInt2;
      this.mDeceleration = getDeceleration(i);
      this.mVelocity = (-i);
      this.mOver = Math.abs(i);
      this.mDuration = (int)(1000.0D * Math.sqrt(-2.0D * i / this.mDeceleration));
    }

    boolean continueWhenFinished()
    {
      int i = this.mState;
      int j = 0;
      switch (i)
      {
      default:
      case 1:
      case 0:
      case 2:
      }
      while (true)
      {
        update();
        j = 1;
        int k;
        int l;
        do
        {
          return j;
          k = this.mDuration;
          l = this.mSplineDuration;
          j = 0;
        }
        while (k >= l);
        this.mStart = this.mFinal;
        this.mVelocity = (int)this.mCurrVelocity;
        this.mDeceleration = getDeceleration(this.mVelocity);
        this.mStartTime += this.mDuration;
        onEdgeReached();
        continue;
        this.mStartTime += this.mDuration;
        startSpringback(this.mFinal, this.mStart, 0);
      }
    }

    void finish()
    {
      this.mCurrentPosition = this.mFinal;
      this.mFinished = true;
    }

    void fling(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      this.mOver = paramInt5;
      this.mFinished = false;
      this.mVelocity = paramInt2;
      this.mCurrVelocity = paramInt2;
      this.mSplineDuration = 0;
      this.mDuration = 0;
      this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
      this.mStart = paramInt1;
      this.mCurrentPosition = paramInt1;
      if ((paramInt1 > paramInt4) || (paramInt1 < paramInt3))
        startAfterEdge(paramInt1, paramInt3, paramInt4, paramInt2);
      do
      {
        return;
        this.mState = 0;
        double d = 0.0D;
        if (paramInt2 != 0)
        {
          int i = getSplineFlingDuration(paramInt2);
          this.mSplineDuration = i;
          this.mDuration = i;
          d = getSplineFlingDistance(paramInt2);
        }
        this.mSplineDistance = (int)(d * Math.signum(paramInt2));
        this.mFinal = (paramInt1 + this.mSplineDistance);
        if (this.mFinal >= paramInt3)
          continue;
        adjustDuration(this.mStart, this.mFinal, paramInt3);
        this.mFinal = paramInt3;
      }
      while (this.mFinal <= paramInt4);
      adjustDuration(this.mStart, this.mFinal, paramInt4);
      this.mFinal = paramInt4;
    }

    void startScroll(int paramInt1, int paramInt2, int paramInt3)
    {
      this.mFinished = false;
      this.mStart = paramInt1;
      this.mFinal = (paramInt1 + paramInt2);
      this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
      this.mDuration = paramInt3;
      this.mDeceleration = 0.0F;
      this.mVelocity = 0;
    }

    boolean update()
    {
      long l = AnimationUtils.currentAnimationTimeMillis() - this.mStartTime;
      if (l > this.mDuration)
        return false;
      double d = 0.0D;
      switch (this.mState)
      {
      default:
      case 0:
      case 2:
      case 1:
      }
      while (true)
      {
        this.mCurrentPosition = (this.mStart + (int)Math.round(d));
        return true;
        float f5 = (float)l / this.mSplineDuration;
        int i = (int)(100.0F * f5);
        float f6 = 1.0F;
        float f7 = 0.0F;
        if (i < 100)
        {
          float f8 = i / 100.0F;
          float f9 = (i + 1) / 100.0F;
          float f10 = SPLINE_POSITION[i];
          f7 = (SPLINE_POSITION[(i + 1)] - f10) / (f9 - f8);
          f6 = f10 + f7 * (f5 - f8);
        }
        d = f6 * this.mSplineDistance;
        this.mCurrVelocity = (1000.0F * (f7 * this.mSplineDistance / this.mSplineDuration));
        continue;
        float f4 = (float)l / 1000.0F;
        this.mCurrVelocity = (this.mVelocity + f4 * this.mDeceleration);
        d = f4 * this.mVelocity + f4 * (f4 * this.mDeceleration) / 2.0F;
        continue;
        float f1 = (float)l / this.mDuration;
        float f2 = f1 * f1;
        float f3 = Math.signum(this.mVelocity);
        d = f3 * this.mOver * (3.0F * f2 - f2 * (2.0F * f1));
        this.mCurrVelocity = (6.0F * (f3 * this.mOver) * (f2 + -f1));
      }
    }

    void updateScroll(float paramFloat)
    {
      this.mCurrentPosition = (this.mStart + Math.round(paramFloat * (this.mFinal - this.mStart)));
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.common.OverScroller
 * JD-Core Version:    0.5.4
 */