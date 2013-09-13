package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SampleToGroupBox extends AbstractFullBox
{
  List<Entry> entries = new LinkedList();
  private String groupingType;
  private String groupingTypeParameter;

  public SampleToGroupBox()
  {
    super("sbgp");
  }

  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.groupingType = IsoTypeReader.read4cc(paramByteBuffer);
    if (getVersion() == 1)
      this.groupingTypeParameter = IsoTypeReader.read4cc(paramByteBuffer);
    long l1 = IsoTypeReader.readUInt32(paramByteBuffer);
    while (true)
    {
      long l2 = l1 - 1L;
      if (l1 <= 0L)
        return;
      this.entries.add(new Entry(CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer)), CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer))));
      l1 = l2;
    }
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(this.groupingType.getBytes());
    if (getVersion() == 1)
      paramByteBuffer.put(this.groupingTypeParameter.getBytes());
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.entries.size());
    Iterator localIterator = this.entries.iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getSampleCount());
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getGroupDescriptionIndex());
    }
  }

  protected long getContentSize()
  {
    if (getVersion() == 1)
      return 16 + 8 * this.entries.size();
    return 12 + 8 * this.entries.size();
  }

  public static class Entry
  {
    private int groupDescriptionIndex;
    private long sampleCount;

    public Entry(long paramLong, int paramInt)
    {
      this.sampleCount = paramLong;
      this.groupDescriptionIndex = paramInt;
    }

    public boolean equals(Object paramObject)
    {
      if (this == paramObject);
      Entry localEntry;
      do
      {
        return true;
        if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
          return false;
        localEntry = (Entry)paramObject;
        if (this.groupDescriptionIndex != localEntry.groupDescriptionIndex)
          return false;
      }
      while (this.sampleCount == localEntry.sampleCount);
      return false;
    }

    public int getGroupDescriptionIndex()
    {
      return this.groupDescriptionIndex;
    }

    public long getSampleCount()
    {
      return this.sampleCount;
    }

    public int hashCode()
    {
      return 31 * (int)(this.sampleCount ^ this.sampleCount >>> 32) + this.groupDescriptionIndex;
    }

    public String toString()
    {
      return "Entry{sampleCount=" + this.sampleCount + ", groupDescriptionIndex=" + this.groupDescriptionIndex + '}';
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleToGroupBox
 * JD-Core Version:    0.5.4
 */