package com.google.android.apps.lightcycle.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.Window;
import com.google.android.apps.lightcycle.panorama.DeviceManager;
import com.google.android.apps.lightcycle.util.AnalyticsHelper;
import com.google.android.apps.lightcycle.util.AnalyticsHelper.Page;
import com.google.android.apps.lightcycle.util.UiUtil;

public class GalleryActivity extends FragmentActivity
{
  private static final String TAG = GalleryActivity.class.getSimpleName();

  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    switch (paramInt1)
    {
    default:
      return;
    case 0:
    }
    GalleryFragment localGalleryFragment = (GalleryFragment)getSupportFragmentManager().findFragmentById(2131558495);
    if (paramInt2 == -1);
    for (boolean bool = true; ; bool = false)
    {
      localGalleryFragment.onAuthenticationActivityResult(bool);
      return;
    }
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (DeviceManager.isWingman())
    {
      requestWindowFeature(1);
      getWindow().setFlags(1024, 1024);
    }
    UiUtil.setDisplayHomeAsUpEnabled(this, true);
    setContentView(2130968601);
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

  protected void onResume()
  {
    super.onResume();
    AnalyticsHelper.getInstance(this).trackPage(AnalyticsHelper.Page.GALLERY);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.gallery.GalleryActivity
 * JD-Core Version:    0.5.4
 */