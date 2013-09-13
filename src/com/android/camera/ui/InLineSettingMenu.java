package com.android.camera.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import com.android.camera.ListPreference;
import java.util.List;

public class InLineSettingMenu extends InLineSettingItem
{
  private TextView mEntry;

  public InLineSettingMenu(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    paramAccessibilityEvent.getText().add(this.mPreference.getTitle() + this.mPreference.getEntry());
    return true;
  }

  public void initialize(ListPreference paramListPreference)
  {
    super.initialize(paramListPreference);
  }

  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mEntry = ((TextView)findViewById(2131558507));
  }

  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    if (this.mTitle != null)
      this.mTitle.setEnabled(paramBoolean);
    if (this.mEntry == null)
      return;
    this.mEntry.setEnabled(paramBoolean);
  }

  protected void updateView()
  {
    if (this.mOverrideValue == null)
    {
      this.mEntry.setText(this.mPreference.getEntry());
      return;
    }
    int i = this.mPreference.findIndexOfValue(this.mOverrideValue);
    if (i != -1)
    {
      this.mEntry.setText(this.mPreference.getEntries()[i]);
      return;
    }
    Log.e("InLineSettingMenu", "Fail to find override value=" + this.mOverrideValue);
    this.mPreference.print();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.InLineSettingMenu
 * JD-Core Version:    0.5.4
 */