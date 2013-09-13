package com.google.android.picasasync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.content.SyncStats;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

class PicasaSyncHelper
{
  private static final String[] ALBUM_PROJECTION_ID_DATE_THUMBNAIL_URL;
  private static final String ALBUM_TABLE_NAME;
  private static final Object LOCK_KEY_ALL_ALBUMS;
  private static final String[] PHOTO_PROJECTION_ID_DATE;
  private static final String PHOTO_TABLE_NAME = PhotoEntry.SCHEMA.getTableName();
  private static final String[] PROJECTION_ID_ACCOUNT;
  private static final String[] PROJECTION_ID_DATE_INDEX_SCREENNAIL_URL;
  private static final String USER_TABLE_NAME;
  private static PicasaSyncHelper sInstance;
  private Context mContext;
  private PicasaDatabaseHelper mDbHelper;
  private SyncLockManager mLockManager = new SyncLockManager();
  private HashMap<String, AlbumEntry> mUploadAlbumMap = null;

  static
  {
    ALBUM_TABLE_NAME = AlbumEntry.SCHEMA.getTableName();
    USER_TABLE_NAME = UserEntry.SCHEMA.getTableName();
    PROJECTION_ID_ACCOUNT = new String[] { "_id", "account" };
    ALBUM_PROJECTION_ID_DATE_THUMBNAIL_URL = new String[] { "_id", "date_updated", "thumbnail_url" };
    PHOTO_PROJECTION_ID_DATE = new String[] { "_id", "date_updated" };
    PROJECTION_ID_DATE_INDEX_SCREENNAIL_URL = new String[] { "_id", "date_updated", "display_index", "screennail_url" };
    LOCK_KEY_ALL_ALBUMS = new Object();
  }

  private PicasaSyncHelper(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mDbHelper = PicasaDatabaseHelper.get(this.mContext);
  }

  private int deleteAlbum(SQLiteDatabase paramSQLiteDatabase, long paramLong)
  {
    String[] arrayOfString = new String[1];
    arrayOfString[0] = Long.toString(paramLong);
    int i = paramSQLiteDatabase.delete(PHOTO_TABLE_NAME, "album_id=?", arrayOfString);
    if (AlbumEntry.SCHEMA.deleteWithId(paramSQLiteDatabase, paramLong))
      ++i;
    return i;
  }

  private int deleteUploadedPhotos(SQLiteDatabase paramSQLiteDatabase, long paramLong)
  {
    String str = PHOTO_TABLE_NAME;
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(paramLong);
    return paramSQLiteDatabase.delete(str, "camera_sync=1 AND user_id=?", arrayOfString);
  }

  private void deleteUser(SQLiteDatabase paramSQLiteDatabase, String paramString)
  {
    paramSQLiteDatabase.beginTransaction();
    String[] arrayOfString1 = { paramString };
    Cursor localCursor;
    try
    {
      String[] arrayOfString2;
      try
      {
        arrayOfString2 = new String[1];
        if (!localCursor.moveToNext())
          break label95;
        arrayOfString2[0] = localCursor.getString(0);
      }
      finally
      {
        localCursor.close();
      }
    }
    finally
    {
      paramSQLiteDatabase.endTransaction();
    }
    label95: localCursor.close();
    paramSQLiteDatabase.delete(ALBUM_TABLE_NAME, "user_id=?", arrayOfString1);
    paramSQLiteDatabase.delete(USER_TABLE_NAME, "_id=?", arrayOfString1);
    paramSQLiteDatabase.setTransactionSuccessful();
    paramSQLiteDatabase.endTransaction();
  }

  private static Account[] getGoogleAccounts(Context paramContext)
  {
    return AccountManager.get(paramContext).getAccountsByType("com.google");
  }

  public static PicasaSyncHelper getInstance(Context paramContext)
  {
    monitorenter;
    try
    {
      if (sInstance == null)
        sInstance = new PicasaSyncHelper(paramContext);
      PicasaSyncHelper localPicasaSyncHelper = sInstance;
      return localPicasaSyncHelper;
    }
    finally
    {
      monitorexit;
    }
  }

  private AlbumEntry getPseudoUploadAlbum(String paramString)
  {
    if (this.mUploadAlbumMap == null)
      this.mUploadAlbumMap = new HashMap();
    AlbumEntry localAlbumEntry = (AlbumEntry)this.mUploadAlbumMap.get(paramString);
    if (localAlbumEntry == null)
    {
      localAlbumEntry = new AlbumEntry();
      localAlbumEntry.user = paramString;
      localAlbumEntry.id = 0L;
      this.mUploadAlbumMap.put(paramString, localAlbumEntry);
    }
    return localAlbumEntry;
  }

