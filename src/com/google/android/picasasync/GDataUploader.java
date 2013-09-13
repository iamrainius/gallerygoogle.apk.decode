package com.google.android.picasasync;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;
import android.util.Xml.Encoding;
import com.android.gallery3d.common.Fingerprint;
import com.google.android.picasastore.HttpUtils;
import com.google.android.picasastore.MetricsUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class GDataUploader
  implements Uploader
{
  private static final Pattern RE_RANGE_HEADER = Pattern.compile("bytes=(\\d+)-(\\d+)");
  private static String sUserAgent;
  private String mAuthToken;
  private Authorizer mAuthorizer;
  private Context mContext;
  private HttpClient mHttpClient;
  private Uploader.UploadProgressListener mListener;
  private UploadTaskEntry mUploadTask;

  GDataUploader(Context paramContext)
  {
    this.mContext = paramContext;
    this.mHttpClient = HttpUtils.createHttpClient(getUserAgent(paramContext));
  }

  private HttpResponse executeWithAuthRetry(HttpUriRequest paramHttpUriRequest)
    throws ClientProtocolException, IOException, Uploader.UnauthorizedException
  {
    long l1 = SystemClock.elapsedRealtime();
    HttpResponse localHttpResponse = this.mHttpClient.execute(paramHttpUriRequest);
    MetricsUtils.incrementNetworkOpDuration(SystemClock.elapsedRealtime() - l1);
    int i = localHttpResponse.getStatusLine().getStatusCode();
    if ((i == 401) || (i == 403))
    {
      try
      {
        this.mAuthToken = this.mAuthorizer.getFreshAuthToken(this.mUploadTask.getAccount(), this.mAuthToken);
        if (this.mAuthToken == null)
          throw new Uploader.UnauthorizedException("null auth token");
      }
      catch (OperationCanceledException localOperationCanceledException)
      {
        Log.d("UploadsManager", "authentication canceled", localOperationCanceledException);
        throw new Uploader.UnauthorizedException(localOperationCanceledException);
      }
      catch (IOException localIOException)
      {
        Log.d("UploadsManager", "authentication failed", localIOException);
        throw localIOException;
      }
      catch (AuthenticatorException localAuthenticatorException)
      {
        Log.w("UploadsManager", localAuthenticatorException);
        throw new Uploader.UnauthorizedException(localAuthenticatorException);
      }
      paramHttpUriRequest.setHeader("Authorization", "GoogleLogin auth=" + this.mAuthToken);
      Log.d("UploadsManager", "executeWithAuthRetry: attempt #2");
      long l2 = SystemClock.elapsedRealtime();
      localHttpResponse = this.mHttpClient.execute(paramHttpUriRequest);
      MetricsUtils.incrementNetworkOpDuration(SystemClock.elapsedRealtime() - l2);
    }
    return localHttpResponse;
  }

  private static HttpEntity getEntity(HttpResponse paramHttpResponse)
    throws IOException
  {
    BufferedHttpEntity localBufferedHttpEntity = new BufferedHttpEntity(paramHttpResponse.getEntity());
    if (localBufferedHttpEntity.getContentLength() == 0L)
    {
      safeConsumeContent(localBufferedHttpEntity);
      localBufferedHttpEntity = null;
    }
    return localBufferedHttpEntity;
  }

  private static HttpUriRequest getInitialRequest(Uri paramUri, String paramString)
    throws UnsupportedEncodingException
  {
    HttpPost localHttpPost = new HttpPost(paramUri.toString());
    int i = paramString.indexOf("\r\n\r\n");
    String str1;
    if (i > 0)
      str1 = paramString.substring(0, i);
    for (String str2 = paramString.substring(i); ; str2 = null)
    {
      HashMap localHashMap = parseHeaders(str1);
      Iterator localIterator = localHashMap.keySet().iterator();
      while (true)
      {
        if (!localIterator.hasNext())
          break label107;
        String str3 = (String)localIterator.next();
        localHttpPost.setHeader(str3, (String)localHashMap.get(str3));
      }
      str1 = paramString;
    }
    if (str2 != null)
    {
      label107: StringEntity localStringEntity = new StringEntity(str2);
      localStringEntity.setContentType((String)null);
      localHttpPost.setEntity(localStringEntity);
    }
    return localHttpPost;
  }

  private static HttpUriRequest getResumeRequest(String paramString)
  {
    HttpPut localHttpPut = new HttpPut(paramString);
    localHttpPut.setHeader("Content-Range", "bytes */*");
    return localHttpPut;
  }

  private static HttpUriRequest getUploadRequest(String paramString1, String paramString2, long paramLong1, int paramInt, long paramLong2, byte[] paramArrayOfByte)
  {
    HttpPut localHttpPut = new HttpPut(paramString1);
    long l = paramLong1 + paramInt - 1L;
    localHttpPut.setHeader("Content-Range", "bytes " + paramLong1 + "-" + l + "/" + paramLong2);
    localHttpPut.setHeader("Content-Type", paramString2);
    InputStreamEntity localInputStreamEntity = new InputStreamEntity(new ByteArrayInputStream(paramArrayOfByte, 0, paramInt), paramInt);
    localInputStreamEntity.setContentType((String)null);
    localHttpPut.setEntity(localInputStreamEntity);
    return localHttpPut;
  }

  private static String getUserAgent(Context paramContext)
  {
    if (sUserAgent == null);
    try
    {
      PackageInfo localPackageInfo = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0);
      Object[] arrayOfObject = new Object[10];
      arrayOfObject[0] = localPackageInfo.packageName;
      arrayOfObject[1] = localPackageInfo.versionName;
      arrayOfObject[2] = Build.BRAND;
      arrayOfObject[3] = Build.DEVICE;
      arrayOfObject[4] = Build.MODEL;
      arrayOfObject[5] = Build.ID;
      arrayOfObject[6] = Build.VERSION.SDK;
      arrayOfObject[7] = Build.VERSION.RELEASE;
      arrayOfObject[8] = Build.VERSION.INCREMENTAL;
      arrayOfObject[9] = Integer.valueOf(1);
      sUserAgent = String.format("%s/%s; %s/%s/%s/%s; %s/%s/%s/%d", arrayOfObject);
      return sUserAgent;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      throw new IllegalStateException("getPackageInfo failed");
    }
  }

  private static boolean isIncompeteStatusCode(int paramInt)
  {
    return paramInt == 308;
  }

  private static boolean isSuccessStatusCode(int paramInt)
  {
    return (paramInt == 200) || (paramInt == 201);
  }

  private UploadedEntry newUploadedEntry(GDataResponse paramGDataResponse)
  {
    return new UploadedEntry(this.mUploadTask, paramGDataResponse.photoId, paramGDataResponse.photoSize, paramGDataResponse.timestamp, paramGDataResponse.photoUrl, paramGDataResponse.fingerprint.getBytes());
  }

  private static HashMap<String, String> parseHeaders(String paramString)
  {
    HashMap localHashMap = new HashMap();
    String[] arrayOfString1 = paramString.split("\r\n");
    int i = arrayOfString1.length;
    for (int j = 0; j < i; ++j)
    {
      String[] arrayOfString2 = arrayOfString1[j].split(":");
      if (arrayOfString2.length != 2)
        continue;
      localHashMap.put(arrayOfString2[0], arrayOfString2[1]);
    }
    return localHashMap;
  }

  private static long parseRangeHeaderEndByte(String paramString)
  {
    if (paramString != null)
    {
      Matcher localMatcher = RE_RANGE_HEADER.matcher(paramString);
      if (localMatcher.find())
        return 1L + Long.parseLong(localMatcher.group(2));
    }
    return -1L;
  }

  private GDataResponse parseResult(HttpEntity paramHttpEntity)
    throws SAXException, IOException, Uploader.UploadException
  {
    if (paramHttpEntity == null)
      throw new Uploader.UploadException("null HttpEntity in response");
    GDataResponse localGDataResponse = new GDataResponse(null);
    InputStream localInputStream = paramHttpEntity.getContent();
    try
    {
      Xml.parse(localInputStream, Xml.Encoding.UTF_8, localGDataResponse);
      localInputStream.close();
      return localGDataResponse;
    }
    finally
    {
      localInputStream.close();
    }
  }

  private static int readFullyOrToEof(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 0;
    while (true)
    {
      int j;
      if (i < paramInt2)
      {
        j = paramInputStream.read(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
        if (j != -1)
          break label33;
      }
      return i;
      label33: i += j;
    }
  }

  private void resetUpload()
  {
    this.mUploadTask.setUploadUrl(null);
    this.mUploadTask.setBytesUploaded(0L);
  }

  private UploadedEntry resume(InputStream paramInputStream)
    throws ClientProtocolException, IOException, Uploader.PicasaQuotaException, SAXException, Uploader.UploadException, Uploader.LocalIoException, Uploader.MediaFileChangedException, Uploader.RestartException, Uploader.UnauthorizedException
  {
    HttpResponse localHttpResponse = executeWithAuthRetry(getResumeRequest(this.mUploadTask.getUploadUrl()));
    int i = localHttpResponse.getStatusLine().getStatusCode();
    HttpEntity localHttpEntity = getEntity(localHttpResponse);
    if (localHttpEntity == null)
      Log.d("UploadsManager", "  Entity: content length was 0.");
    long l;
    try
    {
      if ((!isIncompeteStatusCode(i)) || (!localHttpResponse.containsHeader("range")))
        break label177;
      Header localHeader = localHttpResponse.getFirstHeader("range");
      l = parseRangeHeaderEndByte(localHeader.getValue());
      if (l < 0L)
        throw new Uploader.RestartException("negative range offset: " + localHeader);
    }
    finally
    {
      safeConsumeContent(localHttpEntity);
    }
    paramInputStream.skip(l);
    paramInputStream.mark(262144);
    this.mUploadTask.setBytesUploaded(l);
    UploadedEntry localUploadedEntry2 = uploadChunks(paramInputStream);
    safeConsumeContent(localHttpEntity);
    return localUploadedEntry2;
    if (isSuccessStatusCode(i))
    {
      label177: GDataResponse localGDataResponse = parseResult(localHttpEntity);
      throwIfQuotaError(localGDataResponse);
      Log.w("UploadsManager", "nothing to resume, upload already completed");
      this.mUploadTask.setBytesUploaded(this.mUploadTask.getBytesTotal());
      UploadedEntry localUploadedEntry1 = newUploadedEntry(localGDataResponse);
      safeConsumeContent(localHttpEntity);
      return localUploadedEntry1;
    }
    if (i == 401)
      throw new Uploader.UnauthorizedException(localHttpResponse.getStatusLine().toString());
    resetUpload();
    throw new Uploader.RestartException("unexpected resume response: " + localHttpResponse.getStatusLine());
  }

  private static void safeConsumeContent(HttpEntity paramHttpEntity)
  {
    if (paramHttpEntity != null);
    try
    {
      paramHttpEntity.consumeContent();
      return;
    }
    catch (IOException localIOException)
    {
    }
  }

  private UploadedEntry start(InputStream paramInputStream, Uri paramUri, String paramString)
    throws ClientProtocolException, IOException, Uploader.PicasaQuotaException, SAXException, Uploader.UploadException, Uploader.MediaFileChangedException, Uploader.UnauthorizedException, Uploader.RestartException, Uploader.LocalIoException
  {
    HttpResponse localHttpResponse = executeWithAuthRetry(getInitialRequest(paramUri, paramString));
    HttpEntity localHttpEntity = getEntity(localHttpResponse);
    int i = localHttpResponse.getStatusLine().getStatusCode();
    try
    {
      if (isSuccessStatusCode(i))
      {
        if (localHttpEntity != null)
          throwIfQuotaError(parseResult(localHttpEntity));
        Header localHeader = localHttpResponse.getFirstHeader("Location");
        this.mUploadTask.setUploadUrl(localHeader.getValue());
        this.mUploadTask.setBytesUploaded(0L);
        UploadedEntry localUploadedEntry = uploadChunks(paramInputStream);
        return localUploadedEntry;
      }
      throw new Uploader.UploadException("upload failed (bad payload, file too large) " + localHttpResponse.getStatusLine());
    }
    finally
    {
      safeConsumeContent(localHttpEntity);
    }
    if (i == 401)
      throw new Uploader.UnauthorizedException(localHttpResponse.getStatusLine().toString());
    if ((i >= 500) && (i < 600))
      throw new Uploader.RestartException("upload transient error:" + localHttpResponse.getStatusLine());
    throw new Uploader.UploadException(localHttpResponse.getStatusLine().toString());
  }

  private void throwIfQuotaError(GDataResponse paramGDataResponse)
    throws Uploader.PicasaQuotaException
  {
    if ((paramGDataResponse == null) || (!"LimitQuota".equals(paramGDataResponse.errorCode)))
      return;
    throw new Uploader.PicasaQuotaException(paramGDataResponse.errorCode);
  }

  private UploadedEntry uploadChunks(InputStream paramInputStream)
    throws ClientProtocolException, IOException, Uploader.PicasaQuotaException, SAXException, Uploader.UploadException, Uploader.MediaFileChangedException, Uploader.RestartException, Uploader.LocalIoException, Uploader.UnauthorizedException
  {
    byte[] arrayOfByte = new byte[262144];
    label285: HttpResponse localHttpResponse;
    int l;
    while (true)
    {
      if (this.mUploadTask.getBytesUploaded() >= this.mUploadTask.getBytesTotal())
        break label633;
      if (this.mListener != null)
        this.mListener.onProgress(this.mUploadTask);
      if (!this.mUploadTask.isUploading())
        return null;
      long l1 = this.mUploadTask.getBytesUploaded();
      int i = (int)(this.mUploadTask.getBytesTotal() - l1);
      int j;
      if (i <= 262144)
        j = 1;
      while (true)
      {
        if (j == 0)
          i = 262144;
        paramInputStream.mark(262144);
        try
        {
          int k = readFullyOrToEof(paramInputStream, arrayOfByte, 0, i);
          if ((j == 0) && (k == i))
            break label285;
          long[] arrayOfLong = new long[1];
          Fingerprint localFingerprint = Fingerprint.fromInputStream(this.mContext.getContentResolver().openInputStream(this.mUploadTask.getContentUri()), arrayOfLong);
          if (localFingerprint.equals(this.mUploadTask.getFingerprint()))
            break label285;
          String str1 = this.mUploadTask.getContentUri().toString();
          this.mUploadTask.setFingerprint(localFingerprint);
          String str2 = this.mUploadTask.getUploadUrl();
          this.mUploadTask.setUploadUrl(null);
          this.mUploadTask.setBytesUploaded(0L);
          this.mUploadTask.setBytesTotal(arrayOfLong[0]);
          throw new Uploader.MediaFileChangedException("UPLOAD_SIZE_DATA_MISMATCH: fingerprint changed; uri=" + str1 + ",uploadUrl=" + str2);
          j = 0;
        }
        catch (IOException localIOException)
        {
          throw new Uploader.LocalIoException(localIOException);
        }
      }
      localHttpResponse = executeWithAuthRetry(getUploadRequest(this.mUploadTask.getUploadUrl(), this.mUploadTask.getMimeType(), l1, i, this.mUploadTask.getBytesTotal(), arrayOfByte));
      long l2;
      while (true)
      {
        try
        {
          l = localHttpResponse.getStatusLine().getStatusCode();
          if (isSuccessStatusCode(l))
          {
            GDataResponse localGDataResponse = parseResult(getEntity(localHttpResponse));
            throwIfQuotaError(localGDataResponse);
            this.mUploadTask.setBytesUploaded(this.mUploadTask.getBytesTotal());
            MetricsUtils.incrementNetworkOpCount(1L);
            UploadedEntry localUploadedEntry = newUploadedEntry(localGDataResponse);
            return localUploadedEntry;
          }
          if (!isIncompeteStatusCode(l))
            break label523;
          Header localHeader = localHttpResponse.getFirstHeader("range");
          if (localHeader != null)
          {
            l2 = parseRangeHeaderEndByte(localHeader.getValue());
            throw new Uploader.UploadException("malformed or missing range header for subsequent upload");
          }
        }
        finally
        {
          safeConsumeContent(localHttpResponse.getEntity());
        }
        l2 = -1L;
      }
      if (l2 < l1 + i)
      {
        paramInputStream.reset();
        paramInputStream.skip(l2);
      }
      long l3 = l2;
      this.mUploadTask.setBytesUploaded(l3);
      safeConsumeContent(localHttpResponse.getEntity());
    }
    if (l == 400)
      label523: throw new Uploader.UploadException("upload failed (bad payload, file too large) " + localHttpResponse.getStatusLine());
    if ((l >= 500) && (l < 600))
      throw new Uploader.RestartException("upload transient error" + localHttpResponse.getStatusLine());
    throw new Uploader.UploadException(localHttpResponse.getStatusLine().toString());
    label633: throw new Uploader.UploadException("upload is done but no server confirmation");
  }

  public void close()
  {
    this.mHttpClient = null;
  }

  // ERROR //
  public UploadedEntry upload(UploadTaskEntry paramUploadTaskEntry, Uploader.UploadProgressListener paramUploadProgressListener)
    throws Uploader.UploadException, IOException, Uploader.RestartException, Uploader.MediaFileChangedException, Uploader.UnauthorizedException, Uploader.PicasaQuotaException, Uploader.LocalIoException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 539	com/google/android/picasasync/UploadTaskEntry:getBytesTotal	()J
    //   4: lconst_0
    //   5: lcmp
    //   6: ifgt +14 -> 20
    //   9: new 642	java/lang/IllegalArgumentException
    //   12: dup
    //   13: ldc_w 644
    //   16: invokespecial 645	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   19: athrow
    //   20: aload_0
    //   21: aload_1
    //   22: putfield 96	com/google/android/picasasync/GDataUploader:mUploadTask	Lcom/google/android/picasasync/UploadTaskEntry;
    //   25: aload_0
    //   26: aload_2
    //   27: putfield 572	com/google/android/picasasync/GDataUploader:mListener	Lcom/google/android/picasasync/Uploader$UploadProgressListener;
    //   30: aload_0
    //   31: new 106	com/google/android/picasasync/Authorizer
    //   34: dup
    //   35: aload_0
    //   36: getfield 39	com/google/android/picasasync/GDataUploader:mContext	Landroid/content/Context;
    //   39: aload_1
    //   40: invokevirtual 648	com/google/android/picasasync/UploadTaskEntry:getAuthTokenType	()Ljava/lang/String;
    //   43: invokespecial 651	com/google/android/picasasync/Authorizer:<init>	(Landroid/content/Context;Ljava/lang/String;)V
    //   46: putfield 94	com/google/android/picasasync/GDataUploader:mAuthorizer	Lcom/google/android/picasasync/Authorizer;
    //   49: aload_0
    //   50: aload_0
    //   51: getfield 94	com/google/android/picasasync/GDataUploader:mAuthorizer	Lcom/google/android/picasasync/Authorizer;
    //   54: aload_0
    //   55: getfield 96	com/google/android/picasasync/GDataUploader:mUploadTask	Lcom/google/android/picasasync/UploadTaskEntry;
    //   58: invokevirtual 102	com/google/android/picasasync/UploadTaskEntry:getAccount	()Ljava/lang/String;
    //   61: invokevirtual 655	com/google/android/picasasync/Authorizer:getAuthToken	(Ljava/lang/String;)Ljava/lang/String;
    //   64: putfield 104	com/google/android/picasasync/GDataUploader:mAuthToken	Ljava/lang/String;
    //   67: aload_0
    //   68: getfield 96	com/google/android/picasasync/GDataUploader:mUploadTask	Lcom/google/android/picasasync/UploadTaskEntry;
    //   71: invokevirtual 658	com/google/android/picasasync/UploadTaskEntry:getRequestTemplate	()Ljava/lang/String;
    //   74: astore 9
    //   76: aload_0
    //   77: getfield 104	com/google/android/picasasync/GDataUploader:mAuthToken	Ljava/lang/String;
    //   80: ifnonnull +134 -> 214
    //   83: aload 9
    //   85: ldc_w 660
    //   88: ldc_w 662
    //   91: invokevirtual 665	java/lang/String:replaceAll	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   94: astore 10
    //   96: aconst_null
    //   97: astore 11
    //   99: new 667	java/io/BufferedInputStream
    //   102: dup
    //   103: aload_0
    //   104: getfield 39	com/google/android/picasasync/GDataUploader:mContext	Landroid/content/Context;
    //   107: invokevirtual 587	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   110: aload_1
    //   111: invokevirtual 591	com/google/android/picasasync/UploadTaskEntry:getContentUri	()Landroid/net/Uri;
    //   114: invokevirtual 597	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   117: invokespecial 670	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   120: astore 12
    //   122: aload_0
    //   123: getfield 96	com/google/android/picasasync/GDataUploader:mUploadTask	Lcom/google/android/picasasync/UploadTaskEntry;
    //   126: invokevirtual 477	com/google/android/picasasync/UploadTaskEntry:getUploadUrl	()Ljava/lang/String;
    //   129: invokestatic 676	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   132: ifeq +99 -> 231
    //   135: aload_0
    //   136: aload 12
    //   138: aload_0
    //   139: getfield 96	com/google/android/picasasync/GDataUploader:mUploadTask	Lcom/google/android/picasasync/UploadTaskEntry;
    //   142: invokevirtual 679	com/google/android/picasasync/UploadTaskEntry:getUrl	()Landroid/net/Uri;
    //   145: aload 10
    //   147: invokespecial 681	com/google/android/picasasync/GDataUploader:start	(Ljava/io/InputStream;Landroid/net/Uri;Ljava/lang/String;)Lcom/google/android/picasasync/UploadedEntry;
    //   150: astore 19
    //   152: aload 12
    //   154: invokestatic 687	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   157: aload 19
    //   159: areturn
    //   160: astore 7
    //   162: ldc 117
    //   164: ldc 119
    //   166: aload 7
    //   168: invokestatic 125	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   171: pop
    //   172: new 59	com/google/android/picasasync/Uploader$UnauthorizedException
    //   175: dup
    //   176: aload 7
    //   178: invokespecial 128	com/google/android/picasasync/Uploader$UnauthorizedException:<init>	(Ljava/lang/Throwable;)V
    //   181: athrow
    //   182: astore 5
    //   184: ldc 117
    //   186: ldc 130
    //   188: aload 5
    //   190: invokestatic 125	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   193: pop
    //   194: aload 5
    //   196: athrow
    //   197: astore_3
    //   198: ldc 117
    //   200: aload_3
    //   201: invokestatic 134	android/util/Log:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
    //   204: pop
    //   205: new 59	com/google/android/picasasync/Uploader$UnauthorizedException
    //   208: dup
    //   209: aload_3
    //   210: invokespecial 128	com/google/android/picasasync/Uploader$UnauthorizedException:<init>	(Ljava/lang/Throwable;)V
    //   213: athrow
    //   214: aload 9
    //   216: ldc_w 689
    //   219: aload_0
    //   220: getfield 104	com/google/android/picasasync/GDataUploader:mAuthToken	Ljava/lang/String;
    //   223: invokevirtual 665	java/lang/String:replaceAll	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   226: astore 10
    //   228: goto -132 -> 96
    //   231: aload_0
    //   232: aload 12
    //   234: invokespecial 691	com/google/android/picasasync/GDataUploader:resume	(Ljava/io/InputStream;)Lcom/google/android/picasasync/UploadedEntry;
    //   237: astore 18
    //   239: aload 12
    //   241: invokestatic 687	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   244: aload 18
    //   246: areturn
    //   247: astore 13
    //   249: new 57	java/io/IOException
    //   252: dup
    //   253: aload 13
    //   255: invokevirtual 692	org/apache/http/client/ClientProtocolException:toString	()Ljava/lang/String;
    //   258: invokespecial 693	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   261: athrow
    //   262: astore 14
    //   264: aload 11
    //   266: invokestatic 687	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   269: aload 14
    //   271: athrow
    //   272: astore 15
    //   274: new 470	com/google/android/picasasync/Uploader$LocalIoException
    //   277: dup
    //   278: aload 15
    //   280: invokespecial 619	com/google/android/picasasync/Uploader$LocalIoException:<init>	(Ljava/lang/Throwable;)V
    //   283: athrow
    //   284: ldc 117
    //   286: ldc_w 695
    //   289: aload 16
    //   291: invokestatic 698	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   294: pop
    //   295: new 419	com/google/android/picasasync/Uploader$UploadException
    //   298: dup
    //   299: ldc_w 695
    //   302: aload 16
    //   304: invokespecial 701	com/google/android/picasasync/Uploader$UploadException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   307: athrow
    //   308: astore 14
    //   310: aload 12
    //   312: astore 11
    //   314: goto -50 -> 264
    //   317: astore 16
    //   319: aload 12
    //   321: astore 11
    //   323: goto -39 -> 284
    //   326: astore 15
    //   328: aload 12
    //   330: astore 11
    //   332: goto -58 -> 274
    //   335: astore 13
    //   337: aload 12
    //   339: astore 11
    //   341: goto -92 -> 249
    //   344: astore 16
    //   346: aconst_null
    //   347: astore 11
    //   349: goto -65 -> 284
    //
    // Exception table:
    //   from	to	target	type
    //   49	67	160	android/accounts/OperationCanceledException
    //   49	67	182	java/io/IOException
    //   49	67	197	android/accounts/AuthenticatorException
    //   99	122	247	org/apache/http/client/ClientProtocolException
    //   99	122	262	finally
    //   249	262	262	finally
    //   274	284	262	finally
    //   284	308	262	finally
    //   99	122	272	java/io/FileNotFoundException
    //   122	152	308	finally
    //   231	239	308	finally
    //   122	152	317	org/xml/sax/SAXException
    //   231	239	317	org/xml/sax/SAXException
    //   122	152	326	java/io/FileNotFoundException
    //   231	239	326	java/io/FileNotFoundException
    //   122	152	335	org/apache/http/client/ClientProtocolException
    //   231	239	335	org/apache/http/client/ClientProtocolException
    //   99	122	344	org/xml/sax/SAXException
  }

  private static class GDataResponse extends DefaultHandler
  {
    String errorCode;
    Fingerprint fingerprint;
    private HashMap<String, StringBuilder> mMap = new HashMap();
    private ArrayList<String> mStreamIdList = new ArrayList();
    private StringBuilder mText;
    long photoId;
    long photoSize;
    String photoUrl;
    long timestamp;

    private String getMediaContentAttrs(Attributes paramAttributes)
    {
      int i = paramAttributes.getLength();
      for (int j = 0; j < i; ++j)
        if ("url".contentEquals(paramAttributes.getQName(j)))
          return paramAttributes.getValue(j);
      return "";
    }

    public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    {
      if (this.mText == null)
        return;
      this.mText.append(paramArrayOfChar, paramInt1, paramInt2);
    }

    public void endElement(String paramString1, String paramString2, String paramString3)
    {
      if (("gphoto:streamId".contentEquals(paramString3)) && (this.mText.length() > 0))
        this.mStreamIdList.add(this.mText.toString());
      this.mText = null;
    }

    public void startDocument()
    {
      this.mMap.clear();
      this.mMap.put("code", new StringBuilder());
      this.mMap.put("gphoto:id", new StringBuilder());
      this.mMap.put("gphoto:size", new StringBuilder());
      this.mMap.put("gphoto:streamId", new StringBuilder());
      this.mMap.put("gphoto:timestamp", new StringBuilder());
      this.photoUrl = "";
      this.mStreamIdList.clear();
    }

    public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    {
      this.mText = ((StringBuilder)this.mMap.get(paramString3));
      if (this.mText == null)
      {
        if ("media:content".contentEquals(paramString3))
          this.photoUrl = getMediaContentAttrs(paramAttributes);
        return;
      }
      this.mText.setLength(0);
    }

    // ERROR //
    public void validateResult()
      throws Uploader.UploadException
    {
      // Byte code:
      //   0: aload_0
      //   1: aload_0
      //   2: getfield 30	com/google/android/picasasync/GDataUploader$GDataResponse:mMap	Ljava/util/HashMap;
      //   5: ldc 93
      //   7: invokevirtual 113	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   10: checkcast 68	java/lang/StringBuilder
      //   13: invokevirtual 83	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   16: putfield 128	com/google/android/picasasync/GDataUploader$GDataResponse:errorCode	Ljava/lang/String;
      //   19: aload_0
      //   20: aload_0
      //   21: getfield 30	com/google/android/picasasync/GDataUploader$GDataResponse:mMap	Ljava/util/HashMap;
      //   24: ldc 100
      //   26: invokevirtual 113	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   29: checkcast 68	java/lang/StringBuilder
      //   32: invokevirtual 83	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   35: invokestatic 134	java/lang/Long:parseLong	(Ljava/lang/String;)J
      //   38: putfield 136	com/google/android/picasasync/GDataUploader$GDataResponse:photoId	J
      //   41: aload_0
      //   42: aload_0
      //   43: getfield 30	com/google/android/picasasync/GDataUploader$GDataResponse:mMap	Ljava/util/HashMap;
      //   46: ldc 102
      //   48: invokevirtual 113	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   51: checkcast 68	java/lang/StringBuilder
      //   54: invokevirtual 83	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   57: invokestatic 134	java/lang/Long:parseLong	(Ljava/lang/String;)J
      //   60: putfield 138	com/google/android/picasasync/GDataUploader$GDataResponse:photoSize	J
      //   63: aload_0
      //   64: aload_0
      //   65: getfield 30	com/google/android/picasasync/GDataUploader$GDataResponse:mMap	Ljava/util/HashMap;
      //   68: ldc 104
      //   70: invokevirtual 113	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   73: checkcast 68	java/lang/StringBuilder
      //   76: invokevirtual 83	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   79: invokestatic 134	java/lang/Long:parseLong	(Ljava/lang/String;)J
      //   82: putfield 140	com/google/android/picasasync/GDataUploader$GDataResponse:timestamp	J
      //   85: aload_0
      //   86: getfield 106	com/google/android/picasasync/GDataUploader$GDataResponse:photoUrl	Ljava/lang/String;
      //   89: invokestatic 145	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
      //   92: ifeq +121 -> 213
      //   95: new 124	com/google/android/picasasync/Uploader$UploadException
      //   98: dup
      //   99: ldc 147
      //   101: invokespecial 150	com/google/android/picasasync/Uploader$UploadException:<init>	(Ljava/lang/String;)V
      //   104: athrow
      //   105: astore_1
      //   106: new 124	com/google/android/picasasync/Uploader$UploadException
      //   109: dup
      //   110: new 68	java/lang/StringBuilder
      //   113: dup
      //   114: invokespecial 94	java/lang/StringBuilder:<init>	()V
      //   117: ldc 152
      //   119: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   122: aload_0
      //   123: getfield 30	com/google/android/picasasync/GDataUploader$GDataResponse:mMap	Ljava/util/HashMap;
      //   126: ldc 100
      //   128: invokevirtual 113	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   131: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   134: invokevirtual 83	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   137: invokespecial 150	com/google/android/picasasync/Uploader$UploadException:<init>	(Ljava/lang/String;)V
      //   140: athrow
      //   141: astore_2
      //   142: new 124	com/google/android/picasasync/Uploader$UploadException
      //   145: dup
      //   146: new 68	java/lang/StringBuilder
      //   149: dup
      //   150: invokespecial 94	java/lang/StringBuilder:<init>	()V
      //   153: ldc 160
      //   155: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   158: aload_0
      //   159: getfield 30	com/google/android/picasasync/GDataUploader$GDataResponse:mMap	Ljava/util/HashMap;
      //   162: ldc 102
      //   164: invokevirtual 113	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   167: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   170: invokevirtual 83	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   173: invokespecial 150	com/google/android/picasasync/Uploader$UploadException:<init>	(Ljava/lang/String;)V
      //   176: athrow
      //   177: astore_3
      //   178: new 124	com/google/android/picasasync/Uploader$UploadException
      //   181: dup
      //   182: new 68	java/lang/StringBuilder
      //   185: dup
      //   186: invokespecial 94	java/lang/StringBuilder:<init>	()V
      //   189: ldc 162
      //   191: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   194: aload_0
      //   195: getfield 30	com/google/android/picasasync/GDataUploader$GDataResponse:mMap	Ljava/util/HashMap;
      //   198: ldc 104
      //   200: invokevirtual 113	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   203: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   206: invokevirtual 83	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   209: invokespecial 150	com/google/android/picasasync/Uploader$UploadException:<init>	(Ljava/lang/String;)V
      //   212: athrow
      //   213: aload_0
      //   214: aload_0
      //   215: getfield 35	com/google/android/picasasync/GDataUploader$GDataResponse:mStreamIdList	Ljava/util/ArrayList;
      //   218: invokestatic 168	com/android/gallery3d/common/Fingerprint:extractFingerprint	(Ljava/util/List;)Lcom/android/gallery3d/common/Fingerprint;
      //   221: putfield 170	com/google/android/picasasync/GDataUploader$GDataResponse:fingerprint	Lcom/android/gallery3d/common/Fingerprint;
      //   224: aload_0
      //   225: getfield 170	com/google/android/picasasync/GDataUploader$GDataResponse:fingerprint	Lcom/android/gallery3d/common/Fingerprint;
      //   228: ifnonnull +33 -> 261
      //   231: new 124	com/google/android/picasasync/Uploader$UploadException
      //   234: dup
      //   235: new 68	java/lang/StringBuilder
      //   238: dup
      //   239: invokespecial 94	java/lang/StringBuilder:<init>	()V
      //   242: ldc 172
      //   244: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   247: aload_0
      //   248: getfield 35	com/google/android/picasasync/GDataUploader$GDataResponse:mStreamIdList	Ljava/util/ArrayList;
      //   251: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   254: invokevirtual 83	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   257: invokespecial 150	com/google/android/picasasync/Uploader$UploadException:<init>	(Ljava/lang/String;)V
      //   260: athrow
      //   261: return
      //
      // Exception table:
      //   from	to	target	type
      //   19	41	105	java/lang/NumberFormatException
      //   41	63	141	java/lang/NumberFormatException
      //   63	85	177	java/lang/NumberFormatException
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.GDataUploader
 * JD-Core Version:    0.5.4
 */