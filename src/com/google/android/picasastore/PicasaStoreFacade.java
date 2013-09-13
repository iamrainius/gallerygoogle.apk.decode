package com.google.android.picasastore;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import com.android.gallery3d.common.Utils;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class PicasaStoreFacade
{
  private static File sCacheDir;
  private static PicasaStoreFacade sInstance;
  private static Class<?> sNetworkReceiver;
  private static boolean sPrefetchVersionChecked;
  private Uri mAlbumCoversUri;
  private String mAuthority;
  private Uri mCachedFingerprintUri;
  private final Context mContext;
  private Uri mFingerprintUri;
  private PicasaStoreInfo mLocalInfo;
  private PicasaStoreInfo mMasterInfo;
  private Uri mPhotosUri;
  private Uri mRecalculateFingerprintUri;

  private PicasaStoreFacade(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
    updatePicasaSyncInfo(true);
  }

  public static void broadcastOperationReport(String paramString, long paramLong1, long paramLong2, int paramInt, long paramLong3, long paramLong4)
  {
    if ((sInstance == null) || (sNetworkReceiver == null))
      return;
    Context localContext = sInstance.mContext;
    Intent localIntent = new Intent(localContext, sNetworkReceiver);
    localIntent.setAction("com.google.android.picasastore.op_report");
    localIntent.putExtra("op_name", paramString);
    localIntent.putExtra("total_time", paramLong1);
    localIntent.putExtra("net_duration", paramLong2);
    localIntent.putExtra("transaction_count", paramInt);
    localIntent.putExtra("sent_bytes", paramLong3);
    localIntent.putExtra("received_bytes", paramLong4);
    localContext.sendBroadcast(localIntent);
  }

  public static void checkPrefetchVersion()
  {
    monitorenter;
    while (true)
    {
      int j;
      try
      {
        boolean bool = sPrefetchVersionChecked;
        if (bool);
        File localFile1;
        do
        {
          return;
          localFile1 = getCacheDirectory();
        }
        while (localFile1 == null);
        VersionInfo localVersionInfo = new VersionInfo(localFile1 + "/" + "prefetch_version.info");
        if (localVersionInfo.getVersion("picasa-prefetch-cache-version") != 1)
        {
          File[] arrayOfFile1 = localFile1.listFiles();
          int i = arrayOfFile1.length;
          j = 0;
          if (j < i)
          {
            File localFile2 = arrayOfFile1[j];
            if (!localFile2.isDirectory())
              break label178;
            if (!localFile2.getName().startsWith("picasa--"))
              break label178;
            File[] arrayOfFile2 = localFile2.listFiles();
            int k = arrayOfFile2.length;
            for (int l = 0; ; ++l)
            {
              if (l >= k)
                break label178;
              arrayOfFile2[l].delete();
            }
          }
          localVersionInfo.setVersion("picasa-prefetch-cache-version", 1);
          localVersionInfo.sync();
        }
      }
      finally
      {
        monitorexit;
      }
      label178: ++j;
    }
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
      Log.w("PicasaStore", "not a FIFE url, ignore the crop option");
    return ImageProxyUtil.setImageUrlSize(paramInt, paramString);
  }

  public static File createCacheFile(long paramLong, String paramString)
  {
    File localFile1 = getCacheDirectory();
    File localFile3;
    if (localFile1 == null)
    {
      localFile3 = null;
      return localFile3;
    }
    String str1 = paramLong + paramString;
    int i = (int)(paramLong % 10L);
    String str2 = "picasa--" + i;
    int j = 0;
    while (j < 5)
    {
      File localFile2 = new File(localFile1, str2);
      if ((localFile2.isDirectory()) || (localFile2.mkdirs()))
        localFile3 = new File(localFile2, str1);
      try
      {
        localFile3.createNewFile();
        boolean bool = localFile3.exists();
        if (!bool);
        label132: str2 = str2 + "e";
        ++j;
      }
      catch (IOException localIOException)
      {
        Log.d("PicasaStore", str2 + " is full: " + localIOException);
        break label132:
      }
    }
    return null;
  }

  public static PicasaStoreFacade get(Context paramContext)
  {
    monitorenter;
    try
    {
      if (sInstance == null)
        sInstance = new PicasaStoreFacade(paramContext);
      PicasaStoreFacade localPicasaStoreFacade = sInstance;
      return localPicasaStoreFacade;
    }
    finally
    {
      monitorexit;
    }
  }

  public static File getAlbumCoverCacheFile(long paramLong, String paramString1, String paramString2)
  {
    File localFile = getCacheDirectory();
    if (localFile == null)
      return null;
    return new File(localFile, "picasa_covers/" + getAlbumCoverKey(paramLong, paramString1) + paramString2);
  }

  public static String getAlbumCoverKey(long paramLong, String paramString)
  {
    return paramLong + '_' + Utils.crc64Long(paramString);
  }

  public static File getCacheDirectory()
  {
    monitorenter;
    while (true)
    {
      try
      {
        if (sCacheDir == null)
        {
          sCacheDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "cache/com.google.android.googlephotos");
          if (!sCacheDir.isDirectory())
          {
            boolean bool;
            if (!bool);
          }
        }
      }
      finally
      {
        try
        {
          File localFile2 = new File(sCacheDir, ".nomedia");
          if (!localFile2.exists())
            localFile2.createNewFile();
          label76: File localFile1 = sCacheDir;
          monitorexit;
          return localFile1;
        }
        catch (IOException localIOException)
        {
          Log.w("PicasaStore", "fail to create '.nomedia' in " + sCacheDir);
          sCacheDir = null;
          break label76:
          localObject = finally;
          monitorexit;
          throw localObject;
        }
      }
      Log.w("PicasaStore", "fail to create cache dir in external storage");
      sCacheDir = null;
    }
  }

  public static File getCacheFile(long paramLong, String paramString)
  {
    File localFile1 = getCacheDirectory();
    File localFile3;
    if (localFile1 == null)
    {
      localFile3 = null;
      return localFile3;
    }
    String str1 = paramLong + paramString;
    int i = (int)(paramLong % 10L);
    String str2 = "picasa--" + i;
    for (int j = 0; j < 5; ++j)
    {
      File localFile2 = new File(localFile1, str2);
      if (!localFile2.exists())
        return null;
      if (localFile2.isDirectory())
      {
        localFile3 = new File(localFile2, str1);
        if (localFile3.exists());
      }
      str2 = str2 + "e";
    }
    return null;
  }

  private PicasaStoreInfo getPicasaStoreInfo(ServiceInfo paramServiceInfo)
  {
    if ((!paramServiceInfo.enabled) || (!paramServiceInfo.applicationInfo.enabled))
    {
      Log.w("PicasaStore", "ignore disabled picasa sync adapter: " + paramServiceInfo);
      return null;
    }
    Bundle localBundle = paramServiceInfo.metaData;
    if (localBundle == null)
    {
      Log.w("PicasaStore", "missing metadata: " + paramServiceInfo);
      return null;
    }
    int i = localBundle.getInt("com.google.android.picasastore.priority", -1);
    String str = localBundle.getString("com.google.android.picasastore.authority");
    if ((i == -1) || (TextUtils.isEmpty(str)))
    {
      Log.w("PicasaStore", "missing required metadata info: " + paramServiceInfo);
      return null;
    }
    return new PicasaStoreInfo(paramServiceInfo.packageName, str, i);
  }

  private void updateAuthority(String paramString, boolean paramBoolean)
  {
    if (paramString.equals(this.mAuthority))
      return;
    this.mAuthority = paramString;
    Uri localUri = Uri.parse("content://" + this.mAuthority);
    this.mPhotosUri = Uri.withAppendedPath(localUri, "photos");
    this.mFingerprintUri = Uri.withAppendedPath(localUri, "fingerprint");
    this.mRecalculateFingerprintUri = this.mFingerprintUri.buildUpon().appendQueryParameter("force_recalculate", "1").build();
    this.mCachedFingerprintUri = this.mFingerprintUri.buildUpon().appendQueryParameter("cache_only", "1").build();
    this.mAlbumCoversUri = Uri.withAppendedPath(localUri, "albumcovers");
  }

  public Uri getAlbumCoverUri(long paramLong, String paramString)
  {
    return this.mAlbumCoversUri.buildUpon().appendPath(String.valueOf(paramLong)).appendQueryParameter("content_url", paramString).build();
  }

  public String getAuthority()
  {
    return this.mAuthority;
  }

  public Uri getPhotoUri(long paramLong, String paramString1, String paramString2)
  {
    return this.mPhotosUri.buildUpon().appendPath(String.valueOf(paramLong)).appendQueryParameter("type", paramString1).appendQueryParameter("content_url", paramString2).build();
  }

  public boolean isMaster()
  {
    return this.mMasterInfo == this.mLocalInfo;
  }

  public void onPackageAdded(String paramString)
  {
    updatePicasaSyncInfo(false);
  }

  public void onPackageChanged(String paramString)
  {
    updatePicasaSyncInfo(false);
  }

  public void onPackageRemoved(String paramString)
  {
    updatePicasaSyncInfo(false);
  }

  void updatePicasaSyncInfo(boolean paramBoolean)
  {
    monitorenter;
    while (true)
    {
      PicasaStoreInfo localPicasaStoreInfo;
      try
      {
        List localList = this.mContext.getPackageManager().queryIntentServices(new Intent("com.google.android.picasastore.PACKAGE_METADATA_LOOKUP"), 132);
        String str = this.mContext.getPackageName();
        localObject2 = null;
        Iterator localIterator = localList.iterator();
        do
        {
          do
          {
            if (!localIterator.hasNext())
              break label128;
            localPicasaStoreInfo = getPicasaStoreInfo(((ResolveInfo)localIterator.next()).serviceInfo);
          }
          while (localPicasaStoreInfo == null);
          if (localObject2 == null)
            break label165;
          if (localObject2.priority < localPicasaStoreInfo.priority)
            break label165;
        }
        while (!localPicasaStoreInfo.packageName.equals(str));
      }
      finally
      {
        monitorexit;
      }
      label128: this.mMasterInfo = localObject2;
      Utils.checkNotNull(this.mLocalInfo);
      Utils.checkNotNull(this.mMasterInfo);
      updateAuthority(this.mMasterInfo.authority, paramBoolean);
      monitorexit;
      return;
      label165: Object localObject2 = localPicasaStoreInfo;
    }
  }

  public static class DummyService extends Service
  {
    public IBinder onBind(Intent paramIntent)
    {
      return null;
    }
  }

  static class PicasaStoreInfo
  {
    public final String authority;
    public final String packageName;
    public final int priority;

    public PicasaStoreInfo(String paramString1, String paramString2, int paramInt)
    {
      this.packageName = paramString1;
      this.authority = paramString2;
      this.priority = paramInt;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasastore.PicasaStoreFacade
 * JD-Core Version:    0.5.4
 */