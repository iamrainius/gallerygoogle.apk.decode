package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TimeToSampleBox extends AbstractFullBox
{
  List<Entry> entries = Collections.emptyList();

  static
  {
    if (!TimeToSampleBox.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public TimeToSampleBox()
  {
    super("stts");
  }

  public static long[] blowupTimeToSamples(List<Entry> paramList)
  {
    long l = 0L;
    Iterator localIterator1 = paramList.iterator();
    while (localIterator1.hasNext())
      l += ((Entry)localIterator1.next()).getCount();
    assert (l <= 2147483647L);
    long[] arrayOfLong = new long[(int)l];
    int i = 0;
    Iterator localIterator2 = paramList.iterator();
    if (localIterator2.hasNext())
    {
      Entry localEntry = (Entry)localIterator2.next();
      int j = 0;
      while (true)
      {
        if (j < localEntry.getCount());
        int k = i + 1;
        arrayOfLong[i] = localEntry.getDelta();
        ++j;
        i = k;
      }
    }
    return arrayOfLong;
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int i = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.entries = new ArrayList(i);
    for (int j = 0; j < i; ++j)
      this.entries.add(new Entry(IsoTypeReader.readUInt32(paramByteBuffer), IsoTypeReader.readUInt32(paramByteBuffer)));
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.entries.size());
    Iterator localIterator = this.entries.iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getCount());
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getDelta());
    }
  }

  protected long getContentSize()
  {
    return 8 + 8 * this.entries.size();
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
    return "TimeToSampleBox[entryCount=" + this.entries.size() + "]";
  }

  public static class Entry
  {
    long count;
    long delta;

    public Entry(long paramLong1, long paramLong2)
    {
      this.count = paramLong1;
      this.delta = paramLong2;
    }

    public long getCount()
    {
      return this.count;
    }

    public long getDelta()
    {
      return this.delta;
    }

    public void setCount(long paramLong)
    {
      this.count = paramLong;
    }

    public String toString()
    {
      return "Entry{count=" + this.count + ", delta=" + this.delta + '}';
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.TimeToSampleBox
 * JD-Core Version:    0.5.4
 */