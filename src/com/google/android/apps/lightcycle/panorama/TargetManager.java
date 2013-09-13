package com.google.android.apps.lightcycle.panorama;

import F;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.FloatMath;
import com.google.android.apps.lightcycle.math.Vector3;
import com.google.android.apps.lightcycle.opengl.OpenGLException;
import com.google.android.apps.lightcycle.opengl.Sprite;
import com.google.android.apps.lightcycle.sensor.DeviceOrientationDetector;
import com.google.android.apps.lightcycle.sensor.DeviceOrientationDetector.DeviceOrientation;
import com.google.android.apps.lightcycle.sensor.SensorReader;
import com.google.android.apps.lightcycle.shaders.ScaledTransparencyShader;
import com.google.android.apps.lightcycle.shaders.TargetShader;
import com.google.android.apps.lightcycle.util.LG;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class TargetManager
{
  private static final float MAX_ANGLE_THRESHOLD_RAD = degreesToRadians(22.0F);
  private static final float MIN_ANGLE_THRESHOLD_RAD = degreesToRadians(12.0F);
  private float activeTargetAlpha = 0.0F;
  private int activeTargetIndex = -1;
  private AlphaScalePair alphaScalePair = new AlphaScalePair(null);
  private float[] currentDeviceTransform = null;
  private DeviceOrientationDetector deviceOrientationDetector = null;
  private float halfSurfaceHeight;
  private float halfSurfaceWidth;
  private float hitTargetAlpha = 0.0F;
  private float[] hitTargetTransform = null;
  private Context mContext;
  private boolean mTargetInRange = false;
  private Map<Integer, float[]> mTargets = Collections.synchronizedMap(new TreeMap());
  private Sprite nearestSpriteOrtho;
  private float[] projected = new float[4];
  private SensorReader sensorReader = null;
  private float targetHitAngleDeg = 2.0F;
  private TargetShader targetShader;
  private Sprite targetSpriteOrtho;
  private float[] tempTransform = new float[16];
  private ScaledTransparencyShader transparencyShader;
  private float[] unitVector = { 0.0F, 0.0F, -1.0F, 1.0F };
  private Sprite viewFinderSprite;
  private Sprite viewfinderActivatedSprite;
  private Point viewfinderCoord;

  public TargetManager(Context paramContext)
  {
    this.mContext = paramContext;
  }

  private void computeProximityAlphaAndScale(float[] paramArrayOfFloat, Vector3 paramVector3, AlphaScalePair paramAlphaScalePair)
  {
    float f1 = (float)Math.acos(new Vector3(-paramArrayOfFloat[8], -paramArrayOfFloat[9], -paramArrayOfFloat[10]).dot(paramVector3));
    if (f1 < MIN_ANGLE_THRESHOLD_RAD)
    {
      paramAlphaScalePair.alpha = 1.0F;
      paramAlphaScalePair.scale = 1.0F;
      return;
    }
    if (f1 < MAX_ANGLE_THRESHOLD_RAD)
    {
      float f2 = MAX_ANGLE_THRESHOLD_RAD - MIN_ANGLE_THRESHOLD_RAD;
      float f3 = 1.0F - (f1 - MIN_ANGLE_THRESHOLD_RAD) / f2;
      paramAlphaScalePair.alpha = (0.0F + f3 * 1.0F);
      paramAlphaScalePair.scale = (0.4F + 0.6F * f3);
      return;
    }
    paramAlphaScalePair.alpha = 0.0F;
    paramAlphaScalePair.scale = 0.4F;
  }

  private static float degreesToRadians(float paramFloat)
  {
    return 0.01745329F * paramFloat;
  }

  private void drawHitTarget(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
    throws OpenGLException
  {
    if (this.hitTargetTransform == null);
    do
    {
      return;
      this.targetShader.bind();
      this.targetShader.setAlpha(this.hitTargetAlpha);
      drawTarget(paramArrayOfFloat1, paramArrayOfFloat2, this.hitTargetTransform, this.nearestSpriteOrtho);
      this.hitTargetAlpha = (0.9F * this.hitTargetAlpha);
    }
    while (this.hitTargetAlpha >= 0.05F);
    this.hitTargetAlpha = 0.0F;
    this.hitTargetTransform = null;
  }

  private void drawTarget(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, Sprite paramSprite)
    throws OpenGLException
  {
    Matrix.multiplyMM(this.tempTransform, 0, paramArrayOfFloat1, 0, paramArrayOfFloat3, 0);
    Matrix.multiplyMV(this.projected, 0, this.tempTransform, 0, this.unitVector, 0);
    normalize(this.projected);
    paramSprite.drawRotated(paramArrayOfFloat2, this.projected[0] * this.halfSurfaceWidth + this.halfSurfaceWidth, this.projected[1] * this.halfSurfaceHeight + this.halfSurfaceHeight, 0.0F, 1.0F);
  }

  private void drawViewfinder(float[] paramArrayOfFloat)
    throws OpenGLException
  {
    float f1 = 90.0F;
    float f2 = 0.4F;
    float f3 = this.deviceOrientationDetector.getOrientation().nearestOrthoAngleDegrees;
    int i;
    if ((f3 == f1) || (f3 == -90.0F))
    {
      i = 1;
      label36: GLES20.glEnable(3042);
      GLES20.glBlendFunc(770, 771);
      if (i == 0)
        break label121;
    }
    while (true)
    {
      if (this.hitTargetAlpha > 0.0F)
        f2 += 1.0F * this.hitTargetAlpha;
      this.transparencyShader.bind();
      this.transparencyShader.setAlpha(f2);
      this.viewFinderSprite.drawRotatedCentered(paramArrayOfFloat, this.viewfinderCoord.x, this.viewfinderCoord.y, f1);
      return;
      i = 0;
      break label36:
      label121: f1 = 0.0F;
    }
  }

  private void normalize(float[] paramArrayOfFloat)
  {
    paramArrayOfFloat[0] /= paramArrayOfFloat[3];
    paramArrayOfFloat[1] /= paramArrayOfFloat[3];
    paramArrayOfFloat[2] /= paramArrayOfFloat[3];
    paramArrayOfFloat[3] = 1.0F;
  }

  private void setRotationTranspose(float[] paramArrayOfFloat1, int paramInt, float[] paramArrayOfFloat2)
  {
    paramArrayOfFloat2[0] = paramArrayOfFloat1[paramInt];
    paramArrayOfFloat2[1] = paramArrayOfFloat1[(paramInt + 1)];
    paramArrayOfFloat2[2] = paramArrayOfFloat1[(paramInt + 2)];
    paramArrayOfFloat2[3] = 0.0F;
    paramArrayOfFloat2[4] = paramArrayOfFloat1[(paramInt + 3)];
    paramArrayOfFloat2[5] = paramArrayOfFloat1[(paramInt + 4)];
    paramArrayOfFloat2[6] = paramArrayOfFloat1[(paramInt + 5)];
    paramArrayOfFloat2[7] = 0.0F;
    paramArrayOfFloat2[8] = paramArrayOfFloat1[(paramInt + 6)];
    paramArrayOfFloat2[9] = paramArrayOfFloat1[(paramInt + 7)];
    paramArrayOfFloat2[10] = paramArrayOfFloat1[(paramInt + 8)];
    paramArrayOfFloat2[11] = 0.0F;
    paramArrayOfFloat2[12] = 0.0F;
    paramArrayOfFloat2[13] = 0.0F;
    paramArrayOfFloat2[14] = 0.0F;
    paramArrayOfFloat2[15] = 1.0F;
  }

  private void setTargetHitAngle()
  {
    LightCycleNative.SetTargetHitAngleRadians(0.01745329F * (2.0F + 1.5F * ((Math.max(Math.min(FloatMath.sqrt(this.sensorReader.getAngularVelocitySquaredRad()), 0.6981317F), 0.1745329F) - 0.1745329F) / 0.5235988F)));
  }

  public void drawTargetsOrthographic(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    int i = LightCycleNative.GetTargetInRange();
    this.activeTargetIndex = i;
    int j;
    label16: label45: Vector3 localVector3;
    if (i >= 0)
    {
      j = 1;
      this.mTargetInRange = j;
      if (i < 0)
        break label448;
      this.activeTargetAlpha += 0.1F * (1.0F - this.activeTargetAlpha);
      setTargetHitAngle();
      localVector3 = new Vector3(-this.currentDeviceTransform[2], -this.currentDeviceTransform[6], -this.currentDeviceTransform[10]);
      GLES20.glBlendFunc(1, 771);
      this.targetShader.bind();
      this.targetShader.setContrastFactor(1.0F);
      this.targetShader.setAlpha(1.0F);
    }
    while (true)
    {
      float f1;
      float f2;
      float f3;
      float f4;
      try
      {
        (-this.currentDeviceTransform[6]);
        synchronized (this.mTargets)
        {
          Iterator localIterator = this.mTargets.entrySet().iterator();
          Map.Entry localEntry;
          do
          {
            if (!localIterator.hasNext())
              break label483;
            localEntry = (Map.Entry)localIterator.next();
            float[] arrayOfFloat = (float[])localEntry.getValue();
            Matrix.multiplyMM(this.tempTransform, 0, paramArrayOfFloat1, 0, arrayOfFloat, 0);
            Matrix.multiplyMV(this.projected, 0, this.tempTransform, 0, this.unitVector, 0);
            computeProximityAlphaAndScale(arrayOfFloat, localVector3, this.alphaScalePair);
            f1 = this.alphaScalePair.alpha;
            f2 = this.alphaScalePair.scale;
            if (this.mTargets.size() != 1)
              continue;
            f1 = Math.max(0.75F, f1);
            f2 = 1.0F;
          }
          while (this.projected[3] < 0.0F);
          normalize(this.projected);
          f3 = this.projected[0] * this.halfSurfaceWidth + this.halfSurfaceWidth;
          f4 = this.projected[1] * this.halfSurfaceHeight + this.halfSurfaceHeight;
          if (((Integer)localEntry.getKey()).intValue() != i)
            break label456;
          float f5 = f1 * (1.0F - this.activeTargetAlpha);
          float f6 = f1 * this.activeTargetAlpha;
          this.targetShader.setAlpha(f6);
          this.nearestSpriteOrtho.drawRotated(paramArrayOfFloat2, f3, f4, 0.0F, f2);
          this.targetShader.setAlpha(f5);
          this.targetSpriteOrtho.drawRotated(paramArrayOfFloat2, f3, f4, 0.0F, f2);
          this.targetShader.setAlpha(1.0F);
        }
      }
      catch (OpenGLException localOpenGLException)
      {
        localOpenGLException.printStackTrace();
        GLES20.glBlendFunc(770, 771);
        return;
      }
      j = 0;
      break label16:
      label448: this.activeTargetAlpha = 0.0F;
      break label45:
      label456: this.targetShader.setAlpha(f1);
      this.targetSpriteOrtho.drawRotated(paramArrayOfFloat2, f3, f4, 0.0F, f2);
      continue;
      label483: monitorexit;
      drawHitTarget(paramArrayOfFloat1, paramArrayOfFloat2);
      drawViewfinder(paramArrayOfFloat2);
    }
  }

  public void init(int paramInt1, int paramInt2)
  {
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inScaled = false;
    BitmapFactory.decodeResource(this.mContext.getResources(), 2130837823, localOptions).recycle();
    this.targetSpriteOrtho = new Sprite();
    this.targetSpriteOrtho.init2D(this.mContext, 2130837823, -1.0F, 1.0F);
    this.nearestSpriteOrtho = new Sprite();
    this.nearestSpriteOrtho.init2D(this.mContext, 2130837822, -1.0F, 1.0F);
    try
    {
      this.targetShader = new TargetShader();
      this.transparencyShader = new ScaledTransparencyShader();
      if (this.targetShader == null)
        LG.d("Failed to create target shader");
      if (this.transparencyShader == null)
        LG.d("Failed to create texture shader");
      this.targetSpriteOrtho.setShader(this.targetShader);
      this.nearestSpriteOrtho.setShader(this.targetShader);
      this.halfSurfaceWidth = (paramInt1 / 2.0F);
      this.halfSurfaceHeight = (paramInt2 / 2.0F);
      float[] arrayOfFloat = new float[16];
      Matrix.setIdentityM(arrayOfFloat, 0);
      this.mTargets.put(Integer.valueOf(0), arrayOfFloat);
      this.viewFinderSprite = new Sprite();
      this.viewFinderSprite.init2D(this.mContext, 2130837819, 4.0F, 1.0F);
      this.viewFinderSprite.setShader(this.transparencyShader);
      this.viewfinderActivatedSprite = new Sprite();
      this.viewfinderActivatedSprite.init2D(this.mContext, 2130837818, 4.0F, 1.0F);
      this.viewfinderActivatedSprite.setShader(this.transparencyShader);
      this.viewfinderCoord = new Point(paramInt1 / 2 - this.viewFinderSprite.getWidth() / 2, paramInt2 / 2 - this.viewFinderSprite.getHeight() / 2);
      return;
    }
    catch (OpenGLException localOpenGLException)
    {
      localOpenGLException.printStackTrace();
    }
  }

  public void reset()
  {
    this.mTargets.clear();
    float[] arrayOfFloat = new float[16];
    Matrix.setIdentityM(arrayOfFloat, 0);
    this.mTargets.put(Integer.valueOf(0), arrayOfFloat);
  }

  public void setCurrentOrientation(float[] paramArrayOfFloat)
  {
    this.currentDeviceTransform = paramArrayOfFloat;
  }

  public void setDeviceOrientationDetector(DeviceOrientationDetector paramDeviceOrientationDetector)
  {
    this.deviceOrientationDetector = paramDeviceOrientationDetector;
  }

  public void setSensorReader(SensorReader paramSensorReader)
  {
    this.sensorReader = paramSensorReader;
  }

  public void updateTargets()
  {
    int[] arrayOfInt = LightCycleNative.GetDeletedTargets();
    NewTarget[] arrayOfNewTarget = LightCycleNative.GetNewTargets();
    if (arrayOfInt != null)
    {
      int k = -1 + arrayOfInt.length;
      if (k >= 0)
      {
        if (arrayOfInt[k] == this.activeTargetIndex)
        {
          label18: float[] arrayOfFloat2 = (float[])this.mTargets.get(Integer.valueOf(arrayOfInt[k]));
          if (arrayOfFloat2 == null)
            break label100;
          this.hitTargetTransform = ((float[])arrayOfFloat2.clone());
        }
        for (this.hitTargetAlpha = 1.0F; ; this.hitTargetAlpha = 0.0F)
        {
          this.mTargets.remove(Integer.valueOf(arrayOfInt[k]));
          --k;
          break label18:
          label100: this.hitTargetTransform = null;
        }
      }
    }
    if (arrayOfNewTarget == null)
      return;
    int i = arrayOfNewTarget.length;
    for (int j = 0; j < i; ++j)
    {
      float[] arrayOfFloat1 = new float[16];
      setRotationTranspose(arrayOfNewTarget[j].orientation, 0, arrayOfFloat1);
      this.mTargets.put(Integer.valueOf(arrayOfNewTarget[j].key), arrayOfFloat1);
    }
    LG.d("Number of targets " + this.mTargets.size());
  }

  private class AlphaScalePair
  {
    float alpha;
    float scale;

    private AlphaScalePair()
    {
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.TargetManager
 * JD-Core Version:    0.5.4
 */