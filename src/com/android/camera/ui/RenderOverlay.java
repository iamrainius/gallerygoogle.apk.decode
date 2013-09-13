package com.android.camera.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RenderOverlay extends FrameLayout
{
  private List<Renderer> mClients;
  private int[] mPosition = new int[2];
  private RenderView mRenderView;
  private List<Renderer> mTouchClients;

  public RenderOverlay(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mRenderView = new RenderView(paramContext);
    addView(this.mRenderView, new FrameLayout.LayoutParams(-1, -1));
    this.mClients = new ArrayList(10);
    this.mTouchClients = new ArrayList(10);
    setWillNotDraw(false);
  }

  private void adjustPosition()
  {
    getLocationInWindow(this.mPosition);
  }

  public void addRenderer(Renderer paramRenderer)
  {
    this.mClients.add(paramRenderer);
    paramRenderer.setOverlay(this);
    if (!paramRenderer.handlesTouch())
      return;
    this.mTouchClients.add(0, paramRenderer);
  }

  public boolean directDispatchTouch(MotionEvent paramMotionEvent, Renderer paramRenderer)
  {
    this.mRenderView.setTouchTarget(paramRenderer);
    boolean bool = super.dispatchTouchEvent(paramMotionEvent);
    this.mRenderView.setTouchTarget(null);
    return bool;
  }

  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }

  public int getWindowPositionX()
  {
    return this.mPosition[0];
  }

  public int getWindowPositionY()
  {
    return this.mPosition[1];
  }

  public void update()
  {
    this.mRenderView.invalidate();
  }

  private class RenderView extends View
  {
    private RenderOverlay.Renderer mTouchTarget;

    public RenderView(Context arg2)
    {
      super(localContext);
      setWillNotDraw(false);
    }

    public void draw(Canvas paramCanvas)
    {
      super.draw(paramCanvas);
      if (RenderOverlay.this.mClients == null);
      int i;
      do
      {
        return;
        i = 0;
        Iterator localIterator = RenderOverlay.this.mClients.iterator();
        label31: if (!localIterator.hasNext())
          continue;
        RenderOverlay.Renderer localRenderer = (RenderOverlay.Renderer)localIterator.next();
        localRenderer.draw(paramCanvas);
        if ((i != 0) || (((OverlayRenderer)localRenderer).isVisible()));
        for (i = 1; ; i = 0)
          break label31:
      }
      while (i == 0);
      invalidate();
    }

    public void layout(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      RenderOverlay.this.adjustPosition();
      super.layout(paramInt1, paramInt2, paramInt3, paramInt4);
      if (RenderOverlay.this.mClients == null)
        return;
      Iterator localIterator = RenderOverlay.this.mClients.iterator();
      while (true)
      {
        if (localIterator.hasNext());
        ((RenderOverlay.Renderer)localIterator.next()).layout(paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      boolean bool;
      if (this.mTouchTarget != null)
      {
        bool = this.mTouchTarget.onTouchEvent(paramMotionEvent);
        return bool;
      }
      if (RenderOverlay.this.mTouchClients != null)
      {
        bool = false;
        Iterator localIterator = RenderOverlay.this.mTouchClients.iterator();
        while (true)
        {
          if (localIterator.hasNext());
          bool |= ((RenderOverlay.Renderer)localIterator.next()).onTouchEvent(paramMotionEvent);
        }
      }
      return false;
    }

    public void setTouchTarget(RenderOverlay.Renderer paramRenderer)
    {
      this.mTouchTarget = paramRenderer;
    }
  }

  static abstract interface Renderer
  {
    public abstract void draw(Canvas paramCanvas);

    public abstract boolean handlesTouch();

    public abstract void layout(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

    public abstract boolean onTouchEvent(MotionEvent paramMotionEvent);

    public abstract void setOverlay(RenderOverlay paramRenderOverlay);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.RenderOverlay
 * JD-Core Version:    0.5.4
 */