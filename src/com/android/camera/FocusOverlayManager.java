package com.android.camera;

import android.annotation.TargetApi;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera.Area;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.android.camera.ui.FaceView;
import com.android.camera.ui.FocusIndicator;
import com.android.camera.ui.PieRenderer;
import java.util.ArrayList;
import java.util.List;

public class FocusOverlayManager
{
  private boolean mAeAwbLock;
  private String[] mDefaultFocusModes;
  private int mDisplayOrientation;
  private FaceView mFaceView;
  private List<Object> mFocusArea;
  private boolean mFocusAreaSupported;
  private String mFocusMode;
  private Handler mHandler;
  private boolean mInitialized;
  Listener mListener;
  private boolean mLockAeAwbNeeded;
  private Matrix mMatrix;
  private List<Object> mMeteringArea;
  private boolean mMeteringAreaSupported;
  private boolean mMirror;
  private String mOverrideFocusMode;
  private Camera.Parameters mParameters;
  private PieRenderer mPieRenderer;
  private ComboPreferences mPreferences;
  private int mPreviewHeight;
  private int mPreviewWidth;
  private int mState = 0;

  public FocusOverlayManager(ComboPreferences paramComboPreferences, String[] paramArrayOfString, Camera.Parameters paramParameters, Listener paramListener, boolean paramBoolean, Looper paramLooper)
  {
    this.mHandler = new MainHandler(paramLooper);
    this.mMatrix = new Matrix();
    this.mPreferences = paramComboPreferences;
    this.mDefaultFocusModes = paramArrayOfString;
    setParameters(paramParameters);
    this.mListener = paramListener;
    setMirror(paramBoolean);
  }

  private void autoFocus()
  {
    Log.v("CAM_FocusManager", "Start autofocus.");
    this.mListener.autoFocus();
    this.mState = 1;
    if (this.mFaceView != null)
      this.mFaceView.pause();
    updateFocusUI();
    this.mHandler.removeMessages(0);
  }

  private void calculateTapArea(int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Rect paramRect)
  {
    int i = (int)(paramFloat * paramInt1);
    int j = (int)(paramFloat * paramInt2);
    int k = Util.clamp(paramInt3 - i / 2, 0, paramInt5 - i);
    int l = Util.clamp(paramInt4 - j / 2, 0, paramInt6 - j);
    RectF localRectF = new RectF(k, l, k + i, l + j);
    this.mMatrix.mapRect(localRectF);
    Util.rectFToRect(localRectF, paramRect);
  }

  private void cancelAutoFocus()
  {
    Log.v("CAM_FocusManager", "Cancel autofocus.");
    resetTouchFocus();
    this.mListener.cancelAutoFocus();
    if (this.mFaceView != null)
      this.mFaceView.resume();
    this.mState = 0;
    updateFocusUI();
    this.mHandler.removeMessages(0);
  }

  private void capture()
  {
    if (!this.mListener.capture())
      return;
    this.mState = 0;
    this.mHandler.removeMessages(0);
  }

  @TargetApi(14)
  private void initializeFocusAreas(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    if (this.mFocusArea == null)
    {
      this.mFocusArea = new ArrayList();
      this.mFocusArea.add(new Camera.Area(new Rect(), 1));
    }
    calculateTapArea(paramInt1, paramInt2, 1.0F, paramInt3, paramInt4, paramInt5, paramInt6, ((Camera.Area)this.mFocusArea.get(0)).rect);
  }

  @TargetApi(14)
  private void initializeMeteringAreas(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    if (this.mMeteringArea == null)
    {
      this.mMeteringArea = new ArrayList();
      this.mMeteringArea.add(new Camera.Area(new Rect(), 1));
    }
    calculateTapArea(paramInt1, paramInt2, 1.5F, paramInt3, paramInt4, paramInt5, paramInt6, ((Camera.Area)this.mMeteringArea.get(0)).rect);
  }

  private boolean needAutoFocusCall()
  {
    String str = getFocusMode();
    return (!str.equals("infinity")) && (!str.equals("fixed")) && (!str.equals("edof"));
  }

  private void setMatrix()
  {
    if ((this.mPreviewWidth != 0) && (this.mPreviewHeight != 0))
    {
      Matrix localMatrix = new Matrix();
      Util.prepareMatrix(localMatrix, this.mMirror, this.mDisplayOrientation, this.mPreviewWidth, this.mPreviewHeight);
      localMatrix.invert(this.mMatrix);
      if (this.mPieRenderer == null)
        break label66;
    }
    for (int i = 1; ; i = 0)
    {
      this.mInitialized = i;
      label66: return;
    }
  }

