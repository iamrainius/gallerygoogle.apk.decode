package com.android.camera.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class AbstractSettingPopup extends RotateLayout
{
  protected ViewGroup mSettingList;
  protected TextView mTitle;

  public AbstractSettingPopup(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mTitle = ((TextView)findViewById(2131558404));
    this.mSettingList = ((ViewGroup)findViewById(2131558509));
  }

  public abstract void reloadPreference();
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.AbstractSettingPopup
 * JD-Core Version:    0.5.4
 */