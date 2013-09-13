package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

public abstract class ChunkOffsetBox extends AbstractFullBox
{
  public ChunkOffsetBox(String paramString)
  {
    super(paramString);
  }

  public abstract long[] getChunkOffsets();

  public String toString()
  {
    return super.getClass().getSimpleName() + "[entryCount=" + getChunkOffsets().length + "]";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.ChunkOffsetBox
 * JD-Core Version:    0.5.4
 */