package com.google.android.apps.lightcycle.panorama;

import com.google.android.apps.lightcycle.math.Vector3;
import com.google.android.apps.lightcycle.sensor.SensorReader;

public class VideoFrameProcessor
{
  private float[] rotation;
  private SensorReader sensorReader = null;
  private boolean takeNewPhoto = false;
  private boolean validEstimate = true;

  public VideoFrameProcessor(SensorReader paramSensorReader)
  {
    this.sensorReader = paramSensorReader;
  }

  private void adjustHeading()
  {
    float f1 = LightCycleNative.GetHeadingRadians();
    float f2 = (float)this.sensorReader.getHeadingDegrees();
    float f4;
    if (f1 >= 0.0F)
    {
      float f3 = f1 - f2;
      if (Math.abs(f3 - 360.0F) < Math.abs(f3))
        f3 -= 360.0F;
      if (Math.abs(f3 + 360.0F) < Math.abs(f3))
        f3 += 360.0F;
      f4 = 0.1F * f3;
      if (Math.abs(f4) > 0.5F)
      {
        if (f4 <= 0.0F)
          break label112;
        f4 = 0.5F;
      }
    }
    while (true)
    {
      float f5 = headingDegRange360(f2 + f4);
      this.sensorReader.setHeadingDegrees(f5);
      return;
      label112: f4 = -0.5F;
    }
  }

  private float headingDegRange360(float paramFloat)
  {
    if (paramFloat < 0.0F)
      paramFloat += 360.0F;
    if (paramFloat >= 360.0F)
      paramFloat -= 360.0F;
    return paramFloat;
  }

  public float[] getRotationEstimate()
  {
    return this.rotation;
  }

  public boolean movingTooFast()
  {
    return LightCycleNative.MovingTooFast();
  }

  public void processFrame(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    LightCycleNative.SetCurrentOrientation(this.sensorReader.getAccelInPlaneRotationRadians());
    Vector3 localVector3 = this.sensorReader.getFilteredAcceleration();
    LightCycleNative.SetGravityVector(localVector3.x, localVector3.y, localVector3.z);
    if (paramArrayOfByte == null);
    do
    {
      return;
      this.rotation = LightCycleNative.ProcessFrame(paramArrayOfByte, paramInt1, paramInt2, paramBoolean1);
      boolean bool = this.rotation[0] < -1.0F;
      int i = 0;
      if (bool)
        i = 1;
      this.validEstimate = i;
      this.takeNewPhoto = LightCycleNative.TakeNewPhoto();
    }
    while ((!paramBoolean2) || (!paramBoolean1));
    adjustHeading();
  }

  public boolean takeNewPhoto()
  {
    return this.takeNewPhoto;
  }

  public boolean targetHit()
  {
    return LightCycleNative.TargetHit();
  }

  public boolean validEstimate()
  {
    return this.validEstimate;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.VideoFrameProcessor
 * JD-Core Version:    0.5.4
 */