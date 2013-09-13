package com.google.android.apps.lightcycle.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.google.android.apps.lightcycle.util.LG;
import java.util.ArrayList;
import java.util.List;

public class LocalDatabase extends SQLiteOpenHelper
{
  public LocalDatabase(Context paramContext)
  {
    super(paramContext, "panorama_storage.db", null, 1);
  }

  void addSession(LocalSessionEntry paramLocalSessionEntry)
  {
    deleteSession(paramLocalSessionEntry.id);
    LG.d("Adding with creator version : " + paramLocalSessionEntry.creatorVersion);
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.setLockingEnabled(true);
    try
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("id", paramLocalSessionEntry.id);
      localContentValues.put("stitched_exist", Boolean.valueOf(paramLocalSessionEntry.stitchedExists));
      localContentValues.put("thumb_exist", Boolean.valueOf(paramLocalSessionEntry.thumbnailExists));
      localContentValues.put("stitched_file", paramLocalSessionEntry.stitchedFile);
      localContentValues.put("thumb_file", paramLocalSessionEntry.thumbnailFile);
      localContentValues.put("meta_file", paramLocalSessionEntry.metadataFile);
      localContentValues.put("capture_dir", paramLocalSessionEntry.captureDirectory);
      localContentValues.put("app_version", paramLocalSessionEntry.creatorVersion);
      localSQLiteDatabase.insert("SESSIONS", null, localContentValues);
      return;
    }
    finally
    {
      localSQLiteDatabase.close();
    }
  }

  public boolean deleteSession(String paramString)
  {
    boolean bool = true;
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.setLockingEnabled(bool);
    try
    {
      int i = localSQLiteDatabase.delete("SESSIONS", "id = ?", new String[] { paramString });
      if (i > 0)
        return bool;
      bool = false;
    }
    finally
    {
      localSQLiteDatabase.close();
    }
  }

  public LocalSessionEntry getSession(String paramString)
  {
    SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
    localSQLiteDatabase.setLockingEnabled(true);
    int j;
    try
    {
      Cursor localCursor = localSQLiteDatabase.query("SESSIONS", new String[] { "id", "stitched_exist", "thumb_exist", "stitched_file", "thumb_file", "meta_file", "capture_dir", "app_version" }, "id=?", new String[] { paramString }, null, null, null, null);
      if (localCursor == null)
      {
        Log.e("LightCycle", "Session not found." + paramString);
        return null;
      }
      if (!localCursor.moveToFirst())
      {
        Log.e("LightCycle", "Session data is empty : " + paramString);
        return null;
      }
      LocalSessionEntry localLocalSessionEntry = new LocalSessionEntry();
      localLocalSessionEntry.id = localCursor.getString(localCursor.getColumnIndex("id"));
      if (localCursor.getInt(localCursor.getColumnIndex("stitched_exist")) != 0);
      for (int i = 1; ; i = 0)
      {
        localLocalSessionEntry.stitchedExists = i;
        if (localCursor.getInt(localCursor.getColumnIndex("thumb_exist")) == 0)
          break;
        j = 1;
        localLocalSessionEntry.thumbnailExists = j;
        localLocalSessionEntry.stitchedFile = localCursor.getString(localCursor.getColumnIndex("stitched_file"));
        localLocalSessionEntry.thumbnailFile = localCursor.getString(localCursor.getColumnIndex("thumb_file"));
        localLocalSessionEntry.metadataFile = localCursor.getString(localCursor.getColumnIndex("meta_file"));
        localLocalSessionEntry.captureDirectory = localCursor.getString(localCursor.getColumnIndex("capture_dir"));
        localLocalSessionEntry.creatorVersion = localCursor.getString(localCursor.getColumnIndex("app_version"));
        return localLocalSessionEntry;
      }
    }
    finally
    {
      localSQLiteDatabase.close();
    }
  }

  public List<String> getSessionIdList()
  {
    ArrayList localArrayList = new ArrayList();
    SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
    localSQLiteDatabase.setLockingEnabled(true);
    try
    {
      Cursor localCursor = localSQLiteDatabase.rawQuery("SELECT  * FROM SESSIONS", null);
      if (localCursor == null)
        return localArrayList;
      if (localCursor.moveToFirst())
      {
        boolean bool;
        do
        {
          new String();
          localArrayList.add(localCursor.getString(localCursor.getColumnIndex("id")));
          bool = localCursor.moveToNext();
        }
        while (bool);
      }
      return localArrayList;
    }
    finally
    {
      localSQLiteDatabase.close();
    }
  }

  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.setLockingEnabled(true);
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS SESSIONS;");
    paramSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS SESSIONS (id TEXT PRIMARY KEY NOT NULL,stitched_exist INTEGER NOT NULL,thumb_exist INTEGER NOT NULL,stitched_file TEXT,thumb_file TEXT,meta_file TEXT,capture_dir TEXT,app_version TEXT);");
  }

  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    onCreate(paramSQLiteDatabase);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.storage.LocalDatabase
 * JD-Core Version:    0.5.4
 */