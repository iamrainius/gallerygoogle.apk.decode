package com.android.gallery3d.data;

import android.content.Context;
import com.android.gallery3d.common.BlobCache;
import com.android.gallery3d.common.BlobCache.LookupRequest;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.CacheManager;
import com.android.gallery3d.util.GalleryUtils;
import java.io.IOException;

public class ImageCacheService
{
  private BlobCache mCache;

  public ImageCacheService(Context paramContext)
  {
    this.mCache = CacheManager.getCache(paramContext, "imgcache", 5000, 209715200, 7);
  }

  private static boolean isSameKey(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    int i = paramArrayOfByte1.length;
    if (paramArrayOfByte2.length < i)
      return false;
    for (int j = 0; j < i; ++j)
      if (paramArrayOfByte1[j] != paramArrayOfByte2[j]);
    return true;
  }

  private static byte[] makeKey(Path paramPath, int paramInt)
  {
    return GalleryUtils.getBytes(paramPath.toString() + "+" + paramInt);
  }

  // ERROR //
  public void clearImageData(Path paramPath, int paramInt)
  {
    // Byte code:
    //   0: aload_1
    //   1: iload_2
    //   2: invokestatic 57	com/android/gallery3d/data/ImageCacheService:makeKey	(Lcom/android/gallery3d/data/Path;I)[B
    //   5: invokestatic 63	com/android/gallery3d/common/Utils:crc64Long	([B)J
    //   8: lstore_3
    //   9: aload_0
    //   10: getfield 22	com/android/gallery3d/data/ImageCacheService:mCache	Lcom/android/gallery3d/common/BlobCache;
    //   13: astore 5
    //   15: aload 5
    //   17: monitorenter
    //   18: aload_0
    //   19: getfield 22	com/android/gallery3d/data/ImageCacheService:mCache	Lcom/android/gallery3d/common/BlobCache;
    //   22: lload_3
    //   23: invokevirtual 69	com/android/gallery3d/common/BlobCache:clearEntry	(J)V
    //   26: aload 5
    //   28: monitorexit
    //   29: return
    //   30: astore 7
    //   32: aload 5
    //   34: monitorexit
    //   35: aload 7
    //   37: athrow
    //   38: astore 6
    //   40: goto -14 -> 26
    //
    // Exception table:
    //   from	to	target	type
    //   18	26	30	finally
    //   26	29	30	finally
    //   32	35	30	finally
    //   18	26	38	java/io/IOException
  }

  public boolean getImageData(Path paramPath, int paramInt, BytesBufferPool.BytesBuffer paramBytesBuffer)
  {
    byte[] arrayOfByte = makeKey(paramPath, paramInt);
    long l = Utils.crc64Long(arrayOfByte);
    try
    {
      BlobCache.LookupRequest localLookupRequest = new BlobCache.LookupRequest();
      localLookupRequest.key = l;
      localLookupRequest.buffer = paramBytesBuffer.data;
      synchronized (this.mCache)
      {
        if (!this.mCache.lookup(localLookupRequest))
          return false;
        if (!isSameKey(arrayOfByte, localLookupRequest.buffer))
          break label123;
        paramBytesBuffer.data = localLookupRequest.buffer;
        paramBytesBuffer.offset = arrayOfByte.length;
        paramBytesBuffer.length = (localLookupRequest.length - paramBytesBuffer.offset);
        return true;
      }
    }
    catch (IOException localIOException)
    {
    }
    label123: return false;
  }

  // ERROR //
  public void putImageData(Path paramPath, int paramInt, byte[] paramArrayOfByte)
  {
    // Byte code:
    //   0: aload_1
    //   1: iload_2
    //   2: invokestatic 57	com/android/gallery3d/data/ImageCacheService:makeKey	(Lcom/android/gallery3d/data/Path;I)[B
    //   5: astore 4
    //   7: aload 4
    //   9: invokestatic 63	com/android/gallery3d/common/Utils:crc64Long	([B)J
    //   12: lstore 5
    //   14: aload 4
    //   16: arraylength
    //   17: aload_3
    //   18: arraylength
    //   19: iadd
    //   20: invokestatic 109	java/nio/ByteBuffer:allocate	(I)Ljava/nio/ByteBuffer;
    //   23: astore 7
    //   25: aload 7
    //   27: aload 4
    //   29: invokevirtual 113	java/nio/ByteBuffer:put	([B)Ljava/nio/ByteBuffer;
    //   32: pop
    //   33: aload 7
    //   35: aload_3
    //   36: invokevirtual 113	java/nio/ByteBuffer:put	([B)Ljava/nio/ByteBuffer;
    //   39: pop
    //   40: aload_0
    //   41: getfield 22	com/android/gallery3d/data/ImageCacheService:mCache	Lcom/android/gallery3d/common/BlobCache;
    //   44: astore 10
    //   46: aload 10
    //   48: monitorenter
    //   49: aload_0
    //   50: getfield 22	com/android/gallery3d/data/ImageCacheService:mCache	Lcom/android/gallery3d/common/BlobCache;
    //   53: lload 5
    //   55: aload 7
    //   57: invokevirtual 117	java/nio/ByteBuffer:array	()[B
    //   60: invokevirtual 121	com/android/gallery3d/common/BlobCache:insert	(J[B)V
    //   63: aload 10
    //   65: monitorexit
    //   66: return
    //   67: astore 12
    //   69: aload 10
    //   71: monitorexit
    //   72: aload 12
    //   74: athrow
    //   75: astore 11
    //   77: goto -14 -> 63
    //
    // Exception table:
    //   from	to	target	type
    //   49	63	67	finally
    //   63	66	67	finally
    //   69	72	67	finally
    //   49	63	75	java/io/IOException
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.ImageCacheService
 * JD-Core Version:    0.5.4
 */