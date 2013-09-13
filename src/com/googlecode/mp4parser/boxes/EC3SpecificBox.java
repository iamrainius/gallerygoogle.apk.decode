package com.googlecode.mp4parser.boxes;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EC3SpecificBox extends AbstractBox
{
  int dataRate;
  List<Entry> entries = new LinkedList();
  int numIndSub;

  public EC3SpecificBox()
  {
    super("dec3");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    BitReaderBuffer localBitReaderBuffer = new BitReaderBuffer(paramByteBuffer);
    this.dataRate = localBitReaderBuffer.readBits(13);
    this.numIndSub = (1 + localBitReaderBuffer.readBits(3));
    int i = 0;
    if (i >= this.numIndSub)
      label32: return;
    Entry localEntry = new Entry();
    localEntry.fscod = localBitReaderBuffer.readBits(2);
    localEntry.bsid = localBitReaderBuffer.readBits(5);
    localEntry.bsmod = localBitReaderBuffer.readBits(5);
    localEntry.acmod = localBitReaderBuffer.readBits(3);
    localEntry.lfeon = localBitReaderBuffer.readBits(1);
    localEntry.reserved = localBitReaderBuffer.readBits(3);
    localEntry.num_dep_sub = localBitReaderBuffer.readBits(4);
    if (localEntry.num_dep_sub > 0)
      localEntry.chan_loc = localBitReaderBuffer.readBits(9);
    while (true)
    {
      this.entries.add(localEntry);
      ++i;
      break label32:
      localEntry.reserved2 = localBitReaderBuffer.readBits(1);
    }
  }

  public void getContent(ByteBuffer paramByteBuffer)
  {
    BitWriterBuffer localBitWriterBuffer = new BitWriterBuffer(paramByteBuffer);
    localBitWriterBuffer.writeBits(this.dataRate, 13);
    localBitWriterBuffer.writeBits(-1 + this.entries.size(), 3);
    Iterator localIterator = this.entries.iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      localBitWriterBuffer.writeBits(localEntry.fscod, 2);
      localBitWriterBuffer.writeBits(localEntry.bsid, 5);
      localBitWriterBuffer.writeBits(localEntry.bsmod, 5);
      localBitWriterBuffer.writeBits(localEntry.acmod, 3);
      localBitWriterBuffer.writeBits(localEntry.lfeon, 1);
      localBitWriterBuffer.writeBits(localEntry.reserved, 3);
      localBitWriterBuffer.writeBits(localEntry.num_dep_sub, 4);
      if (localEntry.num_dep_sub > 0)
        localBitWriterBuffer.writeBits(localEntry.chan_loc, 9);
      localBitWriterBuffer.writeBits(localEntry.reserved2, 1);
    }
  }

  public long getContentSize()
  {
    long l = 2L;
    Iterator localIterator = this.entries.iterator();
    while (localIterator.hasNext())
    {
      if (((Entry)localIterator.next()).num_dep_sub > 0)
        l += 4L;
      l += 3L;
    }
    return l;
  }

  public static class Entry
  {
    public int acmod;
    public int bsid;
    public int bsmod;
    public int chan_loc;
    public int fscod;
    public int lfeon;
    public int num_dep_sub;
    public int reserved;
    public int reserved2;

    public String toString()
    {
      return "Entry{fscod=" + this.fscod + ", bsid=" + this.bsid + ", bsmod=" + this.bsmod + ", acmod=" + this.acmod + ", lfeon=" + this.lfeon + ", reserved=" + this.reserved + ", num_dep_sub=" + this.num_dep_sub + ", chan_loc=" + this.chan_loc + ", reserved2=" + this.reserved2 + '}';
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.EC3SpecificBox
 * JD-Core Version:    0.5.4
 */