package com.google.android.picasasync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;
import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import com.google.android.picasastore.MetricsUtils;
import java.util.HashMap;

class ImmediateSync
{
  private static ImmediateSync sInstance;
  private final HashMap<String, Task> mCompleteTaskMap = new HashMap();
  private final Context mContext;
  private final HashMap<String, Task> mPendingTaskMap = new HashMap();
  private final ThreadPool mThreadPool = new ThreadPool();

  private ImmediateSync(Context paramContext)
  {
    this.mContext = paramContext;
  }

  private void completeTask(Task paramTask)
  {
    monitorenter;
    String str;
    try
    {
      str = paramTask.taskId;
      if (this.mPendingTaskMap.remove(str) != paramTask)
        Log.d("ImmediateSync", "new task added, ignored old:" + str);
      do
      {
        return;
        this.mCompleteTaskMap.put(str, paramTask);
        Uri localUri = PicasaFacade.get(this.mContext).getSyncRequestUri().buildUpon().appendPath(str).build();
        this.mContext.getContentResolver().notifyChange(localUri, null, false);
      }
      while (paramTask.syncResultCode == 0);
    }
    finally
    {
      monitorexit;
    }
  }

  private String createTaskId(String paramString)
  {
    return String.valueOf(paramString.hashCode());
  }

  private String createTaskId(String paramString1, String paramString2)
  {
    return paramString1.hashCode() + "." + paramString2;
  }

  public static ImmediateSync get(Context paramContext)
  {
    monitorenter;
    try
    {
      if (sInstance == null)
        sInstance = new ImmediateSync(paramContext);
      ImmediateSync localImmediateSync = sInstance;
      return localImmediateSync;
    }
    finally
    {
      monitorexit;
    }
  }

