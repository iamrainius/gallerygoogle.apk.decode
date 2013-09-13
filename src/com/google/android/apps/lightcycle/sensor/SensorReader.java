package com.google.android.apps.lightcycle.sensor;

import F;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Build.VERSION;
import com.google.android.apps.lightcycle.math.Vector3;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.LG;
import java.util.Arrays;

public class SensorReader
{
  private float accelFilterCoefficient = 0.15F;
  private Vector3 acceleration = new Vector3();
  private float angularVelocitySqrRad = 0.0F;
  private OrientationEKF ekf = new OrientationEKF();
  private boolean filterInitialized = false;
  private Vector3 filteredAcceleration = new Vector3();
  private float[] geomagnetic = new float[3];
  private float[] gyroBias = { 0.0F, 0.0F, 0.0F };
  private long gyroLastTimestamp = 0L;
  private float imuOrientationDeg = 90.0F;
  private int numGyroSamples = 0;
  private float[] rotationAccumulator = new float[3];
  private final SensorEventListener sensorEventListener = new SensorEventListener()
  {
    public void onAccuracyChanged(Sensor paramSensor, int paramInt)
    {
    }

    public void onSensorChanged(SensorEvent paramSensorEvent)
    {
      if (paramSensorEvent.sensor.getType() == 1)
      {
        SensorReader.this.updateAccelerometerState(paramSensorEvent);
        if (SensorReader.this.useEkf)
          SensorReader.this.ekf.processAcc(paramSensorEvent.values, paramSensorEvent.timestamp);
      }
      do
      {
        do
        {
          return;
          if (paramSensorEvent.sensor.getType() != 2)
            continue;
          SensorReader.this.geomagnetic[0] = paramSensorEvent.values[0];
          SensorReader.this.geomagnetic[1] = paramSensorEvent.values[1];
          SensorReader.this.geomagnetic[2] = paramSensorEvent.values[2];
          return;
        }
        while (paramSensorEvent.sensor.getType() != 4);
        float[] arrayOfFloat1 = paramSensorEvent.values;
        arrayOfFloat1[0] -= SensorReader.access$400(SensorReader.this)[0];
        float[] arrayOfFloat2 = paramSensorEvent.values;
        arrayOfFloat2[1] -= SensorReader.access$400(SensorReader.this)[1];
        float[] arrayOfFloat3 = paramSensorEvent.values;
        arrayOfFloat3[2] -= SensorReader.access$400(SensorReader.this)[2];
        float f1 = paramSensorEvent.values[0] * paramSensorEvent.values[0];
        float f2 = paramSensorEvent.values[1] * paramSensorEvent.values[1];
        float f3 = paramSensorEvent.values[2] * paramSensorEvent.values[2];
        SensorReader.access$502(SensorReader.this, f3 + (f1 + f2));
        if (SensorReader.this.sensorVelocityCallback != null)
          SensorReader.this.sensorVelocityCallback.onCallback(Float.valueOf(SensorReader.this.angularVelocitySqrRad));
        SensorReader.this.updateGyroState(paramSensorEvent);
      }
      while (!SensorReader.this.useEkf);
      SensorReader.this.ekf.processGyro(paramSensorEvent.values, paramSensorEvent.timestamp);
    }
  };
  private SensorManager sensorManager = null;
  private Callback<Float> sensorVelocityCallback = null;
  private float[] tForm = new float[16];
  private boolean useEkf = true;

  private void updateAccelerometerState(SensorEvent paramSensorEvent)
  {
    this.acceleration.set(paramSensorEvent.values[0], paramSensorEvent.values[1], paramSensorEvent.values[2]);
    if (!this.filterInitialized)
    {
      this.filteredAcceleration.set(paramSensorEvent.values[0], paramSensorEvent.values[1], paramSensorEvent.values[2]);
      this.filterInitialized = true;
      return;
    }
    float f1 = this.accelFilterCoefficient;
    float f2 = 1.0F - f1;
    this.filteredAcceleration.x = (f1 * paramSensorEvent.values[0] + f2 * this.filteredAcceleration.x);
    this.filteredAcceleration.y = (f1 * paramSensorEvent.values[1] + f2 * this.filteredAcceleration.y);
    this.filteredAcceleration.z = (f1 * paramSensorEvent.values[2] + f2 * this.filteredAcceleration.z);
  }

