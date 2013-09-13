package com.coremedia.iso.boxes.apple;

public final class AppleTempBox extends AbstractAppleMetaDataBox
{
  public AppleTempBox()
  {
    super("tmpo");
    this.appleDataBox = AppleDataBox.getUint16AppleDataBox();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.apple.AppleTempBox
 * JD-Core Version:    0.5.4
 */