  public void doSnap()
  {
    if (!this.mInitialized);
    do
    {
      return;
      if ((!needAutoFocusCall()) || (this.mState == 3) || (this.mState == 4))
      {
        capture();
        return;
      }
      if (this.mState != 1)
        continue;
      this.mState = 2;
      return;
    }
    while (this.mState != 0);
    capture();
  }

  public boolean getAeAwbLock()
  {
    return this.mAeAwbLock;
  }

  public List getFocusAreas()
  {
    return this.mFocusArea;
  }

  public String getFocusMode()
  {
    if (this.mOverrideFocusMode != null)
      return this.mOverrideFocusMode;
    List localList = this.mParameters.getSupportedFocusModes();
    if ((this.mFocusAreaSupported) && (this.mFocusArea != null))
    {
      this.mFocusMode = "auto";
      if (!Util.isSupported(this.mFocusMode, localList))
        label40: if (!Util.isSupported("auto", this.mParameters.getSupportedFocusModes()))
          break label138;
    }
    for (this.mFocusMode = "auto"; ; this.mFocusMode = this.mParameters.getFocusMode())
    {
      return this.mFocusMode;
      this.mFocusMode = this.mPreferences.getString("pref_camera_focusmode_key", null);
      if (this.mFocusMode == null);
      for (int i = 0; ; ++i)
      {
        if (i < this.mDefaultFocusModes.length);
        String str = this.mDefaultFocusModes[i];
        if (!Util.isSupported(str, localList))
          continue;
        this.mFocusMode = str;
        label138: break label40:
      }
    }
  }

  int getFocusState()
  {
    return this.mState;
  }

  public List getMeteringAreas()
  {
    return this.mMeteringArea;
  }

  public boolean isFocusCompleted()
  {
    return (this.mState == 3) || (this.mState == 4);
  }

  public boolean isFocusingSnapOnFinish()
  {
    return this.mState == 2;
  }

  public void onAutoFocus(boolean paramBoolean)
  {
    if (this.mState == 2)
      if (paramBoolean)
      {
        this.mState = 3;
        label17: updateFocusUI();
        capture();
      }
    do
    {
      return;
      this.mState = 4;
      break label17:
      if (this.mState != 1)
        continue;
      if (paramBoolean);
      for (this.mState = 3; ; this.mState = 4)
      {
        updateFocusUI();
        if (this.mFocusArea != null);
        this.mHandler.sendEmptyMessageDelayed(0, 3000L);
        return;
      }
    }
    while (this.mState != 0);
  }

  public void onAutoFocusMoving(boolean paramBoolean)
  {
    if (!this.mInitialized);
    do
    {
      return;
      if ((this.mFaceView == null) || (!this.mFaceView.faceExists()))
        continue;
      this.mPieRenderer.clear();
      return;
    }
    while (this.mState != 0);
    if (paramBoolean)
    {
      this.mPieRenderer.showStart();
      return;
    }
    this.mPieRenderer.showSuccess(true);
  }

  public void onCameraReleased()
  {
    onPreviewStopped();
  }

  public void onPreviewStarted()
  {
    this.mState = 0;
  }

  public void onPreviewStopped()
  {
    this.mState = 0;
    resetTouchFocus();
    updateFocusUI();
  }

  public void onShutterDown()
  {
    if (!this.mInitialized);
    do
    {
      return;
      if ((!this.mLockAeAwbNeeded) || (this.mAeAwbLock))
        continue;
      this.mAeAwbLock = true;
      this.mListener.setFocusParameters();
    }
    while ((!needAutoFocusCall()) || (this.mState == 3) || (this.mState == 4));
    autoFocus();
  }

  public void onShutterUp()
  {
    if (!this.mInitialized);
    do
    {
      return;
      if ((!needAutoFocusCall()) || ((this.mState != 1) && (this.mState != 3) && (this.mState != 4)))
        continue;
      cancelAutoFocus();
    }
    while ((!this.mLockAeAwbNeeded) || (!this.mAeAwbLock) || (this.mState == 2));
    this.mAeAwbLock = false;
    this.mListener.setFocusParameters();
  }

