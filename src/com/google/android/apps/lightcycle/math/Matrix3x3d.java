package com.google.android.apps.lightcycle.math;

public class Matrix3x3d
{
  public double[] m = new double[9];

  public static void add(Matrix3x3d paramMatrix3x3d1, Matrix3x3d paramMatrix3x3d2, Matrix3x3d paramMatrix3x3d3)
  {
    paramMatrix3x3d3.m[0] = (paramMatrix3x3d1.m[0] + paramMatrix3x3d2.m[0]);
    paramMatrix3x3d3.m[1] = (paramMatrix3x3d1.m[1] + paramMatrix3x3d2.m[1]);
    paramMatrix3x3d3.m[2] = (paramMatrix3x3d1.m[2] + paramMatrix3x3d2.m[2]);
    paramMatrix3x3d3.m[3] = (paramMatrix3x3d1.m[3] + paramMatrix3x3d2.m[3]);
    paramMatrix3x3d3.m[4] = (paramMatrix3x3d1.m[4] + paramMatrix3x3d2.m[4]);
    paramMatrix3x3d3.m[5] = (paramMatrix3x3d1.m[5] + paramMatrix3x3d2.m[5]);
    paramMatrix3x3d3.m[6] = (paramMatrix3x3d1.m[6] + paramMatrix3x3d2.m[6]);
    paramMatrix3x3d3.m[7] = (paramMatrix3x3d1.m[7] + paramMatrix3x3d2.m[7]);
    paramMatrix3x3d3.m[8] = (paramMatrix3x3d1.m[8] + paramMatrix3x3d2.m[8]);
  }

  public static void mult(Matrix3x3d paramMatrix3x3d1, Matrix3x3d paramMatrix3x3d2, Matrix3x3d paramMatrix3x3d3)
  {
    paramMatrix3x3d3.set(paramMatrix3x3d1.m[0] * paramMatrix3x3d2.m[0] + paramMatrix3x3d1.m[1] * paramMatrix3x3d2.m[3] + paramMatrix3x3d1.m[2] * paramMatrix3x3d2.m[6], paramMatrix3x3d1.m[0] * paramMatrix3x3d2.m[1] + paramMatrix3x3d1.m[1] * paramMatrix3x3d2.m[4] + paramMatrix3x3d1.m[2] * paramMatrix3x3d2.m[7], paramMatrix3x3d1.m[0] * paramMatrix3x3d2.m[2] + paramMatrix3x3d1.m[1] * paramMatrix3x3d2.m[5] + paramMatrix3x3d1.m[2] * paramMatrix3x3d2.m[8], paramMatrix3x3d1.m[3] * paramMatrix3x3d2.m[0] + paramMatrix3x3d1.m[4] * paramMatrix3x3d2.m[3] + paramMatrix3x3d1.m[5] * paramMatrix3x3d2.m[6], paramMatrix3x3d1.m[3] * paramMatrix3x3d2.m[1] + paramMatrix3x3d1.m[4] * paramMatrix3x3d2.m[4] + paramMatrix3x3d1.m[5] * paramMatrix3x3d2.m[7], paramMatrix3x3d1.m[3] * paramMatrix3x3d2.m[2] + paramMatrix3x3d1.m[4] * paramMatrix3x3d2.m[5] + paramMatrix3x3d1.m[5] * paramMatrix3x3d2.m[8], paramMatrix3x3d1.m[6] * paramMatrix3x3d2.m[0] + paramMatrix3x3d1.m[7] * paramMatrix3x3d2.m[3] + paramMatrix3x3d1.m[8] * paramMatrix3x3d2.m[6], paramMatrix3x3d1.m[6] * paramMatrix3x3d2.m[1] + paramMatrix3x3d1.m[7] * paramMatrix3x3d2.m[4] + paramMatrix3x3d1.m[8] * paramMatrix3x3d2.m[7], paramMatrix3x3d1.m[6] * paramMatrix3x3d2.m[2] + paramMatrix3x3d1.m[7] * paramMatrix3x3d2.m[5] + paramMatrix3x3d1.m[8] * paramMatrix3x3d2.m[8]);
  }

  public static void mult(Matrix3x3d paramMatrix3x3d, Vector3d paramVector3d1, Vector3d paramVector3d2)
  {
    double d1 = paramMatrix3x3d.m[0] * paramVector3d1.x + paramMatrix3x3d.m[1] * paramVector3d1.y + paramMatrix3x3d.m[2] * paramVector3d1.z;
    double d2 = paramMatrix3x3d.m[3] * paramVector3d1.x + paramMatrix3x3d.m[4] * paramVector3d1.y + paramMatrix3x3d.m[5] * paramVector3d1.z;
    double d3 = paramMatrix3x3d.m[6] * paramVector3d1.x + paramMatrix3x3d.m[7] * paramVector3d1.y + paramMatrix3x3d.m[8] * paramVector3d1.z;
    paramVector3d2.x = d1;
    paramVector3d2.y = d2;
    paramVector3d2.z = d3;
  }

