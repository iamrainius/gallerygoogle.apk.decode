package com.googlecode.mp4parser.boxes.threegpp26244;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SegmentIndexBox extends AbstractFullBox
{
  long earliestPresentationTime;
  List<Entry> entries = new ArrayList();
  long firstOffset;
  long referenceId;
  int reserved;
  long timeScale;

  public SegmentIndexBox()
  {
    super("sidx");
  }

  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.referenceId = IsoTypeReader.readUInt32(paramByteBuffer);
    this.timeScale = IsoTypeReader.readUInt32(paramByteBuffer);
    if (getVersion() == 0)
      this.earliestPresentationTime = IsoTypeReader.readUInt32(paramByteBuffer);
    for (this.firstOffset = IsoTypeReader.readUInt32(paramByteBuffer); ; this.firstOffset = IsoTypeReader.readUInt64(paramByteBuffer))
    {
      this.reserved = IsoTypeReader.readUInt16(paramByteBuffer);
      int i = IsoTypeReader.readUInt16(paramByteBuffer);
      for (int j = 0; ; ++j)
      {
        if (j >= i)
          return;
        BitReaderBuffer localBitReaderBuffer1 = new BitReaderBuffer(paramByteBuffer);
        Entry localEntry = new Entry();
        localEntry.setReferenceType((byte)localBitReaderBuffer1.readBits(1));
        localEntry.setReferencedSize(localBitReaderBuffer1.readBits(31));
        localEntry.setSubsegmentDuration(IsoTypeReader.readUInt32(paramByteBuffer));
        BitReaderBuffer localBitReaderBuffer2 = new BitReaderBuffer(paramByteBuffer);
        localEntry.setStartsWithSap((byte)localBitReaderBuffer2.readBits(1));
        localEntry.setSapType((byte)localBitReaderBuffer2.readBits(3));
        localEntry.setSapDeltaTime(localBitReaderBuffer2.readBits(28));
        this.entries.add(localEntry);
      }
      this.earliestPresentationTime = IsoTypeReader.readUInt64(paramByteBuffer);
    }
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.referenceId);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.timeScale);
    if (getVersion() == 0)
    {
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.earliestPresentationTime);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.firstOffset);
    }
    while (true)
    {
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.reserved);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.entries.size());
      Iterator localIterator = this.entries.iterator();
      while (true)
      {
        if (!localIterator.hasNext())
          return;
        Entry localEntry = (Entry)localIterator.next();
        BitWriterBuffer localBitWriterBuffer1 = new BitWriterBuffer(paramByteBuffer);
        localBitWriterBuffer1.writeBits(localEntry.getReferenceType(), 1);
        localBitWriterBuffer1.writeBits(localEntry.getReferencedSize(), 31);
        IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getSubsegmentDuration());
        BitWriterBuffer localBitWriterBuffer2 = new BitWriterBuffer(paramByteBuffer);
        localBitWriterBuffer2.writeBits(localEntry.getStartsWithSap(), 1);
        localBitWriterBuffer2.writeBits(localEntry.getSapType(), 3);
        localBitWriterBuffer2.writeBits(localEntry.getSapDeltaTime(), 28);
      }
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.earliestPresentationTime);
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.firstOffset);
    }
  }

  protected long getContentSize()
  {
    long l1 = 4L + (4L + 4L);
    long l2;
    if (getVersion() == 0)
      l2 = 8L;
    while (true)
    {
      return 2L + (2L + (l1 + l2)) + 12 * this.entries.size();
      l2 = 16L;
    }
  }

  public static class Entry
  {
    byte referenceType;
    int referencedSize;
    int sapDeltaTime;
    byte sapType;
    byte startsWithSap;
    long subsegmentDuration;

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
        if (this.referenceType != localEntry.referenceType)
          return false;
        if (this.referencedSize != localEntry.referencedSize)
          return false;
        if (this.sapDeltaTime != localEntry.sapDeltaTime)
          return false;
        if (this.sapType != localEntry.sapType)
          return false;
        if (this.startsWithSap != localEntry.startsWithSap)
          return false;
      }
      while (this.subsegmentDuration == localEntry.subsegmentDuration);
      return false;
    }

    public byte getReferenceType()
    {
      return this.referenceType;
    }

    public int getReferencedSize()
    {
      return this.referencedSize;
    }

    public int getSapDeltaTime()
    {
      return this.sapDeltaTime;
    }

    public byte getSapType()
    {
      return this.sapType;
    }

    public byte getStartsWithSap()
    {
      return this.startsWithSap;
    }

    public long getSubsegmentDuration()
    {
      return this.subsegmentDuration;
    }

    public int hashCode()
    {
      return 31 * (31 * (31 * (31 * (31 * this.referenceType + this.referencedSize) + (int)(this.subsegmentDuration ^ this.subsegmentDuration >>> 32)) + this.startsWithSap) + this.sapType) + this.sapDeltaTime;
    }

    public void setReferenceType(byte paramByte)
    {
      this.referenceType = paramByte;
    }

    public void setReferencedSize(int paramInt)
    {
      this.referencedSize = paramInt;
    }

    public void setSapDeltaTime(int paramInt)
    {
      this.sapDeltaTime = paramInt;
    }

    public void setSapType(byte paramByte)
    {
      this.sapType = paramByte;
    }

    public void setStartsWithSap(byte paramByte)
    {
      this.startsWithSap = paramByte;
    }

    public void setSubsegmentDuration(long paramLong)
    {
      this.subsegmentDuration = paramLong;
    }

    public String toString()
    {
      return "Entry{referenceType=" + this.referenceType + ", referencedSize=" + this.referencedSize + ", subsegmentDuration=" + this.subsegmentDuration + ", startsWithSap=" + this.startsWithSap + ", sapType=" + this.sapType + ", sapDeltaTime=" + this.sapDeltaTime + '}';
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.threegpp26244.SegmentIndexBox
 * JD-Core Version:    0.5.4
 */