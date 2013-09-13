package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.nio.ByteBuffer;

public class HintMediaHeaderBox extends AbstractMediaHeaderBox
{
  private long avgBitrate;
  private int avgPduSize;
  private long maxBitrate;
  private int maxPduSize;

  public HintMediaHeaderBox()
  {
    super("hmhd");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.maxPduSize = IsoTypeReader.readUInt16(paramByteBuffer);
    this.avgPduSize = IsoTypeReader.readUInt16(paramByteBuffer);
    this.maxBitrate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.avgBitrate = IsoTypeReader.readUInt32(paramByteBuffer);
    IsoTypeReader.readUInt32(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.maxPduSize);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.avgPduSize);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.maxBitrate);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.avgBitrate);
    IsoTypeWriter.writeUInt32(paramByteBuffer, 0L);
  }

  protected long getContentSize()
  {
    return 20L;
  }

  public String toString()
  {
    return "HintMediaHeaderBox{maxPduSize=" + this.maxPduSize + ", avgPduSize=" + this.avgPduSize + ", maxBitrate=" + this.maxBitrate + ", avgBitrate=" + this.avgBitrate + '}';
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.HintMediaHeaderBox
 * JD-Core Version:    0.5.4
 */