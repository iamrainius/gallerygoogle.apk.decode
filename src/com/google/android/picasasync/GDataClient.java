package com.google.android.picasasync;

import android.util.Log;
import com.google.android.picasastore.HttpUtils;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

final class GDataClient
{
  private boolean mAborted;
  private String mAuthToken;
  private HttpClient mHttpClient = HttpUtils.createHttpClient("GData/1.0; gzip");
  private HttpUriRequest mRequest;

  // ERROR //
  private void callMethod(HttpUriRequest paramHttpUriRequest, Operation paramOperation)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 32	com/google/android/picasasync/GDataClient:mAborted	Z
    //   6: ifeq +18 -> 24
    //   9: new 30	java/io/IOException
    //   12: dup
    //   13: ldc 34
    //   15: invokespecial 37	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   18: athrow
    //   19: astore_3
    //   20: aload_0
    //   21: monitorexit
    //   22: aload_3
    //   23: athrow
    //   24: aload_0
    //   25: aload_1
    //   26: putfield 39	com/google/android/picasasync/GDataClient:mRequest	Lorg/apache/http/client/methods/HttpUriRequest;
    //   29: aload_0
    //   30: monitorexit
    //   31: aload_1
    //   32: ldc 41
    //   34: ldc 43
    //   36: invokeinterface 49 3 0
    //   41: aload_1
    //   42: ldc 51
    //   44: ldc 53
    //   46: invokeinterface 49 3 0
    //   51: aload_0
    //   52: getfield 55	com/google/android/picasasync/GDataClient:mAuthToken	Ljava/lang/String;
    //   55: astore 4
    //   57: aload 4
    //   59: invokestatic 61	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   62: ifne +31 -> 93
    //   65: aload_1
    //   66: ldc 63
    //   68: new 65	java/lang/StringBuilder
    //   71: dup
    //   72: invokespecial 66	java/lang/StringBuilder:<init>	()V
    //   75: ldc 68
    //   77: invokevirtual 72	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   80: aload 4
    //   82: invokevirtual 72	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   85: invokevirtual 76	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   88: invokeinterface 49 3 0
    //   93: aload_2
    //   94: getfield 81	com/google/android/picasasync/GDataClient$Operation:inOutEtag	Ljava/lang/String;
    //   97: astore 5
    //   99: aload 5
    //   101: ifnull +13 -> 114
    //   104: aload_1
    //   105: ldc 83
    //   107: aload 5
    //   109: invokeinterface 49 3 0
    //   114: invokestatic 89	android/os/SystemClock:elapsedRealtime	()J
    //   117: lstore 24
    //   119: aload_0
    //   120: getfield 26	com/google/android/picasasync/GDataClient:mHttpClient	Lorg/apache/http/client/HttpClient;
    //   123: aload_1
    //   124: invokeinterface 95 2 0
    //   129: astore 14
    //   131: invokestatic 89	android/os/SystemClock:elapsedRealtime	()J
    //   134: lload 24
    //   136: lsub
    //   137: invokestatic 101	com/google/android/picasastore/MetricsUtils:incrementNetworkOpDuration	(J)V
    //   140: lconst_1
    //   141: invokestatic 104	com/google/android/picasastore/MetricsUtils:incrementNetworkOpCount	(J)V
    //   144: aload_2
    //   145: aconst_null
    //   146: putfield 108	com/google/android/picasasync/GDataClient$Operation:outBody	Ljava/io/InputStream;
    //   149: aload 14
    //   151: invokeinterface 114 1 0
    //   156: invokeinterface 120 1 0
    //   161: istore 17
    //   163: aload 14
    //   165: invokeinterface 124 1 0
    //   170: astore 18
    //   172: aconst_null
    //   173: astore 19
    //   175: aload 18
    //   177: ifnull +57 -> 234
    //   180: aload 18
    //   182: invokeinterface 130 1 0
    //   187: astore 19
    //   189: aload 19
    //   191: ifnull +43 -> 234
    //   194: aload 18
    //   196: invokeinterface 134 1 0
    //   201: astore 20
    //   203: aload 20
    //   205: ifnull +29 -> 234
    //   208: aload 20
    //   210: invokeinterface 139 1 0
    //   215: ldc 53
    //   217: invokevirtual 144	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   220: ifeq +14 -> 234
    //   223: new 146	java/util/zip/GZIPInputStream
    //   226: dup
    //   227: aload 19
    //   229: invokespecial 149	java/util/zip/GZIPInputStream:<init>	(Ljava/io/InputStream;)V
    //   232: astore 19
    //   234: aload 14
    //   236: ldc 151
    //   238: invokeinterface 155 2 0
    //   243: astore 21
    //   245: aload_2
    //   246: iload 17
    //   248: putfield 159	com/google/android/picasasync/GDataClient$Operation:outStatus	I
    //   251: aload 21
    //   253: ifnull +164 -> 417
    //   256: aload 21
    //   258: invokeinterface 139 1 0
    //   263: astore 22
    //   265: aload_2
    //   266: aload 22
    //   268: putfield 81	com/google/android/picasasync/GDataClient$Operation:inOutEtag	Ljava/lang/String;
    //   271: aload_2
    //   272: aload 19
    //   274: putfield 108	com/google/android/picasasync/GDataClient$Operation:outBody	Ljava/io/InputStream;
    //   277: aload_2
    //   278: getfield 108	com/google/android/picasasync/GDataClient$Operation:outBody	Ljava/io/InputStream;
    //   281: ifnonnull +24 -> 305
    //   284: aload 14
    //   286: invokeinterface 124 1 0
    //   291: astore 23
    //   293: aload 23
    //   295: ifnull +10 -> 305
    //   298: aload 23
    //   300: invokeinterface 162 1 0
    //   305: return
    //   306: astore 7
    //   308: aload_0
    //   309: monitorenter
    //   310: aload_0
    //   311: getfield 32	com/google/android/picasasync/GDataClient:mAborted	Z
    //   314: ifeq +22 -> 336
    //   317: aload 7
    //   319: athrow
    //   320: astore 8
    //   322: aload_0
    //   323: monitorexit
    //   324: aload 8
    //   326: athrow
    //   327: astore 6
    //   329: lconst_1
    //   330: invokestatic 104	com/google/android/picasastore/MetricsUtils:incrementNetworkOpCount	(J)V
    //   333: aload 6
    //   335: athrow
    //   336: aload_0
    //   337: monitorexit
    //   338: ldc 164
    //   340: new 65	java/lang/StringBuilder
    //   343: dup
    //   344: invokespecial 66	java/lang/StringBuilder:<init>	()V
    //   347: ldc 166
    //   349: invokevirtual 72	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   352: aload_1
    //   353: invokeinterface 170 1 0
    //   358: invokestatic 176	com/android/gallery3d/common/Utils:maskDebugInfo	(Ljava/lang/Object;)Ljava/lang/String;
    //   361: invokevirtual 72	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   364: invokevirtual 76	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   367: invokestatic 182	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   370: pop
    //   371: invokestatic 89	android/os/SystemClock:elapsedRealtime	()J
    //   374: lstore 12
    //   376: aload_0
    //   377: getfield 26	com/google/android/picasasync/GDataClient:mHttpClient	Lorg/apache/http/client/HttpClient;
    //   380: aload_1
    //   381: invokeinterface 95 2 0
    //   386: astore 14
    //   388: invokestatic 89	android/os/SystemClock:elapsedRealtime	()J
    //   391: lload 12
    //   393: lsub
    //   394: invokestatic 101	com/google/android/picasastore/MetricsUtils:incrementNetworkOpDuration	(J)V
    //   397: lconst_1
    //   398: invokestatic 104	com/google/android/picasastore/MetricsUtils:incrementNetworkOpCount	(J)V
    //   401: goto -257 -> 144
    //   404: astore 10
    //   406: ldc 164
    //   408: ldc 184
    //   410: invokestatic 182	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   413: pop
    //   414: aload 10
    //   416: athrow
    //   417: aconst_null
    //   418: astore 22
    //   420: goto -155 -> 265
    //   423: astore 15
    //   425: aload_2
    //   426: getfield 108	com/google/android/picasasync/GDataClient$Operation:outBody	Ljava/io/InputStream;
    //   429: ifnonnull +24 -> 453
    //   432: aload 14
    //   434: invokeinterface 124 1 0
    //   439: astore 16
    //   441: aload 16
    //   443: ifnull +10 -> 453
    //   446: aload 16
    //   448: invokeinterface 162 1 0
    //   453: aload 15
    //   455: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   2	19	19	finally
    //   20	22	19	finally
    //   24	31	19	finally
    //   114	140	306	java/io/IOException
    //   310	320	320	finally
    //   322	324	320	finally
    //   336	338	320	finally
    //   114	140	327	finally
    //   308	310	327	finally
    //   324	327	327	finally
    //   338	371	327	finally
    //   371	397	327	finally
    //   406	417	327	finally
    //   371	397	404	java/io/IOException
    //   149	172	423	finally
    //   180	189	423	finally
    //   194	203	423	finally
    //   208	234	423	finally
    //   234	251	423	finally
    //   256	265	423	finally
    //   265	277	423	finally
  }

  private static String replaceHttpWithHttps(String paramString)
  {
    if (paramString.startsWith("http:"))
      paramString = "https:" + paramString.substring("http:".length());
    return paramString;
  }

  public void abortCurrentOperation()
  {
    monitorenter;
    try
    {
      this.mAborted = true;
      HttpUriRequest localHttpUriRequest;
      label25: if (localHttpUriRequest == null);
    }
    finally
    {
      try
      {
        this.mRequest.abort();
        monitorexit;
        return;
      }
      catch (Throwable localThrowable)
      {
        Log.w("GDataClient", "trying to abort HTTP connection", localThrowable);
        break label25:
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }
  }

  public void get(String paramString, Operation paramOperation)
    throws IOException
  {
    callMethod(new HttpGet(replaceHttpWithHttps(paramString)), paramOperation);
  }

  public boolean isOperationAborted()
  {
    monitorenter;
    try
    {
      boolean bool = this.mAborted;
      monitorexit;
      return bool;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public void setAuthToken(String paramString)
  {
    this.mAuthToken = paramString;
  }

  public static final class Operation
  {
    public String inOutEtag;
    public InputStream outBody;
    public int outStatus;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.GDataClient
 * JD-Core Version:    0.5.4
 */