package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class TrackFragmentBaseMediaDecodeTimeBox extends AbstractFullBox
{
  private long baseMediaDecodeTime;

  public TrackFragmentBaseMediaDecodeTimeBox()
  {
    super("tfdt");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1)
    {
      this.baseMediaDecodeTime = IsoTypeReader.readUInt64(paramByteBuffer);
      return;
    }
    this.baseMediaDecodeTime = IsoTypeReader.readUInt32(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1)
    {
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.baseMediaDecodeTime);
      return;
    }
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.baseMediaDecodeTime);
  }

  protected long getContentSize()
  {
    if (getVersion() == 0)
      return 8L;
    return 12L;
  }

  public String toString()
  {
    return "TrackFragmentBaseMediaDecodeTimeBox{baseMediaDecodeTime=" + this.baseMediaDecodeTime + '}';
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.fragment.TrackFragmentBaseMediaDecodeTimeBox
 * JD-Core Version:    0.5.4
 */