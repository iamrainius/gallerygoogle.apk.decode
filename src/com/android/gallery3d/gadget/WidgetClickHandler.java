package com.android.gallery3d.gadget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.android.gallery3d.app.Gallery;

public class WidgetClickHandler extends Activity
{
  private boolean isValidDataUri(Uri paramUri)
  {
    if (paramUri == null)
      return false;
    try
    {
      getContentResolver().openAssetFileDescriptor(paramUri, "r").close();
      return true;
    }
    catch (Throwable localThrowable)
    {
      Log.w("PhotoAppWidgetClickHandler", "cannot open uri: " + paramUri, localThrowable);
    }
    return false;
  }

  @TargetApi(11)
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    int i;
    label15: Intent localIntent1;
    if (Build.VERSION.SDK_INT >= 16)
    {
      i = 1;
      Uri localUri = getIntent().getData();
      if (!isValidDataUri(localUri))
        break label88;
      localIntent1 = new Intent("android.intent.action.VIEW", localUri);
      if (i == 0)
        break label113;
      localIntent1.putExtra("treat-back-as-up", true);
    }
    label88: label113: for (Intent localIntent2 = localIntent1; ; localIntent2 = localIntent1)
      while (true)
      {
        if (i != 0)
          localIntent2.setFlags(268484608);
        startActivity(localIntent2);
        finish();
        return;
        i = 0;
        break label15:
        Toast.makeText(this, 2131362228, 1).show();
        localIntent2 = new Intent(this, Gallery.class);
      }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.gadget.WidgetClickHandler
 * JD-Core Version:    0.5.4
 */