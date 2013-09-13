package com.android.gallery3d.ui;

public abstract interface Texture
{
  public abstract void draw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2);

  public abstract void draw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract int getHeight();

  public abstract int getWidth();

  public abstract boolean isOpaque();
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.Texture
 * JD-Core Version:    0.5.4
 */