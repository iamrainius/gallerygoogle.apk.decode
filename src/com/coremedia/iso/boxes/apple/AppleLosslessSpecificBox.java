package com.coremedia.iso.boxes.apple;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public final class AppleLosslessSpecificBox extends AbstractFullBox
{
  private long bitRate;
  private int channels;
  private int historyMult;
  private int initialHistory;
  private int kModifier;
  private long maxCodedFrameSize;
  private long maxSamplePerFrame;
  private long sampleRate;
  private int sampleSize;
  private int unknown1;
  private int unknown2;

  public AppleLosslessSpecificBox()
  {
    super("alac");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.maxSamplePerFrame = IsoTypeReader.readUInt32(paramByteBuffer);
    this.unknown1 = IsoTypeReader.readUInt8(paramByteBuffer);
    this.sampleSize = IsoTypeReader.readUInt8(paramByteBuffer);
    this.historyMult = IsoTypeReader.readUInt8(paramByteBuffer);
    this.initialHistory = IsoTypeReader.readUInt8(paramByteBuffer);
    this.kModifier = IsoTypeReader.readUInt8(paramByteBuffer);
    this.channels = IsoTypeReader.readUInt8(paramByteBuffer);
    this.unknown2 = IsoTypeReader.readUInt16(paramByteBuffer);
    this.maxCodedFrameSize = IsoTypeReader.readUInt32(paramByteBuffer);
    this.bitRate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.sampleRate = IsoTypeReader.readUInt32(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.maxSamplePerFrame);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.unknown1);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.sampleSize);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.historyMult);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.initialHistory);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.kModifier);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.channels);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.unknown2);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.maxCodedFrameSize);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.bitRate);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.sampleRate);
  }

  protected long getContentSize()
  {
    return 28L;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox
 * JD-Core Version:    0.5.4
 */