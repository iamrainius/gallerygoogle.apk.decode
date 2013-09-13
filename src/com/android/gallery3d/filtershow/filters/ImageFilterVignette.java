package com.android.gallery3d.filtershow.filters;

import android.graphics.Bitmap;

public class ImageFilterVignette extends ImageFilter
{
  public ImageFilterVignette()
  {
    setFilterType(4);
    this.mName = "Vignette";
  }

  public Bitmap apply(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    nativeApplyFilter(paramBitmap, paramBitmap.getWidth(), paramBitmap.getHeight(), this.mParameter / 100.0F);
    return paramBitmap;
  }

  protected native void nativeApplyFilter(Bitmap paramBitmap, int paramInt1, int paramInt2, float paramFloat);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ImageFilterVignette
 * JD-Core Version:    0.5.4
 */