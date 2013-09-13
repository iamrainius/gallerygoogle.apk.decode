package com.google.android.picasasync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SyncResult;
import android.content.SyncStats;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.net.TrafficStatsCompat;
import android.util.Log;
import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Fingerprint;
import com.android.gallery3d.common.Utils;
import com.google.android.picasastore.MetricsUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;

class UploadsManager
{
  private static Field EXIF_TAG_APERTURE;
  private static Field EXIF_TAG_EXPOSURE_TIME;
  private static Field EXIF_TAG_ISO;
  private static final Uri EXTERNAL_STORAGE_FSID_URI;
  private static final String PHOTO_TABLE_NAME;
  private static final String[] PROJECTION_COUNT;
  private static final String[] PROJECTION_DATA;
  private static final String[] PROJECTION_ENABLE_ACCOUNT_WIFI;
  private static final String[] PROJECTION_FINGERPRINT;
  private static final String QUERY_MAX_DISPLAY_INDEX;
  private static final String TAG = UploadsManager.class.getSimpleName();
  private static final String UPLOAD_RECORD_TABLE_NAME;
  private static final String UPLOAD_TASK_TABLE_NAME = UploadTaskEntry.SCHEMA.getTableName();
  private static UploadsManager sInstance;
  private static final float[] sLatlon;
  private final AccountManager mAccountManager;
  private final Context mContext;
  private UploadTask mCurrent;
  private int mExternalStorageFsId;
  private final Handler mHandler;
  private volatile boolean mIsExternalStorageFsIdReady = false;
  private final PicasaDatabaseHelper mPicasaDbHelper;
  private HashSet<String> mProblematicAccounts = new HashSet();
  private long mReloadSystemSettingDelay = 500L;
  private long mResetDelay = 15000L;
  private final SharedPreferences mSettings;
  private final PicasaSyncHelper mSyncHelper;
  private boolean mSyncOnBattery;
  private boolean mSyncOnRoaming;
  private HashSet<String> mSyncedAccountAlbumPairs = new HashSet();
  private String mUploadUrl;
  private final UploadsDatabaseHelper mUploadsDbHelper;
  private boolean mWifiOnlyPhoto;
  private boolean mWifiOnlyVideo;

  static
  {
    UPLOAD_RECORD_TABLE_NAME = UploadedEntry.SCHEMA.getTableName();
    PHOTO_TABLE_NAME = PhotoEntry.SCHEMA.getTableName();
    PROJECTION_FINGERPRINT = new String[] { "fingerprint" };
    PROJECTION_ENABLE_ACCOUNT_WIFI = new String[] { "sync_on_wifi_only", "video_upload_wifi_only", "sync_on_roaming", "sync_on_battery" };
    PROJECTION_DATA = new String[] { "_data" };
    PROJECTION_COUNT = new String[] { "COUNT(*)" };
    EXTERNAL_STORAGE_FSID_URI = Uri.parse("content://media/external/fs_id");
    QUERY_MAX_DISPLAY_INDEX = "select MAX(display_index) from " + PHOTO_TABLE_NAME;
    EXIF_TAG_APERTURE = null;
    EXIF_TAG_EXPOSURE_TIME = null;
    EXIF_TAG_ISO = null;
    try
    {
      if (Build.VERSION.SDK_INT >= 11)
      {
        EXIF_TAG_APERTURE = ExifInterface.class.getField("TAG_APERTURE");
        EXIF_TAG_EXPOSURE_TIME = ExifInterface.class.getField("TAG_EXPOSURE_TIME");
        EXIF_TAG_ISO = ExifInterface.class.getField("TAG_ISO");
      }
      sLatlon = new float[2];
      return;
    }
    catch (Exception localException)
    {
      Log.d(TAG, "get exif fields", localException);
    }
  }

