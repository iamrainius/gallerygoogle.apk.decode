package com.android.gallery3d.ui;

import android.os.ConditionVariable;
import com.android.gallery3d.app.AbstractGalleryActivity;

public class PreparePageFadeoutTexture
  implements GLRoot.OnGLIdleListener
{
  private boolean mCancelled = false;
  private ConditionVariable mResultReady = new ConditionVariable(false);
  private GLView mRootPane;
  private RawTexture mTexture;

  public PreparePageFadeoutTexture(GLView paramGLView)
  {
    int i = paramGLView.getWidth();
    int j = paramGLView.getHeight();
    if ((i == 0) || (j == 0))
    {
      this.mCancelled = true;
      return;
    }
    this.mTexture = new RawTexture(i, j, true);
    this.mRootPane = paramGLView;
  }

  public static void prepareFadeOutTexture(AbstractGalleryActivity paramAbstractGalleryActivity, GLView paramGLView)
  {
    PreparePageFadeoutTexture localPreparePageFadeoutTexture = new PreparePageFadeoutTexture(paramGLView);
    if (localPreparePageFadeoutTexture.isCancelled())
      return;
    GLRoot localGLRoot = paramAbstractGalleryActivity.getGLRoot();
    localGLRoot.unlockRenderThread();
    try
    {
      localGLRoot.addOnGLIdleListener(localPreparePageFadeoutTexture);
      RawTexture localRawTexture = localPreparePageFadeoutTexture.get();
      localGLRoot.lockRenderThread();
      if (localRawTexture != null);
      return;
    }
    finally
    {
      localGLRoot.lockRenderThread();
    }
  }

  public RawTexture get()
  {
    monitorenter;
    RawTexture localRawTexture;
    try
    {
      boolean bool = this.mCancelled;
      localRawTexture = null;
      if (bool);
      while (true)
      {
        return localRawTexture;
        if (!this.mResultReady.block(200L))
          break;
        localRawTexture = this.mTexture;
      }
      this.mCancelled = true;
    }
    finally
    {
      monitorexit;
    }
  }

  public boolean isCancelled()
  {
    return this.mCancelled;
  }

  public boolean onGLIdle(GLCanvas paramGLCanvas, boolean paramBoolean)
  {
    if (!this.mCancelled);
    try
    {
      paramGLCanvas.beginRenderTarget(this.mTexture);
      this.mRootPane.render(paramGLCanvas);
      paramGLCanvas.endRenderTarget();
      label31: this.mResultReady.open();
      return false;
    }
    catch (RuntimeException localRuntimeException)
    {
      this.mTexture = null;
      break label31:
      this.mTexture = null;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.PreparePageFadeoutTexture
 * JD-Core Version:    0.5.4
 */