package com.google.android.apps.lightcycle.viewer;

public abstract interface TileProvider
{
  public abstract int getLastColumnWidth();

  public abstract int getLastRowHeight();

  public abstract float getScale();

  public abstract Tile getTile(int paramInt1, int paramInt2);

  public abstract int getTileCountX();

  public abstract int getTileCountY();

  public abstract int getTileSize();

  public abstract void setMaximumTextureSize(int paramInt);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.viewer.TileProvider
 * JD-Core Version:    0.5.4
 */