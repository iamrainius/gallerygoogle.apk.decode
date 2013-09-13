package com.android.camera;

import android.hardware.Camera;
import android.hardware.Camera.ErrorCallback;
import android.util.Log;

public class CameraErrorCallback
  implements Camera.ErrorCallback
{
  public void onError(int paramInt, Camera paramCamera)
  {
    Log.e("CameraErrorCallback", "Got camera error callback. error=" + paramInt);
    if (paramInt != 100)
      return;
    throw new RuntimeException("Media server died.");
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.CameraErrorCallback
 * JD-Core Version:    0.5.4
 */