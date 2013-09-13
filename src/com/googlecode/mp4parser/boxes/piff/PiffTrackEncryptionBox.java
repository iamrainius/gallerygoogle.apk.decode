package com.googlecode.mp4parser.boxes.piff;

import com.googlecode.mp4parser.boxes.AbstractTrackEncryptionBox;

public class PiffTrackEncryptionBox extends AbstractTrackEncryptionBox
{
  public PiffTrackEncryptionBox()
  {
    super("uuid");
  }

  public int getFlags()
  {
    return 0;
  }

  public byte[] getUserType()
  {
    return new byte[] { -119, 116, -37, -50, 123, -25, 76, 81, -124, -7, 113, 72, -7, -120, 37, 84 };
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.piff.PiffTrackEncryptionBox
 * JD-Core Version:    0.5.4
 */