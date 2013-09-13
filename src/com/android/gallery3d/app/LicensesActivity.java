package com.android.gallery3d.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

public class LicensesActivity extends Activity
{
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968581);
    getWindow().setLayout(-1, -1);
    ((WebView)findViewById(2131558408)).loadUrl("file:///android_asset/licenses.html");
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.LicensesActivity
 * JD-Core Version:    0.5.4
 */