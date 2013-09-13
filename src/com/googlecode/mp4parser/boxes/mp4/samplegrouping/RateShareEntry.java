package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class RateShareEntry extends GroupEntry
{
  private short discardPriority;
  private List<Entry> entries = new LinkedList();
  private int maximumBitrate;
  private int minimumBitrate;
  private short operationPointCut;
  private short targetRateShare;

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    RateShareEntry localRateShareEntry;
    do
    {
      return true;
      if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
        return false;
      localRateShareEntry = (RateShareEntry)paramObject;
      if (this.discardPriority != localRateShareEntry.discardPriority)
        return false;
      if (this.maximumBitrate != localRateShareEntry.maximumBitrate)
        return false;
      if (this.minimumBitrate != localRateShareEntry.minimumBitrate)
        return false;
      if (this.operationPointCut != localRateShareEntry.operationPointCut)
        return false;
      if (this.targetRateShare != localRateShareEntry.targetRateShare)
        return false;
      if (this.entries == null)
        break;
    }
    while (this.entries.equals(localRateShareEntry.entries));
    while (true)
    {
      return false;
      if (localRateShareEntry.entries == null);
    }
  }

  public ByteBuffer get()
  {
    if (this.operationPointCut == 1);
    ByteBuffer localByteBuffer;
    for (int i = 13; ; i = 11 + 6 * this.operationPointCut)
    {
      localByteBuffer = ByteBuffer.allocate(i);
      localByteBuffer.putShort(this.operationPointCut);
      if (this.operationPointCut != 1)
        break;
      localByteBuffer.putShort(this.targetRateShare);
      localByteBuffer.putInt(this.maximumBitrate);
      localByteBuffer.putInt(this.minimumBitrate);
      IsoTypeWriter.writeUInt8(localByteBuffer, this.discardPriority);
      localByteBuffer.rewind();
      return localByteBuffer;
    }
    Iterator localIterator = this.entries.iterator();
    while (true)
    {
      if (localIterator.hasNext());
      Entry localEntry = (Entry)localIterator.next();
      localByteBuffer.putInt(localEntry.getAvailableBitrate());
      localByteBuffer.putShort(localEntry.getTargetRateShare());
    }
  }

  public int hashCode()
  {
    int i = 31 * (31 * this.operationPointCut + this.targetRateShare);
    if (this.entries != null);
    for (int j = this.entries.hashCode(); ; j = 0)
      return 31 * (31 * (31 * (i + j) + this.maximumBitrate) + this.minimumBitrate) + this.discardPriority;
  }

  public void parse(ByteBuffer paramByteBuffer)
  {
    this.operationPointCut = paramByteBuffer.getShort();
    if (this.operationPointCut == 1)
    {
      this.targetRateShare = paramByteBuffer.getShort();
      this.maximumBitrate = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
      this.minimumBitrate = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
      this.discardPriority = (short)IsoTypeReader.readUInt8(paramByteBuffer);
      return;
    }
    int j;
    for (int i = this.operationPointCut; ; i = j)
    {
      j = i - 1;
      if (i > 0);
      this.entries.add(new Entry(CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer)), paramByteBuffer.getShort()));
    }
  }

  public static class Entry
  {
    int availableBitrate;
    short targetRateShare;

    public Entry(int paramInt, short paramShort)
    {
      this.availableBitrate = paramInt;
      this.targetRateShare = paramShort;
    }

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
        if (this.availableBitrate != localEntry.availableBitrate)
          return false;
      }
      while (this.targetRateShare == localEntry.targetRateShare);
      return false;
    }

    public int getAvailableBitrate()
    {
      return this.availableBitrate;
    }

    public short getTargetRateShare()
    {
      return this.targetRateShare;
    }

    public int hashCode()
    {
      return 31 * this.availableBitrate + this.targetRateShare;
    }

    public String toString()
    {
      return "{availableBitrate=" + this.availableBitrate + ", targetRateShare=" + this.targetRateShare + '}';
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.samplegrouping.RateShareEntry
 * JD-Core Version:    0.5.4
 */