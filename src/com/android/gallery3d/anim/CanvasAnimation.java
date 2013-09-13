package com.android.gallery3d.anim;

import com.android.gallery3d.ui.GLCanvas;

public abstract class CanvasAnimation extends Animation
{
  public abstract void apply(GLCanvas paramGLCanvas);

  public abstract int getCanvasSaveFlags();
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.anim.CanvasAnimation
 * JD-Core Version:    0.5.4
 */