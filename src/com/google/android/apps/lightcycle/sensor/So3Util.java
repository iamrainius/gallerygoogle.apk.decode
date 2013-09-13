package com.google.android.apps.lightcycle.sensor;

import com.google.android.apps.lightcycle.math.Matrix3x3d;
import com.google.android.apps.lightcycle.math.Vector3d;

public class So3Util
{
  private static Vector3d muFromSO3R2;
  private static Matrix3x3d sO3FromTwoVec33R1;
  private static Matrix3x3d sO3FromTwoVec33R2;
  private static Vector3d sO3FromTwoVecA;
  private static Vector3d sO3FromTwoVecB;
  private static Vector3d sO3FromTwoVecN;
  private static Vector3d temp31 = new Vector3d();

  static
  {
    sO3FromTwoVecN = new Vector3d();
    sO3FromTwoVecA = new Vector3d();
    sO3FromTwoVecB = new Vector3d();
    sO3FromTwoVec33R1 = new Matrix3x3d();
    sO3FromTwoVec33R2 = new Matrix3x3d();
    muFromSO3R2 = new Vector3d();
  }

  public static void muFromSO3(Matrix3x3d paramMatrix3x3d, Vector3d paramVector3d)
  {
    double d1 = 0.5D * (paramMatrix3x3d.get(0, 0) + paramMatrix3x3d.get(1, 1) + paramMatrix3x3d.get(2, 2) - 1.0D);
    paramVector3d.set((paramMatrix3x3d.get(2, 1) - paramMatrix3x3d.get(1, 2)) / 2.0D, (paramMatrix3x3d.get(0, 2) - paramMatrix3x3d.get(2, 0)) / 2.0D, (paramMatrix3x3d.get(1, 0) - paramMatrix3x3d.get(0, 1)) / 2.0D);
    double d2 = paramVector3d.length();
    if (d1 > 0.7071067811865476D)
    {
      if (d2 > 0.0D)
        paramVector3d.scale(Math.asin(d2) / d2);
      return;
    }
    if (d1 > -0.7071067811865476D)
    {
      paramVector3d.scale(Math.acos(d1) / d2);
      return;
    }
    (3.141592653589793D - Math.asin(d2));
    double d3 = paramMatrix3x3d.get(0, 0) - d1;
    double d4 = paramMatrix3x3d.get(1, 1) - d1;
    double d5 = paramMatrix3x3d.get(2, 2) - d1;
    Vector3d localVector3d = muFromSO3R2;
    if ((d3 * d3 > d4 * d4) && (d3 * d3 > d5 * d5))
      localVector3d.set(d3, (paramMatrix3x3d.get(1, 0) + paramMatrix3x3d.get(0, 1)) / 2.0D, (paramMatrix3x3d.get(0, 2) + paramMatrix3x3d.get(2, 0)) / 2.0D);
    while (true)
    {
      if (Vector3d.dot(localVector3d, paramVector3d) < 0.0D)
        localVector3d.scale(-1.0D);
      localVector3d.normalize();
      return;
      if (d4 * d4 > d5 * d5)
        localVector3d.set((paramMatrix3x3d.get(1, 0) + paramMatrix3x3d.get(0, 1)) / 2.0D, d4, (paramMatrix3x3d.get(2, 1) + paramMatrix3x3d.get(1, 2)) / 2.0D);
      localVector3d.set((paramMatrix3x3d.get(0, 2) + paramMatrix3x3d.get(2, 0)) / 2.0D, (paramMatrix3x3d.get(2, 1) + paramMatrix3x3d.get(1, 2)) / 2.0D, d5);
    }
  }

