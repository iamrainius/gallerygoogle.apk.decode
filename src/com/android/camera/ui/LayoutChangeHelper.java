package com.android.camera.ui;

import android.view.View;

public class LayoutChangeHelper
  implements LayoutChangeNotifier
{
  private boolean mFirstTimeLayout;
  private LayoutChangeNotifier.Listener mListener;
  private View mView;

  public LayoutChangeHelper(View paramView)
  {
    this.mView = paramView;
    this.mFirstTimeLayout = true;
  }

  public void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mListener == null);
    do
      return;
    while ((!this.mFirstTimeLayout) && (!paramBoolean));
    this.mFirstTimeLayout = false;
    this.mListener.onLayoutChange(this.mView, paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public void setOnLayoutChangeListener(LayoutChangeNotifier.Listener paramListener)
  {
    this.mListener = paramListener;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.LayoutChangeHelper
 * JD-Core Version:    0.5.4
 */