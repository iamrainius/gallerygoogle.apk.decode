package com.android.gallery3d.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

public class TrimTimeBar extends TimeBar
{
  private int mPressedThumb = 0;
  private final Bitmap mTrimEndScrubber = BitmapFactory.decodeResource(getResources(), 2130837896);
  private int mTrimEndScrubberLeft = 0;
  private int mTrimEndScrubberTop = 0;
  private int mTrimEndTime = 0;
  private final Bitmap mTrimStartScrubber = BitmapFactory.decodeResource(getResources(), 2130837895);
  private int mTrimStartScrubberLeft = 0;
  private int mTrimStartScrubberTop = 0;
  private int mTrimStartTime = 0;

  public TrimTimeBar(Context paramContext, TimeBar.Listener paramListener)
  {
    super(paramContext, paramListener);
    this.mScrubberPadding = 0;
    this.mVPaddingInPx = (3 * this.mVPaddingInPx / 2);
  }

  private int clampScrubber(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return Math.min(paramInt4 - paramInt2, Math.max(paramInt3 - paramInt2, paramInt1));
  }

  private int getBarPosFromTime(int paramInt)
  {
    return this.mProgressBar.left + (int)(this.mProgressBar.width() * paramInt / this.mTotalTime);
  }

  private int getScrubberTime(int paramInt1, int paramInt2)
  {
    return (int)((paramInt1 + paramInt2 - this.mProgressBar.left) * this.mTotalTime / this.mProgressBar.width());
  }

  private boolean inScrubber(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, Bitmap paramBitmap)
  {
    int i = paramInt1 + paramBitmap.getWidth();
    int j = paramInt2 + paramBitmap.getHeight();
    return (paramInt1 < paramFloat1) && (paramFloat1 < i) && (paramInt2 < paramFloat2) && (paramFloat2 < j);
  }

  private void initTrimTimeIfNeeded()
  {
    if ((this.mTotalTime <= 0) || (this.mTrimEndTime != 0))
      return;
    this.mTrimEndTime = this.mTotalTime;
  }

  private int trimEndScrubberTipOffset()
  {
    return this.mTrimEndScrubber.getWidth() / 4;
  }

  private int trimStartScrubberTipOffset()
  {
    return 3 * this.mTrimStartScrubber.getWidth() / 4;
  }

  private void update()
  {
    initTrimTimeIfNeeded();
    updatePlayedBarAndScrubberFromTime();
    invalidate();
  }

  private void updatePlayedBarAndScrubberFromTime()
  {
    this.mPlayedBar.set(this.mProgressBar);
    if (this.mTotalTime > 0)
    {
      this.mPlayedBar.left = getBarPosFromTime(this.mTrimStartTime);
      this.mPlayedBar.right = getBarPosFromTime(this.mCurrentTime);
      if (!this.mScrubbing)
      {
        this.mScrubberLeft = (this.mPlayedBar.right - this.mScrubber.getWidth() / 2);
        this.mTrimStartScrubberLeft = (this.mPlayedBar.left - trimStartScrubberTipOffset());
        this.mTrimEndScrubberLeft = (getBarPosFromTime(this.mTrimEndTime) - trimEndScrubberTipOffset());
      }
      return;
    }
    this.mPlayedBar.right = this.mProgressBar.left;
    this.mScrubberLeft = (this.mProgressBar.left - this.mScrubber.getWidth() / 2);
    this.mTrimStartScrubberLeft = (this.mProgressBar.left - trimStartScrubberTipOffset());
    this.mTrimEndScrubberLeft = (this.mProgressBar.right - trimEndScrubberTipOffset());
  }

  private void updateTimeFromPos()
  {
    this.mCurrentTime = getScrubberTime(this.mScrubberLeft, this.mScrubber.getWidth() / 2);
    this.mTrimStartTime = getScrubberTime(this.mTrimStartScrubberLeft, trimStartScrubberTipOffset());
    this.mTrimEndTime = getScrubberTime(this.mTrimEndScrubberLeft, trimEndScrubberTipOffset());
  }

