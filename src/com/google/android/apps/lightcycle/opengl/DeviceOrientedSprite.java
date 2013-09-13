package com.google.android.apps.lightcycle.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import com.google.android.apps.lightcycle.sensor.DeviceOrientationDetector;
import com.google.android.apps.lightcycle.sensor.DeviceOrientationDetector.DeviceOrientation;

public class DeviceOrientedSprite extends Sprite
{
  final PointF landscapePos = new PointF();
  final PointF landscapePosReverse = new PointF();
  private DeviceOrientationDetector orientationDetector;
  final PointF portraitPos = new PointF();
  final PointF portraitPosReverse = new PointF();
  private SpritePosition position;

  public DeviceOrientedSprite(DeviceOrientationDetector paramDeviceOrientationDetector)
  {
    this.orientationDetector = paramDeviceOrientationDetector;
  }

  public void draw(float[] paramArrayOfFloat)
    throws OpenGLException
  {
    DeviceOrientationDetector.DeviceOrientation localDeviceOrientation = this.orientationDetector.getOrientation();
    PointF localPointF = this.position.getPosition(localDeviceOrientation);
    drawRotated(paramArrayOfFloat, localPointF.x, localPointF.y, -localDeviceOrientation.nearestOrthoAngleDegrees, 1.0F);
  }

  public PointF getPosition()
  {
    return this.position.getPosition(this.orientationDetector.getOrientation());
  }

  public boolean initCentered(Context paramContext, int paramInt1, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt2, int paramInt3)
  {
    if (!super.init2D(paramContext, paramInt1, paramFloat1, paramFloat2))
      return false;
    setPositionsCentered(paramFloat3, paramFloat4, paramInt2, paramInt3);
    return true;
  }

  public boolean initCentered(Bitmap paramBitmap, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2)
  {
    if (!super.init2D(paramBitmap, paramFloat1, paramFloat2))
      return false;
    setPositionsCentered(paramFloat3, paramFloat4, paramInt1, paramInt2);
    return true;
  }

  public void setPositions(PointF paramPointF1, PointF paramPointF2, int paramInt1, int paramInt2)
  {
    PointF localPointF1 = new PointF();
    PointF localPointF2 = new PointF();
    PointF localPointF3 = new PointF();
    PointF localPointF4 = new PointF();
    localPointF1.set(paramPointF2);
    localPointF2.set(paramPointF1);
    localPointF3.set(paramInt1 - localPointF1.x, paramInt2 - localPointF1.y);
    localPointF4.set(paramInt1 - localPointF2.x, paramInt2 - localPointF2.y);
    this.position = new SpritePosition(localPointF1, localPointF2, localPointF3, localPointF4)
    {
      public PointF getPosition(DeviceOrientationDetector.DeviceOrientation paramDeviceOrientation)
      {
        if (paramDeviceOrientation.nearestOrthoAngleDegrees == 0.0F)
          return this.val$pos1;
        if (paramDeviceOrientation.nearestOrthoAngleDegrees == 90.0F)
          return this.val$pos2;
        if (paramDeviceOrientation.nearestOrthoAngleDegrees == 180.0F)
          return this.val$pos3;
        if (paramDeviceOrientation.nearestOrthoAngleDegrees == -90.0F)
          return this.val$pos4;
        throw new RuntimeException("Invalid nearestOrthoAngleDegrees: " + paramDeviceOrientation.nearestOrthoAngleDegrees);
      }
    };
  }

  public void setPositionsCentered(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2)
  {
    setPositions(new PointF(paramFloat1 * paramInt1, paramInt2 / 2.0F), new PointF(paramInt1 / 2.0F, paramFloat2 * paramInt2), paramInt1, paramInt2);
  }

  private static abstract interface SpritePosition
  {
    public abstract PointF getPosition(DeviceOrientationDetector.DeviceOrientation paramDeviceOrientation);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.opengl.DeviceOrientedSprite
 * JD-Core Version:    0.5.4
 */