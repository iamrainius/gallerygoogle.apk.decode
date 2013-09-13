package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import com.googlecode.mp4parser.AbstractBox;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

public class AudioSampleEntry extends SampleEntry
  implements ContainerBox
{
  private long bytesPerFrame;
  private long bytesPerPacket;
  private long bytesPerSample;
  private int channelCount;
  private int compressionId;
  private int packetSize;
  private int reserved1;
  private long reserved2;
  private long sampleRate;
  private int sampleSize;
  private long samplesPerPacket;
  private int soundVersion;
  private byte[] soundVersion2Data;

  public AudioSampleEntry(String paramString)
  {
    super(paramString);
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    _parseReservedAndDataReferenceIndex(paramByteBuffer);
    this.soundVersion = IsoTypeReader.readUInt16(paramByteBuffer);
    this.reserved1 = IsoTypeReader.readUInt16(paramByteBuffer);
    this.reserved2 = IsoTypeReader.readUInt32(paramByteBuffer);
    this.channelCount = IsoTypeReader.readUInt16(paramByteBuffer);
    this.sampleSize = IsoTypeReader.readUInt16(paramByteBuffer);
    this.compressionId = IsoTypeReader.readUInt16(paramByteBuffer);
    this.packetSize = IsoTypeReader.readUInt16(paramByteBuffer);
    this.sampleRate = IsoTypeReader.readUInt32(paramByteBuffer);
    if (!this.type.equals("mlpa"))
      this.sampleRate >>>= 16;
    if (this.soundVersion > 0)
    {
      this.samplesPerPacket = IsoTypeReader.readUInt32(paramByteBuffer);
      this.bytesPerPacket = IsoTypeReader.readUInt32(paramByteBuffer);
      this.bytesPerFrame = IsoTypeReader.readUInt32(paramByteBuffer);
      this.bytesPerSample = IsoTypeReader.readUInt32(paramByteBuffer);
    }
    if (this.soundVersion == 2)
    {
      this.soundVersion2Data = new byte[20];
      paramByteBuffer.get(20);
    }
    _parseChildBoxes(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    _writeReservedAndDataReferenceIndex(paramByteBuffer);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.soundVersion);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.reserved1);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.reserved2);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.channelCount);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.sampleSize);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.compressionId);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.packetSize);
    if (this.type.equals("mlpa"))
      IsoTypeWriter.writeUInt32(paramByteBuffer, getSampleRate());
    while (true)
    {
      if (this.soundVersion > 0)
      {
        IsoTypeWriter.writeUInt32(paramByteBuffer, this.samplesPerPacket);
        IsoTypeWriter.writeUInt32(paramByteBuffer, this.bytesPerPacket);
        IsoTypeWriter.writeUInt32(paramByteBuffer, this.bytesPerFrame);
        IsoTypeWriter.writeUInt32(paramByteBuffer, this.bytesPerSample);
      }
      if (this.soundVersion == 2)
        paramByteBuffer.put(this.soundVersion2Data);
      _writeChildBoxes(paramByteBuffer);
      return;
      IsoTypeWriter.writeUInt32(paramByteBuffer, getSampleRate() << 16);
    }
  }

  protected long getContentSize()
  {
    long l1 = 0L;
    long l2;
    if (this.soundVersion > 0)
      l2 = 16L;
    long l4;
    while (true)
    {
      long l3 = 28L + l2;
      if (this.soundVersion == 2)
        l1 = 20L;
      l4 = l3 + l1;
      Iterator localIterator = this.boxes.iterator();
      while (true)
      {
        if (!localIterator.hasNext())
          break label87;
        l4 += ((Box)localIterator.next()).getSize();
      }
      l2 = l1;
    }
    label87: return l4;
  }

  public long getSampleRate()
  {
    return this.sampleRate;
  }

  public String toString()
  {
    return "AudioSampleEntry{bytesPerSample=" + this.bytesPerSample + ", bytesPerFrame=" + this.bytesPerFrame + ", bytesPerPacket=" + this.bytesPerPacket + ", samplesPerPacket=" + this.samplesPerPacket + ", packetSize=" + this.packetSize + ", compressionId=" + this.compressionId + ", soundVersion=" + this.soundVersion + ", sampleRate=" + this.sampleRate + ", sampleSize=" + this.sampleSize + ", channelCount=" + this.channelCount + ", boxes=" + getBoxes() + '}';
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.sampleentry.AudioSampleEntry
 * JD-Core Version:    0.5.4
 */