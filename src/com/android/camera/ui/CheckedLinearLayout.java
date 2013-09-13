package com.android.camera.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckedLinearLayout extends LinearLayout
  implements Checkable
{
  private static final int[] CHECKED_STATE_SET = { 16842912 };
  private boolean mChecked;

  public CheckedLinearLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public boolean isChecked()
  {
    return this.mChecked;
  }

  public int[] onCreateDrawableState(int paramInt)
  {
    int[] arrayOfInt = super.onCreateDrawableState(paramInt + 1);
    if (this.mChecked)
      mergeDrawableStates(arrayOfInt, CHECKED_STATE_SET);
    return arrayOfInt;
  }

  public void setChecked(boolean paramBoolean)
  {
    if (this.mChecked == paramBoolean)
      return;
    this.mChecked = paramBoolean;
    refreshDrawableState();
  }

  public void toggle()
  {
    if (!this.mChecked);
    for (boolean bool = true; ; bool = false)
    {
      setChecked(bool);
      return;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.CheckedLinearLayout
 * JD-Core Version:    0.5.4
 */