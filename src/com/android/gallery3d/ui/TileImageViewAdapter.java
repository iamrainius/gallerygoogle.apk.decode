package com.android.gallery3d.ui;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.BitmapPool;

public class TileImageViewAdapter
  implements TileImageView.Model
{
  protected int mImageHeight;
  protected int mImageWidth;
  protected int mLevelCount;
  protected BitmapRegionDecoder mRegionDecoder;
  protected ScreenNail mScreenNail;

  private int calculateLevelCount()
  {
    return Math.max(0, Utils.ceilLog2(this.mImageWidth / this.mScreenNail.getWidth()));
  }

  // ERROR //
  private Bitmap getTileWithoutReusingBitmap(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    // Byte code:
    //   0: iload 5
    //   2: iload_1
    //   3: ishl
    //   4: istore 6
    //   6: iload 4
    //   8: iload_1
    //   9: ishl
    //   10: istore 7
    //   12: new 45	android/graphics/Rect
    //   15: dup
    //   16: iload_2
    //   17: iload 6
    //   19: isub
    //   20: iload_3
    //   21: iload 6
    //   23: isub
    //   24: iload 6
    //   26: iload_2
    //   27: iload 7
    //   29: iadd
    //   30: iadd
    //   31: iload 6
    //   33: iload_3
    //   34: iload 7
    //   36: iadd
    //   37: iadd
    //   38: invokespecial 48	android/graphics/Rect:<init>	(IIII)V
    //   41: astore 8
    //   43: aload_0
    //   44: monitorenter
    //   45: aload_0
    //   46: getfield 50	com/android/gallery3d/ui/TileImageViewAdapter:mRegionDecoder	Landroid/graphics/BitmapRegionDecoder;
    //   49: astore 10
    //   51: aload 10
    //   53: ifnonnull +7 -> 60
    //   56: aload_0
    //   57: monitorexit
    //   58: aconst_null
    //   59: areturn
    //   60: new 45	android/graphics/Rect
    //   63: dup
    //   64: iconst_0
    //   65: iconst_0
    //   66: aload_0
    //   67: getfield 22	com/android/gallery3d/ui/TileImageViewAdapter:mImageWidth	I
    //   70: aload_0
    //   71: getfield 52	com/android/gallery3d/ui/TileImageViewAdapter:mImageHeight	I
    //   74: invokespecial 48	android/graphics/Rect:<init>	(IIII)V
    //   77: astore 11
    //   79: aload 11
    //   81: aload 8
    //   83: invokevirtual 56	android/graphics/Rect:intersect	(Landroid/graphics/Rect;)Z
    //   86: invokestatic 60	com/android/gallery3d/common/Utils:assertTrue	(Z)V
    //   89: aload_0
    //   90: monitorexit
    //   91: new 62	android/graphics/BitmapFactory$Options
    //   94: dup
    //   95: invokespecial 63	android/graphics/BitmapFactory$Options:<init>	()V
    //   98: astore 12
    //   100: aload 12
    //   102: getstatic 69	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   105: putfield 72	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
    //   108: aload 12
    //   110: iconst_1
    //   111: putfield 76	android/graphics/BitmapFactory$Options:inPreferQualityOverSpeed	Z
    //   114: aload 12
    //   116: iconst_1
    //   117: iload_1
    //   118: ishl
    //   119: putfield 79	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   122: aload 10
    //   124: monitorenter
    //   125: aload 10
    //   127: aload 11
    //   129: aload 12
    //   131: invokevirtual 85	android/graphics/BitmapRegionDecoder:decodeRegion	(Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   134: astore 14
    //   136: aload 10
    //   138: monitorexit
    //   139: aload 14
    //   141: ifnonnull +11 -> 152
    //   144: ldc 87
    //   146: ldc 89
    //   148: invokestatic 95	com/android/gallery3d/ui/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   151: pop
    //   152: aload 8
    //   154: aload 11
    //   156: invokevirtual 99	android/graphics/Rect:equals	(Ljava/lang/Object;)Z
    //   159: ifne +85 -> 244
    //   162: iload 4
    //   164: iload 5
    //   166: iconst_2
    //   167: imul
    //   168: iadd
    //   169: istore 15
    //   171: iload 15
    //   173: iload 15
    //   175: getstatic 69	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   178: invokestatic 105	android/graphics/Bitmap:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   181: astore 16
    //   183: new 107	android/graphics/Canvas
    //   186: dup
    //   187: aload 16
    //   189: invokespecial 110	android/graphics/Canvas:<init>	(Landroid/graphics/Bitmap;)V
    //   192: aload 14
    //   194: aload 11
    //   196: getfield 113	android/graphics/Rect:left	I
    //   199: aload 8
    //   201: getfield 113	android/graphics/Rect:left	I
    //   204: isub
    //   205: iload_1
    //   206: ishr
    //   207: i2f
    //   208: aload 11
    //   210: getfield 116	android/graphics/Rect:top	I
    //   213: aload 8
    //   215: getfield 116	android/graphics/Rect:top	I
    //   218: isub
    //   219: iload_1
    //   220: ishr
    //   221: i2f
    //   222: aconst_null
    //   223: invokevirtual 120	android/graphics/Canvas:drawBitmap	(Landroid/graphics/Bitmap;FFLandroid/graphics/Paint;)V
    //   226: aload 16
    //   228: areturn
    //   229: astore 9
    //   231: aload_0
    //   232: monitorexit
    //   233: aload 9
    //   235: athrow
    //   236: astore 13
    //   238: aload 10
    //   240: monitorexit
    //   241: aload 13
    //   243: athrow
    //   244: aload 14
    //   246: areturn
    //
    // Exception table:
    //   from	to	target	type
    //   45	51	229	finally
    //   56	58	229	finally
    //   60	91	229	finally
    //   231	233	229	finally
    //   125	139	236	finally
    //   238	241	236	finally
  }

  public void clear()
  {
    monitorenter;
    try
    {
      this.mScreenNail = null;
      this.mImageWidth = 0;
      this.mImageHeight = 0;
      this.mLevelCount = 0;
      this.mRegionDecoder = null;
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public int getImageHeight()
  {
    return this.mImageHeight;
  }

  public int getImageWidth()
  {
    return this.mImageWidth;
  }

  public int getLevelCount()
  {
    return this.mLevelCount;
  }

  public ScreenNail getScreenNail()
  {
    return this.mScreenNail;
  }

  @TargetApi(11)
  public Bitmap getTile(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, BitmapPool paramBitmapPool)
  {
    Bitmap localBitmap;
    if (!ApiHelper.HAS_REUSING_BITMAP_IN_BITMAP_REGION_DECODER)
    {
      localBitmap = getTileWithoutReusingBitmap(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      return localBitmap;
    }
    int i = paramInt5 << paramInt1;
    int j = paramInt4 << paramInt1;
    Rect localRect = new Rect(paramInt2 - i, paramInt3 - i, i + (paramInt2 + j), i + (paramInt3 + j));
    monitorenter;
    BitmapRegionDecoder localBitmapRegionDecoder;
    try
    {
      localBitmapRegionDecoder = this.mRegionDecoder;
      if (localBitmapRegionDecoder == null)
        return null;
    }
    finally
    {
      monitorexit;
    }
    int k;
    if (!new Rect(0, 0, this.mImageWidth, this.mImageHeight).contains(localRect))
    {
      k = 1;
      label117: monitorexit;
      if (paramBitmapPool != null)
        break label259;
      localBitmap = null;
      label127: if (localBitmap == null)
        break label269;
      if (k != 0)
        localBitmap.eraseColor(0);
    }
    while (true)
    {
      BitmapFactory.Options localOptions = new BitmapFactory.Options();
      localOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
      localOptions.inPreferQualityOverSpeed = true;
      localOptions.inSampleSize = (1 << paramInt1);
      localOptions.inBitmap = localBitmap;
      try
      {
        label259: label269: int l;
        try
        {
          localBitmap = localBitmapRegionDecoder.decodeRegion(localRect, localOptions);
          monitorexit;
          if ((localOptions.inBitmap != localBitmap) && (localOptions.inBitmap != null))
          {
            if (paramBitmapPool != null)
              paramBitmapPool.recycle(localOptions.inBitmap);
            localOptions.inBitmap = null;
          }
          if (localBitmap == null);
          return localBitmap;
          k = 0;
          break label117:
          localBitmap = paramBitmapPool.getBitmap();
          break label127:
          l = paramInt4 + paramInt5 * 2;
        }
        finally
        {
          monitorexit;
        }
      }
      finally
      {
        if ((localOptions.inBitmap != localBitmap) && (localOptions.inBitmap != null))
        {
          if (paramBitmapPool != null)
            paramBitmapPool.recycle(localOptions.inBitmap);
          localOptions.inBitmap = null;
        }
      }
    }
  }

  public void setRegionDecoder(BitmapRegionDecoder paramBitmapRegionDecoder)
  {
    monitorenter;
    try
    {
      this.mRegionDecoder = ((BitmapRegionDecoder)Utils.checkNotNull(paramBitmapRegionDecoder));
      this.mImageWidth = paramBitmapRegionDecoder.getWidth();
      this.mImageHeight = paramBitmapRegionDecoder.getHeight();
      this.mLevelCount = calculateLevelCount();
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public void setScreenNail(ScreenNail paramScreenNail, int paramInt1, int paramInt2)
  {
    monitorenter;
    try
    {
      Utils.checkNotNull(paramScreenNail);
      this.mScreenNail = paramScreenNail;
      this.mImageWidth = paramInt1;
      this.mImageHeight = paramInt2;
      this.mRegionDecoder = null;
      this.mLevelCount = 0;
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.TileImageViewAdapter
 * JD-Core Version:    0.5.4
 */