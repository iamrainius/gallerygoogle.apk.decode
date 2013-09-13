package com.coremedia.iso.boxes.apple;

public final class AppleNetworkBox extends AbstractAppleMetaDataBox
{
  public AppleNetworkBox()
  {
    super("tvnn");
    this.appleDataBox = AppleDataBox.getStringAppleDataBox();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.apple.AppleNetworkBox
 * JD-Core Version:    0.5.4
 */