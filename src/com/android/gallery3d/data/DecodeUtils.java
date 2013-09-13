package com.android.gallery3d.data;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.util.FloatMath;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.ui.Log;
import com.android.gallery3d.util.ThreadPool.CancelListener;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.io.FileDescriptor;
import java.io.InputStream;

public class DecodeUtils
{
  public static BitmapRegionDecoder createBitmapRegionDecoder(ThreadPool.JobContext paramJobContext, FileDescriptor paramFileDescriptor, boolean paramBoolean)
  {
    try
    {
      BitmapRegionDecoder localBitmapRegionDecoder = BitmapRegionDecoder.newInstance(paramFileDescriptor, paramBoolean);
      return localBitmapRegionDecoder;
    }
    catch (Throwable localThrowable)
    {
      Log.w("DecodeUtils", localThrowable);
    }
    return null;
  }

  public static BitmapRegionDecoder createBitmapRegionDecoder(ThreadPool.JobContext paramJobContext, InputStream paramInputStream, boolean paramBoolean)
  {
    try
    {
      BitmapRegionDecoder localBitmapRegionDecoder = BitmapRegionDecoder.newInstance(paramInputStream, paramBoolean);
      return localBitmapRegionDecoder;
    }
    catch (Throwable localThrowable)
    {
      Log.w("DecodeUtils", "requestCreateBitmapRegionDecoder: " + localThrowable);
    }
    return null;
  }

  public static BitmapRegionDecoder createBitmapRegionDecoder(ThreadPool.JobContext paramJobContext, String paramString, boolean paramBoolean)
  {
    try
    {
      BitmapRegionDecoder localBitmapRegionDecoder = BitmapRegionDecoder.newInstance(paramString, paramBoolean);
      return localBitmapRegionDecoder;
    }
    catch (Throwable localThrowable)
    {
      Log.w("DecodeUtils", localThrowable);
    }
    return null;
  }

  public static BitmapRegionDecoder createBitmapRegionDecoder(ThreadPool.JobContext paramJobContext, byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if ((paramInt1 < 0) || (paramInt2 <= 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length))
    {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = Integer.valueOf(paramInt1);
      arrayOfObject[1] = Integer.valueOf(paramInt2);
      arrayOfObject[2] = Integer.valueOf(paramArrayOfByte.length);
      throw new IllegalArgumentException(String.format("offset = %s, length = %s, bytes = %s", arrayOfObject));
    }
    try
    {
      BitmapRegionDecoder localBitmapRegionDecoder = BitmapRegionDecoder.newInstance(paramArrayOfByte, paramInt1, paramInt2, paramBoolean);
      return localBitmapRegionDecoder;
    }
    catch (Throwable localThrowable)
    {
      Log.w("DecodeUtils", localThrowable);
    }
    return null;
  }

  public static Bitmap decode(ThreadPool.JobContext paramJobContext, byte[] paramArrayOfByte, int paramInt1, int paramInt2, BitmapFactory.Options paramOptions)
  {
    if (paramOptions == null)
      paramOptions = new BitmapFactory.Options();
    paramJobContext.setCancelListener(new DecodeCanceller(paramOptions));
    setOptionsMutable(paramOptions);
    return ensureGLCompatibleBitmap(BitmapFactory.decodeByteArray(paramArrayOfByte, paramInt1, paramInt2, paramOptions));
  }

