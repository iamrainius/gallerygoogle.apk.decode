package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractBox;
import java.nio.ByteBuffer;

public class TrackReferenceTypeBox extends AbstractBox
{
  private long[] trackIds;

  public TrackReferenceTypeBox(String paramString)
  {
    super(paramString);
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.remaining() / 4;
    this.trackIds = new long[i];
    for (int j = 0; j < i; ++j)
      this.trackIds[j] = IsoTypeReader.readUInt32(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    long[] arrayOfLong = this.trackIds;
    int i = arrayOfLong.length;
    for (int j = 0; j < i; ++j)
      IsoTypeWriter.writeUInt32(paramByteBuffer, arrayOfLong[j]);
  }

  protected long getContentSize()
  {
    return 4 * this.trackIds.length;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("TrackReferenceTypeBox[type=").append(getType());
    for (int i = 0; i < this.trackIds.length; ++i)
    {
      localStringBuilder.append(";trackId");
      localStringBuilder.append(i);
      localStringBuilder.append("=");
      localStringBuilder.append(this.trackIds[i]);
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.TrackReferenceTypeBox
 * JD-Core Version:    0.5.4
 */