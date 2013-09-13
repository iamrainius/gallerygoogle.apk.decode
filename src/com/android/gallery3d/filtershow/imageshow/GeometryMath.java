package com.android.gallery3d.filtershow.imageshow;

import android.graphics.RectF;

public class GeometryMath
{
  public static float clamp(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return Math.max(Math.min(paramFloat1, paramFloat3), paramFloat2);
  }

  public static float dotProduct(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    return paramArrayOfFloat1[0] * paramArrayOfFloat2[0] + paramArrayOfFloat1[1] * paramArrayOfFloat2[1];
  }

  public static float[] normalize(float[] paramArrayOfFloat)
  {
    float f = (float)Math.sqrt(paramArrayOfFloat[0] * paramArrayOfFloat[0] + paramArrayOfFloat[1] * paramArrayOfFloat[1]);
    float[] arrayOfFloat = new float[2];
    arrayOfFloat[0] = (paramArrayOfFloat[0] / f);
    arrayOfFloat[1] = (paramArrayOfFloat[1] / f);
    return arrayOfFloat;
  }

  public static float scalarProjection(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    float f = (float)Math.sqrt(paramArrayOfFloat2[0] * paramArrayOfFloat2[0] + paramArrayOfFloat2[1] * paramArrayOfFloat2[1]);
    return dotProduct(paramArrayOfFloat1, paramArrayOfFloat2) / f;
  }

  public static float scale(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if ((paramFloat2 == 0.0F) || (paramFloat1 == 0.0F))
      return 1.0F;
    return Math.min(paramFloat3 / paramFloat1, paramFloat4 / paramFloat2);
  }

  public static RectF scaleRect(RectF paramRectF, float paramFloat)
  {
    return new RectF(paramFloat * paramRectF.left, paramFloat * paramRectF.top, paramFloat * paramRectF.right, paramFloat * paramRectF.bottom);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.imageshow.GeometryMath
 * JD-Core Version:    0.5.4
 */