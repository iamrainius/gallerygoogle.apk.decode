package com.google.android.picasastore;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Fingerprint;
import com.android.gallery3d.common.Utils;

class FingerprintManager
{
  private static final String FINGERPRINT_TABLE = FingerprintEntry.SCHEMA.getTableName();
  private static final String[] PROJECTION_FINGERPRINT = { "fingerprint" };
  private static FingerprintManager sInstance;
  private final Context mContext;
  private final FingerprintDatabaseHelper mDbHelper;

  private FingerprintManager(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mDbHelper = new FingerprintDatabaseHelper(this.mContext);
  }

  public static FingerprintManager get(Context paramContext)
  {
    monitorenter;
    try
    {
      if (sInstance == null)
        sInstance = new FingerprintManager(paramContext);
      FingerprintManager localFingerprintManager = sInstance;
      return localFingerprintManager;
    }
    finally
    {
      monitorexit;
    }
  }

  public Fingerprint getCachedFingerprint(String paramString)
  {
    Cursor localCursor = this.mDbHelper.getReadableDatabase().query(FINGERPRINT_TABLE, PROJECTION_FINGERPRINT, "content_uri=?", new String[] { paramString }, null, null, null);
    if (localCursor != null);
    try
    {
      if ((localCursor.moveToNext()) && (!localCursor.isNull(0)))
      {
        Fingerprint localFingerprint = new Fingerprint(localCursor.getBlob(0));
        return localFingerprint;
      }
      return null;
    }
    catch (Throwable localThrowable)
    {
      Log.w("FingerprintManager", "cannot get fingerprint from cache for: " + paramString, localThrowable);
      return null;
    }
    finally
    {
      Utils.closeSilently(localCursor);
    }
  }

  // ERROR //
  public Fingerprint getFingerprint(String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: invokevirtual 124	com/google/android/picasastore/FingerprintManager:getCachedFingerprint	(Ljava/lang/String;)Lcom/android/gallery3d/common/Fingerprint;
    //   7: astore 4
    //   9: aload 4
    //   11: astore 5
    //   13: iload_2
    //   14: ifne +13 -> 27
    //   17: aload 5
    //   19: ifnull +8 -> 27
    //   22: aload_0
    //   23: monitorexit
    //   24: aload 5
    //   26: areturn
    //   27: aload_1
    //   28: invokestatic 130	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   31: astore 8
    //   33: aload_0
    //   34: getfield 48	com/google/android/picasastore/FingerprintManager:mContext	Landroid/content/Context;
    //   37: invokevirtual 134	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   40: aload 8
    //   42: invokevirtual 140	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   45: aconst_null
    //   46: invokestatic 144	com/android/gallery3d/common/Fingerprint:fromInputStream	(Ljava/io/InputStream;[J)Lcom/android/gallery3d/common/Fingerprint;
    //   49: astore 9
    //   51: aload 5
    //   53: ifnull +13 -> 66
    //   56: aload 5
    //   58: aload 9
    //   60: invokevirtual 148	com/android/gallery3d/common/Fingerprint:equals	(Ljava/lang/Object;)Z
    //   63: ifne +27 -> 90
    //   66: getstatic 22	com/google/android/picasastore/FingerprintEntry:SCHEMA	Lcom/android/gallery3d/common/EntrySchema;
    //   69: aload_0
    //   70: getfield 54	com/google/android/picasastore/FingerprintManager:mDbHelper	Lcom/google/android/picasastore/FingerprintManager$FingerprintDatabaseHelper;
    //   73: invokevirtual 151	com/google/android/picasastore/FingerprintManager$FingerprintDatabaseHelper:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   76: new 18	com/google/android/picasastore/FingerprintEntry
    //   79: dup
    //   80: aload_1
    //   81: aload 9
    //   83: invokespecial 154	com/google/android/picasastore/FingerprintEntry:<init>	(Ljava/lang/String;Lcom/android/gallery3d/common/Fingerprint;)V
    //   86: invokevirtual 158	com/android/gallery3d/common/EntrySchema:insertOrReplace	(Landroid/database/sqlite/SQLiteDatabase;Lcom/android/gallery3d/common/Entry;)J
    //   89: pop2
    //   90: aload 9
    //   92: astore 5
    //   94: goto -72 -> 22
    //   97: astore 6
    //   99: ldc 102
    //   101: new 104	java/lang/StringBuilder
    //   104: dup
    //   105: invokespecial 105	java/lang/StringBuilder:<init>	()V
    //   108: ldc 160
    //   110: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   113: aload_1
    //   114: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   117: invokevirtual 114	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   120: aload 6
    //   122: invokestatic 163	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   125: pop
    //   126: aconst_null
    //   127: astore 5
    //   129: goto -107 -> 22
    //   132: astore 10
    //   134: ldc 102
    //   136: new 104	java/lang/StringBuilder
    //   139: dup
    //   140: invokespecial 105	java/lang/StringBuilder:<init>	()V
    //   143: ldc 165
    //   145: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   148: aload_1
    //   149: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   152: invokevirtual 114	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   155: aload 10
    //   157: invokestatic 120	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   160: pop
    //   161: goto -71 -> 90
    //   164: astore_3
    //   165: aload_0
    //   166: monitorexit
    //   167: aload_3
    //   168: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   27	51	97	java/lang/Throwable
    //   56	66	132	java/lang/Throwable
    //   66	90	132	java/lang/Throwable
    //   2	9	164	finally
    //   27	51	164	finally
    //   56	66	164	finally
    //   66	90	164	finally
    //   99	126	164	finally
    //   134	161	164	finally
  }

