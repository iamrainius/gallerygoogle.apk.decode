package com.android.gallery3d.ui;

import javax.microedition.khronos.opengles.GL11;

public class RawTexture extends BasicTexture
{
  private static final float[] sCropRect;
  private static final int[] sTextureId = new int[1];
  private final boolean mOpaque;

  static
  {
    sCropRect = new float[4];
  }

  public RawTexture(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.mOpaque = paramBoolean;
    setSize(paramInt1, paramInt2);
  }

  protected int getTarget()
  {
    return 3553;
  }

  public boolean isOpaque()
  {
    return this.mOpaque;
  }

  protected boolean onBind(GLCanvas paramGLCanvas)
  {
    if (isLoaded())
      return true;
    Log.w("RawTexture", "lost the content due to context change");
    return false;
  }

  protected void prepare(GLCanvas paramGLCanvas)
  {
    GL11 localGL11 = paramGLCanvas.getGLInstance();
    sCropRect[0] = 0.0F;
    sCropRect[1] = this.mHeight;
    sCropRect[2] = this.mWidth;
    sCropRect[3] = (-this.mHeight);
    GLId.glGenTextures(1, sTextureId, 0);
    localGL11.glBindTexture(3553, sTextureId[0]);
    localGL11.glTexParameterfv(3553, 35741, sCropRect, 0);
    localGL11.glTexParameteri(3553, 10242, 33071);
    localGL11.glTexParameteri(3553, 10243, 33071);
    localGL11.glTexParameterf(3553, 10241, 9729.0F);
    localGL11.glTexParameterf(3553, 10240, 9729.0F);
    localGL11.glTexImage2D(3553, 0, 6408, getTextureWidth(), getTextureHeight(), 0, 6408, 5121, null);
    this.mId = sTextureId[0];
    this.mState = 1;
    setAssociatedCanvas(paramGLCanvas);
  }

  public void yield()
  {
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.RawTexture
 * JD-Core Version:    0.5.4
 */