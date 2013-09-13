package com.android.camera.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.android.camera.ListPreference;
import java.util.List;

public class InLineSettingSwitch extends InLineSettingItem
{
  CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
  {
    public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
    {
      InLineSettingSwitch localInLineSettingSwitch = InLineSettingSwitch.this;
      if (paramBoolean);
      for (int i = 1; ; i = 0)
      {
        localInLineSettingSwitch.changeIndex(i);
        return;
      }
    }
  };
  private Switch mSwitch;

  public InLineSettingSwitch(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    paramAccessibilityEvent.getText().add(this.mPreference.getTitle());
    return true;
  }

  public void initialize(ListPreference paramListPreference)
  {
    super.initialize(paramListPreference);
    Switch localSwitch = this.mSwitch;
    Resources localResources = getContext().getResources();
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = this.mPreference.getTitle();
    localSwitch.setContentDescription(localResources.getString(2131362004, arrayOfObject));
  }

  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mSwitch = ((Switch)findViewById(2131558508));
    this.mSwitch.setOnCheckedChangeListener(this.mCheckedChangeListener);
  }

  public void setEnabled(boolean paramBoolean)
  {
    if (this.mTitle != null)
      this.mTitle.setEnabled(paramBoolean);
    if (this.mSwitch == null)
      return;
    this.mSwitch.setEnabled(paramBoolean);
  }

  protected void updateView()
  {
    int i = 1;
    this.mSwitch.setOnCheckedChangeListener(null);
    label37: int j;
    if (this.mOverrideValue == null)
    {
      Switch localSwitch2 = this.mSwitch;
      if (this.mIndex == i);
      while (true)
      {
        localSwitch2.setChecked(i);
        this.mSwitch.setOnCheckedChangeListener(this.mCheckedChangeListener);
        return;
        j = 0;
      }
    }
    int k = this.mPreference.findIndexOfValue(this.mOverrideValue);
    Switch localSwitch1 = this.mSwitch;
    if (k == j);
    while (true)
    {
      localSwitch1.setChecked(j);
      break label37:
      j = 0;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.InLineSettingSwitch
 * JD-Core Version:    0.5.4
 */