package com.google.android.picasasync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StatFs;
import android.util.Log;
import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Utils;
import com.google.android.gsf.Gservices;
import com.google.android.picasastore.Config;
import com.google.android.picasastore.MetricsUtils;
import com.google.android.picasastore.PicasaStoreFacade;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class PrefetchHelper
{
  private static final String ALBUM_TABLE_NAME;
  private static final String PHOTO_TABLE_NAME;
  private static final String[] PROJECTION_ID;
  private static final String[] PROJECTION_ID_CACHE_FLAG_STATUS_THUMBNAIL;
  private static final String[] PROJECTION_ID_ROTATION_CONTENT_URL_CONTENT_TYPE_SCREENNAIL_URL;
  private static final String[] PROJECTION_ID_SCREENNAIL_URL;
  private static final String[] PROJECTION_ID_THUMBNAIL_URL;
  private static final String QUERY_CACHE_STATUS_COUNT;
  private static final String WHERE_ALBUM_ID_AND_CACHE_STATUS;
  private static final String WHERE_CACHE_STATUS_AND_USER_ID;
  private static final String WHERE_USER_ID_AND_CACHE_FLAG;
  private static PrefetchHelper sInstance;
  private static int sMaxCachedScreennailCount = -1;
  private AtomicInteger mCacheConfigVersion = new AtomicInteger(0);
  private String mCacheDir;
  private final Context mContext;
  private final PicasaDatabaseHelper mDbHelper;

  static
  {
    ALBUM_TABLE_NAME = AlbumEntry.SCHEMA.getTableName();
    PHOTO_TABLE_NAME = PhotoEntry.SCHEMA.getTableName();
    PROJECTION_ID = new String[] { "_id" };
    PROJECTION_ID_ROTATION_CONTENT_URL_CONTENT_TYPE_SCREENNAIL_URL = new String[] { "_id", "rotation", "content_url", "content_type", "screennail_url" };
    PROJECTION_ID_CACHE_FLAG_STATUS_THUMBNAIL = new String[] { "_id", "cache_flag", "cache_status", "thumbnail_url" };
    WHERE_USER_ID_AND_CACHE_FLAG = String.format("%s=? AND %s=?", new Object[] { "user_id", "cache_flag" });
    WHERE_ALBUM_ID_AND_CACHE_STATUS = String.format("%s=? AND %s=?", new Object[] { "album_id", "cache_status" });
    PROJECTION_ID_SCREENNAIL_URL = new String[] { "_id", "screennail_url" };
    WHERE_CACHE_STATUS_AND_USER_ID = String.format("%s = ? AND %s = ?", new Object[] { "cache_status", "user_id" });
    PROJECTION_ID_THUMBNAIL_URL = new String[] { "_id", "thumbnail_url" };
    Object[] arrayOfObject = new Object[10];
    arrayOfObject[0] = PHOTO_TABLE_NAME;
    arrayOfObject[1] = "cache_status";
    arrayOfObject[2] = PHOTO_TABLE_NAME;
    arrayOfObject[3] = ALBUM_TABLE_NAME;
    arrayOfObject[4] = PHOTO_TABLE_NAME;
    arrayOfObject[5] = "album_id";
    arrayOfObject[6] = ALBUM_TABLE_NAME;
    arrayOfObject[7] = "_id";
    arrayOfObject[8] = ALBUM_TABLE_NAME;
    arrayOfObject[9] = "cache_flag";
    QUERY_CACHE_STATUS_COUNT = String.format("SELECT count(*), %s.%s AS status FROM %s, %s WHERE %s.%s = %s.%s AND %s.%s = ? GROUP BY status", arrayOfObject);
  }

  private PrefetchHelper(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mDbHelper = PicasaDatabaseHelper.get(paramContext);
  }

  private void collectKeepSetForFullImages(SQLiteDatabase paramSQLiteDatabase, HashMap<Long, Integer> paramHashMap)
  {
    Object[] arrayOfObject = new Object[7];
    arrayOfObject[0] = "_id";
    arrayOfObject[1] = PHOTO_TABLE_NAME;
    arrayOfObject[2] = "album_id";
    arrayOfObject[3] = "_id";
    arrayOfObject[4] = ALBUM_TABLE_NAME;
    arrayOfObject[5] = "cache_flag";
    arrayOfObject[6] = Integer.valueOf(2);
    Cursor localCursor = paramSQLiteDatabase.rawQuery(String.format("SELECT %s FROM %s WHERE %s IN (SELECT %s FROM %s WHERE %s = %s)", arrayOfObject), null);
    while (true)
      try
      {
        if (!localCursor.moveToNext())
          break label92;
        label92: paramHashMap.put(Long.valueOf(localCursor.getLong(0)), Integer.valueOf(2));
      }
      finally
      {
        Utils.closeSilently(localCursor);
      }
  }

  private void collectKeepSetForScreenNails(SQLiteDatabase paramSQLiteDatabase, HashMap<Long, Integer> paramHashMap)
  {
    if (sMaxCachedScreennailCount < 0)
    {
      sMaxCachedScreennailCount = Gservices.getInt(this.mContext.getContentResolver(), "picasasync_max_cached_screennail_count", 5000);
      if (sMaxCachedScreennailCount < 0)
        sMaxCachedScreennailCount = 5000;
    }
    Object[] arrayOfObject = new Object[9];
    arrayOfObject[0] = "_id";
    arrayOfObject[1] = PHOTO_TABLE_NAME;
    arrayOfObject[2] = "album_id";
    arrayOfObject[3] = "_id";
    arrayOfObject[4] = ALBUM_TABLE_NAME;
    arrayOfObject[5] = "cache_flag";
    arrayOfObject[6] = Integer.valueOf(1);
    arrayOfObject[7] = "date_updated";
    arrayOfObject[8] = Integer.valueOf(sMaxCachedScreennailCount);
    Cursor localCursor = paramSQLiteDatabase.rawQuery(String.format("SELECT %s FROM %s WHERE %s IN (SELECT %s FROM %s WHERE %s = %s) ORDER BY %s DESC LIMIT %s", arrayOfObject), null);
    while (true)
      try
      {
        if (!localCursor.moveToNext())
          break label144;
        label144: paramHashMap.put(Long.valueOf(localCursor.getLong(0)), Integer.valueOf(1));
      }
      finally
      {
        Utils.closeSilently(localCursor);
      }
  }

  private void deleteUnusedAlbumCovers(PrefetchContext paramPrefetchContext, HashSet<String> paramHashSet)
    throws IOException
  {
    File localFile = new File(getCacheDirectory(), "picasa_covers");
    String[] arrayOfString = localFile.list();
    if (arrayOfString == null)
      return;
    int i = arrayOfString.length;
    for (int j = 0; ; ++j)
    {
      if (j < i);
      String str = arrayOfString[j];
      if ((paramHashSet.contains(getKeyFromFilename(str))) || (new File(localFile, str).delete()))
        continue;
      Log.w("PrefetchHelper", "cannot delete album cover: " + str);
    }
  }

  private void deleteUnusedCacheFiles(PrefetchContext paramPrefetchContext, HashMap<Long, Integer> paramHashMap)
    throws IOException
  {
    String str1 = getCacheDirectory();
    String[] arrayOfString1 = new File(str1).list();
    int i = arrayOfString1.length;
    String str2;
    for (int j = 0; ; ++j)
    {
      if (j < i)
      {
        str2 = arrayOfString1[j];
        if (!paramPrefetchContext.syncInterrupted())
          break label48;
      }
      return;
      label48: label59: if (str2.startsWith("picasa-"))
        break;
    }
    while (true)
    {
      File localFile;
      int l;
      String str3;
      try
      {
        localFile = new File(str1, str2);
        String[] arrayOfString2 = localFile.list();
        if (arrayOfString2 != null);
        int k = arrayOfString2.length;
        l = 0;
        if (l < k)
        {
          str3 = arrayOfString2[l];
          if (!paramPrefetchContext.syncInterrupted())
            break label149;
        }
        if (localFile.list().length == 0);
        localFile.delete();
      }
      catch (Throwable localThrowable)
      {
        Log.w("PrefetchHelper", localThrowable);
      }
      break label59:
      if (!keepCacheFile(localFile, str3, paramHashMap))
        label149: new File(localFile, str3).delete();
      ++l;
    }
  }

  // ERROR //
  private boolean downloadPhoto(PrefetchContext paramPrefetchContext, String paramString, File paramFile)
  {
    // Byte code:
    //   0: invokestatic 277	android/os/SystemClock:elapsedRealtime	()J
    //   3: lstore 4
    //   5: aconst_null
    //   6: astore 6
    //   8: aconst_null
    //   9: astore 7
    //   11: aload_2
    //   12: invokestatic 283	com/google/android/picasastore/HttpUtils:openInputStream	(Ljava/lang/String;)Ljava/io/InputStream;
    //   15: astore 7
    //   17: new 285	java/io/FileOutputStream
    //   20: dup
    //   21: aload_3
    //   22: invokespecial 288	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   25: astore 11
    //   27: sipush 4096
    //   30: newarray byte
    //   32: astore 12
    //   34: aload 7
    //   36: aload 12
    //   38: iconst_0
    //   39: aload 12
    //   41: arraylength
    //   42: invokevirtual 294	java/io/InputStream:read	([BII)I
    //   45: istore 13
    //   47: iload 13
    //   49: ifle +66 -> 115
    //   52: aload_1
    //   53: invokevirtual 256	com/google/android/picasasync/PrefetchHelper$PrefetchContext:syncInterrupted	()Z
    //   56: ifeq +29 -> 85
    //   59: aload 7
    //   61: invokestatic 298	com/google/android/picasastore/HttpUtils:abortConnectionSilently	(Ljava/io/InputStream;)V
    //   64: aload 7
    //   66: invokestatic 301	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   69: aload 11
    //   71: invokestatic 301	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   74: invokestatic 277	android/os/SystemClock:elapsedRealtime	()J
    //   77: lload 4
    //   79: lsub
    //   80: invokestatic 307	com/google/android/picasastore/MetricsUtils:incrementNetworkOpDurationAndCount	(J)V
    //   83: iconst_0
    //   84: ireturn
    //   85: aload 11
    //   87: aload 12
    //   89: iconst_0
    //   90: iload 13
    //   92: invokevirtual 311	java/io/FileOutputStream:write	([BII)V
    //   95: aload 7
    //   97: aload 12
    //   99: iconst_0
    //   100: aload 12
    //   102: arraylength
    //   103: invokevirtual 294	java/io/InputStream:read	([BII)I
    //   106: istore 14
    //   108: iload 14
    //   110: istore 13
    //   112: goto -65 -> 47
    //   115: aload 7
    //   117: invokestatic 301	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   120: aload 11
    //   122: invokestatic 301	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   125: invokestatic 277	android/os/SystemClock:elapsedRealtime	()J
    //   128: lload 4
    //   130: lsub
    //   131: invokestatic 307	com/google/android/picasastore/MetricsUtils:incrementNetworkOpDurationAndCount	(J)V
    //   134: iconst_1
    //   135: ireturn
    //   136: astore 9
    //   138: aload 7
    //   140: invokestatic 298	com/google/android/picasastore/HttpUtils:abortConnectionSilently	(Ljava/io/InputStream;)V
    //   143: ldc 226
    //   145: new 228	java/lang/StringBuilder
    //   148: dup
    //   149: invokespecial 229	java/lang/StringBuilder:<init>	()V
    //   152: ldc_w 313
    //   155: invokevirtual 235	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   158: aload_2
    //   159: invokestatic 317	com/android/gallery3d/common/Utils:maskDebugInfo	(Ljava/lang/Object;)Ljava/lang/String;
    //   162: invokevirtual 235	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   165: invokevirtual 238	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   168: aload 9
    //   170: invokestatic 320	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   173: pop
    //   174: aload 7
    //   176: invokestatic 301	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   179: aload 6
    //   181: invokestatic 301	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   184: invokestatic 277	android/os/SystemClock:elapsedRealtime	()J
    //   187: lload 4
    //   189: lsub
    //   190: invokestatic 307	com/google/android/picasastore/MetricsUtils:incrementNetworkOpDurationAndCount	(J)V
    //   193: iconst_0
    //   194: ireturn
    //   195: astore 8
    //   197: aload 7
    //   199: invokestatic 301	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   202: aload 6
    //   204: invokestatic 301	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   207: invokestatic 277	android/os/SystemClock:elapsedRealtime	()J
    //   210: lload 4
    //   212: lsub
    //   213: invokestatic 307	com/google/android/picasastore/MetricsUtils:incrementNetworkOpDurationAndCount	(J)V
    //   216: aload 8
    //   218: athrow
    //   219: astore 8
    //   221: aload 11
    //   223: astore 6
    //   225: goto -28 -> 197
    //   228: astore 9
    //   230: aload 11
    //   232: astore 6
    //   234: goto -96 -> 138
    //
    // Exception table:
    //   from	to	target	type
    //   11	27	136	java/io/IOException
    //   11	27	195	finally
    //   138	174	195	finally
    //   27	47	219	finally
    //   52	64	219	finally
    //   85	108	219	finally
    //   27	47	228	java/io/IOException
    //   52	64	228	java/io/IOException
    //   85	108	228	java/io/IOException
  }

  // ERROR //
  private void generateScreennail(long paramLong, int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 325	com/google/android/picasasync/PrefetchHelper:getAvailableStorage	()J
    //   4: lstore 4
    //   6: lload 4
    //   8: ldc2_w 326
    //   11: lcmp
    //   12: ifge +38 -> 50
    //   15: new 329	java/lang/RuntimeException
    //   18: dup
    //   19: new 228	java/lang/StringBuilder
    //   22: dup
    //   23: invokespecial 229	java/lang/StringBuilder:<init>	()V
    //   26: ldc_w 331
    //   29: invokevirtual 235	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   32: lload 4
    //   34: invokevirtual 334	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   37: ldc_w 336
    //   40: invokevirtual 235	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   43: invokevirtual 238	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   46: invokespecial 337	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   49: athrow
    //   50: aconst_null
    //   51: astore 6
    //   53: lload_1
    //   54: ldc_w 339
    //   57: invokestatic 345	com/google/android/picasastore/PicasaStoreFacade:getCacheFile	(JLjava/lang/String;)Ljava/io/File;
    //   60: astore 10
    //   62: lload_1
    //   63: ldc_w 347
    //   66: invokestatic 350	com/google/android/picasastore/PicasaStoreFacade:createCacheFile	(JLjava/lang/String;)Ljava/io/File;
    //   69: astore 11
    //   71: aload 10
    //   73: ifnull +8 -> 81
    //   76: aload 11
    //   78: ifnonnull +8 -> 86
    //   81: aconst_null
    //   82: invokestatic 301	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   85: return
    //   86: aload 10
    //   88: invokevirtual 353	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   91: astore 12
    //   93: new 355	android/graphics/BitmapFactory$Options
    //   96: dup
    //   97: invokespecial 356	android/graphics/BitmapFactory$Options:<init>	()V
    //   100: astore 13
    //   102: aload 13
    //   104: iconst_1
    //   105: putfield 360	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   108: aload 12
    //   110: aload 13
    //   112: invokestatic 366	android/graphics/BitmapFactory:decodeFile	(Ljava/lang/String;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   115: pop
    //   116: aload 13
    //   118: aload 13
    //   120: getfield 369	android/graphics/BitmapFactory$Options:outWidth	I
    //   123: aload 13
    //   125: getfield 372	android/graphics/BitmapFactory$Options:outHeight	I
    //   128: getstatic 377	com/google/android/picasastore/Config:sScreenNailSize	I
    //   131: invokestatic 383	com/android/gallery3d/common/BitmapUtils:computeSampleSizeLarger	(III)I
    //   134: putfield 386	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   137: aload 13
    //   139: iconst_0
    //   140: putfield 360	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   143: aload 12
    //   145: aload 13
    //   147: invokestatic 366	android/graphics/BitmapFactory:decodeFile	(Ljava/lang/String;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   150: astore 15
    //   152: aload 15
    //   154: ifnonnull +8 -> 162
    //   157: aconst_null
    //   158: invokestatic 301	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   161: return
    //   162: aload 15
    //   164: getstatic 377	com/google/android/picasastore/Config:sScreenNailSize	I
    //   167: iconst_1
    //   168: invokestatic 390	com/android/gallery3d/common/BitmapUtils:resizeDownBySideLength	(Landroid/graphics/Bitmap;IZ)Landroid/graphics/Bitmap;
    //   171: iload_3
    //   172: iconst_1
    //   173: invokestatic 393	com/android/gallery3d/common/BitmapUtils:rotateBitmap	(Landroid/graphics/Bitmap;IZ)Landroid/graphics/Bitmap;
    //   176: bipush 95
    //   178: invokestatic 397	com/android/gallery3d/common/BitmapUtils:compressToBytes	(Landroid/graphics/Bitmap;I)[B
    //   181: astore 16
    //   183: new 285	java/io/FileOutputStream
    //   186: dup
    //   187: aload 11
    //   189: invokespecial 288	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   192: astore 17
    //   194: aload 17
    //   196: aload 16
    //   198: iconst_0
    //   199: aload 16
    //   201: arraylength
    //   202: invokevirtual 311	java/io/FileOutputStream:write	([BII)V
    //   205: aload 17
    //   207: invokestatic 301	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   210: return
    //   211: astore 8
    //   213: ldc 226
    //   215: new 228	java/lang/StringBuilder
    //   218: dup
    //   219: invokespecial 229	java/lang/StringBuilder:<init>	()V
    //   222: ldc_w 399
    //   225: invokevirtual 235	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   228: aload 8
    //   230: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   233: invokevirtual 238	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   236: invokestatic 405	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   239: pop
    //   240: aload 6
    //   242: invokestatic 301	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   245: return
    //   246: astore 7
    //   248: aload 6
    //   250: invokestatic 301	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   253: aload 7
    //   255: athrow
    //   256: astore 7
    //   258: aload 17
    //   260: astore 6
    //   262: goto -14 -> 248
    //   265: astore 8
    //   267: aload 17
    //   269: astore 6
    //   271: goto -58 -> 213
    //
    // Exception table:
    //   from	to	target	type
    //   53	71	211	java/lang/Throwable
    //   86	152	211	java/lang/Throwable
    //   162	194	211	java/lang/Throwable
    //   53	71	246	finally
    //   86	152	246	finally
    //   162	194	246	finally
    //   213	240	246	finally
    //   194	205	256	finally
    //   194	205	265	java/lang/Throwable
  }

  public static PrefetchHelper get(Context paramContext)
  {
    monitorenter;
    try
    {
      if (sInstance == null)
        sInstance = new PrefetchHelper(paramContext);
      PrefetchHelper localPrefetchHelper = sInstance;
      return localPrefetchHelper;
    }
    finally
    {
      monitorexit;
    }
  }

  private File getAlbumCoverCacheFile(long paramLong, String paramString1, String paramString2)
    throws IOException
  {
    File localFile = PicasaStoreFacade.getAlbumCoverCacheFile(paramLong, paramString1, paramString2);
    if (localFile == null)
      throw new IOException("external storage not present");
    return localFile;
  }

  private long getAvailableStorage()
  {
    try
    {
      StatFs localStatFs = new StatFs(getCacheDirectory());
      long l = localStatFs.getAvailableBlocks();
      int i = localStatFs.getBlockSize();
      return l * i;
    }
    catch (Throwable localThrowable)
    {
      Log.w("PrefetchHelper", "Fail to getAvailableStorage", localThrowable);
    }
    return 0L;
  }

  private String getCacheDirectory()
    throws IOException
  {
    if (this.mCacheDir == null)
    {
      File localFile = PicasaStoreFacade.getCacheDirectory();
      if (localFile == null)
        throw new IOException("external storage is not present");
      this.mCacheDir = localFile.getAbsolutePath();
    }
    return this.mCacheDir;
  }

  private static String getKeyFromFilename(String paramString)
  {
    int i = paramString.lastIndexOf('.');
    if (i < 0)
      return paramString;
    return paramString.substring(0, i);
  }

  public static void invalidateAlbumCoverCache(long paramLong, String paramString)
  {
    File localFile = PicasaStoreFacade.getAlbumCoverCacheFile(paramLong, paramString, ".thumb");
    if (localFile == null)
      return;
    localFile.delete();
  }

  public static void invalidatePhotoCache(long paramLong)
  {
    File localFile1 = PicasaStoreFacade.getCacheFile(paramLong, ".full");
    if (localFile1 != null)
      localFile1.delete();
    File localFile2 = PicasaStoreFacade.getCacheFile(paramLong, ".screen");
    if (localFile2 == null)
      return;
    localFile2.delete();
  }

  private boolean keepCacheFile(File paramFile, String paramString, HashMap<Long, Integer> paramHashMap)
  {
    int i = paramString.lastIndexOf('.');
    if (i == -1)
      return false;
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(i);
    try
    {
      long l = Long.parseLong(str1);
      Integer localInteger = (Integer)paramHashMap.get(Long.valueOf(l));
      if (localInteger == null)
      {
        if ((".screen".equals(str2)) && (new File(paramFile, str1 + ".full").length() > 0L))
          return true;
      }
      else
      {
        if (2 == localInteger.intValue())
        {
          if ((!".full".equals(str2)) || (new File(paramFile, str1 + ".screen").length() <= 0L))
            break label234;
          paramHashMap.remove(Long.valueOf(l));
          break label234:
        }
        if (".screen".equals(str2))
        {
          paramHashMap.remove(Long.valueOf(l));
          return true;
        }
      }
    }
    catch (Throwable localThrowable)
    {
      Log.w("PrefetchHelper", "cannot parse id: " + paramString);
      return false;
    }
    return false;
    label234: return true;
  }

  private void setCacheStatus(SQLiteDatabase paramSQLiteDatabase, HashMap<Long, Integer> paramHashMap)
  {
    paramSQLiteDatabase.beginTransaction();
    while (true)
    {
      ContentValues localContentValues;
      String[] arrayOfString;
      try
      {
        localContentValues = new ContentValues();
        arrayOfString = new String[1];
        Iterator localIterator = paramHashMap.entrySet().iterator();
        if (!localIterator.hasNext())
          break label128;
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        if (((Integer)localEntry.getValue()).intValue() != 2)
          break label122;
        i = 2;
        localContentValues.put("cache_status", Integer.valueOf(i));
        arrayOfString[0] = String.valueOf(localEntry.getKey());
      }
      finally
      {
        paramSQLiteDatabase.endTransaction();
      }
      label122: int i = 1;
    }
    label128: paramSQLiteDatabase.setTransactionSuccessful();
    paramSQLiteDatabase.endTransaction();
  }

  // ERROR //
  private void syncFullImagesForAlbum(PrefetchContext paramPrefetchContext, long paramLong)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 128	com/google/android/picasasync/PrefetchHelper:mDbHelper	Lcom/google/android/picasasync/PicasaDatabaseHelper;
    //   4: invokevirtual 530	com/google/android/picasasync/PicasaDatabaseHelper:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   7: astore 4
    //   9: iconst_2
    //   10: anewarray 53	java/lang/String
    //   13: astore 5
    //   15: aload 5
    //   17: iconst_0
    //   18: lload_2
    //   19: invokestatic 533	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   22: aastore
    //   23: aload 5
    //   25: iconst_1
    //   26: iconst_2
    //   27: invokestatic 535	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   30: aastore
    //   31: aload 4
    //   33: getstatic 51	com/google/android/picasasync/PrefetchHelper:PHOTO_TABLE_NAME	Ljava/lang/String;
    //   36: getstatic 67	com/google/android/picasasync/PrefetchHelper:PROJECTION_ID_ROTATION_CONTENT_URL_CONTENT_TYPE_SCREENNAIL_URL	[Ljava/lang/String;
    //   39: getstatic 89	com/google/android/picasasync/PrefetchHelper:WHERE_ALBUM_ID_AND_CACHE_STATUS	Ljava/lang/String;
    //   42: aload 5
    //   44: aconst_null
    //   45: aconst_null
    //   46: aconst_null
    //   47: invokevirtual 539	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   50: astore 6
    //   52: aload 6
    //   54: ifnonnull +4 -> 58
    //   57: return
    //   58: aload 6
    //   60: invokeinterface 542 1 0
    //   65: istore 8
    //   67: iload 8
    //   69: ifne +11 -> 80
    //   72: aload 6
    //   74: invokeinterface 545 1 0
    //   79: return
    //   80: aload_0
    //   81: aload 4
    //   83: lload_2
    //   84: iconst_1
    //   85: invokespecial 549	com/google/android/picasasync/PrefetchHelper:updateAlbumCacheStatus	(Landroid/database/sqlite/SQLiteDatabase;JI)V
    //   88: aload 6
    //   90: invokeinterface 152 1 0
    //   95: ifeq +304 -> 399
    //   98: aload_1
    //   99: invokevirtual 552	com/google/android/picasasync/PrefetchHelper$PrefetchContext:checkCacheConfigVersion	()Z
    //   102: ifne +16 -> 118
    //   105: ldc 226
    //   107: ldc_w 554
    //   110: invokestatic 244	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   113: pop
    //   114: aload_1
    //   115: invokevirtual 557	com/google/android/picasasync/PrefetchHelper$PrefetchContext:stopSync	()V
    //   118: aload_1
    //   119: invokevirtual 256	com/google/android/picasasync/PrefetchHelper$PrefetchContext:syncInterrupted	()Z
    //   122: istore 9
    //   124: iload 9
    //   126: ifeq +11 -> 137
    //   129: aload 6
    //   131: invokeinterface 545 1 0
    //   136: return
    //   137: aload 6
    //   139: iconst_0
    //   140: invokeinterface 156 2 0
    //   145: lstore 10
    //   147: aload 6
    //   149: iconst_1
    //   150: invokeinterface 559 2 0
    //   155: istore 12
    //   157: aload 6
    //   159: iconst_2
    //   160: invokeinterface 562 2 0
    //   165: astore 13
    //   167: aload 6
    //   169: iconst_3
    //   170: invokeinterface 562 2 0
    //   175: astore 14
    //   177: aload 6
    //   179: iconst_4
    //   180: invokeinterface 562 2 0
    //   185: astore 15
    //   187: new 196	java/io/File
    //   190: dup
    //   191: new 228	java/lang/StringBuilder
    //   194: dup
    //   195: invokespecial 229	java/lang/StringBuilder:<init>	()V
    //   198: lload 10
    //   200: invokevirtual 334	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   203: ldc_w 339
    //   206: invokevirtual 235	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   209: invokevirtual 238	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   212: invokespecial 251	java/io/File:<init>	(Ljava/lang/String;)V
    //   215: invokevirtual 465	java/io/File:length	()J
    //   218: lstore 16
    //   220: lload 16
    //   222: lconst_0
    //   223: lcmp
    //   224: ifne +87 -> 311
    //   227: aload_0
    //   228: aload_1
    //   229: lload 10
    //   231: aload 13
    //   233: ldc_w 339
    //   236: invokespecial 566	com/google/android/picasasync/PrefetchHelper:syncOnePhoto	(Lcom/google/android/picasasync/PrefetchHelper$PrefetchContext;JLjava/lang/String;Ljava/lang/String;)Z
    //   239: istore 20
    //   241: aload_1
    //   242: lload 10
    //   244: iload 20
    //   246: invokevirtual 570	com/google/android/picasasync/PrefetchHelper$PrefetchContext:onDownloadFinish	(JZ)V
    //   249: aload_1
    //   250: invokevirtual 573	com/google/android/picasasync/PrefetchHelper$PrefetchContext:getDownloadFailCount	()I
    //   253: iconst_3
    //   254: if_icmple +64 -> 318
    //   257: new 329	java/lang/RuntimeException
    //   260: dup
    //   261: ldc_w 575
    //   264: invokespecial 337	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   267: athrow
    //   268: astore 7
    //   270: aload 6
    //   272: invokeinterface 545 1 0
    //   277: aload 7
    //   279: athrow
    //   280: astore 19
    //   282: aload_1
    //   283: lload 10
    //   285: iconst_0
    //   286: invokevirtual 570	com/google/android/picasasync/PrefetchHelper$PrefetchContext:onDownloadFinish	(JZ)V
    //   289: aload_1
    //   290: invokevirtual 573	com/google/android/picasasync/PrefetchHelper$PrefetchContext:getDownloadFailCount	()I
    //   293: iconst_3
    //   294: if_icmple +14 -> 308
    //   297: new 329	java/lang/RuntimeException
    //   300: dup
    //   301: ldc_w 575
    //   304: invokespecial 337	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   307: athrow
    //   308: aload 19
    //   310: athrow
    //   311: aload_1
    //   312: lload 10
    //   314: iconst_1
    //   315: invokevirtual 570	com/google/android/picasasync/PrefetchHelper$PrefetchContext:onDownloadFinish	(JZ)V
    //   318: aload 14
    //   320: ldc_w 577
    //   323: invokevirtual 262	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   326: ifeq +14 -> 340
    //   329: aload_0
    //   330: lload 10
    //   332: iload 12
    //   334: invokespecial 579	com/google/android/picasasync/PrefetchHelper:generateScreennail	(JI)V
    //   337: goto -249 -> 88
    //   340: aload 14
    //   342: ldc_w 581
    //   345: invokevirtual 262	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   348: ifeq -260 -> 88
    //   351: aload_0
    //   352: aload_1
    //   353: lload 10
    //   355: aload 15
    //   357: ldc_w 347
    //   360: invokespecial 566	com/google/android/picasasync/PrefetchHelper:syncOnePhoto	(Lcom/google/android/picasasync/PrefetchHelper$PrefetchContext;JLjava/lang/String;Ljava/lang/String;)Z
    //   363: ifne -275 -> 88
    //   366: ldc 226
    //   368: new 228	java/lang/StringBuilder
    //   371: dup
    //   372: invokespecial 229	java/lang/StringBuilder:<init>	()V
    //   375: ldc_w 583
    //   378: invokevirtual 235	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   381: aload 15
    //   383: invokestatic 317	com/android/gallery3d/common/Utils:maskDebugInfo	(Ljava/lang/Object;)Ljava/lang/String;
    //   386: invokevirtual 235	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   389: invokevirtual 238	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   392: invokestatic 244	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   395: pop
    //   396: goto -308 -> 88
    //   399: aload_0
    //   400: aload 4
    //   402: lload_2
    //   403: iconst_3
    //   404: invokespecial 549	com/google/android/picasasync/PrefetchHelper:updateAlbumCacheStatus	(Landroid/database/sqlite/SQLiteDatabase;JI)V
    //   407: aload 6
    //   409: invokeinterface 545 1 0
    //   414: return
    //
    // Exception table:
    //   from	to	target	type
    //   58	67	268	finally
    //   80	88	268	finally
    //   88	118	268	finally
    //   118	124	268	finally
    //   137	220	268	finally
    //   241	268	268	finally
    //   282	308	268	finally
    //   308	311	268	finally
    //   311	318	268	finally
    //   318	337	268	finally
    //   340	396	268	finally
    //   399	407	268	finally
    //   227	241	280	finally
  }

  private boolean syncOneAlbumCover(PrefetchContext paramPrefetchContext, long paramLong, String paramString)
    throws IOException
  {
    long l = getAvailableStorage();
    if (l < 1073741824L)
      throw new RuntimeException("space not enough: " + l + ", stop sync");
    File localFile = getAlbumCoverCacheFile(paramLong, paramString, ".download");
    if (!downloadPhoto(paramPrefetchContext, PicasaApi.convertImageUrl(paramString, Config.sThumbNailSize, true), localFile))
    {
      localFile.delete();
      return false;
    }
    if (!localFile.renameTo(getAlbumCoverCacheFile(paramLong, paramString, ".thumb")))
    {
      Log.e("PrefetchHelper", "cannot rename file: " + localFile);
      localFile.delete();
      return false;
    }
    return true;
  }

  private boolean syncOnePhoto(PrefetchContext paramPrefetchContext, long paramLong, String paramString1, String paramString2)
    throws IOException
  {
    long l = getAvailableStorage();
    if (l < 1073741824L)
      throw new RuntimeException("space not enough: " + l + ", stop sync");
    File localFile = PicasaStoreFacade.createCacheFile(paramLong, ".download");
    if (localFile == null)
      throw new IOException("external storage absent?");
    if ((Log.isLoggable("PrefetchHelper", 2)) && (paramString2 == ".full"))
      Log.v("PrefetchHelper", "download full image for " + Utils.maskDebugInfo(Long.valueOf(paramLong)) + ": " + Utils.maskDebugInfo(paramString1));
    if (!downloadPhoto(paramPrefetchContext, paramString1, localFile))
    {
      localFile.delete();
      return false;
    }
    if (!localFile.renameTo(PicasaStoreFacade.createCacheFile(paramLong, paramString2)))
    {
      Log.e("PrefetchHelper", "cannot rename file: " + localFile);
      localFile.delete();
      return false;
    }
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("cache_status", Integer.valueOf(0));
    SQLiteDatabase localSQLiteDatabase = this.mDbHelper.getWritableDatabase();
    String str = PHOTO_TABLE_NAME;
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(paramLong);
    localSQLiteDatabase.update(str, localContentValues, "_id=?", arrayOfString);
    return true;
  }

  private void updateAlbumCacheStatus(SQLiteDatabase paramSQLiteDatabase, long paramLong, int paramInt)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("cache_status", Integer.valueOf(paramInt));
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(paramLong);
    paramSQLiteDatabase.update(ALBUM_TABLE_NAME, localContentValues, "_id=?", arrayOfString);
    notifyAlbumsChange();
  }

  public void cleanCache(PrefetchContext paramPrefetchContext)
    throws IOException
  {
    int i = MetricsUtils.begin("PrefetchHelper.cleanCache");
    SQLiteDatabase localSQLiteDatabase = this.mDbHelper.getWritableDatabase();
    HashSet localHashSet = new HashSet();
    Cursor localCursor = localSQLiteDatabase.query(ALBUM_TABLE_NAME, PROJECTION_ID_CACHE_FLAG_STATUS_THUMBNAIL, null, null, null, null, null);
    while (true)
    {
      long l;
      int k;
      try
      {
        do
        {
          if (!localCursor.moveToNext())
            break label176;
          boolean bool = paramPrefetchContext.syncInterrupted();
          if (bool)
            return;
          l = localCursor.getLong(0);
          int j = localCursor.getInt(1);
          k = localCursor.getInt(2);
          localHashSet.add(PicasaStoreFacade.getAlbumCoverKey(l, localCursor.getString(3)));
          if (j != 2)
            break label160;
        }
        while ((k == 3) || (k == 1));
      }
      finally
      {
        localCursor.close();
      }
      label160: if (k == 0)
        continue;
      updateAlbumCacheStatus(localSQLiteDatabase, l, 0);
    }
    label176: localCursor.close();
    PicasaStoreFacade.checkPrefetchVersion();
    deleteUnusedAlbumCovers(paramPrefetchContext, localHashSet);
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("cache_status", Integer.valueOf(0));
    localSQLiteDatabase.update(PHOTO_TABLE_NAME, localContentValues, "cache_status <> 0", null);
    HashMap localHashMap = new HashMap();
    collectKeepSetForFullImages(localSQLiteDatabase, localHashMap);
    collectKeepSetForScreenNails(localSQLiteDatabase, localHashMap);
    deleteUnusedCacheFiles(paramPrefetchContext, localHashMap);
    setCacheStatus(localSQLiteDatabase, localHashMap);
    MetricsUtils.end(i);
  }

  public PrefetchContext createPrefetchContext(SyncResult paramSyncResult, Thread paramThread)
  {
    return new PrefetchContext(paramSyncResult, paramThread);
  }

  public File getAlbumCover(long paramLong, String paramString)
    throws IOException
  {
    File localFile = getAlbumCoverCacheFile(paramLong, paramString, ".thumb");
    if (localFile.isFile())
      return localFile;
    return null;
  }

  public CacheStats getCacheStatistics(int paramInt)
  {
    SQLiteDatabase localSQLiteDatabase = this.mDbHelper.getReadableDatabase();
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(paramInt);
    Cursor localCursor = localSQLiteDatabase.rawQuery(QUERY_CACHE_STATUS_COUNT, arrayOfString);
    CacheStats localCacheStats = new CacheStats();
    if (localCursor == null)
      return localCacheStats;
    while (true)
    {
      int i;
      try
      {
        if (!localCursor.moveToNext())
          break label119;
        i = localCursor.getInt(0);
        if (localCursor.getInt(1) != 0)
          localCacheStats.pendingCount = (i + localCacheStats.pendingCount);
      }
      finally
      {
        localCursor.close();
      }
    }
    label119: localCursor.close();
    return localCacheStats;
  }

  public void notifyAlbumsChange()
  {
    this.mContext.getContentResolver().notifyChange(PicasaFacade.get(this.mContext).getAlbumsUri(), null, false);
  }

  public void setAlbumCachingFlag(long paramLong, int paramInt)
  {
    switch (paramInt)
    {
    default:
    case 0:
    case 1:
    case 2:
    }
    ContentValues localContentValues;
    String[] arrayOfString;
    do
    {
      return;
      localContentValues = new ContentValues();
      localContentValues.put("cache_flag", Integer.valueOf(paramInt));
      arrayOfString = new String[1];
      arrayOfString[0] = String.valueOf(paramLong);
    }
    while (this.mDbHelper.getWritableDatabase().update(ALBUM_TABLE_NAME, localContentValues, "_id=?", arrayOfString) <= 0);
    this.mCacheConfigVersion.incrementAndGet();
    notifyAlbumsChange();
    PicasaSyncManager.get(this.mContext).requestPrefetchSync();
  }

  public void syncAlbumCoversForUser(PrefetchContext paramPrefetchContext, UserEntry paramUserEntry)
    throws IOException
  {
    File localFile = new File(getCacheDirectory(), "picasa_covers");
    if ((!localFile.isDirectory()) && (!localFile.mkdirs()))
    {
      Log.e("PrefetchHelper", "cannot create album-cover folder");
      return;
    }
    SQLiteDatabase localSQLiteDatabase = this.mDbHelper.getWritableDatabase();
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(paramUserEntry.id);
    Cursor localCursor = localSQLiteDatabase.query(ALBUM_TABLE_NAME, PROJECTION_ID_THUMBNAIL_URL, "user_id=?", arrayOfString, null, null, null);
    while (true)
    {
      long l;
      String str;
      try
      {
        do
        {
          if (!localCursor.moveToNext())
            break label170;
          boolean bool = paramPrefetchContext.syncInterrupted();
          if (bool)
            return;
          l = localCursor.getLong(0);
          str = localCursor.getString(1);
        }
        while (getAlbumCover(l, str) != null);
      }
      finally
      {
        localCursor.close();
      }
    }
    label170: localCursor.close();
  }

  public void syncFullImagesForUser(PrefetchContext paramPrefetchContext, UserEntry paramUserEntry)
    throws IOException
  {
    SQLiteDatabase localSQLiteDatabase = this.mDbHelper.getWritableDatabase();
    String[] arrayOfString = new String[2];
    arrayOfString[0] = String.valueOf(paramUserEntry.id);
    arrayOfString[1] = String.valueOf(2);
    Cursor localCursor = localSQLiteDatabase.query(ALBUM_TABLE_NAME, PROJECTION_ID, WHERE_USER_ID_AND_CACHE_FLAG, arrayOfString, null, null, null);
    while (true)
      try
      {
        if (!localCursor.moveToNext())
          break label110;
        boolean bool = paramPrefetchContext.syncInterrupted();
        if (bool)
          return;
      }
      finally
      {
        localCursor.close();
      }
    label110: localCursor.close();
  }

  public void syncScreenNailsForUser(PrefetchContext paramPrefetchContext, UserEntry paramUserEntry)
    throws IOException
  {
    String[] arrayOfString = new String[2];
    arrayOfString[0] = String.valueOf(1);
    arrayOfString[1] = String.valueOf(paramUserEntry.id);
    Cursor localCursor = this.mDbHelper.getWritableDatabase().query(PHOTO_TABLE_NAME, PROJECTION_ID_SCREENNAIL_URL, WHERE_CACHE_STATUS_AND_USER_ID, arrayOfString, null, null, "display_index");
    try
    {
      do
        if (localCursor.moveToNext())
        {
          if (!paramPrefetchContext.checkCacheConfigVersion())
          {
            Log.w("PrefetchHelper", "cache config has changed, stop sync");
            paramPrefetchContext.stopSync();
          }
          boolean bool = paramPrefetchContext.syncInterrupted();
          if (bool)
            return;
        }
      while (syncOnePhoto(paramPrefetchContext, localCursor.getLong(0), PicasaApi.convertImageUrl(localCursor.getString(1), Config.sScreenNailSize, false), ".screen"));
      throw new RuntimeException("too many fail downloads");
    }
    finally
    {
      Utils.closeSilently(localCursor);
    }
    Utils.closeSilently(localCursor);
  }

  public static class CacheStats
  {
    public int failedCount;
    public int pendingCount;
    public int totalCount;
  }

  public class PrefetchContext
  {
    private PrefetchHelper.PrefetchListener mCacheListener;
    private int mDownloadFailCount;
    private int mLastVersion;
    private volatile boolean mStopSync;
    private Thread mThread;
    public SyncResult result;

    public PrefetchContext(SyncResult paramThread, Thread arg3)
    {
      this.result = ((SyncResult)Utils.checkNotNull(paramThread));
      Object localObject;
      this.mThread = localObject;
    }

    public boolean checkCacheConfigVersion()
    {
      return this.mLastVersion == PrefetchHelper.this.mCacheConfigVersion.get();
    }

    public int getDownloadFailCount()
    {
      return this.mDownloadFailCount;
    }

    public void onDownloadFinish(long paramLong, boolean paramBoolean)
    {
      if (paramBoolean);
      for (int i = 0; ; i = 1 + this.mDownloadFailCount)
      {
        this.mDownloadFailCount = i;
        if (this.mCacheListener != null)
          this.mCacheListener.onDownloadFinish(paramLong, paramBoolean);
        return;
      }
    }

    public void setCacheDownloadListener(PrefetchHelper.PrefetchListener paramPrefetchListener)
    {
      this.mCacheListener = paramPrefetchListener;
    }

    public void stopSync()
    {
      this.mStopSync = true;
      this.mThread.interrupt();
    }

    public boolean syncInterrupted()
    {
      return this.mStopSync;
    }

    public void updateCacheConfigVersion()
    {
      this.mLastVersion = PrefetchHelper.this.mCacheConfigVersion.get();
    }
  }

  public static abstract interface PrefetchListener
  {
    public abstract void onDownloadFinish(long paramLong, boolean paramBoolean);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.PrefetchHelper
 * JD-Core Version:    0.5.4
 */