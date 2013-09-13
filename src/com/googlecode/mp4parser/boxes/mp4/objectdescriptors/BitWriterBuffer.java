package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import java.nio.ByteBuffer;

public class BitWriterBuffer
{
  private ByteBuffer buffer;
  int initialPos;
  int position = 0;

  static
  {
    if (!BitWriterBuffer.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public BitWriterBuffer(ByteBuffer paramByteBuffer)
  {
    this.buffer = paramByteBuffer;
    this.initialPos = paramByteBuffer.position();
  }

  public void writeBits(int paramInt1, int paramInt2)
  {
    int i = 1;
    if ((!$assertionsDisabled) && (paramInt1 > -1 + (i << paramInt2)))
    {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = Integer.valueOf(paramInt1);
      arrayOfObject[i] = Integer.valueOf(-1 + (i << paramInt2));
      throw new AssertionError(String.format("Trying to write a value bigger (%s) than the number bits (%s) allows. Please mask the value before writing it and make your code is really working as intended.", arrayOfObject));
    }
    int k = 8 - this.position % 8;
    label173: ByteBuffer localByteBuffer1;
    int i1;
    if (paramInt2 <= k)
    {
      int i2 = this.buffer.get(this.initialPos + this.position / 8);
      if (i2 < 0)
        i2 += 256;
      int i3 = i2 + (paramInt1 << k - paramInt2);
      ByteBuffer localByteBuffer2 = this.buffer;
      int i4 = this.initialPos + this.position / 8;
      if (i3 > 127);
      localByteBuffer2.put(???, (byte)(i3 -= 256));
      this.position = (paramInt2 + this.position);
      localByteBuffer1 = this.buffer;
      i1 = this.initialPos + this.position / 8;
      if (this.position % 8 <= 0)
        break label247;
    }
    while (true)
    {
      localByteBuffer1.position(i + i1);
      return;
      int l = paramInt2 - k;
      writeBits(paramInt1 >> l, k);
      writeBits(paramInt1 & -1 + (i << l), l);
      break label173:
      label247: int j = 0;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer
 * JD-Core Version:    0.5.4
 */