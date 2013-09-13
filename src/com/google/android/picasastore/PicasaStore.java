package com.google.android.picasastore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import android.os.SystemClock;
import android.util.Log;
import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.common.BlobCache;
import com.android.gallery3d.common.FileCache;
import com.android.gallery3d.common.FileCache.CacheEntry;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.WeakHashMap;

class PicasaStore
  implements UrlDownloader.Controller
{
  private BlobCache mBlobCache;
  private PipeDataWriter<byte[]> mBytesWriter;
  private File mCacheDir;
  private final Context mContext;
  private final Method mCreatePipe;
  private FileCache mFileCache;
  private PipeDataWriter<ImagePack> mImagePackWriter;
  private final WeakHashMap<ParcelFileDescriptor, Socket> mKeepAlive;
  private final BroadcastReceiver mMountListener;
  private final ServerSocket mServerSocket;
  private final ThreadPool mThreadPool;
  private final UrlDownloader mUrlDownloader;
  private boolean mUsingInternalStorage;
  private VersionInfo mVersionInfo;

  // ERROR //
  PicasaStore(Context paramContext)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 45	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: iconst_0
    //   6: putfield 47	com/google/android/picasastore/PicasaStore:mUsingInternalStorage	Z
    //   9: aload_0
    //   10: new 49	java/util/WeakHashMap
    //   13: dup
    //   14: invokespecial 50	java/util/WeakHashMap:<init>	()V
    //   17: putfield 52	com/google/android/picasastore/PicasaStore:mKeepAlive	Ljava/util/WeakHashMap;
    //   20: aload_0
    //   21: new 54	com/android/gallery3d/util/ThreadPool
    //   24: dup
    //   25: invokespecial 55	com/android/gallery3d/util/ThreadPool:<init>	()V
    //   28: putfield 57	com/google/android/picasastore/PicasaStore:mThreadPool	Lcom/android/gallery3d/util/ThreadPool;
    //   31: aload_0
    //   32: new 59	com/google/android/picasastore/PicasaStore$2
    //   35: dup
    //   36: aload_0
    //   37: invokespecial 62	com/google/android/picasastore/PicasaStore$2:<init>	(Lcom/google/android/picasastore/PicasaStore;)V
    //   40: putfield 64	com/google/android/picasastore/PicasaStore:mImagePackWriter	Lcom/google/android/picasastore/PicasaStore$PipeDataWriter;
    //   43: aload_0
    //   44: new 66	com/google/android/picasastore/PicasaStore$3
    //   47: dup
    //   48: aload_0
    //   49: invokespecial 67	com/google/android/picasastore/PicasaStore$3:<init>	(Lcom/google/android/picasastore/PicasaStore;)V
    //   52: putfield 69	com/google/android/picasastore/PicasaStore:mBytesWriter	Lcom/google/android/picasastore/PicasaStore$PipeDataWriter;
    //   55: aload_0
    //   56: aload_1
    //   57: invokevirtual 75	android/content/Context:getApplicationContext	()Landroid/content/Context;
    //   60: putfield 77	com/google/android/picasastore/PicasaStore:mContext	Landroid/content/Context;
    //   63: aload_0
    //   64: new 79	com/google/android/picasastore/UrlDownloader
    //   67: dup
    //   68: aload_0
    //   69: invokespecial 82	com/google/android/picasastore/UrlDownloader:<init>	(Lcom/google/android/picasastore/UrlDownloader$Controller;)V
    //   72: putfield 84	com/google/android/picasastore/PicasaStore:mUrlDownloader	Lcom/google/android/picasastore/UrlDownloader;
    //   75: aload_0
    //   76: new 86	java/net/ServerSocket
    //   79: dup
    //   80: invokespecial 87	java/net/ServerSocket:<init>	()V
    //   83: putfield 89	com/google/android/picasastore/PicasaStore:mServerSocket	Ljava/net/ServerSocket;
    //   86: aload_0
    //   87: getfield 89	com/google/android/picasastore/PicasaStore:mServerSocket	Ljava/net/ServerSocket;
    //   90: aconst_null
    //   91: iconst_5
    //   92: invokevirtual 93	java/net/ServerSocket:bind	(Ljava/net/SocketAddress;I)V
    //   95: ldc 95
    //   97: ldc 97
    //   99: iconst_0
    //   100: anewarray 99	java/lang/Class
    //   103: invokevirtual 103	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   106: astore 7
    //   108: aload 7
    //   110: astore 4
    //   112: aload_0
    //   113: aload 4
    //   115: putfield 105	com/google/android/picasastore/PicasaStore:mCreatePipe	Ljava/lang/reflect/Method;
    //   118: aload_0
    //   119: new 107	com/google/android/picasastore/PicasaStore$1
    //   122: dup
    //   123: aload_0
    //   124: invokespecial 108	com/google/android/picasastore/PicasaStore$1:<init>	(Lcom/google/android/picasastore/PicasaStore;)V
    //   127: putfield 110	com/google/android/picasastore/PicasaStore:mMountListener	Landroid/content/BroadcastReceiver;
    //   130: new 112	android/content/IntentFilter
    //   133: dup
    //   134: invokespecial 113	android/content/IntentFilter:<init>	()V
    //   137: astore 5
    //   139: aload 5
    //   141: ldc 115
    //   143: invokevirtual 119	android/content/IntentFilter:addAction	(Ljava/lang/String;)V
    //   146: aload 5
    //   148: ldc 121
    //   150: invokevirtual 119	android/content/IntentFilter:addAction	(Ljava/lang/String;)V
    //   153: aload 5
    //   155: ldc 123
    //   157: invokevirtual 126	android/content/IntentFilter:addDataScheme	(Ljava/lang/String;)V
    //   160: aload_0
    //   161: getfield 77	com/google/android/picasastore/PicasaStore:mContext	Landroid/content/Context;
    //   164: aload_0
    //   165: getfield 110	com/google/android/picasastore/PicasaStore:mMountListener	Landroid/content/BroadcastReceiver;
    //   168: aload 5
    //   170: invokevirtual 130	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;
    //   173: pop
    //   174: return
    //   175: astore_2
    //   176: new 132	java/lang/RuntimeException
    //   179: dup
    //   180: ldc 134
    //   182: aload_2
    //   183: invokespecial 137	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   186: athrow
    //   187: astore_3
    //   188: aconst_null
    //   189: astore 4
    //   191: goto -79 -> 112
    //
    // Exception table:
    //   from	to	target	type
    //   75	95	175	java/io/IOException
    //   95	108	187	java/lang/NoSuchMethodException
  }

  private boolean checkCacheVersion(String paramString, int paramInt)
  {
    if (this.mVersionInfo == null)
      this.mVersionInfo = new VersionInfo(getCacheDirectory() + "/" + "cache_versions.info");
    if (this.mVersionInfo.getVersion(paramString) != paramInt)
    {
      this.mVersionInfo.setVersion(paramString, paramInt);
      this.mVersionInfo.sync();
      return true;
    }
    return false;
  }

  private ParcelFileDescriptor[] createPipe()
    throws IOException
  {
    if (this.mCreatePipe == null)
      return createSocketPipe();
    try
    {
      ParcelFileDescriptor[] arrayOfParcelFileDescriptor = (ParcelFileDescriptor[])(ParcelFileDescriptor[])this.mCreatePipe.invoke(null, new Object[0]);
      return arrayOfParcelFileDescriptor;
    }
    catch (Throwable localThrowable)
    {
      Log.e("PicasaStore", "fail to create pipe", localThrowable);
      if (localThrowable instanceof IOException)
        throw ((IOException)localThrowable);
      throw new IOException(localThrowable.getMessage());
    }
  }

  private ParcelFileDescriptor[] createSocketPipe()
    throws IOException
  {
    Socket[] arrayOfSocket = new Socket[2];
    monitorenter;
    try
    {
      arrayOfSocket[0] = new Socket(this.mServerSocket.getInetAddress(), this.mServerSocket.getLocalPort());
      arrayOfSocket[1] = this.mServerSocket.accept();
      monitorexit;
      ParcelFileDescriptor[] arrayOfParcelFileDescriptor = new ParcelFileDescriptor[2];
      arrayOfParcelFileDescriptor[0] = ParcelFileDescriptor.fromSocket(arrayOfSocket[0]);
      arrayOfParcelFileDescriptor[1] = ParcelFileDescriptor.fromSocket(arrayOfSocket[1]);
      this.mKeepAlive.put(arrayOfParcelFileDescriptor[0], arrayOfSocket[0]);
      return arrayOfParcelFileDescriptor;
    }
    finally
    {
      monitorexit;
    }
  }

  private ParcelFileDescriptor findInCacheOrDownload(long paramLong, String paramString)
    throws FileNotFoundException
  {
    monitorenter;
    Object localObject2;
    try
    {
      FileCache localFileCache = getDownloadCache();
      if (localFileCache == null)
        break label61;
      FileCache.CacheEntry localCacheEntry = localFileCache.lookup(paramString);
      if (localCacheEntry == null)
        break label61;
      ParcelFileDescriptor localParcelFileDescriptor2 = ParcelFileDescriptor.open(localCacheEntry.cacheFile, 268435456);
      localObject2 = localParcelFileDescriptor2;
      label61: return localObject2;
    }
    catch (Throwable localThrowable1)
    {
      try
      {
        ParcelFileDescriptor localParcelFileDescriptor1 = openPipeHelper(null, new InputStreamWriter(paramLong, this.mUrlDownloader.openInputStream(paramString)));
        localObject2 = localParcelFileDescriptor1;
      }
      catch (Throwable localThrowable2)
      {
        Log.w("PicasaStore", "download fail", localThrowable2);
        throw new FileNotFoundException();
      }
    }
    finally
    {
      monitorexit;
    }
  }

  private File getAlbumCoverFile(long paramLong, String paramString)
  {
    File localFile = PicasaStoreFacade.getAlbumCoverCacheFile(paramLong, paramString, ".thumb");
    if (localFile.isFile())
      return localFile;
    return null;
  }

  private BlobCache getBlobCache()
  {
    monitorenter;
    File localFile;
    try
    {
      localFile = getCacheDirectory();
    }
    finally
    {
      try
      {
        String str = localFile.getAbsolutePath() + "/" + "picasa-cache";
        if (checkCacheVersion("picasa-image-cache-version", 5))
          BlobCache.deleteFiles(str);
        if (this.mUsingInternalStorage)
        {
          this.mBlobCache = new BlobCache(str, 1250, 52428800, false, 5);
          label88: BlobCache localBlobCache = this.mBlobCache;
          monitorexit;
          return localBlobCache;
        }
        this.mBlobCache = new BlobCache(str, 5000, 209715200, false, 5);
      }
      catch (Throwable localThrowable)
      {
        Log.w("PicasaStore", "fail to create blob cache", localThrowable);
        break label88:
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }
  }

  private FileCache getDownloadCache()
  {
    monitorenter;
    try
    {
      FileCache localFileCache1;
      label80: if (localFileCache1 != null);
    }
    finally
    {
      try
      {
        File localFile = new File(getCacheDirectory(), "download-cache");
        if (checkCacheVersion("picasa-download-cache-version", 1))
          FileCache.deleteFiles(this.mContext, localFile, "picasa-downloads");
        if (!this.mUsingInternalStorage)
        {
          this.mFileCache = new FileCache(this.mContext, localFile, "picasa-downloads", 104857600L);
          FileCache localFileCache2 = this.mFileCache;
          monitorexit;
          return localFileCache2;
        }
        this.mFileCache = new FileCache(this.mContext, localFile, "picasa-downloads", 20971520L);
      }
      catch (Throwable localThrowable)
      {
        Log.w("PicasaStore", "fail to create file cache", localThrowable);
        break label80:
        localObject = finally;
        monitorexit;
        throw localObject;
      }
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
      Log.w("PicasaStore", "cannot get id from: " + paramUri);
    }
    return -1L;
  }

  private boolean isKeyMatched(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
  {
    if (paramArrayOfByte2 == null)
      if (paramArrayOfByte3.length >= paramArrayOfByte1.length);
    int i;
    int i1;
    int i2;
    do
    {
      int l;
      do
      {
        do
        {
          return false;
          int i6 = 0;
          int i7 = paramArrayOfByte1.length;
          while (true)
          {
            if (i6 >= i7)
              break label179;
            if (paramArrayOfByte1[i6] == paramArrayOfByte3[i6]);
            ++i6;
          }
          i = 3 + (paramArrayOfByte1.length + paramArrayOfByte2.length);
        }
        while ((paramArrayOfByte3.length < i) || (isKeyTooLong(i)));
        int j = 0;
        int k = paramArrayOfByte1.length;
        while (j < k)
        {
          if (paramArrayOfByte1[j] == paramArrayOfByte3[j]);
          ++j;
        }
        l = paramArrayOfByte1.length;
        i1 = l + 1;
      }
      while (paramArrayOfByte3[l] != (byte)i);
      i2 = i1 + 1;
    }
    while (paramArrayOfByte3[i1] != (byte)(i >>> 8));
    int i3 = i2 + 1;
    int i4 = 0;
    int i5 = paramArrayOfByte2.length;
    while (i4 < i5)
    {
      if (paramArrayOfByte2[i4] == paramArrayOfByte3[(i4 + i3)]);
      ++i4;
    }
    label179: return true;
  }

  private boolean isKeyTooLong(int paramInt)
  {
    return paramInt > 32767;
  }

  private ImagePack lookupBlobCache(long paramLong, int paramInt, String paramString)
  {
    byte[] arrayOfByte1 = makeKey(paramLong, paramInt);
    if (paramString == null);
    for (byte[] arrayOfByte2 = null; ; arrayOfByte2 = paramString.getBytes())
      return lookupBlobCache(arrayOfByte1, arrayOfByte2);
  }

  private ImagePack lookupBlobCache(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    try
    {
      BlobCache localBlobCache = getBlobCache();
      if (localBlobCache == null)
        return null;
      long l = Utils.crc64Long(paramArrayOfByte1);
      monitorenter;
      try
      {
        byte[] arrayOfByte = localBlobCache.lookup(l);
        monitorexit;
        if ((arrayOfByte == null) || (!isKeyMatched(paramArrayOfByte1, paramArrayOfByte2, arrayOfByte)))
          break label118;
        ImagePack localImagePack;
        return localImagePack;
      }
      finally
      {
        monitorexit;
      }
    }
    catch (Throwable localThrowable)
    {
      Log.w("PicasaStore", "cache lookup fail", localThrowable);
    }
    label118: return null;
  }

  private byte[] makeKey(long paramLong, int paramInt)
  {
    byte[] arrayOfByte = new byte[9];
    for (int i = 0; i < 8; ++i)
      arrayOfByte[i] = (byte)(int)(paramLong >>> i * 8);
    arrayOfByte[8] = (byte)paramInt;
    return arrayOfByte;
  }

  private void onMediaMountOrUnmount()
  {
    monitorenter;
    try
    {
      this.mCacheDir = null;
      if (this.mBlobCache != null)
      {
        Utils.closeSilently(this.mBlobCache);
        this.mBlobCache = null;
      }
      if (this.mFileCache != null)
      {
        Utils.closeSilently(this.mFileCache);
        this.mFileCache = null;
      }
      this.mVersionInfo = null;
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  private <T> ParcelFileDescriptor openPipeHelper(T paramT, PipeDataWriter<T> paramPipeDataWriter)
    throws FileNotFoundException
  {
    try
    {
      ParcelFileDescriptor[] arrayOfParcelFileDescriptor = createPipe();
      4 local4 = new ThreadPool.Job(paramPipeDataWriter, arrayOfParcelFileDescriptor, paramT)
      {
        public Void run(ThreadPool.JobContext paramJobContext)
        {
          try
          {
            this.val$func.writeDataToPipe(this.val$pipe[1], this.val$args);
            return null;
          }
          finally
          {
            Utils.closeSilently(this.val$pipe[1]);
          }
        }
      };
      this.mThreadPool.submit(local4);
      ParcelFileDescriptor localParcelFileDescriptor = arrayOfParcelFileDescriptor[0];
      return localParcelFileDescriptor;
    }
    catch (IOException localIOException)
    {
      throw new FileNotFoundException("failure making pipe");
    }
  }

  private ParcelFileDescriptor openUnknownImage(String paramString1, String paramString2)
    throws FileNotFoundException
  {
    if ("thumbnail".equals(paramString2));
    for (paramString1 = PicasaStoreFacade.convertImageUrl(paramString1, Config.sThumbNailSize, true); ; paramString1 = PicasaStoreFacade.convertImageUrl(paramString1, Config.sScreenNailSize, false))
      do
        return findInCacheOrDownload(-1L, paramString1);
      while (!"screennail".equals(paramString2));
  }

  private void putBlobCache(long paramLong, int paramInt1, String paramString, int paramInt2, byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte1 = makeKey(paramLong, paramInt1);
    if (paramString == null);
    for (byte[] arrayOfByte2 = null; ; arrayOfByte2 = paramString.getBytes())
    {
      putBlobCache(arrayOfByte1, arrayOfByte2, paramInt2, paramArrayOfByte);
      return;
    }
  }

  private void putBlobCache(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt, byte[] paramArrayOfByte3)
  {
    int i = 0;
    if (paramArrayOfByte2 == null);
    int j;
    while (true)
    {
      j = 3 + (i + paramArrayOfByte1.length);
      if (!isKeyTooLong(j))
        break;
      return;
      i = paramArrayOfByte2.length;
    }
    try
    {
      BlobCache localBlobCache = getBlobCache();
      if (localBlobCache != null);
      byte[] arrayOfByte = new byte[j + paramArrayOfByte3.length];
      System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, paramArrayOfByte1.length);
      arrayOfByte[paramArrayOfByte1.length] = (byte)j;
      arrayOfByte[(1 + paramArrayOfByte1.length)] = (byte)(j >>> 8);
      arrayOfByte[(2 + paramArrayOfByte1.length)] = (byte)paramInt;
      if (i > 0)
        System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, 3 + paramArrayOfByte1.length, i);
      System.arraycopy(paramArrayOfByte3, 0, arrayOfByte, j, paramArrayOfByte3.length);
      long l = Utils.crc64Long(paramArrayOfByte1);
      monitorenter;
      try
      {
        localBlobCache.insert(l, arrayOfByte);
        return;
      }
      finally
      {
        monitorexit;
      }
    }
    catch (Throwable localThrowable)
    {
      Log.w("PicasaStore", "cache insert fail", localThrowable);
    }
  }

  public File createTempFile()
  {
    try
    {
      FileCache localFileCache = getDownloadCache();
      if (localFileCache == null)
        return null;
      File localFile = localFileCache.createFile();
      return localFile;
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }

  public File getCacheDirectory()
  {
    monitorenter;
    File localFile1;
    try
    {
      if (this.mCacheDir != null)
      {
        localFile1 = this.mCacheDir;
        return localFile1;
      }
      this.mCacheDir = PicasaStoreFacade.getCacheDirectory();
      this.mUsingInternalStorage = false;
      if (this.mCacheDir == null)
      {
        Log.v("PicasaStore", "switch to internal storage for picasastore cache");
        label92: this.mUsingInternalStorage = true;
      }
    }
    finally
    {
      try
      {
        File localFile2 = new File(this.mCacheDir, ".nomedia");
        if (!localFile2.exists())
          localFile2.createNewFile();
        Utils.checkNotNull(this.mCacheDir);
        localFile1 = this.mCacheDir;
      }
      catch (IOException localIOException)
      {
        Log.w("PicasaStore", "fail to create '.nomedia' in " + this.mCacheDir);
        break label92:
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }
  }

  public void onDownloadComplete(String paramString, File paramFile)
  {
    monitorenter;
    try
    {
      FileCache localFileCache = getDownloadCache();
      if (localFileCache != null)
      {
        localFileCache.store(paramString, paramFile);
        return;
      }
    }
    finally
    {
      monitorexit;
    }
  }

  public ParcelFileDescriptor openAlbumCover(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    if (paramString.contains("w"))
      throw new FileNotFoundException("invalid mode: " + paramString);
    long l = getItemIdFromUri(paramUri);
    String str = paramUri.getQueryParameter("content_url");
    ImagePack localImagePack = lookupBlobCache(l, 2, str);
    if (localImagePack != null)
      return openPipeHelper(localImagePack, this.mImagePackWriter);
    if (str == null)
      throw new FileNotFoundException(Utils.maskDebugInfo(paramUri.toString()));
    File localFile = getAlbumCoverFile(l, str);
    if (localFile != null)
    {
      if (localFile.length() < 524288L)
      {
        byte[] arrayOfByte = new byte[(int)localFile.length()];
        FileInputStream localFileInputStream = new FileInputStream(localFile);
        int i = 0;
        try
        {
          int i1;
          for (int j = localFileInputStream.read(arrayOfByte, 0, arrayOfByte.length - 0); j >= 0; j = localFileInputStream.read(arrayOfByte, i, i1))
          {
            int k = arrayOfByte.length;
            if (i >= k)
              break;
            i += j;
            i1 = arrayOfByte.length - i;
          }
          putBlobCache(l, 2, str, 0, arrayOfByte);
          ParcelFileDescriptor localParcelFileDescriptor2 = openPipeHelper(arrayOfByte, this.mBytesWriter);
          return localParcelFileDescriptor2;
        }
        catch (IOException localIOException)
        {
        }
        finally
        {
          Utils.closeSilently(localFileInputStream);
        }
      }
      return ParcelFileDescriptor.open(localFile, 268435456);
    }
    try
    {
      ParcelFileDescriptor localParcelFileDescriptor1 = openPipeHelper(null, new DownloadWriter(l, PicasaStoreFacade.convertImageUrl(str, Config.sThumbNailSize, true), new BlobCacheRegister(l, 2, 0, str)));
      return localParcelFileDescriptor1;
    }
    catch (Throwable localThrowable)
    {
      Log.w("PicasaStore", "download fail", localThrowable);
      throw new FileNotFoundException();
    }
  }

  public ParcelFileDescriptor openFile(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    if (paramString.contains("w"))
      throw new FileNotFoundException("invalid mode: " + paramString);
    String str1 = paramUri.getQueryParameter("type");
    long l = getItemIdFromUri(paramUri);
    String str2 = paramUri.getQueryParameter("content_url");
    if (l != 0L)
    {
      if ("screennail".equals(str1))
        return openScreenNail(l, str2);
      if ("thumbnail".equals(str1))
        return openThumbNail(l, str2);
      return openFullImage(l, str2);
    }
    if (str2 != null)
      return openUnknownImage(str2, str1);
    throw new FileNotFoundException(paramUri.toString());
  }

  public ParcelFileDescriptor openFullImage(long paramLong, String paramString)
    throws FileNotFoundException
  {
    try
    {
      File localFile = PicasaStoreFacade.getCacheFile(paramLong, ".full");
      if (localFile == null)
        break label40;
      ParcelFileDescriptor localParcelFileDescriptor = ParcelFileDescriptor.open(localFile, 268435456);
      label40: return localParcelFileDescriptor;
    }
    catch (Throwable localThrowable)
    {
      Log.w("PicasaStore", "openFullImage from cache", localThrowable);
      if (paramString == null)
        throw new FileNotFoundException();
    }
    return findInCacheOrDownload(paramLong, paramString);
  }

  public ParcelFileDescriptor openScreenNail(long paramLong, String paramString)
    throws FileNotFoundException
  {
    ImagePack localImagePack = lookupBlobCache(paramLong, 0, paramString);
    if (localImagePack != null)
      return openPipeHelper(localImagePack, this.mImagePackWriter);
    try
    {
      File localFile = PicasaStoreFacade.getCacheFile(paramLong, ".screen");
      if (localFile == null)
        break label65;
      ParcelFileDescriptor localParcelFileDescriptor2 = ParcelFileDescriptor.open(localFile, 268435456);
      label65: return localParcelFileDescriptor2;
    }
    catch (Throwable localThrowable1)
    {
      Log.w("PicasaStore", "openScreenNail from cache", localThrowable1);
      if (paramString == null)
        throw new FileNotFoundException();
      try
      {
        BlobCacheRegister localBlobCacheRegister = new BlobCacheRegister(paramLong, 0, 0, paramString);
        ParcelFileDescriptor localParcelFileDescriptor1 = openPipeHelper(null, new DownloadWriter(paramLong, PicasaStoreFacade.convertImageUrl(paramString, Config.sScreenNailSize, false), localBlobCacheRegister));
        return localParcelFileDescriptor1;
      }
      catch (Throwable localThrowable2)
      {
        Log.w("PicasaStore", "download fail", localThrowable2);
        throw new FileNotFoundException();
      }
    }
  }

  public ParcelFileDescriptor openThumbNail(long paramLong, String paramString)
    throws FileNotFoundException
  {
    ImagePack localImagePack1 = lookupBlobCache(paramLong, 1, paramString);
    if ((localImagePack1 != null) && ((0x1 & localImagePack1.flags) == 0))
      return openPipeHelper(localImagePack1, this.mImagePackWriter);
    try
    {
      File localFile = PicasaStoreFacade.getCacheFile(paramLong, ".screen");
      if (localFile != null)
      {
        String str = localFile.getAbsolutePath();
        BitmapFactory.Options localOptions2 = new BitmapFactory.Options();
        localOptions2.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, localOptions2);
        localOptions2.inSampleSize = BitmapUtils.computeSampleSizeLarger(Config.sThumbNailSize / Math.min(localOptions2.outWidth, localOptions2.outHeight));
        localOptions2.inJustDecodeBounds = false;
        Bitmap localBitmap2 = BitmapFactory.decodeFile(str, localOptions2);
        if (localBitmap2 != null)
        {
          byte[] arrayOfByte2 = BitmapUtils.compressToBytes(BitmapUtils.resizeAndCropCenter(localBitmap2, Config.sThumbNailSize, true), 95);
          putBlobCache(paramLong, 1, paramString, 0, arrayOfByte2);
          return openPipeHelper(arrayOfByte2, this.mBytesWriter);
        }
        Log.e("PicasaStore", "invalid prefetch file: " + str + ", length: " + localFile.length());
        localFile.delete();
      }
      label210: ImagePack localImagePack2 = lookupBlobCache(paramLong, 0, paramString);
      if (localImagePack2 == null)
        break label383;
      BitmapFactory.Options localOptions1 = new BitmapFactory.Options();
      localOptions1.inJustDecodeBounds = true;
      BitmapFactory.decodeByteArray(localImagePack2.data, localImagePack2.offset, localImagePack2.data.length - localImagePack2.offset);
      localOptions1.inSampleSize = BitmapUtils.computeSampleSizeLarger(Config.sThumbNailSize / Math.min(localOptions1.outWidth, localOptions1.outHeight));
      localOptions1.inJustDecodeBounds = false;
      Bitmap localBitmap1 = BitmapFactory.decodeByteArray(localImagePack2.data, localImagePack2.offset, localImagePack2.data.length - localImagePack2.offset);
      if (localBitmap1 == null)
        break label383;
      byte[] arrayOfByte1 = BitmapUtils.compressToBytes(BitmapUtils.resizeAndCropCenter(localBitmap1, Config.sThumbNailSize, true), 95);
      putBlobCache(paramLong, 1, paramString, 0, arrayOfByte1);
      label383: return openPipeHelper(arrayOfByte1, this.mBytesWriter);
    }
    catch (Throwable localThrowable1)
    {
      Log.w("PicasaStore", "openThumbNail from screennail", localThrowable1);
      break label210:
      if (localImagePack1 != null)
        return openPipeHelper(localImagePack1, this.mImagePackWriter);
      if (paramString == null)
        throw new FileNotFoundException();
      try
      {
        BlobCacheRegister localBlobCacheRegister = new BlobCacheRegister(paramLong, 1, 1, paramString);
        ParcelFileDescriptor localParcelFileDescriptor = openPipeHelper(null, new DownloadWriter(paramLong, PicasaStoreFacade.convertImageUrl(paramString, Config.sThumbNailSize, true), localBlobCacheRegister));
        return localParcelFileDescriptor;
      }
      catch (Throwable localThrowable2)
      {
        Log.w("PicasaStore", "download fail", localThrowable2);
        throw new FileNotFoundException();
      }
    }
  }

  private class BlobCacheRegister
    implements PicasaStore.DownloadListener
  {
    private final byte[] mAuxKey;
    private ByteArrayOutputStream mBaos = new ByteArrayOutputStream();
    private final int mFlags;
    private final byte[] mKey;

    public BlobCacheRegister(long arg2, int paramInt2, int paramString, String arg6)
    {
      this.mKey = PicasaStore.this.makeKey(???, paramInt2);
      Object localObject;
      if (localObject == null);
      for (byte[] arrayOfByte = null; ; arrayOfByte = localObject.getBytes())
      {
        this.mAuxKey = arrayOfByte;
        this.mFlags = paramString;
        return;
      }
    }

    public void onDataAvailable(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      if (this.mBaos.size() >= 524288)
        return;
      this.mBaos.write(paramArrayOfByte, paramInt1, paramInt2);
    }

    public void onDownloadAbort()
    {
    }

    public void onDownloadComplete()
    {
      if (this.mBaos.size() >= 524288)
        return;
      PicasaStore.this.putBlobCache(this.mKey, this.mAuxKey, this.mFlags, this.mBaos.toByteArray());
    }
  }

  private static abstract interface DownloadListener
  {
    public abstract void onDataAvailable(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

    public abstract void onDownloadAbort();

    public abstract void onDownloadComplete();
  }

  private class DownloadWriter
    implements PicasaStore.PipeDataWriter<Object>
  {
    private PicasaStore.DownloadListener mDownloadListener;
    private String mDownloadUrl;
    private long mId;

    public DownloadWriter(long arg2, String paramDownloadListener, PicasaStore.DownloadListener arg5)
    {
      this.mId = ???;
      this.mDownloadUrl = paramDownloadListener;
      Object localObject;
      this.mDownloadListener = localObject;
    }

    public void writeDataToPipe(ParcelFileDescriptor paramParcelFileDescriptor, Object paramObject)
    {
      int i = MetricsUtils.begin("PicasaStore.download " + Utils.maskDebugInfo(Long.valueOf(this.mId)));
      InputStream localInputStream = null;
      ParcelFileDescriptor.AutoCloseOutputStream localAutoCloseOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(paramParcelFileDescriptor);
      PicasaStore.DownloadListener localDownloadListener = this.mDownloadListener;
      try
      {
        byte[] arrayOfByte;
        long l;
        try
        {
          localInputStream = HttpUtils.openInputStream(this.mDownloadUrl);
          int k;
          for (int j = localInputStream.read(arrayOfByte); j > 0; j = k)
          {
            localAutoCloseOutputStream.write(arrayOfByte, 0, j);
            if (localDownloadListener != null)
              localDownloadListener.onDataAvailable(arrayOfByte, 0, j);
            k = localInputStream.read(arrayOfByte);
          }
          MetricsUtils.incrementNetworkOpDurationAndCount(SystemClock.elapsedRealtime() - l);
          if (localDownloadListener != null)
            localDownloadListener.onDownloadComplete();
          Utils.closeSilently(localAutoCloseOutputStream);
          Utils.closeSilently(localInputStream);
          return;
        }
        finally
        {
          MetricsUtils.incrementNetworkOpDurationAndCount(SystemClock.elapsedRealtime() - l);
        }
      }
      catch (IOException localIOException)
      {
        HttpUtils.abortConnectionSilently(localInputStream);
        Log.d("PicasaStore", "pipe closed early by caller? " + localIOException);
        if (localDownloadListener != null)
          localDownloadListener.onDownloadAbort();
        return;
      }
      catch (Throwable localThrowable)
      {
        HttpUtils.abortConnectionSilently(localInputStream);
        Log.w("PicasaStore", "fail to write to pipe: " + Utils.maskDebugInfo(this.mDownloadUrl), localThrowable);
        if (localDownloadListener != null)
          localDownloadListener.onDownloadAbort();
        return;
      }
      finally
      {
        Utils.closeSilently(localAutoCloseOutputStream);
        Utils.closeSilently(localInputStream);
        MetricsUtils.endWithReport(i, "picasa.download.photo_video");
      }
    }
  }

  private static class ImagePack
  {
    public final byte[] data;
    public final int flags;
    public final int offset;

    ImagePack(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    {
      this.offset = paramInt1;
      this.flags = paramInt2;
      this.data = paramArrayOfByte;
    }
  }

  private class InputStreamWriter
    implements PicasaStore.PipeDataWriter<Object>
  {
    private long mId;
    private InputStream mInputStream;

    public InputStreamWriter(long arg2, InputStream arg4)
    {
      this.mId = ???;
      Object localObject;
      this.mInputStream = localObject;
    }

    public void writeDataToPipe(ParcelFileDescriptor paramParcelFileDescriptor, Object paramObject)
    {
      int i = MetricsUtils.begin("PicasaStore.download " + Utils.maskDebugInfo(Long.valueOf(this.mId)));
      InputStream localInputStream = this.mInputStream;
      ParcelFileDescriptor.AutoCloseOutputStream localAutoCloseOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(paramParcelFileDescriptor);
      try
      {
        byte[] arrayOfByte = new byte[2048];
        int k;
        for (int j = localInputStream.read(arrayOfByte); j > 0; j = k)
        {
          localAutoCloseOutputStream.write(arrayOfByte, 0, j);
          k = localInputStream.read(arrayOfByte);
        }
        return;
      }
      catch (IOException localIOException)
      {
        Log.d("PicasaStore", "pipe closed early by caller? " + localIOException);
        return;
      }
      catch (Throwable localThrowable)
      {
        Log.w("PicasaStore", "fail to write to pipe", localThrowable);
        return;
      }
      finally
      {
        Utils.closeSilently(localAutoCloseOutputStream);
        Utils.closeSilently(localInputStream);
        MetricsUtils.end(i);
      }
    }
  }

  private static abstract interface PipeDataWriter<T>
  {
    public abstract void writeDataToPipe(ParcelFileDescriptor paramParcelFileDescriptor, T paramT);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasastore.PicasaStore
 * JD-Core Version:    0.5.4
 */