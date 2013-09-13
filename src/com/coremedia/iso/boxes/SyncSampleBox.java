package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;

public class SyncSampleBox extends AbstractFullBox
{
  private long[] sampleNumber;

  public SyncSampleBox()
  {
    super("stss");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int i = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.sampleNumber = new long[i];
    for (int j = 0; j < i; ++j)
      this.sampleNumber[j] = IsoTypeReader.readUInt32(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.sampleNumber.length);
    long[] arrayOfLong = this.sampleNumber;
    int i = arrayOfLong.length;
    for (int j = 0; j < i; ++j)
      IsoTypeWriter.writeUInt32(paramByteBuffer, arrayOfLong[j]);
  }

  protected long getContentSize()
  {
    return 8 + 4 * this.sampleNumber.length;
  }

  public long[] getSampleNumber()
  {
    return this.sampleNumber;
  }

  public void setSampleNumber(long[] paramArrayOfLong)
  {
    this.sampleNumber = paramArrayOfLong;
  }

  public String toString()
  {
    return "SyncSampleBox[entryCount=" + this.sampleNumber.length + "]";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.SyncSampleBox
 * JD-Core Version:    0.5.4
 */