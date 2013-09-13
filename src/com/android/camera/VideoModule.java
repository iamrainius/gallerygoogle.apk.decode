package com.android.camera;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.location.Location;
import android.media.CamcorderProfile;
import android.media.CameraProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.MediaStore.Video.Media;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.camera.ui.AbstractSettingPopup;
import com.android.camera.ui.PieRenderer;
import com.android.camera.ui.PieRenderer.PieListener;
import com.android.camera.ui.PopupManager;
import com.android.camera.ui.PreviewSurfaceView;
import com.android.camera.ui.RenderOverlay;
import com.android.camera.ui.Rotatable;
import com.android.camera.ui.RotateImageView;
import com.android.camera.ui.RotateLayout;
import com.android.camera.ui.RotateTextToast;
import com.android.camera.ui.TwoStateImageView;
import com.android.camera.ui.ZoomRenderer;
import com.android.camera.ui.ZoomRenderer.OnZoomChangedListener;
import com.android.gallery3d.app.OrientationManager;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.ui.GLRoot;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class VideoModule
  implements MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener, CameraModule, CameraPreference.OnPreferenceChangedListener, EffectsRecorder.EffectsListener, ShutterButton.OnShutterButtonListener, PieRenderer.PieListener
{
  private CameraActivity mActivity;
  private View mBgLearningMessageFrame;
  private RotateLayout mBgLearningMessageRotater;
  private View mBlocker;
  private int mCameraDisplayOrientation;
  private int mCameraId;
  private boolean mCaptureTimeLapse = false;
  private ContentResolver mContentResolver;
  private String mCurrentVideoFilename;
  private Uri mCurrentVideoUri;
  private ContentValues mCurrentVideoValues;
  private int mDesiredPreviewHeight;
  private int mDesiredPreviewWidth;
  private int mDisplayRotation;
  private Object mEffectParameter = null;
  private int mEffectType = 0;
  private String mEffectUriFromGallery = null;
  private boolean mEffectsDisplayResult;
  private EffectsRecorder mEffectsRecorder;
  private final CameraErrorCallback mErrorCallback = new CameraErrorCallback();
  private ImageView mFlashIndicator;
  private CameraScreenNail.OnFrameDrawnListener mFrameDrawnListener;
  private PreviewGestures mGestures;
  private final Handler mHandler = new MainHandler(null);
  private boolean mIsVideoCaptureIntent;
  private LinearLayout mLabelsLinearLayout;
  private LocationManager mLocationManager;
  private int mMaxVideoDurationInMs;
  private MediaRecorder mMediaRecorder;
  private boolean mMediaRecorderRecording = false;
  private View mMenu;
  private long mOnResumeTime;
  private View mOnScreenIndicators;
  private int mOrientation = -1;
  private int mOrientationCompensation = 0;
  private int mOrientationCompensationAtRecordStart;
  private boolean mOrientationResetNeeded;
  private Camera.Parameters mParameters;
  private boolean mPaused;
  private int mPendingSwitchCameraId;
  private PieRenderer mPieRenderer;
  private AbstractSettingPopup mPopup;
  private String mPrefVideoEffectDefault;
  private PreferenceGroup mPreferenceGroup;
  private ComboPreferences mPreferences;
  private PreviewFrameLayout mPreviewFrameLayout;
  private PreviewSurfaceView mPreviewSurfaceView;
  boolean mPreviewing = false;
  private CamcorderProfile mProfile;
  private boolean mQuickCapture;
  private BroadcastReceiver mReceiver = null;
  private long mRecordingStartTime;
  private boolean mRecordingTimeCountsDown = false;
  private RotateLayout mRecordingTimeRect;
  private TextView mRecordingTimeView;
  private RenderOverlay mRenderOverlay;
  private boolean mResetEffect = true;
  private boolean mRestoreFlash;
  private Rotatable mReviewCancelButton;
  private View mReviewControl;
  private Rotatable mReviewDoneButton;
  private ImageView mReviewImage;
  private RotateImageView mReviewPlayButton;
  private View mRootView;
  private ShutterButton mShutterButton;
  private boolean mSnapshotInProgress = false;
  private SurfaceHolder.Callback mSurfaceViewCallback;
  private boolean mSurfaceViewReady;
  private boolean mSwitchingCamera;
  private int mTimeBetweenTimeLapseFrameCaptureMs = 0;
  private View mTimeLapseLabel;
  private VideoController mVideoControl;
  private ParcelFileDescriptor mVideoFileDescriptor;
  private String mVideoFilename;
  private VideoNamer mVideoNamer;
  private int mZoomMax;
  private List<Integer> mZoomRatios;
  private ZoomRenderer mZoomRenderer;
  private int mZoomValue;

  private boolean addVideoToMediaStore()
  {
    long l;
    if (this.mVideoFileDescriptor == null)
    {
      this.mCurrentVideoValues.put("_size", Long.valueOf(new File(this.mCurrentVideoFilename).length()));
      l = SystemClock.uptimeMillis() - this.mRecordingStartTime;
      if (l > 0L)
      {
        if (this.mCaptureTimeLapse)
          l = getTimeLapseVideoLength(l);
        this.mCurrentVideoValues.put("duration", Long.valueOf(l));
      }
    }
    while (true)
    {
      int i;
      try
      {
        this.mCurrentVideoUri = this.mVideoNamer.getUri();
        this.mActivity.addSecureAlbumItemIfNeeded(true, this.mCurrentVideoUri);
        String str = this.mCurrentVideoValues.getAsString("_data");
        if (new File(this.mCurrentVideoFilename).renameTo(new File(str)))
          this.mCurrentVideoFilename = str;
        this.mContentResolver.update(this.mCurrentVideoUri, this.mCurrentVideoValues, null, null);
        this.mActivity.sendBroadcast(new Intent("android.hardware.action.NEW_VIDEO", this.mCurrentVideoUri));
        Log.v("CAM_VideoModule", "Current video URI: " + this.mCurrentVideoUri);
        i = 0;
        return i;
        Log.w("CAM_VideoModule", "Video duration <= 0 : " + l);
      }
      catch (Exception localException)
      {
        Log.e("CAM_VideoModule", "failed to add video to media store", localException);
        this.mCurrentVideoUri = null;
        this.mCurrentVideoFilename = null;
        Log.v("CAM_VideoModule", "Current video URI: " + this.mCurrentVideoUri);
        i = 1;
      }
      finally
      {
        Log.v("CAM_VideoModule", "Current video URI: " + this.mCurrentVideoUri);
      }
    }
  }

  private void checkQualityAndStartPreview()
  {
    readVideoPreferences();
    showTimeLapseUI(this.mCaptureTimeLapse);
    Camera.Size localSize = this.mParameters.getPreviewSize();
    if ((localSize.width != this.mDesiredPreviewWidth) || (localSize.height != this.mDesiredPreviewHeight))
      resizeForPreviewAspectRatio();
    startPreview();
  }

  private void cleanupEmptyFile()
  {
    if (this.mVideoFilename == null)
      return;
    File localFile = new File(this.mVideoFilename);
    if ((localFile.length() != 0L) || (!localFile.delete()))
      return;
    Log.v("CAM_VideoModule", "Empty video file deleted: " + this.mVideoFilename);
    this.mVideoFilename = null;
  }

  private void clearVideoNamer()
  {
    if (this.mVideoNamer == null)
      return;
    this.mVideoNamer.finish();
    this.mVideoNamer = null;
  }

  private void closeCamera()
  {
    closeCamera(true);
  }

  private void closeCamera(boolean paramBoolean)
  {
    Log.v("CAM_VideoModule", "closeCamera");
    if (this.mActivity.mCameraDevice == null)
    {
      Log.d("CAM_VideoModule", "already stopped.");
      return;
    }
    if (this.mEffectsRecorder != null)
      this.mEffectsRecorder.disconnectCamera();
    if (paramBoolean)
      closeEffects();
    this.mActivity.mCameraDevice.setZoomChangeListener(null);
    this.mActivity.mCameraDevice.setErrorCallback(null);
    CameraHolder.instance().release();
    this.mActivity.mCameraDevice = null;
    this.mPreviewing = false;
    this.mSnapshotInProgress = false;
  }

  private void closeEffects()
  {
    Log.v("CAM_VideoModule", "Closing effects");
    this.mEffectType = 0;
    if (this.mEffectsRecorder == null)
    {
      Log.d("CAM_VideoModule", "Effects are already closed. Nothing to do");
      return;
    }
    this.mEffectsRecorder.release();
    this.mEffectsRecorder = null;
  }

  private void closeVideoFileDescriptor()
  {
    if (this.mVideoFileDescriptor != null);
    try
    {
      this.mVideoFileDescriptor.close();
      this.mVideoFileDescriptor = null;
      return;
    }
    catch (IOException localIOException)
    {
      Log.e("CAM_VideoModule", "Fail to close fd", localIOException);
    }
  }

  private String convertOutputFormatToFileExt(int paramInt)
  {
    if (paramInt == 2)
      return ".mp4";
    return ".3gp";
  }

  private String convertOutputFormatToMimeType(int paramInt)
  {
    if (paramInt == 2)
      return "video/mp4";
    return "video/3gpp";
  }

  private String createName(long paramLong)
  {
    Date localDate = new Date(paramLong);
    return new SimpleDateFormat(this.mActivity.getString(2131361964)).format(localDate);
  }

  private void deleteVideoFile(String paramString)
  {
    Log.v("CAM_VideoModule", "Deleting video " + paramString);
    if (new File(paramString).delete())
      return;
    Log.v("CAM_VideoModule", "Could not delete " + paramString);
  }

  private void doReturnToCaller(boolean paramBoolean)
  {
    Intent localIntent = new Intent();
    int i;
    if (paramBoolean)
    {
      i = -1;
      localIntent.setData(this.mCurrentVideoUri);
    }
    while (true)
    {
      this.mActivity.setResultEx(i, localIntent);
      this.mActivity.finish();
      return;
      i = 0;
    }
  }

  private boolean effectsActive()
  {
    return this.mEffectType != 0;
  }

  private void enableCameraControls(boolean paramBoolean)
  {
    PreviewGestures localPreviewGestures;
    if (this.mGestures != null)
    {
      localPreviewGestures = this.mGestures;
      if (paramBoolean)
        break label48;
    }
    for (boolean bool = true; ; bool = false)
    {
      localPreviewGestures.setZoomOnly(bool);
      if ((this.mPieRenderer != null) && (this.mPieRenderer.showsItems()))
        this.mPieRenderer.hide();
      label48: return;
    }
  }

  private PreferenceGroup filterPreferenceScreenByIntent(PreferenceGroup paramPreferenceGroup)
  {
    Intent localIntent = this.mActivity.getIntent();
    if (localIntent.hasExtra("android.intent.extra.videoQuality"))
      CameraSettings.removePreferenceFromScreen(paramPreferenceGroup, "pref_video_quality_key");
    if (localIntent.hasExtra("android.intent.extra.durationLimit"))
      CameraSettings.removePreferenceFromScreen(paramPreferenceGroup, "pref_video_quality_key");
    return paramPreferenceGroup;
  }

  private void generateVideoFilename(int paramInt)
  {
    long l = System.currentTimeMillis();
    String str1 = createName(l);
    String str2 = str1 + convertOutputFormatToFileExt(paramInt);
    String str3 = convertOutputFormatToMimeType(paramInt);
    String str4 = Storage.DIRECTORY + '/' + str2;
    String str5 = str4 + ".tmp";
    this.mCurrentVideoValues = new ContentValues(7);
    this.mCurrentVideoValues.put("title", str1);
    this.mCurrentVideoValues.put("_display_name", str2);
    this.mCurrentVideoValues.put("datetaken", Long.valueOf(l));
    this.mCurrentVideoValues.put("mime_type", str3);
    this.mCurrentVideoValues.put("_data", str4);
    this.mCurrentVideoValues.put("resolution", Integer.toString(this.mProfile.videoFrameWidth) + "x" + Integer.toString(this.mProfile.videoFrameHeight));
    Location localLocation = this.mLocationManager.getCurrentLocation();
    if (localLocation != null)
    {
      this.mCurrentVideoValues.put("latitude", Double.valueOf(localLocation.getLatitude()));
      this.mCurrentVideoValues.put("longitude", Double.valueOf(localLocation.getLongitude()));
    }
    this.mVideoNamer.prepareUri(this.mContentResolver, this.mCurrentVideoValues);
    this.mVideoFilename = str5;
    Log.v("CAM_VideoModule", "New video filename: " + this.mVideoFilename);
  }

  @TargetApi(11)
  private void getDesiredPreviewSize()
  {
    this.mParameters = this.mActivity.mCameraDevice.getParameters();
    if (ApiHelper.HAS_GET_SUPPORTED_VIDEO_SIZE)
      if ((this.mParameters.getSupportedVideoSizes() == null) || (effectsActive()))
        this.mDesiredPreviewWidth = this.mProfile.videoFrameWidth;
    for (this.mDesiredPreviewHeight = this.mProfile.videoFrameHeight; ; this.mDesiredPreviewHeight = this.mProfile.videoFrameHeight)
    {
      while (true)
      {
        Log.v("CAM_VideoModule", "mDesiredPreviewWidth=" + this.mDesiredPreviewWidth + ". mDesiredPreviewHeight=" + this.mDesiredPreviewHeight);
        return;
        List localList = this.mParameters.getSupportedPreviewSizes();
        Camera.Size localSize1 = this.mParameters.getPreferredPreviewSizeForVideo();
        int i = localSize1.width * localSize1.height;
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          Camera.Size localSize3 = (Camera.Size)localIterator.next();
          if (localSize3.width * localSize3.height <= i)
            continue;
          localIterator.remove();
        }
        Camera.Size localSize2 = Util.getOptimalPreviewSize(this.mActivity, localList, this.mProfile.videoFrameWidth / this.mProfile.videoFrameHeight);
        this.mDesiredPreviewWidth = localSize2.width;
        this.mDesiredPreviewHeight = localSize2.height;
      }
      this.mDesiredPreviewWidth = this.mProfile.videoFrameWidth;
    }
  }

  @TargetApi(11)
  private static int getLowVideoQuality()
  {
    if (ApiHelper.HAS_FINE_RESOLUTION_QUALITY_LEVELS)
      return 4;
    return 0;
  }

  private int getPreferredCameraId(ComboPreferences paramComboPreferences)
  {
    int i = Util.getCameraFacingIntentExtras(this.mActivity);
    if (i != -1)
      return i;
    return CameraSettings.readPreferredCameraId(paramComboPreferences);
  }

  private long getTimeLapseVideoLength(long paramLong)
  {
    return ()(1000.0D * (paramLong / this.mTimeBetweenTimeLapseFrameCaptureMs / this.mProfile.videoFrameRate));
  }

  private void initializeControlByIntent()
  {
    this.mBlocker = this.mRootView.findViewById(2131558542);
    this.mMenu = this.mRootView.findViewById(2131558543);
    this.mMenu.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        if (VideoModule.this.mPieRenderer == null)
          return;
        VideoModule.this.mPieRenderer.showInCenter();
      }
    });
    this.mOnScreenIndicators = this.mRootView.findViewById(2131558524);
    this.mFlashIndicator = ((ImageView)this.mRootView.findViewById(2131558526));
    if (!this.mIsVideoCaptureIntent)
      return;
    this.mActivity.hideSwitcher();
    this.mReviewDoneButton = ((Rotatable)this.mRootView.findViewById(2131558596));
    this.mReviewCancelButton = ((Rotatable)this.mRootView.findViewById(2131558598));
    this.mReviewPlayButton = ((RotateImageView)this.mRootView.findViewById(2131558590));
    ((View)this.mReviewCancelButton).setVisibility(0);
    ((View)this.mReviewDoneButton).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        VideoModule.this.onReviewDoneClicked(paramView);
      }
    });
    ((View)this.mReviewCancelButton).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        VideoModule.this.onReviewCancelClicked(paramView);
      }
    });
    this.mReviewPlayButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        VideoModule.this.onReviewPlayClicked(paramView);
      }
    });
    if (!this.mReviewDoneButton instanceof TwoStateImageView)
      return;
    ((TwoStateImageView)this.mReviewDoneButton).enableFilter(false);
  }

  private void initializeEffectsPreview()
  {
    Log.v("CAM_VideoModule", "initializeEffectsPreview");
    if (this.mActivity.mCameraDevice == null)
      return;
    if (this.mActivity.getResources().getConfiguration().orientation == 2);
    Camera.CameraInfo localCameraInfo = CameraHolder.instance().getCameraInfo()[this.mCameraId];
    this.mEffectsDisplayResult = false;
    this.mEffectsRecorder = new EffectsRecorder(this.mActivity);
    this.mEffectsRecorder.setCameraDisplayOrientation(this.mCameraDisplayOrientation);
    this.mEffectsRecorder.setCamera(this.mActivity.mCameraDevice);
    this.mEffectsRecorder.setCameraFacing(localCameraInfo.facing);
    this.mEffectsRecorder.setProfile(this.mProfile);
    this.mEffectsRecorder.setEffectsListener(this);
    this.mEffectsRecorder.setOnInfoListener(this);
    this.mEffectsRecorder.setOnErrorListener(this);
    int i = this.mOrientation;
    int j = 0;
    if (i != -1)
      j = this.mOrientation;
    this.mEffectsRecorder.setOrientationHint(j);
    this.mOrientationCompensationAtRecordStart = this.mOrientationCompensation;
    CameraScreenNail localCameraScreenNail = (CameraScreenNail)this.mActivity.mCameraScreenNail;
    this.mEffectsRecorder.setPreviewSurfaceTexture(localCameraScreenNail.getSurfaceTexture(), localCameraScreenNail.getWidth(), localCameraScreenNail.getHeight());
    if ((this.mEffectType == 2) && (((String)this.mEffectParameter).equals("gallery")))
    {
      this.mEffectsRecorder.setEffect(this.mEffectType, this.mEffectUriFromGallery);
      return;
    }
    this.mEffectsRecorder.setEffect(this.mEffectType, this.mEffectParameter);
  }

  private void initializeEffectsRecording()
  {
    Log.v("CAM_VideoModule", "initializeEffectsRecording");
    Bundle localBundle = this.mActivity.getIntent().getExtras();
    closeVideoFileDescriptor();
    Uri localUri;
    if ((this.mIsVideoCaptureIntent) && (localBundle != null))
    {
      localUri = (Uri)localBundle.getParcelable("output");
      if (localUri == null);
    }
    label75: long l1;
    label117: long l2;
    try
    {
      this.mVideoFileDescriptor = this.mContentResolver.openFileDescriptor(localUri, "rw");
      this.mCurrentVideoUri = localUri;
      l1 = localBundle.getLong("android.intent.extra.sizeLimit");
      this.mEffectsRecorder.setProfile(this.mProfile);
      if (!this.mCaptureTimeLapse)
        break label201;
      this.mEffectsRecorder.setCaptureRate(1000.0D / this.mTimeBetweenTimeLapseFrameCaptureMs);
      if (this.mVideoFileDescriptor == null)
        break label212;
      this.mEffectsRecorder.setOutputFile(this.mVideoFileDescriptor.getFileDescriptor());
      label138: l2 = this.mActivity.getStorageSpace() - 50000000L;
      if ((l1 <= 0L) || (l1 >= l2))
        break label237;
      label164: this.mEffectsRecorder.setMaxFileSize(l1);
      this.mEffectsRecorder.setMaxDuration(this.mMaxVideoDurationInMs);
      label201: label212: label237: return;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      Log.e("CAM_VideoModule", localFileNotFoundException.toString());
      break label75:
      this.mEffectsRecorder.setCaptureRate(0.0D);
      break label117:
      generateVideoFilename(this.mProfile.fileFormat);
      this.mEffectsRecorder.setOutputFile(this.mVideoFilename);
      break label138:
      l1 = l2;
      break label164:
      l1 = 0L;
    }
  }

  private void initializeMiscControls()
  {
    this.mPreviewFrameLayout = ((PreviewFrameLayout)this.mRootView.findViewById(2131558581));
    this.mPreviewFrameLayout.setOnLayoutChangeListener(this.mActivity);
    this.mReviewImage = ((ImageView)this.mRootView.findViewById(2131558589));
    this.mShutterButton = this.mActivity.getShutterButton();
    this.mShutterButton.setImageResource(2130837533);
    this.mShutterButton.setOnShutterButtonListener(this);
    this.mShutterButton.requestFocus();
    if (effectsActive())
      this.mShutterButton.setEnabled(false);
    this.mRecordingTimeView = ((TextView)this.mRootView.findViewById(2131558621));
    this.mRecordingTimeRect = ((RotateLayout)this.mRootView.findViewById(2131558587));
    this.mTimeLapseLabel = this.mRootView.findViewById(2131558622);
    this.mLabelsLinearLayout = ((LinearLayout)this.mRootView.findViewById(2131558588));
    this.mBgLearningMessageRotater = ((RotateLayout)this.mRootView.findViewById(2131558414));
    this.mBgLearningMessageFrame = this.mRootView.findViewById(2131558413);
  }

  private void initializeOverlay()
  {
    this.mRenderOverlay = ((RenderOverlay)this.mRootView.findViewById(2131558584));
    if (this.mPieRenderer == null)
    {
      this.mPieRenderer = new PieRenderer(this.mActivity);
      this.mVideoControl = new VideoController(this.mActivity, this, this.mPieRenderer);
      this.mVideoControl.setListener(this);
      this.mPieRenderer.setPieListener(this);
    }
    this.mRenderOverlay.addRenderer(this.mPieRenderer);
    if (this.mZoomRenderer == null)
      this.mZoomRenderer = new ZoomRenderer(this.mActivity);
    this.mRenderOverlay.addRenderer(this.mZoomRenderer);
    if (this.mGestures == null)
      this.mGestures = new PreviewGestures(this.mActivity, this, this.mZoomRenderer, this.mPieRenderer);
    this.mGestures.setRenderOverlay(this.mRenderOverlay);
    this.mGestures.clearTouchReceivers();
    this.mGestures.addTouchReceiver(this.mMenu);
    this.mGestures.addTouchReceiver(this.mBlocker);
    if (!isVideoCaptureIntent())
      return;
    if (this.mReviewCancelButton != null)
      this.mGestures.addTouchReceiver((View)this.mReviewCancelButton);
    if (this.mReviewDoneButton == null)
      return;
    this.mGestures.addTouchReceiver((View)this.mReviewDoneButton);
  }

  // ERROR //
  private void initializeRecorder()
  {
    // Byte code:
    //   0: ldc_w 369
    //   3: ldc_w 1090
    //   6: invokestatic 391	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   9: pop
    //   10: aload_0
    //   11: getfield 257	com/android/camera/VideoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   14: getfield 453	com/android/camera/ActivityBase:mCameraDevice	Lcom/android/camera/CameraManager$CameraProxy;
    //   17: ifnonnull +4 -> 21
    //   20: return
    //   21: getstatic 1093	com/android/gallery3d/common/ApiHelper:HAS_SURFACE_TEXTURE_RECORDING	Z
    //   24: ifne +24 -> 48
    //   27: getstatic 1096	com/android/gallery3d/common/ApiHelper:HAS_SURFACE_TEXTURE	Z
    //   30: ifeq +18 -> 48
    //   33: aload_0
    //   34: getfield 199	com/android/camera/VideoModule:mPreviewSurfaceView	Lcom/android/camera/ui/PreviewSurfaceView;
    //   37: iconst_0
    //   38: invokevirtual 1099	com/android/camera/ui/PreviewSurfaceView:setVisibility	(I)V
    //   41: aload_0
    //   42: getfield 249	com/android/camera/VideoModule:mSurfaceViewReady	Z
    //   45: ifeq -25 -> 20
    //   48: aload_0
    //   49: getfield 257	com/android/camera/VideoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   52: invokevirtual 570	com/android/camera/CameraActivity:getIntent	()Landroid/content/Intent;
    //   55: invokevirtual 924	android/content/Intent:getExtras	()Landroid/os/Bundle;
    //   58: astore_2
    //   59: aload_0
    //   60: invokespecial 926	com/android/camera/VideoModule:closeVideoFileDescriptor	()V
    //   63: aload_0
    //   64: getfield 784	com/android/camera/VideoModule:mIsVideoCaptureIntent	Z
    //   67: ifeq +444 -> 511
    //   70: aload_2
    //   71: ifnull +440 -> 511
    //   74: aload_2
    //   75: ldc_w 928
    //   78: invokevirtual 934	android/os/Bundle:getParcelable	(Ljava/lang/String;)Landroid/os/Parcelable;
    //   81: checkcast 936	android/net/Uri
    //   84: astore 14
    //   86: aload 14
    //   88: ifnull +25 -> 113
    //   91: aload_0
    //   92: aload_0
    //   93: getfield 350	com/android/camera/VideoModule:mContentResolver	Landroid/content/ContentResolver;
    //   96: aload 14
    //   98: ldc_w 938
    //   101: invokevirtual 942	android/content/ContentResolver:openFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;)Landroid/os/ParcelFileDescriptor;
    //   104: putfield 282	com/android/camera/VideoModule:mVideoFileDescriptor	Landroid/os/ParcelFileDescriptor;
    //   107: aload_0
    //   108: aload 14
    //   110: putfield 332	com/android/camera/VideoModule:mCurrentVideoUri	Landroid/net/Uri;
    //   113: aload_2
    //   114: ldc_w 944
    //   117: invokevirtual 948	android/os/Bundle:getLong	(Ljava/lang/String;)J
    //   120: lstore_3
    //   121: aload_0
    //   122: new 1101	android/media/MediaRecorder
    //   125: dup
    //   126: invokespecial 1102	android/media/MediaRecorder:<init>	()V
    //   129: putfield 1104	com/android/camera/VideoModule:mMediaRecorder	Landroid/media/MediaRecorder;
    //   132: aload_0
    //   133: invokespecial 1107	com/android/camera/VideoModule:setupMediaRecorderPreviewDisplay	()V
    //   136: aload_0
    //   137: getfield 257	com/android/camera/VideoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   140: getfield 453	com/android/camera/ActivityBase:mCameraDevice	Lcom/android/camera/CameraManager$CameraProxy;
    //   143: invokevirtual 1110	com/android/camera/CameraManager$CameraProxy:unlock	()V
    //   146: aload_0
    //   147: getfield 1104	com/android/camera/VideoModule:mMediaRecorder	Landroid/media/MediaRecorder;
    //   150: aload_0
    //   151: getfield 257	com/android/camera/VideoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   154: getfield 453	com/android/camera/ActivityBase:mCameraDevice	Lcom/android/camera/CameraManager$CameraProxy;
    //   157: invokevirtual 1114	com/android/camera/CameraManager$CameraProxy:getCamera	()Landroid/hardware/Camera;
    //   160: invokevirtual 1117	android/media/MediaRecorder:setCamera	(Landroid/hardware/Camera;)V
    //   163: aload_0
    //   164: getfield 163	com/android/camera/VideoModule:mCaptureTimeLapse	Z
    //   167: ifne +11 -> 178
    //   170: aload_0
    //   171: getfield 1104	com/android/camera/VideoModule:mMediaRecorder	Landroid/media/MediaRecorder;
    //   174: iconst_5
    //   175: invokevirtual 1120	android/media/MediaRecorder:setAudioSource	(I)V
    //   178: aload_0
    //   179: getfield 1104	com/android/camera/VideoModule:mMediaRecorder	Landroid/media/MediaRecorder;
    //   182: iconst_1
    //   183: invokevirtual 1123	android/media/MediaRecorder:setVideoSource	(I)V
    //   186: aload_0
    //   187: getfield 1104	com/android/camera/VideoModule:mMediaRecorder	Landroid/media/MediaRecorder;
    //   190: aload_0
    //   191: getfield 626	com/android/camera/VideoModule:mProfile	Landroid/media/CamcorderProfile;
    //   194: invokevirtual 1124	android/media/MediaRecorder:setProfile	(Landroid/media/CamcorderProfile;)V
    //   197: aload_0
    //   198: getfield 1104	com/android/camera/VideoModule:mMediaRecorder	Landroid/media/MediaRecorder;
    //   201: aload_0
    //   202: getfield 970	com/android/camera/VideoModule:mMaxVideoDurationInMs	I
    //   205: invokevirtual 1125	android/media/MediaRecorder:setMaxDuration	(I)V
    //   208: aload_0
    //   209: getfield 163	com/android/camera/VideoModule:mCaptureTimeLapse	Z
    //   212: ifeq +23 -> 235
    //   215: ldc2_w 747
    //   218: aload_0
    //   219: getfield 165	com/android/camera/VideoModule:mTimeBetweenTimeLapseFrameCaptureMs	I
    //   222: i2d
    //   223: ddiv
    //   224: dstore 12
    //   226: aload_0
    //   227: getfield 1104	com/android/camera/VideoModule:mMediaRecorder	Landroid/media/MediaRecorder;
    //   230: dload 12
    //   232: invokestatic 1128	com/android/camera/VideoModule:setCaptureRate	(Landroid/media/MediaRecorder;D)V
    //   235: aload_0
    //   236: invokespecial 1131	com/android/camera/VideoModule:setRecordLocation	()V
    //   239: aload_0
    //   240: getfield 282	com/android/camera/VideoModule:mVideoFileDescriptor	Landroid/os/ParcelFileDescriptor;
    //   243: ifnull +159 -> 402
    //   246: aload_0
    //   247: getfield 1104	com/android/camera/VideoModule:mMediaRecorder	Landroid/media/MediaRecorder;
    //   250: aload_0
    //   251: getfield 282	com/android/camera/VideoModule:mVideoFileDescriptor	Landroid/os/ParcelFileDescriptor;
    //   254: invokevirtual 956	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   257: invokevirtual 1132	android/media/MediaRecorder:setOutputFile	(Ljava/io/FileDescriptor;)V
    //   260: aload_0
    //   261: getfield 257	com/android/camera/VideoModule:mActivity	Lcom/android/camera/CameraActivity;
    //   264: invokevirtual 963	com/android/camera/CameraActivity:getStorageSpace	()J
    //   267: ldc2_w 964
    //   270: lsub
    //   271: lstore 5
    //   273: lload_3
    //   274: lconst_0
    //   275: lcmp
    //   276: ifle +229 -> 505
    //   279: lload_3
    //   280: lload 5
    //   282: lcmp
    //   283: ifge +222 -> 505
    //   286: aload_0
    //   287: getfield 1104	com/android/camera/VideoModule:mMediaRecorder	Landroid/media/MediaRecorder;
    //   290: lload_3
    //   291: invokevirtual 1133	android/media/MediaRecorder:setMaxFileSize	(J)V
    //   294: aload_0
    //   295: getfield 176	com/android/camera/VideoModule:mOrientation	I
    //   298: iconst_m1
    //   299: if_icmpeq +200 -> 499
    //   302: invokestatic 484	com/android/camera/CameraHolder:instance	()Lcom/android/camera/CameraHolder;
    //   305: invokevirtual 839	com/android/camera/CameraHolder:getCameraInfo	()[Landroid/hardware/Camera$CameraInfo;
    //   308: aload_0
    //   309: getfield 841	com/android/camera/VideoModule:mCameraId	I
    //   312: aaload
    //   313: astore 11
    //   315: aload 11
    //   317: getfield 860	android/hardware/Camera$CameraInfo:facing	I
    //   320: iconst_1
    //   321: if_icmpne +106 -> 427
    //   324: sipush 360
    //   327: aload 11
    //   329: getfield 1134	android/hardware/Camera$CameraInfo:orientation	I
    //   332: aload_0
    //   333: getfield 176	com/android/camera/VideoModule:mOrientation	I
    //   336: isub
    //   337: iadd
    //   338: sipush 360
    //   341: irem
    //   342: istore 8
    //   344: aload_0
    //   345: getfield 1104	com/android/camera/VideoModule:mMediaRecorder	Landroid/media/MediaRecorder;
    //   348: iload 8
    //   350: invokevirtual 1135	android/media/MediaRecorder:setOrientationHint	(I)V
    //   353: aload_0
    //   354: aload_0
    //   355: getfield 178	com/android/camera/VideoModule:mOrientationCompensation	I
    //   358: putfield 884	com/android/camera/VideoModule:mOrientationCompensationAtRecordStart	I
    //   361: aload_0
    //   362: getfield 1104	com/android/camera/VideoModule:mMediaRecorder	Landroid/media/MediaRecorder;
    //   365: invokevirtual 1138	android/media/MediaRecorder:prepare	()V
    //   368: aload_0
    //   369: getfield 1104	com/android/camera/VideoModule:mMediaRecorder	Landroid/media/MediaRecorder;
    //   372: aload_0
    //   373: invokevirtual 1139	android/media/MediaRecorder:setOnErrorListener	(Landroid/media/MediaRecorder$OnErrorListener;)V
    //   376: aload_0
    //   377: getfield 1104	com/android/camera/VideoModule:mMediaRecorder	Landroid/media/MediaRecorder;
    //   380: aload_0
    //   381: invokevirtual 1140	android/media/MediaRecorder:setOnInfoListener	(Landroid/media/MediaRecorder$OnInfoListener;)V
    //   384: return
    //   385: astore 15
    //   387: ldc_w 369
    //   390: aload 15
    //   392: invokevirtual 974	java/io/FileNotFoundException:toString	()Ljava/lang/String;
    //   395: invokestatic 976	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   398: pop
    //   399: goto -286 -> 113
    //   402: aload_0
    //   403: aload_0
    //   404: getfield 626	com/android/camera/VideoModule:mProfile	Landroid/media/CamcorderProfile;
    //   407: getfield 979	android/media/CamcorderProfile:fileFormat	I
    //   410: invokespecial 981	com/android/camera/VideoModule:generateVideoFilename	(I)V
    //   413: aload_0
    //   414: getfield 1104	com/android/camera/VideoModule:mMediaRecorder	Landroid/media/MediaRecorder;
    //   417: aload_0
    //   418: getfield 434	com/android/camera/VideoModule:mVideoFilename	Ljava/lang/String;
    //   421: invokevirtual 1141	android/media/MediaRecorder:setOutputFile	(Ljava/lang/String;)V
    //   424: goto -164 -> 260
    //   427: aload 11
    //   429: getfield 1134	android/hardware/Camera$CameraInfo:orientation	I
    //   432: aload_0
    //   433: getfield 176	com/android/camera/VideoModule:mOrientation	I
    //   436: iadd
    //   437: sipush 360
    //   440: irem
    //   441: istore 8
    //   443: goto -99 -> 344
    //   446: astore 9
    //   448: ldc_w 369
    //   451: new 371	java/lang/StringBuilder
    //   454: dup
    //   455: invokespecial 372	java/lang/StringBuilder:<init>	()V
    //   458: ldc_w 1143
    //   461: invokevirtual 378	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   464: aload_0
    //   465: getfield 434	com/android/camera/VideoModule:mVideoFilename	Ljava/lang/String;
    //   468: invokevirtual 378	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   471: invokevirtual 385	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   474: aload 9
    //   476: invokestatic 405	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   479: pop
    //   480: aload_0
    //   481: invokespecial 1146	com/android/camera/VideoModule:releaseMediaRecorder	()V
    //   484: new 1089	java/lang/RuntimeException
    //   487: dup
    //   488: aload 9
    //   490: invokespecial 1149	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   493: athrow
    //   494: astore 7
    //   496: goto -202 -> 294
    //   499: iconst_0
    //   500: istore 8
    //   502: goto -158 -> 344
    //   505: lload 5
    //   507: lstore_3
    //   508: goto -222 -> 286
    //   511: lconst_0
    //   512: lstore_3
    //   513: goto -392 -> 121
    //
    // Exception table:
    //   from	to	target	type
    //   91	113	385	java/io/FileNotFoundException
    //   361	368	446	java/io/IOException
    //   286	294	494	java/lang/RuntimeException
  }

  private void initializeSurfaceView()
  {
    this.mPreviewSurfaceView = ((PreviewSurfaceView)this.mRootView.findViewById(2131558591));
    if (!ApiHelper.HAS_SURFACE_TEXTURE)
    {
      if (this.mSurfaceViewCallback == null)
        this.mSurfaceViewCallback = new SurfaceViewCallback();
      this.mPreviewSurfaceView.getHolder().addCallback(this.mSurfaceViewCallback);
      this.mPreviewSurfaceView.setVisibility(0);
    }
    do
      return;
    while (ApiHelper.HAS_SURFACE_TEXTURE_RECORDING);
    if (this.mSurfaceViewCallback == null)
    {
      this.mSurfaceViewCallback = new SurfaceViewCallback();
      this.mFrameDrawnListener = new CameraScreenNail.OnFrameDrawnListener()
      {
        public void onFrameDrawn(CameraScreenNail paramCameraScreenNail)
        {
          VideoModule.this.mHandler.sendEmptyMessage(10);
        }
      };
    }
    this.mPreviewSurfaceView.getHolder().addCallback(this.mSurfaceViewCallback);
  }

  private void initializeVideoControl()
  {
    loadCameraPreferences();
    this.mVideoControl.initialize(this.mPreferenceGroup);
    if (!effectsActive())
      return;
    VideoController localVideoController = this.mVideoControl;
    String[] arrayOfString = new String[2];
    arrayOfString[0] = "pref_video_quality_key";
    arrayOfString[1] = Integer.toString(getLowVideoQuality());
    localVideoController.overrideSettings(arrayOfString);
  }

  private void initializeVideoSnapshot()
  {
    if ((Util.isVideoSnapshotSupported(this.mParameters)) && (!this.mIsVideoCaptureIntent))
    {
      this.mActivity.setSingleTapUpListener(this.mPreviewFrameLayout);
      if (this.mPreferences.getBoolean("pref_video_first_use_hint_shown_key", true))
        this.mHandler.sendEmptyMessageDelayed(7, 1000L);
      return;
    }
    this.mActivity.setSingleTapUpListener(null);
  }

  private void initializeZoom()
  {
    if (!this.mParameters.isZoomSupported())
      return;
    this.mZoomMax = this.mParameters.getMaxZoom();
    this.mZoomRatios = this.mParameters.getZoomRatios();
    this.mZoomRenderer.setZoomMax(this.mZoomMax);
    this.mZoomRenderer.setZoom(this.mParameters.getZoom());
    this.mZoomRenderer.setZoomValue(((Integer)this.mZoomRatios.get(this.mParameters.getZoom())).intValue());
    this.mZoomRenderer.setOnZoomChangeListener(new ZoomChangeListener(null));
  }

  private static boolean isSupported(String paramString, List<String> paramList)
  {
    if (paramList == null);
    do
      return false;
    while (paramList.indexOf(paramString) < 0);
    return true;
  }

  private boolean isVideoCaptureIntent()
  {
    return "android.media.action.VIDEO_CAPTURE".equals(this.mActivity.getIntent().getAction());
  }

  private void keepScreenOn()
  {
    this.mHandler.removeMessages(4);
    this.mActivity.getWindow().addFlags(128);
  }

  private void keepScreenOnAwhile()
  {
    this.mHandler.removeMessages(4);
    this.mActivity.getWindow().addFlags(128);
    this.mHandler.sendEmptyMessageDelayed(4, 120000L);
  }

  private void loadCameraPreferences()
  {
    this.mPreferenceGroup = filterPreferenceScreenByIntent(new CameraSettings(this.mActivity, this.mParameters, this.mCameraId, CameraHolder.instance().getCameraInfo()).getPreferenceGroup(2131165189));
  }

  private static String millisecondToTimeString(long paramLong, boolean paramBoolean)
  {
    long l1 = paramLong / 1000L;
    long l2 = l1 / 60L;
    long l3 = l2 / 60L;
    long l4 = l2 - 60L * l3;
    long l5 = l1 - 60L * l2;
    StringBuilder localStringBuilder = new StringBuilder();
    if (l3 > 0L)
    {
      if (l3 < 10L)
        localStringBuilder.append('0');
      localStringBuilder.append(l3);
      localStringBuilder.append(':');
    }
    if (l4 < 10L)
      localStringBuilder.append('0');
    localStringBuilder.append(l4);
    localStringBuilder.append(':');
    if (l5 < 10L)
      localStringBuilder.append('0');
    localStringBuilder.append(l5);
    if (paramBoolean)
    {
      localStringBuilder.append('.');
      long l6 = (paramLong - 1000L * l1) / 10L;
      if (l6 < 10L)
        localStringBuilder.append('0');
      localStringBuilder.append(l6);
    }
    return localStringBuilder.toString();
  }

  private void onStopVideoRecording()
  {
    boolean bool1 = true;
    this.mEffectsDisplayResult = bool1;
    boolean bool2 = stopVideoRecording();
    if (this.mIsVideoCaptureIntent)
      if (!effectsActive())
      {
        if (!this.mQuickCapture)
          break label48;
        if (bool2)
          break label43;
        label37: doReturnToCaller(bool1);
      }
    do
    {
      do
      {
        return;
        label43: bool1 = false;
        label48: break label37:
      }
      while (bool2);
      showAlert();
      return;
    }
    while ((bool2) || (this.mPaused) || (!ApiHelper.HAS_SURFACE_TEXTURE_RECORDING));
    ((CameraScreenNail)this.mActivity.mCameraScreenNail).animateCapture(this.mDisplayRotation);
  }

  private void openCamera()
  {
    try
    {
      this.mActivity.mCameraDevice = Util.openCamera(this.mActivity, this.mCameraId);
      this.mParameters = this.mActivity.mCameraDevice.getParameters();
      return;
    }
    catch (CameraHardwareException localCameraHardwareException)
    {
      this.mActivity.mOpenCameraFail = true;
      return;
    }
    catch (CameraDisabledException localCameraDisabledException)
    {
      this.mActivity.mCameraDisabled = true;
    }
  }

  private void pauseAudioPlayback()
  {
    Intent localIntent = new Intent("com.android.music.musicservicecommand");
    localIntent.putExtra("command", "pause");
    this.mActivity.sendBroadcast(localIntent);
  }

  private void readVideoPreferences()
  {
    String str = CameraSettings.getDefaultVideoQuality(this.mCameraId, this.mActivity.getResources().getString(2131361912));
    int i = Integer.valueOf(this.mPreferences.getString("pref_video_quality_key", str)).intValue();
    Intent localIntent = this.mActivity.getIntent();
    if (localIntent.hasExtra("android.intent.extra.videoQuality"))
    {
      if (localIntent.getIntExtra("android.intent.extra.videoQuality", 0) <= 0)
        break label232;
      i = 1;
    }
    if (localIntent.hasExtra("android.intent.extra.durationLimit"))
    {
      label70: this.mMaxVideoDurationInMs = (1000 * localIntent.getIntExtra("android.intent.extra.durationLimit", 0));
      label96: this.mEffectType = CameraSettings.readEffectType(this.mPreferences);
      if (this.mEffectType == 0)
        break label245;
      this.mEffectParameter = CameraSettings.readEffectParameter(this.mPreferences);
      if (CamcorderProfile.get(this.mCameraId, i).videoFrameHeight > 480)
        i = getLowVideoQuality();
    }
    while (true)
    {
      if (ApiHelper.HAS_TIME_LAPSE_RECORDING)
      {
        this.mTimeBetweenTimeLapseFrameCaptureMs = Integer.parseInt(this.mPreferences.getString("pref_video_time_lapse_frame_interval_key", this.mActivity.getString(2131361919)));
        int j = this.mTimeBetweenTimeLapseFrameCaptureMs;
        int k = 0;
        if (j != 0)
          k = 1;
        this.mCaptureTimeLapse = k;
      }
      if (this.mCaptureTimeLapse);
      this.mProfile = CamcorderProfile.get(this.mCameraId, i += 1000);
      getDesiredPreviewSize();
      return;
      label232: i = 0;
      break label70:
      this.mMaxVideoDurationInMs = 0;
      break label96:
      label245: this.mEffectParameter = null;
    }
  }

  private void releaseEffectsRecorder()
  {
    Log.v("CAM_VideoModule", "Releasing effects recorder.");
    if (this.mEffectsRecorder != null)
    {
      cleanupEmptyFile();
      this.mEffectsRecorder.release();
      this.mEffectsRecorder = null;
    }
    this.mEffectType = 0;
    this.mVideoFilename = null;
  }

  private void releaseMediaRecorder()
  {
    Log.v("CAM_VideoModule", "Releasing media recorder.");
    if (this.mMediaRecorder != null)
    {
      cleanupEmptyFile();
      this.mMediaRecorder.reset();
      this.mMediaRecorder.release();
      this.mMediaRecorder = null;
    }
    this.mVideoFilename = null;
  }

  private void releasePreviewResources()
  {
    if (!ApiHelper.HAS_SURFACE_TEXTURE)
      return;
    CameraScreenNail localCameraScreenNail = (CameraScreenNail)this.mActivity.mCameraScreenNail;
    if (localCameraScreenNail.getSurfaceTexture() != null)
      localCameraScreenNail.releaseSurfaceTexture();
    if (ApiHelper.HAS_SURFACE_TEXTURE_RECORDING)
      return;
    this.mHandler.removeMessages(10);
    this.mPreviewSurfaceView.setVisibility(8);
  }

  private boolean resetEffect()
  {
    if (this.mResetEffect)
    {
      String str = this.mPreferences.getString("pref_video_effect_key", this.mPrefVideoEffectDefault);
      if (!this.mPrefVideoEffectDefault.equals(str))
      {
        writeDefaultEffectToPrefs();
        return true;
      }
    }
    this.mResetEffect = true;
    return false;
  }

  private void resetScreenOn()
  {
    this.mHandler.removeMessages(4);
    this.mActivity.getWindow().clearFlags(128);
  }

  private void resizeForPreviewAspectRatio()
  {
    this.mPreviewFrameLayout.setAspectRatio(this.mProfile.videoFrameWidth / this.mProfile.videoFrameHeight);
  }

  private void setCameraParameters()
  {
    this.mParameters.setPreviewSize(this.mDesiredPreviewWidth, this.mDesiredPreviewHeight);
    this.mParameters.setPreviewFrameRate(this.mProfile.videoFrameRate);
    String str1;
    if (this.mActivity.mShowCameraAppView)
    {
      str1 = this.mPreferences.getString("pref_camera_video_flashmode_key", this.mActivity.getString(2131361941));
      label60: if (!isSupported(str1, this.mParameters.getSupportedFlashModes()))
        break label384;
      this.mParameters.setFlashMode(str1);
      label82: String str2 = this.mPreferences.getString("pref_camera_whitebalance_key", this.mActivity.getString(2131361942));
      if (!isSupported(str2, this.mParameters.getSupportedWhiteBalance()))
        break label408;
      this.mParameters.setWhiteBalance(str2);
    }
    while (true)
    {
      if (this.mParameters.isZoomSupported())
        this.mParameters.setZoom(this.mZoomValue);
      if (isSupported("continuous-video", this.mParameters.getSupportedFocusModes()))
        this.mParameters.setFocusMode("continuous-video");
      this.mParameters.set("recording-hint", "true");
      if ("true".equals(this.mParameters.get("video-stabilization-supported")))
        this.mParameters.set("video-stabilization", "true");
      Camera.Size localSize = Util.getOptimalVideoSnapshotPictureSize(this.mParameters.getSupportedPictureSizes(), this.mDesiredPreviewWidth / this.mDesiredPreviewHeight);
      if (!this.mParameters.getPictureSize().equals(localSize))
        this.mParameters.setPictureSize(localSize.width, localSize.height);
      Log.v("CAM_VideoModule", "Video snapshot size is " + localSize.width + "x" + localSize.height);
      int i = CameraProfile.getJpegEncodingQualityParameter(this.mCameraId, 2);
      this.mParameters.setJpegQuality(i);
      this.mActivity.mCameraDevice.setParameters(this.mParameters);
      this.mParameters = this.mActivity.mCameraDevice.getParameters();
      updateCameraScreenNailSize(this.mDesiredPreviewWidth, this.mDesiredPreviewHeight);
      return;
      str1 = "off";
      break label60:
      label384: if (this.mParameters.getFlashMode() == null);
      this.mActivity.getString(2131361936);
      break label82:
      label408: if (this.mParameters.getWhiteBalance() != null)
        continue;
    }
  }

  @TargetApi(11)
  private static void setCaptureRate(MediaRecorder paramMediaRecorder, double paramDouble)
  {
    paramMediaRecorder.setCaptureRate(paramDouble);
  }

  private void setDisplayOrientation()
  {
    this.mDisplayRotation = Util.getDisplayRotation(this.mActivity);
    if (ApiHelper.HAS_SURFACE_TEXTURE);
    for (this.mCameraDisplayOrientation = Util.getDisplayOrientation(0, this.mCameraId); ; this.mCameraDisplayOrientation = Util.getDisplayOrientation(this.mDisplayRotation, this.mCameraId))
    {
      this.mActivity.getGLRoot().requestLayoutContentPane();
      return;
    }
  }

  private void setOrientationIndicator(int paramInt, boolean paramBoolean)
  {
    Rotatable[] arrayOfRotatable = new Rotatable[3];
    arrayOfRotatable[0] = this.mBgLearningMessageRotater;
    arrayOfRotatable[1] = this.mReviewDoneButton;
    arrayOfRotatable[2] = this.mReviewPlayButton;
    int i = arrayOfRotatable.length;
    for (int j = 0; j < i; ++j)
    {
      Rotatable localRotatable = arrayOfRotatable[j];
      if (localRotatable == null)
        continue;
      localRotatable.setOrientation(paramInt, paramBoolean);
    }
    if (this.mGestures != null)
      this.mGestures.setOrientation(paramInt);
    if (this.mReviewCancelButton instanceof RotateLayout)
      this.mReviewCancelButton.setOrientation(paramInt, paramBoolean);
    if (this.mLabelsLinearLayout != null)
    {
      if ((0x1 & paramInt / 90) != 0)
        break label139;
      this.mLabelsLinearLayout.setOrientation(1);
    }
    while (true)
    {
      this.mRecordingTimeRect.setOrientation(this.mOrientationCompensation, paramBoolean);
      return;
      label139: this.mLabelsLinearLayout.setOrientation(0);
    }
  }

  @TargetApi(14)
  private void setRecordLocation()
  {
    if (Build.VERSION.SDK_INT < 14)
      return;
    Location localLocation = this.mLocationManager.getCurrentLocation();
    if (localLocation == null)
      return;
    this.mMediaRecorder.setLocation((float)localLocation.getLatitude(), (float)localLocation.getLongitude());
  }

  private void setShowMenu(boolean paramBoolean)
  {
    int j;
    if (this.mOnScreenIndicators != null)
    {
      View localView2 = this.mOnScreenIndicators;
      if (!paramBoolean)
        break label51;
      j = 0;
      label20: localView2.setVisibility(j);
    }
    View localView1;
    int i;
    if (this.mMenu != null)
    {
      localView1 = this.mMenu;
      i = 0;
      if (!paramBoolean)
        break label58;
    }
    while (true)
    {
      localView1.setVisibility(i);
      return;
      label51: j = 8;
      break label20:
      label58: i = 8;
    }
  }

  private void setupMediaRecorderPreviewDisplay()
  {
    if (!ApiHelper.HAS_SURFACE_TEXTURE)
      this.mMediaRecorder.setPreviewDisplay(this.mPreviewSurfaceView.getHolder().getSurface());
    do
      return;
    while (ApiHelper.HAS_SURFACE_TEXTURE_RECORDING);
    stopPreview();
    this.mActivity.mCameraDevice.setPreviewDisplayAsync(this.mPreviewSurfaceView.getHolder());
    this.mActivity.mCameraDevice.setDisplayOrientation(Util.getDisplayOrientation(this.mDisplayRotation, this.mCameraId));
    this.mActivity.mCameraDevice.startPreviewAsync();
    this.mPreviewing = true;
    this.mMediaRecorder.setPreviewDisplay(this.mPreviewSurfaceView.getHolder().getSurface());
  }

  private void showAlert()
  {
    int i = 1;
    Bitmap localBitmap1;
    if (this.mVideoFileDescriptor != null)
    {
      localBitmap1 = Thumbnail.createVideoThumbnailBitmap(this.mVideoFileDescriptor.getFileDescriptor(), this.mPreviewFrameLayout.getWidth());
      if (localBitmap1 != null)
        label27: if (CameraHolder.instance().getCameraInfo()[this.mCameraId].facing != i)
          break label160;
    }
    while (true)
    {
      Bitmap localBitmap2 = Util.rotateAndMirror(localBitmap1, -this.mOrientationCompensationAtRecordStart, i);
      this.mReviewImage.setImageBitmap(localBitmap2);
      this.mReviewImage.setVisibility(0);
      Util.fadeOut(this.mShutterButton);
      Util.fadeIn((View)this.mReviewDoneButton);
      Util.fadeIn(this.mReviewPlayButton);
      this.mMenu.setVisibility(8);
      this.mOnScreenIndicators.setVisibility(8);
      enableCameraControls(false);
      showTimeLapseUI(false);
      return;
      String str = this.mCurrentVideoFilename;
      localBitmap1 = null;
      if (str != null);
      localBitmap1 = Thumbnail.createVideoThumbnailBitmap(this.mCurrentVideoFilename, this.mPreviewFrameLayout.getWidth());
      break label27:
      label160: int j = 0;
    }
  }

  private void showRecordingUI(boolean paramBoolean)
  {
    View localView1 = this.mMenu;
    int i;
    label12: int j;
    if (paramBoolean)
    {
      i = 8;
      localView1.setVisibility(i);
      View localView2 = this.mOnScreenIndicators;
      if (!paramBoolean)
        break label115;
      j = 8;
      label31: localView2.setVisibility(j);
      if (!paramBoolean)
        break label121;
      this.mShutterButton.setImageResource(2130837549);
      this.mActivity.hideSwitcher();
      this.mRecordingTimeView.setText("");
      this.mRecordingTimeView.setVisibility(0);
      if (this.mReviewControl != null)
        this.mReviewControl.setVisibility(8);
      if ((ApiHelper.HAS_ZOOM_WHEN_RECORDING) || (!this.mParameters.isZoomSupported()));
    }
    do
    {
      return;
      i = 0;
      break label12:
      label115: j = 0;
      break label31:
      label121: this.mShutterButton.setImageResource(2130837533);
      this.mActivity.showSwitcher();
      this.mRecordingTimeView.setVisibility(8);
      if (this.mReviewControl == null)
        continue;
      this.mReviewControl.setVisibility(0);
    }
    while ((ApiHelper.HAS_ZOOM_WHEN_RECORDING) || (!this.mParameters.isZoomSupported()));
  }

  private void showTapToSnapshotToast()
  {
    new RotateTextToast(this.mActivity, 2131361989, this.mOrientationCompensation).show();
    SharedPreferences.Editor localEditor = this.mPreferences.edit();
    localEditor.putBoolean("pref_video_first_use_hint_shown_key", false);
    localEditor.apply();
  }

  private void showTimeLapseUI(boolean paramBoolean)
  {
    View localView;
    if (this.mTimeLapseLabel != null)
    {
      localView = this.mTimeLapseLabel;
      if (!paramBoolean)
        break label24;
    }
    for (int i = 0; ; i = 8)
    {
      localView.setVisibility(i);
      label24: return;
    }
  }

  private void startPlayVideoActivity()
  {
    Intent localIntent = new Intent("android.intent.action.VIEW");
    localIntent.setDataAndType(this.mCurrentVideoUri, convertOutputFormatToMimeType(this.mProfile.fileFormat));
    try
    {
      this.mActivity.startActivity(localIntent);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("CAM_VideoModule", "Couldn't view video " + this.mCurrentVideoUri, localActivityNotFoundException);
    }
  }

  private void startPreview()
  {
    Log.v("CAM_VideoModule", "startPreview");
    this.mActivity.mCameraDevice.setErrorCallback(this.mErrorCallback);
    if (this.mPreviewing == true)
    {
      stopPreview();
      if ((effectsActive()) && (this.mEffectsRecorder != null))
      {
        this.mEffectsRecorder.release();
        this.mEffectsRecorder = null;
      }
    }
    setDisplayOrientation();
    this.mActivity.mCameraDevice.setDisplayOrientation(this.mCameraDisplayOrientation);
    setCameraParameters();
    while (true)
    {
      try
      {
        if (effectsActive())
          break label173;
        if (ApiHelper.HAS_SURFACE_TEXTURE)
        {
          this.mActivity.mCameraDevice.setPreviewTextureAsync(((CameraScreenNail)this.mActivity.mCameraScreenNail).getSurfaceTexture());
          this.mActivity.mCameraDevice.startPreviewAsync();
          this.mPreviewing = true;
          return;
        }
        this.mActivity.mCameraDevice.setPreviewDisplayAsync(this.mPreviewSurfaceView.getHolder());
      }
      catch (Throwable localThrowable)
      {
        closeCamera();
        throw new RuntimeException("startPreview failed", localThrowable);
      }
      label173: initializeEffectsPreview();
      this.mEffectsRecorder.startPreview();
    }
  }

  private void startVideoRecording()
  {
    Log.v("CAM_VideoModule", "startVideoRecording");
    this.mActivity.setSwipingEnabled(false);
    this.mActivity.updateStorageSpaceAndHint();
    if (this.mActivity.getStorageSpace() <= 50000000L)
    {
      Log.v("CAM_VideoModule", "Storage issue, ignore the start request");
      return;
    }
    this.mCurrentVideoUri = null;
    if (effectsActive())
    {
      initializeEffectsRecording();
      if (this.mEffectsRecorder == null)
      {
        Log.e("CAM_VideoModule", "Fail to initialize effect recorder");
        return;
      }
    }
    else
    {
      initializeRecorder();
      if (this.mMediaRecorder == null)
      {
        Log.e("CAM_VideoModule", "Fail to initialize media recorder");
        return;
      }
    }
    pauseAudioPlayback();
    if (effectsActive());
    while (true)
    {
      try
      {
        this.mEffectsRecorder.startRecording();
        if (ApiHelper.HAS_ZOOM_WHEN_RECORDING)
          this.mParameters = this.mActivity.mCameraDevice.getParameters();
        enableCameraControls(false);
        this.mMediaRecorderRecording = true;
        this.mActivity.getOrientationManager().lockOrientation();
        this.mRecordingStartTime = SystemClock.uptimeMillis();
        showRecordingUI(true);
        updateRecordingTime();
        keepScreenOn();
        return;
      }
      catch (RuntimeException localRuntimeException2)
      {
        Log.e("CAM_VideoModule", "Could not start effects recorder. ", localRuntimeException2);
        releaseEffectsRecorder();
        return;
      }
      try
      {
        this.mMediaRecorder.start();
      }
      catch (RuntimeException localRuntimeException1)
      {
        Log.e("CAM_VideoModule", "Could not start media recorder. ", localRuntimeException1);
        releaseMediaRecorder();
        this.mActivity.mCameraDevice.lock();
      }
    }
  }

  private void stopPreview()
  {
    this.mActivity.mCameraDevice.stopPreview();
    this.mPreviewing = false;
  }

  private boolean stopVideoRecording()
  {
    Log.v("CAM_VideoModule", "stopVideoRecording");
    this.mActivity.setSwipingEnabled(true);
    this.mActivity.showSwitcher();
    if (this.mMediaRecorderRecording);
    int j;
    try
    {
      if (effectsActive())
      {
        this.mEffectsRecorder.stopRecording();
        j = 0;
      }
    }
    catch (RuntimeException localRuntimeException1)
    {
      label49: int i;
      try
      {
        this.mCurrentVideoFilename = this.mVideoFilename;
        Log.v("CAM_VideoModule", "stopVideoRecording: Setting current video filename: " + this.mCurrentVideoFilename);
        for (i = 0; ; i = 1)
        {
          this.mMediaRecorderRecording = false;
          this.mActivity.getOrientationManager().unlockOrientation();
          if (this.mPaused)
          {
            if (effectsActive())
              break;
            bool = true;
            closeCamera(bool);
          }
          showRecordingUI(false);
          if (!this.mIsVideoCaptureIntent)
            enableCameraControls(true);
          setOrientationIndicator(this.mOrientationCompensation, true);
          keepScreenOnAwhile();
          if ((j != 0) && (addVideoToMediaStore()))
            i = 1;
          if (!effectsActive())
          {
            label171: releaseMediaRecorder();
            if (!this.mPaused)
            {
              this.mActivity.mCameraDevice.lock();
              if ((ApiHelper.HAS_SURFACE_TEXTURE) && (!ApiHelper.HAS_SURFACE_TEXTURE_RECORDING))
              {
                stopPreview();
                ((CameraScreenNail)this.mActivity.mCameraScreenNail).setOneTimeOnFrameDrawnListener(this.mFrameDrawnListener);
                startPreview();
              }
            }
          }
          if (!this.mPaused)
            this.mParameters = this.mActivity.mCameraDevice.getParameters();
          return i;
          this.mMediaRecorder.setOnErrorListener(null);
          this.mMediaRecorder.setOnInfoListener(null);
          this.mMediaRecorder.stop();
          j = 1;
          break label49:
          localRuntimeException1 = localRuntimeException1;
          RuntimeException localRuntimeException2 = localRuntimeException1;
          j = 0;
          label295: Log.e("CAM_VideoModule", "stop fail", localRuntimeException2);
          if (this.mVideoFilename == null)
            continue;
          deleteVideoFile(this.mVideoFilename);
        }
        boolean bool = false;
      }
      catch (RuntimeException localRuntimeException3)
      {
        break label295:
        i = 0;
        break label171:
      }
    }
  }

  private void storeImage(byte[] paramArrayOfByte, Location paramLocation)
  {
    long l = System.currentTimeMillis();
    String str = Util.createJpegName(l);
    int i = Exif.getOrientation(paramArrayOfByte);
    Camera.Size localSize = this.mParameters.getPictureSize();
    Uri localUri = Storage.addImage(this.mContentResolver, str, l, paramLocation, i, paramArrayOfByte, localSize.width, localSize.height);
    if (localUri == null)
      return;
    Util.broadcastNewPicture(this.mActivity, localUri);
  }

  private void switchCamera()
  {
    if (this.mPaused)
      return;
    Log.d("CAM_VideoModule", "Start to switch camera.");
    this.mCameraId = this.mPendingSwitchCameraId;
    this.mPendingSwitchCameraId = -1;
    this.mVideoControl.setCameraId(this.mCameraId);
    closeCamera();
    this.mPreferences.setLocalId(this.mActivity, this.mCameraId);
    CameraSettings.upgradeLocalPreferences(this.mPreferences.getLocal());
    openCamera();
    readVideoPreferences();
    startPreview();
    initializeVideoSnapshot();
    resizeForPreviewAspectRatio();
    initializeVideoControl();
    initializeZoom();
    setOrientationIndicator(this.mOrientationCompensation, false);
    if (ApiHelper.HAS_SURFACE_TEXTURE)
      this.mHandler.sendEmptyMessage(9);
    updateOnScreenIndicators();
  }

  private void updateCameraScreenNailSize(int paramInt1, int paramInt2)
  {
    if (!ApiHelper.HAS_SURFACE_TEXTURE);
    CameraScreenNail localCameraScreenNail;
    do
    {
      return;
      if (this.mCameraDisplayOrientation % 180 != 0)
      {
        int k = paramInt1;
        paramInt1 = paramInt2;
        paramInt2 = k;
      }
      localCameraScreenNail = (CameraScreenNail)this.mActivity.mCameraScreenNail;
      int i = localCameraScreenNail.getWidth();
      int j = localCameraScreenNail.getHeight();
      if ((i == paramInt1) && (j == paramInt2))
        continue;
      localCameraScreenNail.setSize(paramInt1, paramInt2);
      localCameraScreenNail.enableAspectRatioClamping();
      this.mActivity.notifyScreenNailChanged();
    }
    while (localCameraScreenNail.getSurfaceTexture() != null);
    localCameraScreenNail.acquireSurfaceTexture();
  }

  private boolean updateEffectSelection()
  {
    int i = this.mEffectType;
    Object localObject = this.mEffectParameter;
    this.mEffectType = CameraSettings.readEffectType(this.mPreferences);
    this.mEffectParameter = CameraSettings.readEffectParameter(this.mPreferences);
    if (this.mEffectType == i)
    {
      if (this.mEffectType == 0);
      do
        return false;
      while (this.mEffectParameter.equals(localObject));
    }
    Log.v("CAM_VideoModule", "New effect selection: " + this.mPreferences.getString("pref_video_effect_key", "none"));
    if (this.mEffectType == 0)
    {
      this.mEffectsRecorder.stopPreview();
      this.mPreviewing = false;
      return true;
    }
    if ((this.mEffectType == 2) && (((String)this.mEffectParameter).equals("gallery")))
    {
      Intent localIntent = new Intent("android.intent.action.PICK");
      localIntent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
      localIntent.putExtra("android.intent.extra.LOCAL_ONLY", true);
      this.mActivity.startActivityForResult(localIntent, 1000);
      return true;
    }
    if (i == 0)
    {
      stopPreview();
      checkQualityAndStartPreview();
    }
    while (true)
    {
      return true;
      this.mEffectsRecorder.setEffect(this.mEffectType, this.mEffectParameter);
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
    if (("on".equals(paramString)) || ("torch".equals(paramString)))
    {
      this.mFlashIndicator.setImageResource(2130837690);
      return;
    }
    this.mFlashIndicator.setImageResource(2130837689);
  }

  private void updateOnScreenIndicators()
  {
    updateFlashOnScreenIndicator(this.mParameters.getFlashMode());
  }

  private void updateRecordingTime()
  {
    if (!this.mMediaRecorderRecording)
      return;
    long l1 = SystemClock.uptimeMillis() - this.mRecordingStartTime;
    int i;
    label40: long l2;
    if ((this.mMaxVideoDurationInMs != 0) && (l1 >= this.mMaxVideoDurationInMs - 60000))
    {
      i = 1;
      if (i == 0)
        break label194;
      l2 = 999L + Math.max(0L, this.mMaxVideoDurationInMs - l1);
    }
    while (true)
    {
      String str;
      long l3;
      label81: Resources localResources;
      if (!this.mCaptureTimeLapse)
      {
        str = millisecondToTimeString(l2, false);
        l3 = 1000L;
        this.mRecordingTimeView.setText(str);
        if (this.mRecordingTimeCountsDown != i)
        {
          this.mRecordingTimeCountsDown = i;
          localResources = this.mActivity.getResources();
          if (i == 0)
            break label186;
        }
      }
      for (int j = 2131296258; ; j = 2131296257)
      {
        int k = localResources.getColor(j);
        this.mRecordingTimeView.setTextColor(k);
        long l4 = l3 - l1 % l3;
        this.mHandler.sendEmptyMessageDelayed(5, l4);
        return;
        i = 0;
        break label40:
        str = millisecondToTimeString(getTimeLapseVideoLength(l1), true);
        l3 = this.mTimeBetweenTimeLapseFrameCaptureMs;
        label186: break label81:
      }
      label194: l2 = l1;
    }
  }

  private void writeDefaultEffectToPrefs()
  {
    SharedPreferences.Editor localEditor = this.mPreferences.edit();
    localEditor.putString("pref_video_effect_key", this.mActivity.getString(2131361976));
    localEditor.apply();
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

  public void dismissPopup(boolean paramBoolean1, boolean paramBoolean2)
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
    this.mVideoControl.popupDismissed(paramBoolean1);
  }

  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mSwitchingCamera)
      return true;
    if ((this.mPopup == null) && (this.mGestures != null) && (this.mRenderOverlay != null))
      return this.mGestures.dispatchTouch(paramMotionEvent);
    if (this.mPopup != null)
      return this.mActivity.superDispatchTouchEvent(paramMotionEvent);
    return false;
  }

  public void init(CameraActivity paramCameraActivity, View paramView, boolean paramBoolean)
  {
    this.mActivity = paramCameraActivity;
    this.mRootView = paramView;
    this.mPreferences = new ComboPreferences(this.mActivity);
    CameraSettings.upgradeGlobalPreferences(this.mPreferences.getGlobal());
    this.mCameraId = getPreferredCameraId(this.mPreferences);
    this.mPreferences.setLocalId(this.mActivity, this.mCameraId);
    CameraSettings.upgradeLocalPreferences(this.mPreferences.getLocal());
    this.mActivity.mNumberOfCameras = CameraHolder.instance().getNumberOfCameras();
    this.mPrefVideoEffectDefault = this.mActivity.getString(2131361976);
    resetEffect();
    CameraOpenThread localCameraOpenThread = new CameraOpenThread();
    localCameraOpenThread.start();
    this.mContentResolver = this.mActivity.getContentResolver();
    this.mActivity.getLayoutInflater().inflate(2130968662, (ViewGroup)this.mRootView);
    this.mIsVideoCaptureIntent = isVideoCaptureIntent();
    boolean bool2;
    if (paramBoolean)
    {
      CameraActivity localCameraActivity2 = this.mActivity;
      if (!this.mIsVideoCaptureIntent)
      {
        bool2 = true;
        label179: localCameraActivity2.reuseCameraScreenNail(bool2);
        label187: initializeSurfaceView();
      }
    }
    try
    {
      localCameraOpenThread.join();
      if (this.mActivity.mOpenCameraFail)
      {
        Util.showErrorAndFinish(this.mActivity, 2131361890);
        return;
        bool2 = false;
        break label179:
        CameraActivity localCameraActivity1 = this.mActivity;
        if (!this.mIsVideoCaptureIntent);
        for (boolean bool1 = true; ; bool1 = false)
        {
          localCameraActivity1.createCameraScreenNail(bool1);
          break label187:
        }
      }
      if (!this.mActivity.mCameraDisabled)
        break label279;
      Util.showErrorAndFinish(this.mActivity, 2131361891);
      label279: return;
    }
    catch (InterruptedException localInterruptedException1)
    {
      Thread localThread = new Thread(new Runnable()
      {
        public void run()
        {
          VideoModule.this.readVideoPreferences();
          VideoModule.this.startPreview();
        }
      });
      localThread.start();
      initializeControlByIntent();
      initializeOverlay();
      initializeMiscControls();
      this.mQuickCapture = this.mActivity.getIntent().getBooleanExtra("android.intent.extra.quickCapture", false);
      this.mLocationManager = new LocationManager(this.mActivity, null);
      this.mOrientationResetNeeded = true;
      do
        try
        {
          localThread.join();
          if (this.mActivity.mOpenCameraFail)
          {
            Util.showErrorAndFinish(this.mActivity, 2131361890);
            return;
          }
        }
        catch (InterruptedException localInterruptedException2)
        {
          showTimeLapseUI(this.mCaptureTimeLapse);
          initializeVideoSnapshot();
          resizeForPreviewAspectRatio();
          initializeVideoControl();
          this.mPendingSwitchCameraId = -1;
          updateOnScreenIndicators();
          return;
        }
      while (!this.mActivity.mCameraDisabled);
      Util.showErrorAndFinish(this.mActivity, 2131361891);
    }
  }

  public void installIntentFilter()
  {
    IntentFilter localIntentFilter = new IntentFilter("android.intent.action.MEDIA_EJECT");
    localIntentFilter.addAction("android.intent.action.MEDIA_SCANNER_STARTED");
    localIntentFilter.addDataScheme("file");
    this.mReceiver = new MyBroadcastReceiver(null);
    this.mActivity.registerReceiver(this.mReceiver, localIntentFilter);
  }

  public boolean isRecording()
  {
    return this.mMediaRecorderRecording;
  }

  public boolean needsSwitcher()
  {
    return !this.mIsVideoCaptureIntent;
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    switch (paramInt1)
    {
    default:
      return;
    case 1000:
    }
    if (paramInt2 == -1)
    {
      this.mEffectUriFromGallery = paramIntent.getData().toString();
      Log.v("CAM_VideoModule", "Received URI from gallery: " + this.mEffectUriFromGallery);
      this.mResetEffect = false;
      return;
    }
    this.mEffectUriFromGallery = null;
    Log.w("CAM_VideoModule", "No URI from gallery");
    this.mResetEffect = true;
  }

  public boolean onBackPressed()
  {
    if (this.mPaused)
      return true;
    if (this.mMediaRecorderRecording)
    {
      onStopVideoRecording();
      return true;
    }
    if ((this.mPieRenderer != null) && (this.mPieRenderer.showsItems()))
    {
      this.mPieRenderer.hide();
      return true;
    }
    return removeTopLevelPopup();
  }

  public void onCameraPickerClicked(int paramInt)
  {
    if ((this.mPaused) || (this.mPendingSwitchCameraId != -1))
      return;
    this.mPendingSwitchCameraId = paramInt;
    if (ApiHelper.HAS_SURFACE_TEXTURE)
    {
      Log.d("CAM_VideoModule", "Start to copy texture.");
      ((CameraScreenNail)this.mActivity.mCameraScreenNail).copyTexture();
      this.mSwitchingCamera = true;
      return;
    }
    switchCamera();
  }

  public void onCancelBgTraining(View paramView)
  {
    this.mBgLearningMessageFrame.setVisibility(8);
    writeDefaultEffectToPrefs();
    onSharedPreferenceChanged();
  }

  public void onCaptureTextureCopied()
  {
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    setDisplayOrientation();
    LayoutInflater localLayoutInflater = this.mActivity.getLayoutInflater();
    ((ViewGroup)this.mRootView).removeAllViews();
    localLayoutInflater.inflate(2130968662, (ViewGroup)this.mRootView);
    initializeControlByIntent();
    initializeOverlay();
    initializeSurfaceView();
    initializeMiscControls();
    showTimeLapseUI(this.mCaptureTimeLapse);
    initializeVideoSnapshot();
    resizeForPreviewAspectRatio();
    showVideoSnapshotUI(false);
    initializeZoom();
    onFullScreenChanged(this.mActivity.isInCameraApp());
    updateOnScreenIndicators();
  }

  public void onEffectsError(Exception paramException, String paramString)
  {
    monitorenter;
    if (paramString != null);
    try
    {
      if (new File(paramString).exists());
      try
      {
        if (!Class.forName("android.filterpacks.videosink.MediaRecorderStopException").isInstance(paramException))
          break label62;
        Log.w("CAM_VideoModule", "Problem recoding video file. Removing incomplete file.");
        monitorexit;
        label62: return;
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        Log.w("CAM_VideoModule", localClassNotFoundException);
        throw new RuntimeException("Error during recording!", paramException);
      }
    }
    finally
    {
      monitorexit;
    }
  }

  public void onEffectsUpdate(int paramInt1, int paramInt2)
  {
    Log.v("CAM_VideoModule", "onEffectsUpdate. Effect Message = " + paramInt2);
    if (paramInt2 == 3)
    {
      this.mBgLearningMessageFrame.setVisibility(8);
      checkQualityAndStartPreview();
    }
    while (true)
    {
      if (this.mPaused)
      {
        label45: Log.v("CAM_VideoModule", "OnEffectsUpdate: closing effects if activity paused");
        closeEffects();
      }
      return;
      if (paramInt2 == 4)
      {
        if ((this.mEffectsDisplayResult) && (!addVideoToMediaStore()) && (this.mIsVideoCaptureIntent))
        {
          if (!this.mQuickCapture)
            break label128;
          doReturnToCaller(true);
        }
        while (true)
        {
          this.mEffectsDisplayResult = false;
          if (this.mPaused);
          closeVideoFileDescriptor();
          clearVideoNamer();
          break label45:
          label128: showAlert();
        }
      }
      if (paramInt2 == 5)
        this.mShutterButton.setEnabled(true);
      if (paramInt1 != 2)
        continue;
      switch (paramInt2)
      {
      default:
        break;
      case 0:
        this.mBgLearningMessageFrame.setVisibility(0);
        break;
      case 1:
      case 2:
      }
      this.mBgLearningMessageFrame.setVisibility(8);
    }
  }

  public void onError(MediaRecorder paramMediaRecorder, int paramInt1, int paramInt2)
  {
    Log.e("CAM_VideoModule", "MediaRecorder error. what=" + paramInt1 + ". extra=" + paramInt2);
    if (paramInt1 != 1)
      return;
    stopVideoRecording();
    this.mActivity.updateStorageSpaceAndHint();
  }

  public void onFullScreenChanged(boolean paramBoolean)
  {
    if (this.mGestures != null)
      this.mGestures.setEnabled(paramBoolean);
    if (this.mPopup != null)
      dismissPopup(false, paramBoolean);
    int j;
    if (this.mRenderOverlay != null)
    {
      RenderOverlay localRenderOverlay = this.mRenderOverlay;
      if (!paramBoolean)
        break label114;
      j = 0;
      label48: localRenderOverlay.setVisibility(j);
    }
    setShowMenu(paramBoolean);
    View localView;
    int i;
    if (this.mBlocker != null)
    {
      localView = this.mBlocker;
      i = 0;
      if (!paramBoolean)
        break label121;
    }
    while (true)
    {
      localView.setVisibility(i);
      if (!ApiHelper.HAS_SURFACE_TEXTURE)
        break;
      if (this.mActivity.mCameraScreenNail != null)
        ((CameraScreenNail)this.mActivity.mCameraScreenNail).setFullScreen(paramBoolean);
      return;
      label114: j = 8;
      break label48:
      label121: i = 8;
    }
    if (paramBoolean)
    {
      this.mPreviewSurfaceView.expand();
      return;
    }
    this.mPreviewSurfaceView.shrink();
  }

  public void onInfo(MediaRecorder paramMediaRecorder, int paramInt1, int paramInt2)
  {
    if (paramInt1 == 800)
      if (this.mMediaRecorderRecording)
        onStopVideoRecording();
    do
      return;
    while (paramInt1 != 801);
    if (this.mMediaRecorderRecording)
      onStopVideoRecording();
    Toast.makeText(this.mActivity, 2131361966, 1).show();
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (this.mPaused)
      return true;
    switch (paramInt)
    {
    default:
    case 27:
    case 23:
    case 82:
    }
    do
    {
      do
      {
        do
          return false;
        while (paramKeyEvent.getRepeatCount() != 0);
        this.mShutterButton.performClick();
        return true;
      }
      while (paramKeyEvent.getRepeatCount() != 0);
      this.mShutterButton.performClick();
      return true;
    }
    while (!this.mMediaRecorderRecording);
    return true;
  }

  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    switch (paramInt)
    {
    default:
      return false;
    case 27:
    }
    this.mShutterButton.setPressed(false);
    return true;
  }

  public void onOrientationChanged(int paramInt)
  {
    if (paramInt == -1);
    do
    {
      return;
      int i = Util.roundOrientation(paramInt, this.mOrientation);
      if (this.mOrientation != i)
      {
        this.mOrientation = i;
        if (effectsActive())
          this.mEffectsRecorder.setOrientationHint(this.mOrientation);
      }
      int j = (this.mOrientation + Util.getDisplayRotation(this.mActivity)) % 360;
      if ((this.mOrientationCompensation != j) || (this.mOrientationResetNeeded))
      {
        this.mOrientationCompensation = j;
        if (!this.mMediaRecorderRecording)
        {
          setOrientationIndicator(this.mOrientationCompensation, true);
          this.mOrientationResetNeeded = false;
        }
        setDisplayOrientation();
      }
      if (!this.mHandler.hasMessages(7))
        continue;
      this.mHandler.removeMessages(7);
      showTapToSnapshotToast();
    }
    while (this.mPopup == null);
    this.mPopup.setOrientation(this.mOrientationCompensation, true);
  }

  public void onPauseAfterSuper()
  {
  }

  public void onPauseBeforeSuper()
  {
    this.mPaused = true;
    if (this.mMediaRecorderRecording)
    {
      onStopVideoRecording();
      label16: if (!effectsActive())
        break label131;
      this.mEffectsRecorder.disconnectDisplay();
    }
    while (true)
    {
      releasePreviewResources();
      if (this.mReceiver != null)
      {
        this.mActivity.unregisterReceiver(this.mReceiver);
        this.mReceiver = null;
      }
      resetScreenOn();
      if (this.mLocationManager != null)
        this.mLocationManager.recordLocation(false);
      this.mHandler.removeMessages(3);
      this.mHandler.removeMessages(8);
      this.mHandler.removeMessages(9);
      this.mPendingSwitchCameraId = -1;
      this.mSwitchingCamera = false;
      return;
      closeCamera();
      if (!effectsActive());
      releaseMediaRecorder();
      break label16:
      label131: closeVideoFileDescriptor();
      clearVideoNamer();
    }
  }

  public void onPieClosed()
  {
    this.mActivity.setSwipingEnabled(true);
  }

  public void onPieOpened(int paramInt1, int paramInt2)
  {
    this.mActivity.cancelActivityTouchHandling();
    this.mActivity.setSwipingEnabled(false);
  }

  public void onPreviewTextureCopied()
  {
    this.mHandler.sendEmptyMessage(8);
  }

  public void onProtectiveCurtainClick(View paramView)
  {
  }

  public void onResumeAfterSuper()
  {
    if ((this.mActivity.mOpenCameraFail) || (this.mActivity.mCameraDisabled))
      return;
    this.mZoomValue = 0;
    showVideoSnapshotUI(false);
    if (!this.mPreviewing)
    {
      if (resetEffect())
        this.mBgLearningMessageFrame.setVisibility(8);
      openCamera();
      if (this.mActivity.mOpenCameraFail)
      {
        Util.showErrorAndFinish(this.mActivity, 2131361890);
        return;
      }
      if (this.mActivity.mCameraDisabled)
      {
        Util.showErrorAndFinish(this.mActivity, 2131361891);
        return;
      }
      readVideoPreferences();
      resizeForPreviewAspectRatio();
      startPreview();
    }
    initializeZoom();
    keepScreenOnAwhile();
    boolean bool = RecordLocationPreference.get(this.mPreferences, this.mContentResolver);
    this.mLocationManager.recordLocation(bool);
    if (this.mPreviewing)
    {
      this.mOnResumeTime = SystemClock.uptimeMillis();
      this.mHandler.sendEmptyMessageDelayed(3, 100L);
    }
    PopupManager.getInstance(this.mActivity).notifyShowPopup(null);
    this.mVideoNamer = new VideoNamer();
  }

  public void onResumeBeforeSuper()
  {
    this.mPaused = false;
  }

  public void onReviewCancelClicked(View paramView)
  {
    stopVideoRecording();
    doReturnToCaller(false);
  }

  public void onReviewDoneClicked(View paramView)
  {
    doReturnToCaller(true);
  }

  public void onReviewPlayClicked(View paramView)
  {
    startPlayVideoActivity();
  }

  public void onSharedPreferenceChanged()
  {
    if (this.mPaused)
      return;
    synchronized (this.mPreferences)
    {
      if (this.mActivity.mCameraDevice == null)
        return;
    }
    boolean bool = RecordLocationPreference.get(this.mPreferences, this.mContentResolver);
    this.mLocationManager.recordLocation(bool);
    if (updateEffectSelection())
    {
      monitorexit;
      return;
    }
    readVideoPreferences();
    showTimeLapseUI(this.mCaptureTimeLapse);
    Camera.Size localSize = this.mParameters.getPreviewSize();
    if ((localSize.width != this.mDesiredPreviewWidth) || (localSize.height != this.mDesiredPreviewHeight))
      if (!effectsActive())
      {
        stopPreview();
        label119: resizeForPreviewAspectRatio();
        startPreview();
      }
    while (true)
    {
      updateOnScreenIndicators();
      monitorexit;
      return;
      this.mEffectsRecorder.release();
      this.mEffectsRecorder = null;
      break label119:
      setCameraParameters();
    }
  }

  public void onShowSwitcherPopup()
  {
    if (!this.mPieRenderer.showsItems())
      return;
    this.mPieRenderer.hide();
  }

  public void onShutterButtonClick()
  {
    if ((collapseCameraControls()) || (this.mSwitchingCamera))
      return;
    boolean bool = this.mMediaRecorderRecording;
    if (bool)
      onStopVideoRecording();
    while (true)
    {
      this.mShutterButton.setEnabled(false);
      if ((!this.mIsVideoCaptureIntent) || (!bool));
      this.mHandler.sendEmptyMessageDelayed(6, 500L);
      return;
      startVideoRecording();
    }
  }

  public void onShutterButtonFocus(boolean paramBoolean)
  {
  }

  public void onSingleTapUp(View paramView, int paramInt1, int paramInt2)
  {
    if ((this.mMediaRecorderRecording) && (effectsActive()))
      new RotateTextToast(this.mActivity, 2131361990, this.mOrientation).show();
    do
    {
      do
        return;
      while ((this.mPaused) || (this.mSnapshotInProgress) || (effectsActive()));
      if (this.mMediaRecorderRecording)
        break label77;
    }
    while (this.mPopup == null);
    dismissPopup(true);
    return;
    label77: int i = Util.getJpegRotation(this.mCameraId, this.mOrientation);
    this.mParameters.setRotation(i);
    Location localLocation = this.mLocationManager.getCurrentLocation();
    Util.setGpsParameters(this.mParameters, localLocation);
    this.mActivity.mCameraDevice.setParameters(this.mParameters);
    Log.v("CAM_VideoModule", "Video snapshot start");
    this.mActivity.mCameraDevice.takePicture(null, null, null, new JpegPictureCallback(localLocation));
    showVideoSnapshotUI(true);
    this.mSnapshotInProgress = true;
  }

  public void onStop()
  {
  }

  public void onUserInteraction()
  {
    if ((this.mMediaRecorderRecording) || (this.mActivity.isFinishing()))
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

  void showVideoSnapshotUI(boolean paramBoolean)
  {
    label44: ShutterButton localShutterButton;
    if ((Util.isVideoSnapshotSupported(this.mParameters)) && (!this.mIsVideoCaptureIntent))
    {
      if ((!ApiHelper.HAS_SURFACE_TEXTURE) || (!paramBoolean))
        break label61;
      ((CameraScreenNail)this.mActivity.mCameraScreenNail).animateCapture(this.mDisplayRotation);
      localShutterButton = this.mShutterButton;
      if (paramBoolean)
        break label72;
    }
    for (boolean bool = true; ; bool = false)
    {
      localShutterButton.setEnabled(bool);
      return;
      label61: this.mPreviewFrameLayout.showBorder(paramBoolean);
      label72: break label44:
    }
  }

  public void updateCameraAppView()
  {
    if ((!this.mPreviewing) || (this.mParameters.getFlashMode() == null));
    do
    {
      return;
      if (this.mActivity.mShowCameraAppView)
        continue;
      if (this.mParameters.getFlashMode().equals("off"))
      {
        this.mRestoreFlash = false;
        return;
      }
      this.mRestoreFlash = true;
      setCameraParameters();
      return;
    }
    while (!this.mRestoreFlash);
    this.mRestoreFlash = false;
    setCameraParameters();
  }

  public boolean updateStorageHintOnResume()
  {
    return true;
  }

  protected class CameraOpenThread extends Thread
  {
    protected CameraOpenThread()
    {
    }

    public void run()
    {
      VideoModule.this.openCamera();
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
      Log.v("CAM_VideoModule", "onPictureTaken");
      VideoModule.access$2402(VideoModule.this, false);
      VideoModule.this.showVideoSnapshotUI(false);
      VideoModule.this.storeImage(paramArrayOfByte, this.mLocation);
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
        Log.v("CAM_VideoModule", "Unhandled message: " + paramMessage.what);
      case 6:
      case 4:
      case 5:
      case 3:
        do
        {
          return;
          VideoModule.this.mShutterButton.setEnabled(true);
          return;
          VideoModule.this.mActivity.getWindow().clearFlags(128);
          return;
          VideoModule.this.updateRecordingTime();
          return;
          if ((Util.getDisplayRotation(VideoModule.this.mActivity) == VideoModule.this.mDisplayRotation) || (VideoModule.this.mMediaRecorderRecording) || (VideoModule.this.mSwitchingCamera))
            continue;
          VideoModule.this.startPreview();
        }
        while (SystemClock.uptimeMillis() - VideoModule.this.mOnResumeTime >= 5000L);
        VideoModule.this.mHandler.sendEmptyMessageDelayed(3, 100L);
        return;
      case 7:
        VideoModule.this.showTapToSnapshotToast();
        return;
      case 8:
        VideoModule.this.switchCamera();
        return;
      case 9:
        ((CameraScreenNail)VideoModule.this.mActivity.mCameraScreenNail).animateSwitchCamera();
        VideoModule.access$702(VideoModule.this, false);
        return;
      case 10:
      }
      VideoModule.this.mPreviewSurfaceView.setVisibility(8);
    }
  }

  private class MyBroadcastReceiver extends BroadcastReceiver
  {
    private MyBroadcastReceiver()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent)
    {
      String str = paramIntent.getAction();
      if (str.equals("android.intent.action.MEDIA_EJECT"))
        VideoModule.this.stopVideoRecording();
      do
        return;
      while (!str.equals("android.intent.action.MEDIA_SCANNER_STARTED"));
      Toast.makeText(VideoModule.this.mActivity, VideoModule.this.mActivity.getResources().getString(2131361894), 1).show();
    }
  }

  private class SurfaceViewCallback
    implements SurfaceHolder.Callback
  {
    public SurfaceViewCallback()
    {
    }

    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3)
    {
      Log.v("CAM_VideoModule", "Surface changed. width=" + paramInt2 + ". height=" + paramInt3);
    }

    public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
    {
      Log.v("CAM_VideoModule", "Surface created");
      VideoModule.access$2602(VideoModule.this, true);
      if (VideoModule.this.mPaused);
      do
      {
        do
          return;
        while (ApiHelper.HAS_SURFACE_TEXTURE);
        VideoModule.this.mActivity.mCameraDevice.setPreviewDisplayAsync(VideoModule.this.mPreviewSurfaceView.getHolder());
      }
      while (VideoModule.this.mPreviewing);
      VideoModule.this.startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
    {
      Log.v("CAM_VideoModule", "Surface destroyed");
      VideoModule.access$2602(VideoModule.this, false);
      if (VideoModule.this.mPaused);
      do
        return;
      while (ApiHelper.HAS_SURFACE_TEXTURE);
      VideoModule.this.stopVideoRecording();
      VideoModule.this.stopPreview();
    }
  }

  private static class VideoNamer extends Thread
  {
    private boolean mRequestPending;
    private ContentResolver mResolver;
    private boolean mStop;
    private Uri mUri;
    private ContentValues mValues;

    public VideoNamer()
    {
      start();
    }

    private void cleanOldUri()
    {
      if (this.mUri == null)
        return;
      this.mResolver.delete(this.mUri, null, null);
      this.mUri = null;
    }

    private void generateUri()
    {
      Uri localUri = Uri.parse("content://media/external/video/media");
      this.mUri = this.mResolver.insert(localUri, this.mValues);
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

    public void prepareUri(ContentResolver paramContentResolver, ContentValues paramContentValues)
    {
      monitorenter;
      try
      {
        this.mRequestPending = true;
        this.mResolver = paramContentResolver;
        this.mValues = new ContentValues(paramContentValues);
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

  private class ZoomChangeListener
    implements ZoomRenderer.OnZoomChangedListener
  {
    private ZoomChangeListener()
    {
    }

    public void onZoomEnd()
    {
    }

    public void onZoomStart()
    {
    }

    public void onZoomValueChanged(int paramInt)
    {
      if (VideoModule.this.mPaused)
        return;
      VideoModule.access$1902(VideoModule.this, paramInt);
      VideoModule.this.mParameters.setZoom(VideoModule.this.mZoomValue);
      VideoModule.this.mActivity.mCameraDevice.setParametersAsync(VideoModule.this.mParameters);
      Camera.Parameters localParameters = VideoModule.this.mActivity.mCameraDevice.getParameters();
      VideoModule.this.mZoomRenderer.setZoomValue(((Integer)VideoModule.this.mZoomRatios.get(localParameters.getZoom())).intValue());
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.VideoModule
 * JD-Core Version:    0.5.4
 */