package com.android.gallery3d.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.LruCache;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.CancelListener;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

public class DownloadCache
{
  private static final String FREESPACE_ORDER_BY;
  private static final String[] FREESPACE_PROJECTION;
  private static final String[] QUERY_PROJECTION;
  private static final String[] SUM_PROJECTION;
  private static final String TABLE_NAME = DownloadEntry.SCHEMA.getTableName();
  private static final String WHERE_HASH_AND_URL;
  private final GalleryApp mApplication;
  private final long mCapacity;
  private final SQLiteDatabase mDatabase;
  private final LruCache<String, Entry> mEntryMap = new LruCache(4);
  private boolean mInitialized = false;
  private final File mRoot;
  private final HashMap<String, DownloadTask> mTaskMap = new HashMap();
  private long mTotalBytes = 0L;

  static
  {
    QUERY_PROJECTION = new String[] { "_id", "_data" };
    WHERE_HASH_AND_URL = String.format("%s = ? AND %s = ?", new Object[] { "hash_code", "content_url" });
    FREESPACE_PROJECTION = new String[] { "_id", "_data", "content_url", "_size" };
    FREESPACE_ORDER_BY = String.format("%s ASC", new Object[] { "last_access" });
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.format("sum(%s)", new Object[] { "_size" });
    SUM_PROJECTION = arrayOfString;
  }

  public DownloadCache(GalleryApp paramGalleryApp, File paramFile, long paramLong)
  {
    this.mRoot = ((File)Utils.checkNotNull(paramFile));
    this.mApplication = ((GalleryApp)Utils.checkNotNull(paramGalleryApp));
    this.mCapacity = paramLong;
    this.mDatabase = new DatabaseHelper(paramGalleryApp.getAndroidContext()).getWritableDatabase();
  }

  // ERROR //
  private Entry findEntryInDatabase(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 153	com/android/gallery3d/common/Utils:crc64Long	(Ljava/lang/String;)J
    //   4: lstore_2
    //   5: iconst_2
    //   6: anewarray 47	java/lang/String
    //   9: astore 4
    //   11: aload 4
    //   13: iconst_0
    //   14: lload_2
    //   15: invokestatic 157	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   18: aastore
    //   19: aload 4
    //   21: iconst_1
    //   22: aload_1
    //   23: aastore
    //   24: aload_0
    //   25: getfield 130	com/android/gallery3d/data/DownloadCache:mDatabase	Landroid/database/sqlite/SQLiteDatabase;
    //   28: getstatic 45	com/android/gallery3d/data/DownloadCache:TABLE_NAME	Ljava/lang/String;
    //   31: getstatic 53	com/android/gallery3d/data/DownloadCache:QUERY_PROJECTION	[Ljava/lang/String;
    //   34: getstatic 65	com/android/gallery3d/data/DownloadCache:WHERE_HASH_AND_URL	Ljava/lang/String;
    //   37: aload 4
    //   39: aconst_null
    //   40: aconst_null
    //   41: aconst_null
    //   42: invokevirtual 163	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   45: astore 5
    //   47: aload 5
    //   49: invokeinterface 169 1 0
    //   54: ifeq +120 -> 174
    //   57: new 107	java/io/File
    //   60: dup
    //   61: aload 5
    //   63: iconst_1
    //   64: invokeinterface 173 2 0
    //   69: invokespecial 176	java/io/File:<init>	(Ljava/lang/String;)V
    //   72: astore 7
    //   74: aload 5
    //   76: iconst_0
    //   77: invokeinterface 180 2 0
    //   82: i2l
    //   83: lstore 8
    //   85: aload_0
    //   86: getfield 90	com/android/gallery3d/data/DownloadCache:mEntryMap	Lcom/android/gallery3d/common/LruCache;
    //   89: astore 10
    //   91: aload 10
    //   93: monitorenter
    //   94: aload_0
    //   95: getfield 90	com/android/gallery3d/data/DownloadCache:mEntryMap	Lcom/android/gallery3d/common/LruCache;
    //   98: aload_1
    //   99: invokevirtual 183	com/android/gallery3d/common/LruCache:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   102: checkcast 185	com/android/gallery3d/data/DownloadCache$Entry
    //   105: astore 12
    //   107: aload 12
    //   109: ifnonnull +32 -> 141
    //   112: new 185	com/android/gallery3d/data/DownloadCache$Entry
    //   115: dup
    //   116: aload_0
    //   117: lload 8
    //   119: aload 7
    //   121: invokespecial 188	com/android/gallery3d/data/DownloadCache$Entry:<init>	(Lcom/android/gallery3d/data/DownloadCache;JLjava/io/File;)V
    //   124: astore 13
    //   126: aload_0
    //   127: getfield 90	com/android/gallery3d/data/DownloadCache:mEntryMap	Lcom/android/gallery3d/common/LruCache;
    //   130: aload_1
    //   131: aload 13
    //   133: invokevirtual 192	com/android/gallery3d/common/LruCache:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   136: pop
    //   137: aload 13
    //   139: astore 12
    //   141: aload 10
    //   143: monitorexit
    //   144: aload 5
    //   146: invokeinterface 195 1 0
    //   151: aload 12
    //   153: areturn
    //   154: astore 11
    //   156: aload 10
    //   158: monitorexit
    //   159: aload 11
    //   161: athrow
    //   162: astore 6
    //   164: aload 5
    //   166: invokeinterface 195 1 0
    //   171: aload 6
    //   173: athrow
    //   174: aload 5
    //   176: invokeinterface 195 1 0
    //   181: aconst_null
    //   182: areturn
    //   183: astore 11
    //   185: goto -29 -> 156
    //
    // Exception table:
    //   from	to	target	type
    //   94	107	154	finally
    //   112	126	154	finally
    //   141	144	154	finally
    //   156	159	154	finally
    //   47	94	162	finally
    //   159	162	162	finally
    //   126	137	183	finally
  }

