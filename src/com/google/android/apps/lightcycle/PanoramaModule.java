package com.google.android.apps.lightcycle;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images.Media;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import com.android.camera.CameraActivity;
import com.android.camera.CameraModule;
import com.android.camera.CameraScreenNail;
import com.android.camera.CameraSettings;
import com.android.camera.ComboPreferences;
import com.android.camera.RecordLocationPreference;
import com.android.camera.ShutterButton;
import com.android.camera.ShutterButton.OnShutterButtonListener;
import com.android.camera.Storage;
import com.android.camera.Util;
import com.android.camera.ui.RotateImageView;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.app.OrientationManager;
import com.android.gallery3d.app.StitchingProgressManager;
import com.android.gallery3d.ui.GLRoot;
import com.google.android.apps.lightcycle.camera.CameraApiProxy;
import com.google.android.apps.lightcycle.camera.CameraApiProxyAndroidImpl;
import com.google.android.apps.lightcycle.camera.CameraPreview;
import com.google.android.apps.lightcycle.camera.CameraUtility;
import com.google.android.apps.lightcycle.camera.NullSurfaceCameraPreview;
import com.google.android.apps.lightcycle.camera.TextureCameraPreview;
import com.google.android.apps.lightcycle.panorama.DeviceManager;
import com.google.android.apps.lightcycle.panorama.IncrementalAligner;
import com.google.android.apps.lightcycle.panorama.LightCycleNative;
import com.google.android.apps.lightcycle.panorama.LightCycleRenderer;
import com.google.android.apps.lightcycle.panorama.LightCycleView;
import com.google.android.apps.lightcycle.panorama.RenderedGui;
import com.google.android.apps.lightcycle.panorama.StitchingService;
import com.google.android.apps.lightcycle.panorama.StitchingServiceManager;
import com.google.android.apps.lightcycle.sensor.SensorReader;
import com.google.android.apps.lightcycle.storage.LocalSessionStorage;
import com.google.android.apps.lightcycle.storage.StorageManager;
import com.google.android.apps.lightcycle.storage.StorageManagerFactory;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.LG;
import com.google.android.apps.lightcycle.util.Size;
import com.google.android.apps.lightcycle.util.UiUtil;
import java.io.FileOutputStream;
import java.io.IOException;

