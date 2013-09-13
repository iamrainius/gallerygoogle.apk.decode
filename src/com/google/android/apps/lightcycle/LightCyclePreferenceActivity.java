package com.google.android.apps.lightcycle;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import com.google.android.apps.lightcycle.util.UiUtil;

public class LightCyclePreferenceActivity extends PreferenceActivity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    UiUtil.setDisplayHomeAsUpEnabled(this, true);
    addPreferencesFromResource(2131165187);
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return super.onOptionsItemSelected(paramMenuItem);
    case 16908332:
    }
    finish();
    return true;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.LightCyclePreferenceActivity
 * JD-Core Version:    0.5.4
 */