  private void syncAlbumsForUserLocked(SyncContext paramSyncContext, UserEntry paramUserEntry)
  {
    ArrayList localArrayList = new ArrayList();
    SQLiteDatabase localSQLiteDatabase = this.mDbHelper.getWritableDatabase();
    String str = ALBUM_TABLE_NAME;
    String[] arrayOfString1 = ALBUM_PROJECTION_ID_DATE_THUMBNAIL_URL;
    String[] arrayOfString2 = new String[1];
    arrayOfString2[0] = String.valueOf(paramUserEntry.id);
    Cursor localCursor = localSQLiteDatabase.query(str, arrayOfString1, "user_id=?", arrayOfString2, null, null, "date_updated");
    while (true)
      try
      {
        if (!localCursor.moveToNext())
          break label113;
        label113: localArrayList.add(new EntryMetadata(localCursor.getLong(0), localCursor.getLong(1), 0, localCursor.getString(2)));
      }
      finally
      {
        localCursor.close();
      }
    Collections.sort(localArrayList);
    1 local1 = new PicasaApi.EntryHandler(localArrayList, paramUserEntry, localSQLiteDatabase, paramSyncContext)
    {
      private PicasaSyncHelper.EntryMetadata mKey = new PicasaSyncHelper.EntryMetadata();

      public void handleEntry(ContentValues paramContentValues)
      {
        PicasaSyncHelper.EntryMetadata localEntryMetadata1 = this.mKey.updateId(paramContentValues.getAsLong("_id").longValue());
        int i = Collections.binarySearch(this.val$entries, localEntryMetadata1);
        PicasaSyncHelper.EntryMetadata localEntryMetadata2;
        if (i >= 0)
        {
          localEntryMetadata2 = (PicasaSyncHelper.EntryMetadata)this.val$entries.get(i);
          label43: Long localLong = paramContentValues.getAsLong("date_updated");
          String str1 = paramContentValues.getAsString("thumbnail_url");
          int j = 0;
          if (localEntryMetadata2 != null)
          {
            boolean bool = Utils.equals(localEntryMetadata2.url, str1);
            j = 0;
            if (!bool)
            {
              PrefetchHelper.invalidateAlbumCoverCache(localEntryMetadata2.id, localEntryMetadata2.url);
              j = 1;
            }
          }
          if ((j != 0) || (localEntryMetadata2 == null) || (localLong == null) || (localEntryMetadata2.dateEdited < localLong.longValue()))
          {
            paramContentValues.put("photos_dirty", Boolean.valueOf(true));
            paramContentValues.put("user_id", Long.valueOf(this.val$user.id));
            if (localEntryMetadata2 != null)
              break label218;
            this.val$db.replace(PicasaSyncHelper.ALBUM_TABLE_NAME, null, paramContentValues);
          }
        }
        while (true)
        {
          SyncStats localSyncStats = this.val$context.result.stats;
          localSyncStats.numUpdates = (1L + localSyncStats.numUpdates);
          if (localEntryMetadata2 != null)
            localEntryMetadata2.survived = true;
          return;
          localEntryMetadata2 = null;
          break label43:
          label218: SQLiteDatabase localSQLiteDatabase = this.val$db;
          String str2 = PicasaSyncHelper.ALBUM_TABLE_NAME;
          String[] arrayOfString = new String[1];
          arrayOfString[0] = String.valueOf(localEntryMetadata2.id);
          localSQLiteDatabase.update(str2, paramContentValues, "_id=?", arrayOfString);
        }
      }
    };
    int i = 1;
    for (int j = 0; ; ++j)
    {
      if (j <= 1)
      {
        i = paramSyncContext.api.getAlbums(paramUserEntry, local1);
        if (i == 2)
          break label293;
      }
      switch (i)
      {
      default:
        UserEntry.SCHEMA.insertOrReplace(localSQLiteDatabase, paramUserEntry);
        Iterator localIterator = localArrayList.iterator();
        while (true)
        {
          if (!localIterator.hasNext())
            break label347;
          EntryMetadata localEntryMetadata = (EntryMetadata)localIterator.next();
          if (localEntryMetadata.survived)
            continue;
          SyncStats localSyncStats3 = paramSyncContext.result.stats;
          localSyncStats3.numDeletes += deleteAlbum(localSQLiteDatabase, localEntryMetadata.id);
        }
        label293: paramSyncContext.refreshAuthToken();
      case 2:
      case 1:
      case 4:
      case 3:
      }
    }
    SyncStats localSyncStats2 = paramSyncContext.result.stats;
    localSyncStats2.numAuthExceptions = (1L + localSyncStats2.numAuthExceptions);
    return;
    SyncStats localSyncStats1 = paramSyncContext.result.stats;
    localSyncStats1.numParseExceptions = (1L + localSyncStats1.numParseExceptions);
    return;
    label347: notifyAlbumsChange();
  }

