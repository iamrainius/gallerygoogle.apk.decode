package com.googlecode.mp4parser.boxes.piff;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class TfxdBox extends AbstractFullBox
{
  public long fragmentAbsoluteDuration;
  public long fragmentAbsoluteTime;

  public TfxdBox()
  {
    super("uuid");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1)
    {
      this.fragmentAbsoluteTime = IsoTypeReader.readUInt64(paramByteBuffer);
      this.fragmentAbsoluteDuration = IsoTypeReader.readUInt64(paramByteBuffer);
      return;
    }
    this.fragmentAbsoluteTime = IsoTypeReader.readUInt32(paramByteBuffer);
    this.fragmentAbsoluteDuration = IsoTypeReader.readUInt32(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1)
    {
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.fragmentAbsoluteTime);
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.fragmentAbsoluteDuration);
      return;
    }
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.fragmentAbsoluteTime);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.fragmentAbsoluteDuration);
  }

  protected long getContentSize()
  {
    if (getVersion() == 1)
      return 20L;
    return 12L;
  }

  public byte[] getUserType()
  {
    return new byte[] { 109, 29, -101, 5, 66, -43, 68, -26, -128, -30, 20, 29, -81, -9, 87, -78 };
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.piff.TfxdBox
 * JD-Core Version:    0.5.4
 */