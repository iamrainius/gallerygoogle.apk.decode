package com.coremedia.iso.boxes;

import com.coremedia.iso.BoxParser;
import com.googlecode.mp4parser.AbstractContainerBox;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class UserDataBox extends AbstractContainerBox
{
  public UserDataBox()
  {
    super("udta");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    super._parseDetails(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    super.getContent(paramByteBuffer);
  }

  protected long getContentSize()
  {
    return super.getContentSize();
  }

  public void parse(ReadableByteChannel paramReadableByteChannel, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    super.parse(paramReadableByteChannel, paramByteBuffer, paramLong, paramBoxParser);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.UserDataBox
 * JD-Core Version:    0.5.4
 */