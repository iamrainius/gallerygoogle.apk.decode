package com.coremedia.iso.boxes.apple;

public class AppleStoreAccountTypeBox extends AbstractAppleMetaDataBox
{
  public AppleStoreAccountTypeBox()
  {
    super("akID");
    this.appleDataBox = AppleDataBox.getUint8AppleDataBox();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.apple.AppleStoreAccountTypeBox
 * JD-Core Version:    0.5.4
 */