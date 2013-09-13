package com.google.android.apps.lightcycle.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import java.io.IOException;

public abstract class CameraApiProxy
{
  private static final String TAG = CameraApiProxy.class.getSimpleName();
  private static CameraApiProxy instance;

  public static CameraApiProxy instance()
  {
    if (instance == null)
      Log.e(TAG, "No CameraApiProxy implementation set. Use CameraApiProxy.setActiveProxy first.");
    return instance;
  }

  public static void setActiveProxy(CameraApiProxy paramCameraApiProxy)
  {
    instance = paramCameraApiProxy;
  }

  public abstract CameraProxy openBackCamera();

  public static abstract interface CameraProxy
  {
    public abstract void addCallbackBuffer(byte[] paramArrayOfByte);

    public abstract void enableShutterSound(boolean paramBoolean);

    public abstract Camera.Parameters getParameters();

    public abstract void release();

    public abstract void setDisplayOrientation(int paramInt);

    public abstract void setParameters(Camera.Parameters paramParameters);

    public abstract void setPreviewCallback(Camera.PreviewCallback paramPreviewCallback);

    public abstract void setPreviewCallbackWithBuffer(Camera.PreviewCallback paramPreviewCallback);

    public abstract void setPreviewDisplay(SurfaceHolder paramSurfaceHolder)
      throws IOException;

    public abstract void setPreviewTexture(SurfaceTexture paramSurfaceTexture)
      throws IOException;

    public abstract void startPreview();

    public abstract void stopPreview();

    public abstract void takePicture(Camera.ShutterCallback paramShutterCallback, Camera.PictureCallback paramPictureCallback1, Camera.PictureCallback paramPictureCallback2);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.camera.CameraApiProxy
 * JD-Core Version:    0.5.4
 */