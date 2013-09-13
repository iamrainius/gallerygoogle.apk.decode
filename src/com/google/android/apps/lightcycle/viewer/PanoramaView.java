package com.google.android.apps.lightcycle.viewer;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import com.google.android.apps.lightcycle.sensor.SensorReader;
import com.google.android.apps.lightcycle.util.Callback;

public class PanoramaView extends GLSurfaceView
{
  private static final String TAG = PanoramaView.class.getSimpleName();
  private PointF mDownPos;
  private boolean mIgnoreNextActionUpForThrowing = false;
  private boolean mLastZoom;
  private PointF mMotionLast;
  private float mMotionScale = 1.0F;
  private float mPitchAngleDegrees = 0.0F;
  private PanoramaViewRenderer mRenderer;
  private ThrowController mThrowController = new ThrowController();
  private PointF mThrowDelta = new PointF();
  private long mTimeTouchDown;
  private Callback<Void> mTouchReleaseCallback;
  private float mYawAngleDegrees = 0.0F;
  private float mZoomCurrentDistance;
  private float mZoomStartingDistance;
  private boolean mZooming;

  public PanoramaView(Context paramContext)
  {
    super(paramContext);
    try
    {
      this.mRenderer = new PanoramaViewRenderer(this, paramContext);
      this.mRenderer.setOnInitializedCallback(new Callback()
      {
        public void onCallback(Void paramVoid)
        {
          PanoramaView.this.mRenderer.startIntroAnimation();
        }
      });
      setEGLContextClientVersion(2);
      setRenderer(this.mRenderer);
      setRenderMode(1);
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      Log.v(TAG, "Error creating Panorama view renderer.");
    }
  }

  private void computeAngleChange(float paramFloat1, float paramFloat2)
  {
    float f = this.mRenderer.getOrientation();
    if ((f == 0.0F) || (f == 360.0F))
    {
      this.mYawAngleDegrees += 0.12F * paramFloat1;
      this.mPitchAngleDegrees += 0.12F * paramFloat2;
    }
    do
    {
      return;
      if ((f == 90.0F) || (f == -270.0F))
      {
        this.mYawAngleDegrees += 0.12F * paramFloat2;
        this.mPitchAngleDegrees -= 0.12F * paramFloat1;
        return;
      }
      if ((f != 180.0F) && (f != -180.0F))
        continue;
      this.mYawAngleDegrees -= 0.12F * paramFloat1;
      this.mPitchAngleDegrees -= 0.12F * paramFloat2;
      return;
    }
    while ((f != 270.0F) && (f != -90.0F));
    this.mYawAngleDegrees -= 0.12F * paramFloat2;
    this.mPitchAngleDegrees += 0.12F * paramFloat1;
  }

  private float getPinchDistance(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX(0) - paramMotionEvent.getX(1);
    float f2 = paramMotionEvent.getY(0) - paramMotionEvent.getY(1);
    return FloatMath.sqrt(f1 * f1 + f2 * f2);
  }

  private void stopThrowInProgress()
  {
    this.mThrowController.stopThrow();
  }

