package com.android.gallery3d.ui;

import javax.microedition.khronos.opengles.GL11;

public class ExtTexture extends BasicTexture
{
  private static float[] sCropRect;
  private static int[] sTextureId = new int[1];
  private int mTarget;

  static
  {
    sCropRect = new float[4];
  }

  public ExtTexture(int paramInt)
  {
    GLId.glGenTextures(1, sTextureId, 0);
    this.mId = sTextureId[0];
    this.mTarget = paramInt;
  }

  private void uploadToCanvas(GLCanvas paramGLCanvas)
  {
    GL11 localGL11 = paramGLCanvas.getGLInstance();
    int i = getWidth();
    int j = getHeight();
    sCropRect[0] = 0.0F;
    sCropRect[1] = j;
    sCropRect[2] = i;
    sCropRect[3] = (-j);
    localGL11.glBindTexture(this.mTarget, this.mId);
    localGL11.glTexParameterfv(this.mTarget, 35741, sCropRect, 0);
    localGL11.glTexParameteri(this.mTarget, 10242, 33071);
    localGL11.glTexParameteri(this.mTarget, 10243, 33071);
    localGL11.glTexParameterf(this.mTarget, 10241, 9729.0F);
    localGL11.glTexParameterf(this.mTarget, 10240, 9729.0F);
    setAssociatedCanvas(paramGLCanvas);
    this.mState = 1;
  }

  public int getTarget()
  {
    return this.mTarget;
  }

  public boolean isOpaque()
  {
    return true;
  }

  protected boolean onBind(GLCanvas paramGLCanvas)
  {
    if (!isLoaded())
      uploadToCanvas(paramGLCanvas);
    return true;
  }

  public void yield()
  {
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.ExtTexture
 * JD-Core Version:    0.5.4
 */