package com.android.camera.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.util.MotionEventHelper;

public class RotateLayout extends ViewGroup
  implements Rotatable
{
  protected View mChild;
  private Matrix mMatrix = new Matrix();
  private int mOrientation;

  public RotateLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setBackgroundResource(17170445);
  }

  protected void dispatchDraw(Canvas paramCanvas)
  {
    if (ApiHelper.HAS_VIEW_TRANSFORM_PROPERTIES)
    {
      super.dispatchDraw(paramCanvas);
      return;
    }
    paramCanvas.save();
    int i = getMeasuredWidth();
    int j = getMeasuredHeight();
    switch (this.mOrientation)
    {
    default:
    case 0:
    case 90:
    case 180:
    case 270:
    }
    while (true)
    {
      paramCanvas.rotate(-this.mOrientation, 0.0F, 0.0F);
      super.dispatchDraw(paramCanvas);
      paramCanvas.restore();
      return;
      paramCanvas.translate(0.0F, 0.0F);
      continue;
      paramCanvas.translate(0.0F, j);
      continue;
      paramCanvas.translate(i, j);
      continue;
      paramCanvas.translate(i, 0.0F);
    }
  }

  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    int i;
    int j;
    if (!ApiHelper.HAS_VIEW_TRANSFORM_PROPERTIES)
    {
      i = getMeasuredWidth();
      j = getMeasuredHeight();
      switch (this.mOrientation)
      {
      default:
      case 0:
      case 90:
      case 180:
      case 270:
      }
    }
    while (true)
    {
      this.mMatrix.postRotate(this.mOrientation);
      paramMotionEvent = MotionEventHelper.transformEvent(paramMotionEvent, this.mMatrix);
      return super.dispatchTouchEvent(paramMotionEvent);
      this.mMatrix.setTranslate(0.0F, 0.0F);
      continue;
      this.mMatrix.setTranslate(0.0F, -j);
      continue;
      this.mMatrix.setTranslate(-i, -j);
      continue;
      this.mMatrix.setTranslate(-i, 0.0F);
    }
  }

  public ViewParent invalidateChildInParent(int[] paramArrayOfInt, Rect paramRect)
  {
    if ((!ApiHelper.HAS_VIEW_TRANSFORM_PROPERTIES) && (this.mOrientation != 0))
      paramRect.set(0, 0, getWidth(), getHeight());
    return super.invalidateChildInParent(paramArrayOfInt, paramRect);
  }

  @TargetApi(11)
  protected void onFinishInflate()
  {
    this.mChild = getChildAt(0);
    if (!ApiHelper.HAS_VIEW_TRANSFORM_PROPERTIES)
      return;
    this.mChild.setPivotX(0.0F);
    this.mChild.setPivotY(0.0F);
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt3 - paramInt1;
    int j = paramInt4 - paramInt2;
    switch (this.mOrientation)
    {
    default:
      return;
    case 0:
    case 180:
      this.mChild.layout(0, 0, i, j);
      return;
    case 90:
    case 270:
    }
    this.mChild.layout(0, 0, j, i);
  }

  @TargetApi(11)
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = this.mOrientation;
    int j = 0;
    int k = 0;
    switch (i)
    {
    default:
      label56: setMeasuredDimension(k, j);
      if (!ApiHelper.HAS_VIEW_TRANSFORM_PROPERTIES)
        break label129;
      switch (this.mOrientation)
      {
      default:
      case 0:
      case 90:
      case 180:
      case 270:
      }
    case 0:
    case 180:
    case 90:
    case 270:
    }
    while (true)
    {
      this.mChild.setRotation(-this.mOrientation);
      label129: return;
      measureChild(this.mChild, paramInt1, paramInt2);
      k = this.mChild.getMeasuredWidth();
      j = this.mChild.getMeasuredHeight();
      break label56:
      measureChild(this.mChild, paramInt2, paramInt1);
      k = this.mChild.getMeasuredHeight();
      j = this.mChild.getMeasuredWidth();
      break label56:
      this.mChild.setTranslationX(0.0F);
      this.mChild.setTranslationY(0.0F);
      continue;
      this.mChild.setTranslationX(0.0F);
      this.mChild.setTranslationY(j);
      continue;
      this.mChild.setTranslationX(k);
      this.mChild.setTranslationY(j);
      continue;
      this.mChild.setTranslationX(k);
      this.mChild.setTranslationY(0.0F);
    }
  }

  public void setOrientation(int paramInt, boolean paramBoolean)
  {
    int i = paramInt % 360;
    if (this.mOrientation == i)
      return;
    this.mOrientation = i;
    requestLayout();
  }

  public boolean shouldDelayChildPressedState()
  {
    return false;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.RotateLayout
 * JD-Core Version:    0.5.4
 */