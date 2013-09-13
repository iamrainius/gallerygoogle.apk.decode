package com.google.android.picasastore;

import android.util.Log;
import com.android.gallery3d.common.Utils;
import java.util.HashMap;

class VersionInfo
{
  private String mFilepath;
  private HashMap<String, Integer> mMap = new HashMap();

  public VersionInfo(String paramString)
  {
    this.mFilepath = paramString;
    loadVersions();
  }

  // ERROR //
  private void loadVersions()
  {
    // Byte code:
    //   0: new 28	java/io/File
    //   3: dup
    //   4: aload_0
    //   5: getfield 21	com/google/android/picasastore/VersionInfo:mFilepath	Ljava/lang/String;
    //   8: invokespecial 30	java/io/File:<init>	(Ljava/lang/String;)V
    //   11: astore_1
    //   12: aload_1
    //   13: invokevirtual 34	java/io/File:exists	()Z
    //   16: ifne +4 -> 20
    //   19: return
    //   20: new 36	java/io/BufferedReader
    //   23: dup
    //   24: new 38	java/io/FileReader
    //   27: dup
    //   28: aload_1
    //   29: invokespecial 41	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   32: invokespecial 44	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   35: astore_2
    //   36: aload_2
    //   37: invokevirtual 48	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   40: astore 6
    //   42: aload 6
    //   44: ifnull +22 -> 66
    //   47: aload_0
    //   48: aload 6
    //   50: invokespecial 51	com/google/android/picasastore/VersionInfo:parseLine	(Ljava/lang/String;)V
    //   53: aload_2
    //   54: invokevirtual 48	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   57: astore 7
    //   59: aload 7
    //   61: astore 6
    //   63: goto -21 -> 42
    //   66: aload_2
    //   67: invokestatic 57	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   70: return
    //   71: astore 4
    //   73: ldc 59
    //   75: ldc 61
    //   77: aload 4
    //   79: invokestatic 67	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   82: pop
    //   83: return
    //   84: astore_3
    //   85: aload_2
    //   86: invokestatic 57	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   89: aload_3
    //   90: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   20	36	71	java/lang/Throwable
    //   66	70	71	java/lang/Throwable
    //   85	91	71	java/lang/Throwable
    //   36	42	84	finally
    //   47	59	84	finally
  }

  private void parseLine(String paramString)
  {
    int i = paramString.lastIndexOf('=');
    if (i == -1)
      return;
    String str1 = paramString.substring(0, i).trim();
    String str2 = paramString.substring(i + 1).trim();
    try
    {
      int j = Integer.parseInt(str2);
      this.mMap.put(str1, Integer.valueOf(j));
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("VersionInfo", "fail parse line:" + paramString, localThrowable);
    }
  }

  public int getVersion(String paramString)
  {
    Integer localInteger = (Integer)this.mMap.get(paramString);
    if (localInteger == null)
      return 0;
    return localInteger.intValue();
  }

  public void setVersion(String paramString, int paramInt)
  {
    if (paramInt != 0);
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      this.mMap.put(paramString, Integer.valueOf(paramInt));
      return;
    }
  }

  // ERROR //
  public void sync()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: new 127	java/io/PrintWriter
    //   5: dup
    //   6: new 28	java/io/File
    //   9: dup
    //   10: aload_0
    //   11: getfield 21	com/google/android/picasastore/VersionInfo:mFilepath	Ljava/lang/String;
    //   14: invokespecial 30	java/io/File:<init>	(Ljava/lang/String;)V
    //   17: invokespecial 128	java/io/PrintWriter:<init>	(Ljava/io/File;)V
    //   20: astore_2
    //   21: aload_0
    //   22: getfield 19	com/google/android/picasastore/VersionInfo:mMap	Ljava/util/HashMap;
    //   25: invokevirtual 132	java/util/HashMap:entrySet	()Ljava/util/Set;
    //   28: invokeinterface 138 1 0
    //   33: astore 6
    //   35: aload 6
    //   37: invokeinterface 143 1 0
    //   42: ifeq +78 -> 120
    //   45: aload 6
    //   47: invokeinterface 147 1 0
    //   52: checkcast 149	java/util/Map$Entry
    //   55: astore 7
    //   57: iconst_2
    //   58: anewarray 4	java/lang/Object
    //   61: astore 8
    //   63: aload 8
    //   65: iconst_0
    //   66: aload 7
    //   68: invokeinterface 152 1 0
    //   73: aastore
    //   74: aload 8
    //   76: iconst_1
    //   77: aload 7
    //   79: invokeinterface 155 1 0
    //   84: aastore
    //   85: aload_2
    //   86: ldc 157
    //   88: aload 8
    //   90: invokevirtual 161	java/io/PrintWriter:printf	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintWriter;
    //   93: pop
    //   94: aload_2
    //   95: invokevirtual 164	java/io/PrintWriter:println	()V
    //   98: goto -63 -> 35
    //   101: astore 4
    //   103: aload_2
    //   104: astore_1
    //   105: ldc 59
    //   107: ldc 166
    //   109: aload 4
    //   111: invokestatic 67	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   114: pop
    //   115: aload_1
    //   116: invokestatic 57	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   119: return
    //   120: aload_2
    //   121: invokestatic 57	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   124: return
    //   125: astore_3
    //   126: aload_1
    //   127: invokestatic 57	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   130: aload_3
    //   131: athrow
    //   132: astore_3
    //   133: aload_2
    //   134: astore_1
    //   135: goto -9 -> 126
    //   138: astore 4
    //   140: aconst_null
    //   141: astore_1
    //   142: goto -37 -> 105
    //
    // Exception table:
    //   from	to	target	type
    //   21	35	101	java/lang/Throwable
    //   35	98	101	java/lang/Throwable
    //   2	21	125	finally
    //   105	115	125	finally
    //   21	35	132	finally
    //   35	98	132	finally
    //   2	21	138	java/lang/Throwable
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasastore.VersionInfo
 * JD-Core Version:    0.5.4
 */