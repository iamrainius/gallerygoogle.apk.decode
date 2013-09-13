package com.google.android.picasasync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.support.v4.net.TrafficStatsCompat;
import android.util.Log;
import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.Utils;
import com.google.android.picasastore.FIFEUtil;
import com.google.android.picasastore.ImageProxyUtil;
import com.google.android.picasastore.MetricsUtils;

public final class PicasaApi
{
  private final String mBaseUrl;
  private final GDataClient mClient = new GDataClient();
  private final GDataClient.Operation mOperation = new GDataClient.Operation();

  public PicasaApi(ContentResolver paramContentResolver)
  {
    String str = Settings.Secure.getString(paramContentResolver, "picasa_gdata_base_url");
    if (str == null)
      str = "https://picasaweb.google.com/data/feed/api/";
    this.mBaseUrl = str;
  }

  public static String convertImageUrl(String paramString, int paramInt, boolean paramBoolean)
  {
    if (FIFEUtil.isFifeHostedUrl(paramString))
    {
      boolean bool = FIFEUtil.getImageUrlOptions(paramString).contains("I");
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append('s').append(paramInt);
      if (paramBoolean)
        localStringBuilder.append("-c");
      if (bool)
        localStringBuilder.append("-I");
      return FIFEUtil.setImageUrlOptions(localStringBuilder.toString(), paramString).toString();
    }
    if (paramBoolean)
      Log.w("PicasaAPI", "not a FIFE url, ignore the crop option");
    return ImageProxyUtil.setImageUrlSize(paramInt, paramString);
  }

  private static String encodeUsername(String paramString)
  {
    String str = paramString.toLowerCase();
    if ((str.contains("@gmail.")) || (str.contains("@googlemail.")))
      str = str.substring(0, str.indexOf('@'));
    return Uri.encode(str);
  }

  private int getAlbumPhotos(AlbumEntry paramAlbumEntry, EntryHandler paramEntryHandler, String paramString1, String paramString2, PhotoCollectorJson paramPhotoCollectorJson)
  {
    int i = MetricsUtils.begin("PicasaApi." + paramString2);
    TrafficStatsCompat.setThreadStatsTag(1);
    try
    {
      if (Log.isLoggable("PicasaAPI", 2))
      {
        String str = paramString2 + " for %s / %s, etag: %s";
        Object[] arrayOfObject = new Object[3];
        arrayOfObject[0] = Utils.maskDebugInfo(paramAlbumEntry.user);
        arrayOfObject[1] = Utils.maskDebugInfo(Long.valueOf(paramAlbumEntry.id));
        arrayOfObject[2] = paramAlbumEntry.photosEtag;
        Log.v("PicasaAPI", String.format(str, arrayOfObject));
      }
      GDataClient.Operation localOperation = this.mOperation;
      try
      {
        switch (localOperation.outStatus)
        {
        default:
          Log.e("PicasaAPI", paramString2 + " fail: " + localOperation.outStatus);
          Utils.closeSilently(localOperation.outBody);
          TrafficStatsCompat.clearThreadStatsTag();
          return 3;
        case 200:
          paramAlbumEntry.photosEtag = localOperation.inOutEtag;
          paramPhotoCollectorJson.parse(localOperation.outBody);
          Utils.closeSilently(localOperation.outBody);
          TrafficStatsCompat.clearThreadStatsTag();
          return 0;
        case 304:
          Utils.closeSilently(localOperation.outBody);
          TrafficStatsCompat.clearThreadStatsTag();
          return 1;
        case 401:
        case 403:
          Utils.closeSilently(localOperation.outBody);
          TrafficStatsCompat.clearThreadStatsTag();
          return 2;
        case 404:
        }
        Log.d("PicasaAPI", paramString2 + " fail: " + localOperation.outStatus);
        Utils.closeSilently(localOperation.outBody);
        TrafficStatsCompat.clearThreadStatsTag();
        return 5;
      }
      finally
      {
        Utils.closeSilently(localOperation.outBody);
      }
    }
    catch (Exception localException)
    {
      Utils.handleInterrruptedException(localException);
      if (this.mClient.isOperationAborted())
      {
        Log.i("PicasaAPI", paramString2 + " aborted");
        return 4;
      }
      Log.e("PicasaAPI", paramString2 + " fail", localException);
      return 3;
    }
    finally
    {
      TrafficStatsCompat.clearThreadStatsTag();
      MetricsUtils.end(i);
    }
  }

  public void abortCurrentOperation()
  {
    this.mClient.abortCurrentOperation();
  }

  public int getAlbumPhotos(AlbumEntry paramAlbumEntry, EntryHandler paramEntryHandler)
  {
    return getAlbumPhotos(paramAlbumEntry, paramEntryHandler, this.mBaseUrl + "user/" + encodeUsername(paramAlbumEntry.user) + "/albumid/" + paramAlbumEntry.id + "?max-results=1000&imgmax=d&thumbsize=640u&visibility=visible&v=4&alt=json" + "&fd=shapes" + "&kind=photo", "getAlbumPhotos", new PhotoCollectorJson(paramEntryHandler));
  }

