package com.google.android.apps.lightcycle.util;

import android.content.Context;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

public class ExpandShortenedUrlTask extends HttpRequestTask
{
  private static final String SHORTENER_API_URL;
  private static final String TAG = ExpandShortenedUrlTask.class.getSimpleName();
  private final Callback<String> callback;

  static
  {
    SHORTENER_API_URL = UrlShortener.getApiBaseUrl() + "&shortUrl=";
  }

  private ExpandShortenedUrlTask(Context paramContext, Callback<String> paramCallback)
  {
    super(paramContext, true);
    this.callback = paramCallback;
  }

  public static void expandAsync(Context paramContext, String paramString, Callback<String> paramCallback)
  {
    HttpGet localHttpGet = new HttpGet(SHORTENER_API_URL + paramString);
    new ExpandShortenedUrlTask(paramContext, paramCallback).execute(new HttpUriRequest[] { localHttpGet });
  }

  public void processUploadResponse(HttpResponse paramHttpResponse, String paramString)
  {
    try
    {
      JSONObject localJSONObject = new JSONObject(paramString);
      this.callback.onCallback(localJSONObject.getString("longUrl"));
      return;
    }
    catch (JSONException localJSONException)
    {
      Log.e(TAG, localJSONException.getMessage(), localJSONException);
      this.callback.onCallback(null);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.util.ExpandShortenedUrlTask
 * JD-Core Version:    0.5.4
 */