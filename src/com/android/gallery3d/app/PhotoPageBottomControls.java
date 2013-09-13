package com.android.gallery3d.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PhotoPageBottomControls
  implements View.OnClickListener
{
  private ViewGroup mContainer;
  private Animation mContainerAnimIn = new AlphaAnimation(0.0F, 1.0F);
  private Animation mContainerAnimOut = new AlphaAnimation(1.0F, 0.0F);
  private boolean mContainerVisible = false;
  private Map<View, Boolean> mControlsVisible = new HashMap();
  private Delegate mDelegate;
  private ViewGroup mParentLayout;

  public PhotoPageBottomControls(Delegate paramDelegate, Context paramContext, RelativeLayout paramRelativeLayout)
  {
    this.mDelegate = paramDelegate;
    this.mParentLayout = paramRelativeLayout;
    this.mContainer = ((ViewGroup)((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(2130968644, this.mParentLayout, false));
    this.mParentLayout.addView(this.mContainer);
    for (int i = -1 + this.mContainer.getChildCount(); i >= 0; --i)
    {
      View localView = this.mContainer.getChildAt(i);
      localView.setOnClickListener(this);
      this.mControlsVisible.put(localView, Boolean.valueOf(false));
    }
    this.mContainerAnimIn.setDuration(200L);
    this.mContainerAnimOut.setDuration(200L);
    this.mDelegate.refreshBottomControlsWhenReady();
  }

  private static Animation getControlAnimForVisibility(boolean paramBoolean)
  {
    if (paramBoolean);
    for (AlphaAnimation localAlphaAnimation = new AlphaAnimation(0.0F, 1.0F); ; localAlphaAnimation = new AlphaAnimation(1.0F, 0.0F))
    {
      localAlphaAnimation.setDuration(150L);
      return localAlphaAnimation;
    }
  }

  private void hide()
  {
    this.mContainer.clearAnimation();
    this.mContainerAnimOut.reset();
    this.mContainer.startAnimation(this.mContainerAnimOut);
    this.mContainer.setVisibility(4);
  }

  private void show()
  {
    this.mContainer.clearAnimation();
    this.mContainerAnimIn.reset();
    this.mContainer.startAnimation(this.mContainerAnimIn);
    this.mContainer.setVisibility(0);
  }

  public void cleanup()
  {
    this.mParentLayout.removeView(this.mContainer);
    this.mControlsVisible.clear();
  }

  public void onClick(View paramView)
  {
    if ((!this.mContainerVisible) || (!((Boolean)this.mControlsVisible.get(paramView)).booleanValue()))
      return;
    this.mDelegate.onBottomControlClicked(paramView.getId());
  }

  public void refresh()
  {
    boolean bool1 = this.mDelegate.canDisplayBottomControls();
    int i;
    if (bool1 != this.mContainerVisible)
    {
      i = 1;
      if (i != 0)
      {
        label20: if (!bool1)
          break label50;
        show();
      }
    }
    while (true)
    {
      this.mContainerVisible = bool1;
      if (this.mContainerVisible)
        break;
      return;
      i = 0;
      break label20:
      label50: hide();
    }
    Iterator localIterator = this.mControlsVisible.keySet().iterator();
    label72: View localView;
    Boolean localBoolean;
    boolean bool2;
    do
    {
      if (localIterator.hasNext());
      localView = (View)localIterator.next();
      localBoolean = (Boolean)this.mControlsVisible.get(localView);
      bool2 = this.mDelegate.canDisplayBottomControl(localView.getId());
    }
    while (localBoolean.booleanValue() == bool2);
    if (i == 0)
    {
      localView.clearAnimation();
      localView.startAnimation(getControlAnimForVisibility(bool2));
    }
    if (bool2);
    for (int j = 0; ; j = 4)
    {
      localView.setVisibility(j);
      localView.requestLayout();
      this.mControlsVisible.put(localView, Boolean.valueOf(bool2));
      break label72:
    }
  }

  public static abstract interface Delegate
  {
    public abstract boolean canDisplayBottomControl(int paramInt);

    public abstract boolean canDisplayBottomControls();

    public abstract void onBottomControlClicked(int paramInt);

    public abstract void refreshBottomControlsWhenReady();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.PhotoPageBottomControls
 * JD-Core Version:    0.5.4
 */