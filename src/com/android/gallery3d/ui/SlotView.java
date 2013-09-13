package com.android.gallery3d.ui;

import android.graphics.Rect;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import com.android.gallery3d.anim.Animation;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.common.Utils;

public class SlotView extends GLView
{
  private SlotAnimation mAnimation = null;
  private boolean mDownInScrolling;
  private final GestureDetector mGestureDetector = new GestureDetector(paramAbstractGalleryActivity, new MyGestureListener(null));
  private final Handler mHandler;
  private final Layout mLayout = new Layout();
  private Listener mListener;
  private boolean mMoreAnimation = false;
  private int mOverscrollEffect = 0;
  private final Paper mPaper = new Paper();
  private SlotRenderer mRenderer;
  private int[] mRequestRenderSlots = new int[16];
  private final ScrollerHelper mScroller;
  private int mStartIndex = -1;
  private final Rect mTempRect = new Rect();
  private UserInteractionListener mUIListener;

  public SlotView(AbstractGalleryActivity paramAbstractGalleryActivity, Spec paramSpec)
  {
    this.mScroller = new ScrollerHelper(paramAbstractGalleryActivity);
    this.mHandler = new SynchronizedHandler(paramAbstractGalleryActivity.getGLRoot());
    setSlotSpec(paramSpec);
  }

  private static int[] expandIntArray(int[] paramArrayOfInt, int paramInt)
  {
    while (paramArrayOfInt.length < paramInt)
      paramArrayOfInt = new int[2 * paramArrayOfInt.length];
    return paramArrayOfInt;
  }

  private int renderItem(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    paramGLCanvas.save(3);
    Rect localRect = this.mLayout.getSlotRect(paramInt1, this.mTempRect);
    if (paramBoolean)
      paramGLCanvas.multiplyMatrix(this.mPaper.getTransform(localRect, this.mScrollX), 0);
    while (true)
    {
      if ((this.mAnimation != null) && (this.mAnimation.isActive()))
        this.mAnimation.apply(paramGLCanvas, paramInt1, localRect);
      int i = this.mRenderer.renderSlot(paramGLCanvas, paramInt1, paramInt2, localRect.right - localRect.left, localRect.bottom - localRect.top);
      paramGLCanvas.restore();
      return i;
      paramGLCanvas.translate(localRect.left, localRect.top, 0.0F);
    }
  }

  private void updateScrollPosition(int paramInt, boolean paramBoolean)
  {
    if ((!paramBoolean) && (paramInt == this.mScrollX))
      return;
    this.mScrollX = paramInt;
    this.mLayout.setScrollPosition(paramInt);
    onScrollPositionChanged(paramInt);
  }

  public void addComponent(GLView paramGLView)
  {
    throw new UnsupportedOperationException();
  }

  public int getScrollX()
  {
    return this.mScrollX;
  }

  public int getScrollY()
  {
    return this.mScrollY;
  }

  public Rect getSlotRect(int paramInt)
  {
    return this.mLayout.getSlotRect(paramInt, new Rect());
  }

  public Rect getSlotRect(int paramInt, GLView paramGLView)
  {
    Rect localRect1 = new Rect();
    paramGLView.getBoundsOf(this, localRect1);
    Rect localRect2 = getSlotRect(paramInt);
    localRect2.offset(localRect1.left - getScrollX(), localRect1.top - getScrollY());
    return localRect2;
  }

  public int getVisibleEnd()
  {
    return this.mLayout.getVisibleEnd();
  }

  public int getVisibleStart()
  {
    return this.mLayout.getVisibleStart();
  }

