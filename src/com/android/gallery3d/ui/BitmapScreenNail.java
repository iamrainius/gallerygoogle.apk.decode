package com.android.gallery3d.ui;

import android.graphics.Bitmap;
import android.graphics.RectF;

public class BitmapScreenNail
  implements ScreenNail
{
  private final BitmapTexture mBitmapTexture;

  public BitmapScreenNail(Bitmap paramBitmap)
  {
    this.mBitmapTexture = new BitmapTexture(paramBitmap);
  }

  public void draw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mBitmapTexture.draw(paramGLCanvas, paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public void draw(GLCanvas paramGLCanvas, RectF paramRectF1, RectF paramRectF2)
  {
    paramGLCanvas.drawTexture(this.mBitmapTexture, paramRectF1, paramRectF2);
  }

  public int getHeight()
  {
    return this.mBitmapTexture.getHeight();
  }

  public int getWidth()
  {
    return this.mBitmapTexture.getWidth();
  }

  public void noDraw()
  {
  }

  public void recycle()
  {
    this.mBitmapTexture.recycle();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.BitmapScreenNail
 * JD-Core Version:    0.5.4
 */