  private void syncPhotosForAlbumLocked(SyncContext paramSyncContext, AlbumEntry paramAlbumEntry)
  {
    ArrayList localArrayList = new ArrayList();
    SQLiteDatabase localSQLiteDatabase = this.mDbHelper.getWritableDatabase();
    String str = PHOTO_TABLE_NAME;
    String[] arrayOfString1 = PROJECTION_ID_DATE_INDEX_SCREENNAIL_URL;
    String[] arrayOfString2 = new String[1];
    arrayOfString2[0] = Long.toString(paramAlbumEntry.id);
    Cursor localCursor = localSQLiteDatabase.query(str, arrayOfString1, "album_id=?", arrayOfString2, null, null, "date_updated");
    while (true)
      try
      {
        if (!localCursor.moveToNext())
          break label120;
        label120: localArrayList.add(new EntryMetadata(localCursor.getLong(0), localCursor.getLong(1), localCursor.getInt(2), localCursor.getString(3)));
      }
      finally
      {
        localCursor.close();
      }
    Collections.sort(localArrayList);
    2 local2 = new PicasaApi.EntryHandler(localArrayList, paramAlbumEntry, localSQLiteDatabase, paramSyncContext)
    {
      private int mDisplayIndex = 0;
      private PicasaSyncHelper.EntryMetadata mKey = new PicasaSyncHelper.EntryMetadata();

      public void handleEntry(ContentValues paramContentValues)
      {
        PicasaSyncHelper.EntryMetadata localEntryMetadata1 = this.mKey.updateId(paramContentValues.getAsLong("_id").longValue());
        int i = Collections.binarySearch(this.val$entries, localEntryMetadata1);
        PicasaSyncHelper.EntryMetadata localEntryMetadata2;
        if (i >= 0)
        {
          localEntryMetadata2 = (PicasaSyncHelper.EntryMetadata)this.val$entries.get(i);
          label43: Long localLong = paramContentValues.getAsLong("date_updated");
          String str1 = paramContentValues.getAsString("screennail_url");
          if ((localEntryMetadata2 != null) && (!Utils.equals(localEntryMetadata2.url, str1)))
            PrefetchHelper.invalidatePhotoCache(localEntryMetadata2.id);
          if ((localEntryMetadata2 == null) || (localLong == null) || (localEntryMetadata2.dateEdited < localLong.longValue()) || (localEntryMetadata2.displayIndex != this.mDisplayIndex))
          {
            paramContentValues.put("display_index", Integer.valueOf(this.mDisplayIndex));
            paramContentValues.put("user_id", Long.valueOf(this.val$album.userId));
            if (localEntryMetadata2 != null)
              break label220;
            this.val$db.replace(PicasaSyncHelper.PHOTO_TABLE_NAME, null, paramContentValues);
          }
        }
        while (true)
        {
          SyncStats localSyncStats = this.val$context.result.stats;
          localSyncStats.numUpdates = (1L + localSyncStats.numUpdates);
          if (localEntryMetadata2 != null)
            localEntryMetadata2.survived = true;
          this.mDisplayIndex = (1 + this.mDisplayIndex);
          return;
          localEntryMetadata2 = null;
          break label43:
          label220: SQLiteDatabase localSQLiteDatabase = this.val$db;
          String str2 = PicasaSyncHelper.PHOTO_TABLE_NAME;
          String[] arrayOfString = new String[1];
          arrayOfString[0] = String.valueOf(localEntryMetadata2.id);
          localSQLiteDatabase.update(str2, paramContentValues, "_id=?", arrayOfString);
        }
      }
    };
    int i = 3;
    int j = 0;
    if (j <= 1)
    {
      label164: i = paramSyncContext.api.getAlbumPhotos(paramAlbumEntry, local2);
      if (i == 2)
        break label237;
    }
    switch (i)
    {
    default:
    case 1:
    case 4:
    case 2:
    case 3:
    case 5:
    case 0:
    }
    while (true)
    {
      notifyAlbumsChange();
      notifyPhotosChange();
      return;
      label237: paramSyncContext.refreshAuthToken();
      ++j;
      break label164:
      SyncStats localSyncStats4 = paramSyncContext.result.stats;
      localSyncStats4.numAuthExceptions = (1L + localSyncStats4.numAuthExceptions);
      return;
      SyncStats localSyncStats3 = paramSyncContext.result.stats;
      localSyncStats3.numParseExceptions = (1L + localSyncStats3.numParseExceptions);
      return;
      SyncStats localSyncStats2 = paramSyncContext.result.stats;
      localSyncStats2.numDeletes += deleteAlbum(localSQLiteDatabase, paramAlbumEntry.id);
      continue;
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
      {
        EntryMetadata localEntryMetadata = (EntryMetadata)localIterator.next();
        if (localEntryMetadata.survived)
          continue;
        PhotoEntry.SCHEMA.deleteWithId(localSQLiteDatabase, localEntryMetadata.id);
        SyncStats localSyncStats1 = paramSyncContext.result.stats;
        localSyncStats1.numDeletes = (1L + localSyncStats1.numDeletes);
      }
      paramAlbumEntry.photosDirty = false;
      AlbumEntry.SCHEMA.insertOrReplace(localSQLiteDatabase, paramAlbumEntry);
    }
  }

