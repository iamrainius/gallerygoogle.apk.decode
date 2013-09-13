package com.google.android.apps.lightcycle.math;

public class Quaternion
{
  public double w;
  public double x;
  public double y;
  public double z;

  public static double dot(Quaternion paramQuaternion1, Quaternion paramQuaternion2)
  {
    return paramQuaternion1.x * paramQuaternion2.x + paramQuaternion1.y * paramQuaternion2.y + paramQuaternion1.z * paramQuaternion2.z + paramQuaternion1.w * paramQuaternion2.w;
  }

  public static void slerp(Quaternion paramQuaternion1, Quaternion paramQuaternion2, double paramDouble, Quaternion paramQuaternion3)
  {
    double d1 = dot(paramQuaternion1, paramQuaternion2);
    if ((d1 > 1.0D) || (d1 < -1.0D))
      paramQuaternion3.set(paramQuaternion2);
    double d2 = 1.0D;
    if (d1 < 0.0D)
    {
      d2 = -1.0D;
      d1 = -d1;
    }
    double d3 = Math.acos(d1);
    if (d3 <= 1.0E-006D)
    {
      paramQuaternion3.set(paramQuaternion2);
      return;
    }
    double d4 = 1.0D / Math.sin(d3);
    double d5 = d4 * Math.sin(d3 * (1.0D - paramDouble));
    double d6 = d4 * (d2 * Math.sin(paramDouble * d3));
    paramQuaternion3.x = (d5 * paramQuaternion1.x + d6 * paramQuaternion2.x);
    paramQuaternion3.y = (d5 * paramQuaternion1.y + d6 * paramQuaternion2.y);
    paramQuaternion3.z = (d5 * paramQuaternion1.z + d6 * paramQuaternion2.z);
    paramQuaternion3.w = (d5 * paramQuaternion1.w + d6 * paramQuaternion2.w);
  }

  public void fromRotationMatrix(float[] paramArrayOfFloat)
  {
    double d1 = paramArrayOfFloat[0];
    double d2 = paramArrayOfFloat[5];
    double d3 = paramArrayOfFloat[10];
    this.w = (0.5D * Math.sqrt(Math.max(0.0D, d3 + (d2 + (1.0D + d1)))));
    this.x = (0.5D * Math.sqrt(Math.max(0.0D, 1.0D + d1 - d2 - d3)));
    this.y = (0.5D * Math.sqrt(Math.max(0.0D, d2 + (1.0D - d1) - d3)));
    this.z = (0.5D * Math.sqrt(Math.max(0.0D, d3 + (1.0D - d1 - d2))));
    int i;
    label131: int j;
    label143: double d4;
    label157: int k;
    label179: int l;
    label191: double d5;
    label205: int i1;
    label226: int i2;
    label238: double d6;
    if (paramArrayOfFloat[6] - paramArrayOfFloat[9] < 0.0F)
    {
      i = 1;
      if (this.x >= 0.0D)
        break label265;
      j = 1;
      if (i == j)
        break label271;
      d4 = -this.x;
      this.x = d4;
      if (paramArrayOfFloat[8] - paramArrayOfFloat[2] >= 0.0F)
        break label280;
      k = 1;
      if (this.y >= 0.0D)
        break label286;
      l = 1;
      if (k == l)
        break label292;
      d5 = -this.y;
      this.y = d5;
      if (paramArrayOfFloat[1] - paramArrayOfFloat[4] >= 0.0F)
        break label301;
      i1 = 1;
      if (this.z >= 0.0D)
        break label307;
      i2 = 1;
      if (i1 == i2)
        break label313;
      d6 = -this.z;
    }
    while (true)
    {
      this.z = d6;
      return;
      i = 0;
      break label131:
      label265: j = 0;
      break label143:
      label271: d4 = this.x;
      break label157:
      label280: k = 0;
      break label179:
      label286: l = 0;
      break label191:
      label292: d5 = this.y;
      break label205:
      label301: i1 = 0;
      break label226:
      label307: i2 = 0;
      break label238:
      label313: d6 = this.z;
    }
  }

  public void set(Quaternion paramQuaternion)
  {
    this.x = paramQuaternion.x;
    this.y = paramQuaternion.y;
    this.z = paramQuaternion.z;
    this.w = paramQuaternion.w;
  }

  public void toRotationMatrix(float[] paramArrayOfFloat)
  {
    float f1 = (float)this.x;
    float f2 = (float)this.y;
    float f3 = (float)this.z;
    float f4 = (float)this.w;
    float f5 = 2.0F * f1;
    float f6 = 2.0F * f2;
    float f7 = 2.0F * f3;
    float f8 = f5 * f4;
    float f9 = f6 * f4;
    float f10 = f7 * f4;
    float f11 = f5 * f1;
    float f12 = f6 * f1;
    float f13 = f7 * f1;
    float f14 = f6 * f2;
    float f15 = f7 * f2;
    float f16 = f7 * f3;
    paramArrayOfFloat[0] = (1.0F - (f14 + f16));
    paramArrayOfFloat[1] = (f12 + f10);
    paramArrayOfFloat[2] = (f13 - f9);
    paramArrayOfFloat[3] = 0.0F;
    paramArrayOfFloat[4] = (f12 - f10);
    paramArrayOfFloat[5] = (1.0F - (f11 + f16));
    paramArrayOfFloat[6] = (f15 + f8);
    paramArrayOfFloat[7] = 0.0F;
    paramArrayOfFloat[8] = (f13 + f9);
    paramArrayOfFloat[9] = (f15 - f8);
    paramArrayOfFloat[10] = (1.0F - (f11 + f14));
    paramArrayOfFloat[11] = 0.0F;
    paramArrayOfFloat[12] = 0.0F;
    paramArrayOfFloat[13] = 0.0F;
    paramArrayOfFloat[14] = 0.0F;
    paramArrayOfFloat[15] = 1.0F;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.math.Quaternion
 * JD-Core Version:    0.5.4
 */