  public double determinant()
  {
    return get(0, 0) * (get(1, 1) * get(2, 2) - get(2, 1) * get(1, 2)) - get(0, 1) * (get(1, 0) * get(2, 2) - get(1, 2) * get(2, 0)) + get(0, 2) * (get(1, 0) * get(2, 1) - get(1, 1) * get(2, 0));
  }

  public double get(int paramInt1, int paramInt2)
  {
    return this.m[(paramInt2 + paramInt1 * 3)];
  }

  public boolean invert(Matrix3x3d paramMatrix3x3d)
  {
    double d1 = determinant();
    if (d1 == 0.0D)
      return false;
    double d2 = 1.0D / d1;
    paramMatrix3x3d.set(d2 * (get(1, 1) * get(2, 2) - get(2, 1) * get(1, 2)), d2 * -(get(1, 0) * get(2, 2) - get(1, 2) * get(2, 0)), d2 * (get(1, 0) * get(2, 1) - get(2, 0) * get(1, 1)), d2 * -(get(0, 1) * get(2, 2) - get(0, 2) * get(2, 1)), d2 * (get(0, 0) * get(2, 2) - get(0, 2) * get(2, 0)), d2 * -(get(0, 0) * get(2, 1) - get(2, 0) * get(0, 1)), d2 * (get(0, 1) * get(1, 2) - get(0, 2) * get(1, 1)), d2 * -(get(0, 0) * get(1, 2) - get(1, 0) * get(0, 2)), d2 * (get(0, 0) * get(1, 1) - get(1, 0) * get(0, 1)));
    return true;
  }

  public void minusEquals(Matrix3x3d paramMatrix3x3d)
  {
    double[] arrayOfDouble1 = this.m;
    arrayOfDouble1[0] -= paramMatrix3x3d.m[0];
    double[] arrayOfDouble2 = this.m;
    arrayOfDouble2[1] -= paramMatrix3x3d.m[1];
    double[] arrayOfDouble3 = this.m;
    arrayOfDouble3[2] -= paramMatrix3x3d.m[2];
    double[] arrayOfDouble4 = this.m;
    arrayOfDouble4[3] -= paramMatrix3x3d.m[3];
    double[] arrayOfDouble5 = this.m;
    arrayOfDouble5[4] -= paramMatrix3x3d.m[4];
    double[] arrayOfDouble6 = this.m;
    arrayOfDouble6[5] -= paramMatrix3x3d.m[5];
    double[] arrayOfDouble7 = this.m;
    arrayOfDouble7[6] -= paramMatrix3x3d.m[6];
    double[] arrayOfDouble8 = this.m;
    arrayOfDouble8[7] -= paramMatrix3x3d.m[7];
    double[] arrayOfDouble9 = this.m;
    arrayOfDouble9[8] -= paramMatrix3x3d.m[8];
  }

  public void plusEquals(Matrix3x3d paramMatrix3x3d)
  {
    double[] arrayOfDouble1 = this.m;
    arrayOfDouble1[0] += paramMatrix3x3d.m[0];
    double[] arrayOfDouble2 = this.m;
    arrayOfDouble2[1] += paramMatrix3x3d.m[1];
    double[] arrayOfDouble3 = this.m;
    arrayOfDouble3[2] += paramMatrix3x3d.m[2];
    double[] arrayOfDouble4 = this.m;
    arrayOfDouble4[3] += paramMatrix3x3d.m[3];
    double[] arrayOfDouble5 = this.m;
    arrayOfDouble5[4] += paramMatrix3x3d.m[4];
    double[] arrayOfDouble6 = this.m;
    arrayOfDouble6[5] += paramMatrix3x3d.m[5];
    double[] arrayOfDouble7 = this.m;
    arrayOfDouble7[6] += paramMatrix3x3d.m[6];
    double[] arrayOfDouble8 = this.m;
    arrayOfDouble8[7] += paramMatrix3x3d.m[7];
    double[] arrayOfDouble9 = this.m;
    arrayOfDouble9[8] += paramMatrix3x3d.m[8];
  }

  public void scale(double paramDouble)
  {
    double[] arrayOfDouble1 = this.m;
    arrayOfDouble1[0] = (paramDouble * arrayOfDouble1[0]);
    double[] arrayOfDouble2 = this.m;
    arrayOfDouble2[1] = (paramDouble * arrayOfDouble2[1]);
    double[] arrayOfDouble3 = this.m;
    arrayOfDouble3[2] = (paramDouble * arrayOfDouble3[2]);
    double[] arrayOfDouble4 = this.m;
    arrayOfDouble4[3] = (paramDouble * arrayOfDouble4[3]);
    double[] arrayOfDouble5 = this.m;
    arrayOfDouble5[4] = (paramDouble * arrayOfDouble5[4]);
    double[] arrayOfDouble6 = this.m;
    arrayOfDouble6[5] = (paramDouble * arrayOfDouble6[5]);
    double[] arrayOfDouble7 = this.m;
    arrayOfDouble7[6] = (paramDouble * arrayOfDouble7[6]);
    double[] arrayOfDouble8 = this.m;
    arrayOfDouble8[7] = (paramDouble * arrayOfDouble8[7]);
    double[] arrayOfDouble9 = this.m;
    arrayOfDouble9[8] = (paramDouble * arrayOfDouble9[8]);
  }

