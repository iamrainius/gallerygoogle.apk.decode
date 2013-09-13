package com.coremedia.iso.boxes.apple;

import java.util.logging.Logger;

public final class AppleCoverBox extends AbstractAppleMetaDataBox
{
  private static Logger LOG = Logger.getLogger(AppleCoverBox.class.getName());

  public AppleCoverBox()
  {
    super("covr");
  }

  public String getValue()
  {
    return "---";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.apple.AppleCoverBox
 * JD-Core Version:    0.5.4
 */