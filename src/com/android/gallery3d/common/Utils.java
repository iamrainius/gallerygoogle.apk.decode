package com.android.gallery3d.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import java.io.Closeable;
import java.io.InterruptedIOException;

public class Utils
{
  private static final boolean IS_DEBUG_BUILD;
  private static long[] sCrcTable = new long[256];

  static
  {
    int i;
    if ((Build.TYPE.equals("eng")) || (Build.TYPE.equals("userdebug")))
    {
      i = 1;
      label32: IS_DEBUG_BUILD = i;
    }
    for (int j = 0; j < 256; ++j)
    {
      long l1 = j;
      int k = 0;
      if (k < 8)
      {
        label51: long l2;
        if ((0x1 & (int)l1) != 0)
          l2 = -7661587058870466123L;
        while (true)
        {
          l1 = l2 ^ l1 >> 1;
          ++k;
          break label51:
          i = 0;
          break label32:
          l2 = 0L;
        }
      }
      sCrcTable[j] = l1;
    }
  }

  public static void assertTrue(boolean paramBoolean)
  {
    if (paramBoolean)
      return;
    throw new AssertionError();
  }

  public static int ceilLog2(float paramFloat)
  {
    for (int i = 0; ; ++i)
      if ((i >= 31) || (1 << i >= paramFloat))
        return i;
  }

  public static <T> T checkNotNull(T paramT)
  {
    if (paramT == null)
      throw new NullPointerException();
    return paramT;
  }

