package com.android.camera.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class LayoutNotifyView extends View
  implements LayoutChangeNotifier
{
  private LayoutChangeHelper mLayoutChangeHelper = new LayoutChangeHelper(this);

  public LayoutNotifyView(Context paramContext)
  {
    super(paramContext);
  }

  public LayoutNotifyView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.mLayoutChangeHelper.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public void setOnLayoutChangeListener(LayoutChangeNotifier.Listener paramListener)
  {
    this.mLayoutChangeHelper.setOnLayoutChangeListener(paramListener);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.LayoutNotifyView
 * JD-Core Version:    0.5.4
 */