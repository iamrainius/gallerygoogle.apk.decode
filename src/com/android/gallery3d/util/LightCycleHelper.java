package com.android.gallery3d.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.android.camera.CameraModule;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.app.StitchingProgressManager;
import com.google.android.apps.lightcycle.LightCycleApp;
import com.google.android.apps.lightcycle.PanoramaModule;
import com.google.android.apps.lightcycle.util.PanoMetadata;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.panorama.PanoramaClient;
import com.google.android.gms.panorama.PanoramaClient.OnPanoramaInfoLoadedListener;

public class LightCycleHelper
{
  public static final PanoramaMetadata NOT_PANORAMA;
  private static Object mInitializationLock;
  private static boolean mIsNativeInitialized = false;

  static
  {
    mInitializationLock = new Object();
    NOT_PANORAMA = new PanoramaMetadata();
  }

  public static CameraModule createPanoramaModule()
  {
    return new PanoramaModule();
  }

  public static StitchingProgressManager createStitchingManagerInstance(GalleryApp paramGalleryApp)
  {
    return new StitchingProgressManager(paramGalleryApp);
  }

  public static PanoramaMetadata getPanoramaMetadata(Context paramContext, Uri paramUri)
  {
    ContentResolver localContentResolver = paramContext.getContentResolver();
    if ("content".equals(paramUri.getScheme()));
    for (String str = getPathFromURI(localContentResolver, paramUri); str == null; str = paramUri.getPath())
      return NOT_PANORAMA;
    PanoMetadata localPanoMetadata = PanoMetadata.parse(str);
    if (localPanoMetadata == null)
      return NOT_PANORAMA;
    return new PanoramaMetadata(localPanoMetadata);
  }

  private static String getPathFromURI(ContentResolver paramContentResolver, Uri paramUri)
  {
    Cursor localCursor = paramContentResolver.query(paramUri, new String[] { "_data" }, null, null, null);
    if (localCursor == null)
      return null;
    try
    {
      int i = localCursor.getColumnIndexOrThrow("_data");
      boolean bool = localCursor.moveToFirst();
      if (!bool)
        return null;
      String str = localCursor.getString(i);
      return str;
    }
    finally
    {
      localCursor.close();
    }
  }

  public static boolean hasLightCycleCapture(Context paramContext)
  {
    return true;
  }

  public static void initNative()
  {
    synchronized (mInitializationLock)
    {
      if (!mIsNativeInitialized)
      {
        LightCycleApp.initLightCycleNative();
        mIsNativeInitialized = true;
      }
      return;
    }
  }

  public static class PanoramaMetadata
  {
    public final boolean mIsPanorama360;
    public final boolean mUsePanoramaViewer;

    public PanoramaMetadata()
    {
      this.mUsePanoramaViewer = false;
      this.mIsPanorama360 = false;
    }

    public PanoramaMetadata(PanoMetadata paramPanoMetadata)
    {
      float f1 = 360.0F * paramPanoMetadata.croppedAreaWidth / paramPanoMetadata.fullPanoWidth;
      float f2 = 180.0F * paramPanoMetadata.croppedAreaHeight / paramPanoMetadata.fullPanoHeight;
      int j;
      if ((f1 >= 70.0F) || (f2 >= 70.0F))
      {
        j = i;
        label55: this.mUsePanoramaViewer = j;
        if (f1 != 360.0F)
          break label80;
      }
      while (true)
      {
        this.mIsPanorama360 = i;
        return;
        j = 0;
        break label55:
        label80: i = 0;
      }
    }
  }

  public static class PanoramaViewHelper
    implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, PanoramaClient.OnPanoramaInfoLoadedListener
  {
    private final Activity mActivity;
    private final boolean mGooglePlayServicesAvailable;
    private PanoramaClient mPanoramaClient;

    public PanoramaViewHelper(Activity paramActivity)
    {
      this.mActivity = paramActivity;
      if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.mActivity) == 0);
      for (int i = 1; ; i = 0)
      {
        this.mGooglePlayServicesAvailable = i;
        return;
      }
    }

    public void onConnected()
    {
    }

    public void onConnectionFailed(ConnectionResult paramConnectionResult)
    {
      Log.e("LightCycleHelper", "Connection failed: " + paramConnectionResult);
      if (paramConnectionResult.hasResolution());
      try
      {
        paramConnectionResult.startResolutionForResult(this.mActivity, 9000);
        return;
      }
      catch (IntentSender.SendIntentException localSendIntentException)
      {
        Log.e("LightCycleHelper", "Could not start resolution", localSendIntentException);
      }
    }

    public void onCreate()
    {
      this.mPanoramaClient = new PanoramaClient(this.mActivity, this, this);
    }

    public void onDisconnected()
    {
    }

    public void onPanoramaInfoLoaded(ConnectionResult paramConnectionResult, int paramInt, Intent paramIntent)
    {
      if (paramConnectionResult.getErrorCode() == 0)
      {
        if (paramIntent != null)
          this.mActivity.startActivity(paramIntent);
        return;
      }
      Log.e("LightCycleHelper", "Could not load panorama info: " + paramConnectionResult);
    }

    public void onStart()
    {
      this.mPanoramaClient.connect();
    }

    public void onStop()
    {
      if (this.mPanoramaClient == null)
        return;
      this.mPanoramaClient.disconnect();
    }

    public void showPanorama(Uri paramUri)
    {
      if ((!this.mGooglePlayServicesAvailable) || (this.mPanoramaClient == null) || (!this.mPanoramaClient.isConnected()))
      {
        Log.d("LightCycleHelper", "PanoramaClient not available.");
        return;
      }
      this.mPanoramaClient.loadPanoramaInfoAndGrantAccess(this, paramUri, null);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.LightCycleHelper
 * JD-Core Version:    0.5.4
 */