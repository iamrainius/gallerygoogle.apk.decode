package com.google.android.apps.lightcycle.panorama;

public class MovingSpeedCalibrator
{
  private double exposure = -1.0D;
  private float velocitySquared = 0.0F;

  private void update()
  {
    float f = 0.16F;
    if (this.exposure > 0.0D)
    {
      if (this.exposure <= 0.025D)
        break label42;
      f = 0.0025F;
    }
    label26: if (this.velocitySquared > f);
    for (boolean bool = true; ; bool = false)
    {
      LightCycleNative.SetSensorMovementTooFast(bool);
      return;
      label42: if (this.exposure < 0.01D);
      f = 1.0F;
      break label26:
    }
  }

  public void onExposureUpdate(double paramDouble)
  {
    this.exposure = paramDouble;
    update();
  }

  public void onSensorVelocityUpdate(float paramFloat)
  {
    this.velocitySquared = paramFloat;
    update();
  }

  public void reset()
  {
    this.exposure = -1.0D;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.MovingSpeedCalibrator
 * JD-Core Version:    0.5.4
 */