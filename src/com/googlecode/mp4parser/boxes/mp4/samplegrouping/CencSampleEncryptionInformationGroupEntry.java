package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class CencSampleEncryptionInformationGroupEntry extends GroupEntry
{
  private int isEncrypted;
  private byte ivSize;
  private byte[] kid = new byte[16];

  static
  {
    if (!CencSampleEncryptionInformationGroupEntry.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    CencSampleEncryptionInformationGroupEntry localCencSampleEncryptionInformationGroupEntry;
    do
    {
      return true;
      if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
        return false;
      localCencSampleEncryptionInformationGroupEntry = (CencSampleEncryptionInformationGroupEntry)paramObject;
      if (this.isEncrypted != localCencSampleEncryptionInformationGroupEntry.isEncrypted)
        return false;
      if (this.ivSize != localCencSampleEncryptionInformationGroupEntry.ivSize)
        return false;
    }
    while (Arrays.equals(this.kid, localCencSampleEncryptionInformationGroupEntry.kid));
    return false;
  }

  public ByteBuffer get()
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(20);
    IsoTypeWriter.writeUInt24(localByteBuffer, this.isEncrypted);
    IsoTypeWriter.writeUInt8(localByteBuffer, this.ivSize);
    localByteBuffer.put(this.kid);
    localByteBuffer.rewind();
    return localByteBuffer;
  }

  public int hashCode()
  {
    int i = 31 * (31 * this.isEncrypted + this.ivSize);
    if (this.kid != null);
    for (int j = Arrays.hashCode(this.kid); ; j = 0)
      return i + j;
  }

  public void parse(ByteBuffer paramByteBuffer)
  {
    this.isEncrypted = IsoTypeReader.readUInt24(paramByteBuffer);
    this.ivSize = (byte)IsoTypeReader.readUInt8(paramByteBuffer);
    this.kid = new byte[16];
    paramByteBuffer.get(this.kid);
  }

  public String toString()
  {
    return "CencSampleEncryptionInformationGroupEntry{isEncrypted=" + this.isEncrypted + ", ivSize=" + this.ivSize + ", kid=" + Hex.encodeHex(this.kid) + '}';
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.samplegrouping.CencSampleEncryptionInformationGroupEntry
 * JD-Core Version:    0.5.4
 */