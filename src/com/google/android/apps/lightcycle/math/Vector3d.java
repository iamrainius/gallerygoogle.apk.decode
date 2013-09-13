package com.google.android.apps.lightcycle.math;

public class Vector3d
{
  public double x;
  public double y;
  public double z;

  public static void cross(Vector3d paramVector3d1, Vector3d paramVector3d2, Vector3d paramVector3d3)
  {
    paramVector3d3.set(paramVector3d1.y * paramVector3d2.z - paramVector3d1.z * paramVector3d2.y, paramVector3d1.z * paramVector3d2.x - paramVector3d1.x * paramVector3d2.z, paramVector3d1.x * paramVector3d2.y - paramVector3d1.y * paramVector3d2.x);
  }

  public static double dot(Vector3d paramVector3d1, Vector3d paramVector3d2)
  {
    return paramVector3d1.x * paramVector3d2.x + paramVector3d1.y * paramVector3d2.y + paramVector3d1.z * paramVector3d2.z;
  }

  public static void sub(Vector3d paramVector3d1, Vector3d paramVector3d2, Vector3d paramVector3d3)
  {
    paramVector3d3.set(paramVector3d1.x - paramVector3d2.x, paramVector3d1.y - paramVector3d2.y, paramVector3d1.z - paramVector3d2.z);
  }

  public double length()
  {
    return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
  }

  public void normalize()
  {
    double d = length();
    if (d == 0.0D)
      return;
    scale(1.0D / d);
  }

  public void scale(double paramDouble)
  {
    this.x = (paramDouble * this.x);
    this.y = (paramDouble * this.y);
    this.z = (paramDouble * this.z);
  }

  public void set(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    this.x = paramDouble1;
    this.y = paramDouble2;
    this.z = paramDouble3;
  }

  public void set(Vector3d paramVector3d)
  {
    this.x = paramVector3d.x;
    this.y = paramVector3d.y;
    this.z = paramVector3d.z;
  }

  public void setComponent(int paramInt, double paramDouble)
  {
    if (paramInt == 0)
    {
      this.x = paramDouble;
      return;
    }
    if (paramInt == 1)
    {
      this.y = paramDouble;
      return;
    }
    this.z = paramDouble;
  }

  public void setZero()
  {
    this.z = 0.0D;
    this.y = 0.0D;
    this.x = 0.0D;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.math.Vector3d
 * JD-Core Version:    0.5.4
 */