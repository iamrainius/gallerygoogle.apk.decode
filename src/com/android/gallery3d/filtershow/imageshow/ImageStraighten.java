package com.android.gallery3d.filtershow.imageshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import com.android.gallery3d.filtershow.PanelController;

public class ImageStraighten extends ImageGeometry
{
  private static final Paint gPaint = new Paint();
  private float mAngle = 0.0F;
  private float mBaseAngle = 0.0F;

  public ImageStraighten(Context paramContext)
  {
    super(paramContext);
  }

  public ImageStraighten(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  private void computeValue()
  {
    float f = getCurrentTouchAngle();
    this.mAngle = ((this.mBaseAngle - f) % 360.0F);
    this.mAngle = Math.max(-45.0F, this.mAngle);
    this.mAngle = Math.min(45.0F, this.mAngle);
  }

  private void setCropToStraighten()
  {
    setLocalCropBounds(getUntranslatedStraightenCropBounds(getLocalPhotoBounds(), getLocalStraighten()));
  }

  protected void drawShape(Canvas paramCanvas, Bitmap paramBitmap)
  {
    drawTransformed(paramCanvas, paramBitmap, gPaint);
    RectF localRectF1 = straightenBounds();
    Path localPath = new Path();
    localPath.addRect(localRectF1, Path.Direction.CCW);
    gPaint.setARGB(255, 255, 255, 255);
    gPaint.setStrokeWidth(3.0F);
    gPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    RectF localRectF2 = getLocalDisplayBounds();
    float f1 = localRectF2.width();
    float f2 = localRectF2.height();
    if (this.mMode != ImageGeometry.MODES.MOVE)
      return;
    paramCanvas.save();
    paramCanvas.clipPath(localPath);
    float f3 = f1 / 16;
    for (int i = 1; i < 16; ++i)
    {
      float f4 = f3 * i;
      gPaint.setARGB(60, 255, 255, 255);
      paramCanvas.drawLine(f4, 0.0F, f4, f2, gPaint);
      paramCanvas.drawLine(0.0F, f4, f1, f4, gPaint);
    }
    paramCanvas.restore();
  }

  protected void gainedVisibility()
  {
    setCropToStraighten();
  }

  protected int getLocalValue()
  {
    return (int)getLocalStraighten();
  }

  public String getName()
  {
    return getContext().getString(2131362130);
  }

  protected void lostVisibility()
  {
    saveAndSetPreset();
  }

  public void onNewValue(int paramInt)
  {
    setLocalStraighten(GeometryMath.clamp(paramInt, -45.0F, 45.0F));
    if (getPanelController() != null)
      getPanelController().onNewValue((int)getLocalStraighten());
    invalidate();
  }

  protected void setActionDown(float paramFloat1, float paramFloat2)
  {
    super.setActionDown(paramFloat1, paramFloat2);
    float f = getLocalStraighten();
    this.mAngle = f;
    this.mBaseAngle = f;
  }

  protected void setActionMove(float paramFloat1, float paramFloat2)
  {
    super.setActionMove(paramFloat1, paramFloat2);
    computeValue();
    setLocalStraighten(this.mAngle);
    setCropToStraighten();
  }

  protected void setActionUp()
  {
    super.setActionUp();
    setCropToStraighten();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.imageshow.ImageStraighten
 * JD-Core Version:    0.5.4
 */