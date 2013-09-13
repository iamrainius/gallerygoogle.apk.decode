package com.android.gallery3d.common;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;

public final class EntrySchema
{
  private static final String[] SQLITE_TYPES = { "TEXT", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "REAL", "REAL", "NONE" };
  private final ColumnInfo[] mColumnInfo;
  private final boolean mHasFullTextIndex;
  private final String[] mProjection;
  private final String mTableName;

  public EntrySchema(Class<? extends Entry> paramClass)
  {
    ColumnInfo[] arrayOfColumnInfo = parseColumnInfo(paramClass);
    this.mTableName = parseTableName(paramClass);
    this.mColumnInfo = arrayOfColumnInfo;
    String[] arrayOfString = new String[0];
    int i = 0;
    if (arrayOfColumnInfo != null)
    {
      arrayOfString = new String[arrayOfColumnInfo.length];
      for (int j = 0; j != arrayOfColumnInfo.length; ++j)
      {
        ColumnInfo localColumnInfo = arrayOfColumnInfo[j];
        arrayOfString[j] = localColumnInfo.name;
        if (!localColumnInfo.fullText)
          continue;
        i = 1;
      }
    }
    this.mProjection = arrayOfString;
    this.mHasFullTextIndex = i;
  }

  private void logExecSql(SQLiteDatabase paramSQLiteDatabase, String paramString)
  {
    paramSQLiteDatabase.execSQL(paramString);
  }

