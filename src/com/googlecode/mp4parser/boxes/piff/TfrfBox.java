package com.googlecode.mp4parser.boxes.piff;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TfrfBox extends AbstractFullBox
{
  public List<Entry> entries = new ArrayList();

  public TfrfBox()
  {
    super("uuid");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    int j = 0;
    if (j >= i)
      label15: return;
    Entry localEntry = new Entry();
    if (getVersion() == 1)
      localEntry.fragmentAbsoluteTime = IsoTypeReader.readUInt64(paramByteBuffer);
    for (localEntry.fragmentAbsoluteDuration = IsoTypeReader.readUInt64(paramByteBuffer); ; localEntry.fragmentAbsoluteDuration = IsoTypeReader.readUInt32(paramByteBuffer))
    {
      this.entries.add(localEntry);
      ++j;
      break label15:
      localEntry.fragmentAbsoluteTime = IsoTypeReader.readUInt32(paramByteBuffer);
    }
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.entries.size());
    Iterator localIterator = this.entries.iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      if (getVersion() == 1)
      {
        IsoTypeWriter.writeUInt64(paramByteBuffer, localEntry.fragmentAbsoluteTime);
        IsoTypeWriter.writeUInt64(paramByteBuffer, localEntry.fragmentAbsoluteDuration);
      }
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.fragmentAbsoluteTime);
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.fragmentAbsoluteDuration);
    }
  }

  protected long getContentSize()
  {
    int i = this.entries.size();
    if (getVersion() == 1);
    for (int j = 16; ; j = 8)
      return 5 + j * i;
  }

  public byte[] getUserType()
  {
    return new byte[] { -44, -128, 126, -14, -54, 57, 70, -107, -114, 84, 38, -53, -98, 70, -89, -97 };
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("TfrfBox");
    localStringBuilder.append("{entries=").append(this.entries);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }

  public class Entry
  {
    long fragmentAbsoluteDuration;
    long fragmentAbsoluteTime;

    public Entry()
    {
    }

    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Entry");
      localStringBuilder.append("{fragmentAbsoluteTime=").append(this.fragmentAbsoluteTime);
      localStringBuilder.append(", fragmentAbsoluteDuration=").append(this.fragmentAbsoluteDuration);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.piff.TfrfBox
 * JD-Core Version:    0.5.4
 */