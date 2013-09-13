package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class MovieFragmentHeaderBox extends AbstractFullBox
{
  private long sequenceNumber;

  public MovieFragmentHeaderBox()
  {
    super("mfhd");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.sequenceNumber = IsoTypeReader.readUInt32(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.sequenceNumber);
  }

  protected long getContentSize()
  {
    return 8L;
  }

  public String toString()
  {
    return "MovieFragmentHeaderBox{sequenceNumber=" + this.sequenceNumber + '}';
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.fragment.MovieFragmentHeaderBox
 * JD-Core Version:    0.5.4
 */