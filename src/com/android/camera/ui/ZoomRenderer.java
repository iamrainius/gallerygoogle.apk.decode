package com.android.camera.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

public class ZoomRenderer extends OverlayRenderer
  implements ScaleGestureDetector.OnScaleGestureListener
{
  private int mCenterX;
  private int mCenterY;
  private int mCircleSize;
  private ScaleGestureDetector mDetector;
  private int mInnerStroke;
  private OnZoomChangedListener mListener;
  private float mMaxCircle;
  private int mMaxZoom;
  private float mMinCircle;
  private int mMinZoom;
  private int mOuterStroke;
  private Paint mPaint;
  private Rect mTextBounds;
  private Paint mTextPaint;
  private int mZoomFraction;
  private int mZoomSig;

  public ZoomRenderer(Context paramContext)
  {
    Resources localResources = paramContext.getResources();
    this.mPaint = new Paint();
    this.mPaint.setAntiAlias(true);
    this.mPaint.setColor(-1);
    this.mPaint.setStyle(Paint.Style.STROKE);
    this.mTextPaint = new Paint(this.mPaint);
    this.mTextPaint.setStyle(Paint.Style.FILL);
    this.mTextPaint.setTextSize(localResources.getDimensionPixelSize(2131624008));
    this.mTextPaint.setTextAlign(Paint.Align.LEFT);
    this.mTextPaint.setAlpha(192);
    this.mInnerStroke = localResources.getDimensionPixelSize(2131624004);
    this.mOuterStroke = localResources.getDimensionPixelSize(2131624003);
    this.mDetector = new ScaleGestureDetector(paramContext, this);
    this.mMinCircle = localResources.getDimensionPixelSize(2131624005);
    this.mTextBounds = new Rect();
    setVisible(false);
  }

  public void layout(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.layout(paramInt1, paramInt2, paramInt3, paramInt4);
    this.mCenterX = ((paramInt3 - paramInt1) / 2);
    this.mCenterY = ((paramInt4 - paramInt2) / 2);
    this.mMaxCircle = Math.min(getWidth(), getHeight());
    this.mMaxCircle = ((this.mMaxCircle - this.mMinCircle) / 2.0F);
  }

  public void onDraw(Canvas paramCanvas)
  {
    this.mPaint.setStrokeWidth(this.mInnerStroke);
    paramCanvas.drawCircle(this.mCenterX, this.mCenterY, this.mMinCircle, this.mPaint);
    paramCanvas.drawCircle(this.mCenterX, this.mCenterY, this.mMaxCircle, this.mPaint);
    paramCanvas.drawLine(this.mCenterX - this.mMinCircle, this.mCenterY, this.mCenterX - this.mMaxCircle - 4.0F, this.mCenterY, this.mPaint);
    this.mPaint.setStrokeWidth(this.mOuterStroke);
    paramCanvas.drawCircle(this.mCenterX, this.mCenterY, this.mCircleSize, this.mPaint);
    String str = this.mZoomSig + "." + this.mZoomFraction + "x";
    this.mTextPaint.getTextBounds(str, 0, str.length(), this.mTextBounds);
    paramCanvas.drawText(str, this.mCenterX - this.mTextBounds.centerX(), this.mCenterY - this.mTextBounds.centerY(), this.mTextPaint);
  }

  public boolean onScale(ScaleGestureDetector paramScaleGestureDetector)
  {
    float f1 = paramScaleGestureDetector.getScaleFactor();
    float f2 = (int)(f1 * (f1 * this.mCircleSize));
    float f3 = Math.max(this.mMinCircle, f2);
    float f4 = Math.min(this.mMaxCircle, f3);
    if ((this.mListener != null) && ((int)f4 != this.mCircleSize))
    {
      this.mCircleSize = (int)f4;
      int i = this.mMinZoom + (int)((this.mCircleSize - this.mMinCircle) * (this.mMaxZoom - this.mMinZoom) / (this.mMaxCircle - this.mMinCircle));
      this.mListener.onZoomValueChanged(i);
    }
    return true;
  }

  public boolean onScaleBegin(ScaleGestureDetector paramScaleGestureDetector)
  {
    setVisible(true);
    if (this.mListener != null)
      this.mListener.onZoomStart();
    update();
    return true;
  }

  public void onScaleEnd(ScaleGestureDetector paramScaleGestureDetector)
  {
    setVisible(false);
    if (this.mListener == null)
      return;
    this.mListener.onZoomEnd();
  }

  public void setOnZoomChangeListener(OnZoomChangedListener paramOnZoomChangedListener)
  {
    this.mListener = paramOnZoomChangedListener;
  }

  public void setZoom(int paramInt)
  {
    this.mCircleSize = (int)(this.mMinCircle + paramInt * (this.mMaxCircle - this.mMinCircle) / (this.mMaxZoom - this.mMinZoom));
  }

  public void setZoomMax(int paramInt)
  {
    this.mMaxZoom = paramInt;
    this.mMinZoom = 0;
  }

  public void setZoomValue(int paramInt)
  {
    int i = paramInt / 10;
    this.mZoomSig = (i / 10);
    this.mZoomFraction = (i % 10);
  }

  public static abstract interface OnZoomChangedListener
  {
    public abstract void onZoomEnd();

    public abstract void onZoomStart();

    public abstract void onZoomValueChanged(int paramInt);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.ZoomRenderer
 * JD-Core Version:    0.5.4
 */