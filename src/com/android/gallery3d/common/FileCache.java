package com.android.gallery3d.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class FileCache
  implements Closeable
{
  private static final String FREESPACE_ORDER_BY;
  private static final String[] FREESPACE_PROJECTION;
  private static final String[] PROJECTION_SIZE_SUM;
  private static final String TABLE_NAME = FileEntry.SCHEMA.getTableName();
  private long mCapacity;
  private DatabaseHelper mDbHelper;
  private final LruCache<String, CacheEntry> mEntryMap = new LruCache(4);
  private boolean mInitialized = false;
  private File mRootDir;
  private long mTotalBytes;

  static
  {
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.format("sum(%s)", new Object[] { "size" });
    PROJECTION_SIZE_SUM = arrayOfString;
    FREESPACE_PROJECTION = new String[] { "_id", "filename", "content_url", "size" };
    FREESPACE_ORDER_BY = String.format("%s ASC", new Object[] { "last_access" });
  }

  public FileCache(Context paramContext, File paramFile, String paramString, long paramLong)
  {
    this.mRootDir = ((File)Utils.checkNotNull(paramFile));
    this.mCapacity = paramLong;
    this.mDbHelper = new DatabaseHelper(paramContext, paramString);
  }

  public static void deleteFiles(Context paramContext, File paramFile, String paramString)
  {
    try
    {
      paramContext.getDatabasePath(paramString).delete();
      if (paramFile.listFiles() == null)
        return;
      File[] arrayOfFile = paramFile.listFiles();
      int i = arrayOfFile.length;
      int j = 0;
      if (j >= i)
        return;
      File localFile = arrayOfFile[j];
      String str = localFile.getName();
      if ((localFile.isFile()) && (str.startsWith("download")) && (str.endsWith(".tmp")))
        localFile.delete();
      ++j;
    }
    catch (Throwable localThrowable)
    {
      Log.w("FileCache", "cannot reset database", localThrowable);
    }
  }

  private void freeSomeSpaceIfNeed(int paramInt)
  {
    Cursor localCursor = this.mDbHelper.getReadableDatabase().query(TABLE_NAME, FREESPACE_PROJECTION, null, null, null, null, FREESPACE_ORDER_BY);
    while (paramInt > 0)
    {
      long l1;
      String str1;
      long l2;
      try
      {
        if ((this.mTotalBytes <= this.mCapacity) || (!localCursor.moveToNext()))
          break label238;
        l1 = localCursor.getLong(0);
        str1 = localCursor.getString(1);
        String str2 = localCursor.getString(2);
        synchronized (this.mEntryMap)
        {
          if (!this.mEntryMap.containsKey(str2))
            break label129;
        }
      }
      finally
      {
        localCursor.close();
      }
      label129: monitorexit;
      --paramInt;
      if (new File(this.mRootDir, str1).delete())
      {
        this.mTotalBytes -= l2;
        SQLiteDatabase localSQLiteDatabase = this.mDbHelper.getWritableDatabase();
        String str3 = TABLE_NAME;
        String[] arrayOfString = new String[1];
        arrayOfString[0] = String.valueOf(l1);
        localSQLiteDatabase.delete(str3, "_id=?", arrayOfString);
      }
      Log.w("FileCache", "unable to delete file: " + str1);
    }
    label238: localCursor.close();
  }

  private void initialize()
  {
    monitorenter;
    while (true)
    {
      try
      {
        boolean bool = this.mInitialized;
        if (bool)
          return;
        if (!this.mRootDir.isDirectory())
        {
          this.mRootDir.mkdirs();
          throw new RuntimeException("cannot create: " + this.mRootDir.getAbsolutePath());
        }
      }
      finally
      {
        monitorexit;
      }
      Cursor localCursor = this.mDbHelper.getReadableDatabase().query(TABLE_NAME, PROJECTION_SIZE_SUM, null, null, null, null, null);
      try
      {
        if (localCursor.moveToNext())
          this.mTotalBytes = localCursor.getLong(0);
        localCursor.close();
        if (this.mTotalBytes > this.mCapacity)
          freeSomeSpaceIfNeed(16);
        this.mInitialized = true;
      }
      finally
      {
        localCursor.close();
      }
    }
  }

  private FileEntry queryDatabase(String paramString)
  {
    long l = Utils.crc64Long(paramString);
    String[] arrayOfString = new String[2];
    arrayOfString[0] = String.valueOf(l);
    arrayOfString[1] = paramString;
    Cursor localCursor = this.mDbHelper.getReadableDatabase().query(TABLE_NAME, FileEntry.SCHEMA.getProjection(), "hash_code=? AND content_url=?", arrayOfString, null, null, null);
    try
    {
      boolean bool = localCursor.moveToNext();
      if (!bool)
        return null;
      FileEntry localFileEntry = new FileEntry(null);
      FileEntry.SCHEMA.cursorToObject(localCursor, localFileEntry);
      updateLastAccess(localFileEntry.id);
      return localFileEntry;
    }
    finally
    {
      localCursor.close();
    }
  }

  private void updateLastAccess(long paramLong)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("last_access", Long.valueOf(System.currentTimeMillis()));
    SQLiteDatabase localSQLiteDatabase = this.mDbHelper.getWritableDatabase();
    String str = TABLE_NAME;
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(paramLong);
    localSQLiteDatabase.update(str, localContentValues, "_id=?", arrayOfString);
  }

  public void close()
  {
    this.mDbHelper.close();
  }

  public File createFile()
    throws IOException
  {
    return File.createTempFile("download", ".tmp", this.mRootDir);
  }

  // ERROR //
  public CacheEntry lookup(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 79	com/android/gallery3d/common/FileCache:mInitialized	Z
    //   4: ifne +7 -> 11
    //   7: aload_0
    //   8: invokespecial 290	com/android/gallery3d/common/FileCache:initialize	()V
    //   11: aload_0
    //   12: getfield 77	com/android/gallery3d/common/FileCache:mEntryMap	Lcom/android/gallery3d/common/LruCache;
    //   15: astore_2
    //   16: aload_2
    //   17: monitorenter
    //   18: aload_0
    //   19: getfield 77	com/android/gallery3d/common/FileCache:mEntryMap	Lcom/android/gallery3d/common/LruCache;
    //   22: aload_1
    //   23: invokevirtual 293	com/android/gallery3d/common/LruCache:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   26: checkcast 295	com/android/gallery3d/common/FileCache$CacheEntry
    //   29: astore 4
    //   31: aload_2
    //   32: monitorexit
    //   33: aload 4
    //   35: ifnull +31 -> 66
    //   38: aload_0
    //   39: monitorenter
    //   40: aload_0
    //   41: aload 4
    //   43: invokestatic 299	com/android/gallery3d/common/FileCache$CacheEntry:access$100	(Lcom/android/gallery3d/common/FileCache$CacheEntry;)J
    //   46: invokespecial 255	com/android/gallery3d/common/FileCache:updateLastAccess	(J)V
    //   49: aload_0
    //   50: monitorexit
    //   51: aload 4
    //   53: areturn
    //   54: astore_3
    //   55: aload_2
    //   56: monitorexit
    //   57: aload_3
    //   58: athrow
    //   59: astore 18
    //   61: aload_0
    //   62: monitorexit
    //   63: aload 18
    //   65: athrow
    //   66: aload_0
    //   67: monitorenter
    //   68: aload_0
    //   69: aload_1
    //   70: invokespecial 301	com/android/gallery3d/common/FileCache:queryDatabase	(Ljava/lang/String;)Lcom/android/gallery3d/common/FileCache$FileEntry;
    //   73: astore 6
    //   75: aload 6
    //   77: ifnonnull +7 -> 84
    //   80: aload_0
    //   81: monitorexit
    //   82: aconst_null
    //   83: areturn
    //   84: new 295	com/android/gallery3d/common/FileCache$CacheEntry
    //   87: dup
    //   88: aload 6
    //   90: getfield 251	com/android/gallery3d/common/Entry:id	J
    //   93: aload_1
    //   94: new 87	java/io/File
    //   97: dup
    //   98: aload_0
    //   99: getfield 89	com/android/gallery3d/common/FileCache:mRootDir	Ljava/io/File;
    //   102: aload 6
    //   104: getfield 303	com/android/gallery3d/common/FileCache$FileEntry:filename	Ljava/lang/String;
    //   107: invokespecial 181	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   110: aconst_null
    //   111: invokespecial 306	com/android/gallery3d/common/FileCache$CacheEntry:<init>	(JLjava/lang/String;Ljava/io/File;Lcom/android/gallery3d/common/FileCache$1;)V
    //   114: astore 7
    //   116: aload 7
    //   118: getfield 309	com/android/gallery3d/common/FileCache$CacheEntry:cacheFile	Ljava/io/File;
    //   121: invokevirtual 124	java/io/File:isFile	()Z
    //   124: istore 8
    //   126: iload 8
    //   128: ifne +107 -> 235
    //   131: aload_0
    //   132: getfield 98	com/android/gallery3d/common/FileCache:mDbHelper	Lcom/android/gallery3d/common/FileCache$DatabaseHelper;
    //   135: invokevirtual 184	com/android/gallery3d/common/FileCache$DatabaseHelper:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   138: astore 14
    //   140: getstatic 40	com/android/gallery3d/common/FileCache:TABLE_NAME	Ljava/lang/String;
    //   143: astore 15
    //   145: iconst_1
    //   146: anewarray 42	java/lang/String
    //   149: astore 16
    //   151: aload 16
    //   153: iconst_0
    //   154: aload 6
    //   156: getfield 251	com/android/gallery3d/common/Entry:id	J
    //   159: invokestatic 188	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   162: aastore
    //   163: aload 14
    //   165: aload 15
    //   167: ldc 190
    //   169: aload 16
    //   171: invokevirtual 193	android/database/sqlite/SQLiteDatabase:delete	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
    //   174: pop
    //   175: aload_0
    //   176: aload_0
    //   177: getfield 158	com/android/gallery3d/common/FileCache:mTotalBytes	J
    //   180: aload 6
    //   182: getfield 311	com/android/gallery3d/common/FileCache$FileEntry:size	J
    //   185: lsub
    //   186: putfield 158	com/android/gallery3d/common/FileCache:mTotalBytes	J
    //   189: aload_0
    //   190: monitorexit
    //   191: aconst_null
    //   192: areturn
    //   193: astore 12
    //   195: ldc 137
    //   197: new 195	java/lang/StringBuilder
    //   200: dup
    //   201: invokespecial 196	java/lang/StringBuilder:<init>	()V
    //   204: ldc_w 313
    //   207: invokevirtual 202	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   210: aload 6
    //   212: getfield 303	com/android/gallery3d/common/FileCache$FileEntry:filename	Ljava/lang/String;
    //   215: invokevirtual 202	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   218: invokevirtual 205	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   221: aload 12
    //   223: invokestatic 145	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   226: pop
    //   227: goto -38 -> 189
    //   230: aload_0
    //   231: monitorexit
    //   232: aload 5
    //   234: athrow
    //   235: aload_0
    //   236: getfield 77	com/android/gallery3d/common/FileCache:mEntryMap	Lcom/android/gallery3d/common/LruCache;
    //   239: astore 9
    //   241: aload 9
    //   243: monitorenter
    //   244: aload_0
    //   245: getfield 77	com/android/gallery3d/common/FileCache:mEntryMap	Lcom/android/gallery3d/common/LruCache;
    //   248: aload_1
    //   249: aload 7
    //   251: invokevirtual 316	com/android/gallery3d/common/LruCache:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   254: pop
    //   255: aload 9
    //   257: monitorexit
    //   258: aload_0
    //   259: monitorexit
    //   260: aload 7
    //   262: areturn
    //   263: astore 10
    //   265: aload 9
    //   267: monitorexit
    //   268: aload 10
    //   270: athrow
    //   271: astore 5
    //   273: goto -43 -> 230
    //   276: astore 5
    //   278: goto -48 -> 230
    //
    // Exception table:
    //   from	to	target	type
    //   18	33	54	finally
    //   55	57	54	finally
    //   40	51	59	finally
    //   61	63	59	finally
    //   131	189	193	java/lang/Throwable
    //   244	258	263	finally
    //   265	268	263	finally
    //   68	75	271	finally
    //   80	82	271	finally
    //   84	116	271	finally
    //   116	126	276	finally
    //   131	189	276	finally
    //   189	191	276	finally
    //   195	227	276	finally
    //   230	232	276	finally
    //   235	244	276	finally
    //   258	260	276	finally
    //   268	271	276	finally
  }

  public void store(String paramString, File paramFile)
  {
    if (!this.mInitialized)
      initialize();
    Utils.assertTrue(paramFile.getParentFile().equals(this.mRootDir));
    FileEntry localFileEntry1 = new FileEntry(null);
    localFileEntry1.hashCode = Utils.crc64Long(paramString);
    localFileEntry1.contentUrl = paramString;
    localFileEntry1.filename = paramFile.getName();
    localFileEntry1.size = paramFile.length();
    localFileEntry1.lastAccess = System.currentTimeMillis();
    if (localFileEntry1.size >= this.mCapacity)
    {
      paramFile.delete();
      throw new IllegalArgumentException("file too large: " + localFileEntry1.size);
    }
    monitorenter;
    try
    {
      FileEntry localFileEntry2 = queryDatabase(paramString);
      if (localFileEntry2 != null)
      {
        paramFile.delete();
        localFileEntry1.filename = localFileEntry2.filename;
        localFileEntry1.size = localFileEntry2.size;
        FileEntry.SCHEMA.insertOrReplace(this.mDbHelper.getWritableDatabase(), localFileEntry1);
        if (this.mTotalBytes > this.mCapacity)
          freeSomeSpaceIfNeed(16);
        return;
      }
    }
    finally
    {
      monitorexit;
    }
  }

  public static final class CacheEntry
  {
    public File cacheFile;
    public String contentUrl;
    private long id;

    private CacheEntry(long paramLong, String paramString, File paramFile)
    {
      this.id = paramLong;
      this.contentUrl = paramString;
      this.cacheFile = paramFile;
    }
  }

  private final class DatabaseHelper extends SQLiteOpenHelper
  {
    public DatabaseHelper(Context paramString, String arg3)
    {
      super(paramString, str, null, 1);
    }

    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      FileCache.FileEntry.SCHEMA.createTables(paramSQLiteDatabase);
      for (File localFile : FileCache.this.mRootDir.listFiles())
      {
        if (localFile.delete())
          continue;
        Log.w("FileCache", "fail to remove: " + localFile.getAbsolutePath());
      }
    }

    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
      FileCache.FileEntry.SCHEMA.dropTables(paramSQLiteDatabase);
      onCreate(paramSQLiteDatabase);
    }
  }

  @Entry.Table("files")
  private static class FileEntry extends Entry
  {
    public static final EntrySchema SCHEMA = new EntrySchema(FileEntry.class);

    @Entry.Column("content_url")
    public String contentUrl;

    @Entry.Column("filename")
    public String filename;

    @Entry.Column(indexed=true, value="hash_code")
    public long hashCode;

    @Entry.Column(indexed=true, value="last_access")
    public long lastAccess;

    @Entry.Column("size")
    public long size;

    public String toString()
    {
      return "hash_code: " + this.hashCode + ", " + "content_url" + this.contentUrl + ", " + "last_access" + this.lastAccess + ", " + "filename" + this.filename;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.common.FileCache
 * JD-Core Version:    0.5.4
 */