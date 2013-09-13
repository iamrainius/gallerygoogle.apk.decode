package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ProgressiveDownloadInformationBox extends AbstractFullBox
{
  List<Entry> entries = Collections.emptyList();

  public ProgressiveDownloadInformationBox()
  {
    super("pdin");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.entries = new LinkedList();
    while (paramByteBuffer.remaining() >= 8)
    {
      Entry localEntry = new Entry(IsoTypeReader.readUInt32(paramByteBuffer), IsoTypeReader.readUInt32(paramByteBuffer));
      this.entries.add(localEntry);
    }
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    Iterator localIterator = this.entries.iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getRate());
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getInitialDelay());
    }
  }

  protected long getContentSize()
  {
    return 4 + 8 * this.entries.size();
  }

  public String toString()
  {
    return "ProgressiveDownloadInfoBox{entries=" + this.entries + '}';
  }

  public static class Entry
  {
    long initialDelay;
    long rate;

    public Entry(long paramLong1, long paramLong2)
    {
      this.rate = paramLong1;
      this.initialDelay = paramLong2;
    }

    public long getInitialDelay()
    {
      return this.initialDelay;
    }

    public long getRate()
    {
      return this.rate;
    }

    public String toString()
    {
      return "Entry{rate=" + this.rate + ", initialDelay=" + this.initialDelay + '}';
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.ProgressiveDownloadInformationBox
 * JD-Core Version:    0.5.4
 */