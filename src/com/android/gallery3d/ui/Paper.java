package com.android.gallery3d.ui;

import android.graphics.Rect;
import android.opengl.Matrix;

class Paper
{
  private EdgeAnimation mAnimationLeft = new EdgeAnimation();
  private EdgeAnimation mAnimationRight = new EdgeAnimation();
  private float[] mMatrix = new float[16];
  private int mWidth;

  public boolean advanceAnimation()
  {
    return this.mAnimationLeft.update() | this.mAnimationRight.update();
  }

  public void edgeReached(float paramFloat)
  {
    float f = paramFloat / this.mWidth;
    if (f < 0.0F)
    {
      this.mAnimationRight.onAbsorb(-f);
      return;
    }
    this.mAnimationLeft.onAbsorb(f);
  }

  public float[] getTransform(Rect paramRect, float paramFloat)
  {
    float f1 = this.mAnimationLeft.getValue();
    float f2 = this.mAnimationRight.getValue();
    float f3 = paramRect.centerX() - paramFloat + this.mWidth / 4;
    int i = 3 * this.mWidth / 2;
    float f4 = -45.0F * (2.0F * (1.0F / (1.0F + (float)Math.exp(4.0F * -((f1 * (i - f3) - f3 * f2) / i))) - 0.5F));
    Matrix.setIdentityM(this.mMatrix, 0);
    Matrix.translateM(this.mMatrix, 0, this.mMatrix, 0, paramRect.centerX(), paramRect.centerY(), 0.0F);
    Matrix.rotateM(this.mMatrix, 0, f4, 0.0F, 1.0F, 0.0F);
    Matrix.translateM(this.mMatrix, 0, this.mMatrix, 0, -paramRect.width() / 2, -paramRect.height() / 2, 0.0F);
    return this.mMatrix;
  }

  public void onRelease()
  {
    this.mAnimationLeft.onRelease();
    this.mAnimationRight.onRelease();
  }

  public void overScroll(float paramFloat)
  {
    float f = paramFloat / this.mWidth;
    if (f < 0.0F)
    {
      this.mAnimationLeft.onPull(-f);
      return;
    }
    this.mAnimationRight.onPull(f);
  }

  public void setSize(int paramInt1, int paramInt2)
  {
    this.mWidth = paramInt1;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.Paper
 * JD-Core Version:    0.5.4
 */