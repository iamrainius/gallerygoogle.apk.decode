package com.android.camera;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.android.gallery3d.common.ApiHelper;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

@TargetApi(11)
public class MosaicPreviewRenderer
{
  private static final int[] CONFIG_SPEC = { 12352, 4, 12324, 8, 12323, 8, 12322, 8, 12344 };
  private EGL10 mEgl;
  private EGLConfig mEglConfig;
  private EGLContext mEglContext;
  private EGLDisplay mEglDisplay;
  private EGLHandler mEglHandler;
  private EGLSurface mEglSurface;
  private HandlerThread mEglThread;
  private ConditionVariable mEglThreadBlockVar = new ConditionVariable();
  private GL10 mGl;
  private int mHeight;
  private SurfaceTexture mInputSurfaceTexture;
  private boolean mIsLandscape = true;
  private SurfaceTexture mMosaicOutputSurfaceTexture;
  private final float[] mTransformMatrix = new float[16];
  private int mWidth;

  public MosaicPreviewRenderer(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.mMosaicOutputSurfaceTexture = paramSurfaceTexture;
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    this.mIsLandscape = paramBoolean;
    this.mEglThread = new HandlerThread("PanoramaRealtimeRenderer");
    this.mEglThread.start();
    this.mEglHandler = new EGLHandler(this.mEglThread.getLooper());
    this.mEglHandler.sendMessageSync(0);
  }

  private static EGLConfig chooseConfig(EGL10 paramEGL10, EGLDisplay paramEGLDisplay)
  {
    int[] arrayOfInt = new int[1];
    if (!paramEGL10.eglChooseConfig(paramEGLDisplay, CONFIG_SPEC, null, 0, arrayOfInt))
      throw new IllegalArgumentException("eglChooseConfig failed");
    int i = arrayOfInt[0];
    if (i <= 0)
      throw new IllegalArgumentException("No configs match configSpec");
    EGLConfig[] arrayOfEGLConfig = new EGLConfig[i];
    if (!paramEGL10.eglChooseConfig(paramEGLDisplay, CONFIG_SPEC, arrayOfEGLConfig, i, arrayOfInt))
      throw new IllegalArgumentException("eglChooseConfig#2 failed");
    return arrayOfEGLConfig[0];
  }

  private void draw()
  {
    MosaicRenderer.step();
  }

  public void alignFrameSync()
  {
    this.mEglHandler.sendMessageSync(3);
  }

  public SurfaceTexture getInputSurfaceTexture()
  {
    return this.mInputSurfaceTexture;
  }

  public void release()
  {
    this.mEglHandler.sendEmptyMessage(4);
  }

  public void showPreviewFrame()
  {
    this.mEglHandler.sendEmptyMessage(2);
  }

  public void showPreviewFrameSync()
  {
    this.mEglHandler.sendMessageSync(1);
  }

  private class EGLHandler extends Handler
  {
    public EGLHandler(Looper arg2)
    {
      super(localLooper);
    }

    private void doAlignFrame()
    {
      MosaicPreviewRenderer.this.mInputSurfaceTexture.updateTexImage();
      MosaicPreviewRenderer.this.mInputSurfaceTexture.getTransformMatrix(MosaicPreviewRenderer.this.mTransformMatrix);
      MosaicRenderer.setWarping(true);
      MosaicRenderer.preprocess(MosaicPreviewRenderer.this.mTransformMatrix);
      MosaicRenderer.transferGPUtoCPU();
      MosaicRenderer.updateMatrix();
      MosaicPreviewRenderer.this.draw();
      MosaicPreviewRenderer.this.mEgl.eglSwapBuffers(MosaicPreviewRenderer.this.mEglDisplay, MosaicPreviewRenderer.this.mEglSurface);
    }

