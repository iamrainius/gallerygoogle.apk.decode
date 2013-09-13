package com.android.gallery3d.common;

public class Scroller
{
  private static float ALPHA;
  private static float DECELERATION_RATE = (float)(Math.log(0.75D) / Math.log(0.9D));
  private static float END_TENSION;
  private static final float[] SPLINE;
  private static float START_TENSION;
  private static float sViscousFluidNormalize;
  private static float sViscousFluidScale;

  static
  {
    ALPHA = 800.0F;
    START_TENSION = 0.4F;
    END_TENSION = 1.0F - START_TENSION;
    SPLINE = new float[101];
    float f1 = 0.0F;
    int i = 0;
    if (i <= 100)
    {
      float f2 = i / 100.0F;
      float f3 = 1.0F;
      while (true)
      {
        float f4 = f1 + (f3 - f1) / 2.0F;
        float f5 = 3.0F * f4 * (1.0F - f4);
        float f6 = f5 * ((1.0F - f4) * START_TENSION + f4 * END_TENSION) + f4 * (f4 * f4);
        if (Math.abs(f6 - f2) < 1.E-005D)
        {
          float f7 = f5 + f4 * (f4 * f4);
          SPLINE[i] = f7;
          ++i;
        }
        if (f6 > f2)
          f3 = f4;
        f1 = f4;
      }
    }
    SPLINE[100] = 1.0F;
    sViscousFluidScale = 8.0F;
    sViscousFluidNormalize = 1.0F;
    sViscousFluidNormalize = 1.0F / viscousFluid(1.0F);
  }

  static float viscousFluid(float paramFloat)
  {
    float f1 = paramFloat * sViscousFluidScale;
    float f2;
    if (f1 < 1.0F)
      f2 = f1 - (1.0F - (float)Math.exp(-f1));
    while (true)
    {
      return f2 * sViscousFluidNormalize;
      f2 = 0.3678795F + (1.0F - (float)Math.exp(1.0F - f1)) * (1.0F - 0.3678795F);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.common.Scroller
 * JD-Core Version:    0.5.4
 */