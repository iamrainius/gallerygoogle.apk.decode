package com.google.android.apps.lightcycle.util;

import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Build;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.impl.XMPDateTimeImpl;
import com.google.android.apps.lightcycle.xmp.XmpUtil;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

public class MetadataUtils
{
  private static String convertDoubleToDegreeFormat(double paramDouble)
  {
    String[] arrayOfString = Location.convert(Math.abs(paramDouble), 2).split(":");
    if (arrayOfString.length != 3)
      return null;
    int i = (int)(1000.0F * Float.valueOf(arrayOfString[2]).floatValue());
    return arrayOfString[0] + "/1," + arrayOfString[1] + "/1," + String.valueOf(i) + "/1000";
  }

  private static String getFirstJpegFileInDir(String paramString)
  {
    File[] arrayOfFile = new File(paramString).listFiles(new FilenameFilter()
    {
      public boolean accept(File paramFile, String paramString)
      {
        return paramString.toLowerCase().endsWith(".jpg");
      }
    });
    if (arrayOfFile.length > 0)
      return arrayOfFile[0].getAbsolutePath();
    return null;
  }

  // ERROR //
  private static Map<String, String> loadMetadataFromFile(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: new 84	java/io/BufferedReader
    //   5: dup
    //   6: new 86	java/io/InputStreamReader
    //   9: dup
    //   10: new 88	java/io/FileInputStream
    //   13: dup
    //   14: aload_0
    //   15: invokespecial 89	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   18: invokespecial 92	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   21: invokespecial 95	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   24: astore_2
    //   25: new 97	java/util/HashMap
    //   28: dup
    //   29: invokespecial 98	java/util/HashMap:<init>	()V
    //   32: astore_3
    //   33: aload_2
    //   34: invokevirtual 101	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   37: astore 11
    //   39: aload 11
    //   41: ifnull +58 -> 99
    //   44: aload 11
    //   46: ldc 103
    //   48: iconst_2
    //   49: invokevirtual 106	java/lang/String:split	(Ljava/lang/String;I)[Ljava/lang/String;
    //   52: astore 12
    //   54: aload 12
    //   56: arraylength
    //   57: iconst_2
    //   58: if_icmpne -25 -> 33
    //   61: aload_3
    //   62: aload 12
    //   64: iconst_0
    //   65: aaload
    //   66: aload 12
    //   68: iconst_1
    //   69: aaload
    //   70: invokevirtual 109	java/lang/String:trim	()Ljava/lang/String;
    //   73: invokeinterface 115 3 0
    //   78: pop
    //   79: goto -46 -> 33
    //   82: astore 8
    //   84: aload_2
    //   85: astore 9
    //   87: aload 9
    //   89: ifnull +8 -> 97
    //   92: aload 9
    //   94: invokevirtual 118	java/io/BufferedReader:close	()V
    //   97: aconst_null
    //   98: areturn
    //   99: aload_2
    //   100: ifnull +7 -> 107
    //   103: aload_2
    //   104: invokevirtual 118	java/io/BufferedReader:close	()V
    //   107: aload_3
    //   108: areturn
    //   109: astore 16
    //   111: aload_1
    //   112: ifnull +7 -> 119
    //   115: aload_1
    //   116: invokevirtual 118	java/io/BufferedReader:close	()V
    //   119: aconst_null
    //   120: areturn
    //   121: astore 6
    //   123: aload_1
    //   124: ifnull +7 -> 131
    //   127: aload_1
    //   128: invokevirtual 118	java/io/BufferedReader:close	()V
    //   131: aload 6
    //   133: athrow
    //   134: astore 14
    //   136: goto -29 -> 107
    //   139: astore 10
    //   141: goto -44 -> 97
    //   144: astore 5
    //   146: goto -27 -> 119
    //   149: astore 7
    //   151: goto -20 -> 131
    //   154: astore 6
    //   156: aload_2
    //   157: astore_1
    //   158: goto -35 -> 123
    //   161: astore 4
    //   163: aload_2
    //   164: astore_1
    //   165: goto -54 -> 111
    //   168: astore 15
    //   170: aconst_null
    //   171: astore 9
    //   173: goto -86 -> 87
    //
    // Exception table:
    //   from	to	target	type
    //   25	33	82	java/io/FileNotFoundException
    //   33	39	82	java/io/FileNotFoundException
    //   44	79	82	java/io/FileNotFoundException
    //   2	25	109	java/io/IOException
    //   2	25	121	finally
    //   103	107	134	java/io/IOException
    //   92	97	139	java/io/IOException
    //   115	119	144	java/io/IOException
    //   127	131	149	java/io/IOException
    //   25	33	154	finally
    //   33	39	154	finally
    //   44	79	154	finally
    //   25	33	161	java/io/IOException
    //   33	39	161	java/io/IOException
    //   44	79	161	java/io/IOException
    //   2	25	168	java/io/FileNotFoundException
  }

