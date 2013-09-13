package com.google.android.apps.lightcycle.panorama;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.os.SystemClock;
import android.util.Log;
import com.google.android.apps.lightcycle.opengl.DeviceOrientedSprite;
import com.google.android.apps.lightcycle.opengl.OpenGLException;
import com.google.android.apps.lightcycle.opengl.SimpleTextureShader;
import com.google.android.apps.lightcycle.sensor.DeviceOrientationDetector;
import com.google.android.apps.lightcycle.sensor.SensorReader;
import com.google.android.apps.lightcycle.shaders.ScaledTransparencyShader;
import com.google.android.apps.lightcycle.util.LG;

public class CountdownDisplay
{
  private Context mContext;
  private boolean mFinished = false;
  private DeviceOrientedSprite mLightOff;
  private ScaledTransparencyShader mLightOffShader;
  private DeviceOrientedSprite mLightOn;
  private SimpleTextureShader mLightOnShader;
  private boolean[] mLightStates = new boolean[3];
  private DeviceOrientationDetector mOrientationDetector;
  private PointF[] mPositionsLandscape = new PointF[3];
  private PointF[] mPositionsPortrait = new PointF[3];
  private boolean mRunning = false;
  private SensorReader mSensorReader;
  private long mStartTime;

  CountdownDisplay(Context paramContext)
  {
    this.mContext = paramContext;
  }

  private void turnOffLights()
  {
    for (int i = 0; i < 3; ++i)
      this.mLightStates[i] = false;
  }

  private void updateLights()
  {
    int i = 1 + (int)((SystemClock.uptimeMillis() - this.mStartTime) / 800L);
    for (int j = 0; j < i; ++j)
      this.mLightStates[j] = true;
    if (i < 3)
      return;
    this.mRunning = false;
    this.mFinished = true;
    long l = SystemClock.uptimeMillis() - this.mStartTime;
    int k = this.mSensorReader.getNumGyroSamples();
    float[] arrayOfFloat = LightCycleNative.EndGyroCalibration(this.mSensorReader.getAndResetGyroData(), k, l);
    LG.d("Bias : " + arrayOfFloat[0] + ", " + arrayOfFloat[1] + ", " + arrayOfFloat[2]);
    this.mSensorReader.setGyroBias(arrayOfFloat);
  }

  public void draw(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    updateLights();
    GLES20.glBlendFunc(770, 771);
    for (int i = 0; ; ++i)
    {
      if (i < 3);
      try
      {
        if (this.mLightStates[i] != 0)
        {
          this.mLightOn.setPositions(this.mPositionsLandscape[i], this.mPositionsPortrait[i], paramInt1, paramInt2);
          this.mLightOn.draw(paramArrayOfFloat);
        }
        else
        {
          this.mLightOff.setPositions(this.mPositionsLandscape[i], this.mPositionsPortrait[i], paramInt1, paramInt2);
          this.mLightOffShader.bind();
          this.mLightOffShader.setAlpha(0.3F);
          this.mLightOff.draw(paramArrayOfFloat);
        }
      }
      catch (OpenGLException localOpenGLException)
      {
        localOpenGLException.printStackTrace();
        GLES20.glBlendFunc(770, 771);
        return;
      }
    }
  }

  public boolean finished()
  {
    return this.mFinished;
  }

  public void init(int paramInt1, int paramInt2, SensorReader paramSensorReader, DeviceOrientationDetector paramDeviceOrientationDetector)
  {
    this.mSensorReader = paramSensorReader;
    this.mOrientationDetector = paramDeviceOrientationDetector;
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inScaled = false;
    localOptions.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(this.mContext.getResources(), 2130837822, localOptions);
    float f1;
    if (paramInt1 < paramInt2)
      f1 = paramInt2 / paramInt1;
    while (true)
    {
      (0.035625F * f1 * paramInt1 / localOptions.outWidth);
      this.mLightOff = new DeviceOrientedSprite(this.mOrientationDetector);
      this.mLightOff.init2D(this.mContext, 2130837821, -1.0F, 1.0F);
      this.mLightOn = new DeviceOrientedSprite(this.mOrientationDetector);
      this.mLightOn.init2D(this.mContext, 2130837820, -1.0F, 1.0F);
      label161: float f2;
      float f3;
      float f4;
      try
      {
        this.mLightOffShader = new ScaledTransparencyShader();
        this.mLightOnShader = new SimpleTextureShader();
        if ((this.mLightOffShader == null) || (this.mLightOnShader == null))
          LG.d("Failed to initialize - shader is null.");
        this.mLightOn.setShader(this.mLightOnShader);
        this.mLightOff.setShader(this.mLightOffShader);
        f2 = f1 * (0.056875F * paramInt1);
        f3 = f1 * (0.035625F * paramInt1);
        f4 = 3.0F * f3 + 2.0F * f2;
        float f5 = (paramInt1 - f4) / 2.0F + f3 / 2.0F;
        float f6 = 0.75F * paramInt2;
        for (int i = 0; ; ++i)
        {
          if (i >= 3)
            break label328;
          this.mPositionsPortrait[i] = new PointF(f5, f6);
          f5 += f3 + f2;
          this.mLightStates[i] = false;
        }
        label328: f1 = 1.0F;
      }
      catch (OpenGLException localOpenGLException)
      {
        Log.e("LightCycle", localOpenGLException.getMessage());
        break label161:
        float f7 = 0.75F * paramInt1;
        float f8 = (f4 + paramInt2) / 2.0F - f3 / 2.0F;
        for (int j = 0; j < 3; ++j)
        {
          this.mPositionsLandscape[j] = new PointF(f7, f8);
          f8 -= f3 + f2;
        }
      }
    }
  }

  public void reset()
  {
    this.mRunning = false;
    this.mFinished = false;
  }

  public boolean running()
  {
    return this.mRunning;
  }

  public void startCountdown()
  {
    this.mSensorReader.resetGyroBias();
    this.mSensorReader.getAndResetGyroData();
    this.mStartTime = SystemClock.uptimeMillis();
    LightCycleNative.StartGyroCalibration(this.mSensorReader.getImuOrientationDegrees());
    this.mRunning = true;
    this.mFinished = false;
    turnOffLights();
  }

  public void stopCountdown()
  {
    int i = this.mSensorReader.getNumGyroSamples();
    LightCycleNative.EndGyroCalibration(this.mSensorReader.getAndResetGyroData(), i, SystemClock.uptimeMillis() - this.mStartTime);
    this.mRunning = false;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.CountdownDisplay
 * JD-Core Version:    0.5.4
 */