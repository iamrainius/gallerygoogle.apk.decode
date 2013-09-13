package com.android.camera.ui;

import android.view.View;

public abstract interface LayoutChangeNotifier
{
  public static abstract interface Listener
  {
    public abstract void onLayoutChange(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.LayoutChangeNotifier
 * JD-Core Version:    0.5.4
 */