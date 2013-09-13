package com.googlecode.mp4parser.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;
import java.util.Arrays;

public abstract class AbstractTrackEncryptionBox extends AbstractFullBox
{
  int defaultAlgorithmId;
  int defaultIvSize;
  byte[] default_KID;

  protected AbstractTrackEncryptionBox(String paramString)
  {
    super(paramString);
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.defaultAlgorithmId = IsoTypeReader.readUInt24(paramByteBuffer);
    this.defaultIvSize = IsoTypeReader.readUInt8(paramByteBuffer);
    this.default_KID = new byte[16];
    paramByteBuffer.get(this.default_KID);
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    AbstractTrackEncryptionBox localAbstractTrackEncryptionBox;
    do
    {
      return true;
      if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
        return false;
      localAbstractTrackEncryptionBox = (AbstractTrackEncryptionBox)paramObject;
      if (this.defaultAlgorithmId != localAbstractTrackEncryptionBox.defaultAlgorithmId)
        return false;
      if (this.defaultIvSize != localAbstractTrackEncryptionBox.defaultIvSize)
        return false;
    }
    while (Arrays.equals(this.default_KID, localAbstractTrackEncryptionBox.default_KID));
    return false;
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt24(paramByteBuffer, this.defaultAlgorithmId);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.defaultIvSize);
    paramByteBuffer.put(this.default_KID);
  }

  protected long getContentSize()
  {
    return 24L;
  }

  public int getDefaultIvSize()
  {
    return this.defaultIvSize;
  }

  public int hashCode()
  {
    int i = 31 * (31 * this.defaultAlgorithmId + this.defaultIvSize);
    if (this.default_KID != null);
    for (int j = Arrays.hashCode(this.default_KID); ; j = 0)
      return i + j;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.AbstractTrackEncryptionBox
 * JD-Core Version:    0.5.4
 */