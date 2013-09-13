package com.android.gallery3d.filtershow.filters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;

public class ImageFilterParametricBorder extends ImageFilter
{
  private int mBorderColor = -1;
  private int mBorderCornerRadius = 10;
  private int mBorderSize = 10;

  public ImageFilterParametricBorder()
  {
    setFilterType(1);
    this.mName = "Border";
  }

  public ImageFilterParametricBorder(int paramInt1, int paramInt2, int paramInt3)
  {
    setBorder(paramInt1, paramInt2, paramInt3);
    setFilterType(1);
  }

  public Bitmap apply(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    Canvas localCanvas = new Canvas(paramBitmap);
    Path localPath = new Path();
    localPath.moveTo(0.0F, 0.0F);
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    float f1 = this.mBorderSize / 100.0F * paramBitmap.getWidth();
    float f2 = this.mBorderCornerRadius / 100.0F * paramBitmap.getWidth();
    localPath.lineTo(0.0F, j);
    localPath.lineTo(i, j);
    localPath.lineTo(i, 0.0F);
    localPath.lineTo(0.0F, 0.0F);
    localPath.addRoundRect(new RectF(f1, f1, i - f1, j - f1), f2, f2, Path.Direction.CW);
    Paint localPaint = new Paint();
    localPaint.setAntiAlias(true);
    localPaint.setColor(this.mBorderColor);
    localCanvas.drawPath(localPath, localPaint);
    return paramBitmap;
  }

  public ImageFilter clone()
    throws CloneNotSupportedException
  {
    ImageFilterParametricBorder localImageFilterParametricBorder = (ImageFilterParametricBorder)super.clone();
    localImageFilterParametricBorder.setBorder(this.mBorderColor, this.mBorderSize, this.mBorderCornerRadius);
    return localImageFilterParametricBorder;
  }

  public boolean isNil()
  {
    return false;
  }

  public boolean same(ImageFilter paramImageFilter)
  {
    if (!super.same(paramImageFilter));
    ImageFilterParametricBorder localImageFilterParametricBorder;
    do
    {
      return false;
      localImageFilterParametricBorder = (ImageFilterParametricBorder)paramImageFilter;
    }
    while ((localImageFilterParametricBorder.mBorderColor != this.mBorderColor) || (localImageFilterParametricBorder.mBorderSize != this.mBorderSize) || (localImageFilterParametricBorder.mBorderCornerRadius != this.mBorderCornerRadius));
    return true;
  }

  public void setBorder(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mBorderColor = paramInt1;
    this.mBorderSize = paramInt2;
    this.mBorderCornerRadius = paramInt3;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ImageFilterParametricBorder
 * JD-Core Version:    0.5.4
 */