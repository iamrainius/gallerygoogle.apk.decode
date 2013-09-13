package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SubSampleInformationBox extends AbstractFullBox
{
  private List<SampleEntry> entries = new ArrayList();
  private long entryCount;

  public SubSampleInformationBox()
  {
    super("subs");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.entryCount = IsoTypeReader.readUInt32(paramByteBuffer);
    for (int i = 0; i < this.entryCount; ++i)
    {
      SampleEntry localSampleEntry = new SampleEntry();
      localSampleEntry.setSampleDelta(IsoTypeReader.readUInt32(paramByteBuffer));
      int j = IsoTypeReader.readUInt16(paramByteBuffer);
      int k = 0;
      if (k < j)
      {
        label55: SubSampleInformationBox.SampleEntry.SubsampleEntry localSubsampleEntry = new SubSampleInformationBox.SampleEntry.SubsampleEntry();
        long l;
        if (getVersion() == 1)
          l = IsoTypeReader.readUInt32(paramByteBuffer);
        while (true)
        {
          localSubsampleEntry.setSubsampleSize(l);
          localSubsampleEntry.setSubsamplePriority(IsoTypeReader.readUInt8(paramByteBuffer));
          localSubsampleEntry.setDiscardable(IsoTypeReader.readUInt8(paramByteBuffer));
          localSubsampleEntry.setReserved(IsoTypeReader.readUInt32(paramByteBuffer));
          localSampleEntry.addSubsampleEntry(localSubsampleEntry);
          ++k;
          break label55:
          l = IsoTypeReader.readUInt16(paramByteBuffer);
        }
      }
      this.entries.add(localSampleEntry);
    }
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.entries.size());
    Iterator localIterator1 = this.entries.iterator();
    Iterator localIterator2;
    do
    {
      if (!localIterator1.hasNext())
        return;
      SampleEntry localSampleEntry = (SampleEntry)localIterator1.next();
      IsoTypeWriter.writeUInt32(paramByteBuffer, localSampleEntry.getSampleDelta());
      IsoTypeWriter.writeUInt16(paramByteBuffer, localSampleEntry.getSubsampleCount());
      label75: localIterator2 = localSampleEntry.getSubsampleEntries().iterator();
    }
    while (!localIterator2.hasNext());
    SubSampleInformationBox.SampleEntry.SubsampleEntry localSubsampleEntry = (SubSampleInformationBox.SampleEntry.SubsampleEntry)localIterator2.next();
    if (getVersion() == 1)
      IsoTypeWriter.writeUInt32(paramByteBuffer, localSubsampleEntry.getSubsampleSize());
    while (true)
    {
      IsoTypeWriter.writeUInt8(paramByteBuffer, localSubsampleEntry.getSubsamplePriority());
      IsoTypeWriter.writeUInt8(paramByteBuffer, localSubsampleEntry.getDiscardable());
      IsoTypeWriter.writeUInt32(paramByteBuffer, localSubsampleEntry.getReserved());
      break label75:
      IsoTypeWriter.writeUInt16(paramByteBuffer, CastUtils.l2i(localSubsampleEntry.getSubsampleSize()));
    }
  }

  protected long getContentSize()
  {
    long l = 8L + 6L * this.entryCount;
    int i = 0;
    Iterator localIterator = this.entries.iterator();
    if (localIterator.hasNext())
    {
      label26: int j = ((SampleEntry)localIterator.next()).getSubsampleCount();
      if (getVersion() == 1);
      for (int k = 4; ; k = 2)
      {
        i += j * (4 + (1 + (k + 1)));
        break label26:
      }
    }
    return l + i;
  }

  public String toString()
  {
    return "SubSampleInformationBox{entryCount=" + this.entryCount + ", entries=" + this.entries + '}';
  }

  public static class SampleEntry
  {
    private long sampleDelta;
    private int subsampleCount;
    private List<SubsampleEntry> subsampleEntries = new ArrayList();

    public void addSubsampleEntry(SubsampleEntry paramSubsampleEntry)
    {
      this.subsampleEntries.add(paramSubsampleEntry);
      this.subsampleCount = (1 + this.subsampleCount);
    }

    public long getSampleDelta()
    {
      return this.sampleDelta;
    }

    public int getSubsampleCount()
    {
      return this.subsampleCount;
    }

    public List<SubsampleEntry> getSubsampleEntries()
    {
      return this.subsampleEntries;
    }

    public void setSampleDelta(long paramLong)
    {
      this.sampleDelta = paramLong;
    }

    public String toString()
    {
      return "SampleEntry{sampleDelta=" + this.sampleDelta + ", subsampleCount=" + this.subsampleCount + ", subsampleEntries=" + this.subsampleEntries + '}';
    }

    public static class SubsampleEntry
    {
      private int discardable;
      private long reserved;
      private int subsamplePriority;
      private long subsampleSize;

      public int getDiscardable()
      {
        return this.discardable;
      }

      public long getReserved()
      {
        return this.reserved;
      }

      public int getSubsamplePriority()
      {
        return this.subsamplePriority;
      }

      public long getSubsampleSize()
      {
        return this.subsampleSize;
      }

      public void setDiscardable(int paramInt)
      {
        this.discardable = paramInt;
      }

      public void setReserved(long paramLong)
      {
        this.reserved = paramLong;
      }

      public void setSubsamplePriority(int paramInt)
      {
        this.subsamplePriority = paramInt;
      }

      public void setSubsampleSize(long paramLong)
      {
        this.subsampleSize = paramLong;
      }

      public String toString()
      {
        return "SubsampleEntry{subsampleSize=" + this.subsampleSize + ", subsamplePriority=" + this.subsamplePriority + ", discardable=" + this.discardable + ", reserved=" + this.reserved + '}';
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.SubSampleInformationBox
 * JD-Core Version:    0.5.4
 */