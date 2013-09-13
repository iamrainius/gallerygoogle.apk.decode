package com.android.gallery3d.gadget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.util.Log;
import com.android.gallery3d.common.Utils;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WidgetDatabaseHelper extends SQLiteOpenHelper
{
  private static final String[] PROJECTION = { "widgetType", "imageUri", "photoBlob", "albumPath", "appWidgetId" };

  public WidgetDatabaseHelper(Context paramContext)
  {
    super(paramContext, "launcher.db", null, 4);
  }

  private void restoreData(SQLiteDatabase paramSQLiteDatabase, ArrayList<Entry> paramArrayList)
  {
    paramSQLiteDatabase.beginTransaction();
    ContentValues localContentValues;
    try
    {
      Iterator localIterator = paramArrayList.iterator();
      if (!localIterator.hasNext())
        break label127;
      Entry localEntry = (Entry)localIterator.next();
      localContentValues = new ContentValues();
      localContentValues.put("appWidgetId", Integer.valueOf(localEntry.widgetId));
      localContentValues.put("widgetType", Integer.valueOf(localEntry.type));
      localContentValues.put("imageUri", localEntry.imageUri);
      localContentValues.put("photoBlob", localEntry.imageData);
      localContentValues.put("albumPath", localEntry.albumPath);
    }
    finally
    {
      paramSQLiteDatabase.endTransaction();
    }
    label127: paramSQLiteDatabase.setTransactionSuccessful();
    paramSQLiteDatabase.endTransaction();
  }

  private void saveData(SQLiteDatabase paramSQLiteDatabase, int paramInt, ArrayList<Entry> paramArrayList)
  {
    Cursor localCursor2;
    if (paramInt <= 2)
    {
      localCursor2 = paramSQLiteDatabase.query("photos", new String[] { "appWidgetId", "photoBlob" }, null, null, null, null, null);
      if (localCursor2 != null);
    }
    label100: Cursor localCursor1;
    do
    {
      do
      {
        return;
        while (true)
          try
          {
            if (!localCursor2.moveToNext())
              break label100;
            Entry localEntry2 = new Entry(null);
            localEntry2.type = 0;
            localEntry2.widgetId = localCursor2.getInt(0);
            localEntry2.imageData = localCursor2.getBlob(1);
            paramArrayList.add(localEntry2);
          }
          finally
          {
            localCursor2.close();
          }
        return;
      }
      while (paramInt != 3);
      localCursor1 = paramSQLiteDatabase.query("photos", new String[] { "appWidgetId", "photoBlob", "imageUri" }, null, null, null, null, null);
    }
    while (localCursor1 == null);
    while (true)
      try
      {
        if (!localCursor1.moveToNext())
          break label237;
        Entry localEntry1 = new Entry(null);
        localEntry1.type = 0;
        localEntry1.widgetId = localCursor1.getInt(0);
        localEntry1.imageData = localCursor1.getBlob(1);
        localEntry1.imageUri = localCursor1.getString(2);
        label237: paramArrayList.add(localEntry1);
      }
      finally
      {
        localCursor1.close();
      }
  }

  public void deleteEntry(int paramInt)
  {
    try
    {
      SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
      String[] arrayOfString = new String[1];
      arrayOfString[0] = String.valueOf(paramInt);
      localSQLiteDatabase.delete("widgets", "appWidgetId = ?", arrayOfString);
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      Log.e("PhotoDatabaseHelper", "Could not delete photo from database", localSQLiteException);
    }
  }

  public List<Entry> getEntries(int paramInt)
  {
    Cursor localCursor = null;
    ArrayList localArrayList;
    try
    {
      SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
      String[] arrayOfString1 = PROJECTION;
      String[] arrayOfString2 = new String[1];
      arrayOfString2[0] = String.valueOf(paramInt);
      localCursor = localSQLiteDatabase.query("widgets", arrayOfString1, "widgetType = ?", arrayOfString2, null, null, null);
      if (localCursor == null)
      {
        Log.e("PhotoDatabaseHelper", "query fail: null cursor: " + localCursor);
        return null;
      }
      localArrayList = new ArrayList(localCursor.getCount());
      label139: if (!localCursor.moveToNext())
        break label139;
    }
    catch (Throwable localThrowable)
    {
      Log.e("PhotoDatabaseHelper", "Could not load widget from database", localThrowable);
      return null;
      return localArrayList;
    }
    finally
    {
      Utils.closeSilently(localCursor);
    }
  }

  public Entry getEntry(int paramInt)
  {
    Cursor localCursor = null;
    try
    {
      SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
      String[] arrayOfString1 = PROJECTION;
      String[] arrayOfString2 = new String[1];
      arrayOfString2[0] = String.valueOf(paramInt);
      localCursor = localSQLiteDatabase.query("widgets", arrayOfString1, "appWidgetId = ?", arrayOfString2, null, null, null);
      if ((localCursor == null) || (!localCursor.moveToNext()))
      {
        Log.e("PhotoDatabaseHelper", "query fail: empty cursor: " + localCursor);
        return null;
      }
      Entry localEntry = new Entry(paramInt, localCursor, null);
      return localEntry;
    }
    catch (Throwable localThrowable)
    {
      Log.e("PhotoDatabaseHelper", "Could not load photo from database", localThrowable);
      return null;
    }
    finally
    {
      Utils.closeSilently(localCursor);
    }
  }

  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("CREATE TABLE widgets (appWidgetId INTEGER PRIMARY KEY, widgetType INTEGER DEFAULT 0, imageUri TEXT, albumPath TEXT, photoBlob BLOB)");
  }

  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    if (paramInt1 == 4)
      return;
    ArrayList localArrayList = new ArrayList();
    saveData(paramSQLiteDatabase, paramInt1, localArrayList);
    Log.w("PhotoDatabaseHelper", "destroying all old data.");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS photos");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS widgets");
    onCreate(paramSQLiteDatabase);
    restoreData(paramSQLiteDatabase, localArrayList);
  }

  public boolean setPhoto(int paramInt, Uri paramUri, Bitmap paramBitmap)
  {
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(4 * (paramBitmap.getWidth() * paramBitmap.getHeight()));
      paramBitmap.compress(Bitmap.CompressFormat.PNG, 100, localByteArrayOutputStream);
      localByteArrayOutputStream.close();
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("appWidgetId", Integer.valueOf(paramInt));
      localContentValues.put("widgetType", Integer.valueOf(0));
      localContentValues.put("imageUri", paramUri.toString());
      localContentValues.put("photoBlob", localByteArrayOutputStream.toByteArray());
      getWritableDatabase().replaceOrThrow("widgets", null, localContentValues);
      return true;
    }
    catch (Throwable localThrowable)
    {
      Log.e("PhotoDatabaseHelper", "set widget photo fail", localThrowable);
    }
    return false;
  }

  public boolean setWidget(int paramInt1, int paramInt2, String paramString)
  {
    try
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("appWidgetId", Integer.valueOf(paramInt1));
      localContentValues.put("widgetType", Integer.valueOf(paramInt2));
      localContentValues.put("albumPath", Utils.ensureNotNull(paramString));
      getWritableDatabase().replaceOrThrow("widgets", null, localContentValues);
      return true;
    }
    catch (Throwable localThrowable)
    {
      Log.e("PhotoDatabaseHelper", "set widget fail", localThrowable);
    }
    return false;
  }

  public void updateEntry(Entry paramEntry)
  {
    deleteEntry(paramEntry.widgetId);
    try
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("appWidgetId", Integer.valueOf(paramEntry.widgetId));
      localContentValues.put("widgetType", Integer.valueOf(paramEntry.type));
      localContentValues.put("albumPath", paramEntry.albumPath);
      localContentValues.put("imageUri", paramEntry.imageUri);
      localContentValues.put("photoBlob", paramEntry.imageData);
      getWritableDatabase().insert("widgets", null, localContentValues);
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.e("PhotoDatabaseHelper", "set widget fail", localThrowable);
    }
  }

  public static class Entry
  {
    public String albumPath;
    public byte[] imageData;
    public String imageUri;
    public int type;
    public int widgetId;

    private Entry()
    {
    }

    private Entry(int paramInt, Cursor paramCursor)
    {
      this.widgetId = paramInt;
      this.type = paramCursor.getInt(0);
      if (this.type == 0)
      {
        this.imageUri = paramCursor.getString(1);
        this.imageData = paramCursor.getBlob(2);
      }
      do
        return;
      while (this.type != 2);
      this.albumPath = paramCursor.getString(3);
    }

    private Entry(Cursor paramCursor)
    {
      this(paramCursor.getInt(4), paramCursor);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.gadget.WidgetDatabaseHelper
 * JD-Core Version:    0.5.4
 */