  private UploadsManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mAccountManager = AccountManager.get(paramContext);
    this.mUploadsDbHelper = new UploadsDatabaseHelper(paramContext);
    this.mPicasaDbHelper = PicasaDatabaseHelper.get(paramContext);
    this.mSettings = PreferenceManager.getDefaultSharedPreferences(this.mContext);
    this.mSyncHelper = PicasaSyncHelper.getInstance(paramContext);
    HandlerThread localHandlerThread = new HandlerThread("picasa-uploads-manager", 10);
    localHandlerThread.start();
    this.mHandler = initHandler(localHandlerThread);
    loadSavedStates();
    if (isAndroidUpgraded())
      reset();
    while (true)
    {
      paramContext.getContentResolver().registerContentObserver(EXTERNAL_STORAGE_FSID_URI, false, new ContentObserver(this.mHandler)
      {
        public void onChange(boolean paramBoolean)
        {
          UploadsManager.this.onFsIdChangedInternal();
        }
      });
      Message.obtain(this.mHandler, 3).sendToTarget();
      return;
      reloadSystemSettingsInternal();
    }
  }

  private boolean accountExists(String paramString)
  {
    Account[] arrayOfAccount = this.mAccountManager.getAccountsByType("com.google");
    int i = arrayOfAccount.length;
    for (int j = 0; j < i; ++j)
      if (arrayOfAccount[j].name.equals(paramString))
        return true;
    return false;
  }

  private void cancelTaskInternal(long paramLong)
  {
    monitorenter;
    try
    {
      if ((this.mCurrent == null) || (!this.mCurrent.cancelTask(paramLong)))
      {
        UploadTaskEntry localUploadTaskEntry = getTaskFromDb(paramLong);
        if (localUploadTaskEntry != null)
        {
          removeTaskFromDb(paramLong);
          localUploadTaskEntry.setState(8);
          recordResult(new UploadedEntry(localUploadTaskEntry));
          notifyManualUploadDbChanges(true);
        }
      }
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  // ERROR //
  private UploadedEntry doUpload(UploadTaskEntry paramUploadTaskEntry, Uploader.UploadProgressListener paramUploadProgressListener, SyncResult paramSyncResult)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_1
    //   3: invokevirtual 442	com/google/android/picasasync/UploadTaskEntry:isReadyForUpload	()Z
    //   6: ifeq +56 -> 62
    //   9: aload_1
    //   10: iconst_1
    //   11: invokevirtual 421	com/google/android/picasasync/UploadTaskEntry:setState	(I)V
    //   14: aload_0
    //   15: monitorexit
    //   16: ldc2_w 443
    //   19: lstore 5
    //   21: iconst_0
    //   22: istore 7
    //   24: aload_0
    //   25: aload_1
    //   26: invokevirtual 447	com/google/android/picasasync/UploadTaskEntry:getUploadUrl	()Ljava/lang/String;
    //   29: putfield 449	com/google/android/picasasync/UploadsManager:mUploadUrl	Ljava/lang/String;
    //   32: new 451	com/google/android/picasasync/GDataUploader
    //   35: dup
    //   36: aload_0
    //   37: getfield 196	com/google/android/picasasync/UploadsManager:mContext	Landroid/content/Context;
    //   40: invokespecial 452	com/google/android/picasasync/GDataUploader:<init>	(Landroid/content/Context;)V
    //   43: astore 8
    //   45: aload 8
    //   47: aload_1
    //   48: aload_2
    //   49: invokevirtual 456	com/google/android/picasasync/GDataUploader:upload	(Lcom/google/android/picasasync/UploadTaskEntry;Lcom/google/android/picasasync/Uploader$UploadProgressListener;)Lcom/google/android/picasasync/UploadedEntry;
    //   52: astore 35
    //   54: aload 8
    //   56: invokevirtual 459	com/google/android/picasasync/GDataUploader:close	()V
    //   59: aload 35
    //   61: areturn
    //   62: aload_0
    //   63: monitorexit
    //   64: aconst_null
    //   65: areturn
    //   66: astore 4
    //   68: aload_0
    //   69: monitorexit
    //   70: aload 4
    //   72: athrow
    //   73: astore 33
    //   75: getstatic 69	com/google/android/picasasync/UploadsManager:TAG	Ljava/lang/String;
    //   78: new 128	java/lang/StringBuilder
    //   81: dup
    //   82: invokespecial 131	java/lang/StringBuilder:<init>	()V
    //   85: ldc_w 461
    //   88: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   91: aload_1
    //   92: invokevirtual 464	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   95: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   98: aload 33
    //   100: invokestatic 467	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   103: pop
    //   104: aload_0
    //   105: aload_1
    //   106: bipush 6
    //   108: aload 33
    //   110: invokespecial 384	com/google/android/picasasync/UploadsManager:setState	(Lcom/google/android/picasasync/UploadTaskEntry;ILjava/lang/Throwable;)V
    //   113: aload 8
    //   115: invokevirtual 459	com/google/android/picasasync/GDataUploader:close	()V
    //   118: aconst_null
    //   119: areturn
    //   120: astore 25
    //   122: getstatic 69	com/google/android/picasasync/UploadsManager:TAG	Ljava/lang/String;
    //   125: new 128	java/lang/StringBuilder
    //   128: dup
    //   129: invokespecial 131	java/lang/StringBuilder:<init>	()V
    //   132: ldc_w 469
    //   135: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   138: aload_1
    //   139: invokevirtual 464	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   142: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   145: aload 25
    //   147: invokestatic 467	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   150: pop
    //   151: aload 8
    //   153: invokevirtual 459	com/google/android/picasasync/GDataUploader:close	()V
    //   156: aload_0
    //   157: monitorenter
    //   158: aload_1
    //   159: invokevirtual 472	com/google/android/picasasync/UploadTaskEntry:isUploading	()Z
    //   162: ifeq +497 -> 659
    //   165: iinc 7 1
    //   168: iload 7
    //   170: iconst_5
    //   171: if_icmple +441 -> 612
    //   174: getstatic 69	com/google/android/picasasync/UploadsManager:TAG	Ljava/lang/String;
    //   177: ldc_w 474
    //   180: invokestatic 477	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   183: pop
    //   184: aload_1
    //   185: iconst_5
    //   186: new 435	java/io/IOException
    //   189: dup
    //   190: ldc_w 479
    //   193: invokespecial 482	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   196: invokevirtual 485	com/google/android/picasasync/UploadTaskEntry:setState	(ILjava/lang/Throwable;)V
    //   199: aload_3
    //   200: getfield 491	android/content/SyncResult:stats	Landroid/content/SyncStats;
    //   203: astore 29
    //   205: aload 29
    //   207: lconst_1
    //   208: aload 29
    //   210: getfield 496	android/content/SyncStats:numSkippedEntries	J
    //   213: ladd
    //   214: putfield 496	android/content/SyncStats:numSkippedEntries	J
    //   217: aload_0
    //   218: monitorexit
    //   219: aconst_null
    //   220: areturn
    //   221: astore 27
    //   223: aload_0
    //   224: monitorexit
    //   225: aload 27
    //   227: athrow
    //   228: astore 22
    //   230: getstatic 69	com/google/android/picasasync/UploadsManager:TAG	Ljava/lang/String;
    //   233: new 128	java/lang/StringBuilder
    //   236: dup
    //   237: invokespecial 131	java/lang/StringBuilder:<init>	()V
    //   240: ldc_w 498
    //   243: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   246: aload_1
    //   247: invokevirtual 464	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   250: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   253: aload 22
    //   255: invokestatic 467	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   258: pop
    //   259: aload_3
    //   260: getfield 491	android/content/SyncResult:stats	Landroid/content/SyncStats;
    //   263: astore 24
    //   265: aload 24
    //   267: lconst_1
    //   268: aload 24
    //   270: getfield 501	android/content/SyncStats:numAuthExceptions	J
    //   273: ladd
    //   274: putfield 501	android/content/SyncStats:numAuthExceptions	J
    //   277: aload_0
    //   278: aload_1
    //   279: bipush 9
    //   281: aload 22
    //   283: invokespecial 384	com/google/android/picasasync/UploadsManager:setState	(Lcom/google/android/picasasync/UploadTaskEntry;ILjava/lang/Throwable;)V
    //   286: aload 8
    //   288: invokevirtual 459	com/google/android/picasasync/GDataUploader:close	()V
    //   291: aconst_null
    //   292: areturn
    //   293: astore 20
    //   295: getstatic 69	com/google/android/picasasync/UploadsManager:TAG	Ljava/lang/String;
    //   298: new 128	java/lang/StringBuilder
    //   301: dup
    //   302: invokespecial 131	java/lang/StringBuilder:<init>	()V
    //   305: ldc_w 503
    //   308: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   311: aload_1
    //   312: invokevirtual 464	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   315: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   318: invokestatic 505	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   321: pop
    //   322: aload_0
    //   323: aload_1
    //   324: bipush 10
    //   326: aload 20
    //   328: invokespecial 384	com/google/android/picasasync/UploadsManager:setState	(Lcom/google/android/picasasync/UploadTaskEntry;ILjava/lang/Throwable;)V
    //   331: aload 8
    //   333: invokevirtual 459	com/google/android/picasasync/GDataUploader:close	()V
    //   336: aconst_null
    //   337: areturn
    //   338: astore 16
    //   340: invokestatic 365	com/google/android/picasasync/UploadsManager:isExternalStorageMounted	()Z
    //   343: ifeq +65 -> 408
    //   346: getstatic 69	com/google/android/picasasync/UploadsManager:TAG	Ljava/lang/String;
    //   349: new 128	java/lang/StringBuilder
    //   352: dup
    //   353: invokespecial 131	java/lang/StringBuilder:<init>	()V
    //   356: ldc_w 507
    //   359: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   362: aload_1
    //   363: invokevirtual 464	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   366: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   369: aload 16
    //   371: invokestatic 467	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   374: pop
    //   375: aload_0
    //   376: aload_1
    //   377: iconst_5
    //   378: aload 16
    //   380: invokespecial 384	com/google/android/picasasync/UploadsManager:setState	(Lcom/google/android/picasasync/UploadTaskEntry;ILjava/lang/Throwable;)V
    //   383: aload_3
    //   384: getfield 491	android/content/SyncResult:stats	Landroid/content/SyncStats;
    //   387: astore 19
    //   389: aload 19
    //   391: lconst_1
    //   392: aload 19
    //   394: getfield 496	android/content/SyncStats:numSkippedEntries	J
    //   397: ladd
    //   398: putfield 496	android/content/SyncStats:numSkippedEntries	J
    //   401: aload 8
    //   403: invokevirtual 459	com/google/android/picasasync/GDataUploader:close	()V
    //   406: aconst_null
    //   407: areturn
    //   408: getstatic 69	com/google/android/picasasync/UploadsManager:TAG	Ljava/lang/String;
    //   411: new 128	java/lang/StringBuilder
    //   414: dup
    //   415: invokespecial 131	java/lang/StringBuilder:<init>	()V
    //   418: ldc_w 507
    //   421: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   424: aload_1
    //   425: invokevirtual 464	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   428: ldc_w 509
    //   431: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   434: aload 16
    //   436: invokevirtual 464	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   439: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   442: invokestatic 512	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   445: pop
    //   446: aload_0
    //   447: aload_1
    //   448: bipush 6
    //   450: aload 16
    //   452: invokespecial 384	com/google/android/picasasync/UploadsManager:setState	(Lcom/google/android/picasasync/UploadTaskEntry;ILjava/lang/Throwable;)V
    //   455: goto -54 -> 401
    //   458: astore 12
    //   460: aload 8
    //   462: invokevirtual 459	com/google/android/picasasync/GDataUploader:close	()V
    //   465: aload 12
    //   467: athrow
    //   468: astore 13
    //   470: getstatic 69	com/google/android/picasasync/UploadsManager:TAG	Ljava/lang/String;
    //   473: new 128	java/lang/StringBuilder
    //   476: dup
    //   477: invokespecial 131	java/lang/StringBuilder:<init>	()V
    //   480: ldc_w 507
    //   483: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   486: aload_1
    //   487: invokevirtual 464	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   490: ldc_w 514
    //   493: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   496: aload 13
    //   498: invokevirtual 464	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   501: ldc_w 516
    //   504: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   507: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   510: invokestatic 505	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   513: pop
    //   514: aload_3
    //   515: getfield 491	android/content/SyncResult:stats	Landroid/content/SyncStats;
    //   518: astore 15
    //   520: aload 15
    //   522: lconst_1
    //   523: aload 15
    //   525: getfield 519	android/content/SyncStats:numIoExceptions	J
    //   528: ladd
    //   529: putfield 519	android/content/SyncStats:numIoExceptions	J
    //   532: aload_0
    //   533: aload_1
    //   534: bipush 6
    //   536: aload 13
    //   538: invokespecial 384	com/google/android/picasasync/UploadsManager:setState	(Lcom/google/android/picasasync/UploadTaskEntry;ILjava/lang/Throwable;)V
    //   541: aload 8
    //   543: invokevirtual 459	com/google/android/picasasync/GDataUploader:close	()V
    //   546: aconst_null
    //   547: areturn
    //   548: astore 9
    //   550: getstatic 69	com/google/android/picasasync/UploadsManager:TAG	Ljava/lang/String;
    //   553: new 128	java/lang/StringBuilder
    //   556: dup
    //   557: invokespecial 131	java/lang/StringBuilder:<init>	()V
    //   560: ldc_w 507
    //   563: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   566: aload_1
    //   567: invokevirtual 464	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   570: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   573: aload 9
    //   575: invokestatic 522	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   578: pop
    //   579: aload_0
    //   580: aload_1
    //   581: iconst_5
    //   582: aload 9
    //   584: invokespecial 384	com/google/android/picasasync/UploadsManager:setState	(Lcom/google/android/picasasync/UploadTaskEntry;ILjava/lang/Throwable;)V
    //   587: aload_3
    //   588: getfield 491	android/content/SyncResult:stats	Landroid/content/SyncStats;
    //   591: astore 11
    //   593: aload 11
    //   595: lconst_1
    //   596: aload 11
    //   598: getfield 496	android/content/SyncStats:numSkippedEntries	J
    //   601: ladd
    //   602: putfield 496	android/content/SyncStats:numSkippedEntries	J
    //   605: aload 8
    //   607: invokevirtual 459	com/google/android/picasasync/GDataUploader:close	()V
    //   610: aconst_null
    //   611: areturn
    //   612: getstatic 69	com/google/android/picasasync/UploadsManager:TAG	Ljava/lang/String;
    //   615: new 128	java/lang/StringBuilder
    //   618: dup
    //   619: invokespecial 131	java/lang/StringBuilder:<init>	()V
    //   622: ldc_w 524
    //   625: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   628: lload 5
    //   630: invokevirtual 527	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   633: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   636: invokestatic 477	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   639: pop
    //   640: aload_1
    //   641: iconst_2
    //   642: invokevirtual 421	com/google/android/picasasync/UploadTaskEntry:setState	(I)V
    //   645: aload_0
    //   646: lload 5
    //   648: invokevirtual 530	java/lang/Object:wait	(J)V
    //   651: lload 5
    //   653: ldc2_w 531
    //   656: lmul
    //   657: lstore 5
    //   659: aload_1
    //   660: invokevirtual 535	com/google/android/picasasync/UploadTaskEntry:shouldRetry	()Z
    //   663: ifeq +28 -> 691
    //   666: aload_1
    //   667: iconst_1
    //   668: invokevirtual 421	com/google/android/picasasync/UploadTaskEntry:setState	(I)V
    //   671: aload_0
    //   672: monitorexit
    //   673: goto -641 -> 32
    //   676: astore 31
    //   678: getstatic 69	com/google/android/picasasync/UploadsManager:TAG	Ljava/lang/String;
    //   681: ldc_w 537
    //   684: invokestatic 477	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   687: pop
    //   688: goto -37 -> 651
    //   691: aload_0
    //   692: monitorexit
    //   693: aconst_null
    //   694: areturn
    //
    // Exception table:
    //   from	to	target	type
    //   2	16	66	finally
    //   62	64	66	finally
    //   68	70	66	finally
    //   45	54	73	com/google/android/picasasync/Uploader$MediaFileChangedException
    //   45	54	120	com/google/android/picasasync/Uploader$RestartException
    //   158	165	221	finally
    //   174	219	221	finally
    //   223	225	221	finally
    //   612	645	221	finally
    //   645	651	221	finally
    //   659	673	221	finally
    //   678	688	221	finally
    //   691	693	221	finally
    //   45	54	228	com/google/android/picasasync/Uploader$UnauthorizedException
    //   45	54	293	com/google/android/picasasync/Uploader$PicasaQuotaException
    //   45	54	338	com/google/android/picasasync/Uploader$LocalIoException
    //   45	54	458	finally
    //   75	113	458	finally
    //   122	151	458	finally
    //   230	286	458	finally
    //   295	331	458	finally
    //   340	401	458	finally
    //   408	455	458	finally
    //   470	541	458	finally
    //   550	605	458	finally
    //   45	54	468	java/io/IOException
    //   45	54	548	java/lang/Throwable
    //   645	651	676	java/lang/InterruptedException
  }

  private static void fillExif(PhotoEntry paramPhotoEntry, String paramString)
    throws IOException
  {
    ExifInterface localExifInterface = new ExifInterface(paramString);
    paramPhotoEntry.exifMake = localExifInterface.getAttribute("Make");
    paramPhotoEntry.exifModel = localExifInterface.getAttribute("Model");
    paramPhotoEntry.exifExposure = getExifFloatByReflection(localExifInterface, EXIF_TAG_EXPOSURE_TIME, 0.0F);
    paramPhotoEntry.exifFlash = localExifInterface.getAttributeInt("Flash", 0);
    paramPhotoEntry.exifFocalLength = (float)localExifInterface.getAttributeDouble("FocalLength", 0.0D);
    paramPhotoEntry.exifFstop = getExifFloatByReflection(localExifInterface, EXIF_TAG_APERTURE, 0.0F);
    paramPhotoEntry.exifIso = getExifIntByReflection(localExifInterface, EXIF_TAG_ISO, 0);
    paramPhotoEntry.rotation = getRotationDegreeFromExifOrientation(localExifInterface.getAttributeInt("Orientation", 1));
    if (!localExifInterface.getLatLong(sLatlon))
      return;
    paramPhotoEntry.latitude = sLatlon[0];
    paramPhotoEntry.longitude = sLatlon[1];
  }

  private static float getExifFloatByReflection(ExifInterface paramExifInterface, Field paramField, float paramFloat)
  {
    if (paramField == null)
      return paramFloat;
    try
    {
      float f = Utils.parseFloatSafely(paramExifInterface.getAttribute(paramField.get(paramExifInterface).toString()), paramFloat);
      return f;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
  }

  private static int getExifIntByReflection(ExifInterface paramExifInterface, Field paramField, int paramInt)
  {
    if (paramField == null)
      return paramInt;
    try
    {
      int i = Utils.parseIntSafely(paramExifInterface.getAttribute(paramField.get(paramExifInterface).toString()), paramInt);
      return i;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
  }

  private String getFileName(String paramString)
  {
    if (paramString == null)
      paramString = "No title";
    int i;
    do
    {
      return paramString;
      i = paramString.lastIndexOf("/");
    }
    while (i <= 0);
    return paramString.substring(i + 1);
  }

  // ERROR //
  private String getFilePath(Uri paramUri, ContentResolver paramContentResolver)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 650	android/net/Uri:getScheme	()Ljava/lang/String;
    //   4: ldc_w 652
    //   7: invokevirtual 655	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   10: ifeq +16 -> 26
    //   13: aload_1
    //   14: invokevirtual 658	android/net/Uri:getSchemeSpecificPart	()Ljava/lang/String;
    //   17: iconst_2
    //   18: invokevirtual 645	java/lang/String:substring	(I)Ljava/lang/String;
    //   21: astore 9
    //   23: aload 9
    //   25: areturn
    //   26: aload_2
    //   27: aload_1
    //   28: getstatic 112	com/google/android/picasasync/UploadsManager:PROJECTION_DATA	[Ljava/lang/String;
    //   31: aconst_null
    //   32: aconst_null
    //   33: aconst_null
    //   34: invokevirtual 662	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   37: astore 7
    //   39: aload 7
    //   41: astore 4
    //   43: aload 4
    //   45: invokeinterface 667 1 0
    //   50: ifeq +32 -> 82
    //   53: aload 4
    //   55: iconst_0
    //   56: invokeinterface 670 2 0
    //   61: astore 10
    //   63: aload 10
    //   65: astore 9
    //   67: aload 4
    //   69: ifnull -46 -> 23
    //   72: aload 4
    //   74: invokeinterface 671 1 0
    //   79: aload 9
    //   81: areturn
    //   82: aconst_null
    //   83: astore 9
    //   85: goto -18 -> 67
    //   88: astore 5
    //   90: aconst_null
    //   91: astore 6
    //   93: aload 6
    //   95: ifnull +10 -> 105
    //   98: aload 6
    //   100: invokeinterface 671 1 0
    //   105: aconst_null
    //   106: areturn
    //   107: astore_3
    //   108: aconst_null
    //   109: astore 4
    //   111: aload 4
    //   113: ifnull +10 -> 123
    //   116: aload 4
    //   118: invokeinterface 671 1 0
    //   123: aload_3
    //   124: athrow
    //   125: astore_3
    //   126: goto -15 -> 111
    //   129: astore 8
    //   131: aload 4
    //   133: astore 6
    //   135: goto -42 -> 93
    //
    // Exception table:
    //   from	to	target	type
    //   26	39	88	java/lang/Exception
    //   26	39	107	finally
    //   43	63	125	finally
    //   43	63	129	java/lang/Exception
  }

  private static int getFsId(Context paramContext)
  {
    Cursor localCursor = paramContext.getContentResolver().query(EXTERNAL_STORAGE_FSID_URI, null, null, null, null);
    if (localCursor != null);
    try
    {
      if (localCursor.moveToFirst())
      {
        int i = localCursor.getInt(0);
        return i;
      }
      Log.d(TAG, "No FSID on this device!");
      return -1;
    }
    finally
    {
      Utils.closeSilently(localCursor);
    }
  }

  public static UploadsManager getInstance(Context paramContext)
  {
    monitorenter;
    try
    {
      if (sInstance == null)
        sInstance = new UploadsManager(paramContext);
      UploadsManager localUploadsManager = sInstance;
      return localUploadsManager;
    }
    finally
    {
      monitorexit;
    }
  }

  private int getMaxDisplayIndex()
  {
    Cursor localCursor = this.mPicasaDbHelper.getReadableDatabase().rawQuery(QUERY_MAX_DISPLAY_INDEX, null);
    if (localCursor == null)
      return 0;
    try
    {
      boolean bool = localCursor.moveToFirst();
      int i = 0;
      if (bool)
      {
        int j = localCursor.getInt(0);
        i = j;
      }
      return i;
    }
    finally
    {
      localCursor.close();
    }
  }

  private UploadTaskEntry getNextManualUploadFromDb(String paramString)
  {
    String[] arrayOfString = new String[2];
    arrayOfString[0] = String.valueOf(1);
    arrayOfString[1] = paramString;
    Cursor localCursor = this.mUploadsDbHelper.getReadableDatabase().query(UPLOAD_TASK_TABLE_NAME, UploadTaskEntry.SCHEMA.getProjection(), "priority=? AND mime_type LIKE ?", arrayOfString, null, null, "priority,_id", null);
    if (localCursor == null)
      return null;
    try
    {
      UploadTaskEntry localUploadTaskEntry1;
      String str;
      do
      {
        if (!localCursor.moveToNext())
          break label181;
        localUploadTaskEntry1 = UploadTaskEntry.fromCursor(localCursor);
        str = localUploadTaskEntry1.getAccount();
      }
      while (this.mProblematicAccounts.contains(str));
      boolean bool = accountExists(str);
      if (bool)
        return localUploadTaskEntry1;
      Log.d(TAG, "invalid account, remove all uploads in DB: " + Utils.maskDebugInfo(str));
      this.mUploadsDbHelper.getWritableDatabase().delete(UPLOAD_TASK_TABLE_NAME, "account=?", new String[] { str });
      UploadTaskEntry localUploadTaskEntry2 = getNextManualUploadFromDb(paramString);
      return localUploadTaskEntry2;
      label181: return null;
    }
    finally
    {
      localCursor.close();
    }
  }

  private static int getRecordCount(SQLiteDatabase paramSQLiteDatabase, long paramLong, int paramInt)
  {
    String[] arrayOfString = new String[2];
    arrayOfString[0] = String.valueOf(paramLong);
    arrayOfString[1] = String.valueOf(paramInt);
    Cursor localCursor = paramSQLiteDatabase.query(UPLOAD_RECORD_TABLE_NAME, PROJECTION_COUNT, "_id<? AND uid=?", arrayOfString, null, null, null);
    if (localCursor != null);
    try
    {
      if (localCursor.moveToFirst())
      {
        int i = localCursor.getInt(0);
        return i;
      }
      return 0;
    }
    finally
    {
      Utils.closeSilently(localCursor);
    }
  }

  private static int getRotationDegreeFromExifOrientation(int paramInt)
  {
    switch (paramInt)
    {
    case 4:
    case 5:
    case 7:
    default:
      return 0;
    case 6:
      return 90;
    case 3:
      return 180;
    case 8:
    }
    return 270;
  }

  private UploadTaskEntry getTaskFromDb(long paramLong)
  {
    return UploadTaskEntry.fromDb(this.mUploadsDbHelper.getWritableDatabase(), paramLong);
  }

  private static boolean hasNetworkConnectivity(Context paramContext)
  {
    return ((ConnectivityManager)paramContext.getSystemService("connectivity")).getActiveNetworkInfo() != null;
  }

  private Handler initHandler(HandlerThread paramHandlerThread)
  {
    return new Handler(paramHandlerThread.getLooper())
    {
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default:
          throw new AssertionError("unknown message: " + paramMessage.what);
        case 2:
          UploadsManager.this.reloadSystemSettingsInternal();
          return;
        case 1:
          UploadsManager.this.cancelTaskInternal(((Long)paramMessage.obj).longValue());
          return;
        case 3:
          UploadsManager.this.onFsIdChangedInternal();
          return;
        case 4:
        }
        Log.d(UploadsManager.TAG, "Try to reset UploadsManager again!");
        UploadsManager.this.reset();
      }
    };
  }

  private boolean isAndroidUpgraded()
  {
    String str = this.mSettings.getString("system_release", null);
    if (!Build.VERSION.RELEASE.equals(str))
    {
      this.mSettings.edit().putString("system_release", Build.VERSION.RELEASE).commit();
      Log.d(TAG, "System upgrade from " + str + " to " + Build.VERSION.RELEASE);
      return true;
    }
    return false;
  }

  private static boolean isExternalStorageMounted()
  {
    String str = Environment.getExternalStorageState();
    return (str.equals("mounted")) || (str.equals("mounted_ro"));
  }

  private void loadSavedStates()
  {
    this.mIsExternalStorageFsIdReady = this.mSettings.contains("external_storage_fsid");
    if (!this.mIsExternalStorageFsIdReady)
      return;
    this.mExternalStorageFsId = this.mSettings.getInt("external_storage_fsid", -1);
  }

  private void notifyManualUploadDbChanges(boolean paramBoolean)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    PicasaFacade localPicasaFacade = PicasaFacade.get(this.mContext);
    localContentResolver.notifyChange(localPicasaFacade.getUploadsUri(), null, false);
    if (!paramBoolean)
      return;
    localContentResolver.notifyChange(localPicasaFacade.getUploadRecordsUri(), null, false);
  }

  private void onFsIdChangedInternal()
  {
    monitorenter;
    while (true)
    {
      int i;
      try
      {
        boolean bool = isExternalStorageMounted();
        if (!bool)
          return;
        i = getFsId(this.mContext);
        if (this.mIsExternalStorageFsIdReady)
          break label110;
        Log.d(TAG, "set fsid=" + i);
        this.mIsExternalStorageFsIdReady = true;
        this.mExternalStorageFsId = i;
        this.mSettings.edit().putInt("external_storage_fsid", i).commit();
      }
      finally
      {
        monitorexit;
      }
      if (this.mExternalStorageFsId == i)
        label110: this.mSyncedAccountAlbumPairs.clear();
      Log.d(TAG, "fsid changed from " + this.mExternalStorageFsId + " to " + i);
      this.mExternalStorageFsId = i;
      this.mSettings.edit().putInt("external_storage_fsid", i).commit();
      reset();
    }
  }

  private static void purgeRecords(SQLiteDatabase paramSQLiteDatabase, int paramInt1, long paramLong, int paramInt2)
  {
    Log.v(TAG, "target purge count = " + paramInt1 + " maxID = " + paramLong);
    long l1 = 0L;
    while (true)
    {
      long l2 = (paramLong + l1) / 2L;
      if (l2 == l1)
      {
        String[] arrayOfString = new String[2];
        arrayOfString[0] = String.valueOf(l1);
        arrayOfString[1] = String.valueOf(paramInt2);
        int i = paramSQLiteDatabase.delete(UPLOAD_RECORD_TABLE_NAME, "_id<? AND uid=?", arrayOfString);
        Log.v(TAG, i + " purged");
        return;
      }
      if (getRecordCount(paramSQLiteDatabase, l2, paramInt2) > paramInt1)
        paramLong = l2;
      l1 = l2;
    }
  }

  private UploadedEntry recordResult(UploadedEntry paramUploadedEntry)
  {
    SQLiteDatabase localSQLiteDatabase = this.mUploadsDbHelper.getWritableDatabase();
    UploadedEntry.SCHEMA.insertOrReplace(localSQLiteDatabase, paramUploadedEntry);
    if (Math.random() > 0.0005D)
      return paramUploadedEntry;
    localSQLiteDatabase.beginTransaction();
    try
    {
      String str = UPLOAD_RECORD_TABLE_NAME;
      String[] arrayOfString1 = PROJECTION_COUNT;
      String[] arrayOfString2 = new String[1];
      arrayOfString2[0] = String.valueOf(paramUploadedEntry.uid);
      Cursor localCursor;
      try
      {
        if (localCursor.moveToFirst())
        {
          int i = localCursor.getInt(0);
          if (i > 2000)
            purgeRecords(localSQLiteDatabase, i / 2, paramUploadedEntry.id, paramUploadedEntry.uid);
        }
        Utils.closeSilently(localCursor);
        localSQLiteDatabase.setTransactionSuccessful();
        return paramUploadedEntry;
      }
      finally
      {
        Utils.closeSilently(localCursor);
      }
    }
    finally
    {
      localSQLiteDatabase.setTransactionSuccessful();
      localSQLiteDatabase.endTransaction();
    }
  }

  // ERROR //
  private void reloadSystemSettingsInternal()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 196	com/google/android/picasasync/UploadsManager:mContext	Landroid/content/Context;
    //   6: invokevirtual 265	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   9: aload_0
    //   10: getfield 196	com/google/android/picasasync/UploadsManager:mContext	Landroid/content/Context;
    //   13: invokestatic 826	com/google/android/picasasync/PicasaFacade:get	(Landroid/content/Context;)Lcom/google/android/picasasync/PicasaFacade;
    //   16: invokevirtual 910	com/google/android/picasasync/PicasaFacade:getSettingsUri	()Landroid/net/Uri;
    //   19: getstatic 108	com/google/android/picasasync/UploadsManager:PROJECTION_ENABLE_ACCOUNT_WIFI	[Ljava/lang/String;
    //   22: aconst_null
    //   23: aconst_null
    //   24: aconst_null
    //   25: invokevirtual 662	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   28: astore_2
    //   29: aload_2
    //   30: ifnull +12 -> 42
    //   33: aload_2
    //   34: invokeinterface 676 1 0
    //   39: ifne +45 -> 84
    //   42: getstatic 69	com/google/android/picasasync/UploadsManager:TAG	Ljava/lang/String;
    //   45: ldc_w 912
    //   48: invokestatic 505	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   51: pop
    //   52: aload_0
    //   53: getfield 249	com/google/android/picasasync/UploadsManager:mHandler	Landroid/os/Handler;
    //   56: iconst_2
    //   57: aload_0
    //   58: getfield 194	com/google/android/picasasync/UploadsManager:mReloadSystemSettingDelay	J
    //   61: invokevirtual 918	android/os/Handler:sendEmptyMessageDelayed	(IJ)Z
    //   64: pop
    //   65: aload_0
    //   66: ldc2_w 531
    //   69: aload_0
    //   70: getfield 194	com/google/android/picasasync/UploadsManager:mReloadSystemSettingDelay	J
    //   73: lmul
    //   74: putfield 194	com/google/android/picasasync/UploadsManager:mReloadSystemSettingDelay	J
    //   77: aload_2
    //   78: invokestatic 683	com/android/gallery3d/common/Utils:closeSilently	(Landroid/database/Cursor;)V
    //   81: aload_0
    //   82: monitorexit
    //   83: return
    //   84: aload_2
    //   85: iconst_0
    //   86: invokeinterface 679 2 0
    //   91: ifeq +285 -> 376
    //   94: iconst_1
    //   95: istore 6
    //   97: aload_2
    //   98: iconst_1
    //   99: invokeinterface 679 2 0
    //   104: ifeq +278 -> 382
    //   107: iconst_1
    //   108: istore 7
    //   110: aload_2
    //   111: iconst_2
    //   112: invokeinterface 679 2 0
    //   117: ifeq +271 -> 388
    //   120: iconst_1
    //   121: istore 8
    //   123: aload_2
    //   124: iconst_3
    //   125: invokeinterface 679 2 0
    //   130: ifeq +264 -> 394
    //   133: iconst_1
    //   134: istore 9
    //   136: aload_0
    //   137: ldc2_w 191
    //   140: putfield 194	com/google/android/picasasync/UploadsManager:mReloadSystemSettingDelay	J
    //   143: aload_2
    //   144: invokestatic 683	com/android/gallery3d/common/Utils:closeSilently	(Landroid/database/Cursor;)V
    //   147: iload 6
    //   149: aload_0
    //   150: getfield 920	com/google/android/picasasync/UploadsManager:mWifiOnlyPhoto	Z
    //   153: if_icmpne +30 -> 183
    //   156: iload 7
    //   158: aload_0
    //   159: getfield 922	com/google/android/picasasync/UploadsManager:mWifiOnlyVideo	Z
    //   162: if_icmpne +21 -> 183
    //   165: iload 8
    //   167: aload_0
    //   168: getfield 924	com/google/android/picasasync/UploadsManager:mSyncOnRoaming	Z
    //   171: if_icmpne +12 -> 183
    //   174: iload 9
    //   176: aload_0
    //   177: getfield 926	com/google/android/picasasync/UploadsManager:mSyncOnBattery	Z
    //   180: if_icmpeq +16 -> 196
    //   183: aload_0
    //   184: getfield 196	com/google/android/picasasync/UploadsManager:mContext	Landroid/content/Context;
    //   187: invokestatic 853	com/google/android/picasasync/PicasaSyncManager:get	(Landroid/content/Context;)Lcom/google/android/picasasync/PicasaSyncManager;
    //   190: ldc2_w 191
    //   193: invokevirtual 856	com/google/android/picasasync/PicasaSyncManager:updateTasks	(J)V
    //   196: iload 6
    //   198: aload_0
    //   199: getfield 920	com/google/android/picasasync/UploadsManager:mWifiOnlyPhoto	Z
    //   202: if_icmpeq +31 -> 233
    //   205: getstatic 69	com/google/android/picasasync/UploadsManager:TAG	Ljava/lang/String;
    //   208: new 128	java/lang/StringBuilder
    //   211: dup
    //   212: invokespecial 131	java/lang/StringBuilder:<init>	()V
    //   215: ldc_w 928
    //   218: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   221: iload 6
    //   223: invokevirtual 931	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   226: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   229: invokestatic 477	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   232: pop
    //   233: iload 7
    //   235: aload_0
    //   236: getfield 922	com/google/android/picasasync/UploadsManager:mWifiOnlyVideo	Z
    //   239: if_icmpeq +31 -> 270
    //   242: getstatic 69	com/google/android/picasasync/UploadsManager:TAG	Ljava/lang/String;
    //   245: new 128	java/lang/StringBuilder
    //   248: dup
    //   249: invokespecial 131	java/lang/StringBuilder:<init>	()V
    //   252: ldc_w 933
    //   255: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   258: iload 7
    //   260: invokevirtual 931	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   263: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   266: invokestatic 477	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   269: pop
    //   270: iload 8
    //   272: aload_0
    //   273: getfield 924	com/google/android/picasasync/UploadsManager:mSyncOnRoaming	Z
    //   276: if_icmpeq +31 -> 307
    //   279: getstatic 69	com/google/android/picasasync/UploadsManager:TAG	Ljava/lang/String;
    //   282: new 128	java/lang/StringBuilder
    //   285: dup
    //   286: invokespecial 131	java/lang/StringBuilder:<init>	()V
    //   289: ldc_w 935
    //   292: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   295: iload 8
    //   297: invokevirtual 931	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   300: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   303: invokestatic 477	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   306: pop
    //   307: iload 9
    //   309: aload_0
    //   310: getfield 926	com/google/android/picasasync/UploadsManager:mSyncOnBattery	Z
    //   313: if_icmpeq +31 -> 344
    //   316: getstatic 69	com/google/android/picasasync/UploadsManager:TAG	Ljava/lang/String;
    //   319: new 128	java/lang/StringBuilder
    //   322: dup
    //   323: invokespecial 131	java/lang/StringBuilder:<init>	()V
    //   326: ldc_w 937
    //   329: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   332: iload 9
    //   334: invokevirtual 931	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   337: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   340: invokestatic 477	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   343: pop
    //   344: aload_0
    //   345: iload 6
    //   347: putfield 920	com/google/android/picasasync/UploadsManager:mWifiOnlyPhoto	Z
    //   350: aload_0
    //   351: iload 7
    //   353: putfield 922	com/google/android/picasasync/UploadsManager:mWifiOnlyVideo	Z
    //   356: aload_0
    //   357: iload 8
    //   359: putfield 924	com/google/android/picasasync/UploadsManager:mSyncOnRoaming	Z
    //   362: aload_0
    //   363: iload 9
    //   365: putfield 926	com/google/android/picasasync/UploadsManager:mSyncOnBattery	Z
    //   368: goto -287 -> 81
    //   371: astore_1
    //   372: aload_0
    //   373: monitorexit
    //   374: aload_1
    //   375: athrow
    //   376: iconst_0
    //   377: istore 6
    //   379: goto -282 -> 97
    //   382: iconst_0
    //   383: istore 7
    //   385: goto -275 -> 110
    //   388: iconst_0
    //   389: istore 8
    //   391: goto -268 -> 123
    //   394: iconst_0
    //   395: istore 9
    //   397: goto -261 -> 136
    //   400: astore_3
    //   401: aload_2
    //   402: invokestatic 683	com/android/gallery3d/common/Utils:closeSilently	(Landroid/database/Cursor;)V
    //   405: aload_3
    //   406: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   2	29	371	finally
    //   77	81	371	finally
    //   143	183	371	finally
    //   183	196	371	finally
    //   196	233	371	finally
    //   233	270	371	finally
    //   270	307	371	finally
    //   307	344	371	finally
    //   344	368	371	finally
    //   401	407	371	finally
    //   33	42	400	finally
    //   42	77	400	finally
    //   84	94	400	finally
    //   97	107	400	finally
    //   110	120	400	finally
    //   123	133	400	finally
    //   136	143	400	finally
  }

  private boolean removeTaskFromDb(long paramLong)
  {
    return UploadTaskEntry.SCHEMA.deleteWithId(this.mUploadsDbHelper.getWritableDatabase(), paramLong);
  }

  private void reset()
  {
    monitorenter;
    try
    {
      resetStates();
      resetUploadDatabase();
      reloadSystemSettingsInternal();
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  private void resetStates()
  {
    this.mSyncedAccountAlbumPairs.clear();
  }

  private void resetUploadDatabase()
  {
    try
    {
      this.mUploadsDbHelper.reset();
      this.mResetDelay = 15000L;
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      Log.i(TAG, "DB not ready for reset?", localSQLiteException);
      this.mHandler.sendEmptyMessageDelayed(4, this.mResetDelay);
      this.mResetDelay = (2L * this.mResetDelay);
    }
  }

  private void sendManualUploadReport(UploadTaskEntry paramUploadTaskEntry, UploadedEntry paramUploadedEntry, int paramInt)
  {
    ComponentName localComponentName = paramUploadTaskEntry.getComponentName();
    if (localComponentName == null)
      return;
    Intent localIntent = new Intent("com.google.android.picasasync.manual_upload_report");
    localIntent.setComponent(localComponentName);
    localIntent.putExtra("manual_upload_upload_id", paramUploadTaskEntry.id);
    localIntent.putExtra("manual_upload_content_uri", paramUploadTaskEntry.getContentUri());
    localIntent.putExtra("manual_upload_state", paramUploadTaskEntry.getState());
    localIntent.putExtra("manual_upload_uploader_state", paramInt);
    localIntent.putExtra("manual_upload_progress", paramUploadTaskEntry.getPercentageUploaded());
    if (paramUploadedEntry != null)
      localIntent.putExtra("manual_upload_record_id", paramUploadedEntry.id);
    this.mContext.sendBroadcast(localIntent);
  }

  private void setCurrentUploadTask(UploadTask paramUploadTask)
  {
    monitorenter;
    try
    {
      this.mCurrent = paramUploadTask;
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  private void setPhotoSize(PhotoEntry paramPhotoEntry, Uri paramUri, ContentResolver paramContentResolver)
  {
    try
    {
      BitmapFactory.Options localOptions = new BitmapFactory.Options();
      localOptions.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(paramContentResolver.openInputStream(paramUri), null, localOptions);
      paramPhotoEntry.width = localOptions.outWidth;
      paramPhotoEntry.height = localOptions.outHeight;
      return;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      Log.d(TAG, "setPhotoSize: " + paramUri + ": " + localFileNotFoundException);
    }
  }

  private void setState(UploadTaskEntry paramUploadTaskEntry, int paramInt)
  {
    monitorenter;
    try
    {
      paramUploadTaskEntry.setState(paramInt);
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  private void setState(UploadTaskEntry paramUploadTaskEntry, int paramInt, Throwable paramThrowable)
  {
    monitorenter;
    try
    {
      paramUploadTaskEntry.setState(paramInt, paramThrowable);
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  private void updateTaskStateAndProgressInDb(UploadTaskEntry paramUploadTaskEntry)
  {
    monitorenter;
    try
    {
      if (paramUploadTaskEntry.isReadyForUpload())
        UploadTaskEntry.SCHEMA.insertOrReplace(this.mUploadsDbHelper.getWritableDatabase(), paramUploadTaskEntry);
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  private boolean writeToPhotoTable(UploadTaskEntry paramUploadTaskEntry, UploadedEntry paramUploadedEntry, SyncResult paramSyncResult)
  {
    String str1 = paramUploadTaskEntry.getAlbumId();
    String str2 = this.mPicasaDbHelper.getRealAlbumId(paramUploadTaskEntry.getAccount(), str1);
    if (str2 == null)
    {
      Log.d(TAG, "no album to write new photo data to");
      return false;
    }
    long l = this.mPicasaDbHelper.getUserId(paramUploadTaskEntry.getAccount());
    if (l == -1L)
    {
      Log.d(TAG, "no user owns the photo");
      return false;
    }
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    Uri localUri = paramUploadTaskEntry.getContentUri();
    String str3 = getFilePath(localUri, localContentResolver);
    PhotoEntry localPhotoEntry = new PhotoEntry();
    localPhotoEntry.id = paramUploadedEntry.idFromServer;
    localPhotoEntry.userId = l;
    localPhotoEntry.albumId = Long.parseLong(str2);
    localPhotoEntry.title = getFileName(str3);
    localPhotoEntry.size = (int)paramUploadedEntry.bytesTotal;
    String str4 = paramUploadedEntry.url;
    localPhotoEntry.screennailUrl = str4;
    localPhotoEntry.contentUrl = str4;
    localPhotoEntry.contentType = paramUploadTaskEntry.getMimeType();
    localPhotoEntry.fingerprint = paramUploadedEntry.fingerprint;
    localPhotoEntry.fingerprintHash = paramUploadedEntry.fingerprintHash;
    localPhotoEntry.dateTaken = paramUploadedEntry.timestamp;
    int i;
    if ("camera-sync".equals(str1))
      i = 1;
    while (true)
    {
      localPhotoEntry.cameraSync = i;
      setPhotoSize(localPhotoEntry, localUri, localContentResolver);
      localPhotoEntry.displayIndex = (1 + getMaxDisplayIndex());
      if (str3 != null);
      try
      {
        fillExif(localPhotoEntry, str3);
        label263: PhotoEntry.SCHEMA.insertOrReplace(this.mPicasaDbHelper.getWritableDatabase(), localPhotoEntry);
        PicasaFacade localPicasaFacade = PicasaFacade.get(this.mContext);
        localContentResolver.notifyChange(localPicasaFacade.getAlbumsUri(), null, false);
        localContentResolver.notifyChange(localPicasaFacade.getPhotosUri(), null, false);
        return true;
        i = 0;
      }
      catch (Exception localException)
      {
        Log.d(TAG, "fillExif", localException);
        break label263:
      }
    }
  }

  public long addManualUpload(UploadTaskEntry paramUploadTaskEntry)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    paramUploadTaskEntry.setPriority(1);
    String str = PicasaUploadHelper.setContentType(localContentResolver, paramUploadTaskEntry);
    PicasaUploadHelper.setFileSize(localContentResolver, paramUploadTaskEntry);
    SQLiteDatabase localSQLiteDatabase = this.mUploadsDbHelper.getWritableDatabase();
    long l = UploadTaskEntry.SCHEMA.insertOrReplace(localSQLiteDatabase, paramUploadTaskEntry);
    Log.d(TAG, "manual upload: " + paramUploadTaskEntry + "id=" + paramUploadTaskEntry.id + " type=" + str + " " + paramUploadTaskEntry.getBytesTotal());
    PicasaSyncManager.get(this.mContext).updateTasks(500L);
    return l;
  }

  public void cancelTask(long paramLong)
  {
    Message.obtain(this.mHandler, 1, Long.valueOf(paramLong)).sendToTarget();
  }

  public SyncTaskProvider getManualPhotoUploadTaskProvider()
  {
    return new ManualUploadTaskProvider("image/%");
  }

  public SyncTaskProvider getManualVideoUploadTaskProvider()
  {
    return new ManualUploadTaskProvider("%");
  }

  public UploadsDatabaseHelper getUploadsDatabaseHelper()
  {
    return this.mUploadsDbHelper;
  }

  public void reloadSystemSettings()
  {
    Message.obtain(this.mHandler, 2).sendToTarget();
  }

  private class ManualUploadTaskProvider
    implements SyncTaskProvider
  {
    private final String mTypePrefix;

    public ManualUploadTaskProvider(String arg2)
    {
      Object localObject;
      this.mTypePrefix = localObject;
    }

    public void collectTasks(Collection<SyncTask> paramCollection)
    {
      if ((!UploadsManager.access$2400()) || (!UploadsManager.this.mIsExternalStorageFsIdReady));
      UploadTaskEntry localUploadTaskEntry;
      do
      {
        return;
        localUploadTaskEntry = UploadsManager.this.getNextManualUploadFromDb(this.mTypePrefix);
      }
      while (localUploadTaskEntry == null);
      paramCollection.add(new UploadsManager.UploadTask(UploadsManager.this, localUploadTaskEntry.getAccount(), this.mTypePrefix));
    }

    public void onSyncStart()
    {
      UploadsManager.this.mProblematicAccounts.clear();
      UploadsManager.this.mSyncedAccountAlbumPairs.clear();
    }

    public void resetSyncStates()
    {
    }
  }

  private class UploadTask extends SyncTask
    implements Uploader.UploadProgressListener
  {
    protected UploadTaskEntry mCurrentTask;
    protected volatile boolean mRunning = true;
    protected PicasaSyncHelper.SyncContext mSyncContext;
    private final String mTypePrefix;

    protected UploadTask(String paramString1, String arg3)
    {
      super(paramString1);
      Object localObject;
      this.mTypePrefix = localObject;
    }

    private boolean onIncompleteUpload(UploadTaskEntry paramUploadTaskEntry, boolean paramBoolean)
    {
      while (true)
      {
        synchronized (UploadsManager.this)
        {
          switch (paramUploadTaskEntry.getState())
          {
          case 8:
          default:
            if (paramUploadTaskEntry.getState() != 5)
            {
              Log.e(UploadsManager.TAG, "wrong state after upload: " + paramUploadTaskEntry);
              paramUploadTaskEntry.setState(5, new RuntimeException("wrong state after upload: " + paramUploadTaskEntry.getState()));
            }
            return false;
          case 6:
            paramUploadTaskEntry.setState(3);
            UploadsManager.this.updateTaskStateAndProgressInDb(paramUploadTaskEntry);
            onStalled(paramUploadTaskEntry, paramBoolean);
            return true;
          case 9:
          case 10:
          case 7:
          }
        }
        UploadsManager.this.mProblematicAccounts.add(paramUploadTaskEntry.getAccount());
        onUnauthorized(paramUploadTaskEntry);
        monitorexit;
        return true;
        UploadsManager.this.mProblematicAccounts.add(paramUploadTaskEntry.getAccount());
        onQuotaReached(paramUploadTaskEntry);
        monitorexit;
        return true;
        paramUploadTaskEntry.setState(8);
      }
    }

    public void cancelSync()
    {
      synchronized (UploadsManager.this)
      {
        this.mRunning = false;
        stopCurrentTask(6);
        if (this.mSyncContext != null)
          this.mSyncContext.stopSync();
        return;
      }
    }

    public boolean cancelTask(long paramLong)
    {
      synchronized (UploadsManager.this)
      {
        if ((this.mCurrentTask != null) && (paramLong == this.mCurrentTask.id))
        {
          stopCurrentTask(7);
          return true;
        }
        return false;
      }
    }

    protected UploadTaskEntry getNextUpload()
      throws IOException
    {
      return UploadsManager.this.getNextManualUploadFromDb(this.mTypePrefix);
    }

    public boolean isBackgroundSync()
    {
      return false;
    }

    public boolean isSyncOnBattery()
    {
      return true;
    }

    public boolean isSyncOnExternalStorageOnly()
    {
      return true;
    }

    public boolean isSyncOnRoaming()
    {
      return true;
    }

    public boolean isSyncOnWifiOnly()
    {
      return false;
    }

    public boolean isUploadedBefore(UploadTaskEntry paramUploadTaskEntry)
    {
      int i = paramUploadTaskEntry.getFingerprint().hashCode();
      SQLiteDatabase localSQLiteDatabase = UploadsManager.this.mPicasaDbHelper.getReadableDatabase();
      localSQLiteDatabase.beginTransaction();
      try
      {
        AlbumEntry localAlbumEntry = UploadsManager.this.mPicasaDbHelper.getAlbumEntry(paramUploadTaskEntry.getAccount(), paramUploadTaskEntry.getAlbumId());
        if (localAlbumEntry != null)
        {
          boolean bool1 = "Buzz".equals(localAlbumEntry.albumType);
          if (!bool1)
            break label71;
        }
        return false;
        label71: String str = UploadsManager.PHOTO_TABLE_NAME;
        String[] arrayOfString1 = UploadsManager.PROJECTION_FINGERPRINT;
        String[] arrayOfString2 = new String[2];
        arrayOfString2[0] = String.valueOf(i);
        arrayOfString2[1] = String.valueOf(localAlbumEntry.id);
        Cursor localCursor = localSQLiteDatabase.query(str, arrayOfString1, "fingerprint_hash=? AND album_id=?", arrayOfString2, null, null, null);
        if (localCursor == null)
          localSQLiteDatabase.endTransaction();
        try
        {
          boolean bool2;
          do
          {
            if (!localCursor.moveToNext())
              break label184;
            byte[] arrayOfByte = localCursor.getBlob(0);
            bool2 = paramUploadTaskEntry.getFingerprint().equals(arrayOfByte);
          }
          while (!bool2);
          localCursor.close();
          return true;
          label184: localCursor.close();
          return false;
        }
        finally
        {
          localCursor.close();
        }
      }
      finally
      {
        localSQLiteDatabase.endTransaction();
      }
    }

    public void onProgress(UploadTaskEntry paramUploadTaskEntry)
    {
      synchronized (UploadsManager.this)
      {
        if (this.mRunning)
        {
          UploadsManager.this.updateTaskStateAndProgressInDb(paramUploadTaskEntry);
          UploadsManager.this.notifyManualUploadDbChanges(false);
          UploadsManager.this.sendManualUploadReport(paramUploadTaskEntry, null, 1);
        }
        return;
      }
    }

    protected void onQuotaReached(UploadTaskEntry paramUploadTaskEntry)
    {
      UploadsManager.this.notifyManualUploadDbChanges(false);
      UploadsManager.this.sendManualUploadReport(paramUploadTaskEntry, null, 9);
    }

    public void onRejected(int paramInt)
    {
      Log.e(UploadsManager.TAG, "manual upload rejected! " + paramInt);
    }

    protected void onStalled(UploadTaskEntry paramUploadTaskEntry, boolean paramBoolean)
    {
      UploadsManager.this.notifyManualUploadDbChanges(false);
      if (paramBoolean)
      {
        if (UploadsManager.access$1900(UploadsManager.this.mContext))
        {
          UploadsManager.this.sendManualUploadReport(paramUploadTaskEntry, null, 15);
          return;
        }
        UploadsManager.this.sendManualUploadReport(paramUploadTaskEntry, null, 14);
        return;
      }
      UploadsManager.this.sendManualUploadReport(paramUploadTaskEntry, null, 13);
    }

    protected void onTaskDone(UploadTaskEntry paramUploadTaskEntry, UploadedEntry paramUploadedEntry)
    {
      UploadsManager localUploadsManager = UploadsManager.this;
      if (paramUploadedEntry != null);
      for (boolean bool = true; ; bool = false)
      {
        localUploadsManager.notifyManualUploadDbChanges(bool);
        UploadsManager.this.sendManualUploadReport(paramUploadTaskEntry, paramUploadedEntry, 1);
        return;
      }
    }

    protected void onUnauthorized(UploadTaskEntry paramUploadTaskEntry)
    {
      UploadsManager.this.notifyManualUploadDbChanges(false);
      UploadsManager.this.sendManualUploadReport(paramUploadTaskEntry, null, 10);
    }

    public final void performSync(SyncResult paramSyncResult)
      throws IOException
    {
      int i;
      synchronized (UploadsManager.this)
      {
        if (!this.mRunning)
          return;
        this.mSyncContext = UploadsManager.this.mSyncHelper.createSyncContext(paramSyncResult, Thread.currentThread());
        this.mSyncContext.setAccount(this.syncAccount);
        UploadsManager.this.setCurrentUploadTask(this);
        i = MetricsUtils.begin(super.getClass().getSimpleName());
        TrafficStatsCompat.setThreadStatsTag(5);
      }
      try
      {
        performSyncInternal(paramSyncResult);
        this.mCurrentTask = null;
        UploadsManager.this.setCurrentUploadTask(null);
        this.mSyncContext = null;
        TrafficStatsCompat.clearThreadStatsTag();
        MetricsUtils.endWithReport(i, "picasa.upload");
        return;
        localObject1 = finally;
        monitorexit;
        throw localObject1;
      }
      finally
      {
        this.mCurrentTask = null;
        UploadsManager.this.setCurrentUploadTask(null);
        this.mSyncContext = null;
        TrafficStatsCompat.clearThreadStatsTag();
        MetricsUtils.endWithReport(i, "picasa.upload");
      }
    }

    // ERROR //
    protected void performSyncInternal(SyncResult paramSyncResult)
      throws IOException
    {
      // Byte code:
      //   0: aload_1
      //   1: getfield 316	android/content/SyncResult:stats	Landroid/content/SyncStats;
      //   4: astore_2
      //   5: aload_0
      //   6: getfield 25	com/google/android/picasasync/UploadsManager$UploadTask:mRunning	Z
      //   9: ifeq +29 -> 38
      //   12: aload_0
      //   13: getfield 20	com/google/android/picasasync/UploadsManager$UploadTask:this$0	Lcom/google/android/picasasync/UploadsManager;
      //   16: astore_3
      //   17: aload_3
      //   18: monitorenter
      //   19: aload_0
      //   20: invokevirtual 318	com/google/android/picasasync/UploadsManager$UploadTask:getNextUpload	()Lcom/google/android/picasasync/UploadTaskEntry;
      //   23: astore 5
      //   25: aload_0
      //   26: aload 5
      //   28: putfield 120	com/google/android/picasasync/UploadsManager$UploadTask:mCurrentTask	Lcom/google/android/picasasync/UploadTaskEntry;
      //   31: aload_3
      //   32: monitorexit
      //   33: aload 5
      //   35: ifnonnull +11 -> 46
      //   38: return
      //   39: astore 4
      //   41: aload_3
      //   42: monitorexit
      //   43: aload 4
      //   45: athrow
      //   46: aload 5
      //   48: invokevirtual 92	com/google/android/picasasync/UploadTaskEntry:getAccount	()Ljava/lang/String;
      //   51: aload_0
      //   52: getfield 266	com/google/android/picasasync/SyncTask:syncAccount	Ljava/lang/String;
      //   55: invokevirtual 185	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   58: ifne +52 -> 110
      //   61: invokestatic 41	com/google/android/picasasync/UploadsManager:access$300	()Ljava/lang/String;
      //   64: astore 22
      //   66: iconst_2
      //   67: anewarray 276	java/lang/Object
      //   70: astore 23
      //   72: aload 23
      //   74: iconst_0
      //   75: aload_0
      //   76: getfield 266	com/google/android/picasasync/SyncTask:syncAccount	Ljava/lang/String;
      //   79: invokestatic 324	com/android/gallery3d/common/Utils:maskDebugInfo	(Ljava/lang/Object;)Ljava/lang/String;
      //   82: aastore
      //   83: aload 23
      //   85: iconst_1
      //   86: aload 5
      //   88: invokevirtual 92	com/google/android/picasasync/UploadTaskEntry:getAccount	()Ljava/lang/String;
      //   91: invokestatic 324	com/android/gallery3d/common/Utils:maskDebugInfo	(Ljava/lang/Object;)Ljava/lang/String;
      //   94: aastore
      //   95: aload 22
      //   97: ldc_w 326
      //   100: aload 23
      //   102: invokestatic 330	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      //   105: invokestatic 333	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   108: pop
      //   109: return
      //   110: aload 5
      //   112: invokevirtual 336	com/google/android/picasasync/UploadTaskEntry:isStartedYet	()Z
      //   115: ifne +20 -> 135
      //   118: aload 5
      //   120: invokevirtual 339	com/google/android/picasasync/UploadTaskEntry:setUploadedTime	()V
      //   123: aload_0
      //   124: getfield 20	com/google/android/picasasync/UploadsManager$UploadTask:this$0	Lcom/google/android/picasasync/UploadsManager;
      //   127: invokestatic 239	com/google/android/picasasync/UploadsManager:access$700	(Lcom/google/android/picasasync/UploadsManager;)Landroid/content/Context;
      //   130: aload 5
      //   132: invokestatic 345	com/google/android/picasasync/PicasaUploadHelper:fillRequest	(Landroid/content/Context;Lcom/google/android/picasasync/UploadTaskEntry;)V
      //   135: aload_0
      //   136: aload_1
      //   137: aload_0
      //   138: getfield 266	com/google/android/picasasync/SyncTask:syncAccount	Ljava/lang/String;
      //   141: aload 5
      //   143: invokevirtual 169	com/google/android/picasasync/UploadTaskEntry:getAlbumId	()Ljava/lang/String;
      //   146: invokevirtual 349	com/google/android/picasasync/UploadsManager$UploadTask:syncAlbum	(Landroid/content/SyncResult;Ljava/lang/String;Ljava/lang/String;)V
      //   149: aconst_null
      //   150: astore 6
      //   152: aload_0
      //   153: aload 5
      //   155: invokevirtual 351	com/google/android/picasasync/UploadsManager$UploadTask:isUploadedBefore	(Lcom/google/android/picasasync/UploadTaskEntry;)Z
      //   158: ifeq +185 -> 343
      //   161: invokestatic 41	com/google/android/picasasync/UploadsManager:access$300	()Ljava/lang/String;
      //   164: new 43	java/lang/StringBuilder
      //   167: dup
      //   168: invokespecial 46	java/lang/StringBuilder:<init>	()V
      //   171: ldc_w 353
      //   174: invokevirtual 52	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   177: aload 5
      //   179: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   182: invokevirtual 58	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   185: invokestatic 333	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   188: pop
      //   189: aload_0
      //   190: getfield 20	com/google/android/picasasync/UploadsManager$UploadTask:this$0	Lcom/google/android/picasasync/UploadsManager;
      //   193: aload 5
      //   195: bipush 12
      //   197: invokestatic 357	com/google/android/picasasync/UploadsManager:access$1100	(Lcom/google/android/picasasync/UploadsManager;Lcom/google/android/picasasync/UploadTaskEntry;I)V
      //   200: aload_0
      //   201: getfield 20	com/google/android/picasasync/UploadsManager$UploadTask:this$0	Lcom/google/android/picasasync/UploadsManager;
      //   204: aload 5
      //   206: getfield 126	com/android/gallery3d/common/Entry:id	J
      //   209: invokestatic 361	com/google/android/picasasync/UploadsManager:access$900	(Lcom/google/android/picasasync/UploadsManager;J)Z
      //   212: pop
      //   213: aload 6
      //   215: ifnonnull +14 -> 229
      //   218: new 363	com/google/android/picasasync/UploadedEntry
      //   221: dup
      //   222: aload 5
      //   224: invokespecial 365	com/google/android/picasasync/UploadedEntry:<init>	(Lcom/google/android/picasasync/UploadTaskEntry;)V
      //   227: astore 6
      //   229: aload_0
      //   230: getfield 20	com/google/android/picasasync/UploadsManager$UploadTask:this$0	Lcom/google/android/picasasync/UploadsManager;
      //   233: aload 6
      //   235: invokestatic 369	com/google/android/picasasync/UploadsManager:access$1000	(Lcom/google/android/picasasync/UploadsManager;Lcom/google/android/picasasync/UploadedEntry;)Lcom/google/android/picasasync/UploadedEntry;
      //   238: pop
      //   239: aload_0
      //   240: aload 5
      //   242: aload 6
      //   244: invokevirtual 371	com/google/android/picasasync/UploadsManager$UploadTask:onTaskDone	(Lcom/google/android/picasasync/UploadTaskEntry;Lcom/google/android/picasasync/UploadedEntry;)V
      //   247: goto -242 -> 5
      //   250: astore 19
      //   252: invokestatic 41	com/google/android/picasasync/UploadsManager:access$300	()Ljava/lang/String;
      //   255: new 43	java/lang/StringBuilder
      //   258: dup
      //   259: invokespecial 46	java/lang/StringBuilder:<init>	()V
      //   262: ldc_w 373
      //   265: invokevirtual 52	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   268: aload 5
      //   270: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   273: invokevirtual 58	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   276: aload 19
      //   278: invokestatic 376	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   281: pop
      //   282: aload_0
      //   283: getfield 20	com/google/android/picasasync/UploadsManager$UploadTask:this$0	Lcom/google/android/picasasync/UploadsManager;
      //   286: aload 5
      //   288: bipush 11
      //   290: aload 19
      //   292: invokestatic 380	com/google/android/picasasync/UploadsManager:access$800	(Lcom/google/android/picasasync/UploadsManager;Lcom/google/android/picasasync/UploadTaskEntry;ILjava/lang/Throwable;)V
      //   295: aload_2
      //   296: lconst_1
      //   297: aload_2
      //   298: getfield 385	android/content/SyncStats:numSkippedEntries	J
      //   301: ladd
      //   302: putfield 385	android/content/SyncStats:numSkippedEntries	J
      //   305: aload_0
      //   306: getfield 20	com/google/android/picasasync/UploadsManager$UploadTask:this$0	Lcom/google/android/picasasync/UploadsManager;
      //   309: aload 5
      //   311: getfield 126	com/android/gallery3d/common/Entry:id	J
      //   314: invokestatic 361	com/google/android/picasasync/UploadsManager:access$900	(Lcom/google/android/picasasync/UploadsManager;J)Z
      //   317: pop
      //   318: aload_0
      //   319: aload 5
      //   321: aload_0
      //   322: getfield 20	com/google/android/picasasync/UploadsManager$UploadTask:this$0	Lcom/google/android/picasasync/UploadsManager;
      //   325: new 363	com/google/android/picasasync/UploadedEntry
      //   328: dup
      //   329: aload 5
      //   331: invokespecial 365	com/google/android/picasasync/UploadedEntry:<init>	(Lcom/google/android/picasasync/UploadTaskEntry;)V
      //   334: invokestatic 369	com/google/android/picasasync/UploadsManager:access$1000	(Lcom/google/android/picasasync/UploadsManager;Lcom/google/android/picasasync/UploadedEntry;)Lcom/google/android/picasasync/UploadedEntry;
      //   337: invokevirtual 371	com/google/android/picasasync/UploadsManager$UploadTask:onTaskDone	(Lcom/google/android/picasasync/UploadTaskEntry;Lcom/google/android/picasasync/UploadedEntry;)V
      //   340: goto -335 -> 5
      //   343: invokestatic 41	com/google/android/picasasync/UploadsManager:access$300	()Ljava/lang/String;
      //   346: new 43	java/lang/StringBuilder
      //   349: dup
      //   350: invokespecial 46	java/lang/StringBuilder:<init>	()V
      //   353: ldc_w 387
      //   356: invokevirtual 52	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   359: aload 5
      //   361: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   364: invokevirtual 58	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   367: invokestatic 333	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   370: pop
      //   371: aload_2
      //   372: getfield 390	android/content/SyncStats:numIoExceptions	J
      //   375: lstore 8
      //   377: aload_0
      //   378: getfield 20	com/google/android/picasasync/UploadsManager$UploadTask:this$0	Lcom/google/android/picasasync/UploadsManager;
      //   381: aload 5
      //   383: aload_0
      //   384: aload_1
      //   385: invokestatic 394	com/google/android/picasasync/UploadsManager:access$1200	(Lcom/google/android/picasasync/UploadsManager;Lcom/google/android/picasasync/UploadTaskEntry;Lcom/google/android/picasasync/Uploader$UploadProgressListener;Landroid/content/SyncResult;)Lcom/google/android/picasasync/UploadedEntry;
      //   388: astore 6
      //   390: aload 6
      //   392: ifnonnull +34 -> 426
      //   395: aload_2
      //   396: getfield 390	android/content/SyncStats:numIoExceptions	J
      //   399: lload 8
      //   401: lcmp
      //   402: ifle +18 -> 420
      //   405: iconst_1
      //   406: istore 17
      //   408: aload_0
      //   409: aload 5
      //   411: iload 17
      //   413: invokespecial 396	com/google/android/picasasync/UploadsManager$UploadTask:onIncompleteUpload	(Lcom/google/android/picasasync/UploadTaskEntry;Z)Z
      //   416: ifeq -216 -> 200
      //   419: return
      //   420: iconst_0
      //   421: istore 17
      //   423: goto -15 -> 408
      //   426: aload_0
      //   427: getfield 20	com/google/android/picasasync/UploadsManager$UploadTask:this$0	Lcom/google/android/picasasync/UploadsManager;
      //   430: aload 5
      //   432: iconst_4
      //   433: invokestatic 357	com/google/android/picasasync/UploadsManager:access$1100	(Lcom/google/android/picasasync/UploadsManager;Lcom/google/android/picasasync/UploadTaskEntry;I)V
      //   436: aload_1
      //   437: getfield 316	android/content/SyncResult:stats	Landroid/content/SyncStats;
      //   440: astore 10
      //   442: aload 10
      //   444: lconst_1
      //   445: aload 10
      //   447: getfield 399	android/content/SyncStats:numEntries	J
      //   450: ladd
      //   451: putfield 399	android/content/SyncStats:numEntries	J
      //   454: aload_1
      //   455: getfield 316	android/content/SyncResult:stats	Landroid/content/SyncStats;
      //   458: astore 11
      //   460: aload 11
      //   462: lconst_1
      //   463: aload 11
      //   465: getfield 402	android/content/SyncStats:numInserts	J
      //   468: ladd
      //   469: putfield 402	android/content/SyncStats:numInserts	J
      //   472: aload_0
      //   473: getfield 20	com/google/android/picasasync/UploadsManager$UploadTask:this$0	Lcom/google/android/picasasync/UploadsManager;
      //   476: aload 5
      //   478: aload 6
      //   480: aload_1
      //   481: invokestatic 406	com/google/android/picasasync/UploadsManager:access$1300	(Lcom/google/android/picasasync/UploadsManager;Lcom/google/android/picasasync/UploadTaskEntry;Lcom/google/android/picasasync/UploadedEntry;Landroid/content/SyncResult;)Z
      //   484: ifne -284 -> 200
      //   487: invokestatic 41	com/google/android/picasasync/UploadsManager:access$300	()Ljava/lang/String;
      //   490: astore 12
      //   492: iconst_2
      //   493: anewarray 276	java/lang/Object
      //   496: astore 13
      //   498: aload 13
      //   500: iconst_0
      //   501: aload 5
      //   503: invokevirtual 169	com/google/android/picasasync/UploadTaskEntry:getAlbumId	()Ljava/lang/String;
      //   506: invokestatic 324	com/android/gallery3d/common/Utils:maskDebugInfo	(Ljava/lang/Object;)Ljava/lang/String;
      //   509: aastore
      //   510: aload 13
      //   512: iconst_1
      //   513: aload 5
      //   515: invokevirtual 92	com/google/android/picasasync/UploadTaskEntry:getAccount	()Ljava/lang/String;
      //   518: invokestatic 324	com/android/gallery3d/common/Utils:maskDebugInfo	(Ljava/lang/Object;)Ljava/lang/String;
      //   521: aastore
      //   522: aload 12
      //   524: ldc_w 408
      //   527: aload 13
      //   529: invokestatic 330	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      //   532: invokestatic 333	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   535: pop
      //   536: aload_0
      //   537: aload_1
      //   538: aload_0
      //   539: getfield 266	com/google/android/picasasync/SyncTask:syncAccount	Ljava/lang/String;
      //   542: aload 5
      //   544: invokevirtual 169	com/google/android/picasasync/UploadTaskEntry:getAlbumId	()Ljava/lang/String;
      //   547: invokevirtual 349	com/google/android/picasasync/UploadsManager$UploadTask:syncAlbum	(Landroid/content/SyncResult;Ljava/lang/String;Ljava/lang/String;)V
      //   550: goto -350 -> 200
      //   553: astore 4
      //   555: goto -514 -> 41
      //
      // Exception table:
      //   from	to	target	type
      //   19	31	39	finally
      //   41	43	39	finally
      //   123	135	250	java/lang/Throwable
      //   31	33	553	finally
    }

    protected void stopCurrentTask(int paramInt)
    {
      UploadTaskEntry localUploadTaskEntry = this.mCurrentTask;
      Log.d(UploadsManager.TAG, "stopCurrentTask: " + localUploadTaskEntry);
      if (localUploadTaskEntry == null)
        return;
      synchronized (UploadsManager.this)
      {
        if (localUploadTaskEntry.isCancellable())
        {
          localUploadTaskEntry.setState(paramInt);
          UploadsManager.this.notify();
        }
        return;
      }
    }

    protected void syncAlbum(SyncResult paramSyncResult, String paramString1, String paramString2)
    {
      String str1 = paramString1 + "," + paramString2;
      PicasaSyncHelper.SyncContext localSyncContext = this.mSyncContext;
      PicasaSyncHelper localPicasaSyncHelper = UploadsManager.this.mSyncHelper;
      synchronized (UploadsManager.this)
      {
        if (UploadsManager.this.mSyncedAccountAlbumPairs.contains(str1))
          return;
        if (!this.mRunning)
          return;
      }
      monitorexit;
      String str2 = UploadsManager.TAG;
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = Utils.maskDebugInfo(paramString1);
      arrayOfObject[1] = Utils.maskDebugInfo(paramString2);
      Log.d(str2, String.format("sync album for dedup: %s/%s", arrayOfObject));
      AlbumEntry localAlbumEntry = UploadsManager.this.mPicasaDbHelper.getAlbumEntry(paramString1, paramString2);
      long l = paramSyncResult.stats.numAuthExceptions;
      if (localAlbumEntry == null)
      {
        Log.d(UploadsManager.TAG, "sync albumlist to get ID for " + Utils.maskDebugInfo(paramString2));
        UserEntry localUserEntry = localPicasaSyncHelper.findUser(paramString1);
        if (localUserEntry == null)
          break label288;
        localPicasaSyncHelper.syncAlbumsForUser(localSyncContext, localUserEntry);
        localAlbumEntry = UploadsManager.this.mPicasaDbHelper.getAlbumEntry(paramString1, paramString2);
      }
      if (localAlbumEntry != null)
        if ("camera-sync".equals(paramString2))
          localPicasaSyncHelper.syncUploadedPhotos(localSyncContext, paramString1);
      while (true)
      {
        UploadsManager localUploadsManager2 = UploadsManager.this;
        monitorenter;
        if (localAlbumEntry != null);
        try
        {
          UploadsManager.this.mSyncedAccountAlbumPairs.add(str1);
          return;
        }
        finally
        {
          monitorexit;
        }
        label288: Log.e(UploadsManager.TAG, "no userEntry for " + Utils.maskDebugInfo(paramString1));
        cancelSync();
        UploadsManager.this.mProblematicAccounts.add(paramString1);
        return;
        if (!"Buzz".equals(localAlbumEntry.albumType))
          localPicasaSyncHelper.syncPhotosForAlbum(localSyncContext, localAlbumEntry);
        Log.d(UploadsManager.TAG, "post album; don't sync");
        continue;
        if (l < paramSyncResult.stats.numAuthExceptions)
        {
          Log.d(UploadsManager.TAG, "need authorization for picasa access: " + Utils.maskDebugInfo(paramString1));
          cancelSync();
          UploadsManager.this.mProblematicAccounts.add(paramString1);
          return;
        }
        if ("camera-sync".equals(paramString2))
          Log.d(UploadsManager.TAG, "album doesn't exist yet: " + Utils.maskDebugInfo(paramString2));
        Log.w(UploadsManager.TAG, "album doesn't exist: " + Utils.maskDebugInfo(paramString2));
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.UploadsManager
 * JD-Core Version:    0.5.4
 */