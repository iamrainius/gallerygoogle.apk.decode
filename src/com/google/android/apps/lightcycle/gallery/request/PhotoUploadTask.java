package com.google.android.apps.lightcycle.gallery.request;

import android.content.Context;
import android.util.Log;
import com.google.android.apps.lightcycle.gallery.data.PhotoUrls;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.Dialogs;
import com.google.android.apps.lightcycle.util.HttpRequestTask;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

public class PhotoUploadTask extends HttpRequestTask
{
  private static final String TAG = PhotoUploadTask.class.getSimpleName();
  private final Callback<PhotoUrls> successCallback;

  public PhotoUploadTask(Callback<PhotoUrls> paramCallback, Context paramContext)
  {
    super(paramContext, true);
    this.successCallback = paramCallback;
  }

  public void processUploadResponse(HttpResponse paramHttpResponse, String paramString)
  {
    String str;
    if (paramHttpResponse == null)
      str = "I/O Error: Could not upload photo";
    while (true)
    {
      if (str != null)
        Log.e(TAG, str);
      if (str != null)
        this.successCallback.onCallback(null);
      this.successCallback.onCallback(PhotoUrls.parseFromXml(paramString));
      return;
      int i = paramHttpResponse.getStatusLine().getStatusCode();
      if ((i != 401) && (i != 403))
      {
        str = null;
        if (i != 405)
          break label120;
      }
      str = "Authentication error returned by the Picasa Server: " + i;
      Dialogs.showDialog(2131361838, str, this.context, null);
      label120: if (i == 201)
        continue;
      str = "An error response received from the Picasa Server: " + i;
      paramHttpResponse.getStatusLine();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.gallery.request.PhotoUploadTask
 * JD-Core Version:    0.5.4
 */