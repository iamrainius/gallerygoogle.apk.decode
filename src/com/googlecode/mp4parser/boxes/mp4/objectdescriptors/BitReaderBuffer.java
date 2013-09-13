package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import java.nio.ByteBuffer;

public class BitReaderBuffer
{
  private ByteBuffer buffer;
  int initialPos;
  int position;

  public BitReaderBuffer(ByteBuffer paramByteBuffer)
  {
    this.buffer = paramByteBuffer;
    this.initialPos = paramByteBuffer.position();
  }

  public int readBits(int paramInt)
  {
    int i = this.buffer.get(this.initialPos + this.position / 8);
    int j;
    label30: int k;
    int i1;
    if (i < 0)
    {
      j = i + 256;
      k = 8 - this.position % 8;
      if (paramInt > k)
        break label120;
      i1 = (0xFF & j << this.position % 8) >> this.position % 8 + (k - paramInt);
      this.position = (paramInt + this.position);
    }
    while (true)
    {
      this.buffer.position(this.initialPos + (int)Math.ceil(this.position / 8.0D));
      return i1;
      j = i;
      break label30:
      label120: int l = paramInt - k;
      i1 = (readBits(k) << l) + readBits(l);
    }
  }

  public int remainingBits()
  {
    return 8 * this.buffer.limit() - this.position;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer
 * JD-Core Version:    0.5.4
 */