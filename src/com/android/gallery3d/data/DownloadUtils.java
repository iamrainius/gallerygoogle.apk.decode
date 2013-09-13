package com.android.gallery3d.data;

import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.ThreadPool.CancelListener;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.URL;

public class DownloadUtils
{
  public static boolean download(ThreadPool.JobContext paramJobContext, URL paramURL, OutputStream paramOutputStream)
  {
    InputStream localInputStream = null;
    try
    {
      localInputStream = paramURL.openStream();
      dump(paramJobContext, localInputStream, paramOutputStream);
      return true;
    }
    catch (Throwable localThrowable)
    {
      Log.w("DownloadService", "fail to download", localThrowable);
      return false;
    }
    finally
    {
      Utils.closeSilently(localInputStream);
    }
  }

  public static void dump(ThreadPool.JobContext paramJobContext, InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte[4096];
    int i = paramInputStream.read(arrayOfByte, 0, arrayOfByte.length);
    paramJobContext.setCancelListener(new ThreadPool.CancelListener(Thread.currentThread())
    {
      public void onCancel()
      {
        this.val$thread.interrupt();
      }
    });
    while (i > 0)
    {
      if (paramJobContext.isCancelled())
        throw new InterruptedIOException();
      paramOutputStream.write(arrayOfByte, 0, i);
      i = paramInputStream.read(arrayOfByte, 0, arrayOfByte.length);
    }
    paramJobContext.setCancelListener(null);
    Thread.interrupted();
  }

  // ERROR //
  public static boolean requestDownload(ThreadPool.JobContext paramJobContext, URL paramURL, java.io.File paramFile)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: new 83	java/io/FileOutputStream
    //   5: dup
    //   6: aload_2
    //   7: invokespecial 86	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   10: astore 4
    //   12: aload_0
    //   13: aload_1
    //   14: aload 4
    //   16: invokestatic 88	com/android/gallery3d/data/DownloadUtils:download	(Lcom/android/gallery3d/util/ThreadPool$JobContext;Ljava/net/URL;Ljava/io/OutputStream;)Z
    //   19: istore 7
    //   21: aload 4
    //   23: invokestatic 28	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   26: iload 7
    //   28: ireturn
    //   29: astore 8
    //   31: aload_3
    //   32: invokestatic 28	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   35: iconst_0
    //   36: ireturn
    //   37: astore 6
    //   39: aload_3
    //   40: invokestatic 28	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   43: aload 6
    //   45: athrow
    //   46: astore 6
    //   48: aload 4
    //   50: astore_3
    //   51: goto -12 -> 39
    //   54: astore 5
    //   56: aload 4
    //   58: astore_3
    //   59: goto -28 -> 31
    //
    // Exception table:
    //   from	to	target	type
    //   2	12	29	java/lang/Throwable
    //   2	12	37	finally
    //   12	21	46	finally
    //   12	21	54	java/lang/Throwable
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.DownloadUtils
 * JD-Core Version:    0.5.4
 */