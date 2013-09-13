package com.android.camera;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.AutoFocusMoveCallback;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.OnZoomChangeListener;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import java.io.IOException;

public class CameraManager
{
  private static CameraManager sCameraManager = new CameraManager();
  private Camera mCamera;
  private Handler mCameraHandler;
  private CameraProxy mCameraProxy;
  private Camera.Parameters mParameters;
  private IOException mReconnectException;
  private ConditionVariable mSig = new ConditionVariable();

  private CameraManager()
  {
    HandlerThread localHandlerThread = new HandlerThread("Camera Handler Thread");
    localHandlerThread.start();
    this.mCameraHandler = new CameraHandler(localHandlerThread.getLooper());
  }

  public static CameraManager instance()
  {
    return sCameraManager;
  }

  @TargetApi(16)
  private void setAutoFocusMoveCallback(Camera paramCamera, Object paramObject)
  {
    paramCamera.setAutoFocusMoveCallback((Camera.AutoFocusMoveCallback)paramObject);
  }

  CameraProxy cameraOpen(int paramInt)
  {
    this.mCamera = Camera.open(paramInt);
    Camera localCamera = this.mCamera;
    CameraProxy localCameraProxy = null;
    if (localCamera != null)
    {
      this.mCameraProxy = new CameraProxy(null);
      localCameraProxy = this.mCameraProxy;
    }
    return localCameraProxy;
  }

  private class CameraHandler extends Handler
  {
    CameraHandler(Looper arg2)
    {
      super(localLooper);
    }

    @TargetApi(17)
    private void enableShutterSound(boolean paramBoolean)
    {
      CameraManager.this.mCamera.enableShutterSound(paramBoolean);
    }

    @TargetApi(14)
    private void setFaceDetectionListener(Camera.FaceDetectionListener paramFaceDetectionListener)
    {
      CameraManager.this.mCamera.setFaceDetectionListener(paramFaceDetectionListener);
    }

    @TargetApi(11)
    private void setPreviewTexture(Object paramObject)
    {
      try
      {
        CameraManager.this.mCamera.setPreviewTexture((SurfaceTexture)paramObject);
        return;
      }
      catch (IOException localIOException)
      {
        throw new RuntimeException(localIOException);
      }
    }

    @TargetApi(14)
    private void startFaceDetection()
    {
      CameraManager.this.mCamera.startFaceDetection();
    }

    @TargetApi(14)
    private void stopFaceDetection()
    {
      CameraManager.this.mCamera.stopFaceDetection();
    }

