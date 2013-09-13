package com.android.gallery3d.filtershow.imageshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

public class ImageRotate extends ImageGeometry
{
  private static final Paint gPaint = new Paint();
  private float mAngle = 0.0F;
  private float mBaseAngle = 0.0F;
  private final boolean mSnapToNinety = true;

  public ImageRotate(Context paramContext)
  {
    super(paramContext);
  }

  public ImageRotate(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  private void computeValue()
  {
    float f = getCurrentTouchAngle();
    this.mAngle = ((this.mBaseAngle - f) % 360.0F);
  }

  protected void drawShape(Canvas paramCanvas, Bitmap paramBitmap)
  {
    gPaint.setAntiAlias(true);
    gPaint.setFilterBitmap(true);
    gPaint.setDither(true);
    gPaint.setARGB(255, 255, 255, 255);
    drawTransformedCropped(paramCanvas, paramBitmap, gPaint);
  }

  protected int getLocalValue()
  {
    return (int)getLocalRotation();
  }

  public String getName()
  {
    return getContext().getString(2131362132);
  }

  protected void setActionDown(float paramFloat1, float paramFloat2)
  {
    super.setActionDown(paramFloat1, paramFloat2);
    float f = getLocalRotation();
    this.mAngle = f;
    this.mBaseAngle = f;
  }

  protected void setActionMove(float paramFloat1, float paramFloat2)
  {
    super.setActionMove(paramFloat1, paramFloat2);
    computeValue();
    setLocalRotation(this.mAngle % 360.0F);
  }

  protected void setActionUp()
  {
    super.setActionUp();
    setLocalRotation(snappedAngle(this.mAngle % 360.0F));
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.imageshow.ImageRotate
 * JD-Core Version:    0.5.4
 */