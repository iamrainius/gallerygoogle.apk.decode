package com.android.camera;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CameraHolder
{
  private static CameraManager.CameraProxy[] mMockCamera;
  private static Camera.CameraInfo[] mMockCameraInfo;
  private static SimpleDateFormat sDateFormat;
  private static CameraHolder sHolder;
  private static ArrayList<OpenReleaseState> sOpenReleaseStates = new ArrayList();
  private int mBackCameraId = -1;
  private CameraManager.CameraProxy mCameraDevice;
  private int mCameraId = -1;
  private boolean mCameraOpened;
  private int mFrontCameraId = -1;
  private final Handler mHandler;
  private final Camera.CameraInfo[] mInfo;
  private long mKeepBeforeTime;
  private final int mNumberOfCameras;
  private Camera.Parameters mParameters;

  static
  {
    sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  }

  private CameraHolder()
  {
    HandlerThread localHandlerThread = new HandlerThread("CameraHolder");
    localHandlerThread.start();
    this.mHandler = new MyHandler(localHandlerThread.getLooper());
    int j;
    if (mMockCameraInfo != null)
    {
      this.mNumberOfCameras = mMockCameraInfo.length;
      this.mInfo = mMockCameraInfo;
      j = 0;
      if (j >= this.mNumberOfCameras)
        label72: return;
      if ((this.mBackCameraId != -1) || (this.mInfo[j].facing != 0))
        break label168;
      this.mBackCameraId = j;
    }
    while (true)
    {
      ++j;
      break label72:
      this.mNumberOfCameras = Camera.getNumberOfCameras();
      this.mInfo = new Camera.CameraInfo[this.mNumberOfCameras];
      for (int i = 0; ; ++i)
      {
        if (i < this.mNumberOfCameras);
        this.mInfo[i] = new Camera.CameraInfo();
        Camera.getCameraInfo(i, this.mInfo[i]);
      }
      label168: if ((this.mFrontCameraId != -1) || (this.mInfo[j].facing != 1))
        continue;
      this.mFrontCameraId = j;
    }
  }

  private static void collectState(int paramInt, CameraManager.CameraProxy paramCameraProxy)
  {
    monitorenter;
    OpenReleaseState localOpenReleaseState;
    String[] arrayOfString;
    try
    {
      localOpenReleaseState = new OpenReleaseState(null);
      localOpenReleaseState.time = System.currentTimeMillis();
      localOpenReleaseState.id = paramInt;
      if (paramCameraProxy == null)
      {
        localOpenReleaseState.device = "(null)";
        StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
        arrayOfString = new String[arrayOfStackTraceElement.length];
        for (int i = 0; ; ++i)
        {
          if (i >= arrayOfStackTraceElement.length)
            break label97;
          arrayOfString[i] = arrayOfStackTraceElement[i].toString();
        }
      }
    }
    finally
    {
      monitorexit;
    }
    label97: localOpenReleaseState.stack = arrayOfString;
    if (sOpenReleaseStates.size() > 10)
      sOpenReleaseStates.remove(0);
    sOpenReleaseStates.add(localOpenReleaseState);
    monitorexit;
  }

  private static void dumpStates()
  {
    monitorenter;
    try
    {
      for (int i = -1 + sOpenReleaseStates.size(); i >= 0; --i)
      {
        OpenReleaseState localOpenReleaseState = (OpenReleaseState)sOpenReleaseStates.get(i);
        String str = sDateFormat.format(new Date(localOpenReleaseState.time));
        Log.d("CameraHolder", "State " + i + " at " + str);
        Log.d("CameraHolder", "mCameraId = " + localOpenReleaseState.id + ", mCameraDevice = " + localOpenReleaseState.device);
        Log.d("CameraHolder", "Stack:");
        for (int j = 0; j < localOpenReleaseState.stack.length; ++j)
          Log.d("CameraHolder", "  " + localOpenReleaseState.stack[j]);
      }
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  public static void injectMockCamera(Camera.CameraInfo[] paramArrayOfCameraInfo, CameraManager.CameraProxy[] paramArrayOfCameraProxy)
  {
    mMockCameraInfo = paramArrayOfCameraInfo;
    mMockCamera = paramArrayOfCameraProxy;
    sHolder = new CameraHolder();
  }

  public static CameraHolder instance()
  {
    monitorenter;
    try
    {
      if (sHolder == null)
        sHolder = new CameraHolder();
      CameraHolder localCameraHolder = sHolder;
      return localCameraHolder;
    }
    finally
    {
      monitorexit;
    }
  }

  public int getBackCameraId()
  {
    return this.mBackCameraId;
  }

  public Camera.CameraInfo[] getCameraInfo()
  {
    return this.mInfo;
  }

  public int getFrontCameraId()
  {
    return this.mFrontCameraId;
  }

  public int getNumberOfCameras()
  {
    return this.mNumberOfCameras;
  }

  public void keep()
  {
    keep(3000);
  }

  public void keep(int paramInt)
  {
    monitorenter;
    try
    {
      this.mKeepBeforeTime = (System.currentTimeMillis() + paramInt);
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public CameraManager.CameraProxy open(int paramInt)
    throws CameraHardwareException
  {
    boolean bool = true;
    monitorenter;
    while (true)
    {
      try
      {
        collectState(paramInt, this.mCameraDevice);
        if (this.mCameraOpened)
        {
          Log.e("CameraHolder", "double open");
          dumpStates();
        }
        CameraManager.CameraProxy localCameraProxy1;
        if (!this.mCameraOpened)
        {
          Util.Assert(bool);
          if ((this.mCameraDevice != null) && (this.mCameraId != paramInt))
          {
            this.mCameraDevice.release();
            this.mCameraDevice = null;
            this.mCameraId = -1;
          }
          localCameraProxy1 = this.mCameraDevice;
        }
        try
        {
          Log.v("CameraHolder", "open camera " + paramInt);
          if (mMockCameraInfo == null)
          {
            this.mCameraDevice = CameraManager.instance().cameraOpen(paramInt);
            this.mCameraId = paramInt;
            this.mParameters = this.mCameraDevice.getParameters();
            this.mCameraOpened = true;
            this.mHandler.removeMessages(1);
            this.mKeepBeforeTime = 0L;
            CameraManager.CameraProxy localCameraProxy2 = this.mCameraDevice;
            monitorexit;
            return localCameraProxy2;
            bool = false;
          }
          if (mMockCamera != null)
            break label218;
          throw new RuntimeException();
        }
        catch (RuntimeException localRuntimeException)
        {
          Log.e("CameraHolder", "fail to connect Camera", localRuntimeException);
          throw new CameraHardwareException(localRuntimeException);
        }
      }
      finally
      {
        monitorexit;
      }
      label218: this.mCameraDevice = mMockCamera[paramInt];
      continue;
      try
      {
        this.mCameraDevice.reconnect();
        this.mCameraDevice.setParameters(this.mParameters);
      }
      catch (IOException localIOException)
      {
        Log.e("CameraHolder", "reconnect failed.");
        throw new CameraHardwareException(localIOException);
      }
    }
  }

  public void release()
  {
    monitorenter;
    while (true)
    {
      long l;
      try
      {
        collectState(this.mCameraId, this.mCameraDevice);
        CameraManager.CameraProxy localCameraProxy = this.mCameraDevice;
        if (localCameraProxy == null)
          return;
        l = System.currentTimeMillis();
        if (l >= this.mKeepBeforeTime)
          break label80;
        if (this.mCameraOpened)
        {
          this.mCameraOpened = false;
          this.mCameraDevice.stopPreview();
        }
      }
      finally
      {
        monitorexit;
      }
      label80: this.mCameraOpened = false;
      this.mCameraDevice.release();
      this.mCameraDevice = null;
      this.mParameters = null;
      this.mCameraId = -1;
    }
  }

  public CameraManager.CameraProxy tryOpen(int paramInt)
  {
    monitorenter;
    Object localObject2;
    try
    {
      boolean bool2 = this.mCameraOpened;
      localObject2 = null;
      if (!bool2)
      {
        CameraManager.CameraProxy localCameraProxy = open(paramInt);
        localObject2 = localCameraProxy;
      }
      return localObject2;
    }
    catch (CameraHardwareException localCameraHardwareException)
    {
      boolean bool1 = "eng".equals(Build.TYPE);
      localObject2 = null;
      throw new RuntimeException(localCameraHardwareException);
    }
    finally
    {
      monitorexit;
    }
  }

  private class MyHandler extends Handler
  {
    MyHandler(Looper arg2)
    {
      super(localLooper);
    }

    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default:
        return;
      case 1:
      }
      synchronized (CameraHolder.this)
      {
        if (!CameraHolder.this.mCameraOpened)
          CameraHolder.this.release();
        return;
      }
    }
  }

  private static class OpenReleaseState
  {
    String device;
    int id;
    String[] stack;
    long time;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.CameraHolder
 * JD-Core Version:    0.5.4
 */