    public void handleMessage(Message paramMessage)
    {
      try
      {
        switch (paramMessage.what)
        {
        default:
          throw new RuntimeException("Invalid CameraProxy message=" + paramMessage.what);
        case 1:
        case 22:
        case 2:
        case 3:
        case 4:
        case 5:
        case 23:
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
        case 11:
        case 12:
        case 13:
        case 14:
        case 15:
        case 16:
        case 17:
        case 18:
        case 19:
        case 20:
        case 21:
        case 24:
        case 25:
        }
      }
      catch (RuntimeException localRuntimeException)
      {
        if ((paramMessage.what != 1) && (CameraManager.this.mCamera != null));
        try
        {
          CameraManager.this.mCamera.release();
          label179: CameraManager.access$002(CameraManager.this, null);
          CameraManager.access$102(CameraManager.this, null);
          throw localRuntimeException;
          CameraManager.this.mCamera.release();
          CameraManager.access$002(CameraManager.this, null);
          CameraManager.access$102(CameraManager.this, null);
          while (true)
          {
            CameraManager.this.mSig.open();
            return;
            CameraManager.access$202(CameraManager.this, null);
            try
            {
              CameraManager.this.mCamera.reconnect();
            }
            catch (IOException localIOException2)
            {
              CameraManager.access$202(CameraManager.this, localIOException2);
            }
            continue;
            CameraManager.this.mCamera.unlock();
            continue;
            CameraManager.this.mCamera.lock();
            continue;
            setPreviewTexture(paramMessage.obj);
            return;
            try
            {
              CameraManager.this.mCamera.setPreviewDisplay((SurfaceHolder)paramMessage.obj);
              return;
            }
            catch (IOException localIOException1)
            {
              throw new RuntimeException(localIOException1);
            }
            CameraManager.this.mCamera.startPreview();
            return;
            CameraManager.this.mCamera.stopPreview();
            continue;
            CameraManager.this.mCamera.setPreviewCallbackWithBuffer((Camera.PreviewCallback)paramMessage.obj);
            continue;
            CameraManager.this.mCamera.addCallbackBuffer((byte[])(byte[])paramMessage.obj);
            continue;
            CameraManager.this.mCamera.autoFocus((Camera.AutoFocusCallback)paramMessage.obj);
            continue;
            CameraManager.this.mCamera.cancelAutoFocus();
            continue;
            CameraManager.this.setAutoFocusMoveCallback(CameraManager.this.mCamera, paramMessage.obj);
            continue;
            CameraManager.this.mCamera.setDisplayOrientation(paramMessage.arg1);
            continue;
            CameraManager.this.mCamera.setZoomChangeListener((Camera.OnZoomChangeListener)paramMessage.obj);
            continue;
            setFaceDetectionListener((Camera.FaceDetectionListener)paramMessage.obj);
            continue;
            startFaceDetection();
            continue;
            stopFaceDetection();
            continue;
            CameraManager.this.mCamera.setErrorCallback((Camera.ErrorCallback)paramMessage.obj);
            continue;
            CameraManager.this.mCamera.setParameters((Camera.Parameters)paramMessage.obj);
            continue;
            CameraManager.access$402(CameraManager.this, CameraManager.this.mCamera.getParameters());
            continue;
            CameraManager.this.mCamera.setParameters((Camera.Parameters)paramMessage.obj);
            return;
            CameraManager.this.mCamera.setPreviewCallback((Camera.PreviewCallback)paramMessage.obj);
            continue;
            if (paramMessage.arg1 != 1)
              break;
            bool = true;
            enableShutterSound(bool);
          }
          boolean bool = false;
        }
        catch (Exception localException)
        {
          Log.e("CameraManager", "Fail to release the camera.");
          break label179:
        }
      }
    }
  }

  public class CameraProxy
  {
    private CameraProxy()
    {
      if (CameraManager.this.mCamera != null);
      for (boolean bool = true; ; bool = false)
      {
        Util.Assert(bool);
        return;
      }
    }

    public void addCallbackBuffer(byte[] paramArrayOfByte)
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.obtainMessage(9, paramArrayOfByte).sendToTarget();
      CameraManager.this.mSig.block();
    }

