package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class PerformerBox extends AbstractFullBox
{
  private String language;
  private String performer;

  public PerformerBox()
  {
    super("perf");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.language = IsoTypeReader.readIso639(paramByteBuffer);
    this.performer = IsoTypeReader.readString(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeIso639(paramByteBuffer, this.language);
    paramByteBuffer.put(Utf8.convert(this.performer));
    paramByteBuffer.put(0);
  }

  protected long getContentSize()
  {
    return 1 + (6 + Utf8.utf8StringLengthInBytes(this.performer));
  }

  public String getLanguage()
  {
    return this.language;
  }

  public String getPerformer()
  {
    return this.performer;
  }

  public String toString()
  {
    return "PerformerBox[language=" + getLanguage() + ";performer=" + getPerformer() + "]";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.PerformerBox
 * JD-Core Version:    0.5.4
 */