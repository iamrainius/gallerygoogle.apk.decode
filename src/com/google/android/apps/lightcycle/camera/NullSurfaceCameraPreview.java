package com.google.android.apps.lightcycle.camera;

import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import com.google.android.apps.lightcycle.panorama.LightCycleView;
import com.google.android.apps.lightcycle.util.LG;
import com.google.android.apps.lightcycle.util.Size;
import java.io.IOException;

public class NullSurfaceCameraPreview
  implements CameraPreview
{
  private CameraApiProxy.CameraProxy mCamera = null;
  protected float mCameraOrientationDeg = 90.0F;
  private final CameraUtility mCameraUtil;
  private String mFastShutterMode = "auto";
  private Camera.PreviewCallback mPreviewCallback;
  private Size mPreviewSize;
  private boolean mUsePreviewBuffers;

  public NullSurfaceCameraPreview(CameraUtility paramCameraUtility)
  {
    this.mCameraUtil = paramCameraUtility;
  }

  private void setSceneMode(String paramString)
  {
    Camera.Parameters localParameters = this.mCamera.getParameters();
    localParameters.setSceneMode(paramString);
    this.mCamera.setParameters(localParameters);
  }

  public CameraApiProxy.CameraProxy getCamera()
  {
    return this.mCamera;
  }

  public Camera.Size getPhotoSize()
  {
    return this.mCameraUtil.getPhotoSize();
  }

  public float getReportedHorizontalFovDegrees()
  {
    return this.mCamera.getParameters().getHorizontalViewAngle();
  }

  public Size initCamera(Camera.PreviewCallback paramPreviewCallback, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.mPreviewCallback = paramPreviewCallback;
    this.mUsePreviewBuffers = paramBoolean;
    if (this.mCamera == null)
      this.mCamera = CameraApiProxy.instance().openBackCamera();
    if (this.mCamera == null)
    {
      Log.v("LightCycle", "Camera is null");
      return null;
    }
    Camera.Parameters localParameters = this.mCamera.getParameters();
    localParameters.setFocusMode(this.mCameraUtil.getFocusMode(this.mCamera));
    localParameters.setFlashMode(this.mCameraUtil.getFlashMode(this.mCamera));
    localParameters.setZoom(0);
    this.mPreviewSize = this.mCameraUtil.getPreviewSize();
    Log.v("LightCycle", "Video size : " + this.mPreviewSize.width + ", " + this.mPreviewSize.height);
    localParameters.setPreviewSize(this.mPreviewSize.width, this.mPreviewSize.height);
    this.mCameraUtil.setFrameRate(localParameters);
    localParameters.setJpegThumbnailSize(0, 0);
    localParameters.setJpegQuality(100);
    setPictureSize(localParameters, 1000);
    localParameters.setRotation(0);
    this.mCamera.setParameters(localParameters);
    float f = localParameters.getHorizontalViewAngle();
    LG.d("Field of view reported = " + f);
    LG.d("Setting the preview display.");
    this.mCamera.setDisplayOrientation(0);
    try
    {
      this.mCamera.setPreviewDisplay(null);
      label274: if (!this.mUsePreviewBuffers)
        break label316;
      this.mCameraUtil.allocateBuffers(this.mCamera, this.mPreviewSize, 3, this.mPreviewCallback);
      label316: return this.mPreviewSize;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
      break label274:
      this.mCamera.setPreviewCallback(this.mPreviewCallback);
    }
  }

  public void releaseCamera()
  {
    if (this.mCamera == null)
      return;
    this.mCamera.stopPreview();
    this.mCamera.setPreviewCallback(null);
    this.mPreviewCallback = null;
    this.mCamera.release();
    this.mCamera = null;
  }

  public void returnCallbackBuffer(byte[] paramArrayOfByte)
  {
    if (!this.mUsePreviewBuffers)
      return;
    this.mCamera.addCallbackBuffer(paramArrayOfByte);
  }

  public void setFastShutter(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      setSceneMode(this.mFastShutterMode);
      return;
    }
    setSceneMode("auto");
  }

  public void setMainView(LightCycleView paramLightCycleView)
  {
  }

  public void setPictureSize(Camera.Parameters paramParameters, int paramInt)
  {
    this.mCameraUtil.setPictureSize(paramParameters, paramInt);
  }

  public void startPreview()
  {
    this.mCamera.startPreview();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.camera.NullSurfaceCameraPreview
 * JD-Core Version:    0.5.4
 */