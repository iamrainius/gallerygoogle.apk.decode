package com.googlecode.mp4parser.boxes.apple;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.sampleentry.SampleEntry;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

public class TimeCodeBox extends SampleEntry
{
  byte[] data;

  public TimeCodeBox()
  {
    super("tmcd");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    _parseReservedAndDataReferenceIndex(paramByteBuffer);
    this.data = new byte[18];
    paramByteBuffer.get(this.data);
    _parseChildBoxes(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    _writeReservedAndDataReferenceIndex(paramByteBuffer);
    paramByteBuffer.put(this.data);
    _writeChildBoxes(paramByteBuffer);
  }

  protected long getContentSize()
  {
    long l = 26L;
    Iterator localIterator = this.boxes.iterator();
    while (localIterator.hasNext())
      l += ((Box)localIterator.next()).getSize();
    return l;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.apple.TimeCodeBox
 * JD-Core Version:    0.5.4
 */