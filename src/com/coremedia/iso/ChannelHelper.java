package com.coremedia.iso;

import com.googlecode.mp4parser.util.CastUtils;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.ReadableByteChannel;

public class ChannelHelper
{
  static
  {
    if (!ChannelHelper.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public static int readFully(ReadableByteChannel paramReadableByteChannel, ByteBuffer paramByteBuffer, int paramInt)
    throws IOException
  {
    int i = 0;
    int j;
    do
    {
      j = paramReadableByteChannel.read(paramByteBuffer);
      if (-1 == j)
        break;
      i += j;
    }
    while (i != paramInt);
    if (j == -1)
      throw new EOFException("End of file. No more boxes.");
    return i;
  }

  public static ByteBuffer readFully(ReadableByteChannel paramReadableByteChannel, long paramLong)
    throws IOException
  {
    if ((paramReadableByteChannel instanceof FileChannel) && (paramLong > 1048576L))
    {
      MappedByteBuffer localMappedByteBuffer = ((FileChannel)paramReadableByteChannel).map(FileChannel.MapMode.READ_ONLY, ((FileChannel)paramReadableByteChannel).position(), paramLong);
      ((FileChannel)paramReadableByteChannel).position(paramLong + ((FileChannel)paramReadableByteChannel).position());
      return localMappedByteBuffer;
    }
    ByteBuffer localByteBuffer = ByteBuffer.allocate(CastUtils.l2i(paramLong));
    readFully(paramReadableByteChannel, localByteBuffer, localByteBuffer.limit());
    localByteBuffer.rewind();
    assert (localByteBuffer.limit() == paramLong);
    return localByteBuffer;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.ChannelHelper
 * JD-Core Version:    0.5.4
 */