package com.android.gallery3d.filtershow.filters;

import java.util.Arrays;

public class ColorSpaceMatrix
{
  private final float[] mMatrix = new float[16];

  public ColorSpaceMatrix()
  {
    identity();
  }

  public ColorSpaceMatrix(ColorSpaceMatrix paramColorSpaceMatrix)
  {
    System.arraycopy(paramColorSpaceMatrix.mMatrix, 0, this.mMatrix, 0, paramColorSpaceMatrix.mMatrix.length);
  }

  private float getBluef(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return paramFloat1 * this.mMatrix[2] + paramFloat2 * this.mMatrix[6] + paramFloat3 * this.mMatrix[10] + this.mMatrix[14];
  }

  private float getGreenf(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return paramFloat1 * this.mMatrix[1] + paramFloat2 * this.mMatrix[5] + paramFloat3 * this.mMatrix[9] + this.mMatrix[13];
  }

  private float getRedf(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return paramFloat1 * this.mMatrix[0] + paramFloat2 * this.mMatrix[4] + paramFloat3 * this.mMatrix[8] + this.mMatrix[12];
  }

  private void multiply(float[] paramArrayOfFloat)
  {
    float[] arrayOfFloat = new float[16];
    for (int i = 0; i < 4; ++i)
    {
      int k = i * 4;
      for (int l = 0; l < 4; ++l)
        arrayOfFloat[(k + l)] = (this.mMatrix[(k + 0)] * paramArrayOfFloat[l] + this.mMatrix[(k + 1)] * paramArrayOfFloat[(l + 4)] + this.mMatrix[(k + 2)] * paramArrayOfFloat[(l + 8)] + this.mMatrix[(k + 3)] * paramArrayOfFloat[(l + 12)]);
    }
    for (int j = 0; j < 16; ++j)
      this.mMatrix[j] = arrayOfFloat[j];
  }

  private void xRotateMatrix(float paramFloat1, float paramFloat2)
  {
    float[] arrayOfFloat = new ColorSpaceMatrix().mMatrix;
    arrayOfFloat[5] = paramFloat2;
    arrayOfFloat[6] = paramFloat1;
    arrayOfFloat[9] = (-paramFloat1);
    arrayOfFloat[10] = paramFloat2;
    multiply(arrayOfFloat);
  }

  private void yRotateMatrix(float paramFloat1, float paramFloat2)
  {
    float[] arrayOfFloat = new ColorSpaceMatrix().mMatrix;
    arrayOfFloat[0] = paramFloat2;
    arrayOfFloat[2] = (-paramFloat1);
    arrayOfFloat[8] = paramFloat1;
    arrayOfFloat[10] = paramFloat2;
    multiply(arrayOfFloat);
  }

  private void zRotateMatrix(float paramFloat1, float paramFloat2)
  {
    float[] arrayOfFloat = new ColorSpaceMatrix().mMatrix;
    arrayOfFloat[0] = paramFloat2;
    arrayOfFloat[1] = paramFloat1;
    arrayOfFloat[4] = (-paramFloat1);
    arrayOfFloat[5] = paramFloat2;
    multiply(arrayOfFloat);
  }

  private void zShearMatrix(float paramFloat1, float paramFloat2)
  {
    float[] arrayOfFloat = new ColorSpaceMatrix().mMatrix;
    arrayOfFloat[2] = paramFloat1;
    arrayOfFloat[6] = paramFloat2;
    multiply(arrayOfFloat);
  }

  public float[] getMatrix()
  {
    return this.mMatrix;
  }

  public void identity()
  {
    Arrays.fill(this.mMatrix, 0.0F);
    float[] arrayOfFloat1 = this.mMatrix;
    float[] arrayOfFloat2 = this.mMatrix;
    float[] arrayOfFloat3 = this.mMatrix;
    this.mMatrix[15] = 1.0F;
    arrayOfFloat3[10] = 1.0F;
    arrayOfFloat2[5] = 1.0F;
    arrayOfFloat1[0] = 1.0F;
  }

  public void setHue(float paramFloat)
  {
    float f1 = (float)Math.sqrt(2.0D);
    float f2 = 1.0F / f1;
    float f3 = 1.0F / f1;
    xRotateMatrix(f2, f3);
    float f4 = (float)Math.sqrt(3.0D);
    float f5 = -1.0F / f4;
    float f6 = (float)Math.sqrt(2.0D) / f4;
    yRotateMatrix(f5, f6);
    float f7 = getRedf(0.3086F, 0.6094F, 0.082F);
    float f8 = getGreenf(0.3086F, 0.6094F, 0.082F);
    float f9 = getBluef(0.3086F, 0.6094F, 0.082F);
    float f10 = f7 / f9;
    float f11 = f8 / f9;
    zShearMatrix(f10, f11);
    zRotateMatrix((float)Math.sin(3.141592653589793D * paramFloat / 180.0D), (float)Math.cos(3.141592653589793D * paramFloat / 180.0D));
    zShearMatrix(-f10, -f11);
    yRotateMatrix(-f5, f6);
    xRotateMatrix(-f2, f3);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ColorSpaceMatrix
 * JD-Core Version:    0.5.4
 */