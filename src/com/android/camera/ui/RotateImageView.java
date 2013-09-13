package com.android.camera.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView.ScaleType;

public class RotateImageView extends TwoStateImageView
  implements Rotatable
{
  private long mAnimationEndTime = 0L;
  private long mAnimationStartTime = 0L;
  private boolean mClockwise = false;
  private int mCurrentDegree = 0;
  private boolean mEnableAnimation = true;
  private int mStartDegree = 0;
  private int mTargetDegree = 0;

  public RotateImageView(Context paramContext)
  {
    super(paramContext);
  }

  public RotateImageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  protected void onDraw(Canvas paramCanvas)
  {
    Drawable localDrawable = getDrawable();
    if (localDrawable == null);
    int i;
    int j;
    do
    {
      return;
      Rect localRect = localDrawable.getBounds();
      i = localRect.right - localRect.left;
      j = localRect.bottom - localRect.top;
    }
    while ((i == 0) || (j == 0));
    int i6;
    label96: int i8;
    int i9;
    if (this.mCurrentDegree != this.mTargetDegree)
    {
      long l1 = AnimationUtils.currentAnimationTimeMillis();
      if (l1 >= this.mAnimationEndTime)
        break label330;
      i6 = (int)(l1 - this.mAnimationStartTime);
      int i7 = this.mStartDegree;
      if (!this.mClockwise)
        break label307;
      i8 = i7 + i6 * 270 / 1000;
      if (i8 < 0)
        break label315;
      i9 = i8 % 360;
      label124: this.mCurrentDegree = i9;
      invalidate();
    }
    while (true)
    {
      int k = getPaddingLeft();
      int l = getPaddingTop();
      int i1 = getPaddingRight();
      int i2 = getPaddingBottom();
      int i3 = getWidth() - k - i1;
      int i4 = getHeight() - l - i2;
      int i5 = paramCanvas.getSaveCount();
      if ((getScaleType() == ImageView.ScaleType.FIT_CENTER) && (((i3 < i) || (i4 < j))))
      {
        float f = Math.min(i3 / i, i4 / j);
        paramCanvas.scale(f, f, i3 / 2.0F, i4 / 2.0F);
      }
      paramCanvas.translate(k + i3 / 2, l + i4 / 2);
      paramCanvas.rotate(-this.mCurrentDegree);
      paramCanvas.translate(-i / 2, -j / 2);
      localDrawable.draw(paramCanvas);
      paramCanvas.restoreToCount(i5);
      return;
      label307: i6 = -i6;
      break label96:
      label315: i9 = 360 + i8 % 360;
      break label124:
      label330: this.mCurrentDegree = this.mTargetDegree;
    }
  }

  public void setOrientation(int paramInt, boolean paramBoolean)
  {
    this.mEnableAnimation = paramBoolean;
    if (paramInt >= 0);
    for (int i = paramInt % 360; i == this.mTargetDegree; i = 360 + paramInt % 360)
      return;
    this.mTargetDegree = i;
    int j;
    label80: int k;
    if (this.mEnableAnimation)
    {
      this.mStartDegree = this.mCurrentDegree;
      this.mAnimationStartTime = AnimationUtils.currentAnimationTimeMillis();
      j = this.mTargetDegree - this.mCurrentDegree;
      if (j >= 0)
      {
        if (j > 180)
          j -= 360;
        if (j < 0)
          break label145;
        k = 1;
        label102: this.mClockwise = k;
        this.mAnimationEndTime = (this.mAnimationStartTime + 1000 * Math.abs(j) / 270);
      }
    }
    while (true)
    {
      invalidate();
      return;
      j += 360;
      break label80:
      label145: k = 0;
      break label102:
      this.mCurrentDegree = this.mTargetDegree;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.RotateImageView
 * JD-Core Version:    0.5.4
 */