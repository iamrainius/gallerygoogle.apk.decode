package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MpegSampleEntry extends SampleEntry
  implements ContainerBox
{
  public MpegSampleEntry(String paramString)
  {
    super(paramString);
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

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("MpegSampleEntry");
    List[] arrayOfList = new List[1];
    arrayOfList[0] = getBoxes();
    return Arrays.asList(arrayOfList);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.sampleentry.MpegSampleEntry
 * JD-Core Version:    0.5.4
 */