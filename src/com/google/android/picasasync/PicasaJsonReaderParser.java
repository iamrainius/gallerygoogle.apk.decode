package com.google.android.picasasync;

import android.content.ContentValues;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import com.android.gallery3d.common.EntrySchema.ColumnInfo;
import com.android.gallery3d.common.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

abstract class PicasaJsonReaderParser
{
  int entryCount;
  private final PicasaApi.EntryHandler mHandler;
  int totalCount;

  protected PicasaJsonReaderParser(PicasaApi.EntryHandler paramEntryHandler)
  {
    this.mHandler = ((PicasaApi.EntryHandler)Utils.checkNotNull(paramEntryHandler));
  }

  protected static ObjectField newObjectField(EntrySchema.ColumnInfo paramColumnInfo)
  {
    switch (paramColumnInfo.type)
    {
    case 2:
    default:
      Log.e("PicasaAPI", "unexpected column " + paramColumnInfo.name + " of type " + paramColumnInfo.type);
    case 0:
    case 1:
    case 3:
    case 4:
    case 5:
    case 6:
    }
    for (int i = 11; ; i = 6)
      while (true)
      {
        return new ObjectField(paramColumnInfo.name, i);
        i = 0;
        continue;
        i = 1;
        continue;
        i = 3;
        continue;
        i = 4;
        continue;
        i = 5;
      }
  }

  private static long parseAtomTimestamp(String paramString)
  {
    Time localTime = new Time();
    localTime.parse3339(paramString);
    return localTime.toMillis(true);
  }

  private void parseEntry(JsonReader paramJsonReader)
    throws IOException
  {
    ContentValues localContentValues = new ContentValues();
    parseObject(paramJsonReader, getEntryFieldMap(), localContentValues);
    this.mHandler.handleEntry(localContentValues);
  }

  private void parseFeed(JsonReader paramJsonReader)
    throws IOException
  {
    String str1 = null;
    this.entryCount = 0;
    this.totalCount = -1;
    paramJsonReader.beginObject();
    while (paramJsonReader.hasNext())
    {
      String str2 = paramJsonReader.nextName();
      if (str2.equals("gd$etag"))
        str1 = paramJsonReader.nextString();
      if (str2.equals("openSearch$totalResults"))
        this.totalCount = Integer.parseInt(parseObject(paramJsonReader, "$t"));
      if (str2.equals("entry"))
      {
        paramJsonReader.beginArray();
        while (paramJsonReader.hasNext())
        {
          parseEntry(paramJsonReader);
          this.entryCount = (1 + this.entryCount);
        }
        paramJsonReader.endArray();
      }
      paramJsonReader.skipValue();
    }
    paramJsonReader.endObject();
    if (!Log.isLoggable("PicasaAPI", 2))
      return;
    Log.v("PicasaAPI", "   etag: --> " + str1 + ",entryCount=" + this.entryCount);
  }

  private void parseFieldValue(JsonReader paramJsonReader, ObjectField paramObjectField, ContentValues paramContentValues)
    throws IOException
  {
    if (paramObjectField.type >= 12)
    {
      switch (paramObjectField.type)
      {
      default:
        handleComplexValue(paramJsonReader, paramObjectField.type, paramContentValues);
        return;
      case 12:
      }
      parseObject(paramJsonReader, ((NestedObjectField)paramObjectField).map, paramContentValues);
      return;
    }
    if (paramJsonReader.peek() == JsonToken.BEGIN_OBJECT)
    {
      paramJsonReader.beginObject();
      if (paramJsonReader.hasNext())
      {
        Utils.assertTrue(paramJsonReader.nextName().equals("$t"));
        parsePrimitiveValue(paramJsonReader, paramObjectField, paramContentValues);
        if (paramJsonReader.hasNext())
          break label117;
      }
      for (boolean bool = true; ; bool = false)
      {
        Utils.assertTrue(bool);
        paramJsonReader.endObject();
        label117: return;
      }
    }
    parsePrimitiveValue(paramJsonReader, paramObjectField, paramContentValues);
  }

