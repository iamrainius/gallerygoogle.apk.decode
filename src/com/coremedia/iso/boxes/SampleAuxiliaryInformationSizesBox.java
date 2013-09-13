package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SampleAuxiliaryInformationSizesBox extends AbstractFullBox
{
  private String auxInfoType;
  private String auxInfoTypeParameter;
  private int defaultSampleInfoSize;
  private int sampleCount;
  private List<Short> sampleInfoSizes = new LinkedList();

  static
  {
    if (!SampleAuxiliaryInformationSizesBox.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public SampleAuxiliaryInformationSizesBox()
  {
    super("saiz");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    if ((0x1 & getFlags()) == 1)
    {
      this.auxInfoType = IsoTypeReader.read4cc(paramByteBuffer);
      this.auxInfoTypeParameter = IsoTypeReader.read4cc(paramByteBuffer);
    }
    this.defaultSampleInfoSize = (short)IsoTypeReader.readUInt8(paramByteBuffer);
    this.sampleCount = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.sampleInfoSizes.clear();
    if (this.defaultSampleInfoSize != 0)
      return;
    for (int i = 0; i < this.sampleCount; ++i)
      this.sampleInfoSizes.add(Short.valueOf((short)IsoTypeReader.readUInt8(paramByteBuffer)));
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    if ((0x1 & getFlags()) == 1)
    {
      paramByteBuffer.put(IsoFile.fourCCtoBytes(this.auxInfoType));
      paramByteBuffer.put(IsoFile.fourCCtoBytes(this.auxInfoTypeParameter));
    }
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.defaultSampleInfoSize);
    if (this.defaultSampleInfoSize == 0)
    {
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.sampleInfoSizes.size());
      Iterator localIterator = this.sampleInfoSizes.iterator();
      while (true)
      {
        if (!localIterator.hasNext())
          return;
        IsoTypeWriter.writeUInt8(paramByteBuffer, ((Short)localIterator.next()).shortValue());
      }
    }
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.sampleCount);
  }

  protected long getContentSize()
  {
    int i = 4;
    if ((0x1 & getFlags()) == 1)
      i += 8;
    int j = i + 5;
    if (this.defaultSampleInfoSize == 0);
    for (int k = this.sampleInfoSizes.size(); ; k = 0)
      return j + k;
  }

  public String toString()
  {
    return "SampleAuxiliaryInformationSizesBox{defaultSampleInfoSize=" + this.defaultSampleInfoSize + ", sampleCount=" + this.sampleCount + ", auxInfoType='" + this.auxInfoType + '\'' + ", auxInfoTypeParameter='" + this.auxInfoTypeParameter + '\'' + '}';
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.SampleAuxiliaryInformationSizesBox
 * JD-Core Version:    0.5.4
 */