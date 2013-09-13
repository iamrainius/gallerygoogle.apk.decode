package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class DataEntryUrlBox extends AbstractFullBox
{
  public DataEntryUrlBox()
  {
    super("url ");
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
    return "DataEntryUrlBox[]";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.DataEntryUrlBox
 * JD-Core Version:    0.5.4
 */