  private void parsePrimitiveValue(JsonReader paramJsonReader, ObjectField paramObjectField, ContentValues paramContentValues)
    throws IOException
  {
    String str1 = paramObjectField.columnName;
    int i = paramObjectField.type;
    switch (i)
    {
    case 2:
    case 7:
    case 8:
    case 9:
    default:
    case 0:
    case 1:
    case 3:
    case 4:
    case 5:
    case 6:
    case 10:
    }
    String str2;
    boolean bool;
    do
    {
      try
      {
        throw new RuntimeException("unexpected type: " + i + " for " + str1);
      }
      catch (Exception localException2)
      {
        Log.e("PicasaAPI", "error parsing value", localException2);
        paramJsonReader.skipValue();
        return;
      }
      paramContentValues.put(str1, paramJsonReader.nextString());
      return;
      paramContentValues.put(str1, Integer.valueOf(((BooleanObjectField)paramObjectField).getValue(Boolean.parseBoolean(paramJsonReader.nextString()))));
      return;
      paramContentValues.put(str1, Integer.valueOf(paramJsonReader.nextInt()));
      return;
      paramContentValues.put(str1, Long.valueOf(paramJsonReader.nextLong()));
      return;
      paramContentValues.put(str1, Float.valueOf((float)paramJsonReader.nextDouble()));
      return;
      paramContentValues.put(str1, Double.valueOf(paramJsonReader.nextDouble()));
      return;
      str2 = paramJsonReader.nextString();
      bool = TextUtils.isEmpty(str2);
    }
    while (bool);
    try
    {
      paramContentValues.put(str1, Long.valueOf(parseAtomTimestamp(str2)));
      return;
    }
    catch (Exception localException1)
    {
      Log.w("PicasaAPI", "parseAtomTimestamp", localException1);
    }
  }

  protected abstract Map<String, ObjectField> getEntryFieldMap();

  protected abstract void handleComplexValue(JsonReader paramJsonReader, int paramInt, ContentValues paramContentValues)
    throws IOException;

  public final void parse(InputStream paramInputStream)
    throws IOException
  {
    JsonReader localJsonReader = new JsonReader(new InputStreamReader(paramInputStream, "UTF-8"));
    localJsonReader.beginObject();
    while (localJsonReader.hasNext())
    {
      if (localJsonReader.nextName().equals("feed"))
      {
        parseFeed(localJsonReader);
        return;
      }
      localJsonReader.skipValue();
    }
    localJsonReader.endObject();
  }

  protected final String parseObject(JsonReader paramJsonReader, String paramString)
    throws IOException
  {
    paramJsonReader.beginObject();
    String str;
    while (true)
    {
      boolean bool = paramJsonReader.hasNext();
      str = null;
      if (!bool)
        break;
      if (paramString.equals(paramJsonReader.nextName()))
      {
        if (paramJsonReader.peek() == JsonToken.BEGIN_OBJECT);
        for (str = parseObject(paramJsonReader, "$t"); ; str = paramJsonReader.nextString())
          while (true)
          {
            if (!paramJsonReader.hasNext())
              break label81;
            paramJsonReader.nextName();
            paramJsonReader.skipValue();
          }
      }
      paramJsonReader.skipValue();
    }
    label81: paramJsonReader.endObject();
    return str;
  }

  protected final void parseObject(JsonReader paramJsonReader, Map<String, ObjectField> paramMap, ContentValues paramContentValues)
    throws IOException
  {
    paramJsonReader.beginObject();
    while (paramJsonReader.hasNext())
    {
      ObjectField localObjectField = (ObjectField)paramMap.get(paramJsonReader.nextName());
      if (localObjectField != null)
        parseFieldValue(paramJsonReader, localObjectField, paramContentValues);
      paramJsonReader.skipValue();
    }
    paramJsonReader.endObject();
  }

  protected static class BooleanObjectField extends PicasaJsonReaderParser.ObjectField
  {
    final int offValue;
    final int onValue;

    BooleanObjectField(String paramString, int paramInt1, int paramInt2)
    {
      super(paramString, 1);
      this.onValue = paramInt1;
      this.offValue = paramInt2;
    }

    int getValue(boolean paramBoolean)
    {
      if (paramBoolean)
        return this.onValue;
      return this.offValue;
    }
  }

  protected static class NestedObjectField extends PicasaJsonReaderParser.ObjectField
  {
    final Map<String, PicasaJsonReaderParser.ObjectField> map;

    NestedObjectField(Map<String, PicasaJsonReaderParser.ObjectField> paramMap)
    {
      super(12);
      this.map = paramMap;
    }
  }

  protected static class ObjectField
  {
    final String columnName;
    final int type;

    ObjectField(int paramInt)
    {
      this.columnName = null;
      this.type = paramInt;
      if (paramInt > 10);
      for (boolean bool = true; ; bool = false)
      {
        Utils.assertTrue(bool);
        return;
      }
    }

    ObjectField(String paramString, int paramInt)
    {
      this.columnName = paramString;
      this.type = paramInt;
      if (paramInt <= 10);
      for (boolean bool = true; ; bool = false)
      {
        Utils.assertTrue(bool);
        return;
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.PicasaJsonReaderParser
 * JD-Core Version:    0.5.4
 */