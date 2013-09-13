package com.android.gallery3d.filtershow.cache;

import android.graphics.Bitmap;
import com.android.gallery3d.filtershow.imageshow.ImageShow;
import com.android.gallery3d.filtershow.presets.ImagePreset;

public abstract interface Cache
{
  public abstract void addObserver(ImageShow paramImageShow);

  public abstract Bitmap get(ImagePreset paramImagePreset);

  public abstract void prepare(ImagePreset paramImagePreset);

  public abstract void reset(ImagePreset paramImagePreset);

  public abstract void setOriginalBitmap(Bitmap paramBitmap);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.cache.Cache
 * JD-Core Version:    0.5.4
 */