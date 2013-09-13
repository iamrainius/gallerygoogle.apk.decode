package com.android.gallery3d.ui;

public class FadeOutTexture extends FadeTexture
{
  private final BasicTexture mTexture;

  public FadeOutTexture(BasicTexture paramBasicTexture)
  {
    super(paramBasicTexture.getWidth(), paramBasicTexture.getHeight(), paramBasicTexture.isOpaque());
    this.mTexture = paramBasicTexture;
  }

  public void draw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!isAnimating())
      return;
    paramGLCanvas.save(1);
    paramGLCanvas.setAlpha(getRatio());
    this.mTexture.draw(paramGLCanvas, paramInt1, paramInt2, paramInt3, paramInt4);
    paramGLCanvas.restore();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.FadeOutTexture
 * JD-Core Version:    0.5.4
 */