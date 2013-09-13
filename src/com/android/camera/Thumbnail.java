package com.android.camera;

import android.graphics.Bitmap;
import java.io.FileDescriptor;

public class Thumbnail
{
  public static Bitmap createVideoThumbnailBitmap(FileDescriptor paramFileDescriptor, int paramInt)
  {
    return createVideoThumbnailBitmap(null, paramFileDescriptor, paramInt);
  }

  public static Bitmap createVideoThumbnailBitmap(String paramString, int paramInt)
  {
    return createVideoThumbnailBitmap(paramString, null, paramInt);
  }

  // ERROR //
  private static Bitmap createVideoThumbnailBitmap(String paramString, FileDescriptor paramFileDescriptor, int paramInt)
  {
    // Byte code:
    //   0: new 20	android/media/MediaMetadataRetriever
    //   3: dup
    //   4: invokespecial 21	android/media/MediaMetadataRetriever:<init>	()V
    //   7: astore_3
    //   8: aload_0
    //   9: ifnull +32 -> 41
    //   12: aload_3
    //   13: aload_0
    //   14: invokevirtual 25	android/media/MediaMetadataRetriever:setDataSource	(Ljava/lang/String;)V
    //   17: aload_3
    //   18: ldc2_w 26
    //   21: invokevirtual 31	android/media/MediaMetadataRetriever:getFrameAtTime	(J)Landroid/graphics/Bitmap;
    //   24: astore 14
    //   26: aload 14
    //   28: astore 8
    //   30: aload_3
    //   31: invokevirtual 34	android/media/MediaMetadataRetriever:release	()V
    //   34: aload 8
    //   36: ifnonnull +62 -> 98
    //   39: aconst_null
    //   40: areturn
    //   41: aload_3
    //   42: aload_1
    //   43: invokevirtual 37	android/media/MediaMetadataRetriever:setDataSource	(Ljava/io/FileDescriptor;)V
    //   46: goto -29 -> 17
    //   49: astore 12
    //   51: aload_3
    //   52: invokevirtual 34	android/media/MediaMetadataRetriever:release	()V
    //   55: aconst_null
    //   56: astore 8
    //   58: goto -24 -> 34
    //   61: astore 13
    //   63: aconst_null
    //   64: astore 8
    //   66: goto -32 -> 34
    //   69: astore 6
    //   71: aload_3
    //   72: invokevirtual 34	android/media/MediaMetadataRetriever:release	()V
    //   75: aconst_null
    //   76: astore 8
    //   78: goto -44 -> 34
    //   81: astore 7
    //   83: aconst_null
    //   84: astore 8
    //   86: goto -52 -> 34
    //   89: astore 4
    //   91: aload_3
    //   92: invokevirtual 34	android/media/MediaMetadataRetriever:release	()V
    //   95: aload 4
    //   97: athrow
    //   98: aload 8
    //   100: invokevirtual 43	android/graphics/Bitmap:getWidth	()I
    //   103: istore 9
    //   105: aload 8
    //   107: invokevirtual 46	android/graphics/Bitmap:getHeight	()I
    //   110: istore 10
    //   112: iload 9
    //   114: iload_2
    //   115: if_icmple +37 -> 152
    //   118: iload_2
    //   119: i2f
    //   120: iload 9
    //   122: i2f
    //   123: fdiv
    //   124: fstore 11
    //   126: aload 8
    //   128: fload 11
    //   130: iload 9
    //   132: i2f
    //   133: fmul
    //   134: invokestatic 52	java/lang/Math:round	(F)I
    //   137: fload 11
    //   139: iload 10
    //   141: i2f
    //   142: fmul
    //   143: invokestatic 52	java/lang/Math:round	(F)I
    //   146: iconst_1
    //   147: invokestatic 56	android/graphics/Bitmap:createScaledBitmap	(Landroid/graphics/Bitmap;IIZ)Landroid/graphics/Bitmap;
    //   150: astore 8
    //   152: aload 8
    //   154: areturn
    //   155: astore 15
    //   157: goto -123 -> 34
    //   160: astore 5
    //   162: goto -67 -> 95
    //
    // Exception table:
    //   from	to	target	type
    //   12	17	49	java/lang/IllegalArgumentException
    //   17	26	49	java/lang/IllegalArgumentException
    //   41	46	49	java/lang/IllegalArgumentException
    //   51	55	61	java/lang/RuntimeException
    //   12	17	69	java/lang/RuntimeException
    //   17	26	69	java/lang/RuntimeException
    //   41	46	69	java/lang/RuntimeException
    //   71	75	81	java/lang/RuntimeException
    //   12	17	89	finally
    //   17	26	89	finally
    //   41	46	89	finally
    //   30	34	155	java/lang/RuntimeException
    //   91	95	160	java/lang/RuntimeException
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.Thumbnail
 * JD-Core Version:    0.5.4
 */