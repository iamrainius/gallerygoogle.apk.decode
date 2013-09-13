package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractBox;
import java.nio.ByteBuffer;

public final class BitRateBox extends AbstractBox
{
  private long avgBitrate;
  private long bufferSizeDb;
  private long maxBitrate;

  public BitRateBox()
  {
    super("btrt");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.bufferSizeDb = IsoTypeReader.readUInt32(paramByteBuffer);
    this.maxBitrate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.avgBitrate = IsoTypeReader.readUInt32(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.bufferSizeDb);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.maxBitrate);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.avgBitrate);
  }

  protected long getContentSize()
  {
    return 12L;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.BitRateBox
 * JD-Core Version:    0.5.4
 */