package com.googlecode.mp4parser.boxes.piff;

import com.googlecode.mp4parser.boxes.AbstractSampleEncryptionBox;

public class PiffSampleEncryptionBox extends AbstractSampleEncryptionBox
{
  public PiffSampleEncryptionBox()
  {
    super("uuid");
  }

  public byte[] getUserType()
  {
    return new byte[] { -94, 57, 79, 82, 90, -101, 79, 20, -94, 68, 108, 66, 124, 100, -115, -12 };
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.piff.PiffSampleEncryptionBox
 * JD-Core Version:    0.5.4
 */