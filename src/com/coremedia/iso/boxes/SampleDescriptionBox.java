package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.FullContainerBox;
import java.nio.ByteBuffer;
import java.util.List;

public class SampleDescriptionBox extends FullContainerBox
{
  public SampleDescriptionBox()
  {
    super("stsd");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    paramByteBuffer.get(new byte[4]);
    parseChildBoxes(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.boxes.size());
    writeChildBoxes(paramByteBuffer);
  }

  protected long getContentSize()
  {
    return 4L + super.getContentSize();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.SampleDescriptionBox
 * JD-Core Version:    0.5.4
 */