  public void onSingleTapUp(int paramInt1, int paramInt2)
  {
    if ((!this.mInitialized) || (this.mState == 2));
    int i;
    int j;
    do
    {
      return;
      if ((this.mFocusArea != null) && (((this.mState == 1) || (this.mState == 3) || (this.mState == 4))))
        cancelAutoFocus();
      i = this.mPieRenderer.getSize();
      j = this.mPieRenderer.getSize();
    }
    while ((i == 0) || (this.mPieRenderer.getWidth() == 0) || (this.mPieRenderer.getHeight() == 0));
    int k = this.mPreviewWidth;
    int l = this.mPreviewHeight;
    if (this.mFocusAreaSupported)
      initializeFocusAreas(i, j, paramInt1, paramInt2, k, l);
    if (this.mMeteringAreaSupported)
      initializeMeteringAreas(i, j, paramInt1, paramInt2, k, l);
    this.mPieRenderer.setFocus(paramInt1, paramInt2);
    this.mListener.stopFaceDetection();
    this.mListener.setFocusParameters();
    if (this.mFocusAreaSupported)
    {
      autoFocus();
      return;
    }
    updateFocusUI();
    this.mHandler.removeMessages(0);
    this.mHandler.sendEmptyMessageDelayed(0, 3000L);
  }

  public void overrideFocusMode(String paramString)
  {
    this.mOverrideFocusMode = paramString;
  }

  public void removeMessages()
  {
    this.mHandler.removeMessages(0);
  }

  public void resetTouchFocus()
  {
    if (!this.mInitialized)
      return;
    this.mPieRenderer.clear();
    this.mFocusArea = null;
    this.mMeteringArea = null;
  }

  public void setAeAwbLock(boolean paramBoolean)
  {
    this.mAeAwbLock = paramBoolean;
  }

  public void setDisplayOrientation(int paramInt)
  {
    this.mDisplayOrientation = paramInt;
    setMatrix();
  }

  public void setFaceView(FaceView paramFaceView)
  {
    this.mFaceView = paramFaceView;
  }

  public void setFocusRenderer(PieRenderer paramPieRenderer)
  {
    this.mPieRenderer = paramPieRenderer;
    if (this.mMatrix != null);
    for (int i = 1; ; i = 0)
    {
      this.mInitialized = i;
      return;
    }
  }

  public void setMirror(boolean paramBoolean)
  {
    this.mMirror = paramBoolean;
    setMatrix();
  }

  public void setParameters(Camera.Parameters paramParameters)
  {
    if (paramParameters == null)
      return;
    this.mParameters = paramParameters;
    this.mFocusAreaSupported = Util.isFocusAreaSupported(paramParameters);
    this.mMeteringAreaSupported = Util.isMeteringAreaSupported(paramParameters);
    if ((Util.isAutoExposureLockSupported(this.mParameters)) || (Util.isAutoWhiteBalanceLockSupported(this.mParameters)));
    for (int i = 1; ; i = 0)
    {
      this.mLockAeAwbNeeded = i;
      return;
    }
  }

  public void setPreviewSize(int paramInt1, int paramInt2)
  {
    if ((this.mPreviewWidth == paramInt1) && (this.mPreviewHeight == paramInt2))
      return;
    this.mPreviewWidth = paramInt1;
    this.mPreviewHeight = paramInt2;
    setMatrix();
  }

  public void updateFocusUI()
  {
    if (!this.mInitialized);
    label27: Object localObject;
    do
    {
      return;
      int i;
      if ((this.mFaceView != null) && (this.mFaceView.faceExists()))
      {
        i = 1;
        if (i == 0)
          break label62;
      }
      for (localObject = this.mFaceView; ; localObject = this.mPieRenderer)
      {
        if (this.mState != 0)
          break label77;
        if (this.mFocusArea != null)
          break;
        ((FocusIndicator)localObject).clear();
        return;
        i = 0;
        label62: break label27:
      }
      ((FocusIndicator)localObject).showStart();
      return;
      if ((this.mState == 1) || (this.mState == 2))
      {
        label77: ((FocusIndicator)localObject).showStart();
        return;
      }
      if ("continuous-picture".equals(this.mFocusMode))
      {
        ((FocusIndicator)localObject).showSuccess(false);
        return;
      }
      if (this.mState != 3)
        continue;
      ((FocusIndicator)localObject).showSuccess(false);
      return;
    }
    while (this.mState != 4);
    ((FocusIndicator)localObject).showFail(false);
  }

  public static abstract interface Listener
  {
    public abstract void autoFocus();

    public abstract void cancelAutoFocus();

    public abstract boolean capture();

    public abstract void setFocusParameters();

    public abstract void startFaceDetection();

    public abstract void stopFaceDetection();
  }

  private class MainHandler extends Handler
  {
    public MainHandler(Looper arg2)
    {
      super(localLooper);
    }

    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default:
        return;
      case 0:
      }
      FocusOverlayManager.this.cancelAutoFocus();
      FocusOverlayManager.this.mListener.startFaceDetection();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.FocusOverlayManager
 * JD-Core Version:    0.5.4
 */