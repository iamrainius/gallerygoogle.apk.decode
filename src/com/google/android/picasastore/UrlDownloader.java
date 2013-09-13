package com.google.android.picasastore;

import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import com.android.gallery3d.common.Utils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class UrlDownloader
{
  private final Controller mController;
  private final Executor mExecutor = new ThreadPoolExecutor(0, 3, 60L, TimeUnit.SECONDS, this.mQueue, new PriorityThreadFactory("download-manager", 10));
  private final LinkedBlockingQueue<Runnable> mQueue = new LinkedBlockingQueue();
  private final HashMap<String, DownloadTask> mTaskMap = new HashMap();

  public UrlDownloader(Controller paramController)
  {
    this.mController = ((Controller)Utils.checkNotNull(paramController));
  }

  private static void deleteSilently(File paramFile)
  {
    if ((paramFile == null) || (paramFile.delete()))
      return;
    Log.w("PicasaDownloader", "cannot delete temp file: " + paramFile.getAbsolutePath());
  }

  public InputStream openInputStream(String paramString)
  {
    monitorenter;
    try
    {
      DownloadTask localDownloadTask = (DownloadTask)this.mTaskMap.get(paramString);
      if (localDownloadTask == null)
      {
        localDownloadTask = new DownloadTask(paramString);
        this.mTaskMap.put(paramString, localDownloadTask);
        this.mExecutor.execute(localDownloadTask);
      }
      SharedInputStream localSharedInputStream = new SharedInputStream(localDownloadTask);
      return localSharedInputStream;
    }
    finally
    {
      monitorexit;
    }
  }

  public static abstract interface Controller
  {
    public abstract File createTempFile();

    public abstract void onDownloadComplete(String paramString, File paramFile);
  }

  private class DownloadTask
    implements Runnable
  {
    public long cancelTimeout;
    public File downloadFile;
    public long downloadSize = 0L;
    public final String downloadUrl;
    public RandomAccessFile randomAccessFile;
    public int requestCount;
    public int state = 1;

    public DownloadTask(String arg2)
    {
      Object localObject;
      this.downloadUrl = localObject;
    }

    private void setFinalState(int paramInt)
    {
      this.state = paramInt;
      UrlDownloader.this.mTaskMap.remove(this.downloadUrl);
      UrlDownloader.this.notifyAll();
    }

    public void releaseReadRequest()
    {
      synchronized (UrlDownloader.this)
      {
        this.requestCount = (-1 + this.requestCount);
        if (this.requestCount == 0)
        {
          this.cancelTimeout = (3000L + SystemClock.elapsedRealtime());
          if ((0x1C & this.state) != 0)
          {
            Utils.closeSilently(this.randomAccessFile);
            if (this.state != 4)
              UrlDownloader.access$000(this.downloadFile);
          }
        }
        return;
      }
    }

    public void requestRead()
    {
      synchronized (UrlDownloader.this)
      {
        this.requestCount = (1 + this.requestCount);
        return;
      }
    }

    // ERROR //
    public void run()
    {
      // Byte code:
      //   0: iconst_1
      //   1: istore_1
      //   2: aload_0
      //   3: getfield 24	com/google/android/picasastore/UrlDownloader$DownloadTask:this$0	Lcom/google/android/picasastore/UrlDownloader;
      //   6: astore_2
      //   7: aload_2
      //   8: monitorenter
      //   9: aload_0
      //   10: getfield 31	com/google/android/picasastore/UrlDownloader$DownloadTask:state	I
      //   13: iload_1
      //   14: if_icmpne +709 -> 723
      //   17: iload_1
      //   18: invokestatic 85	com/android/gallery3d/common/Utils:assertTrue	(Z)V
      //   21: aload_0
      //   22: getfield 53	com/google/android/picasastore/UrlDownloader$DownloadTask:requestCount	I
      //   25: ifne +12 -> 37
      //   28: aload_0
      //   29: bipush 16
      //   31: invokespecial 87	com/google/android/picasastore/UrlDownloader$DownloadTask:setFinalState	(I)V
      //   34: aload_2
      //   35: monitorexit
      //   36: return
      //   37: aload_0
      //   38: iconst_2
      //   39: putfield 31	com/google/android/picasastore/UrlDownloader$DownloadTask:state	I
      //   42: aload_2
      //   43: monitorexit
      //   44: new 89	java/lang/StringBuilder
      //   47: dup
      //   48: invokespecial 90	java/lang/StringBuilder:<init>	()V
      //   51: ldc 92
      //   53: invokevirtual 96	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   56: aload_0
      //   57: getfield 33	com/google/android/picasastore/UrlDownloader$DownloadTask:downloadUrl	Ljava/lang/String;
      //   60: invokestatic 100	com/android/gallery3d/common/Utils:maskDebugInfo	(Ljava/lang/Object;)Ljava/lang/String;
      //   63: invokevirtual 96	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   66: invokevirtual 104	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   69: invokestatic 110	com/google/android/picasastore/MetricsUtils:begin	(Ljava/lang/String;)I
      //   72: istore 4
      //   74: aconst_null
      //   75: astore 5
      //   77: aload_0
      //   78: aload_0
      //   79: getfield 24	com/google/android/picasastore/UrlDownloader$DownloadTask:this$0	Lcom/google/android/picasastore/UrlDownloader;
      //   82: invokestatic 114	com/google/android/picasastore/UrlDownloader:access$200	(Lcom/google/android/picasastore/UrlDownloader;)Lcom/google/android/picasastore/UrlDownloader$Controller;
      //   85: invokeinterface 120 1 0
      //   90: putfield 73	com/google/android/picasastore/UrlDownloader$DownloadTask:downloadFile	Ljava/io/File;
      //   93: aload_0
      //   94: new 122	java/io/RandomAccessFile
      //   97: dup
      //   98: aload_0
      //   99: getfield 73	com/google/android/picasastore/UrlDownloader$DownloadTask:downloadFile	Ljava/io/File;
      //   102: ldc 124
      //   104: invokespecial 127	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   107: putfield 65	com/google/android/picasastore/UrlDownloader$DownloadTask:randomAccessFile	Ljava/io/RandomAccessFile;
      //   110: aload_0
      //   111: getfield 33	com/google/android/picasastore/UrlDownloader$DownloadTask:downloadUrl	Ljava/lang/String;
      //   114: invokestatic 133	com/google/android/picasastore/HttpUtils:openInputStream	(Ljava/lang/String;)Ljava/io/InputStream;
      //   117: astore 5
      //   119: sipush 2048
      //   122: newarray byte
      //   124: astore 15
      //   126: invokestatic 61	android/os/SystemClock:elapsedRealtime	()J
      //   129: lstore 16
      //   131: aload 5
      //   133: aload 15
      //   135: invokevirtual 139	java/io/InputStream:read	([B)I
      //   138: istore 19
      //   140: iload 19
      //   142: ifle +332 -> 474
      //   145: aload_0
      //   146: monitorenter
      //   147: aload_0
      //   148: getfield 65	com/google/android/picasastore/UrlDownloader$DownloadTask:randomAccessFile	Ljava/io/RandomAccessFile;
      //   151: aload_0
      //   152: getfield 29	com/google/android/picasastore/UrlDownloader$DownloadTask:downloadSize	J
      //   155: invokevirtual 143	java/io/RandomAccessFile:seek	(J)V
      //   158: aload_0
      //   159: getfield 65	com/google/android/picasastore/UrlDownloader$DownloadTask:randomAccessFile	Ljava/io/RandomAccessFile;
      //   162: aload 15
      //   164: iconst_0
      //   165: iload 19
      //   167: invokevirtual 147	java/io/RandomAccessFile:write	([BII)V
      //   170: aload_0
      //   171: monitorexit
      //   172: aload_0
      //   173: getfield 24	com/google/android/picasastore/UrlDownloader$DownloadTask:this$0	Lcom/google/android/picasastore/UrlDownloader;
      //   176: astore 21
      //   178: aload 21
      //   180: monitorenter
      //   181: aload_0
      //   182: getfield 53	com/google/android/picasastore/UrlDownloader$DownloadTask:requestCount	I
      //   185: ifne +243 -> 428
      //   188: aload_0
      //   189: getfield 24	com/google/android/picasastore/UrlDownloader$DownloadTask:this$0	Lcom/google/android/picasastore/UrlDownloader;
      //   192: invokestatic 151	com/google/android/picasastore/UrlDownloader:access$300	(Lcom/google/android/picasastore/UrlDownloader;)Ljava/util/concurrent/LinkedBlockingQueue;
      //   195: invokevirtual 157	java/util/concurrent/LinkedBlockingQueue:isEmpty	()Z
      //   198: ifeq +14 -> 212
      //   201: invokestatic 61	android/os/SystemClock:elapsedRealtime	()J
      //   204: aload_0
      //   205: getfield 63	com/google/android/picasastore/UrlDownloader$DownloadTask:cancelTimeout	J
      //   208: lcmp
      //   209: ifle +219 -> 428
      //   212: aload_0
      //   213: bipush 16
      //   215: invokespecial 87	com/google/android/picasastore/UrlDownloader$DownloadTask:setFinalState	(I)V
      //   218: aload 21
      //   220: monitorexit
      //   221: invokestatic 61	android/os/SystemClock:elapsedRealtime	()J
      //   224: lload 16
      //   226: lsub
      //   227: invokestatic 160	com/google/android/picasastore/MetricsUtils:incrementNetworkOpDurationAndCount	(J)V
      //   230: aload_0
      //   231: getfield 31	com/google/android/picasastore/UrlDownloader$DownloadTask:state	I
      //   234: iconst_4
      //   235: if_icmpeq +8 -> 243
      //   238: aload 5
      //   240: invokestatic 164	com/google/android/picasastore/HttpUtils:abortConnectionSilently	(Ljava/io/InputStream;)V
      //   243: aload 5
      //   245: invokestatic 71	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
      //   248: aload_0
      //   249: getfield 24	com/google/android/picasastore/UrlDownloader$DownloadTask:this$0	Lcom/google/android/picasastore/UrlDownloader;
      //   252: astore 24
      //   254: aload 24
      //   256: monitorenter
      //   257: aload_0
      //   258: getfield 53	com/google/android/picasastore/UrlDownloader$DownloadTask:requestCount	I
      //   261: ifne +25 -> 286
      //   264: aload_0
      //   265: getfield 65	com/google/android/picasastore/UrlDownloader$DownloadTask:randomAccessFile	Ljava/io/RandomAccessFile;
      //   268: invokestatic 71	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
      //   271: aload_0
      //   272: getfield 31	com/google/android/picasastore/UrlDownloader$DownloadTask:state	I
      //   275: iconst_4
      //   276: if_icmpeq +10 -> 286
      //   279: aload_0
      //   280: getfield 73	com/google/android/picasastore/UrlDownloader$DownloadTask:downloadFile	Ljava/io/File;
      //   283: invokestatic 77	com/google/android/picasastore/UrlDownloader:access$000	(Ljava/io/File;)V
      //   286: aload 24
      //   288: monitorexit
      //   289: iload 4
      //   291: ldc 166
      //   293: invokestatic 170	com/google/android/picasastore/MetricsUtils:endWithReport	(ILjava/lang/String;)V
      //   296: return
      //   297: astore_3
      //   298: aload_2
      //   299: monitorexit
      //   300: aload_3
      //   301: athrow
      //   302: astore 20
      //   304: aload_0
      //   305: monitorexit
      //   306: aload 20
      //   308: athrow
      //   309: astore 18
      //   311: invokestatic 61	android/os/SystemClock:elapsedRealtime	()J
      //   314: lload 16
      //   316: lsub
      //   317: invokestatic 160	com/google/android/picasastore/MetricsUtils:incrementNetworkOpDurationAndCount	(J)V
      //   320: aload 18
      //   322: athrow
      //   323: astore 9
      //   325: ldc 172
      //   327: ldc 174
      //   329: aload 9
      //   331: invokestatic 180	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   334: pop
      //   335: aload_0
      //   336: getfield 24	com/google/android/picasastore/UrlDownloader$DownloadTask:this$0	Lcom/google/android/picasastore/UrlDownloader;
      //   339: astore 11
      //   341: aload 11
      //   343: monitorenter
      //   344: aload_0
      //   345: bipush 8
      //   347: invokespecial 87	com/google/android/picasastore/UrlDownloader$DownloadTask:setFinalState	(I)V
      //   350: aload 11
      //   352: monitorexit
      //   353: aload_0
      //   354: getfield 31	com/google/android/picasastore/UrlDownloader$DownloadTask:state	I
      //   357: iconst_4
      //   358: if_icmpeq +8 -> 366
      //   361: aload 5
      //   363: invokestatic 164	com/google/android/picasastore/HttpUtils:abortConnectionSilently	(Ljava/io/InputStream;)V
      //   366: aload 5
      //   368: invokestatic 71	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
      //   371: aload_0
      //   372: getfield 24	com/google/android/picasastore/UrlDownloader$DownloadTask:this$0	Lcom/google/android/picasastore/UrlDownloader;
      //   375: astore 13
      //   377: aload 13
      //   379: monitorenter
      //   380: aload_0
      //   381: getfield 53	com/google/android/picasastore/UrlDownloader$DownloadTask:requestCount	I
      //   384: ifne +25 -> 409
      //   387: aload_0
      //   388: getfield 65	com/google/android/picasastore/UrlDownloader$DownloadTask:randomAccessFile	Ljava/io/RandomAccessFile;
      //   391: invokestatic 71	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
      //   394: aload_0
      //   395: getfield 31	com/google/android/picasastore/UrlDownloader$DownloadTask:state	I
      //   398: iconst_4
      //   399: if_icmpeq +10 -> 409
      //   402: aload_0
      //   403: getfield 73	com/google/android/picasastore/UrlDownloader$DownloadTask:downloadFile	Ljava/io/File;
      //   406: invokestatic 77	com/google/android/picasastore/UrlDownloader:access$000	(Ljava/io/File;)V
      //   409: aload 13
      //   411: monitorexit
      //   412: iload 4
      //   414: ldc 166
      //   416: invokestatic 170	com/google/android/picasastore/MetricsUtils:endWithReport	(ILjava/lang/String;)V
      //   419: return
      //   420: astore 25
      //   422: aload 24
      //   424: monitorexit
      //   425: aload 25
      //   427: athrow
      //   428: aload_0
      //   429: aload_0
      //   430: getfield 29	com/google/android/picasastore/UrlDownloader$DownloadTask:downloadSize	J
      //   433: iload 19
      //   435: i2l
      //   436: ladd
      //   437: putfield 29	com/google/android/picasastore/UrlDownloader$DownloadTask:downloadSize	J
      //   440: aload_0
      //   441: getfield 24	com/google/android/picasastore/UrlDownloader$DownloadTask:this$0	Lcom/google/android/picasastore/UrlDownloader;
      //   444: invokevirtual 50	java/lang/Object:notifyAll	()V
      //   447: aload 21
      //   449: monitorexit
      //   450: aload 5
      //   452: aload 15
      //   454: invokevirtual 139	java/io/InputStream:read	([B)I
      //   457: istore 23
      //   459: iload 23
      //   461: istore 19
      //   463: goto -323 -> 140
      //   466: astore 22
      //   468: aload 21
      //   470: monitorexit
      //   471: aload 22
      //   473: athrow
      //   474: invokestatic 61	android/os/SystemClock:elapsedRealtime	()J
      //   477: lload 16
      //   479: lsub
      //   480: invokestatic 160	com/google/android/picasastore/MetricsUtils:incrementNetworkOpDurationAndCount	(J)V
      //   483: aload_0
      //   484: getfield 65	com/google/android/picasastore/UrlDownloader$DownloadTask:randomAccessFile	Ljava/io/RandomAccessFile;
      //   487: invokevirtual 184	java/io/RandomAccessFile:getFD	()Ljava/io/FileDescriptor;
      //   490: invokevirtual 189	java/io/FileDescriptor:sync	()V
      //   493: aload_0
      //   494: getfield 24	com/google/android/picasastore/UrlDownloader$DownloadTask:this$0	Lcom/google/android/picasastore/UrlDownloader;
      //   497: invokestatic 114	com/google/android/picasastore/UrlDownloader:access$200	(Lcom/google/android/picasastore/UrlDownloader;)Lcom/google/android/picasastore/UrlDownloader$Controller;
      //   500: aload_0
      //   501: getfield 33	com/google/android/picasastore/UrlDownloader$DownloadTask:downloadUrl	Ljava/lang/String;
      //   504: aload_0
      //   505: getfield 73	com/google/android/picasastore/UrlDownloader$DownloadTask:downloadFile	Ljava/io/File;
      //   508: invokeinterface 193 3 0
      //   513: aload_0
      //   514: getfield 24	com/google/android/picasastore/UrlDownloader$DownloadTask:this$0	Lcom/google/android/picasastore/UrlDownloader;
      //   517: astore 28
      //   519: aload 28
      //   521: monitorenter
      //   522: aload_0
      //   523: iconst_4
      //   524: invokespecial 87	com/google/android/picasastore/UrlDownloader$DownloadTask:setFinalState	(I)V
      //   527: aload 28
      //   529: monitorexit
      //   530: aload_0
      //   531: getfield 31	com/google/android/picasastore/UrlDownloader$DownloadTask:state	I
      //   534: iconst_4
      //   535: if_icmpeq +8 -> 543
      //   538: aload 5
      //   540: invokestatic 164	com/google/android/picasastore/HttpUtils:abortConnectionSilently	(Ljava/io/InputStream;)V
      //   543: aload 5
      //   545: invokestatic 71	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
      //   548: aload_0
      //   549: getfield 24	com/google/android/picasastore/UrlDownloader$DownloadTask:this$0	Lcom/google/android/picasastore/UrlDownloader;
      //   552: astore 30
      //   554: aload 30
      //   556: monitorenter
      //   557: aload_0
      //   558: getfield 53	com/google/android/picasastore/UrlDownloader$DownloadTask:requestCount	I
      //   561: ifne +25 -> 586
      //   564: aload_0
      //   565: getfield 65	com/google/android/picasastore/UrlDownloader$DownloadTask:randomAccessFile	Ljava/io/RandomAccessFile;
      //   568: invokestatic 71	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
      //   571: aload_0
      //   572: getfield 31	com/google/android/picasastore/UrlDownloader$DownloadTask:state	I
      //   575: iconst_4
      //   576: if_icmpeq +10 -> 586
      //   579: aload_0
      //   580: getfield 73	com/google/android/picasastore/UrlDownloader$DownloadTask:downloadFile	Ljava/io/File;
      //   583: invokestatic 77	com/google/android/picasastore/UrlDownloader:access$000	(Ljava/io/File;)V
      //   586: aload 30
      //   588: monitorexit
      //   589: iload 4
      //   591: ldc 166
      //   593: invokestatic 170	com/google/android/picasastore/MetricsUtils:endWithReport	(ILjava/lang/String;)V
      //   596: return
      //   597: astore 26
      //   599: ldc 172
      //   601: ldc 195
      //   603: aload 26
      //   605: invokestatic 180	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   608: pop
      //   609: goto -96 -> 513
      //   612: astore 6
      //   614: aload_0
      //   615: getfield 31	com/google/android/picasastore/UrlDownloader$DownloadTask:state	I
      //   618: iconst_4
      //   619: if_icmpeq +8 -> 627
      //   622: aload 5
      //   624: invokestatic 164	com/google/android/picasastore/HttpUtils:abortConnectionSilently	(Ljava/io/InputStream;)V
      //   627: aload 5
      //   629: invokestatic 71	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
      //   632: aload_0
      //   633: getfield 24	com/google/android/picasastore/UrlDownloader$DownloadTask:this$0	Lcom/google/android/picasastore/UrlDownloader;
      //   636: astore 7
      //   638: aload 7
      //   640: monitorenter
      //   641: aload_0
      //   642: getfield 53	com/google/android/picasastore/UrlDownloader$DownloadTask:requestCount	I
      //   645: ifne +25 -> 670
      //   648: aload_0
      //   649: getfield 65	com/google/android/picasastore/UrlDownloader$DownloadTask:randomAccessFile	Ljava/io/RandomAccessFile;
      //   652: invokestatic 71	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
      //   655: aload_0
      //   656: getfield 31	com/google/android/picasastore/UrlDownloader$DownloadTask:state	I
      //   659: iconst_4
      //   660: if_icmpeq +10 -> 670
      //   663: aload_0
      //   664: getfield 73	com/google/android/picasastore/UrlDownloader$DownloadTask:downloadFile	Ljava/io/File;
      //   667: invokestatic 77	com/google/android/picasastore/UrlDownloader:access$000	(Ljava/io/File;)V
      //   670: aload 7
      //   672: monitorexit
      //   673: iload 4
      //   675: ldc 166
      //   677: invokestatic 170	com/google/android/picasastore/MetricsUtils:endWithReport	(ILjava/lang/String;)V
      //   680: aload 6
      //   682: athrow
      //   683: astore 29
      //   685: aload 28
      //   687: monitorexit
      //   688: aload 29
      //   690: athrow
      //   691: astore 31
      //   693: aload 30
      //   695: monitorexit
      //   696: aload 31
      //   698: athrow
      //   699: astore 12
      //   701: aload 11
      //   703: monitorexit
      //   704: aload 12
      //   706: athrow
      //   707: astore 14
      //   709: aload 13
      //   711: monitorexit
      //   712: aload 14
      //   714: athrow
      //   715: astore 8
      //   717: aload 7
      //   719: monitorexit
      //   720: aload 8
      //   722: athrow
      //   723: iconst_0
      //   724: istore_1
      //   725: goto -708 -> 17
      //
      // Exception table:
      //   from	to	target	type
      //   9	17	297	finally
      //   17	36	297	finally
      //   37	44	297	finally
      //   298	300	297	finally
      //   147	172	302	finally
      //   304	306	302	finally
      //   131	140	309	finally
      //   145	147	309	finally
      //   172	181	309	finally
      //   306	309	309	finally
      //   450	459	309	finally
      //   471	474	309	finally
      //   77	131	323	java/lang/Throwable
      //   221	230	323	java/lang/Throwable
      //   311	323	323	java/lang/Throwable
      //   474	483	323	java/lang/Throwable
      //   513	522	323	java/lang/Throwable
      //   599	609	323	java/lang/Throwable
      //   688	691	323	java/lang/Throwable
      //   257	286	420	finally
      //   286	289	420	finally
      //   422	425	420	finally
      //   181	212	466	finally
      //   212	221	466	finally
      //   428	450	466	finally
      //   468	471	466	finally
      //   483	513	597	java/lang/Throwable
      //   77	131	612	finally
      //   221	230	612	finally
      //   311	323	612	finally
      //   325	344	612	finally
      //   474	483	612	finally
      //   483	513	612	finally
      //   513	522	612	finally
      //   599	609	612	finally
      //   688	691	612	finally
      //   704	707	612	finally
      //   522	530	683	finally
      //   685	688	683	finally
      //   557	586	691	finally
      //   586	589	691	finally
      //   693	696	691	finally
      //   344	353	699	finally
      //   701	704	699	finally
      //   380	409	707	finally
      //   409	412	707	finally
      //   709	712	707	finally
      //   641	670	715	finally
      //   670	673	715	finally
      //   717	720	715	finally
    }
  }

  private static class PriorityThreadFactory
    implements ThreadFactory
  {
    private final String mName;
    private final AtomicInteger mNumber = new AtomicInteger();
    private final int mPriority;

    public PriorityThreadFactory(String paramString, int paramInt)
    {
      this.mName = paramString;
      this.mPriority = paramInt;
    }

    public Thread newThread(Runnable paramRunnable)
    {
      return new Thread(paramRunnable, this.mName + '-' + this.mNumber.getAndIncrement())
      {
        public void run()
        {
          Process.setThreadPriority(UrlDownloader.PriorityThreadFactory.this.mPriority);
          super.run();
        }
      };
    }
  }

  private class SharedInputStream extends InputStream
  {
    private long mOffset = 0L;
    private UrlDownloader.DownloadTask mTask;

    public SharedInputStream(UrlDownloader.DownloadTask arg2)
    {
      Object localObject;
      this.mTask = localObject;
      this.mTask.requestRead();
    }

    private boolean isDownloading(int paramInt)
    {
      return (paramInt & 0x3) != 0;
    }

    public void close()
    {
      monitorenter;
      try
      {
        if (this.mTask == null)
          return;
        UrlDownloader.DownloadTask localDownloadTask = this.mTask;
        this.mTask = null;
        monitorexit;
        return;
      }
      finally
      {
        monitorexit;
      }
    }

    protected void finalize()
      throws Throwable
    {
      try
      {
        super.finalize();
        return;
      }
      finally
      {
        if (this.mTask != null)
          Log.w("PicasaDownloader", "unclosed input stream");
        Utils.closeSilently(this);
      }
    }

    public int read()
      throws IOException
    {
      byte[] arrayOfByte = new byte[1];
      if (read(arrayOfByte, 0, 1) > 0)
        return 0xFF & arrayOfByte[1];
      return -1;
    }

    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (paramInt2 == 0)
        return 0;
      UrlDownloader.DownloadTask localDownloadTask = this.mTask;
      long l;
      synchronized (UrlDownloader.this)
      {
        l = localDownloadTask.downloadSize;
        while ((l <= this.mOffset) && (isDownloading(localDownloadTask.state)))
        {
          Utils.waitWithoutInterrupt(UrlDownloader.this);
          l = localDownloadTask.downloadSize;
        }
        if (localDownloadTask.state != 8)
          break label95;
        throw new IOException("download fail!");
      }
      label95: monitorexit;
      int i = (int)Math.min(paramInt2, l - this.mOffset);
      if (i == 0)
        return -1;
      monitorenter;
      try
      {
        localDownloadTask.randomAccessFile.seek(this.mOffset);
        int j = localDownloadTask.randomAccessFile.read(paramArrayOfByte, paramInt1, i);
        this.mOffset += j;
        return j;
      }
      finally
      {
        monitorexit;
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasastore.UrlDownloader
 * JD-Core Version:    0.5.4
 */