  public int getAlbums(UserEntry paramUserEntry, EntryHandler paramEntryHandler)
  {
    int i = MetricsUtils.begin("PicasaApi.getAlbums");
    TrafficStatsCompat.setThreadStatsTag(1);
    try
    {
      StringBuilder localStringBuilder = new StringBuilder(this.mBaseUrl).append("user/").append(encodeUsername(paramUserEntry.account)).append("?max-results=1000&imgmax=d&thumbsize=640u&visibility=visible&v=4&alt=json").append("&fd=shapes").append("&kind=album");
      GDataClient.Operation localOperation = this.mOperation;
      localOperation.inOutEtag = paramUserEntry.albumsEtag;
      Object[] arrayOfObject3;
      if (Log.isLoggable("PicasaAPI", 2))
      {
        arrayOfObject3 = new Object[2];
        arrayOfObject3[0] = Utils.maskDebugInfo(paramUserEntry.account);
        arrayOfObject3[1] = paramUserEntry.albumsEtag;
      }
      try
      {
        switch (localOperation.outStatus)
        {
        default:
          Object[] arrayOfObject2 = new Object[2];
          arrayOfObject2[0] = Utils.maskDebugInfo(localStringBuilder.toString());
          arrayOfObject2[1] = Integer.valueOf(localOperation.outStatus);
          Log.e("PicasaAPI", String.format("    getAlbums fail - uri: %s, status code: %s", arrayOfObject2));
          Utils.closeSilently(localOperation.outBody);
          TrafficStatsCompat.clearThreadStatsTag();
          return 3;
        case 200:
          paramUserEntry.albumsEtag = localOperation.inOutEtag;
          new AlbumCollectorJson(paramEntryHandler).parse(localOperation.outBody);
          Utils.closeSilently(localOperation.outBody);
          TrafficStatsCompat.clearThreadStatsTag();
          return 0;
        case 304:
          Utils.closeSilently(localOperation.outBody);
          TrafficStatsCompat.clearThreadStatsTag();
          return 1;
        case 401:
        case 403:
        }
        Object[] arrayOfObject1 = new Object[2];
        arrayOfObject1[0] = Utils.maskDebugInfo(localStringBuilder.toString());
        arrayOfObject1[1] = Integer.valueOf(localOperation.outStatus);
        Log.e("PicasaAPI", String.format("    getAlbums fail - uri: %s, status code: %s", arrayOfObject1));
        Utils.closeSilently(localOperation.outBody);
        TrafficStatsCompat.clearThreadStatsTag();
        return 2;
      }
      finally
      {
        Utils.closeSilently(localOperation.outBody);
      }
    }
    catch (Throwable localThrowable)
    {
      Utils.handleInterrruptedException(localThrowable);
      if (this.mClient.isOperationAborted())
      {
        Log.i("PicasaAPI", "getAlbums aborted");
        return 4;
      }
      Log.e("PicasaAPI", "getAlbums fail", localThrowable);
      return 3;
    }
    finally
    {
      TrafficStatsCompat.clearThreadStatsTag();
      MetricsUtils.end(i);
    }
  }

  public int getUploadedPhotos(AlbumEntry paramAlbumEntry, EntryHandler paramEntryHandler)
  {
    String str = this.mBaseUrl + "user/" + encodeUsername(paramAlbumEntry.user) + "?max-results=1000&imgmax=d&thumbsize=640u&visibility=visible&v=4&alt=json" + "&kind=photo" + "&streamid=camera_sync_created";
    PhotoCollectorJson localPhotoCollectorJson = new PhotoCollectorJson(paramEntryHandler);
    int i = 1;
    int j = i;
    while (i <= j)
    {
      int k = getAlbumPhotos(paramAlbumEntry, paramEntryHandler, str + "&start-index=" + i, "getUploadedPhotos", localPhotoCollectorJson);
      if (k != 0)
        return k;
      if (localPhotoCollectorJson.entryCount <= 0)
      {
        Log.e("PicasaAPI", "getUploadedPhotos: server return zero entry");
        return 3;
      }
      i += localPhotoCollectorJson.entryCount;
      j = localPhotoCollectorJson.totalCount;
      if (i > j)
        continue;
      paramAlbumEntry.photosEtag = null;
      if (!Log.isLoggable("PicasaAPI", 2))
        continue;
      Log.v("PicasaAPI", "  progress=" + i + "/" + localPhotoCollectorJson.totalCount);
    }
    return 0;
  }

  public void setAuthToken(String paramString)
  {
    this.mClient.setAuthToken(paramString);
  }

  public static abstract interface EntryHandler
  {
    public abstract void handleEntry(ContentValues paramContentValues);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.PicasaApi
 * JD-Core Version:    0.5.4
 */