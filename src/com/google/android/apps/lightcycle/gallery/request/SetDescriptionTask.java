package com.google.android.apps.lightcycle.gallery.request;

import android.util.Log;
import com.google.android.apps.lightcycle.gallery.data.PhotoUrls;
import com.google.android.apps.lightcycle.gallery.data.PicasaRequestContext;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.HttpRequestTask;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;

public class SetDescriptionTask extends HttpRequestTask
{
  private static final String TAG = SetDescriptionTask.class.getSimpleName();
  private final PicasaRequestContext picasaRequestContext;

  private SetDescriptionTask(PicasaRequestContext paramPicasaRequestContext)
  {
    super(paramPicasaRequestContext.androidContext, true);
    this.picasaRequestContext = paramPicasaRequestContext;
  }

  public static void setDescriptionAsync(PicasaRequestContext paramPicasaRequestContext, PhotoUrls paramPhotoUrls)
  {
    paramPhotoUrls.getShortDogfoodUrl(new Callback(paramPhotoUrls, paramPicasaRequestContext)
    {
      public void onCallback(String paramString)
      {
        String str = "<entry xmlns='http://www.w3.org/2005/Atom'><title>panorama.jpg</title><summary>" + paramString + "</summary>" + " <category scheme=\"http://schemas.google.com/g/2005#kind\"" + " term=\"http://schemas.google.com/photos/2007#photo\"/></entry>";
        HttpPut localHttpPut = new HttpPut(this.val$photoUrls.editUrl);
        try
        {
          localHttpPut.setEntity(new StringEntity(str));
          localHttpPut.addHeader("Content-Type", "application/atom+xml");
          localHttpPut.addHeader("Authorization", "GoogleLogin auth=" + this.val$context.authToken);
          new SetDescriptionTask(this.val$context, null).execute(new HttpUriRequest[] { localHttpPut });
          Log.d(SetDescriptionTask.TAG, "Setting summary ...");
          return;
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
          Log.e(SetDescriptionTask.TAG, localUnsupportedEncodingException.getMessage(), localUnsupportedEncodingException);
        }
      }
    });
  }

  public void processUploadResponse(HttpResponse paramHttpResponse, String paramString)
  {
    Log.d(TAG, "Response Status: " + paramHttpResponse.getStatusLine().toString());
    if (paramHttpResponse.getStatusLine().getStatusCode() != 409)
      return;
    Log.d(TAG, "Retrying to set the description due to conflict");
    PhotoUrls localPhotoUrls = PhotoUrls.parseFromXml(paramString);
    setDescriptionAsync(this.picasaRequestContext, localPhotoUrls);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.gallery.request.SetDescriptionTask
 * JD-Core Version:    0.5.4
 */