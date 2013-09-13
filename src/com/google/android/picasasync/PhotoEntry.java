package com.google.android.picasasync;

import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.Entry.Column;
import com.android.gallery3d.common.Entry.Table;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Utils;

@Entry.Table("photos")
public final class PhotoEntry extends Entry
{
  public static final EntrySchema SCHEMA = new EntrySchema(PhotoEntry.class);

  @Entry.Column(indexed=true, value="album_id")
  public long albumId;

  @Entry.Column(defaultValue="0", value="cache_status")
  int cacheStatus;

  @Entry.Column("camera_sync")
  int cameraSync;

  @Entry.Column("comment_count")
  public int commentCount;

  @Entry.Column("content_type")
  public String contentType;

  @Entry.Column("content_url")
  public String contentUrl;

  @Entry.Column("date_edited")
  public long dateEdited;

  @Entry.Column("date_published")
  public long datePublished;

  @Entry.Column("date_taken")
  public long dateTaken;

  @Entry.Column("date_updated")
  public long dateUpdated;

  @Entry.Column(indexed=true, value="display_index")
  public int displayIndex;

  @Entry.Column("exif_exposure")
  public float exifExposure;

  @Entry.Column("exif_flash")
  public int exifFlash;

  @Entry.Column("exif_focal_length")
  public float exifFocalLength;

  @Entry.Column("exif_fstop")
  public float exifFstop;

  @Entry.Column("exif_iso")
  public int exifIso;

  @Entry.Column("exif_make")
  public String exifMake;

  @Entry.Column("exif_model")
  public String exifModel;

  @Entry.Column("face_ids")
  public String faceIds;

  @Entry.Column("face_names")
  public String faceNames;

  @Entry.Column("face_rectangles")
  public String faceRects;

  @Entry.Column("fingerprint")
  public byte[] fingerprint;

  @Entry.Column("fingerprint_hash")
  int fingerprintHash;

  @Entry.Column("height")
  public int height;

  @Entry.Column("html_page_url")
  public String htmlPageUrl;

  @Entry.Column("keywords")
  public String keywords;

  @Entry.Column("latitude")
  public double latitude;

  @Entry.Column("longitude")
  public double longitude;

  @Entry.Column("rotation")
  public int rotation;

  @Entry.Column("screennail_url")
  public String screennailUrl;

  @Entry.Column("size")
  public int size;

  @Entry.Column("summary")
  public String summary;

  @Entry.Column("title")
  public String title;

  @Entry.Column("user_id")
  public long userId;

  @Entry.Column("width")
  public int width;

  public boolean equals(Object paramObject)
  {
    if (!paramObject instanceof PhotoEntry);
    PhotoEntry localPhotoEntry;
    do
    {
      return false;
      localPhotoEntry = (PhotoEntry)paramObject;
    }
    while ((this.albumId != localPhotoEntry.albumId) || (this.displayIndex != localPhotoEntry.displayIndex) || (this.userId != localPhotoEntry.userId) || (!Utils.equals(this.title, localPhotoEntry.title)) || (!Utils.equals(this.summary, localPhotoEntry.summary)) || (this.datePublished != localPhotoEntry.datePublished) || (this.dateUpdated != localPhotoEntry.dateUpdated) || (this.dateEdited != localPhotoEntry.dateEdited) || (this.dateTaken != localPhotoEntry.dateTaken) || (this.commentCount != localPhotoEntry.commentCount) || (this.width != localPhotoEntry.width) || (this.height != localPhotoEntry.height) || (this.rotation != localPhotoEntry.rotation) || (this.size != localPhotoEntry.size) || (this.latitude != localPhotoEntry.latitude) || (this.longitude != localPhotoEntry.longitude) || (!Utils.equals(this.contentUrl, localPhotoEntry.contentUrl)) || (!Utils.equals(this.htmlPageUrl, localPhotoEntry.htmlPageUrl)) || (!Utils.equals(this.keywords, localPhotoEntry.keywords)) || (!Utils.equals(this.faceNames, localPhotoEntry.faceNames)) || (!Utils.equals(this.faceIds, localPhotoEntry.faceIds)) || (!Utils.equals(this.faceRects, localPhotoEntry.faceRects)) || (!Utils.equals(this.exifMake, localPhotoEntry.exifMake)) || (!Utils.equals(this.exifModel, localPhotoEntry.exifModel)) || (this.exifExposure != localPhotoEntry.exifExposure) || (this.exifFlash != localPhotoEntry.exifFlash) || (this.exifFocalLength != localPhotoEntry.exifFocalLength) || (this.exifFstop != localPhotoEntry.exifFstop) || (this.exifIso != localPhotoEntry.exifIso));
    return true;
  }

  public int hashCode()
  {
    return super.hashCode();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.PhotoEntry
 * JD-Core Version:    0.5.4
 */