  private void parseColumnInfo(Class<? extends Object> paramClass, ArrayList<ColumnInfo> paramArrayList)
  {
    Field[] arrayOfField = paramClass.getDeclaredFields();
    Field localField;
    Entry.Column localColumn;
    for (int i = 0; ; ++i)
    {
      if (i == arrayOfField.length)
        return;
      localField = arrayOfField[i];
      localColumn = (Entry.Column)localField.getAnnotation(Entry.Column.class);
      label40: if (localColumn != null)
        break;
    }
    Class localClass = localField.getType();
    if (localClass == String.class);
    for (int j = 0; ; j = 7)
    {
      while (true)
      {
        int k = paramArrayList.size();
        paramArrayList.add(new ColumnInfo(localColumn.value(), j, localColumn.indexed(), localColumn.unique(), localColumn.fullText(), localColumn.defaultValue(), localField, k));
        break label40:
        if (localClass == Boolean.TYPE)
          j = 1;
        if (localClass == Short.TYPE)
          j = 2;
        if (localClass == Integer.TYPE)
          j = 3;
        if (localClass == Long.TYPE)
          j = 4;
        if (localClass == Float.TYPE)
          j = 5;
        if (localClass != Double.TYPE)
          break;
        j = 6;
      }
      if (localClass != [B.class)
        break;
    }
    throw new IllegalArgumentException("Unsupported field type for column: " + localClass.getName());
  }

  private ColumnInfo[] parseColumnInfo(Class<? extends Object> paramClass)
  {
    ArrayList localArrayList = new ArrayList();
    while (paramClass != null)
    {
      parseColumnInfo(paramClass, localArrayList);
      paramClass = paramClass.getSuperclass();
    }
    ColumnInfo[] arrayOfColumnInfo = new ColumnInfo[localArrayList.size()];
    localArrayList.toArray(arrayOfColumnInfo);
    return arrayOfColumnInfo;
  }

  private String parseTableName(Class<? extends Object> paramClass)
  {
    Entry.Table localTable = (Entry.Table)paramClass.getAnnotation(Entry.Table.class);
    if (localTable == null)
      return null;
    return localTable.value();
  }

  private void setIfNotNull(Field paramField, Object paramObject1, Object paramObject2)
    throws IllegalAccessException
  {
    if (paramObject2 == null)
      return;
    paramField.set(paramObject1, paramObject2);
  }

  public void createTables(SQLiteDatabase paramSQLiteDatabase)
  {
    String str1 = this.mTableName;
    boolean bool;
    label11: StringBuilder localStringBuilder1;
    StringBuilder localStringBuilder2;
    int j;
    label64: ColumnInfo localColumnInfo5;
    if (str1 != null)
    {
      bool = true;
      Utils.assertTrue(bool);
      localStringBuilder1 = new StringBuilder("CREATE TABLE ");
      localStringBuilder1.append(str1);
      localStringBuilder1.append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT");
      localStringBuilder2 = new StringBuilder();
      ColumnInfo[] arrayOfColumnInfo1 = this.mColumnInfo;
      int i = arrayOfColumnInfo1.length;
      j = 0;
      if (j >= i)
        break label215;
      localColumnInfo5 = arrayOfColumnInfo1[j];
      if (!localColumnInfo5.isId())
      {
        localStringBuilder1.append(',');
        localStringBuilder1.append(localColumnInfo5.name);
        localStringBuilder1.append(' ');
        localStringBuilder1.append(SQLITE_TYPES[localColumnInfo5.type]);
        if (!TextUtils.isEmpty(localColumnInfo5.defaultValue))
        {
          localStringBuilder1.append(" DEFAULT ");
          localStringBuilder1.append(localColumnInfo5.defaultValue);
        }
        if (localColumnInfo5.unique)
        {
          if (localStringBuilder2.length() != 0)
            break label196;
          localStringBuilder2.append(localColumnInfo5.name);
        }
      }
    }
    while (true)
    {
      ++j;
      break label64:
      bool = false;
      break label11:
      label196: localStringBuilder2.append(',').append(localColumnInfo5.name);
    }
    if (localStringBuilder2.length() > 0)
      label215: localStringBuilder1.append(",UNIQUE(").append(localStringBuilder2).append(')');
    localStringBuilder1.append(");");
    logExecSql(paramSQLiteDatabase, localStringBuilder1.toString());
    localStringBuilder1.setLength(0);
    for (ColumnInfo localColumnInfo4 : this.mColumnInfo)
    {
      if (!localColumnInfo4.indexed)
        continue;
      localStringBuilder1.append("CREATE INDEX ");
      localStringBuilder1.append(str1);
      localStringBuilder1.append("_index_");
      localStringBuilder1.append(localColumnInfo4.name);
      localStringBuilder1.append(" ON ");
      localStringBuilder1.append(str1);
      localStringBuilder1.append(" (");
      localStringBuilder1.append(localColumnInfo4.name);
      localStringBuilder1.append(");");
      logExecSql(paramSQLiteDatabase, localStringBuilder1.toString());
      localStringBuilder1.setLength(0);
    }
    if (!this.mHasFullTextIndex)
      return;
    String str2 = str1 + "_fulltext";
    localStringBuilder1.append("CREATE VIRTUAL TABLE ");
    localStringBuilder1.append(str2);
    localStringBuilder1.append(" USING FTS3 (_id INTEGER PRIMARY KEY");
    for (ColumnInfo localColumnInfo3 : this.mColumnInfo)
    {
      if (!localColumnInfo3.fullText)
        continue;
      String str4 = localColumnInfo3.name;
      localStringBuilder1.append(',');
      localStringBuilder1.append(str4);
      localStringBuilder1.append(" TEXT");
    }
    localStringBuilder1.append(");");
    logExecSql(paramSQLiteDatabase, localStringBuilder1.toString());
    localStringBuilder1.setLength(0);
    StringBuilder localStringBuilder3 = new StringBuilder("INSERT OR REPLACE INTO ");
    localStringBuilder3.append(str2);
    localStringBuilder3.append(" (_id");
    for (ColumnInfo localColumnInfo2 : this.mColumnInfo)
    {
      if (!localColumnInfo2.fullText)
        continue;
      localStringBuilder3.append(',');
      localStringBuilder3.append(localColumnInfo2.name);
    }
    localStringBuilder3.append(") VALUES (new._id");
    for (ColumnInfo localColumnInfo1 : this.mColumnInfo)
    {
      if (!localColumnInfo1.fullText)
        continue;
      localStringBuilder3.append(",new.");
      localStringBuilder3.append(localColumnInfo1.name);
    }
    localStringBuilder3.append(");");
    String str3 = localStringBuilder3.toString();
    localStringBuilder1.append("CREATE TRIGGER ");
    localStringBuilder1.append(str1);
    localStringBuilder1.append("_insert_trigger AFTER INSERT ON ");
    localStringBuilder1.append(str1);
    localStringBuilder1.append(" FOR EACH ROW BEGIN ");
    localStringBuilder1.append(str3);
    localStringBuilder1.append("END;");
    logExecSql(paramSQLiteDatabase, localStringBuilder1.toString());
    localStringBuilder1.setLength(0);
    localStringBuilder1.append("CREATE TRIGGER ");
    localStringBuilder1.append(str1);
    localStringBuilder1.append("_update_trigger AFTER UPDATE ON ");
    localStringBuilder1.append(str1);
    localStringBuilder1.append(" FOR EACH ROW BEGIN ");
    localStringBuilder1.append(str3);
    localStringBuilder1.append("END;");
    logExecSql(paramSQLiteDatabase, localStringBuilder1.toString());
    localStringBuilder1.setLength(0);
    localStringBuilder1.append("CREATE TRIGGER ");
    localStringBuilder1.append(str1);
    localStringBuilder1.append("_delete_trigger AFTER DELETE ON ");
    localStringBuilder1.append(str1);
    localStringBuilder1.append(" FOR EACH ROW BEGIN DELETE FROM ");
    localStringBuilder1.append(str2);
    localStringBuilder1.append(" WHERE _id = old._id; END;");
    logExecSql(paramSQLiteDatabase, localStringBuilder1.toString());
    localStringBuilder1.setLength(0);
  }

  public <T extends Entry> T cursorToObject(Cursor paramCursor, T paramT)
  {
    int j;
    label14: int k;
    Field localField;
    while (true)
    {
      try
      {
        ColumnInfo[] arrayOfColumnInfo = this.mColumnInfo;
        int i = arrayOfColumnInfo.length;
        j = 0;
        if (j >= i)
          break label293;
        ColumnInfo localColumnInfo = arrayOfColumnInfo[j];
        k = localColumnInfo.projectionIndex;
        localField = localColumnInfo.field;
        switch (localColumnInfo.type)
        {
        case 0:
          if (!paramCursor.isNull(k))
            break label127;
          localObject2 = null;
          localField.set(paramT, localObject2);
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        }
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new RuntimeException(localIllegalAccessException);
      }
      label127: Object localObject2 = paramCursor.getString(k);
    }
    if (paramCursor.getShort(k) == 1);
    for (boolean bool = true; ; bool = false)
    {
      localField.setBoolean(paramT, bool);
      break label295:
      localField.setShort(paramT, paramCursor.getShort(k));
      break label295:
      localField.setInt(paramT, paramCursor.getInt(k));
      break label295:
      localField.setLong(paramT, paramCursor.getLong(k));
      break label295:
      localField.setFloat(paramT, paramCursor.getFloat(k));
      break label295:
      localField.setDouble(paramT, paramCursor.getDouble(k));
      break label295:
      if (paramCursor.isNull(k));
      byte[] arrayOfByte;
      for (Object localObject1 = null; ; localObject1 = arrayOfByte)
      {
        localField.set(paramT, localObject1);
        break label295:
        arrayOfByte = paramCursor.getBlob(k);
      }
      label293: return paramT;
      label295: ++j;
      break label14:
    }
  }

  public boolean deleteWithId(SQLiteDatabase paramSQLiteDatabase, long paramLong)
  {
    String str = this.mTableName;
    String[] arrayOfString = new String[1];
    arrayOfString[0] = Long.toString(paramLong);
    return paramSQLiteDatabase.delete(str, "_id=?", arrayOfString) == 1;
  }

  public void dropTables(SQLiteDatabase paramSQLiteDatabase)
  {
    String str = this.mTableName;
    StringBuilder localStringBuilder = new StringBuilder("DROP TABLE IF EXISTS ");
    localStringBuilder.append(str);
    localStringBuilder.append(';');
    logExecSql(paramSQLiteDatabase, localStringBuilder.toString());
    localStringBuilder.setLength(0);
    if (!this.mHasFullTextIndex)
      return;
    localStringBuilder.append("DROP TABLE IF EXISTS ");
    localStringBuilder.append(str);
    localStringBuilder.append("_fulltext");
    localStringBuilder.append(';');
    logExecSql(paramSQLiteDatabase, localStringBuilder.toString());
  }

  public ColumnInfo getColumn(String paramString)
  {
    int i = getColumnIndex(paramString);
    if (i < 0)
      return null;
    return this.mColumnInfo[i];
  }

  public int getColumnIndex(String paramString)
  {
    for (ColumnInfo localColumnInfo : this.mColumnInfo)
      if (localColumnInfo.name.equals(paramString))
        return localColumnInfo.projectionIndex;
    return -1;
  }

  public String[] getProjection()
  {
    return this.mProjection;
  }

  public String getTableName()
  {
    return this.mTableName;
  }

  public long insertOrReplace(SQLiteDatabase paramSQLiteDatabase, Entry paramEntry)
  {
    ContentValues localContentValues = new ContentValues();
    objectToValues(paramEntry, localContentValues);
    if (paramEntry.id == 0L)
      localContentValues.remove("_id");
    long l = paramSQLiteDatabase.replace(this.mTableName, "_id", localContentValues);
    paramEntry.id = l;
    return l;
  }

  public void objectToValues(Entry paramEntry, ContentValues paramContentValues)
  {
    while (true)
    {
      int j;
      String str;
      Field localField;
      try
      {
        ColumnInfo[] arrayOfColumnInfo = this.mColumnInfo;
        int i = arrayOfColumnInfo.length;
        j = 0;
        if (j >= i)
          break label249;
        ColumnInfo localColumnInfo = arrayOfColumnInfo[j];
        str = localColumnInfo.name;
        localField = localColumnInfo.field;
        switch (localColumnInfo.type)
        {
        case 0:
          paramContentValues.put(str, (String)localField.get(paramEntry));
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        }
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new RuntimeException(localIllegalAccessException);
      }
      paramContentValues.put(str, Boolean.valueOf(localField.getBoolean(paramEntry)));
      break label250:
      paramContentValues.put(str, Short.valueOf(localField.getShort(paramEntry)));
      break label250:
      paramContentValues.put(str, Integer.valueOf(localField.getInt(paramEntry)));
      break label250:
      paramContentValues.put(str, Long.valueOf(localField.getLong(paramEntry)));
      break label250:
      paramContentValues.put(str, Float.valueOf(localField.getFloat(paramEntry)));
      break label250:
      paramContentValues.put(str, Double.valueOf(localField.getDouble(paramEntry)));
      break label250:
      paramContentValues.put(str, (byte[])(byte[])localField.get(paramEntry));
      break label250:
      label249: return;
      label250: ++j;
    }
  }

  public boolean queryWithId(SQLiteDatabase paramSQLiteDatabase, long paramLong, Entry paramEntry)
  {
    String str = this.mTableName;
    String[] arrayOfString1 = this.mProjection;
    String[] arrayOfString2 = new String[1];
    arrayOfString2[0] = Long.toString(paramLong);
    Cursor localCursor = paramSQLiteDatabase.query(str, arrayOfString1, "_id=?", arrayOfString2, null, null, null);
    boolean bool = localCursor.moveToFirst();
    int i = 0;
    if (bool)
    {
      cursorToObject(localCursor, paramEntry);
      i = 1;
    }
    localCursor.close();
    return i;
  }

  public String toDebugString(Entry paramEntry, String[] paramArrayOfString)
  {
    try
    {
      StringBuilder localStringBuilder1 = new StringBuilder();
      localStringBuilder1.append("ID=").append(paramEntry.id);
      int i = paramArrayOfString.length;
      int j = 0;
      if (j < i)
      {
        label30: String str1 = paramArrayOfString[j];
        Object localObject = getColumn(str1).field.get(paramEntry);
        StringBuilder localStringBuilder2 = localStringBuilder1.append(" ").append(str1).append("=");
        if (localObject == null);
        for (String str2 = "null"; ; str2 = localObject.toString())
        {
          localStringBuilder2.append(str2);
          ++j;
          break label30:
        }
      }
      String str3 = localStringBuilder1.toString();
      return str3;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
  }

  public <T extends Entry> T valuesToObject(ContentValues paramContentValues, T paramT)
  {
    while (true)
    {
      int j;
      String str;
      Field localField;
      try
      {
        ColumnInfo[] arrayOfColumnInfo = this.mColumnInfo;
        int i = arrayOfColumnInfo.length;
        j = 0;
        if (j >= i)
          break label230;
        ColumnInfo localColumnInfo = arrayOfColumnInfo[j];
        str = localColumnInfo.name;
        localField = localColumnInfo.field;
        switch (localColumnInfo.type)
        {
        case 0:
          setIfNotNull(localField, paramT, paramContentValues.getAsString(str));
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        }
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new RuntimeException(localIllegalAccessException);
      }
      setIfNotNull(localField, paramT, paramContentValues.getAsBoolean(str));
      break label232:
      setIfNotNull(localField, paramT, paramContentValues.getAsShort(str));
      break label232:
      setIfNotNull(localField, paramT, paramContentValues.getAsInteger(str));
      break label232:
      setIfNotNull(localField, paramT, paramContentValues.getAsLong(str));
      break label232:
      setIfNotNull(localField, paramT, paramContentValues.getAsFloat(str));
      break label232:
      setIfNotNull(localField, paramT, paramContentValues.getAsDouble(str));
      break label232:
      setIfNotNull(localField, paramT, paramContentValues.getAsByteArray(str));
      break label232:
      label230: return paramT;
      label232: ++j;
    }
  }

  public static final class ColumnInfo
  {
    public final String defaultValue;
    public final Field field;
    public final boolean fullText;
    public final boolean indexed;
    public final String name;
    public final int projectionIndex;
    public final int type;
    public final boolean unique;

    public ColumnInfo(String paramString1, int paramInt1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString2, Field paramField, int paramInt2)
    {
      this.name = paramString1.toLowerCase();
      this.type = paramInt1;
      this.indexed = paramBoolean1;
      this.unique = paramBoolean2;
      this.fullText = paramBoolean3;
      this.defaultValue = paramString2;
      this.field = paramField;
      this.projectionIndex = paramInt2;
      paramField.setAccessible(true);
    }

    public boolean isId()
    {
      return "_id".equals(this.name);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.common.EntrySchema
 * JD-Core Version:    0.5.4
 */