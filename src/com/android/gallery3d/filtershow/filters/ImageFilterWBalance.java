package com.android.gallery3d.filtershow.filters;

import android.graphics.Bitmap;

public class ImageFilterWBalance extends ImageFilter
{
  public ImageFilterWBalance()
  {
    setFilterType(3);
    this.mName = "WBalance";
  }

  public Bitmap apply(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    nativeApplyFilter(paramBitmap, paramBitmap.getWidth(), paramBitmap.getHeight(), -1, -1);
    return paramBitmap;
  }

  protected native void nativeApplyFilter(Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ImageFilterWBalance
 * JD-Core Version:    0.5.4
 */