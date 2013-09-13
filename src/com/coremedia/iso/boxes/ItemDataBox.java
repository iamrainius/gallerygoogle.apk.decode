package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractBox;
import java.nio.ByteBuffer;

public class ItemDataBox extends AbstractBox
{
  ByteBuffer data = ByteBuffer.allocate(0);

  public ItemDataBox()
  {
    super("idat");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.data = paramByteBuffer.slice();
    paramByteBuffer.position(paramByteBuffer.position() + paramByteBuffer.remaining());
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(this.data);
  }

  protected long getContentSize()
  {
    return this.data.limit();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.ItemDataBox
 * JD-Core Version:    0.5.4
 */