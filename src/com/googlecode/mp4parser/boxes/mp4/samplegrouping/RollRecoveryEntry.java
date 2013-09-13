package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import java.nio.ByteBuffer;

public class RollRecoveryEntry extends GroupEntry
{
  private short rollDistance;

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    RollRecoveryEntry localRollRecoveryEntry;
    do
    {
      return true;
      if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
        return false;
      localRollRecoveryEntry = (RollRecoveryEntry)paramObject;
    }
    while (this.rollDistance == localRollRecoveryEntry.rollDistance);
    return false;
  }

  public ByteBuffer get()
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(2);
    localByteBuffer.putShort(this.rollDistance);
    localByteBuffer.rewind();
    return localByteBuffer;
  }

  public int hashCode()
  {
    return this.rollDistance;
  }

  public void parse(ByteBuffer paramByteBuffer)
  {
    this.rollDistance = paramByteBuffer.getShort();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.samplegrouping.RollRecoveryEntry
 * JD-Core Version:    0.5.4
 */