package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.googlecode.mp4parser.AbstractBox;
import java.nio.ByteBuffer;

public class OriginalFormatBox extends AbstractBox
{
  private String dataFormat = "    ";

  static
  {
    if (!OriginalFormatBox.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public OriginalFormatBox()
  {
    super("frma");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.dataFormat = IsoTypeReader.read4cc(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.dataFormat));
  }

  protected long getContentSize()
  {
    return 4L;
  }

  public String getDataFormat()
  {
    return this.dataFormat;
  }

  public String toString()
  {
    return "OriginalFormatBox[dataFormat=" + getDataFormat() + "]";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.OriginalFormatBox
 * JD-Core Version:    0.5.4
 */