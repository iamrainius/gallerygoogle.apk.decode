package com.android.camera.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import com.android.gallery3d.common.ApiHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PieRenderer extends OverlayRenderer
  implements FocusIndicator
{
  private ScaleAnimation mAnimation = new ScaleAnimation();
  private boolean mBlockFocus;
  private Point mCenter;
  private int mCenterX;
  private int mCenterY;
  private RectF mCircle;
  private int mCircleSize;
  private PieItem mCurrentItem;
  private RectF mDial;
  private int mDialAngle;
  private Runnable mDisappear = new Disappear(null);
  private Point mDown;
  private Animation.AnimationListener mEndAction = new EndAction(null);
  private LinearAnimation mFadeIn;
  private int mFailColor;
  private volatile boolean mFocusCancelled;
  private Paint mFocusPaint;
  private int mFocusX;
  private int mFocusY;
  private boolean mFocused;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default:
      case 0:
      case 1:
      }
      do
      {
        do
          return;
        while (PieRenderer.this.mListener == null);
        PieRenderer.this.mListener.onPieOpened(PieRenderer.this.mCenter.x, PieRenderer.this.mCenter.y);
        return;
      }
      while (PieRenderer.this.mListener == null);
      PieRenderer.this.mListener.onPieClosed();
    }
  };
  private int mInnerOffset;
  private int mInnerStroke;
  private List<PieItem> mItems;
  private PieListener mListener;
  private PieItem mOpenItem;
  private boolean mOpening;
  private int mOuterStroke;
  private Point mPoint1;
  private Point mPoint2;
  private int mRadius;
  private int mRadiusInc;
  private Paint mSelectedPaint;
  private int mStartAnimationAngle;
  private volatile int mState;
  private Paint mSubPaint;
  private int mSuccessColor;
  private boolean mTapMode;
  private int mTouchOffset;
  private int mTouchSlopSquared;
  private LinearAnimation mXFade;

  public PieRenderer(Context paramContext)
  {
    init(paramContext);
  }

  private void cancelFocus()
  {
    this.mFocusCancelled = true;
    this.mOverlay.removeCallbacks(this.mDisappear);
    if (this.mAnimation != null)
      this.mAnimation.cancel();
    this.mFocusCancelled = false;
    this.mFocused = false;
    this.mState = 0;
  }

  private static void convertCart(int paramInt1, int paramInt2, Point paramPoint)
  {
    double d = 6.283185307179586D * (paramInt1 % 360) / 360.0D;
    paramPoint.x = (int)(0.5D + paramInt2 * Math.cos(d));
    paramPoint.y = (int)(0.5D + paramInt2 * Math.sin(d));
  }

  private void deselect()
  {
    if (this.mCurrentItem != null)
      this.mCurrentItem.setSelected(false);
    if (this.mOpenItem != null)
      this.mOpenItem = null;
    this.mCurrentItem = null;
  }

  private void drawItem(Canvas paramCanvas, PieItem paramPieItem, float paramFloat)
  {
    float f;
    if ((this.mState == 8) && (paramPieItem.getPath() != null))
    {
      if (paramPieItem.isSelected())
      {
        Paint localPaint = this.mSelectedPaint;
        int i = paramCanvas.save();
        paramCanvas.rotate(getDegrees(paramPieItem.getStartAngle()), this.mCenter.x, this.mCenter.y);
        paramCanvas.drawPath(paramPieItem.getPath(), localPaint);
        paramCanvas.restoreToCount(i);
      }
      if (!paramPieItem.isEnabled())
        break label104;
      f = 1.0F;
    }
    while (true)
    {
      paramPieItem.setAlpha(paramFloat * f);
      paramPieItem.draw(paramCanvas);
      return;
      label104: f = 0.3F;
    }
  }

  private void drawLine(Canvas paramCanvas, int paramInt, Paint paramPaint)
  {
    convertCart(paramInt, this.mCircleSize - this.mInnerOffset, this.mPoint1);
    convertCart(paramInt, this.mCircleSize - this.mInnerOffset + this.mInnerOffset / 3, this.mPoint2);
    paramCanvas.drawLine(this.mPoint1.x + this.mFocusX, this.mPoint1.y + this.mFocusY, this.mPoint2.x + this.mFocusX, this.mPoint2.y + this.mFocusY, paramPaint);
  }

  private void fadeIn()
  {
    this.mFadeIn = new LinearAnimation(0.0F, 1.0F);
    this.mFadeIn.setDuration(200L);
    this.mFadeIn.setAnimationListener(new Animation.AnimationListener()
    {
      public void onAnimationEnd(Animation paramAnimation)
      {
        PieRenderer.access$402(PieRenderer.this, null);
      }

      public void onAnimationRepeat(Animation paramAnimation)
      {
      }

      public void onAnimationStart(Animation paramAnimation)
      {
      }
    });
    this.mFadeIn.startNow();
    this.mOverlay.startAnimation(this.mFadeIn);
  }

  private PieItem findItem(PointF paramPointF)
  {
    if (this.mOpenItem != null);
    for (List localList = this.mOpenItem.getItems(); ; localList = this.mItems)
    {
      Iterator localIterator = localList.iterator();
      PieItem localPieItem;
      do
      {
        if (!localIterator.hasNext())
          break label63;
        localPieItem = (PieItem)localIterator.next();
      }
      while (!inside(paramPointF, localPieItem));
      return localPieItem;
    }
    label63: return null;
  }

  private float getDegrees(double paramDouble)
  {
    return (float)(360.0D - 180.0D * paramDouble / 3.141592653589793D);
  }

  private PointF getPolar(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    PointF localPointF = new PointF();
    localPointF.x = 1.570796F;
    float f1 = paramFloat1 - this.mCenter.x;
    float f2 = this.mCenter.y - paramFloat2;
    localPointF.y = (float)Math.sqrt(f1 * f1 + f2 * f2);
    if (f1 != 0.0F)
    {
      localPointF.x = (float)Math.atan2(f2, f1);
      if (localPointF.x < 0.0F)
        localPointF.x = (float)(6.283185307179586D + localPointF.x);
    }
    float f3 = localPointF.y;
    if (paramBoolean);
    for (int i = this.mTouchOffset; ; i = 0)
    {
      localPointF.y = (f3 + i);
      return localPointF;
    }
  }

  private int getRandomRange()
  {
    return (int)(-60.0D + 120.0D * Math.random());
  }

  private boolean hasMoved(MotionEvent paramMotionEvent)
  {
    return this.mTouchSlopSquared < (paramMotionEvent.getX() - this.mDown.x) * (paramMotionEvent.getX() - this.mDown.x) + (paramMotionEvent.getY() - this.mDown.y) * (paramMotionEvent.getY() - this.mDown.y);
  }

  private void init(Context paramContext)
  {
    setVisible(false);
    this.mItems = new ArrayList();
    Resources localResources = paramContext.getResources();
    this.mRadius = localResources.getDimensionPixelSize(2131623996);
    this.mCircleSize = (this.mRadius - localResources.getDimensionPixelSize(2131624001));
    this.mRadiusInc = localResources.getDimensionPixelSize(2131623997);
    this.mTouchOffset = localResources.getDimensionPixelSize(2131623999);
    this.mCenter = new Point(0, 0);
    this.mSelectedPaint = new Paint();
    this.mSelectedPaint.setColor(Color.argb(255, 51, 181, 229));
    this.mSelectedPaint.setAntiAlias(true);
    this.mSubPaint = new Paint();
    this.mSubPaint.setAntiAlias(true);
    this.mSubPaint.setColor(Color.argb(200, 250, 230, 128));
    this.mFocusPaint = new Paint();
    this.mFocusPaint.setAntiAlias(true);
    this.mFocusPaint.setColor(-1);
    this.mFocusPaint.setStyle(Paint.Style.STROKE);
    this.mSuccessColor = -16711936;
    this.mFailColor = -65536;
    this.mCircle = new RectF();
    this.mDial = new RectF();
    this.mPoint1 = new Point();
    this.mPoint2 = new Point();
    this.mInnerOffset = localResources.getDimensionPixelSize(2131624002);
    this.mOuterStroke = localResources.getDimensionPixelSize(2131624003);
    this.mInnerStroke = localResources.getDimensionPixelSize(2131624004);
    this.mState = 0;
    this.mBlockFocus = false;
    this.mTouchSlopSquared = ViewConfiguration.get(paramContext).getScaledTouchSlop();
    this.mTouchSlopSquared *= this.mTouchSlopSquared;
    this.mDown = new Point();
  }

  private boolean inside(PointF paramPointF, PieItem paramPieItem)
  {
    return (paramPieItem.getInnerRadius() < paramPointF.y) && (paramPieItem.getStartAngle() < paramPointF.x) && (paramPieItem.getStartAngle() + paramPieItem.getSweep() > paramPointF.x) && (((!this.mTapMode) || (paramPieItem.getOuterRadius() > paramPointF.y)));
  }

  private void layoutItems(List<PieItem> paramList, float paramFloat, int paramInt1, int paramInt2, int paramInt3)
  {
    float f1 = (2.094395F - 2.0F * 0.1309F) / paramList.size();
    float f2 = 0.1309F + (paramFloat - 1.047198F) + f1 / 2.0F;
    Iterator localIterator1 = paramList.iterator();
    PieItem localPieItem2;
    do
    {
      if (!localIterator1.hasNext())
        break label82;
      localPieItem2 = (PieItem)localIterator1.next();
    }
    while (localPieItem2.getCenter() < 0.0F);
    f1 = localPieItem2.getSweep();
    label82: Path localPath = makeSlice(getDegrees(0.0D) - paramInt3, getDegrees(f1) + paramInt3, paramInt2, paramInt1, this.mCenter);
    Iterator localIterator2 = paramList.iterator();
    float f3 = f2;
    while (localIterator2.hasNext())
    {
      PieItem localPieItem1 = (PieItem)localIterator2.next();
      localPieItem1.setPath(localPath);
      if (localPieItem1.getCenter() >= 0.0F)
        f3 = localPieItem1.getCenter();
      int i = localPieItem1.getIntrinsicWidth();
      int j = localPieItem1.getIntrinsicHeight();
      int k = paramInt1 + 2 * (paramInt2 - paramInt1) / 3;
      int l = (int)(k * Math.cos(f3));
      int i1 = this.mCenter.y - (int)(k * Math.sin(f3)) - j / 2;
      int i2 = l + this.mCenter.x - i / 2;
      localPieItem1.setBounds(i2, i1, i2 + i, i1 + j);
      localPieItem1.setGeometry(f3 - f1 / 2.0F, f1, paramInt1, paramInt2);
      if (localPieItem1.hasItems())
        layoutItems(localPieItem1.getItems(), f3, paramInt1, paramInt2 + this.mRadiusInc / 2, paramInt3);
      f3 += f1;
    }
  }

  private void layoutPie()
  {
    int i = 2 + this.mRadius;
    int j = this.mRadius + this.mRadiusInc - 2;
    layoutItems(this.mItems, 1.570796F, i, j, 1);
  }

  private Path makeSlice(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, Point paramPoint)
  {
    RectF localRectF1 = new RectF(paramPoint.x - paramInt1, paramPoint.y - paramInt1, paramInt1 + paramPoint.x, paramInt1 + paramPoint.y);
    RectF localRectF2 = new RectF(paramPoint.x - paramInt2, paramPoint.y - paramInt2, paramInt2 + paramPoint.x, paramInt2 + paramPoint.y);
    Path localPath = new Path();
    localPath.arcTo(localRectF1, paramFloat1, paramFloat2 - paramFloat1, true);
    localPath.arcTo(localRectF2, paramFloat2, paramFloat1 - paramFloat2);
    localPath.close();
    return localPath;
  }

  private void onEnter(PieItem paramPieItem)
  {
    if (this.mCurrentItem != null)
      this.mCurrentItem.setSelected(false);
    if ((paramPieItem != null) && (paramPieItem.isEnabled()))
    {
      paramPieItem.setSelected(true);
      this.mCurrentItem = paramPieItem;
      if ((this.mCurrentItem != this.mOpenItem) && (this.mCurrentItem.hasItems()))
        openCurrentItem();
      return;
    }
    this.mCurrentItem = null;
  }

  private void openCurrentItem()
  {
    if ((this.mCurrentItem == null) || (!this.mCurrentItem.hasItems()))
      return;
    this.mCurrentItem.setSelected(false);
    this.mOpenItem = this.mCurrentItem;
    this.mOpening = true;
    this.mXFade = new LinearAnimation(1.0F, 0.0F);
    this.mXFade.setDuration(200L);
    this.mXFade.setAnimationListener(new Animation.AnimationListener()
    {
      public void onAnimationEnd(Animation paramAnimation)
      {
        PieRenderer.access$702(PieRenderer.this, null);
      }

      public void onAnimationRepeat(Animation paramAnimation)
      {
      }

      public void onAnimationStart(Animation paramAnimation)
      {
      }
    });
    this.mXFade.startNow();
    this.mOverlay.startAnimation(this.mXFade);
  }

  private void setCircle(int paramInt1, int paramInt2)
  {
    this.mCircle.set(paramInt1 - this.mCircleSize, paramInt2 - this.mCircleSize, paramInt1 + this.mCircleSize, paramInt2 + this.mCircleSize);
    this.mDial.set(paramInt1 - this.mCircleSize + this.mInnerOffset, paramInt2 - this.mCircleSize + this.mInnerOffset, paramInt1 + this.mCircleSize - this.mInnerOffset, paramInt2 + this.mCircleSize - this.mInnerOffset);
  }

  private void show(boolean paramBoolean)
  {
    label66: Handler localHandler;
    int i;
    if (paramBoolean)
    {
      this.mState = 8;
      this.mCurrentItem = null;
      this.mOpenItem = null;
      Iterator localIterator = this.mItems.iterator();
      while (localIterator.hasNext())
        ((PieItem)localIterator.next()).setSelected(false);
      layoutPie();
      fadeIn();
      setVisible(paramBoolean);
      localHandler = this.mHandler;
      i = 0;
      if (!paramBoolean)
        break label116;
    }
    while (true)
    {
      localHandler.sendEmptyMessage(i);
      return;
      this.mState = 0;
      this.mTapMode = false;
      if (this.mXFade != null);
      this.mXFade.cancel();
      break label66:
      label116: i = 1;
    }
  }

  private void startAnimation(long paramLong, boolean paramBoolean, float paramFloat)
  {
    startAnimation(paramLong, paramBoolean, this.mDialAngle, paramFloat);
  }

  private void startAnimation(long paramLong, boolean paramBoolean, float paramFloat1, float paramFloat2)
  {
    setVisible(true);
    this.mAnimation.reset();
    this.mAnimation.setDuration(paramLong);
    this.mAnimation.setScale(paramFloat1, paramFloat2);
    ScaleAnimation localScaleAnimation = this.mAnimation;
    if (paramBoolean);
    for (Animation.AnimationListener localAnimationListener = this.mEndAction; ; localAnimationListener = null)
    {
      localScaleAnimation.setAnimationListener(localAnimationListener);
      this.mOverlay.startAnimation(this.mAnimation);
      update();
      return;
    }
  }

  private void startFadeOut()
  {
    if (ApiHelper.HAS_VIEW_PROPERTY_ANIMATOR)
    {
      this.mOverlay.animate().alpha(0.0F).setListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          PieRenderer.this.deselect();
          PieRenderer.this.show(false);
          PieRenderer.this.mOverlay.setAlpha(1.0F);
          super.onAnimationEnd(paramAnimator);
        }
      }).setDuration(300L);
      return;
    }
    deselect();
    show(false);
  }

  public void addItem(PieItem paramPieItem)
  {
    this.mItems.add(paramPieItem);
  }

  public void alignFocus(int paramInt1, int paramInt2)
  {
    this.mOverlay.removeCallbacks(this.mDisappear);
    this.mAnimation.cancel();
    this.mAnimation.reset();
    this.mFocusX = paramInt1;
    this.mFocusY = paramInt2;
    this.mDialAngle = 157;
    setCircle(paramInt1, paramInt2);
    this.mFocused = false;
  }

  public void clear()
  {
    if (this.mState == 8)
      return;
    cancelFocus();
    this.mOverlay.post(this.mDisappear);
  }

  public void clearItems()
  {
    this.mItems.clear();
  }

  public void drawFocus(Canvas paramCanvas)
  {
    if (this.mBlockFocus);
    do
    {
      return;
      this.mFocusPaint.setStrokeWidth(this.mOuterStroke);
      paramCanvas.drawCircle(this.mFocusX, this.mFocusY, this.mCircleSize, this.mFocusPaint);
    }
    while (this.mState == 8);
    int i = this.mFocusPaint.getColor();
    Paint localPaint;
    if (this.mState == 2)
    {
      localPaint = this.mFocusPaint;
      if (!this.mFocused)
        break label242;
    }
    for (int j = this.mSuccessColor; ; j = this.mFailColor)
    {
      localPaint.setColor(j);
      this.mFocusPaint.setStrokeWidth(this.mInnerStroke);
      drawLine(paramCanvas, this.mDialAngle, this.mFocusPaint);
      drawLine(paramCanvas, 45 + this.mDialAngle, this.mFocusPaint);
      drawLine(paramCanvas, 180 + this.mDialAngle, this.mFocusPaint);
      drawLine(paramCanvas, 225 + this.mDialAngle, this.mFocusPaint);
      paramCanvas.save();
      paramCanvas.rotate(this.mDialAngle, this.mFocusX, this.mFocusY);
      paramCanvas.drawArc(this.mDial, 0.0F, 45.0F, false, this.mFocusPaint);
      paramCanvas.drawArc(this.mDial, 180.0F, 45.0F, false, this.mFocusPaint);
      paramCanvas.restore();
      this.mFocusPaint.setColor(i);
      label242: return;
    }
  }

  public int getSize()
  {
    return 2 * this.mCircleSize;
  }

  public boolean handlesTouch()
  {
    return true;
  }

  public void hide()
  {
    show(false);
  }

  public void layout(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.layout(paramInt1, paramInt2, paramInt3, paramInt4);
    this.mCenterX = ((paramInt3 - paramInt1) / 2);
    this.mCenterY = ((paramInt4 - paramInt2) / 2);
    this.mFocusX = this.mCenterX;
    this.mFocusY = this.mCenterY;
    setCircle(this.mFocusX, this.mFocusY);
    if ((!isVisible()) || (this.mState != 8))
      return;
    setCenter(this.mCenterX, this.mCenterY);
    layoutPie();
  }

  public void onDraw(Canvas paramCanvas)
  {
    float f1 = 1.0F;
    if (this.mXFade != null)
      f1 = this.mXFade.getValue();
    int i;
    while (true)
    {
      i = paramCanvas.save();
      if (this.mFadeIn != null)
      {
        float f3 = 0.9F + 0.1F * f1;
        paramCanvas.scale(f3, f3, this.mCenter.x, this.mCenter.y);
      }
      drawFocus(paramCanvas);
      if (this.mState != 2)
        break;
      paramCanvas.restoreToCount(i);
      return;
      if (this.mFadeIn == null)
        continue;
      f1 = this.mFadeIn.getValue();
    }
    if ((this.mOpenItem == null) || (this.mXFade != null))
    {
      Iterator localIterator1 = this.mItems.iterator();
      while (localIterator1.hasNext())
        drawItem(paramCanvas, (PieItem)localIterator1.next(), f1);
    }
    if (this.mOpenItem != null)
    {
      Iterator localIterator2 = this.mOpenItem.getItems().iterator();
      if (localIterator2.hasNext())
      {
        label176: PieItem localPieItem = (PieItem)localIterator2.next();
        float f2;
        if (this.mXFade != null)
          f2 = 1.0F - 0.5F * f1;
        while (true)
        {
          drawItem(paramCanvas, localPieItem, f2);
          break label176:
          f2 = 1.0F;
        }
      }
    }
    paramCanvas.restoreToCount(i);
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    int i = paramMotionEvent.getActionMasked();
    boolean bool1;
    label26: PointF localPointF;
    if (!this.mTapMode)
    {
      bool1 = true;
      localPointF = getPolar(f1, f2, bool1);
      if (i != 0)
        break label134;
      this.mDown.x = (int)paramMotionEvent.getX();
      this.mDown.y = (int)paramMotionEvent.getY();
      this.mOpening = false;
      if (!this.mTapMode)
        break label119;
      PieItem localPieItem3 = findItem(localPointF);
      if ((localPieItem3 != null) && (this.mCurrentItem != localPieItem3))
      {
        this.mState = 8;
        onEnter(localPieItem3);
      }
    }
    label119: PieItem localPieItem2;
    do
    {
      return true;
      bool1 = false;
      break label26:
      setCenter((int)f1, (int)f2);
      show(true);
      return true;
      label134: if (1 != i)
        break label235;
      if (!isVisible())
        break label369;
      localPieItem2 = this.mCurrentItem;
      if (this.mTapMode)
      {
        localPieItem2 = findItem(localPointF);
        if ((localPieItem2 != null) && (this.mOpening))
        {
          this.mOpening = false;
          return true;
        }
      }
      if (localPieItem2 != null)
        continue;
      this.mTapMode = false;
      show(false);
      return true;
    }
    while ((this.mOpening) || (localPieItem2.hasItems()));
    localPieItem2.performClick();
    startFadeOut();
    this.mTapMode = false;
    return true;
    if (3 == i)
    {
      if ((isVisible()) || (this.mTapMode))
        label235: show(false);
      deselect();
      return false;
    }
    if (2 == i)
    {
      if (localPointF.y < this.mRadius)
      {
        if (this.mOpenItem != null)
          this.mOpenItem = null;
        while (true)
        {
          return false;
          deselect();
        }
      }
      PieItem localPieItem1 = findItem(localPointF);
      boolean bool2 = hasMoved(paramMotionEvent);
      if ((localPieItem1 != null) && (this.mCurrentItem != localPieItem1) && (((!this.mOpening) || (bool2))))
      {
        this.mOpening = false;
        if (bool2)
          this.mTapMode = false;
        onEnter(localPieItem1);
      }
    }
    label369: return false;
  }

  public void setBlockFocus(boolean paramBoolean)
  {
    this.mBlockFocus = paramBoolean;
    if (!paramBoolean)
      return;
    clear();
  }

  public void setCenter(int paramInt1, int paramInt2)
  {
    this.mCenter.x = paramInt1;
    this.mCenter.y = paramInt2;
    alignFocus(paramInt1, paramInt2);
  }

  public void setFocus(int paramInt1, int paramInt2)
  {
    this.mFocusX = paramInt1;
    this.mFocusY = paramInt2;
    setCircle(this.mFocusX, this.mFocusY);
  }

  public void setPieListener(PieListener paramPieListener)
  {
    this.mListener = paramPieListener;
  }

  public void showFail(boolean paramBoolean)
  {
    if (this.mState != 1)
      return;
    startAnimation(100L, paramBoolean, this.mStartAnimationAngle);
    this.mState = 2;
    this.mFocused = false;
  }

  public void showInCenter()
  {
    if ((this.mState == 8) && (isVisible()))
    {
      this.mTapMode = false;
      show(false);
      return;
    }
    if (this.mState != 0)
      cancelFocus();
    this.mState = 8;
    setCenter(this.mCenterX, this.mCenterY);
    this.mTapMode = true;
    show(true);
  }

  public void showStart()
  {
    if (this.mState == 8)
      return;
    cancelFocus();
    this.mStartAnimationAngle = 67;
    int i = getRandomRange();
    startAnimation(600L, false, this.mStartAnimationAngle, i + this.mStartAnimationAngle);
    this.mState = 1;
  }

  public void showSuccess(boolean paramBoolean)
  {
    if (this.mState != 1)
      return;
    startAnimation(100L, paramBoolean, this.mStartAnimationAngle);
    this.mState = 2;
    this.mFocused = true;
  }

  public boolean showsItems()
  {
    return this.mTapMode;
  }

  private class Disappear
    implements Runnable
  {
    private Disappear()
    {
    }

    public void run()
    {
      if (PieRenderer.this.mState == 8)
        return;
      PieRenderer.this.setVisible(false);
      PieRenderer.access$1102(PieRenderer.this, PieRenderer.this.mCenterX);
      PieRenderer.access$1302(PieRenderer.this, PieRenderer.this.mCenterY);
      PieRenderer.access$1002(PieRenderer.this, 0);
      PieRenderer.this.setCircle(PieRenderer.this.mFocusX, PieRenderer.this.mFocusY);
      PieRenderer.access$1602(PieRenderer.this, false);
    }
  }

  private class EndAction
    implements Animation.AnimationListener
  {
    private EndAction()
    {
    }

    public void onAnimationEnd(Animation paramAnimation)
    {
      if (PieRenderer.this.mFocusCancelled)
        return;
      PieRenderer.this.mOverlay.postDelayed(PieRenderer.this.mDisappear, 200L);
    }

    public void onAnimationRepeat(Animation paramAnimation)
    {
    }

    public void onAnimationStart(Animation paramAnimation)
    {
    }
  }

  private class LinearAnimation extends Animation
  {
    private float mFrom;
    private float mTo;
    private float mValue;

    public LinearAnimation(float paramFloat1, float arg3)
    {
      setFillAfter(true);
      setInterpolator(new LinearInterpolator());
      this.mFrom = paramFloat1;
      Object localObject;
      this.mTo = localObject;
    }

    protected void applyTransformation(float paramFloat, Transformation paramTransformation)
    {
      this.mValue = (this.mFrom + paramFloat * (this.mTo - this.mFrom));
    }

    public float getValue()
    {
      return this.mValue;
    }
  }

  public static abstract interface PieListener
  {
    public abstract void onPieClosed();

    public abstract void onPieOpened(int paramInt1, int paramInt2);
  }

  private class ScaleAnimation extends Animation
  {
    private float mFrom = 1.0F;
    private float mTo = 1.0F;

    public ScaleAnimation()
    {
      setFillAfter(true);
    }

    protected void applyTransformation(float paramFloat, Transformation paramTransformation)
    {
      PieRenderer.access$1702(PieRenderer.this, (int)(this.mFrom + paramFloat * (this.mTo - this.mFrom)));
    }

    public void setScale(float paramFloat1, float paramFloat2)
    {
      this.mFrom = paramFloat1;
      this.mTo = paramFloat2;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.PieRenderer
 * JD-Core Version:    0.5.4
 */