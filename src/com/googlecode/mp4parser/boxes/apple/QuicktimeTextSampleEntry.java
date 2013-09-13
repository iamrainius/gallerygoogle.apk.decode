package com.googlecode.mp4parser.boxes.apple;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.sampleentry.SampleEntry;
import java.nio.ByteBuffer;

public class QuicktimeTextSampleEntry extends SampleEntry
{
  int backgroundB;
  int backgroundG;
  int backgroundR;
  long defaultTextBox;
  int displayFlags;
  short fontFace;
  String fontName = "";
  short fontNumber;
  int foregroundB = 65535;
  int foregroundG = 65535;
  int foregroundR = 65535;
  long reserved1;
  byte reserved2;
  short reserved3;
  int textJustification;

  public QuicktimeTextSampleEntry()
  {
    super("text");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    _parseReservedAndDataReferenceIndex(paramByteBuffer);
    this.displayFlags = paramByteBuffer.getInt();
    this.textJustification = paramByteBuffer.getInt();
    this.backgroundR = IsoTypeReader.readUInt16(paramByteBuffer);
    this.backgroundG = IsoTypeReader.readUInt16(paramByteBuffer);
    this.backgroundB = IsoTypeReader.readUInt16(paramByteBuffer);
    this.defaultTextBox = IsoTypeReader.readUInt64(paramByteBuffer);
    this.reserved1 = IsoTypeReader.readUInt64(paramByteBuffer);
    this.fontNumber = paramByteBuffer.getShort();
    this.fontFace = paramByteBuffer.getShort();
    this.reserved2 = paramByteBuffer.get();
    this.reserved3 = paramByteBuffer.getShort();
    this.foregroundR = IsoTypeReader.readUInt16(paramByteBuffer);
    this.foregroundG = IsoTypeReader.readUInt16(paramByteBuffer);
    this.foregroundB = IsoTypeReader.readUInt16(paramByteBuffer);
    if (paramByteBuffer.remaining() > 0)
    {
      byte[] arrayOfByte = new byte[IsoTypeReader.readUInt8(paramByteBuffer)];
      paramByteBuffer.get(arrayOfByte);
      this.fontName = new String(arrayOfByte);
      return;
    }
    this.fontName = null;
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    _writeReservedAndDataReferenceIndex(paramByteBuffer);
    paramByteBuffer.putInt(this.displayFlags);
    paramByteBuffer.putInt(this.textJustification);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.backgroundR);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.backgroundG);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.backgroundB);
    IsoTypeWriter.writeUInt64(paramByteBuffer, this.defaultTextBox);
    IsoTypeWriter.writeUInt64(paramByteBuffer, this.reserved1);
    paramByteBuffer.putShort(this.fontNumber);
    paramByteBuffer.putShort(this.fontFace);
    paramByteBuffer.put(this.reserved2);
    paramByteBuffer.putShort(this.reserved3);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.foregroundR);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.foregroundG);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.foregroundB);
    if (this.fontName == null)
      return;
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.fontName.length());
    paramByteBuffer.put(this.fontName.getBytes());
  }

  protected long getContentSize()
  {
    if (this.fontName != null);
    for (int i = this.fontName.length(); ; i = 0)
      return i + 52;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.apple.QuicktimeTextSampleEntry
 * JD-Core Version:    0.5.4
 */