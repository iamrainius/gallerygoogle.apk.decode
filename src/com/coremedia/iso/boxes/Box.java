package com.coremedia.iso.boxes;

import com.coremedia.iso.BoxParser;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public abstract interface Box
{
  public abstract void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException;

  public abstract ContainerBox getParent();

  public abstract long getSize();

  public abstract String getType();

  public abstract void parse(ReadableByteChannel paramReadableByteChannel, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException;

  public abstract void setParent(ContainerBox paramContainerBox);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.Box
 * JD-Core Version:    0.5.4
 */