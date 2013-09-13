package com.coremedia.iso.boxes.apple;

public final class AppleTvSeasonBox extends AbstractAppleMetaDataBox
{
  public AppleTvSeasonBox()
  {
    super("tvsn");
    this.appleDataBox = AppleDataBox.getUint32AppleDataBox();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.apple.AppleTvSeasonBox
 * JD-Core Version:    0.5.4
 */