package com.android.camera.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.android.gallery3d.common.ApiHelper;

public class CameraSwitcher extends RotateImageView
  implements View.OnClickListener, View.OnTouchListener
{
  private int mCurrentIndex;
  private int[] mDrawIds;
  private Animator.AnimatorListener mHideAnimationListener;
  private Drawable mIndicator;
  private int mItemSize;
  private CameraSwitchListener mListener;
  private boolean mNeedsAnimationSetup;
  private View mParent;
  private View mPopup;
  private Animator.AnimatorListener mShowAnimationListener;
  private boolean mShowingPopup;
  private float mTranslationX = 0.0F;
  private float mTranslationY = 0.0F;

  public CameraSwitcher(Context paramContext)
  {
    super(paramContext);
    init(paramContext);
  }

  public CameraSwitcher(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext);
  }

  private boolean animateHidePopup()
  {
    if (!ApiHelper.HAS_VIEW_PROPERTY_ANIMATOR)
      return false;
    if (this.mHideAnimationListener == null)
      this.mHideAnimationListener = new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          if (CameraSwitcher.this.showsPopup())
            return;
          CameraSwitcher.this.mPopup.setVisibility(4);
        }
      };
    this.mPopup.animate().alpha(0.0F).scaleX(0.3F).scaleY(0.3F).translationX(this.mTranslationX).translationY(this.mTranslationY).setDuration(200L).setListener(this.mHideAnimationListener);
    animate().alpha(1.0F).setDuration(200L).setListener(null);
    return true;
  }

  private boolean animateShowPopup()
  {
    if (!ApiHelper.HAS_VIEW_PROPERTY_ANIMATOR)
      return false;
    if (this.mNeedsAnimationSetup)
      popupAnimationSetup();
    if (this.mShowAnimationListener == null)
      this.mShowAnimationListener = new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          if (!CameraSwitcher.this.showsPopup())
            return;
          CameraSwitcher.this.setVisibility(4);
        }
      };
    this.mPopup.animate().alpha(1.0F).scaleX(1.0F).scaleY(1.0F).translationX(0.0F).translationY(0.0F).setDuration(200L).setListener(null);
    animate().alpha(0.0F).setDuration(200L).setListener(this.mShowAnimationListener);
    return true;
  }

  private void hidePopup()
  {
    this.mShowingPopup = false;
    setVisibility(0);
    if ((this.mPopup != null) && (!animateHidePopup()))
      this.mPopup.setVisibility(4);
    this.mParent.setOnTouchListener(null);
  }

  private void init(Context paramContext)
  {
    this.mItemSize = paramContext.getResources().getDimensionPixelSize(2131624006);
    setOnClickListener(this);
    this.mIndicator = paramContext.getResources().getDrawable(2130837763);
  }

  private void initPopup()
  {
    this.mParent = LayoutInflater.from(getContext()).inflate(2130968657, (ViewGroup)getParent());
    LinearLayout localLinearLayout = (LinearLayout)this.mParent.findViewById(2131558408);
    this.mPopup = localLinearLayout;
    this.mPopup.setVisibility(4);
    this.mNeedsAnimationSetup = true;
    int i = -1 + this.mDrawIds.length;
    if (i < 0)
      label62: return;
    RotateImageView localRotateImageView = new RotateImageView(getContext());
    localRotateImageView.setImageResource(this.mDrawIds[i]);
    localRotateImageView.setBackgroundResource(2130837513);
    localRotateImageView.setOnClickListener(new View.OnClickListener(i)
    {
      public void onClick(View paramView)
      {
        CameraSwitcher.this.onCameraSelected(this.val$index);
      }
    });
    switch (this.mDrawIds[i])
    {
    case 2130837753:
    case 2130837754:
    case 2130837755:
    case 2130837756:
    default:
    case 2130837751:
    case 2130837758:
    case 2130837752:
    case 2130837757:
    }
    while (true)
    {
      localLinearLayout.addView(localRotateImageView, new LinearLayout.LayoutParams(this.mItemSize, this.mItemSize));
      --i;
      break label62:
      localRotateImageView.setContentDescription(getContext().getResources().getString(2131362005));
      continue;
      localRotateImageView.setContentDescription(getContext().getResources().getString(2131362006));
      continue;
      localRotateImageView.setContentDescription(getContext().getResources().getString(2131362007));
      continue;
      localRotateImageView.setContentDescription(getContext().getResources().getString(2131362008));
    }
  }

  private void onCameraSelected(int paramInt)
  {
    hidePopup();
    if ((paramInt == this.mCurrentIndex) || (this.mListener == null))
      return;
    setCurrentIndex(paramInt);
    this.mListener.onCameraSelected(paramInt);
  }

  private void popupAnimationSetup()
  {
    if (!ApiHelper.HAS_VIEW_PROPERTY_ANIMATOR)
      return;
    updateInitialTranslations();
    this.mPopup.setScaleX(0.3F);
    this.mPopup.setScaleY(0.3F);
    this.mPopup.setTranslationX(this.mTranslationX);
    this.mPopup.setTranslationY(this.mTranslationY);
    this.mNeedsAnimationSetup = false;
  }

  private void showSwitcher()
  {
    this.mShowingPopup = true;
    if (this.mPopup == null)
      initPopup();
    this.mPopup.setVisibility(0);
    if (!animateShowPopup())
      setVisibility(4);
    this.mParent.setOnTouchListener(this);
  }

  private void updateInitialTranslations()
  {
    if (getResources().getConfiguration().orientation == 1)
    {
      this.mTranslationX = (-getWidth() / 2);
      this.mTranslationY = getHeight();
      return;
    }
    this.mTranslationX = getWidth();
    this.mTranslationY = (getHeight() / 2);
  }

  public void closePopup()
  {
    if (!showsPopup())
      return;
    hidePopup();
  }

  public boolean isInsidePopup(MotionEvent paramMotionEvent)
  {
    if (!showsPopup());
    do
      return false;
    while ((paramMotionEvent.getX() < this.mPopup.getLeft()) || (paramMotionEvent.getX() >= this.mPopup.getRight()) || (paramMotionEvent.getY() < this.mPopup.getTop()) || (paramMotionEvent.getY() >= this.mPopup.getBottom()));
    return true;
  }

  public void onClick(View paramView)
  {
    showSwitcher();
    this.mListener.onShowSwitcherPopup();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    this.mIndicator.setBounds(getDrawable().getBounds());
    this.mIndicator.draw(paramCanvas);
  }

  public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
  {
    closePopup();
    return true;
  }

  public void setCurrentIndex(int paramInt)
  {
    this.mCurrentIndex = paramInt;
    setImageResource(this.mDrawIds[paramInt]);
  }

  public void setDrawIds(int[] paramArrayOfInt)
  {
    this.mDrawIds = paramArrayOfInt;
  }

  public void setOrientation(int paramInt, boolean paramBoolean)
  {
    super.setOrientation(paramInt, paramBoolean);
    ViewGroup localViewGroup = (ViewGroup)this.mPopup;
    if (localViewGroup == null)
      return;
    for (int i = 0; ; ++i)
    {
      if (i < localViewGroup.getChildCount());
      ((RotateImageView)localViewGroup.getChildAt(i)).setOrientation(paramInt, paramBoolean);
    }
  }

  public void setSwitchListener(CameraSwitchListener paramCameraSwitchListener)
  {
    this.mListener = paramCameraSwitchListener;
  }

  public boolean showsPopup()
  {
    return this.mShowingPopup;
  }

  public static abstract interface CameraSwitchListener
  {
    public abstract void onCameraSelected(int paramInt);

    public abstract void onShowSwitcherPopup();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.CameraSwitcher
 * JD-Core Version:    0.5.4
 */