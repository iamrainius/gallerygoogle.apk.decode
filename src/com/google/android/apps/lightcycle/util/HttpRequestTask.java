package com.google.android.apps.lightcycle.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpRequestTask extends AsyncTask<HttpUriRequest, Integer, HttpResponse>
{
  private static final String TAG = HttpRequestTask.class.getSimpleName();
  protected final Context context;
  private final boolean parseResponseAsString;
  private ProgressDialog progressDialog;
  private String responseContentString = null;

  public HttpRequestTask(Context paramContext, boolean paramBoolean)
  {
    this.context = paramContext;
    this.parseResponseAsString = paramBoolean;
  }

  private static String getStringResponse(HttpResponse paramHttpResponse)
  {
    StringBuilder localStringBuilder;
    try
    {
      InputStreamReader localInputStreamReader = new InputStreamReader(paramHttpResponse.getEntity().getContent());
      localStringBuilder = new StringBuilder();
      char[] arrayOfChar = new char[4096];
      int i = localInputStreamReader.read(arrayOfChar);
      if (i < 0)
        break label78;
      label78: localStringBuilder.append(arrayOfChar, 0, i);
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Log.e(TAG, localIllegalStateException.getMessage(), localIllegalStateException);
      return null;
      String str = localStringBuilder.toString();
      return str;
    }
    catch (IOException localIOException)
    {
      Log.e(TAG, localIOException.getMessage(), localIOException);
    }
    return null;
  }

  protected HttpResponse doInBackground(HttpUriRequest[] paramArrayOfHttpUriRequest)
  {
    DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
    try
    {
      HttpResponse localHttpResponse = localDefaultHttpClient.execute(paramArrayOfHttpUriRequest[0]);
      if (this.parseResponseAsString)
        this.responseContentString = getStringResponse(localHttpResponse);
      return localHttpResponse;
    }
    catch (ClientProtocolException localClientProtocolException)
    {
      Log.e(TAG, localClientProtocolException.getMessage(), localClientProtocolException);
      return null;
    }
    catch (IOException localIOException)
    {
      Log.e(TAG, localIOException.getMessage(), localIOException);
    }
  }

  protected void onPostExecute(HttpResponse paramHttpResponse)
  {
    Log.d(TAG, "HTTP request done.");
    if (this.progressDialog != null)
      this.progressDialog.dismiss();
    processUploadResponse(paramHttpResponse, this.responseContentString);
  }

  protected void onPreExecute()
  {
    super.onPreExecute();
    Log.d(TAG, "HTTP request in progess...");
  }

  public void processUploadResponse(HttpResponse paramHttpResponse, String paramString)
  {
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.util.HttpRequestTask
 * JD-Core Version:    0.5.4
 */