  public static void rodriguesSo3Exp(Vector3d paramVector3d, double paramDouble1, double paramDouble2, Matrix3x3d paramMatrix3x3d)
  {
    double d1 = paramVector3d.x * paramVector3d.x;
    double d2 = paramVector3d.y * paramVector3d.y;
    double d3 = paramVector3d.z * paramVector3d.z;
    paramMatrix3x3d.set(0, 0, 1.0D - paramDouble2 * (d2 + d3));
    paramMatrix3x3d.set(1, 1, 1.0D - paramDouble2 * (d1 + d3));
    paramMatrix3x3d.set(2, 2, 1.0D - paramDouble2 * (d1 + d2));
    double d4 = paramDouble1 * paramVector3d.z;
    double d5 = paramDouble2 * (paramVector3d.x * paramVector3d.y);
    paramMatrix3x3d.set(0, 1, d5 - d4);
    paramMatrix3x3d.set(1, 0, d5 + d4);
    double d6 = paramDouble1 * paramVector3d.y;
    double d7 = paramDouble2 * (paramVector3d.x * paramVector3d.z);
    paramMatrix3x3d.set(0, 2, d7 + d6);
    paramMatrix3x3d.set(2, 0, d7 - d6);
    double d8 = paramDouble1 * paramVector3d.x;
    double d9 = paramDouble2 * (paramVector3d.y * paramVector3d.z);
    paramMatrix3x3d.set(1, 2, d9 - d8);
    paramMatrix3x3d.set(2, 1, d9 + d8);
  }

  public static void sO3FromMu(Vector3d paramVector3d, Matrix3x3d paramMatrix3x3d)
  {
    double d1 = Vector3d.dot(paramVector3d, paramVector3d);
    double d2 = Math.sqrt(d1);
    double d4;
    double d5;
    if (d1 < 1.0E-008D)
    {
      d4 = 1.0D - 0.16666667163372D * d1;
      d5 = 0.5D;
    }
    while (true)
    {
      rodriguesSo3Exp(paramVector3d, d4, d5, paramMatrix3x3d);
      return;
      if (d1 < 1.0E-006D)
      {
        d5 = 0.5D - 0.0416666679084301D * d1;
        d4 = 1.0D - 0.16666667163372D * d1 * (1.0D - 0.16666667163372D * d1);
      }
      double d3 = 1.0D / d2;
      d4 = d3 * Math.sin(d2);
      d5 = (1.0D - Math.cos(d2)) * (d3 * d3);
    }
  }

  public static void sO3FromTwoVec(Vector3d paramVector3d1, Vector3d paramVector3d2, Matrix3x3d paramMatrix3x3d)
  {
    paramMatrix3x3d.setIdentity();
    Vector3d.cross(paramVector3d1, paramVector3d2, sO3FromTwoVecN);
    if (sO3FromTwoVecN.length() == 0.0D)
      return;
    sO3FromTwoVecA.set(paramVector3d1);
    sO3FromTwoVecB.set(paramVector3d2);
    sO3FromTwoVecN.normalize();
    sO3FromTwoVecA.normalize();
    sO3FromTwoVecB.normalize();
    Matrix3x3d localMatrix3x3d1 = sO3FromTwoVec33R1;
    localMatrix3x3d1.setColumn(0, sO3FromTwoVecA);
    localMatrix3x3d1.setColumn(1, sO3FromTwoVecN);
    Vector3d.cross(sO3FromTwoVecN, sO3FromTwoVecA, temp31);
    localMatrix3x3d1.setColumn(2, temp31);
    Matrix3x3d localMatrix3x3d2 = sO3FromTwoVec33R2;
    localMatrix3x3d2.setColumn(0, sO3FromTwoVecB);
    localMatrix3x3d2.setColumn(1, sO3FromTwoVecN);
    Vector3d.cross(sO3FromTwoVecN, sO3FromTwoVecB, temp31);
    localMatrix3x3d2.setColumn(2, temp31);
    localMatrix3x3d1.transpose();
    Matrix3x3d.mult(localMatrix3x3d2, localMatrix3x3d1, paramMatrix3x3d);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.sensor.So3Util
 * JD-Core Version:    0.5.4
 */