package com.google.android.picasasync;

import android.net.Uri;
import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.Entry.Column;
import com.android.gallery3d.common.Entry.Table;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Fingerprint;

@Entry.Table("upload_records")
public class UploadedEntry extends Entry
{
  public static final EntrySchema SCHEMA = new EntrySchema(UploadedEntry.class);

  @Entry.Column("account")
  public final String account;

  @Entry.Column("album_id")
  public final String albumId;

  @Entry.Column("album_title")
  public final String albumTitle;

  @Entry.Column("bytes_total")
  public final long bytesTotal;

  @Entry.Column("caption")
  public final String caption;

  @Entry.Column("content_uri")
  public final String contentUri;

  @Entry.Column("display_name")
  public final String displayName;

  @Entry.Column("error")
  public final String error;

  @Entry.Column("fingerprint")
  public final byte[] fingerprint;

  @Entry.Column(indexed=true, value="fingerprint_hash")
  final int fingerprintHash;

  @Entry.Column("id_from_server")
  public final long idFromServer;

  @Entry.Column("state")
  public final int state;

  @Entry.Column("timestamp")
  public final long timestamp;

  @Entry.Column("uid")
  int uid;

  @Entry.Column("uploaded_time")
  public final long uploadedTime;

  @Entry.Column("url")
  public final String url;

  private UploadedEntry()
  {
    this.error = null;
    this.albumId = null;
    this.url = null;
    this.contentUri = null;
    this.account = null;
    this.fingerprint = null;
    this.timestamp = 0L;
    this.bytesTotal = 0L;
    this.idFromServer = 0L;
    this.uploadedTime = 0L;
    this.fingerprintHash = 0;
    this.state = 0;
    this.displayName = null;
    this.albumTitle = null;
    this.caption = null;
  }

  public UploadedEntry(UploadTaskEntry paramUploadTaskEntry)
  {
    this.account = paramUploadTaskEntry.getAccount();
    this.uploadedTime = System.currentTimeMillis();
    this.contentUri = paramUploadTaskEntry.getContentUri().toString();
    this.albumId = paramUploadTaskEntry.getAlbumId();
    this.idFromServer = 0L;
    this.bytesTotal = paramUploadTaskEntry.getBytesTotal();
    this.timestamp = paramUploadTaskEntry.getUploadedTime();
    String str;
    label68: Fingerprint localFingerprint;
    if (paramUploadTaskEntry.getUrl() == null)
    {
      str = null;
      this.url = str;
      localFingerprint = paramUploadTaskEntry.getFingerprint();
      if (localFingerprint != null)
        break label155;
      this.fingerprint = null;
    }
    for (this.fingerprintHash = 0; ; this.fingerprintHash = localFingerprint.hashCode())
    {
      this.state = paramUploadTaskEntry.getState();
      this.error = getFullErrorMessage(paramUploadTaskEntry.getError());
      this.uid = paramUploadTaskEntry.getUid();
      this.caption = paramUploadTaskEntry.getCaption();
      this.albumTitle = paramUploadTaskEntry.getAlbumTitle();
      this.displayName = paramUploadTaskEntry.getDisplayName();
      return;
      str = paramUploadTaskEntry.getUrl().toString();
      break label68:
      label155: this.fingerprint = localFingerprint.getBytes();
    }
  }

  public UploadedEntry(UploadTaskEntry paramUploadTaskEntry, long paramLong1, long paramLong2, long paramLong3, String paramString, byte[] paramArrayOfByte)
  {
    this.account = paramUploadTaskEntry.getAccount();
    this.uploadedTime = System.currentTimeMillis();
    this.contentUri = paramUploadTaskEntry.getContentUri().toString();
    this.albumId = paramUploadTaskEntry.getAlbumId();
    this.idFromServer = paramLong1;
    this.bytesTotal = paramLong2;
    this.timestamp = paramLong3;
    this.url = paramString;
    this.fingerprint = paramArrayOfByte;
    this.fingerprintHash = new Fingerprint(paramArrayOfByte).hashCode();
    this.state = 4;
    this.error = null;
    this.uid = paramUploadTaskEntry.getUid();
    this.caption = paramUploadTaskEntry.getCaption();
    this.albumTitle = paramUploadTaskEntry.getAlbumTitle();
    this.displayName = paramUploadTaskEntry.getDisplayName();
  }

  private static String getFullErrorMessage(Throwable paramThrowable)
  {
    if (paramThrowable == null)
      return null;
    StringBuilder localStringBuilder = new StringBuilder();
    Throwable localThrowable = null;
    while (paramThrowable != null)
    {
      localThrowable = paramThrowable;
      localStringBuilder.append(paramThrowable).append("\n");
      paramThrowable = paramThrowable.getCause();
    }
    StackTraceElement[] arrayOfStackTraceElement = localThrowable.getStackTrace();
    int i = arrayOfStackTraceElement.length;
    for (int j = 0; j < i; ++j)
      localStringBuilder.append(arrayOfStackTraceElement[j]).append("\n");
    return localStringBuilder.toString();
  }

  public String toString()
  {
    return SCHEMA.toDebugString(this, new String[] { "id_from_server", "content_uri", "bytes_total" });
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.UploadedEntry
 * JD-Core Version:    0.5.4
 */