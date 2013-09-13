package com.google.android.apps.lightcycle.viewer;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Display;
import com.google.android.apps.lightcycle.Constants;
import com.google.android.apps.lightcycle.opengl.OpenGLException;
import com.google.android.apps.lightcycle.opengl.PartialSphere;
import com.google.android.apps.lightcycle.opengl.SingleColorShader;
import com.google.android.apps.lightcycle.opengl.Sphere;
import com.google.android.apps.lightcycle.opengl.TexturedCube;
import com.google.android.apps.lightcycle.sensor.SensorReader;
import com.google.android.apps.lightcycle.shaders.TransparencyShader;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.LG;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class PanoramaViewRenderer
  implements GLSurfaceView.Renderer
{
  private static final String TAG = PanoramaViewRenderer.class.getSimpleName();
  private Callback<Boolean> mAutoRotationCallback = null;
  private boolean mAutoSpin = false;
  private float mAutoSpinFadeFactor = 0.0F;
  private float mAutospinRateDegrees = 0.35F;
  private boolean mCompassMode = false;
  private final Context mContext;
  private float mCurFieldOfViewDegrees = 75.0F;
  private float mFieldOfViewDegreesZoomStart = 75.0F;
  private float[] mFrameTransform = new float[16];
  private PanoramaImage mImage;
  private float mIntroSpinAngleLeftDegrees = 0.0F;
  private float[] mMVPMatrix = new float[16];
  private float[] mModelView = new float[16];
  private boolean mObjectsInitialized = false;
  private Callback<Void> mOnInitializedCallback = null;
  private float mOrientationAngleDegrees = 0.0F;
  private float mOrientationAngleDegreesTarget = 0.0F;
  private boolean mOrientationInitialized = false;
  private TransparencyShader mPanoSphereShader;
  private float mPanoramaOpacity = 0.0F;
  private PartialSphere mPanoramaSphere;
  private PanoramaView mPanoramaView = null;
  private float[] mPerspective = new float[16];
  private float mPitchAngleDegrees = 0.0F;
  private float[] mRotate90 = new float[16];
  private SensorReader mSensorReader = null;
  private int mSurfaceHeight;
  private int mSurfaceWidth;
  private float[] mTempMatrix = new float[16];
  private TexturedCube mTexturedCube;
  private SingleColorShader mWireShader;
  private Sphere mWireSphere;
  private float mYawAngleDegrees = 0.0F;

  public PanoramaViewRenderer(PanoramaView paramPanoramaView, Context paramContext)
  {
    this.mPanoramaView = paramPanoramaView;
    this.mContext = paramContext;
  }

  private void drawScene()
  {
    setView();
    initFrame();
    while (true)
      try
      {
        if (this.mCompassMode)
        {
          float[] arrayOfFloat = this.mSensorReader.getFilterOutput();
          Matrix.multiplyMM(this.mTempMatrix, 0, this.mModelView, 0, arrayOfFloat, 0);
          Matrix.multiplyMM(this.mMVPMatrix, 0, this.mPerspective, 0, this.mTempMatrix, 0);
          this.mTexturedCube.draw(this.mMVPMatrix);
          GLES20.glLineWidth(1.0F);
          GLES20.glEnable(3042);
          GLES20.glBlendFunc(770, 771);
          this.mWireShader.setColor(Constants.TRANSPARENT_GRAY);
          this.mPanoSphereShader.bind();
          this.mPanoSphereShader.setAlpha(this.mPanoramaOpacity);
          this.mPanoramaSphere.draw(this.mMVPMatrix);
          return;
        }
        Matrix.rotateM(this.mModelView, 0, -this.mOrientationAngleDegrees, 0.0F, 0.0F, 1.0F);
        Matrix.rotateM(this.mModelView, 0, this.mPitchAngleDegrees, 1.0F, 0.0F, 0.0F);
        Matrix.rotateM(this.mModelView, 0, this.mYawAngleDegrees, 0.0F, 1.0F, 0.0F);
        Matrix.multiplyMM(this.mMVPMatrix, 0, this.mPerspective, 0, this.mModelView, 0);
      }
      catch (OpenGLException localOpenGLException)
      {
        localOpenGLException.printStackTrace();
      }
  }

  private int getMaxTextureSize()
  {
    int[] arrayOfInt = new int[1];
    GLES20.glGetIntegerv(3379, arrayOfInt, 0);
    return arrayOfInt[0];
  }

  private void initFrame()
  {
    GLES20.glViewport(0, 0, this.mSurfaceWidth, this.mSurfaceHeight);
    GLES20.glClear(16384);
    GLES20.glClear(256);
    GLES20.glEnable(2929);
  }

  private void initRendering()
    throws OpenGLException
  {
    this.mWireShader = new SingleColorShader();
    this.mPanoSphereShader = new TransparencyShader();
    this.mWireShader.setColor(Constants.ANDROID_BLUE);
    this.mImage.setMaximumTextureSize(getMaxTextureSize());
    this.mPanoramaSphere = new PartialSphere(this.mImage, 4.9F);
    this.mPanoramaSphere.setShader(this.mPanoSphereShader);
    this.mWireSphere = new Sphere(24, 48, 8.0F);
    this.mWireSphere.setLineDrawing(true);
    this.mWireSphere.setShader(this.mWireShader);
    Matrix.setIdentityM(this.mRotate90, 0);
    this.mRotate90[0] = 0.0F;
    this.mRotate90[1] = -1.0F;
    this.mRotate90[4] = 1.0F;
    this.mRotate90[5] = 0.0F;
    Matrix.setIdentityM(this.mFrameTransform, 0);
    GLES20.glClearColor(Constants.BACKGROUND_BLACK[0], Constants.BACKGROUND_BLACK[1], Constants.BACKGROUND_BLACK[2], Constants.BACKGROUND_BLACK[3]);
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inScaled = false;
    this.mTexturedCube = new TexturedCube(BitmapFactory.decodeResource(this.mContext.getResources(), 2130837573, localOptions), 32.0F);
    LG.d("Rendering objects are intialized.");
    this.mObjectsInitialized = true;
    this.mOnInitializedCallback.onCallback(null);
  }

  private void onAutoRotationStateChanged()
  {
    if (this.mAutoRotationCallback == null)
      return;
    this.mAutoRotationCallback.onCallback(Boolean.valueOf(this.mAutoSpin));
  }

  private void setView()
  {
    float f = this.mSurfaceWidth / this.mSurfaceHeight;
    setPerspective(this.mCurFieldOfViewDegrees, f, 0.5F, 200.0F);
    Matrix.setIdentityM(this.mModelView, 0);
    this.mOrientationAngleDegrees += 0.08F * (this.mOrientationAngleDegreesTarget - this.mOrientationAngleDegrees);
    Matrix.multiplyMM(this.mMVPMatrix, 0, this.mPerspective, 0, this.mModelView, 0);
  }

  private void updateFieldOfViewDegrees(float paramFloat)
  {
    this.mCurFieldOfViewDegrees = (this.mFieldOfViewDegreesZoomStart / paramFloat);
    this.mCurFieldOfViewDegrees = Math.min(this.mCurFieldOfViewDegrees, 90.0F);
    this.mCurFieldOfViewDegrees = Math.max(this.mCurFieldOfViewDegrees, 20.0F);
    this.mAutospinRateDegrees = (0.35F * (this.mCurFieldOfViewDegrees / 90.0F));
  }

  public void endPinchZoom(float paramFloat)
  {
    updateFieldOfViewDegrees(paramFloat);
    this.mFieldOfViewDegreesZoomStart = this.mCurFieldOfViewDegrees;
  }

  public float getCurrentFieldOfViewDegrees()
  {
    return this.mCurFieldOfViewDegrees;
  }

  public float getOrientation()
  {
    return this.mOrientationAngleDegreesTarget;
  }

  public float getTargetPitchDegrees()
  {
    return this.mPitchAngleDegrees;
  }

  public float getTargetYawDegrees()
  {
    return this.mYawAngleDegrees;
  }

  public void onDrawFrame(GL10 paramGL10)
  {
    if (!this.mObjectsInitialized)
      return;
    if (this.mPanoramaView != null)
      this.mPanoramaView.onDrawFrame();
    if (this.mAutoSpin)
    {
      this.mYawAngleDegrees += this.mAutospinRateDegrees;
      this.mAutoSpinFadeFactor = 1.0F;
      if (this.mIntroSpinAngleLeftDegrees > 0.0F)
      {
        this.mIntroSpinAngleLeftDegrees -= this.mAutospinRateDegrees;
        if (this.mIntroSpinAngleLeftDegrees <= 0.0F)
        {
          this.mAutoSpin = false;
          onAutoRotationStateChanged();
        }
      }
    }
    while (true)
    {
      this.mPanoramaOpacity += 0.05F * (1.0F - this.mPanoramaOpacity);
      if (this.mObjectsInitialized);
      drawScene();
      return;
      if (this.mAutoSpinFadeFactor <= 0.0002F)
        continue;
      this.mYawAngleDegrees += this.mAutospinRateDegrees * this.mAutoSpinFadeFactor;
      this.mAutoSpinFadeFactor = (0.92F * this.mAutoSpinFadeFactor);
    }
  }

  public void onSurfaceChanged(GL10 paramGL10, int paramInt1, int paramInt2)
  {
    if (this.mImage == null)
    {
      Log.d(TAG, "Image file not set. Cannot initialize rendering.");
      return;
    }
    this.mSurfaceWidth = paramInt1;
    this.mSurfaceHeight = paramInt2;
    try
    {
      initRendering();
      LG.d("Rendering init completed.");
      return;
    }
    catch (OpenGLException localOpenGLException)
    {
      localOpenGLException.printStackTrace();
    }
  }

  public void onSurfaceCreated(GL10 paramGL10, EGLConfig paramEGLConfig)
  {
  }

  public void pinchZoom(float paramFloat)
  {
    updateFieldOfViewDegrees(paramFloat);
  }

  public void setAutoRotationCallback(Callback<Boolean> paramCallback)
  {
    this.mAutoRotationCallback = paramCallback;
  }

  public void setOnInitializedCallback(Callback<Void> paramCallback)
  {
    this.mOnInitializedCallback = paramCallback;
    if (!this.mObjectsInitialized)
      return;
    paramCallback.onCallback(null);
  }

  public void setPanoramaImage(PanoramaImage paramPanoramaImage)
  {
    this.mImage = paramPanoramaImage;
  }

  public void setPerspective(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    float f1 = paramFloat3 * (float)Math.tan(3.141592653589793D * (paramFloat1 / 360.0D));
    float f2 = f1 * paramFloat2;
    Matrix.frustumM(this.mPerspective, 0, -f2, f2, -f1, f1, paramFloat3, paramFloat4);
  }

  public void setPitchAngleRadians(float paramFloat)
  {
    this.mPitchAngleDegrees = paramFloat;
    this.mAutoSpin = false;
    onAutoRotationStateChanged();
  }

  public void setSensorReader(Display paramDisplay, SensorReader paramSensorReader)
  {
    this.mSensorReader = paramSensorReader;
  }

  public void setYawAngleRadians(float paramFloat)
  {
    this.mYawAngleDegrees = paramFloat;
    this.mAutoSpin = false;
    onAutoRotationStateChanged();
  }

  public void startIntroAnimation()
  {
    this.mIntroSpinAngleLeftDegrees = 25.0F;
    this.mYawAngleDegrees = -30.0F;
    this.mAutoSpin = true;
    onAutoRotationStateChanged();
  }

  public void toggleAutoSpin()
  {
    if (!this.mAutoSpin);
    for (int i = 1; ; i = 0)
    {
      this.mAutoSpin = i;
      onAutoRotationStateChanged();
      return;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.viewer.PanoramaViewRenderer
 * JD-Core Version:    0.5.4
 */