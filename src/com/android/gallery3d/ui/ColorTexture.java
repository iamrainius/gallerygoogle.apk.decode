package com.android.gallery3d.ui;

import com.android.gallery3d.common.Utils;

public class ColorTexture
  implements Texture
{
  private final int mColor;
  private int mHeight;
  private int mWidth;

  public ColorTexture(int paramInt)
  {
    this.mColor = paramInt;
    this.mWidth = 1;
    this.mHeight = 1;
  }

  public void draw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2)
  {
    draw(paramGLCanvas, paramInt1, paramInt2, this.mWidth, this.mHeight);
  }

  public void draw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramGLCanvas.fillRect(paramInt1, paramInt2, paramInt3, paramInt4, this.mColor);
  }

  public int getHeight()
  {
    return this.mHeight;
  }

  public int getWidth()
  {
    return this.mWidth;
  }

  public boolean isOpaque()
  {
    return Utils.isOpaque(this.mColor);
  }

  public void setSize(int paramInt1, int paramInt2)
  {
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.ColorTexture
 * JD-Core Version:    0.5.4
 */