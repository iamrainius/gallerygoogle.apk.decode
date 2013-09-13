package com.google.android.apps.lightcycle.camera;

import android.graphics.ImageFormat;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import com.google.android.apps.lightcycle.panorama.DeviceManager;
import com.google.android.apps.lightcycle.util.LG;
import com.google.android.apps.lightcycle.util.Size;
import java.util.Iterator;
import java.util.List;

public class CameraUtility
{
  private static final String TAG = CameraUtility.class.getSimpleName();
  private final float fieldOfView;
  private boolean hasBackFacingCamera = false;
  private Camera.Size photoSize;
  private final Size previewSize;

  public CameraUtility(int paramInt1, int paramInt2)
  {
    CameraApiProxy.CameraProxy localCameraProxy = CameraApiProxy.instance().openBackCamera();
    if (localCameraProxy == null)
    {
      this.hasBackFacingCamera = false;
      this.previewSize = new Size(0, 0);
      this.fieldOfView = 0.0F;
      return;
    }
    this.hasBackFacingCamera = true;
    Camera.Size localSize = getClosestPreviewSize(localCameraProxy, paramInt1, paramInt2);
    this.previewSize = new Size(localSize.width, localSize.height);
    this.fieldOfView = DeviceManager.getCameraFieldOfViewDegrees(localCameraProxy.getParameters().getHorizontalViewAngle());
    localCameraProxy.release();
  }

  private Camera.Size getClosestPreviewSize(CameraApiProxy.CameraProxy paramCameraProxy, int paramInt1, int paramInt2)
  {
    List localList = paramCameraProxy.getParameters().getSupportedPreviewSizes();
    int i = paramInt1 * paramInt2;
    int j = 2147483647;
    Object localObject = (Camera.Size)localList.get(0);
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Camera.Size localSize = (Camera.Size)localIterator.next();
      int k = Math.abs(localSize.width * localSize.height - i);
      if (k >= j)
        continue;
      j = k;
      localObject = localSize;
    }
    return (Camera.Size)localObject;
  }

  public void allocateBuffers(CameraApiProxy.CameraProxy paramCameraProxy, Size paramSize, int paramInt, Camera.PreviewCallback paramPreviewCallback)
  {
    paramCameraProxy.setPreviewCallbackWithBuffer(null);
    int i = (int)Math.ceil(ImageFormat.getBitsPerPixel(paramCameraProxy.getParameters().getPreviewFormat()) / 8.0F * (paramSize.width * paramSize.height));
    for (int j = 0; j < paramInt; ++j)
      paramCameraProxy.addCallbackBuffer(new byte[i]);
    paramCameraProxy.setPreviewCallbackWithBuffer(paramPreviewCallback);
  }

  public float getFieldOfView()
  {
    return this.fieldOfView;
  }

  public String getFlashMode(CameraApiProxy.CameraProxy paramCameraProxy)
  {
    List localList = paramCameraProxy.getParameters().getSupportedFlashModes();
    if ((localList != null) && (localList.contains("off")))
      return "off";
    return "auto";
  }

  public String getFocusMode(CameraApiProxy.CameraProxy paramCameraProxy)
  {
    List localList = paramCameraProxy.getParameters().getSupportedFocusModes();
    if (localList != null)
    {
      if (localList.contains("infinity"))
      {
        Log.v(TAG, "Using Focus mode infinity");
        return "infinity";
      }
      if (localList.contains("fixed"))
      {
        Log.v(TAG, "Using Focus mode fixed");
        return "fixed";
      }
    }
    Log.v(TAG, "Using Focus mode auto.");
    return "auto";
  }

  public Camera.Size getPhotoSize()
  {
    return this.photoSize;
  }

  public Size getPreviewSize()
  {
    return this.previewSize;
  }

  public boolean hasBackFacingCamera()
  {
    return this.hasBackFacingCamera;
  }

  public void setFrameRate(Camera.Parameters paramParameters)
  {
    List localList = paramParameters.getSupportedPreviewFpsRange();
    if (localList.size() == 0)
      LG.d("No suppoted frame rates returned!");
    Object localObject;
    do
    {
      return;
      localObject = new int[] { -1, -1 };
      Iterator localIterator = localList.iterator();
      label40: if (!localIterator.hasNext())
        continue;
      int[] arrayOfInt = (int[])localIterator.next();
      if ((arrayOfInt[1] > localObject[1]) && (arrayOfInt[1] <= 40000));
      for (localObject = arrayOfInt; ; localObject = arrayOfInt)
        do
        {
          LG.d("Available rates : " + arrayOfInt[0] + " to " + arrayOfInt[1]);
          break label40:
        }
        while ((arrayOfInt[1] != localObject[1]) || (arrayOfInt[0] <= localObject[0]));
    }
    while (localObject[0] <= 0);
    LG.d("Setting frame rate : " + localObject[0] + " to " + localObject[1]);
    paramParameters.setPreviewFpsRange(localObject[0], localObject[1]);
  }

  public void setPictureSize(Camera.Parameters paramParameters, int paramInt)
  {
    List localList = paramParameters.getSupportedPictureSizes();
    int i = 0;
    int j = 0;
    int k = 1000000000;
    while (i < localList.size())
    {
      int l = Math.abs(((Camera.Size)localList.get(i)).width - paramInt);
      if (l < k)
      {
        k = l;
        j = i;
      }
      ++i;
    }
    this.photoSize = ((Camera.Size)localList.get(j));
    paramParameters.setPictureSize(((Camera.Size)localList.get(j)).width, ((Camera.Size)localList.get(j)).height);
    Log.e(TAG, "Picture size : " + ((Camera.Size)localList.get(j)).width + ", " + ((Camera.Size)localList.get(j)).height);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.camera.CameraUtility
 * JD-Core Version:    0.5.4
 */