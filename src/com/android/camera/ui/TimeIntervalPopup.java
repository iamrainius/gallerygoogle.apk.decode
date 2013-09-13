package com.android.camera.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.NumberPicker;
import android.widget.TextView;
import com.android.camera.IconListPreference;
import com.android.camera.ListPreference;

public class TimeIntervalPopup extends AbstractSettingPopup
{
  private Button mConfirmButton;
  private final String[] mDurations;
  private TextView mHelpText;
  private Listener mListener;
  private NumberPicker mNumberSpinner;
  private IconListPreference mPreference;
  private Switch mTimeLapseSwitch;
  private View mTimePicker;
  private NumberPicker mUnitSpinner;
  private final String[] mUnits;

  public TimeIntervalPopup(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    Resources localResources = paramContext.getResources();
    this.mUnits = localResources.getStringArray(2131427335);
    this.mDurations = localResources.getStringArray(2131427334);
  }

  private void restoreSetting()
  {
    int i = this.mPreference.findIndexOfValue(this.mPreference.getValue());
    if (i == -1)
    {
      Log.e("TimeIntervalPopup", "Invalid preference value.");
      this.mPreference.print();
      throw new IllegalArgumentException();
    }
    if (i == 0)
    {
      this.mTimeLapseSwitch.setChecked(false);
      setTimeSelectionEnabled(false);
      return;
    }
    this.mTimeLapseSwitch.setChecked(true);
    setTimeSelectionEnabled(true);
    int j = 1 + this.mNumberSpinner.getMaxValue();
    int k = (i - 1) / j;
    int l = (i - 1) % j;
    this.mUnitSpinner.setValue(k);
    this.mNumberSpinner.setValue(l);
  }

  private void updateInputState()
  {
    if (this.mTimeLapseSwitch.isChecked())
    {
      int i = 1 + (this.mUnitSpinner.getValue() * (1 + this.mNumberSpinner.getMaxValue()) + this.mNumberSpinner.getValue());
      this.mPreference.setValueIndex(i);
    }
    while (true)
    {
      if (this.mListener != null)
        this.mListener.onListPrefChanged(this.mPreference);
      return;
      this.mPreference.setValueIndex(0);
    }
  }

  public void initialize(IconListPreference paramIconListPreference)
  {
    this.mPreference = paramIconListPreference;
    this.mTitle.setText(this.mPreference.getTitle());
    int i = this.mDurations.length;
    this.mNumberSpinner = ((NumberPicker)findViewById(2131558614));
    this.mNumberSpinner.setMinValue(0);
    this.mNumberSpinner.setMaxValue(i - 1);
    this.mNumberSpinner.setDisplayedValues(this.mDurations);
    this.mNumberSpinner.setWrapSelectorWheel(false);
    this.mUnitSpinner = ((NumberPicker)findViewById(2131558615));
    this.mUnitSpinner.setMinValue(0);
    this.mUnitSpinner.setMaxValue(-1 + this.mUnits.length);
    this.mUnitSpinner.setDisplayedValues(this.mUnits);
    this.mUnitSpinner.setWrapSelectorWheel(false);
    this.mTimePicker = findViewById(2131558612);
    this.mTimeLapseSwitch = ((Switch)findViewById(2131558616));
    this.mHelpText = ((TextView)findViewById(2131558617));
    this.mConfirmButton = ((Button)findViewById(2131558618));
    this.mNumberSpinner.setDescendantFocusability(393216);
    this.mUnitSpinner.setDescendantFocusability(393216);
    this.mTimeLapseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    {
      public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
      {
        TimeIntervalPopup.this.setTimeSelectionEnabled(paramBoolean);
      }
    });
    this.mConfirmButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        TimeIntervalPopup.this.updateInputState();
      }
    });
  }

  public void reloadPreference()
  {
  }

  public void setSettingChangedListener(Listener paramListener)
  {
    this.mListener = paramListener;
  }

  protected void setTimeSelectionEnabled(boolean paramBoolean)
  {
    TextView localTextView = this.mHelpText;
    int i;
    label12: View localView;
    int j;
    if (paramBoolean)
    {
      i = 8;
      localTextView.setVisibility(i);
      localView = this.mTimePicker;
      j = 0;
      if (!paramBoolean)
        break label43;
    }
    while (true)
    {
      localView.setVisibility(j);
      return;
      i = 0;
      break label12:
      label43: j = 8;
    }
  }

  public void setVisibility(int paramInt)
  {
    if ((paramInt == 0) && (getVisibility() != 0))
      restoreSetting();
    super.setVisibility(paramInt);
  }

  public static abstract interface Listener
  {
    public abstract void onListPrefChanged(ListPreference paramListPreference);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.TimeIntervalPopup
 * JD-Core Version:    0.5.4
 */