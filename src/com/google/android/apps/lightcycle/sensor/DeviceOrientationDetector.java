package com.google.android.apps.lightcycle.sensor;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.Display;
import com.google.android.apps.lightcycle.math.Vector3;

public class DeviceOrientationDetector
{
  private boolean initialized;
  private boolean isLandscape;
  private boolean landscapeNatural;
  private int lockedRotation;
  private float orientationAngleDegrees;
  private final SensorReader sensorReader;

  public DeviceOrientationDetector(Display paramDisplay, SensorReader paramSensorReader)
  {
    this.isLandscape = i;
    this.initialized = false;
    this.sensorReader = paramSensorReader;
    this.initialized = false;
    this.lockedRotation = 0;
    this.orientationAngleDegrees = this.lockedRotation;
    if (Build.VERSION.SDK_INT >= 9)
    {
      Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
      Camera.getCameraInfo(0, localCameraInfo);
      if (localCameraInfo.orientation != 0)
        break label81;
    }
    while (true)
    {
      this.landscapeNatural = i;
      update();
      return;
      label81: i = 0;
    }
  }

  private static float normalizeAngle(float paramFloat)
  {
    if (paramFloat > 180.0F)
      paramFloat -= 360.0F;
    do
      return paramFloat;
    while (paramFloat >= -90.0F);
    return paramFloat + 360.0F;
  }

  private boolean orientationChanged(Vector3 paramVector3)
  {
    if ((this.landscapeNatural ^ this.isLandscape))
      if (Math.abs(paramVector3.y) <= 1.5F * Math.abs(paramVector3.x));
    do
    {
      return true;
      return false;
    }
    while (Math.abs(paramVector3.x) > 1.5F * Math.abs(paramVector3.y));
    return false;
  }

  public int getDisplayInitialOrientationDegrees()
  {
    return this.lockedRotation;
  }

  public DeviceOrientation getOrientation()
  {
    if (this.sensorReader == null)
    {
      Log.e("LightCycle", "Sensor reader is not initialized.");
      return null;
    }
    float f = normalizeAngle(this.orientationAngleDegrees - this.lockedRotation);
    return new DeviceOrientation(this.lockedRotation, this.orientationAngleDegrees, f);
  }

  public void update()
  {
    int i = 1;
    if ((this.sensorReader == null) || (!this.sensorReader.isFilteredAccelerationInitialized()))
      Log.e("LightCycle", "Sensor reader is not initialized.");
    Vector3 localVector3;
    int k;
    label53: int j;
    do
    {
      return;
      localVector3 = this.sensorReader.getFilteredAcceleration();
      if (this.initialized)
        break label148;
      if (this.landscapeNatural)
        break label142;
      k = i;
      boolean bool2 = Math.abs(localVector3.x) < Math.abs(localVector3.y);
      int l = 0;
      if (!bool2)
        l = i;
      this.isLandscape = (k ^ l);
      j = 1;
      label98: this.initialized = i;
    }
    while (j == 0);
    if ((this.isLandscape ^ this.landscapeNatural))
      if (localVector3.x <= 0.0F);
    for (this.orientationAngleDegrees = 90.0F; ; this.orientationAngleDegrees = 180.0F)
      while (true)
      {
        this.orientationAngleDegrees = normalizeAngle(this.orientationAngleDegrees);
        return;
        label142: k = 0;
        break label53:
        label148: boolean bool1 = orientationChanged(localVector3);
        j = 0;
        if (bool1);
        if (!this.isLandscape);
        while (true)
        {
          this.isLandscape = i;
          j = 1;
          break label98:
          i = 0;
        }
        this.orientationAngleDegrees = -90.0F;
        continue;
        if (localVector3.y <= 0.0F)
          break;
        this.orientationAngleDegrees = 0.0F;
      }
  }

  public static class DeviceOrientation
  {
    public final float absoluteRotation;
    public final float lockedRotation;
    public final float nearestOrthoAngleDegrees;

    public DeviceOrientation(float paramFloat1, float paramFloat2, float paramFloat3)
    {
      this.lockedRotation = paramFloat1;
      this.absoluteRotation = paramFloat2;
      this.nearestOrthoAngleDegrees = paramFloat3;
    }

    public boolean isOrientationChanged90()
    {
      return (this.nearestOrthoAngleDegrees == 90.0F) || (this.nearestOrthoAngleDegrees == -90.0F);
    }

    public String toString()
    {
      return "Absolute rotation: " + this.absoluteRotation + " - Angle : " + this.nearestOrthoAngleDegrees;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.sensor.DeviceOrientationDetector
 * JD-Core Version:    0.5.4
 */