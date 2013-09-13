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

public class CompositionTimeToSample extends AbstractFullBox
{
  List<Entry> entries = Collections.emptyList();

  static
  {
    if (!CompositionTimeToSample.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public CompositionTimeToSample()
  {
    super("ctts");
  }

  public static int[] blowupCompositionTimes(List<Entry> paramList)
  {
    long l = 0L;
    Iterator localIterator1 = paramList.iterator();
    while (localIterator1.hasNext())
      l += ((Entry)localIterator1.next()).getCount();
    assert (l <= 2147483647L);
    int[] arrayOfInt = new int[(int)l];
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
        arrayOfInt[i] = localEntry.getOffset();
        ++j;
        i = k;
      }
    }
    return arrayOfInt;
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int i = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.entries = new ArrayList(i);
    for (int j = 0; j < i; ++j)
    {
      Entry localEntry = new Entry(CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer)), paramByteBuffer.getInt());
      this.entries.add(localEntry);
    }
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
      paramByteBuffer.putInt(localEntry.getOffset());
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

  public static class Entry
  {
    int count;
    int offset;

    public Entry(int paramInt1, int paramInt2)
    {
      this.count = paramInt1;
      this.offset = paramInt2;
    }

    public int getCount()
    {
      return this.count;
    }

    public int getOffset()
    {
      return this.offset;
    }

    public void setCount(int paramInt)
    {
      this.count = paramInt;
    }

    public String toString()
    {
      return "Entry{count=" + this.count + ", offset=" + this.offset + '}';
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.CompositionTimeToSample
 * JD-Core Version:    0.5.4
 */