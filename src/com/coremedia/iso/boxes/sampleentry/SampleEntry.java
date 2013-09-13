package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.util.ByteBufferByteChannel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class SampleEntry extends AbstractBox
  implements ContainerBox
{
  private BoxParser boxParser;
  protected List<Box> boxes = new LinkedList();
  private int dataReferenceIndex = 1;

  protected SampleEntry(String paramString)
  {
    super(paramString);
  }

  public void _parseChildBoxes(ByteBuffer paramByteBuffer)
  {
    while (paramByteBuffer.remaining() > 8)
      try
      {
        this.boxes.add(this.boxParser.parseBox(new ByteBufferByteChannel(paramByteBuffer), this));
      }
      catch (IOException localIOException)
      {
        throw new RuntimeException(localIOException);
      }
    setDeadBytes(paramByteBuffer.slice());
  }

  public void _parseReservedAndDataReferenceIndex(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.get(new byte[6]);
    this.dataReferenceIndex = IsoTypeReader.readUInt16(paramByteBuffer);
  }

  public void _writeChildBoxes(ByteBuffer paramByteBuffer)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    WritableByteChannel localWritableByteChannel = Channels.newChannel(localByteArrayOutputStream);
    try
    {
      Iterator localIterator = this.boxes.iterator();
      if (!localIterator.hasNext())
        break label65;
      ((Box)localIterator.next()).getBox(localWritableByteChannel);
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException("Cannot happen. Everything should be in memory and therefore no exceptions.");
    }
    label65: localWritableByteChannel.close();
    paramByteBuffer.put(localByteArrayOutputStream.toByteArray());
  }

  public void _writeReservedAndDataReferenceIndex(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(new byte[6]);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.dataReferenceIndex);
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

  public int getDataReferenceIndex()
  {
    return this.dataReferenceIndex;
  }

  public void parse(ReadableByteChannel paramReadableByteChannel, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    this.boxParser = paramBoxParser;
    super.parse(paramReadableByteChannel, paramByteBuffer, paramLong, paramBoxParser);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.sampleentry.SampleEntry
 * JD-Core Version:    0.5.4
 */