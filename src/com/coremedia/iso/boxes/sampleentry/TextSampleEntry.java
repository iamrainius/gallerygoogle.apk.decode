package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

public class TextSampleEntry extends SampleEntry
{
  private int[] backgroundColorRgba = new int[4];
  private BoxRecord boxRecord = new BoxRecord();
  private long displayFlags;
  private int horizontalJustification;
  private StyleRecord styleRecord = new StyleRecord();
  private int verticalJustification;

  public TextSampleEntry(String paramString)
  {
    super(paramString);
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    _parseReservedAndDataReferenceIndex(paramByteBuffer);
    this.displayFlags = IsoTypeReader.readUInt32(paramByteBuffer);
    this.horizontalJustification = IsoTypeReader.readUInt8(paramByteBuffer);
    this.verticalJustification = IsoTypeReader.readUInt8(paramByteBuffer);
    this.backgroundColorRgba = new int[4];
    this.backgroundColorRgba[0] = IsoTypeReader.readUInt8(paramByteBuffer);
    this.backgroundColorRgba[1] = IsoTypeReader.readUInt8(paramByteBuffer);
    this.backgroundColorRgba[2] = IsoTypeReader.readUInt8(paramByteBuffer);
    this.backgroundColorRgba[3] = IsoTypeReader.readUInt8(paramByteBuffer);
    this.boxRecord = new BoxRecord();
    this.boxRecord.parse(paramByteBuffer);
    this.styleRecord = new StyleRecord();
    this.styleRecord.parse(paramByteBuffer);
    _parseChildBoxes(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    _writeReservedAndDataReferenceIndex(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.displayFlags);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.horizontalJustification);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.verticalJustification);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.backgroundColorRgba[0]);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.backgroundColorRgba[1]);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.backgroundColorRgba[2]);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.backgroundColorRgba[3]);
    this.boxRecord.getContent(paramByteBuffer);
    this.styleRecord.getContent(paramByteBuffer);
    _writeChildBoxes(paramByteBuffer);
  }

  protected long getContentSize()
  {
    long l = 18L + this.boxRecord.getSize() + this.styleRecord.getSize();
    Iterator localIterator = this.boxes.iterator();
    while (localIterator.hasNext())
      l += ((Box)localIterator.next()).getSize();
    return l;
  }

  public String toString()
  {
    return "TextSampleEntry";
  }

  public static class BoxRecord
  {
    int bottom;
    int left;
    int right;
    int top;

    public void getContent(ByteBuffer paramByteBuffer)
    {
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.top);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.left);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.bottom);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.right);
    }

    public int getSize()
    {
      return 8;
    }

    public void parse(ByteBuffer paramByteBuffer)
    {
      this.top = IsoTypeReader.readUInt16(paramByteBuffer);
      this.left = IsoTypeReader.readUInt16(paramByteBuffer);
      this.bottom = IsoTypeReader.readUInt16(paramByteBuffer);
      this.right = IsoTypeReader.readUInt16(paramByteBuffer);
    }
  }

  public static class StyleRecord
  {
    int endChar;
    int faceStyleFlags;
    int fontId;
    int fontSize;
    int startChar;
    int[] textColor = { 255, 255, 255, 255 };

    public void getContent(ByteBuffer paramByteBuffer)
    {
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.startChar);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.endChar);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.fontId);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.faceStyleFlags);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.fontSize);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.textColor[0]);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.textColor[1]);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.textColor[2]);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.textColor[3]);
    }

    public int getSize()
    {
      return 12;
    }

    public void parse(ByteBuffer paramByteBuffer)
    {
      this.startChar = IsoTypeReader.readUInt16(paramByteBuffer);
      this.endChar = IsoTypeReader.readUInt16(paramByteBuffer);
      this.fontId = IsoTypeReader.readUInt16(paramByteBuffer);
      this.faceStyleFlags = IsoTypeReader.readUInt8(paramByteBuffer);
      this.fontSize = IsoTypeReader.readUInt8(paramByteBuffer);
      this.textColor = new int[4];
      this.textColor[0] = IsoTypeReader.readUInt8(paramByteBuffer);
      this.textColor[1] = IsoTypeReader.readUInt8(paramByteBuffer);
      this.textColor[2] = IsoTypeReader.readUInt8(paramByteBuffer);
      this.textColor[3] = IsoTypeReader.readUInt8(paramByteBuffer);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.sampleentry.TextSampleEntry
 * JD-Core Version:    0.5.4
 */