package com.android.camera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CameraButtonIntentReceiver extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    CameraHolder localCameraHolder = CameraHolder.instance();
    if (localCameraHolder.tryOpen(CameraSettings.readPreferredCameraId(new ComboPreferences(paramContext))) == null)
      return;
    localCameraHolder.keep();
    localCameraHolder.release();
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.setClass(paramContext, CameraActivity.class);
    localIntent.addCategory("android.intent.category.LAUNCHER");
    localIntent.setFlags(335544320);
    paramContext.startActivity(localIntent);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.CameraButtonIntentReceiver
 * JD-Core Version:    0.5.4
 */