    private void doInitGL()
    {
      MosaicPreviewRenderer.access$602(MosaicPreviewRenderer.this, (EGL10)EGLContext.getEGL());
      MosaicPreviewRenderer.access$402(MosaicPreviewRenderer.this, MosaicPreviewRenderer.this.mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY));
      if (MosaicPreviewRenderer.this.mEglDisplay == EGL10.EGL_NO_DISPLAY)
        throw new RuntimeException("eglGetDisplay failed");
      int[] arrayOfInt1 = new int[2];
      if (!MosaicPreviewRenderer.this.mEgl.eglInitialize(MosaicPreviewRenderer.this.mEglDisplay, arrayOfInt1))
        throw new RuntimeException("eglInitialize failed");
      Log.v("MosaicPreviewRenderer", "EGL version: " + arrayOfInt1[0] + '.' + arrayOfInt1[1]);
      int[] arrayOfInt2 = { 12440, 2, 12344 };
      MosaicPreviewRenderer.access$702(MosaicPreviewRenderer.this, MosaicPreviewRenderer.access$800(MosaicPreviewRenderer.this.mEgl, MosaicPreviewRenderer.this.mEglDisplay));
      MosaicPreviewRenderer.access$902(MosaicPreviewRenderer.this, MosaicPreviewRenderer.this.mEgl.eglCreateContext(MosaicPreviewRenderer.this.mEglDisplay, MosaicPreviewRenderer.this.mEglConfig, EGL10.EGL_NO_CONTEXT, arrayOfInt2));
      if ((MosaicPreviewRenderer.this.mEglContext == null) || (MosaicPreviewRenderer.this.mEglContext == EGL10.EGL_NO_CONTEXT))
        throw new RuntimeException("failed to createContext");
      MosaicPreviewRenderer.access$502(MosaicPreviewRenderer.this, MosaicPreviewRenderer.this.mEgl.eglCreateWindowSurface(MosaicPreviewRenderer.this.mEglDisplay, MosaicPreviewRenderer.this.mEglConfig, MosaicPreviewRenderer.this.mMosaicOutputSurfaceTexture, null));
      if ((MosaicPreviewRenderer.this.mEglSurface == null) || (MosaicPreviewRenderer.this.mEglSurface == EGL10.EGL_NO_SURFACE))
        throw new RuntimeException("failed to createWindowSurface");
      if (!MosaicPreviewRenderer.this.mEgl.eglMakeCurrent(MosaicPreviewRenderer.this.mEglDisplay, MosaicPreviewRenderer.this.mEglSurface, MosaicPreviewRenderer.this.mEglSurface, MosaicPreviewRenderer.this.mEglContext))
        throw new RuntimeException("failed to eglMakeCurrent");
      MosaicPreviewRenderer.access$1102(MosaicPreviewRenderer.this, (GL10)MosaicPreviewRenderer.this.mEglContext.getGL());
      MosaicPreviewRenderer.access$102(MosaicPreviewRenderer.this, new SurfaceTexture(MosaicRenderer.init()));
      MosaicRenderer.reset(MosaicPreviewRenderer.this.mWidth, MosaicPreviewRenderer.this.mHeight, MosaicPreviewRenderer.this.mIsLandscape);
    }

    private void doRelease()
    {
      MosaicPreviewRenderer.this.mEgl.eglDestroySurface(MosaicPreviewRenderer.this.mEglDisplay, MosaicPreviewRenderer.this.mEglSurface);
      MosaicPreviewRenderer.this.mEgl.eglDestroyContext(MosaicPreviewRenderer.this.mEglDisplay, MosaicPreviewRenderer.this.mEglContext);
      MosaicPreviewRenderer.this.mEgl.eglMakeCurrent(MosaicPreviewRenderer.this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
      MosaicPreviewRenderer.this.mEgl.eglTerminate(MosaicPreviewRenderer.this.mEglDisplay);
      MosaicPreviewRenderer.access$502(MosaicPreviewRenderer.this, null);
      MosaicPreviewRenderer.access$902(MosaicPreviewRenderer.this, null);
      MosaicPreviewRenderer.access$402(MosaicPreviewRenderer.this, null);
      releaseSurfaceTexture(MosaicPreviewRenderer.this.mInputSurfaceTexture);
      MosaicPreviewRenderer.this.mEglThread.quit();
    }

    private void doShowPreviewFrame()
    {
      MosaicPreviewRenderer.this.mInputSurfaceTexture.updateTexImage();
      MosaicPreviewRenderer.this.mInputSurfaceTexture.getTransformMatrix(MosaicPreviewRenderer.this.mTransformMatrix);
      MosaicRenderer.setWarping(false);
      MosaicRenderer.preprocess(MosaicPreviewRenderer.this.mTransformMatrix);
      MosaicRenderer.updateMatrix();
      MosaicPreviewRenderer.this.draw();
      MosaicPreviewRenderer.this.mEgl.eglSwapBuffers(MosaicPreviewRenderer.this.mEglDisplay, MosaicPreviewRenderer.this.mEglSurface);
    }

    @TargetApi(14)
    private void releaseSurfaceTexture(SurfaceTexture paramSurfaceTexture)
    {
      if (!ApiHelper.HAS_RELEASE_SURFACE_TEXTURE)
        return;
      paramSurfaceTexture.release();
    }

    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default:
        return;
      case 0:
        doInitGL();
        MosaicPreviewRenderer.this.mEglThreadBlockVar.open();
        return;
      case 1:
        doShowPreviewFrame();
        MosaicPreviewRenderer.this.mEglThreadBlockVar.open();
        return;
      case 2:
        doShowPreviewFrame();
        return;
      case 3:
        doAlignFrame();
        MosaicPreviewRenderer.this.mEglThreadBlockVar.open();
        return;
      case 4:
      }
      doRelease();
    }

    public void sendMessageSync(int paramInt)
    {
      MosaicPreviewRenderer.this.mEglThreadBlockVar.close();
      sendEmptyMessage(paramInt);
      MosaicPreviewRenderer.this.mEglThreadBlockVar.block();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.MosaicPreviewRenderer
 * JD-Core Version:    0.5.4
 */