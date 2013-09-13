package com.android.camera;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.camera.ui.PieRenderer;
import com.android.camera.ui.RenderOverlay;
import com.android.camera.ui.ZoomRenderer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PreviewGestures
  implements ScaleGestureDetector.OnScaleGestureListener
{
  private CameraActivity mActivity;
  private MotionEvent mCurrent;
  private MotionEvent mDown;
  private boolean mEnabled;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramMessage)
    {
      if (paramMessage.what != 1)
        return;
      PreviewGestures.access$002(PreviewGestures.this, 1);
      PreviewGestures.this.openPie();
      PreviewGestures.this.cancelActivityTouchHandling(PreviewGestures.this.mDown);
    }
  };
  private int[] mLocation;
  private int mMode;
  private CameraModule mModule;
  private int mOrientation;
  private RenderOverlay mOverlay;
  private PieRenderer mPie;
  private List<View> mReceivers;
  private ScaleGestureDetector mScale;
  private int mSlop;
  private int mTapTimeout;
  private ZoomRenderer mZoom;
  private boolean mZoomOnly;

  public PreviewGestures(CameraActivity paramCameraActivity, CameraModule paramCameraModule, ZoomRenderer paramZoomRenderer, PieRenderer paramPieRenderer)
  {
    this.mActivity = paramCameraActivity;
    this.mModule = paramCameraModule;
    this.mPie = paramPieRenderer;
    this.mZoom = paramZoomRenderer;
    this.mMode = 4;
    this.mScale = new ScaleGestureDetector(paramCameraActivity, this);
    this.mSlop = (int)paramCameraActivity.getResources().getDimension(2131623998);
    this.mTapTimeout = ViewConfiguration.getTapTimeout();
    this.mEnabled = true;
    this.mLocation = new int[2];
  }

  private void cancelPie()
  {
    this.mHandler.removeMessages(1);
  }

  private boolean checkReceivers(MotionEvent paramMotionEvent)
  {
    if (this.mReceivers != null)
    {
      Iterator localIterator = this.mReceivers.iterator();
      while (localIterator.hasNext())
        if (isInside(paramMotionEvent, (View)localIterator.next()))
          return true;
    }
    return false;
  }

  private boolean isInside(MotionEvent paramMotionEvent, View paramView)
  {
    paramView.getLocationInWindow(this.mLocation);
    return (paramView.getVisibility() == 0) && (paramMotionEvent.getX() >= this.mLocation[0]) && (paramMotionEvent.getX() < this.mLocation[0] + paramView.getWidth()) && (paramMotionEvent.getY() >= this.mLocation[1]) && (paramMotionEvent.getY() < this.mLocation[1] + paramView.getHeight());
  }

  private boolean isSwipe(MotionEvent paramMotionEvent, boolean paramBoolean)
  {
    int i = this.mOrientation;
    float f1 = 0.0F;
    float f2 = 0.0F;
    switch (i)
    {
    default:
      if (paramBoolean)
        label56: if ((f1 >= 0.0F) || (f2 / -f1 >= 0.6F));
    case 0:
    case 90:
    case 180:
    case 270:
    }
    do
    {
      return true;
      f1 = paramMotionEvent.getX() - this.mDown.getX();
      f2 = Math.abs(paramMotionEvent.getY() - this.mDown.getY());
      break label56:
      f1 = -(paramMotionEvent.getY() - this.mDown.getY());
      f2 = Math.abs(paramMotionEvent.getX() - this.mDown.getX());
      break label56:
      f1 = -(paramMotionEvent.getX() - this.mDown.getX());
      f2 = Math.abs(paramMotionEvent.getY() - this.mDown.getY());
      break label56:
      f1 = paramMotionEvent.getY() - this.mDown.getY();
      f2 = Math.abs(paramMotionEvent.getX() - this.mDown.getX());
      break label56:
      return false;
    }
    while ((f1 > 0.0F) && (f2 / f1 < 0.6F));
    return false;
  }

  private MotionEvent makeCancelEvent(MotionEvent paramMotionEvent)
  {
    MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
    localMotionEvent.setAction(3);
    return localMotionEvent;
  }

  private void openPie()
  {
    this.mDown.offsetLocation(-this.mOverlay.getWindowPositionX(), -this.mOverlay.getWindowPositionY());
    this.mOverlay.directDispatchTouch(this.mDown, this.mPie);
  }

  private boolean sendToPie(MotionEvent paramMotionEvent)
  {
    paramMotionEvent.offsetLocation(-this.mOverlay.getWindowPositionX(), -this.mOverlay.getWindowPositionY());
    return this.mOverlay.directDispatchTouch(paramMotionEvent, this.mPie);
  }

  public void addTouchReceiver(View paramView)
  {
    if (this.mReceivers == null)
      this.mReceivers = new ArrayList();
    this.mReceivers.add(paramView);
  }

  public void cancelActivityTouchHandling(MotionEvent paramMotionEvent)
  {
    this.mActivity.superDispatchTouchEvent(makeCancelEvent(paramMotionEvent));
  }

  public void clearTouchReceivers()
  {
    if (this.mReceivers == null)
      return;
    this.mReceivers.clear();
  }

  public boolean dispatchTouch(MotionEvent paramMotionEvent)
  {
    if (!this.mEnabled)
      return this.mActivity.superDispatchTouchEvent(paramMotionEvent);
    this.mCurrent = paramMotionEvent;
    if (paramMotionEvent.getActionMasked() == 0)
    {
      if (checkReceivers(paramMotionEvent))
      {
        this.mMode = 3;
        return this.mActivity.superDispatchTouchEvent(paramMotionEvent);
      }
      this.mMode = 4;
      this.mDown = MotionEvent.obtain(paramMotionEvent);
      if ((this.mPie != null) && (this.mPie.showsItems()))
      {
        this.mMode = 1;
        return sendToPie(paramMotionEvent);
      }
      if ((this.mPie != null) && (!this.mZoomOnly))
        this.mHandler.sendEmptyMessageDelayed(1, 200L);
      if (this.mZoom != null)
        this.mScale.onTouchEvent(paramMotionEvent);
      return this.mActivity.superDispatchTouchEvent(paramMotionEvent);
    }
    if (this.mMode == 0)
      return false;
    if (this.mMode == 1)
    {
      if (5 == paramMotionEvent.getActionMasked())
      {
        sendToPie(makeCancelEvent(paramMotionEvent));
        if (this.mZoom != null)
          onScaleBegin(this.mScale);
        return true;
      }
      return sendToPie(paramMotionEvent);
    }
    if (this.mMode == 2)
    {
      this.mScale.onTouchEvent(paramMotionEvent);
      if ((!this.mScale.isInProgress()) && (6 == paramMotionEvent.getActionMasked()))
      {
        this.mMode = 0;
        onScaleEnd(this.mScale);
      }
      return true;
    }
    if (this.mMode == 3)
      return this.mActivity.superDispatchTouchEvent(paramMotionEvent);
    if (this.mDown == null)
      return true;
    if (5 == paramMotionEvent.getActionMasked())
    {
      if (!this.mZoomOnly)
      {
        cancelPie();
        sendToPie(makeCancelEvent(paramMotionEvent));
      }
      if (this.mZoom != null)
      {
        this.mScale.onTouchEvent(paramMotionEvent);
        onScaleBegin(this.mScale);
      }
    }
    while (this.mZoom != null)
    {
      boolean bool = this.mScale.onTouchEvent(paramMotionEvent);
      if (!this.mScale.isInProgress())
        break;
      cancelPie();
      cancelActivityTouchHandling(paramMotionEvent);
      return bool;
      if ((this.mMode != 2) || (this.mScale.isInProgress()) || (6 != paramMotionEvent.getActionMasked()))
        continue;
      this.mScale.onTouchEvent(paramMotionEvent);
      onScaleEnd(this.mScale);
    }
    if (1 == paramMotionEvent.getActionMasked())
    {
      cancelPie();
      cancelActivityTouchHandling(paramMotionEvent);
      if (paramMotionEvent.getEventTime() - this.mDown.getEventTime() < this.mTapTimeout)
      {
        this.mModule.onSingleTapUp(null, (int)this.mDown.getX() - this.mOverlay.getWindowPositionX(), (int)this.mDown.getY() - this.mOverlay.getWindowPositionY());
        return true;
      }
      return this.mActivity.superDispatchTouchEvent(paramMotionEvent);
    }
    if ((2 == paramMotionEvent.getActionMasked()) && (((Math.abs(paramMotionEvent.getX() - this.mDown.getX()) > this.mSlop) || (Math.abs(paramMotionEvent.getY() - this.mDown.getY()) > this.mSlop))))
    {
      cancelPie();
      if (isSwipe(paramMotionEvent, true))
      {
        this.mMode = 3;
        return this.mActivity.superDispatchTouchEvent(paramMotionEvent);
      }
      cancelActivityTouchHandling(paramMotionEvent);
      if (!isSwipe(paramMotionEvent, false))
        break label613;
      this.mMode = 0;
    }
    while (true)
    {
      return false;
      label613: if (this.mZoomOnly)
        continue;
      this.mMode = 1;
      openPie();
      sendToPie(paramMotionEvent);
    }
  }

  public boolean onScale(ScaleGestureDetector paramScaleGestureDetector)
  {
    return this.mZoom.onScale(paramScaleGestureDetector);
  }

  public boolean onScaleBegin(ScaleGestureDetector paramScaleGestureDetector)
  {
    if (this.mMode != 2)
    {
      this.mMode = 2;
      cancelActivityTouchHandling(this.mCurrent);
    }
    if (this.mCurrent.getActionMasked() != 2)
      return this.mZoom.onScaleBegin(paramScaleGestureDetector);
    return true;
  }

  public void onScaleEnd(ScaleGestureDetector paramScaleGestureDetector)
  {
    if (this.mCurrent.getActionMasked() == 2)
      return;
    this.mZoom.onScaleEnd(paramScaleGestureDetector);
  }

  public void setEnabled(boolean paramBoolean)
  {
    this.mEnabled = paramBoolean;
    if (paramBoolean)
      return;
    cancelPie();
  }

  public void setOrientation(int paramInt)
  {
    this.mOrientation = paramInt;
  }

  public void setRenderOverlay(RenderOverlay paramRenderOverlay)
  {
    this.mOverlay = paramRenderOverlay;
  }

  public void setZoomOnly(boolean paramBoolean)
  {
    this.mZoomOnly = paramBoolean;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.PreviewGestures
 * JD-Core Version:    0.5.4
 */