package com.android.camera;

import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.AutoFocusMoveCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.location.Location;
import android.media.CameraProfile;
import android.net.Uri;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.MessageQueue.IdleHandler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import com.android.camera.ui.AbstractSettingPopup;
import com.android.camera.ui.FaceView;
import com.android.camera.ui.PieRenderer;
import com.android.camera.ui.PieRenderer.PieListener;
import com.android.camera.ui.PopupManager;
import com.android.camera.ui.PreviewSurfaceView;
import com.android.camera.ui.RenderOverlay;
import com.android.camera.ui.Rotatable;
import com.android.camera.ui.RotateLayout;
import com.android.camera.ui.RotateTextToast;
import com.android.camera.ui.TwoStateImageView;
import com.android.camera.ui.ZoomRenderer;
import com.android.camera.ui.ZoomRenderer.OnZoomChangedListener;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.ui.GLRoot;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

public class PhotoModule
  implements SurfaceHolder.Callback, CameraModule, CameraPreference.OnPreferenceChangedListener, FocusOverlayManager.Listener, LocationManager.Listener, PreviewFrameLayout.OnSizeChangedListener, ShutterButton.OnShutterButtonListener, PieRenderer.PieListener
{
  private CameraActivity mActivity;
  private boolean mAeLockSupported;
  private final AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback(null);
  private final Object mAutoFocusMoveCallback;
  public long mAutoFocusTime;
  private boolean mAwbLockSupported;
  private View mBlocker;
  private final StringBuilder mBuilder = new StringBuilder();
  private CameraManager.CameraProxy mCameraDevice;
  private boolean mCameraDisabled;
  private int mCameraDisplayOrientation;
  private int mCameraId;
  CameraStartUpThread mCameraStartUpThread;
  private int mCameraState = 0;
  private volatile SurfaceHolder mCameraSurfaceHolder;
  public long mCaptureStartTime;
  private ContentResolver mContentResolver;
  private boolean mContinousFocusSupported;
  private String mCropValue;
  private int mDisplayOrientation;
  private int mDisplayRotation;
  private Runnable mDoSnapRunnable = new Runnable()
  {
    public void run()
    {
      PhotoModule.this.onShutterButtonClick();
    }
  };
  private final CameraErrorCallback mErrorCallback;
  private ImageView mExposureIndicator;
  private boolean mFaceDetectionStarted = false;
  private FaceView mFaceView;
  private boolean mFirstTimeInitialized;
  private ImageView mFlashIndicator;
  private boolean mFocusAreaSupported;
  private FocusOverlayManager mFocusManager;
  private long mFocusStartTime;
  private final Formatter mFormatter = new Formatter(this.mBuilder);
  private final Object[] mFormatterArgs = new Object[1];
  private PreviewGestures mGestures;
  private final Handler mHandler;
  private ImageView mHdrIndicator;
  private ImageNamer mImageNamer;
  private ImageSaver mImageSaver;
  private Camera.Parameters mInitialParams;
  private boolean mIsImageCaptureIntent;
  public long mJpegCallbackFinishTime;
  private byte[] mJpegImageData;
  private long mJpegPictureCallbackTime;
  private int mJpegRotation;
  private LocationManager mLocationManager;
  private ContentProviderClient mMediaProviderClient;
  private View mMenu;
  private boolean mMeteringAreaSupported;
  private long mOnResumeTime;
  private RotateLayout mOnScreenIndicators;
  private boolean mOpenCameraFail;
  private int mOrientation = -1;
  private int mOrientationCompensation = 0;
  private Camera.Parameters mParameters;
  private boolean mPaused;
  protected int mPendingSwitchCameraId = -1;
  private PhotoController mPhotoControl;
  public long mPictureDisplayedToJpegCallbackTime;
  private PieRenderer mPieRenderer;
  private AbstractSettingPopup mPopup;
  private final PostViewPictureCallback mPostViewPictureCallback = new PostViewPictureCallback(null);
  private long mPostViewPictureCallbackTime;
  private PreferenceGroup mPreferenceGroup;
  private ComboPreferences mPreferences;
  private PreviewFrameLayout mPreviewFrameLayout;
  private PreviewSurfaceView mPreviewSurfaceView;
  private boolean mQuickCapture;
  private final RawPictureCallback mRawPictureCallback = new RawPictureCallback(null);
  private long mRawPictureCallbackTime;
  private RenderOverlay mRenderOverlay;
  private Rotatable mReviewCancelButton;
  private Rotatable mReviewDoneButton;
  private View mReviewRetakeButton;
  private View mRootView;
  private Uri mSaveUri;
  private ImageView mSceneIndicator;
  private String mSceneMode;
  private ShutterButton mShutterButton;
  private final ShutterCallback mShutterCallback = new ShutterCallback(null);
  private long mShutterCallbackTime;
  public long mShutterLag;
  public long mShutterToPictureDisplayedTime;
  private boolean mSnapshotOnIdle = false;
  ConditionVariable mStartPreviewPrerequisiteReady;
  private Object mSurfaceTexture;
  private int mUpdateSet;
  private int mZoomMax;
  private List<Integer> mZoomRatios;
  private ZoomRenderer mZoomRenderer;
  private int mZoomValue;

  public PhotoModule()
  {
    if (ApiHelper.HAS_AUTO_FOCUS_MOVE_CALLBACK);
    for (AutoFocusMoveCallback localAutoFocusMoveCallback = new AutoFocusMoveCallback(null); ; localAutoFocusMoveCallback = null)
    {
      this.mAutoFocusMoveCallback = localAutoFocusMoveCallback;
      this.mErrorCallback = new CameraErrorCallback();
      this.mHandler = new MainHandler(null);
      this.mStartPreviewPrerequisiteReady = new ConditionVariable();
      return;
    }
  }

  private void addIdleHandler()
  {
    Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler()
    {
      public boolean queueIdle()
      {
        Storage.ensureOSXCompatible();
        return false;
      }
    });
  }

  private void animateFlash()
  {
    if ((!ApiHelper.HAS_SURFACE_TEXTURE) || (this.mIsImageCaptureIntent) || (!this.mActivity.mShowCameraAppView))
      return;
    ((CameraScreenNail)this.mActivity.mCameraScreenNail).animateFlash(this.mDisplayRotation);
  }

  private boolean canTakePicture()
  {
    return (isCameraIdle()) && (this.mActivity.getStorageSpace() > 50000000L);
  }

  @TargetApi(14)
  private void closeCamera()
  {
    if (this.mCameraDevice == null)
      return;
    this.mCameraDevice.setZoomChangeListener(null);
    if (ApiHelper.HAS_FACE_DETECTION)
      this.mCameraDevice.setFaceDetectionListener(null);
    this.mCameraDevice.setErrorCallback(null);
    CameraHolder.instance().release();
    this.mFaceDetectionStarted = false;
    this.mCameraDevice = null;
    setCameraState(0);
    this.mFocusManager.onCameraReleased();
  }

  private void dismissPopup(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean2)
    {
      this.mActivity.showUI();
      this.mBlocker.setVisibility(0);
    }
    setShowMenu(paramBoolean2);
    if (this.mPopup != null)
    {
      ((FrameLayout)this.mRootView).removeView(this.mPopup);
      this.mPopup = null;
    }
    this.mPhotoControl.popupDismissed(paramBoolean1);
  }

  // ERROR //
  private void doAttach()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: aload_0
    //   3: getfield 332	com/android/camera/PhotoModule:mPaused	Z
    //   6: ifeq +4 -> 10
    //   9: return
    //   10: aload_0
    //   11: getfield 391	com/android/camera/PhotoModule:mJpegImageData	[B
    //   14: astore_2
    //   15: aload_0
    //   16: getfield 547	com/android/camera/PhotoModule:mCropValue	Ljava/lang/String;
    //   19: ifnonnull +133 -> 152
    //   22: aload_0
    //   23: getfield 549	com/android/camera/PhotoModule:mSaveUri	Landroid/net/Uri;
    //   26: ifnull +72 -> 98
    //   29: aload_0
    //   30: getfield 409	com/android/camera/PhotoModule:mContentResolver	Landroid/content/ContentResolver;
    //   33: aload_0
    //   34: getfield 549	com/android/camera/PhotoModule:mSaveUri	Landroid/net/Uri;
    //   37: invokevirtual 555	android/content/ContentResolver:openOutputStream	(Landroid/net/Uri;)Ljava/io/OutputStream;
    //   40: astore 23
    //   42: aload 23
    //   44: astore_1
    //   45: aload_1
    //   46: aload_2
    //   47: invokevirtual 561	java/io/OutputStream:write	([B)V
    //   50: aload_1
    //   51: invokevirtual 564	java/io/OutputStream:close	()V
    //   54: aload_0
    //   55: getfield 419	com/android/camera/PhotoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   58: iconst_m1
    //   59: invokevirtual 567	com/android/camera/CameraActivity:setResultEx	(I)V
    //   62: aload_0
    //   63: getfield 419	com/android/camera/PhotoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   66: invokevirtual 570	com/android/camera/CameraActivity:finish	()V
    //   69: aload_1
    //   70: invokestatic 576	com/android/camera/Util:closeSilently	(Ljava/io/Closeable;)V
    //   73: return
    //   74: astore 22
    //   76: aload_1
    //   77: invokestatic 576	com/android/camera/Util:closeSilently	(Ljava/io/Closeable;)V
    //   80: return
    //   81: astore 19
    //   83: aconst_null
    //   84: astore 20
    //   86: aload 19
    //   88: astore 21
    //   90: aload 20
    //   92: invokestatic 576	com/android/camera/Util:closeSilently	(Ljava/io/Closeable;)V
    //   95: aload 21
    //   97: athrow
    //   98: aload_2
    //   99: invokestatic 582	com/android/camera/Exif:getOrientation	([B)I
    //   102: istore 17
    //   104: aload_2
    //   105: ldc_w 583
    //   108: invokestatic 587	com/android/camera/Util:makeBitmap	([BI)Landroid/graphics/Bitmap;
    //   111: iload 17
    //   113: invokestatic 591	com/android/camera/Util:rotate	(Landroid/graphics/Bitmap;I)Landroid/graphics/Bitmap;
    //   116: astore 18
    //   118: aload_0
    //   119: getfield 419	com/android/camera/PhotoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   122: iconst_m1
    //   123: new 593	android/content/Intent
    //   126: dup
    //   127: ldc_w 595
    //   130: invokespecial 597	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   133: ldc_w 599
    //   136: aload 18
    //   138: invokevirtual 603	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   141: invokevirtual 606	com/android/camera/CameraActivity:setResultEx	(ILandroid/content/Intent;)V
    //   144: aload_0
    //   145: getfield 419	com/android/camera/PhotoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   148: invokevirtual 570	com/android/camera/CameraActivity:finish	()V
    //   151: return
    //   152: aload_0
    //   153: getfield 419	com/android/camera/PhotoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   156: ldc_w 608
    //   159: invokevirtual 612	com/android/camera/CameraActivity:getFileStreamPath	(Ljava/lang/String;)Ljava/io/File;
    //   162: astore 9
    //   164: aload 9
    //   166: invokevirtual 617	java/io/File:delete	()Z
    //   169: pop
    //   170: aload_0
    //   171: getfield 419	com/android/camera/PhotoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   174: ldc_w 608
    //   177: iconst_0
    //   178: invokevirtual 621	com/android/camera/CameraActivity:openFileOutput	(Ljava/lang/String;I)Ljava/io/FileOutputStream;
    //   181: astore 11
    //   183: aload 11
    //   185: astore_1
    //   186: aload_1
    //   187: aload_2
    //   188: invokevirtual 624	java/io/FileOutputStream:write	([B)V
    //   191: aload_1
    //   192: invokevirtual 625	java/io/FileOutputStream:close	()V
    //   195: aload 9
    //   197: invokestatic 631	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
    //   200: astore 12
    //   202: aload_1
    //   203: invokestatic 576	com/android/camera/Util:closeSilently	(Ljava/io/Closeable;)V
    //   206: new 633	android/os/Bundle
    //   209: dup
    //   210: invokespecial 634	android/os/Bundle:<init>	()V
    //   213: astore 13
    //   215: aload_0
    //   216: getfield 547	com/android/camera/PhotoModule:mCropValue	Ljava/lang/String;
    //   219: ldc_w 636
    //   222: invokevirtual 642	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   225: ifeq +14 -> 239
    //   228: aload 13
    //   230: ldc_w 644
    //   233: ldc_w 646
    //   236: invokevirtual 650	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   239: aload_0
    //   240: getfield 549	com/android/camera/PhotoModule:mSaveUri	Landroid/net/Uri;
    //   243: ifnull +134 -> 377
    //   246: aload 13
    //   248: ldc_w 652
    //   251: aload_0
    //   252: getfield 549	com/android/camera/PhotoModule:mSaveUri	Landroid/net/Uri;
    //   255: invokevirtual 656	android/os/Bundle:putParcelable	(Ljava/lang/String;Landroid/os/Parcelable;)V
    //   258: aload_0
    //   259: getfield 419	com/android/camera/PhotoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   262: invokevirtual 659	com/android/camera/CameraActivity:isSecureCamera	()Z
    //   265: ifeq +12 -> 277
    //   268: aload 13
    //   270: ldc_w 661
    //   273: iconst_1
    //   274: invokevirtual 665	android/os/Bundle:putBoolean	(Ljava/lang/String;Z)V
    //   277: new 593	android/content/Intent
    //   280: dup
    //   281: ldc_w 667
    //   284: invokespecial 597	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   287: astore 14
    //   289: aload 14
    //   291: aload 12
    //   293: invokevirtual 671	android/content/Intent:setData	(Landroid/net/Uri;)Landroid/content/Intent;
    //   296: pop
    //   297: aload 14
    //   299: aload 13
    //   301: invokevirtual 675	android/content/Intent:putExtras	(Landroid/os/Bundle;)Landroid/content/Intent;
    //   304: pop
    //   305: aload_0
    //   306: getfield 419	com/android/camera/PhotoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   309: aload 14
    //   311: sipush 1000
    //   314: invokevirtual 679	com/android/camera/CameraActivity:startActivityForResult	(Landroid/content/Intent;I)V
    //   317: return
    //   318: astore 8
    //   320: aload_0
    //   321: getfield 419	com/android/camera/PhotoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   324: iconst_0
    //   325: invokevirtual 567	com/android/camera/CameraActivity:setResultEx	(I)V
    //   328: aload_0
    //   329: getfield 419	com/android/camera/PhotoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   332: invokevirtual 570	com/android/camera/CameraActivity:finish	()V
    //   335: aload_1
    //   336: invokestatic 576	com/android/camera/Util:closeSilently	(Ljava/io/Closeable;)V
    //   339: return
    //   340: astore 6
    //   342: aload_0
    //   343: getfield 419	com/android/camera/PhotoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   346: iconst_0
    //   347: invokevirtual 567	com/android/camera/CameraActivity:setResultEx	(I)V
    //   350: aload_0
    //   351: getfield 419	com/android/camera/PhotoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   354: invokevirtual 570	com/android/camera/CameraActivity:finish	()V
    //   357: aload_1
    //   358: invokestatic 576	com/android/camera/Util:closeSilently	(Ljava/io/Closeable;)V
    //   361: return
    //   362: astore_3
    //   363: aconst_null
    //   364: astore 4
    //   366: aload_3
    //   367: astore 5
    //   369: aload 4
    //   371: invokestatic 576	com/android/camera/Util:closeSilently	(Ljava/io/Closeable;)V
    //   374: aload 5
    //   376: athrow
    //   377: aload 13
    //   379: ldc_w 681
    //   382: iconst_1
    //   383: invokevirtual 665	android/os/Bundle:putBoolean	(Ljava/lang/String;Z)V
    //   386: goto -128 -> 258
    //   389: astore 7
    //   391: aload_1
    //   392: astore 4
    //   394: aload 7
    //   396: astore 5
    //   398: goto -29 -> 369
    //   401: astore 24
    //   403: aload_1
    //   404: astore 20
    //   406: aload 24
    //   408: astore 21
    //   410: goto -320 -> 90
    //
    // Exception table:
    //   from	to	target	type
    //   29	42	74	java/io/IOException
    //   45	69	74	java/io/IOException
    //   29	42	81	finally
    //   152	183	318	java/io/FileNotFoundException
    //   186	202	318	java/io/FileNotFoundException
    //   152	183	340	java/io/IOException
    //   186	202	340	java/io/IOException
    //   152	183	362	finally
    //   186	202	389	finally
    //   320	335	389	finally
    //   342	357	389	finally
    //   45	69	401	finally
  }

  private void doCancel()
  {
    this.mActivity.setResultEx(0, new Intent());
    this.mActivity.finish();
  }

  private int getPreferredCameraId(ComboPreferences paramComboPreferences)
  {
    int i = Util.getCameraFacingIntentExtras(this.mActivity);
    if (i != -1)
      return i;
    return CameraSettings.readPreferredCameraId(paramComboPreferences);
  }

  private void hidePostCaptureAlert()
  {
    if (!this.mIsImageCaptureIntent)
      return;
    this.mOnScreenIndicators.setVisibility(0);
    this.mMenu.setVisibility(0);
    Util.fadeOut((View)this.mReviewDoneButton);
    this.mShutterButton.setVisibility(0);
    Util.fadeOut(this.mReviewRetakeButton);
  }

  private void initOnScreenIndicator()
  {
    this.mOnScreenIndicators = ((RotateLayout)this.mRootView.findViewById(2131558524));
    this.mExposureIndicator = ((ImageView)this.mOnScreenIndicators.findViewById(2131558527));
    this.mFlashIndicator = ((ImageView)this.mOnScreenIndicators.findViewById(2131558526));
    this.mSceneIndicator = ((ImageView)this.mOnScreenIndicators.findViewById(2131558528));
    this.mHdrIndicator = ((ImageView)this.mOnScreenIndicators.findViewById(2131558525));
  }

  private void initializeAfterCameraOpen()
  {
    if (this.mPieRenderer == null)
    {
      this.mPieRenderer = new PieRenderer(this.mActivity);
      this.mPhotoControl = new PhotoController(this.mActivity, this, this.mPieRenderer);
      this.mPhotoControl.setListener(this);
      this.mPieRenderer.setPieListener(this);
    }
    if (this.mZoomRenderer == null)
      this.mZoomRenderer = new ZoomRenderer(this.mActivity);
    if (this.mGestures == null)
      this.mGestures = new PreviewGestures(this.mActivity, this, this.mZoomRenderer, this.mPieRenderer);
    initializeRenderOverlay();
    initializePhotoControl();
    setPreviewFrameLayoutAspectRatio();
    this.mFocusManager.setPreviewSize(this.mPreviewFrameLayout.getWidth(), this.mPreviewFrameLayout.getHeight());
    loadCameraPreferences();
    initializeZoom();
    updateOnScreenIndicators();
    showTapToFocusToastIfNeeded();
  }

  private void initializeCapabilities()
  {
    this.mInitialParams = this.mCameraDevice.getParameters();
    this.mFocusAreaSupported = Util.isFocusAreaSupported(this.mInitialParams);
    this.mMeteringAreaSupported = Util.isMeteringAreaSupported(this.mInitialParams);
    this.mAeLockSupported = Util.isAutoExposureLockSupported(this.mInitialParams);
    this.mAwbLockSupported = Util.isAutoWhiteBalanceLockSupported(this.mInitialParams);
    this.mContinousFocusSupported = this.mInitialParams.getSupportedFocusModes().contains("continuous-picture");
  }

  private void initializeControlByIntent()
  {
    this.mBlocker = this.mRootView.findViewById(2131558542);
    this.mMenu = this.mRootView.findViewById(2131558543);
    this.mMenu.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        if (PhotoModule.this.mPieRenderer == null)
          return;
        PhotoModule.this.mPieRenderer.showInCenter();
      }
    });
    if (!this.mIsImageCaptureIntent)
      return;
    this.mActivity.hideSwitcher();
    this.mReviewDoneButton = ((Rotatable)this.mRootView.findViewById(2131558596));
    this.mReviewCancelButton = ((Rotatable)this.mRootView.findViewById(2131558598));
    this.mReviewRetakeButton = this.mRootView.findViewById(2131558597);
    ((View)this.mReviewCancelButton).setVisibility(0);
    ((View)this.mReviewDoneButton).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        PhotoModule.this.onReviewDoneClicked(paramView);
      }
    });
    ((View)this.mReviewCancelButton).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        PhotoModule.this.onReviewCancelClicked(paramView);
      }
    });
    this.mReviewRetakeButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        PhotoModule.this.onReviewRetakeClicked(paramView);
      }
    });
    if (this.mReviewDoneButton instanceof TwoStateImageView)
      ((TwoStateImageView)this.mReviewDoneButton).enableFilter(false);
    setupCaptureParams();
  }

  private void initializeFirstTime()
  {
    if (this.mFirstTimeInitialized)
      return;
    boolean bool = RecordLocationPreference.get(this.mPreferences, this.mContentResolver);
    this.mLocationManager.recordLocation(bool);
    keepMediaProviderInstance();
    this.mShutterButton = this.mActivity.getShutterButton();
    this.mShutterButton.setImageResource(2130837532);
    this.mShutterButton.setOnShutterButtonListener(this);
    this.mShutterButton.setVisibility(0);
    this.mImageSaver = new ImageSaver();
    this.mImageNamer = new ImageNamer();
    this.mFirstTimeInitialized = true;
    addIdleHandler();
    this.mActivity.updateStorageSpaceAndHint();
  }

  private void initializeFocusManager()
  {
    int i = 1;
    this.mRenderOverlay = ((RenderOverlay)this.mRootView.findViewById(2131558584));
    if (this.mFocusManager != null)
    {
      this.mFocusManager.removeMessages();
      return;
    }
    if (CameraHolder.instance().getCameraInfo()[this.mCameraId].facing == i);
    while (true)
    {
      String[] arrayOfString = this.mActivity.getResources().getStringArray(2131427366);
      this.mFocusManager = new FocusOverlayManager(this.mPreferences, arrayOfString, this.mInitialParams, this, i, this.mActivity.getMainLooper());
      return;
      int j = 0;
    }
  }

  private void initializeMiscControls()
  {
    this.mPreviewFrameLayout = ((PreviewFrameLayout)this.mRootView.findViewById(2131558581));
    this.mActivity.setSingleTapUpListener(this.mPreviewFrameLayout);
    this.mFaceView = ((FaceView)this.mRootView.findViewById(2131558583));
    this.mPreviewFrameLayout.setOnSizeChangedListener(this);
    this.mPreviewFrameLayout.setOnLayoutChangeListener(this.mActivity);
    if (ApiHelper.HAS_SURFACE_TEXTURE)
      return;
    this.mPreviewSurfaceView = ((PreviewSurfaceView)this.mRootView.findViewById(2131558591));
    this.mPreviewSurfaceView.setVisibility(0);
    this.mPreviewSurfaceView.getHolder().addCallback(this);
  }

  private void initializePhotoControl()
  {
    loadCameraPreferences();
    if (this.mPhotoControl != null)
      this.mPhotoControl.initialize(this.mPreferenceGroup);
    updateSceneModeUI();
  }

  private void initializeRenderOverlay()
  {
    if (this.mPieRenderer != null)
    {
      this.mRenderOverlay.addRenderer(this.mPieRenderer);
      this.mFocusManager.setFocusRenderer(this.mPieRenderer);
    }
    if (this.mZoomRenderer != null)
      this.mRenderOverlay.addRenderer(this.mZoomRenderer);
    if (this.mGestures != null)
    {
      this.mGestures.clearTouchReceivers();
      this.mGestures.setRenderOverlay(this.mRenderOverlay);
      this.mGestures.addTouchReceiver(this.mMenu);
      this.mGestures.addTouchReceiver(this.mBlocker);
      if (isImageCaptureIntent())
      {
        if (this.mReviewCancelButton != null)
          this.mGestures.addTouchReceiver((View)this.mReviewCancelButton);
        if (this.mReviewDoneButton != null)
          this.mGestures.addTouchReceiver((View)this.mReviewDoneButton);
      }
    }
    this.mRenderOverlay.requestLayout();
  }

  private void initializeSecondTime()
  {
    boolean bool = RecordLocationPreference.get(this.mPreferences, this.mContentResolver);
    this.mLocationManager.recordLocation(bool);
    this.mImageSaver = new ImageSaver();
    this.mImageNamer = new ImageNamer();
    initializeZoom();
    keepMediaProviderInstance();
    hidePostCaptureAlert();
    if (this.mPhotoControl == null)
      return;
    this.mPhotoControl.reloadPreferences();
  }

  private void initializeZoom()
  {
    if ((this.mParameters == null) || (!this.mParameters.isZoomSupported()) || (this.mZoomRenderer == null));
    do
    {
      return;
      this.mZoomMax = this.mParameters.getMaxZoom();
      this.mZoomRatios = this.mParameters.getZoomRatios();
    }
    while (this.mZoomRenderer == null);
    this.mZoomRenderer.setZoomMax(this.mZoomMax);
    this.mZoomRenderer.setZoom(this.mParameters.getZoom());
    this.mZoomRenderer.setZoomValue(((Integer)this.mZoomRatios.get(this.mParameters.getZoom())).intValue());
    this.mZoomRenderer.setOnZoomChangeListener(new ZoomChangeListener(null));
  }

  private boolean isCameraIdle()
  {
    return (this.mCameraState == 1) || ((this.mFocusManager != null) && (this.mFocusManager.isFocusCompleted()) && (this.mCameraState != 4));
  }

  private boolean isImageCaptureIntent()
  {
    String str = this.mActivity.getIntent().getAction();
    return ("android.media.action.IMAGE_CAPTURE".equals(str)) || ("android.media.action.IMAGE_CAPTURE_SECURE".equals(str));
  }

  private void keepMediaProviderInstance()
  {
    if (this.mMediaProviderClient != null)
      return;
    this.mMediaProviderClient = this.mContentResolver.acquireContentProviderClient("media");
  }

  private void keepScreenOnAwhile()
  {
    this.mHandler.removeMessages(3);
    this.mActivity.getWindow().addFlags(128);
    this.mHandler.sendEmptyMessageDelayed(3, 120000L);
  }

  private void loadCameraPreferences()
  {
    this.mPreferenceGroup = new CameraSettings(this.mActivity, this.mInitialParams, this.mCameraId, CameraHolder.instance().getCameraInfo()).getPreferenceGroup(2131165184);
  }

  private void locationFirstRun()
  {
    if (RecordLocationPreference.isSet(this.mPreferences));
    do
      return;
    while (CameraHolder.instance().getBackCameraId() == -1);
    new AlertDialog.Builder(this.mActivity).setTitle(2131362061).setMessage(2131362062).setPositiveButton(2131362064, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        PhotoModule.this.setLocationPreference("on");
      }
    }).setNegativeButton(2131362063, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        paramDialogInterface.cancel();
      }
    }).setOnCancelListener(new DialogInterface.OnCancelListener()
    {
      public void onCancel(DialogInterface paramDialogInterface)
      {
        PhotoModule.this.setLocationPreference("off");
      }
    }).show();
  }

  private void overrideCameraSettings(String paramString1, String paramString2, String paramString3)
  {
    if (this.mPhotoControl == null)
      return;
    this.mPhotoControl.overrideSettings(new String[] { "pref_camera_flashmode_key", paramString1, "pref_camera_whitebalance_key", paramString2, "pref_camera_focusmode_key", paramString3 });
  }

  private void resetExposureCompensation()
  {
    if ("0".equals(this.mPreferences.getString("pref_camera_exposure_key", "0")))
      return;
    SharedPreferences.Editor localEditor = this.mPreferences.edit();
    localEditor.putString("pref_camera_exposure_key", "0");
    localEditor.apply();
  }

  private void resetScreenOn()
  {
    this.mHandler.removeMessages(3);
    this.mActivity.getWindow().clearFlags(128);
  }

  @TargetApi(16)
  private void setAutoExposureLockIfSupported()
  {
    if (!this.mAeLockSupported)
      return;
    this.mParameters.setAutoExposureLock(this.mFocusManager.getAeAwbLock());
  }

  @TargetApi(16)
  private void setAutoWhiteBalanceLockIfSupported()
  {
    if (!this.mAwbLockSupported)
      return;
    this.mParameters.setAutoWhiteBalanceLock(this.mFocusManager.getAeAwbLock());
  }

  private void setCameraParameters(int paramInt)
  {
    if ((paramInt & 0x1) != 0)
      updateCameraParametersInitialize();
    if ((paramInt & 0x2) != 0)
      updateCameraParametersZoom();
    if ((paramInt & 0x4) != 0)
      updateCameraParametersPreference();
    this.mCameraDevice.setParameters(this.mParameters);
  }

  private void setCameraParametersWhenIdle(int paramInt)
  {
    this.mUpdateSet = (paramInt | this.mUpdateSet);
    if (this.mCameraDevice == null)
      this.mUpdateSet = 0;
    do
    {
      return;
      if (!isCameraIdle())
        continue;
      setCameraParameters(this.mUpdateSet);
      updateSceneModeUI();
      this.mUpdateSet = 0;
      return;
    }
    while (this.mHandler.hasMessages(4));
    this.mHandler.sendEmptyMessageDelayed(4, 1000L);
  }

  private void setCameraState(int paramInt)
  {
    this.mCameraState = paramInt;
    switch (paramInt)
    {
    default:
    case 0:
    case 2:
    case 3:
    case 4:
    case 1:
    }
    do
    {
      do
        return;
      while (this.mGestures == null);
      this.mGestures.setEnabled(false);
      return;
    }
    while ((this.mGestures == null) || (!this.mActivity.mShowCameraAppView));
    this.mGestures.setEnabled(true);
  }

  private void setDisplayOrientation()
  {
    this.mDisplayRotation = Util.getDisplayRotation(this.mActivity);
    this.mDisplayOrientation = Util.getDisplayOrientation(this.mDisplayRotation, this.mCameraId);
    this.mCameraDisplayOrientation = Util.getDisplayOrientation(0, this.mCameraId);
    if (this.mFaceView != null)
      this.mFaceView.setDisplayOrientation(this.mDisplayOrientation);
    if (this.mFocusManager != null)
      this.mFocusManager.setDisplayOrientation(this.mDisplayOrientation);
    this.mActivity.getGLRoot().requestLayoutContentPane();
  }

  @TargetApi(14)
  private void setFocusAreasIfSupported()
  {
    if (!this.mFocusAreaSupported)
      return;
    this.mParameters.setFocusAreas(this.mFocusManager.getFocusAreas());
  }

  private void setLocationPreference(String paramString)
  {
    this.mPreferences.edit().putString("pref_camera_recordlocation_key", paramString).apply();
    onSharedPreferenceChanged();
  }

  @TargetApi(14)
  private void setMeteringAreasIfSupported()
  {
    if (!this.mMeteringAreaSupported)
      return;
    this.mParameters.setMeteringAreas(this.mFocusManager.getMeteringAreas());
  }

  private void setShowMenu(boolean paramBoolean)
  {
    int j;
    if (this.mOnScreenIndicators != null)
    {
      RotateLayout localRotateLayout = this.mOnScreenIndicators;
      if (!paramBoolean)
        break label51;
      j = 0;
      label20: localRotateLayout.setVisibility(j);
    }
    View localView;
    int i;
    if (this.mMenu != null)
    {
      localView = this.mMenu;
      i = 0;
      if (!paramBoolean)
        break label58;
    }
    while (true)
    {
      localView.setVisibility(i);
      return;
      label51: j = 8;
      break label20:
      label58: i = 8;
    }
  }

  private void setupCaptureParams()
  {
    Bundle localBundle = this.mActivity.getIntent().getExtras();
    if (localBundle == null)
      return;
    this.mSaveUri = ((Uri)localBundle.getParcelable("output"));
    this.mCropValue = localBundle.getString("crop");
  }

  private void setupPreview()
  {
    this.mFocusManager.resetTouchFocus();
    startPreview();
    setCameraState(1);
    startFaceDetection();
  }

  private void showPostCaptureAlert()
  {
    if (!this.mIsImageCaptureIntent)
      return;
    this.mOnScreenIndicators.setVisibility(8);
    this.mMenu.setVisibility(8);
    Util.fadeIn((View)this.mReviewDoneButton);
    this.mShutterButton.setVisibility(4);
    Util.fadeIn(this.mReviewRetakeButton);
  }

  private void showTapToFocusToast()
  {
    new RotateTextToast(this.mActivity, 2131361975, this.mOrientationCompensation).show();
    SharedPreferences.Editor localEditor = this.mPreferences.edit();
    localEditor.putBoolean("pref_camera_first_use_hint_shown_key", false);
    localEditor.apply();
  }

  private void showTapToFocusToastIfNeeded()
  {
    if ((!this.mFocusAreaSupported) || (!this.mPreferences.getBoolean("pref_camera_first_use_hint_shown_key", true)))
      return;
    this.mHandler.sendEmptyMessageDelayed(6, 1000L);
  }

  private void startPreview()
  {
    this.mCameraDevice.setErrorCallback(this.mErrorCallback);
    if (this.mCameraState != 0)
      stopPreview();
    setDisplayOrientation();
    if (!this.mSnapshotOnIdle)
    {
      if ("continuous-picture".equals(this.mFocusManager.getFocusMode()))
        this.mCameraDevice.cancelAutoFocus();
      this.mFocusManager.setAeAwbLock(false);
    }
    setCameraParameters(-1);
    CameraScreenNail localCameraScreenNail;
    Camera.Size localSize;
    if (ApiHelper.HAS_SURFACE_TEXTURE)
    {
      localCameraScreenNail = (CameraScreenNail)this.mActivity.mCameraScreenNail;
      if (this.mSurfaceTexture == null)
      {
        localSize = this.mParameters.getPreviewSize();
        if (this.mCameraDisplayOrientation % 180 != 0)
          break label219;
        localCameraScreenNail.setSize(localSize.width, localSize.height);
        label127: localCameraScreenNail.enableAspectRatioClamping();
        this.mActivity.notifyScreenNailChanged();
        localCameraScreenNail.acquireSurfaceTexture();
        this.mSurfaceTexture = localCameraScreenNail.getSurfaceTexture();
      }
      this.mCameraDevice.setDisplayOrientation(this.mCameraDisplayOrientation);
      this.mCameraDevice.setPreviewTextureAsync((SurfaceTexture)this.mSurfaceTexture);
    }
    while (true)
    {
      Log.v("CAM_PhotoModule", "startPreview");
      this.mCameraDevice.startPreviewAsync();
      this.mFocusManager.onPreviewStarted();
      if (this.mSnapshotOnIdle)
        this.mHandler.post(this.mDoSnapRunnable);
      return;
      label219: localCameraScreenNail.setSize(localSize.height, localSize.width);
      break label127:
      this.mCameraDevice.setDisplayOrientation(this.mDisplayOrientation);
      this.mCameraDevice.setPreviewDisplayAsync(this.mCameraSurfaceHolder);
    }
  }

  private void stopPreview()
  {
    if ((this.mCameraDevice != null) && (this.mCameraState != 0))
    {
      Log.v("CAM_PhotoModule", "stopPreview");
      this.mCameraDevice.stopPreview();
      this.mFaceDetectionStarted = false;
    }
    setCameraState(0);
    if (this.mFocusManager == null)
      return;
    this.mFocusManager.onPreviewStopped();
  }

  private void switchCamera()
  {
    int i = 1;
    if (this.mPaused)
      return;
    Log.v("CAM_PhotoModule", "Start to switch camera. id=" + this.mPendingSwitchCameraId);
    this.mCameraId = this.mPendingSwitchCameraId;
    this.mPendingSwitchCameraId = -1;
    this.mPhotoControl.setCameraId(this.mCameraId);
    closeCamera();
    collapseCameraControls();
    if (this.mFaceView != null)
      this.mFaceView.clear();
    if (this.mFocusManager != null)
      this.mFocusManager.removeMessages();
    this.mPreferences.setLocalId(this.mActivity, this.mCameraId);
    CameraSettings.upgradeLocalPreferences(this.mPreferences.getLocal());
    while (true)
    {
      try
      {
        this.mCameraDevice = Util.openCamera(this.mActivity, this.mCameraId);
        this.mParameters = this.mCameraDevice.getParameters();
        initializeCapabilities();
        if (CameraHolder.instance().getCameraInfo()[this.mCameraId].facing == i)
        {
          this.mFocusManager.setMirror(i);
          this.mFocusManager.setParameters(this.mInitialParams);
          setupPreview();
          loadCameraPreferences();
          initializePhotoControl();
          initializeZoom();
          updateOnScreenIndicators();
          showTapToFocusToastIfNeeded();
          if (ApiHelper.HAS_SURFACE_TEXTURE);
          this.mHandler.sendEmptyMessage(8);
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
      int j = 0;
    }
  }

  @TargetApi(16)
  private void updateAutoFocusMoveCallback()
  {
    if (this.mParameters.getFocusMode().equals("continuous-picture"))
    {
      this.mCameraDevice.setAutoFocusMoveCallback((AutoFocusMoveCallback)this.mAutoFocusMoveCallback);
      return;
    }
    this.mCameraDevice.setAutoFocusMoveCallback(null);
  }

  private void updateCameraParametersInitialize()
  {
    List localList = this.mParameters.getSupportedPreviewFrameRates();
    if (localList != null)
    {
      Integer localInteger = (Integer)Collections.max(localList);
      this.mParameters.setPreviewFrameRate(localInteger.intValue());
    }
    this.mParameters.set("recording-hint", "false");
    if (!"true".equals(this.mParameters.get("video-stabilization-supported")))
      return;
    this.mParameters.set("video-stabilization", "false");
  }

  private void updateCameraParametersPreference()
  {
    setAutoExposureLockIfSupported();
    setAutoWhiteBalanceLockIfSupported();
    setFocusAreasIfSupported();
    setMeteringAreasIfSupported();
    String str1 = this.mPreferences.getString("pref_camera_picturesize_key", null);
    label43: label228: int j;
    if (str1 == null)
    {
      CameraSettings.initialCameraPictureSize(this.mActivity, this.mParameters);
      Camera.Size localSize1 = this.mParameters.getPictureSize();
      List localList = this.mParameters.getSupportedPreviewSizes();
      Camera.Size localSize2 = Util.getOptimalPreviewSize(this.mActivity, localList, localSize1.width / localSize1.height);
      if (!this.mParameters.getPreviewSize().equals(localSize2))
      {
        this.mParameters.setPreviewSize(localSize2.width, localSize2.height);
        this.mCameraDevice.setParameters(this.mParameters);
        this.mParameters = this.mCameraDevice.getParameters();
      }
      Log.v("CAM_PhotoModule", "Preview size is " + localSize2.width + "x" + localSize2.height);
      String str2 = this.mPreferences.getString("pref_camera_hdr_key", this.mActivity.getString(2131361960));
      if (!this.mActivity.getString(2131361910).equals(str2))
        break label524;
      this.mSceneMode = "hdr";
      if (!Util.isSupported(this.mSceneMode, this.mParameters.getSupportedSceneModes()))
        break label551;
      if (!this.mParameters.getSceneMode().equals(this.mSceneMode))
      {
        this.mParameters.setSceneMode(this.mSceneMode);
        this.mCameraDevice.setParameters(this.mParameters);
        this.mParameters = this.mCameraDevice.getParameters();
      }
      label295: int i = CameraProfile.getJpegEncodingQualityParameter(this.mCameraId, 2);
      this.mParameters.setJpegQuality(i);
      j = CameraSettings.readExposure(this.mPreferences);
      int k = this.mParameters.getMaxExposureCompensation();
      if ((j < this.mParameters.getMinExposureCompensation()) || (j > k))
        break label579;
      this.mParameters.setExposureCompensation(j);
      label360: if (!"auto".equals(this.mSceneMode))
        break label647;
      String str3 = this.mPreferences.getString("pref_camera_flashmode_key", this.mActivity.getString(2131361935));
      if (!Util.isSupported(str3, this.mParameters.getSupportedFlashModes()))
        break label610;
      this.mParameters.setFlashMode(str3);
      label419: String str4 = this.mPreferences.getString("pref_camera_whitebalance_key", this.mActivity.getString(2131361942));
      if (!Util.isSupported(str4, this.mParameters.getSupportedWhiteBalance()))
        break label634;
      this.mParameters.setWhiteBalance(str4);
      label465: this.mFocusManager.overrideFocusMode(null);
      this.mParameters.setFocusMode(this.mFocusManager.getFocusMode());
    }
    while (true)
    {
      if ((this.mContinousFocusSupported) && (ApiHelper.HAS_AUTO_FOCUS_MOVE_CALLBACK))
        updateAutoFocusMoveCallback();
      return;
      CameraSettings.setCameraPictureSize(str1, this.mParameters.getSupportedPictureSizes(), this.mParameters);
      break label43:
      label524: this.mSceneMode = this.mPreferences.getString("pref_camera_scenemode_key", this.mActivity.getString(2131361949));
      break label228:
      label551: this.mSceneMode = this.mParameters.getSceneMode();
      if (this.mSceneMode == null);
      this.mSceneMode = "auto";
      break label295:
      label579: Log.w("CAM_PhotoModule", "invalid exposure range: " + j);
      break label360:
      label610: if (this.mParameters.getFlashMode() == null);
      this.mActivity.getString(2131361936);
      break label419:
      label634: if (this.mParameters.getWhiteBalance() == null);
      break label465:
      label647: this.mFocusManager.overrideFocusMode(this.mParameters.getFocusMode());
    }
  }

  private void updateCameraParametersZoom()
  {
    if (!this.mParameters.isZoomSupported())
      return;
    this.mParameters.setZoom(this.mZoomValue);
  }

  private void updateExposureOnScreenIndicator(int paramInt)
  {
    if (this.mExposureIndicator == null)
      return;
    int i = Math.round(this.mParameters.getExposureCompensationStep() * paramInt);
    int j = 0;
    switch (i)
    {
    default:
    case -3:
    case -2:
    case -1:
    case 0:
    case 1:
    case 2:
    case 3:
    }
    while (true)
    {
      this.mExposureIndicator.setImageResource(j);
      return;
      j = 2130837684;
      continue;
      j = 2130837683;
      continue;
      j = 2130837682;
      continue;
      j = 2130837681;
      continue;
      j = 2130837685;
      continue;
      j = 2130837686;
      continue;
      j = 2130837687;
    }
  }

  private void updateFlashOnScreenIndicator(String paramString)
  {
    if (this.mFlashIndicator == null)
      return;
    if ((paramString == null) || ("off".equals(paramString)))
    {
      this.mFlashIndicator.setImageResource(2130837689);
      return;
    }
    if ("auto".equals(paramString))
    {
      this.mFlashIndicator.setImageResource(2130837688);
      return;
    }
    if ("on".equals(paramString))
    {
      this.mFlashIndicator.setImageResource(2130837690);
      return;
    }
    this.mFlashIndicator.setImageResource(2130837689);
  }

  private void updateHdrOnScreenIndicator(String paramString)
  {
    if (this.mHdrIndicator == null)
      return;
    if ((paramString != null) && ("hdr".equals(paramString)))
    {
      this.mHdrIndicator.setImageResource(2130837692);
      return;
    }
    this.mHdrIndicator.setImageResource(2130837691);
  }

  private void updateOnScreenIndicators()
  {
    updateSceneOnScreenIndicator(this.mParameters.getSceneMode());
    updateExposureOnScreenIndicator(CameraSettings.readExposure(this.mPreferences));
    updateFlashOnScreenIndicator(this.mParameters.getFlashMode());
    updateHdrOnScreenIndicator(this.mParameters.getSceneMode());
  }

  private void updateSceneModeUI()
  {
    if (!"auto".equals(this.mSceneMode))
    {
      overrideCameraSettings(this.mParameters.getFlashMode(), this.mParameters.getWhiteBalance(), this.mParameters.getFocusMode());
      return;
    }
    overrideCameraSettings(null, null, null);
  }

  private void updateSceneOnScreenIndicator(String paramString)
  {
    if (this.mSceneIndicator == null)
      return;
    if ((paramString == null) || ("auto".equals(paramString)) || ("hdr".equals(paramString)))
    {
      this.mSceneIndicator.setImageResource(2130837693);
      return;
    }
    this.mSceneIndicator.setImageResource(2130837694);
  }

  public void autoFocus()
  {
    this.mFocusStartTime = System.currentTimeMillis();
    this.mCameraDevice.autoFocus(this.mAutoFocusCallback);
    setCameraState(2);
  }

  public void cancelAutoFocus()
  {
    this.mCameraDevice.cancelAutoFocus();
    setCameraState(1);
    setCameraParameters(4);
  }

  public boolean capture()
  {
    if ((this.mCameraDevice == null) || (this.mCameraState == 3) || (this.mCameraState == 4))
      return false;
    this.mCaptureStartTime = System.currentTimeMillis();
    this.mPostViewPictureCallbackTime = 0L;
    this.mJpegImageData = null;
    if (this.mSceneMode == "hdr");
    for (int i = 1; ; i = 0)
    {
      if (i != 0)
      {
        animateFlash();
        this.mActivity.hideSwitcher();
      }
      this.mJpegRotation = Util.getJpegRotation(this.mCameraId, this.mOrientation);
      this.mParameters.setRotation(this.mJpegRotation);
      Location localLocation = this.mLocationManager.getCurrentLocation();
      Util.setGpsParameters(this.mParameters, localLocation);
      this.mCameraDevice.setParameters(this.mParameters);
      this.mCameraDevice.takePicture2(this.mShutterCallback, this.mRawPictureCallback, this.mPostViewPictureCallback, new JpegPictureCallback(localLocation), this.mCameraState, this.mFocusManager.getFocusState());
      if (i == 0)
        animateFlash();
      Camera.Size localSize = this.mParameters.getPictureSize();
      this.mImageNamer.prepareUri(this.mContentResolver, this.mCaptureStartTime, localSize.width, localSize.height, this.mJpegRotation);
      this.mFaceDetectionStarted = false;
      setCameraState(3);
      return true;
    }
  }

  public boolean collapseCameraControls()
  {
    AbstractSettingPopup localAbstractSettingPopup = this.mPopup;
    int i = 0;
    if (localAbstractSettingPopup != null)
    {
      dismissPopup(false);
      i = 1;
    }
    return i;
  }

  public void dismissPopup(boolean paramBoolean)
  {
    dismissPopup(paramBoolean, true);
  }

  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mCameraState == 4)
      return true;
    if (this.mPopup != null)
      return this.mActivity.superDispatchTouchEvent(paramMotionEvent);
    if ((this.mGestures != null) && (this.mRenderOverlay != null))
      return this.mGestures.dispatchTouch(paramMotionEvent);
    return false;
  }

  public void hideGpsOnScreenIndicator()
  {
  }

  public void init(CameraActivity paramCameraActivity, View paramView, boolean paramBoolean)
  {
    boolean bool1 = true;
    this.mActivity = paramCameraActivity;
    this.mRootView = paramView;
    this.mPreferences = new ComboPreferences(this.mActivity);
    CameraSettings.upgradeGlobalPreferences(this.mPreferences.getGlobal());
    this.mCameraId = getPreferredCameraId(this.mPreferences);
    this.mContentResolver = this.mActivity.getContentResolver();
    this.mCameraStartUpThread = new CameraStartUpThread(null);
    this.mCameraStartUpThread.start();
    this.mActivity.getLayoutInflater().inflate(2130968624, (ViewGroup)this.mRootView);
    this.mIsImageCaptureIntent = isImageCaptureIntent();
    if (paramBoolean)
    {
      CameraActivity localCameraActivity2 = this.mActivity;
      if (!this.mIsImageCaptureIntent);
      for (boolean bool2 = bool1; ; bool2 = false)
      {
        localCameraActivity2.reuseCameraScreenNail(bool2);
        label139: this.mPreferences.setLocalId(this.mActivity, this.mCameraId);
        CameraSettings.upgradeLocalPreferences(this.mPreferences.getLocal());
        resetExposureCompensation();
        this.mStartPreviewPrerequisiteReady.open();
        initializeControlByIntent();
        this.mQuickCapture = this.mActivity.getIntent().getBooleanExtra("android.intent.extra.quickCapture", false);
        initializeMiscControls();
        this.mLocationManager = new LocationManager(this.mActivity, this);
        initOnScreenIndicator();
        return;
      }
    }
    CameraActivity localCameraActivity1 = this.mActivity;
    if (!this.mIsImageCaptureIntent);
    while (true)
    {
      localCameraActivity1.createCameraScreenNail(bool1);
      break label139:
      bool1 = false;
    }
  }

  public void installIntentFilter()
  {
  }

  public boolean needsSwitcher()
  {
    return !this.mIsImageCaptureIntent;
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    switch (paramInt1)
    {
    default:
      return;
    case 1000:
    }
    Intent localIntent = new Intent();
    if (paramIntent != null)
    {
      Bundle localBundle = paramIntent.getExtras();
      if (localBundle != null)
        localIntent.putExtras(localBundle);
    }
    this.mActivity.setResultEx(paramInt2, localIntent);
    this.mActivity.finish();
    this.mActivity.getFileStreamPath("crop-temp").delete();
  }

  public boolean onBackPressed()
  {
    if ((this.mPieRenderer != null) && (this.mPieRenderer.showsItems()))
      this.mPieRenderer.hide();
    do
    {
      do
      {
        return true;
        if (!this.mIsImageCaptureIntent)
          break label46;
      }
      while (removeTopLevelPopup());
      doCancel();
      label46: return true;
    }
    while (!isCameraIdle());
    return removeTopLevelPopup();
  }

  public void onCameraPickerClicked(int paramInt)
  {
    if ((this.mPaused) || (this.mPendingSwitchCameraId != -1))
      return;
    this.mPendingSwitchCameraId = paramInt;
    if (ApiHelper.HAS_SURFACE_TEXTURE)
    {
      Log.v("CAM_PhotoModule", "Start to copy texture. cameraId=" + paramInt);
      ((CameraScreenNail)this.mActivity.mCameraScreenNail).copyTexture();
      setCameraState(4);
      return;
    }
    switchCamera();
  }

  public void onCaptureTextureCopied()
  {
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    Log.v("CAM_PhotoModule", "onConfigurationChanged");
    setDisplayOrientation();
    ((ViewGroup)this.mRootView).removeAllViews();
    this.mActivity.getLayoutInflater().inflate(2130968624, (ViewGroup)this.mRootView);
    initializeControlByIntent();
    initializeFocusManager();
    initializeMiscControls();
    loadCameraPreferences();
    this.mShutterButton = this.mActivity.getShutterButton();
    this.mShutterButton.setOnShutterButtonListener(this);
    initializeZoom();
    initOnScreenIndicator();
    updateOnScreenIndicators();
    FaceView localFaceView;
    if (this.mFaceView != null)
    {
      this.mFaceView.clear();
      this.mFaceView.setVisibility(0);
      this.mFaceView.setDisplayOrientation(this.mDisplayOrientation);
      Camera.CameraInfo localCameraInfo = CameraHolder.instance().getCameraInfo()[this.mCameraId];
      localFaceView = this.mFaceView;
      if (localCameraInfo.facing != 1)
        break label208;
    }
    for (boolean bool = true; ; bool = false)
    {
      localFaceView.setMirror(bool);
      this.mFaceView.resume();
      this.mFocusManager.setFaceView(this.mFaceView);
      initializeRenderOverlay();
      onFullScreenChanged(this.mActivity.isInCameraApp());
      if (this.mJpegImageData != null)
        showPostCaptureAlert();
      label208: return;
    }
  }

  public void onFullScreenChanged(boolean paramBoolean)
  {
    boolean bool1 = true;
    boolean bool2;
    if (this.mFaceView != null)
    {
      FaceView localFaceView = this.mFaceView;
      if (paramBoolean)
        break label168;
      bool2 = bool1;
      label22: localFaceView.setBlockDraw(bool2);
    }
    if (this.mPopup != null)
      dismissPopup(false, paramBoolean);
    if (this.mGestures != null)
      this.mGestures.setEnabled(paramBoolean);
    int j;
    if (this.mRenderOverlay != null)
    {
      RenderOverlay localRenderOverlay = this.mRenderOverlay;
      if (!paramBoolean)
        break label174;
      j = 0;
      label77: localRenderOverlay.setVisibility(j);
    }
    if (this.mPieRenderer != null)
    {
      PieRenderer localPieRenderer = this.mPieRenderer;
      if (paramBoolean)
        break label181;
      label101: localPieRenderer.setBlockFocus(bool1);
    }
    setShowMenu(paramBoolean);
    View localView;
    int i;
    if (this.mBlocker != null)
    {
      localView = this.mBlocker;
      i = 0;
      if (!paramBoolean)
        break label186;
    }
    while (true)
    {
      localView.setVisibility(i);
      if (!ApiHelper.HAS_SURFACE_TEXTURE)
        break;
      if (this.mActivity.mCameraScreenNail != null)
        ((CameraScreenNail)this.mActivity.mCameraScreenNail).setFullScreen(paramBoolean);
      return;
      label168: bool2 = false;
      break label22:
      label174: j = 8;
      break label77:
      label181: bool1 = false;
      break label101:
      label186: i = 8;
    }
    if (paramBoolean)
    {
      this.mPreviewSurfaceView.expand();
      return;
    }
    this.mPreviewSurfaceView.shrink();
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool = true;
    switch (paramInt)
    {
    default:
      bool = false;
    case 80:
    case 27:
    case 23:
    }
    do
    {
      do
      {
        do
          return bool;
        while ((!this.mFirstTimeInitialized) || (paramKeyEvent.getRepeatCount() != 0));
        onShutterButtonFocus(bool);
        return bool;
      }
      while ((!this.mFirstTimeInitialized) || (paramKeyEvent.getRepeatCount() != 0));
      onShutterButtonClick();
      return bool;
    }
    while ((!this.mFirstTimeInitialized) || (paramKeyEvent.getRepeatCount() != 0) || (removeTopLevelPopup()));
    onShutterButtonFocus(bool);
    if (this.mShutterButton.isInTouchMode())
      this.mShutterButton.requestFocusFromTouch();
    while (true)
    {
      this.mShutterButton.setPressed(bool);
      return bool;
      this.mShutterButton.requestFocus();
    }
  }

  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    switch (paramInt)
    {
    default:
      return false;
    case 80:
    }
    if (this.mFirstTimeInitialized)
      onShutterButtonFocus(false);
    return true;
  }

  public void onOrientationChanged(int paramInt)
  {
    if (paramInt == -1);
    do
    {
      return;
      this.mOrientation = Util.roundOrientation(paramInt, this.mOrientation);
      int i = (this.mOrientation + Util.getDisplayRotation(this.mActivity)) % 360;
      if (this.mOrientationCompensation == i)
        continue;
      this.mOrientationCompensation = i;
      if (this.mFaceView != null)
        this.mFaceView.setOrientation(this.mOrientationCompensation, true);
      setDisplayOrientation();
    }
    while (!this.mHandler.hasMessages(6));
    this.mHandler.removeMessages(6);
    showTapToFocusToast();
  }

  public void onPauseAfterSuper()
  {
    waitCameraStartUpThread();
    if ((this.mCameraDevice != null) && (this.mActivity.isSecureCamera()) && (ActivityBase.isFirstStartAfterScreenOn()))
    {
      ActivityBase.resetFirstStartAfterScreenOn();
      CameraHolder.instance().keep(1000);
    }
    if ((this.mCameraDevice != null) && (this.mCameraState != 0))
      this.mCameraDevice.cancelAutoFocus();
    stopPreview();
    closeCamera();
    if (this.mSurfaceTexture != null)
    {
      ((CameraScreenNail)this.mActivity.mCameraScreenNail).releaseSurfaceTexture();
      this.mSurfaceTexture = null;
    }
    resetScreenOn();
    collapseCameraControls();
    if (this.mFaceView != null)
      this.mFaceView.clear();
    if ((this.mFirstTimeInitialized) && (this.mImageSaver != null))
    {
      this.mImageSaver.finish();
      this.mImageSaver = null;
      this.mImageNamer.finish();
      this.mImageNamer = null;
    }
    if (this.mLocationManager != null)
      this.mLocationManager.recordLocation(false);
    this.mJpegImageData = null;
    this.mHandler.removeMessages(1);
    this.mHandler.removeMessages(2);
    this.mHandler.removeMessages(5);
    this.mHandler.removeMessages(7);
    this.mHandler.removeMessages(8);
    this.mHandler.removeMessages(9);
    this.mHandler.removeMessages(10);
    this.mHandler.removeMessages(11);
    this.mHandler.removeMessages(12);
    this.mPendingSwitchCameraId = -1;
    if (this.mFocusManager == null)
      return;
    this.mFocusManager.removeMessages();
  }

  public void onPauseBeforeSuper()
  {
    this.mPaused = true;
  }

  public void onPieClosed()
  {
    this.mActivity.setSwipingEnabled(true);
    if (this.mFaceView == null)
      return;
    this.mFaceView.setBlockDraw(false);
  }

  public void onPieOpened(int paramInt1, int paramInt2)
  {
    this.mActivity.cancelActivityTouchHandling();
    this.mActivity.setSwipingEnabled(false);
    if (this.mFaceView == null)
      return;
    this.mFaceView.setBlockDraw(true);
  }

  public void onPreviewTextureCopied()
  {
    this.mHandler.sendEmptyMessage(7);
  }

  public void onResumeAfterSuper()
  {
    if ((this.mOpenCameraFail) || (this.mCameraDisabled))
      return;
    this.mJpegPictureCallbackTime = 0L;
    this.mZoomValue = 0;
    if ((this.mCameraState == 0) && (this.mCameraStartUpThread == null))
    {
      resetExposureCompensation();
      this.mCameraStartUpThread = new CameraStartUpThread(null);
      this.mCameraStartUpThread.start();
    }
    if (!this.mFirstTimeInitialized)
      this.mHandler.sendEmptyMessage(2);
    while (true)
    {
      keepScreenOnAwhile();
      PopupManager.getInstance(this.mActivity).notifyShowPopup(null);
      return;
      initializeSecondTime();
    }
  }

  public void onResumeBeforeSuper()
  {
    this.mPaused = false;
  }

  public void onReviewCancelClicked(View paramView)
  {
    doCancel();
  }

  public void onReviewDoneClicked(View paramView)
  {
    doAttach();
  }

  public void onReviewRetakeClicked(View paramView)
  {
    if (this.mPaused)
      return;
    hidePostCaptureAlert();
    setupPreview();
  }

  public void onSharedPreferenceChanged()
  {
    if (this.mPaused)
      return;
    boolean bool = RecordLocationPreference.get(this.mPreferences, this.mContentResolver);
    this.mLocationManager.recordLocation(bool);
    setCameraParametersWhenIdle(4);
    setPreviewFrameLayoutAspectRatio();
    updateOnScreenIndicators();
  }

  public void onShowSwitcherPopup()
  {
    if ((this.mPieRenderer == null) || (!this.mPieRenderer.showsItems()))
      return;
    this.mPieRenderer.hide();
  }

  public void onShutterButtonClick()
  {
    if ((this.mPaused) || (collapseCameraControls()) || (this.mCameraState == 4) || (this.mCameraState == 0))
      return;
    if (this.mActivity.getStorageSpace() <= 50000000L)
    {
      Log.i("CAM_PhotoModule", "Not enough space or storage not ready. remaining=" + this.mActivity.getStorageSpace());
      return;
    }
    Log.v("CAM_PhotoModule", "onShutterButtonClick: mCameraState=" + this.mCameraState);
    if ((((this.mFocusManager.isFocusingSnapOnFinish()) || (this.mCameraState == 3))) && (!this.mIsImageCaptureIntent))
    {
      this.mSnapshotOnIdle = true;
      return;
    }
    this.mSnapshotOnIdle = false;
    this.mFocusManager.doSnap();
  }

  public void onShutterButtonFocus(boolean paramBoolean)
  {
    if ((this.mPaused) || (collapseCameraControls()) || (this.mCameraState == 3) || (this.mCameraState == 0));
    do
      return;
    while ((paramBoolean) && (!canTakePicture()));
    if (paramBoolean)
    {
      this.mFocusManager.onShutterDown();
      return;
    }
    this.mFocusManager.onShutterUp();
  }

  public void onSingleTapUp(View paramView, int paramInt1, int paramInt2)
  {
    if ((this.mPaused) || (this.mCameraDevice == null) || (!this.mFirstTimeInitialized) || (this.mCameraState == 3) || (this.mCameraState == 4) || (this.mCameraState == 0));
    do
      return;
    while ((removeTopLevelPopup()) || ((!this.mFocusAreaSupported) && (!this.mMeteringAreaSupported)));
    this.mFocusManager.onSingleTapUp(paramInt1, paramInt2);
  }

  public void onSizeChanged(int paramInt1, int paramInt2)
  {
    if (this.mFocusManager == null)
      return;
    this.mFocusManager.setPreviewSize(paramInt1, paramInt2);
  }

  public void onStop()
  {
    if (this.mMediaProviderClient == null)
      return;
    this.mMediaProviderClient.release();
    this.mMediaProviderClient = null;
  }

  public void onUserInteraction()
  {
    if (this.mActivity.isFinishing())
      return;
    keepScreenOnAwhile();
  }

  public boolean removeTopLevelPopup()
  {
    if (this.mPopup != null)
    {
      dismissPopup(true);
      return true;
    }
    return false;
  }

  public void setFocusParameters()
  {
    setCameraParameters(4);
  }

  void setPreviewFrameLayoutAspectRatio()
  {
    Camera.Size localSize = this.mParameters.getPictureSize();
    this.mPreviewFrameLayout.setAspectRatio(localSize.width / localSize.height);
  }

  public void showGpsOnScreenIndicator(boolean paramBoolean)
  {
  }

  public void showPopup(AbstractSettingPopup paramAbstractSettingPopup)
  {
    this.mActivity.hideUI();
    this.mBlocker.setVisibility(4);
    setShowMenu(false);
    this.mPopup = paramAbstractSettingPopup;
    this.mPopup.setOrientation(this.mOrientationCompensation, false);
    this.mPopup.setVisibility(0);
    FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(-2, -2);
    localLayoutParams.gravity = 17;
    ((FrameLayout)this.mRootView).addView(this.mPopup, localLayoutParams);
  }

  @TargetApi(14)
  public void startFaceDetection()
  {
    int i = 1;
    if (!ApiHelper.HAS_FACE_DETECTION);
    do
      return;
    while ((this.mFaceDetectionStarted) || (this.mParameters.getMaxNumDetectedFaces() <= 0));
    this.mFaceDetectionStarted = i;
    this.mFaceView.clear();
    this.mFaceView.setVisibility(0);
    this.mFaceView.setDisplayOrientation(this.mDisplayOrientation);
    Camera.CameraInfo localCameraInfo = CameraHolder.instance().getCameraInfo()[this.mCameraId];
    FaceView localFaceView = this.mFaceView;
    if (localCameraInfo.facing == i);
    while (true)
    {
      localFaceView.setMirror(i);
      this.mFaceView.resume();
      this.mFocusManager.setFaceView(this.mFaceView);
      this.mCameraDevice.setFaceDetectionListener(new Camera.FaceDetectionListener()
      {
        public void onFaceDetection(Camera.Face[] paramArrayOfFace, Camera paramCamera)
        {
          PhotoModule.this.mFaceView.setFaces(paramArrayOfFace);
        }
      });
      this.mCameraDevice.startFaceDetection();
      return;
      int j = 0;
    }
  }

  @TargetApi(14)
  public void stopFaceDetection()
  {
    if (!ApiHelper.HAS_FACE_DETECTION);
    do
    {
      do
        return;
      while ((!this.mFaceDetectionStarted) || (this.mParameters.getMaxNumDetectedFaces() <= 0));
      this.mFaceDetectionStarted = false;
      this.mCameraDevice.setFaceDetectionListener(null);
      this.mCameraDevice.stopFaceDetection();
    }
    while (this.mFaceView == null);
    this.mFaceView.clear();
  }

  public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3)
  {
    Log.v("CAM_PhotoModule", "surfaceChanged:" + paramSurfaceHolder + " width=" + paramInt2 + ". height=" + paramInt3);
  }

  public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
  {
    Log.v("CAM_PhotoModule", "surfaceCreated: " + paramSurfaceHolder);
    this.mCameraSurfaceHolder = paramSurfaceHolder;
    if ((this.mCameraDevice == null) || (this.mCameraStartUpThread != null));
    do
    {
      return;
      this.mCameraDevice.setPreviewDisplayAsync(paramSurfaceHolder);
    }
    while (this.mCameraState != 0);
    setupPreview();
  }

  public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
  {
    Log.v("CAM_PhotoModule", "surfaceDestroyed: " + paramSurfaceHolder);
    this.mCameraSurfaceHolder = null;
    stopPreview();
  }

  public void updateCameraAppView()
  {
  }

  public boolean updateStorageHintOnResume()
  {
    return this.mFirstTimeInitialized;
  }

  void waitCameraStartUpThread()
  {
    try
    {
      if (this.mCameraStartUpThread != null)
      {
        this.mCameraStartUpThread.cancel();
        this.mCameraStartUpThread.join();
        this.mCameraStartUpThread = null;
        setCameraState(1);
      }
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
    }
  }

  private final class AutoFocusCallback
    implements Camera.AutoFocusCallback
  {
    private AutoFocusCallback()
    {
    }

    public void onAutoFocus(boolean paramBoolean, Camera paramCamera)
    {
      if (PhotoModule.this.mPaused)
        return;
      PhotoModule.this.mAutoFocusTime = (System.currentTimeMillis() - PhotoModule.this.mFocusStartTime);
      Log.v("CAM_PhotoModule", "mAutoFocusTime = " + PhotoModule.this.mAutoFocusTime + "ms");
      PhotoModule.this.setCameraState(1);
      PhotoModule.this.mFocusManager.onAutoFocus(paramBoolean);
    }
  }

  @TargetApi(16)
  private final class AutoFocusMoveCallback
    implements Camera.AutoFocusMoveCallback
  {
    private AutoFocusMoveCallback()
    {
    }

    public void onAutoFocusMoving(boolean paramBoolean, Camera paramCamera)
    {
      PhotoModule.this.mFocusManager.onAutoFocusMoving(paramBoolean);
    }
  }

  private class CameraStartUpThread extends Thread
  {
    private volatile boolean mCancelled;

    private CameraStartUpThread()
    {
    }

    public void cancel()
    {
      this.mCancelled = true;
    }

    public void run()
    {
      try
      {
        if (this.mCancelled)
          return;
        PhotoModule.access$602(PhotoModule.this, Util.openCamera(PhotoModule.this.mActivity, PhotoModule.this.mCameraId));
        PhotoModule.access$902(PhotoModule.this, PhotoModule.this.mCameraDevice.getParameters());
        PhotoModule.this.mStartPreviewPrerequisiteReady.block();
        PhotoModule.this.initializeCapabilities();
        if (PhotoModule.this.mFocusManager == null)
          PhotoModule.this.initializeFocusManager();
        if (this.mCancelled)
          return;
        PhotoModule.this.setCameraParameters(-1);
        PhotoModule.this.mHandler.sendEmptyMessage(9);
        if (this.mCancelled)
          return;
        PhotoModule.this.startPreview();
        PhotoModule.this.mHandler.sendEmptyMessage(10);
        PhotoModule.access$1602(PhotoModule.this, SystemClock.uptimeMillis());
        PhotoModule.this.mHandler.sendEmptyMessage(5);
        return;
      }
      catch (CameraHardwareException localCameraHardwareException)
      {
        PhotoModule.this.mHandler.sendEmptyMessage(11);
        return;
      }
      catch (CameraDisabledException localCameraDisabledException)
      {
        PhotoModule.this.mHandler.sendEmptyMessage(12);
      }
    }
  }

  private static class ImageNamer extends Thread
  {
    private long mDateTaken;
    private int mHeight;
    private boolean mRequestPending;
    private ContentResolver mResolver;
    private boolean mStop;
    private String mTitle;
    private Uri mUri;
    private int mWidth;

    public ImageNamer()
    {
      start();
    }

    private void cleanOldUri()
    {
      if (this.mUri == null)
        return;
      Storage.deleteImage(this.mResolver, this.mUri);
      this.mUri = null;
    }

    private void generateUri()
    {
      this.mTitle = Util.createJpegName(this.mDateTaken);
      this.mUri = Storage.newImage(this.mResolver, this.mTitle, this.mDateTaken, this.mWidth, this.mHeight);
    }

    public void finish()
    {
      monitorenter;
      try
      {
        this.mStop = true;
        super.notifyAll();
        monitorexit;
        return;
      }
      finally
      {
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }

    public String getTitle()
    {
      monitorenter;
      try
      {
        String str = this.mTitle;
        monitorexit;
        return str;
      }
      finally
      {
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }

    public Uri getUri()
    {
      monitorenter;
      try
      {
        while (true)
        {
          boolean bool = this.mRequestPending;
          if (!bool)
            break;
          try
          {
            super.wait();
          }
          catch (InterruptedException localInterruptedException)
          {
          }
        }
        Uri localUri = this.mUri;
        this.mUri = null;
        return localUri;
      }
      finally
      {
        monitorexit;
      }
    }

    public void prepareUri(ContentResolver paramContentResolver, long paramLong, int paramInt1, int paramInt2, int paramInt3)
    {
      monitorenter;
      if (paramInt3 % 180 != 0)
      {
        int i = paramInt1;
        paramInt1 = paramInt2;
        paramInt2 = i;
      }
      try
      {
        this.mRequestPending = true;
        this.mResolver = paramContentResolver;
        this.mDateTaken = paramLong;
        this.mWidth = paramInt1;
        this.mHeight = paramInt2;
        super.notifyAll();
        monitorexit;
        return;
      }
      finally
      {
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }

    public void run()
    {
      monitorenter;
      while (true)
        try
        {
          while (true)
          {
            if (this.mStop)
            {
              cleanOldUri();
              return;
            }
            boolean bool = this.mRequestPending;
            if (bool)
              break;
            try
            {
              super.wait();
            }
            catch (InterruptedException localInterruptedException)
            {
            }
          }
          cleanOldUri();
          generateUri();
          this.mRequestPending = false;
        }
        finally
        {
          monitorexit;
        }
    }
  }

  private class ImageSaver extends Thread
  {
    private ArrayList<PhotoModule.SaveRequest> mQueue = new ArrayList();
    private boolean mStop;

    public ImageSaver()
    {
      start();
    }

    private void storeImage(byte[] paramArrayOfByte, Uri paramUri, String paramString, Location paramLocation, int paramInt1, int paramInt2, int paramInt3)
    {
      if (!Storage.updateImage(PhotoModule.this.mContentResolver, paramUri, paramString, paramLocation, paramInt3, paramArrayOfByte, paramInt1, paramInt2))
        return;
      Util.broadcastNewPicture(PhotoModule.this.mActivity, paramUri);
    }

    public void addImage(byte[] paramArrayOfByte, Uri paramUri, String paramString, Location paramLocation, int paramInt1, int paramInt2, int paramInt3)
    {
      PhotoModule.SaveRequest localSaveRequest = new PhotoModule.SaveRequest(null);
      localSaveRequest.data = paramArrayOfByte;
      localSaveRequest.uri = paramUri;
      localSaveRequest.title = paramString;
      Location localLocation = null;
      if (paramLocation == null)
      {
        label36: localSaveRequest.loc = localLocation;
        localSaveRequest.width = paramInt1;
        localSaveRequest.height = paramInt2;
        localSaveRequest.orientation = paramInt3;
        monitorenter;
      }
      try
      {
        while (true)
        {
          int i = this.mQueue.size();
          if (i < 3)
            break label107;
          try
          {
            super.wait();
          }
          catch (InterruptedException localInterruptedException)
          {
          }
        }
        localLocation = new Location(paramLocation);
        break label36:
        label107: this.mQueue.add(localSaveRequest);
        super.notifyAll();
        return;
      }
      finally
      {
        monitorexit;
      }
    }

    // ERROR //
    public void finish()
    {
      // Byte code:
      //   0: aload_0
      //   1: invokevirtual 109	com/android/camera/PhotoModule$ImageSaver:waitDone	()V
      //   4: aload_0
      //   5: monitorenter
      //   6: aload_0
      //   7: iconst_1
      //   8: putfield 111	com/android/camera/PhotoModule$ImageSaver:mStop	Z
      //   11: aload_0
      //   12: invokevirtual 105	java/lang/Object:notifyAll	()V
      //   15: aload_0
      //   16: monitorexit
      //   17: aload_0
      //   18: invokevirtual 114	com/android/camera/PhotoModule$ImageSaver:join	()V
      //   21: return
      //   22: astore_1
      //   23: aload_0
      //   24: monitorexit
      //   25: aload_1
      //   26: athrow
      //   27: astore_2
      //   28: return
      //
      // Exception table:
      //   from	to	target	type
      //   6	17	22	finally
      //   23	25	22	finally
      //   17	21	27	java/lang/InterruptedException
    }

    // ERROR //
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 23	com/android/camera/PhotoModule$ImageSaver:mQueue	Ljava/util/ArrayList;
      //   6: invokevirtual 119	java/util/ArrayList:isEmpty	()Z
      //   9: ifeq +31 -> 40
      //   12: aload_0
      //   13: invokevirtual 105	java/lang/Object:notifyAll	()V
      //   16: aload_0
      //   17: getfield 111	com/android/camera/PhotoModule$ImageSaver:mStop	Z
      //   20: ifeq +6 -> 26
      //   23: aload_0
      //   24: monitorexit
      //   25: return
      //   26: aload_0
      //   27: invokevirtual 93	java/lang/Object:wait	()V
      //   30: aload_0
      //   31: monitorexit
      //   32: goto -32 -> 0
      //   35: astore_1
      //   36: aload_0
      //   37: monitorexit
      //   38: aload_1
      //   39: athrow
      //   40: aload_0
      //   41: getfield 23	com/android/camera/PhotoModule$ImageSaver:mQueue	Ljava/util/ArrayList;
      //   44: iconst_0
      //   45: invokevirtual 123	java/util/ArrayList:get	(I)Ljava/lang/Object;
      //   48: checkcast 55	com/android/camera/PhotoModule$SaveRequest
      //   51: astore_2
      //   52: aload_0
      //   53: monitorexit
      //   54: aload_0
      //   55: aload_2
      //   56: getfield 62	com/android/camera/PhotoModule$SaveRequest:data	[B
      //   59: aload_2
      //   60: getfield 66	com/android/camera/PhotoModule$SaveRequest:uri	Landroid/net/Uri;
      //   63: aload_2
      //   64: getfield 70	com/android/camera/PhotoModule$SaveRequest:title	Ljava/lang/String;
      //   67: aload_2
      //   68: getfield 74	com/android/camera/PhotoModule$SaveRequest:loc	Landroid/location/Location;
      //   71: aload_2
      //   72: getfield 78	com/android/camera/PhotoModule$SaveRequest:width	I
      //   75: aload_2
      //   76: getfield 81	com/android/camera/PhotoModule$SaveRequest:height	I
      //   79: aload_2
      //   80: getfield 84	com/android/camera/PhotoModule$SaveRequest:orientation	I
      //   83: invokespecial 125	com/android/camera/PhotoModule$ImageSaver:storeImage	([BLandroid/net/Uri;Ljava/lang/String;Landroid/location/Location;III)V
      //   86: aload_0
      //   87: monitorenter
      //   88: aload_0
      //   89: getfield 23	com/android/camera/PhotoModule$ImageSaver:mQueue	Ljava/util/ArrayList;
      //   92: iconst_0
      //   93: invokevirtual 128	java/util/ArrayList:remove	(I)Ljava/lang/Object;
      //   96: pop
      //   97: aload_0
      //   98: invokevirtual 105	java/lang/Object:notifyAll	()V
      //   101: aload_0
      //   102: monitorexit
      //   103: goto -103 -> 0
      //   106: astore_3
      //   107: aload_0
      //   108: monitorexit
      //   109: aload_3
      //   110: athrow
      //   111: astore 5
      //   113: goto -83 -> 30
      //
      // Exception table:
      //   from	to	target	type
      //   2	25	35	finally
      //   26	30	35	finally
      //   30	32	35	finally
      //   36	38	35	finally
      //   40	54	35	finally
      //   88	103	106	finally
      //   107	109	106	finally
      //   26	30	111	java/lang/InterruptedException
    }

    public void waitDone()
    {
      monitorenter;
      try
      {
        while (true)
        {
          boolean bool = this.mQueue.isEmpty();
          if (bool)
            break;
          try
          {
            super.wait();
          }
          catch (InterruptedException localInterruptedException)
          {
          }
        }
        return;
      }
      finally
      {
        monitorexit;
      }
    }
  }

  private final class JpegPictureCallback
    implements Camera.PictureCallback
  {
    Location mLocation;

    public JpegPictureCallback(Location arg2)
    {
      Object localObject;
      this.mLocation = localObject;
    }

    public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
    {
      if (PhotoModule.this.mPaused)
        return;
      if (PhotoModule.this.mSceneMode == "hdr")
        PhotoModule.this.mActivity.showSwitcher();
      PhotoModule.access$4302(PhotoModule.this, System.currentTimeMillis());
      label100: Camera.Size localSize;
      label214: int j;
      int k;
      if (PhotoModule.this.mPostViewPictureCallbackTime != 0L)
      {
        PhotoModule.this.mShutterToPictureDisplayedTime = (PhotoModule.this.mPostViewPictureCallbackTime - PhotoModule.this.mShutterCallbackTime);
        PhotoModule.this.mPictureDisplayedToJpegCallbackTime = (PhotoModule.this.mJpegPictureCallbackTime - PhotoModule.this.mPostViewPictureCallbackTime);
        Log.v("CAM_PhotoModule", "mPictureDisplayedToJpegCallbackTime = " + PhotoModule.this.mPictureDisplayedToJpegCallbackTime + "ms");
        if ((ApiHelper.HAS_SURFACE_TEXTURE) && (!PhotoModule.this.mIsImageCaptureIntent) && (PhotoModule.this.mActivity.mShowCameraAppView))
          ((CameraScreenNail)PhotoModule.this.mActivity.mCameraScreenNail).animateSlide();
        PhotoModule.this.mFocusManager.updateFocusUI();
        if (!PhotoModule.this.mIsImageCaptureIntent)
        {
          if (!ApiHelper.CAN_START_PREVIEW_IN_JPEG_CALLBACK)
            break label460;
          PhotoModule.this.setupPreview();
        }
        if (PhotoModule.this.mIsImageCaptureIntent)
          break label495;
        localSize = PhotoModule.this.mParameters.getPictureSize();
        int i = Exif.getOrientation(paramArrayOfByte);
        if ((i + PhotoModule.this.mJpegRotation) % 180 != 0)
          break label478;
        j = localSize.width;
        k = localSize.height;
        label273: Uri localUri = PhotoModule.this.mImageNamer.getUri();
        PhotoModule.this.mActivity.addSecureAlbumItemIfNeeded(false, localUri);
        String str = PhotoModule.this.mImageNamer.getTitle();
        PhotoModule.this.mImageSaver.addImage(paramArrayOfByte, localUri, str, this.mLocation, j, k, i);
      }
      while (true)
      {
        PhotoModule.this.mActivity.updateStorageSpaceAndHint();
        long l = System.currentTimeMillis();
        PhotoModule.this.mJpegCallbackFinishTime = (l - PhotoModule.this.mJpegPictureCallbackTime);
        Log.v("CAM_PhotoModule", "mJpegCallbackFinishTime = " + PhotoModule.this.mJpegCallbackFinishTime + "ms");
        PhotoModule.access$4302(PhotoModule.this, 0L);
        return;
        PhotoModule.this.mShutterToPictureDisplayedTime = (PhotoModule.this.mRawPictureCallbackTime - PhotoModule.this.mShutterCallbackTime);
        PhotoModule.this.mPictureDisplayedToJpegCallbackTime = (PhotoModule.this.mJpegPictureCallbackTime - PhotoModule.this.mRawPictureCallbackTime);
        break label100:
        label460: PhotoModule.this.mHandler.sendEmptyMessageDelayed(1, 300L);
        break label214:
        label478: j = localSize.height;
        k = localSize.width;
        break label273:
        label495: PhotoModule.access$4802(PhotoModule.this, paramArrayOfByte);
        if (!PhotoModule.this.mQuickCapture)
          PhotoModule.this.showPostCaptureAlert();
        PhotoModule.this.doAttach();
      }
    }
  }

  private class MainHandler extends Handler
  {
    private MainHandler()
    {
    }

    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default:
      case 1:
      case 3:
      case 2:
      case 4:
      case 5:
        do
        {
          return;
          PhotoModule.this.setupPreview();
          return;
          PhotoModule.this.mActivity.getWindow().clearFlags(128);
          return;
          PhotoModule.this.initializeFirstTime();
          return;
          PhotoModule.this.setCameraParametersWhenIdle(0);
          return;
          if (Util.getDisplayRotation(PhotoModule.this.mActivity) == PhotoModule.this.mDisplayRotation)
            continue;
          PhotoModule.this.setDisplayOrientation();
        }
        while (SystemClock.uptimeMillis() - PhotoModule.this.mOnResumeTime >= 5000L);
        PhotoModule.this.mHandler.sendEmptyMessageDelayed(5, 100L);
        return;
      case 6:
        PhotoModule.this.showTapToFocusToast();
        return;
      case 7:
        PhotoModule.this.switchCamera();
        return;
      case 8:
        ((CameraScreenNail)PhotoModule.this.mActivity.mCameraScreenNail).animateSwitchCamera();
        return;
      case 9:
        PhotoModule.this.initializeAfterCameraOpen();
        return;
      case 10:
        PhotoModule.this.mCameraStartUpThread = null;
        PhotoModule.this.setCameraState(1);
        if (!ApiHelper.HAS_SURFACE_TEXTURE)
          PhotoModule.this.mCameraDevice.setPreviewDisplayAsync(PhotoModule.this.mCameraSurfaceHolder);
        PhotoModule.this.startFaceDetection();
        PhotoModule.this.locationFirstRun();
        return;
      case 11:
        PhotoModule.this.mCameraStartUpThread = null;
        PhotoModule.access$2802(PhotoModule.this, true);
        Util.showErrorAndFinish(PhotoModule.this.mActivity, 2131361890);
        return;
      case 12:
      }
      PhotoModule.this.mCameraStartUpThread = null;
      PhotoModule.access$2902(PhotoModule.this, true);
      Util.showErrorAndFinish(PhotoModule.this.mActivity, 2131361891);
    }
  }

  private final class PostViewPictureCallback
    implements Camera.PictureCallback
  {
    private PostViewPictureCallback()
    {
    }

    public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
    {
      PhotoModule.access$4002(PhotoModule.this, System.currentTimeMillis());
      Log.v("CAM_PhotoModule", "mShutterToPostViewCallbackTime = " + (PhotoModule.this.mPostViewPictureCallbackTime - PhotoModule.this.mShutterCallbackTime) + "ms");
    }
  }

  private final class RawPictureCallback
    implements Camera.PictureCallback
  {
    private RawPictureCallback()
    {
    }

    public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
    {
      PhotoModule.access$4102(PhotoModule.this, System.currentTimeMillis());
      Log.v("CAM_PhotoModule", "mShutterToRawCallbackTime = " + (PhotoModule.this.mRawPictureCallbackTime - PhotoModule.this.mShutterCallbackTime) + "ms");
    }
  }

  private static class SaveRequest
  {
    byte[] data;
    int height;
    Location loc;
    int orientation;
    String title;
    Uri uri;
    int width;
  }

  private final class ShutterCallback
    implements Camera.ShutterCallback
  {
    private ShutterCallback()
    {
    }

    public void onShutter()
    {
      PhotoModule.access$3902(PhotoModule.this, System.currentTimeMillis());
      PhotoModule.this.mShutterLag = (PhotoModule.this.mShutterCallbackTime - PhotoModule.this.mCaptureStartTime);
      Log.v("CAM_PhotoModule", "mShutterLag = " + PhotoModule.this.mShutterLag + "ms");
    }
  }

  private class ZoomChangeListener
    implements ZoomRenderer.OnZoomChangedListener
  {
    private ZoomChangeListener()
    {
    }

    public void onZoomEnd()
    {
      if (PhotoModule.this.mPieRenderer == null)
        return;
      PhotoModule.this.mPieRenderer.setBlockFocus(false);
    }

    public void onZoomStart()
    {
      if (PhotoModule.this.mPieRenderer == null)
        return;
      PhotoModule.this.mPieRenderer.setBlockFocus(true);
    }

    public void onZoomValueChanged(int paramInt)
    {
      if (PhotoModule.this.mPaused);
      do
      {
        do
        {
          return;
          PhotoModule.access$3302(PhotoModule.this, paramInt);
        }
        while ((PhotoModule.this.mParameters == null) || (PhotoModule.this.mCameraDevice == null));
        PhotoModule.this.mParameters.setZoom(PhotoModule.this.mZoomValue);
        PhotoModule.this.mCameraDevice.setParametersAsync(PhotoModule.this.mParameters);
      }
      while (PhotoModule.this.mZoomRenderer == null);
      Camera.Parameters localParameters = PhotoModule.this.mCameraDevice.getParameters();
      PhotoModule.this.mZoomRenderer.setZoomValue(((Integer)PhotoModule.this.mZoomRatios.get(localParameters.getZoom())).intValue());
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.PhotoModule
 * JD-Core Version:    0.5.4
 */