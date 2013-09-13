package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.coremedia.iso.Hex;
import java.nio.ByteBuffer;

public class UnknownEntry extends GroupEntry
{
  private ByteBuffer content;

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    UnknownEntry localUnknownEntry;
    do
    {
      return true;
      if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
        return false;
      localUnknownEntry = (UnknownEntry)paramObject;
      if (this.content == null)
        break;
    }
    while (this.content.equals(localUnknownEntry.content));
    while (true)
    {
      return false;
      if (localUnknownEntry.content == null);
    }
  }

  public ByteBuffer get()
  {
    return this.content.duplicate();
  }

  public int hashCode()
  {
    if (this.content != null)
      return this.content.hashCode();
    return 0;
  }

  public void parse(ByteBuffer paramByteBuffer)
  {
    this.content = ((ByteBuffer)paramByteBuffer.duplicate().rewind());
  }

  public String toString()
  {
    ByteBuffer localByteBuffer = this.content.duplicate();
    localByteBuffer.rewind();
    byte[] arrayOfByte = new byte[localByteBuffer.limit()];
    localByteBuffer.get(arrayOfByte);
    return "UnknownEntry{content=" + Hex.encodeHex(arrayOfByte) + '}';
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.samplegrouping.UnknownEntry
 * JD-Core Version:    0.5.4
 */