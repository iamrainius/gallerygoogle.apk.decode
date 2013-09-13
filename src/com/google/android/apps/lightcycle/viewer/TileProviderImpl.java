package com.google.android.apps.lightcycle.viewer;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TileProviderImpl
  implements TileProvider
{
  private static final String TAG = TileProviderImpl.class.getSimpleName();
  private final BitmapRegionDecoder decoder;
  private final int lastColumnWidth;
  private final int lastRowHeight;
  private final int tileCountX;
  private final int tileCountY;

  public TileProviderImpl(File paramFile)
  {
    while (true)
    {
      FileInputStream localFileInputStream2;
      try
      {
        FileInputStream localFileInputStream1 = new FileInputStream(paramFile);
        localFileInputStream2 = localFileInputStream1;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        try
        {
          this.decoder = BitmapRegionDecoder.newInstance(localFileInputStream2, false);
          this.tileCountX = (int)Math.ceil(this.decoder.getWidth() / 512.0D);
          i = this.decoder.getWidth() % 512;
          if (i <= 0)
            break label145;
          this.lastColumnWidth = i;
          this.tileCountY = (int)Math.ceil(this.decoder.getHeight() / 512.0D);
          j = this.decoder.getHeight() % 512;
          if (j <= 0)
            break label153;
          this.lastRowHeight = j;
          return;
          localFileNotFoundException = localFileNotFoundException;
          Log.e(TAG, "File not found", localFileNotFoundException);
          localFileInputStream2 = null;
        }
        catch (IOException localIOException)
        {
          throw new RuntimeException("Could not create decoder", localIOException);
        }
      }
      label145: int i = 512;
      continue;
      label153: int j = 512;
    }
  }

  private Rect getImageRegionForTileCoordinate(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 512;
    int j = paramInt2 * 512;
    int k = i + 512;
    int l = j + 512;
    if (paramInt1 == -1 + this.tileCountX)
      k -= 512 - this.lastColumnWidth;
    if (paramInt2 == -1 + this.tileCountY)
      l -= 512 - this.lastRowHeight;
    return new Rect(i, j, k, l);
  }

  public int getLastColumnWidth()
  {
    return this.lastColumnWidth;
  }

  public int getLastRowHeight()
  {
    return this.lastRowHeight;
  }

  public float getScale()
  {
    return 1.0F;
  }

  @SuppressLint({"NewApi"})
  public Tile getTile(int paramInt1, int paramInt2)
  {
    monitorenter;
    Bitmap localBitmap;
    Tile localTile;
    try
    {
      BitmapFactory.Options localOptions = new BitmapFactory.Options();
      localOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
      localOptions.inPreferQualityOverSpeed = true;
      localBitmap = this.decoder.decodeRegion(getImageRegionForTileCoordinate(paramInt1, paramInt2), localOptions);
      if (localBitmap == null)
      {
        localTile = null;
        return localTile;
      }
    }
    finally
    {
      monitorexit;
    }
  }

  public int getTileCountX()
  {
    return this.tileCountX;
  }

  public int getTileCountY()
  {
    return this.tileCountY;
  }

  public int getTileSize()
  {
    return 512;
  }

  public void setMaximumTextureSize(int paramInt)
  {
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.viewer.TileProviderImpl
 * JD-Core Version:    0.5.4
 */