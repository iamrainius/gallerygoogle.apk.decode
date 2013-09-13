package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import java.nio.ByteBuffer;

public abstract class GroupEntry
{
  public abstract ByteBuffer get();

  public abstract void parse(ByteBuffer paramByteBuffer);

  public int size()
  {
    return get().limit();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry
 * JD-Core Version:    0.5.4
 */