package com.android.gallery3d.ui;

import android.graphics.Matrix;

public abstract interface GLRoot
{
  public abstract void addOnGLIdleListener(OnGLIdleListener paramOnGLIdleListener);

  public abstract void freeze();

  public abstract int getCompensation();

  public abstract Matrix getCompensationMatrix();

  public abstract int getDisplayRotation();

  public abstract void lockRenderThread();

  public abstract void requestLayoutContentPane();

  public abstract void requestRender();

  public abstract void requestRenderForced();

  public abstract void setContentPane(GLView paramGLView);

  public abstract void setLightsOutMode(boolean paramBoolean);

  public abstract void setOrientationSource(OrientationSource paramOrientationSource);

  public abstract void unfreeze();

  public abstract void unlockRenderThread();

  public static abstract interface OnGLIdleListener
  {
    public abstract boolean onGLIdle(GLCanvas paramGLCanvas, boolean paramBoolean);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.GLRoot
 * JD-Core Version:    0.5.4
 */