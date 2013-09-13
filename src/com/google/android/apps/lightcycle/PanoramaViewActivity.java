package com.google.android.apps.lightcycle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.WindowManager;
import com.google.android.apps.lightcycle.sensor.SensorReader;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.Dialogs;
import com.google.android.apps.lightcycle.util.ExpandShortenedUrlTask;
import com.google.android.apps.lightcycle.util.FileUtil;
import com.google.android.apps.lightcycle.util.LG;
import com.google.android.apps.lightcycle.util.PanoMetadata;
import com.google.android.apps.lightcycle.util.UiUtil;
import com.google.android.apps.lightcycle.viewer.LegacyTileProvider;
import com.google.android.apps.lightcycle.viewer.PanoramaImage;
import com.google.android.apps.lightcycle.viewer.PanoramaView;
import com.google.android.apps.lightcycle.viewer.TileProvider;
import com.google.android.apps.lightcycle.viewer.TileProviderImpl;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;

public class PanoramaViewActivity extends Activity
{
  private static final String TAG = PanoramaViewActivity.class.getSimpleName();
  private PanoramaView mainView;
  private SensorReader sensorReader;
  private PowerManager.WakeLock wakeLock;

  private void fetchSharedPanoFile(String paramString, Callback<String> paramCallback)
  {
    String str = getMediumSizePanoUrlFromLandingPage(paramString);
    if (str == null)
    {
      showLoadingErrorAndExit();
      return;
    }
    new AsyncTask(str, new File(getExternalCacheDir(), "temp_pano.jpg"), paramCallback)
    {
      protected String doInBackground(Void[] paramArrayOfVoid)
      {
        try
        {
          FileUtil.storeFile(new DefaultHttpClient().execute(new HttpGet(this.val$mediumSizePanoUrl)).getEntity().getContent(), this.val$tempFile);
          String str = this.val$tempFile.getAbsolutePath();
          return str;
        }
        catch (ClientProtocolException localClientProtocolException)
        {
          Log.e(PanoramaViewActivity.TAG, localClientProtocolException.getMessage(), localClientProtocolException);
          return null;
        }
        catch (IOException localIOException)
        {
          Log.e(PanoramaViewActivity.TAG, localIOException.getMessage(), localIOException);
        }
      }

      protected void onPostExecute(String paramString)
      {
        this.val$callback.onCallback(paramString);
      }
    }
    .execute(new Void[0]);
  }

  private static String getMediumSizePanoUrlFromLandingPage(String paramString)
  {
    String str1;
    try
    {
      URI localURI = new URI(paramString);
      Iterator localIterator = URLEncodedUtils.parse(localURI, "UTF-8").iterator();
      NameValuePair localNameValuePair;
      do
      {
        boolean bool = localIterator.hasNext();
        str1 = null;
        if (!bool)
          break label70;
        localNameValuePair = (NameValuePair)localIterator.next();
      }
      while (!localNameValuePair.getName().equals("pano"));
      str1 = localNameValuePair.getValue();
      if (str1 == null)
        label70: return null;
    }
    catch (URISyntaxException localURISyntaxException)
    {
      Log.e(TAG, localURISyntaxException.getMessage(), localURISyntaxException);
      return null;
    }
    String str2 = str1.substring(str1.lastIndexOf('/'));
    String str3 = str1.replace(str2, "/s2048" + str2).replace("https://", "http://");
    Log.d(TAG, "Medium Size URL: " + str3);
    return str3;
  }

  private String getSendImage(Intent paramIntent)
  {
    Uri localUri = (Uri)paramIntent.getParcelableExtra("android.intent.extra.STREAM");
    if (localUri != null)
    {
      LG.d("Got filename from Send intent");
      return getPathFromURI(localUri);
    }
    return "";
  }

  private String getViewImage(Intent paramIntent)
  {
    Uri localUri = paramIntent.getData();
    if (localUri == null)
    {
      LG.d("URI is null!");
      return "";
    }
    LG.d("Got filename from View intent");
    return getPathFromURI(localUri);
  }

  private boolean isImageUsablePanorama(String paramString)
  {
    return PanoMetadata.parse(paramString) != null;
  }

  private void loadAndShowUrl(String paramString)
  {
    if (paramString == null)
    {
      showLoadingErrorAndExit();
      return;
    }
    ProgressDialog localProgressDialog = Dialogs.createProgressDialog(2131361884, this);
    localProgressDialog.show();
    fetchSharedPanoFile(paramString, new Callback(localProgressDialog)
    {
      public void onCallback(String paramString)
      {
        this.val$dialog.dismiss();
        PanoramaViewActivity.this.showFile(paramString);
      }
    });
  }

  private void onImageLoadingError(int paramInt)
  {
    Dialogs.showDialog(-1, paramInt, this, new Callback()
    {
      public void onCallback(Void paramVoid)
      {
        PanoramaViewActivity.this.finish();
      }
    });
  }