  private void updateGyroState(SensorEvent paramSensorEvent)
  {
    float f;
    if (this.gyroLastTimestamp != 0L)
    {
      f = 1.0E-009F * (float)(paramSensorEvent.timestamp - this.gyroLastTimestamp);
      monitorenter;
    }
    try
    {
      float[] arrayOfFloat1 = this.rotationAccumulator;
      arrayOfFloat1[0] += f * paramSensorEvent.values[0];
      float[] arrayOfFloat2 = this.rotationAccumulator;
      arrayOfFloat2[1] += f * paramSensorEvent.values[1];
      float[] arrayOfFloat3 = this.rotationAccumulator;
      arrayOfFloat3[2] += f * paramSensorEvent.values[2];
      this.numGyroSamples = (1 + this.numGyroSamples);
      monitorexit;
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  public void enableEkf(boolean paramBoolean)
  {
    this.useEkf = paramBoolean;
  }

  public float getAccelInPlaneRotationRadians()
  {
    return (float)Math.atan2(this.filteredAcceleration.y, this.filteredAcceleration.x);
  }

  public float[] getAndResetGyroData()
  {
    monitorenter;
    try
    {
      float[] arrayOfFloat = (float[])this.rotationAccumulator.clone();
      this.rotationAccumulator[0] = 0.0F;
      this.rotationAccumulator[1] = 0.0F;
      this.rotationAccumulator[2] = 0.0F;
      this.numGyroSamples = 0;
      return arrayOfFloat;
    }
    finally
    {
      monitorexit;
    }
  }

  public float getAngularVelocitySquaredRad()
  {
    return this.angularVelocitySqrRad;
  }

  public int getAzimuthInDeg()
  {
    float[] arrayOfFloat1 = new float[16];
    float[] arrayOfFloat2 = new float[3];
    SensorManager.getRotationMatrix(arrayOfFloat1, null, this.filteredAcceleration.toFloatArray(), this.geomagnetic);
    SensorManager.getOrientation(arrayOfFloat1, arrayOfFloat2);
    return (int)(180.0F * arrayOfFloat2[0] / 3.141592653589793D);
  }

  public boolean getEkfEnabled()
  {
    return this.useEkf;
  }

  public float[] getFilterOutput()
  {
    float[] arrayOfFloat1 = new float[16];
    double[] arrayOfDouble = this.ekf.getGLMatrix();
    for (int i = 0; i < 16; ++i)
      arrayOfFloat1[i] = (float)arrayOfDouble[i];
    Matrix.rotateM(arrayOfFloat1, 0, 90.0F, 1.0F, 0.0F, 0.0F);
    float[] arrayOfFloat2 = new float[16];
    Matrix.setIdentityM(arrayOfFloat2, 0);
    Matrix.rotateM(arrayOfFloat2, 0, this.imuOrientationDeg, 0.0F, 0.0F, 1.0F);
    Matrix.multiplyMM(this.tForm, 0, arrayOfFloat2, 0, arrayOfFloat1, 0);
    return this.tForm;
  }

  public Vector3 getFilteredAcceleration()
  {
    return this.filteredAcceleration;
  }

  public double getHeadingDegrees()
  {
    return this.ekf.getHeadingDegrees();
  }

  public float getImuOrientationDegrees()
  {
    return this.imuOrientationDeg;
  }

  public int getNumGyroSamples()
  {
    return this.numGyroSamples;
  }

  public boolean isFilteredAccelerationInitialized()
  {
    return this.filterInitialized;
  }

  public void resetGyroBias()
  {
    Arrays.fill(this.gyroBias, 0.0F);
  }

  public void setGyroBias(float[] paramArrayOfFloat)
  {
    this.gyroBias[0] = paramArrayOfFloat[0];
    this.gyroBias[1] = paramArrayOfFloat[1];
    this.gyroBias[2] = paramArrayOfFloat[2];
  }

  public void setHeadingDegrees(double paramDouble)
  {
    if (paramDouble < 0.0D)
      paramDouble += 360.0D;
    if (paramDouble > 360.0D)
      paramDouble -= 360.0D;
    this.ekf.setHeadingDegrees(paramDouble);
  }

  public void setSensorVelocityCallback(Callback<Float> paramCallback)
  {
    this.sensorVelocityCallback = paramCallback;
  }

  public void start(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 9)
    {
      Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
      Camera.getCameraInfo(0, localCameraInfo);
      this.imuOrientationDeg = localCameraInfo.orientation;
      LG.d("Model is " + Build.MODEL);
      if (Build.MODEL.startsWith("Nexus 7"))
        this.imuOrientationDeg = 90.0F;
      LG.d("Camera orientation is : " + localCameraInfo.orientation);
    }
    this.sensorManager = ((SensorManager)paramContext.getSystemService("sensor"));
    this.sensorManager.registerListener(this.sensorEventListener, this.sensorManager.getDefaultSensor(1), 1);
    this.sensorManager.registerListener(this.sensorEventListener, this.sensorManager.getDefaultSensor(4), 1);
    this.sensorManager.registerListener(this.sensorEventListener, this.sensorManager.getDefaultSensor(2), 3);
    this.filterInitialized = false;
    resetGyroBias();
  }

  public void stop()
  {
    if (this.sensorManager == null)
      return;
    this.sensorManager.unregisterListener(this.sensorEventListener);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.sensor.SensorReader
 * JD-Core Version:    0.5.4
 */