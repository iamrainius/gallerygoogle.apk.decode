package com.android.gallery3d.ui;

import android.os.Handler;
import android.os.Message;
import com.android.gallery3d.common.Utils;

public class SynchronizedHandler extends Handler
{
  private final GLRoot mRoot;

  public SynchronizedHandler(GLRoot paramGLRoot)
  {
    this.mRoot = ((GLRoot)Utils.checkNotNull(paramGLRoot));
  }

  public void dispatchMessage(Message paramMessage)
  {
    this.mRoot.lockRenderThread();
    try
    {
      super.dispatchMessage(paramMessage);
      return;
    }
    finally
    {
      this.mRoot.unlockRenderThread();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.SynchronizedHandler
 * JD-Core Version:    0.5.4
 */