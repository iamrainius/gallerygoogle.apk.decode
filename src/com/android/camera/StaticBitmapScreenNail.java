package com.android.camera;

import android.graphics.Bitmap;
import com.android.gallery3d.ui.BitmapScreenNail;

public class StaticBitmapScreenNail extends BitmapScreenNail
{
  public StaticBitmapScreenNail(Bitmap paramBitmap)
  {
    super(paramBitmap);
  }

  public void recycle()
  {
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.StaticBitmapScreenNail
 * JD-Core Version:    0.5.4
 */