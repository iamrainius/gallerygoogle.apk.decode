package com.google.android.picasastore;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import com.android.gallery3d.common.Fingerprint;
import com.android.gallery3d.common.Utils;

public class PicasaPhotoContentProvider0 extends ContentProvider
{
  private static final Uri EXTERNAL_STORAGE_FSID_URI = Uri.parse("content://media/external/fs_id");
  private String mAuthority;
  private int mExternalStorageFsId;
  private FingerprintManager mFingerprintManager;
  private boolean mIsExternalStorageFsIdReady = false;
  private PicasaStore mPicasaStore;
  private SharedPreferences mPrefs;
  private final UriMatcher mUriMatcher = new UriMatcher(-1);

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
      Log.d("PicasaPhotoCP", "No FSID on this device!");
      return -1;
    }
    finally
    {
      Utils.closeSilently(localCursor);
    }
  }

  private PicasaStore getPicasaStore()
  {
    if (this.mPicasaStore == null)
      this.mPicasaStore = new PicasaStore(getContext());
    return this.mPicasaStore;
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
        case 1:
        }
        PicasaPhotoContentProvider0.this.onFsIdChanged();
      }
    };
  }

  private int invalidateFingerprints(String[] paramArrayOfString)
  {
    return this.mFingerprintManager.invalidate(paramArrayOfString);
  }

  private static boolean isExternalStorageMounted()
  {
    String str = Environment.getExternalStorageState();
    return (str.equals("mounted")) || (str.equals("mounted_ro"));
  }

  private void onFsIdChanged()
  {
    monitorenter;
    while (true)
    {
      Context localContext;
      int i;
      try
      {
        boolean bool = isExternalStorageMounted();
        if (!bool)
          return;
        localContext = getContext();
        i = getFsId(localContext);
        if (!this.mIsExternalStorageFsIdReady)
        {
          this.mIsExternalStorageFsIdReady = this.mPrefs.contains("external_storage_fsid");
          if (this.mIsExternalStorageFsIdReady)
            this.mExternalStorageFsId = this.mPrefs.getInt("external_storage_fsid", -1);
        }
        if (this.mIsExternalStorageFsIdReady)
          break label145;
        Log.d("PicasaPhotoCP", "set fsid first time:" + i);
        this.mIsExternalStorageFsIdReady = true;
        this.mExternalStorageFsId = i;
      }
      finally
      {
        monitorexit;
      }
      label145: if (this.mExternalStorageFsId == i)
        continue;
      Log.d("PicasaPhotoCP", "fsid changed: " + this.mExternalStorageFsId + " -> " + i);
      this.mExternalStorageFsId = i;
      this.mPrefs.edit().putInt("external_storage_fsid", i).commit();
      if (!PicasaStoreFacade.get(localContext).isMaster())
        continue;
      this.mFingerprintManager.reset();
    }
  }

  private Cursor queryFingerprint(Uri paramUri, String[] paramArrayOfString)
  {
    boolean bool = "1".equals(paramUri.getQueryParameter("force_recalculate"));
    int i;
    label33: PicasaMatrixCursor localPicasaMatrixCursor;
    FingerprintManager localFingerprintManager;
    Object[] arrayOfObject;
    int l;
    label68: Fingerprint localFingerprint2;
    if ((!bool) && ("1".equals(paramUri.getQueryParameter("cache_only"))))
    {
      i = 1;
      localPicasaMatrixCursor = new PicasaMatrixCursor(paramArrayOfString);
      localFingerprintManager = this.mFingerprintManager;
      arrayOfObject = new Object[paramArrayOfString.length];
      if (i == 0)
        break label123;
      l = 0;
      int i1 = paramArrayOfString.length;
      if (l >= i1)
        break label180;
      localFingerprint2 = localFingerprintManager.getCachedFingerprint(paramArrayOfString[l]);
      if (localFingerprint2 != null)
        break label113;
    }
    for (byte[] arrayOfByte2 = null; ; arrayOfByte2 = localFingerprint2.getBytes())
    {
      arrayOfObject[l] = arrayOfByte2;
      ++l;
      break label68:
      i = 0;
      label113: break label33:
    }
    label123: int j = 0;
    int k = paramArrayOfString.length;
    if (j < k)
    {
      label130: Fingerprint localFingerprint1 = localFingerprintManager.getFingerprint(paramArrayOfString[j], bool);
      if (localFingerprint1 == null);
      for (byte[] arrayOfByte1 = null; ; arrayOfByte1 = localFingerprint1.getBytes())
      {
        arrayOfObject[j] = arrayOfByte1;
        ++j;
        break label130:
      }
    }
    label180: localPicasaMatrixCursor.addRow(arrayOfObject);
    return localPicasaMatrixCursor;
  }

  private Cursor queryInternal(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    switch (this.mUriMatcher.match(paramUri))
    {
    default:
      throw new IllegalArgumentException("Invalid URI: " + paramUri);
    case 3:
    }
    return queryFingerprint(paramUri, paramArrayOfString1);
  }

  public void attachInfo(Context paramContext, ProviderInfo paramProviderInfo)
  {
    super.attachInfo(paramContext, paramProviderInfo);
    this.mAuthority = paramProviderInfo.authority;
    this.mUriMatcher.addURI(this.mAuthority, "photos", 1);
    this.mUriMatcher.addURI(this.mAuthority, "fingerprint", 3);
    this.mUriMatcher.addURI(this.mAuthority, "photos/#", 2);
    this.mUriMatcher.addURI(this.mAuthority, "albumcovers/#", 4);
  }

  public int delete(Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    switch (this.mUriMatcher.match(paramUri))
    {
    default:
      throw new IllegalArgumentException("unsupported uri:" + paramUri);
    case 3:
    }
    return invalidateFingerprints(paramArrayOfString);
  }

  public String getType(Uri paramUri)
  {
    switch (this.mUriMatcher.match(paramUri))
    {
    case 3:
    default:
      throw new IllegalArgumentException("Invalid URI: " + paramUri);
    case 1:
      return "vnd.android.cursor.dir/vnd.google.android.picasasync.item";
    case 2:
      return "vnd.android.cursor.item/vnd.google.android.picasasync.item";
    case 4:
    }
    return "vnd.android.cursor.item/vnd.google.android.picasasync.album_cover";
  }

  public Uri insert(Uri paramUri, ContentValues paramContentValues)
  {
    int i = MetricsUtils.begin("INSERT " + paramUri);
    try
    {
      this.mUriMatcher.match(paramUri);
      throw new IllegalArgumentException("unsupported uri:" + paramUri);
    }
    finally
    {
      MetricsUtils.end(i);
    }
  }

  public boolean onCreate()
  {
    Context localContext = getContext();
    Config.init(localContext);
    this.mFingerprintManager = FingerprintManager.get(localContext);
    this.mPrefs = PreferenceManager.getDefaultSharedPreferences(localContext);
    HandlerThread localHandlerThread = new HandlerThread("picasa-photo-provider", 10);
    localHandlerThread.start();
    Handler localHandler = initHandler(localHandlerThread);
    localContext.getContentResolver().registerContentObserver(EXTERNAL_STORAGE_FSID_URI, false, new ContentObserver(localHandler)
    {
      public void onChange(boolean paramBoolean)
      {
        PicasaPhotoContentProvider0.this.onFsIdChanged();
      }
    });
    Message.obtain(localHandler, 1).sendToTarget();
    return true;
  }

  // ERROR //
  public android.os.ParcelFileDescriptor openFile(Uri paramUri, String paramString)
    throws java.io.FileNotFoundException
  {
    // Byte code:
    //   0: new 162	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 163	java/lang/StringBuilder:<init>	()V
    //   7: ldc_w 357
    //   10: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   13: aload_1
    //   14: invokevirtual 358	android/net/Uri:toString	()Ljava/lang/String;
    //   17: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: invokevirtual 175	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   23: invokestatic 305	com/google/android/picasastore/MetricsUtils:begin	(Ljava/lang/String;)I
    //   26: istore_3
    //   27: aload_0
    //   28: getfield 42	com/google/android/picasastore/PicasaPhotoContentProvider0:mUriMatcher	Landroid/content/UriMatcher;
    //   31: aload_1
    //   32: invokevirtual 246	android/content/UriMatcher:match	(Landroid/net/Uri;)I
    //   35: tableswitch	default:+25 -> 60, 2:+62->97, 3:+25->60, 4:+80->115
    //   61: nop
    //   62: <illegal opcode>
    //   63: dup
    //   64: new 162	java/lang/StringBuilder
    //   67: dup
    //   68: invokespecial 163	java/lang/StringBuilder:<init>	()V
    //   71: ldc_w 360
    //   74: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   77: aload_1
    //   78: invokevirtual 253	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   81: invokevirtual 175	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   84: invokespecial 256	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   87: athrow
    //   88: astore 4
    //   90: iload_3
    //   91: invokestatic 308	com/google/android/picasastore/MetricsUtils:end	(I)V
    //   94: aload 4
    //   96: athrow
    //   97: aload_0
    //   98: invokespecial 362	com/google/android/picasastore/PicasaPhotoContentProvider0:getPicasaStore	()Lcom/google/android/picasastore/PicasaStore;
    //   101: aload_1
    //   102: aload_2
    //   103: invokevirtual 364	com/google/android/picasastore/PicasaStore:openFile	(Landroid/net/Uri;Ljava/lang/String;)Landroid/os/ParcelFileDescriptor;
    //   106: astore 6
    //   108: iload_3
    //   109: invokestatic 308	com/google/android/picasastore/MetricsUtils:end	(I)V
    //   112: aload 6
    //   114: areturn
    //   115: aload_0
    //   116: invokespecial 362	com/google/android/picasastore/PicasaPhotoContentProvider0:getPicasaStore	()Lcom/google/android/picasastore/PicasaStore;
    //   119: aload_1
    //   120: aload_2
    //   121: invokevirtual 367	com/google/android/picasastore/PicasaStore:openAlbumCover	(Landroid/net/Uri;Ljava/lang/String;)Landroid/os/ParcelFileDescriptor;
    //   124: astore 5
    //   126: iload_3
    //   127: invokestatic 308	com/google/android/picasastore/MetricsUtils:end	(I)V
    //   130: aload 5
    //   132: areturn
    //
    // Exception table:
    //   from	to	target	type
    //   27	60	88	finally
    //   60	88	88	finally
    //   97	108	88	finally
    //   115	126	88	finally
  }

  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    int i = MetricsUtils.begin("QUERY " + paramUri.toString());
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
    this.mUriMatcher.match(paramUri);
    throw new IllegalArgumentException("unsupported uri:" + paramUri);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasastore.PicasaPhotoContentProvider0
 * JD-Core Version:    0.5.4
 */