  private static void writeExif(String paramString1, String paramString2, Map<String, String> paramMap)
  {
    ExifInterface localExifInterface1;
    label164: Map.Entry localEntry;
    String str6;
    label229: String str5;
    while (true)
    {
      try
      {
        localExifInterface1 = new ExifInterface(paramString1);
        if (paramString2 == null)
          break label265;
        ExifInterface localExifInterface2 = new ExifInterface(paramString2);
        localExifInterface1.setAttribute("Make", localExifInterface2.getAttribute("Make"));
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(paramString1, localOptions);
        localExifInterface1.setAttribute("ImageWidth", String.valueOf(localOptions.outWidth));
        localExifInterface1.setAttribute("ImageLength", String.valueOf(localOptions.outHeight));
        Date localDate1 = new Date(System.currentTimeMillis());
        String str1 = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(localDate1);
        localExifInterface1.setAttribute("DateTime", str1);
        localExifInterface1.setAttribute("DateTimeOriginal", str1);
        localExifInterface1.setAttribute("DateTimeDigitized", str1);
        localExifInterface1.setAttribute("Model", Build.MODEL);
        if (paramMap == null)
          break label568;
        Iterator localIterator = paramMap.entrySet().iterator();
        if (!localIterator.hasNext())
          break label568;
        localEntry = (Map.Entry)localIterator.next();
        if (!((String)localEntry.getKey()).equals("location_altitude"))
          break label277;
        if (Double.valueOf((String)localEntry.getValue()).doubleValue() >= 0.0D)
          break label573;
        str6 = "1";
        localExifInterface1.setAttribute("GPSAltitudeRef", str6);
      }
      catch (IOException localIOException)
      {
        LG.d("Write exif failed :" + paramString1);
        return;
      }
      label265: localExifInterface1.setAttribute("Make", Build.MANUFACTURER);
      continue;
      label277: if (!((String)localEntry.getKey()).equals("location_latitude"))
        break;
      double d2 = Double.valueOf((String)localEntry.getValue()).doubleValue();
      String str4 = convertDoubleToDegreeFormat(d2);
      if (d2 < 0.0D)
        break label581;
      str5 = "N";
      label331: if (str4 == null)
        continue;
      localExifInterface1.setAttribute("GPSLatitude", str4);
      localExifInterface1.setAttribute("GPSLatitudeRef", str5);
    }
    String str2;
    if (((String)localEntry.getKey()).equals("location_longitude"))
    {
      double d1 = Double.valueOf((String)localEntry.getValue()).doubleValue();
      str2 = convertDoubleToDegreeFormat(d1);
      if (d1 < 0.0D)
        break label589;
    }
    for (String str3 = "E"; ; str3 = "W")
    {
      if (str2 != null);
      localExifInterface1.setAttribute("GPSLongitude", str2);
      localExifInterface1.setAttribute("GPSLongitudeRef", str3);
      break label164:
      if (((String)localEntry.getKey()).equals("location_provider"))
        localExifInterface1.setAttribute("GPSProcessingMethod", (String)localEntry.getValue());
      if (((String)localEntry.getKey()).equals("location_time"));
      Date localDate2 = new Date(Long.valueOf((String)localEntry.getValue()).longValue());
      localExifInterface1.setAttribute("GPSDateStamp", new SimpleDateFormat("yyyy:MM:dd").format(localDate2));
      localExifInterface1.setAttribute("GPSTimeStamp", new SimpleDateFormat("HH:mm:ss").format(localDate2));
      break label164:
      label568: localExifInterface1.saveAttributes();
      return;
      label573: str6 = "0";
      break label229:
      label581: str5 = "S";
      label589: break label331:
    }
  }

