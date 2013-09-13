package com.coremedia.iso.boxes.mdat;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.ChannelHelper;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class MediaDataBox
  implements Box
{
  private static Logger LOG;
  private Map<Long, Reference<ByteBuffer>> cache = new HashMap();
  private ByteBuffer content;
  private long contentSize;
  private FileChannel fileChannel;
  ByteBuffer header;
  ContainerBox parent;
  private long startPosition;

  static
  {
    if (!MediaDataBox.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      LOG = Logger.getLogger(MediaDataBox.class.getName());
      return;
    }
  }

  private boolean checkStillOk()
  {
    try
    {
      this.fileChannel.position(this.startPosition - this.header.limit());
      ByteBuffer localByteBuffer = ByteBuffer.allocate(this.header.limit());
      this.fileChannel.read(localByteBuffer);
      this.header.rewind();
      localByteBuffer.rewind();
      if ((!$assertionsDisabled) && (!localByteBuffer.equals(this.header)))
        throw new AssertionError("It seems that the content I want to read has already been overwritten.");
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
      return false;
    }
    return true;
  }

  private static void transfer(FileChannel paramFileChannel, long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    long l = 0L;
    while (l < paramLong2)
      l += paramFileChannel.transferTo(paramLong1 + l, Math.min(67076096L, paramLong2 - l), paramWritableByteChannel);
  }

  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    if (this.fileChannel != null)
    {
      assert (checkStillOk());
      transfer(this.fileChannel, this.startPosition - this.header.limit(), this.contentSize + this.header.limit(), paramWritableByteChannel);
      return;
    }
    this.header.rewind();
    paramWritableByteChannel.write(this.header);
    paramWritableByteChannel.write(this.content);
  }

  // ERROR //
  public ByteBuffer getContent(long paramLong, int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 53	com/coremedia/iso/boxes/mdat/MediaDataBox:cache	Ljava/util/Map;
    //   6: invokeinterface 138 1 0
    //   11: invokeinterface 144 1 0
    //   16: astore 5
    //   18: aload 5
    //   20: invokeinterface 149 1 0
    //   25: ifeq +119 -> 144
    //   28: aload 5
    //   30: invokeinterface 153 1 0
    //   35: checkcast 155	java/lang/Long
    //   38: astore 12
    //   40: aload 12
    //   42: invokevirtual 159	java/lang/Long:longValue	()J
    //   45: lload_1
    //   46: lcmp
    //   47: ifgt -29 -> 18
    //   50: lload_1
    //   51: ldc2_w 160
    //   54: aload 12
    //   56: invokevirtual 159	java/lang/Long:longValue	()J
    //   59: ladd
    //   60: lcmp
    //   61: ifgt -43 -> 18
    //   64: aload_0
    //   65: getfield 53	com/coremedia/iso/boxes/mdat/MediaDataBox:cache	Ljava/util/Map;
    //   68: aload 12
    //   70: invokeinterface 165 2 0
    //   75: checkcast 167	java/lang/ref/Reference
    //   78: invokevirtual 169	java/lang/ref/Reference:get	()Ljava/lang/Object;
    //   81: checkcast 64	java/nio/ByteBuffer
    //   84: astore 13
    //   86: aload 13
    //   88: ifnull -70 -> 18
    //   91: aload 12
    //   93: invokevirtual 159	java/lang/Long:longValue	()J
    //   96: aload 13
    //   98: invokevirtual 68	java/nio/ByteBuffer:limit	()I
    //   101: i2l
    //   102: ladd
    //   103: lload_1
    //   104: iload_3
    //   105: i2l
    //   106: ladd
    //   107: lcmp
    //   108: iflt -90 -> 18
    //   111: aload 13
    //   113: lload_1
    //   114: aload 12
    //   116: invokevirtual 159	java/lang/Long:longValue	()J
    //   119: lsub
    //   120: l2i
    //   121: invokevirtual 172	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   124: pop
    //   125: aload 13
    //   127: invokevirtual 176	java/nio/ByteBuffer:slice	()Ljava/nio/ByteBuffer;
    //   130: astore 10
    //   132: aload 10
    //   134: iload_3
    //   135: invokevirtual 178	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
    //   138: pop
    //   139: aload_0
    //   140: monitorexit
    //   141: aload 10
    //   143: areturn
    //   144: aload_0
    //   145: getfield 58	com/coremedia/iso/boxes/mdat/MediaDataBox:fileChannel	Ljava/nio/channels/FileChannel;
    //   148: getstatic 184	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
    //   151: lload_1
    //   152: aload_0
    //   153: getfield 60	com/coremedia/iso/boxes/mdat/MediaDataBox:startPosition	J
    //   156: ladd
    //   157: ldc2_w 160
    //   160: aload_0
    //   161: getfield 121	com/coremedia/iso/boxes/mdat/MediaDataBox:contentSize	J
    //   164: lload_1
    //   165: lsub
    //   166: invokestatic 110	java/lang/Math:min	(JJ)J
    //   169: invokevirtual 188	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
    //   172: astore 7
    //   174: aload_0
    //   175: getfield 53	com/coremedia/iso/boxes/mdat/MediaDataBox:cache	Ljava/util/Map;
    //   178: lload_1
    //   179: invokestatic 192	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   182: new 194	java/lang/ref/SoftReference
    //   185: dup
    //   186: aload 7
    //   188: invokespecial 195	java/lang/ref/SoftReference:<init>	(Ljava/lang/Object;)V
    //   191: invokeinterface 199 3 0
    //   196: pop
    //   197: aload 7
    //   199: iconst_0
    //   200: invokevirtual 202	java/nio/MappedByteBuffer:position	(I)Ljava/nio/Buffer;
    //   203: pop
    //   204: aload 7
    //   206: invokevirtual 176	java/nio/ByteBuffer:slice	()Ljava/nio/ByteBuffer;
    //   209: astore 10
    //   211: aload 10
    //   213: iload_3
    //   214: invokevirtual 178	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
    //   217: pop
    //   218: goto -79 -> 139
    //   221: astore 4
    //   223: aload_0
    //   224: monitorexit
    //   225: aload 4
    //   227: athrow
    //   228: astore 6
    //   230: getstatic 45	com/coremedia/iso/boxes/mdat/MediaDataBox:LOG	Ljava/util/logging/Logger;
    //   233: new 204	java/lang/StringBuilder
    //   236: dup
    //   237: invokespecial 205	java/lang/StringBuilder:<init>	()V
    //   240: ldc 207
    //   242: invokevirtual 211	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   245: aload 6
    //   247: invokevirtual 214	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   250: invokevirtual 217	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   253: invokevirtual 221	java/util/logging/Logger:fine	(Ljava/lang/String;)V
    //   256: new 223	java/lang/RuntimeException
    //   259: dup
    //   260: ldc 225
    //   262: aload 6
    //   264: invokespecial 228	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   267: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   2	18	221	finally
    //   18	86	221	finally
    //   91	139	221	finally
    //   144	174	221	finally
    //   174	218	221	finally
    //   230	268	221	finally
    //   144	174	228	java/io/IOException
  }

  public ByteBuffer getHeader()
  {
    return this.header;
  }

  public ContainerBox getParent()
  {
    return this.parent;
  }

  public long getSize()
  {
    return this.header.limit() + this.contentSize;
  }

  public String getType()
  {
    return "mdat";
  }

  public void parse(ReadableByteChannel paramReadableByteChannel, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    this.header = paramByteBuffer;
    this.contentSize = paramLong;
    if ((paramReadableByteChannel instanceof FileChannel) && (paramLong > AbstractBox.MEM_MAP_THRESHOLD))
    {
      this.fileChannel = ((FileChannel)paramReadableByteChannel);
      this.startPosition = ((FileChannel)paramReadableByteChannel).position();
      ((FileChannel)paramReadableByteChannel).position(paramLong + ((FileChannel)paramReadableByteChannel).position());
      return;
    }
    this.content = ChannelHelper.readFully(paramReadableByteChannel, CastUtils.l2i(paramLong));
    this.cache.put(Long.valueOf(0L), new SoftReference(this.content));
  }

  public void setParent(ContainerBox paramContainerBox)
  {
    this.parent = paramContainerBox;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.mdat.MediaDataBox
 * JD-Core Version:    0.5.4
 */