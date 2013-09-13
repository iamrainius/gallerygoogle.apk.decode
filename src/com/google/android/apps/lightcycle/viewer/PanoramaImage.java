package com.google.android.apps.lightcycle.viewer;

import com.google.android.apps.lightcycle.util.PanoMetadata;

public class PanoramaImage
{
  private float lastColumnWidthRad;
  private float lastRowHeightRad;
  private float panoHeightRad;
  private PanoMetadata panoMetadata;
  private float panoOffsetLeftRad;
  private float panoOffsetTopRad;
  private float panoWidthRad;
  private final TileProvider tileProvider;
  private float tileSizeRad;

  public PanoramaImage(TileProvider paramTileProvider, PanoMetadata paramPanoMetadata)
  {
    this.tileProvider = paramTileProvider;
    this.panoMetadata = paramPanoMetadata;
    this.panoWidthRad = (float)(2.0D * (3.141592653589793D * (this.panoMetadata.croppedAreaWidth / this.panoMetadata.fullPanoWidth)));
    this.panoHeightRad = (float)(3.141592653589793D * (this.panoMetadata.croppedAreaHeight / this.panoMetadata.fullPanoHeight));
    this.panoOffsetLeftRad = (float)(2.0D * (3.141592653589793D * (this.panoMetadata.croppedAreaLeft / this.panoMetadata.fullPanoWidth)));
    this.panoOffsetTopRad = (float)(3.141592653589793D * (this.panoMetadata.croppedAreaTop / this.panoMetadata.fullPanoHeight));
  }

  public float getLastColumnWidthRad()
  {
    return this.lastColumnWidthRad;
  }

  public float getLastRowHeightRad()
  {
    return this.lastRowHeightRad;
  }

  public float getOffsetLeftRad()
  {
    return this.panoOffsetLeftRad;
  }

  public float getOffsetTopRad()
  {
    return this.panoOffsetTopRad;
  }

  public float getPanoHeightRad()
  {
    return this.panoHeightRad;
  }

  public TileProvider getTileProvider()
  {
    return this.tileProvider;
  }

  public float getTileSizeRad()
  {
    return this.tileSizeRad;
  }

  public void init()
  {
    float f = this.panoMetadata.imageWidth / this.panoMetadata.croppedAreaWidth * this.tileProvider.getScale();
    this.tileSizeRad = ((float)(2.0D * (3.141592653589793D * (this.tileProvider.getTileSize() / this.panoMetadata.fullPanoWidth))) / f);
    this.lastColumnWidthRad = ((float)(2.0D * (3.141592653589793D * (this.tileProvider.getLastColumnWidth() / this.panoMetadata.fullPanoWidth))) / f);
    this.lastRowHeightRad = ((float)(3.141592653589793D * (this.tileProvider.getLastRowHeight() / this.panoMetadata.fullPanoHeight)) / f);
  }

  public void setMaximumTextureSize(int paramInt)
  {
    this.tileProvider.setMaximumTextureSize(paramInt);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.viewer.PanoramaImage
 * JD-Core Version:    0.5.4
 */