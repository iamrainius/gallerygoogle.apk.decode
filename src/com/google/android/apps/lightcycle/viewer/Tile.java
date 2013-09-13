package com.google.android.apps.lightcycle.viewer;

import android.graphics.Bitmap;

public class Tile
{
  public final Bitmap bitmap;
  public final int height;
  public final int width;
  public final int x;
  public final int y;

  public Tile(Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.bitmap = paramBitmap;
    this.x = paramInt1;
    this.y = paramInt2;
    this.width = paramInt3;
    this.height = paramInt4;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.viewer.Tile
 * JD-Core Version:    0.5.4
 */