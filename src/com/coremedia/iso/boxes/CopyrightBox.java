package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class CopyrightBox extends AbstractFullBox
{
  private String copyright;
  private String language;

  public CopyrightBox()
  {
    super("cprt");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.language = IsoTypeReader.readIso639(paramByteBuffer);
    this.copyright = IsoTypeReader.readString(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeIso639(paramByteBuffer, this.language);
    paramByteBuffer.put(Utf8.convert(this.copyright));
    paramByteBuffer.put(0);
  }

  protected long getContentSize()
  {
    return 7 + Utf8.utf8StringLengthInBytes(this.copyright);
  }

  public String getCopyright()
  {
    return this.copyright;
  }

  public String getLanguage()
  {
    return this.language;
  }

  public String toString()
  {
    return "CopyrightBox[language=" + getLanguage() + ";copyright=" + getCopyright() + "]";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.CopyrightBox
 * JD-Core Version:    0.5.4
 */