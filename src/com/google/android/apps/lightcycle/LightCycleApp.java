package com.google.android.apps.lightcycle;

import com.google.android.apps.lightcycle.camera.CameraUtility;
import com.google.android.apps.lightcycle.panorama.LightCycleNative;
import com.google.android.apps.lightcycle.util.Size;

public class LightCycleApp
{
  private static String appVersion = "999";
  private static CameraUtility cameraUtil;

  public static String getAppVersion()
  {
    return appVersion;
  }

  public static CameraUtility getCameraUtility()
  {
    return cameraUtil;
  }

  public static void initLightCycleNative()
  {
    cameraUtil = new CameraUtility(320, 240);
    Size localSize = cameraUtil.getPreviewSize();
    float f = cameraUtil.getFieldOfView();
    LightCycleNative.InitNative(localSize.width, localSize.height, f, false);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.LightCycleApp
 * JD-Core Version:    0.5.4
 */