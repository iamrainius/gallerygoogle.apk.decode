package com.google.android.apps.lightcycle.camera;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import com.google.android.apps.lightcycle.panorama.LightCycleView;
import com.google.android.apps.lightcycle.util.LG;
import com.google.android.apps.lightcycle.util.Size;
import java.io.IOException;

@SuppressLint({"NewApi"})
public class TextureCameraPreview
  implements CameraPreview
{
  private CameraApiProxy.CameraProxy mCamera = null;
  private final CameraUtility mCameraUtil;
  private String mFastShutterMode = "auto";
  private float mHorizontalViewAngle;
  private Camera.PreviewCallback mPreviewCallback;
  private Size mPreviewSize;
  private SurfaceTexture mSurfaceTexture = new SurfaceTexture(100);
  private boolean mUsePreviewBuffers;

  public TextureCameraPreview(CameraUtility paramCameraUtility)
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
    return this.mHorizontalViewAngle;
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
    this.mHorizontalViewAngle = this.mCamera.getParameters().getHorizontalViewAngle();
    this.mCamera.enableShutterSound(false);
    Camera.Parameters localParameters = this.mCamera.getParameters();
    localParameters.setFocusMode(this.mCameraUtil.getFocusMode(this.mCamera));
    localParameters.setFlashMode(this.mCameraUtil.getFlashMode(this.mCamera));
    localParameters.setZoom(0);
    this.mPreviewSize = this.mCameraUtil.getPreviewSize();
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
    try
    {
      this.mCamera.setPreviewTexture(this.mSurfaceTexture);
      label247: if (!this.mUsePreviewBuffers)
        break label289;
      this.mCameraUtil.allocateBuffers(this.mCamera, this.mPreviewSize, 3, this.mPreviewCallback);
      label289: return this.mPreviewSize;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
      break label247:
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
    this.mCamera.enableShutterSound(true);
    this.mCamera.release();
    this.mCamera = null;
    this.mHorizontalViewAngle = 0.0F;
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
 * Qualified Name:     com.google.android.apps.lightcycle.camera.TextureCameraPreview
 * JD-Core Version:    0.5.4
 */