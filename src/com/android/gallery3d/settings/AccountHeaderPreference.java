package com.android.gallery3d.settings;

import android.content.Context;
import android.preference.Preference;
import android.view.View;
import android.widget.TextView;
import com.android.gallery3d.common.Utils;

public class AccountHeaderPreference extends Preference
{
  private String mAccountName;

  public AccountHeaderPreference(Context paramContext)
  {
    super(paramContext, null, 16842894);
    setLayoutResource(2130968576);
  }

  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    ((TextView)paramView.findViewById(2131558404)).setText(this.mAccountName);
  }

  public void setAccountName(String paramString)
  {
    if (Utils.equals(this.mAccountName, paramString))
      return;
    this.mAccountName = paramString;
    notifyChanged();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.settings.AccountHeaderPreference
 * JD-Core Version:    0.5.4
 */