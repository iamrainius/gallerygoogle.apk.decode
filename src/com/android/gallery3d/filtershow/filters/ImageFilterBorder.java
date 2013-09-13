package com.android.gallery3d.filtershow.filters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class ImageFilterBorder extends ImageFilter
{
  Drawable mNinePatch = null;

  public ImageFilterBorder(Drawable paramDrawable)
  {
    setFilterType(1);
    this.mName = "Border";
    this.mNinePatch = paramDrawable;
  }

  public Bitmap apply(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    if (this.mNinePatch == null)
      return paramBitmap;
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    float f = paramFloat * 2.0F;
    Rect localRect = new Rect(0, 0, (int)(i / f), (int)(j / f));
    Canvas localCanvas = new Canvas(paramBitmap);
    localCanvas.scale(f, f);
    this.mNinePatch.setBounds(localRect);
    this.mNinePatch.draw(localCanvas);
    return paramBitmap;
  }

  public ImageFilter clone()
    throws CloneNotSupportedException
  {
    ImageFilterBorder localImageFilterBorder = (ImageFilterBorder)super.clone();
    localImageFilterBorder.setDrawable(this.mNinePatch);
    return localImageFilterBorder;
  }

  public boolean isNil()
  {
    return this.mNinePatch == null;
  }

  public boolean same(ImageFilter paramImageFilter)
  {
    if (!super.same(paramImageFilter));
    ImageFilterBorder localImageFilterBorder;
    do
    {
      return false;
      localImageFilterBorder = (ImageFilterBorder)paramImageFilter;
    }
    while (this.mNinePatch != localImageFilterBorder.mNinePatch);
    return true;
  }

  public void setDrawable(Drawable paramDrawable)
  {
    this.mNinePatch = paramDrawable;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ImageFilterBorder
 * JD-Core Version:    0.5.4
 */