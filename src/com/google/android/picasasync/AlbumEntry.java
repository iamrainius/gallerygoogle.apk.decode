package com.google.android.picasasync;

import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.Entry.Column;
import com.android.gallery3d.common.Entry.Table;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Utils;

@Entry.Table("albums")
public class AlbumEntry extends Entry
{
  public static final EntrySchema SCHEMA = new EntrySchema(AlbumEntry.class);

  @Entry.Column("album_type")
  public String albumType;

  @Entry.Column("bytes_used")
  public long bytesUsed;

  @Entry.Column(defaultValue="1", value="cache_flag")
  public int cacheFlag;

  @Entry.Column(defaultValue="0", value="cache_status")
  public int cacheStatus;

  @Entry.Column("date_edited")
  public long dateEdited;

  @Entry.Column("date_published")
  public long datePublished;

  @Entry.Column("date_updated")
  public long dateUpdated;

  @Entry.Column("html_page_url")
  public String htmlPageUrl;

  @Entry.Column("location_string")
  public String locationString;

  @Entry.Column("num_photos")
  public int numPhotos;

  @Entry.Column("photos_dirty")
  public boolean photosDirty;

  @Entry.Column("photos_etag")
  public String photosEtag = null;

  @Entry.Column("summary")
  public String summary;

  @Entry.Column("thumbnail_url")
  public String thumbnailUrl;

  @Entry.Column("title")
  public String title;

  @Entry.Column("user")
  public String user;

  @Entry.Column(indexed=true, value="user_id")
  public long userId;

  public boolean equals(Object paramObject)
  {
    if (!paramObject instanceof AlbumEntry);
    AlbumEntry localAlbumEntry;
    do
    {
      return false;
      localAlbumEntry = (AlbumEntry)paramObject;
    }
    while ((this.userId != localAlbumEntry.userId) || (this.cacheFlag != localAlbumEntry.cacheFlag) || (this.cacheStatus != localAlbumEntry.cacheStatus) || (this.photosDirty != localAlbumEntry.photosDirty) || (!Utils.equals(this.albumType, localAlbumEntry.albumType)) || (!Utils.equals(this.user, localAlbumEntry.user)) || (!Utils.equals(this.title, localAlbumEntry.title)) || (!Utils.equals(this.summary, localAlbumEntry.summary)) || (this.datePublished != localAlbumEntry.datePublished) || (this.dateUpdated != localAlbumEntry.dateUpdated) || (this.dateEdited != localAlbumEntry.dateEdited) || (this.numPhotos != localAlbumEntry.numPhotos) || (this.bytesUsed != localAlbumEntry.bytesUsed) || (!Utils.equals(this.locationString, localAlbumEntry.locationString)) || (!Utils.equals(this.thumbnailUrl, localAlbumEntry.thumbnailUrl)) || (!Utils.equals(this.htmlPageUrl, localAlbumEntry.htmlPageUrl)));
    return true;
  }

  public int hashCode()
  {
    return super.hashCode();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.AlbumEntry
 * JD-Core Version:    0.5.4
 */