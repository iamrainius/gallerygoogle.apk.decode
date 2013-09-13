package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;

public class SampleSizeBox extends AbstractFullBox
{
  int sampleCount;
  private long sampleSize;
  private long[] sampleSizes = new long[0];

  public SampleSizeBox()
  {
    super("stsz");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.sampleSize = IsoTypeReader.readUInt32(paramByteBuffer);
    this.sampleCount = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    if (this.sampleSize != 0L)
      return;
    this.sampleSizes = new long[this.sampleCount];
    for (int i = 0; i < this.sampleCount; ++i)
      this.sampleSizes[i] = IsoTypeReader.readUInt32(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.sampleSize);
    if (this.sampleSize == 0L)
    {
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.sampleSizes.length);
      long[] arrayOfLong = this.sampleSizes;
      int i = arrayOfLong.length;
      for (int j = 0; ; ++j)
      {
        if (j >= i)
          return;
        IsoTypeWriter.writeUInt32(paramByteBuffer, arrayOfLong[j]);
      }
    }
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.sampleCount);
  }

  protected long getContentSize()
  {
    if (this.sampleSize == 0L);
    for (int i = 4 * this.sampleSizes.length; ; i = 0)
      return i + 12;
  }

  public long getSampleCount()
  {
    if (this.sampleSize > 0L)
      return this.sampleCount;
    return this.sampleSizes.length;
  }

  public long getSampleSize()
  {
    return this.sampleSize;
  }

  public long[] getSampleSizes()
  {
    return this.sampleSizes;
  }

  public void setSampleSizes(long[] paramArrayOfLong)
  {
    this.sampleSizes = paramArrayOfLong;
  }

  public String toString()
  {
    return "SampleSizeBox[sampleSize=" + getSampleSize() + ";sampleCount=" + getSampleCount() + "]";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.SampleSizeBox
 * JD-Core Version:    0.5.4
 */