  private void syncPhotosForUploadLocked(SyncContext paramSyncContext, String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    SQLiteDatabase localSQLiteDatabase = this.mDbHelper.getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    Cursor localCursor;
    try
    {
      long l1 = this.mDbHelper.getUserId(paramString);
      return;
      String str = PHOTO_TABLE_NAME;
      String[] arrayOfString1 = PHOTO_PROJECTION_ID_DATE;
      String[] arrayOfString2 = new String[1];
      if (localCursor == null)
        break label167;
    }
    finally
    {
      try
      {
        if (!localCursor.moveToNext())
          break label160;
        localArrayList.add(new EntryMetadata(localCursor.getLong(0), localCursor.getLong(1), 0, null));
      }
      finally
      {
        localCursor.close();
      }
      localSQLiteDatabase.endTransaction();
    }
    label160: localCursor.close();
    label167: localSQLiteDatabase.endTransaction();
    long l2 = this.mDbHelper.getUserId(paramString);
    3 local3 = new PicasaApi.EntryHandler(localArrayList, l2, localSQLiteDatabase, paramSyncContext)
    {
      private PicasaSyncHelper.EntryMetadata mKey = new PicasaSyncHelper.EntryMetadata();

      public void handleEntry(ContentValues paramContentValues)
      {
        PicasaSyncHelper.EntryMetadata localEntryMetadata1 = this.mKey.updateId(paramContentValues.getAsLong("_id").longValue());
        int i = Collections.binarySearch(this.val$entries, localEntryMetadata1);
        PicasaSyncHelper.EntryMetadata localEntryMetadata2;
        if (i >= 0)
        {
          localEntryMetadata2 = (PicasaSyncHelper.EntryMetadata)this.val$entries.get(i);
          label43: Long localLong = paramContentValues.getAsLong("date_updated");
          if ((localEntryMetadata2 == null) || (localLong == null) || (localEntryMetadata2.dateEdited < localLong.longValue()))
          {
            paramContentValues.put("user_id", Long.valueOf(this.val$userId));
            if (localEntryMetadata2 != null)
              break label148;
            this.val$db.replace(PicasaSyncHelper.PHOTO_TABLE_NAME, null, paramContentValues);
          }
        }
        while (true)
        {
          SyncStats localSyncStats = this.val$context.result.stats;
          localSyncStats.numUpdates = (1L + localSyncStats.numUpdates);
          if (localEntryMetadata2 != null)
            localEntryMetadata2.survived = true;
          return;
          localEntryMetadata2 = null;
          break label43:
          label148: SQLiteDatabase localSQLiteDatabase = this.val$db;
          String str = PicasaSyncHelper.PHOTO_TABLE_NAME;
          String[] arrayOfString = new String[1];
          arrayOfString[0] = String.valueOf(localEntryMetadata2.id);
          localSQLiteDatabase.update(str, paramContentValues, "_id=?", arrayOfString);
        }
      }
    };
    AlbumEntry localAlbumEntry = getPseudoUploadAlbum(paramString);
    int i = 3;
    int j = 0;
    if (j <= 1)
    {
      label211: i = paramSyncContext.api.getUploadedPhotos(localAlbumEntry, local3);
      if (i == 2)
        break label285;
    }
    switch (i)
    {
    case 1:
    case 4:
    default:
    case 2:
    case 3:
    case 5:
      while (true)
      {
        notifyAlbumsChange();
        notifyPhotosChange();
        return;
        label285: paramSyncContext.refreshAuthToken();
        ++j;
        break label211:
        SyncStats localSyncStats4 = paramSyncContext.result.stats;
        localSyncStats4.numAuthExceptions = (1L + localSyncStats4.numAuthExceptions);
        return;
        SyncStats localSyncStats3 = paramSyncContext.result.stats;
        localSyncStats3.numParseExceptions = (1L + localSyncStats3.numParseExceptions);
        return;
        SyncStats localSyncStats2 = paramSyncContext.result.stats;
        localSyncStats2.numDeletes += deleteUploadedPhotos(localSQLiteDatabase, l2);
      }
    case 0:
    }
    Iterator localIterator = localArrayList.iterator();
    while (true)
    {
      if (localIterator.hasNext());
      EntryMetadata localEntryMetadata = (EntryMetadata)localIterator.next();
      if (localEntryMetadata.survived)
        continue;
      PhotoEntry.SCHEMA.deleteWithId(localSQLiteDatabase, localEntryMetadata.id);
      SyncStats localSyncStats1 = paramSyncContext.result.stats;
      localSyncStats1.numDeletes = (1L + localSyncStats1.numDeletes);
    }
  }

