package com.android.gallery3d.ui;

import android.content.Context;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;

public class GestureRecognizer
{
  private final DownUpDetector mDownUpDetector;
  private final GestureDetector mGestureDetector;
  private final Listener mListener;
  private final ScaleGestureDetector mScaleDetector;

  public GestureRecognizer(Context paramContext, Listener paramListener)
  {
    this.mListener = paramListener;
    this.mGestureDetector = new GestureDetector(paramContext, new MyGestureListener(null), null, true);
    this.mScaleDetector = new ScaleGestureDetector(paramContext, new MyScaleListener(null));
    this.mDownUpDetector = new DownUpDetector(new MyDownUpListener(null));
  }

  public void cancelScale()
  {
    long l = SystemClock.uptimeMillis();
    MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
    this.mScaleDetector.onTouchEvent(localMotionEvent);
    localMotionEvent.recycle();
  }

  public void onTouchEvent(MotionEvent paramMotionEvent)
  {
    this.mGestureDetector.onTouchEvent(paramMotionEvent);
    this.mScaleDetector.onTouchEvent(paramMotionEvent);
    this.mDownUpDetector.onTouchEvent(paramMotionEvent);
  }

  public static abstract interface Listener
  {
    public abstract boolean onDoubleTap(float paramFloat1, float paramFloat2);

    public abstract void onDown(float paramFloat1, float paramFloat2);

    public abstract boolean onFling(float paramFloat1, float paramFloat2);

    public abstract boolean onScale(float paramFloat1, float paramFloat2, float paramFloat3);

    public abstract boolean onScaleBegin(float paramFloat1, float paramFloat2);

    public abstract void onScaleEnd();

    public abstract boolean onScroll(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);

    public abstract boolean onSingleTapUp(float paramFloat1, float paramFloat2);

    public abstract void onUp();
  }

  private class MyDownUpListener
    implements DownUpDetector.DownUpListener
  {
    private MyDownUpListener()
    {
    }

    public void onDown(MotionEvent paramMotionEvent)
    {
      GestureRecognizer.this.mListener.onDown(paramMotionEvent.getX(), paramMotionEvent.getY());
    }

    public void onUp(MotionEvent paramMotionEvent)
    {
      GestureRecognizer.this.mListener.onUp();
    }
  }

  private class MyGestureListener extends GestureDetector.SimpleOnGestureListener
  {
    private MyGestureListener()
    {
    }

    public boolean onDoubleTap(MotionEvent paramMotionEvent)
    {
      return GestureRecognizer.this.mListener.onDoubleTap(paramMotionEvent.getX(), paramMotionEvent.getY());
    }

    public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
    {
      return GestureRecognizer.this.mListener.onFling(paramFloat1, paramFloat2);
    }

    public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
    {
      return GestureRecognizer.this.mListener.onScroll(paramFloat1, paramFloat2, paramMotionEvent2.getX() - paramMotionEvent1.getX(), paramMotionEvent2.getY() - paramMotionEvent1.getY());
    }

    public boolean onSingleTapUp(MotionEvent paramMotionEvent)
    {
      return GestureRecognizer.this.mListener.onSingleTapUp(paramMotionEvent.getX(), paramMotionEvent.getY());
    }
  }

  private class MyScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
  {
    private MyScaleListener()
    {
    }

    public boolean onScale(ScaleGestureDetector paramScaleGestureDetector)
    {
      return GestureRecognizer.this.mListener.onScale(paramScaleGestureDetector.getFocusX(), paramScaleGestureDetector.getFocusY(), paramScaleGestureDetector.getScaleFactor());
    }

    public boolean onScaleBegin(ScaleGestureDetector paramScaleGestureDetector)
    {
      return GestureRecognizer.this.mListener.onScaleBegin(paramScaleGestureDetector.getFocusX(), paramScaleGestureDetector.getFocusY());
    }

    public void onScaleEnd(ScaleGestureDetector paramScaleGestureDetector)
    {
      GestureRecognizer.this.mListener.onScaleEnd();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.GestureRecognizer
 * JD-Core Version:    0.5.4
 */