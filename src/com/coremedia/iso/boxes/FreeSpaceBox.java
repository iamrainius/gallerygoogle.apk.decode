package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractBox;
import java.nio.ByteBuffer;

public class FreeSpaceBox extends AbstractBox
{
  byte[] data;

  public FreeSpaceBox()
  {
    super("skip");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.data = new byte[paramByteBuffer.remaining()];
    paramByteBuffer.get(this.data);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(this.data);
  }

  protected long getContentSize()
  {
    return this.data.length;
  }

  public String toString()
  {
    return "FreeSpaceBox[size=" + this.data.length + ";type=" + getType() + "]";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.FreeSpaceBox
 * JD-Core Version:    0.5.4
 */