  // ERROR //
  private void freeSomeSpaceIfNeed(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 97	com/android/gallery3d/data/DownloadCache:mTotalBytes	J
    //   6: lstore_3
    //   7: aload_0
    //   8: getfield 115	com/android/gallery3d/data/DownloadCache:mCapacity	J
    //   11: lstore 5
    //   13: lload_3
    //   14: lload 5
    //   16: lcmp
    //   17: ifgt +6 -> 23
    //   20: aload_0
    //   21: monitorexit
    //   22: return
    //   23: aload_0
    //   24: getfield 130	com/android/gallery3d/data/DownloadCache:mDatabase	Landroid/database/sqlite/SQLiteDatabase;
    //   27: getstatic 45	com/android/gallery3d/data/DownloadCache:TABLE_NAME	Ljava/lang/String;
    //   30: getstatic 69	com/android/gallery3d/data/DownloadCache:FREESPACE_PROJECTION	[Ljava/lang/String;
    //   33: aconst_null
    //   34: aconst_null
    //   35: aconst_null
    //   36: aconst_null
    //   37: getstatic 75	com/android/gallery3d/data/DownloadCache:FREESPACE_ORDER_BY	Ljava/lang/String;
    //   40: invokevirtual 163	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   43: astore 7
    //   45: iload_1
    //   46: ifle +186 -> 232
    //   49: aload_0
    //   50: getfield 97	com/android/gallery3d/data/DownloadCache:mTotalBytes	J
    //   53: aload_0
    //   54: getfield 115	com/android/gallery3d/data/DownloadCache:mCapacity	J
    //   57: lcmp
    //   58: ifle +174 -> 232
    //   61: aload 7
    //   63: invokeinterface 169 1 0
    //   68: ifeq +164 -> 232
    //   71: aload 7
    //   73: iconst_0
    //   74: invokeinterface 199 2 0
    //   79: lstore 9
    //   81: aload 7
    //   83: iconst_2
    //   84: invokeinterface 173 2 0
    //   89: astore 11
    //   91: aload 7
    //   93: iconst_3
    //   94: invokeinterface 199 2 0
    //   99: lstore 12
    //   101: aload 7
    //   103: iconst_1
    //   104: invokeinterface 173 2 0
    //   109: astore 14
    //   111: aload_0
    //   112: getfield 90	com/android/gallery3d/data/DownloadCache:mEntryMap	Lcom/android/gallery3d/common/LruCache;
    //   115: astore 15
    //   117: aload 15
    //   119: monitorenter
    //   120: aload_0
    //   121: getfield 90	com/android/gallery3d/data/DownloadCache:mEntryMap	Lcom/android/gallery3d/common/LruCache;
    //   124: aload 11
    //   126: invokevirtual 203	com/android/gallery3d/common/LruCache:containsKey	(Ljava/lang/Object;)Z
    //   129: istore 17
    //   131: aload 15
    //   133: monitorexit
    //   134: iload 17
    //   136: ifne -91 -> 45
    //   139: iinc 1 255
    //   142: aload_0
    //   143: aload_0
    //   144: getfield 97	com/android/gallery3d/data/DownloadCache:mTotalBytes	J
    //   147: lload 12
    //   149: lsub
    //   150: putfield 97	com/android/gallery3d/data/DownloadCache:mTotalBytes	J
    //   153: new 107	java/io/File
    //   156: dup
    //   157: aload 14
    //   159: invokespecial 176	java/io/File:<init>	(Ljava/lang/String;)V
    //   162: invokevirtual 206	java/io/File:delete	()Z
    //   165: pop
    //   166: aload_0
    //   167: getfield 130	com/android/gallery3d/data/DownloadCache:mDatabase	Landroid/database/sqlite/SQLiteDatabase;
    //   170: astore 19
    //   172: getstatic 45	com/android/gallery3d/data/DownloadCache:TABLE_NAME	Ljava/lang/String;
    //   175: astore 20
    //   177: iconst_1
    //   178: anewarray 47	java/lang/String
    //   181: astore 21
    //   183: aload 21
    //   185: iconst_0
    //   186: lload 9
    //   188: invokestatic 157	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   191: aastore
    //   192: aload 19
    //   194: aload 20
    //   196: ldc 208
    //   198: aload 21
    //   200: invokevirtual 211	android/database/sqlite/SQLiteDatabase:delete	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
    //   203: pop
    //   204: goto -159 -> 45
    //   207: astore 8
    //   209: aload 7
    //   211: invokeinterface 195 1 0
    //   216: aload 8
    //   218: athrow
    //   219: astore_2
    //   220: aload_0
    //   221: monitorexit
    //   222: aload_2
    //   223: athrow
    //   224: astore 16
    //   226: aload 15
    //   228: monitorexit
    //   229: aload 16
    //   231: athrow
    //   232: aload 7
    //   234: invokeinterface 195 1 0
    //   239: goto -219 -> 20
    //
    // Exception table:
    //   from	to	target	type
    //   49	120	207	finally
    //   142	204	207	finally
    //   229	232	207	finally
    //   2	13	219	finally
    //   23	45	219	finally
    //   209	219	219	finally
    //   232	239	219	finally
    //   120	134	224	finally
    //   226	229	224	finally
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
        this.mInitialized = true;
        if (!this.mRoot.isDirectory())
          this.mRoot.mkdirs();
        throw new RuntimeException("cannot create " + this.mRoot.getAbsolutePath());
      }
      finally
      {
        monitorexit;
      }
      Cursor localCursor = this.mDatabase.query(TABLE_NAME, SUM_PROJECTION, null, null, null, null, null);
      this.mTotalBytes = 0L;
      try
      {
        if (localCursor.moveToNext())
          this.mTotalBytes = localCursor.getLong(0);
        localCursor.close();
        if (this.mTotalBytes > this.mCapacity);
        freeSomeSpaceIfNeed(16);
      }
      finally
      {
        localCursor.close();
      }
    }
  }

  private long insertEntry(String paramString, File paramFile)
  {
    monitorenter;
    try
    {
      long l1 = paramFile.length();
      this.mTotalBytes = (l1 + this.mTotalBytes);
      ContentValues localContentValues = new ContentValues();
      String str = String.valueOf(Utils.crc64Long(paramString));
      localContentValues.put("_data", paramFile.getAbsolutePath());
      localContentValues.put("hash_code", str);
      localContentValues.put("content_url", paramString);
      localContentValues.put("_size", Long.valueOf(l1));
      localContentValues.put("last_updated", Long.valueOf(System.currentTimeMillis()));
      long l2 = this.mDatabase.insert(TABLE_NAME, "", localContentValues);
      monitorexit;
      return l2;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  private void updateLastAccess(long paramLong)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("last_access", Long.valueOf(System.currentTimeMillis()));
    SQLiteDatabase localSQLiteDatabase = this.mDatabase;
    String str = TABLE_NAME;
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(paramLong);
    localSQLiteDatabase.update(str, localContentValues, "_id = ?", arrayOfString);
  }

  public Entry download(ThreadPool.JobContext paramJobContext, URL paramURL)
  {
    if (!this.mInitialized)
      initialize();
    String str = paramURL.toString();
    TaskProxy localTaskProxy;
    synchronized (this.mEntryMap)
    {
      Entry localEntry1 = (Entry)this.mEntryMap.get(str);
      if (localEntry1 != null)
      {
        updateLastAccess(localEntry1.mId);
        return localEntry1;
      }
      localTaskProxy = new TaskProxy();
      synchronized (this.mTaskMap)
      {
        Entry localEntry2 = findEntryInDatabase(str);
        if (localEntry2 == null)
          break label122;
        updateLastAccess(localEntry2.mId);
        return localEntry2;
      }
    }
    label122: DownloadTask localDownloadTask = (DownloadTask)this.mTaskMap.get(str);
    if (localDownloadTask == null)
    {
      localDownloadTask = new DownloadTask(str);
      this.mTaskMap.put(str, localDownloadTask);
      DownloadTask.access$002(localDownloadTask, this.mApplication.getThreadPool().submit(localDownloadTask, localDownloadTask));
    }
    localDownloadTask.addProxy(localTaskProxy);
    monitorexit;
    return localTaskProxy.get(paramJobContext);
  }

  private final class DatabaseHelper extends SQLiteOpenHelper
  {
    public DatabaseHelper(Context arg2)
    {
      super(localContext, "download.db", null, 2);
    }

    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      DownloadEntry.SCHEMA.createTables(paramSQLiteDatabase);
      for (File localFile : DownloadCache.this.mRoot.listFiles())
      {
        if (localFile.delete())
          continue;
        Log.w("DownloadCache", "fail to remove: " + localFile.getAbsolutePath());
      }
    }

    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
      DownloadEntry.SCHEMA.dropTables(paramSQLiteDatabase);
      onCreate(paramSQLiteDatabase);
    }
  }

  private class DownloadTask
    implements ThreadPool.Job<File>, FutureListener<File>
  {
    private Future<File> mFuture;
    private HashSet<DownloadCache.TaskProxy> mProxySet = new HashSet();
    private final String mUrl;

    public DownloadTask(String arg2)
    {
      Object localObject;
      this.mUrl = ((String)Utils.checkNotNull(localObject));
    }

    public void addProxy(DownloadCache.TaskProxy paramTaskProxy)
    {
      DownloadCache.TaskProxy.access$302(paramTaskProxy, this);
      this.mProxySet.add(paramTaskProxy);
    }

    // ERROR //
    public void onFutureDone(Future<File> paramFuture)
    {
      // Byte code:
      //   0: aload_1
      //   1: invokeinterface 65 1 0
      //   6: checkcast 67	java/io/File
      //   9: astore_2
      //   10: lconst_0
      //   11: lstore_3
      //   12: aload_2
      //   13: ifnull +16 -> 29
      //   16: aload_0
      //   17: getfield 23	com/android/gallery3d/data/DownloadCache$DownloadTask:this$0	Lcom/android/gallery3d/data/DownloadCache;
      //   20: aload_0
      //   21: getfield 41	com/android/gallery3d/data/DownloadCache$DownloadTask:mUrl	Ljava/lang/String;
      //   24: aload_2
      //   25: invokestatic 73	com/android/gallery3d/data/DownloadCache:access$400	(Lcom/android/gallery3d/data/DownloadCache;Ljava/lang/String;Ljava/io/File;)J
      //   28: lstore_3
      //   29: aload_1
      //   30: invokeinterface 77 1 0
      //   35: ifeq +14 -> 49
      //   38: aload_0
      //   39: getfield 31	com/android/gallery3d/data/DownloadCache$DownloadTask:mProxySet	Ljava/util/HashSet;
      //   42: invokevirtual 80	java/util/HashSet:isEmpty	()Z
      //   45: invokestatic 84	com/android/gallery3d/common/Utils:assertTrue	(Z)V
      //   48: return
      //   49: aload_0
      //   50: getfield 23	com/android/gallery3d/data/DownloadCache$DownloadTask:this$0	Lcom/android/gallery3d/data/DownloadCache;
      //   53: invokestatic 88	com/android/gallery3d/data/DownloadCache:access$200	(Lcom/android/gallery3d/data/DownloadCache;)Ljava/util/HashMap;
      //   56: astore 5
      //   58: aload 5
      //   60: monitorenter
      //   61: aload_0
      //   62: getfield 23	com/android/gallery3d/data/DownloadCache$DownloadTask:this$0	Lcom/android/gallery3d/data/DownloadCache;
      //   65: invokestatic 92	com/android/gallery3d/data/DownloadCache:access$500	(Lcom/android/gallery3d/data/DownloadCache;)Lcom/android/gallery3d/common/LruCache;
      //   68: astore 7
      //   70: aload 7
      //   72: monitorenter
      //   73: aconst_null
      //   74: astore 8
      //   76: aload_2
      //   77: ifnull +49 -> 126
      //   80: new 94	com/android/gallery3d/data/DownloadCache$Entry
      //   83: dup
      //   84: aload_0
      //   85: getfield 23	com/android/gallery3d/data/DownloadCache$DownloadTask:this$0	Lcom/android/gallery3d/data/DownloadCache;
      //   88: lload_3
      //   89: aload_2
      //   90: invokespecial 97	com/android/gallery3d/data/DownloadCache$Entry:<init>	(Lcom/android/gallery3d/data/DownloadCache;JLjava/io/File;)V
      //   93: astore 12
      //   95: aload_0
      //   96: getfield 23	com/android/gallery3d/data/DownloadCache$DownloadTask:this$0	Lcom/android/gallery3d/data/DownloadCache;
      //   99: invokestatic 92	com/android/gallery3d/data/DownloadCache:access$500	(Lcom/android/gallery3d/data/DownloadCache;)Lcom/android/gallery3d/common/LruCache;
      //   102: aload_0
      //   103: getfield 41	com/android/gallery3d/data/DownloadCache$DownloadTask:mUrl	Ljava/lang/String;
      //   106: aload 12
      //   108: invokevirtual 103	com/android/gallery3d/common/LruCache:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      //   111: ifnonnull +63 -> 174
      //   114: iconst_1
      //   115: istore 13
      //   117: iload 13
      //   119: invokestatic 84	com/android/gallery3d/common/Utils:assertTrue	(Z)V
      //   122: aload 12
      //   124: astore 8
      //   126: aload 7
      //   128: monitorexit
      //   129: aload_0
      //   130: getfield 31	com/android/gallery3d/data/DownloadCache$DownloadTask:mProxySet	Ljava/util/HashSet;
      //   133: invokevirtual 107	java/util/HashSet:iterator	()Ljava/util/Iterator;
      //   136: astore 10
      //   138: aload 10
      //   140: invokeinterface 112 1 0
      //   145: ifeq +43 -> 188
      //   148: aload 10
      //   150: invokeinterface 115 1 0
      //   155: checkcast 49	com/android/gallery3d/data/DownloadCache$TaskProxy
      //   158: aload 8
      //   160: invokevirtual 119	com/android/gallery3d/data/DownloadCache$TaskProxy:setResult	(Lcom/android/gallery3d/data/DownloadCache$Entry;)V
      //   163: goto -25 -> 138
      //   166: astore 6
      //   168: aload 5
      //   170: monitorexit
      //   171: aload 6
      //   173: athrow
      //   174: iconst_0
      //   175: istore 13
      //   177: goto -60 -> 117
      //   180: astore 9
      //   182: aload 7
      //   184: monitorexit
      //   185: aload 9
      //   187: athrow
      //   188: aload_0
      //   189: getfield 23	com/android/gallery3d/data/DownloadCache$DownloadTask:this$0	Lcom/android/gallery3d/data/DownloadCache;
      //   192: invokestatic 88	com/android/gallery3d/data/DownloadCache:access$200	(Lcom/android/gallery3d/data/DownloadCache;)Ljava/util/HashMap;
      //   195: aload_0
      //   196: getfield 41	com/android/gallery3d/data/DownloadCache$DownloadTask:mUrl	Ljava/lang/String;
      //   199: invokevirtual 124	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
      //   202: pop
      //   203: aload_0
      //   204: getfield 23	com/android/gallery3d/data/DownloadCache$DownloadTask:this$0	Lcom/android/gallery3d/data/DownloadCache;
      //   207: bipush 16
      //   209: invokestatic 128	com/android/gallery3d/data/DownloadCache:access$600	(Lcom/android/gallery3d/data/DownloadCache;I)V
      //   212: aload 5
      //   214: monitorexit
      //   215: return
      //   216: astore 9
      //   218: goto -36 -> 182
      //
      // Exception table:
      //   from	to	target	type
      //   61	73	166	finally
      //   129	138	166	finally
      //   138	163	166	finally
      //   168	171	166	finally
      //   185	188	166	finally
      //   188	215	166	finally
      //   80	95	180	finally
      //   126	129	180	finally
      //   182	185	180	finally
      //   95	114	216	finally
      //   117	122	216	finally
    }

    public void removeProxy(DownloadCache.TaskProxy paramTaskProxy)
    {
      synchronized (DownloadCache.this.mTaskMap)
      {
        Utils.assertTrue(this.mProxySet.remove(paramTaskProxy));
        if (this.mProxySet.isEmpty())
        {
          this.mFuture.cancel();
          DownloadCache.this.mTaskMap.remove(this.mUrl);
        }
        return;
      }
    }

    public File run(ThreadPool.JobContext paramJobContext)
    {
      paramJobContext.setMode(2);
      File localFile = null;
      try
      {
        URL localURL = new URL(this.mUrl);
        localFile = File.createTempFile("cache", ".tmp", DownloadCache.this.mRoot);
        paramJobContext.setMode(2);
        boolean bool = DownloadUtils.requestDownload(paramJobContext, localURL, localFile);
        paramJobContext.setMode(0);
        if (bool)
          return localFile;
        paramJobContext.setMode(0);
        if (localFile != null);
        return null;
      }
      catch (Exception localException)
      {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = this.mUrl;
        Log.e("DownloadCache", String.format("fail to download %s", arrayOfObject), localException);
      }
      finally
      {
        paramJobContext.setMode(0);
      }
    }
  }

  public class Entry
  {
    public File cacheFile;
    protected long mId;

    Entry(long arg2, File arg4)
    {
      this.mId = ???;
      Object localObject;
      this.cacheFile = ((File)Utils.checkNotNull(localObject));
    }
  }

  public static class TaskProxy
  {
    private DownloadCache.Entry mEntry;
    private boolean mIsCancelled = false;
    private DownloadCache.DownloadTask mTask;

    public DownloadCache.Entry get(ThreadPool.JobContext paramJobContext)
    {
      monitorenter;
      try
      {
        paramJobContext.setCancelListener(new ThreadPool.CancelListener()
        {
          public void onCancel()
          {
            DownloadCache.TaskProxy.this.mTask.removeProxy(DownloadCache.TaskProxy.this);
            synchronized (DownloadCache.TaskProxy.this)
            {
              DownloadCache.TaskProxy.access$702(DownloadCache.TaskProxy.this, true);
              DownloadCache.TaskProxy.this.notifyAll();
              return;
            }
          }
        });
        if (this.mIsCancelled)
          break label61;
        DownloadCache.Entry localEntry2 = this.mEntry;
        if (localEntry2 != null)
          break label61;
      }
      finally
      {
        monitorexit;
      }
      label61: paramJobContext.setCancelListener(null);
      DownloadCache.Entry localEntry1 = this.mEntry;
      monitorexit;
      return localEntry1;
    }

    void setResult(DownloadCache.Entry paramEntry)
    {
      monitorenter;
      try
      {
        boolean bool = this.mIsCancelled;
        if (bool)
          return;
        this.mEntry = paramEntry;
      }
      finally
      {
        monitorexit;
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.DownloadCache
 * JD-Core Version:    0.5.4
 */