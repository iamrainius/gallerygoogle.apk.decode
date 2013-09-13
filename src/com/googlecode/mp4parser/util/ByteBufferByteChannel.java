package com.googlecode.mp4parser.util;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class ByteBufferByteChannel
  implements ByteChannel
{
  ByteBuffer byteBuffer;

  public ByteBufferByteChannel(ByteBuffer paramByteBuffer)
  {
    this.byteBuffer = paramByteBuffer;
  }

  public void close()
    throws IOException
  {
  }

  public boolean isOpen()
  {
    return true;
  }

  public int read(ByteBuffer paramByteBuffer)
    throws IOException
  {
    byte[] arrayOfByte = paramByteBuffer.array();
    int i = paramByteBuffer.remaining();
    if (this.byteBuffer.remaining() >= i)
    {
      this.byteBuffer.get(arrayOfByte, paramByteBuffer.position(), i);
      return i;
    }
    throw new EOFException("Reading beyond end of stream");
  }

  public int write(ByteBuffer paramByteBuffer)
    throws IOException
  {
    int i = paramByteBuffer.remaining();
    this.byteBuffer.put(paramByteBuffer);
    return i;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.util.ByteBufferByteChannel
 * JD-Core Version:    0.5.4
 */