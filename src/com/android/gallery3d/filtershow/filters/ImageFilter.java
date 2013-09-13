package com.android.gallery3d.filtershow.filters;

import android.graphics.Bitmap;
import com.android.gallery3d.filtershow.presets.ImagePreset;

public class ImageFilter
  implements Cloneable
{
  private final String LOGTAG = "ImageFilter";
  private byte filterType = 5;
  protected int mDefaultParameter = 0;
  private ImagePreset mImagePreset;
  protected int mMaxParameter = 100;
  protected int mMinParameter = -100;
  protected String mName = "Original";
  protected int mParameter = 0;
  protected int mPreviewParameter = this.mMaxParameter;

  public Bitmap apply(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    return paramBitmap;
  }

  public ImageFilter clone()
    throws CloneNotSupportedException
  {
    ImageFilter localImageFilter = (ImageFilter)super.clone();
    localImageFilter.setName(getName());
    localImageFilter.setParameter(getParameter());
    localImageFilter.setFilterType(this.filterType);
    localImageFilter.mMaxParameter = this.mMaxParameter;
    localImageFilter.mMinParameter = this.mMinParameter;
    localImageFilter.mImagePreset = this.mImagePreset;
    localImageFilter.mDefaultParameter = this.mDefaultParameter;
    localImageFilter.mPreviewParameter = this.mPreviewParameter;
    return localImageFilter;
  }

  public int getDefaultParameter()
  {
    return this.mDefaultParameter;
  }

  public byte getFilterType()
  {
    return this.filterType;
  }

  public ImagePreset getImagePreset()
  {
    return this.mImagePreset;
  }

  public int getMaxParameter()
  {
    return this.mMaxParameter;
  }

  public int getMinParameter()
  {
    return this.mMinParameter;
  }

  public String getName()
  {
    return this.mName;
  }

  public int getParameter()
  {
    return this.mParameter;
  }

  public int getPreviewParameter()
  {
    return this.mPreviewParameter;
  }

  public boolean isNil()
  {
    return this.mParameter == this.mDefaultParameter;
  }

  protected native void nativeApplyGradientFilter(Bitmap paramBitmap, int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3);

  public boolean same(ImageFilter paramImageFilter)
  {
    return paramImageFilter.getName().equalsIgnoreCase(getName());
  }

  protected void setFilterType(byte paramByte)
  {
    this.filterType = paramByte;
  }

  public void setImagePreset(ImagePreset paramImagePreset)
  {
    this.mImagePreset = paramImagePreset;
  }

  public void setName(String paramString)
  {
    this.mName = paramString;
  }

  public void setParameter(int paramInt)
  {
    this.mParameter = paramInt;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ImageFilter
 * JD-Core Version:    0.5.4
 */