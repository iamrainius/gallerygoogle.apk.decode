package com.android.camera.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.camera.ListPreference;

public abstract class InLineSettingItem extends LinearLayout
{
  protected int mIndex;
  private Listener mListener;
  protected String mOverrideValue;
  protected ListPreference mPreference;
  protected TextView mTitle;

  public InLineSettingItem(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  protected boolean changeIndex(int paramInt)
  {
    if ((paramInt >= this.mPreference.getEntryValues().length) || (paramInt < 0))
      return false;
    this.mIndex = paramInt;
    this.mPreference.setValueIndex(this.mIndex);
    if (this.mListener != null)
      this.mListener.onSettingChanged(this.mPreference);
    updateView();
    sendAccessibilityEvent(4);
    return true;
  }

  public void initialize(ListPreference paramListPreference)
  {
    setTitle(paramListPreference);
    if (paramListPreference == null)
      return;
    this.mPreference = paramListPreference;
    reloadPreference();
  }

  public void reloadPreference()
  {
    this.mIndex = this.mPreference.findIndexOfValue(this.mPreference.getValue());
    updateView();
  }

  public void setSettingChangedListener(Listener paramListener)
  {
    this.mListener = paramListener;
  }

  protected void setTitle(ListPreference paramListPreference)
  {
    this.mTitle = ((TextView)findViewById(2131558404));
    this.mTitle.setText(paramListPreference.getTitle());
  }

  protected abstract void updateView();

  public static abstract interface Listener
  {
    public abstract void onSettingChanged(ListPreference paramListPreference);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.InLineSettingItem
 * JD-Core Version:    0.5.4
 */