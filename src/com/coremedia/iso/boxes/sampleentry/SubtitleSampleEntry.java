package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.nio.ByteBuffer;

public class SubtitleSampleEntry extends SampleEntry
{
  private String imageMimeType;
  private String namespace;
  private String schemaLocation;

  public SubtitleSampleEntry(String paramString)
  {
    super(paramString);
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    _parseReservedAndDataReferenceIndex(paramByteBuffer);
    this.namespace = IsoTypeReader.readString(paramByteBuffer);
    this.schemaLocation = IsoTypeReader.readString(paramByteBuffer);
    this.imageMimeType = IsoTypeReader.readString(paramByteBuffer);
    _parseChildBoxes(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    _writeReservedAndDataReferenceIndex(paramByteBuffer);
    IsoTypeWriter.writeUtf8String(paramByteBuffer, this.namespace);
    IsoTypeWriter.writeUtf8String(paramByteBuffer, this.schemaLocation);
    IsoTypeWriter.writeUtf8String(paramByteBuffer, this.imageMimeType);
  }

  protected long getContentSize()
  {
    return 3 + (8 + this.namespace.length() + this.schemaLocation.length() + this.imageMimeType.length());
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.sampleentry.SubtitleSampleEntry
 * JD-Core Version:    0.5.4
 */