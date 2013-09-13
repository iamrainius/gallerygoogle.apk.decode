package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractBox;
import java.nio.ByteBuffer;

public class AmrSpecificBox extends AbstractBox
{
  private int decoderVersion;
  private int framesPerSample;
  private int modeChangePeriod;
  private int modeSet;
  private String vendor;

  public AmrSpecificBox()
  {
    super("damr");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    byte[] arrayOfByte = new byte[4];
    paramByteBuffer.get(arrayOfByte);
    this.vendor = IsoFile.bytesToFourCC(arrayOfByte);
    this.decoderVersion = IsoTypeReader.readUInt8(paramByteBuffer);
    this.modeSet = IsoTypeReader.readUInt16(paramByteBuffer);
    this.modeChangePeriod = IsoTypeReader.readUInt8(paramByteBuffer);
    this.framesPerSample = IsoTypeReader.readUInt8(paramByteBuffer);
  }

  public void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.vendor));
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.decoderVersion);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.modeSet);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.modeChangePeriod);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.framesPerSample);
  }

  protected long getContentSize()
  {
    return 9L;
  }

  public int getDecoderVersion()
  {
    return this.decoderVersion;
  }

  public int getFramesPerSample()
  {
    return this.framesPerSample;
  }

  public int getModeChangePeriod()
  {
    return this.modeChangePeriod;
  }

  public int getModeSet()
  {
    return this.modeSet;
  }

  public String getVendor()
  {
    return this.vendor;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("AmrSpecificBox[vendor=").append(getVendor());
    localStringBuilder.append(";decoderVersion=").append(getDecoderVersion());
    localStringBuilder.append(";modeSet=").append(getModeSet());
    localStringBuilder.append(";modeChangePeriod=").append(getModeChangePeriod());
    localStringBuilder.append(";framesPerSample=").append(getFramesPerSample());
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.sampleentry.AmrSpecificBox
 * JD-Core Version:    0.5.4
 */