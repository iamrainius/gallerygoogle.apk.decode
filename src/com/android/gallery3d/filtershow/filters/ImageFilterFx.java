package com.android.gallery3d.filtershow.filters;

import android.graphics.Bitmap;

public class ImageFilterFx extends ImageFilter
{
  Bitmap fxBitmap;

  public ImageFilterFx(Bitmap paramBitmap, String paramString)
  {
    setFilterType(2);
    this.mName = paramString;
    this.fxBitmap = paramBitmap;
  }

  public Bitmap apply(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    if (this.fxBitmap == null)
      return paramBitmap;
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    int k = this.fxBitmap.getWidth();
    int l = this.fxBitmap.getHeight();
    nativeApplyFilter(paramBitmap, i, j, this.fxBitmap, k, l);
    return paramBitmap;
  }

  public ImageFilter clone()
    throws CloneNotSupportedException
  {
    ImageFilterFx localImageFilterFx = (ImageFilterFx)super.clone();
    localImageFilterFx.fxBitmap = this.fxBitmap;
    return localImageFilterFx;
  }

  public boolean isNil()
  {
    return this.fxBitmap == null;
  }

  protected native void nativeApplyFilter(Bitmap paramBitmap1, int paramInt1, int paramInt2, Bitmap paramBitmap2, int paramInt3, int paramInt4);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ImageFilterFx
 * JD-Core Version:    0.5.4
 */