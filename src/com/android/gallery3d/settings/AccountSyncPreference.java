package com.android.gallery3d.settings;

import android.content.Context;
import android.preference.Preference;
import android.view.View;
import android.widget.TextView;

public class AccountSyncPreference extends Preference
{
  private boolean mSyncEnabled = false;

  public AccountSyncPreference(Context paramContext)
  {
    super(paramContext);
    setLayoutResource(2130968577);
  }

  public void onBindView(View paramView)
  {
    super.onBindView(paramView);
    TextView localTextView1 = (TextView)paramView.findViewById(2131558404);
    TextView localTextView2 = (TextView)paramView.findViewById(2131558405);
    if (this.mSyncEnabled);
    for (int i = 2131362071; ; i = 2131362072)
    {
      localTextView1.setText(i);
      localTextView2.setText(2131362073);
      return;
    }
  }

  public void setSyncEnabled(boolean paramBoolean)
  {
    if (paramBoolean == this.mSyncEnabled)
      return;
    this.mSyncEnabled = paramBoolean;
    notifyChanged();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.settings.AccountSyncPreference
 * JD-Core Version:    0.5.4
 */