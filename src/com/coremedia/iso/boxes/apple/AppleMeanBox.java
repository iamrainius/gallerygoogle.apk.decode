package com.coremedia.iso.boxes.apple;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public final class AppleMeanBox extends AbstractFullBox
{
  private String meaning;

  public AppleMeanBox()
  {
    super("mean");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.meaning = IsoTypeReader.readString(paramByteBuffer, paramByteBuffer.remaining());
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(Utf8.convert(this.meaning));
  }

  protected long getContentSize()
  {
    return 4 + Utf8.utf8StringLengthInBytes(this.meaning);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.apple.AppleMeanBox
 * JD-Core Version:    0.5.4
 */