package com.google.android.apps.lightcycle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;
import com.android.gallery3d.util.LightCycleHelper;

public class ProtectedPanoramaCaptureActivity extends PanoramaCaptureActivity
{
  private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      ProtectedPanoramaCaptureActivity.this.finish();
    }
  };
  protected boolean mSecureCamera;

  public void onCreate(Bundle paramBundle)
  {
    LightCycleHelper.initNative();
    this.mSecureCamera = getIntent().getBooleanExtra("secure_camera", false);
    if (this.mSecureCamera)
    {
      getWindow().addFlags(524288);
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
      registerReceiver(this.mScreenOffReceiver, localIntentFilter);
    }
    super.onCreate(paramBundle);
  }

  protected void onDestroy()
  {
    if (this.mSecureCamera)
      unregisterReceiver(this.mScreenOffReceiver);
    super.onDestroy();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.ProtectedPanoramaCaptureActivity
 * JD-Core Version:    0.5.4
 */