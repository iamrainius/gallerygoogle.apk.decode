package com.android.gallery3d.ui;

public class FadeInTexture extends FadeTexture
  implements Texture
{
  private final int mColor;
  private final TiledTexture mTexture;

  public FadeInTexture(int paramInt, TiledTexture paramTiledTexture)
  {
    super(paramTiledTexture.getWidth(), paramTiledTexture.getHeight(), paramTiledTexture.isOpaque());
    this.mColor = paramInt;
    this.mTexture = paramTiledTexture;
  }

  public void draw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (isAnimating())
    {
      this.mTexture.drawMixed(paramGLCanvas, this.mColor, getRatio(), paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    this.mTexture.draw(paramGLCanvas, paramInt1, paramInt2, paramInt3, paramInt4);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.FadeInTexture
 * JD-Core Version:    0.5.4
 */