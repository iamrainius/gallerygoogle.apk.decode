package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import java.nio.ByteBuffer;

public class TemporalLevelEntry extends GroupEntry
{
  private boolean levelIndependentlyDecodable;
  private short reserved;

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    TemporalLevelEntry localTemporalLevelEntry;
    do
    {
      return true;
      if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
        return false;
      localTemporalLevelEntry = (TemporalLevelEntry)paramObject;
      if (this.levelIndependentlyDecodable != localTemporalLevelEntry.levelIndependentlyDecodable)
        return false;
    }
    while (this.reserved == localTemporalLevelEntry.reserved);
    return false;
  }

  public ByteBuffer get()
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(1);
    if (this.levelIndependentlyDecodable);
    for (int i = 128; ; i = 0)
    {
      localByteBuffer.put((byte)i);
      localByteBuffer.rewind();
      return localByteBuffer;
    }
  }

  public int hashCode()
  {
    if (this.levelIndependentlyDecodable);
    for (int i = 1; ; i = 0)
      return i * 31 + this.reserved;
  }

  public void parse(ByteBuffer paramByteBuffer)
  {
    if ((0x80 & paramByteBuffer.get()) == 128);
    for (int i = 1; ; i = 0)
    {
      this.levelIndependentlyDecodable = i;
      return;
    }
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("TemporalLevelEntry");
    localStringBuilder.append("{levelIndependentlyDecodable=").append(this.levelIndependentlyDecodable);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.samplegrouping.TemporalLevelEntry
 * JD-Core Version:    0.5.4
 */