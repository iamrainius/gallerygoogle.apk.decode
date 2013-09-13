package com.android.gallery3d.data;

import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.Entry.Column;
import com.android.gallery3d.common.Entry.Table;
import com.android.gallery3d.common.EntrySchema;

@Entry.Table("download")
public class DownloadEntry extends Entry
{
  public static final EntrySchema SCHEMA = new EntrySchema(DownloadEntry.class);

  @Entry.Column("_size")
  public long contentSize;

  @Entry.Column("content_url")
  public String contentUrl;

  @Entry.Column("etag")
  public String eTag;

  @Entry.Column(indexed=true, value="hash_code")
  public long hashCode;

  @Entry.Column(indexed=true, value="last_access")
  public long lastAccessTime;

  @Entry.Column("last_updated")
  public long lastUpdatedTime;

  @Entry.Column("_data")
  public String path;

  public String toString()
  {
    return "hash_code: " + this.hashCode + ", " + "content_url" + this.contentUrl + ", " + "_size" + this.contentSize + ", " + "etag" + this.eTag + ", " + "last_access" + this.lastAccessTime + ", " + "last_updated" + this.lastUpdatedTime + "," + "_data" + this.path;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.DownloadEntry
 * JD-Core Version:    0.5.4
 */