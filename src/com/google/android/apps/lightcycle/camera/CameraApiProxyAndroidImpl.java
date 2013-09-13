package com.google.android.apps.lightcycle.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Build.VERSION;
import android.view.SurfaceHolder;
import com.android.camera.CameraHardwareException;
import com.android.camera.CameraHolder;
import com.android.camera.CameraManager.CameraProxy;
import java.io.IOException;

public class CameraApiProxyAndroidImpl extends CameraApiProxy
{
  public CameraApiProxy.CameraProxy openBackCamera()
  {
    int i = Camera.getNumberOfCameras();
    Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
    int j = 0;
    label14: Object localObject = null;
    if (j < i);
    try
    {
      Camera.getCameraInfo(j, localCameraInfo);
      if (localCameraInfo.facing == 0)
      {
        CameraManager.CameraProxy localCameraProxy = CameraHolder.instance().open(j);
        localObject = localCameraProxy;
        if (localObject == null)
          return null;
      }
      else
      {
        ++j;
        break label14:
      }
      return new CameraProxyAndroidImpl(localObject);
    }
    catch (CameraHardwareException localCameraHardwareException)
    {
      localObject = null;
    }
  }

  private static class CameraProxyAndroidImpl
    implements CameraApiProxy.CameraProxy
  {
    private CameraManager.CameraProxy camera;

    public CameraProxyAndroidImpl(CameraManager.CameraProxy paramCameraProxy)
    {
      this.camera = paramCameraProxy;
    }

    public void addCallbackBuffer(byte[] paramArrayOfByte)
    {
      this.camera.addCallbackBuffer(paramArrayOfByte);
    }

    public void enableShutterSound(boolean paramBoolean)
    {
      if (Build.VERSION.SDK_INT < 17)
        return;
      this.camera.enableShutterSound(paramBoolean);
    }

    public Camera.Parameters getParameters()
    {
      return this.camera.getParameters();
    }

    public void release()
    {
      if (this.camera == null)
        return;
      CameraHolder.instance().release();
      this.camera = null;
    }

    public void setDisplayOrientation(int paramInt)
    {
      this.camera.setDisplayOrientation(paramInt);
    }

    public void setParameters(Camera.Parameters paramParameters)
    {
      this.camera.setParameters(paramParameters);
    }

    public void setPreviewCallback(Camera.PreviewCallback paramPreviewCallback)
    {
      this.camera.setPreviewCallback(paramPreviewCallback);
    }

    public void setPreviewCallbackWithBuffer(Camera.PreviewCallback paramPreviewCallback)
    {
      this.camera.setPreviewCallbackWithBuffer(paramPreviewCallback);
    }

    public void setPreviewDisplay(SurfaceHolder paramSurfaceHolder)
      throws IOException
    {
      this.camera.setPreviewDisplayAsync(paramSurfaceHolder);
      this.camera.waitForIdle();
    }

    public void setPreviewTexture(SurfaceTexture paramSurfaceTexture)
      throws IOException
    {
      this.camera.setPreviewTextureAsync(paramSurfaceTexture);
      this.camera.waitForIdle();
    }

    public void startPreview()
    {
      this.camera.startPreviewAsync();
      this.camera.waitForIdle();
    }

    public void stopPreview()
    {
      this.camera.stopPreview();
    }

    public void takePicture(Camera.ShutterCallback paramShutterCallback, Camera.PictureCallback paramPictureCallback1, Camera.PictureCallback paramPictureCallback2)
    {
      this.camera.takePicture(paramShutterCallback, paramPictureCallback1, null, paramPictureCallback2);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.camera.CameraApiProxyAndroidImpl
 * JD-Core Version:    0.5.4
 */