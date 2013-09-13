package com.android.gallery3d.common;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.FloatMath;
import java.io.ByteArrayOutputStream;

public class BitmapUtils
{
  public static byte[] compressToBytes(Bitmap paramBitmap)
  {
    return compressToBytes(paramBitmap, 90);
  }

  public static byte[] compressToBytes(Bitmap paramBitmap, int paramInt)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(65536);
    paramBitmap.compress(Bitmap.CompressFormat.JPEG, paramInt, localByteArrayOutputStream);
    return localByteArrayOutputStream.toByteArray();
  }

  private static int computeInitialSampleSize(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = 1;
    if ((paramInt4 == -1) && (paramInt3 == -1))
      return i;
    if (paramInt4 == -1);
    while (true)
    {
      if (paramInt3 != -1);
      return Math.max(Math.min(paramInt1 / paramInt3, paramInt2 / paramInt3), i);
      i = (int)FloatMath.ceil(FloatMath.sqrt(paramInt1 * paramInt2 / paramInt4));
    }
  }

  public static int computeSampleSize(float paramFloat)
  {
    if (paramFloat > 0.0F);
    int i;
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      i = Math.max(1, (int)FloatMath.ceil(1.0F / paramFloat));
      if (i > 8)
        break;
      return Utils.nextPowerOf2(i);
    }
    return 8 * ((i + 7) / 8);
  }

  public static int computeSampleSize(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = computeInitialSampleSize(paramInt1, paramInt2, paramInt3, paramInt4);
    if (i <= 8)
      return Utils.nextPowerOf2(i);
    return 8 * ((i + 7) / 8);
  }

  public static int computeSampleSizeLarger(float paramFloat)
  {
    int i = (int)FloatMath.floor(1.0F / paramFloat);
    if (i <= 1)
      return 1;
    if (i <= 8)
      return Utils.prevPowerOf2(i);
    return 8 * (i / 8);
  }

  public static int computeSampleSizeLarger(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = Math.max(paramInt1 / paramInt3, paramInt2 / paramInt3);
    if (i <= 1)
      return 1;
    if (i <= 8)
      return Utils.prevPowerOf2(i);
    return 8 * (i / 8);
  }

  // ERROR //
  public static Bitmap createVideoThumbnail(String paramString)
  {
    // Byte code:
    //   0: ldc 97
    //   2: invokestatic 103	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   5: astore 34
    //   7: aload 34
    //   9: astore_3
    //   10: aload_3
    //   11: invokevirtual 107	java/lang/Class:newInstance	()Ljava/lang/Object;
    //   14: astore 37
    //   16: aload 37
    //   18: astore_2
    //   19: aload_3
    //   20: ldc 109
    //   22: iconst_1
    //   23: anewarray 99	java/lang/Class
    //   26: dup
    //   27: iconst_0
    //   28: ldc 111
    //   30: aastore
    //   31: invokevirtual 115	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   34: aload_2
    //   35: iconst_1
    //   36: anewarray 4	java/lang/Object
    //   39: dup
    //   40: iconst_0
    //   41: aload_0
    //   42: aastore
    //   43: invokevirtual 121	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   46: pop
    //   47: getstatic 127	android/os/Build$VERSION:SDK_INT	I
    //   50: bipush 9
    //   52: if_icmpgt +52 -> 104
    //   55: aload_3
    //   56: ldc 129
    //   58: iconst_0
    //   59: anewarray 99	java/lang/Class
    //   62: invokevirtual 115	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   65: aload_2
    //   66: iconst_0
    //   67: anewarray 4	java/lang/Object
    //   70: invokevirtual 121	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   73: checkcast 27	android/graphics/Bitmap
    //   76: astore 43
    //   78: aload_2
    //   79: ifnull +22 -> 101
    //   82: aload_3
    //   83: ldc 131
    //   85: iconst_0
    //   86: anewarray 99	java/lang/Class
    //   89: invokevirtual 115	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   92: aload_2
    //   93: iconst_0
    //   94: anewarray 4	java/lang/Object
    //   97: invokevirtual 121	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   100: pop
    //   101: aload 43
    //   103: areturn
    //   104: aload_3
    //   105: ldc 133
    //   107: iconst_0
    //   108: anewarray 99	java/lang/Class
    //   111: invokevirtual 115	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   114: aload_2
    //   115: iconst_0
    //   116: anewarray 4	java/lang/Object
    //   119: invokevirtual 121	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   122: checkcast 135	[B
    //   125: checkcast 135	[B
    //   128: astore 41
    //   130: aload 41
    //   132: ifnull +54 -> 186
    //   135: aload 41
    //   137: iconst_0
    //   138: aload 41
    //   140: arraylength
    //   141: invokestatic 141	android/graphics/BitmapFactory:decodeByteArray	([BII)Landroid/graphics/Bitmap;
    //   144: astore 42
    //   146: aload 42
    //   148: astore 43
    //   150: aload 43
    //   152: ifnull +34 -> 186
    //   155: aload_2
    //   156: ifnull -55 -> 101
    //   159: aload_3
    //   160: ldc 131
    //   162: iconst_0
    //   163: anewarray 99	java/lang/Class
    //   166: invokevirtual 115	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   169: aload_2
    //   170: iconst_0
    //   171: anewarray 4	java/lang/Object
    //   174: invokevirtual 121	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   177: pop
    //   178: aload 43
    //   180: areturn
    //   181: astore 46
    //   183: aload 43
    //   185: areturn
    //   186: aload_3
    //   187: ldc 143
    //   189: iconst_0
    //   190: anewarray 99	java/lang/Class
    //   193: invokevirtual 115	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   196: aload_2
    //   197: iconst_0
    //   198: anewarray 4	java/lang/Object
    //   201: invokevirtual 121	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   204: checkcast 27	android/graphics/Bitmap
    //   207: astore 43
    //   209: aload_2
    //   210: ifnull -109 -> 101
    //   213: aload_3
    //   214: ldc 131
    //   216: iconst_0
    //   217: anewarray 99	java/lang/Class
    //   220: invokevirtual 115	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   223: aload_2
    //   224: iconst_0
    //   225: anewarray 4	java/lang/Object
    //   228: invokevirtual 121	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   231: pop
    //   232: aload 43
    //   234: areturn
    //   235: astore 44
    //   237: aload 43
    //   239: areturn
    //   240: astore 29
    //   242: aconst_null
    //   243: astore 30
    //   245: aconst_null
    //   246: astore 31
    //   248: aload 30
    //   250: ifnull +24 -> 274
    //   253: aload 31
    //   255: ldc 131
    //   257: iconst_0
    //   258: anewarray 99	java/lang/Class
    //   261: invokevirtual 115	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   264: aload 30
    //   266: iconst_0
    //   267: anewarray 4	java/lang/Object
    //   270: invokevirtual 121	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   273: pop
    //   274: aconst_null
    //   275: areturn
    //   276: astore 26
    //   278: aconst_null
    //   279: astore_2
    //   280: aconst_null
    //   281: astore_3
    //   282: aload_2
    //   283: ifnull -9 -> 274
    //   286: aload_3
    //   287: ldc 131
    //   289: iconst_0
    //   290: anewarray 99	java/lang/Class
    //   293: invokevirtual 115	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   296: aload_2
    //   297: iconst_0
    //   298: anewarray 4	java/lang/Object
    //   301: invokevirtual 121	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   304: pop
    //   305: goto -31 -> 274
    //   308: astore 27
    //   310: goto -36 -> 274
    //   313: astore 22
    //   315: aconst_null
    //   316: astore_2
    //   317: aconst_null
    //   318: astore_3
    //   319: ldc 145
    //   321: ldc 146
    //   323: aload 22
    //   325: invokestatic 152	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   328: pop
    //   329: aload_2
    //   330: ifnull -56 -> 274
    //   333: aload_3
    //   334: ldc 131
    //   336: iconst_0
    //   337: anewarray 99	java/lang/Class
    //   340: invokevirtual 115	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   343: aload_2
    //   344: iconst_0
    //   345: anewarray 4	java/lang/Object
    //   348: invokevirtual 121	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   351: pop
    //   352: goto -78 -> 274
    //   355: astore 24
    //   357: goto -83 -> 274
    //   360: astore 18
    //   362: aconst_null
    //   363: astore_2
    //   364: aconst_null
    //   365: astore_3
    //   366: ldc 145
    //   368: ldc 146
    //   370: aload 18
    //   372: invokestatic 152	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   375: pop
    //   376: aload_2
    //   377: ifnull -103 -> 274
    //   380: aload_3
    //   381: ldc 131
    //   383: iconst_0
    //   384: anewarray 99	java/lang/Class
    //   387: invokevirtual 115	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   390: aload_2
    //   391: iconst_0
    //   392: anewarray 4	java/lang/Object
    //   395: invokevirtual 121	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   398: pop
    //   399: goto -125 -> 274
    //   402: astore 20
    //   404: goto -130 -> 274
    //   407: astore 14
    //   409: aconst_null
    //   410: astore_2
    //   411: aconst_null
    //   412: astore_3
    //   413: ldc 145
    //   415: ldc 146
    //   417: aload 14
    //   419: invokestatic 152	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   422: pop
    //   423: aload_2
    //   424: ifnull -150 -> 274
    //   427: aload_3
    //   428: ldc 131
    //   430: iconst_0
    //   431: anewarray 99	java/lang/Class
    //   434: invokevirtual 115	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   437: aload_2
    //   438: iconst_0
    //   439: anewarray 4	java/lang/Object
    //   442: invokevirtual 121	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   445: pop
    //   446: goto -172 -> 274
    //   449: astore 16
    //   451: goto -177 -> 274
    //   454: astore 10
    //   456: aconst_null
    //   457: astore_2
    //   458: aconst_null
    //   459: astore_3
    //   460: ldc 145
    //   462: ldc 146
    //   464: aload 10
    //   466: invokestatic 152	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   469: pop
    //   470: aload_2
    //   471: ifnull -197 -> 274
    //   474: aload_3
    //   475: ldc 131
    //   477: iconst_0
    //   478: anewarray 99	java/lang/Class
    //   481: invokevirtual 115	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   484: aload_2
    //   485: iconst_0
    //   486: anewarray 4	java/lang/Object
    //   489: invokevirtual 121	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   492: pop
    //   493: goto -219 -> 274
    //   496: astore 12
    //   498: goto -224 -> 274
    //   501: astore 6
    //   503: aconst_null
    //   504: astore_2
    //   505: aconst_null
    //   506: astore_3
    //   507: ldc 145
    //   509: ldc 146
    //   511: aload 6
    //   513: invokestatic 152	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   516: pop
    //   517: aload_2
    //   518: ifnull -244 -> 274
    //   521: aload_3
    //   522: ldc 131
    //   524: iconst_0
    //   525: anewarray 99	java/lang/Class
    //   528: invokevirtual 115	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   531: aload_2
    //   532: iconst_0
    //   533: anewarray 4	java/lang/Object
    //   536: invokevirtual 121	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   539: pop
    //   540: goto -266 -> 274
    //   543: astore 8
    //   545: goto -271 -> 274
    //   548: astore_1
    //   549: aconst_null
    //   550: astore_2
    //   551: aconst_null
    //   552: astore_3
    //   553: aload_2
    //   554: ifnull +22 -> 576
    //   557: aload_3
    //   558: ldc 131
    //   560: iconst_0
    //   561: anewarray 99	java/lang/Class
    //   564: invokevirtual 115	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   567: aload_2
    //   568: iconst_0
    //   569: anewarray 4	java/lang/Object
    //   572: invokevirtual 121	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   575: pop
    //   576: aload_1
    //   577: athrow
    //   578: astore 4
    //   580: goto -4 -> 576
    //   583: astore_1
    //   584: aconst_null
    //   585: astore_2
    //   586: goto -33 -> 553
    //   589: astore_1
    //   590: goto -37 -> 553
    //   593: astore 6
    //   595: aconst_null
    //   596: astore_2
    //   597: goto -90 -> 507
    //   600: astore 6
    //   602: goto -95 -> 507
    //   605: astore 10
    //   607: aconst_null
    //   608: astore_2
    //   609: goto -149 -> 460
    //   612: astore 10
    //   614: goto -154 -> 460
    //   617: astore 14
    //   619: aconst_null
    //   620: astore_2
    //   621: goto -208 -> 413
    //   624: astore 14
    //   626: goto -213 -> 413
    //   629: astore 18
    //   631: aconst_null
    //   632: astore_2
    //   633: goto -267 -> 366
    //   636: astore 18
    //   638: goto -272 -> 366
    //   641: astore 22
    //   643: aconst_null
    //   644: astore_2
    //   645: goto -326 -> 319
    //   648: astore 22
    //   650: goto -331 -> 319
    //   653: astore 36
    //   655: aconst_null
    //   656: astore_2
    //   657: goto -375 -> 282
    //   660: astore 39
    //   662: goto -380 -> 282
    //   665: astore 32
    //   667: goto -393 -> 274
    //   670: astore 35
    //   672: aload_3
    //   673: astore 31
    //   675: aconst_null
    //   676: astore 30
    //   678: goto -430 -> 248
    //   681: astore 38
    //   683: aload_2
    //   684: astore 30
    //   686: aload_3
    //   687: astore 31
    //   689: goto -441 -> 248
    //   692: astore 48
    //   694: aload 43
    //   696: areturn
    //
    // Exception table:
    //   from	to	target	type
    //   159	178	181	java/lang/Exception
    //   213	232	235	java/lang/Exception
    //   0	7	240	java/lang/IllegalArgumentException
    //   0	7	276	java/lang/RuntimeException
    //   286	305	308	java/lang/Exception
    //   0	7	313	java/lang/InstantiationException
    //   333	352	355	java/lang/Exception
    //   0	7	360	java/lang/reflect/InvocationTargetException
    //   380	399	402	java/lang/Exception
    //   0	7	407	java/lang/ClassNotFoundException
    //   427	446	449	java/lang/Exception
    //   0	7	454	java/lang/NoSuchMethodException
    //   474	493	496	java/lang/Exception
    //   0	7	501	java/lang/IllegalAccessException
    //   521	540	543	java/lang/Exception
    //   0	7	548	finally
    //   557	576	578	java/lang/Exception
    //   10	16	583	finally
    //   19	78	589	finally
    //   104	130	589	finally
    //   135	146	589	finally
    //   186	209	589	finally
    //   319	329	589	finally
    //   366	376	589	finally
    //   413	423	589	finally
    //   460	470	589	finally
    //   507	517	589	finally
    //   10	16	593	java/lang/IllegalAccessException
    //   19	78	600	java/lang/IllegalAccessException
    //   104	130	600	java/lang/IllegalAccessException
    //   135	146	600	java/lang/IllegalAccessException
    //   186	209	600	java/lang/IllegalAccessException
    //   10	16	605	java/lang/NoSuchMethodException
    //   19	78	612	java/lang/NoSuchMethodException
    //   104	130	612	java/lang/NoSuchMethodException
    //   135	146	612	java/lang/NoSuchMethodException
    //   186	209	612	java/lang/NoSuchMethodException
    //   10	16	617	java/lang/ClassNotFoundException
    //   19	78	624	java/lang/ClassNotFoundException
    //   104	130	624	java/lang/ClassNotFoundException
    //   135	146	624	java/lang/ClassNotFoundException
    //   186	209	624	java/lang/ClassNotFoundException
    //   10	16	629	java/lang/reflect/InvocationTargetException
    //   19	78	636	java/lang/reflect/InvocationTargetException
    //   104	130	636	java/lang/reflect/InvocationTargetException
    //   135	146	636	java/lang/reflect/InvocationTargetException
    //   186	209	636	java/lang/reflect/InvocationTargetException
    //   10	16	641	java/lang/InstantiationException
    //   19	78	648	java/lang/InstantiationException
    //   104	130	648	java/lang/InstantiationException
    //   135	146	648	java/lang/InstantiationException
    //   186	209	648	java/lang/InstantiationException
    //   10	16	653	java/lang/RuntimeException
    //   19	78	660	java/lang/RuntimeException
    //   104	130	660	java/lang/RuntimeException
    //   135	146	660	java/lang/RuntimeException
    //   186	209	660	java/lang/RuntimeException
    //   253	274	665	java/lang/Exception
    //   10	16	670	java/lang/IllegalArgumentException
    //   19	78	681	java/lang/IllegalArgumentException
    //   104	130	681	java/lang/IllegalArgumentException
    //   135	146	681	java/lang/IllegalArgumentException
    //   186	209	681	java/lang/IllegalArgumentException
    //   82	101	692	java/lang/Exception
  }

  private static Bitmap.Config getConfig(Bitmap paramBitmap)
  {
    Bitmap.Config localConfig = paramBitmap.getConfig();
    if (localConfig == null)
      localConfig = Bitmap.Config.ARGB_8888;
    return localConfig;
  }

  public static boolean isRotationSupported(String paramString)
  {
    if (paramString == null)
      return false;
    return paramString.toLowerCase().equals("image/jpeg");
  }

  public static boolean isSupportedByRegionDecoder(String paramString)
  {
    if (paramString == null);
    String str;
    do
    {
      return false;
      str = paramString.toLowerCase();
    }
    while ((!str.startsWith("image/")) || (str.equals("image/gif")) || (str.endsWith("bmp")));
    return true;
  }

  public static Bitmap resizeAndCropCenter(Bitmap paramBitmap, int paramInt, boolean paramBoolean)
  {
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    if ((i == paramInt) && (j == paramInt))
      return paramBitmap;
    float f = paramInt / Math.min(i, j);
    Bitmap localBitmap = Bitmap.createBitmap(paramInt, paramInt, getConfig(paramBitmap));
    int k = Math.round(f * paramBitmap.getWidth());
    int l = Math.round(f * paramBitmap.getHeight());
    Canvas localCanvas = new Canvas(localBitmap);
    localCanvas.translate((paramInt - k) / 2.0F, (paramInt - l) / 2.0F);
    localCanvas.scale(f, f);
    localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, new Paint(6));
    if (paramBoolean)
      paramBitmap.recycle();
    return localBitmap;
  }

  public static Bitmap resizeBitmapByScale(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    int i = Math.round(paramFloat * paramBitmap.getWidth());
    int j = Math.round(paramFloat * paramBitmap.getHeight());
    if ((i == paramBitmap.getWidth()) && (j == paramBitmap.getHeight()))
      return paramBitmap;
    Bitmap localBitmap = Bitmap.createBitmap(i, j, getConfig(paramBitmap));
    Canvas localCanvas = new Canvas(localBitmap);
    localCanvas.scale(paramFloat, paramFloat);
    localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, new Paint(6));
    if (paramBoolean)
      paramBitmap.recycle();
    return localBitmap;
  }

  public static Bitmap resizeDownBySideLength(Bitmap paramBitmap, int paramInt, boolean paramBoolean)
  {
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    float f = Math.min(paramInt / i, paramInt / j);
    if (f >= 1.0F)
      return paramBitmap;
    return resizeBitmapByScale(paramBitmap, f, paramBoolean);
  }

  public static Bitmap rotateBitmap(Bitmap paramBitmap, int paramInt, boolean paramBoolean)
  {
    if (paramInt == 0)
      return paramBitmap;
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    Matrix localMatrix = new Matrix();
    localMatrix.postRotate(paramInt);
    Bitmap localBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, i, j, localMatrix, true);
    if (paramBoolean)
      paramBitmap.recycle();
    return localBitmap;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.common.BitmapUtils
 * JD-Core Version:    0.5.4
 */