package com.android.gallery3d.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class PhotoPageProgressBar
{
  private ViewGroup mContainer;
  private View mProgress;

  public PhotoPageProgressBar(Context paramContext, RelativeLayout paramRelativeLayout)
  {
    this.mContainer = ((ViewGroup)((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(2130968645, paramRelativeLayout, false));
    paramRelativeLayout.addView(this.mContainer);
    this.mProgress = this.mContainer.findViewById(2131558569);
  }

  public void hideProgress()
  {
    this.mContainer.setVisibility(4);
  }

  public void setProgress(int paramInt)
  {
    this.mContainer.setVisibility(0);
    ViewGroup.LayoutParams localLayoutParams = this.mProgress.getLayoutParams();
    localLayoutParams.width = (paramInt * this.mContainer.getWidth() / 100);
    this.mProgress.setLayoutParams(localLayoutParams);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.PhotoPageProgressBar
 * JD-Core Version:    0.5.4
 */