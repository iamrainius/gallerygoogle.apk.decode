package com.coremedia.iso.boxes.vodafone;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class ContentDistributorIdBox extends AbstractFullBox
{
  private String contentDistributorId;
  private String language;

  public ContentDistributorIdBox()
  {
    super("cdis");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.language = IsoTypeReader.readIso639(paramByteBuffer);
    this.contentDistributorId = IsoTypeReader.readString(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeIso639(paramByteBuffer, this.language);
    paramByteBuffer.put(Utf8.convert(this.contentDistributorId));
    paramByteBuffer.put(0);
  }

  public String getContentDistributorId()
  {
    return this.contentDistributorId;
  }

  protected long getContentSize()
  {
    return 5 + (2 + Utf8.utf8StringLengthInBytes(this.contentDistributorId));
  }

  public String getLanguage()
  {
    return this.language;
  }

  public String toString()
  {
    return "ContentDistributorIdBox[language=" + getLanguage() + ";contentDistributorId=" + getContentDistributorId() + "]";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.vodafone.ContentDistributorIdBox
 * JD-Core Version:    0.5.4
 */