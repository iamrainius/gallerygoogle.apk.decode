package com.android.gallery3d.ui;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;

abstract class CanvasTexture extends UploadedTexture
{
  protected Canvas mCanvas;
  private final Bitmap.Config mConfig = Bitmap.Config.ARGB_8888;

  public CanvasTexture(int paramInt1, int paramInt2)
  {
    setSize(paramInt1, paramInt2);
    setOpaque(false);
  }

  protected abstract void onDraw(Canvas paramCanvas, Bitmap paramBitmap);

  protected void onFreeBitmap(Bitmap paramBitmap)
  {
    if (inFinalizer())
      return;
    paramBitmap.recycle();
  }

  protected Bitmap onGetBitmap()
  {
    Bitmap localBitmap = Bitmap.createBitmap(this.mWidth, this.mHeight, this.mConfig);
    this.mCanvas = new Canvas(localBitmap);
    onDraw(this.mCanvas, localBitmap);
    return localBitmap;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.CanvasTexture
 * JD-Core Version:    0.5.4
 */