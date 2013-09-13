package com.coremedia.iso.boxes;

import java.nio.ByteBuffer;

public class SubtitleMediaHeaderBox extends AbstractMediaHeaderBox
{
  public SubtitleMediaHeaderBox()
  {
    super("sthd");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
  }

  protected long getContentSize()
  {
    return 4L;
  }

  public String toString()
  {
    return "SubtitleMediaHeaderBox";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.SubtitleMediaHeaderBox
 * JD-Core Version:    0.5.4
 */