package com.coremedia.iso.boxes.vodafone;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class CoverUriBox extends AbstractFullBox
{
  private String coverUri;

  public CoverUriBox()
  {
    super("cvru");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.coverUri = IsoTypeReader.readString(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(Utf8.convert(this.coverUri));
    paramByteBuffer.put(0);
  }

  protected long getContentSize()
  {
    return 5 + Utf8.utf8StringLengthInBytes(this.coverUri);
  }

  public String getCoverUri()
  {
    return this.coverUri;
  }

  public String toString()
  {
    return "CoverUriBox[coverUri=" + getCoverUri() + "]";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.vodafone.CoverUriBox
 * JD-Core Version:    0.5.4
 */