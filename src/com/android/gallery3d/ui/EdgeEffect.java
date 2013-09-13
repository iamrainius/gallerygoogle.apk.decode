package com.android.gallery3d.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class EdgeEffect
{
  private final int MIN_WIDTH = 300;
  private float mDuration;
  private final Drawable mEdge;
  private float mEdgeAlpha;
  private float mEdgeAlphaFinish;
  private float mEdgeAlphaStart;
  private float mEdgeScaleY;
  private float mEdgeScaleYFinish;
  private float mEdgeScaleYStart;
  private final Drawable mGlow;
  private float mGlowAlpha;
  private float mGlowAlphaFinish;
  private float mGlowAlphaStart;
  private float mGlowScaleY;
  private float mGlowScaleYFinish;
  private float mGlowScaleYStart;
  private int mHeight;
  private final Interpolator mInterpolator;
  private final int mMinWidth;
  private float mPullDistance;
  private long mStartTime;
  private int mState = 0;
  private int mWidth;

  public EdgeEffect(Context paramContext)
  {
    this.mEdge = new Drawable(paramContext, 2130837813);
    this.mGlow = new Drawable(paramContext, 2130837814);
    this.mMinWidth = (int)(0.5F + 300.0F * paramContext.getResources().getDisplayMetrics().density);
    this.mInterpolator = new DecelerateInterpolator();
  }

  private void update()
  {
    float f1 = Math.min((float)(AnimationTime.get() - this.mStartTime) / this.mDuration, 1.0F);
    float f2 = this.mInterpolator.getInterpolation(f1);
    this.mEdgeAlpha = (this.mEdgeAlphaStart + f2 * (this.mEdgeAlphaFinish - this.mEdgeAlphaStart));
    this.mEdgeScaleY = (this.mEdgeScaleYStart + f2 * (this.mEdgeScaleYFinish - this.mEdgeScaleYStart));
    this.mGlowAlpha = (this.mGlowAlphaStart + f2 * (this.mGlowAlphaFinish - this.mGlowAlphaStart));
    this.mGlowScaleY = (this.mGlowScaleYStart + f2 * (this.mGlowScaleYFinish - this.mGlowScaleYStart));
    if (f1 >= 0.999F);
    switch (this.mState)
    {
    default:
      return;
    case 2:
      this.mState = 3;
      this.mStartTime = AnimationTime.get();
      this.mDuration = 1000.0F;
      this.mEdgeAlphaStart = this.mEdgeAlpha;
      this.mEdgeScaleYStart = this.mEdgeScaleY;
      this.mGlowAlphaStart = this.mGlowAlpha;
      this.mGlowScaleYStart = this.mGlowScaleY;
      this.mEdgeAlphaFinish = 0.0F;
      this.mEdgeScaleYFinish = 0.0F;
      this.mGlowAlphaFinish = 0.0F;
      this.mGlowScaleYFinish = 0.0F;
      return;
    case 1:
      this.mState = 4;
      this.mStartTime = AnimationTime.get();
      this.mDuration = 1000.0F;
      this.mEdgeAlphaStart = this.mEdgeAlpha;
      this.mEdgeScaleYStart = this.mEdgeScaleY;
      this.mGlowAlphaStart = this.mGlowAlpha;
      this.mGlowScaleYStart = this.mGlowScaleY;
      this.mEdgeAlphaFinish = 0.0F;
      this.mEdgeScaleYFinish = 0.0F;
      this.mGlowAlphaFinish = 0.0F;
      this.mGlowScaleYFinish = 0.0F;
      return;
    case 4:
      float f3;
      if (this.mGlowScaleYFinish != 0.0F)
        f3 = 1.0F / (this.mGlowScaleYFinish * this.mGlowScaleYFinish);
      while (true)
      {
        this.mEdgeScaleY = (this.mEdgeScaleYStart + f3 * (f2 * (this.mEdgeScaleYFinish - this.mEdgeScaleYStart)));
        this.mState = 3;
        return;
        f3 = 3.4028235E+38F;
      }
    case 3:
    }
    this.mState = 0;
  }

  public boolean draw(GLCanvas paramGLCanvas)
  {
    update();
    int i = this.mEdge.getIntrinsicHeight();
    this.mEdge.getIntrinsicWidth();
    int j = this.mGlow.getIntrinsicHeight();
    int k = this.mGlow.getIntrinsicWidth();
    this.mGlow.setAlpha((int)(255.0F * Math.max(0.0F, Math.min(this.mGlowAlpha, 1.0F))));
    int l = (int)Math.min(0.6F * (j * this.mGlowScaleY * j / k), 4.0F * j);
    label135: int i1;
    if (this.mWidth < this.mMinWidth)
    {
      int i3 = (this.mWidth - this.mMinWidth) / 2;
      this.mGlow.setBounds(i3, 0, this.mWidth - i3, l);
      this.mGlow.draw(paramGLCanvas);
      this.mEdge.setAlpha((int)(255.0F * Math.max(0.0F, Math.min(this.mEdgeAlpha, 1.0F))));
      i1 = (int)(i * this.mEdgeScaleY);
      if (this.mWidth >= this.mMinWidth)
        break label254;
      int i2 = (this.mWidth - this.mMinWidth) / 2;
      this.mEdge.setBounds(i2, 0, this.mWidth - i2, i1);
    }
    while (true)
    {
      this.mEdge.draw(paramGLCanvas);
      if (this.mState == 0)
        break;
      return true;
      this.mGlow.setBounds(0, 0, this.mWidth, l);
      break label135:
      label254: this.mEdge.setBounds(0, 0, this.mWidth, i1);
    }
    return false;
  }

  public boolean isFinished()
  {
    return this.mState == 0;
  }

  public void onAbsorb(int paramInt)
  {
    this.mState = 2;
    int i = Math.max(100, Math.abs(paramInt));
    this.mStartTime = AnimationTime.get();
    this.mDuration = (0.1F + 0.03F * i);
    this.mEdgeAlphaStart = 0.0F;
    this.mEdgeScaleYStart = 0.0F;
    this.mEdgeScaleY = 0.0F;
    this.mGlowAlphaStart = 0.5F;
    this.mGlowScaleYStart = 0.0F;
    this.mEdgeAlphaFinish = Math.max(0, Math.min(i * 8, 1));
    this.mEdgeScaleYFinish = Math.max(0.5F, Math.min(i * 8, 1.0F));
    this.mGlowScaleYFinish = Math.min(0.025F + 0.00015F * (i * (i / 100)), 1.75F);
    this.mGlowAlphaFinish = Math.max(this.mGlowAlphaStart, Math.min(1.0E-005F * (i * 16), 0.8F));
  }

  public void onPull(float paramFloat)
  {
    long l = AnimationTime.get();
    if ((this.mState == 4) && ((float)(l - this.mStartTime) < this.mDuration))
      return;
    if (this.mState != 1)
      this.mGlowScaleY = 1.0F;
    this.mState = 1;
    this.mStartTime = l;
    this.mDuration = 167.0F;
    this.mPullDistance = (paramFloat + this.mPullDistance);
    float f1 = Math.abs(this.mPullDistance);
    float f2 = Math.max(0.6F, Math.min(f1, 0.8F));
    this.mEdgeAlphaStart = f2;
    this.mEdgeAlpha = f2;
    float f3 = Math.max(0.5F, Math.min(f1 * 7.0F, 1.0F));
    this.mEdgeScaleYStart = f3;
    this.mEdgeScaleY = f3;
    float f4 = Math.min(0.8F, this.mGlowAlpha + 1.1F * Math.abs(paramFloat));
    this.mGlowAlphaStart = f4;
    this.mGlowAlpha = f4;
    float f5 = Math.abs(paramFloat);
    if ((paramFloat > 0.0F) && (this.mPullDistance < 0.0F))
      f5 = -f5;
    if (this.mPullDistance == 0.0F)
      this.mGlowScaleY = 0.0F;
    float f6 = Math.min(4.0F, Math.max(0.0F, this.mGlowScaleY + f5 * 7.0F));
    this.mGlowScaleYStart = f6;
    this.mGlowScaleY = f6;
    this.mEdgeAlphaFinish = this.mEdgeAlpha;
    this.mEdgeScaleYFinish = this.mEdgeScaleY;
    this.mGlowAlphaFinish = this.mGlowAlpha;
    this.mGlowScaleYFinish = this.mGlowScaleY;
  }

  public void onRelease()
  {
    this.mPullDistance = 0.0F;
    if ((this.mState != 1) && (this.mState != 4))
      return;
    this.mState = 3;
    this.mEdgeAlphaStart = this.mEdgeAlpha;
    this.mEdgeScaleYStart = this.mEdgeScaleY;
    this.mGlowAlphaStart = this.mGlowAlpha;
    this.mGlowScaleYStart = this.mGlowScaleY;
    this.mEdgeAlphaFinish = 0.0F;
    this.mEdgeScaleYFinish = 0.0F;
    this.mGlowAlphaFinish = 0.0F;
    this.mGlowScaleYFinish = 0.0F;
    this.mStartTime = AnimationTime.get();
    this.mDuration = 1000.0F;
  }

  public void setSize(int paramInt1, int paramInt2)
  {
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
  }

  private static class Drawable extends ResourceTexture
  {
    private int mAlpha = 255;
    private Rect mBounds = new Rect();

    public Drawable(Context paramContext, int paramInt)
    {
      super(paramContext, paramInt);
    }

    public void draw(GLCanvas paramGLCanvas)
    {
      paramGLCanvas.save(1);
      paramGLCanvas.multiplyAlpha(this.mAlpha / 255.0F);
      Rect localRect = this.mBounds;
      draw(paramGLCanvas, localRect.left, localRect.top, localRect.width(), localRect.height());
      paramGLCanvas.restore();
    }

    public int getIntrinsicHeight()
    {
      return getHeight();
    }

    public int getIntrinsicWidth()
    {
      return getWidth();
    }

    public void setAlpha(int paramInt)
    {
      this.mAlpha = paramInt;
    }

    public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.mBounds.set(paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.EdgeEffect
 * JD-Core Version:    0.5.4
 */