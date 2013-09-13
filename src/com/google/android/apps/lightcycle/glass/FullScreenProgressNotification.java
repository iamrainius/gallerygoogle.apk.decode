package com.google.android.apps.lightcycle.glass;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.apps.lightcycle.panorama.StitchingServiceManager;
import com.google.android.apps.lightcycle.panorama.StitchingServiceManager.ProgressUpdateCallback;
import com.google.android.apps.lightcycle.panorama.StitchingServiceManager.StitchingResultCallback;

public class FullScreenProgressNotification extends Activity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968600);
    StitchingServiceManager localStitchingServiceManager = StitchingServiceManager.getStitchingServiceManager(this);
    ((TextView)findViewById(2131558493)).setText("Stitching panorama ...");
    TextView localTextView = (TextView)findViewById(2131558494);
    localStitchingServiceManager.addStitchingResultCallback(new StitchingServiceManager.StitchingResultCallback()
    {
      public void onResult(String paramString, Uri paramUri)
      {
        FullScreenProgressNotification.this.finish();
      }
    });
    localStitchingServiceManager.setStitchingProgressCallback(new StitchingServiceManager.ProgressUpdateCallback(localTextView)
    {
      public void onProgress(String paramString, Uri paramUri, int paramInt)
      {
        FullScreenProgressNotification.this.runOnUiThread(new Runnable(paramInt)
        {
          public void run()
          {
            FullScreenProgressNotification.2.this.val$valueTextView.setText(this.val$progress + " %");
          }
        });
      }
    });
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.glass.FullScreenProgressNotification
 * JD-Core Version:    0.5.4
 */