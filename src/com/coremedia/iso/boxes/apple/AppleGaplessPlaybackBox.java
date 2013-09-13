package com.coremedia.iso.boxes.apple;

public final class AppleGaplessPlaybackBox extends AbstractAppleMetaDataBox
{
  public AppleGaplessPlaybackBox()
  {
    super("pgap");
    this.appleDataBox = AppleDataBox.getUint8AppleDataBox();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.apple.AppleGaplessPlaybackBox
 * JD-Core Version:    0.5.4
 */