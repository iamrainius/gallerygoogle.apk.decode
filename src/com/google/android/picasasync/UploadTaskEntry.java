package com.google.android.picasasync;

import android.content.ComponentName;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.Entry.Column;
import com.android.gallery3d.common.Entry.Table;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Fingerprint;
import java.util.ArrayList;

@Entry.Table("upload_tasks")
public class UploadTaskEntry extends Entry
{
  private static final String[] REQUIRED_COLUMNS;
  public static final EntrySchema SCHEMA = new EntrySchema(UploadTaskEntry.class);

  @Entry.Column("account")
  private String mAccount;

  @Entry.Column("album_id")
  private String mAlbumId;

  @Entry.Column("album_title")
  private String mAlbumTitle;

  @Entry.Column("auth_token_type")
  private String mAuthTokenType;

  @Entry.Column("bytes_total")
  private long mBytesTotal;

  @Entry.Column("bytes_uploaded")
  private long mBytesUploaded;

  @Entry.Column("caption")
  private String mCaption;
  private ComponentName mComponentName;

  @Entry.Column("content_uri")
  private String mContentUri;

  @Entry.Column("display_name")
  private String mDisplayName;
  private Throwable mError;

  @Entry.Column("fingerprint")
  private byte[] mFingerprint;

  @Entry.Column("mime_type")
  private String mMimeType;

  @Entry.Column("priority")
  private int mPriority;

  @Entry.Column("component_name")
  private String mRawComponentName;

  @Entry.Column("request_template")
  private String mRequestTemplate;

  @Entry.Column("state")
  private int mState = 3;

  @Entry.Column("uid")
  private int mUid;

  @Entry.Column("upload_url")
  private String mUploadUrl;

  @Entry.Column("uploaded_time")
  private long mUploadedTime;

  @Entry.Column("url")
  private String mUrl;

  static
  {
    REQUIRED_COLUMNS = new String[] { "account", "content_uri" };
  }

  private static void checkRequest(ContentValues paramContentValues)
  {
    ArrayList localArrayList = new ArrayList();
    for (String str : REQUIRED_COLUMNS)
    {
      if (paramContentValues.get(str) != null)
        continue;
      localArrayList.add(str);
    }
    if (localArrayList.isEmpty())
      return;
    throw new RuntimeException("missing fields in upload request: " + localArrayList);
  }

  static UploadTaskEntry createNew(ContentValues paramContentValues)
  {
    checkRequest(paramContentValues);
    return (UploadTaskEntry)SCHEMA.valuesToObject(paramContentValues, new UploadTaskEntry());
  }

  static UploadTaskEntry createNew(ContentValues paramContentValues, int paramInt)
  {
    paramContentValues.put("uid", Integer.valueOf(paramInt));
    return createNew(paramContentValues);
  }

  public static UploadTaskEntry fromCursor(Cursor paramCursor)
  {
    return (UploadTaskEntry)SCHEMA.cursorToObject(paramCursor, new UploadTaskEntry());
  }

  public static UploadTaskEntry fromDb(SQLiteDatabase paramSQLiteDatabase, long paramLong)
  {
    UploadTaskEntry localUploadTaskEntry = new UploadTaskEntry();
    if (SCHEMA.queryWithId(paramSQLiteDatabase, paramLong, localUploadTaskEntry))
      return localUploadTaskEntry;
    return null;
  }

  public String getAccount()
  {
    return this.mAccount;
  }

  public String getAlbumId()
  {
    return this.mAlbumId;
  }

  public String getAlbumTitle()
  {
    return this.mAlbumTitle;
  }

  String getAuthTokenType()
  {
    return this.mAuthTokenType;
  }

  public long getBytesTotal()
  {
    return this.mBytesTotal;
  }

  public long getBytesUploaded()
  {
    return this.mBytesUploaded;
  }

  public String getCaption()
  {
    return this.mCaption;
  }

  ComponentName getComponentName()
  {
    if ((this.mComponentName == null) && (this.mRawComponentName != null))
      this.mComponentName = ComponentName.unflattenFromString(this.mRawComponentName);
    return this.mComponentName;
  }

  public Uri getContentUri()
  {
    return Uri.parse(this.mContentUri);
  }

  public String getDisplayName()
  {
    return this.mDisplayName;
  }

  public Throwable getError()
  {
    return this.mError;
  }

  public Fingerprint getFingerprint()
  {
    if (this.mFingerprint == null)
      return null;
    return new Fingerprint(this.mFingerprint);
  }

  String getMimeType()
  {
    return this.mMimeType;
  }

  public int getPercentageUploaded()
  {
    int i;
    if ((this.mBytesTotal == 0L) || (this.mBytesUploaded == 0L))
      i = 0;
    do
    {
      return i;
      i = (int)Math.round(100.0D * ((float)this.mBytesUploaded / (float)this.mBytesTotal));
    }
    while (i <= 100);
    return 100;
  }

  String getRequestTemplate()
  {
    return this.mRequestTemplate;
  }

  public int getState()
  {
    return this.mState;
  }

  int getUid()
  {
    return this.mUid;
  }

  String getUploadUrl()
  {
    return this.mUploadUrl;
  }

  public long getUploadedTime()
  {
    return this.mUploadedTime;
  }

  public Uri getUrl()
  {
    if (this.mUrl == null)
      return null;
    return Uri.parse(this.mUrl);
  }

  public boolean hasFingerprint()
  {
    return this.mFingerprint != null;
  }

  public boolean hasPriority()
  {
    return (this.mPriority == 2) || (this.mPriority == 1);
  }

  public boolean isCancellable()
  {
    return (this.mState == 1) || (this.mState == 2) || (this.mState == 3);
  }

  public boolean isReadyForUpload()
  {
    return (this.mState == 3) || (this.mState == 1);
  }

  public boolean isStartedYet()
  {
    return this.mBytesUploaded > 0L;
  }

  public boolean isUploading()
  {
    return this.mState == 1;
  }

  public void setAlbumId(String paramString)
  {
    this.mAlbumId = paramString;
  }

  void setAuthTokenType(String paramString)
  {
    this.mAuthTokenType = paramString;
  }

  public void setBytesTotal(long paramLong)
  {
    this.mBytesTotal = paramLong;
  }

  public void setBytesUploaded(long paramLong)
  {
    this.mBytesUploaded = paramLong;
  }

  public void setFingerprint(Fingerprint paramFingerprint)
  {
    this.mFingerprint = paramFingerprint.getBytes();
  }

  void setMimeType(String paramString)
  {
    this.mMimeType = paramString;
  }

  void setPriority(int paramInt)
  {
    this.mPriority = paramInt;
  }

  void setRequestTemplate(String paramString)
  {
    this.mRequestTemplate = paramString;
  }

  public void setState(int paramInt)
  {
    this.mState = paramInt;
  }

  public void setState(int paramInt, Throwable paramThrowable)
  {
    this.mState = paramInt;
    this.mError = paramThrowable;
  }

  void setUploadUrl(String paramString)
  {
    this.mUploadUrl = paramString;
  }

  void setUploadedTime()
  {
    this.mUploadedTime = System.currentTimeMillis();
  }

  void setUrl(String paramString)
  {
    this.mUrl = paramString;
  }

  public boolean shouldRetry()
  {
    return this.mState == 2;
  }

  public String toString()
  {
    return SCHEMA.toDebugString(this, new String[] { "content_uri", "state", "bytes_total" }) + "," + getPercentageUploaded() + "%";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.UploadTaskEntry
 * JD-Core Version:    0.5.4
 */