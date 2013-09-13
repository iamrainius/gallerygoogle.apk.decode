package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeReaderVariable;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.IsoTypeWriterVariable;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TrackFragmentRandomAccessBox extends AbstractFullBox
{
  private List<Entry> entries = Collections.emptyList();
  private int lengthSizeOfSampleNum = 2;
  private int lengthSizeOfTrafNum = 2;
  private int lengthSizeOfTrunNum = 2;
  private int reserved;
  private long trackId;

  public TrackFragmentRandomAccessBox()
  {
    super("tfra");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.trackId = IsoTypeReader.readUInt32(paramByteBuffer);
    long l1 = IsoTypeReader.readUInt32(paramByteBuffer);
    this.reserved = (int)(l1 >> 6);
    this.lengthSizeOfTrafNum = (1 + ((int)(0x3F & l1) >> 4));
    this.lengthSizeOfTrunNum = (1 + ((int)(0xC & l1) >> 2));
    this.lengthSizeOfSampleNum = (1 + (int)(0x3 & l1));
    long l2 = IsoTypeReader.readUInt32(paramByteBuffer);
    this.entries = new ArrayList();
    int i = 0;
    if (i >= l2)
      label93: return;
    Entry localEntry = new Entry();
    if (getVersion() == 1)
    {
      Entry.access$002(localEntry, IsoTypeReader.readUInt64(paramByteBuffer));
      Entry.access$102(localEntry, IsoTypeReader.readUInt64(paramByteBuffer));
    }
    while (true)
    {
      Entry.access$202(localEntry, IsoTypeReaderVariable.read(paramByteBuffer, this.lengthSizeOfTrafNum));
      Entry.access$302(localEntry, IsoTypeReaderVariable.read(paramByteBuffer, this.lengthSizeOfTrunNum));
      Entry.access$402(localEntry, IsoTypeReaderVariable.read(paramByteBuffer, this.lengthSizeOfSampleNum));
      this.entries.add(localEntry);
      ++i;
      break label93:
      Entry.access$002(localEntry, IsoTypeReader.readUInt32(paramByteBuffer));
      Entry.access$102(localEntry, IsoTypeReader.readUInt32(paramByteBuffer));
    }
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.trackId);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.reserved << 6 | (0x3 & -1 + this.lengthSizeOfTrafNum) << 4 | (0x3 & -1 + this.lengthSizeOfTrunNum) << 2 | 0x3 & -1 + this.lengthSizeOfSampleNum);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.entries.size());
    Iterator localIterator = this.entries.iterator();
    if (!localIterator.hasNext())
      label83: return;
    Entry localEntry = (Entry)localIterator.next();
    if (getVersion() == 1)
    {
      IsoTypeWriter.writeUInt64(paramByteBuffer, localEntry.time);
      IsoTypeWriter.writeUInt64(paramByteBuffer, localEntry.moofOffset);
    }
    while (true)
    {
      IsoTypeWriterVariable.write(localEntry.trafNumber, paramByteBuffer, this.lengthSizeOfTrafNum);
      IsoTypeWriterVariable.write(localEntry.trunNumber, paramByteBuffer, this.lengthSizeOfTrunNum);
      IsoTypeWriterVariable.write(localEntry.sampleNumber, paramByteBuffer, this.lengthSizeOfSampleNum);
      break label83:
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.time);
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.moofOffset);
    }
  }

  protected long getContentSize()
  {
    long l1 = 4L + 12L;
    long l2;
    if (getVersion() == 1)
      l2 = l1 + 16 * this.entries.size();
    while (true)
    {
      return l2 + this.lengthSizeOfTrafNum * this.entries.size() + this.lengthSizeOfTrunNum * this.entries.size() + this.lengthSizeOfSampleNum * this.entries.size();
      l2 = l1 + 8 * this.entries.size();
    }
  }

  public String toString()
  {
    return "TrackFragmentRandomAccessBox{trackId=" + this.trackId + ", entries=" + this.entries + '}';
  }

  public static class Entry
  {
    private long moofOffset;
    private long sampleNumber;
    private long time;
    private long trafNumber;
    private long trunNumber;

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
        if (this.moofOffset != localEntry.moofOffset)
          return false;
        if (this.sampleNumber != localEntry.sampleNumber)
          return false;
        if (this.time != localEntry.time)
          return false;
        if (this.trafNumber != localEntry.trafNumber)
          return false;
      }
      while (this.trunNumber == localEntry.trunNumber);
      return false;
    }

    public int hashCode()
    {
      return 31 * (31 * (31 * (31 * (int)(this.time ^ this.time >>> 32) + (int)(this.moofOffset ^ this.moofOffset >>> 32)) + (int)(this.trafNumber ^ this.trafNumber >>> 32)) + (int)(this.trunNumber ^ this.trunNumber >>> 32)) + (int)(this.sampleNumber ^ this.sampleNumber >>> 32);
    }

    public String toString()
    {
      return "Entry{time=" + this.time + ", moofOffset=" + this.moofOffset + ", trafNumber=" + this.trafNumber + ", trunNumber=" + this.trunNumber + ", sampleNumber=" + this.sampleNumber + '}';
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.fragment.TrackFragmentRandomAccessBox
 * JD-Core Version:    0.5.4
 */