  public int invalidate(String[] paramArrayOfString)
  {
    monitorenter;
    try
    {
      int i = this.mDbHelper.getWritableDatabase().delete(FINGERPRINT_TABLE, "content_uri=?", paramArrayOfString);
      monitorexit;
      return i;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public void reset()
  {
    this.mDbHelper.getWritableDatabase().delete(FingerprintEntry.SCHEMA.getTableName(), null, null);
  }

  private static class FingerprintDatabaseHelper extends SQLiteOpenHelper
  {
    private Context mContext;

    FingerprintDatabaseHelper(Context paramContext)
    {
      super(paramContext.getApplicationContext(), "fingerprint.db", null, 1);
      this.mContext = paramContext;
    }

    public SQLiteDatabase getReadableDatabase()
    {
      monitorenter;
      Object localObject2;
      SQLiteDatabase localSQLiteDatabase1;
      try
      {
        SQLiteDatabase localSQLiteDatabase2 = super.getReadableDatabase();
        localObject2 = localSQLiteDatabase2;
        return localObject2;
      }
      catch (Throwable localThrowable)
      {
        this.mContext.deleteDatabase("fingerprint.db");
        localSQLiteDatabase1 = super.getReadableDatabase();
      }
      finally
      {
        monitorexit;
      }
    }

    public SQLiteDatabase getWritableDatabase()
    {
      monitorenter;
      Object localObject2;
      SQLiteDatabase localSQLiteDatabase1;
      try
      {
        SQLiteDatabase localSQLiteDatabase2 = super.getWritableDatabase();
        localObject2 = localSQLiteDatabase2;
        return localObject2;
      }
      catch (Throwable localThrowable)
      {
        this.mContext.deleteDatabase("fingerprint.db");
        localSQLiteDatabase1 = super.getWritableDatabase();
      }
      finally
      {
        monitorexit;
      }
    }

    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      FingerprintEntry.SCHEMA.createTables(paramSQLiteDatabase);
    }

    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
      FingerprintEntry.SCHEMA.dropTables(paramSQLiteDatabase);
      onCreate(paramSQLiteDatabase);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasastore.FingerprintManager
 * JD-Core Version:    0.5.4
 */