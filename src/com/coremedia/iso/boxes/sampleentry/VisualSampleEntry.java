package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

public class VisualSampleEntry extends SampleEntry
  implements ContainerBox
{
  private String compressorname;
  private int depth = 24;
  private int frameCount = 1;
  private int height;
  private double horizresolution = 72.0D;
  private long[] predefined = new long[3];
  private double vertresolution = 72.0D;
  private int width;

  static
  {
    if (!VisualSampleEntry.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public VisualSampleEntry(String paramString)
  {
    super(paramString);
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    _parseReservedAndDataReferenceIndex(paramByteBuffer);
    long l1 = IsoTypeReader.readUInt16(paramByteBuffer);
    assert (0L == l1) : "reserved byte not 0";
    long l2 = IsoTypeReader.readUInt16(paramByteBuffer);
    assert (0L == l2) : "reserved byte not 0";
    this.predefined[0] = IsoTypeReader.readUInt32(paramByteBuffer);
    this.predefined[1] = IsoTypeReader.readUInt32(paramByteBuffer);
    this.predefined[2] = IsoTypeReader.readUInt32(paramByteBuffer);
    this.width = IsoTypeReader.readUInt16(paramByteBuffer);
    this.height = IsoTypeReader.readUInt16(paramByteBuffer);
    this.horizresolution = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
    this.vertresolution = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
    long l3 = IsoTypeReader.readUInt32(paramByteBuffer);
    assert (0L == l3) : "reserved byte not 0";
    this.frameCount = IsoTypeReader.readUInt16(paramByteBuffer);
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    if (i > 31)
    {
      System.out.println("invalid compressor name displayable data: " + i);
      i = 31;
    }
    byte[] arrayOfByte = new byte[i];
    paramByteBuffer.get(arrayOfByte);
    this.compressorname = Utf8.convert(arrayOfByte);
    if (i < 31)
      paramByteBuffer.get(new byte[31 - i]);
    this.depth = IsoTypeReader.readUInt16(paramByteBuffer);
    long l4 = IsoTypeReader.readUInt16(paramByteBuffer);
    assert (65535L == l4);
    _parseChildBoxes(paramByteBuffer);
  }

  public String getCompressorname()
  {
    return this.compressorname;
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    _writeReservedAndDataReferenceIndex(paramByteBuffer);
    IsoTypeWriter.writeUInt16(paramByteBuffer, 0);
    IsoTypeWriter.writeUInt16(paramByteBuffer, 0);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.predefined[0]);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.predefined[1]);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.predefined[2]);
    IsoTypeWriter.writeUInt16(paramByteBuffer, getWidth());
    IsoTypeWriter.writeUInt16(paramByteBuffer, getHeight());
    IsoTypeWriter.writeFixedPont1616(paramByteBuffer, getHorizresolution());
    IsoTypeWriter.writeFixedPont1616(paramByteBuffer, getVertresolution());
    IsoTypeWriter.writeUInt32(paramByteBuffer, 0L);
    IsoTypeWriter.writeUInt16(paramByteBuffer, getFrameCount());
    IsoTypeWriter.writeUInt8(paramByteBuffer, Utf8.utf8StringLengthInBytes(getCompressorname()));
    paramByteBuffer.put(Utf8.convert(getCompressorname()));
    int i = Utf8.utf8StringLengthInBytes(getCompressorname());
    while (i < 31)
    {
      ++i;
      paramByteBuffer.put(0);
    }
    IsoTypeWriter.writeUInt16(paramByteBuffer, getDepth());
    IsoTypeWriter.writeUInt16(paramByteBuffer, 65535);
    _writeChildBoxes(paramByteBuffer);
  }

  protected long getContentSize()
  {
    long l = 78L;
    Iterator localIterator = this.boxes.iterator();
    while (localIterator.hasNext())
      l += ((Box)localIterator.next()).getSize();
    return l;
  }

  public int getDepth()
  {
    return this.depth;
  }

  public int getFrameCount()
  {
    return this.frameCount;
  }

  public int getHeight()
  {
    return this.height;
  }

  public double getHorizresolution()
  {
    return this.horizresolution;
  }

  public double getVertresolution()
  {
    return this.vertresolution;
  }

  public int getWidth()
  {
    return this.width;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.sampleentry.VisualSampleEntry
 * JD-Core Version:    0.5.4
 */