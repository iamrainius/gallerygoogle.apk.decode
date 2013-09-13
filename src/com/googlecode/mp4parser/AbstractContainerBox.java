package com.googlecode.mp4parser;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import com.googlecode.mp4parser.util.ByteBufferByteChannel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractContainerBox extends AbstractBox
  implements ContainerBox
{
  private static Logger LOG = Logger.getLogger(AbstractContainerBox.class.getName());
  protected BoxParser boxParser;
  protected List<Box> boxes = new LinkedList();

  public AbstractContainerBox(String paramString)
  {
    super(paramString);
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseChildBoxes(paramByteBuffer);
  }

  public void addBox(Box paramBox)
  {
    paramBox.setParent(this);
    this.boxes.add(paramBox);
  }

  public List<Box> getBoxes()
  {
    return Collections.unmodifiableList(this.boxes);
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
      if (paramClass.isInstance(localBox))
        localArrayList.add(localBox);
      if ((!paramBoolean) || (!localBox instanceof ContainerBox))
        continue;
      localArrayList.addAll(((ContainerBox)localBox).getBoxes(paramClass, paramBoolean));
    }
    return localArrayList;
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeChildBoxes(paramByteBuffer);
  }

  protected long getContentSize()
  {
    long l = 0L;
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
    LOG.warning("Something's wrong with the sizes. There are dead bytes in a container box.");
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
        throw new RuntimeException("Cannot happen to me", localIOException);
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.AbstractContainerBox
 * JD-Core Version:    0.5.4
 */