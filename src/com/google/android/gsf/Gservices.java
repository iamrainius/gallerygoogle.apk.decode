package com.google.android.gsf;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class Gservices
{
  public static final Uri CONTENT_PREFIX_URI;
  public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.gsf.gservices");
  public static final Pattern FALSE_PATTERN;
  public static final Pattern TRUE_PATTERN;
  private static HashMap<String, String> sCache;
  private static String[] sPreloadedPrefixes;
  private static ContentResolver sResolver;
  private static Object sVersionToken;

  static
  {
    CONTENT_PREFIX_URI = Uri.parse("content://com.google.android.gsf.gservices/prefix");
    TRUE_PATTERN = Pattern.compile("^(1|true|t|on|yes|y)$", 2);
    FALSE_PATTERN = Pattern.compile("^(0|false|f|off|no|n)$", 2);
    sPreloadedPrefixes = new String[0];
  }

  public static void bulkCacheByPrefix(ContentResolver paramContentResolver, String[] paramArrayOfString)
  {
    Map localMap = getStringsByPrefix(paramContentResolver, paramArrayOfString);
    monitorenter;
    Map.Entry localEntry;
    try
    {
      ensureCacheInitializedLocked(paramContentResolver);
      sPreloadedPrefixes = paramArrayOfString;
      Iterator localIterator = localMap.entrySet().iterator();
      if (!localIterator.hasNext())
        break label82;
      localEntry = (Map.Entry)localIterator.next();
    }
    finally
    {
      monitorexit;
    }
    label82: monitorexit;
  }

  private static void ensureCacheInitializedLocked(ContentResolver paramContentResolver)
  {
    if (sCache != null)
      return;
    sCache = new HashMap();
    sVersionToken = new Object();
    sResolver = paramContentResolver;
    new Thread("Gservices", paramContentResolver)
    {
      public void run()
      {
        Looper.prepare();
        this.val$cr.registerContentObserver(Gservices.CONTENT_URI, true, new ContentObserver(new Handler(Looper.myLooper()))
        {
          public void onChange(boolean paramBoolean)
          {
            monitorenter;
            try
            {
              Gservices.sCache.clear();
              Gservices.access$102(new Object());
              if (Gservices.sPreloadedPrefixes.length > 0)
                Gservices.bulkCacheByPrefix(Gservices.sResolver, Gservices.sPreloadedPrefixes);
              return;
            }
            finally
            {
              monitorexit;
            }
          }
        });
        Looper.loop();
      }
    }
    .start();
  }

  public static int getInt(ContentResolver paramContentResolver, String paramString, int paramInt)
  {
    String str = getString(paramContentResolver, paramString);
    if (str != null);
    try
    {
      int i = Integer.parseInt(str);
      return i;
      return paramInt;
    }
    catch (NumberFormatException localNumberFormatException)
    {
    }
    return paramInt;
  }

  public static String getString(ContentResolver paramContentResolver, String paramString)
  {
    return getString(paramContentResolver, paramString, null);
  }

  public static String getString(ContentResolver paramContentResolver, String paramString1, String paramString2)
  {
    monitorenter;
    Object localObject2;
    int j;
    try
    {
      ensureCacheInitializedLocked(paramContentResolver);
      localObject2 = sVersionToken;
      if (sCache.containsKey(paramString1))
      {
        String str2 = (String)sCache.get(paramString1);
        if (str2 != null)
          paramString2 = str2;
        return paramString2;
      }
      monitorexit;
      String[] arrayOfString = sPreloadedPrefixes;
      int i = arrayOfString.length;
      j = 0;
      if (j >= i)
        break label94;
      if (paramString1.startsWith(arrayOfString[j]))
        break label211;
    }
    finally
    {
      monitorexit;
    }
    label94: Cursor localCursor = sResolver.query(CONTENT_URI, null, null, new String[] { paramString1 }, null);
    if (localCursor == null)
    {
      sCache.put(paramString1, null);
      return paramString2;
    }
    try
    {
      localCursor.moveToFirst();
      String str1 = localCursor.getString(1);
      try
      {
        if (localObject2 == sVersionToken)
          sCache.put(paramString1, str1);
        monitorexit;
        if (str1 != null)
          paramString2 = str1;
        return paramString2;
      }
      finally
      {
        monitorexit;
      }
    }
    finally
    {
      localCursor.close();
    }
    label211: return paramString2;
  }

  public static Map<String, String> getStringsByPrefix(ContentResolver paramContentResolver, String[] paramArrayOfString)
  {
    Cursor localCursor = paramContentResolver.query(CONTENT_PREFIX_URI, null, null, paramArrayOfString, null);
    TreeMap localTreeMap = new TreeMap();
    if (localCursor == null)
      return localTreeMap;
    while (true)
      try
      {
        if (!localCursor.moveToNext())
          break label57;
        label57: localTreeMap.put(localCursor.getString(0), localCursor.getString(1));
      }
      finally
      {
        localCursor.close();
      }
    return localTreeMap;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.gsf.Gservices
 * JD-Core Version:    0.5.4
 */