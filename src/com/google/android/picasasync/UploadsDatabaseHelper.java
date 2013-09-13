package com.google.android.picasasync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.gallery3d.common.EntrySchema;

class UploadsDatabaseHelper extends SQLiteOpenHelper
{
  private static final String UPLOAD_RECORD_TABLE;
  private static final String UPLOAD_TASK_TABLE = UploadTaskEntry.SCHEMA.getTableName();
  private Context mContext;

  static
  {
    UPLOAD_RECORD_TABLE = UploadedEntry.SCHEMA.getTableName();
  }

  UploadsDatabaseHelper(Context paramContext)
  {
    super(paramContext.getApplicationContext(), "picasa.upload.db", null, 9);
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
      this.mContext.deleteDatabase("picasa.upload.db");
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
      this.mContext.deleteDatabase("picasa.upload.db");
      localSQLiteDatabase1 = super.getWritableDatabase();
    }
    finally
    {
      monitorexit;
    }
  }

  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    UploadTaskEntry.SCHEMA.createTables(paramSQLiteDatabase);
    UploadedEntry.SCHEMA.createTables(paramSQLiteDatabase);
  }

  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    UploadTaskEntry.SCHEMA.dropTables(paramSQLiteDatabase);
    UploadedEntry.SCHEMA.dropTables(paramSQLiteDatabase);
    onCreate(paramSQLiteDatabase);
  }

  public void reset()
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.delete(UploadTaskEntry.SCHEMA.getTableName(), null, null);
    localSQLiteDatabase.delete(UploadedEntry.SCHEMA.getTableName(), null, null);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.UploadsDatabaseHelper
 * JD-Core Version:    0.5.4
 */