  private void requestSyncAlbumList(String paramString, String[] paramArrayOfString)
  {
    1 local1 = new Task(paramString, paramArrayOfString)
    {
      // ERROR //
      protected int doSync()
      {
        // Byte code:
        //   0: invokestatic 32	android/content/ContentResolver:getMasterSyncAutomatically	()Z
        //   3: ifne +13 -> 16
        //   6: ldc 34
        //   8: ldc 36
        //   10: invokestatic 42	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
        //   13: pop
        //   14: iconst_0
        //   15: ireturn
        //   16: ldc 44
        //   18: invokestatic 50	com/google/android/picasastore/MetricsUtils:begin	(Ljava/lang/String;)I
        //   21: istore_1
        //   22: aload_0
        //   23: getfield 17	com/google/android/picasasync/ImmediateSync$1:this$0	Lcom/google/android/picasasync/ImmediateSync;
        //   26: invokestatic 54	com/google/android/picasasync/ImmediateSync:access$100	(Lcom/google/android/picasasync/ImmediateSync;)Landroid/content/Context;
        //   29: invokestatic 60	com/google/android/picasasync/PicasaSyncHelper:getInstance	(Landroid/content/Context;)Lcom/google/android/picasasync/PicasaSyncHelper;
        //   32: astore_2
        //   33: aload_0
        //   34: getfield 19	com/google/android/picasasync/ImmediateSync$1:val$accountArgs	[Ljava/lang/String;
        //   37: astore_3
        //   38: aload_3
        //   39: ifnonnull +123 -> 162
        //   42: aload_2
        //   43: invokevirtual 64	com/google/android/picasasync/PicasaSyncHelper:getUsers	()Ljava/util/ArrayList;
        //   46: astore 25
        //   48: new 66	java/util/ArrayList
        //   51: dup
        //   52: invokespecial 69	java/util/ArrayList:<init>	()V
        //   55: astore 26
        //   57: aload_0
        //   58: getfield 17	com/google/android/picasasync/ImmediateSync$1:this$0	Lcom/google/android/picasasync/ImmediateSync;
        //   61: invokestatic 54	com/google/android/picasasync/ImmediateSync:access$100	(Lcom/google/android/picasasync/ImmediateSync;)Landroid/content/Context;
        //   64: invokestatic 75	com/google/android/picasasync/PicasaFacade:get	(Landroid/content/Context;)Lcom/google/android/picasasync/PicasaFacade;
        //   67: invokevirtual 79	com/google/android/picasasync/PicasaFacade:getAuthority	()Ljava/lang/String;
        //   70: astore 27
        //   72: iconst_0
        //   73: istore 28
        //   75: aload 25
        //   77: invokeinterface 84 1 0
        //   82: istore 29
        //   84: iload 28
        //   86: iload 29
        //   88: if_icmpge +57 -> 145
        //   91: aload 25
        //   93: iload 28
        //   95: invokeinterface 87 2 0
        //   100: checkcast 89	com/google/android/picasasync/UserEntry
        //   103: getfield 93	com/google/android/picasasync/UserEntry:account	Ljava/lang/String;
        //   106: astore 30
        //   108: new 95	android/accounts/Account
        //   111: dup
        //   112: aload 30
        //   114: ldc 97
        //   116: invokespecial 100	android/accounts/Account:<init>	(Ljava/lang/String;Ljava/lang/String;)V
        //   119: astore 31
        //   121: aload 31
        //   123: aload 27
        //   125: invokestatic 104	android/content/ContentResolver:getSyncAutomatically	(Landroid/accounts/Account;Ljava/lang/String;)Z
        //   128: ifeq +11 -> 139
        //   131: aload 26
        //   133: aload 30
        //   135: invokevirtual 108	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   138: pop
        //   139: iinc 28 1
        //   142: goto -58 -> 84
        //   145: aload 26
        //   147: aload 26
        //   149: invokevirtual 109	java/util/ArrayList:size	()I
        //   152: anewarray 111	java/lang/String
        //   155: invokevirtual 115	java/util/ArrayList:toArray	([Ljava/lang/Object;)[Ljava/lang/Object;
        //   158: checkcast 116	[Ljava/lang/String;
        //   161: astore_3
        //   162: ldc 34
        //   164: new 118	java/lang/StringBuilder
        //   167: dup
        //   168: invokespecial 119	java/lang/StringBuilder:<init>	()V
        //   171: ldc 121
        //   173: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   176: aload_3
        //   177: arraylength
        //   178: invokevirtual 128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   181: ldc 130
        //   183: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   186: invokevirtual 133	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   189: invokestatic 42	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
        //   192: pop
        //   193: new 135	android/content/SyncResult
        //   196: dup
        //   197: invokespecial 136	android/content/SyncResult:<init>	()V
        //   200: astore 6
        //   202: aload_0
        //   203: getfield 17	com/google/android/picasasync/ImmediateSync$1:this$0	Lcom/google/android/picasasync/ImmediateSync;
        //   206: astore 7
        //   208: aload 7
        //   210: monitorenter
        //   211: aload_0
        //   212: invokevirtual 139	com/google/android/picasasync/ImmediateSync$1:syncInterrupted	()Z
        //   215: ifeq +14 -> 229
        //   218: aload 7
        //   220: monitorexit
        //   221: iload_1
        //   222: ldc 141
        //   224: invokestatic 145	com/google/android/picasastore/MetricsUtils:endWithReport	(ILjava/lang/String;)V
        //   227: iconst_1
        //   228: ireturn
        //   229: aload_0
        //   230: aload_2
        //   231: aload 6
        //   233: invokestatic 151	java/lang/Thread:currentThread	()Ljava/lang/Thread;
        //   236: invokevirtual 155	com/google/android/picasasync/PicasaSyncHelper:createSyncContext	(Landroid/content/SyncResult;Ljava/lang/Thread;)Lcom/google/android/picasasync/PicasaSyncHelper$SyncContext;
        //   239: putfield 159	com/google/android/picasasync/ImmediateSync$Task:syncContext	Lcom/google/android/picasasync/PicasaSyncHelper$SyncContext;
        //   242: aload 7
        //   244: monitorexit
        //   245: aload_3
        //   246: astore 9
        //   248: aload 9
        //   250: arraylength
        //   251: istore 10
        //   253: iconst_0
        //   254: istore 11
        //   256: iload 11
        //   258: iload 10
        //   260: if_icmpge +168 -> 428
        //   263: aload 9
        //   265: iload 11
        //   267: aaload
        //   268: astore 12
        //   270: aload_2
        //   271: aload 12
        //   273: invokevirtual 163	com/google/android/picasasync/PicasaSyncHelper:findUser	(Ljava/lang/String;)Lcom/google/android/picasasync/UserEntry;
        //   276: astore 13
        //   278: aload_2
        //   279: aload 12
        //   281: invokevirtual 167	com/google/android/picasasync/PicasaSyncHelper:isPicasaAccount	(Ljava/lang/String;)Z
        //   284: istore 24
        //   286: iload 24
        //   288: istore 16
        //   290: iload 16
        //   292: ifne +72 -> 364
        //   295: ldc 34
        //   297: new 118	java/lang/StringBuilder
        //   300: dup
        //   301: invokespecial 119	java/lang/StringBuilder:<init>	()V
        //   304: ldc 169
        //   306: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   309: aload 12
        //   311: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   314: invokevirtual 133	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   317: invokestatic 172	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
        //   320: pop
        //   321: iinc 11 1
        //   324: goto -68 -> 256
        //   327: astore 8
        //   329: aload 7
        //   331: monitorexit
        //   332: aload 8
        //   334: athrow
        //   335: astore 4
        //   337: iload_1
        //   338: ldc 141
        //   340: invokestatic 145	com/google/android/picasastore/MetricsUtils:endWithReport	(ILjava/lang/String;)V
        //   343: aload 4
        //   345: athrow
        //   346: astore 14
        //   348: ldc 34
        //   350: ldc 174
        //   352: aload 14
        //   354: invokestatic 177	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   357: pop
        //   358: iconst_0
        //   359: istore 16
        //   361: goto -71 -> 290
        //   364: aload 13
        //   366: ifnull +88 -> 454
        //   369: ldc 34
        //   371: new 118	java/lang/StringBuilder
        //   374: dup
        //   375: invokespecial 119	java/lang/StringBuilder:<init>	()V
        //   378: ldc 179
        //   380: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   383: aload 12
        //   385: invokestatic 185	com/android/gallery3d/common/Utils:maskDebugInfo	(Ljava/lang/Object;)Ljava/lang/String;
        //   388: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   391: invokevirtual 133	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   394: invokestatic 42	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
        //   397: pop
        //   398: aload_0
        //   399: getfield 159	com/google/android/picasasync/ImmediateSync$Task:syncContext	Lcom/google/android/picasasync/PicasaSyncHelper$SyncContext;
        //   402: aload 12
        //   404: invokevirtual 190	com/google/android/picasasync/PicasaSyncHelper$SyncContext:setAccount	(Ljava/lang/String;)Z
        //   407: pop
        //   408: aload_2
        //   409: aload_0
        //   410: getfield 159	com/google/android/picasasync/ImmediateSync$Task:syncContext	Lcom/google/android/picasasync/PicasaSyncHelper$SyncContext;
        //   413: aload 13
        //   415: invokevirtual 194	com/google/android/picasasync/PicasaSyncHelper:syncAlbumsForUser	(Lcom/google/android/picasasync/PicasaSyncHelper$SyncContext;Lcom/google/android/picasasync/UserEntry;)V
        //   418: aload_0
        //   419: getfield 159	com/google/android/picasasync/ImmediateSync$Task:syncContext	Lcom/google/android/picasasync/PicasaSyncHelper$SyncContext;
        //   422: invokevirtual 195	com/google/android/picasasync/PicasaSyncHelper$SyncContext:syncInterrupted	()Z
        //   425: ifeq -104 -> 321
        //   428: aload_0
        //   429: getfield 159	com/google/android/picasasync/ImmediateSync$Task:syncContext	Lcom/google/android/picasasync/PicasaSyncHelper$SyncContext;
        //   432: invokevirtual 195	com/google/android/picasasync/PicasaSyncHelper$SyncContext:syncInterrupted	()Z
        //   435: istore 19
        //   437: iload 19
        //   439: ifeq +47 -> 486
        //   442: iconst_1
        //   443: istore 21
        //   445: iload_1
        //   446: ldc 141
        //   448: invokestatic 145	com/google/android/picasastore/MetricsUtils:endWithReport	(ILjava/lang/String;)V
        //   451: iload 21
        //   453: ireturn
        //   454: ldc 34
        //   456: new 118	java/lang/StringBuilder
        //   459: dup
        //   460: invokespecial 119	java/lang/StringBuilder:<init>	()V
        //   463: ldc 197
        //   465: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   468: aload 12
        //   470: invokestatic 185	com/android/gallery3d/common/Utils:maskDebugInfo	(Ljava/lang/Object;)Ljava/lang/String;
        //   473: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   476: invokevirtual 133	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   479: invokestatic 172	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
        //   482: pop
        //   483: goto -65 -> 418
        //   486: aload 6
        //   488: invokevirtual 200	android/content/SyncResult:hasError	()Z
        //   491: istore 20
        //   493: iload 20
        //   495: ifeq +9 -> 504
        //   498: iconst_2
        //   499: istore 21
        //   501: goto -56 -> 445
        //   504: iconst_0
        //   505: istore 21
        //   507: goto -62 -> 445
        //
        // Exception table:
        //   from	to	target	type
        //   211	221	327	finally
        //   229	245	327	finally
        //   329	332	327	finally
        //   162	211	335	finally
        //   248	253	335	finally
        //   263	278	335	finally
        //   278	286	335	finally
        //   295	321	335	finally
        //   332	335	335	finally
        //   348	358	335	finally
        //   369	418	335	finally
        //   418	428	335	finally
        //   428	437	335	finally
        //   454	483	335	finally
        //   486	493	335	finally
        //   278	286	346	java/lang/Exception
      }
    };
    this.mPendingTaskMap.put(paramString, local1);
    this.mThreadPool.submit(local1);
  }

