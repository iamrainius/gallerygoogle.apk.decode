package com.android.gallery3d.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Process;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import com.android.gallery3d.anim.CanvasAnimation;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.MotionEventHelper;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class GLRootView extends GLSurfaceView
  implements GLSurfaceView.Renderer, GLRoot
{
  private final ArrayList<CanvasAnimation> mAnimations = new ArrayList();
  private GLCanvas mCanvas;
  private int mCompensation;
  private Matrix mCompensationMatrix = new Matrix();
  private GLView mContentView;
  private int mDisplayRotation;
  private final GalleryEGLConfigChooser mEglConfigChooser = new GalleryEGLConfigChooser();
  private boolean mFirstDraw = true;
  private int mFlags = 2;
  private int mFrameCount = 0;
  private long mFrameCountingStart = 0L;
  private boolean mFreeze;
  private final Condition mFreezeCondition = this.mRenderLock.newCondition();
  private GL11 mGL;
  private final ArrayDeque<GLRoot.OnGLIdleListener> mIdleListeners = new ArrayDeque();
  private final IdleRunner mIdleRunner = new IdleRunner(null);
  private boolean mInDownState = false;
  private int mInvalidateColor = 0;
  private OrientationSource mOrientationSource;
  private final ReentrantLock mRenderLock = new ReentrantLock();
  private volatile boolean mRenderRequested = false;
  private Runnable mRequestRenderOnAnimationFrame = new Runnable()
  {
    public void run()
    {
      GLRootView.this.superRequestRender();
    }
  };

  public GLRootView(Context paramContext)
  {
    this(paramContext, null);
  }

  public GLRootView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setBackgroundDrawable(null);
    setEGLConfigChooser(this.mEglConfigChooser);
    setRenderer(this);
    if (ApiHelper.USE_888_PIXEL_FORMAT)
    {
      getHolder().setFormat(3);
      return;
    }
    getHolder().setFormat(4);
  }

  private void layoutContentPane()
  {
    this.mFlags = (0xFFFFFFFD & this.mFlags);
    int i = getWidth();
    int j = getHeight();
    int l;
    int k;
    if (this.mOrientationSource != null)
    {
      l = this.mOrientationSource.getDisplayRotation();
      k = this.mOrientationSource.getCompensation();
      if (this.mCompensation != k)
      {
        label49: this.mCompensation = k;
        if (this.mCompensation % 180 == 0)
          break label230;
        this.mCompensationMatrix.setRotate(this.mCompensation);
        this.mCompensationMatrix.preTranslate(-i / 2, -j / 2);
        this.mCompensationMatrix.postTranslate(j / 2, i / 2);
      }
    }
    while (true)
    {
      this.mDisplayRotation = l;
      if (this.mCompensation % 180 != 0)
      {
        int i1 = i;
        i = j;
        j = i1;
      }
      Log.i("GLRootView", "layout content pane " + i + "x" + j + " (compensation " + this.mCompensation + ")");
      if ((this.mContentView != null) && (i != 0) && (j != 0))
        this.mContentView.layout(0, 0, i, j);
      return;
      k = 0;
      l = 0;
      break label49:
      label230: this.mCompensationMatrix.setRotate(this.mCompensation, i / 2, j / 2);
    }
  }

  private void onDrawFrameLocked(GL10 paramGL10)
  {
    this.mCanvas.deleteRecycledResources();
    UploadedTexture.resetUploadLimit();
    this.mRenderRequested = false;
    if ((0x2 & this.mFlags) != 0)
      layoutContentPane();
    this.mCanvas.save(-1);
    rotateCanvas(-this.mCompensation);
    if (this.mContentView != null)
      this.mContentView.render(this.mCanvas);
    this.mCanvas.restore();
    if (!this.mAnimations.isEmpty())
    {
      long l = AnimationTime.get();
      int i = 0;
      int j = this.mAnimations.size();
      while (i < j)
      {
        ((CanvasAnimation)this.mAnimations.get(i)).setStartTime(l);
        ++i;
      }
      this.mAnimations.clear();
    }
    if (UploadedTexture.uploadLimitReached())
      requestRender();
    synchronized (this.mIdleListeners)
    {
      if (!this.mIdleListeners.isEmpty())
        this.mIdleRunner.enable();
      return;
    }
  }

  private void rotateCanvas(int paramInt)
  {
    if (paramInt == 0)
      return;
    int i = getWidth();
    int j = getHeight();
    int k = i / 2;
    int l = j / 2;
    this.mCanvas.translate(k, l);
    this.mCanvas.rotate(paramInt, 0.0F, 0.0F, 1.0F);
    if (paramInt % 180 != 0)
    {
      this.mCanvas.translate(-l, -k);
      return;
    }
    this.mCanvas.translate(-k, -l);
  }

  private void superRequestRender()
  {
    super.requestRender();
  }

  public void addOnGLIdleListener(GLRoot.OnGLIdleListener paramOnGLIdleListener)
  {
    synchronized (this.mIdleListeners)
    {
      this.mIdleListeners.addLast(paramOnGLIdleListener);
      this.mIdleRunner.enable();
      return;
    }
  }

  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!isEnabled())
      return false;
    int i = paramMotionEvent.getAction();
    if ((i == 3) || (i == 1))
    {
      this.mInDownState = false;
      if (this.mCompensation != 0)
        paramMotionEvent = MotionEventHelper.transformEvent(paramMotionEvent, this.mCompensationMatrix);
      this.mRenderLock.lock();
    }
    try
    {
      GLView localGLView = this.mContentView;
      int j = 0;
      if (localGLView != null)
      {
        boolean bool = this.mContentView.dispatchTouchEvent(paramMotionEvent);
        j = 0;
        if (bool)
          j = 1;
      }
      if ((i == 0) && (j != 0))
        this.mInDownState = true;
      return j;
      if (!this.mInDownState);
      return false;
    }
    finally
    {
      this.mRenderLock.unlock();
    }
  }

  protected void finalize()
    throws Throwable
  {
    try
    {
      unfreeze();
      return;
    }
    finally
    {
      super.finalize();
    }
  }

  public void freeze()
  {
    this.mRenderLock.lock();
    this.mFreeze = true;
    this.mRenderLock.unlock();
  }

  public int getCompensation()
  {
    return this.mCompensation;
  }

  public Matrix getCompensationMatrix()
  {
    return this.mCompensationMatrix;
  }

  public int getDisplayRotation()
  {
    return this.mDisplayRotation;
  }

  public void lockRenderThread()
  {
    this.mRenderLock.lock();
  }

  protected void onDetachedFromWindow()
  {
    unfreeze();
    super.onDetachedFromWindow();
  }

  public void onDrawFrame(GL10 paramGL10)
  {
    AnimationTime.update();
    this.mRenderLock.lock();
    while (this.mFreeze)
      this.mFreezeCondition.awaitUninterruptibly();
    try
    {
      onDrawFrameLocked(paramGL10);
      this.mRenderLock.unlock();
      if (this.mFirstDraw)
        this.mFirstDraw = false;
      return;
    }
    finally
    {
      this.mRenderLock.unlock();
    }
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!paramBoolean)
      return;
    requestLayoutContentPane();
  }

  public void onPause()
  {
    unfreeze();
    super.onPause();
  }

  public void onSurfaceChanged(GL10 paramGL10, int paramInt1, int paramInt2)
  {
    Log.i("GLRootView", "onSurfaceChanged: " + paramInt1 + "x" + paramInt2 + ", gl10: " + paramGL10.toString());
    Process.setThreadPriority(-4);
    GalleryUtils.setRenderThread();
    GL11 localGL11 = (GL11)paramGL10;
    if (this.mGL == localGL11);
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      this.mCanvas.setSize(paramInt1, paramInt2);
      return;
    }
  }

  public void onSurfaceCreated(GL10 paramGL10, EGLConfig paramEGLConfig)
  {
    GL11 localGL11 = (GL11)paramGL10;
    if (this.mGL != null)
      Log.i("GLRootView", "GLObject has changed from " + this.mGL + " to " + localGL11);
    this.mRenderLock.lock();
    try
    {
      this.mGL = localGL11;
      this.mCanvas = new GLCanvasImpl(localGL11);
      BasicTexture.invalidateAllTextures();
      this.mRenderLock.unlock();
      return;
    }
    finally
    {
      this.mRenderLock.unlock();
    }
  }

  public void requestLayoutContentPane()
  {
    this.mRenderLock.lock();
    try
    {
      if (this.mContentView != null)
      {
        int i = this.mFlags;
        if ((i & 0x2) == 0)
          break label33;
      }
      return;
      label33: int j = this.mFlags;
      if ((j & 0x1) == 0)
        return;
      this.mFlags = (0x2 | this.mFlags);
      requestRender();
      return;
    }
    finally
    {
      this.mRenderLock.unlock();
    }
  }

  public void requestRender()
  {
    if (this.mRenderRequested)
      return;
    this.mRenderRequested = true;
    if (ApiHelper.HAS_POST_ON_ANIMATION)
    {
      postOnAnimation(this.mRequestRenderOnAnimationFrame);
      return;
    }
    super.requestRender();
  }

  public void requestRenderForced()
  {
    superRequestRender();
  }

  public void setContentPane(GLView paramGLView)
  {
    if (this.mContentView == paramGLView);
    do
    {
      return;
      if (this.mContentView != null)
      {
        if (this.mInDownState)
        {
          long l = SystemClock.uptimeMillis();
          MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
          this.mContentView.dispatchTouchEvent(localMotionEvent);
          localMotionEvent.recycle();
          this.mInDownState = false;
        }
        this.mContentView.detachFromRoot();
        BasicTexture.yieldAllTextures();
      }
      this.mContentView = paramGLView;
    }
    while (paramGLView == null);
    paramGLView.attachToRoot(this);
    requestLayoutContentPane();
  }

  @TargetApi(16)
  public void setLightsOutMode(boolean paramBoolean)
  {
    if (!ApiHelper.HAS_SET_SYSTEM_UI_VISIBILITY)
      return;
    int i = 0;
    if (paramBoolean)
    {
      i = 1;
      if (ApiHelper.HAS_VIEW_SYSTEM_UI_FLAG_LAYOUT_STABLE)
        i |= 260;
    }
    setSystemUiVisibility(i);
  }

  public void setOrientationSource(OrientationSource paramOrientationSource)
  {
    this.mOrientationSource = paramOrientationSource;
  }

  public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3)
  {
    unfreeze();
    super.surfaceChanged(paramSurfaceHolder, paramInt1, paramInt2, paramInt3);
  }

  public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
  {
    unfreeze();
    super.surfaceCreated(paramSurfaceHolder);
  }

  public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
  {
    unfreeze();
    super.surfaceDestroyed(paramSurfaceHolder);
  }

  public void unfreeze()
  {
    this.mRenderLock.lock();
    this.mFreeze = false;
    this.mFreezeCondition.signalAll();
    this.mRenderLock.unlock();
  }

  public void unlockRenderThread()
  {
    this.mRenderLock.unlock();
  }

  private class IdleRunner
    implements Runnable
  {
    private boolean mActive = false;

    private IdleRunner()
    {
    }

    public void enable()
    {
      if (this.mActive)
        return;
      this.mActive = true;
      GLRootView.this.queueEvent(this);
    }

    public void run()
    {
      GLRoot.OnGLIdleListener localOnGLIdleListener;
      synchronized (GLRootView.this.mIdleListeners)
      {
        this.mActive = false;
        if (GLRootView.this.mIdleListeners.isEmpty())
          return;
        localOnGLIdleListener = (GLRoot.OnGLIdleListener)GLRootView.this.mIdleListeners.removeFirst();
        GLRootView.this.mRenderLock.lock();
      }
      try
      {
        boolean bool = localOnGLIdleListener.onGLIdle(GLRootView.this.mCanvas, GLRootView.this.mRenderRequested);
        GLRootView.this.mRenderLock.unlock();
        ArrayDeque localArrayDeque2 = GLRootView.this.mIdleListeners;
        monitorenter;
        if (bool);
        try
        {
          GLRootView.this.mIdleListeners.addLast(localOnGLIdleListener);
          if ((!GLRootView.this.mRenderRequested) && (!GLRootView.this.mIdleListeners.isEmpty()))
            enable();
          return;
        }
        finally
        {
          monitorexit;
        }
        localObject1 = finally;
        monitorexit;
        throw localObject1;
      }
      finally
      {
        GLRootView.this.mRenderLock.unlock();
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.GLRootView
 * JD-Core Version:    0.5.4
 */