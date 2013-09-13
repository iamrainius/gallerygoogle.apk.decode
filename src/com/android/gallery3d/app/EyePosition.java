package com.android.gallery3d.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.FloatMath;
import android.view.Display;
import android.view.WindowManager;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.GalleryUtils;

public class EyePosition
{
  private static final float USER_ANGEL = (float)Math.toRadians(10.0D);
  private static final float USER_ANGEL_COS = FloatMath.cos(USER_ANGEL);
  private static final float USER_ANGEL_SIN = FloatMath.sin(USER_ANGEL);
  private Context mContext;
  private Display mDisplay;
  private int mGyroscopeCountdown = 0;
  private final float mLimit;
  private EyePositionListener mListener;
  private PositionListener mPositionListener = new PositionListener(null);
  private Sensor mSensor;
  private long mStartTime = -1L;
  private final float mUserDistance;
  private float mX;
  private float mY;
  private float mZ;

  public EyePosition(Context paramContext, EyePositionListener paramEyePositionListener)
  {
    this.mContext = paramContext;
    this.mListener = paramEyePositionListener;
    this.mUserDistance = GalleryUtils.meterToPixel(0.3F);
    this.mLimit = (0.5F * this.mUserDistance);
    this.mDisplay = ((WindowManager)this.mContext.getSystemService("window")).getDefaultDisplay();
  }

  private void onAccelerometerChanged(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    float f1 = paramFloat1;
    float f2 = paramFloat2;
    switch (this.mDisplay.getRotation())
    {
    default:
    case 1:
    case 2:
    case 3:
    }
    while (true)
    {
      float f3 = f1 * f1 + f2 * f2 + paramFloat3 * paramFloat3;
      float f4 = -f2 / f3;
      float f5 = f4 * f1;
      float f6 = -1.0F + f4 * f2;
      float f7 = f4 * paramFloat3;
      float f8 = FloatMath.sqrt(f5 * f5 + f6 * f6 + f7 * f7);
      float f9 = FloatMath.sqrt(f3);
      this.mX = Utils.clamp((f1 * USER_ANGEL_COS / f9 + f5 * USER_ANGEL_SIN / f8) * this.mUserDistance, -this.mLimit, this.mLimit);
      this.mY = (-Utils.clamp((f2 * USER_ANGEL_COS / f9 + f6 * USER_ANGEL_SIN / f8) * this.mUserDistance, -this.mLimit, this.mLimit));
      this.mZ = (-FloatMath.sqrt(this.mUserDistance * this.mUserDistance - this.mX * this.mX - this.mY * this.mY));
      this.mListener.onEyePositionChanged(this.mX, this.mY, this.mZ);
      return;
      f1 = -paramFloat2;
      f2 = paramFloat1;
      continue;
      f1 = -paramFloat1;
      f2 = -paramFloat2;
      continue;
      f1 = paramFloat2;
      f2 = -paramFloat1;
    }
  }

  private void onGyroscopeChanged(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    long l = SystemClock.elapsedRealtime();
    float f1;
    label14: float f2;
    if (paramFloat1 > 0.0F)
    {
      f1 = paramFloat1;
      if (paramFloat2 <= 0.0F)
        break label210;
      f2 = paramFloat2;
    }
    while (true)
    {
      float f3 = f1 + f2;
      if ((f3 >= 0.15F) && (f3 <= 10.0F) && (this.mGyroscopeCountdown <= 0))
        break;
      this.mGyroscopeCountdown = (-1 + this.mGyroscopeCountdown);
      this.mStartTime = l;
      float f4 = this.mUserDistance / 20.0F;
      if ((this.mX > f4) || (this.mX < -f4) || (this.mY > f4) || (this.mY < -f4))
      {
        this.mX = (0.995F * this.mX);
        this.mY = (0.995F * this.mY);
        this.mZ = (float)(-Math.sqrt(this.mUserDistance * this.mUserDistance - this.mX * this.mX - this.mY * this.mY));
        this.mListener.onEyePositionChanged(this.mX, this.mY, this.mZ);
      }
      return;
      f1 = -paramFloat1;
      break label14:
      label210: f2 = -paramFloat2;
    }
    float f5 = (float)(l - this.mStartTime) / 1000.0F * this.mUserDistance * -this.mZ;
    this.mStartTime = l;
    float f6 = -paramFloat2;
    float f7 = -paramFloat1;
    switch (this.mDisplay.getRotation())
    {
    default:
    case 1:
    case 2:
    case 3:
    }
    while (true)
    {
      this.mX = (0.995F * Utils.clamp((float)(this.mX + f6 * f5 / Math.hypot(this.mZ, this.mX)), -this.mLimit, this.mLimit));
      this.mY = (0.995F * Utils.clamp((float)(this.mY + f7 * f5 / Math.hypot(this.mZ, this.mY)), -this.mLimit, this.mLimit));
      this.mZ = (-FloatMath.sqrt(this.mUserDistance * this.mUserDistance - this.mX * this.mX - this.mY * this.mY));
      this.mListener.onEyePositionChanged(this.mX, this.mY, this.mZ);
      return;
      f6 = -paramFloat1;
      f7 = paramFloat2;
      continue;
      f6 = paramFloat2;
      f7 = paramFloat1;
      continue;
      f6 = paramFloat1;
      f7 = -paramFloat2;
    }
  }

  public void pause()
  {
    if (this.mSensor == null)
      return;
    ((SensorManager)this.mContext.getSystemService("sensor")).unregisterListener(this.mPositionListener);
  }

  public void resetPosition()
  {
    this.mStartTime = -1L;
    this.mY = 0.0F;
    this.mX = 0.0F;
    this.mZ = (-this.mUserDistance);
    this.mListener.onEyePositionChanged(this.mX, this.mY, this.mZ);
  }

  public void resume()
  {
    if (this.mSensor != null)
      ((SensorManager)this.mContext.getSystemService("sensor")).registerListener(this.mPositionListener, this.mSensor, 1);
    this.mStartTime = -1L;
    this.mGyroscopeCountdown = 15;
    this.mY = 0.0F;
    this.mX = 0.0F;
    this.mZ = (-this.mUserDistance);
    this.mListener.onEyePositionChanged(this.mX, this.mY, this.mZ);
  }

  public static abstract interface EyePositionListener
  {
    public abstract void onEyePositionChanged(float paramFloat1, float paramFloat2, float paramFloat3);
  }

  private class PositionListener
    implements SensorEventListener
  {
    private PositionListener()
    {
    }

    public void onAccuracyChanged(Sensor paramSensor, int paramInt)
    {
    }

    public void onSensorChanged(SensorEvent paramSensorEvent)
    {
      switch (paramSensorEvent.sensor.getType())
      {
      case 2:
      case 3:
      default:
        return;
      case 4:
        EyePosition.this.onGyroscopeChanged(paramSensorEvent.values[0], paramSensorEvent.values[1], paramSensorEvent.values[2]);
        return;
      case 1:
      }
      EyePosition.this.onAccelerometerChanged(paramSensorEvent.values[0], paramSensorEvent.values[1], paramSensorEvent.values[2]);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.EyePosition
 * JD-Core Version:    0.5.4
 */