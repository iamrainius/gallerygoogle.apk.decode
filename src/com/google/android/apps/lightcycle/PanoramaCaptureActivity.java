package com.google.android.apps.lightcycle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera.Size;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import com.google.android.apps.lightcycle.camera.CameraPreview;
import com.google.android.apps.lightcycle.camera.CameraUtility;
import com.google.android.apps.lightcycle.camera.NullSurfaceCameraPreview;
import com.google.android.apps.lightcycle.camera.TextureCameraPreview;
import com.google.android.apps.lightcycle.glass.FullScreenProgressNotification;
import com.google.android.apps.lightcycle.panorama.DeviceManager;
import com.google.android.apps.lightcycle.panorama.IncrementalAligner;
import com.google.android.apps.lightcycle.panorama.LightCycleNative;
import com.google.android.apps.lightcycle.panorama.LightCycleRenderer;
import com.google.android.apps.lightcycle.panorama.LightCycleView;
import com.google.android.apps.lightcycle.panorama.MessageSender.MessageSubscriber;
import com.google.android.apps.lightcycle.panorama.RenderedGui;
import com.google.android.apps.lightcycle.panorama.StitchingServiceManager;
import com.google.android.apps.lightcycle.panorama.StitchingServiceManager.StitchingResultCallback;
import com.google.android.apps.lightcycle.sensor.SensorReader;
import com.google.android.apps.lightcycle.storage.LocalSessionStorage;
import com.google.android.apps.lightcycle.storage.StorageManager;
import com.google.android.apps.lightcycle.storage.StorageManagerFactory;
import com.google.android.apps.lightcycle.util.AnalyticsHelper;
import com.google.android.apps.lightcycle.util.AnalyticsHelper.Page;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.LG;
import com.google.android.apps.lightcycle.util.LightCycleCaptureEventListener;
import com.google.android.apps.lightcycle.util.Size;
import com.google.android.apps.lightcycle.util.UiUtil;

public class PanoramaCaptureActivity extends Activity
{
  private IncrementalAligner aligner;
  private AnalyticsHelper analyticsHelper;
  private LightCycleCaptureEventListener captureEventListener;
  private long captureStartTimeMs;
  private LocalSessionStorage localStorage;
  private LightCycleView mainView = null;
  private RenderedGui renderedGui;
  private SensorReader sensorReader = new SensorReader();
  private boolean showOwnDoneButton = true;
  private boolean showOwnUndoButton = true;
  private StorageManager storageManager = StorageManagerFactory.getStorageManager();
  private PowerManager.WakeLock wakeLock = null;

  private void applyPreferences()
  {
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    boolean bool1 = localSharedPreferences.getBoolean("useFastShutter", true);
    if (bool1)
      this.mainView.getCameraPreview().setFastShutter(bool1);
    this.sensorReader.enableEkf(localSharedPreferences.getBoolean("useGyro", true));
    this.mainView.setLiveImageDisplay(localSharedPreferences.getBoolean("displayLiveImage", false));
    LightCycleNative.AllowFastMotion(localSharedPreferences.getBoolean("allowFastMotion", false));
    boolean bool2 = localSharedPreferences.getBoolean("enableLocationProvider", true);
    this.mainView.setLocationProviderEnabled(bool2);
  }

