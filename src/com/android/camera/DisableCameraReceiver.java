package com.android.camera;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;

public class DisableCameraReceiver extends BroadcastReceiver
{
  private static final String[] ACTIVITIES = { "com.android.camera.CameraLauncher" };

  private void disableComponent(Context paramContext, String paramString)
  {
    ComponentName localComponentName = new ComponentName(paramContext, paramString);
    paramContext.getPackageManager().setComponentEnabledSetting(localComponentName, 2, 1);
  }

  private boolean hasBackCamera()
  {
    int i = Camera.getNumberOfCameras();
    Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
    for (int j = 0; j < i; ++j)
    {
      Camera.getCameraInfo(j, localCameraInfo);
      if (localCameraInfo.facing != 0)
        continue;
      Log.i("DisableCameraReceiver", "back camera found: " + j);
      return true;
    }
    Log.i("DisableCameraReceiver", "no back camera");
    return false;
  }

  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (!hasBackCamera())
    {
      Log.i("DisableCameraReceiver", "disable all camera activities");
      for (int i = 0; i < ACTIVITIES.length; ++i)
        disableComponent(paramContext, ACTIVITIES[i]);
    }
    disableComponent(paramContext, "com.android.camera.DisableCameraReceiver");
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.DisableCameraReceiver
 * JD-Core Version:    0.5.4
 */