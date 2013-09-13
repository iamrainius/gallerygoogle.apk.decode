package com.google.android.picasasync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import com.android.gallery3d.common.EntrySchema;
import java.util.HashMap;

final class PicasaDatabaseHelper extends SQLiteOpenHelper
{
  private static final String ALBUM_ENTRY_PROJECTION_LIST;
  private static final String ALBUM_ENTRY_QUERY;
  private static final String ALBUM_TABLE;
  private static final String CAMERA_SYNC_ALBUM_QUERY;
  private static final String USER_ACCOUNT_QUERY;
  private static final String USER_ID_QUERY;
  private static final String USER_TABLE = UserEntry.SCHEMA.getTableName();
  public static PicasaDatabaseHelper sInstance;
  private HashMap<String, String> mCachedAlbumId = new HashMap();
  private Context mContext;

  static
  {
    ALBUM_TABLE = AlbumEntry.SCHEMA.getTableName();
    USER_ID_QUERY = "select _id from " + USER_TABLE + " where " + "account" + "='%s' LIMIT 1";
    CAMERA_SYNC_ALBUM_QUERY = "select %s from " + ALBUM_TABLE + " where " + "user_id" + "=(" + USER_ID_QUERY + ") AND " + "album_type" + "='" + "InstantUpload" + "' LIMIT 1";
    ALBUM_ENTRY_PROJECTION_LIST = TextUtils.join(",", AlbumEntry.SCHEMA.getProjection());
    ALBUM_ENTRY_QUERY = "select " + ALBUM_ENTRY_PROJECTION_LIST + " from " + ALBUM_TABLE + " where _id=(%s) LIMIT 1";
    USER_ACCOUNT_QUERY = "select " + "account" + " from " + USER_TABLE + " where _id=%s LIMIT 1";
  }

  private PicasaDatabaseHelper(Context paramContext)
  {
    super(paramContext.getApplicationContext(), "picasa.db", null, 107);
    this.mContext = paramContext.getApplicationContext();
  }

  public static PicasaDatabaseHelper get(Context paramContext)
  {
    monitorenter;
    try
    {
      if (sInstance == null)
        sInstance = new PicasaDatabaseHelper(paramContext);
      PicasaDatabaseHelper localPicasaDatabaseHelper = sInstance;
      return localPicasaDatabaseHelper;
    }
    finally
    {
      monitorexit;
    }
  }

  AlbumEntry getAlbumEntry(String paramString1, String paramString2)
  {
    String str2;
    Object[] arrayOfObject;
    if ("camera-sync".equals(paramString2))
    {
      str2 = CAMERA_SYNC_ALBUM_QUERY;
      arrayOfObject = new Object[2];
      arrayOfObject[0] = ALBUM_ENTRY_PROJECTION_LIST;
      arrayOfObject[1] = paramString1;
    }
    Cursor localCursor;
    for (String str1 = String.format(str2, arrayOfObject); ; str1 = String.format(ALBUM_ENTRY_QUERY, new Object[] { paramString2 }))
    {
      localCursor = getReadableDatabase().rawQuery(str1, null);
      if (localCursor != null)
        break;
      return null;
    }
    try
    {
      if (localCursor.moveToNext())
      {
        AlbumEntry localAlbumEntry = (AlbumEntry)AlbumEntry.SCHEMA.cursorToObject(localCursor, new AlbumEntry());
        return localAlbumEntry;
      }
      return null;
    }
    finally
    {
      localCursor.close();
    }
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
      this.mContext.deleteDatabase("picasa.db");
      localSQLiteDatabase1 = super.getReadableDatabase();
    }
    finally
    {
      monitorexit;
    }
  }

  String getRealAlbumId(String paramString1, String paramString2)
  {
    if ("camera-sync".equals(paramString2))
    {
      String str1 = (String)this.mCachedAlbumId.get(paramString1);
      String str3;
      if (str1 != null)
        str3 = str1;
      Cursor localCursor;
      do
      {
        return str3;
        String str2 = String.format(CAMERA_SYNC_ALBUM_QUERY, new Object[] { "_id", paramString1 });
        localCursor = getReadableDatabase().rawQuery(str2, null);
        str3 = null;
      }
      while (localCursor == null);
      try
      {
        if (localCursor.moveToFirst())
        {
          String str4 = localCursor.getString(0);
          this.mCachedAlbumId.put(paramString1, str4);
          return str4;
        }
        return null;
      }
      finally
      {
        localCursor.close();
      }
    }
    return paramString2;
  }

  String getUserAccount(long paramLong)
  {
    String str1 = USER_ACCOUNT_QUERY;
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = String.valueOf(paramLong);
    String str2 = String.format(str1, arrayOfObject);
    Cursor localCursor = getReadableDatabase().rawQuery(str2, null);
    if (localCursor == null)
      return null;
    try
    {
      if (localCursor.moveToNext())
      {
        String str3 = localCursor.getString(0);
        return str3;
      }
      return null;
    }
    finally
    {
      localCursor.close();
    }
  }

  long getUserId(String paramString)
  {
    String str = String.format(USER_ID_QUERY, new Object[] { paramString });
    Cursor localCursor = getReadableDatabase().rawQuery(str, null);
    if (localCursor == null)
      return -1L;
    try
    {
      if (localCursor.moveToNext())
      {
        long l = localCursor.getLong(0);
        return l;
      }
      return -1L;
    }
    finally
    {
      localCursor.close();
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
      this.mContext.deleteDatabase("picasa.db");
      localSQLiteDatabase1 = super.getWritableDatabase();
    }
    finally
    {
      monitorexit;
    }
  }

  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    PhotoEntry.SCHEMA.createTables(paramSQLiteDatabase);
    AlbumEntry.SCHEMA.createTables(paramSQLiteDatabase);
    UserEntry.SCHEMA.createTables(paramSQLiteDatabase);
    PicasaSyncManager.get(this.mContext).requestAccountSync();
  }

  public void onDowngrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    onUpgrade(paramSQLiteDatabase, paramInt1, paramInt2);
  }

  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    PhotoEntry.SCHEMA.dropTables(paramSQLiteDatabase);
    AlbumEntry.SCHEMA.dropTables(paramSQLiteDatabase);
    UserEntry.SCHEMA.dropTables(paramSQLiteDatabase);
    onCreate(paramSQLiteDatabase);
    PicasaSyncManager.get(this.mContext).requestMetadataSync(true);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.PicasaDatabaseHelper
 * JD-Core Version:    0.5.4
 */