  public void onDrawFrame()
  {
    if (!this.mThrowController.getThrowDelta(this.mThrowDelta))
      return;
    PointF localPointF1 = this.mThrowDelta;
    localPointF1.x *= this.mMotionScale;
    PointF localPointF2 = this.mThrowDelta;
    localPointF2.y *= this.mMotionScale;
    computeAngleChange(this.mThrowDelta.x, this.mThrowDelta.y);
    this.mRenderer.setPitchAngleRadians(this.mPitchAngleDegrees);
    this.mRenderer.setYawAngleRadians(this.mYawAngleDegrees);
    PointF localPointF3 = this.mMotionLast;
    localPointF3.x += this.mThrowDelta.x;
    PointF localPointF4 = this.mMotionLast;
    localPointF4.y += this.mThrowDelta.y;
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    switch (0xFF & paramMotionEvent.getAction())
    {
    case 3:
    case 4:
    default:
    case 0:
    case 5:
    case 2:
    case 6:
      while (true)
      {
        label52: return true;
        stopThrowInProgress();
        this.mMotionLast = new PointF(paramMotionEvent.getX(), paramMotionEvent.getY());
        this.mDownPos = new PointF(paramMotionEvent.getX(), paramMotionEvent.getY());
        this.mTimeTouchDown = paramMotionEvent.getEventTime();
        this.mThrowController.onPointerDown(paramMotionEvent.getX(), paramMotionEvent.getY(), paramMotionEvent.getEventTime());
        continue;
        this.mZoomStartingDistance = getPinchDistance(paramMotionEvent);
        this.mZooming = true;
        continue;
        if (paramMotionEvent.getPointerCount() == 1)
          this.mThrowController.onPointerMove(paramMotionEvent.getX(), paramMotionEvent.getY(), paramMotionEvent.getEventTime());
        if (this.mLastZoom)
        {
          this.mMotionLast = new PointF(paramMotionEvent.getX(), paramMotionEvent.getY());
          this.mLastZoom = false;
        }
        if (this.mZooming)
        {
          this.mZoomCurrentDistance = getPinchDistance(paramMotionEvent);
          float f10 = this.mZoomCurrentDistance / this.mZoomStartingDistance;
          this.mRenderer.pinchZoom(f10);
          this.mMotionScale = (this.mRenderer.getCurrentFieldOfViewDegrees() / 90.0F);
        }
        float f4 = this.mMotionLast.x - paramMotionEvent.getX();
        float f5 = this.mMotionLast.y - paramMotionEvent.getY();
        float f6 = f4 * this.mMotionScale;
        float f7 = f5 * this.mMotionScale;
        this.mYawAngleDegrees = this.mRenderer.getTargetYawDegrees();
        this.mPitchAngleDegrees = this.mRenderer.getTargetPitchDegrees();
        computeAngleChange(f6, f7);
        float f8 = this.mDownPos.x - paramMotionEvent.getX();
        float f9 = this.mDownPos.y - paramMotionEvent.getY();
        if (Math.hypot(f8, f9) >= 4.0D)
        {
          this.mRenderer.setPitchAngleRadians(this.mPitchAngleDegrees);
          this.mRenderer.setYawAngleRadians(this.mYawAngleDegrees);
        }
        this.mMotionLast.x = paramMotionEvent.getX();
        this.mMotionLast.y = paramMotionEvent.getY();
        continue;
        this.mZooming = false;
        float f3 = this.mZoomCurrentDistance / this.mZoomStartingDistance;
        this.mRenderer.endPinchZoom(f3);
        this.mLastZoom = true;
        this.mIgnoreNextActionUpForThrowing = true;
      }
    case 1:
    }
    paramMotionEvent.getX();
    paramMotionEvent.getY();
    float f1 = this.mDownPos.x - paramMotionEvent.getX();
    float f2 = this.mDownPos.y - paramMotionEvent.getY();
    double d = Math.hypot(f1, f2);
    if ((paramMotionEvent.getEventTime() - this.mTimeTouchDown < 400L) && (d < 10.0D))
      this.mRenderer.toggleAutoSpin();
    this.mLastZoom = false;
    if (!this.mIgnoreNextActionUpForThrowing)
      this.mThrowController.onPointerUp(paramMotionEvent.getX(), paramMotionEvent.getY(), paramMotionEvent.getEventTime());
    while (true)
    {
      if (this.mTouchReleaseCallback != null);
      this.mTouchReleaseCallback.onCallback(null);
      break label52:
      this.mIgnoreNextActionUpForThrowing = false;
    }
  }

  public void setAutoRotationCallback(Callback<Boolean> paramCallback)
  {
    this.mRenderer.setAutoRotationCallback(paramCallback);
  }

  public void setPanoramaImage(PanoramaImage paramPanoramaImage)
  {
    this.mRenderer.setPanoramaImage(paramPanoramaImage);
  }

  public void setSensorReader(Display paramDisplay, SensorReader paramSensorReader)
  {
    this.mRenderer.setSensorReader(paramDisplay, paramSensorReader);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.viewer.PanoramaView
 * JD-Core Version:    0.5.4
 */