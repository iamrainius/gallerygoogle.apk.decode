package com.android.gallery3d.ui;

import android.graphics.Bitmap;
import com.android.gallery3d.common.Utils;

public class BitmapTexture extends UploadedTexture
{
  protected Bitmap mContentBitmap;

  public BitmapTexture(Bitmap paramBitmap)
  {
    this(paramBitmap, false);
  }

  public BitmapTexture(Bitmap paramBitmap, boolean paramBoolean)
  {
    super(paramBoolean);
    if ((paramBitmap != null) && (!paramBitmap.isRecycled()));
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      this.mContentBitmap = paramBitmap;
      return;
    }
  }

  public Bitmap getBitmap()
  {
    return this.mContentBitmap;
  }

  protected void onFreeBitmap(Bitmap paramBitmap)
  {
  }

  protected Bitmap onGetBitmap()
  {
    return this.mContentBitmap;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.BitmapTexture
 * JD-Core Version:    0.5.4
 */