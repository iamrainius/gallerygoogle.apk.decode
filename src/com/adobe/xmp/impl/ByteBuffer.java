package com.adobe.xmp.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteBuffer
{
  private byte[] buffer;
  private String encoding = null;
  private int length;

  public ByteBuffer(int paramInt)
  {
    this.buffer = new byte[paramInt];
    this.length = 0;
  }

  public ByteBuffer(InputStream paramInputStream)
    throws IOException
  {
    this.length = 0;
    this.buffer = new byte[16384];
    while (true)
    {
      int i = paramInputStream.read(this.buffer, this.length, 16384);
      if (i <= 0)
        return;
      this.length = (i + this.length);
      if (i != 16384)
        return;
      ensureCapacity(16384 + this.length);
    }
  }

  public ByteBuffer(byte[] paramArrayOfByte)
  {
    this.buffer = paramArrayOfByte;
    this.length = paramArrayOfByte.length;
  }

  private void ensureCapacity(int paramInt)
  {
    if (paramInt <= this.buffer.length)
      return;
    byte[] arrayOfByte = this.buffer;
    this.buffer = new byte[2 * arrayOfByte.length];
    System.arraycopy(arrayOfByte, 0, this.buffer, 0, arrayOfByte.length);
  }

  public void append(byte paramByte)
  {
    ensureCapacity(1 + this.length);
    byte[] arrayOfByte = this.buffer;
    int i = this.length;
    this.length = (i + 1);
    arrayOfByte[i] = paramByte;
  }

  public void append(byte[] paramArrayOfByte)
  {
    append(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public void append(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    ensureCapacity(paramInt2 + this.length);
    System.arraycopy(paramArrayOfByte, paramInt1, this.buffer, this.length, paramInt2);
    this.length = (paramInt2 + this.length);
  }

  public int charAt(int paramInt)
  {
    if (paramInt < this.length)
      return 0xFF & this.buffer[paramInt];
    throw new IndexOutOfBoundsException("The index exceeds the valid buffer area");
  }

  public InputStream getByteStream()
  {
    return new ByteArrayInputStream(this.buffer, 0, this.length);
  }

  public String getEncoding()
  {
    if (this.encoding == null)
      if (this.length >= 2)
        break label26;
    for (this.encoding = "UTF-8"; ; this.encoding = "UTF-32")
      while (true)
      {
        return this.encoding;
        if (this.buffer[0] == 0)
        {
          if ((this.length < 4) || (this.buffer[1] != 0))
            label26: this.encoding = "UTF-16BE";
          if (((0xFF & this.buffer[2]) == 254) && ((0xFF & this.buffer[3]) == 255))
            this.encoding = "UTF-32BE";
          this.encoding = "UTF-32";
        }
        if ((0xFF & this.buffer[0]) < 128)
        {
          if (this.buffer[1] != 0)
            this.encoding = "UTF-8";
          if ((this.length < 4) || (this.buffer[2] != 0))
            this.encoding = "UTF-16LE";
          this.encoding = "UTF-32LE";
        }
        if ((0xFF & this.buffer[0]) == 239)
          this.encoding = "UTF-8";
        if ((0xFF & this.buffer[0]) == 254)
          this.encoding = "UTF-16";
        if ((this.length >= 4) && (this.buffer[2] == 0))
          break;
        this.encoding = "UTF-16";
      }
  }

  public int length()
  {
    return this.length;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.ByteBuffer
 * JD-Core Version:    0.5.4
 */