package com.android.gallery3d.picasasource;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import com.android.gallery3d.common.BlobCache;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.util.ThreadPool.Job;

public class FaceImage extends MediaItem
{
  private BlobCache mBlobCache;
  private PicasaImage mImage;
  private int mIndex;

  public FaceImage(Path paramPath, PicasaImage paramPicasaImage, int paramInt, BlobCache paramBlobCache)
  {
    super(paramPath, nextVersionNumber());
    this.mImage = paramPicasaImage;
    this.mIndex = paramInt;
    this.mBlobCache = paramBlobCache;
  }

  public int getHeight()
  {
    return getTargetSize(2);
  }

  public String getMimeType()
  {
    return this.mImage.getMimeType();
  }

  public int getWidth()
  {
    return getTargetSize(2);
  }

  public ThreadPool.Job<Bitmap> requestImage(int paramInt)
  {
    if (paramInt == 2)
      return new FaceBitmapJob(null);
    throw new UnsupportedOperationException();
  }

  public ThreadPool.Job<BitmapRegionDecoder> requestLargeImage()
  {
    throw new UnsupportedOperationException();
  }

  private class FaceBitmapJob
    implements ThreadPool.Job<Bitmap>
  {
    private FaceBitmapJob()
    {
    }

    private void findSquareRegion(Rect paramRect, int paramInt1, int paramInt2)
    {
      int i = paramRect.width();
      int j = paramRect.height();
      int k;
      int l;
      if (i != j)
      {
        k = Math.min(Math.min(paramInt1, paramInt2), Math.max(i, j));
        l = (k - i) / 2;
        if (paramRect.left >= l)
          break label95;
        paramRect.right = k;
        paramRect.left = 0;
      }
      int i1;
      while (true)
      {
        i1 = (k - j) / 2;
        if (paramRect.top >= i1)
          break;
        paramRect.bottom = k;
        paramRect.top = 0;
        return;
        if (l + paramRect.right > paramInt1)
        {
          label95: paramRect.left = (paramInt1 - k);
          paramRect.right = paramInt1;
        }
        paramRect.left -= l;
        paramRect.right = (l + paramRect.right);
      }
      if (i1 + paramRect.bottom > paramInt2)
      {
        paramRect.top = (paramInt2 - k);
        paramRect.bottom = paramInt2;
        return;
      }
      paramRect.top -= i1;
      paramRect.bottom = (i1 + paramRect.bottom);
    }

    // ERROR //
    public Bitmap run(com.android.gallery3d.util.ThreadPool.JobContext paramJobContext)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/android/gallery3d/picasasource/FaceImage$FaceBitmapJob:this$0	Lcom/android/gallery3d/picasasource/FaceImage;
      //   4: invokestatic 62	com/android/gallery3d/picasasource/FaceImage:access$100	(Lcom/android/gallery3d/picasasource/FaceImage;)Lcom/android/gallery3d/data/Path;
      //   7: invokevirtual 68	com/android/gallery3d/data/Path:toString	()Ljava/lang/String;
      //   10: invokestatic 74	com/android/gallery3d/common/Utils:crc64Long	(Ljava/lang/String;)J
      //   13: lstore_2
      //   14: aload_0
      //   15: getfield 13	com/android/gallery3d/picasasource/FaceImage$FaceBitmapJob:this$0	Lcom/android/gallery3d/picasasource/FaceImage;
      //   18: invokestatic 78	com/android/gallery3d/picasasource/FaceImage:access$200	(Lcom/android/gallery3d/picasasource/FaceImage;)Lcom/android/gallery3d/common/BlobCache;
      //   21: astore 6
      //   23: aload 6
      //   25: monitorenter
      //   26: aload_0
      //   27: getfield 13	com/android/gallery3d/picasasource/FaceImage$FaceBitmapJob:this$0	Lcom/android/gallery3d/picasasource/FaceImage;
      //   30: invokestatic 78	com/android/gallery3d/picasasource/FaceImage:access$200	(Lcom/android/gallery3d/picasasource/FaceImage;)Lcom/android/gallery3d/common/BlobCache;
      //   33: lload_2
      //   34: invokevirtual 84	com/android/gallery3d/common/BlobCache:lookup	(J)[B
      //   37: astore 8
      //   39: aload 6
      //   41: monitorexit
      //   42: aload 8
      //   44: ifnonnull +207 -> 251
      //   47: aload_0
      //   48: getfield 13	com/android/gallery3d/picasasource/FaceImage$FaceBitmapJob:this$0	Lcom/android/gallery3d/picasasource/FaceImage;
      //   51: invokestatic 88	com/android/gallery3d/picasasource/FaceImage:access$300	(Lcom/android/gallery3d/picasasource/FaceImage;)Lcom/android/gallery3d/picasasource/PicasaImage;
      //   54: invokevirtual 94	com/android/gallery3d/picasasource/PicasaImage:requestLargeImage	()Lcom/android/gallery3d/util/ThreadPool$Job;
      //   57: aload_1
      //   58: invokeinterface 97 2 0
      //   63: checkcast 99	android/graphics/BitmapRegionDecoder
      //   66: astore 10
      //   68: aload_0
      //   69: getfield 13	com/android/gallery3d/picasasource/FaceImage$FaceBitmapJob:this$0	Lcom/android/gallery3d/picasasource/FaceImage;
      //   72: invokestatic 88	com/android/gallery3d/picasasource/FaceImage:access$300	(Lcom/android/gallery3d/picasasource/FaceImage;)Lcom/android/gallery3d/picasasource/PicasaImage;
      //   75: invokevirtual 103	com/android/gallery3d/picasasource/PicasaImage:getFaces	()[Lcom/android/gallery3d/data/Face;
      //   78: aload_0
      //   79: getfield 13	com/android/gallery3d/picasasource/FaceImage$FaceBitmapJob:this$0	Lcom/android/gallery3d/picasasource/FaceImage;
      //   82: invokestatic 107	com/android/gallery3d/picasasource/FaceImage:access$400	(Lcom/android/gallery3d/picasasource/FaceImage;)I
      //   85: aaload
      //   86: astore 11
      //   88: aload 10
      //   90: ifnull +151 -> 241
      //   93: aload 11
      //   95: ifnull +146 -> 241
      //   98: new 109	android/graphics/BitmapFactory$Options
      //   101: dup
      //   102: invokespecial 110	android/graphics/BitmapFactory$Options:<init>	()V
      //   105: astore 12
      //   107: new 23	android/graphics/Rect
      //   110: dup
      //   111: aload 11
      //   113: invokevirtual 116	com/android/gallery3d/data/Face:getPosition	()Landroid/graphics/Rect;
      //   116: invokespecial 119	android/graphics/Rect:<init>	(Landroid/graphics/Rect;)V
      //   119: astore 13
      //   121: aload_0
      //   122: aload 13
      //   124: aload 10
      //   126: invokevirtual 122	android/graphics/BitmapRegionDecoder:getWidth	()I
      //   129: aload 10
      //   131: invokevirtual 125	android/graphics/BitmapRegionDecoder:getHeight	()I
      //   134: invokespecial 127	com/android/gallery3d/picasasource/FaceImage$FaceBitmapJob:findSquareRegion	(Landroid/graphics/Rect;II)V
      //   137: aload 12
      //   139: aload 13
      //   141: invokevirtual 27	android/graphics/Rect:width	()I
      //   144: aload 13
      //   146: invokevirtual 30	android/graphics/Rect:height	()I
      //   149: iconst_2
      //   150: invokestatic 133	com/android/gallery3d/data/MediaItem:getTargetSize	(I)I
      //   153: iconst_m1
      //   154: invokestatic 139	com/android/gallery3d/common/BitmapUtils:computeSampleSize	(IIII)I
      //   157: putfield 142	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   160: aload 12
      //   162: invokestatic 148	com/android/gallery3d/data/DecodeUtils:setOptionsMutable	(Landroid/graphics/BitmapFactory$Options;)V
      //   165: aload 10
      //   167: aload 13
      //   169: aload 12
      //   171: invokevirtual 152	android/graphics/BitmapRegionDecoder:decodeRegion	(Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   174: astore 14
      //   176: aload 14
      //   178: bipush 95
      //   180: invokestatic 156	com/android/gallery3d/common/BitmapUtils:compressToBytes	(Landroid/graphics/Bitmap;I)[B
      //   183: astore 15
      //   185: aload 14
      //   187: ifnull +31 -> 218
      //   190: aload_0
      //   191: getfield 13	com/android/gallery3d/picasasource/FaceImage$FaceBitmapJob:this$0	Lcom/android/gallery3d/picasasource/FaceImage;
      //   194: invokestatic 78	com/android/gallery3d/picasasource/FaceImage:access$200	(Lcom/android/gallery3d/picasasource/FaceImage;)Lcom/android/gallery3d/common/BlobCache;
      //   197: astore 16
      //   199: aload 16
      //   201: monitorenter
      //   202: aload_0
      //   203: getfield 13	com/android/gallery3d/picasasource/FaceImage$FaceBitmapJob:this$0	Lcom/android/gallery3d/picasasource/FaceImage;
      //   206: invokestatic 78	com/android/gallery3d/picasasource/FaceImage:access$200	(Lcom/android/gallery3d/picasasource/FaceImage;)Lcom/android/gallery3d/common/BlobCache;
      //   209: lload_2
      //   210: aload 15
      //   212: invokevirtual 160	com/android/gallery3d/common/BlobCache:insert	(J[B)V
      //   215: aload 16
      //   217: monitorexit
      //   218: aload 14
      //   220: areturn
      //   221: astore 7
      //   223: aload 6
      //   225: monitorexit
      //   226: aload 7
      //   228: athrow
      //   229: astore 4
      //   231: ldc 162
      //   233: ldc 164
      //   235: aload 4
      //   237: invokestatic 170	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   240: pop
      //   241: aconst_null
      //   242: areturn
      //   243: astore 17
      //   245: aload 16
      //   247: monitorexit
      //   248: aload 17
      //   250: athrow
      //   251: aload_1
      //   252: aload 8
      //   254: iconst_0
      //   255: aload 8
      //   257: arraylength
      //   258: aconst_null
      //   259: invokestatic 174	com/android/gallery3d/data/MediaItem:getMicroThumbPool	()Lcom/android/gallery3d/data/BitmapPool;
      //   262: invokestatic 178	com/android/gallery3d/data/DecodeUtils:decode	(Lcom/android/gallery3d/util/ThreadPool$JobContext;[BIILandroid/graphics/BitmapFactory$Options;Lcom/android/gallery3d/data/BitmapPool;)Landroid/graphics/Bitmap;
      //   265: astore 9
      //   267: aload 9
      //   269: areturn
      //
      // Exception table:
      //   from	to	target	type
      //   26	42	221	finally
      //   223	226	221	finally
      //   14	26	229	java/io/IOException
      //   47	88	229	java/io/IOException
      //   98	185	229	java/io/IOException
      //   190	202	229	java/io/IOException
      //   226	229	229	java/io/IOException
      //   248	251	229	java/io/IOException
      //   251	267	229	java/io/IOException
      //   202	218	243	finally
      //   245	248	243	finally
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.picasasource.FaceImage
 * JD-Core Version:    0.5.4
 */