    public void autoFocus(Camera.AutoFocusCallback paramAutoFocusCallback)
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.obtainMessage(10, paramAutoFocusCallback).sendToTarget();
      CameraManager.this.mSig.block();
    }

    public void cancelAutoFocus()
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.sendEmptyMessage(11);
      CameraManager.this.mSig.block();
    }

    public void enableShutterSound(boolean paramBoolean)
    {
      CameraManager.this.mSig.close();
      Handler localHandler = CameraManager.this.mCameraHandler;
      if (paramBoolean);
      for (int i = 1; ; i = 0)
      {
        localHandler.obtainMessage(25, i, 0).sendToTarget();
        CameraManager.this.mSig.block();
        return;
      }
    }

    public Camera getCamera()
    {
      return CameraManager.this.mCamera;
    }

    public Camera.Parameters getParameters()
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.sendEmptyMessage(20);
      CameraManager.this.mSig.block();
      Camera.Parameters localParameters = CameraManager.this.mParameters;
      CameraManager.access$402(CameraManager.this, null);
      return localParameters;
    }

    public void lock()
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.sendEmptyMessage(4);
      CameraManager.this.mSig.block();
    }

    public void reconnect()
      throws IOException
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.sendEmptyMessage(2);
      CameraManager.this.mSig.block();
      if (CameraManager.this.mReconnectException == null)
        return;
      throw CameraManager.this.mReconnectException;
    }

    public void release()
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.sendEmptyMessage(1);
      CameraManager.this.mSig.block();
    }

    @TargetApi(16)
    public void setAutoFocusMoveCallback(Camera.AutoFocusMoveCallback paramAutoFocusMoveCallback)
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.obtainMessage(12, paramAutoFocusMoveCallback).sendToTarget();
      CameraManager.this.mSig.block();
    }

    public void setDisplayOrientation(int paramInt)
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.obtainMessage(13, paramInt, 0).sendToTarget();
      CameraManager.this.mSig.block();
    }

    public void setErrorCallback(Camera.ErrorCallback paramErrorCallback)
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.obtainMessage(18, paramErrorCallback).sendToTarget();
      CameraManager.this.mSig.block();
    }

    @TargetApi(14)
    public void setFaceDetectionListener(Camera.FaceDetectionListener paramFaceDetectionListener)
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.obtainMessage(15, paramFaceDetectionListener).sendToTarget();
      CameraManager.this.mSig.block();
    }

    public void setParameters(Camera.Parameters paramParameters)
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.obtainMessage(19, paramParameters).sendToTarget();
      CameraManager.this.mSig.block();
    }

    public void setParametersAsync(Camera.Parameters paramParameters)
    {
      CameraManager.this.mCameraHandler.removeMessages(21);
      CameraManager.this.mCameraHandler.obtainMessage(21, paramParameters).sendToTarget();
    }

    public void setPreviewCallback(Camera.PreviewCallback paramPreviewCallback)
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.obtainMessage(24, paramPreviewCallback).sendToTarget();
      CameraManager.this.mSig.block();
    }

    public void setPreviewCallbackWithBuffer(Camera.PreviewCallback paramPreviewCallback)
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.obtainMessage(8, paramPreviewCallback).sendToTarget();
      CameraManager.this.mSig.block();
    }

    public void setPreviewDisplayAsync(SurfaceHolder paramSurfaceHolder)
    {
      CameraManager.this.mCameraHandler.obtainMessage(23, paramSurfaceHolder).sendToTarget();
    }

    @TargetApi(11)
    public void setPreviewTextureAsync(SurfaceTexture paramSurfaceTexture)
    {
      CameraManager.this.mCameraHandler.obtainMessage(5, paramSurfaceTexture).sendToTarget();
    }

    public void setZoomChangeListener(Camera.OnZoomChangeListener paramOnZoomChangeListener)
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.obtainMessage(14, paramOnZoomChangeListener).sendToTarget();
      CameraManager.this.mSig.block();
    }

    public void startFaceDetection()
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.sendEmptyMessage(16);
      CameraManager.this.mSig.block();
    }

    public void startPreviewAsync()
    {
      CameraManager.this.mCameraHandler.sendEmptyMessage(6);
    }

    public void stopFaceDetection()
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.sendEmptyMessage(17);
      CameraManager.this.mSig.block();
    }

    public void stopPreview()
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.sendEmptyMessage(7);
      CameraManager.this.mSig.block();
    }

    public void takePicture(Camera.ShutterCallback paramShutterCallback, Camera.PictureCallback paramPictureCallback1, Camera.PictureCallback paramPictureCallback2, Camera.PictureCallback paramPictureCallback3)
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.post(new Runnable(paramShutterCallback, paramPictureCallback1, paramPictureCallback2, paramPictureCallback3)
      {
        public void run()
        {
          CameraManager.this.mCamera.takePicture(this.val$shutter, this.val$raw, this.val$postview, this.val$jpeg);
          CameraManager.this.mSig.open();
        }
      });
      CameraManager.this.mSig.block();
    }

    public void takePicture2(Camera.ShutterCallback paramShutterCallback, Camera.PictureCallback paramPictureCallback1, Camera.PictureCallback paramPictureCallback2, Camera.PictureCallback paramPictureCallback3, int paramInt1, int paramInt2)
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.post(new Runnable(paramShutterCallback, paramPictureCallback1, paramPictureCallback2, paramPictureCallback3, paramInt1, paramInt2)
      {
        public void run()
        {
          try
          {
            CameraManager.this.mCamera.takePicture(this.val$shutter, this.val$raw, this.val$postview, this.val$jpeg);
            CameraManager.this.mSig.open();
            return;
          }
          catch (RuntimeException localRuntimeException)
          {
            Log.w("CameraManager", "take picture failed; cameraState:" + this.val$cameraState + ", focusState:" + this.val$focusState);
            throw localRuntimeException;
          }
        }
      });
      CameraManager.this.mSig.block();
    }

    public void unlock()
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.sendEmptyMessage(3);
      CameraManager.this.mSig.block();
    }

    public void waitForIdle()
    {
      CameraManager.this.mSig.close();
      CameraManager.this.mCameraHandler.sendEmptyMessage(22);
      CameraManager.this.mSig.block();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.CameraManager
 * JD-Core Version:    0.5.4
 */