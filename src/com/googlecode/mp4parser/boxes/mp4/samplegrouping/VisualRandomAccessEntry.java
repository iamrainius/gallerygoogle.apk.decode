package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import java.nio.ByteBuffer;

public class VisualRandomAccessEntry extends GroupEntry
{
  private short numLeadingSamples;
  private boolean numLeadingSamplesKnown;

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    VisualRandomAccessEntry localVisualRandomAccessEntry;
    do
    {
      return true;
      if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
        return false;
      localVisualRandomAccessEntry = (VisualRandomAccessEntry)paramObject;
      if (this.numLeadingSamples != localVisualRandomAccessEntry.numLeadingSamples)
        return false;
    }
    while (this.numLeadingSamplesKnown == localVisualRandomAccessEntry.numLeadingSamplesKnown);
    return false;
  }

  public ByteBuffer get()
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(1);
    if (this.numLeadingSamplesKnown);
    for (int i = 128; ; i = 0)
    {
      localByteBuffer.put((byte)(i | 0x7F & this.numLeadingSamples));
      localByteBuffer.rewind();
      return localByteBuffer;
    }
  }

  public int hashCode()
  {
    if (this.numLeadingSamplesKnown);
    for (int i = 1; ; i = 0)
      return i * 31 + this.numLeadingSamples;
  }

  public void parse(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.get();
    if ((i & 0x80) == 128);
    for (int j = 1; ; j = 0)
    {
      this.numLeadingSamplesKnown = j;
      this.numLeadingSamples = (short)(i & 0x7F);
      return;
    }
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("VisualRandomAccessEntry");
    localStringBuilder.append("{numLeadingSamplesKnown=").append(this.numLeadingSamplesKnown);
    localStringBuilder.append(", numLeadingSamples=").append(this.numLeadingSamples);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.samplegrouping.VisualRandomAccessEntry
 * JD-Core Version:    0.5.4
 */