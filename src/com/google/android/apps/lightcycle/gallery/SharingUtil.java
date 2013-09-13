package com.google.android.apps.lightcycle.gallery;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.apps.lightcycle.gallery.data.PhotoUrls;
import com.google.android.apps.lightcycle.panorama.DeviceManager;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.HttpRequestTask;
import com.google.android.apps.lightcycle.util.ProgressCallback;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

public class SharingUtil
{
  private static final String TAG = SharingUtil.class.getSimpleName();

  private static String getMetadataUrl(String paramString)
  {
    int i = paramString.lastIndexOf('/');
    StringBuilder localStringBuilder = new StringBuilder(paramString.substring(0, i));
    localStringBuilder.append("/g");
    localStringBuilder.append(paramString.substring(i, paramString.length()));
    return localStringBuilder.toString();
  }

  private static void shareForGlass(String paramString, Context paramContext)
  {
    String str = "http://panoviewer.haeberling-testing.appspot.com/latest?action=add&url=" + paramString;
    Log.d(TAG, "Sharing URL: " + paramString);
    new HttpRequestTask(paramContext, false).execute(new HttpUriRequest[] { new HttpGet(str) });
  }

  public static void sharePano(PhotoUrls paramPhotoUrls, Context paramContext, ProgressCallback<Void> paramProgressCallback)
  {
    triggerTiling(paramPhotoUrls.baseUrl, paramContext);
    if (DeviceManager.isWingman())
    {
      shareForGlass(paramPhotoUrls.baseUrl, paramContext);
      paramProgressCallback.onDone(null);
      return;
    }
    paramProgressCallback.onNewProgressMessage(paramContext.getString(2131361848));
    paramPhotoUrls.getShortDogfoodUrl(new Callback(paramProgressCallback, paramContext)
    {
      public void onCallback(String paramString)
      {
        this.val$progressCallback.onDone(null);
        SharingUtil.access$000(paramString, this.val$context);
      }
    });
  }

  private static void shareUrl(String paramString, Context paramContext)
  {
    Intent localIntent = new Intent("android.intent.action.SEND");
    localIntent.setType("text/plain");
    localIntent.putExtra("android.intent.extra.TEXT", paramString);
    paramContext.startActivity(Intent.createChooser(localIntent, paramContext.getText(2131361841)));
  }

  private static void triggerTiling(String paramString, Context paramContext)
  {
    HttpGet localHttpGet = new HttpGet(getMetadataUrl(paramString));
    new HttpRequestTask(paramContext, false).execute(new HttpUriRequest[] { localHttpGet });
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.gallery.SharingUtil
 * JD-Core Version:    0.5.4
 */