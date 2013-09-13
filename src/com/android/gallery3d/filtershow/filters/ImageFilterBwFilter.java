package com.android.gallery3d.filtershow.filters;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ImageFilterBwFilter extends ImageFilter
{
  public ImageFilterBwFilter()
  {
    this.mName = "BW Filter";
    this.mMaxParameter = 180;
    this.mMinParameter = -180;
  }

  public Bitmap apply(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    float[] arrayOfFloat = new float[3];
    arrayOfFloat[0] = (180 + this.mParameter);
    arrayOfFloat[1] = 1.0F;
    arrayOfFloat[2] = 1.0F;
    int k = Color.HSVToColor(arrayOfFloat);
    nativeApplyFilter(paramBitmap, i, j, 0xFF & k >> 16, 0xFF & k >> 8, 0xFF & k >> 0);
    return paramBitmap;
  }

  public ImageFilter clone()
    throws CloneNotSupportedException
  {
    return (ImageFilterBwFilter)super.clone();
  }

  protected native void nativeApplyFilter(Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ImageFilterBwFilter
 * JD-Core Version:    0.5.4
 */