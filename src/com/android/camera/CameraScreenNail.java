package com.android.camera;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.opengl.Matrix;
import android.util.Log;
import com.android.gallery3d.ui.GLCanvas;
import com.android.gallery3d.ui.RawTexture;
import com.android.gallery3d.ui.SurfaceTextureScreenNail;

@TargetApi(11)
public class CameraScreenNail extends SurfaceTextureScreenNail
{
  private int mAnimState = 0;
  private RawTexture mAnimTexture;
  private CaptureAnimManager mCaptureAnimManager = new CaptureAnimManager();
  private boolean mEnableAspectRatioClamping = false;
  private boolean mFirstFrameArrived;
  private boolean mFullScreen;
  private Listener mListener;
  private Object mLock = new Object();
  private OnFrameDrawnListener mOneTimeFrameDrawnListener;
  private int mRenderHeight;
  private int mRenderWidth;
  private float mScaleX = 1.0F;
  private float mScaleY = 1.0F;
  private SwitchAnimManager mSwitchAnimManager = new SwitchAnimManager();
  private final float[] mTextureTransformMatrix = new float[16];
  private int mUncroppedRenderHeight;
  private int mUncroppedRenderWidth;
  private boolean mVisible;

  public CameraScreenNail(Listener paramListener)
  {
    this.mListener = paramListener;
  }

  private void callbackIfNeeded()
  {
    if (this.mOneTimeFrameDrawnListener == null)
      return;
    this.mOneTimeFrameDrawnListener.onFrameDrawn(this);
    this.mOneTimeFrameDrawnListener = null;
  }

  private void copyPreviewTexture(GLCanvas paramGLCanvas)
  {
    int i = this.mAnimTexture.getWidth();
    int j = this.mAnimTexture.getHeight();
    paramGLCanvas.beginRenderTarget(this.mAnimTexture);
    paramGLCanvas.translate(0.0F, j);
    paramGLCanvas.scale(1.0F, -1.0F, 1.0F);
    getSurfaceTexture().getTransformMatrix(this.mTextureTransformMatrix);
    updateTransformMatrix(this.mTextureTransformMatrix);
    paramGLCanvas.drawTexture(this.mExtTexture, this.mTextureTransformMatrix, 0, 0, i, j);
    paramGLCanvas.endRenderTarget();
  }

  private int getTextureHeight()
  {
    return super.getHeight();
  }

  private int getTextureWidth()
  {
    return super.getWidth();
  }

  private void setPreviewLayoutSize(int paramInt1, int paramInt2)
  {
    Log.i("CAM_ScreenNail", "preview layout size: " + paramInt1 + "/" + paramInt2);
    this.mRenderWidth = paramInt1;
    this.mRenderHeight = paramInt2;
    updateRenderSize();
  }

  private void updateRenderSize()
  {
    if (!this.mEnableAspectRatioClamping)
    {
      this.mScaleY = 1.0F;
      this.mScaleX = 1.0F;
      this.mUncroppedRenderWidth = getTextureWidth();
      this.mUncroppedRenderHeight = getTextureHeight();
      Log.i("CAM_ScreenNail", "aspect ratio clamping disabled");
      return;
    }
    float f1;
    label65: float f2;
    float f3;
    if (getTextureWidth() > getTextureHeight())
    {
      f1 = getTextureWidth() / getTextureHeight();
      if (this.mRenderWidth <= this.mRenderHeight)
        break label204;
      f2 = Math.max(this.mRenderWidth, (int)(f1 * this.mRenderHeight));
      f3 = Math.max(this.mRenderHeight, (int)(this.mRenderWidth / f1));
    }
    while (true)
    {
      this.mScaleX = (this.mRenderWidth / f2);
      this.mScaleY = (this.mRenderHeight / f3);
      this.mUncroppedRenderWidth = Math.round(f2);
      this.mUncroppedRenderHeight = Math.round(f3);
      Log.i("CAM_ScreenNail", "aspect ratio clamping enabled, surfaceTexture scale: " + this.mScaleX + ", " + this.mScaleY);
      return;
      f1 = getTextureHeight() / getTextureWidth();
      break label65:
      label204: f2 = Math.max(this.mRenderWidth, (int)(this.mRenderHeight / f1));
      f3 = Math.max(this.mRenderHeight, (int)(f1 * this.mRenderWidth));
    }
  }

  public void acquireSurfaceTexture()
  {
    synchronized (this.mLock)
    {
      this.mFirstFrameArrived = false;
      super.acquireSurfaceTexture();
      this.mAnimTexture = new RawTexture(getTextureWidth(), getTextureHeight(), true);
      return;
    }
  }

