package com.google.android.apps.lightcycle.xmp;

import android.util.Log;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.options.SerializeOptions;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XmpUtil
{
  static
  {
    try
    {
      XMPMetaFactory.getSchemaRegistry().registerNamespace("http://ns.google.com/photos/1.0/panorama/", "GPano");
      return;
    }
    catch (XMPException localXMPException)
    {
      localXMPException.printStackTrace();
    }
  }

  public static XMPMeta createXMPMeta()
  {
    return XMPMetaFactory.create();
  }

  public static XMPMeta extractXMPMeta(InputStream paramInputStream)
  {
    List localList = parse(paramInputStream, true);
    if (localList == null)
      return null;
    Iterator localIterator = localList.iterator();
    Section localSection;
    do
    {
      if (localIterator.hasNext());
      localSection = (Section)localIterator.next();
    }
    while (!hasXMPHeader(localSection.data));
    byte[] arrayOfByte = new byte[-29 + localSection.data.length];
    System.arraycopy(localSection.data, 29, arrayOfByte, 0, arrayOfByte.length);
    try
    {
      XMPMeta localXMPMeta = XMPMetaFactory.parseFromBuffer(arrayOfByte);
      return localXMPMeta;
    }
    catch (XMPException localXMPException)
    {
      Log.d("XmpUtil", "XMP parse error", localXMPException);
    }
    return null;
  }

  private static boolean hasXMPHeader(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length < 29)
      return false;
    try
    {
      byte[] arrayOfByte = new byte[29];
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, 29);
      boolean bool = new String(arrayOfByte, "UTF-8").equals("");
      if (bool);
      return true;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
    }
    return false;
  }

  private static List<Section> insertXMPSection(List<Section> paramList, XMPMeta paramXMPMeta)
  {
    int i = 1;
    if ((paramList == null) || (paramList.size() <= i))
      return null;
    byte[] arrayOfByte1;
    try
    {
      SerializeOptions localSerializeOptions = new SerializeOptions();
      localSerializeOptions.setUseCompactFormat(true);
      localSerializeOptions.setOmitPacketWrapper(true);
      arrayOfByte1 = XMPMetaFactory.serializeToBuffer(paramXMPMeta, localSerializeOptions);
      if (arrayOfByte1.length > 65502)
        return null;
    }
    catch (XMPException localXMPException)
    {
      Log.d("XmpUtil", "Serialize xmp failed", localXMPException);
      return null;
    }
    byte[] arrayOfByte2 = new byte[29 + arrayOfByte1.length];
    System.arraycopy("".getBytes(), 0, arrayOfByte2, 0, 29);
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 29, arrayOfByte1.length);
    Section localSection = new Section(null);
    localSection.marker = 225;
    localSection.length = (2 + arrayOfByte2.length);
    localSection.data = arrayOfByte2;
    for (int j = 0; j < paramList.size(); ++j)
    {
      if ((((Section)paramList.get(j)).marker != 225) || (!hasXMPHeader(((Section)paramList.get(j)).data)))
        continue;
      paramList.set(j, localSection);
      return paramList;
    }
    ArrayList localArrayList = new ArrayList();
    if (((Section)paramList.get(0)).marker == 225);
    while (true)
    {
      localArrayList.addAll(paramList.subList(0, i));
      localArrayList.add(localSection);
      localArrayList.addAll(paramList.subList(i, paramList.size()));
      return localArrayList;
      i = 0;
    }
  }

  // ERROR //
  private static List<Section> parse(InputStream paramInputStream, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 168	java/io/InputStream:read	()I
    //   4: sipush 255
    //   7: if_icmpne +17 -> 24
    //   10: aload_0
    //   11: invokevirtual 168	java/io/InputStream:read	()I
    //   14: istore 9
    //   16: iload 9
    //   18: sipush 216
    //   21: if_icmpeq +17 -> 38
    //   24: aload_0
    //   25: ifnull +7 -> 32
    //   28: aload_0
    //   29: invokevirtual 171	java/io/InputStream:close	()V
    //   32: aconst_null
    //   33: astore 7
    //   35: aload 7
    //   37: areturn
    //   38: new 149	java/util/ArrayList
    //   41: dup
    //   42: invokespecial 150	java/util/ArrayList:<init>	()V
    //   45: astore 7
    //   47: aload_0
    //   48: invokevirtual 168	java/io/InputStream:read	()I
    //   51: istore 10
    //   53: iload 10
    //   55: iconst_m1
    //   56: if_icmpeq +309 -> 365
    //   59: iload 10
    //   61: sipush 255
    //   64: if_icmpeq +13 -> 77
    //   67: aload_0
    //   68: ifnull +7 -> 75
    //   71: aload_0
    //   72: invokevirtual 171	java/io/InputStream:close	()V
    //   75: aconst_null
    //   76: areturn
    //   77: aload_0
    //   78: invokevirtual 168	java/io/InputStream:read	()I
    //   81: istore 12
    //   83: iload 12
    //   85: sipush 255
    //   88: if_icmpeq -11 -> 77
    //   91: iload 12
    //   93: iconst_m1
    //   94: if_icmpne +13 -> 107
    //   97: aload_0
    //   98: ifnull +7 -> 105
    //   101: aload_0
    //   102: invokevirtual 171	java/io/InputStream:close	()V
    //   105: aconst_null
    //   106: areturn
    //   107: iload 12
    //   109: sipush 218
    //   112: if_icmpne +84 -> 196
    //   115: iload_1
    //   116: ifne +64 -> 180
    //   119: new 59	com/google/android/apps/lightcycle/xmp/XmpUtil$Section
    //   122: dup
    //   123: aconst_null
    //   124: invokespecial 132	com/google/android/apps/lightcycle/xmp/XmpUtil$Section:<init>	(Lcom/google/android/apps/lightcycle/xmp/XmpUtil$1;)V
    //   127: astore 25
    //   129: aload 25
    //   131: iload 12
    //   133: putfield 136	com/google/android/apps/lightcycle/xmp/XmpUtil$Section:marker	I
    //   136: aload 25
    //   138: iconst_m1
    //   139: putfield 139	com/google/android/apps/lightcycle/xmp/XmpUtil$Section:length	I
    //   142: aload 25
    //   144: aload_0
    //   145: invokevirtual 174	java/io/InputStream:available	()I
    //   148: newarray byte
    //   150: putfield 63	com/google/android/apps/lightcycle/xmp/XmpUtil$Section:data	[B
    //   153: aload_0
    //   154: aload 25
    //   156: getfield 63	com/google/android/apps/lightcycle/xmp/XmpUtil$Section:data	[B
    //   159: iconst_0
    //   160: aload 25
    //   162: getfield 63	com/google/android/apps/lightcycle/xmp/XmpUtil$Section:data	[B
    //   165: arraylength
    //   166: invokevirtual 177	java/io/InputStream:read	([BII)I
    //   169: pop
    //   170: aload 7
    //   172: aload 25
    //   174: invokeinterface 161 2 0
    //   179: pop
    //   180: aload_0
    //   181: ifnull -146 -> 35
    //   184: aload_0
    //   185: invokevirtual 171	java/io/InputStream:close	()V
    //   188: aload 7
    //   190: areturn
    //   191: astore 24
    //   193: aload 7
    //   195: areturn
    //   196: aload_0
    //   197: invokevirtual 168	java/io/InputStream:read	()I
    //   200: istore 13
    //   202: aload_0
    //   203: invokevirtual 168	java/io/InputStream:read	()I
    //   206: istore 14
    //   208: iload 13
    //   210: iconst_m1
    //   211: if_icmpeq +9 -> 220
    //   214: iload 14
    //   216: iconst_m1
    //   217: if_icmpne +13 -> 230
    //   220: aload_0
    //   221: ifnull +7 -> 228
    //   224: aload_0
    //   225: invokevirtual 171	java/io/InputStream:close	()V
    //   228: aconst_null
    //   229: areturn
    //   230: iload 14
    //   232: iload 13
    //   234: bipush 8
    //   236: ishl
    //   237: ior
    //   238: istore 16
    //   240: iload_1
    //   241: ifeq +11 -> 252
    //   244: iload 12
    //   246: sipush 225
    //   249: if_icmpne +88 -> 337
    //   252: new 59	com/google/android/apps/lightcycle/xmp/XmpUtil$Section
    //   255: dup
    //   256: aconst_null
    //   257: invokespecial 132	com/google/android/apps/lightcycle/xmp/XmpUtil$Section:<init>	(Lcom/google/android/apps/lightcycle/xmp/XmpUtil$1;)V
    //   260: astore 17
    //   262: aload 17
    //   264: iload 12
    //   266: putfield 136	com/google/android/apps/lightcycle/xmp/XmpUtil$Section:marker	I
    //   269: aload 17
    //   271: iload 16
    //   273: putfield 139	com/google/android/apps/lightcycle/xmp/XmpUtil$Section:length	I
    //   276: aload 17
    //   278: iload 16
    //   280: iconst_2
    //   281: isub
    //   282: newarray byte
    //   284: putfield 63	com/google/android/apps/lightcycle/xmp/XmpUtil$Section:data	[B
    //   287: aload_0
    //   288: aload 17
    //   290: getfield 63	com/google/android/apps/lightcycle/xmp/XmpUtil$Section:data	[B
    //   293: iconst_0
    //   294: iload 16
    //   296: iconst_2
    //   297: isub
    //   298: invokevirtual 177	java/io/InputStream:read	([BII)I
    //   301: pop
    //   302: aload 7
    //   304: aload 17
    //   306: invokeinterface 161 2 0
    //   311: pop
    //   312: goto -265 -> 47
    //   315: astore 4
    //   317: ldc 79
    //   319: ldc 179
    //   321: aload 4
    //   323: invokestatic 87	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   326: pop
    //   327: aload_0
    //   328: ifnull +7 -> 335
    //   331: aload_0
    //   332: invokevirtual 171	java/io/InputStream:close	()V
    //   335: aconst_null
    //   336: areturn
    //   337: iload 16
    //   339: iconst_2
    //   340: isub
    //   341: i2l
    //   342: lstore 20
    //   344: aload_0
    //   345: lload 20
    //   347: invokevirtual 183	java/io/InputStream:skip	(J)J
    //   350: pop2
    //   351: goto -304 -> 47
    //   354: astore_2
    //   355: aload_0
    //   356: ifnull +7 -> 363
    //   359: aload_0
    //   360: invokevirtual 171	java/io/InputStream:close	()V
    //   363: aload_2
    //   364: athrow
    //   365: aload_0
    //   366: ifnull -331 -> 35
    //   369: aload_0
    //   370: invokevirtual 171	java/io/InputStream:close	()V
    //   373: aload 7
    //   375: areturn
    //   376: astore 11
    //   378: aload 7
    //   380: areturn
    //   381: astore 8
    //   383: goto -351 -> 32
    //   386: astore 29
    //   388: goto -313 -> 75
    //   391: astore 28
    //   393: goto -288 -> 105
    //   396: astore 15
    //   398: goto -170 -> 228
    //   401: astore 6
    //   403: goto -68 -> 335
    //   406: astore_3
    //   407: goto -44 -> 363
    //
    // Exception table:
    //   from	to	target	type
    //   184	188	191	java/io/IOException
    //   0	16	315	java/io/IOException
    //   38	47	315	java/io/IOException
    //   47	53	315	java/io/IOException
    //   77	83	315	java/io/IOException
    //   119	180	315	java/io/IOException
    //   196	208	315	java/io/IOException
    //   252	312	315	java/io/IOException
    //   344	351	315	java/io/IOException
    //   0	16	354	finally
    //   38	47	354	finally
    //   47	53	354	finally
    //   77	83	354	finally
    //   119	180	354	finally
    //   196	208	354	finally
    //   252	312	354	finally
    //   317	327	354	finally
    //   344	351	354	finally
    //   369	373	376	java/io/IOException
    //   28	32	381	java/io/IOException
    //   71	75	386	java/io/IOException
    //   101	105	391	java/io/IOException
    //   224	228	396	java/io/IOException
    //   331	335	401	java/io/IOException
    //   359	363	406	java/io/IOException
  }

  // ERROR //
  public static boolean writeXMPMeta(String paramString, XMPMeta paramXMPMeta)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 191	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   4: ldc 193
    //   6: invokevirtual 197	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   9: ifne +22 -> 31
    //   12: aload_0
    //   13: invokevirtual 191	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   16: ldc 199
    //   18: invokevirtual 197	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   21: ifne +10 -> 31
    //   24: ldc 201
    //   26: invokestatic 206	com/google/android/apps/lightcycle/util/LG:d	(Ljava/lang/String;)V
    //   29: iconst_0
    //   30: ireturn
    //   31: new 208	java/io/FileInputStream
    //   34: dup
    //   35: aload_0
    //   36: invokespecial 210	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   39: iconst_0
    //   40: invokestatic 41	com/google/android/apps/lightcycle/xmp/XmpUtil:parse	(Ljava/io/InputStream;Z)Ljava/util/List;
    //   43: aload_1
    //   44: invokestatic 212	com/google/android/apps/lightcycle/xmp/XmpUtil:insertXMPSection	(Ljava/util/List;Lcom/adobe/xmp/XMPMeta;)Ljava/util/List;
    //   47: astore 4
    //   49: aload 4
    //   51: ifnull -22 -> 29
    //   54: aconst_null
    //   55: astore 5
    //   57: new 214	java/io/FileOutputStream
    //   60: dup
    //   61: aload_0
    //   62: invokespecial 215	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   65: astore 6
    //   67: aload 6
    //   69: sipush 255
    //   72: invokevirtual 219	java/io/FileOutputStream:write	(I)V
    //   75: aload 6
    //   77: sipush 216
    //   80: invokevirtual 219	java/io/FileOutputStream:write	(I)V
    //   83: aload 4
    //   85: invokeinterface 47 1 0
    //   90: astore 12
    //   92: aload 12
    //   94: invokeinterface 53 1 0
    //   99: ifeq +167 -> 266
    //   102: aload 12
    //   104: invokeinterface 57 1 0
    //   109: checkcast 59	com/google/android/apps/lightcycle/xmp/XmpUtil$Section
    //   112: astore 14
    //   114: aload 6
    //   116: sipush 255
    //   119: invokevirtual 219	java/io/FileOutputStream:write	(I)V
    //   122: aload 6
    //   124: aload 14
    //   126: getfield 136	com/google/android/apps/lightcycle/xmp/XmpUtil$Section:marker	I
    //   129: invokevirtual 219	java/io/FileOutputStream:write	(I)V
    //   132: aload 14
    //   134: getfield 139	com/google/android/apps/lightcycle/xmp/XmpUtil$Section:length	I
    //   137: ifle +38 -> 175
    //   140: aload 14
    //   142: getfield 139	com/google/android/apps/lightcycle/xmp/XmpUtil$Section:length	I
    //   145: bipush 8
    //   147: ishr
    //   148: istore 15
    //   150: sipush 255
    //   153: aload 14
    //   155: getfield 139	com/google/android/apps/lightcycle/xmp/XmpUtil$Section:length	I
    //   158: iand
    //   159: istore 16
    //   161: aload 6
    //   163: iload 15
    //   165: invokevirtual 219	java/io/FileOutputStream:write	(I)V
    //   168: aload 6
    //   170: iload 16
    //   172: invokevirtual 219	java/io/FileOutputStream:write	(I)V
    //   175: aload 6
    //   177: aload 14
    //   179: getfield 63	com/google/android/apps/lightcycle/xmp/XmpUtil$Section:data	[B
    //   182: invokevirtual 222	java/io/FileOutputStream:write	([B)V
    //   185: goto -93 -> 92
    //   188: astore 9
    //   190: aload 6
    //   192: astore 5
    //   194: ldc 79
    //   196: new 224	java/lang/StringBuilder
    //   199: dup
    //   200: invokespecial 225	java/lang/StringBuilder:<init>	()V
    //   203: ldc 227
    //   205: invokevirtual 231	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   208: aload_0
    //   209: invokevirtual 231	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   212: invokevirtual 234	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   215: aload 9
    //   217: invokestatic 87	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   220: pop
    //   221: aload 5
    //   223: ifnull -194 -> 29
    //   226: aload 5
    //   228: invokevirtual 235	java/io/FileOutputStream:close	()V
    //   231: iconst_0
    //   232: ireturn
    //   233: astore 11
    //   235: iconst_0
    //   236: ireturn
    //   237: astore_2
    //   238: ldc 79
    //   240: new 224	java/lang/StringBuilder
    //   243: dup
    //   244: invokespecial 225	java/lang/StringBuilder:<init>	()V
    //   247: ldc 237
    //   249: invokevirtual 231	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   252: aload_0
    //   253: invokevirtual 231	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   256: invokevirtual 234	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   259: aload_2
    //   260: invokestatic 240	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   263: pop
    //   264: iconst_0
    //   265: ireturn
    //   266: aload 6
    //   268: ifnull +8 -> 276
    //   271: aload 6
    //   273: invokevirtual 235	java/io/FileOutputStream:close	()V
    //   276: iconst_1
    //   277: ireturn
    //   278: astore 7
    //   280: aload 5
    //   282: ifnull +8 -> 290
    //   285: aload 5
    //   287: invokevirtual 235	java/io/FileOutputStream:close	()V
    //   290: aload 7
    //   292: athrow
    //   293: astore 13
    //   295: goto -19 -> 276
    //   298: astore 8
    //   300: goto -10 -> 290
    //   303: astore 7
    //   305: aload 6
    //   307: astore 5
    //   309: goto -29 -> 280
    //   312: astore 9
    //   314: aconst_null
    //   315: astore 5
    //   317: goto -123 -> 194
    //
    // Exception table:
    //   from	to	target	type
    //   67	92	188	java/io/IOException
    //   92	175	188	java/io/IOException
    //   175	185	188	java/io/IOException
    //   226	231	233	java/io/IOException
    //   31	49	237	java/io/FileNotFoundException
    //   57	67	278	finally
    //   194	221	278	finally
    //   271	276	293	java/io/IOException
    //   285	290	298	java/io/IOException
    //   67	92	303	finally
    //   92	175	303	finally
    //   175	185	303	finally
    //   57	67	312	java/io/IOException
  }

  private static class Section
  {
    public byte[] data;
    public int length;
    public int marker;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.xmp.XmpUtil
 * JD-Core Version:    0.5.4
 */