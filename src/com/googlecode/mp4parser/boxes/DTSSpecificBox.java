package com.googlecode.mp4parser.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import java.nio.ByteBuffer;

public class DTSSpecificBox extends AbstractBox
{
  long DTSSamplingFrequency;
  int LBRDurationMod;
  long avgBitRate;
  int channelLayout;
  int coreLFEPresent;
  int coreLayout;
  int coreSize;
  int frameDuration;
  long maxBitRate;
  int multiAssetFlag;
  int pcmSampleDepth;
  int representationType;
  int reserved;
  int reservedBoxPresent;
  int stereoDownmix;
  int streamConstruction;

  public DTSSpecificBox()
  {
    super("ddts");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.DTSSamplingFrequency = IsoTypeReader.readUInt32(paramByteBuffer);
    this.maxBitRate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.avgBitRate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.pcmSampleDepth = IsoTypeReader.readUInt8(paramByteBuffer);
    BitReaderBuffer localBitReaderBuffer = new BitReaderBuffer(paramByteBuffer);
    this.frameDuration = localBitReaderBuffer.readBits(2);
    this.streamConstruction = localBitReaderBuffer.readBits(5);
    this.coreLFEPresent = localBitReaderBuffer.readBits(1);
    this.coreLayout = localBitReaderBuffer.readBits(6);
    this.coreSize = localBitReaderBuffer.readBits(14);
    this.stereoDownmix = localBitReaderBuffer.readBits(1);
    this.representationType = localBitReaderBuffer.readBits(3);
    this.channelLayout = localBitReaderBuffer.readBits(16);
    this.multiAssetFlag = localBitReaderBuffer.readBits(1);
    this.LBRDurationMod = localBitReaderBuffer.readBits(1);
    this.reservedBoxPresent = localBitReaderBuffer.readBits(1);
    this.reserved = localBitReaderBuffer.readBits(5);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.DTSSamplingFrequency);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.maxBitRate);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.avgBitRate);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.pcmSampleDepth);
    BitWriterBuffer localBitWriterBuffer = new BitWriterBuffer(paramByteBuffer);
    localBitWriterBuffer.writeBits(this.frameDuration, 2);
    localBitWriterBuffer.writeBits(this.streamConstruction, 5);
    localBitWriterBuffer.writeBits(this.coreLFEPresent, 1);
    localBitWriterBuffer.writeBits(this.coreLayout, 6);
    localBitWriterBuffer.writeBits(this.coreSize, 14);
    localBitWriterBuffer.writeBits(this.stereoDownmix, 1);
    localBitWriterBuffer.writeBits(this.representationType, 3);
    localBitWriterBuffer.writeBits(this.channelLayout, 16);
    localBitWriterBuffer.writeBits(this.multiAssetFlag, 1);
    localBitWriterBuffer.writeBits(this.LBRDurationMod, 1);
    localBitWriterBuffer.writeBits(this.reservedBoxPresent, 1);
    localBitWriterBuffer.writeBits(this.reserved, 5);
  }

  protected long getContentSize()
  {
    return 20L;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.DTSSpecificBox
 * JD-Core Version:    0.5.4
 */