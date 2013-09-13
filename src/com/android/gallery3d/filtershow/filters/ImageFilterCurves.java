package com.android.gallery3d.filtershow.filters;

import android.graphics.Bitmap;
import com.android.gallery3d.filtershow.ui.Spline;

public class ImageFilterCurves extends ImageFilter
{
  private final Spline[] mSplines = new Spline[4];

  public ImageFilterCurves()
  {
    this.mName = "Curves";
    reset();
  }

  public Bitmap apply(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    if (!this.mSplines[0].isOriginal())
    {
      int[] arrayOfInt4 = new int[256];
      populateArray(arrayOfInt4, 0);
      nativeApplyGradientFilter(paramBitmap, paramBitmap.getWidth(), paramBitmap.getHeight(), arrayOfInt4, arrayOfInt4, arrayOfInt4);
    }
    boolean bool1 = this.mSplines[1].isOriginal();
    int[] arrayOfInt1 = null;
    if (!bool1)
    {
      arrayOfInt1 = new int[256];
      populateArray(arrayOfInt1, 1);
    }
    boolean bool2 = this.mSplines[2].isOriginal();
    int[] arrayOfInt2 = null;
    if (!bool2)
    {
      arrayOfInt2 = new int[256];
      populateArray(arrayOfInt2, 2);
    }
    boolean bool3 = this.mSplines[3].isOriginal();
    int[] arrayOfInt3 = null;
    if (!bool3)
    {
      arrayOfInt3 = new int[256];
      populateArray(arrayOfInt3, 3);
    }
    nativeApplyGradientFilter(paramBitmap, paramBitmap.getWidth(), paramBitmap.getHeight(), arrayOfInt1, arrayOfInt2, arrayOfInt3);
    return paramBitmap;
  }

  public ImageFilter clone()
    throws CloneNotSupportedException
  {
    ImageFilterCurves localImageFilterCurves = (ImageFilterCurves)super.clone();
    for (int i = 0; i < 4; ++i)
    {
      if (this.mSplines[i] == null)
        continue;
      localImageFilterCurves.setSpline(this.mSplines[i], i);
    }
    return localImageFilterCurves;
  }

  public Spline getSpline(int paramInt)
  {
    return this.mSplines[paramInt];
  }

  public boolean isNil()
  {
    for (int i = 0; i < 4; ++i)
      if ((this.mSplines[i] != null) && (!this.mSplines[i].isOriginal()))
        return false;
    return true;
  }

  public void populateArray(int[] paramArrayOfInt, int paramInt)
  {
    Spline localSpline = this.mSplines[paramInt];
    if (localSpline == null)
      return;
    float[] arrayOfFloat = localSpline.getAppliedCurve();
    for (int i = 0; ; ++i)
    {
      if (i < 256);
      paramArrayOfInt[i] = (int)(255.0F * arrayOfFloat[i]);
    }
  }

  public void reset()
  {
    Spline localSpline = new Spline();
    localSpline.addPoint(0.0F, 1.0F);
    localSpline.addPoint(1.0F, 0.0F);
    for (int i = 0; i < 4; ++i)
      this.mSplines[i] = new Spline(localSpline);
  }

  public boolean same(ImageFilter paramImageFilter)
  {
    if (!super.same(paramImageFilter))
      return false;
    ImageFilterCurves localImageFilterCurves = (ImageFilterCurves)paramImageFilter;
    for (int i = 0; i < 4; ++i)
      if (this.mSplines[i] != localImageFilterCurves.mSplines[i]);
    return true;
  }

  public void setSpline(Spline paramSpline, int paramInt)
  {
    this.mSplines[paramInt] = new Spline(paramSpline);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ImageFilterCurves
 * JD-Core Version:    0.5.4
 */