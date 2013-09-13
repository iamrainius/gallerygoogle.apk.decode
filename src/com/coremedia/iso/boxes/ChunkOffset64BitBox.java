package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;

public class ChunkOffset64BitBox extends ChunkOffsetBox
{
  private long[] chunkOffsets;

  public ChunkOffset64BitBox()
  {
    super("co64");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int i = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.chunkOffsets = new long[i];
    for (int j = 0; j < i; ++j)
      this.chunkOffsets[j] = IsoTypeReader.readUInt64(paramByteBuffer);
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
      IsoTypeWriter.writeUInt64(paramByteBuffer, arrayOfLong[j]);
  }

  protected long getContentSize()
  {
    return 8 + 8 * this.chunkOffsets.length;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.ChunkOffset64BitBox
 * JD-Core Version:    0.5.4
 */