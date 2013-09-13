package com.coremedia.iso.boxes.apple;

import java.util.HashMap;
import java.util.Map;

public class AppleMediaTypeBox extends AbstractAppleMetaDataBox
{
  private static Map<String, String> mediaTypes = new HashMap();

  static
  {
    mediaTypes.put("0", "Movie (is now 9)");
    mediaTypes.put("1", "Normal (Music)");
    mediaTypes.put("2", "Audiobook");
    mediaTypes.put("6", "Music Video");
    mediaTypes.put("9", "Movie");
    mediaTypes.put("10", "TV Show");
    mediaTypes.put("11", "Booklet");
    mediaTypes.put("14", "Ringtone");
  }

  public AppleMediaTypeBox()
  {
    super("stik");
    this.appleDataBox = AppleDataBox.getUint8AppleDataBox();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.apple.AppleMediaTypeBox
 * JD-Core Version:    0.5.4
 */