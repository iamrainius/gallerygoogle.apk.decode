package com.google.android.apps.lightcycle.gallery;

import android.content.Context;
import android.util.Log;
import com.google.android.apps.lightcycle.gallery.data.PhotoUrls;
import com.google.android.apps.lightcycle.gallery.data.PicasaRequestContext;
import com.google.android.apps.lightcycle.gallery.request.GetAlbumUrlTask;
import com.google.android.apps.lightcycle.gallery.request.PhotoUploadTask;
import com.google.android.apps.lightcycle.gallery.request.SetDescriptionTask;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.ProgressCallback;
import com.google.android.apps.lightcycle.util.ResourceUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;

public class UploadPhotoUtil
{
  private static final String TAG = UploadPhotoUtil.class.getSimpleName();

  public static void createAlbum(Callback<Boolean> paramCallback, PicasaRequestContext paramPicasaRequestContext)
  {
    String str1 = "https://picasaweb.google.com/data/feed/api/user/{UID}".replace("{UID}", paramPicasaRequestContext.accountName);
    String str2 = getAlbumCreateXml("My Panoramas", System.currentTimeMillis(), "panorama", paramPicasaRequestContext.androidContext);
    try
    {
      StringEntity localStringEntity = new StringEntity(str2);
      HttpPost localHttpPost = new HttpPost(str1);
      localHttpPost.setHeader("Content-Type", "application/atom+xml");
      localHttpPost.addHeader("Authorization", "GoogleLogin auth=" + paramPicasaRequestContext.authToken);
      localHttpPost.setEntity(localStringEntity);
      new PhotoUploadTask(new Callback(paramCallback)
      {
        public void onCallback(PhotoUrls paramPhotoUrls)
        {
          Callback localCallback = this.val$callback;
          if (paramPhotoUrls != null);
          for (boolean bool = true; ; bool = false)
          {
            localCallback.onCallback(Boolean.valueOf(bool));
            return;
          }
        }
      }
      , paramPicasaRequestContext.androidContext).execute(new HttpUriRequest[] { localHttpPost });
      return;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      Log.e(TAG, localUnsupportedEncodingException.getMessage(), localUnsupportedEncodingException);
    }
  }

  private static void createAlbumAndUpload(String paramString, HttpEntity paramHttpEntity, PicasaRequestContext paramPicasaRequestContext, ProgressCallback<PhotoUrls> paramProgressCallback)
  {
    paramProgressCallback.onNewProgressMessage(paramPicasaRequestContext.androidContext.getString(2131361844));
    createAlbum(new Callback(paramProgressCallback, paramPicasaRequestContext, paramString, paramHttpEntity)
    {
      public void onCallback(Boolean paramBoolean)
      {
        if (!paramBoolean.booleanValue())
          return;
        this.val$callback.onNewProgressMessage(this.val$context.androidContext.getString(2131361845));
        UploadPhotoUtil.access$300(this.val$context, new Callback()
        {
          public void onCallback(String paramString)
          {
            if (paramString == null)
            {
              Log.e(UploadPhotoUtil.TAG, "Even though album got created, we cannot get the URL.");
              return;
            }
            UploadPhotoUtil.access$100(paramString, UploadPhotoUtil.2.this.val$fileName, UploadPhotoUtil.2.this.val$entity, UploadPhotoUtil.2.this.val$context, UploadPhotoUtil.2.this.val$callback);
          }
        });
      }
    }
    , paramPicasaRequestContext);
  }

  private static String getAlbumCreateXml(String paramString1, long paramLong, String paramString2, Context paramContext)
  {
    try
    {
      String str = ResourceUtil.getRawResourceAsString(paramContext, 2131230726).replace("{ALBUM_NAME}", paramString1).replace("{TIMESTAMP}", String.valueOf(paramLong)).replace("{KEYWORDS}", paramString2);
      return str;
    }
    catch (IOException localIOException)
    {
      Log.e(TAG, localIOException.getMessage(), localIOException);
    }
    return null;
  }

  private static void getLightcycleAlbumUrl(PicasaRequestContext paramPicasaRequestContext, Callback<String> paramCallback)
  {
    HttpGet localHttpGet = new HttpGet("https://picasaweb.google.com/data/feed/api/user/{UID}".replace("{UID}", paramPicasaRequestContext.accountName));
    localHttpGet.setHeader("Authorization", "GoogleLogin auth=" + paramPicasaRequestContext.authToken);
    new GetAlbumUrlTask("My Panoramas", paramCallback, paramPicasaRequestContext.androidContext).execute(new HttpUriRequest[] { localHttpGet });
  }

  public static void uploadPhoto(String paramString, HttpEntity paramHttpEntity, PicasaRequestContext paramPicasaRequestContext, ProgressCallback<PhotoUrls> paramProgressCallback)
  {
    paramProgressCallback.onNewProgressMessage(paramPicasaRequestContext.androidContext.getString(2131361845));
    getLightcycleAlbumUrl(paramPicasaRequestContext, new Callback(paramString, paramHttpEntity, paramPicasaRequestContext, paramProgressCallback)
    {
      public void onCallback(String paramString)
      {
        if (paramString == null)
        {
          UploadPhotoUtil.access$000(this.val$fileName, this.val$entity, this.val$context, this.val$callback);
          return;
        }
        UploadPhotoUtil.access$100(paramString, this.val$fileName, this.val$entity, this.val$context, this.val$callback);
      }
    });
  }

  private static void uploadPhotoToAlbum(String paramString1, String paramString2, HttpEntity paramHttpEntity, PicasaRequestContext paramPicasaRequestContext, ProgressCallback<PhotoUrls> paramProgressCallback)
  {
    Log.d(TAG, "Picasa URL: " + paramString1);
    HttpPost localHttpPost = new HttpPost(paramString1);
    localHttpPost.setHeader("Content-Type", "image/jpeg");
    localHttpPost.addHeader("Slug", paramString2);
    localHttpPost.addHeader("Authorization", "GoogleLogin auth=" + paramPicasaRequestContext.authToken);
    localHttpPost.setEntity(paramHttpEntity);
    paramProgressCallback.onNewProgressMessage(paramPicasaRequestContext.androidContext.getString(2131361846));
    new PhotoUploadTask(new Callback(paramPicasaRequestContext, paramProgressCallback)
    {
      public void onCallback(PhotoUrls paramPhotoUrls)
      {
        SetDescriptionTask.setDescriptionAsync(this.val$context, paramPhotoUrls);
        this.val$callback.onDone(paramPhotoUrls);
      }
    }
    , paramPicasaRequestContext.androidContext).execute(new HttpUriRequest[] { localHttpPost });
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.gallery.UploadPhotoUtil
 * JD-Core Version:    0.5.4
 */