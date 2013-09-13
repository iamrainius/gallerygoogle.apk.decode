package com.coremedia.iso.boxes.apple;

public final class AppleCompilationBox extends AbstractAppleMetaDataBox
{
  public AppleCompilationBox()
  {
    super("cpil");
    this.appleDataBox = AppleDataBox.getUint8AppleDataBox();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.apple.AppleCompilationBox
 * JD-Core Version:    0.5.4
 */