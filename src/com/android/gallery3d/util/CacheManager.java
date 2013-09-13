package com.android.gallery3d.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.android.gallery3d.common.BlobCache;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class CacheManager
{
  private static HashMap<String, BlobCache> sCacheMap = new HashMap();
  private static boolean sOldCheckDone = false;

  public static BlobCache getCache(Context paramContext, String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    BlobCache localBlobCache1;
    String str;
    synchronized (sCacheMap)
    {
      if (!sOldCheckDone)
      {
        removeOldFilesIfNecessary(paramContext);
        sOldCheckDone = true;
      }
      localBlobCache1 = (BlobCache)sCacheMap.get(paramString);
      if (localBlobCache1 != null)
        break label132;
      File localFile = paramContext.getExternalCacheDir();
      str = localFile.getAbsolutePath() + "/" + paramString;
    }
    BlobCache localBlobCache2;
    try
    {
      localBlobCache2 = new BlobCache(str, paramInt1, paramInt2, false, paramInt3);
    }
    catch ( localObject)
    {
      try
      {
        sCacheMap.put(paramString, localBlobCache2);
        while (true)
        {
          label100: monitorexit;
          return localBlobCache2;
          label106: Throwable localThrowable;
          Log.e("CacheManager", "Cannot instantiate cache!", localThrowable);
        }
        localObject = finally;
        monitorexit;
        label132: throw localObject;
      }
      catch (IOException localIOException2)
      {
        break label106:
        localBlobCache2 = localBlobCache1;
        break label100:
        localIOException2 = localIOException2;
        localBlobCache2 = localBlobCache1;
      }
    }
  }

  private static void removeOldFilesIfNecessary(Context paramContext)
  {
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(paramContext);
    int i;
    try
    {
      int j = localSharedPreferences.getInt("cache-up-to-date", 0);
      i = j;
      if (i != 0)
        return;
      localSharedPreferences.edit().putInt("cache-up-to-date", 1).commit();
      File localFile = paramContext.getExternalCacheDir();
      String str = localFile.getAbsolutePath() + "/";
      BlobCache.deleteFiles(str + "imgcache");
      BlobCache.deleteFiles(str + "rev_geocoding");
      BlobCache.deleteFiles(str + "bookmark");
      return;
    }
    catch (Throwable localThrowable)
    {
      i = 0;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.CacheManager
 * JD-Core Version:    0.5.4
 */