public class PanoramaModule
  implements CameraModule
{
  private CameraActivity mActivity;
  private IncrementalAligner mAligner;
  private Callback<Void> mCaptureListener = new Callback()
  {
    public void onCallback(Void paramVoid)
    {
      PanoramaModule.access$008(PanoramaModule.this);
      PanoramaModule.this.mActivity.runOnUiThread(new Runnable()
      {
        public void run()
        {
          PanoramaModule.this.adjustSwitcherAndSwipe();
        }
      });
    }
  };
  private ViewGroup mContainer;
  private int mDisplayRotation;
  private boolean mFullScreen = true;
  private boolean mIsPaused = true;
  private LocalSessionStorage mLocalStorage;
  private LightCycleView mMainView = null;
  private int mNumberOfImages = 0;
  private int mOrientation;
  private int mOrientationCompensation;
  private Thread mPhotoSpherePreviewWriter;
  private ComboPreferences mPreferences;
  private LightCycleRenderer mRenderer;
  private View mRootView;
  private CameraScreenNail mScreenNail;
  private SensorReader mSensorReader = new SensorReader();
  private ShutterButton mShutterButton;
  private boolean mSitchingPaused = false;
  private StorageManager mStorageManager = StorageManagerFactory.getStorageManager();
  private RotateImageView mUndoButton;
  private Callback<Boolean> mUndoEnabledListener = new Callback()
  {
    public void onCallback(Boolean paramBoolean)
    {
      PanoramaModule.this.mActivity.runOnUiThread(new Runnable(paramBoolean)
      {
        public void run()
        {
          PanoramaModule.this.mUndoButton.setEnabled(this.val$enabled.booleanValue());
        }
      });
    }
  };
  private View.OnClickListener mUndoListener = new View.OnClickListener()
  {
    public void onClick(View paramView)
    {
      if (PanoramaModule.this.mNumberOfImages <= 0)
        return;
      PanoramaModule.access$010(PanoramaModule.this);
      PanoramaModule.this.mMainView.undoLastCapturedPhoto();
      PanoramaModule.this.mActivity.runOnUiThread(new Runnable()
      {
        public void run()
        {
          PanoramaModule.this.adjustSwitcherAndSwipe();
        }
      });
    }
  };
  private Callback<Boolean> mUndoVisibilityListener = new Callback()
  {
    public void onCallback(Boolean paramBoolean)
    {
      PanoramaModule.this.mActivity.runOnUiThread(new Runnable(paramBoolean)
      {
        public void run()
        {
          RotateImageView localRotateImageView = PanoramaModule.this.mUndoButton;
          if (this.val$visible.booleanValue());
          for (int i = 0; ; i = 8)
          {
            localRotateImageView.setVisibility(i);
            return;
          }
        }
      });
    }
  };

  static
  {
    CameraApiProxy.setActiveProxy(new CameraApiProxyAndroidImpl());
    LightCycleApp.initLightCycleNative();
  }

  private void adjustSwitcherAndSwipe()
  {
    boolean bool1 = true;
    if (!this.mFullScreen)
      return;
    boolean bool2;
    label19: CameraActivity localCameraActivity;
    if (this.mNumberOfImages != 0)
    {
      bool2 = bool1;
      localCameraActivity = this.mActivity;
      if (bool2)
        break label68;
    }
    while (true)
    {
      localCameraActivity.setSwipingEnabled(bool1);
      if (!bool2)
        break;
      this.mActivity.hideSwitcher();
      this.mShutterButton.setVisibility(0);
      this.mActivity.getOrientationManager().lockOrientation();
      return;
      bool2 = false;
      break label19:
      label68: bool1 = false;
    }
    this.mActivity.showSwitcher();
    this.mShutterButton.setVisibility(8);
    this.mActivity.getOrientationManager().unlockOrientation();
  }

  private void applyPreferences()
  {
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mActivity.getBaseContext());
    boolean bool1 = localSharedPreferences.getBoolean("useFastShutter", true);
    if (bool1)
      this.mMainView.getCameraPreview().setFastShutter(bool1);
    this.mSensorReader.enableEkf(localSharedPreferences.getBoolean("useGyro", true));
    boolean bool2 = localSharedPreferences.getBoolean("displayLiveImage", false);
    this.mMainView.setLiveImageDisplay(bool2);
    LightCycleNative.AllowFastMotion(localSharedPreferences.getBoolean("allowFastMotion", false));
    boolean bool3 = RecordLocationPreference.get(this.mPreferences, this.mActivity.getContentResolver());
    this.mMainView.setLocationProviderEnabled(bool3);
  }

  private void displayErrorAndExit(String paramString)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.mActivity);
    localBuilder.setMessage(paramString).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        PanoramaModule.this.mActivity.finish();
      }
    });
    localBuilder.create().show();
  }

  private void initButtons()
  {
    if (this.mUndoButton != null)
    {
      this.mContainer.removeView(this.mUndoButton);
      this.mActivity.getLayoutInflater().inflate(2130968626, this.mContainer);
    }
    this.mUndoButton = ((RotateImageView)this.mContainer.findViewById(2131558544));
    this.mUndoButton.enableFilter(false);
    this.mUndoButton.setOnClickListener(this.mUndoListener);
    this.mShutterButton = this.mActivity.getShutterButton();
    this.mShutterButton.setImageResource(2130837546);
    this.mShutterButton.setOnShutterButtonListener(new ShutterButton.OnShutterButtonListener()
    {
      public void onShutterButtonClick()
      {
        PanoramaModule.this.onDoneButtonPressed();
      }

      public void onShutterButtonFocus(boolean paramBoolean)
      {
      }
    });
  }

  private void pauseCapture()
  {
    this.mMainView.stopCamera();
    this.mSensorReader.stop();
  }

  private void setDisplayRotation()
  {
    this.mDisplayRotation = Util.getDisplayRotation(this.mActivity);
    this.mActivity.getGLRoot().requestLayoutContentPane();
  }

  private void startCapture()
  {
    this.mNumberOfImages = 0;
    CameraUtility localCameraUtility = LightCycleApp.getCameraUtility();
    Object localObject;
    if (Build.VERSION.SDK_INT < 11)
      localObject = new NullSurfaceCameraPreview(localCameraUtility);
    while (true)
    {
      boolean bool = PreferenceManager.getDefaultSharedPreferences(this.mActivity.getBaseContext()).getBoolean("useRealtimeAlignment", false);
      this.mAligner = new IncrementalAligner(bool);
      RenderedGui localRenderedGui = new RenderedGui();
      localRenderedGui.setShowOwnDoneButton(false);
      localRenderedGui.setShowOwnUndoButton(false);
      localRenderedGui.setUndoButtonStatusListener(this.mUndoEnabledListener);
      localRenderedGui.setUndoButtonVisibilityListener(this.mUndoVisibilityListener);
      try
      {
        this.mRenderer = new LightCycleRenderer(this.mActivity, localRenderedGui, bool);
        this.mSensorReader.start(this.mActivity);
        this.mLocalStorage = this.mStorageManager.getLocalSessionStorage();
        LG.d("storage : " + this.mLocalStorage.metadataFilePath + " " + this.mLocalStorage.mosaicFilePath + " " + this.mLocalStorage.orientationFilePath + " " + this.mLocalStorage.sessionDir + " " + this.mLocalStorage.sessionId + " " + this.mLocalStorage.thumbnailFilePath);
        this.mMainView = new LightCycleView(this.mActivity, (CameraPreview)localObject, this.mSensorReader, this.mLocalStorage, this.mAligner, this.mRenderer, this.mScreenNail.getSurfaceTexture());
        this.mMainView.setOnPhotoTakenCallback(new Callback()
        {
          public void onCallback(Void paramVoid)
          {
            if (PanoramaModule.this.mSitchingPaused)
              return;
            PanoramaModule.access$502(PanoramaModule.this, true);
            LocalBroadcastManager.getInstance(PanoramaModule.this.mActivity).sendBroadcast(new Intent("com.google.android.apps.lightcycle.panorama.PAUSE"));
          }
        });
        this.mMainView.setOnPhotoTakenCallback(this.mCaptureListener);
        if (Build.VERSION.SDK_INT < 11);
        Size localSize = ((CameraPreview)localObject).initCamera(this.mMainView.getPreviewCallback(), 320, 240, true);
        this.mMainView.setFrameDimensions(localSize.width, localSize.height);
        this.mMainView.startCamera();
        applyPreferences();
        Camera.Size localSize1 = ((CameraPreview)localObject).getPhotoSize();
        this.mAligner.start(new Size(localSize1.width, localSize1.height));
        int i = this.mScreenNail.getWidth();
        int j = this.mScreenNail.getHeight();
        ((ViewGroup)this.mRootView).addView(this.mMainView, 0, new ViewGroup.LayoutParams(i, j));
        UiUtil.switchSystemUiToLightsOut(this.mActivity.getWindow());
        this.mUndoButton.setVisibility(8);
        adjustSwitcherAndSwipe();
        return;
        localObject = new TextureCameraPreview(localCameraUtility);
      }
      catch (Exception localException)
      {
        Log.e("LightCycle", "Error creating PanoRenderer.", localException);
      }
    }
  }

  private void startStitchService(LocalSessionStorage paramLocalSessionStorage)
  {
    LightCycleNative.CleanUp();
    this.mStorageManager.addSessionData(paramLocalSessionStorage);
    StitchingServiceManager.getStitchingServiceManager(this.mActivity).newTask(paramLocalSessionStorage);
  }

  private void stopCapture()
  {
    this.mSitchingPaused = false;
    LocalBroadcastManager.getInstance(this.mActivity).sendBroadcast(new Intent("com.google.android.apps.lightcycle.panorama.RESUME"));
    if (this.mMainView != null)
    {
      this.mMainView.onPause();
      ((ViewGroup)this.mRootView).removeView(this.mMainView);
      this.mMainView.stopCamera();
    }
    this.mMainView = null;
    this.mNumberOfImages = 0;
    adjustSwitcherAndSwipe();
  }

  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mMainView != null)
      this.mMainView.setEnableTouchEvents(false);
    boolean bool = this.mActivity.superDispatchTouchEvent(paramMotionEvent);
    if (this.mMainView != null)
    {
      this.mMainView.setEnableTouchEvents(true);
      if ((!this.mMainView.dispatchTouchEvent(paramMotionEvent)) && (!bool))
        break label58;
      bool = true;
    }
    return bool;
    label58: return false;
  }

  public void init(CameraActivity paramCameraActivity, View paramView, boolean paramBoolean)
  {
    this.mActivity = paramCameraActivity;
    this.mRootView = paramView;
    this.mPreferences = new ComboPreferences(this.mActivity);
    CameraSettings.upgradeGlobalPreferences(this.mPreferences.getGlobal());
    this.mActivity.getLayoutInflater().inflate(2130968625, (ViewGroup)this.mRootView);
    this.mContainer = ((ViewGroup)this.mRootView.findViewById(2131558532));
    this.mScreenNail = ((CameraScreenNail)this.mActivity.createCameraScreenNail(true));
    int i = this.mRootView.getWidth();
    int j = this.mRootView.getHeight();
    if (Util.getDisplayRotation(this.mActivity) % 180 != 0)
    {
      int k = i;
      i = j;
      j = k;
    }
    this.mScreenNail.setSize(i, j);
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
    return false;
  }

  public void onCaptureTextureCopied()
  {
    if (this.mMainView == null)
      return;
    this.mMainView.clearRendering();
    this.mAligner.shutdown(new Callback()
    {
      public void onCallback(Void paramVoid)
      {
        if ((PanoramaModule.this.mAligner.isRealtimeAlignmentEnabled()) || (PanoramaModule.this.mAligner.isExtractFeaturesAndThumbnailEnabled()));
        try
        {
          PanoramaModule.this.mPhotoSpherePreviewWriter.join();
          label36: LightCycleNative.PreviewStitch(PanoramaModule.this.mLocalStorage.mosaicFilePath);
          if (PanoramaModule.this.mLocalStorage.imageUri == null)
          {
            Log.w("LightCycle", "Prepared preview doesn't exist");
            PanoramaModule.this.mActivity.runOnUiThread(new Runnable()
            {
              public void run()
              {
                PanoramaModule.this.startStitchService(PanoramaModule.this.mLocalStorage);
                if (PanoramaModule.this.mIsPaused)
                  return;
                PanoramaModule.this.stopCapture();
                PanoramaModule.this.startCapture();
              }
            });
            return;
          }
          ((GalleryApp)PanoramaModule.this.mActivity.getApplication()).getStitchingProgressManager().clearCachedThumbnails(PanoramaModule.this.mLocalStorage.imageUri);
          ContentValues localContentValues = StitchingService.createImageContentValues(PanoramaModule.this.mLocalStorage.mosaicFilePath);
          localContentValues.remove("mime_type");
          localContentValues.remove("datetaken");
          PanoramaModule.this.mActivity.getContentResolver().update(PanoramaModule.this.mLocalStorage.imageUri, localContentValues, null, null);
        }
        catch (InterruptedException localInterruptedException)
        {
          break label36:
        }
      }
    });
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    initButtons();
    setDisplayRotation();
    adjustSwitcherAndSwipe();
  }

  public void onDoneButtonPressed()
  {
    pauseCapture();
    this.mNumberOfImages = 0;
    adjustSwitcherAndSwipe();
    this.mScreenNail.animateCapture(this.mDisplayRotation);
    this.mPhotoSpherePreviewWriter = new Thread()
    {
      public void run()
      {
        Bitmap localBitmap = ((BitmapDrawable)PanoramaModule.this.mActivity.getResources().getDrawable(2130837781)).getBitmap();
        Object localObject = null;
        FileOutputStream localFileOutputStream;
        try
        {
          localFileOutputStream = new FileOutputStream(PanoramaModule.this.mLocalStorage.mosaicFilePath);
        }
        catch (IOException localIOException3)
        {
          try
          {
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, localFileOutputStream);
            localFileOutputStream.close();
            ContentValues localContentValues = StitchingService.createImageContentValues(PanoramaModule.this.mLocalStorage.mosaicFilePath);
            localContentValues.put("mime_type", "application/stitching-preview");
            Uri localUri = PanoramaModule.this.mActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);
            PanoramaModule.this.mLocalStorage.imageUri = localUri;
            StitchingServiceManager.getStitchingServiceManager(PanoramaModule.this.mActivity).onStitchingQueued(PanoramaModule.this.mLocalStorage);
            do
            {
              return;
              localIOException3 = localIOException3;
              Log.e("LightCycle", "Could not write image: " + PanoramaModule.this.mLocalStorage.mosaicFilePath);
            }
            while (localObject == null);
            try
            {
              localObject.close();
              return;
            }
            catch (IOException localIOException2)
            {
              Log.e("LightCycle", "Could not close write image: " + PanoramaModule.this.mLocalStorage.mosaicFilePath);
              return;
            }
          }
          catch (IOException localIOException1)
          {
            localObject = localFileOutputStream;
          }
        }
      }
    };
    this.mPhotoSpherePreviewWriter.start();
  }

  public void onFullScreenChanged(boolean paramBoolean)
  {
    this.mFullScreen = paramBoolean;
    this.mRootView.setKeepScreenOn(paramBoolean);
    this.mScreenNail.setFullScreen(paramBoolean);
    LightCycleRenderer localLightCycleRenderer;
    if (this.mRenderer != null)
    {
      localLightCycleRenderer = this.mRenderer;
      if (paramBoolean)
        break label49;
    }
    for (boolean bool = true; ; bool = false)
    {
      localLightCycleRenderer.setDisablePhotoTaking(bool);
      adjustSwitcherAndSwipe();
      label49: return;
    }
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt == 23) && (DeviceManager.isWingman()))
    {
      startStitchService(this.mLocalStorage);
      return true;
    }
    return false;
  }

  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }

  public void onOrientationChanged(int paramInt)
  {
    this.mOrientation = Util.roundOrientation(paramInt, this.mOrientation);
    int i = (this.mOrientation + Util.getDisplayRotation(this.mActivity)) % 360;
    if (this.mOrientationCompensation == i)
      return;
    this.mOrientationCompensation = i;
    if (this.mUndoButton != null)
      this.mUndoButton.setOrientation(this.mOrientationCompensation, true);
    setDisplayRotation();
  }

  public void onPauseAfterSuper()
  {
  }

  public void onPauseBeforeSuper()
  {
    this.mIsPaused = true;
    this.mShutterButton.setOnShutterButtonListener(null);
    if (this.mLocalStorage != null)
      this.mStorageManager.addSessionData(this.mLocalStorage);
    stopCapture();
    this.mSensorReader.stop();
    if ((this.mAligner != null) && (!this.mAligner.isInterrupted()))
      this.mAligner.interrupt();
    this.mRootView.setKeepScreenOn(false);
    this.mScreenNail.releaseSurfaceTexture();
  }

  public void onPreviewTextureCopied()
  {
  }

  public void onResumeAfterSuper()
  {
    this.mIsPaused = false;
    initButtons();
    this.mScreenNail.acquireSurfaceTexture();
    this.mActivity.notifyScreenNailChanged();
    String str = Build.MODEL + " (" + Build.MANUFACTURER + ")";
    LG.d("Model is: " + str);
    if (!DeviceManager.isDeviceSupported())
    {
      displayErrorAndExit("Sorry, your device is not yet supported. Model : " + str);
      return;
    }
    if (!LightCycleApp.getCameraUtility().hasBackFacingCamera())
    {
      displayErrorAndExit("Sorry, your device does not have a back facing camera");
      return;
    }
    Process.setThreadPriority(-19);
    this.mRootView.setKeepScreenOn(true);
    this.mStorageManager.init(this.mActivity);
    this.mStorageManager.setPanoramaDestination(Storage.DIRECTORY);
    setDisplayRotation();
    startCapture();
  }

  public void onResumeBeforeSuper()
  {
  }

  public void onShowSwitcherPopup()
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
  }

  public void updateCameraAppView()
  {
  }

  public boolean updateStorageHintOnResume()
  {
    return false;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.PanoramaModule
 * JD-Core Version:    0.5.4
 */