  public boolean cancelTask(String paramString)
  {
    monitorenter;
    int i;
    try
    {
      Log.d("ImmediateSync", "cancel sync " + paramString);
      Task localTask = (Task)this.mPendingTaskMap.get(paramString);
      if ((localTask != null) && (localTask.refCount > 0))
      {
        int j = -1 + localTask.refCount;
        localTask.refCount = j;
        if (j == 0)
          localTask.cancelSync();
        i = 1;
        return i;
      }
    }
    finally
    {
      monitorexit;
    }
  }

  public int getResult(String paramString)
  {
    monitorenter;
    Task localTask;
    int i;
    try
    {
      localTask = (Task)this.mCompleteTaskMap.get(paramString);
      if (localTask == null)
        localTask = (Task)this.mPendingTaskMap.get(paramString);
      if (localTask == null)
      {
        i = 3;
        return i;
      }
    }
    finally
    {
      monitorexit;
    }
  }

  public String requestSyncAlbum(String paramString)
  {
    monitorenter;
    PicasaDatabaseHelper localPicasaDatabaseHelper;
    AlbumEntry localAlbumEntry;
    try
    {
      localPicasaDatabaseHelper = PicasaDatabaseHelper.get(this.mContext);
      localAlbumEntry = localPicasaDatabaseHelper.getAlbumEntry(null, paramString);
      throw new IllegalArgumentException("album does not exist");
    }
    finally
    {
      monitorexit;
    }
    String str1 = localPicasaDatabaseHelper.getUserAccount(localAlbumEntry.userId);
    String str2 = createTaskId(str1, paramString);
    Task localTask = (Task)this.mPendingTaskMap.get(str2);
    if ((localTask != null) && (localTask.addRequester()))
      Log.d("ImmediateSync", "task already exists:" + str2);
    while (true)
    {
      monitorexit;
      return str2;
      this.mCompleteTaskMap.remove(str2);
      2 local2 = new Task(str2, str1, localAlbumEntry)
      {
        protected int doSync()
        {
          int i = 1;
          if (!ContentResolver.getMasterSyncAutomatically())
          {
            Log.d("ImmediateSync", "master auto sync is off");
            return 0;
          }
          if (!ContentResolver.getSyncAutomatically(new Account(this.val$account, "com.google"), PicasaFacade.get(ImmediateSync.this.mContext).getAuthority()))
          {
            Log.d("ImmediateSync", "auto sync is off on " + Utils.maskDebugInfo(this.val$account));
            return 0;
          }
          int j = MetricsUtils.begin("ImmediateSync.album");
          while (true)
          {
            SyncResult localSyncResult;
            try
            {
              PicasaSyncHelper localPicasaSyncHelper = PicasaSyncHelper.getInstance(ImmediateSync.this.mContext);
              synchronized (ImmediateSync.this)
              {
                if (syncInterrupted())
                {
                  MetricsUtils.endWithReport(j, "picasa.sync.metadata");
                  return i;
                }
                this.syncContext = localPicasaSyncHelper.createSyncContext(localSyncResult, Thread.currentThread());
                this.syncContext.setAccount(this.val$account);
                Log.d("ImmediateSync", "sync album for " + Utils.maskDebugInfo(this.val$account) + "/" + this.val$album.id);
                localPicasaSyncHelper.syncPhotosForAlbum(this.syncContext, this.val$album);
                boolean bool1 = this.syncContext.syncInterrupted();
                if (!bool1)
                  break label264;
                MetricsUtils.endWithReport(j, "picasa.sync.metadata");
                return i;
              }
            }
            finally
            {
              MetricsUtils.endWithReport(j, "picasa.sync.metadata");
            }
            label264: boolean bool2 = localSyncResult.hasError();
            if (bool2)
              i = 2;
            i = 0;
          }
        }
      };
      this.mPendingTaskMap.put(str2, local2);
      this.mThreadPool.submit(local2);
    }
  }

