package com.google.android.apps.lightcycle.util;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONException;
import org.json.JSONObject;

public class UrlShortener
{
  private static final String TAG = UrlShortener.class.getSimpleName();

  private static String createShortUrl(String paramString)
  {
    BufferedReader localBufferedReader;
    StringBuffer localStringBuffer;
    try
    {
      URLConnection localURLConnection = new URL(getApiBaseUrl()).openConnection();
      localURLConnection.setDoInput(true);
      localURLConnection.setDoOutput(true);
      localURLConnection.setUseCaches(false);
      localURLConnection.setRequestProperty("Content-Type", "application/json");
      DataOutputStream localDataOutputStream = new DataOutputStream(localURLConnection.getOutputStream());
      localDataOutputStream.writeBytes("{\"longUrl\": \"" + paramString + "\"}");
      localDataOutputStream.flush();
      localDataOutputStream.close();
      localBufferedReader = new BufferedReader(new InputStreamReader(localURLConnection.getInputStream()));
      localStringBuffer = new StringBuffer();
      String str1 = localBufferedReader.readLine();
      if (str1 == null)
        break label161;
      label159: label161: localStringBuffer.append(str1);
    }
    catch (IOException localIOException)
    {
      Log.d(TAG, "Could not get shortened URL", localIOException);
      return null;
      localBufferedReader.close();
      Log.d(TAG, "Response from url shortener service is: " + localStringBuffer.toString());
      String str2 = new JSONObject(localStringBuffer.toString()).getString("id").trim();
      return str2;
    }
    catch (JSONException localJSONException)
    {
      Log.d(TAG, "Could not parse goo.gl response", localJSONException);
      break label159:
    }
  }

  public static void createShortUrlAsync(String paramString, Callback<String> paramCallback)
  {
    new AsyncTask(paramCallback)
    {
      protected String doInBackground(String[] paramArrayOfString)
      {
        return UrlShortener.access$000(paramArrayOfString[0]);
      }

      protected void onPostExecute(String paramString)
      {
        this.val$callback.onCallback(paramString);
      }
    }
    .execute(new String[] { paramString });
  }

  public static String getApiBaseUrl()
  {
    return "https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyAeGRfhdlINliJODCqF7rs-CUyofCVfkk0";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.util.UrlShortener
 * JD-Core Version:    0.5.4
 */