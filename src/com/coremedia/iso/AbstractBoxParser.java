package com.coremedia.iso;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Logger;

public abstract class AbstractBoxParser
  implements BoxParser
{
  private static Logger LOG;

  static
  {
    if (!AbstractBoxParser.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      LOG = Logger.getLogger(AbstractBoxParser.class.getName());
      return;
    }
  }

  public abstract Box createBox(String paramString1, byte[] paramArrayOfByte, String paramString2);

  public Box parseBox(ReadableByteChannel paramReadableByteChannel, ContainerBox paramContainerBox)
    throws IOException
  {
    ByteBuffer localByteBuffer1 = ChannelHelper.readFully(paramReadableByteChannel, 8L);
    long l1 = IsoTypeReader.readUInt32(localByteBuffer1);
    Box localBox;
    if ((l1 < 8L) && (l1 > 1L))
    {
      LOG.severe("Plausibility check failed: size < 8 (size = " + l1 + "). Stop parsing!");
      localBox = null;
      return localBox;
    }
    String str = IsoTypeReader.read4cc(localByteBuffer1);
    long l2;
    label117: byte[] arrayOfByte;
    if (l1 == 1L)
    {
      ByteBuffer localByteBuffer3 = ByteBuffer.allocate(8);
      paramReadableByteChannel.read(localByteBuffer3);
      localByteBuffer3.rewind();
      l1 = IsoTypeReader.readUInt64(localByteBuffer3);
      l2 = l1 - 16L;
      boolean bool = "uuid".equals(str);
      arrayOfByte = null;
      if (bool)
      {
        ByteBuffer localByteBuffer2 = ByteBuffer.allocate(16);
        paramReadableByteChannel.read(localByteBuffer2);
        localByteBuffer2.rewind();
        arrayOfByte = localByteBuffer2.array();
        l2 -= 16L;
      }
      localBox = createBox(str, arrayOfByte, paramContainerBox.getType());
      localBox.setParent(paramContainerBox);
      LOG.finest("Parsing " + localBox.getType());
      if (CastUtils.l2i(l1 - l2) != 8)
        break label405;
      localByteBuffer1.rewind();
    }
    while (true)
    {
      localBox.parse(paramReadableByteChannel, localByteBuffer1, l2, this);
      if ((!$assertionsDisabled) && (l1 != localBox.getSize()));
      throw new AssertionError("Reconstructed Size is not x to the number of parsed bytes! (" + localBox.getType() + ")" + " Actual Box size: " + l1 + " Calculated size: " + localBox.getSize());
      if (l1 == 0L)
      {
        if (paramReadableByteChannel instanceof FileChannel)
        {
          l1 = ((FileChannel)paramReadableByteChannel).size() - ((FileChannel)paramReadableByteChannel).position() - 8L;
          l2 = l1 - 8L;
        }
        throw new RuntimeException("Only FileChannel inputs may use size == 0 (box reaches to the end of file)");
      }
      l2 = l1 - 8L;
      break label117:
      if (CastUtils.l2i(l1 - l2) == 16)
      {
        label405: localByteBuffer1 = ByteBuffer.allocate(16);
        IsoTypeWriter.writeUInt32(localByteBuffer1, 1L);
        localByteBuffer1.put(IsoFile.fourCCtoBytes(str));
        IsoTypeWriter.writeUInt64(localByteBuffer1, l1);
      }
      if (CastUtils.l2i(l1 - l2) == 24)
      {
        localByteBuffer1 = ByteBuffer.allocate(24);
        IsoTypeWriter.writeUInt32(localByteBuffer1, l1);
        localByteBuffer1.put(IsoFile.fourCCtoBytes(str));
        localByteBuffer1.put(arrayOfByte);
      }
      if (CastUtils.l2i(l1 - l2) != 32)
        break;
      localByteBuffer1 = ByteBuffer.allocate(32);
      IsoTypeWriter.writeUInt32(localByteBuffer1, l1);
      localByteBuffer1.put(IsoFile.fourCCtoBytes(str));
      IsoTypeWriter.writeUInt64(localByteBuffer1, l1);
      localByteBuffer1.put(arrayOfByte);
    }
    throw new RuntimeException("I didn't expect that");
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.AbstractBoxParser
 * JD-Core Version:    0.5.4
 */