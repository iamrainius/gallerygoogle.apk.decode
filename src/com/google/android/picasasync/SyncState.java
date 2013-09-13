package com.google.android.picasasync;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.android.gallery3d.common.EntrySchema;

public class SyncState
{
  public static final SyncState METADATA = new SyncState(0);
  public static final SyncState METADATA_MANUAL = new SyncState(1);
  public static final SyncState PREFETCH_ALBUM_COVER;
  public static final SyncState PREFETCH_FULL_IMAGE;
  public static final SyncState PREFETCH_SCREEN_NAIL = new SyncState(2);
  private static final String[] STATE_PROJECTION;
  private static final String USER_TABLE_NAME;
  private static final ContentValues sValues;
  private static final String[] sWhereArgs;
  private final int mOffset;

  static
  {
    PREFETCH_FULL_IMAGE = new SyncState(3);
    PREFETCH_ALBUM_COVER = new SyncState(4);
    USER_TABLE_NAME = UserEntry.SCHEMA.getTableName();
    STATE_PROJECTION = new String[] { "sync_states" };
    sWhereArgs = new String[1];
    sValues = new ContentValues();
  }

  private SyncState(int paramInt)
  {
    this.mOffset = (paramInt * 2);
  }

  private boolean compareAndSet(SQLiteDatabase paramSQLiteDatabase, String paramString, int paramInt1, int paramInt2)
  {
    monitorenter;
    try
    {
      int i = getStates(paramSQLiteDatabase, paramString);
      if ((i != -1) && ((0x3 & i >> this.mOffset) == paramInt1))
      {
        writeStates(paramSQLiteDatabase, paramString, i, paramInt2);
        return true;
      }
      return false;
    }
    finally
    {
      monitorexit;
    }
  }

  private static int getStates(SQLiteDatabase paramSQLiteDatabase, String paramString)
  {
    sWhereArgs[0] = paramString;
    Cursor localCursor = paramSQLiteDatabase.query(USER_TABLE_NAME, STATE_PROJECTION, "account=?", sWhereArgs, null, null, null, "1");
    while (true)
      try
      {
        if (localCursor.moveToNext())
        {
          int j = localCursor.getInt(0);
          i = j;
          return i;
        }
        int i = -1;
      }
      finally
      {
        localCursor.close();
      }
  }

  private void writeStates(SQLiteDatabase paramSQLiteDatabase, String paramString, int paramInt1, int paramInt2)
  {
    sWhereArgs[0] = paramString;
    int i = paramInt1 & (0xFFFFFFFF ^ 3 << this.mOffset) | paramInt2 << this.mOffset;
    sValues.put("sync_states", Integer.valueOf(i));
    paramSQLiteDatabase.beginTransaction();
    try
    {
      paramSQLiteDatabase.update(USER_TABLE_NAME, sValues, "account=?", sWhereArgs);
      paramSQLiteDatabase.setTransactionSuccessful();
      return;
    }
    finally
    {
      paramSQLiteDatabase.endTransaction();
    }
  }

  public int getState(SQLiteDatabase paramSQLiteDatabase, String paramString)
  {
    return 0x3 & getStates(paramSQLiteDatabase, paramString) >> this.mOffset;
  }

  public boolean isRequested(SQLiteDatabase paramSQLiteDatabase, String paramString)
  {
    monitorenter;
    while (true)
    {
      try
      {
        int i = getStates(paramSQLiteDatabase, paramString);
        if ((i != -1) && ((0x3 & i >> this.mOffset) == 2))
        {
          j = 1;
          return j;
        }
      }
      finally
      {
        monitorexit;
      }
      int j = 0;
    }
  }

  public void onSyncFinish(SQLiteDatabase paramSQLiteDatabase, String paramString)
  {
    compareAndSet(paramSQLiteDatabase, paramString, 1, 0);
  }

  public boolean onSyncRequested(SQLiteDatabase paramSQLiteDatabase, String paramString)
  {
    monitorenter;
    try
    {
      int i = getStates(paramSQLiteDatabase, paramString);
      if ((i != -1) && ((0x3 & i >> this.mOffset) != 2))
      {
        writeStates(paramSQLiteDatabase, paramString, i, 2);
        return true;
      }
      return false;
    }
    finally
    {
      monitorexit;
    }
  }

  public boolean onSyncStart(SQLiteDatabase paramSQLiteDatabase, String paramString)
  {
    return compareAndSet(paramSQLiteDatabase, paramString, 2, 1);
  }

  public void resetSyncToDirty(SQLiteDatabase paramSQLiteDatabase, String paramString)
  {
    compareAndSet(paramSQLiteDatabase, paramString, 1, 2);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.SyncState
 * JD-Core Version:    0.5.4
 */