  // ERROR //
  public static boolean writeMetadataFile(String paramString, java.util.List<com.google.android.apps.lightcycle.storage.PhotoMetadata> paramList)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnull +12 -> 13
    //   4: aload_1
    //   5: invokeinterface 304 1 0
    //   10: ifne +5 -> 15
    //   13: iconst_0
    //   14: ireturn
    //   15: aconst_null
    //   16: astore_2
    //   17: new 306	java/io/FileWriter
    //   20: dup
    //   21: aload_0
    //   22: invokespecial 307	java/io/FileWriter:<init>	(Ljava/lang/String;)V
    //   25: astore_3
    //   26: iconst_2
    //   27: anewarray 4	java/lang/Object
    //   30: astore 8
    //   32: aload 8
    //   34: iconst_0
    //   35: ldc_w 309
    //   38: aastore
    //   39: aload 8
    //   41: iconst_1
    //   42: aload_1
    //   43: iconst_0
    //   44: invokeinterface 313 2 0
    //   49: checkcast 315	com/google/android/apps/lightcycle/storage/PhotoMetadata
    //   52: getfield 319	com/google/android/apps/lightcycle/storage/PhotoMetadata:timestamp	J
    //   55: invokestatic 322	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   58: aastore
    //   59: aload_3
    //   60: ldc_w 324
    //   63: aload 8
    //   65: invokestatic 327	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   68: invokevirtual 330	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   71: iconst_2
    //   72: anewarray 4	java/lang/Object
    //   75: astore 9
    //   77: aload 9
    //   79: iconst_0
    //   80: ldc_w 332
    //   83: aastore
    //   84: aload 9
    //   86: iconst_1
    //   87: aload_1
    //   88: iconst_m1
    //   89: aload_1
    //   90: invokeinterface 304 1 0
    //   95: iadd
    //   96: invokeinterface 313 2 0
    //   101: checkcast 315	com/google/android/apps/lightcycle/storage/PhotoMetadata
    //   104: getfield 319	com/google/android/apps/lightcycle/storage/PhotoMetadata:timestamp	J
    //   107: invokestatic 322	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   110: aastore
    //   111: aload_3
    //   112: ldc_w 324
    //   115: aload 9
    //   117: invokestatic 327	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   120: invokevirtual 330	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   123: iconst_2
    //   124: anewarray 4	java/lang/Object
    //   127: astore 10
    //   129: aload 10
    //   131: iconst_0
    //   132: ldc_w 334
    //   135: aastore
    //   136: aload 10
    //   138: iconst_1
    //   139: aload_1
    //   140: invokeinterface 304 1 0
    //   145: invokestatic 339	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   148: aastore
    //   149: aload_3
    //   150: ldc_w 324
    //   153: aload 10
    //   155: invokestatic 327	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   158: invokevirtual 330	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   161: iconst_2
    //   162: anewarray 4	java/lang/Object
    //   165: astore 11
    //   167: aload 11
    //   169: iconst_0
    //   170: ldc_w 341
    //   173: aastore
    //   174: aload 11
    //   176: iconst_1
    //   177: aload_1
    //   178: iconst_0
    //   179: invokeinterface 313 2 0
    //   184: checkcast 315	com/google/android/apps/lightcycle/storage/PhotoMetadata
    //   187: getfield 344	com/google/android/apps/lightcycle/storage/PhotoMetadata:poseHeading	I
    //   190: invokestatic 339	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   193: aastore
    //   194: aload_3
    //   195: ldc_w 324
    //   198: aload 11
    //   200: invokestatic 327	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   203: invokevirtual 330	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   206: iconst_m1
    //   207: aload_1
    //   208: invokeinterface 304 1 0
    //   213: iadd
    //   214: istore 12
    //   216: iload 12
    //   218: iflt +204 -> 422
    //   221: aload_1
    //   222: iload 12
    //   224: invokeinterface 313 2 0
    //   229: checkcast 315	com/google/android/apps/lightcycle/storage/PhotoMetadata
    //   232: getfield 348	com/google/android/apps/lightcycle/storage/PhotoMetadata:location	Landroid/location/Location;
    //   235: astore 13
    //   237: aload 13
    //   239: ifnull +193 -> 432
    //   242: iconst_2
    //   243: anewarray 4	java/lang/Object
    //   246: astore 14
    //   248: aload 14
    //   250: iconst_0
    //   251: ldc 217
    //   253: aastore
    //   254: aload 14
    //   256: iconst_1
    //   257: aload 13
    //   259: invokevirtual 351	android/location/Location:getAltitude	()D
    //   262: invokestatic 354	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   265: aastore
    //   266: aload_3
    //   267: ldc_w 356
    //   270: aload 14
    //   272: invokestatic 327	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   275: invokevirtual 330	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   278: iconst_2
    //   279: anewarray 4	java/lang/Object
    //   282: astore 15
    //   284: aload 15
    //   286: iconst_0
    //   287: ldc 249
    //   289: aastore
    //   290: aload 15
    //   292: iconst_1
    //   293: aload 13
    //   295: invokevirtual 359	android/location/Location:getLatitude	()D
    //   298: invokestatic 354	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   301: aastore
    //   302: aload_3
    //   303: ldc_w 356
    //   306: aload 15
    //   308: invokestatic 327	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   311: invokevirtual 330	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   314: iconst_2
    //   315: anewarray 4	java/lang/Object
    //   318: astore 16
    //   320: aload 16
    //   322: iconst_0
    //   323: ldc_w 259
    //   326: aastore
    //   327: aload 16
    //   329: iconst_1
    //   330: aload 13
    //   332: invokevirtual 362	android/location/Location:getLongitude	()D
    //   335: invokestatic 354	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   338: aastore
    //   339: aload_3
    //   340: ldc_w 356
    //   343: aload 16
    //   345: invokestatic 327	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   348: invokevirtual 330	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   351: iconst_2
    //   352: anewarray 4	java/lang/Object
    //   355: astore 17
    //   357: aload 17
    //   359: iconst_0
    //   360: ldc_w 267
    //   363: aastore
    //   364: aload 17
    //   366: iconst_1
    //   367: aload 13
    //   369: invokevirtual 365	android/location/Location:getProvider	()Ljava/lang/String;
    //   372: aastore
    //   373: aload_3
    //   374: ldc_w 367
    //   377: aload 17
    //   379: invokestatic 327	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   382: invokevirtual 330	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   385: iconst_2
    //   386: anewarray 4	java/lang/Object
    //   389: astore 18
    //   391: aload 18
    //   393: iconst_0
    //   394: ldc_w 271
    //   397: aastore
    //   398: aload 18
    //   400: iconst_1
    //   401: aload 13
    //   403: invokevirtual 370	android/location/Location:getTime	()J
    //   406: invokestatic 322	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   409: aastore
    //   410: aload_3
    //   411: ldc_w 324
    //   414: aload 18
    //   416: invokestatic 327	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   419: invokevirtual 330	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   422: aload_3
    //   423: ifnull +7 -> 430
    //   426: aload_3
    //   427: invokevirtual 371	java/io/FileWriter:close	()V
    //   430: iconst_1
    //   431: ireturn
    //   432: iinc 12 255
    //   435: goto -219 -> 216
    //   438: astore 20
    //   440: new 43	java/lang/StringBuilder
    //   443: dup
    //   444: invokespecial 44	java/lang/StringBuilder:<init>	()V
    //   447: ldc_w 373
    //   450: invokevirtual 48	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   453: aload_0
    //   454: invokevirtual 48	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   457: invokevirtual 59	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   460: invokestatic 244	com/google/android/apps/lightcycle/util/LG:d	(Ljava/lang/String;)V
    //   463: aload_2
    //   464: ifnull +7 -> 471
    //   467: aload_2
    //   468: invokevirtual 371	java/io/FileWriter:close	()V
    //   471: iconst_0
    //   472: ireturn
    //   473: astore 5
    //   475: aload_2
    //   476: ifnull +7 -> 483
    //   479: aload_2
    //   480: invokevirtual 371	java/io/FileWriter:close	()V
    //   483: aload 5
    //   485: athrow
    //   486: astore 19
    //   488: goto -58 -> 430
    //   491: astore 7
    //   493: goto -22 -> 471
    //   496: astore 6
    //   498: goto -15 -> 483
    //   501: astore 5
    //   503: aload_3
    //   504: astore_2
    //   505: goto -30 -> 475
    //   508: astore 4
    //   510: aload_3
    //   511: astore_2
    //   512: goto -72 -> 440
    //
    // Exception table:
    //   from	to	target	type
    //   17	26	438	java/io/IOException
    //   17	26	473	finally
    //   440	463	473	finally
    //   426	430	486	java/io/IOException
    //   467	471	491	java/io/IOException
    //   479	483	496	java/io/IOException
    //   26	216	501	finally
    //   221	237	501	finally
    //   242	422	501	finally
    //   26	216	508	java/io/IOException
    //   221	237	508	java/io/IOException
    //   242	422	508	java/io/IOException
  }

  public static void writeMetadataIntoJpegFile(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if ((paramString1 == null) || (!new File(paramString1).exists()))
      return;
    Map localMap = loadMetadataFromFile(paramString2);
    writeExif(paramString1, getFirstJpegFileInDir(paramString3), localMap);
    writeXmpMetadata(paramString1, paramBoolean, localMap);
  }

  private static void writeXmpMetadata(String paramString, boolean paramBoolean, Map<String, String> paramMap)
  {
    XMPMeta localXMPMeta = XmpUtil.createXMPMeta();
    do
    {
      while (true)
      {
        float f;
        Map.Entry localEntry;
        try
        {
          localXMPMeta.setPropertyBoolean("http://ns.google.com/photos/1.0/panorama/", "UsePanoramaViewer", paramBoolean);
          localXMPMeta.setProperty("http://ns.google.com/photos/1.0/panorama/", "ProjectionType", "equirectangular");
          if (paramMap == null)
            break label709;
          f = 0.0F;
          Iterator localIterator = paramMap.entrySet().iterator();
          if (!localIterator.hasNext())
            break label709;
          localEntry = (Map.Entry)localIterator.next();
          if (!((String)localEntry.getKey()).equals("full_pano_width"))
            break label181;
          localXMPMeta.setPropertyInteger("http://ns.google.com/photos/1.0/panorama/", "FullPanoWidthPixels", Integer.valueOf((String)localEntry.getValue()).intValue());
          f = (720.0F + f) % 360.0F;
          localXMPMeta.setPropertyDouble("http://ns.google.com/photos/1.0/panorama/", "PoseHeadingDegrees", f);
        }
        catch (XMPException localXMPException)
        {
          LG.d("Set xmp property failed:" + localXMPException.getLocalizedMessage());
          return;
        }
        if (((String)localEntry.getKey()).equals("full_pano_height"))
          label181: localXMPMeta.setPropertyInteger("http://ns.google.com/photos/1.0/panorama/", "FullPanoHeightPixels", Integer.valueOf((String)localEntry.getValue()).intValue());
        if (((String)localEntry.getKey()).equals("cropped_area_width"))
          localXMPMeta.setPropertyInteger("http://ns.google.com/photos/1.0/panorama/", "CroppedAreaImageWidthPixels", Integer.valueOf((String)localEntry.getValue()).intValue());
        if (((String)localEntry.getKey()).equals("cropped_area_height"))
          localXMPMeta.setPropertyInteger("http://ns.google.com/photos/1.0/panorama/", "CroppedAreaImageHeightPixels", Integer.valueOf((String)localEntry.getValue()).intValue());
        if (((String)localEntry.getKey()).equals("cropped_area_top"))
          localXMPMeta.setPropertyInteger("http://ns.google.com/photos/1.0/panorama/", "CroppedAreaTopPixels", Integer.valueOf((String)localEntry.getValue()).intValue());
        if (((String)localEntry.getKey()).equals("cropped_area_left"))
          localXMPMeta.setPropertyInteger("http://ns.google.com/photos/1.0/panorama/", "CroppedAreaLeftPixels", Integer.valueOf((String)localEntry.getValue()).intValue());
        if (((String)localEntry.getKey()).equals("first_photo_time"))
          localXMPMeta.setProperty("http://ns.google.com/photos/1.0/panorama/", "FirstPhotoDate", new XMPDateTimeImpl(new Date(Long.valueOf((String)localEntry.getValue()).longValue()), TimeZone.getTimeZone("GMT")));
        if (((String)localEntry.getKey()).equals("last_photo_time"))
          localXMPMeta.setPropertyDate("http://ns.google.com/photos/1.0/panorama/", "LastPhotoDate", new XMPDateTimeImpl(new Date(Long.valueOf((String)localEntry.getValue()).longValue()), TimeZone.getTimeZone("GMT")));
        if (((String)localEntry.getKey()).equals("source_photos_count"))
          localXMPMeta.setPropertyInteger("http://ns.google.com/photos/1.0/panorama/", "SourcePhotosCount", Integer.valueOf((String)localEntry.getValue()).intValue());
        if (((String)localEntry.getKey()).equals("pose_heading"))
          f += Integer.valueOf((String)localEntry.getValue()).intValue();
        if (!((String)localEntry.getKey()).equals("yaw_correction_deg"))
          continue;
        f += Integer.valueOf((String)localEntry.getValue()).intValue();
      }
      label709: BitmapFactory.Options localOptions = new BitmapFactory.Options();
      localOptions.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(paramString, localOptions);
      int i = localOptions.outWidth;
      int j = localOptions.outHeight;
      localXMPMeta.setPropertyInteger("http://ns.google.com/photos/1.0/panorama/", "LargestValidInteriorRectLeft", 0);
      localXMPMeta.setPropertyInteger("http://ns.google.com/photos/1.0/panorama/", "LargestValidInteriorRectTop", 0);
      localXMPMeta.setPropertyInteger("http://ns.google.com/photos/1.0/panorama/", "LargestValidInteriorRectWidth", i);
      localXMPMeta.setPropertyInteger("http://ns.google.com/photos/1.0/panorama/", "LargestValidInteriorRectHeight", j);
    }
    while (XmpUtil.writeXMPMeta(paramString, localXMPMeta));
    LG.d("Write XMP meta to file failed:" + paramString);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.util.MetadataUtils
 * JD-Core Version:    0.5.4
 */