  public void animateCapture(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mCaptureAnimManager.setOrientation(paramInt);
      this.mCaptureAnimManager.animateFlashAndSlide();
      this.mListener.requestRender();
      this.mAnimState = 1;
      return;
    }
  }

  public void animateFlash(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mCaptureAnimManager.setOrientation(paramInt);
      this.mCaptureAnimManager.animateFlash();
      this.mListener.requestRender();
      this.mAnimState = 1;
      return;
    }
  }

  public void animateSlide()
  {
    synchronized (this.mLock)
    {
      if (this.mAnimState != 2)
      {
        Log.v("CAM_ScreenNail", "Cannot animateSlide outside of animateCapture! Animation state = " + this.mAnimState);
        return;
      }
      this.mCaptureAnimManager.animateSlide();
      this.mListener.requestRender();
      return;
    }
  }

  public void animateSwitchCamera()
  {
    Log.v("CAM_ScreenNail", "animateSwitchCamera");
    synchronized (this.mLock)
    {
      if (this.mAnimState == 4)
        this.mAnimState = 5;
      return;
    }
  }

  public void copyTexture()
  {
    synchronized (this.mLock)
    {
      this.mListener.requestRender();
      this.mAnimState = 3;
      return;
    }
  }

  public void directDraw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.draw(paramGLCanvas, paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public void draw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    while (true)
    {
      SurfaceTexture localSurfaceTexture;
      synchronized (this.mLock)
      {
        if (!this.mVisible)
          this.mVisible = true;
        localSurfaceTexture = getSurfaceTexture();
        if ((localSurfaceTexture == null) || (!this.mFirstFrameArrived))
          return;
        switch (this.mAnimState)
        {
        case 2:
        default:
          if ((this.mAnimState == 2) || (this.mAnimState == 7))
          {
            if (this.mAnimState != 2)
              break label293;
            if (this.mFullScreen)
              break label275;
            bool = false;
            if (!bool)
              break label317;
            this.mListener.requestRender();
          }
          callbackIfNeeded();
          return;
        case 0:
        case 3:
        case 4:
        case 5:
        case 6:
        case 1:
        }
      }
      super.draw(paramGLCanvas, paramInt1, paramInt2, paramInt3, paramInt4);
      continue;
      copyPreviewTexture(paramGLCanvas);
      this.mSwitchAnimManager.setReviewDrawingSize(paramInt3, paramInt4);
      this.mListener.onPreviewTextureCopied();
      this.mAnimState = 4;
      localSurfaceTexture.updateTexImage();
      this.mSwitchAnimManager.drawDarkPreview(paramGLCanvas, paramInt1, paramInt2, paramInt3, paramInt4, this.mAnimTexture);
      continue;
      this.mSwitchAnimManager.startAnimation();
      this.mAnimState = 7;
      continue;
      copyPreviewTexture(paramGLCanvas);
      this.mListener.onCaptureTextureCopied();
      this.mCaptureAnimManager.startAnimation(paramInt1, paramInt2, paramInt3, paramInt4);
      this.mAnimState = 2;
      continue;
      label275: boolean bool = this.mCaptureAnimManager.drawAnimation(paramGLCanvas, this, this.mAnimTexture);
      continue;
      label293: bool = this.mSwitchAnimManager.drawAnimation(paramGLCanvas, paramInt1, paramInt2, paramInt3, paramInt4, this, this.mAnimTexture);
      continue;
      label317: this.mAnimState = 0;
      super.draw(paramGLCanvas, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }

  public void enableAspectRatioClamping()
  {
    this.mEnableAspectRatioClamping = true;
    updateRenderSize();
  }

  public int getHeight()
  {
    if (this.mEnableAspectRatioClamping)
      return this.mRenderHeight;
    return getTextureHeight();
  }

  public int getUncroppedRenderHeight()
  {
    return this.mUncroppedRenderHeight;
  }

  public int getUncroppedRenderWidth()
  {
    return this.mUncroppedRenderWidth;
  }

  public int getWidth()
  {
    if (this.mEnableAspectRatioClamping)
      return this.mRenderWidth;
    return getTextureWidth();
  }

  public void noDraw()
  {
    synchronized (this.mLock)
    {
      this.mVisible = false;
      return;
    }
  }

  public void onFrameAvailable(SurfaceTexture paramSurfaceTexture)
  {
    synchronized (this.mLock)
    {
      if (getSurfaceTexture() != paramSurfaceTexture)
        return;
      this.mFirstFrameArrived = true;
      if (this.mVisible)
      {
        if (this.mAnimState == 5)
          this.mAnimState = 6;
        this.mListener.requestRender();
      }
      return;
    }
  }

  public void recycle()
  {
    synchronized (this.mLock)
    {
      this.mVisible = false;
      return;
    }
  }

  public void releaseSurfaceTexture()
  {
    synchronized (this.mLock)
    {
      super.releaseSurfaceTexture();
      this.mAnimState = 0;
      return;
    }
  }

  public void setFullScreen(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      this.mFullScreen = paramBoolean;
      return;
    }
  }

  public void setOneTimeOnFrameDrawnListener(OnFrameDrawnListener paramOnFrameDrawnListener)
  {
    synchronized (this.mLock)
    {
      this.mFirstFrameArrived = false;
      this.mOneTimeFrameDrawnListener = paramOnFrameDrawnListener;
      return;
    }
  }

  public void setPreviewFrameLayoutSize(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      this.mSwitchAnimManager.setPreviewFrameLayoutSize(paramInt1, paramInt2);
      setPreviewLayoutSize(paramInt1, paramInt2);
      return;
    }
  }

  public void setSize(int paramInt1, int paramInt2)
  {
    super.setSize(paramInt1, paramInt2);
    this.mEnableAspectRatioClamping = false;
    if (this.mRenderWidth == 0)
    {
      this.mRenderWidth = paramInt1;
      this.mRenderHeight = paramInt2;
    }
    updateRenderSize();
  }

  protected void updateTransformMatrix(float[] paramArrayOfFloat)
  {
    super.updateTransformMatrix(paramArrayOfFloat);
    Matrix.translateM(paramArrayOfFloat, 0, 0.5F, 0.5F, 0.0F);
    Matrix.scaleM(paramArrayOfFloat, 0, this.mScaleX, this.mScaleY, 1.0F);
    Matrix.translateM(paramArrayOfFloat, 0, -0.5F, -0.5F, 0.0F);
  }

  public static abstract interface Listener
  {
    public abstract void onCaptureTextureCopied();

    public abstract void onPreviewTextureCopied();

    public abstract void requestRender();
  }

  public static abstract interface OnFrameDrawnListener
  {
    public abstract void onFrameDrawn(CameraScreenNail paramCameraScreenNail);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.CameraScreenNail
 * JD-Core Version:    0.5.4
 */