package com.google.android.picasasync;

import android.content.ContentValues;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Utils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class AlbumCollectorJson extends PicasaJsonReaderParser
{
  private static final Map<String, PicasaJsonReaderParser.ObjectField> sAlbumEntryFieldMap = new HashMap();
  private static final Map<String, PicasaJsonReaderParser.ObjectField> sLinkFieldMap;

  static
  {
    EntrySchema localEntrySchema = AlbumEntry.SCHEMA;
    Map localMap1 = sAlbumEntryFieldMap;
    localMap1.put("gphoto$id", newObjectField(localEntrySchema.getColumn("_id")));
    localMap1.put("gphoto$albumType", newObjectField(localEntrySchema.getColumn("album_type")));
    localMap1.put("gphoto$user", newObjectField(localEntrySchema.getColumn("user")));
    localMap1.put("gphoto$bytesUsed", newObjectField(localEntrySchema.getColumn("bytes_used")));
    localMap1.put("title", newObjectField(localEntrySchema.getColumn("title")));
    localMap1.put("summary", newObjectField(localEntrySchema.getColumn("summary")));
    localMap1.put("gphoto$numphotos", newObjectField(localEntrySchema.getColumn("num_photos")));
    localMap1.put("published", new PicasaJsonReaderParser.ObjectField("date_published", 10));
    localMap1.put("updated", new PicasaJsonReaderParser.ObjectField("date_updated", 10));
    localMap1.put("app$edited", new PicasaJsonReaderParser.ObjectField("date_edited", 10));
    localMap1.put("link", new PicasaJsonReaderParser.ObjectField(13));
    HashMap localHashMap = new HashMap();
    localMap1.put("media$group", new PicasaJsonReaderParser.NestedObjectField(localHashMap));
    localHashMap.put("media$thumbnail", new PicasaJsonReaderParser.ObjectField(14));
    sLinkFieldMap = new HashMap();
    Map localMap2 = sLinkFieldMap;
    localMap2.put("rel", new PicasaJsonReaderParser.ObjectField("rel", 0));
    localMap2.put("type", new PicasaJsonReaderParser.ObjectField("type", 0));
    localMap2.put("href", new PicasaJsonReaderParser.ObjectField("href", 0));
  }

  public AlbumCollectorJson(PicasaApi.EntryHandler paramEntryHandler)
  {
    super(paramEntryHandler);
  }

  protected final void addHtmlPageUrl(JsonReader paramJsonReader, ContentValues paramContentValues)
    throws IOException
  {
    paramJsonReader.beginArray();
    ContentValues localContentValues = new ContentValues();
    String str1;
    String str2;
    do
    {
      if (!paramJsonReader.hasNext())
        break label94;
      localContentValues.clear();
      parseObject(paramJsonReader, sLinkFieldMap, localContentValues);
      str1 = localContentValues.getAsString("rel");
      str2 = localContentValues.getAsString("type");
    }
    while ((!Utils.equals(str1, "alternate")) || (!Utils.equals(str2, "text/html")));
    paramContentValues.put("html_page_url", localContentValues.getAsString("href"));
    while (paramJsonReader.hasNext())
      paramJsonReader.skipValue();
    label94: paramJsonReader.endArray();
  }

  protected final void addThumbnailUrl(JsonReader paramJsonReader, ContentValues paramContentValues, String paramString)
    throws IOException
  {
    paramJsonReader.beginArray();
    String str;
    do
    {
      if (!paramJsonReader.hasNext())
        break label46;
      str = parseObject(paramJsonReader, "url");
    }
    while (str == null);
    paramContentValues.put(paramString, str);
    while (paramJsonReader.hasNext())
      paramJsonReader.skipValue();
    label46: paramJsonReader.endArray();
  }

  protected Map<String, PicasaJsonReaderParser.ObjectField> getEntryFieldMap()
  {
    return sAlbumEntryFieldMap;
  }

  protected void handleComplexValue(JsonReader paramJsonReader, int paramInt, ContentValues paramContentValues)
    throws IOException
  {
    switch (paramInt)
    {
    default:
      paramJsonReader.skipValue();
      return;
    case 13:
      addHtmlPageUrl(paramJsonReader, paramContentValues);
      return;
    case 14:
    }
    addThumbnailUrl(paramJsonReader, paramContentValues, "thumbnail_url");
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.AlbumCollectorJson
 * JD-Core Version:    0.5.4
 */