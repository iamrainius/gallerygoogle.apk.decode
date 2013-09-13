package com.coremedia.iso.boxes.dece;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TrickPlayBox extends AbstractFullBox
{
  private List<Entry> entries = new ArrayList();

  public TrickPlayBox()
  {
    super("trik");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    while (paramByteBuffer.remaining() > 0)
      this.entries.add(new Entry(IsoTypeReader.readUInt8(paramByteBuffer)));
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    Iterator localIterator = this.entries.iterator();
    while (localIterator.hasNext())
      IsoTypeWriter.writeUInt8(paramByteBuffer, ((Entry)localIterator.next()).value);
  }

  protected long getContentSize()
  {
    return 4 + this.entries.size();
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("TrickPlayBox");
    localStringBuilder.append("{entries=").append(this.entries);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }

  public static class Entry
  {
    private int value;

    public Entry()
    {
    }

    public Entry(int paramInt)
    {
      this.value = paramInt;
    }

    public int getDependencyLevel()
    {
      return 0x3F & this.value;
    }

    public int getPicType()
    {
      return 0x3 & this.value >> 6;
    }

    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Entry");
      localStringBuilder.append("{picType=").append(getPicType());
      localStringBuilder.append(",dependencyLevel=").append(getDependencyLevel());
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.dece.TrickPlayBox
 * JD-Core Version:    0.5.4
 */