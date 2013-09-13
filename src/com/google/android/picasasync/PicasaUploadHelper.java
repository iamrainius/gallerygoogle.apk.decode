package com.google.android.picasasync;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;
import com.android.gallery3d.common.Fingerprint;
import com.android.gallery3d.common.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class PicasaUploadHelper
{
  private static final Uri PICASA_BASE_UPLOAD_URL = Uri.parse("https://picasaweb.google.com/data/upload/resumable/media/create-session/feed/api/user/default/albumid/");
  private static final String[] PROJECTION_DATA = { "_data" };
  private static final String[] PROJECTION_DATE_TAKEN;
  private static final String[] PROJECTION_SIZE = { "_size" };

  static
  {
    PROJECTION_DATE_TAKEN = new String[] { "datetaken" };
  }

  private static String buildMetadata(String paramString1, String paramString2, Date paramDate, String paramString3)
  {
    StringBuilder localStringBuilder = new StringBuilder("\r\n<entry xmlns='http://www.w3.org/2005/Atom' xmlns:gphoto='http://schemas.google.com/photos/2007'><category scheme='http://schemas.google.com/g/2005#kind' term='http://schemas.google.com/photos/2007#photo'/>");
    localStringBuilder.append("<title>");
    localStringBuilder.append(Utils.escapeXml(paramString1));
    localStringBuilder.append("</title>");
    if (!TextUtils.isEmpty(paramString2))
    {
      localStringBuilder.append("<summary>");
      localStringBuilder.append(Utils.escapeXml(paramString2));
      localStringBuilder.append("</summary>");
    }
    localStringBuilder.append("<gphoto:timestamp>");
    localStringBuilder.append(paramDate.getTime());
    localStringBuilder.append("</gphoto:timestamp>");
    if (!TextUtils.isEmpty(paramString3))
    {
      localStringBuilder.append("<gphoto:streamId>");
      localStringBuilder.append(paramString3);
      localStringBuilder.append("</gphoto:streamId>");
    }
    localStringBuilder.append("<gphoto:streamId>mobile_uploaded</gphoto:streamId>");
    localStringBuilder.append("</entry>");
    return localStringBuilder.toString();
  }

  private static String buildRequestTemplate(String paramString1, String paramString2, String paramString3, String paramString4, Date paramDate, String paramString5, long paramLong)
  {
    String str = getFileName(paramString2);
    if (paramDate == null)
      paramDate = new Date();
    return "Authorization: GoogleLogin auth=%=_auth_token_=%\r\nUser-Agent: " + paramString1 + "\r\n" + "GData-Version: 2.0" + "\r\n" + "Slug: " + str + "\r\n" + "X-Upload-Content-Type: " + paramString3 + "\r\n" + "X-Upload-Content-Length: " + Long.toString(paramLong) + "\r\n" + "Content-Type: application/atom+xml; charset=UTF-8" + "\r\n" + buildMetadata(str, paramString4, paramDate, paramString5) + "\r\n";
  }

  private static String buildUploadUrl(String paramString1, String paramString2)
  {
    return PICASA_BASE_UPLOAD_URL.buildUpon().appendEncodedPath(paramString1).appendQueryParameter("caid", paramString2).appendQueryParameter("xmlerrors", "1").build().toString();
  }

  static void fillRequest(Context paramContext, UploadTaskEntry paramUploadTaskEntry)
    throws IOException
  {
    ContentResolver localContentResolver = paramContext.getContentResolver();
    Uri localUri = paramUploadTaskEntry.getContentUri();
    String str1 = paramUploadTaskEntry.getAlbumId();
    if (str1 == null)
    {
      str1 = "camera-sync";
      paramUploadTaskEntry.setAlbumId(str1);
    }
    paramUploadTaskEntry.setAuthTokenType("lh2");
    if (!paramUploadTaskEntry.hasPriority())
      paramUploadTaskEntry.setPriority(1);
    if (!paramUploadTaskEntry.hasFingerprint())
    {
      long[] arrayOfLong = new long[1];
      paramUploadTaskEntry.setFingerprint(Fingerprint.fromInputStream(localContentResolver.openInputStream(localUri), arrayOfLong));
      paramUploadTaskEntry.setBytesTotal(arrayOfLong[0]);
    }
    String str2;
    while (true)
    {
      str2 = paramUploadTaskEntry.getMimeType();
      if (str2 != null)
        break;
      str2 = setContentType(localContentResolver, paramUploadTaskEntry);
      if (str2 != null)
        break;
      throw new IOException("MIME type not known for " + localUri);
      if (paramUploadTaskEntry.getBytesTotal() > 0L)
        continue;
      setFileSize(localContentResolver, paramUploadTaskEntry);
    }
    String str3 = paramUploadTaskEntry.getCaption();
    long l = getOptionalLong(localContentResolver, localUri, PROJECTION_DATE_TAKEN, 0L);
    if (l > 0L);
    for (Date localDate = new Date(l); ; localDate = null)
    {
      String str4 = getOptionalString(localContentResolver, localUri, PROJECTION_DATA);
      if (str4 == null)
        str4 = localUri.toString();
      String str5 = paramUploadTaskEntry.getFingerprint().toStreamId();
      paramUploadTaskEntry.setRequestTemplate(buildRequestTemplate(Utils.getUserAgent(paramContext), str4, str2, str3, localDate, str5, paramUploadTaskEntry.getBytesTotal()));
      paramUploadTaskEntry.setUrl(buildUploadUrl(str1, str5));
      return;
    }
  }

  private static long getFileLengthFromContent(ContentResolver paramContentResolver, Uri paramUri)
  {
    InputStream localInputStream = null;
    try
    {
      localInputStream = paramContentResolver.openInputStream(paramUri);
      byte[] arrayOfByte = new byte[1024];
      long l = 0L;
      int j;
      for (int i = localInputStream.read(arrayOfByte); i >= 0; i = j)
      {
        l += i;
        j = localInputStream.read(arrayOfByte);
      }
      return l;
    }
    catch (Exception localException)
    {
      throw ((RuntimeException)localException);
    }
    finally
    {
      Utils.closeSilently(localInputStream);
    }
    throw new RuntimeException(localException);
  }

  private static long getFileLengthFromRawFdOrContent(ContentResolver paramContentResolver, Uri paramUri)
  {
    AssetFileDescriptor localAssetFileDescriptor = null;
    long l2;
    try
    {
      localAssetFileDescriptor = paramContentResolver.openAssetFileDescriptor(paramUri, "r");
      long l3 = localAssetFileDescriptor.getLength();
      l2 = l3;
      return l2;
    }
    catch (Exception localException)
    {
      long l1 = getFileLengthFromContent(paramContentResolver, paramUri);
      l2 = l1;
      return l2;
    }
    finally
    {
      if (localAssetFileDescriptor != null)
        Utils.closeSilently(localAssetFileDescriptor.getParcelFileDescriptor());
    }
  }

  private static String getFileName(String paramString)
  {
    int i = paramString.lastIndexOf("/");
    if (i > 0)
      paramString = paramString.substring(i + 1);
    return paramString;
  }

  // ERROR //
  private static long getOptionalLong(ContentResolver paramContentResolver, Uri paramUri, String[] paramArrayOfString, long paramLong)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aload_0
    //   4: aload_1
    //   5: aload_2
    //   6: aconst_null
    //   7: aconst_null
    //   8: aconst_null
    //   9: invokevirtual 319	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   12: astore 5
    //   14: aload 5
    //   16: invokeinterface 324 1 0
    //   21: ifeq +16 -> 37
    //   24: aload 5
    //   26: iconst_0
    //   27: invokeinterface 328 2 0
    //   32: lstore 8
    //   34: lload 8
    //   36: lstore_3
    //   37: aload 5
    //   39: ifnull +10 -> 49
    //   42: aload 5
    //   44: invokeinterface 331 1 0
    //   49: lload_3
    //   50: lreturn
    //   51: astore 7
    //   53: aload 5
    //   55: ifnull -6 -> 49
    //   58: aload 5
    //   60: invokeinterface 331 1 0
    //   65: lload_3
    //   66: lreturn
    //   67: astore 6
    //   69: aload 5
    //   71: ifnull +10 -> 81
    //   74: aload 5
    //   76: invokeinterface 331 1 0
    //   81: aload 6
    //   83: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   3	34	51	java/lang/Exception
    //   3	34	67	finally
  }

  // ERROR //
  private static String getOptionalString(ContentResolver paramContentResolver, Uri paramUri, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aload_0
    //   3: aload_1
    //   4: aload_2
    //   5: aconst_null
    //   6: aconst_null
    //   7: aconst_null
    //   8: invokevirtual 319	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   11: astore_3
    //   12: aload_3
    //   13: invokeinterface 324 1 0
    //   18: ifeq +29 -> 47
    //   21: aload_3
    //   22: iconst_0
    //   23: invokeinterface 334 2 0
    //   28: astore 7
    //   30: aload 7
    //   32: astore 6
    //   34: aload_3
    //   35: ifnull +9 -> 44
    //   38: aload_3
    //   39: invokeinterface 331 1 0
    //   44: aload 6
    //   46: areturn
    //   47: aconst_null
    //   48: astore 6
    //   50: goto -16 -> 34
    //   53: astore 5
    //   55: aload_3
    //   56: ifnull +9 -> 65
    //   59: aload_3
    //   60: invokeinterface 331 1 0
    //   65: aconst_null
    //   66: areturn
    //   67: astore 4
    //   69: aload_3
    //   70: ifnull +9 -> 79
    //   73: aload_3
    //   74: invokeinterface 331 1 0
    //   79: aload 4
    //   81: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   2	30	53	java/lang/Exception
    //   2	30	67	finally
  }

  static String setContentType(ContentResolver paramContentResolver, UploadTaskEntry paramUploadTaskEntry)
  {
    String str = paramContentResolver.getType(paramUploadTaskEntry.getContentUri());
    if (str != null)
    {
      paramUploadTaskEntry.setMimeType(str);
      return str;
    }
    return paramUploadTaskEntry.getMimeType();
  }

  static void setFileSize(ContentResolver paramContentResolver, UploadTaskEntry paramUploadTaskEntry)
  {
    Uri localUri = paramUploadTaskEntry.getContentUri();
    long l = getOptionalLong(paramContentResolver, localUri, PROJECTION_SIZE, 0L);
    if (l == 0L)
      l = getFileLengthFromRawFdOrContent(paramContentResolver, localUri);
    if (l == 0L)
      throw new RuntimeException("no content to upload: " + localUri);
    paramUploadTaskEntry.setBytesTotal(l);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.PicasaUploadHelper
 * JD-Core Version:    0.5.4
 */