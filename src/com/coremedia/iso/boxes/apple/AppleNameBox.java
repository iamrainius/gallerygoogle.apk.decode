package com.coremedia.iso.boxes.apple;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public final class AppleNameBox extends AbstractFullBox
{
  private String name;

  public AppleNameBox()
  {
    super("name");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.name = IsoTypeReader.readString(paramByteBuffer, paramByteBuffer.remaining());
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(Utf8.convert(this.name));
  }

  protected long getContentSize()
  {
    return 4 + Utf8.convert(this.name).length;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.apple.AppleNameBox
 * JD-Core Version:    0.5.4
 */