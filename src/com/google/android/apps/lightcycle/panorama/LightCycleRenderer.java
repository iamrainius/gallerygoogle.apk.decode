package com.google.android.apps.lightcycle.panorama;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.view.Display;
import com.google.android.apps.lightcycle.Constants;
import com.google.android.apps.lightcycle.camera.CameraPreview;
import com.google.android.apps.lightcycle.math.Vector3;
import com.google.android.apps.lightcycle.opengl.GLTexture;
import com.google.android.apps.lightcycle.opengl.OpenGLException;
import com.google.android.apps.lightcycle.opengl.SimpleTextureShader;
import com.google.android.apps.lightcycle.opengl.SingleColorShader;
import com.google.android.apps.lightcycle.opengl.Sphere;
import com.google.android.apps.lightcycle.opengl.TexturedCube;
import com.google.android.apps.lightcycle.sensor.DeviceOrientationDetector;
import com.google.android.apps.lightcycle.sensor.SensorReader;
import com.google.android.apps.lightcycle.shaders.PanoSphereShader;
import com.google.android.apps.lightcycle.shaders.TransparencyShader;
import com.google.android.apps.lightcycle.util.LG;
import java.util.Calendar;
import java.util.Vector;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LightCycleRenderer
  implements GLSurfaceView.Renderer
{
  private int kFpsLoggingInterval = 30;
  private boolean mAddNextFrame = false;
  private float mAnimationFovTargetDegrees;
  private int mBlankFrames = 0;
  private boolean mBlankPending = false;
  private final Activity mContext;
  private CountdownDisplay mCountdownDisplay;
  private float mCurFieldOfViewDegrees = 100.0F;
  private float mCurFieldOfViewDegreesScaled = 100.0F;
  private int mCurrentFrameTexture;
  private double mDeltaHeading = 0.0D;
  private Vector<Double> mDeltaHeadingStack = new Vector();
  private double mDeltaHeadingStep = 0.0D;
  private float mDeviceFieldOfViewDegrees = -1.0F;
  private boolean mDisablePhotoTaking = false;
  private boolean mDisplayFrameImage = true;
  private long mFPSStartTime;
  private float mFieldOfViewDegreesZoomStart = 60.0F;
  private boolean mFlatDisplayMode = false;
  private int mFrameCount = 0;
  private int mFrameHeight = 0;
  private final PanoramaFrameOverlay mFrameOverlay = new PanoramaFrameOverlay();
  private boolean mFramePending = false;
  private float[] mFrameTransform = new float[16];
  private int mFrameWidth = 0;
  private long mHoldStillStartTimeNs = 0L;
  private boolean mHoldStillTargetHit = false;
  private boolean mHoldStillTimerStarted = false;
  private byte[] mImageData = null;
  private boolean mInitialized = false;
  private boolean mIntroAnimating = true;
  private int mIntroFrameCount = 0;
  private float[] mMVPMatrix = new float[16];
  private int mMaxFieldOfViewDegrees = 120;
  private MessageDisplay mMessageDisplay;
  private int mMinFieldOfViewDegrees = 80;
  private float[] mModelView = new float[16];
  private boolean mMovingTooFast = false;
  private DeviceOrientationDetector mOrientationDetector;
  private float[] mOrthographic = new float[16];
  private PanoSphereShader mPanoSphereShader;
  private boolean mPanoUpdate;
  private boolean mPanoramaEmpty = true;
  private Sphere mPanoramaSphere;
  private float[] mPerspective = new float[16];
  private PhotoCollection mPhotoCollection;
  private boolean mPhotoInProgress = false;
  private Pano2dPreviewOverlay mPreview2dOverlay;
  private int mPreviousFrameTexture;
  private final boolean mRealtimeAlignmentEnabled;
  private boolean mRenderBlankScreen = false;
  private boolean mRenderPending = false;
  private boolean mRenderTexturedPreview = false;
  private final RenderedGui mRenderedGui;
  private float[] mRotate90 = new float[16];
  private SensorReader mSensorReader;
  private int mSurfaceHeight;
  private int mSurfaceWidth;
  private TargetManager mTargetManager;
  private float[] mTempMVPMatrix = new float[16];
  private float[] mTempMatrix = new float[16];
  private float[] mTestMatrix = new float[16];
  private SimpleTextureShader mTextureShader;
  private TexturedCube mTexturedCube;
  private boolean mTexturesInitialized = false;
  private TiledGroundPlaneDrawable mTiledGroundPlane;
  private TransparencyShader mTransparencyShader;
  private boolean mUpdateTextures = false;
  private boolean mUseBlendedPreview = false;
  private boolean mValidEstimate;
  private VideoFrameProcessor mVideoFrameProcessor = null;
  private LightCycleView mView;
  private SingleColorShader mWireShader;
  private Sphere mWireSphere;
  private boolean mZooming = false;
  private Vector3 newForwardVec = new Vector3();
  private Vector3 oldForwardVec = new Vector3();
  private boolean renderingStopped = false;
  private UpdatePhotoRendering transformsCallback = new UpdatePhotoRendering()
  {
    public void thumbnailLoaded(int paramInt)
    {
      LG.d("UpdatePhotoRendering:thumbnailLoaded index = " + paramInt);
      LightCycleRenderer.this.mPhotoCollection.thumbnailLoaded(paramInt);
      LightCycleRenderer.access$1202(LightCycleRenderer.this, true);
    }

    public void updateTransforms(float[] paramArrayOfFloat)
    {
      int i = paramArrayOfFloat.length / 9;
      if (LightCycleRenderer.this.mPhotoCollection.getNumFrames() > i)
      {
        LG.d("Bad number of Transforms in UpdateTransformsCallback.");
        return;
      }
      int j = i - 1;
      boolean bool = LightCycleNative.IsImageInLargestComponent(j);
      LG.d("new image " + j + " in largest component = " + bool);
      if ((bool) && (i > 1))
      {
        float[] arrayOfFloat = LightCycleRenderer.this.mPhotoCollection.getCameraToWorld(j);
        LightCycleRenderer.this.oldForwardVec.x = arrayOfFloat[2];
        LightCycleRenderer.this.oldForwardVec.y = 0.0F;
        LightCycleRenderer.this.oldForwardVec.z = arrayOfFloat[8];
        LightCycleRenderer.this.oldForwardVec.normalize();
        int k = j * 9;
        LightCycleRenderer.this.newForwardVec.x = paramArrayOfFloat[(k + 2)];
        LightCycleRenderer.this.newForwardVec.y = 0.0F;
        LightCycleRenderer.this.newForwardVec.z = paramArrayOfFloat[(k + 8)];
        LightCycleRenderer.this.newForwardVec.normalize();
        double d = Math.max(Math.min(LightCycleRenderer.this.oldForwardVec.dot(LightCycleRenderer.this.newForwardVec), 1.0D), -1.0D);
        LightCycleRenderer.access$918(LightCycleRenderer.this, Math.toDegrees(Math.acos(d)));
        if (LightCycleRenderer.this.oldForwardVec.z * LightCycleRenderer.this.newForwardVec.x - LightCycleRenderer.this.oldForwardVec.x * LightCycleRenderer.this.newForwardVec.z > 0.0D)
          LightCycleRenderer.access$902(LightCycleRenderer.this, -LightCycleRenderer.this.mDeltaHeading);
        LightCycleRenderer.access$1002(LightCycleRenderer.this, LightCycleRenderer.this.mDeltaHeading / 45.0D);
        LG.d("vision heading update delta = " + LightCycleRenderer.this.mDeltaHeading);
        for (int l = LightCycleRenderer.this.mDeltaHeadingStack.size(); l < i - 1; ++l)
          LightCycleRenderer.this.mDeltaHeadingStack.add(Double.valueOf(0.0D));
        LightCycleRenderer.this.mDeltaHeadingStack.add(Double.valueOf(LightCycleRenderer.this.mDeltaHeading));
      }
      LightCycleRenderer.this.mPhotoCollection.updateTransforms(paramArrayOfFloat);
    }
  };

  public LightCycleRenderer(Activity paramActivity, RenderedGui paramRenderedGui, boolean paramBoolean)
  {
    this.mContext = paramActivity;
    this.mRenderedGui = paramRenderedGui;
    this.mRealtimeAlignmentEnabled = paramBoolean;
    this.mCurFieldOfViewDegrees = DeviceManager.getOpenGlDefaultFieldOfViewDegrees();
    this.mFieldOfViewDegreesZoomStart = this.mCurFieldOfViewDegrees;
    this.mMaxFieldOfViewDegrees = (int)DeviceManager.getOpenGlMaxFieldOfViewDegrees();
    this.mMinFieldOfViewDegrees = (int)DeviceManager.getOpenGlMinFieldOfViewDegrees();
    this.mCurFieldOfViewDegreesScaled = scaleFov(this.mCurFieldOfViewDegrees);
    LightCycleNative.setUpdatePhotoRenderingCallback(this.transformsCallback);
  }

  private void animateIntro()
  {
    if (this.mDeviceFieldOfViewDegrees < 0.0F)
      this.mDeviceFieldOfViewDegrees = DeviceManager.getCameraFieldOfViewDegrees(this.mView.getCameraPreview().getReportedHorizontalFovDegrees());
    setView();
    if (this.mIntroFrameCount == 0)
    {
      this.mAnimationFovTargetDegrees = this.mCurFieldOfViewDegrees;
      this.mCurFieldOfViewDegrees = this.mDeviceFieldOfViewDegrees;
      this.mCurFieldOfViewDegreesScaled = scaleFov(this.mCurFieldOfViewDegrees);
    }
    this.mSensorReader.setHeadingDegrees(0.0D);
    try
    {
      initFrame();
      setView();
      float[] arrayOfFloat = this.mSensorReader.getFilterOutput();
      Matrix.multiplyMM(this.mTempMatrix, 0, this.mModelView, 0, arrayOfFloat, 0);
      Matrix.multiplyMM(this.mMVPMatrix, 0, this.mPerspective, 0, this.mTempMatrix, 0);
      this.mTexturedCube.drawObject(this.mMVPMatrix);
      GLES20.glLineWidth(1.0F);
      GLES20.glEnable(3042);
      GLES20.glBlendFunc(770, 771);
      this.mWireShader.setColor(Constants.TRANSPARENT_GRAY);
      this.mTiledGroundPlane.draw(this.mMVPMatrix);
      Matrix.setIdentityM(this.mModelView, 0);
      Matrix.rotateM(this.mModelView, 0, 180.0F, 1.0F, 0.0F, 0.0F);
      float f = this.mOrientationDetector.getDisplayInitialOrientationDegrees() - this.mSensorReader.getImuOrientationDegrees();
      Matrix.rotateM(this.mModelView, 0, f, 0.0F, 0.0F, 1.0F);
      Matrix.multiplyMM(this.mMVPMatrix, 0, this.mPerspective, 0, this.mModelView, 0);
      this.mWireShader.setColor(Constants.TRANSPARENT_WHITE);
      this.mFrameOverlay.setDrawOutlineOnly(true);
      this.mFrameOverlay.draw(this.mMVPMatrix);
      this.mCurFieldOfViewDegrees += 0.17F * (this.mAnimationFovTargetDegrees - this.mCurFieldOfViewDegrees);
      this.mIntroFrameCount = (1 + this.mIntroFrameCount);
      if (this.mIntroFrameCount == 25)
      {
        this.mIntroAnimating = false;
        this.mCurFieldOfViewDegrees = this.mAnimationFovTargetDegrees;
      }
      this.mCurFieldOfViewDegreesScaled = scaleFov(this.mCurFieldOfViewDegrees);
      this.mView.requestRender();
      return;
    }
    catch (OpenGLException localOpenGLException)
    {
      localOpenGLException.printStackTrace();
    }
  }

  private void createTextures()
  {
    LG.d("Creating textures");
    int i = GLTexture.createStandardTexture();
    LightCycleNative.InitTexture(i);
    this.mPanoramaSphere.createTexture(i);
    this.mPreview2dOverlay.setTextureId(i);
    int j = GLTexture.createStandardTexture();
    LightCycleNative.InitFrameTexture(j, this.mFrameWidth, this.mFrameHeight);
    this.mFrameOverlay.createTexture(j);
    this.mTexturesInitialized = true;
    this.mCurrentFrameTexture = GLTexture.createNNTexture();
    LightCycleNative.InitFrameTexture(this.mCurrentFrameTexture, this.mFrameWidth, this.mFrameHeight);
    this.mPreviousFrameTexture = GLTexture.createNNTexture();
    LightCycleNative.InitFrameTexture(this.mPreviousFrameTexture, this.mFrameWidth, this.mFrameHeight);
    LG.d("Finished creating textures.");
  }

  private void drawScene(int paramInt)
  {
    setView();
    updateTextures();
    int i;
    label20: boolean bool1;
    label40: boolean bool2;
    if (this.mPhotoCollection.getNumFrames() == 0)
    {
      i = 1;
      this.mRenderTexturedPreview = i;
      PanoramaFrameOverlay localPanoramaFrameOverlay = this.mFrameOverlay;
      if (this.mRenderTexturedPreview)
        break label237;
      bool1 = true;
      localPanoramaFrameOverlay.setDrawOutlineOnly(bool1);
      this.mFrameOverlay.setTextureId(paramInt);
      RenderedGui localRenderedGui = this.mRenderedGui;
      if ((this.mPhotoCollection.getNumFrames() < 1) || (this.mView.isProcessingAlignment()))
        break label243;
      bool2 = true;
      localRenderedGui.setUndoButtonEnabled(bool2);
      initFrame();
    }
    label226: label237: label243: int j;
    try
    {
      Matrix.multiplyMM(this.mTempMatrix, 0, this.mModelView, 0, this.mFrameTransform, 0);
      Matrix.multiplyMM(this.mMVPMatrix, 0, this.mPerspective, 0, this.mTempMatrix, 0);
      this.mTexturedCube.drawObject(this.mMVPMatrix);
      this.mTiledGroundPlane.drawObject(this.mMVPMatrix);
      GLES20.glLineWidth(1.0F);
      GLES20.glEnable(3042);
      GLES20.glBlendFunc(770, 771);
      this.mWireShader.setColor(Constants.TRANSPARENT_GRAY);
      if ((this.mRenderBlankScreen) && (this.mBlankPending))
      {
        this.mRenderPending = false;
        int k = this.mBlankFrames;
        this.mBlankFrames = (k + 1);
        if (k == 4)
          this.mBlankPending = false;
        this.mRenderPending = false;
        return;
        i = 0;
        break label20:
        bool1 = false;
        break label40:
        bool2 = false;
      }
      if (!this.mUseBlendedPreview)
        break label705;
      this.mPanoramaSphere.draw(this.mMVPMatrix);
      if (!this.mFlatDisplayMode)
      {
        label267: Matrix.setIdentityM(this.mModelView, 0);
        float f = this.mOrientationDetector.getDisplayInitialOrientationDegrees() - this.mSensorReader.getImuOrientationDegrees();
        Matrix.rotateM(this.mModelView, 0, f, 0.0F, 0.0F, 1.0F);
        Matrix.rotateM(this.mModelView, 0, 180.0F, 1.0F, 0.0F, 0.0F);
        Matrix.multiplyMM(this.mTempMVPMatrix, 0, this.mPerspective, 0, this.mModelView, 0);
        if (!this.mAddNextFrame)
          break label725;
        this.mWireShader.setColor(Constants.GREEN);
        if (((!this.mZooming) && (this.mValidEstimate)) || (this.mPanoramaEmpty))
        {
          label362: this.mTransparencyShader.bind();
          this.mTransparencyShader.setAlpha(0.7F);
          this.mFrameOverlay.draw(this.mTempMVPMatrix);
        }
      }
      this.mTargetManager.setCurrentOrientation(this.mFrameTransform);
      this.mTargetManager.drawTargetsOrthographic(this.mMVPMatrix, this.mOrthographic);
      GLES20.glDisable(3042);
      GLES20.glDisable(2929);
      GLES20.glBlendFunc(770, 771);
      GLES20.glDisable(2929);
      GLES20.glEnable(3042);
      if (!this.mValidEstimate)
        this.mMessageDisplay.drawMessage(this.mOrthographic, MessageDisplay.Message.ALIGNMENTLOST);
      if ((!this.mHoldStillTargetHit) && (LightCycleNative.PhotoSkippedTooFast()))
      {
        this.mHoldStillTargetHit = true;
        this.mHoldStillTimerStarted = false;
      }
      if ((this.mHoldStillTargetHit) && (!LightCycleNative.PhotoSkippedTooFast()))
      {
        this.mHoldStillTargetHit = false;
        this.mHoldStillTimerStarted = true;
        this.mHoldStillStartTimeNs = System.nanoTime();
      }
      if ((this.mHoldStillTimerStarted) && ((System.nanoTime() - this.mHoldStillStartTimeNs) / 1000000000.0D > 0.25D))
      {
        this.mHoldStillTimerStarted = false;
        this.mMessageDisplay.activateMessage(MessageDisplay.Message.HOLDSTILL, 0.75D);
      }
      if (this.mPanoramaEmpty)
        this.mMessageDisplay.activateMessage(MessageDisplay.Message.HITTOSTART, 0.0D);
      this.mMessageDisplay.drawMessages(this.mOrthographic);
      if (this.mCountdownDisplay.running())
        this.mCountdownDisplay.draw(this.mOrthographic, this.mSurfaceWidth, this.mSurfaceHeight);
      this.mRenderedGui.draw(this.mOrthographic);
      GLES20.glDisable(2929);
      j = LightCycleNative.ValidInPlaneAngle();
      if (j >= 0)
        break label738;
      this.mMessageDisplay.drawRotateDevice(this.mOrthographic, true);
      label686: label705: label725: label738: GLES20.glEnable(2929);
    }
    catch (OpenGLException localOpenGLException)
    {
      localOpenGLException.printStackTrace();
      break label226:
      GLES20.glDisable(2929);
      this.mPhotoCollection.draw(this.mMVPMatrix);
      break label267:
      this.mWireShader.setColor(Constants.TRANSPARENT_WHITE);
      break label362:
      if (j > 0);
      this.mMessageDisplay.drawRotateDevice(this.mOrthographic, false);
      break label686:
    }
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
    this.mTextureShader = new SimpleTextureShader();
    this.mWireShader = new SingleColorShader();
    this.mPanoSphereShader = new PanoSphereShader();
    this.mTransparencyShader = new TransparencyShader();
    this.mWireShader.setColor(Constants.ANDROID_BLUE);
    this.mPreview2dOverlay = new Pano2dPreviewOverlay();
    this.mPanoramaSphere = new Sphere(32, 64, 4.9F);
    this.mPanoramaSphere.setShader(this.mPanoSphereShader);
    this.mWireSphere = new Sphere(24, 48, 8.0F);
    this.mWireSphere.setLineDrawing(true);
    this.mWireSphere.setShader(this.mWireShader);
    this.mTiledGroundPlane = new TiledGroundPlaneDrawable();
    this.mMessageDisplay = new MessageDisplay();
    this.mRenderedGui.init(this.mContext, this.mTextureShader, this.mSurfaceWidth, this.mSurfaceHeight, this.mOrientationDetector);
    this.mPhotoCollection = new PhotoCollection(this.mFrameOverlay);
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inScaled = false;
    this.mTexturedCube = new TexturedCube(BitmapFactory.decodeResource(this.mContext.getResources(), 2130837572, localOptions), 32.0F);
    this.mRenderedGui.subscribe(new MessageSender.MessageSubscriber()
    {
      public void message(int paramInt, float paramFloat, String paramString)
      {
        if (paramInt != 2)
          return;
        LightCycleRenderer.this.undoLastCapturedPhoto();
        LightCycleRenderer.this.mRenderedGui.setUndoButtonVisible(false);
      }
    });
    this.mFrameOverlay.setShader(this.mTransparencyShader);
    this.mFrameOverlay.setOutlineShader(this.mWireShader);
    this.mPreview2dOverlay.setShader(this.mTextureShader);
    this.mPreview2dOverlay.setLineShader(this.mWireShader);
    this.mMessageDisplay.setShader(this.mTextureShader);
    if (this.mRenderTexturedPreview)
      this.mFrameOverlay.setDrawOutlineOnly(false);
    Matrix.setIdentityM(this.mRotate90, 0);
    this.mRotate90[0] = 0.0F;
    this.mRotate90[1] = -1.0F;
    this.mRotate90[4] = 1.0F;
    this.mRotate90[5] = 0.0F;
    Matrix.setIdentityM(this.mFrameTransform, 0);
    GLES20.glClearColor(Constants.BACKGROUND_BLACK[0], Constants.BACKGROUND_BLACK[1], Constants.BACKGROUND_BLACK[2], Constants.BACKGROUND_BLACK[3]);
    this.mInitialized = true;
    this.mTargetManager = new TargetManager(this.mContext);
    this.mCountdownDisplay = new CountdownDisplay(this.mContext);
    Matrix.setIdentityM(this.mTestMatrix, 0);
  }

  private void logFPS()
  {
    if (this.mFrameCount == 0)
      this.mFPSStartTime = Calendar.getInstance().getTimeInMillis();
    if ((this.mFrameCount % this.kFpsLoggingInterval != 0) || (this.mFrameCount == 0))
      return;
    long l1 = Calendar.getInstance().getTimeInMillis();
    long l2 = l1 - this.mFPSStartTime;
    (1000.0F * this.kFpsLoggingInterval / (float)l2);
    this.mFPSStartTime = l1;
  }

  private void processFrame()
  {
    monitorenter;
    try
    {
      this.mVideoFrameProcessor.processFrame(this.mImageData, this.mFrameWidth, this.mFrameHeight, this.mCountdownDisplay.finished(), false);
      boolean bool1 = this.mVideoFrameProcessor.validEstimate();
      setValidEstimate(bool1);
      this.mMovingTooFast = this.mVideoFrameProcessor.movingTooFast();
      if ((this.mVideoFrameProcessor.takeNewPhoto()) && (bool1) && (!this.mMovingTooFast) && (!this.mDisablePhotoTaking))
      {
        setPanoUpdate();
        float[] arrayOfFloat = this.mVideoFrameProcessor.getRotationEstimate();
        int i = this.mPhotoCollection.addNewPhoto(arrayOfFloat);
        int j = -1 + this.mPhotoCollection.getNumFrames();
        this.mView.requestPhoto(arrayOfFloat, j, i);
        this.mTargetManager.updateTargets();
        this.mPhotoInProgress = true;
        this.mView.requestRender();
        updateButtonVisibility();
        this.mPanoramaEmpty = false;
        this.mHoldStillTargetHit = false;
        this.mHoldStillTimerStarted = false;
      }
      boolean bool2;
      if (!this.mCountdownDisplay.finished())
      {
        bool2 = this.mVideoFrameProcessor.targetHit();
        if ((this.mCountdownDisplay.running()) || (!bool2))
          break label234;
        LG.d("Starting the countdown");
        this.mCountdownDisplay.startCountdown();
      }
      do
      {
        if (this.mDisablePhotoTaking)
          this.mCountdownDisplay.stopCountdown();
        this.mFramePending = false;
        label234: return;
      }
      while ((!this.mCountdownDisplay.running()) || ((bool2) && (((!bool2) || (!this.mMovingTooFast)))));
      LG.d("Stopping the countdown.");
    }
    finally
    {
      monitorexit;
    }
  }

  private float scaleFov(float paramFloat)
  {
    if (this.mSurfaceWidth < this.mSurfaceHeight)
    {
      double d = this.mSurfaceWidth / (2.0D * Math.tan(Math.toRadians(paramFloat) / 2.0D));
      paramFloat = (float)Math.toDegrees(2.0D * Math.atan(this.mSurfaceHeight / (2.0D * d)));
    }
    return paramFloat;
  }

  private void setOrthographic(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    (1.0F / paramFloat1);
    Matrix.orthoM(this.mOrthographic, 0, 0.0F, this.mSurfaceWidth, 0.0F, this.mSurfaceHeight, paramFloat2, paramFloat3);
  }

  private void setPerspective(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    float f1 = paramFloat3 * (float)Math.tan(3.141592653589793D * (paramFloat1 / 360.0D));
    float f2 = f1 * paramFloat2;
    Matrix.frustumM(this.mPerspective, 0, -f2, f2, -f1, f1, paramFloat3, paramFloat4);
  }

  private void setView()
  {
    float f1 = this.mSurfaceWidth / this.mSurfaceHeight;
    setPerspective(this.mCurFieldOfViewDegreesScaled, f1, 0.1F, 200.0F);
    Matrix.setIdentityM(this.mModelView, 0);
    float f2 = this.mOrientationDetector.getDisplayInitialOrientationDegrees() - this.mSensorReader.getImuOrientationDegrees();
    Matrix.rotateM(this.mModelView, 0, f2, 0.0F, 0.0F, 1.0F);
    Matrix.multiplyMM(this.mMVPMatrix, 0, this.mPerspective, 0, this.mModelView, 0);
    if (this.mPreview2dOverlay.initialized())
      return;
    this.mPreview2dOverlay.init(this.mContext, f1, this.mSurfaceWidth, this.mSurfaceHeight, 0.45F, this.mOrientationDetector);
    setOrthographic(f1, -50.0F, 50.0F);
    this.mMessageDisplay.init(this.mContext, this.mSurfaceWidth, this.mSurfaceHeight, this.mOrientationDetector);
    this.mTargetManager.init(this.mSurfaceWidth, this.mSurfaceHeight);
    this.mTargetManager.setDeviceOrientationDetector(this.mOrientationDetector);
    this.mTargetManager.setSensorReader(this.mSensorReader);
    this.mCountdownDisplay.init(this.mSurfaceWidth, this.mSurfaceHeight, this.mSensorReader, this.mOrientationDetector);
  }

  private void updateButtonVisibility()
  {
    int i = 1;
    RenderedGui localRenderedGui1 = this.mRenderedGui;
    label20: RenderedGui localRenderedGui2;
    if (this.mPhotoCollection.getNumFrames() >= 2)
    {
      int k = i;
      localRenderedGui1.setDoneButtonVisible(k);
      localRenderedGui2 = this.mRenderedGui;
      if (this.mPhotoCollection.getNumFrames() < i)
        break label54;
    }
    while (true)
    {
      localRenderedGui2.setUndoButtonVisible(i);
      return;
      int l = 0;
      break label20:
      label54: int j = 0;
    }
  }

  private void updateFieldOfViewDegrees(float paramFloat)
  {
    this.mCurFieldOfViewDegrees = (this.mFieldOfViewDegreesZoomStart / paramFloat);
    this.mCurFieldOfViewDegrees = Math.min(this.mCurFieldOfViewDegrees, this.mMaxFieldOfViewDegrees);
    this.mCurFieldOfViewDegrees = Math.max(this.mCurFieldOfViewDegrees, this.mMinFieldOfViewDegrees);
    this.mCurFieldOfViewDegreesScaled = scaleFov(this.mCurFieldOfViewDegrees);
  }

  private void updateTextures()
  {
    if ((this.mPanoUpdate) && (this.mUseBlendedPreview))
    {
      LightCycleNative.UpdateTexture(this.mPanoramaSphere.getTextureId());
      this.mPanoUpdate = false;
    }
    if (this.mDisplayFrameImage)
    {
      if (!this.mRenderTexturedPreview)
        break label53;
      this.mFrameOverlay.setDrawOutlineOnly(false);
    }
    return;
    label53: this.mFrameOverlay.setDrawOutlineOnly(true);
  }

  public void createFrameDisplay(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    this.mFrameOverlay.generateGeometry(paramArrayOfFloat, paramInt1, paramInt2, 1.0F);
  }

  public void endPinchZoom(float paramFloat)
  {
    updateFieldOfViewDegrees(paramFloat);
    this.mFieldOfViewDegreesZoomStart = this.mCurFieldOfViewDegrees;
    this.mZooming = false;
  }

  public Pano2dPreviewOverlay getPanoPreview2d()
  {
    return this.mPreview2dOverlay;
  }

  public RenderedGui getRenderedGui()
  {
    return this.mRenderedGui;
  }

  public void onDrawFrame(GL10 paramGL10)
  {
    if ((((!this.mInitialized) || (this.renderingStopped))) && (!this.mBlankPending))
      return;
    if (!this.mTexturesInitialized)
      createTextures();
    if (this.mIntroAnimating)
    {
      animateIntro();
      this.mRenderPending = false;
      return;
    }
    if (this.mUpdateTextures)
    {
      this.mUpdateTextures = false;
      LightCycleNative.UpdateNewTextures();
    }
    if ((!this.mPhotoInProgress) && (this.mFramePending) && (!this.mBlankPending))
      processFrame();
    if (this.mSensorReader.getEkfEnabled())
    {
      if (!this.mPanoramaEmpty)
        break label210;
      this.mSensorReader.setHeadingDegrees(0.0D);
    }
    while (true)
    {
      this.mFrameTransform = this.mSensorReader.getFilterOutput();
      LightCycleNative.SetFilteredRotation(this.mFrameTransform);
      if (this.mRenderTexturedPreview)
        LightCycleNative.UpdateFrameTexture(this.mCurrentFrameTexture);
      this.mOrientationDetector.update();
      if (this.mFrameCount > 0)
        drawScene(this.mCurrentFrameTexture);
      int i = this.mCurrentFrameTexture;
      this.mCurrentFrameTexture = this.mPreviousFrameTexture;
      this.mPreviousFrameTexture = i;
      this.mFrameCount = (1 + this.mFrameCount);
      this.mView.requestRender();
      logFPS();
      return;
      label210: if (this.mDeltaHeading == 0.0D)
        continue;
      if (Math.abs(this.mDeltaHeading) < 2.0D * Math.abs(this.mDeltaHeadingStep))
      {
        this.mSensorReader.setHeadingDegrees(this.mSensorReader.getHeadingDegrees() + this.mDeltaHeading);
        this.mDeltaHeading = 0.0D;
      }
      this.mSensorReader.setHeadingDegrees(this.mSensorReader.getHeadingDegrees() + this.mDeltaHeadingStep);
      this.mDeltaHeading -= this.mDeltaHeadingStep;
    }
  }

  public void onSurfaceChanged(GL10 paramGL10, int paramInt1, int paramInt2)
  {
    this.mSurfaceWidth = paramInt1;
    this.mSurfaceHeight = paramInt2;
    LG.d("Rendering init completed.");
    this.mRenderedGui.init(this.mContext, this.mTextureShader, this.mSurfaceWidth, this.mSurfaceHeight, this.mOrientationDetector);
    this.mFramePending = false;
  }

  public void onSurfaceCreated(GL10 paramGL10, EGLConfig paramEGLConfig)
  {
    try
    {
      initRendering();
      this.mFramePending = false;
      return;
    }
    catch (OpenGLException localOpenGLException)
    {
      localOpenGLException.printStackTrace();
    }
  }

  public void pinchZoom(float paramFloat)
  {
    updateFieldOfViewDegrees(paramFloat);
    this.mZooming = true;
  }

  public void setAddNextFrame()
  {
    this.mAddNextFrame = true;
  }

  public void setDisablePhotoTaking(boolean paramBoolean)
  {
    this.mDisablePhotoTaking = paramBoolean;
  }

  public void setFrameDimensions(int paramInt1, int paramInt2)
  {
    this.mFrameWidth = paramInt1;
    this.mFrameHeight = paramInt2;
  }

  public void setImageData(byte[] paramArrayOfByte)
  {
    this.mImageData = paramArrayOfByte;
    this.mFramePending = true;
  }

  public void setLiveImageDisplay(boolean paramBoolean)
  {
    this.mRenderTexturedPreview = paramBoolean;
  }

  public void setPanoUpdate()
  {
    this.mPanoUpdate = true;
    this.mPanoramaEmpty = false;
  }

  public void setPhotoFinished()
  {
    this.mPhotoInProgress = false;
  }

  public void setRenderBlankScreen(boolean paramBoolean)
  {
    this.mRenderBlankScreen = paramBoolean;
    if (!paramBoolean)
      return;
    this.mBlankPending = true;
  }

  public void setRenderingStopped(boolean paramBoolean)
  {
    this.renderingStopped = paramBoolean;
  }

  public void setSensorReader(Display paramDisplay, SensorReader paramSensorReader)
  {
    this.mSensorReader = paramSensorReader;
    this.mOrientationDetector = new DeviceOrientationDetector(paramDisplay, paramSensorReader);
    this.mVideoFrameProcessor = new VideoFrameProcessor(paramSensorReader);
  }

  public void setValidEstimate(boolean paramBoolean)
  {
    this.mValidEstimate = paramBoolean;
  }

  public void setView(LightCycleView paramLightCycleView)
  {
    this.mView = paramLightCycleView;
  }

  public void undoLastCapturedPhoto()
  {
    monitorenter;
    int i;
    try
    {
      i = this.mPhotoCollection.getNumFrames();
      this.mPhotoCollection.undoAddPhoto();
      if (this.mDeltaHeadingStack.size() <= i)
        break label52;
    }
    finally
    {
      monitorexit;
    }
    if (this.mDeltaHeadingStack.size() == i)
    {
      label52: this.mDeltaHeading -= ((Double)this.mDeltaHeadingStack.lastElement()).doubleValue();
      this.mDeltaHeadingStep = (this.mDeltaHeading / 45.0D);
      this.mDeltaHeadingStack.removeElementAt(-1 + this.mDeltaHeadingStack.size());
    }
    updateButtonVisibility();
    2 local2 = new Thread()
    {
      public void run()
      {
        LightCycleNative.UndoAddImage(LightCycleRenderer.this.mRealtimeAlignmentEnabled);
        LightCycleRenderer.this.mTargetManager.updateTargets();
        if (LightCycleRenderer.this.mView.undoAddImage() != 0)
          return;
        LightCycleRenderer.this.mTargetManager.reset();
        LightCycleRenderer.this.mCountdownDisplay.reset();
        LightCycleRenderer.this.mView.resetVelocityLimit();
        LightCycleRenderer.access$502(LightCycleRenderer.this, true);
      }
    };
    local2.start();
    try
    {
      local2.join();
      monitorexit;
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      localInterruptedException.printStackTrace();
    }
  }

  public static abstract interface UpdatePhotoRendering
  {
    public abstract void thumbnailLoaded(int paramInt);

    public abstract void updateTransforms(float[] paramArrayOfFloat);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.LightCycleRenderer
 * JD-Core Version:    0.5.4
 */