  public SyncContext createSyncContext(SyncResult paramSyncResult, Thread paramThread)
  {
    return new SyncContext(paramSyncResult, paramThread);
  }

  public UserEntry findUser(String paramString)
  {
    Cursor localCursor = this.mDbHelper.getReadableDatabase().query(USER_TABLE_NAME, UserEntry.SCHEMA.getProjection(), "account=?", new String[] { paramString }, null, null, null);
    while (true)
      try
      {
        if (localCursor.moveToNext())
        {
          localUserEntry = (UserEntry)UserEntry.SCHEMA.cursorToObject(localCursor, new UserEntry());
          return localUserEntry;
        }
        UserEntry localUserEntry = null;
      }
      finally
      {
        localCursor.close();
      }
  }

  public ArrayList<UserEntry> getUsers()
  {
    ArrayList localArrayList = new ArrayList();
    Cursor localCursor = this.mDbHelper.getReadableDatabase().query(USER_TABLE_NAME, UserEntry.SCHEMA.getProjection(), null, null, null, null, null);
    while (true)
      try
      {
        if (!localCursor.moveToNext())
          break label64;
        label64: localArrayList.add(UserEntry.SCHEMA.cursorToObject(localCursor, new UserEntry()));
      }
      finally
      {
        localCursor.close();
      }
    return localArrayList;
  }

  public SQLiteDatabase getWritableDatabase()
  {
    return this.mDbHelper.getWritableDatabase();
  }

  public boolean isPicasaAccount(String paramString)
    throws AuthenticatorException, IOException, OperationCanceledException
  {
    return ((Boolean)AccountManager.get(this.mContext).hasFeatures(new Account(paramString, "com.google"), new String[] { "service_lh2" }, null, null).getResult(30000L, TimeUnit.MILLISECONDS)).booleanValue();
  }

  public void notifyAlbumsChange()
  {
    this.mContext.getContentResolver().notifyChange(PicasaFacade.get(this.mContext).getAlbumsUri(), null, false);
  }

  public void notifyPhotosChange()
  {
    this.mContext.getContentResolver().notifyChange(PicasaFacade.get(this.mContext).getPhotosUri(), null, false);
  }

