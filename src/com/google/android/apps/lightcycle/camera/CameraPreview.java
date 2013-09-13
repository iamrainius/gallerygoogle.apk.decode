package com.google.android.apps.lightcycle.camera;

import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import com.google.android.apps.lightcycle.panorama.LightCycleView;
import com.google.android.apps.lightcycle.util.Size;

public abstract interface CameraPreview
{
  public abstract CameraApiProxy.CameraProxy getCamera();

  public abstract Camera.Size getPhotoSize();

  public abstract float getReportedHorizontalFovDegrees();

  public abstract Size initCamera(Camera.PreviewCallback paramPreviewCallback, int paramInt1, int paramInt2, boolean paramBoolean);

  public abstract void releaseCamera();

  public abstract void returnCallbackBuffer(byte[] paramArrayOfByte);

  public abstract void setFastShutter(boolean paramBoolean);

  public abstract void setMainView(LightCycleView paramLightCycleView);

  public abstract void startPreview();
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.camera.CameraPreview
 * JD-Core Version:    0.5.4
 */