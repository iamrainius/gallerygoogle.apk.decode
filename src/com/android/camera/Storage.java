package com.android.camera;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import com.android.gallery3d.common.ApiHelper;
import java.io.File;

public class Storage
{
  public static final String BUCKET_ID;
  public static final String DCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
  public static final String DIRECTORY = DCIM + "/Camera";

  static
  {
    BUCKET_ID = String.valueOf(DIRECTORY.toLowerCase().hashCode());
  }

  public static Uri addImage(ContentResolver paramContentResolver, String paramString1, long paramLong, Location paramLocation, int paramInt1, int paramInt2, String paramString2, int paramInt3, int paramInt4)
  {
    ContentValues localContentValues = new ContentValues(9);
    localContentValues.put("title", paramString1);
    localContentValues.put("_display_name", paramString1 + ".jpg");
    localContentValues.put("datetaken", Long.valueOf(paramLong));
    localContentValues.put("mime_type", "image/jpeg");
    localContentValues.put("orientation", Integer.valueOf(paramInt1));
    localContentValues.put("_data", paramString2);
    localContentValues.put("_size", Integer.valueOf(paramInt2));
    setImageSize(localContentValues, paramInt3, paramInt4);
    if (paramLocation != null)
    {
      localContentValues.put("latitude", Double.valueOf(paramLocation.getLatitude()));
      localContentValues.put("longitude", Double.valueOf(paramLocation.getLongitude()));
    }
    try
    {
      Uri localUri = paramContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);
      return localUri;
    }
    catch (Throwable localThrowable)
    {
      Log.e("CameraStorage", "Failed to write MediaStore" + localThrowable);
    }
    return null;
  }

  public static Uri addImage(ContentResolver paramContentResolver, String paramString, long paramLong, Location paramLocation, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    String str = generateFilepath(paramString);
    writeFile(str, paramArrayOfByte);
    return addImage(paramContentResolver, paramString, paramLong, paramLocation, paramInt1, paramArrayOfByte.length, str, paramInt2, paramInt3);
  }

  public static void deleteImage(ContentResolver paramContentResolver, Uri paramUri)
  {
    try
    {
      paramContentResolver.delete(paramUri, null, null);
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.e("CameraStorage", "Failed to delete image: " + paramUri);
    }
  }

  public static void ensureOSXCompatible()
  {
    File localFile = new File(DCIM, "100ANDRO");
    if ((localFile.exists()) || (localFile.mkdirs()))
      return;
    Log.e("CameraStorage", "Failed to create " + localFile.getPath());
  }

  public static String generateFilepath(String paramString)
  {
    return DIRECTORY + '/' + paramString + ".jpg";
  }

  public static long getAvailableSpace()
  {
    long l1 = -1L;
    String str = Environment.getExternalStorageState();
    Log.d("CameraStorage", "External storage state=" + str);
    if ("checking".equals(str))
      l1 = -2L;
    File localFile;
    do
    {
      do
        return l1;
      while (!"mounted".equals(str));
      localFile = new File(DIRECTORY);
      localFile.mkdirs();
    }
    while ((!localFile.isDirectory()) || (!localFile.canWrite()));
    try
    {
      StatFs localStatFs = new StatFs(DIRECTORY);
      long l2 = localStatFs.getAvailableBlocks();
      int i = localStatFs.getBlockSize();
      return l2 * i;
    }
    catch (Exception localException)
    {
      Log.i("CameraStorage", "Fail to access external storage", localException);
    }
    return -3L;
  }

  public static Uri newImage(ContentResolver paramContentResolver, String paramString, long paramLong, int paramInt1, int paramInt2)
  {
    String str = generateFilepath(paramString);
    ContentValues localContentValues = new ContentValues(4);
    localContentValues.put("datetaken", Long.valueOf(paramLong));
    localContentValues.put("_data", str);
    setImageSize(localContentValues, paramInt1, paramInt2);
    try
    {
      Uri localUri = paramContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);
      return localUri;
    }
    catch (Throwable localThrowable)
    {
      Log.e("CameraStorage", "Failed to new image" + localThrowable);
    }
    return null;
  }

  @TargetApi(16)
  private static void setImageSize(ContentValues paramContentValues, int paramInt1, int paramInt2)
  {
    if (!ApiHelper.HAS_MEDIA_COLUMNS_WIDTH_AND_HEIGHT)
      return;
    paramContentValues.put("width", Integer.valueOf(paramInt1));
    paramContentValues.put("height", Integer.valueOf(paramInt2));
  }

  // ERROR //
  public static boolean updateImage(ContentResolver paramContentResolver, Uri paramUri, String paramString, Location paramLocation, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    // Byte code:
    //   0: aload_2
    //   1: invokestatic 159	com/android/camera/Storage:generateFilepath	(Ljava/lang/String;)Ljava/lang/String;
    //   4: astore 8
    //   6: new 29	java/lang/StringBuilder
    //   9: dup
    //   10: invokespecial 32	java/lang/StringBuilder:<init>	()V
    //   13: aload 8
    //   15: invokevirtual 36	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   18: ldc_w 264
    //   21: invokevirtual 36	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: invokevirtual 39	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   27: astore 9
    //   29: aconst_null
    //   30: astore 10
    //   32: new 266	java/io/FileOutputStream
    //   35: dup
    //   36: aload 9
    //   38: invokespecial 267	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   41: astore 11
    //   43: aload 11
    //   45: aload 5
    //   47: invokevirtual 271	java/io/FileOutputStream:write	([B)V
    //   50: aload 11
    //   52: invokevirtual 274	java/io/FileOutputStream:close	()V
    //   55: new 21	java/io/File
    //   58: dup
    //   59: aload 9
    //   61: invokespecial 220	java/io/File:<init>	(Ljava/lang/String;)V
    //   64: new 21	java/io/File
    //   67: dup
    //   68: aload 8
    //   70: invokespecial 220	java/io/File:<init>	(Ljava/lang/String;)V
    //   73: invokevirtual 278	java/io/File:renameTo	(Ljava/io/File;)Z
    //   76: pop
    //   77: aload 11
    //   79: invokevirtual 274	java/io/FileOutputStream:close	()V
    //   82: new 63	android/content/ContentValues
    //   85: dup
    //   86: bipush 9
    //   88: invokespecial 66	android/content/ContentValues:<init>	(I)V
    //   91: astore 19
    //   93: aload 19
    //   95: ldc 68
    //   97: aload_2
    //   98: invokevirtual 72	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   101: aload 19
    //   103: ldc 74
    //   105: new 29	java/lang/StringBuilder
    //   108: dup
    //   109: invokespecial 32	java/lang/StringBuilder:<init>	()V
    //   112: aload_2
    //   113: invokevirtual 36	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   116: ldc 76
    //   118: invokevirtual 36	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   121: invokevirtual 39	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   124: invokevirtual 72	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   127: aload 19
    //   129: ldc 88
    //   131: ldc 90
    //   133: invokevirtual 72	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   136: aload 19
    //   138: ldc 92
    //   140: iload 4
    //   142: invokestatic 97	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   145: invokevirtual 100	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   148: aload 19
    //   150: ldc 104
    //   152: aload 5
    //   154: arraylength
    //   155: invokestatic 97	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   158: invokevirtual 100	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   161: aload 19
    //   163: iload 6
    //   165: iload 7
    //   167: invokestatic 108	com/android/camera/Storage:setImageSize	(Landroid/content/ContentValues;II)V
    //   170: aload_3
    //   171: ifnull +31 -> 202
    //   174: aload 19
    //   176: ldc 110
    //   178: aload_3
    //   179: invokevirtual 116	android/location/Location:getLatitude	()D
    //   182: invokestatic 121	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   185: invokevirtual 124	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Double;)V
    //   188: aload 19
    //   190: ldc 126
    //   192: aload_3
    //   193: invokevirtual 129	android/location/Location:getLongitude	()D
    //   196: invokestatic 121	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   199: invokevirtual 124	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Double;)V
    //   202: aload_0
    //   203: aload_1
    //   204: aload 19
    //   206: aconst_null
    //   207: aconst_null
    //   208: invokevirtual 282	android/content/ContentResolver:update	(Landroid/net/Uri;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    //   211: pop
    //   212: iconst_1
    //   213: ireturn
    //   214: astore 12
    //   216: ldc 143
    //   218: ldc_w 284
    //   221: aload 12
    //   223: invokestatic 286	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   226: pop
    //   227: aload 10
    //   229: invokevirtual 274	java/io/FileOutputStream:close	()V
    //   232: iconst_0
    //   233: ireturn
    //   234: astore 16
    //   236: iconst_0
    //   237: ireturn
    //   238: astore 13
    //   240: aload 10
    //   242: invokevirtual 274	java/io/FileOutputStream:close	()V
    //   245: aload 13
    //   247: athrow
    //   248: astore 20
    //   250: ldc 143
    //   252: new 29	java/lang/StringBuilder
    //   255: dup
    //   256: invokespecial 32	java/lang/StringBuilder:<init>	()V
    //   259: ldc_w 288
    //   262: invokevirtual 36	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   265: aload 20
    //   267: invokevirtual 148	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   270: invokevirtual 39	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   273: invokestatic 154	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   276: pop
    //   277: iconst_0
    //   278: ireturn
    //   279: astore 18
    //   281: goto -199 -> 82
    //   284: astore 14
    //   286: goto -41 -> 245
    //   289: astore 13
    //   291: aload 11
    //   293: astore 10
    //   295: goto -55 -> 240
    //   298: astore 12
    //   300: aload 11
    //   302: astore 10
    //   304: goto -88 -> 216
    //
    // Exception table:
    //   from	to	target	type
    //   32	43	214	java/lang/Exception
    //   227	232	234	java/lang/Exception
    //   32	43	238	finally
    //   216	227	238	finally
    //   202	212	248	java/lang/Throwable
    //   77	82	279	java/lang/Exception
    //   240	245	284	java/lang/Exception
    //   43	77	289	finally
    //   43	77	298	java/lang/Exception
  }

  // ERROR //
  public static void writeFile(String paramString, byte[] paramArrayOfByte)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: new 266	java/io/FileOutputStream
    //   5: dup
    //   6: aload_0
    //   7: invokespecial 267	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   10: astore_3
    //   11: aload_3
    //   12: aload_1
    //   13: invokevirtual 271	java/io/FileOutputStream:write	([B)V
    //   16: aload_3
    //   17: invokevirtual 274	java/io/FileOutputStream:close	()V
    //   20: return
    //   21: astore 9
    //   23: return
    //   24: astore 4
    //   26: ldc 143
    //   28: ldc_w 290
    //   31: aload 4
    //   33: invokestatic 286	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   36: pop
    //   37: aload_2
    //   38: invokevirtual 274	java/io/FileOutputStream:close	()V
    //   41: return
    //   42: astore 8
    //   44: return
    //   45: astore 5
    //   47: aload_2
    //   48: invokevirtual 274	java/io/FileOutputStream:close	()V
    //   51: aload 5
    //   53: athrow
    //   54: astore 6
    //   56: goto -5 -> 51
    //   59: astore 5
    //   61: aload_3
    //   62: astore_2
    //   63: goto -16 -> 47
    //   66: astore 4
    //   68: aload_3
    //   69: astore_2
    //   70: goto -44 -> 26
    //
    // Exception table:
    //   from	to	target	type
    //   16	20	21	java/lang/Exception
    //   2	11	24	java/lang/Exception
    //   37	41	42	java/lang/Exception
    //   2	11	45	finally
    //   26	37	45	finally
    //   47	51	54	java/lang/Exception
    //   11	16	59	finally
    //   11	16	66	java/lang/Exception
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.Storage
 * JD-Core Version:    0.5.4
 */