package com.googlecode.mp4parser.boxes;

import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.util.Path;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractSampleEncryptionBox extends AbstractFullBox
{
  int algorithmId = -1;
  List<Entry> entries = new LinkedList();
  int ivSize = -1;
  byte[] kid = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

  protected AbstractSampleEncryptionBox(String paramString)
  {
    super(paramString);
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int i;
    if ((0x1 & getFlags()) > 0)
    {
      this.algorithmId = IsoTypeReader.readUInt24(paramByteBuffer);
      this.ivSize = IsoTypeReader.readUInt8(paramByteBuffer);
      i = this.ivSize;
      this.kid = new byte[16];
      paramByteBuffer.get(this.kid);
    }
    long l1;
    label60: long l2;
    Entry localEntry;
    int k;
    label92: Iterator localIterator;
    do
    {
      l1 = IsoTypeReader.readUInt32(paramByteBuffer);
      l2 = l1 - 1L;
      if (l1 <= 0L)
        break label322;
      localEntry = new Entry();
      if (i >= 0)
        break label296;
      k = 8;
      localEntry.iv = new byte[k];
      paramByteBuffer.get(localEntry.iv);
      if ((0x2 & getFlags()) <= 0)
        break label303;
      int l = IsoTypeReader.readUInt16(paramByteBuffer);
      localEntry.pairs = new LinkedList();
      while (true)
      {
        int i1 = l - 1;
        if (l <= 0)
          break label303;
        localEntry.pairs.add(localEntry.createPair(IsoTypeReader.readUInt16(paramByteBuffer), IsoTypeReader.readUInt32(paramByteBuffer)));
        l = i1;
      }
      localIterator = Path.getPaths(this, "/moov[0]/trak/tkhd").iterator();
      label196: i = -1;
    }
    while (!localIterator.hasNext());
    Box localBox = (Box)localIterator.next();
    AbstractTrackEncryptionBox localAbstractTrackEncryptionBox1;
    AbstractTrackEncryptionBox localAbstractTrackEncryptionBox2;
    if (((TrackHeaderBox)localBox).getTrackId() == ((TrackFragmentHeaderBox)getParent().getBoxes(TrackFragmentHeaderBox.class).get(0)).getTrackId())
    {
      localAbstractTrackEncryptionBox1 = (AbstractTrackEncryptionBox)Path.getPath(localBox, "../mdia[0]/minf[0]/stbl[0]/stsd[0]/enc.[0]/sinf[0]/schi[0]/tenc[0]");
      if (localAbstractTrackEncryptionBox1 == null)
        localAbstractTrackEncryptionBox2 = (AbstractTrackEncryptionBox)Path.getPath(localBox, "../mdia[0]/minf[0]/stbl[0]/stsd[0]/enc.[0]/sinf[0]/schi[0]/uuid[0]");
    }
    for (int j = localAbstractTrackEncryptionBox2.getDefaultIvSize(); ; j = i)
    {
      label282: i = j;
      break label196:
      label296: k = i;
      break label92:
      label303: this.entries.add(localEntry);
      l1 = l2;
      break label60:
      label322: return;
      localAbstractTrackEncryptionBox2 = localAbstractTrackEncryptionBox1;
      break label282:
    }
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    AbstractSampleEncryptionBox localAbstractSampleEncryptionBox;
    do
    {
      return true;
      if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
        return false;
      localAbstractSampleEncryptionBox = (AbstractSampleEncryptionBox)paramObject;
      if (this.algorithmId != localAbstractSampleEncryptionBox.algorithmId)
        return false;
      if (this.ivSize != localAbstractSampleEncryptionBox.ivSize)
        return false;
      if (this.entries != null)
        if (this.entries.equals(localAbstractSampleEncryptionBox.entries))
          continue;
      do
        return false;
      while (localAbstractSampleEncryptionBox.entries != null);
    }
    while (Arrays.equals(this.kid, localAbstractSampleEncryptionBox.kid));
    return false;
  }

  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    super.getBox(paramWritableByteChannel);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    if (isOverrideTrackEncryptionBoxParameters())
    {
      IsoTypeWriter.writeUInt24(paramByteBuffer, this.algorithmId);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.ivSize);
      paramByteBuffer.put(this.kid);
    }
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.entries.size());
    Iterator localIterator1 = this.entries.iterator();
    if (!localIterator1.hasNext())
      return;
    Entry localEntry = (Entry)localIterator1.next();
    if (isOverrideTrackEncryptionBoxParameters())
    {
      byte[] arrayOfByte = new byte[this.ivSize];
      System.arraycopy(localEntry.iv, 0, arrayOfByte, this.ivSize - localEntry.iv.length, localEntry.iv.length);
      paramByteBuffer.put(arrayOfByte);
    }
    while (true)
    {
      if (isSubSampleEncryption());
      IsoTypeWriter.writeUInt16(paramByteBuffer, localEntry.pairs.size());
      Iterator localIterator2 = localEntry.pairs.iterator();
      while (true)
      {
        if (localIterator2.hasNext());
        AbstractSampleEncryptionBox.Entry.Pair localPair = (AbstractSampleEncryptionBox.Entry.Pair)localIterator2.next();
        IsoTypeWriter.writeUInt16(paramByteBuffer, localPair.clear);
        IsoTypeWriter.writeUInt32(paramByteBuffer, localPair.encrypted);
      }
      paramByteBuffer.put(localEntry.iv);
    }
  }

  protected long getContentSize()
  {
    long l1 = 4L;
    if (isOverrideTrackEncryptionBoxParameters())
      l1 = l1 + 4L + this.kid.length;
    long l2 = l1 + 4L;
    Iterator localIterator = this.entries.iterator();
    while (localIterator.hasNext())
      l2 += ((Entry)localIterator.next()).getSize();
    return l2;
  }

  public int hashCode()
  {
    int i = 31 * (31 * this.algorithmId + this.ivSize);
    if (this.kid != null);
    for (int j = Arrays.hashCode(this.kid); ; j = 0)
    {
      int k = 31 * (i + j);
      List localList = this.entries;
      int l = 0;
      if (localList != null)
        l = this.entries.hashCode();
      return k + l;
    }
  }

  public boolean isOverrideTrackEncryptionBoxParameters()
  {
    return (0x1 & getFlags()) > 0;
  }

  public boolean isSubSampleEncryption()
  {
    return (0x2 & getFlags()) > 0;
  }

  public class Entry
  {
    public byte[] iv;
    public List<Pair> pairs = new LinkedList();

    public Entry()
    {
    }

    public Pair createPair(int paramInt, long paramLong)
    {
      return new Pair(paramInt, paramLong);
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
        if (!new BigInteger(this.iv).equals(new BigInteger(localEntry.iv)))
          return false;
        if (this.pairs == null)
          break;
      }
      while (this.pairs.equals(localEntry.pairs));
      while (true)
      {
        return false;
        if (localEntry.pairs == null);
      }
    }

    public int getSize()
    {
      if (AbstractSampleEncryptionBox.this.isOverrideTrackEncryptionBoxParameters());
      for (int i = AbstractSampleEncryptionBox.this.ivSize; AbstractSampleEncryptionBox.this.isSubSampleEncryption(); i = this.iv.length)
      {
        i += 2;
        Iterator localIterator = this.pairs.iterator();
        while (true)
        {
          if (!localIterator.hasNext())
            break label75;
          ((Pair)localIterator.next());
          i += 6;
        }
      }
      label75: return i;
    }

    public int hashCode()
    {
      if (this.iv != null);
      for (int i = Arrays.hashCode(this.iv); ; i = 0)
      {
        int j = i * 31;
        List localList = this.pairs;
        int k = 0;
        if (localList != null)
          k = this.pairs.hashCode();
        return j + k;
      }
    }

    public String toString()
    {
      return "Entry{iv=" + Hex.encodeHex(this.iv) + ", pairs=" + this.pairs + '}';
    }

    public class Pair
    {
      public int clear;
      public long encrypted;

      public Pair(int paramLong, long arg3)
      {
        this.clear = paramLong;
        Object localObject;
        this.encrypted = localObject;
      }

      public boolean equals(Object paramObject)
      {
        if (this == paramObject);
        Pair localPair;
        do
        {
          return true;
          if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
            return false;
          localPair = (Pair)paramObject;
          if (this.clear != localPair.clear)
            return false;
        }
        while (this.encrypted == localPair.encrypted);
        return false;
      }

      public int hashCode()
      {
        return 31 * this.clear + (int)(this.encrypted ^ this.encrypted >>> 32);
      }

      public String toString()
      {
        return "clr:" + this.clear + " enc:" + this.encrypted;
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.AbstractSampleEncryptionBox
 * JD-Core Version:    0.5.4
 */