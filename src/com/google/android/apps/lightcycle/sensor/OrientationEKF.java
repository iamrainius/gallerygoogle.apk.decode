package com.google.android.apps.lightcycle.sensor;

import com.google.android.apps.lightcycle.math.Matrix3x3d;
import com.google.android.apps.lightcycle.math.Vector3d;

public class OrientationEKF
{
  private Matrix3x3d accObservationFunctionForNumericalJacobianTempM = new Matrix3x3d();
  private Vector3d down = new Vector3d();
  private float filteredGyroTimestep;
  private boolean gyroFilterValid = true;
  private Matrix3x3d mH = new Matrix3x3d();
  private Matrix3x3d mK = new Matrix3x3d();
  private Vector3d mNu = new Vector3d();
  private Matrix3x3d mP = new Matrix3x3d();
  private Matrix3x3d mQ = new Matrix3x3d();
  private Matrix3x3d mR = new Matrix3x3d();
  private Matrix3x3d mRaccel = new Matrix3x3d();
  private Matrix3x3d mS = new Matrix3x3d();
  private Matrix3x3d magObservationFunctionForNumericalJacobianTempM = new Matrix3x3d();
  private Vector3d mh = new Vector3d();
  private Vector3d mu = new Vector3d();
  private Vector3d mx = new Vector3d();
  private Vector3d mz = new Vector3d();
  private Vector3d north = new Vector3d();
  private int numGyroTimestepSamples;
  private Matrix3x3d processAccTempM1 = new Matrix3x3d();
  private Matrix3x3d processAccTempM2 = new Matrix3x3d();
  private Matrix3x3d processAccTempM3 = new Matrix3x3d();
  private Matrix3x3d processAccTempM4 = new Matrix3x3d();
  private Matrix3x3d processAccTempM5 = new Matrix3x3d();
  private Vector3d processAccTempV1 = new Vector3d();
  private Vector3d processAccTempV2 = new Vector3d();
  private Vector3d processAccVDelta = new Vector3d();
  private Matrix3x3d processGyroTempM1 = new Matrix3x3d();
  private Matrix3x3d processGyroTempM2 = new Matrix3x3d();
  private Matrix3x3d processMagTempM1 = new Matrix3x3d();
  private Matrix3x3d processMagTempM2 = new Matrix3x3d();
  private Matrix3x3d processMagTempM4 = new Matrix3x3d();
  private Matrix3x3d processMagTempM5 = new Matrix3x3d();
  private Matrix3x3d processMagTempM6 = new Matrix3x3d();
  private Vector3d processMagTempV1 = new Vector3d();
  private Vector3d processMagTempV2 = new Vector3d();
  private Vector3d processMagTempV3 = new Vector3d();
  private Vector3d processMagTempV4 = new Vector3d();
  private Vector3d processMagTempV5 = new Vector3d();
  private double[] rotationMatrix = new double[16];
  private long sensorTimeStampAcc;
  private long sensorTimeStampGyro;
  private long sensorTimeStampMag;
  private Matrix3x3d setHeadingDegreesTempM1 = new Matrix3x3d();
  private Matrix3x3d so3LastMotion = new Matrix3x3d();
  private Matrix3x3d so3SensorFromWorld = new Matrix3x3d();
  private boolean timestepFilterInit = false;
  private Matrix3x3d updateCovariancesAfterMotionTempM1 = new Matrix3x3d();
  private Matrix3x3d updateCovariancesAfterMotionTempM2 = new Matrix3x3d();

