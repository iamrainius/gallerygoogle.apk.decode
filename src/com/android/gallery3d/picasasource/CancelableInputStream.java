package com.android.gallery3d.picasasource;

import android.content.ContentProviderClient;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import com.android.gallery3d.common.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CancelableInputStream extends InputStream
  implements Runnable
{
  private static Executor sExecutor = Executors.newFixedThreadPool(3);
  private byte[] mBuffer;
  private volatile boolean mCancelled = false;
  private InputStream mInput;
  private int mLength;
  private int mOffset;
  private ParcelFileDescriptor mPfd;
  private final ContentProviderClient mProvider;
  private int mReadCount;
  private Thread mReadThread = null;
  private boolean mReadyToRead = true;
  private final Uri mUri;

  public CancelableInputStream(ContentProviderClient paramContentProviderClient, Uri paramUri)
  {
    this.mUri = paramUri;
    this.mProvider = paramContentProviderClient;
  }

  public void cancel()
  {
    monitorenter;
    try
    {
      this.mCancelled = true;
      super.notifyAll();
      if (this.mReadThread != null)
        this.mReadThread.interrupt();
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

  public void close()
  {
    monitorenter;
    while (true)
      try
      {
        if (this.mReadyToRead)
        {
          Utils.closeSilently(this.mInput);
          Utils.closeSilently(this.mPfd);
          return;
        }
      }
      finally
      {
        monitorexit;
      }
  }

  public int read()
    throws IOException
  {
    byte[] arrayOfByte = new byte[1];
    if (read(arrayOfByte) > 0)
      return 0xFF & arrayOfByte[0];
    return -1;
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = -1;
    monitorenter;
    while (true)
    {
      try
      {
        boolean bool1 = this.mCancelled;
        if (bool1)
          return i;
        Utils.assertTrue(this.mReadyToRead);
        this.mBuffer = paramArrayOfByte;
        this.mOffset = paramInt1;
        this.mLength = paramInt2;
        this.mReadyToRead = false;
        sExecutor.execute(this);
        if (this.mReadyToRead)
          break label104;
        boolean bool2 = this.mCancelled;
        if (bool2)
          break label104;
      }
      finally
      {
        monitorexit;
      }
      label104: if (this.mCancelled)
        continue;
      i = this.mReadCount;
    }
  }

  // ERROR //
  public void run()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 47	com/android/gallery3d/picasasource/CancelableInputStream:mCancelled	Z
    //   4: ifeq +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: getfield 65	com/android/gallery3d/picasasource/CancelableInputStream:mInput	Ljava/io/InputStream;
    //   12: astore 10
    //   14: iconst_0
    //   15: istore_3
    //   16: aload 10
    //   18: ifnonnull +38 -> 56
    //   21: aload_0
    //   22: aload_0
    //   23: getfield 51	com/android/gallery3d/picasasource/CancelableInputStream:mProvider	Landroid/content/ContentProviderClient;
    //   26: aload_0
    //   27: getfield 49	com/android/gallery3d/picasasource/CancelableInputStream:mUri	Landroid/net/Uri;
    //   30: ldc 122
    //   32: invokevirtual 128	android/content/ContentProviderClient:openFile	(Landroid/net/Uri;Ljava/lang/String;)Landroid/os/ParcelFileDescriptor;
    //   35: putfield 73	com/android/gallery3d/picasasource/CancelableInputStream:mPfd	Landroid/os/ParcelFileDescriptor;
    //   38: aload_0
    //   39: new 130	java/io/FileInputStream
    //   42: dup
    //   43: aload_0
    //   44: getfield 73	com/android/gallery3d/picasasource/CancelableInputStream:mPfd	Landroid/os/ParcelFileDescriptor;
    //   47: invokevirtual 136	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   50: invokespecial 139	java/io/FileInputStream:<init>	(Ljava/io/FileDescriptor;)V
    //   53: putfield 65	com/android/gallery3d/picasasource/CancelableInputStream:mInput	Ljava/io/InputStream;
    //   56: iload_3
    //   57: ifne +57 -> 114
    //   60: aload_0
    //   61: getfield 47	com/android/gallery3d/picasasource/CancelableInputStream:mCancelled	Z
    //   64: ifne +50 -> 114
    //   67: aload_0
    //   68: monitorenter
    //   69: aload_0
    //   70: invokestatic 143	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   73: putfield 45	com/android/gallery3d/picasasource/CancelableInputStream:mReadThread	Ljava/lang/Thread;
    //   76: aload_0
    //   77: monitorexit
    //   78: aload_0
    //   79: aload_0
    //   80: getfield 65	com/android/gallery3d/picasasource/CancelableInputStream:mInput	Ljava/io/InputStream;
    //   83: aload_0
    //   84: getfield 92	com/android/gallery3d/picasasource/CancelableInputStream:mBuffer	[B
    //   87: aload_0
    //   88: getfield 94	com/android/gallery3d/picasasource/CancelableInputStream:mOffset	I
    //   91: aload_0
    //   92: getfield 96	com/android/gallery3d/picasasource/CancelableInputStream:mLength	I
    //   95: invokevirtual 145	java/io/InputStream:read	([BII)I
    //   98: putfield 117	com/android/gallery3d/picasasource/CancelableInputStream:mReadCount	I
    //   101: aload_0
    //   102: monitorenter
    //   103: aload_0
    //   104: aconst_null
    //   105: putfield 45	com/android/gallery3d/picasasource/CancelableInputStream:mReadThread	Ljava/lang/Thread;
    //   108: invokestatic 149	java/lang/Thread:interrupted	()Z
    //   111: pop
    //   112: aload_0
    //   113: monitorexit
    //   114: aload_0
    //   115: monitorenter
    //   116: iload_3
    //   117: ifeq +8 -> 125
    //   120: aload_0
    //   121: iconst_1
    //   122: putfield 47	com/android/gallery3d/picasasource/CancelableInputStream:mCancelled	Z
    //   125: aload_0
    //   126: getfield 47	com/android/gallery3d/picasasource/CancelableInputStream:mCancelled	Z
    //   129: ifeq +17 -> 146
    //   132: aload_0
    //   133: getfield 65	com/android/gallery3d/picasasource/CancelableInputStream:mInput	Ljava/io/InputStream;
    //   136: invokestatic 71	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   139: aload_0
    //   140: getfield 73	com/android/gallery3d/picasasource/CancelableInputStream:mPfd	Landroid/os/ParcelFileDescriptor;
    //   143: invokestatic 76	com/android/gallery3d/common/Utils:closeSilently	(Landroid/os/ParcelFileDescriptor;)V
    //   146: aload_0
    //   147: iconst_1
    //   148: putfield 43	com/android/gallery3d/picasasource/CancelableInputStream:mReadyToRead	Z
    //   151: aload_0
    //   152: invokevirtual 57	java/lang/Object:notifyAll	()V
    //   155: aload_0
    //   156: monitorexit
    //   157: return
    //   158: astore 4
    //   160: aload_0
    //   161: monitorexit
    //   162: aload 4
    //   164: athrow
    //   165: astore_1
    //   166: ldc 107
    //   168: ldc 151
    //   170: aload_1
    //   171: invokestatic 115	com/android/gallery3d/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   174: pop
    //   175: iconst_1
    //   176: istore_3
    //   177: goto -121 -> 56
    //   180: astore 7
    //   182: aload_0
    //   183: monitorexit
    //   184: aload 7
    //   186: athrow
    //   187: astore 5
    //   189: iconst_1
    //   190: istore_3
    //   191: ldc 107
    //   193: ldc 153
    //   195: aload 5
    //   197: invokestatic 115	com/android/gallery3d/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   200: pop
    //   201: goto -87 -> 114
    //   204: astore 8
    //   206: aload_0
    //   207: monitorexit
    //   208: aload 8
    //   210: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   120	125	158	finally
    //   125	146	158	finally
    //   146	157	158	finally
    //   160	162	158	finally
    //   8	14	165	java/lang/Throwable
    //   21	56	165	java/lang/Throwable
    //   69	78	180	finally
    //   182	184	180	finally
    //   67	69	187	java/lang/Throwable
    //   78	103	187	java/lang/Throwable
    //   184	187	187	java/lang/Throwable
    //   208	211	187	java/lang/Throwable
    //   103	114	204	finally
    //   206	208	204	finally
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.picasasource.CancelableInputStream
 * JD-Core Version:    0.5.4
 */