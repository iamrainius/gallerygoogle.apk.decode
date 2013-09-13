package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public final class OmaDrmAccessUnitFormatBox extends AbstractFullBox
{
  private byte allBits;
  private int initVectorLength;
  private int keyIndicatorLength;
  private boolean selectiveEncryption;

  public OmaDrmAccessUnitFormatBox()
  {
    super("odaf");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.allBits = (byte)IsoTypeReader.readUInt8(paramByteBuffer);
    if ((0x80 & this.allBits) == 128);
    for (int i = 1; ; i = 0)
    {
      this.selectiveEncryption = i;
      this.keyIndicatorLength = IsoTypeReader.readUInt8(paramByteBuffer);
      this.initVectorLength = IsoTypeReader.readUInt8(paramByteBuffer);
      return;
    }
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.allBits);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.keyIndicatorLength);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.initVectorLength);
  }

  protected long getContentSize()
  {
    return 7L;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.OmaDrmAccessUnitFormatBox
 * JD-Core Version:    0.5.4
 */