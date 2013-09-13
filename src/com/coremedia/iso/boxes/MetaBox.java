package com.coremedia.iso.boxes;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.googlecode.mp4parser.util.ByteBufferByteChannel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class MetaBox extends AbstractContainerBox
{
  private int flags = 0;
  private int version = 0;

  public MetaBox()
  {
    super("meta");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.position();
    paramByteBuffer.get(new byte[4]);
    if ("hdlr".equals(IsoTypeReader.read4cc(paramByteBuffer)))
    {
      paramByteBuffer.position(i);
      this.version = -1;
    }
    for (this.flags = -1; paramByteBuffer.remaining() >= 8; this.flags = IsoTypeReader.readUInt24(paramByteBuffer))
    {
      try
      {
        this.boxes.add(this.boxParser.parseBox(new ByteBufferByteChannel(paramByteBuffer), this));
      }
      catch (IOException localIOException)
      {
        throw new RuntimeException("Sebastian needs to fix 7518765283");
      }
      paramByteBuffer.position(i);
      this.version = IsoTypeReader.readUInt8(paramByteBuffer);
    }
    if (paramByteBuffer.remaining() <= 0)
      return;
    throw new RuntimeException("Sebastian needs to fix it 90732r26537");
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    if (isMp4Box())
    {
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.version);
      IsoTypeWriter.writeUInt24(paramByteBuffer, this.flags);
    }
    writeChildBoxes(paramByteBuffer);
  }

  public long getContentSize()
  {
    if (isMp4Box())
      return 4L + super.getContentSize();
    return super.getContentSize();
  }

  public boolean isMp4Box()
  {
    return (this.version != -1) && (this.flags != -1);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.MetaBox
 * JD-Core Version:    0.5.4
 */