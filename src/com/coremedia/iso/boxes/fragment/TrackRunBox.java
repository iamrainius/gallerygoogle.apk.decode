package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TrackRunBox extends AbstractFullBox
{
  private int dataOffset;
  private List<Entry> entries = new ArrayList();
  private SampleFlags firstSampleFlags;

  public TrackRunBox()
  {
    super("trun");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    long l = IsoTypeReader.readUInt32(paramByteBuffer);
    if ((0x1 & getFlags()) == 1);
    for (this.dataOffset = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer)); ; this.dataOffset = -1)
    {
      if ((0x4 & getFlags()) == 4)
        this.firstSampleFlags = new SampleFlags(paramByteBuffer);
      for (int i = 0; ; ++i)
      {
        if (i >= l)
          return;
        Entry localEntry = new Entry();
        if ((0x100 & getFlags()) == 256)
          Entry.access$002(localEntry, IsoTypeReader.readUInt32(paramByteBuffer));
        if ((0x200 & getFlags()) == 512)
          Entry.access$102(localEntry, IsoTypeReader.readUInt32(paramByteBuffer));
        if ((0x400 & getFlags()) == 1024)
          Entry.access$202(localEntry, new SampleFlags(paramByteBuffer));
        if ((0x800 & getFlags()) == 2048)
          Entry.access$302(localEntry, paramByteBuffer.getInt());
        this.entries.add(localEntry);
      }
    }
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.entries.size());
    int i = getFlags();
    if ((i & 0x1) == 1)
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.dataOffset);
    if ((i & 0x4) == 4)
      this.firstSampleFlags.getContent(paramByteBuffer);
    Iterator localIterator = this.entries.iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      if ((i & 0x100) == 256)
        IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.sampleDuration);
      if ((i & 0x200) == 512)
        IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.sampleSize);
      if ((i & 0x400) == 1024)
        localEntry.sampleFlags.getContent(paramByteBuffer);
      if ((i & 0x800) != 2048)
        continue;
      paramByteBuffer.putInt(localEntry.sampleCompositionTimeOffset);
    }
  }

  protected long getContentSize()
  {
    long l1 = 8L;
    int i = getFlags();
    if ((i & 0x1) == 1)
      l1 += 4L;
    if ((i & 0x4) == 4)
      l1 += 4L;
    long l2 = 0L;
    if ((i & 0x100) == 256)
      l2 += 4L;
    if ((i & 0x200) == 512)
      l2 += 4L;
    if ((i & 0x400) == 1024)
      l2 += 4L;
    if ((i & 0x800) == 2048)
      l2 += 4L;
    return l1 + l2 * this.entries.size();
  }

  public int getDataOffset()
  {
    return this.dataOffset;
  }

  public List<Entry> getEntries()
  {
    return this.entries;
  }

  public SampleFlags getFirstSampleFlags()
  {
    return this.firstSampleFlags;
  }

  public boolean isDataOffsetPresent()
  {
    return (0x1 & getFlags()) == 1;
  }

  public boolean isFirstSampleFlagsPresent()
  {
    return (0x4 & getFlags()) == 4;
  }

  public boolean isSampleCompositionTimeOffsetPresent()
  {
    return (0x800 & getFlags()) == 2048;
  }

  public boolean isSampleDurationPresent()
  {
    return (0x100 & getFlags()) == 256;
  }

  public boolean isSampleFlagsPresent()
  {
    return (0x400 & getFlags()) == 1024;
  }

  public boolean isSampleSizePresent()
  {
    return (0x200 & getFlags()) == 512;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("TrackRunBox");
    localStringBuilder.append("{sampleCount=").append(this.entries.size());
    localStringBuilder.append(", dataOffset=").append(this.dataOffset);
    localStringBuilder.append(", dataOffsetPresent=").append(isDataOffsetPresent());
    localStringBuilder.append(", sampleSizePresent=").append(isSampleSizePresent());
    localStringBuilder.append(", sampleDurationPresent=").append(isSampleDurationPresent());
    localStringBuilder.append(", sampleFlagsPresentPresent=").append(isSampleFlagsPresent());
    localStringBuilder.append(", sampleCompositionTimeOffsetPresent=").append(isSampleCompositionTimeOffsetPresent());
    localStringBuilder.append(", firstSampleFlags=").append(this.firstSampleFlags);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }

  public static class Entry
  {
    private int sampleCompositionTimeOffset;
    private long sampleDuration;
    private SampleFlags sampleFlags;
    private long sampleSize;

    public int getSampleCompositionTimeOffset()
    {
      return this.sampleCompositionTimeOffset;
    }

    public long getSampleDuration()
    {
      return this.sampleDuration;
    }

    public SampleFlags getSampleFlags()
    {
      return this.sampleFlags;
    }

    public long getSampleSize()
    {
      return this.sampleSize;
    }

    public String toString()
    {
      return "Entry{sampleDuration=" + this.sampleDuration + ", sampleSize=" + this.sampleSize + ", sampleFlags=" + this.sampleFlags + ", sampleCompositionTimeOffset=" + this.sampleCompositionTimeOffset + '}';
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.fragment.TrackRunBox
 * JD-Core Version:    0.5.4
 */