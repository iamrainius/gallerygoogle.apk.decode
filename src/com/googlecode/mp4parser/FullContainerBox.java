package com.googlecode.mp4parser;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import com.googlecode.mp4parser.util.ByteBufferByteChannel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public abstract class FullContainerBox extends AbstractFullBox
  implements ContainerBox
{
  private static Logger LOG = Logger.getLogger(FullContainerBox.class.getName());
  BoxParser boxParser;
  protected List<Box> boxes = new LinkedList();

  public FullContainerBox(String paramString)
  {
    super(paramString);
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    parseChildBoxes(paramByteBuffer);
  }

  public void addBox(Box paramBox)
  {
    paramBox.setParent(this);
    this.boxes.add(paramBox);
  }

  public List<Box> getBoxes()
  {
    return this.boxes;
  }

  public <T extends Box> List<T> getBoxes(Class<T> paramClass)
  {
    return getBoxes(paramClass, false);
  }

  public <T extends Box> List<T> getBoxes(Class<T> paramClass, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList(2);
    Iterator localIterator = this.boxes.iterator();
    while (localIterator.hasNext())
    {
      Box localBox = (Box)localIterator.next();
      if (paramClass == localBox.getClass())
        localArrayList.add(localBox);
      if ((!paramBoolean) || (!localBox instanceof ContainerBox))
        continue;
      localArrayList.addAll(((ContainerBox)localBox).getBoxes(paramClass, paramBoolean));
    }
    return localArrayList;
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    writeChildBoxes(paramByteBuffer);
  }

  protected long getContentSize()
  {
    long l = 4L;
    Iterator localIterator = this.boxes.iterator();
    while (localIterator.hasNext())
      l += ((Box)localIterator.next()).getSize();
    return l;
  }

  public void parse(ReadableByteChannel paramReadableByteChannel, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    this.boxParser = paramBoxParser;
    super.parse(paramReadableByteChannel, paramByteBuffer, paramLong, paramBoxParser);
  }

  protected final void parseChildBoxes(ByteBuffer paramByteBuffer)
  {
    try
    {
      if (paramByteBuffer.remaining() < 8)
        break label50;
      this.boxes.add(this.boxParser.parseBox(new ByteBufferByteChannel(paramByteBuffer), this));
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
    if (paramByteBuffer.remaining() == 0)
      label50: return;
    setDeadBytes(paramByteBuffer.slice());
    LOG.severe("Some sizes are wrong");
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(super.getClass().getSimpleName()).append("[");
    for (int i = 0; i < this.boxes.size(); ++i)
    {
      if (i > 0)
        localStringBuilder.append(";");
      localStringBuilder.append(((Box)this.boxes.get(i)).toString());
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }

  protected final void writeChildBoxes(ByteBuffer paramByteBuffer)
  {
    ByteBufferByteChannel localByteBufferByteChannel = new ByteBufferByteChannel(paramByteBuffer);
    Iterator localIterator = this.boxes.iterator();
    while (localIterator.hasNext())
    {
      Box localBox = (Box)localIterator.next();
      try
      {
        localBox.getBox(localByteBufferByteChannel);
      }
      catch (IOException localIOException)
      {
        throw new RuntimeException("Cannot happen.", localIOException);
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.FullContainerBox
 * JD-Core Version:    0.5.4
 */