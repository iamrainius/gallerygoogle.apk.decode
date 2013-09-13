package com.android.gallery3d.filtershow.filters;

import android.graphics.Bitmap;

public class ImageFilterExposure extends ImageFilter
{
  public ImageFilterExposure()
  {
    this.mName = "Exposure";
  }

  public Bitmap apply(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    nativeApplyFilter(paramBitmap, paramBitmap.getWidth(), paramBitmap.getHeight(), this.mParameter);
    return paramBitmap;
  }

  protected native void nativeApplyFilter(Bitmap paramBitmap, int paramInt1, int paramInt2, float paramFloat);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ImageFilterExposure
 * JD-Core Version:    0.5.4
 */