  private void displayErrorAndExit(String paramString)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
    localBuilder.setMessage(paramString).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        PanoramaCaptureActivity.this.finish();
      }
    });
    localBuilder.create().show();
  }

  private void endCapture()
  {
    logEndCaptureToAnalytics();
    this.mainView.stopCamera();
    this.sensorReader.stop();
    try
    {
      Thread.sleep(100L);
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
    }
  }

  private void logEndCaptureToAnalytics()
  {
    this.analyticsHelper.trackPage(AnalyticsHelper.Page.END_CAPTURE);
    this.analyticsHelper.trackEvent("Capture", "Session", "NumPhotos", this.mainView.getTotalPhotos());
    long l = SystemClock.uptimeMillis() - this.captureStartTimeMs;
    this.analyticsHelper.trackEvent("Capture", "Session", "CaptureTime", (int)(l / 1000L));
    int i = Build.VERSION.SDK_INT;
    this.analyticsHelper.trackEvent("Capture", "Session", "AndroidVersion", i);
  }

  private void startStitchService(LocalSessionStorage paramLocalSessionStorage)
  {
    LightCycleNative.CleanUp();
    this.storageManager.addSessionData(paramLocalSessionStorage);
    StitchingServiceManager localStitchingServiceManager = StitchingServiceManager.getStitchingServiceManager(this);
    if (DeviceManager.isWingman())
      localStitchingServiceManager.addStitchingResultCallback(new StitchingServiceManager.StitchingResultCallback()
      {
        public void onResult(String paramString, Uri paramUri)
        {
          Intent localIntent = new Intent(PanoramaCaptureActivity.this, PanoramaViewActivity.class);
          localIntent.putExtra("filename", paramString);
          PanoramaCaptureActivity.this.startActivity(localIntent);
        }
      });
    localStitchingServiceManager.newTask(paramLocalSessionStorage);
    if (DeviceManager.isWingman())
      startActivity(new Intent(this, FullScreenProgressNotification.class));
    while (true)
    {
      finish();
      return;
      Toast.makeText(this, getText(2131361801), 1).show();
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.analyticsHelper = AnalyticsHelper.getInstance(this);
  }

  public void onDoneButtonPressed(Callback<Void> paramCallback)
  {
    this.mainView.clearRendering();
    endCapture();
    this.aligner.shutdown(new Callback(paramCallback)
    {
      public void onCallback(Void paramVoid)
      {
        if ((PanoramaCaptureActivity.this.aligner.isRealtimeAlignmentEnabled()) || (PanoramaCaptureActivity.this.aligner.isExtractFeaturesAndThumbnailEnabled()))
        {
          String str = PanoramaCaptureActivity.this.localStorage.mosaicFilePath;
          LG.d("Creating preview stitch into file: " + str);
          LightCycleNative.PreviewStitch(str);
          MediaScannerConnection.scanFile(PanoramaCaptureActivity.this, new String[] { str }, new String[] { "image/jpeg" }, null);
        }
        PanoramaCaptureActivity.this.runOnUiThread(new Runnable()
        {
          public void run()
          {
            PanoramaCaptureActivity.this.startStitchService(PanoramaCaptureActivity.this.localStorage);
          }
        });
        if (this.val$stitchingStartedCallback == null)
          return;
        this.val$stitchingStartedCallback.onCallback(null);
      }
    });
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt == 23) && (DeviceManager.isWingman()))
    {
      endCapture();
      startStitchService(this.localStorage);
      return true;
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }

  public void onPause()
  {
    super.onPause();
    if (this.localStorage != null)
      this.storageManager.addSessionData(this.localStorage);
    if (this.mainView != null)
      this.mainView.stopCamera();
    this.mainView = null;
    if (this.sensorReader != null)
      this.sensorReader.stop();
    if ((this.aligner != null) && (!this.aligner.isInterrupted()))
      this.aligner.interrupt();
    if (this.wakeLock != null)
      this.wakeLock.release();
    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("com.google.android.apps.lightcycle.panorama.RESUME"));
  }

  public void onResume()
  {
    super.onResume();
    UiUtil.switchSystemUiToLightsOut(getWindow());
    this.sensorReader.start(this);
    String str1 = Build.MODEL + " (" + Build.MANUFACTURER + ")";
    LG.d("Model is: " + str1);
    if (!DeviceManager.isDeviceSupported())
    {
      this.analyticsHelper.trackEvent("Capture", "UnsupportedDevice", str1, 1);
      displayErrorAndExit("Sorry, your device is not yet supported. Model : " + str1);
      return;
    }
    Process.setThreadPriority(-19);
    this.wakeLock = ((PowerManager)getSystemService("power")).newWakeLock(536870922, "LightCycle");
    this.wakeLock.acquire();
    this.storageManager.init(this);
    String str2 = getIntent().getStringExtra("output_dir");
    if (str2 != null)
    {
      LG.d("Setting the panorama destination to : " + str2);
      if (!this.storageManager.setPanoramaDestination(str2))
        Log.e("LightCycle", "Unable to set the panorama destination directory : " + str2);
    }
    this.localStorage = this.storageManager.getLocalSessionStorage();
    LG.d("storage : " + this.localStorage.metadataFilePath + " " + this.localStorage.mosaicFilePath + " " + this.localStorage.orientationFilePath + " " + this.localStorage.sessionDir + " " + this.localStorage.sessionId + " " + this.localStorage.thumbnailFilePath);
    CameraUtility localCameraUtility = LightCycleApp.getCameraUtility();
    if (!localCameraUtility.hasBackFacingCamera())
    {
      displayErrorAndExit("Sorry, your device does not have a back facing camera");
      return;
    }
    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("com.google.android.apps.lightcycle.panorama.PAUSE"));
    Object localObject;
    if (Build.VERSION.SDK_INT < 11)
      localObject = new NullSurfaceCameraPreview(localCameraUtility);
    while (true)
    {
      this.renderedGui = new RenderedGui();
      this.renderedGui.setShowOwnDoneButton(this.showOwnDoneButton);
      this.renderedGui.setDoneButtonVisibilityListener(new Callback()
      {
        public void onCallback(Boolean paramBoolean)
        {
          if (PanoramaCaptureActivity.this.captureEventListener == null)
            return;
          PanoramaCaptureActivity.this.captureEventListener.onDoneButtonVisibilityChanged(paramBoolean.booleanValue());
        }
      });
      this.renderedGui.setShowOwnUndoButton(this.showOwnUndoButton);
      this.renderedGui.setUndoButtonVisibilityListener(new Callback()
      {
        public void onCallback(Boolean paramBoolean)
        {
          if (PanoramaCaptureActivity.this.captureEventListener == null)
            return;
          PanoramaCaptureActivity.this.captureEventListener.onUndoButtonVisibilityChanged(paramBoolean.booleanValue());
        }
      });
      this.renderedGui.setUndoButtonStatusListener(new Callback()
      {
        public void onCallback(Boolean paramBoolean)
        {
          if (PanoramaCaptureActivity.this.captureEventListener == null)
            return;
          PanoramaCaptureActivity.this.captureEventListener.onUndoButtonStatusChanged(paramBoolean.booleanValue());
        }
      });
      boolean bool = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("useRealtimeAlignment", false);
      try
      {
        LightCycleRenderer localLightCycleRenderer = new LightCycleRenderer(this, this.renderedGui, bool);
        this.aligner = new IncrementalAligner(bool);
        this.mainView = new LightCycleView(this, (CameraPreview)localObject, this.sensorReader, this.localStorage, this.aligner, localLightCycleRenderer);
        this.mainView.setZOrderOnTop(true);
        setContentView(this.mainView);
        this.mainView.registerMessageSink(new MessageSender.MessageSubscriber()
        {
          public void message(int paramInt, float paramFloat, String paramString)
          {
            if (paramInt != 1)
              return;
            PanoramaCaptureActivity.this.onDoneButtonPressed(null);
          }
        });
        if (Build.VERSION.SDK_INT < 11);
        Size localSize = ((CameraPreview)localObject).initCamera(this.mainView.getPreviewCallback(), 320, 240, true);
        this.mainView.setFrameDimensions(localSize.width, localSize.height);
        this.mainView.startCamera();
        Camera.Size localSize1 = ((CameraPreview)localObject).getPhotoSize();
        this.aligner.start(new Size(localSize1.width, localSize1.height));
        applyPreferences();
        this.analyticsHelper.trackPage(AnalyticsHelper.Page.BEGIN_CAPTURE);
        this.captureStartTimeMs = SystemClock.uptimeMillis();
        UiUtil.lockCurrentScreenOrientation(this);
        this.mainView.setOnPhotoTakenCallback(new Callback()
        {
          public void onCallback(Void paramVoid)
          {
            if (PanoramaCaptureActivity.this.captureEventListener == null)
              return;
            PanoramaCaptureActivity.this.captureEventListener.onPhotoTaken();
          }
        });
        return;
        localObject = new TextureCameraPreview(localCameraUtility);
      }
      catch (Exception localException)
      {
        Log.e("LightCycle", "Error creating PanoRenderer.", localException);
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.PanoramaCaptureActivity
 * JD-Core Version:    0.5.4
 */