  public static float clamp(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat1 > paramFloat3)
      return paramFloat3;
    if (paramFloat1 < paramFloat2)
      return paramFloat2;
    return paramFloat1;
  }

  public static int clamp(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 > paramInt3)
      return paramInt3;
    if (paramInt1 < paramInt2)
      return paramInt2;
    return paramInt1;
  }

  public static long clamp(long paramLong1, long paramLong2, long paramLong3)
  {
    if (paramLong1 > paramLong3)
      return paramLong3;
    if (paramLong1 < paramLong2)
      return paramLong2;
    return paramLong1;
  }

  public static void closeSilently(Cursor paramCursor)
  {
    if (paramCursor != null);
    try
    {
      paramCursor.close();
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("Utils", "fail to close", localThrowable);
    }
  }

  public static void closeSilently(ParcelFileDescriptor paramParcelFileDescriptor)
  {
    if (paramParcelFileDescriptor != null);
    try
    {
      paramParcelFileDescriptor.close();
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("Utils", "fail to close", localThrowable);
    }
  }

  public static void closeSilently(Closeable paramCloseable)
  {
    if (paramCloseable == null)
      return;
    try
    {
      paramCloseable.close();
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("Utils", "close fail", localThrowable);
    }
  }

  public static int compare(long paramLong1, long paramLong2)
  {
    if (paramLong1 < paramLong2)
      return -1;
    if (paramLong1 == paramLong2)
      return 0;
    return 1;
  }

  public static String[] copyOf(String[] paramArrayOfString, int paramInt)
  {
    String[] arrayOfString = new String[paramInt];
    System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, Math.min(paramArrayOfString.length, paramInt));
    return arrayOfString;
  }

  public static final long crc64Long(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0))
      return 0L;
    return crc64Long(getBytes(paramString));
  }

  public static final long crc64Long(byte[] paramArrayOfByte)
  {
    long l = -1L;
    int i = 0;
    int j = paramArrayOfByte.length;
    while (i < j)
    {
      l = sCrcTable[(0xFF & ((int)l ^ paramArrayOfByte[i]))] ^ l >> 8;
      ++i;
    }
    return l;
  }

  public static String ensureNotNull(String paramString)
  {
    if (paramString == null)
      paramString = "";
    return paramString;
  }

  public static boolean equals(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 != paramObject2)
    {
      if (paramObject1 == null);
      do
        return false;
      while (!paramObject1.equals(paramObject2));
    }
    return true;
  }

  public static String escapeXml(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    int j = paramString.length();
    if (i < j)
    {
      label15: char c = paramString.charAt(i);
      switch (c)
      {
      default:
        localStringBuilder.append(c);
      case '<':
      case '>':
      case '"':
      case '\'':
      case '&':
      }
      while (true)
      {
        ++i;
        break label15:
        localStringBuilder.append("&lt;");
        continue;
        localStringBuilder.append("&gt;");
        continue;
        localStringBuilder.append("&quot;");
        continue;
        localStringBuilder.append("&#039;");
        continue;
        localStringBuilder.append("&amp;");
      }
    }
    return localStringBuilder.toString();
  }

  public static void fail(String paramString, Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject.length == 0);
    while (true)
    {
      throw new AssertionError(paramString);
      paramString = String.format(paramString, paramArrayOfObject);
    }
  }

  public static int floorLog2(float paramFloat)
  {
    for (int i = 0; ; ++i)
      if ((i >= 31) || (1 << i > paramFloat))
        return i - 1;
  }

  public static byte[] getBytes(String paramString)
  {
    byte[] arrayOfByte = new byte[2 * paramString.length()];
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length;
    int j = 0;
    int k = 0;
    while (j < i)
    {
      int l = arrayOfChar[j];
      int i1 = k + 1;
      arrayOfByte[k] = (byte)(l & 0xFF);
      k = i1 + 1;
      arrayOfByte[i1] = (byte)(l >> 8);
      ++j;
    }
    return arrayOfByte;
  }

  public static String getUserAgent(Context paramContext)
  {
    try
    {
      PackageInfo localPackageInfo = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0);
      Object[] arrayOfObject = new Object[9];
      arrayOfObject[0] = localPackageInfo.packageName;
      arrayOfObject[1] = localPackageInfo.versionName;
      arrayOfObject[2] = Build.BRAND;
      arrayOfObject[3] = Build.DEVICE;
      arrayOfObject[4] = Build.MODEL;
      arrayOfObject[5] = Build.ID;
      arrayOfObject[6] = Integer.valueOf(Build.VERSION.SDK_INT);
      arrayOfObject[7] = Build.VERSION.RELEASE;
      arrayOfObject[8] = Build.VERSION.INCREMENTAL;
      return String.format("%s/%s; %s/%s/%s/%s; %s/%s/%s", arrayOfObject);
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      throw new IllegalStateException("getPackageInfo failed");
    }
  }

  public static boolean handleInterrruptedException(Throwable paramThrowable)
  {
    if ((paramThrowable instanceof InterruptedIOException) || (paramThrowable instanceof InterruptedException))
    {
      Thread.currentThread().interrupt();
      return true;
    }
    return false;
  }

  public static boolean isNullOrEmpty(String paramString)
  {
    return TextUtils.isEmpty(paramString);
  }

  public static boolean isOpaque(int paramInt)
  {
    return paramInt >>> 24 == 255;
  }

  public static String maskDebugInfo(Object paramObject)
  {
    String str;
    if (paramObject == null)
      str = null;
    int i;
    do
    {
      return str;
      str = paramObject.toString();
      i = Math.min(str.length(), "********************************".length());
    }
    while (IS_DEBUG_BUILD);
    return "********************************".substring(0, i);
  }

  public static int nextPowerOf2(int paramInt)
  {
    if ((paramInt <= 0) || (paramInt > 1073741824))
      throw new IllegalArgumentException("n is invalid: " + paramInt);
    int i = paramInt - 1;
    int j = i | i >> 16;
    int k = j | j >> 8;
    int l = k | k >> 4;
    int i1 = l | l >> 2;
    return 1 + (i1 | i1 >> 1);
  }

  public static float parseFloatSafely(String paramString, float paramFloat)
  {
    if (paramString == null)
      return paramFloat;
    try
    {
      float f = Float.parseFloat(paramString);
      return f;
    }
    catch (NumberFormatException localNumberFormatException)
    {
    }
    return paramFloat;
  }

  public static int parseIntSafely(String paramString, int paramInt)
  {
    if (paramString == null)
      return paramInt;
    try
    {
      int i = Integer.parseInt(paramString);
      return i;
    }
    catch (NumberFormatException localNumberFormatException)
    {
    }
    return paramInt;
  }

  public static int prevPowerOf2(int paramInt)
  {
    if (paramInt <= 0)
      throw new IllegalArgumentException();
    return Integer.highestOneBit(paramInt);
  }

  public static void swap(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = paramArrayOfInt[paramInt1];
    paramArrayOfInt[paramInt1] = paramArrayOfInt[paramInt2];
    paramArrayOfInt[paramInt2] = i;
  }

  public static void waitWithoutInterrupt(Object paramObject)
  {
    try
    {
      paramObject.wait();
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      Log.w("Utils", "unexpected interrupt: " + paramObject);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.common.Utils
 * JD-Core Version:    0.5.4
 */