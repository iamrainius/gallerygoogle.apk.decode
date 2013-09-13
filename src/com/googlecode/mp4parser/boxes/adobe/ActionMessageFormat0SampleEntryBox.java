package com.googlecode.mp4parser.boxes.adobe;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.sampleentry.SampleEntry;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

public class ActionMessageFormat0SampleEntryBox extends SampleEntry
{
  public ActionMessageFormat0SampleEntryBox()
  {
    super("amf0");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    _parseReservedAndDataReferenceIndex(paramByteBuffer);
    _parseChildBoxes(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    _writeReservedAndDataReferenceIndex(paramByteBuffer);
    _writeChildBoxes(paramByteBuffer);
  }

  protected long getContentSize()
  {
    long l = 8L;
    Iterator localIterator = this.boxes.iterator();
    while (localIterator.hasNext())
      l += ((Box)localIterator.next()).getSize();
    return l;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.adobe.ActionMessageFormat0SampleEntryBox
 * JD-Core Version:    0.5.4
 */