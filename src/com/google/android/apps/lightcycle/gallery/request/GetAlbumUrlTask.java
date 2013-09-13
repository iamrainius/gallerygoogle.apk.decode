package com.google.android.apps.lightcycle.gallery.request;

import android.content.Context;
import android.util.Log;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.HttpRequestTask;
import org.apache.http.HttpResponse;

public class GetAlbumUrlTask extends HttpRequestTask
{
  private static final String TAG = GetAlbumUrlTask.class.getSimpleName();
  private final String albumName;
  private final Callback<String> callback;

  public GetAlbumUrlTask(String paramString, Callback<String> paramCallback, Context paramContext)
  {
    super(paramContext, true);
    this.albumName = paramString;
    this.callback = paramCallback;
  }

  public void processUploadResponse(HttpResponse paramHttpResponse, String paramString)
  {
    int i = paramString.indexOf(this.albumName);
    if (i == -1)
    {
      Log.w(TAG, "Lightcycle album not found.");
      this.callback.onCallback(null);
      return;
    }
    int j = paramString.lastIndexOf("<id>", i) + "<id>".length();
    if (j == -1)
    {
      Log.e(TAG, "Lightcycle album found but no <id> element for it.");
      this.callback.onCallback(null);
      return;
    }
    String str = paramString.substring(j, paramString.indexOf("</id>", j)).replace("data/entry", "data/feed");
    Log.i(TAG, "Found Lightcycle album URL: " + str);
    this.callback.onCallback(str);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.gallery.request.GetAlbumUrlTask
 * JD-Core Version:    0.5.4
 */