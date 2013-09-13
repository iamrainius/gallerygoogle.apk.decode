package com.coremedia.iso.boxes;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.ChannelHelper;
import com.coremedia.iso.IsoTypeWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FreeBox
  implements Box
{
  ByteBuffer data;
  private ContainerBox parent;
  List<Box> replacers = new LinkedList();

  static
  {
    if (!FreeBox.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    Iterator localIterator = this.replacers.iterator();
    while (localIterator.hasNext())
      ((Box)localIterator.next()).getBox(paramWritableByteChannel);
    ByteBuffer localByteBuffer = ByteBuffer.allocate(8);
    IsoTypeWriter.writeUInt32(localByteBuffer, 8 + this.data.limit());
    localByteBuffer.put("free".getBytes());
    localByteBuffer.rewind();
    paramWritableByteChannel.write(localByteBuffer);
    this.data.rewind();
    paramWritableByteChannel.write(this.data);
  }

  public ContainerBox getParent()
  {
    return this.parent;
  }

  public long getSize()
  {
    long l = 8L;
    Iterator localIterator = this.replacers.iterator();
    while (localIterator.hasNext())
      l += ((Box)localIterator.next()).getSize();
    return l + this.data.limit();
  }

  public String getType()
  {
    return "free";
  }

  public void parse(ReadableByteChannel paramReadableByteChannel, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    if ((paramReadableByteChannel instanceof FileChannel) && (paramLong > 1048576L))
    {
      this.data = ((FileChannel)paramReadableByteChannel).map(FileChannel.MapMode.READ_ONLY, ((FileChannel)paramReadableByteChannel).position(), paramLong);
      ((FileChannel)paramReadableByteChannel).position(paramLong + ((FileChannel)paramReadableByteChannel).position());
      return;
    }
    assert (paramLong < 2147483647L);
    this.data = ChannelHelper.readFully(paramReadableByteChannel, paramLong);
  }

  public void setParent(ContainerBox paramContainerBox)
  {
    this.parent = paramContainerBox;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.FreeBox
 * JD-Core Version:    0.5.4
 */