  public void set(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9)
  {
    this.m[0] = paramDouble1;
    this.m[1] = paramDouble2;
    this.m[2] = paramDouble3;
    this.m[3] = paramDouble4;
    this.m[4] = paramDouble5;
    this.m[5] = paramDouble6;
    this.m[6] = paramDouble7;
    this.m[7] = paramDouble8;
    this.m[8] = paramDouble9;
  }

  public void set(int paramInt1, int paramInt2, double paramDouble)
  {
    this.m[(paramInt2 + paramInt1 * 3)] = paramDouble;
  }

  public void set(Matrix3x3d paramMatrix3x3d)
  {
    this.m[0] = paramMatrix3x3d.m[0];
    this.m[1] = paramMatrix3x3d.m[1];
    this.m[2] = paramMatrix3x3d.m[2];
    this.m[3] = paramMatrix3x3d.m[3];
    this.m[4] = paramMatrix3x3d.m[4];
    this.m[5] = paramMatrix3x3d.m[5];
    this.m[6] = paramMatrix3x3d.m[6];
    this.m[7] = paramMatrix3x3d.m[7];
    this.m[8] = paramMatrix3x3d.m[8];
  }

  public void setColumn(int paramInt, Vector3d paramVector3d)
  {
    this.m[paramInt] = paramVector3d.x;
    this.m[(paramInt + 3)] = paramVector3d.y;
    this.m[(paramInt + 6)] = paramVector3d.z;
  }

  public void setIdentity()
  {
    double[] arrayOfDouble1 = this.m;
    double[] arrayOfDouble2 = this.m;
    double[] arrayOfDouble3 = this.m;
    double[] arrayOfDouble4 = this.m;
    double[] arrayOfDouble5 = this.m;
    this.m[7] = 0.0D;
    arrayOfDouble5[6] = 0.0D;
    arrayOfDouble4[5] = 0.0D;
    arrayOfDouble3[3] = 0.0D;
    arrayOfDouble2[2] = 0.0D;
    arrayOfDouble1[1] = 0.0D;
    double[] arrayOfDouble6 = this.m;
    double[] arrayOfDouble7 = this.m;
    this.m[8] = 1.0D;
    arrayOfDouble7[4] = 1.0D;
    arrayOfDouble6[0] = 1.0D;
  }

  public void setSameDiagonal(double paramDouble)
  {
    double[] arrayOfDouble1 = this.m;
    double[] arrayOfDouble2 = this.m;
    this.m[8] = paramDouble;
    arrayOfDouble2[4] = paramDouble;
    arrayOfDouble1[0] = paramDouble;
  }

  public void setZero()
  {
    double[] arrayOfDouble1 = this.m;
    double[] arrayOfDouble2 = this.m;
    double[] arrayOfDouble3 = this.m;
    double[] arrayOfDouble4 = this.m;
    double[] arrayOfDouble5 = this.m;
    double[] arrayOfDouble6 = this.m;
    double[] arrayOfDouble7 = this.m;
    double[] arrayOfDouble8 = this.m;
    this.m[8] = 0.0D;
    arrayOfDouble8[7] = 0.0D;
    arrayOfDouble7[6] = 0.0D;
    arrayOfDouble6[5] = 0.0D;
    arrayOfDouble5[4] = 0.0D;
    arrayOfDouble4[3] = 0.0D;
    arrayOfDouble3[2] = 0.0D;
    arrayOfDouble2[1] = 0.0D;
    arrayOfDouble1[0] = 0.0D;
  }

  public void transpose()
  {
    double d1 = this.m[1];
    this.m[1] = this.m[3];
    this.m[3] = d1;
    double d2 = this.m[2];
    this.m[2] = this.m[6];
    this.m[6] = d2;
    double d3 = this.m[5];
    this.m[5] = this.m[7];
    this.m[7] = d3;
  }

  public void transpose(Matrix3x3d paramMatrix3x3d)
  {
    double d1 = this.m[1];
    double d2 = this.m[2];
    double d3 = this.m[5];
    paramMatrix3x3d.m[0] = this.m[0];
    paramMatrix3x3d.m[1] = this.m[3];
    paramMatrix3x3d.m[2] = this.m[6];
    paramMatrix3x3d.m[3] = d1;
    paramMatrix3x3d.m[4] = this.m[4];
    paramMatrix3x3d.m[5] = this.m[7];
    paramMatrix3x3d.m[6] = d2;
    paramMatrix3x3d.m[7] = d3;
    paramMatrix3x3d.m[8] = this.m[8];
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.math.Matrix3x3d
 * JD-Core Version:    0.5.4
 */