  private void showFile(String paramString)
  {
    Log.d(TAG, "Attempting to show panorama : " + paramString);
    PanoMetadata localPanoMetadata = PanoMetadata.parse(paramString);
    if (localPanoMetadata == null)
    {
      Dialogs.showDialog(2131361885, 2131361886, this, null);
      return;
    }
    File localFile = new File(paramString);
    if (!localFile.exists())
    {
      Log.e(TAG, "Could not load file: " + paramString);
      Dialogs.showDialog(2131361885, 2131361887, this, null);
      return;
    }
    int i;
    if (Build.VERSION.SDK_INT < 10)
    {
      i = 1;
      label110: if (i == 0)
        break label150;
    }
    for (Object localObject = new LegacyTileProvider(localFile); ; localObject = new TileProviderImpl(localFile))
    {
      this.mainView.setPanoramaImage(new PanoramaImage((TileProvider)localObject, localPanoMetadata));
      return;
      i = 0;
      label150: break label110:
    }
  }

  private void showLoadingErrorAndExit()
  {
    Dialogs.showDialog(-1, 2131361882, this, new Callback()
    {
      public void onCallback(Void paramVoid)
      {
        PanoramaViewActivity.this.finish();
      }
    });
  }

  private String writeStreamToTempFile(InputStream paramInputStream)
    throws IOException
  {
    File localFile = new File(getCacheDir(), "temp_pano.jpg");
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(localFile));
    byte[] arrayOfByte = new byte[4096];
    while (true)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (i <= 0)
        break;
      localBufferedOutputStream.write(arrayOfByte, 0, i);
    }
    localBufferedOutputStream.close();
    String str = localFile.getAbsolutePath();
    Log.d(TAG, "Wrote stream to temporary file: " + str);
    return str;
  }

  public String getPathFromURI(Uri paramUri)
  {
    Cursor localCursor = managedQuery(paramUri, new String[] { "_data" }, null, null, null);
    if (localCursor == null)
      return "";
    int i = localCursor.getColumnIndexOrThrow("_data");
    localCursor.moveToFirst();
    return localCursor.getString(i);
  }

  public void onNewIntent(Intent paramIntent)
  {
    setIntent(paramIntent);
  }

  public void onPause()
  {
    super.onPause();
    this.mainView.setAutoRotationCallback(null);
    synchronized (this.wakeLock)
    {
      if (this.wakeLock.isHeld())
        this.wakeLock.release();
      if (this.sensorReader != null)
        this.sensorReader.stop();
      return;
    }
  }

  public void onResume()
  {
    super.onResume();
    UiUtil.switchSystemUiToLightsOut(getWindow());
    this.mainView = new PanoramaView(this);
    this.sensorReader = new SensorReader();
    setContentView(this.mainView);
    this.mainView.setSensorReader(getWindowManager().getDefaultDisplay(), this.sensorReader);
    Intent localIntent = getIntent();
    Uri localUri = localIntent.getData();
    Object localObject = localIntent.getStringExtra("filename");
    if (localUri == null)
    {
      if (localObject != null)
        break label358;
      String str2 = localIntent.getAction();
      String str3 = localIntent.getType();
      LG.d("Intent : " + localIntent.toString());
      if ("android.intent.action.VIEW".equals(str2))
        if (str3 != null)
          if (str3.startsWith("image/"))
            localObject = getViewImage(localIntent);
      while (localObject == null)
      {
        onImageLoadingError(2131361882);
        return;
        String str4 = localIntent.getDataString();
        if (str4.startsWith("https://panoramas.googleplex.com/s/"))
        {
          ProgressDialog localProgressDialog = Dialogs.createProgressDialog(2131361881, this);
          localProgressDialog.show();
          String str5 = str4.substring("https://panoramas.googleplex.com/s/".length());
          ExpandShortenedUrlTask.expandAsync(this, "http://goo.gl/" + str5, new Callback(localProgressDialog)
          {
            public void onCallback(String paramString)
            {
              this.val$dialog.dismiss();
              PanoramaViewActivity.this.loadAndShowUrl(paramString);
            }
          });
          return;
        }
        if (!str4.startsWith("https://panoramas.googleplex.com"))
          continue;
        loadAndShowUrl(str4);
        return;
        if (("android.intent.action.SEND".equals(str2)) && (str3 != null))
        {
          if (!str3.startsWith("image/"))
            continue;
          localObject = getSendImage(localIntent);
        }
        localObject = localIntent.getExtras().getString("filename");
        LG.d("Got filename from intent extras.");
      }
      if (isImageUsablePanorama((String)localObject))
        break label358;
      onImageLoadingError(2131361883);
      return;
    }
    try
    {
      String str1 = writeStreamToTempFile(getContentResolver().openInputStream(localUri));
      localObject = str1;
      label358: this.sensorReader.start(this);
      this.wakeLock = ((PowerManager)getSystemService("power")).newWakeLock(536870938, TAG);
      this.wakeLock.acquire();
      this.mainView.setAutoRotationCallback(new Callback()
      {
        public void onCallback(Boolean paramBoolean)
        {
          synchronized (PanoramaViewActivity.this.wakeLock)
          {
            if (paramBoolean.booleanValue())
              if (!PanoramaViewActivity.this.wakeLock.isHeld())
                PanoramaViewActivity.this.wakeLock.acquire();
            do
              return;
            while (!PanoramaViewActivity.this.wakeLock.isHeld());
            PanoramaViewActivity.this.wakeLock.release();
          }
        }
      });
      showFile((String)localObject);
      return;
    }
    catch (IOException localIOException)
    {
      Log.e(TAG, "Could not open file. ", localIOException);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.PanoramaViewActivity
 * JD-Core Version:    0.5.4
 */