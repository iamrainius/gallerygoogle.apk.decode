package com.android.gallery3d.filtershow.filters;

import android.graphics.Bitmap;

public class ImageFilterSaturated extends ImageFilter
{
  public ImageFilterSaturated()
  {
    this.mName = "Saturated";
  }

  public Bitmap apply(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    nativeApplyFilter(paramBitmap, paramBitmap.getWidth(), paramBitmap.getHeight(), 1.0F + this.mParameter / 100.0F);
    return paramBitmap;
  }

  protected native void nativeApplyFilter(Bitmap paramBitmap, int paramInt1, int paramInt2, float paramFloat);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ImageFilterSaturated
 * JD-Core Version:    0.5.4
 */