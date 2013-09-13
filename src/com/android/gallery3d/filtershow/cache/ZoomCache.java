package com.android.gallery3d.filtershow.cache;

import android.graphics.Bitmap;
import android.graphics.Rect;
import com.android.gallery3d.filtershow.presets.ImagePreset;

public class ZoomCache
{
  private Bitmap mBitmap = null;
  private Rect mBounds = null;
  private ImagePreset mImagePreset = null;

  public Bitmap getImage(ImagePreset paramImagePreset, Rect paramRect)
  {
    if (this.mBounds != paramRect);
    do
      return null;
    while ((this.mImagePreset == null) || (!this.mImagePreset.same(paramImagePreset)));
    return this.mBitmap;
  }

  public void reset(ImagePreset paramImagePreset)
  {
    if (paramImagePreset != this.mImagePreset)
      return;
    this.mBitmap = null;
  }

  public void setImage(ImagePreset paramImagePreset, Rect paramRect, Bitmap paramBitmap)
  {
    this.mBitmap = paramBitmap;
    this.mBounds = paramRect;
    this.mImagePreset = paramImagePreset;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.cache.ZoomCache
 * JD-Core Version:    0.5.4
 */