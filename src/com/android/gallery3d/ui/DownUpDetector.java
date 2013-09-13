package com.android.gallery3d.ui;

import android.view.MotionEvent;

public class DownUpDetector
{
  private DownUpListener mListener;
  private boolean mStillDown;

  public DownUpDetector(DownUpListener paramDownUpListener)
  {
    this.mListener = paramDownUpListener;
  }

  private void setState(boolean paramBoolean, MotionEvent paramMotionEvent)
  {
    if (paramBoolean == this.mStillDown)
      return;
    this.mStillDown = paramBoolean;
    if (paramBoolean)
    {
      this.mListener.onDown(paramMotionEvent);
      return;
    }
    this.mListener.onUp(paramMotionEvent);
  }

  public void onTouchEvent(MotionEvent paramMotionEvent)
  {
    switch (0xFF & paramMotionEvent.getAction())
    {
    case 2:
    case 4:
    default:
      return;
    case 0:
      setState(true, paramMotionEvent);
      return;
    case 1:
    case 3:
    case 5:
    }
    setState(false, paramMotionEvent);
  }

  public static abstract interface DownUpListener
  {
    public abstract void onDown(MotionEvent paramMotionEvent);

    public abstract void onUp(MotionEvent paramMotionEvent);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.DownUpDetector
 * JD-Core Version:    0.5.4
 */