package com.android.gallery3d.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public final class UsbDeviceActivity extends Activity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Intent localIntent = new Intent(this, Gallery.class);
    localIntent.addFlags(335544320);
    try
    {
      startActivity(localIntent);
      finish();
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("UsbDeviceActivity", "unable to start Gallery activity", localActivityNotFoundException);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.UsbDeviceActivity
 * JD-Core Version:    0.5.4
 */