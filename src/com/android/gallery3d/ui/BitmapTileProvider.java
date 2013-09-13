package com.android.gallery3d.ui;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.data.BitmapPool;
import java.util.ArrayList;

public class BitmapTileProvider
  implements TileImageView.Model
{
  private final Bitmap.Config mConfig;
  private final int mImageHeight;
  private final int mImageWidth;
  private final Bitmap[] mMipmaps;
  private boolean mRecycled = false;
  private final ScreenNail mScreenNail;

  public BitmapTileProvider(Bitmap paramBitmap, int paramInt)
  {
    this.mImageWidth = paramBitmap.getWidth();
    this.mImageHeight = paramBitmap.getHeight();
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(paramBitmap);
    while ((paramBitmap.getWidth() > paramInt) || (paramBitmap.getHeight() > paramInt))
    {
      paramBitmap = BitmapUtils.resizeBitmapByScale(paramBitmap, 0.5F, false);
      localArrayList.add(paramBitmap);
    }
    this.mScreenNail = new BitmapScreenNail((Bitmap)localArrayList.remove(-1 + localArrayList.size()));
    this.mMipmaps = ((Bitmap[])localArrayList.toArray(new Bitmap[localArrayList.size()]));
    this.mConfig = Bitmap.Config.ARGB_8888;
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
    return this.mMipmaps.length;
  }

  public ScreenNail getScreenNail()
  {
    return this.mScreenNail;
  }

  public Bitmap getTile(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, BitmapPool paramBitmapPool)
  {
    int i = paramInt2 >> paramInt1;
    int j = paramInt3 >> paramInt1;
    int k = paramInt4 + paramInt5 * 2;
    Bitmap localBitmap1;
    if (paramBitmapPool == null)
    {
      localBitmap1 = null;
      label27: if (localBitmap1 != null)
        break label107;
      localBitmap1 = Bitmap.createBitmap(k, k, this.mConfig);
    }
    while (true)
    {
      Bitmap localBitmap2 = this.mMipmaps[paramInt1];
      Canvas localCanvas = new Canvas(localBitmap1);
      int l = paramInt5 + -i;
      int i1 = paramInt5 + -j;
      localCanvas.drawBitmap(localBitmap2, l, i1, null);
      return localBitmap1;
      localBitmap1 = paramBitmapPool.getBitmap();
      break label27:
      label107: localBitmap1.eraseColor(0);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.BitmapTileProvider
 * JD-Core Version:    0.5.4
 */