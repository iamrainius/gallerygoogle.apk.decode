package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SampleAuxiliaryInformationOffsetsBox extends AbstractFullBox
{
  private long auxInfoType;
  private long auxInfoTypeParameter;
  private List<Long> offsets = new LinkedList();

  public SampleAuxiliaryInformationOffsetsBox()
  {
    super("saio");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    if ((0x1 & getFlags()) == 1)
    {
      this.auxInfoType = IsoTypeReader.readUInt32(paramByteBuffer);
      this.auxInfoTypeParameter = IsoTypeReader.readUInt32(paramByteBuffer);
    }
    int i = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.offsets.clear();
    int j = 0;
    if (j >= i)
      label53: return;
    if (getVersion() == 0)
      this.offsets.add(Long.valueOf(IsoTypeReader.readUInt32(paramByteBuffer)));
    while (true)
    {
      ++j;
      break label53:
      this.offsets.add(Long.valueOf(IsoTypeReader.readUInt64(paramByteBuffer)));
    }
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    if ((0x1 & getFlags()) == 1)
    {
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.auxInfoType);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.auxInfoTypeParameter);
    }
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.offsets.size());
    Iterator localIterator = this.offsets.iterator();
    while (localIterator.hasNext())
    {
      Long localLong = (Long)localIterator.next();
      if (getVersion() == 0)
        IsoTypeWriter.writeUInt32(paramByteBuffer, localLong.longValue());
      IsoTypeWriter.writeUInt64(paramByteBuffer, localLong.longValue());
    }
  }

  protected long getContentSize()
  {
    int i;
    label19: int j;
    if (getVersion() == 0)
    {
      i = 4 * this.offsets.size();
      j = i + 8;
      if ((0x1 & getFlags()) != 1)
        break label58;
    }
    for (int k = 8; ; k = 0)
    {
      return k + j;
      i = 8 * this.offsets.size();
      label58: break label19:
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.SampleAuxiliaryInformationOffsetsBox
 * JD-Core Version:    0.5.4
 */