package com.android.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

class PanoProgressBar extends ImageView
{
  private final Paint mBackgroundPaint = new Paint();
  private int mDirection = 0;
  private final Paint mDoneAreaPaint = new Paint();
  private RectF mDrawBounds;
  private float mHeight;
  private final Paint mIndicatorPaint = new Paint();
  private float mIndicatorWidth = 0.0F;
  private float mLeftMostProgress = 0.0F;
  private OnDirectionChangeListener mListener = null;
  private float mMaxProgress = 0.0F;
  private float mProgress = 0.0F;
  private float mProgressOffset = 0.0F;
  private float mRightMostProgress = 0.0F;
  private float mWidth;

  public PanoProgressBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mDoneAreaPaint.setStyle(Paint.Style.FILL);
    this.mDoneAreaPaint.setAlpha(255);
    this.mBackgroundPaint.setStyle(Paint.Style.FILL);
    this.mBackgroundPaint.setAlpha(255);
    this.mIndicatorPaint.setStyle(Paint.Style.FILL);
    this.mIndicatorPaint.setAlpha(255);
    this.mDrawBounds = new RectF();
  }

  private void setDirection(int paramInt)
  {
    if (this.mDirection == paramInt)
      return;
    this.mDirection = paramInt;
    if (this.mListener != null)
      this.mListener.onDirectionChange(this.mDirection);
    invalidate();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    paramCanvas.drawRect(this.mDrawBounds, this.mBackgroundPaint);
    float f1;
    float f2;
    if (this.mDirection != 0)
    {
      paramCanvas.drawRect(this.mLeftMostProgress, this.mDrawBounds.top, this.mRightMostProgress, this.mDrawBounds.bottom, this.mDoneAreaPaint);
      if (this.mDirection != 2)
        break label106;
      f1 = Math.max(this.mProgress - this.mIndicatorWidth, 0.0F);
      f2 = this.mProgress;
    }
    while (true)
    {
      paramCanvas.drawRect(f1, this.mDrawBounds.top, f2, this.mDrawBounds.bottom, this.mIndicatorPaint);
      super.onDraw(paramCanvas);
      return;
      label106: f1 = this.mProgress;
      f2 = Math.min(this.mProgress + this.mIndicatorWidth, this.mWidth);
    }
  }

  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    this.mDrawBounds.set(0.0F, 0.0F, this.mWidth, this.mHeight);
  }

  public void reset()
  {
    this.mProgress = 0.0F;
    this.mProgressOffset = 0.0F;
    setDirection(0);
    invalidate();
  }

  public void setBackgroundColor(int paramInt)
  {
    this.mBackgroundPaint.setColor(paramInt);
    invalidate();
  }

  public void setDoneColor(int paramInt)
  {
    this.mDoneAreaPaint.setColor(paramInt);
    invalidate();
  }

  public void setIndicatorColor(int paramInt)
  {
    this.mIndicatorPaint.setColor(paramInt);
    invalidate();
  }

  public void setIndicatorWidth(float paramFloat)
  {
    this.mIndicatorWidth = paramFloat;
    invalidate();
  }

  public void setMaxProgress(int paramInt)
  {
    this.mMaxProgress = paramInt;
  }

  public void setOnDirectionChangeListener(OnDirectionChangeListener paramOnDirectionChangeListener)
  {
    this.mListener = paramOnDirectionChangeListener;
  }

  public void setProgress(int paramInt)
  {
    if (this.mDirection == 0)
    {
      if (paramInt <= 10)
        break label116;
      setRightIncreasing(true);
    }
    while (true)
    {
      if (this.mDirection != 0)
      {
        this.mProgress = (paramInt * this.mWidth / this.mMaxProgress + this.mProgressOffset);
        this.mProgress = Math.min(this.mWidth, Math.max(0.0F, this.mProgress));
        if (this.mDirection == 2)
          this.mRightMostProgress = Math.max(this.mRightMostProgress, this.mProgress);
        if (this.mDirection == 1)
          this.mLeftMostProgress = Math.min(this.mLeftMostProgress, this.mProgress);
        invalidate();
      }
      return;
      label116: if (paramInt >= -10)
        continue;
      setRightIncreasing(false);
    }
  }

  public void setRightIncreasing(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mLeftMostProgress = 0.0F;
      this.mRightMostProgress = 0.0F;
      this.mProgressOffset = 0.0F;
      setDirection(2);
    }
    while (true)
    {
      invalidate();
      return;
      this.mLeftMostProgress = this.mWidth;
      this.mRightMostProgress = this.mWidth;
      this.mProgressOffset = this.mWidth;
      setDirection(1);
    }
  }

  public static abstract interface OnDirectionChangeListener
  {
    public abstract void onDirectionChange(int paramInt);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.PanoProgressBar
 * JD-Core Version:    0.5.4
 */