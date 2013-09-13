package com.android.gallery3d.filtershow.filters;

import android.graphics.Bitmap;

public class ImageFilterRedEye extends ImageFilter
{
  public ImageFilterRedEye()
  {
    this.mName = "Redeye";
  }

  public Bitmap apply(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    float f = this.mParameter;
    int k = Math.min(i, j);
    int l = Math.min((int)((f + 100.0F) * k / 400.0F), i / 2);
    int i1 = Math.min((int)((f + 100.0F) * k / 800.0F), j / 2);
    short[] arrayOfShort = new short[4];
    arrayOfShort[0] = (short)(i / 2 - l);
    arrayOfShort[1] = (short)(i / 2 - i1);
    arrayOfShort[2] = (short)(l * 2);
    arrayOfShort[3] = (short)(i1 * 2);
    nativeApplyFilter(paramBitmap, i, j, arrayOfShort);
    return paramBitmap;
  }

  public ImageFilter clone()
    throws CloneNotSupportedException
  {
    return (ImageFilterRedEye)super.clone();
  }

  protected native void nativeApplyFilter(Bitmap paramBitmap, int paramInt1, int paramInt2, short[] paramArrayOfShort);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ImageFilterRedEye
 * JD-Core Version:    0.5.4
 */