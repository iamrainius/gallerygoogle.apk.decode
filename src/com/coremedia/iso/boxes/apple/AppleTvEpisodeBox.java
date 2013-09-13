package com.coremedia.iso.boxes.apple;

public class AppleTvEpisodeBox extends AbstractAppleMetaDataBox
{
  public AppleTvEpisodeBox()
  {
    super("tves");
    this.appleDataBox = AppleDataBox.getUint32AppleDataBox();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.apple.AppleTvEpisodeBox
 * JD-Core Version:    0.5.4
 */