  public String requestSyncAlbumListForAccount(String paramString)
  {
    monitorenter;
    String str;
    try
    {
      str = createTaskId(paramString);
      Task localTask = (Task)this.mPendingTaskMap.get(str);
      if ((localTask != null) && (localTask.addRequester()))
      {
        Log.d("ImmediateSync", "task already exists:" + str);
        return str;
      }
      this.mCompleteTaskMap.remove(str);
    }
    finally
    {
      monitorexit;
    }
  }

  public String requestSyncAlbumListForAllAccounts()
  {
    monitorenter;
    String str;
    try
    {
      Task localTask = (Task)this.mPendingTaskMap.get("all");
      if ((localTask != null) && (localTask.addRequester()))
      {
        Log.d("ImmediateSync", "task already exists:all");
        str = "all";
        return str;
      }
      this.mCompleteTaskMap.remove("all");
      requestSyncAlbumList("all", (String[])null);
    }
    finally
    {
      monitorexit;
    }
  }

  private abstract class Task
    implements ThreadPool.Job<Void>
  {
    public int refCount = 1;
    public PicasaSyncHelper.SyncContext syncContext;
    public int syncResultCode = -1;
    public final String taskId;

    Task(String arg2)
    {
      Object localObject;
      this.taskId = localObject;
    }

    boolean addRequester()
    {
      if ((this.syncResultCode == -1) || (this.syncResultCode == 0))
      {
        this.refCount = (1 + this.refCount);
        return true;
      }
      return false;
    }

    void cancelSync()
    {
      this.syncResultCode = 1;
      if (this.syncContext == null)
        return;
      this.syncContext.stopSync();
    }

    protected abstract int doSync();

    public Void run(ThreadPool.JobContext paramJobContext)
    {
      try
      {
        int i;
        synchronized (ImmediateSync.this)
        {
          if (this.syncResultCode == -1)
            this.syncResultCode = i;
          ImmediateSync.this.completeTask(this);
          return null;
        }
      }
      finally
      {
        ImmediateSync.this.completeTask(this);
      }
    }

    protected boolean syncInterrupted()
    {
      label26: for (int i = 1; ; i = 0)
        synchronized (ImmediateSync.this)
        {
          if (this.syncResultCode != i)
            break label26;
          return i;
        }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.ImmediateSync
 * JD-Core Version:    0.5.4
 */