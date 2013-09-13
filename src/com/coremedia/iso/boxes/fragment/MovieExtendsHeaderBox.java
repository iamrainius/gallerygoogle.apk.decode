package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class MovieExtendsHeaderBox extends AbstractFullBox
{
  private long fragmentDuration;

  public MovieExtendsHeaderBox()
  {
    super("mehd");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    long l;
    if (getVersion() == 1)
      l = IsoTypeReader.readUInt64(paramByteBuffer);
    while (true)
    {
      this.fragmentDuration = l;
      return;
      l = IsoTypeReader.readUInt32(paramByteBuffer);
    }
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1)
    {
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.fragmentDuration);
      return;
    }
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.fragmentDuration);
  }

  protected long getContentSize()
  {
    if (getVersion() == 1)
      return 12L;
    return 8L;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.fragment.MovieExtendsHeaderBox
 * JD-Core Version:    0.5.4
 */