  @TargetApi(11)
  public static Bitmap decode(ThreadPool.JobContext paramJobContext, byte[] paramArrayOfByte, int paramInt1, int paramInt2, BitmapFactory.Options paramOptions, BitmapPool paramBitmapPool)
  {
    Bitmap localBitmap2;
    if (paramBitmapPool == null)
    {
      localBitmap2 = decode(paramJobContext, paramArrayOfByte, paramInt1, paramInt2, paramOptions);
      return localBitmap2;
    }
    if (paramOptions == null)
      paramOptions = new BitmapFactory.Options();
    if (paramOptions.inSampleSize < 1)
      paramOptions.inSampleSize = 1;
    paramOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
    Bitmap localBitmap1;
    if (paramOptions.inSampleSize == 1)
    {
      localBitmap1 = findCachedBitmap(paramBitmapPool, paramJobContext, paramArrayOfByte, paramInt1, paramInt2, paramOptions);
      paramOptions.inBitmap = localBitmap1;
    }
    try
    {
      localBitmap2 = decode(paramJobContext, paramArrayOfByte, paramInt1, paramInt2, paramOptions);
      if ((paramOptions.inBitmap != null) && (paramOptions.inBitmap != localBitmap2));
      paramBitmapPool.recycle(paramOptions.inBitmap);
      paramOptions.inBitmap = null;
      return localBitmap2;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      if (paramOptions.inBitmap == null)
      {
        throw localIllegalArgumentException;
        localBitmap1 = null;
      }
      Log.w("DecodeUtils", "decode fail with a given bitmap, try decode to a new bitmap");
      paramBitmapPool.recycle(paramOptions.inBitmap);
      paramOptions.inBitmap = null;
    }
    return decode(paramJobContext, paramArrayOfByte, paramInt1, paramInt2, paramOptions);
  }

  public static Bitmap decode(ThreadPool.JobContext paramJobContext, byte[] paramArrayOfByte, BitmapFactory.Options paramOptions)
  {
    return decode(paramJobContext, paramArrayOfByte, 0, paramArrayOfByte.length, paramOptions);
  }

