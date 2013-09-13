package com.android.gallery3d.ui;

import android.content.Context;
import android.view.ViewConfiguration;
import com.android.gallery3d.common.OverScroller;
import com.android.gallery3d.common.Utils;

public class ScrollerHelper
{
  private int mOverflingDistance;
  private boolean mOverflingEnabled;
  private OverScroller mScroller;

  public ScrollerHelper(Context paramContext)
  {
    this.mScroller = new OverScroller(paramContext);
    this.mOverflingDistance = ViewConfiguration.get(paramContext).getScaledOverflingDistance();
  }

  public boolean advanceAnimation(long paramLong)
  {
    return this.mScroller.computeScrollOffset();
  }

  public void fling(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = getPosition();
    OverScroller localOverScroller = this.mScroller;
    if (this.mOverflingEnabled);
    for (int j = this.mOverflingDistance; ; j = 0)
    {
      localOverScroller.fling(i, 0, paramInt1, 0, paramInt2, paramInt3, 0, 0, j, 0);
      return;
    }
  }

  public void forceFinished()
  {
    this.mScroller.forceFinished(true);
  }

  public float getCurrVelocity()
  {
    return this.mScroller.getCurrVelocity();
  }

  public int getPosition()
  {
    return this.mScroller.getCurrX();
  }

  public boolean isFinished()
  {
    return this.mScroller.isFinished();
  }

  public void setPosition(int paramInt)
  {
    this.mScroller.startScroll(paramInt, 0, 0, 0, 0);
    this.mScroller.abortAnimation();
  }

  public int startScroll(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = this.mScroller.getCurrX();
    if (this.mScroller.isFinished());
    for (int j = i; ; j = this.mScroller.getFinalX())
    {
      int k = Utils.clamp(j + paramInt1, paramInt2, paramInt3);
      if (k != i)
        this.mScroller.startScroll(i, 0, k - i, 0, 0);
      return j + paramInt1 - k;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.ScrollerHelper
 * JD-Core Version:    0.5.4
 */