package com.google.android.apps.lightcycle.viewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import com.google.android.apps.lightcycle.util.LG;
import java.io.File;

public class LegacyTileProvider
  implements TileProvider
{
  private File mImageFile;
  private int mMaxTextureSize = -1;
  private int mSampling;
  private int mTileHeight;
  private int mTileWidth;

  public LegacyTileProvider(File paramFile)
  {
    this.mImageFile = paramFile;
  }

  private Bitmap getBitmap(File paramFile)
  {
    if (this.mMaxTextureSize < 0)
      return null;
    BitmapFactory.Options localOptions1 = new BitmapFactory.Options();
    localOptions1.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(paramFile.getAbsolutePath(), localOptions1);
    int i = Math.max(localOptions1.outWidth, localOptions1.outHeight);
    BitmapFactory.Options localOptions2 = new BitmapFactory.Options();
    int j = 1;
    if (this.mMaxTextureSize < i)
    {
      j = i / this.mMaxTextureSize;
      if (i % this.mMaxTextureSize != 0)
        ++j;
    }
    this.mSampling = 1;
    while (this.mSampling < j)
      this.mSampling <<= 1;
    localOptions2.inSampleSize = this.mSampling;
    Bitmap localBitmap = BitmapFactory.decodeFile(paramFile.getAbsolutePath(), localOptions2);
    this.mTileWidth = localOptions2.outWidth;
    this.mTileHeight = localOptions2.outHeight;
    return localBitmap;
  }

  public int getLastColumnWidth()
  {
    return this.mTileWidth;
  }

  public int getLastRowHeight()
  {
    return this.mTileHeight;
  }

  public float getScale()
  {
    return 1.0F / this.mSampling;
  }

  public Tile getTile(int paramInt1, int paramInt2)
  {
    if ((paramInt1 != 0) || (paramInt2 != 0))
    {
      LG.d("Cannot load tile " + paramInt1 + ", " + paramInt2);
      return null;
    }
    return new Tile(getBitmap(this.mImageFile), 0, 0, this.mTileWidth, this.mTileHeight);
  }

  public int getTileCountX()
  {
    return 1;
  }

  public int getTileCountY()
  {
    return 1;
  }

  public int getTileSize()
  {
    return Math.max(this.mTileWidth, this.mTileHeight);
  }

  public void setMaximumTextureSize(int paramInt)
  {
    this.mMaxTextureSize = paramInt;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.viewer.LegacyTileProvider
 * JD-Core Version:    0.5.4
 */