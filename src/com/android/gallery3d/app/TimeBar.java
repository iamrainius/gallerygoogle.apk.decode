package com.android.gallery3d.app;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import com.android.gallery3d.common.Utils;

public class TimeBar extends View
{
  protected int mCurrentTime;
  protected final Listener mListener;
  protected final Rect mPlayedBar;
  protected final Paint mPlayedPaint;
  protected final Rect mProgressBar;
  protected final Paint mProgressPaint;
  protected final Bitmap mScrubber;
  protected int mScrubberCorrection;
  protected int mScrubberLeft;
  protected int mScrubberPadding;
  protected int mScrubberTop;
  protected boolean mScrubbing;
  protected boolean mShowScrubber;
  protected boolean mShowTimes;
  protected final Rect mTimeBounds;
  protected final Paint mTimeTextPaint;
  protected int mTotalTime;
  protected int mVPaddingInPx;

  public TimeBar(Context paramContext, Listener paramListener)
  {
    super(paramContext);
    this.mListener = ((Listener)Utils.checkNotNull(paramListener));
    this.mShowTimes = true;
    this.mShowScrubber = true;
    this.mProgressBar = new Rect();
    this.mPlayedBar = new Rect();
    this.mProgressPaint = new Paint();
    this.mProgressPaint.setColor(-8355712);
    this.mPlayedPaint = new Paint();
    this.mPlayedPaint.setColor(-1);
    DisplayMetrics localDisplayMetrics = paramContext.getResources().getDisplayMetrics();
    float f = 14.0F * localDisplayMetrics.density;
    this.mTimeTextPaint = new Paint(1);
    this.mTimeTextPaint.setColor(-3223858);
    this.mTimeTextPaint.setTextSize(f);
    this.mTimeTextPaint.setTextAlign(Paint.Align.CENTER);
    this.mTimeBounds = new Rect();
    this.mTimeTextPaint.getTextBounds("0:00:00", 0, 7, this.mTimeBounds);
    this.mScrubber = BitmapFactory.decodeResource(getResources(), 2130837880);
    this.mScrubberPadding = (int)(10.0F * localDisplayMetrics.density);
    this.mVPaddingInPx = (int)(30.0F * localDisplayMetrics.density);
  }

  private void clampScrubber()
  {
    int i = this.mScrubber.getWidth() / 2;
    this.mScrubberLeft = Math.min(this.mProgressBar.right - i, Math.max(this.mProgressBar.left - i, this.mScrubberLeft));
  }

  private int getScrubberTime()
  {
    return (int)((this.mScrubberLeft + this.mScrubber.getWidth() / 2 - this.mProgressBar.left) * this.mTotalTime / this.mProgressBar.width());
  }

  private boolean inScrubber(float paramFloat1, float paramFloat2)
  {
    int i = this.mScrubberLeft + this.mScrubber.getWidth();
    int j = this.mScrubberTop + this.mScrubber.getHeight();
    return (this.mScrubberLeft - this.mScrubberPadding < paramFloat1) && (paramFloat1 < i + this.mScrubberPadding) && (this.mScrubberTop - this.mScrubberPadding < paramFloat2) && (paramFloat2 < j + this.mScrubberPadding);
  }

  private void update()
  {
    this.mPlayedBar.set(this.mProgressBar);
    if (this.mTotalTime > 0);
    for (this.mPlayedBar.right = (this.mPlayedBar.left + (int)(this.mProgressBar.width() * this.mCurrentTime / this.mTotalTime)); ; this.mPlayedBar.right = this.mProgressBar.left)
    {
      if (!this.mScrubbing)
        this.mScrubberLeft = (this.mPlayedBar.right - this.mScrubber.getWidth() / 2);
      invalidate();
      return;
    }
  }

  public int getBarHeight()
  {
    return this.mTimeBounds.height() + this.mVPaddingInPx;
  }

  public int getPreferredHeight()
  {
    return this.mTimeBounds.height() + this.mVPaddingInPx + this.mScrubberPadding;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    paramCanvas.drawRect(this.mProgressBar, this.mProgressPaint);
    paramCanvas.drawRect(this.mPlayedBar, this.mPlayedPaint);
    if (this.mShowScrubber)
      paramCanvas.drawBitmap(this.mScrubber, this.mScrubberLeft, this.mScrubberTop, null);
    if (!this.mShowTimes)
      return;
    paramCanvas.drawText(stringForTime(this.mCurrentTime), this.mTimeBounds.width() / 2 + getPaddingLeft(), 1 + (this.mTimeBounds.height() + this.mVPaddingInPx / 2 + this.mScrubberPadding), this.mTimeTextPaint);
    paramCanvas.drawText(stringForTime(this.mTotalTime), getWidth() - getPaddingRight() - this.mTimeBounds.width() / 2, 1 + (this.mTimeBounds.height() + this.mVPaddingInPx / 2 + this.mScrubberPadding), this.mTimeTextPaint);
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
      int l = (j + this.mScrubberPadding) / 2;
      this.mScrubberTop = (1 + (l - this.mScrubber.getHeight() / 2));
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
    }
    switch (paramMotionEvent.getAction())
    {
    default:
      return false;
    case 0:
      if (!inScrubber(i, j));
    case 2:
      for (int k = i - this.mScrubberLeft; ; k = this.mScrubber.getWidth() / 2)
      {
        this.mScrubberCorrection = k;
        this.mScrubbing = true;
        this.mListener.onScrubbingStart();
        this.mScrubberLeft = (i - this.mScrubberCorrection);
        clampScrubber();
        this.mCurrentTime = getScrubberTime();
        this.mListener.onScrubbingMove(this.mCurrentTime);
        invalidate();
        return true;
      }
    case 1:
    case 3:
    }
    this.mListener.onScrubbingEnd(getScrubberTime(), 0, 0);
    this.mScrubbing = false;
    return true;
  }

  public void setTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((this.mCurrentTime == paramInt1) && (this.mTotalTime == paramInt2))
      return;
    this.mCurrentTime = paramInt1;
    this.mTotalTime = paramInt2;
    update();
  }

  protected String stringForTime(long paramLong)
  {
    int i = (int)paramLong / 1000;
    int j = i % 60;
    int k = i / 60 % 60;
    int l = i / 3600;
    if (l > 0)
    {
      Object[] arrayOfObject2 = new Object[3];
      arrayOfObject2[0] = Integer.valueOf(l);
      arrayOfObject2[1] = Integer.valueOf(k);
      arrayOfObject2[2] = Integer.valueOf(j);
      return String.format("%d:%02d:%02d", arrayOfObject2).toString();
    }
    Object[] arrayOfObject1 = new Object[2];
    arrayOfObject1[0] = Integer.valueOf(k);
    arrayOfObject1[1] = Integer.valueOf(j);
    return String.format("%02d:%02d", arrayOfObject1).toString();
  }

  public static abstract interface Listener
  {
    public abstract void onScrubbingEnd(int paramInt1, int paramInt2, int paramInt3);

    public abstract void onScrubbingMove(int paramInt);

    public abstract void onScrubbingStart();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.TimeBar
 * JD-Core Version:    0.5.4
 */