  private int whichScrubber(float paramFloat1, float paramFloat2)
  {
    if (inScrubber(paramFloat1, paramFloat2, this.mTrimStartScrubberLeft, this.mTrimStartScrubberTop, this.mTrimStartScrubber))
      return 1;
    if (inScrubber(paramFloat1, paramFloat2, this.mTrimEndScrubberLeft, this.mTrimEndScrubberTop, this.mTrimEndScrubber))
      return 3;
    if (inScrubber(paramFloat1, paramFloat2, this.mScrubberLeft, this.mScrubberTop, this.mScrubber))
      return 2;
    return 0;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    paramCanvas.drawRect(this.mProgressBar, this.mProgressPaint);
    paramCanvas.drawRect(this.mPlayedBar, this.mPlayedPaint);
    if (this.mShowTimes)
    {
      paramCanvas.drawText(stringForTime(this.mCurrentTime), this.mTimeBounds.width() / 2 + getPaddingLeft(), this.mTimeBounds.height() / 2 + this.mTrimStartScrubberTop, this.mTimeTextPaint);
      paramCanvas.drawText(stringForTime(this.mTotalTime), getWidth() - getPaddingRight() - this.mTimeBounds.width() / 2, this.mTimeBounds.height() / 2 + this.mTrimStartScrubberTop, this.mTimeTextPaint);
    }
    if (!this.mShowScrubber)
      return;
    paramCanvas.drawBitmap(this.mScrubber, this.mScrubberLeft, this.mScrubberTop, null);
    paramCanvas.drawBitmap(this.mTrimStartScrubber, this.mTrimStartScrubberLeft, this.mTrimStartScrubberTop, null);
    paramCanvas.drawBitmap(this.mTrimEndScrubber, this.mTrimEndScrubberLeft, this.mTrimEndScrubberTop, null);
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt3 - paramInt1;
    int j = paramInt4 - paramInt2;
    if ((!this.mShowTimes) && (!this.mShowScrubber))
      this.mProgressBar.set(0, 0, i, j);
    while (true)
    {
      update();
      return;
      int k = this.mScrubber.getWidth() / 3;
      if (this.mShowTimes)
        k += this.mTimeBounds.width();
      int l = j / 4;
      this.mScrubberTop = (1 + (l - this.mScrubber.getHeight() / 2));
      this.mTrimStartScrubberTop = l;
      this.mTrimEndScrubberTop = l;
      this.mProgressBar.set(k + getPaddingLeft(), l, i - getPaddingRight() - k, l + 4);
    }
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i;
    int j;
    if (this.mShowScrubber)
    {
      i = (int)paramMotionEvent.getX();
      j = (int)paramMotionEvent.getY();
      switch (paramMotionEvent.getAction())
      {
      default:
      case 0:
      case 2:
      case 1:
      case 3:
      }
    }
    do
    {
      do
      {
        return false;
        this.mPressedThumb = whichScrubber(i, j);
        switch (this.mPressedThumb)
        {
        case 0:
        default:
        case 2:
        case 1:
        case 3:
        }
        while (true)
        {
          if (this.mScrubbing == true);
          this.mListener.onScrubbingStart();
          return true;
          this.mScrubbing = true;
          this.mScrubberCorrection = (i - this.mScrubberLeft);
          continue;
          this.mScrubbing = true;
          this.mScrubberCorrection = (i - this.mTrimStartScrubberLeft);
          continue;
          this.mScrubbing = true;
          this.mScrubberCorrection = (i - this.mTrimEndScrubberLeft);
        }
      }
      while (!this.mScrubbing);
      int i1 = -1;
      int i2 = this.mTrimStartScrubberLeft + trimStartScrubberTipOffset();
      int i3 = this.mTrimEndScrubberLeft + trimEndScrubberTipOffset();
      switch (this.mPressedThumb)
      {
      default:
      case 2:
      case 1:
      case 3:
      }
      while (true)
      {
        updateTimeFromPos();
        updatePlayedBarAndScrubberFromTime();
        if (i1 != -1)
          this.mListener.onScrubbingMove(i1);
        invalidate();
        return true;
        this.mScrubberLeft = (i - this.mScrubberCorrection);
        this.mScrubberLeft = clampScrubber(this.mScrubberLeft, this.mScrubber.getWidth() / 2, i2, i3);
        i1 = getScrubberTime(this.mScrubberLeft, this.mScrubber.getWidth() / 2);
        continue;
        this.mTrimStartScrubberLeft = (i - this.mScrubberCorrection);
        if (this.mTrimStartScrubberLeft > this.mTrimEndScrubberLeft)
          this.mTrimStartScrubberLeft = this.mTrimEndScrubberLeft;
        int i5 = this.mProgressBar.left;
        this.mTrimStartScrubberLeft = clampScrubber(this.mTrimStartScrubberLeft, trimStartScrubberTipOffset(), i5, i3);
        i1 = getScrubberTime(this.mTrimStartScrubberLeft, trimStartScrubberTipOffset());
        continue;
        this.mTrimEndScrubberLeft = (i - this.mScrubberCorrection);
        int i4 = this.mProgressBar.right;
        this.mTrimEndScrubberLeft = clampScrubber(this.mTrimEndScrubberLeft, trimEndScrubberTipOffset(), i2, i4);
        i1 = getScrubberTime(this.mTrimEndScrubberLeft, trimEndScrubberTipOffset());
      }
    }
    while (!this.mScrubbing);
    int k = this.mPressedThumb;
    int l = 0;
    switch (k)
    {
    default:
    case 2:
    case 1:
    case 3:
    }
    while (true)
    {
      updateTimeFromPos();
      this.mListener.onScrubbingEnd(l, getScrubberTime(this.mTrimStartScrubberLeft, trimStartScrubberTipOffset()), getScrubberTime(this.mTrimEndScrubberLeft, trimEndScrubberTipOffset()));
      this.mScrubbing = false;
      this.mPressedThumb = 0;
      return true;
      l = getScrubberTime(this.mScrubberLeft, this.mScrubber.getWidth() / 2);
      continue;
      l = getScrubberTime(this.mTrimStartScrubberLeft, trimStartScrubberTipOffset());
      this.mScrubberLeft = (this.mTrimStartScrubberLeft + trimStartScrubberTipOffset() - this.mScrubber.getWidth() / 2);
      continue;
      l = getScrubberTime(this.mTrimEndScrubberLeft, trimEndScrubberTipOffset());
      this.mScrubberLeft = (this.mTrimEndScrubberLeft + trimEndScrubberTipOffset() - this.mScrubber.getWidth() / 2);
    }
  }

  public void setTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((this.mCurrentTime == paramInt1) && (this.mTotalTime == paramInt2) && (this.mTrimStartTime == paramInt3) && (this.mTrimEndTime == paramInt4))
      return;
    this.mCurrentTime = paramInt1;
    this.mTotalTime = paramInt2;
    this.mTrimStartTime = paramInt3;
    this.mTrimEndTime = paramInt4;
    update();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.TrimTimeBar
 * JD-Core Version:    0.5.4
 */