package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SampleDependencyTypeBox extends AbstractFullBox
{
  private List<Entry> entries = new ArrayList();

  public SampleDependencyTypeBox()
  {
    super("sdtp");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    while (paramByteBuffer.remaining() > 0)
      this.entries.add(new Entry(IsoTypeReader.readUInt8(paramByteBuffer)));
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    Iterator localIterator = this.entries.iterator();
    while (localIterator.hasNext())
      IsoTypeWriter.writeUInt8(paramByteBuffer, ((Entry)localIterator.next()).value);
  }

  protected long getContentSize()
  {
    return 4 + this.entries.size();
  }

  public List<Entry> getEntries()
  {
    return this.entries;
  }

  public void setEntries(List<Entry> paramList)
  {
    this.entries = paramList;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("SampleDependencyTypeBox");
    localStringBuilder.append("{entries=").append(this.entries);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }

  public static class Entry
  {
    private int value;

    public Entry(int paramInt)
    {
      this.value = paramInt;
    }

    public int getReserved()
    {
      return 0x3 & this.value >> 6;
    }

    public int getSampleDependsOn()
    {
      return 0x3 & this.value >> 4;
    }

    public int getSampleHasRedundancy()
    {
      return 0x3 & this.value;
    }

    public int getSampleIsDependentOn()
    {
      return 0x3 & this.value >> 2;
    }

    public String toString()
    {
      return "Entry{reserved=" + getReserved() + ", sampleDependsOn=" + getSampleDependsOn() + ", sampleIsDependentOn=" + getSampleIsDependentOn() + ", sampleHasRedundancy=" + getSampleHasRedundancy() + '}';
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.SampleDependencyTypeBox
 * JD-Core Version:    0.5.4
 */