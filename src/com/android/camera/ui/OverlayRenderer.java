package com.android.camera.ui;

import android.graphics.Canvas;
import android.view.MotionEvent;

public abstract class OverlayRenderer
  implements RenderOverlay.Renderer
{
  protected int mBottom;
  protected int mLeft;
  protected RenderOverlay mOverlay;
  protected int mRight;
  protected int mTop;
  protected boolean mVisible;

  public void draw(Canvas paramCanvas)
  {
    if (!this.mVisible)
      return;
    onDraw(paramCanvas);
  }

  public int getHeight()
  {
    return this.mBottom - this.mTop;
  }

  public int getWidth()
  {
    return this.mRight - this.mLeft;
  }

  public boolean handlesTouch()
  {
    return false;
  }

  public boolean isVisible()
  {
    return this.mVisible;
  }

  public void layout(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mLeft = paramInt1;
    this.mRight = paramInt3;
    this.mTop = paramInt2;
    this.mBottom = paramInt4;
  }

  public abstract void onDraw(Canvas paramCanvas);

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }

  public void setOverlay(RenderOverlay paramRenderOverlay)
  {
    this.mOverlay = paramRenderOverlay;
  }

  public void setVisible(boolean paramBoolean)
  {
    this.mVisible = paramBoolean;
    update();
  }

  protected void update()
  {
    if (this.mOverlay == null)
      return;
    this.mOverlay.update();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.OverlayRenderer
 * JD-Core Version:    0.5.4
 */