  public void makeSlotVisible(int paramInt)
  {
    Rect localRect = this.mLayout.getSlotRect(paramInt, this.mTempRect);
    int i = this.mScrollX;
    int j = getWidth();
    int k = i + j;
    int l = localRect.left;
    int i1 = localRect.right;
    int i2 = i;
    if (j < i1 - l);
    for (i2 = i; ; i2 = i1 - j)
      do
        while (true)
        {
          setScrollPosition(i2);
          return;
          if (l >= i)
            break;
          i2 = l;
        }
      while (i1 <= k);
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!paramBoolean);
    do
    {
      return;
      int i = (this.mLayout.getVisibleStart() + this.mLayout.getVisibleEnd()) / 2;
      this.mLayout.setSize(paramInt3 - paramInt1, paramInt4 - paramInt2);
      makeSlotVisible(i);
    }
    while (this.mOverscrollEffect != 0);
    this.mPaper.setSize(paramInt3 - paramInt1, paramInt4 - paramInt2);
  }

  protected void onScrollPositionChanged(int paramInt)
  {
    int i = this.mLayout.getScrollLimit();
    this.mListener.onScrollPositionChanged(paramInt, i);
  }

  protected boolean onTouch(MotionEvent paramMotionEvent)
  {
    if (this.mUIListener != null)
      this.mUIListener.onUserInteraction();
    this.mGestureDetector.onTouchEvent(paramMotionEvent);
    switch (paramMotionEvent.getAction())
    {
    default:
      return true;
    case 0:
      if (!this.mScroller.isFinished());
      for (int i = 1; ; i = 0)
      {
        this.mDownInScrolling = i;
        this.mScroller.forceFinished();
        return true;
      }
    case 1:
    }
    this.mPaper.onRelease();
    invalidate();
    return true;
  }

  protected void render(GLCanvas paramGLCanvas)
  {
    super.render(paramGLCanvas);
    if (this.mRenderer == null)
      return;
    this.mRenderer.prepareDrawing();
    long l = AnimationTime.get();
    boolean bool1 = this.mScroller.advanceAnimation(l) | this.mLayout.advanceAnimation(l);
    int i = this.mScrollX;
    updateScrollPosition(this.mScroller.getPosition(), false);
    int j = this.mOverscrollEffect;
    boolean bool2 = false;
    if (j == 0)
    {
      int i9 = this.mScrollX;
      int i10 = this.mLayout.getScrollLimit();
      if (((i > 0) && (i9 == 0)) || ((i < i10) && (i9 == i10)))
      {
        float f = this.mScroller.getCurrVelocity();
        if (i9 == i10)
          f = -f;
        if (!Float.isNaN(f))
          this.mPaper.edgeReached(f);
      }
      bool2 = this.mPaper.advanceAnimation();
    }
    boolean bool3 = bool1 | bool2;
    if (this.mAnimation != null)
      bool3 |= this.mAnimation.calculate(l);
    paramGLCanvas.translate(-this.mScrollX, -this.mScrollY);
    int k = 0;
    int[] arrayOfInt = expandIntArray(this.mRequestRenderSlots, this.mLayout.mVisibleEnd - this.mLayout.mVisibleStart);
    for (int i1 = -1 + this.mLayout.mVisibleEnd; i1 >= this.mLayout.mVisibleStart; --i1)
    {
      int i7 = renderItem(paramGLCanvas, i1, 0, bool2);
      if ((i7 & 0x2) != 0)
        bool3 = true;
      if ((i7 & 0x1) == 0)
        continue;
      int i8 = k + 1;
      arrayOfInt[k] = i1;
      k = i8;
    }
    int i2 = 1;
    label313: int i3;
    int i4;
    label324: int i6;
    if (k != 0)
    {
      i3 = 0;
      i4 = 0;
      if (i3 < k)
      {
        int i5 = renderItem(paramGLCanvas, arrayOfInt[i3], i2, bool2);
        if ((i5 & 0x2) != 0)
          bool3 = true;
        if ((i5 & 0x1) == 0)
          break label478;
        i6 = i4 + 1;
        arrayOfInt[i4] = i3;
      }
    }
    while (true)
    {
      ++i3;
      i4 = i6;
      break label324:
      k = i4;
      ++i2;
      break label313:
      paramGLCanvas.translate(this.mScrollX, this.mScrollY);
      if (bool3)
        invalidate();
      UserInteractionListener localUserInteractionListener = this.mUIListener;
      if ((this.mMoreAnimation) && (!bool3) && (localUserInteractionListener != null))
      {
        Handler localHandler = this.mHandler;
        1 local1 = new Runnable(localUserInteractionListener)
        {
          public void run()
          {
            this.val$listener.onUserInteractionEnd();
          }
        };
        localHandler.post(local1);
      }
      this.mMoreAnimation = bool3;
      return;
      label478: i6 = i4;
    }
  }

  public void setCenterIndex(int paramInt)
  {
    int i = this.mLayout.mSlotCount;
    if ((paramInt < 0) || (paramInt >= i))
      return;
    Rect localRect = this.mLayout.getSlotRect(paramInt, this.mTempRect);
    setScrollPosition((localRect.left + localRect.right - getWidth()) / 2);
  }

  public void setListener(Listener paramListener)
  {
    this.mListener = paramListener;
  }

  public void setScrollPosition(int paramInt)
  {
    int i = Utils.clamp(paramInt, 0, this.mLayout.getScrollLimit());
    this.mScroller.setPosition(i);
    updateScrollPosition(i, false);
  }

  public boolean setSlotCount(int paramInt)
  {
    boolean bool = this.mLayout.setSlotCount(paramInt);
    if (this.mStartIndex != -1)
    {
      setCenterIndex(this.mStartIndex);
      this.mStartIndex = -1;
    }
    setScrollPosition(this.mScrollX);
    return bool;
  }

  public void setSlotRenderer(SlotRenderer paramSlotRenderer)
  {
    this.mRenderer = paramSlotRenderer;
    if (this.mRenderer == null)
      return;
    this.mRenderer.onSlotSizeChanged(this.mLayout.mSlotWidth, this.mLayout.mSlotHeight);
    this.mRenderer.onVisibleRangeChanged(getVisibleStart(), getVisibleEnd());
  }

  public void setSlotSpec(Spec paramSpec)
  {
    this.mLayout.setSlotSpec(paramSpec);
  }

  public void startRisingAnimation()
  {
    this.mAnimation = new RisingAnimation();
    this.mAnimation.start();
    if (this.mLayout.mSlotCount == 0)
      return;
    invalidate();
  }

  private static class IntegerAnimation extends Animation
  {
    private int mCurrent = 0;
    private boolean mEnabled = false;
    private int mFrom = 0;
    private int mTarget;

    public int get()
    {
      return this.mCurrent;
    }

    public int getTarget()
    {
      return this.mTarget;
    }

    protected void onCalculate(float paramFloat)
    {
      this.mCurrent = Math.round(this.mFrom + paramFloat * (this.mTarget - this.mFrom));
      if (paramFloat != 1.0F)
        return;
      this.mEnabled = false;
    }

    public void setEnabled(boolean paramBoolean)
    {
      this.mEnabled = paramBoolean;
    }

    public void startAnimateTo(int paramInt)
    {
      if (!this.mEnabled)
      {
        this.mCurrent = paramInt;
        this.mTarget = paramInt;
      }
      do
        return;
      while (paramInt == this.mTarget);
      this.mFrom = this.mCurrent;
      this.mTarget = paramInt;
      setDuration(180);
      start();
    }
  }

  public class Layout
  {
    private int mContentLength;
    private int mHeight;
    private SlotView.IntegerAnimation mHorizontalPadding = new SlotView.IntegerAnimation(null);
    private int mScrollPosition;
    private int mSlotCount;
    private int mSlotGap;
    private int mSlotHeight;
    private int mSlotWidth;
    private SlotView.Spec mSpec;
    private int mUnitCount;
    private SlotView.IntegerAnimation mVerticalPadding = new SlotView.IntegerAnimation(null);
    private int mVisibleEnd;
    private int mVisibleStart;
    private int mWidth;

    public Layout()
    {
    }

    private void initLayoutParameters()
    {
      if (this.mSpec.slotWidth != -1)
      {
        this.mSlotGap = 0;
        this.mSlotWidth = this.mSpec.slotWidth;
        this.mSlotHeight = this.mSpec.slotHeight;
        if (SlotView.this.mRenderer != null)
          label38: SlotView.this.mRenderer.onSlotSizeChanged(this.mSlotWidth, this.mSlotHeight);
        int[] arrayOfInt = new int[2];
        initLayoutParameters(this.mWidth, this.mHeight, this.mSlotWidth, this.mSlotHeight, arrayOfInt);
        this.mVerticalPadding.startAnimateTo(arrayOfInt[0]);
        this.mHorizontalPadding.startAnimateTo(arrayOfInt[1]);
        updateVisibleSlotRange();
        return;
      }
      if (this.mWidth > this.mHeight);
      for (int i = this.mSpec.rowsLand; ; i = this.mSpec.rowsPort)
      {
        this.mSlotGap = this.mSpec.slotGap;
        this.mSlotHeight = Math.max(1, (this.mHeight - (i - 1) * this.mSlotGap) / i);
        this.mSlotWidth = (this.mSlotHeight - this.mSpec.slotHeightAdditional);
        break label38:
      }
    }

    private void initLayoutParameters(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
    {
      int i = (paramInt2 + this.mSlotGap) / (paramInt4 + this.mSlotGap);
      if (i == 0)
        i = 1;
      this.mUnitCount = i;
      int j = Math.min(this.mUnitCount, this.mSlotCount);
      paramArrayOfInt[0] = ((paramInt2 - (j * paramInt4 + (j - 1) * this.mSlotGap)) / 2);
      int k = (-1 + (this.mSlotCount + this.mUnitCount)) / this.mUnitCount;
      this.mContentLength = (k * paramInt3 + (k - 1) * this.mSlotGap);
      paramArrayOfInt[1] = Math.max(0, (paramInt1 - this.mContentLength) / 2);
    }

    private void setVisibleRange(int paramInt1, int paramInt2)
    {
      if ((paramInt1 == this.mVisibleStart) && (paramInt2 == this.mVisibleEnd))
        return;
      if (paramInt1 < paramInt2)
      {
        this.mVisibleStart = paramInt1;
        this.mVisibleEnd = paramInt2;
      }
      while (true)
      {
        if (SlotView.this.mRenderer != null);
        SlotView.this.mRenderer.onVisibleRangeChanged(this.mVisibleStart, this.mVisibleEnd);
        return;
        this.mVisibleEnd = 0;
        this.mVisibleStart = 0;
      }
    }

    private void updateVisibleSlotRange()
    {
      int i = this.mScrollPosition;
      int j = Math.max(0, i / (this.mSlotWidth + this.mSlotGap) * this.mUnitCount);
      int k = (-1 + (i + this.mWidth + this.mSlotWidth + this.mSlotGap)) / (this.mSlotWidth + this.mSlotGap);
      setVisibleRange(j, Math.min(this.mSlotCount, k * this.mUnitCount));
    }

    public boolean advanceAnimation(long paramLong)
    {
      return this.mVerticalPadding.calculate(paramLong) | this.mHorizontalPadding.calculate(paramLong);
    }

    public int getScrollLimit()
    {
      int i = this.mContentLength - this.mWidth;
      if (i <= 0)
        i = 0;
      return i;
    }

    public int getSlotIndexByPosition(float paramFloat1, float paramFloat2)
    {
      int i = Math.round(paramFloat1) + this.mScrollPosition;
      int j = 0 + Math.round(paramFloat2);
      int k = i - this.mHorizontalPadding.get();
      int l = j - this.mVerticalPadding.get();
      if ((k < 0) || (l < 0));
      int i1;
      int i2;
      do
      {
        return -1;
        i1 = k / (this.mSlotWidth + this.mSlotGap);
        i2 = l / (this.mSlotHeight + this.mSlotGap);
      }
      while ((i2 >= this.mUnitCount) || (k % (this.mSlotWidth + this.mSlotGap) >= this.mSlotWidth) || (l % (this.mSlotHeight + this.mSlotGap) >= this.mSlotHeight));
      int i3 = i2 + i1 * this.mUnitCount;
      if (i3 >= this.mSlotCount)
        i3 = -1;
      return i3;
    }

    public Rect getSlotRect(int paramInt, Rect paramRect)
    {
      int i = paramInt / this.mUnitCount;
      int j = paramInt - i * this.mUnitCount;
      int k = this.mHorizontalPadding.get() + i * (this.mSlotWidth + this.mSlotGap);
      int l = this.mVerticalPadding.get() + j * (this.mSlotHeight + this.mSlotGap);
      paramRect.set(k, l, k + this.mSlotWidth, l + this.mSlotHeight);
      return paramRect;
    }

    public int getVisibleEnd()
    {
      return this.mVisibleEnd;
    }

    public int getVisibleStart()
    {
      return this.mVisibleStart;
    }

    public void setScrollPosition(int paramInt)
    {
      if (this.mScrollPosition == paramInt)
        return;
      this.mScrollPosition = paramInt;
      updateVisibleSlotRange();
    }

    public void setSize(int paramInt1, int paramInt2)
    {
      this.mWidth = paramInt1;
      this.mHeight = paramInt2;
      initLayoutParameters();
    }

    public boolean setSlotCount(int paramInt)
    {
      if (paramInt == this.mSlotCount);
      int i;
      int j;
      do
      {
        return false;
        if (this.mSlotCount != 0)
        {
          this.mHorizontalPadding.setEnabled(true);
          this.mVerticalPadding.setEnabled(true);
        }
        this.mSlotCount = paramInt;
        i = this.mHorizontalPadding.getTarget();
        j = this.mVerticalPadding.getTarget();
        initLayoutParameters();
      }
      while ((j == this.mVerticalPadding.getTarget()) && (i == this.mHorizontalPadding.getTarget()));
      return true;
    }

    public void setSlotSpec(SlotView.Spec paramSpec)
    {
      this.mSpec = paramSpec;
    }
  }

  public static abstract interface Listener
  {
    public abstract void onDown(int paramInt);

    public abstract void onLongTap(int paramInt);

    public abstract void onScrollPositionChanged(int paramInt1, int paramInt2);

    public abstract void onSingleTapUp(int paramInt);

    public abstract void onUp(boolean paramBoolean);
  }

  private class MyGestureListener
    implements GestureDetector.OnGestureListener
  {
    private boolean isDown;

    private MyGestureListener()
    {
    }

    private void cancelDown(boolean paramBoolean)
    {
      if (!this.isDown)
        return;
      this.isDown = false;
      SlotView.this.mListener.onUp(paramBoolean);
    }

    public boolean onDown(MotionEvent paramMotionEvent)
    {
      return false;
    }

    public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
    {
      cancelDown(false);
      int i = SlotView.this.mLayout.getScrollLimit();
      if (i == 0)
        return false;
      SlotView.this.mScroller.fling((int)(-paramFloat1), 0, i);
      if (SlotView.this.mUIListener != null)
        SlotView.this.mUIListener.onUserInteractionBegin();
      SlotView.this.invalidate();
      return true;
    }

    public void onLongPress(MotionEvent paramMotionEvent)
    {
      cancelDown(true);
      if (SlotView.this.mDownInScrolling)
        return;
      SlotView.this.lockRendering();
      try
      {
        int i = SlotView.this.mLayout.getSlotIndexByPosition(paramMotionEvent.getX(), paramMotionEvent.getY());
        if (i != -1)
          SlotView.this.mListener.onLongTap(i);
        return;
      }
      finally
      {
        SlotView.this.unlockRendering();
      }
    }

    public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
    {
      cancelDown(false);
      int i = SlotView.this.mScroller.startScroll(Math.round(paramFloat1), 0, SlotView.this.mLayout.getScrollLimit());
      if ((SlotView.this.mOverscrollEffect == 0) && (i != 0))
        SlotView.this.mPaper.overScroll(i);
      SlotView.this.invalidate();
      return true;
    }

    public void onShowPress(MotionEvent paramMotionEvent)
    {
      GLRoot localGLRoot = SlotView.this.getGLRoot();
      localGLRoot.lockRenderThread();
      try
      {
        boolean bool = this.isDown;
        if (bool)
          return;
        int i = SlotView.this.mLayout.getSlotIndexByPosition(paramMotionEvent.getX(), paramMotionEvent.getY());
        if (i != -1)
        {
          this.isDown = true;
          SlotView.this.mListener.onDown(i);
        }
        return;
      }
      finally
      {
        localGLRoot.unlockRenderThread();
      }
    }

    public boolean onSingleTapUp(MotionEvent paramMotionEvent)
    {
      cancelDown(false);
      if (SlotView.this.mDownInScrolling);
      int i;
      do
      {
        return true;
        i = SlotView.this.mLayout.getSlotIndexByPosition(paramMotionEvent.getX(), paramMotionEvent.getY());
      }
      while (i == -1);
      SlotView.this.mListener.onSingleTapUp(i);
      return true;
    }
  }

  public static class RisingAnimation extends SlotView.SlotAnimation
  {
    public void apply(GLCanvas paramGLCanvas, int paramInt, Rect paramRect)
    {
      paramGLCanvas.translate(0.0F, 0.0F, 128.0F * (1.0F - this.mProgress));
    }
  }

  public static class SimpleListener
    implements SlotView.Listener
  {
    public void onDown(int paramInt)
    {
    }

    public void onLongTap(int paramInt)
    {
    }

    public void onScrollPositionChanged(int paramInt1, int paramInt2)
    {
    }

    public void onSingleTapUp(int paramInt)
    {
    }

    public void onUp(boolean paramBoolean)
    {
    }
  }

  public static abstract class SlotAnimation extends Animation
  {
    protected float mProgress = 0.0F;

    public SlotAnimation()
    {
      setInterpolator(new DecelerateInterpolator(4.0F));
      setDuration(1500);
    }

    public abstract void apply(GLCanvas paramGLCanvas, int paramInt, Rect paramRect);

    protected void onCalculate(float paramFloat)
    {
      this.mProgress = paramFloat;
    }
  }

  public static abstract interface SlotRenderer
  {
    public abstract void onSlotSizeChanged(int paramInt1, int paramInt2);

    public abstract void onVisibleRangeChanged(int paramInt1, int paramInt2);

    public abstract void prepareDrawing();

    public abstract int renderSlot(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  }

  public static class Spec
  {
    public int rowsLand = -1;
    public int rowsPort = -1;
    public int slotGap = -1;
    public int slotHeight = -1;
    public int slotHeightAdditional = 0;
    public int slotWidth = -1;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.SlotView
 * JD-Core Version:    0.5.4
 */