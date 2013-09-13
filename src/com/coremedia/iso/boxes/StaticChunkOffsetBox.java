package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;

public class StaticChunkOffsetBox extends ChunkOffsetBox
{
  private long[] chunkOffsets = new long[0];

  public StaticChunkOffsetBox()
  {
    super("stco");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int i = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.chunkOffsets = new long[i];
    for (int j = 0; j < i; ++j)
      this.chunkOffsets[j] = IsoTypeReader.readUInt32(paramByteBuffer);
  }

  public long[] getChunkOffsets()
  {
    return this.chunkOffsets;
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.chunkOffsets.length);
    long[] arrayOfLong = this.chunkOffsets;
    int i = arrayOfLong.length;
    for (int j = 0; j < i; ++j)
      IsoTypeWriter.writeUInt32(paramByteBuffer, arrayOfLong[j]);
  }

  protected long getContentSize()
  {
    return 8 + 4 * this.chunkOffsets.length;
  }

  public void setChunkOffsets(long[] paramArrayOfLong)
  {
    this.chunkOffsets = paramArrayOfLong;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.StaticChunkOffsetBox
 * JD-Core Version:    0.5.4
 */