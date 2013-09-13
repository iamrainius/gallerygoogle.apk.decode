package com.android.camera;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.camera.ui.LayoutChangeNotifier.Listener;
import com.android.camera.ui.LayoutNotifyView;
import com.android.camera.ui.PopupManager;
import com.android.camera.ui.Rotatable;
import com.android.gallery3d.app.OrientationManager;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.GLRootView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

@TargetApi(11)
public class PanoramaModule
  implements SurfaceTexture.OnFrameAvailableListener, CameraModule, ShutterButton.OnShutterButtonListener, LayoutChangeNotifier.Listener
{
  private CameraActivity mActivity;
  private CameraManager.CameraProxy mCameraDevice;
  private int mCameraOrientation;
  private int mCameraState;
  private SurfaceTexture mCameraTexture;
  private boolean mCancelComputation;
  private View mCaptureIndicator;
  private LinearLayout mCaptureLayout;
  private int mCaptureState;
  private ContentResolver mContentResolver;
  private DateFormat mDateTimeStampFormat;
  private int mDeviceOrientation;
  private int mDeviceOrientationAtCapture;
  private String mDialogOkString;
  private String mDialogPanoramaFailedString;
  private String mDialogTitle;
  private String mDialogWaitingPreviousString;
  private GLRootView mGLRootView;
  private DateFormat mGPSDateStampFormat;
  private DateFormat mGPSTimeStampFormat;
  private float mHorizontalViewAngle;
  private int mIndicatorColor;
  private int mIndicatorColorFast;
  private View mLeftIndicator;
  private Handler mMainHandler;
  private MosaicFrameProcessor mMosaicFrameProcessor;
  private boolean mMosaicFrameProcessorInitialized;
  private MosaicPreviewRenderer mMosaicPreviewRenderer;
  private Runnable mOnFrameAvailableRunnable;
  private int mOrientationCompensation;
  private PanoOrientationEventListener mOrientationEventListener;
  private ViewGroup mPanoLayout;
  private PanoProgressBar mPanoProgressBar;
  private PowerManager.WakeLock mPartialWakeLock;
  private boolean mPaused;
  private String mPreparePreviewString;
  private LayoutNotifyView mPreviewArea;
  private int mPreviewHeight;
  private int mPreviewWidth;
  private float[] mProgressAngle = new float[2];
  private Matrix mProgressDirectionMatrix = new Matrix();
  private ImageView mReview;
  private View mReviewLayout;
  private View mRightIndicator;
  private View mRootView;
  private RotateDialogController mRotateDialog;
  private PanoProgressBar mSavingProgressBar;
  private ShutterButton mShutterButton;
  private SoundClips.Player mSoundPlayer;
  private String mTargetFocusMode = "infinity";
  private boolean mThreadRunning;
  private long mTimeTaken;
  private TextView mTooFastPrompt;
  private boolean mUsingFrontCamera;
  private float mVerticalViewAngle;
  private Object mWaitObject = new Object();
  private AsyncTask<Void, Void, Void> mWaitProcessorTask;

  private void cancelHighResComputation()
  {
    this.mCancelComputation = true;
    synchronized (this.mWaitObject)
    {
      this.mWaitObject.notify();
      return;
    }
  }

  private void clearMosaicFrameProcessorIfNeeded()
  {
    if ((!this.mPaused) || (this.mThreadRunning));
    do
      return;
    while (!this.mMosaicFrameProcessorInitialized);
    this.mMosaicFrameProcessor.clear();
    this.mMosaicFrameProcessorInitialized = false;
  }

  private void configMosaicPreview(int paramInt1, int paramInt2)
  {
    stopCameraPreview();
    CameraScreenNail localCameraScreenNail = (CameraScreenNail)this.mActivity.mCameraScreenNail;
    localCameraScreenNail.setSize(paramInt1, paramInt2);
    if (localCameraScreenNail.getSurfaceTexture() == null)
    {
      localCameraScreenNail.acquireSurfaceTexture();
      label32: if (this.mActivity.getResources().getConfiguration().orientation != 2)
        break label140;
    }
    for (boolean bool = true; ; bool = false)
    {
      if (this.mMosaicPreviewRenderer != null)
        this.mMosaicPreviewRenderer.release();
      this.mMosaicPreviewRenderer = new MosaicPreviewRenderer(localCameraScreenNail.getSurfaceTexture(), paramInt1, paramInt2, bool);
      this.mCameraTexture = this.mMosaicPreviewRenderer.getInputSurfaceTexture();
      if ((!this.mPaused) && (!this.mThreadRunning) && (this.mWaitProcessorTask == null))
        resetToPreview();
      return;
      localCameraScreenNail.releaseSurfaceTexture();
      localCameraScreenNail.acquireSurfaceTexture();
      this.mActivity.notifyScreenNailChanged();
      label140: break label32:
    }
  }

  private void configureCamera(Camera.Parameters paramParameters)
  {
    this.mCameraDevice.setParameters(paramParameters);
  }

  private void createContentView()
  {
    this.mActivity.getLayoutInflater().inflate(2130968622, (ViewGroup)this.mRootView);
    Resources localResources = this.mActivity.getResources();
    this.mCaptureLayout = ((LinearLayout)this.mRootView.findViewById(2131558532));
    this.mIndicatorColor = localResources.getColor(2131296269);
    this.mIndicatorColorFast = localResources.getColor(2131296270);
    this.mPanoLayout = ((ViewGroup)this.mRootView.findViewById(2131558540));
    this.mRotateDialog = new RotateDialogController(this.mActivity, 2130968653);
    setViews(localResources);
  }

  private boolean findBestPreviewSize(List<Camera.Size> paramList, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = 691200;
    int j = 0;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Camera.Size localSize = (Camera.Size)localIterator.next();
      int k = localSize.height;
      int l = localSize.width;
      int i1 = 691200 - k * l;
      if (((paramBoolean2) && (i1 < 0)) || ((paramBoolean1) && (k * 4 != l * 3)))
        continue;
      int i2 = Math.abs(i1);
      if (i2 >= i)
        continue;
      this.mPreviewWidth = l;
      this.mPreviewHeight = k;
      i = i2;
      j = 1;
    }
    return j;
  }

  private int getCaptureOrientation()
  {
    if (this.mUsingFrontCamera)
      return (360 + (this.mDeviceOrientationAtCapture - this.mCameraOrientation)) % 360;
    return (this.mDeviceOrientationAtCapture + this.mCameraOrientation) % 360;
  }

  private static String getExifOrientation(int paramInt)
  {
    switch (paramInt)
    {
    default:
      throw new AssertionError("invalid: " + paramInt);
    case 0:
      return String.valueOf(1);
    case 90:
      return String.valueOf(6);
    case 180:
      return String.valueOf(3);
    case 270:
    }
    return String.valueOf(8);
  }

  private void hideDirectionIndicators()
  {
    this.mLeftIndicator.setVisibility(8);
    this.mRightIndicator.setVisibility(8);
  }

  private void hideTooFastIndication()
  {
    this.mTooFastPrompt.setVisibility(8);
    this.mPreviewArea.setVisibility(4);
    this.mPanoProgressBar.setIndicatorColor(this.mIndicatorColor);
    this.mLeftIndicator.setEnabled(false);
    this.mRightIndicator.setEnabled(false);
  }

  private void initMosaicFrameProcessorIfNeeded()
  {
    if ((this.mPaused) || (this.mThreadRunning))
      return;
    this.mMosaicFrameProcessor.initialize(this.mPreviewWidth, this.mPreviewHeight, getPreviewBufSize());
    this.mMosaicFrameProcessorInitialized = true;
  }

  private void keepScreenOn()
  {
    this.mMainHandler.removeMessages(4);
    this.mActivity.getWindow().addFlags(128);
  }

  private void keepScreenOnAwhile()
  {
    this.mMainHandler.removeMessages(4);
    this.mActivity.getWindow().addFlags(128);
    this.mMainHandler.sendEmptyMessageDelayed(4, 120000L);
  }

  private void onBackgroundThreadFinished()
  {
    this.mThreadRunning = false;
    this.mRotateDialog.dismissDialog();
  }

  private void openCamera()
    throws CameraHardwareException, CameraDisabledException
  {
    int i = CameraHolder.instance().getBackCameraId();
    if (i == -1)
      i = 0;
    this.mCameraDevice = Util.openCamera(this.mActivity, i);
    this.mCameraOrientation = Util.getCameraOrientation(i);
    if (i != CameraHolder.instance().getFrontCameraId())
      return;
    this.mUsingFrontCamera = true;
  }

  private void releaseCamera()
  {
    if (this.mCameraDevice == null)
      return;
    this.mCameraDevice.setPreviewCallbackWithBuffer(null);
    CameraHolder.instance().release();
    this.mCameraDevice = null;
    this.mCameraState = 0;
  }

  private void reset()
  {
    this.mCaptureState = 0;
    this.mActivity.getOrientationManager().unlockOrientation();
    this.mActivity.setSwipingEnabled(true);
    this.mShutterButton.setImageResource(2130837532);
    this.mReviewLayout.setVisibility(8);
    this.mPanoProgressBar.setVisibility(8);
    if (this.mActivity.mShowCameraAppView)
    {
      this.mCaptureLayout.setVisibility(0);
      this.mActivity.showUI();
    }
    this.mMosaicFrameProcessor.reset();
  }

  private void resetScreenOn()
  {
    this.mMainHandler.removeMessages(4);
    this.mActivity.getWindow().clearFlags(128);
  }

  private void resetToPreview()
  {
    reset();
    if (this.mPaused)
      return;
    startCameraPreview();
  }

  private void runBackgroundThread(Thread paramThread)
  {
    this.mThreadRunning = true;
    paramThread.start();
  }

  private Uri savePanorama(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    Uri localUri = null;
    String str1;
    String str2;
    if (paramArrayOfByte != null)
    {
      str1 = PanoUtil.createName(this.mActivity.getResources().getString(2131361965), this.mTimeTaken);
      str2 = Storage.generateFilepath(str1);
      Storage.writeFile(str2, paramArrayOfByte);
    }
    try
    {
      ExifInterface localExifInterface = new ExifInterface(str2);
      localExifInterface.setAttribute("GPSDateStamp", this.mGPSDateStampFormat.format(Long.valueOf(this.mTimeTaken)));
      localExifInterface.setAttribute("GPSTimeStamp", this.mGPSTimeStampFormat.format(Long.valueOf(this.mTimeTaken)));
      localExifInterface.setAttribute("DateTime", this.mDateTimeStampFormat.format(Long.valueOf(this.mTimeTaken)));
      localExifInterface.setAttribute("Orientation", getExifOrientation(paramInt3));
      localExifInterface.saveAttributes();
      int i = (int)new File(str2).length();
      localUri = Storage.addImage(this.mContentResolver, str1, this.mTimeTaken, null, paramInt3, i, str2, paramInt1, paramInt2);
      return localUri;
    }
    catch (IOException localIOException)
    {
      Log.e("CAM PanoModule", "Cannot set EXIF for " + str2, localIOException);
    }
  }

  private void setViews(Resources paramResources)
  {
    this.mCaptureState = 0;
    this.mPanoProgressBar = ((PanoProgressBar)this.mRootView.findViewById(2131558575));
    this.mPanoProgressBar.setBackgroundColor(paramResources.getColor(2131296267));
    this.mPanoProgressBar.setDoneColor(paramResources.getColor(2131296268));
    this.mPanoProgressBar.setIndicatorColor(this.mIndicatorColor);
    this.mPanoProgressBar.setOnDirectionChangeListener(new PanoProgressBar.OnDirectionChangeListener()
    {
      public void onDirectionChange(int paramInt)
      {
        if (PanoramaModule.this.mCaptureState != 1)
          return;
        PanoramaModule.this.showDirectionIndicators(paramInt);
      }
    });
    this.mLeftIndicator = this.mRootView.findViewById(2131558576);
    this.mRightIndicator = this.mRootView.findViewById(2131558577);
    this.mLeftIndicator.setEnabled(false);
    this.mRightIndicator.setEnabled(false);
    this.mTooFastPrompt = ((TextView)this.mRootView.findViewById(2131558579));
    this.mPreviewArea = ((LayoutNotifyView)this.mRootView.findViewById(2131558573));
    this.mPreviewArea.setOnLayoutChangeListener(this);
    this.mSavingProgressBar = ((PanoProgressBar)this.mRootView.findViewById(2131558536));
    this.mSavingProgressBar.setIndicatorWidth(0.0F);
    this.mSavingProgressBar.setMaxProgress(100);
    this.mSavingProgressBar.setBackgroundColor(paramResources.getColor(2131296267));
    this.mSavingProgressBar.setDoneColor(paramResources.getColor(2131296269));
    this.mCaptureIndicator = this.mRootView.findViewById(2131558572);
    this.mReviewLayout = this.mRootView.findViewById(2131558533);
    this.mReview = ((ImageView)this.mRootView.findViewById(2131558534));
    this.mRootView.findViewById(2131558537).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        if ((PanoramaModule.this.mPaused) || (PanoramaModule.this.mCameraTexture == null))
          return;
        PanoramaModule.this.cancelHighResComputation();
      }
    });
    this.mShutterButton = this.mActivity.getShutterButton();
    this.mShutterButton.setImageResource(2130837532);
    this.mShutterButton.setOnShutterButtonListener(this);
    if (this.mActivity.getResources().getConfiguration().orientation != 1)
      return;
    ((Rotatable)this.mRootView.findViewById(2131558538)).setOrientation(270, false);
  }

  private void setupCamera()
    throws CameraHardwareException, CameraDisabledException
  {
    openCamera();
    Camera.Parameters localParameters = this.mCameraDevice.getParameters();
    setupCaptureParams(localParameters);
    configureCamera(localParameters);
  }

  private void setupCaptureParams(Camera.Parameters paramParameters)
  {
    List localList1 = paramParameters.getSupportedPreviewSizes();
    if (!findBestPreviewSize(localList1, true, true))
    {
      Log.w("CAM PanoModule", "No 4:3 ratio preview size supported.");
      if (!findBestPreviewSize(localList1, false, true))
      {
        Log.w("CAM PanoModule", "Can't find a supported preview size smaller than 960x720.");
        findBestPreviewSize(localList1, false, false);
      }
    }
    Log.v("CAM PanoModule", "preview h = " + this.mPreviewHeight + " , w = " + this.mPreviewWidth);
    paramParameters.setPreviewSize(this.mPreviewWidth, this.mPreviewHeight);
    List localList2 = paramParameters.getSupportedPreviewFpsRange();
    int i = -1 + localList2.size();
    int j = ((int[])localList2.get(i))[0];
    int k = ((int[])localList2.get(i))[1];
    paramParameters.setPreviewFpsRange(j, k);
    Log.v("CAM PanoModule", "preview fps: " + j + ", " + k);
    if (paramParameters.getSupportedFocusModes().indexOf(this.mTargetFocusMode) >= 0)
      paramParameters.setFocusMode(this.mTargetFocusMode);
    while (true)
    {
      paramParameters.set("recording-hint", "false");
      this.mHorizontalViewAngle = paramParameters.getHorizontalViewAngle();
      this.mVerticalViewAngle = paramParameters.getVerticalViewAngle();
      return;
      Log.w("CAM PanoModule", "Cannot set the focus mode to " + this.mTargetFocusMode + " becuase the mode is not supported.");
    }
  }

  private void showDirectionIndicators(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return;
    case 0:
      this.mLeftIndicator.setVisibility(0);
      this.mRightIndicator.setVisibility(0);
      return;
    case 1:
      this.mLeftIndicator.setVisibility(0);
      this.mRightIndicator.setVisibility(8);
      return;
    case 2:
    }
    this.mLeftIndicator.setVisibility(8);
    this.mRightIndicator.setVisibility(0);
  }

  private void showFinalMosaic(Bitmap paramBitmap)
  {
    if (paramBitmap != null)
    {
      if (getCaptureOrientation() < 180)
        break label63;
      this.mReview.setImageDrawable(new FlipBitmapDrawable(this.mActivity.getResources(), paramBitmap));
    }
    while (true)
    {
      this.mGLRootView.setVisibility(8);
      this.mCaptureLayout.setVisibility(8);
      this.mReviewLayout.setVisibility(0);
      return;
      label63: this.mReview.setImageBitmap(paramBitmap);
    }
  }

  private void showTooFastIndication()
  {
    this.mTooFastPrompt.setVisibility(0);
    this.mPreviewArea.setVisibility(0);
    this.mPanoProgressBar.setIndicatorColor(this.mIndicatorColorFast);
    this.mLeftIndicator.setEnabled(true);
    this.mRightIndicator.setEnabled(true);
  }

  private void startCameraPreview()
  {
    if (this.mCameraDevice == null);
    do
      return;
    while (this.mCameraTexture == null);
    if (this.mCameraState != 0)
      stopCameraPreview();
    this.mCameraDevice.setDisplayOrientation(0);
    if (this.mCameraTexture != null)
      this.mCameraTexture.setOnFrameAvailableListener(this);
    this.mCameraDevice.setPreviewTextureAsync(this.mCameraTexture);
    this.mCameraDevice.startPreviewAsync();
    this.mCameraState = 1;
  }

  private void stopCameraPreview()
  {
    if ((this.mCameraDevice != null) && (this.mCameraState != 0))
    {
      Log.v("CAM PanoModule", "stopPreview");
      this.mCameraDevice.stopPreview();
    }
    this.mCameraState = 0;
  }

  private void stopCapture(boolean paramBoolean)
  {
    this.mCaptureState = 0;
    this.mCaptureIndicator.setVisibility(8);
    hideTooFastIndication();
    hideDirectionIndicators();
    this.mMosaicFrameProcessor.setProgressListener(null);
    stopCameraPreview();
    this.mCameraTexture.setOnFrameAvailableListener(null);
    if ((!paramBoolean) && (!this.mThreadRunning))
    {
      this.mRotateDialog.showWaitingDialog(this.mPreparePreviewString);
      this.mActivity.hideUI();
      runBackgroundThread(new Thread()
      {
        public void run()
        {
          PanoramaModule.MosaicJpeg localMosaicJpeg = PanoramaModule.this.generateFinalMosaic(false);
          if ((localMosaicJpeg != null) && (localMosaicJpeg.isValid))
          {
            Bitmap localBitmap = BitmapFactory.decodeByteArray(localMosaicJpeg.data, 0, localMosaicJpeg.data.length);
            PanoramaModule.this.mMainHandler.sendMessage(PanoramaModule.this.mMainHandler.obtainMessage(1, localBitmap));
            return;
          }
          PanoramaModule.this.mMainHandler.sendMessage(PanoramaModule.this.mMainHandler.obtainMessage(3));
        }
      });
    }
    keepScreenOnAwhile();
  }

  private void updateProgress(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.mGLRootView.requestRender();
    if ((Math.abs(paramFloat1) > 2.5F) || (Math.abs(paramFloat2) > 2.5F))
    {
      showTooFastIndication();
      label33: this.mProgressAngle[0] = paramFloat3;
      this.mProgressAngle[1] = paramFloat4;
      this.mProgressDirectionMatrix.mapPoints(this.mProgressAngle);
      if (Math.abs(this.mProgressAngle[0]) <= Math.abs(this.mProgressAngle[1]))
        break label107;
    }
    for (int i = (int)this.mProgressAngle[0]; ; i = (int)this.mProgressAngle[1])
    {
      this.mPanoProgressBar.setProgress(i);
      return;
      hideTooFastIndication();
      label107: break label33:
    }
  }

  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    return this.mActivity.superDispatchTouchEvent(paramMotionEvent);
  }

  public MosaicJpeg generateFinalMosaic(boolean paramBoolean)
  {
    int i = this.mMosaicFrameProcessor.createMosaic(paramBoolean);
    if (i == -2)
      return null;
    if (i == -1)
      return new MosaicJpeg();
    byte[] arrayOfByte = this.mMosaicFrameProcessor.getFinalMosaicNV21();
    if (arrayOfByte == null)
    {
      Log.e("CAM PanoModule", "getFinalMosaicNV21() returned null.");
      return new MosaicJpeg();
    }
    int j = -8 + arrayOfByte.length;
    int k = (arrayOfByte[(j + 0)] << 24) + ((0xFF & arrayOfByte[(j + 1)]) << 16) + ((0xFF & arrayOfByte[(j + 2)]) << 8) + (0xFF & arrayOfByte[(j + 3)]);
    int l = (arrayOfByte[(j + 4)] << 24) + ((0xFF & arrayOfByte[(j + 5)]) << 16) + ((0xFF & arrayOfByte[(j + 6)]) << 8) + (0xFF & arrayOfByte[(j + 7)]);
    Log.v("CAM PanoModule", "ImLength = " + j + ", W = " + k + ", H = " + l);
    if ((k <= 0) || (l <= 0))
    {
      Log.e("CAM PanoModule", "width|height <= 0!!, len = " + j + ", W = " + k + ", H = " + l);
      return new MosaicJpeg();
    }
    YuvImage localYuvImage = new YuvImage(arrayOfByte, 17, k, l, null);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    localYuvImage.compressToJpeg(new Rect(0, 0, k, l), 100, localByteArrayOutputStream);
    try
    {
      localByteArrayOutputStream.close();
      return new MosaicJpeg(localByteArrayOutputStream.toByteArray(), k, l);
    }
    catch (Exception localException)
    {
      Log.e("CAM PanoModule", "Exception in storing final mosaic", localException);
    }
    return new MosaicJpeg();
  }

  public int getPreviewBufSize()
  {
    PixelFormat localPixelFormat = new PixelFormat();
    PixelFormat.getPixelFormatInfo(this.mCameraDevice.getParameters().getPreviewFormat(), localPixelFormat);
    return 32 + this.mPreviewWidth * this.mPreviewHeight * localPixelFormat.bitsPerPixel / 8;
  }

  public void init(CameraActivity paramCameraActivity, View paramView, boolean paramBoolean)
  {
    this.mActivity = paramCameraActivity;
    this.mRootView = ((ViewGroup)paramView);
    createContentView();
    this.mContentResolver = this.mActivity.getContentResolver();
    if (paramBoolean)
      this.mActivity.reuseCameraScreenNail(true);
    while (true)
    {
      this.mOnFrameAvailableRunnable = new Runnable()
      {
        public void run()
        {
          if (PanoramaModule.this.mPaused)
            return;
          if (PanoramaModule.this.mGLRootView.getVisibility() != 0)
          {
            PanoramaModule.this.mMosaicPreviewRenderer.showPreviewFrameSync();
            PanoramaModule.this.mGLRootView.setVisibility(0);
            return;
          }
          if (PanoramaModule.this.mCaptureState == 0)
          {
            PanoramaModule.this.mMosaicPreviewRenderer.showPreviewFrame();
            return;
          }
          PanoramaModule.this.mMosaicPreviewRenderer.alignFrameSync();
          PanoramaModule.this.mMosaicFrameProcessor.processFrame();
        }
      };
      this.mGPSDateStampFormat = new SimpleDateFormat("yyyy:MM:dd");
      this.mGPSTimeStampFormat = new SimpleDateFormat("kk/1,mm/1,ss/1");
      this.mDateTimeStampFormat = new SimpleDateFormat("yyyy:MM:dd kk:mm:ss");
      TimeZone localTimeZone = TimeZone.getTimeZone("UTC");
      this.mGPSDateStampFormat.setTimeZone(localTimeZone);
      this.mGPSTimeStampFormat.setTimeZone(localTimeZone);
      this.mPartialWakeLock = ((PowerManager)this.mActivity.getSystemService("power")).newWakeLock(1, "Panorama");
      this.mOrientationEventListener = new PanoOrientationEventListener(this.mActivity);
      this.mMosaicFrameProcessor = MosaicFrameProcessor.getInstance();
      Resources localResources = this.mActivity.getResources();
      this.mPreparePreviewString = localResources.getString(2131361968);
      this.mDialogTitle = localResources.getString(2131361970);
      this.mDialogOkString = localResources.getString(2131361961);
      this.mDialogPanoramaFailedString = localResources.getString(2131361969);
      this.mDialogWaitingPreviousString = localResources.getString(2131361972);
      this.mGLRootView = ((GLRootView)this.mActivity.getGLRoot());
      this.mMainHandler = new Handler()
      {
        public void handleMessage(Message paramMessage)
        {
          switch (paramMessage.what)
          {
          default:
            return;
          case 1:
            PanoramaModule.this.onBackgroundThreadFinished();
            PanoramaModule.this.showFinalMosaic((Bitmap)paramMessage.obj);
            PanoramaModule.this.saveHighResMosaic();
            return;
          case 2:
            PanoramaModule.this.onBackgroundThreadFinished();
            if (PanoramaModule.this.mPaused)
              PanoramaModule.this.resetToPreview();
            while (true)
            {
              PanoramaModule.this.clearMosaicFrameProcessorIfNeeded();
              return;
              PanoramaModule.this.mRotateDialog.showAlertDialog(PanoramaModule.this.mDialogTitle, PanoramaModule.this.mDialogPanoramaFailedString, PanoramaModule.this.mDialogOkString, new Runnable()
              {
                public void run()
                {
                  PanoramaModule.this.resetToPreview();
                }
              }
              , null, null);
            }
          case 3:
            PanoramaModule.this.onBackgroundThreadFinished();
            PanoramaModule.this.resetToPreview();
            PanoramaModule.this.clearMosaicFrameProcessorIfNeeded();
            return;
          case 4:
          }
          PanoramaModule.this.mActivity.getWindow().clearFlags(128);
        }
      };
      return;
      this.mActivity.createCameraScreenNail(true);
    }
  }

  public void installIntentFilter()
  {
  }

  public boolean needsSwitcher()
  {
    return true;
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
  }

  public boolean onBackPressed()
  {
    return this.mThreadRunning;
  }

  public void onCaptureTextureCopied()
  {
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    boolean bool = this.mThreadRunning;
    Drawable localDrawable = null;
    if (bool)
      localDrawable = this.mReview.getDrawable();
    LinearLayout localLinearLayout = this.mCaptureLayout;
    if (paramConfiguration.orientation == 2);
    for (int i = 0; ; i = 1)
    {
      localLinearLayout.setOrientation(i);
      this.mCaptureLayout.removeAllViews();
      LayoutInflater localLayoutInflater = this.mActivity.getLayoutInflater();
      localLayoutInflater.inflate(2130968647, this.mCaptureLayout);
      this.mPanoLayout.removeView(this.mReviewLayout);
      localLayoutInflater.inflate(2130968621, this.mPanoLayout);
      setViews(this.mActivity.getResources());
      if (this.mThreadRunning)
      {
        this.mReview.setImageDrawable(localDrawable);
        this.mCaptureLayout.setVisibility(8);
        this.mReviewLayout.setVisibility(0);
      }
      return;
    }
  }

  public void onFrameAvailable(SurfaceTexture paramSurfaceTexture)
  {
    this.mActivity.runOnUiThread(this.mOnFrameAvailableRunnable);
  }

  public void onFullScreenChanged(boolean paramBoolean)
  {
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }

  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }

  public void onLayoutChange(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Log.i("CAM PanoModule", "layout change: " + (paramInt3 - paramInt1) + "/" + (paramInt4 - paramInt2));
    this.mActivity.onLayoutChange(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
    configMosaicPreview(paramInt3 - paramInt1, paramInt4 - paramInt2);
  }

  public void onOrientationChanged(int paramInt)
  {
  }

  public void onPauseAfterSuper()
  {
    this.mOrientationEventListener.disable();
    if (this.mCameraDevice == null)
      return;
    if (this.mCaptureState == 1)
    {
      stopCapture(true);
      reset();
    }
    releaseCamera();
    this.mCameraTexture = null;
    if (this.mMosaicPreviewRenderer != null)
    {
      this.mMosaicPreviewRenderer.release();
      this.mMosaicPreviewRenderer = null;
    }
    clearMosaicFrameProcessorIfNeeded();
    if (this.mWaitProcessorTask != null)
    {
      this.mWaitProcessorTask.cancel(true);
      this.mWaitProcessorTask = null;
    }
    resetScreenOn();
    if (this.mSoundPlayer != null)
    {
      this.mSoundPlayer.release();
      this.mSoundPlayer = null;
    }
    CameraScreenNail localCameraScreenNail = (CameraScreenNail)this.mActivity.mCameraScreenNail;
    if (localCameraScreenNail.getSurfaceTexture() != null)
      localCameraScreenNail.releaseSurfaceTexture();
    System.gc();
  }

  public void onPauseBeforeSuper()
  {
    this.mPaused = true;
  }

  public void onPreviewTextureCopied()
  {
  }

  public void onResumeAfterSuper()
  {
    this.mOrientationEventListener.enable();
    this.mCaptureState = 0;
    while (true)
    {
      try
      {
        setupCamera();
        this.mSoundPlayer = SoundClips.getPlayer(this.mActivity);
        this.mRotateDialog.dismissDialog();
        if ((!this.mThreadRunning) && (this.mMosaicFrameProcessor.isMosaicMemoryAllocated()))
        {
          this.mGLRootView.setVisibility(8);
          this.mRotateDialog.showWaitingDialog(this.mDialogWaitingPreviousString);
          this.mActivity.hideUI();
          this.mWaitProcessorTask = new WaitProcessorTask(null).execute(new Void[0]);
          keepScreenOnAwhile();
          PopupManager.getInstance(this.mActivity).notifyShowPopup(null);
          this.mRootView.requestLayout();
          return;
        }
      }
      catch (CameraHardwareException localCameraHardwareException)
      {
        Util.showErrorAndFinish(this.mActivity, 2131361890);
        return;
      }
      catch (CameraDisabledException localCameraDisabledException)
      {
        Util.showErrorAndFinish(this.mActivity, 2131361891);
        return;
      }
      if (!this.mThreadRunning)
        this.mGLRootView.setVisibility(0);
      initMosaicFrameProcessorIfNeeded();
      int i = this.mPreviewArea.getWidth();
      int j = this.mPreviewArea.getHeight();
      if ((i == 0) || (j == 0))
        continue;
      configMosaicPreview(i, j);
    }
  }

  public void onResumeBeforeSuper()
  {
    this.mPaused = false;
  }

  public void onShowSwitcherPopup()
  {
  }

  public void onShutterButtonClick()
  {
    if ((this.mPaused) || (this.mThreadRunning) || (this.mCameraTexture == null));
    do
    {
      return;
      switch (this.mCaptureState)
      {
      default:
        return;
      case 0:
      case 1:
      }
    }
    while (this.mActivity.getStorageSpace() <= 50000000L);
    this.mSoundPlayer.play(1);
    startCapture();
    return;
    this.mSoundPlayer.play(2);
    stopCapture(false);
  }

  public void onShutterButtonFocus(boolean paramBoolean)
  {
  }

  public void onSingleTapUp(View paramView, int paramInt1, int paramInt2)
  {
  }

  public void onStop()
  {
  }

  public void onUserInteraction()
  {
    if (this.mCaptureState == 1)
      return;
    keepScreenOnAwhile();
  }

  public void reportProgress()
  {
    this.mSavingProgressBar.reset();
    this.mSavingProgressBar.setRightIncreasing(true);
    new Thread()
    {
      public void run()
      {
        if (!PanoramaModule.this.mThreadRunning)
          return;
        int i = PanoramaModule.this.mMosaicFrameProcessor.reportProgress(true, PanoramaModule.this.mCancelComputation);
        try
        {
          synchronized (PanoramaModule.this.mWaitObject)
          {
            PanoramaModule.this.mWaitObject.wait(50L);
            PanoramaModule.this.mActivity.runOnUiThread(new Runnable(i)
            {
              public void run()
              {
                PanoramaModule.this.mSavingProgressBar.setProgress(this.val$progress);
              }
            });
          }
        }
        catch (InterruptedException localInterruptedException)
        {
          throw new RuntimeException("Panorama reportProgress failed", localInterruptedException);
        }
      }
    }
    .start();
  }

  public void saveHighResMosaic()
  {
    runBackgroundThread(new Thread()
    {
      public void run()
      {
        PanoramaModule.this.mPartialWakeLock.acquire();
        PanoramaModule.MosaicJpeg localMosaicJpeg;
        try
        {
          localMosaicJpeg = PanoramaModule.this.generateFinalMosaic(true);
          PanoramaModule.this.mPartialWakeLock.release();
          if (localMosaicJpeg == null)
            return;
        }
        finally
        {
          PanoramaModule.this.mPartialWakeLock.release();
        }
        if (!localMosaicJpeg.isValid)
        {
          PanoramaModule.this.mMainHandler.sendEmptyMessage(2);
          return;
        }
        int i = PanoramaModule.this.getCaptureOrientation();
        Uri localUri = PanoramaModule.this.savePanorama(localMosaicJpeg.data, localMosaicJpeg.width, localMosaicJpeg.height, i);
        if (localUri != null)
        {
          PanoramaModule.this.mActivity.addSecureAlbumItemIfNeeded(false, localUri);
          Util.broadcastNewPicture(PanoramaModule.this.mActivity, localUri);
        }
        PanoramaModule.this.mMainHandler.sendMessage(PanoramaModule.this.mMainHandler.obtainMessage(3));
      }
    });
    reportProgress();
  }

  void setupProgressDirectionMatrix()
  {
    int i = Util.getDisplayOrientation(Util.getDisplayRotation(this.mActivity), CameraHolder.instance().getBackCameraId());
    this.mProgressDirectionMatrix.reset();
    this.mProgressDirectionMatrix.postRotate(i);
  }

  public void startCapture()
  {
    this.mCancelComputation = false;
    this.mTimeTaken = System.currentTimeMillis();
    this.mActivity.setSwipingEnabled(false);
    this.mActivity.hideSwitcher();
    this.mShutterButton.setImageResource(2130837546);
    this.mCaptureState = 1;
    this.mCaptureIndicator.setVisibility(0);
    showDirectionIndicators(0);
    this.mMosaicFrameProcessor.setProgressListener(new MosaicFrameProcessor.ProgressListener()
    {
      public void onProgress(boolean paramBoolean, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
      {
        float f1 = paramFloat3 * PanoramaModule.this.mHorizontalViewAngle;
        float f2 = paramFloat4 * PanoramaModule.this.mVerticalViewAngle;
        if ((paramBoolean) || (Math.abs(f1) >= 160.0F) || (Math.abs(f2) >= 160.0F))
        {
          PanoramaModule.this.stopCapture(false);
          return;
        }
        float f3 = paramFloat1 * PanoramaModule.this.mHorizontalViewAngle;
        float f4 = paramFloat2 * PanoramaModule.this.mVerticalViewAngle;
        PanoramaModule.this.updateProgress(f3, f4, f1, f2);
      }
    });
    this.mPanoProgressBar.reset();
    this.mPanoProgressBar.setIndicatorWidth(20.0F);
    this.mPanoProgressBar.setMaxProgress(160);
    this.mPanoProgressBar.setVisibility(0);
    this.mDeviceOrientationAtCapture = this.mDeviceOrientation;
    keepScreenOn();
    this.mActivity.getOrientationManager().lockOrientation();
    setupProgressDirectionMatrix();
  }

  public void updateCameraAppView()
  {
  }

  public boolean updateStorageHintOnResume()
  {
    return false;
  }

  private static class FlipBitmapDrawable extends BitmapDrawable
  {
    public FlipBitmapDrawable(Resources paramResources, Bitmap paramBitmap)
    {
      super(paramResources, paramBitmap);
    }

    public void draw(Canvas paramCanvas)
    {
      Rect localRect = getBounds();
      int i = localRect.centerX();
      int j = localRect.centerY();
      paramCanvas.save(1);
      paramCanvas.rotate(180.0F, i, j);
      super.draw(paramCanvas);
      paramCanvas.restore();
    }
  }

  private class MosaicJpeg
  {
    public final byte[] data;
    public final int height;
    public final boolean isValid;
    public final int width;

    public MosaicJpeg()
    {
      this.data = null;
      this.width = 0;
      this.height = 0;
      this.isValid = false;
    }

    public MosaicJpeg(byte[] paramInt1, int paramInt2, int arg4)
    {
      this.data = paramInt1;
      this.width = paramInt2;
      int i;
      this.height = i;
      this.isValid = true;
    }
  }

  private class PanoOrientationEventListener extends OrientationEventListener
  {
    public PanoOrientationEventListener(Context arg2)
    {
      super(localContext);
    }

    public void onOrientationChanged(int paramInt)
    {
      if (paramInt == -1);
      int i;
      do
      {
        return;
        PanoramaModule.access$002(PanoramaModule.this, Util.roundOrientation(paramInt, PanoramaModule.this.mDeviceOrientation));
        i = PanoramaModule.this.mDeviceOrientation + Util.getDisplayRotation(PanoramaModule.this.mActivity) % 360;
      }
      while (PanoramaModule.this.mOrientationCompensation == i);
      PanoramaModule.access$202(PanoramaModule.this, i);
      PanoramaModule.this.mActivity.getGLRoot().requestLayoutContentPane();
    }
  }

  private class WaitProcessorTask extends AsyncTask<Void, Void, Void>
  {
    private WaitProcessorTask()
    {
    }

    protected Void doInBackground(Void[] paramArrayOfVoid)
    {
      synchronized (PanoramaModule.this.mMosaicFrameProcessor)
      {
        while (!isCancelled())
        {
          boolean bool = PanoramaModule.this.mMosaicFrameProcessor.isMosaicMemoryAllocated();
          if (!bool)
            break;
          try
          {
            PanoramaModule.this.mMosaicFrameProcessor.wait();
          }
          catch (Exception localException)
          {
          }
        }
        return null;
      }
    }

    protected void onPostExecute(Void paramVoid)
    {
      PanoramaModule.access$3202(PanoramaModule.this, null);
      PanoramaModule.this.mRotateDialog.dismissDialog();
      PanoramaModule.this.mGLRootView.setVisibility(0);
      PanoramaModule.this.initMosaicFrameProcessorIfNeeded();
      int i = PanoramaModule.this.mPreviewArea.getWidth();
      int j = PanoramaModule.this.mPreviewArea.getHeight();
      if ((i != 0) && (j != 0))
        PanoramaModule.this.configMosaicPreview(i, j);
      PanoramaModule.this.resetToPreview();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.PanoramaModule
 * JD-Core Version:    0.5.4
 */