package com.android.gallery3d.ui;

import android.graphics.RectF;
import javax.microedition.khronos.opengles.GL11;

public abstract interface GLCanvas
{
  public abstract void beginRenderTarget(RawTexture paramRawTexture);

  public abstract void clearBuffer();

  public abstract void clearBuffer(float[] paramArrayOfFloat);

  public abstract void deleteBuffer(int paramInt);

  public abstract void deleteRecycledResources();

  public abstract void drawMesh(BasicTexture paramBasicTexture, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);

  public abstract void drawMixed(BasicTexture paramBasicTexture, int paramInt, float paramFloat, RectF paramRectF1, RectF paramRectF2);

  public abstract void drawRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, GLPaint paramGLPaint);

  public abstract void drawTexture(BasicTexture paramBasicTexture, int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract void drawTexture(BasicTexture paramBasicTexture, RectF paramRectF1, RectF paramRectF2);

  public abstract void drawTexture(BasicTexture paramBasicTexture, float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract void endRenderTarget();

  public abstract void fillRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt);

  public abstract float getAlpha();

  public abstract GL11 getGLInstance();

  public abstract void multiplyAlpha(float paramFloat);

  public abstract void multiplyMatrix(float[] paramArrayOfFloat, int paramInt);

  public abstract void restore();

  public abstract void rotate(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);

  public abstract void save();

  public abstract void save(int paramInt);

  public abstract void scale(float paramFloat1, float paramFloat2, float paramFloat3);

  public abstract void setAlpha(float paramFloat);

  public abstract void setSize(int paramInt1, int paramInt2);

  public abstract void translate(float paramFloat1, float paramFloat2);

  public abstract void translate(float paramFloat1, float paramFloat2, float paramFloat3);

  public abstract boolean unloadTexture(BasicTexture paramBasicTexture);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.GLCanvas
 * JD-Core Version:    0.5.4
 */