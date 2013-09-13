package com.android.gallery3d.filtershow.imageshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

public class ImageFlip extends ImageGeometry
{
  private static final Paint gPaint = new Paint();
  private GeometryMetadata.FLIP mNextFlip = GeometryMetadata.FLIP.NONE;

  public ImageFlip(Context paramContext)
  {
    super(paramContext);
  }

  public ImageFlip(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  private float getScaledMinFlick()
  {
    RectF localRectF = getLocalDisplayBounds();
    return 0.1F * Math.min(localRectF.width(), localRectF.height()) / getLocalScale();
  }

  protected void drawShape(Canvas paramCanvas, Bitmap paramBitmap)
  {
    gPaint.setAntiAlias(true);
    gPaint.setFilterBitmap(true);
    gPaint.setDither(true);
    gPaint.setARGB(255, 255, 255, 255);
    drawTransformedCropped(paramCanvas, paramBitmap, gPaint);
  }

  public String getName()
  {
    return getContext().getString(2131362133);
  }

  boolean hasRotated90()
  {
    return constrainedRotation(getLocalRotation()) / 90 % 2 != 0;
  }

  public void resetParameter()
  {
    super.resetParameter();
    this.mNextFlip = GeometryMetadata.FLIP.NONE;
  }

  protected void setActionDown(float paramFloat1, float paramFloat2)
  {
    super.setActionDown(paramFloat1, paramFloat2);
  }

  protected void setActionMove(float paramFloat1, float paramFloat2)
  {
    super.setActionMove(paramFloat1, paramFloat2);
    float f1 = this.mTouchCenterX - paramFloat1;
    float f2 = this.mTouchCenterY - paramFloat2;
    float f3 = getScaledMinFlick();
    if (hasRotated90())
    {
      float f4 = f1;
      f1 = f2;
      f2 = f4;
    }
    GeometryMetadata.FLIP localFLIP3;
    if (Math.abs(f1) >= f3)
      localFLIP3 = getLocalFlip();
    GeometryMetadata.FLIP localFLIP4;
    switch (1.$SwitchMap$com$android$gallery3d$filtershow$imageshow$GeometryMetadata$FLIP[localFLIP3.ordinal()])
    {
    default:
      localFLIP4 = GeometryMetadata.FLIP.NONE;
      label105: this.mNextFlip = localFLIP4;
      if (Math.abs(f2) < f3)
        break label179;
      GeometryMetadata.FLIP localFLIP1 = getLocalFlip();
      switch (1.$SwitchMap$com$android$gallery3d$filtershow$imageshow$GeometryMetadata$FLIP[localFLIP1.ordinal()])
      {
      default:
      case 1:
      case 3:
      case 2:
      case 4:
      }
    case 1:
    case 2:
    case 3:
    case 4:
    }
    for (GeometryMetadata.FLIP localFLIP2 = GeometryMetadata.FLIP.NONE; ; localFLIP2 = GeometryMetadata.FLIP.HORIZONTAL)
      while (true)
      {
        this.mNextFlip = localFLIP2;
        label179: return;
        localFLIP4 = GeometryMetadata.FLIP.HORIZONTAL;
        break label105:
        localFLIP4 = GeometryMetadata.FLIP.NONE;
        break label105:
        localFLIP4 = GeometryMetadata.FLIP.BOTH;
        break label105:
        localFLIP4 = GeometryMetadata.FLIP.VERTICAL;
        break label105:
        localFLIP2 = GeometryMetadata.FLIP.VERTICAL;
        continue;
        localFLIP2 = GeometryMetadata.FLIP.NONE;
        continue;
        localFLIP2 = GeometryMetadata.FLIP.BOTH;
      }
  }

  protected void setActionUp()
  {
    super.setActionUp();
    setLocalFlip(this.mNextFlip);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.imageshow.ImageFlip
 * JD-Core Version:    0.5.4
 */