  public void syncAccounts(String paramString)
  {
    Log.d("PicasaSync", "sync account database");
    SQLiteDatabase localSQLiteDatabase = this.mDbHelper.getWritableDatabase();
    HashMap localHashMap = new HashMap();
    Cursor localCursor = localSQLiteDatabase.query(USER_TABLE_NAME, PROJECTION_ID_ACCOUNT, null, null, null, null, null);
    while (true)
      try
      {
        if (!localCursor.moveToNext())
          break label83;
        String str = localCursor.getString(0);
        label83: localHashMap.put(localCursor.getString(1), str);
      }
      finally
      {
        localCursor.close();
      }
    Account[] arrayOfAccount = getGoogleAccounts(this.mContext);
    Log.d("PicasaSync", "accounts in DB=" + localHashMap.size());
    if (arrayOfAccount != null)
    {
      boolean bool = PicasaFacade.get(this.mContext).isMaster();
      int i = arrayOfAccount.length;
      int j = 0;
      if (j < i)
      {
        label167: Account localAccount = arrayOfAccount[j];
        int k;
        if (localHashMap.remove(localAccount.name) != null)
        {
          k = 1;
          label197: if (ContentResolver.getIsSyncable(localAccount, paramString) <= 0)
            break label293;
        }
        for (int l = 1; ; l = 0)
        {
          if ((bool) && (k == 0) && (l != 0))
          {
            Log.d("PicasaSync", "add account to DB:" + Utils.maskDebugInfo(localAccount));
            UserEntry.SCHEMA.insertOrReplace(this.mDbHelper.getWritableDatabase(), new UserEntry(localAccount.name));
          }
          ++j;
          break label167:
          k = 0;
          label293: break label197:
        }
      }
    }
    if (localHashMap.isEmpty())
      return;
    Iterator localIterator = localHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Log.d("PicasaSync", "remove account:" + Utils.maskDebugInfo(localEntry.getKey()));
      deleteUser(localSQLiteDatabase, (String)localEntry.getValue());
    }
    notifyAlbumsChange();
    notifyPhotosChange();
    PicasaSyncManager.get(this.mContext).requestPrefetchSync();
  }

  // ERROR //
  public void syncAlbumsForUser(SyncContext paramSyncContext, UserEntry paramUserEntry)
  {
    // Byte code:
    //   0: new 445	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 446	java/lang/StringBuilder:<init>	()V
    //   7: ldc_w 519
    //   10: invokevirtual 452	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   13: aload_2
    //   14: getfield 521	com/google/android/picasasync/UserEntry:account	Ljava/lang/String;
    //   17: invokestatic 482	com/android/gallery3d/common/Utils:maskDebugInfo	(Ljava/lang/Object;)Ljava/lang/String;
    //   20: invokevirtual 452	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   23: invokevirtual 461	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   26: invokestatic 527	com/google/android/picasastore/MetricsUtils:begin	(Ljava/lang/String;)I
    //   29: istore 4
    //   31: aload_0
    //   32: getfield 85	com/google/android/picasasync/PicasaSyncHelper:mLockManager	Lcom/google/android/picasasync/SyncLockManager;
    //   35: iconst_0
    //   36: aload_2
    //   37: getfield 205	com/android/gallery3d/common/Entry:id	J
    //   40: invokestatic 530	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   43: invokevirtual 534	com/google/android/picasasync/SyncLockManager:acquireLock	(ILjava/lang/Object;)Lcom/google/android/picasasync/SyncLockManager$SyncLock;
    //   46: astore 5
    //   48: aload_1
    //   49: invokevirtual 537	com/google/android/picasasync/PicasaSyncHelper$SyncContext:syncInterrupted	()Z
    //   52: istore 7
    //   54: iload 7
    //   56: ifeq +14 -> 70
    //   59: aload 5
    //   61: invokevirtual 542	com/google/android/picasasync/SyncLockManager$SyncLock:unlock	()V
    //   64: iload 4
    //   66: invokestatic 546	com/google/android/picasastore/MetricsUtils:end	(I)V
    //   69: return
    //   70: aload_0
    //   71: aload_1
    //   72: aload_2
    //   73: invokespecial 548	com/google/android/picasasync/PicasaSyncHelper:syncAlbumsForUserLocked	(Lcom/google/android/picasasync/PicasaSyncHelper$SyncContext;Lcom/google/android/picasasync/UserEntry;)V
    //   76: aload 5
    //   78: invokevirtual 542	com/google/android/picasasync/SyncLockManager$SyncLock:unlock	()V
    //   81: iload 4
    //   83: invokestatic 546	com/google/android/picasastore/MetricsUtils:end	(I)V
    //   86: return
    //   87: astore_3
    //   88: invokestatic 554	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   91: invokevirtual 557	java/lang/Thread:interrupt	()V
    //   94: return
    //   95: astore 6
    //   97: aload 5
    //   99: invokevirtual 542	com/google/android/picasasync/SyncLockManager$SyncLock:unlock	()V
    //   102: iload 4
    //   104: invokestatic 546	com/google/android/picasastore/MetricsUtils:end	(I)V
    //   107: aload 6
    //   109: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   0	48	87	java/lang/InterruptedException
    //   59	69	87	java/lang/InterruptedException
    //   76	86	87	java/lang/InterruptedException
    //   97	110	87	java/lang/InterruptedException
    //   48	54	95	finally
    //   70	76	95	finally
  }

  // ERROR //
  public void syncPhotosForAlbum(SyncContext paramSyncContext, AlbumEntry paramAlbumEntry)
  {
    // Byte code:
    //   0: new 445	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 446	java/lang/StringBuilder:<init>	()V
    //   7: ldc_w 560
    //   10: invokevirtual 452	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   13: aload_2
    //   14: getfield 205	com/android/gallery3d/common/Entry:id	J
    //   17: invokestatic 530	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   20: invokestatic 482	com/android/gallery3d/common/Utils:maskDebugInfo	(Ljava/lang/Object;)Ljava/lang/String;
    //   23: invokevirtual 452	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   26: invokevirtual 461	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   29: invokestatic 527	com/google/android/picasastore/MetricsUtils:begin	(Ljava/lang/String;)I
    //   32: istore 4
    //   34: aload_0
    //   35: getfield 85	com/google/android/picasasync/PicasaSyncHelper:mLockManager	Lcom/google/android/picasasync/SyncLockManager;
    //   38: iconst_2
    //   39: getstatic 79	com/google/android/picasasync/PicasaSyncHelper:LOCK_KEY_ALL_ALBUMS	Ljava/lang/Object;
    //   42: invokevirtual 534	com/google/android/picasasync/SyncLockManager:acquireLock	(ILjava/lang/Object;)Lcom/google/android/picasasync/SyncLockManager$SyncLock;
    //   45: astore 5
    //   47: aload_0
    //   48: getfield 85	com/google/android/picasasync/PicasaSyncHelper:mLockManager	Lcom/google/android/picasasync/SyncLockManager;
    //   51: iconst_2
    //   52: aload_2
    //   53: getfield 205	com/android/gallery3d/common/Entry:id	J
    //   56: invokestatic 530	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   59: invokevirtual 534	com/google/android/picasasync/SyncLockManager:acquireLock	(ILjava/lang/Object;)Lcom/google/android/picasasync/SyncLockManager$SyncLock;
    //   62: astore 7
    //   64: aload_1
    //   65: invokevirtual 537	com/google/android/picasasync/PicasaSyncHelper$SyncContext:syncInterrupted	()Z
    //   68: istore 9
    //   70: iload 9
    //   72: ifeq +19 -> 91
    //   75: aload 7
    //   77: invokevirtual 542	com/google/android/picasasync/SyncLockManager$SyncLock:unlock	()V
    //   80: aload 5
    //   82: invokevirtual 542	com/google/android/picasasync/SyncLockManager$SyncLock:unlock	()V
    //   85: iload 4
    //   87: invokestatic 546	com/google/android/picasastore/MetricsUtils:end	(I)V
    //   90: return
    //   91: aload_0
    //   92: aload_1
    //   93: aload_2
    //   94: invokespecial 562	com/google/android/picasasync/PicasaSyncHelper:syncPhotosForAlbumLocked	(Lcom/google/android/picasasync/PicasaSyncHelper$SyncContext;Lcom/google/android/picasasync/AlbumEntry;)V
    //   97: aload 7
    //   99: invokevirtual 542	com/google/android/picasasync/SyncLockManager$SyncLock:unlock	()V
    //   102: aload 5
    //   104: invokevirtual 542	com/google/android/picasasync/SyncLockManager$SyncLock:unlock	()V
    //   107: iload 4
    //   109: invokestatic 546	com/google/android/picasastore/MetricsUtils:end	(I)V
    //   112: return
    //   113: astore_3
    //   114: invokestatic 554	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   117: invokevirtual 557	java/lang/Thread:interrupt	()V
    //   120: return
    //   121: astore 8
    //   123: aload 7
    //   125: invokevirtual 542	com/google/android/picasasync/SyncLockManager$SyncLock:unlock	()V
    //   128: aload 8
    //   130: athrow
    //   131: astore 6
    //   133: aload 5
    //   135: invokevirtual 542	com/google/android/picasasync/SyncLockManager$SyncLock:unlock	()V
    //   138: iload 4
    //   140: invokestatic 546	com/google/android/picasastore/MetricsUtils:end	(I)V
    //   143: aload 6
    //   145: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   0	47	113	java/lang/InterruptedException
    //   80	90	113	java/lang/InterruptedException
    //   102	112	113	java/lang/InterruptedException
    //   133	146	113	java/lang/InterruptedException
    //   64	70	121	finally
    //   91	97	121	finally
    //   47	64	131	finally
    //   75	80	131	finally
    //   97	102	131	finally
    //   123	131	131	finally
  }

  public void syncPhotosForUser(SyncContext paramSyncContext, UserEntry paramUserEntry)
  {
    SQLiteDatabase localSQLiteDatabase = this.mDbHelper.getReadableDatabase();
    String str = ALBUM_TABLE_NAME;
    String[] arrayOfString1 = AlbumEntry.SCHEMA.getProjection();
    String[] arrayOfString2 = new String[1];
    arrayOfString2[0] = String.valueOf(paramUserEntry.id);
    Cursor localCursor = localSQLiteDatabase.query(str, arrayOfString1, "user_id=?", arrayOfString2, null, null, null);
    AlbumEntry localAlbumEntry = new AlbumEntry();
    try
    {
      boolean bool;
      do
      {
        if (!localCursor.moveToNext())
          break;
        AlbumEntry.SCHEMA.cursorToObject(localCursor, localAlbumEntry);
        syncPhotosForAlbum(paramSyncContext, localAlbumEntry);
        bool = paramSyncContext.syncInterrupted();
      }
      while (!bool);
      return;
    }
    finally
    {
      localCursor.close();
    }
  }

  // ERROR //
  public void syncUploadedPhotos(SyncContext paramSyncContext, String paramString)
  {
    // Byte code:
    //   0: ldc_w 568
    //   3: invokestatic 527	com/google/android/picasastore/MetricsUtils:begin	(Ljava/lang/String;)I
    //   6: istore 4
    //   8: aload_0
    //   9: getfield 85	com/google/android/picasasync/PicasaSyncHelper:mLockManager	Lcom/google/android/picasasync/SyncLockManager;
    //   12: iconst_2
    //   13: getstatic 79	com/google/android/picasasync/PicasaSyncHelper:LOCK_KEY_ALL_ALBUMS	Ljava/lang/Object;
    //   16: invokevirtual 534	com/google/android/picasasync/SyncLockManager:acquireLock	(ILjava/lang/Object;)Lcom/google/android/picasasync/SyncLockManager$SyncLock;
    //   19: astore 5
    //   21: aload_1
    //   22: invokevirtual 537	com/google/android/picasasync/PicasaSyncHelper$SyncContext:syncInterrupted	()Z
    //   25: istore 7
    //   27: iload 7
    //   29: ifeq +14 -> 43
    //   32: aload 5
    //   34: invokevirtual 542	com/google/android/picasasync/SyncLockManager$SyncLock:unlock	()V
    //   37: iload 4
    //   39: invokestatic 546	com/google/android/picasastore/MetricsUtils:end	(I)V
    //   42: return
    //   43: aload_0
    //   44: aload_1
    //   45: aload_2
    //   46: invokespecial 570	com/google/android/picasasync/PicasaSyncHelper:syncPhotosForUploadLocked	(Lcom/google/android/picasasync/PicasaSyncHelper$SyncContext;Ljava/lang/String;)V
    //   49: aload 5
    //   51: invokevirtual 542	com/google/android/picasasync/SyncLockManager$SyncLock:unlock	()V
    //   54: iload 4
    //   56: invokestatic 546	com/google/android/picasastore/MetricsUtils:end	(I)V
    //   59: return
    //   60: astore_3
    //   61: invokestatic 554	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   64: invokevirtual 557	java/lang/Thread:interrupt	()V
    //   67: return
    //   68: astore 6
    //   70: aload 5
    //   72: invokevirtual 542	com/google/android/picasasync/SyncLockManager$SyncLock:unlock	()V
    //   75: iload 4
    //   77: invokestatic 546	com/google/android/picasastore/MetricsUtils:end	(I)V
    //   80: aload 6
    //   82: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   0	21	60	java/lang/InterruptedException
    //   32	42	60	java/lang/InterruptedException
    //   49	59	60	java/lang/InterruptedException
    //   70	83	60	java/lang/InterruptedException
    //   21	27	68	finally
    //   43	49	68	finally
  }

  private static final class EntryMetadata
    implements Comparable<EntryMetadata>
  {
    public long dateEdited;
    public int displayIndex;
    public long id;
    public boolean survived = false;
    public String url;

    public EntryMetadata()
    {
    }

    public EntryMetadata(long paramLong1, long paramLong2, int paramInt, String paramString)
    {
      this.id = paramLong1;
      this.dateEdited = paramLong2;
      this.displayIndex = paramInt;
      this.url = paramString;
    }

    public int compareTo(EntryMetadata paramEntryMetadata)
    {
      return Utils.compare(this.id, paramEntryMetadata.id);
    }

    public EntryMetadata updateId(long paramLong)
    {
      this.id = paramLong;
      return this;
    }
  }

  public class SyncContext
  {
    public PicasaApi api;
    private Account mAccount;
    private String mAuthToken;
    private volatile boolean mStopSync;
    private Thread mThread;
    public SyncResult result;

    public SyncContext(SyncResult paramThread, Thread arg3)
    {
      this.result = ((SyncResult)Utils.checkNotNull(paramThread));
      this.api = new PicasaApi(PicasaSyncHelper.this.mContext.getContentResolver());
      Object localObject;
      this.mThread = localObject;
    }

    public void refreshAuthToken()
    {
      AccountManager localAccountManager = AccountManager.get(PicasaSyncHelper.this.mContext);
      if (this.mAuthToken != null)
        localAccountManager.invalidateAuthToken("com.google", this.mAuthToken);
      this.mAuthToken = null;
      try
      {
        this.mAuthToken = localAccountManager.blockingGetAuthToken(this.mAccount, "lh2", true);
        this.api.setAuthToken(this.mAuthToken);
        if (this.mAuthToken == null)
        {
          Log.w("PicasaSync", "cannot get auth token: " + Utils.maskDebugInfo(this.mAccount.name));
          SyncStats localSyncStats = this.result.stats;
          localSyncStats.numAuthExceptions = (1L + localSyncStats.numAuthExceptions);
        }
        return;
      }
      catch (Exception localException)
      {
        Log.w("PicasaSync", "getAuthToken fail", localException);
      }
    }

    public boolean setAccount(String paramString)
    {
      if ((this.mAccount == null) || (!this.mAccount.name.equals(paramString)))
      {
        this.mAccount = new Account(paramString, "com.google");
        this.mAuthToken = null;
        refreshAuthToken();
      }
      return this.mAuthToken != null;
    }

    public void stopSync()
    {
      this.mStopSync = true;
      if (this.mThread != null)
        this.mThread.interrupt();
      this.api.abortCurrentOperation();
    }

    public boolean syncInterrupted()
    {
      return this.mStopSync;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.PicasaSyncHelper
 * JD-Core Version:    0.5.4
 */