  public static void decodeBounds(ThreadPool.JobContext paramJobContext, byte[] paramArrayOfByte, int paramInt1, int paramInt2, BitmapFactory.Options paramOptions)
  {
    if (paramOptions != null);
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      paramOptions.inJustDecodeBounds = true;
      paramJobContext.setCancelListener(new DecodeCanceller(paramOptions));
      BitmapFactory.decodeByteArray(paramArrayOfByte, paramInt1, paramInt2, paramOptions);
      paramOptions.inJustDecodeBounds = false;
      return;
    }
  }

  public static Bitmap decodeIfBigEnough(ThreadPool.JobContext paramJobContext, byte[] paramArrayOfByte, BitmapFactory.Options paramOptions, int paramInt)
  {
    if (paramOptions == null)
      paramOptions = new BitmapFactory.Options();
    paramJobContext.setCancelListener(new DecodeCanceller(paramOptions));
    paramOptions.inJustDecodeBounds = true;
    BitmapFactory.decodeByteArray(paramArrayOfByte, 0, paramArrayOfByte.length, paramOptions);
    if (paramJobContext.isCancelled());
    do
      return null;
    while ((paramOptions.outWidth < paramInt) || (paramOptions.outHeight < paramInt));
    paramOptions.inSampleSize = BitmapUtils.computeSampleSizeLarger(paramOptions.outWidth, paramOptions.outHeight, paramInt);
    paramOptions.inJustDecodeBounds = false;
    setOptionsMutable(paramOptions);
    return ensureGLCompatibleBitmap(BitmapFactory.decodeByteArray(paramArrayOfByte, 0, paramArrayOfByte.length, paramOptions));
  }

  public static Bitmap decodeThumbnail(ThreadPool.JobContext paramJobContext, FileDescriptor paramFileDescriptor, BitmapFactory.Options paramOptions, int paramInt1, int paramInt2)
  {
    if (paramOptions == null)
      paramOptions = new BitmapFactory.Options();
    paramJobContext.setCancelListener(new DecodeCanceller(paramOptions));
    paramOptions.inJustDecodeBounds = true;
    BitmapFactory.decodeFileDescriptor(paramFileDescriptor, null, paramOptions);
    if (paramJobContext.isCancelled());
    int i;
    int j;
    label124: Bitmap localBitmap;
    do
    {
      return null;
      i = paramOptions.outWidth;
      j = paramOptions.outHeight;
      if (paramInt2 != 2)
        break label205;
      paramOptions.inSampleSize = BitmapUtils.computeSampleSizeLarger(paramInt1 / Math.min(i, j));
      if (i / paramOptions.inSampleSize * (j / paramOptions.inSampleSize) > 640000)
        paramOptions.inSampleSize = BitmapUtils.computeSampleSize(FloatMath.sqrt(640000.0F / (i * j)));
      paramOptions.inJustDecodeBounds = false;
      setOptionsMutable(paramOptions);
      localBitmap = BitmapFactory.decodeFileDescriptor(paramFileDescriptor, null, paramOptions);
    }
    while (localBitmap == null);
    float f1 = paramInt1;
    if (paramInt2 == 2);
    for (int k = Math.min(localBitmap.getWidth(), localBitmap.getHeight()); ; k = Math.max(localBitmap.getWidth(), localBitmap.getHeight()))
    {
      float f2 = f1 / k;
      if (f2 <= 0.5D)
        localBitmap = BitmapUtils.resizeBitmapByScale(localBitmap, f2, true);
      return ensureGLCompatibleBitmap(localBitmap);
      label205: paramOptions.inSampleSize = BitmapUtils.computeSampleSizeLarger(paramInt1 / Math.max(i, j));
      break label124:
    }
  }

  // ERROR //
  public static Bitmap decodeThumbnail(ThreadPool.JobContext paramJobContext, String paramString, BitmapFactory.Options paramOptions, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: new 220	java/io/FileInputStream
    //   6: dup
    //   7: aload_1
    //   8: invokespecial 221	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   11: astore 6
    //   13: aload_0
    //   14: aload 6
    //   16: invokevirtual 225	java/io/FileInputStream:getFD	()Ljava/io/FileDescriptor;
    //   19: aload_2
    //   20: iload_3
    //   21: iload 4
    //   23: invokestatic 227	com/android/gallery3d/data/DecodeUtils:decodeThumbnail	(Lcom/android/gallery3d/util/ThreadPool$JobContext;Ljava/io/FileDescriptor;Landroid/graphics/BitmapFactory$Options;II)Landroid/graphics/Bitmap;
    //   26: astore 10
    //   28: aload 6
    //   30: invokestatic 231	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   33: aload 10
    //   35: areturn
    //   36: astore 7
    //   38: ldc 20
    //   40: aload 7
    //   42: invokestatic 26	com/android/gallery3d/ui/Log:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
    //   45: pop
    //   46: aload 5
    //   48: invokestatic 231	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   51: aconst_null
    //   52: areturn
    //   53: astore 8
    //   55: aload 5
    //   57: invokestatic 231	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
    //   60: aload 8
    //   62: athrow
    //   63: astore 8
    //   65: aload 6
    //   67: astore 5
    //   69: goto -14 -> 55
    //   72: astore 7
    //   74: aload 6
    //   76: astore 5
    //   78: goto -40 -> 38
    //
    // Exception table:
    //   from	to	target	type
    //   3	13	36	java/lang/Exception
    //   3	13	53	finally
    //   38	46	53	finally
    //   13	28	63	finally
    //   13	28	72	java/lang/Exception
  }

  public static Bitmap ensureGLCompatibleBitmap(Bitmap paramBitmap)
  {
    if ((paramBitmap == null) || (paramBitmap.getConfig() != null))
      return paramBitmap;
    Bitmap localBitmap = paramBitmap.copy(Bitmap.Config.ARGB_8888, false);
    paramBitmap.recycle();
    return localBitmap;
  }

  private static Bitmap findCachedBitmap(BitmapPool paramBitmapPool, ThreadPool.JobContext paramJobContext, byte[] paramArrayOfByte, int paramInt1, int paramInt2, BitmapFactory.Options paramOptions)
  {
    if (paramBitmapPool.isOneSize())
      return paramBitmapPool.getBitmap();
    decodeBounds(paramJobContext, paramArrayOfByte, paramInt1, paramInt2, paramOptions);
    return paramBitmapPool.getBitmap(paramOptions.outWidth, paramOptions.outHeight);
  }

  @TargetApi(11)
  public static void setOptionsMutable(BitmapFactory.Options paramOptions)
  {
    if (!ApiHelper.HAS_OPTIONS_IN_MUTABLE)
      return;
    paramOptions.inMutable = true;
  }

  private static class DecodeCanceller
    implements ThreadPool.CancelListener
  {
    BitmapFactory.Options mOptions;

    public DecodeCanceller(BitmapFactory.Options paramOptions)
    {
      this.mOptions = paramOptions;
    }

    public void onCancel()
    {
      this.mOptions.requestCancelDecode();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.DecodeUtils
 * JD-Core Version:    0.5.4
 */