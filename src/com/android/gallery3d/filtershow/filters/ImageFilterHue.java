package com.android.gallery3d.filtershow.filters;

import android.graphics.Bitmap;

public class ImageFilterHue extends ImageFilter
{
  private ColorSpaceMatrix cmatrix = null;

  public ImageFilterHue()
  {
    this.mName = "Hue";
    this.cmatrix = new ColorSpaceMatrix();
    this.mMaxParameter = 180;
    this.mMinParameter = -180;
  }

  public Bitmap apply(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    float f = this.mParameter;
    this.cmatrix.identity();
    this.cmatrix.setHue(f);
    nativeApplyFilter(paramBitmap, i, j, this.cmatrix.getMatrix());
    return paramBitmap;
  }

  public ImageFilter clone()
    throws CloneNotSupportedException
  {
    ImageFilterHue localImageFilterHue = (ImageFilterHue)super.clone();
    localImageFilterHue.cmatrix = new ColorSpaceMatrix(this.cmatrix);
    return localImageFilterHue;
  }

  protected native void nativeApplyFilter(Bitmap paramBitmap, int paramInt1, int paramInt2, float[] paramArrayOfFloat);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ImageFilterHue
 * JD-Core Version:    0.5.4
 */