  static
  {
    if (!OrientationEKF.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public OrientationEKF()
  {
    init();
  }

  private void accObservationFunctionForNumericalJacobian(Matrix3x3d paramMatrix3x3d, Vector3d paramVector3d)
  {
    Matrix3x3d.mult(paramMatrix3x3d, this.down, this.mh);
    So3Util.sO3FromTwoVec(this.mh, this.mz, this.accObservationFunctionForNumericalJacobianTempM);
    So3Util.muFromSO3(this.accObservationFunctionForNumericalJacobianTempM, paramVector3d);
  }

  public static void arrayAssign(double[][] paramArrayOfDouble, Matrix3x3d paramMatrix3x3d)
  {
    assert (3 == paramArrayOfDouble.length);
    assert (3 == paramArrayOfDouble[0].length);
    assert (3 == paramArrayOfDouble[1].length);
    assert (3 == paramArrayOfDouble[2].length);
    paramMatrix3x3d.set(paramArrayOfDouble[0][0], paramArrayOfDouble[0][1], paramArrayOfDouble[0][2], paramArrayOfDouble[1][0], paramArrayOfDouble[1][1], paramArrayOfDouble[1][2], paramArrayOfDouble[2][0], paramArrayOfDouble[2][1], paramArrayOfDouble[2][2]);
  }

  private void filterGyroTimestep(float paramFloat)
  {
    if (!this.timestepFilterInit)
    {
      this.filteredGyroTimestep = paramFloat;
      this.numGyroTimestepSamples = 1;
      this.timestepFilterInit = true;
    }
    int i;
    do
    {
      return;
      this.filteredGyroTimestep = (0.95F * this.filteredGyroTimestep + 0.05000001F * paramFloat);
      i = 1 + this.numGyroTimestepSamples;
      this.numGyroTimestepSamples = i;
    }
    while (i <= 10.0F);
    this.gyroFilterValid = true;
  }

  private void init()
  {
    this.sensorTimeStampGyro = 0L;
    this.sensorTimeStampAcc = 0L;
    this.sensorTimeStampMag = 0L;
    this.so3SensorFromWorld.setIdentity();
    this.so3LastMotion.setIdentity();
    this.mP.setZero();
    this.mP.setSameDiagonal(25.0D);
    this.mQ.setZero();
    this.mQ.setSameDiagonal(1.0D);
    this.mR.setZero();
    this.mR.setSameDiagonal(0.0625D);
    this.mRaccel.setZero();
    this.mRaccel.setSameDiagonal(4.0D);
    this.mS.setZero();
    this.mH.setZero();
    this.mK.setZero();
    this.mNu.setZero();
    this.mz.setZero();
    this.mh.setZero();
    this.mu.setZero();
    this.mx.setZero();
    this.down.set(0.0D, 0.0D, 9.810000000000001D);
    this.north.set(0.0D, 1.0D, 0.0D);
  }

  private void updateCovariancesAfterMotion()
  {
    this.so3LastMotion.transpose(this.updateCovariancesAfterMotionTempM1);
    Matrix3x3d.mult(this.mP, this.updateCovariancesAfterMotionTempM1, this.updateCovariancesAfterMotionTempM2);
    Matrix3x3d.mult(this.so3LastMotion, this.updateCovariancesAfterMotionTempM2, this.mP);
    this.so3LastMotion.setIdentity();
  }

  public double[] getGLMatrix()
  {
    for (int i = 0; i < 3; ++i)
      for (int j = 0; j < 3; ++j)
        this.rotationMatrix[(i + j * 4)] = this.so3SensorFromWorld.get(i, j);
    double[] arrayOfDouble1 = this.rotationMatrix;
    double[] arrayOfDouble2 = this.rotationMatrix;
    this.rotationMatrix[11] = 0.0D;
    arrayOfDouble2[7] = 0.0D;
    arrayOfDouble1[3] = 0.0D;
    double[] arrayOfDouble3 = this.rotationMatrix;
    double[] arrayOfDouble4 = this.rotationMatrix;
    this.rotationMatrix[14] = 0.0D;
    arrayOfDouble4[13] = 0.0D;
    arrayOfDouble3[12] = 0.0D;
    this.rotationMatrix[15] = 1.0D;
    return this.rotationMatrix;
  }

  public double getHeadingDegrees()
  {
    double d1 = this.so3SensorFromWorld.get(2, 0);
    double d2 = this.so3SensorFromWorld.get(2, 1);
    double d3;
    if (Math.sqrt(d1 * d1 + d2 * d2) < 0.1D)
      d3 = 0.0D;
    do
    {
      return d3;
      d3 = -90.0D - 180.0D * (Math.atan2(d2, d1) / 3.141592653589793D);
      if (d3 >= 0.0D)
        continue;
      d3 += 360.0D;
    }
    while (d3 < 360.0D);
    return d3 - 360.0D;
  }

  public void processAcc(float[] paramArrayOfFloat, long paramLong)
  {
    monitorenter;
    try
    {
      this.mz.set(paramArrayOfFloat[0], paramArrayOfFloat[1], paramArrayOfFloat[2]);
      if (this.sensorTimeStampAcc != 0L)
      {
        accObservationFunctionForNumericalJacobian(this.so3SensorFromWorld, this.mNu);
        for (int i = 0; i < 3; ++i)
        {
          Vector3d localVector3d1 = this.processAccVDelta;
          localVector3d1.setZero();
          localVector3d1.setComponent(i, 1.0E-007D);
          So3Util.sO3FromMu(localVector3d1, this.processAccTempM1);
          Matrix3x3d.mult(this.processAccTempM1, this.so3SensorFromWorld, this.processAccTempM2);
          accObservationFunctionForNumericalJacobian(this.processAccTempM2, this.processAccTempV1);
          Vector3d localVector3d2 = this.processAccTempV1;
          Vector3d.sub(this.mNu, localVector3d2, this.processAccTempV2);
          this.processAccTempV2.scale(1.0D / 1.0E-007D);
          this.mH.setColumn(i, this.processAccTempV2);
        }
        this.mH.transpose(this.processAccTempM3);
        Matrix3x3d.mult(this.mP, this.processAccTempM3, this.processAccTempM4);
        Matrix3x3d.mult(this.mH, this.processAccTempM4, this.processAccTempM5);
        Matrix3x3d.add(this.processAccTempM5, this.mRaccel, this.mS);
        this.mS.invert(this.processAccTempM3);
        this.mH.transpose(this.processAccTempM4);
        Matrix3x3d.mult(this.processAccTempM4, this.processAccTempM3, this.processAccTempM5);
        Matrix3x3d.mult(this.mP, this.processAccTempM5, this.mK);
        Matrix3x3d.mult(this.mK, this.mNu, this.mx);
        Matrix3x3d.mult(this.mK, this.mH, this.processAccTempM3);
        this.processAccTempM4.setIdentity();
        this.processAccTempM4.minusEquals(this.processAccTempM3);
        Matrix3x3d.mult(this.processAccTempM4, this.mP, this.processAccTempM3);
        this.mP.set(this.processAccTempM3);
        So3Util.sO3FromMu(this.mx, this.so3LastMotion);
        Matrix3x3d.mult(this.so3LastMotion, this.so3SensorFromWorld, this.so3SensorFromWorld);
        updateCovariancesAfterMotion();
        this.sensorTimeStampAcc = paramLong;
        return;
      }
    }
    finally
    {
      monitorexit;
    }
  }

  public void processGyro(float[] paramArrayOfFloat, long paramLong)
  {
    monitorenter;
    float f;
    try
    {
      if (this.sensorTimeStampGyro != 0L)
      {
        f = 1.0E-009F * (float)(paramLong - this.sensorTimeStampGyro);
        if (f <= 0.04F)
          break label180;
        if (!this.gyroFilterValid)
          break label172;
        f = this.filteredGyroTimestep;
      }
      while (true)
      {
        this.mu.set(paramArrayOfFloat[0] * -f, paramArrayOfFloat[1] * -f, paramArrayOfFloat[2] * -f);
        So3Util.sO3FromMu(this.mu, this.so3LastMotion);
        this.processGyroTempM1.set(this.so3SensorFromWorld);
        Matrix3x3d.mult(this.so3LastMotion, this.so3SensorFromWorld, this.processGyroTempM1);
        this.so3SensorFromWorld.set(this.processGyroTempM1);
        updateCovariancesAfterMotion();
        this.processGyroTempM2.set(this.mQ);
        this.processGyroTempM2.scale(f * f);
        this.mP.plusEquals(this.processGyroTempM2);
        this.sensorTimeStampGyro = paramLong;
        return;
        label172: label180: f = 0.01F;
      }
    }
    finally
    {
      monitorexit;
    }
  }

  public void setHeadingDegrees(double paramDouble)
  {
    monitorenter;
    try
    {
      double d1 = paramDouble - getHeadingDegrees();
      double d2 = Math.sin(3.141592653589793D * (d1 / 180.0D));
      double d3 = Math.cos(3.141592653589793D * (d1 / 180.0D));
      double[][] arrayOfDouble = new double[3][];
      double[] arrayOfDouble1 = new double[3];
      arrayOfDouble1[0] = d3;
      arrayOfDouble1[1] = (-d2);
      arrayOfDouble1[2] = 0.0D;
      arrayOfDouble[0] = arrayOfDouble1;
      arrayOfDouble[1] = { d2, d3, 0.0D };
      arrayOfDouble[2] = { 0.0D, 0.0D, 1.0D };
      arrayAssign(arrayOfDouble, this.setHeadingDegreesTempM1);
      Matrix3x3d.mult(this.so3SensorFromWorld, this.setHeadingDegreesTempM1, this.so3SensorFromWorld);
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.sensor.OrientationEKF
 * JD-Core Version:    0.5.4
 */