package com.googlecode.mp4parser.boxes.threegpp26245;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractBox;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FontTableBox extends AbstractBox
{
  List<FontRecord> entries = new LinkedList();

  public FontTableBox()
  {
    super("ftab");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    int i = IsoTypeReader.readUInt16(paramByteBuffer);
    for (int j = 0; j < i; ++j)
    {
      FontRecord localFontRecord = new FontRecord();
      localFontRecord.parse(paramByteBuffer);
      this.entries.add(localFontRecord);
    }
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.entries.size());
    Iterator localIterator = this.entries.iterator();
    while (localIterator.hasNext())
      ((FontRecord)localIterator.next()).getContent(paramByteBuffer);
  }

  protected long getContentSize()
  {
    int i = 2;
    Iterator localIterator = this.entries.iterator();
    while (localIterator.hasNext())
      i += ((FontRecord)localIterator.next()).getSize();
    return i;
  }

  public static class FontRecord
  {
    int fontId;
    String fontname;

    public void getContent(ByteBuffer paramByteBuffer)
    {
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.fontId);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.fontname.length());
      paramByteBuffer.put(Utf8.convert(this.fontname));
    }

    public int getSize()
    {
      return 3 + Utf8.utf8StringLengthInBytes(this.fontname);
    }

    public void parse(ByteBuffer paramByteBuffer)
    {
      this.fontId = IsoTypeReader.readUInt16(paramByteBuffer);
      this.fontname = IsoTypeReader.readString(paramByteBuffer, IsoTypeReader.readUInt8(paramByteBuffer));
    }

    public String toString()
    {
      return "FontRecord{fontId=" + this.fontId + ", fontname='" + this.fontname + '\'' + '}';
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.threegpp26245.FontTableBox
 * JD-Core Version:    0.5.4
 */