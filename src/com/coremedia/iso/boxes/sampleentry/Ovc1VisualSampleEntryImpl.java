package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

public class Ovc1VisualSampleEntryImpl extends SampleEntry
{
  private byte[] vc1Content;

  protected Ovc1VisualSampleEntryImpl()
  {
    super("ovc1");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    _parseReservedAndDataReferenceIndex(paramByteBuffer);
    this.vc1Content = new byte[paramByteBuffer.remaining()];
    paramByteBuffer.get(this.vc1Content);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(new byte[6]);
    IsoTypeWriter.writeUInt16(paramByteBuffer, getDataReferenceIndex());
    paramByteBuffer.put(this.vc1Content);
  }

  protected long getContentSize()
  {
    long l = 8L;
    Iterator localIterator = this.boxes.iterator();
    while (localIterator.hasNext())
      l += ((Box)localIterator.next()).getSize();
    return l + this.vc1Content.length;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.sampleentry.Ovc1VisualSampleEntryImpl
 * JD-Core Version:    0.5.4
 */