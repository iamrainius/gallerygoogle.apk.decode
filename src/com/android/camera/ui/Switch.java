package com.android.camera.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;
import java.util.List;

public class Switch extends CompoundButton
{
  private static final int[] CHECKED_STATE_SET = { 16842912 };
  private int mMinFlingVelocity;
  private Layout mOffLayout;
  private Layout mOnLayout;
  private int mSwitchBottom;
  private int mSwitchHeight;
  private int mSwitchLeft;
  private int mSwitchMinWidth;
  private int mSwitchPadding;
  private int mSwitchRight;
  private int mSwitchTextMaxWidth;
  private int mSwitchTop;
  private int mSwitchWidth;
  private final Rect mTempRect = new Rect();
  private ColorStateList mTextColors;
  private CharSequence mTextOff;
  private CharSequence mTextOn;
  private TextPaint mTextPaint = new TextPaint(1);
  private Drawable mThumbDrawable;
  private float mThumbPosition;
  private int mThumbTextPadding;
  private int mThumbWidth;
  private int mTouchMode;
  private int mTouchSlop;
  private float mTouchX;
  private float mTouchY;
  private Drawable mTrackDrawable;
  private VelocityTracker mVelocityTracker = VelocityTracker.obtain();

  public Switch(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 2130771979);
  }

  public Switch(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    Resources localResources = getResources();
    DisplayMetrics localDisplayMetrics = localResources.getDisplayMetrics();
    this.mTextPaint.density = localDisplayMetrics.density;
    this.mThumbDrawable = localResources.getDrawable(2130837887);
    this.mTrackDrawable = localResources.getDrawable(2130837892);
    this.mTextOn = localResources.getString(2131362013);
    this.mTextOff = localResources.getString(2131362014);
    this.mThumbTextPadding = localResources.getDimensionPixelSize(2131623965);
    this.mSwitchMinWidth = localResources.getDimensionPixelSize(2131623963);
    this.mSwitchTextMaxWidth = localResources.getDimensionPixelSize(2131623964);
    this.mSwitchPadding = localResources.getDimensionPixelSize(2131623962);
    setSwitchTextAppearance(paramContext, 16974081);
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(paramContext);
    this.mTouchSlop = localViewConfiguration.getScaledTouchSlop();
    this.mMinFlingVelocity = localViewConfiguration.getScaledMinimumFlingVelocity();
    refreshDrawableState();
    setChecked(isChecked());
  }

  private void animateThumbToCheckedState(boolean paramBoolean)
  {
    setChecked(paramBoolean);
  }

  private void cancelSuperTouch(MotionEvent paramMotionEvent)
  {
    MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
    localMotionEvent.setAction(3);
    super.onTouchEvent(localMotionEvent);
    localMotionEvent.recycle();
  }

  private boolean getTargetCheckedState()
  {
    return this.mThumbPosition >= getThumbScrollRange() / 2;
  }

  private int getThumbScrollRange()
  {
    if (this.mTrackDrawable == null)
      return 0;
    this.mTrackDrawable.getPadding(this.mTempRect);
    return this.mSwitchWidth - this.mThumbWidth - this.mTempRect.left - this.mTempRect.right;
  }

  private boolean hitThumb(float paramFloat1, float paramFloat2)
  {
    this.mThumbDrawable.getPadding(this.mTempRect);
    int i = this.mSwitchTop - this.mTouchSlop;
    int j = this.mSwitchLeft + (int)(0.5F + this.mThumbPosition) - this.mTouchSlop;
    int k = j + this.mThumbWidth + this.mTempRect.left + this.mTempRect.right + this.mTouchSlop;
    int l = this.mSwitchBottom + this.mTouchSlop;
    return (paramFloat1 > j) && (paramFloat1 < k) && (paramFloat2 > i) && (paramFloat2 < l);
  }

  private Layout makeLayout(CharSequence paramCharSequence, int paramInt)
  {
    int i = (int)Math.ceil(Layout.getDesiredWidth(paramCharSequence, this.mTextPaint));
    return new StaticLayout(paramCharSequence, 0, paramCharSequence.length(), this.mTextPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true, TextUtils.TruncateAt.END, Math.min(i, paramInt));
  }

  private void setThumbPosition(boolean paramBoolean)
  {
    float f;
    if (paramBoolean)
      f = getThumbScrollRange();
    while (true)
    {
      this.mThumbPosition = f;
      return;
      f = 0.0F;
    }
  }

  private void stopDrag(MotionEvent paramMotionEvent)
  {
    this.mTouchMode = 0;
    int i;
    if ((paramMotionEvent.getAction() == 1) && (isEnabled()))
    {
      i = 1;
      label22: cancelSuperTouch(paramMotionEvent);
      if (i == 0)
        break label98;
      this.mVelocityTracker.computeCurrentVelocity(1000);
      float f = this.mVelocityTracker.getXVelocity();
      if (Math.abs(f) <= this.mMinFlingVelocity)
        break label89;
      if (f <= 0.0F)
        break label83;
    }
    label83: label89: for (boolean bool = true; ; bool = getTargetCheckedState())
      while (true)
      {
        animateThumbToCheckedState(bool);
        return;
        i = 0;
        break label22:
        bool = false;
      }
    label98: animateThumbToCheckedState(isChecked());
  }

  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    int[] arrayOfInt = getDrawableState();
    if (this.mThumbDrawable != null)
      this.mThumbDrawable.setState(arrayOfInt);
    if (this.mTrackDrawable != null)
      this.mTrackDrawable.setState(arrayOfInt);
    invalidate();
  }

  public int getCompoundPaddingRight()
  {
    int i = super.getCompoundPaddingRight() + this.mSwitchWidth;
    if (!TextUtils.isEmpty(getText()))
      i += this.mSwitchPadding;
    return i;
  }

  @TargetApi(11)
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    this.mThumbDrawable.jumpToCurrentState();
    this.mTrackDrawable.jumpToCurrentState();
  }

  protected int[] onCreateDrawableState(int paramInt)
  {
    int[] arrayOfInt = super.onCreateDrawableState(paramInt + 1);
    if (isChecked())
      mergeDrawableStates(arrayOfInt, CHECKED_STATE_SET);
    return arrayOfInt;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int i = this.mSwitchLeft;
    int j = this.mSwitchTop;
    int k = this.mSwitchRight;
    int l = this.mSwitchBottom;
    this.mTrackDrawable.setBounds(i, j, k, l);
    this.mTrackDrawable.draw(paramCanvas);
    paramCanvas.save();
    this.mTrackDrawable.getPadding(this.mTempRect);
    int i1 = i + this.mTempRect.left;
    int i2 = j + this.mTempRect.top;
    int i3 = k - this.mTempRect.right;
    int i4 = l - this.mTempRect.bottom;
    paramCanvas.clipRect(i1, j, i3, l);
    this.mThumbDrawable.getPadding(this.mTempRect);
    int i5 = (int)(0.5F + this.mThumbPosition);
    int i6 = i5 + (i1 - this.mTempRect.left);
    int i7 = i1 + i5 + this.mThumbWidth + this.mTempRect.right;
    this.mThumbDrawable.setBounds(i6, j, i7, l);
    this.mThumbDrawable.draw(paramCanvas);
    if (this.mTextColors != null)
      this.mTextPaint.setColor(this.mTextColors.getColorForState(getDrawableState(), this.mTextColors.getDefaultColor()));
    this.mTextPaint.drawableState = getDrawableState();
    if (getTargetCheckedState());
    for (Layout localLayout = this.mOnLayout; ; localLayout = this.mOffLayout)
    {
      paramCanvas.translate((i6 + i7) / 2 - localLayout.getEllipsizedWidth() / 2, (i2 + i4) / 2 - localLayout.getHeight() / 2);
      localLayout.draw(paramCanvas);
      paramCanvas.restore();
      return;
    }
  }

  @TargetApi(14)
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    paramAccessibilityEvent.setClassName(Switch.class.getName());
  }

  @TargetApi(14)
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setClassName(Switch.class.getName());
    if (isChecked());
    CharSequence localCharSequence2;
    for (CharSequence localCharSequence1 = this.mTextOn; ; localCharSequence1 = this.mTextOff)
    {
      if (!TextUtils.isEmpty(localCharSequence1))
      {
        localCharSequence2 = paramAccessibilityNodeInfo.getText();
        if (!TextUtils.isEmpty(localCharSequence2))
          break;
        paramAccessibilityNodeInfo.setText(localCharSequence1);
      }
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(localCharSequence2).append(' ').append(localCharSequence1);
    paramAccessibilityNodeInfo.setText(localStringBuilder);
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    setThumbPosition(isChecked());
    int i = getWidth() - getPaddingRight();
    int j = i - this.mSwitchWidth;
    int l;
    int k;
    switch (0x70 & getGravity())
    {
    default:
      l = getPaddingTop();
      k = l + this.mSwitchHeight;
    case 16:
    case 80:
    }
    while (true)
    {
      this.mSwitchLeft = j;
      this.mSwitchTop = l;
      this.mSwitchBottom = k;
      this.mSwitchRight = i;
      return;
      l = (getPaddingTop() + getHeight() - getPaddingBottom()) / 2 - this.mSwitchHeight / 2;
      k = l + this.mSwitchHeight;
      continue;
      k = getHeight() - getPaddingBottom();
      l = k - this.mSwitchHeight;
    }
  }

  public void onMeasure(int paramInt1, int paramInt2)
  {
    View.MeasureSpec.getMode(paramInt1);
    View.MeasureSpec.getSize(paramInt1);
    if (this.mOnLayout == null)
      this.mOnLayout = makeLayout(this.mTextOn, this.mSwitchTextMaxWidth);
    if (this.mOffLayout == null)
      this.mOffLayout = makeLayout(this.mTextOff, this.mSwitchTextMaxWidth);
    this.mTrackDrawable.getPadding(this.mTempRect);
    int i = Math.min(this.mSwitchTextMaxWidth, Math.max(this.mOnLayout.getWidth(), this.mOffLayout.getWidth()));
    int j = Math.max(this.mSwitchMinWidth, i * 2 + 4 * this.mThumbTextPadding + this.mTempRect.left + this.mTempRect.right);
    int k = this.mTrackDrawable.getIntrinsicHeight();
    this.mThumbWidth = (i + 2 * this.mThumbTextPadding);
    this.mSwitchWidth = j;
    this.mSwitchHeight = k;
    super.onMeasure(paramInt1, paramInt2);
    int l = getMeasuredHeight();
    int i1 = getMeasuredWidth();
    if (l >= k)
      return;
    setMeasuredDimension(i1, k);
  }

  @TargetApi(14)
  public void onPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onPopulateAccessibilityEvent(paramAccessibilityEvent);
    if (isChecked());
    for (CharSequence localCharSequence = this.mOnLayout.getText(); ; localCharSequence = this.mOffLayout.getText())
    {
      if (!TextUtils.isEmpty(localCharSequence))
        paramAccessibilityEvent.getText().add(localCharSequence);
      return;
    }
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool = true;
    this.mVelocityTracker.addMovement(paramMotionEvent);
    switch (paramMotionEvent.getActionMasked())
    {
    default:
    case 0:
    case 2:
    case 1:
    case 3:
    }
    while (true)
    {
      label44: bool = super.onTouchEvent(paramMotionEvent);
      float f1;
      float f2;
      do
      {
        return bool;
        float f5 = paramMotionEvent.getX();
        float f6 = paramMotionEvent.getY();
        if ((isEnabled()) && (hitThumb(f5, f6)));
        this.mTouchMode = bool;
        this.mTouchX = f5;
        this.mTouchY = f6;
        break label44:
        switch (this.mTouchMode)
        {
        case 0:
        default:
          break;
        case 1:
          float f3 = paramMotionEvent.getX();
          float f4 = paramMotionEvent.getY();
          if ((Math.abs(f3 - this.mTouchX) > this.mTouchSlop) || (Math.abs(f4 - this.mTouchY) > this.mTouchSlop));
          this.mTouchMode = 2;
          getParent().requestDisallowInterceptTouchEvent(bool);
          this.mTouchX = f3;
          this.mTouchY = f4;
          return bool;
        case 2:
        }
        f1 = paramMotionEvent.getX();
        f2 = Math.max(0.0F, Math.min(f1 - this.mTouchX + this.mThumbPosition, getThumbScrollRange()));
      }
      while (f2 == this.mThumbPosition);
      this.mThumbPosition = f2;
      this.mTouchX = f1;
      invalidate();
      return bool;
      if (this.mTouchMode == 2)
      {
        stopDrag(paramMotionEvent);
        return bool;
      }
      this.mTouchMode = 0;
      this.mVelocityTracker.clear();
    }
  }

  public void setChecked(boolean paramBoolean)
  {
    super.setChecked(paramBoolean);
    setThumbPosition(paramBoolean);
    invalidate();
  }

  public void setSwitchTextAppearance(Context paramContext, int paramInt)
  {
    Resources localResources = getResources();
    this.mTextColors = getTextColors();
    int i = localResources.getDimensionPixelSize(2131623966);
    if (i == this.mTextPaint.getTextSize())
      return;
    this.mTextPaint.setTextSize(i);
    requestLayout();
  }

  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return (super.verifyDrawable(paramDrawable)) || (paramDrawable == this.mThumbDrawable) || (paramDrawable == this.mTrackDrawable);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.Switch
 * JD-Core Version:    0.5.4
 */