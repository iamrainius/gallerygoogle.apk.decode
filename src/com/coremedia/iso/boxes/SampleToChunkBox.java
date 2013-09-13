package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SampleToChunkBox extends AbstractFullBox
{
  List<Entry> entries = Collections.emptyList();

  public SampleToChunkBox()
  {
    super("stsc");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int i = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.entries = new ArrayList(i);
    for (int j = 0; j < i; ++j)
      this.entries.add(new Entry(IsoTypeReader.readUInt32(paramByteBuffer), IsoTypeReader.readUInt32(paramByteBuffer), IsoTypeReader.readUInt32(paramByteBuffer)));
  }

  public long[] blowup(int paramInt)
  {
    long[] arrayOfLong = new long[paramInt];
    LinkedList localLinkedList = new LinkedList(this.entries);
    Collections.reverse(localLinkedList);
    Iterator localIterator = localLinkedList.iterator();
    Entry localEntry = (Entry)localIterator.next();
    for (int i = arrayOfLong.length; i > 1; --i)
    {
      arrayOfLong[(i - 1)] = localEntry.getSamplesPerChunk();
      if (i != localEntry.getFirstChunk())
        continue;
      localEntry = (Entry)localIterator.next();
    }
    arrayOfLong[0] = localEntry.getSamplesPerChunk();
    return arrayOfLong;
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.entries.size());
    Iterator localIterator = this.entries.iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getFirstChunk());
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getSamplesPerChunk());
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getSampleDescriptionIndex());
    }
  }

  protected long getContentSize()
  {
    return 8 + 12 * this.entries.size();
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
    return "SampleToChunkBox[entryCount=" + this.entries.size() + "]";
  }

  public static class Entry
  {
    long firstChunk;
    long sampleDescriptionIndex;
    long samplesPerChunk;

    public Entry(long paramLong1, long paramLong2, long paramLong3)
    {
      this.firstChunk = paramLong1;
      this.samplesPerChunk = paramLong2;
      this.sampleDescriptionIndex = paramLong3;
    }

    public long getFirstChunk()
    {
      return this.firstChunk;
    }

    public long getSampleDescriptionIndex()
    {
      return this.sampleDescriptionIndex;
    }

    public long getSamplesPerChunk()
    {
      return this.samplesPerChunk;
    }

    public String toString()
    {
      return "Entry{firstChunk=" + this.firstChunk + ", samplesPerChunk=" + this.samplesPerChunk + ", sampleDescriptionIndex=" + this.sampleDescriptionIndex + '}';
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.SampleToChunkBox
 * JD-Core Version:    0.5.4
 */