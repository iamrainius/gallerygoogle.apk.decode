package com.google.android.picasasync;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Binder;
import android.os.ParcelFileDescriptor;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.Log;
import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Utils;
import com.google.android.picasastore.MetricsUtils;
import com.google.android.picasastore.PicasaMatrixCursor;
import com.google.android.picasastore.PicasaMatrixCursor.RowBuilder;
import com.google.android.picasastore.PicasaStoreFacade;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class PicasaContentProvider0 extends ContentProvider
{
  private static final String ALBUM_TABLE_NAME;
  private static final String ALBUM_TYPE_WHERE;
  private static final String PHOTO_TABLE_NAME;
  private static String[] PROJECTION_CONTENT_URL;
  private static String[] PROJECTION_SCREENNAIL_URL;
  private static String[] PROJECTION_THUMBNAIL_URL;
  private static final HashMap<String, String> SETTING_DEFAULTS;
  private static final HashMap<String, String> SETTING_DEPRECATED;
  private static final String UPLOAD_RECORD_TABLE;
  private static final String UPLOAD_TASK_TABLE;
  private static final String USER_TABLE_NAME = UserEntry.SCHEMA.getTableName();
  private String mAuthority;
  private PicasaDatabaseHelper mDbHelper;
  private PicasaStoreFacade mPicasaStoreFacade = null;
  private final UriMatcher mUriMatcher = new UriMatcher(-1);

  static
  {
    ALBUM_TABLE_NAME = AlbumEntry.SCHEMA.getTableName();
    PHOTO_TABLE_NAME = PhotoEntry.SCHEMA.getTableName();
    ALBUM_TYPE_WHERE = "_id in (SELECT album_id FROM " + PHOTO_TABLE_NAME + " WHERE " + "content_type" + " LIKE ?)";
    UPLOAD_TASK_TABLE = UploadTaskEntry.SCHEMA.getTableName();
    UPLOAD_RECORD_TABLE = UploadedEntry.SCHEMA.getTableName();
    SETTING_DEFAULTS = new HashMap();
    SETTING_DEPRECATED = new HashMap();
    SETTING_DEFAULTS.put("sync_on_wifi_only", "1");
    SETTING_DEFAULTS.put("sync_picasa_on_wifi_only", "1");
    SETTING_DEFAULTS.put("video_upload_wifi_only", "1");
    SETTING_DEFAULTS.put("sync_on_roaming", "0");
    SETTING_DEFAULTS.put("sync_on_battery", "1");
    SETTING_DEPRECATED.put("sync_photo_on_mobile", "0");
    SETTING_DEPRECATED.put("auto_upload_enabled", "0");
    SETTING_DEPRECATED.put("auto_upload_account_name", null);
    SETTING_DEPRECATED.put("auto_upload_account_type", null);
    PROJECTION_THUMBNAIL_URL = new String[] { "thumbnail_url" };
    PROJECTION_CONTENT_URL = new String[] { "content_url" };
    PROJECTION_SCREENNAIL_URL = new String[] { "screennail_url" };
  }

  private int cancelImmediateSync(Uri paramUri)
  {
    List localList = paramUri.getPathSegments();
    if (localList.size() != 2)
      throw new IllegalArgumentException("Invalid URI: expect /sync_request/<task_ID>");
    String str = (String)localList.get(1);
    if (ImmediateSync.get(getContext()).cancelTask(str))
      return 1;
    return 0;
  }

  private int cancelSingleUpload(Uri paramUri)
  {
    if (!querySingleUpload(UPLOAD_TASK_TABLE, paramUri, Entry.ID_PROJECTION).moveToNext())
      return 0;
    long l = Long.parseLong((String)paramUri.getPathSegments().get(1));
    UploadsManager.getInstance(getContext()).cancelTask(l);
    return 1;
  }

  private int cancelUploads(Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    Cursor localCursor = queryUploads(UPLOAD_TASK_TABLE, paramUri, Entry.ID_PROJECTION, paramString, paramArrayOfString, null);
    int i = 0;
    try
    {
      while (localCursor.moveToNext())
      {
        long l = localCursor.getLong(localCursor.getColumnIndex("_id"));
        UploadsManager.getInstance(getContext()).cancelTask(l);
        ++i;
      }
      return i;
    }
    finally
    {
      localCursor.close();
    }
  }

  private static long getItemIdFromUri(Uri paramUri)
  {
    try
    {
      long l = Long.parseLong((String)paramUri.getPathSegments().get(1));
      return l;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Log.w("PicasaContentProvider0", "cannot get id from: " + paramUri);
    }
    return -1L;
  }

  private static long getLastSegmentAsLong(Uri paramUri, long paramLong)
  {
    List localList = paramUri.getPathSegments();
    if (localList.size() == 0)
    {
      Log.w("PicasaContentProvider0", "parse fail: " + paramUri);
      return paramLong;
    }
    String str = (String)localList.get(-1 + localList.size());
    try
    {
      long l = Long.parseLong(str);
      return l;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Log.w("PicasaContentProvider0", "pasre fail:" + paramUri, localNumberFormatException);
    }
    return paramLong;
  }

  private Uri insertSyncRequest(Uri paramUri, ContentValues paramContentValues)
  {
    String str = paramContentValues.getAsString("task");
    if ("manual_metadata".equals(str))
      PicasaSyncManager.get(getContext()).requestMetadataSync(true);
    do
    {
      return paramUri;
      if ("metadata".equals(str))
      {
        PicasaSyncManager.get(getContext()).requestMetadataSync(false);
        return paramUri;
      }
      if ("new_photo_upload".equals(str))
      {
        PicasaSyncManager.get(getContext()).updateTasks(500L);
        return paramUri;
      }
      if ("immediate_albums".equals(str))
        return requestImmediateSyncOnAlbumList(paramUri, paramContentValues);
    }
    while (!"immediate_photos".equals(str));
    return requestImmediateSyncOnPhotos(paramUri, paramContentValues);
  }

  private String lookupAlbumCoverUrl(long paramLong)
  {
    SQLiteDatabase localSQLiteDatabase = PicasaDatabaseHelper.get(getContext()).getReadableDatabase();
    String str1 = ALBUM_TABLE_NAME;
    String[] arrayOfString1 = PROJECTION_THUMBNAIL_URL;
    String[] arrayOfString2 = new String[1];
    arrayOfString2[0] = String.valueOf(paramLong);
    Cursor localCursor = localSQLiteDatabase.query(str1, arrayOfString1, "_id=?", arrayOfString2, null, null, null);
    if (localCursor != null);
    try
    {
      if (localCursor.moveToNext())
      {
        boolean bool = localCursor.isNull(0);
        if (!bool)
          break label90;
      }
      return null;
      label90: String str2 = localCursor.getString(0);
      return str2;
    }
    finally
    {
      Utils.closeSilently(localCursor);
    }
  }

  private String lookupContentUrl(long paramLong, String paramString)
  {
    if (paramString == null)
      paramString = "full";
    Context localContext = getContext();
    String[] arrayOfString1;
    label29: Cursor localCursor;
    if ("full".equals(paramString))
    {
      arrayOfString1 = PROJECTION_CONTENT_URL;
      SQLiteDatabase localSQLiteDatabase = PicasaDatabaseHelper.get(localContext).getReadableDatabase();
      String str1 = PHOTO_TABLE_NAME;
      String[] arrayOfString2 = new String[1];
      arrayOfString2[0] = String.valueOf(paramLong);
      localCursor = localSQLiteDatabase.query(str1, arrayOfString1, "_id=?", arrayOfString2, null, null, null);
      if (localCursor == null);
    }
    try
    {
      if (localCursor.moveToNext())
      {
        boolean bool = localCursor.isNull(0);
        if (!bool)
          break label122;
      }
      return null;
      arrayOfString1 = PROJECTION_SCREENNAIL_URL;
      break label29:
      label122: String str2 = localCursor.getString(0);
      return str2;
    }
    finally
    {
      Utils.closeSilently(localCursor);
    }
  }

  private Cursor queryAlbums(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    String str2;
    if (paramString1 == null)
    {
      str2 = paramUri.getQueryParameter("type");
      if (!"image".equals(str2))
        break label74;
      paramString1 = ALBUM_TYPE_WHERE;
      paramArrayOfString2 = new String[1];
      paramArrayOfString2[0] = "image/%";
    }
    while (true)
    {
      String str1 = paramUri.getQueryParameter("limit");
      return this.mDbHelper.getReadableDatabase().query(ALBUM_TABLE_NAME, paramArrayOfString1, paramString1, paramArrayOfString2, null, null, paramString2, str1);
      label74: if (!"video".equals(str2))
        continue;
      paramString1 = ALBUM_TYPE_WHERE;
      paramArrayOfString2 = new String[] { "video/%" };
    }
  }

  private Cursor queryImmediateSyncResult(Uri paramUri)
  {
    List localList = paramUri.getPathSegments();
    if (localList.size() != 2)
      throw new IllegalArgumentException("Invalid URI: expect /sync_request/<task_ID>");
    String str = (String)localList.get(1);
    int i = ImmediateSync.get(getContext()).getResult(str);
    PicasaMatrixCursor localPicasaMatrixCursor = new PicasaMatrixCursor(new String[] { "immediate_sync_result" });
    localPicasaMatrixCursor.newRow().add(Integer.valueOf(i));
    return localPicasaMatrixCursor;
  }

  private Cursor queryInternal(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    switch (this.mUriMatcher.match(paramUri))
    {
    case 10:
    case 14:
    default:
      throw new IllegalArgumentException("Invalid URI: " + paramUri);
    case 12:
      return queryUsers(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
    case 3:
      return queryAlbums(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
    case 15:
      return queryPostPhotos(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
    case 16:
      return queryPostAlbum(paramUri, paramArrayOfString1);
    case 1:
      return queryPhotos(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
    case 5:
      return queryUploads(UPLOAD_TASK_TABLE, paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
    case 7:
      return queryUploads(UPLOAD_RECORD_TABLE, paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
    case 13:
      return querySingleUser(paramUri, paramArrayOfString1);
    case 4:
      return querySingleAlbum(paramUri, paramArrayOfString1);
    case 2:
      return querySinglePhoto(paramUri, paramArrayOfString1);
    case 6:
      return querySingleUpload(UPLOAD_TASK_TABLE, paramUri, paramArrayOfString1);
    case 8:
      return querySingleUpload(UPLOAD_RECORD_TABLE, paramUri, paramArrayOfString1);
    case 9:
      return querySettings(paramUri, paramArrayOfString1);
    case 11:
    }
    return queryImmediateSyncResult(paramUri);
  }

  private Cursor queryPhotos(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    String str = paramUri.getQueryParameter("limit");
    return this.mDbHelper.getReadableDatabase().query(PHOTO_TABLE_NAME, paramArrayOfString1, paramString1, paramArrayOfString2, null, null, paramString2, str);
  }

  private Cursor queryPostAlbum(Uri paramUri, String[] paramArrayOfString)
  {
    String str1 = "album_type = 'Buzz'";
    String str2 = paramUri.getQueryParameter("user_id");
    String[] arrayOfString = null;
    if (str2 != null)
    {
      str1 = "album_type = 'Buzz' AND user_id = ?";
      arrayOfString = new String[1];
      arrayOfString[0] = str2;
    }
    return this.mDbHelper.getReadableDatabase().query(ALBUM_TABLE_NAME, paramArrayOfString, str1, arrayOfString, null, null, null, null);
  }

  private Cursor queryPostPhotos(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    WhereEntry localWhereEntry = new WhereEntry(paramUri, paramString2);
    return this.mDbHelper.getReadableDatabase().query(PHOTO_TABLE_NAME, paramArrayOfString1, localWhereEntry.selection, localWhereEntry.selectionArgs, null, null, localWhereEntry.sortOrder, localWhereEntry.sortLimit);
  }

  private Cursor querySettings(Uri paramUri, String[] paramArrayOfString)
  {
    monitorenter;
    while (true)
    {
      PicasaMatrixCursor localPicasaMatrixCursor;
      Object[] arrayOfObject;
      ContentResolver localContentResolver;
      int i;
      String str;
      try
      {
        localPicasaMatrixCursor = new PicasaMatrixCursor(paramArrayOfString);
        arrayOfObject = new Object[paramArrayOfString.length];
        localContentResolver = getContext().getContentResolver();
        i = 0;
        int j = paramArrayOfString.length;
        if (i >= j)
          break label176;
        str = paramArrayOfString[i];
        if (!SETTING_DEFAULTS.containsKey(str))
        {
          if (SETTING_DEPRECATED.containsKey(str))
            arrayOfObject[i] = SETTING_DEPRECATED.get(str);
          throw new IllegalArgumentException("unknown column: " + str);
        }
      }
      finally
      {
        monitorexit;
      }
      arrayOfObject[i] = Settings.System.getString(localContentResolver, "com.google.android.picasasync." + str);
      if (arrayOfObject[i] == null)
      {
        arrayOfObject[i] = SETTING_DEFAULTS.get(str);
        break label186:
        label176: localPicasaMatrixCursor.addRow(arrayOfObject);
        monitorexit;
        return localPicasaMatrixCursor;
      }
      label186: ++i;
    }
  }

  private Cursor querySingleAlbum(Uri paramUri, String[] paramArrayOfString)
  {
    String[] arrayOfString = new String[1];
    arrayOfString[0] = ((String)paramUri.getPathSegments().get(1));
    return this.mDbHelper.getReadableDatabase().query(ALBUM_TABLE_NAME, paramArrayOfString, "_id=?", arrayOfString, null, null, null);
  }

  private Cursor querySinglePhoto(Uri paramUri, String[] paramArrayOfString)
  {
    String[] arrayOfString = new String[1];
    arrayOfString[0] = ((String)paramUri.getPathSegments().get(1));
    return this.mDbHelper.getReadableDatabase().query(PHOTO_TABLE_NAME, paramArrayOfString, "_id=?", arrayOfString, null, null, null);
  }

  private Cursor querySingleUpload(String paramString, Uri paramUri, String[] paramArrayOfString)
  {
    String[] arrayOfString = new String[2];
    arrayOfString[0] = ((String)paramUri.getPathSegments().get(1));
    arrayOfString[1] = String.valueOf(Binder.getCallingUid());
    return UploadsManager.getInstance(getContext()).getUploadsDatabaseHelper().getReadableDatabase().query(paramString, paramArrayOfString, "_id=? AND uid=?", arrayOfString, null, null, null);
  }

  private Cursor querySingleUser(Uri paramUri, String[] paramArrayOfString)
  {
    String[] arrayOfString = new String[1];
    arrayOfString[0] = ((String)paramUri.getPathSegments().get(1));
    return this.mDbHelper.getReadableDatabase().query(USER_TABLE_NAME, paramArrayOfString, "_id=?", arrayOfString, null, null, null);
  }

  private Cursor queryUploads(String paramString1, Uri paramUri, String[] paramArrayOfString1, String paramString2, String[] paramArrayOfString2, String paramString3)
  {
    String str1 = paramUri.getQueryParameter("limit");
    Object localObject;
    if (TextUtils.isEmpty(paramString2))
    {
      str2 = "uid=?";
      localObject = new String[1];
      localObject[0] = String.valueOf(Binder.getCallingUid());
      label38: return UploadsManager.getInstance(getContext()).getUploadsDatabaseHelper().getReadableDatabase().query(paramString1, paramArrayOfString1, str2, localObject, null, null, paramString3, str1);
    }
    String str2 = "(" + paramString2 + ") AND " + "uid=?";
    if (paramArrayOfString2 == null);
    for (String[] arrayOfString = new String[1]; ; arrayOfString = Utils.copyOf(paramArrayOfString2, 1 + paramArrayOfString2.length))
    {
      arrayOfString[(-1 + arrayOfString.length)] = String.valueOf(Binder.getCallingUid());
      localObject = arrayOfString;
      break label38:
    }
  }

  private Cursor queryUsers(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    String str = paramUri.getQueryParameter("limit");
    return this.mDbHelper.getReadableDatabase().query(USER_TABLE_NAME, paramArrayOfString1, paramString1, paramArrayOfString2, null, null, paramString2, str);
  }

  private Uri requestImmediateSyncOnAlbumList(Uri paramUri, ContentValues paramContentValues)
  {
    String str1 = paramContentValues.getAsString("account");
    Context localContext = getContext();
    if (str1 == null);
    for (String str2 = ImmediateSync.get(localContext).requestSyncAlbumListForAllAccounts(); ; str2 = ImmediateSync.get(localContext).requestSyncAlbumListForAccount(str1))
      return paramUri.buildUpon().appendPath(str2).build();
  }

  private Uri requestImmediateSyncOnPhotos(Uri paramUri, ContentValues paramContentValues)
  {
    String str1 = paramContentValues.getAsString("album_id");
    if (TextUtils.isEmpty(str1))
      throw new IllegalArgumentException("album ID missing");
    String str2 = ImmediateSync.get(getContext()).requestSyncAlbum(str1);
    return paramUri.buildUpon().appendPath(str2).build();
  }

  private boolean resetSettings()
  {
    ContentValues localContentValues = new ContentValues();
    Iterator localIterator = SETTING_DEFAULTS.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localContentValues.put((String)localEntry.getKey(), (String)localEntry.getValue());
    }
    return updateSettings(localContentValues);
  }

  private boolean updateSettings(ContentValues paramContentValues)
  {
    ContentResolver localContentResolver = getContext().getContentResolver();
    int i = 0;
    monitorenter;
    label23: Map.Entry localEntry;
    try
    {
      Iterator localIterator = paramContentValues.valueSet().iterator();
      if (!localIterator.hasNext())
        break label208;
      localEntry = (Map.Entry)localIterator.next();
      if (!SETTING_DEFAULTS.containsKey(localEntry.getKey()))
        throw new IllegalArgumentException("unknown setting: " + (String)localEntry.getKey());
    }
    finally
    {
      monitorexit;
    }
    String str = "com.google.android.picasasync." + (String)localEntry.getKey();
    if (localEntry.getValue() == null);
    for (Object localObject2 = null; ; localObject2 = localEntry.getValue().toString())
    {
      if (!Utils.equals(Settings.System.getString(localContentResolver, str), localObject2));
      Settings.System.putString(localContentResolver, str, (String)localObject2);
      i = 1;
      break label23:
    }
    label208: monitorexit;
    if (i != 0)
    {
      Context localContext = getContext();
      PicasaSyncManager.get(localContext).updateTasks(0L);
      UploadsManager.getInstance(localContext).reloadSystemSettings();
      localContentResolver.notifyChange(PicasaFacade.get(localContext).getSettingsUri(), null);
    }
    return i;
  }

  public void attachInfo(Context paramContext, ProviderInfo paramProviderInfo)
  {
    super.attachInfo(paramContext, paramProviderInfo);
    this.mAuthority = paramProviderInfo.authority;
    this.mUriMatcher.addURI(this.mAuthority, "photos", 1);
    this.mUriMatcher.addURI(this.mAuthority, "albums", 3);
    this.mUriMatcher.addURI(this.mAuthority, "posts", 15);
    this.mUriMatcher.addURI(this.mAuthority, "posts_album", 16);
    this.mUriMatcher.addURI(this.mAuthority, "users", 12);
    this.mUriMatcher.addURI(this.mAuthority, "uploads", 5);
    this.mUriMatcher.addURI(this.mAuthority, "upload_records", 7);
    this.mUriMatcher.addURI(this.mAuthority, "photos/#", 2);
    this.mUriMatcher.addURI(this.mAuthority, "albums/#", 4);
    this.mUriMatcher.addURI(this.mAuthority, "users/#", 13);
    this.mUriMatcher.addURI(this.mAuthority, "uploads/#", 6);
    this.mUriMatcher.addURI(this.mAuthority, "settings", 9);
    this.mUriMatcher.addURI(this.mAuthority, "sync_request", 10);
    this.mUriMatcher.addURI(this.mAuthority, "sync_request/*", 11);
    this.mUriMatcher.addURI(this.mAuthority, "upload_records/#", 8);
    this.mUriMatcher.addURI(this.mAuthority, "albumcovers/#", 14);
  }

  public int delete(Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    switch (this.mUriMatcher.match(paramUri))
    {
    case 7:
    case 8:
    case 10:
    default:
      throw new IllegalArgumentException("unsupported uri:" + paramUri);
    case 5:
      return cancelUploads(paramUri, paramString, paramArrayOfString);
    case 6:
      return cancelSingleUpload(paramUri);
    case 11:
      return cancelImmediateSync(paramUri);
    case 9:
    }
    if (resetSettings())
      return 1;
    return 0;
  }

  public String getType(Uri paramUri)
  {
    switch (this.mUriMatcher.match(paramUri))
    {
    case 9:
    case 10:
    case 11:
    default:
      throw new IllegalArgumentException("Invalid URI: " + paramUri);
    case 1:
      return "vnd.android.cursor.dir/vnd.google.android.picasasync.item";
    case 3:
      return "vnd.android.cursor.dir/vnd.google.android.picasasync.album";
    case 15:
      return "vnd.android.cursor.dir/vnd.google.android.picasasync.post";
    case 16:
      return "vnd.android.cursor.dir/vnd.google.android.picasasync.post_album";
    case 12:
      return "vnd.android.cursor.dir/vnd.google.android.picasasync.user";
    case 5:
      return "vnd.android.cursor.dir/vnd.google.android.picasasync.upload";
    case 7:
      return "vnd.android.cursor.dir/vnd.google.android.picasasync.upload_record";
    case 2:
      return "vnd.android.cursor.item/vnd.google.android.picasasync.item";
    case 4:
      return "vnd.android.cursor.item/vnd.google.android.picasasync.album";
    case 13:
      return "vnd.android.cursor.item/vnd.google.android.picasasync.user";
    case 6:
      return "vnd.android.cursor.item/vnd.google.android.picasasync.upload";
    case 8:
      return "vnd.android.cursor.item/vnd.google.android.picasasync.upload_record";
    case 14:
    }
    return "vnd.android.cursor.item/vnd.google.android.picasasync.album_cover";
  }

  // ERROR //
  public Uri insert(Uri paramUri, ContentValues paramContentValues)
  {
    // Byte code:
    //   0: new 54	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 57	java/lang/StringBuilder:<init>	()V
    //   7: ldc_w 692
    //   10: invokevirtual 63	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   13: aload_1
    //   14: invokevirtual 241	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   17: invokevirtual 72	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   20: invokestatic 697	com/google/android/picasastore/MetricsUtils:begin	(Ljava/lang/String;)I
    //   23: istore_3
    //   24: aload_0
    //   25: getfield 139	com/google/android/picasasync/PicasaContentProvider0:mUriMatcher	Landroid/content/UriMatcher;
    //   28: aload_1
    //   29: invokevirtual 395	android/content/UriMatcher:match	(Landroid/net/Uri;)I
    //   32: lookupswitch	default:+28->60, 5:+65->97, 10:+105->137
    //   61: nop
    //   62: ifle +22971 -> 23033
    //   65: nop
    //   66: istore 89
    //   68: invokespecial 57	java/lang/StringBuilder:<init>	()V
    //   71: ldc_w 653
    //   74: invokevirtual 63	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   77: aload_1
    //   78: invokevirtual 241	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   81: invokevirtual 72	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   84: invokespecial 163	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   87: athrow
    //   88: astore 4
    //   90: iload_3
    //   91: invokestatic 700	com/google/android/picasastore/MetricsUtils:end	(I)V
    //   94: aload 4
    //   96: athrow
    //   97: aload_0
    //   98: invokevirtual 171	com/google/android/picasasync/PicasaContentProvider0:getContext	()Landroid/content/Context;
    //   101: invokestatic 208	com/google/android/picasasync/UploadsManager:getInstance	(Landroid/content/Context;)Lcom/google/android/picasasync/UploadsManager;
    //   104: aload_2
    //   105: invokestatic 481	android/os/Binder:getCallingUid	()I
    //   108: invokestatic 704	com/google/android/picasasync/UploadTaskEntry:createNew	(Landroid/content/ContentValues;I)Lcom/google/android/picasasync/UploadTaskEntry;
    //   111: invokevirtual 708	com/google/android/picasasync/UploadsManager:addManualUpload	(Lcom/google/android/picasasync/UploadTaskEntry;)J
    //   114: lstore 6
    //   116: aload_0
    //   117: invokevirtual 171	com/google/android/picasasync/PicasaContentProvider0:getContext	()Landroid/content/Context;
    //   120: invokestatic 594	com/google/android/picasasync/PicasaFacade:get	(Landroid/content/Context;)Lcom/google/android/picasasync/PicasaFacade;
    //   123: lload 6
    //   125: invokevirtual 712	com/google/android/picasasync/PicasaFacade:getUploadUri	(J)Landroid/net/Uri;
    //   128: astore 8
    //   130: iload_3
    //   131: invokestatic 700	com/google/android/picasastore/MetricsUtils:end	(I)V
    //   134: aload 8
    //   136: areturn
    //   137: aload_0
    //   138: aload_1
    //   139: aload_2
    //   140: invokespecial 714	com/google/android/picasasync/PicasaContentProvider0:insertSyncRequest	(Landroid/net/Uri;Landroid/content/ContentValues;)Landroid/net/Uri;
    //   143: astore 5
    //   145: iload_3
    //   146: invokestatic 700	com/google/android/picasastore/MetricsUtils:end	(I)V
    //   149: aload 5
    //   151: areturn
    //
    // Exception table:
    //   from	to	target	type
    //   24	60	88	finally
    //   60	88	88	finally
    //   97	130	88	finally
    //   137	145	88	finally
  }

  public boolean onCreate()
  {
    this.mDbHelper = PicasaDatabaseHelper.get(getContext());
    return true;
  }

  public ParcelFileDescriptor openFile(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    int i = MetricsUtils.begin("OPEN " + Utils.maskDebugInfo(paramUri.toString()));
    int j;
    try
    {
      j = this.mUriMatcher.match(paramUri);
      throw new IllegalArgumentException("unsupported uri: " + paramUri);
    }
    finally
    {
      MetricsUtils.end(i);
    }
    Context localContext = getContext();
    Uri localUri1 = null;
    try
    {
      PicasaStoreFacade localPicasaStoreFacade = this.mPicasaStoreFacade;
      localUri1 = null;
      if (localPicasaStoreFacade == null)
        this.mPicasaStoreFacade = PicasaStoreFacade.get(localContext);
      localUri1 = paramUri.buildUpon().authority(this.mPicasaStoreFacade.getAuthority()).build();
      ParcelFileDescriptor localParcelFileDescriptor2 = localContext.getContentResolver().openFileDescriptor(localUri1, paramString);
      MetricsUtils.end(i);
      return localParcelFileDescriptor2;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      String str1 = paramUri.getQueryParameter("content_url");
      if ((paramString.contains("w")) || (str1 != null))
        throw localFileNotFoundException;
    }
    Log.d("PicasaContentProvider0", "FileNotFoundException, look up photo metadata for " + Utils.maskDebugInfo(paramUri.toString()));
    long l = getItemIdFromUri(paramUri);
    if (l == -1L)
      throw new FileNotFoundException(Utils.maskDebugInfo(paramUri.toString()));
    if (j == 14);
    for (String str2 = lookupAlbumCoverUrl(l); str2 == null; str2 = lookupContentUrl(l, paramUri.getQueryParameter("type")))
      throw localFileNotFoundException;
    Uri localUri2 = localUri1.buildUpon().appendQueryParameter("content_url", str2).build();
    ParcelFileDescriptor localParcelFileDescriptor1 = localContext.getContentResolver().openFileDescriptor(localUri2, paramString);
    MetricsUtils.end(i);
    return localParcelFileDescriptor1;
  }

  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    int i = MetricsUtils.begin("QUERY " + Utils.maskDebugInfo(paramUri.toString()));
    try
    {
      Cursor localCursor = queryInternal(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
      MetricsUtils.incrementQueryResultCount(localCursor.getCount());
      return localCursor;
    }
    finally
    {
      MetricsUtils.end(i);
    }
  }

  public int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
  {
    int i = 1;
    Integer localInteger;
    switch (this.mUriMatcher.match(paramUri))
    {
    default:
      throw new IllegalArgumentException("unsupported uri:" + paramUri);
    case 4:
      localInteger = paramContentValues.getAsInteger("cache_flag");
      if (localInteger == null)
        i = 0;
    case 9:
    }
    do
    {
      long l;
      do
      {
        return i;
        l = getLastSegmentAsLong(paramUri, -1L);
      }
      while (l == -1L);
      PrefetchHelper.get(getContext()).setAlbumCachingFlag(l, localInteger.intValue());
      return i;
    }
    while (updateSettings(paramContentValues));
    return 0;
  }

  private static class WhereEntry
  {
    private static final String TYPE_WHERE_CLAUSE;
    private static final String WHERE_CLAUSE = "album_id in (SELECT _id FROM " + PicasaContentProvider0.ALBUM_TABLE_NAME + " WHERE " + "album_type = 'Buzz' AND user_id = ?" + ")";
    public String selection;
    public String[] selectionArgs;
    public String sortLimit;
    public String sortOrder;

    static
    {
      TYPE_WHERE_CLAUSE = WHERE_CLAUSE + " AND " + "content_type" + " LIKE ?";
    }

    public WhereEntry(Uri paramUri, String paramString)
    {
      String str1 = paramUri.getQueryParameter("user_id");
      String str2 = paramUri.getQueryParameter("type");
      if ("image".equals(str2))
        this.selection = TYPE_WHERE_CLAUSE;
      for (this.selectionArgs = new String[] { str1, "image/%" }; ; this.selectionArgs = new String[] { str1 })
      {
        while (true)
        {
          this.sortLimit = paramUri.getQueryParameter("limit");
          this.sortOrder = paramString;
          return;
          if (!"video".equals(str2))
            break;
          this.selection = TYPE_WHERE_CLAUSE;
          this.selectionArgs = new String[] { str1, "video/%" };
        }
        this.selection = WHERE_CLAUSE;
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.PicasaContentProvider0
 * JD-Core Version:    0.5.4
 */