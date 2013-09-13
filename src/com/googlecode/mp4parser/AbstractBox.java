package com.googlecode.mp4parser;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.ChannelHelper;
import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Logger;

public abstract class AbstractBox
  implements Box
{
  private static Logger LOG;
  public static int MEM_MAP_THRESHOLD;
  private ByteBuffer content;
  private ByteBuffer deadBytes = null;
  private ContainerBox parent;
  protected String type;
  private byte[] userType;

  static
  {
    if (!AbstractBox.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      MEM_MAP_THRESHOLD = 102400;
      LOG = Logger.getLogger(AbstractBox.class.getName());
      return;
    }
  }

  protected AbstractBox(String paramString)
  {
    this.type = paramString;
  }

  protected AbstractBox(String paramString, byte[] paramArrayOfByte)
  {
    this.type = paramString;
    this.userType = paramArrayOfByte;
  }

  private void getHeader(ByteBuffer paramByteBuffer)
  {
    if (isSmallBox())
    {
      IsoTypeWriter.writeUInt32(paramByteBuffer, getSize());
      paramByteBuffer.put(IsoFile.fourCCtoBytes(getType()));
    }
    while (true)
    {
      if ("uuid".equals(getType()))
        paramByteBuffer.put(getUserType());
      return;
      IsoTypeWriter.writeUInt32(paramByteBuffer, 1L);
      paramByteBuffer.put(IsoFile.fourCCtoBytes(getType()));
      IsoTypeWriter.writeUInt64(paramByteBuffer, getSize());
    }
  }

  private boolean isSmallBox()
  {
    int j;
    label29: long l1;
    if (this.content == null)
    {
      long l2 = getContentSize();
      if (this.deadBytes != null)
      {
        j = this.deadBytes.limit();
        l1 = 8L + (l2 + j);
      }
    }
    while (true)
    {
      boolean bool = l1 < 4294967296L;
      int i = 0;
      if (bool)
        i = 1;
      return i;
      j = 0;
      break label29:
      l1 = this.content.limit();
    }
  }

  private boolean verify(ByteBuffer paramByteBuffer)
  {
    long l = getContentSize();
    if (this.deadBytes != null);
    ByteBuffer localByteBuffer;
    for (int i = this.deadBytes.limit(); ; i = 0)
    {
      localByteBuffer = ByteBuffer.allocate(CastUtils.l2i(l + i));
      getContent(localByteBuffer);
      if (this.deadBytes == null)
        break;
      this.deadBytes.rewind();
      while (true)
      {
        if (this.deadBytes.remaining() <= 0)
          break label84;
        localByteBuffer.put(this.deadBytes);
      }
    }
    label84: paramByteBuffer.rewind();
    localByteBuffer.rewind();
    if (paramByteBuffer.remaining() != localByteBuffer.remaining())
    {
      LOG.severe(getType() + ": remaining differs " + paramByteBuffer.remaining() + " vs. " + localByteBuffer.remaining());
      return false;
    }
    int j = paramByteBuffer.position();
    int k = -1 + paramByteBuffer.limit();
    for (int i1 = -1 + localByteBuffer.limit(); k >= j; --i1)
    {
      byte b1 = paramByteBuffer.get(k);
      byte b2 = localByteBuffer.get(i1);
      if (b1 != b2)
      {
        Logger localLogger = LOG;
        Object[] arrayOfObject = new Object[4];
        arrayOfObject[0] = getType();
        arrayOfObject[1] = Integer.valueOf(k);
        arrayOfObject[2] = Byte.valueOf(b1);
        arrayOfObject[3] = Byte.valueOf(b2);
        localLogger.severe(String.format("%s: buffers differ at %d: %2X/%2X", arrayOfObject));
        byte[] arrayOfByte1 = new byte[paramByteBuffer.remaining()];
        byte[] arrayOfByte2 = new byte[localByteBuffer.remaining()];
        paramByteBuffer.get(arrayOfByte1);
        localByteBuffer.get(arrayOfByte2);
        System.err.println("original      : " + Hex.encodeHex(arrayOfByte1, 4));
        System.err.println("reconstructed : " + Hex.encodeHex(arrayOfByte2, 4));
        return false;
      }
      --k;
    }
    return true;
  }

  protected abstract void _parseDetails(ByteBuffer paramByteBuffer);

  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(CastUtils.l2i(getSize()));
    getHeader(localByteBuffer);
    if (this.content == null)
    {
      getContent(localByteBuffer);
      if (this.deadBytes == null)
        break label82;
      this.deadBytes.rewind();
      while (true)
      {
        if (this.deadBytes.remaining() <= 0)
          break label82;
        localByteBuffer.put(this.deadBytes);
      }
    }
    this.content.rewind();
    localByteBuffer.put(this.content);
    label82: localByteBuffer.rewind();
    paramWritableByteChannel.write(localByteBuffer);
  }

  protected abstract void getContent(ByteBuffer paramByteBuffer);

  protected abstract long getContentSize();

  public IsoFile getIsoFile()
  {
    return this.parent.getIsoFile();
  }

  public ContainerBox getParent()
  {
    return this.parent;
  }

  public long getSize()
  {
    long l1;
    label12: int i;
    label23: int k;
    label45: long l2;
    int l;
    if (this.content == null)
    {
      l1 = getContentSize();
      if (l1 < 4294967288L)
        break label88;
      i = 8;
      int j = i + 8;
      if (!"uuid".equals(getType()))
        break label93;
      k = 16;
      l2 = l1 + (k + j);
      ByteBuffer localByteBuffer = this.deadBytes;
      l = 0;
      if (localByteBuffer != null)
        break label99;
    }
    while (true)
    {
      return l2 + l;
      l1 = this.content.limit();
      break label12:
      label88: i = 0;
      break label23:
      label93: k = 0;
      break label45:
      label99: l = this.deadBytes.limit();
    }
  }

  public String getType()
  {
    return this.type;
  }

  public byte[] getUserType()
  {
    return this.userType;
  }

  public boolean isParsed()
  {
    return this.content == null;
  }

  public void parse(ReadableByteChannel paramReadableByteChannel, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    if ((paramReadableByteChannel instanceof FileChannel) && (paramLong > MEM_MAP_THRESHOLD))
    {
      this.content = ((FileChannel)paramReadableByteChannel).map(FileChannel.MapMode.READ_ONLY, ((FileChannel)paramReadableByteChannel).position(), paramLong);
      ((FileChannel)paramReadableByteChannel).position(paramLong + ((FileChannel)paramReadableByteChannel).position());
    }
    while (true)
    {
      if (!isParsed())
        parseDetails();
      return;
      assert (paramLong < 2147483647L);
      this.content = ChannelHelper.readFully(paramReadableByteChannel, paramLong);
    }
  }

  final void parseDetails()
  {
    monitorenter;
    try
    {
      if (this.content != null)
      {
        ByteBuffer localByteBuffer = this.content;
        this.content = null;
        localByteBuffer.rewind();
        _parseDetails(localByteBuffer);
        if (localByteBuffer.remaining() > 0)
          this.deadBytes = localByteBuffer.slice();
        if (!$assertionsDisabled)
          throw new AssertionError();
      }
    }
    finally
    {
      monitorexit;
    }
    monitorexit;
  }

  protected void setDeadBytes(ByteBuffer paramByteBuffer)
  {
    this.deadBytes = paramByteBuffer;
  }

  public void setParent(ContainerBox paramContainerBox)
  {
    this.parent = paramContainerBox;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.AbstractBox
 * JD-Core Version:    0.5.4
 */