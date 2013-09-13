package com.android.gallery3d.exif;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class CountedDataInputStream extends FilterInputStream
{
  private final byte[] mByteArray = new byte[8];
  private final ByteBuffer mByteBuffer = ByteBuffer.wrap(this.mByteArray);
  private int mCount = 0;

  static
  {
    if (!CountedDataInputStream.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  protected CountedDataInputStream(InputStream paramInputStream)
  {
    super(paramInputStream);
  }

  public ByteOrder getByteOrder()
  {
    return this.mByteBuffer.order();
  }

  public int getReadByteCount()
  {
    return this.mCount;
  }

  public int read()
    throws IOException
  {
    int i = this.in.read();
    int j = this.mCount;
    if (i >= 0);
    for (int k = 1; ; k = 0)
    {
      this.mCount = (k + j);
      return i;
    }
  }

  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    int i = this.in.read(paramArrayOfByte);
    int j = this.mCount;
    if (i >= 0);
    for (int k = i; ; k = 0)
    {
      this.mCount = (k + j);
      return i;
    }
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
    int j = this.mCount;
    if (i >= 0);
    for (int k = i; ; k = 0)
    {
      this.mCount = (k + j);
      return i;
    }
  }

  public int readInt()
    throws IOException
  {
    readOrThrow(this.mByteArray, 0, 4);
    this.mByteBuffer.rewind();
    return this.mByteBuffer.getInt();
  }

  public void readOrThrow(byte[] paramArrayOfByte)
    throws IOException
  {
    readOrThrow(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public void readOrThrow(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (read(paramArrayOfByte, paramInt1, paramInt2) == paramInt2)
      return;
    throw new EOFException();
  }

  public short readShort()
    throws IOException
  {
    readOrThrow(this.mByteArray, 0, 2);
    this.mByteBuffer.rewind();
    return this.mByteBuffer.getShort();
  }

  public long readUnsignedInt()
    throws IOException
  {
    return 0xFFFFFFFF & readInt();
  }

  public int readUnsignedShort()
    throws IOException
  {
    return 0xFFFF & readShort();
  }

  public void setByteOrder(ByteOrder paramByteOrder)
  {
    this.mByteBuffer.order(paramByteOrder);
  }

  public long skip(long paramLong)
    throws IOException
  {
    long l = this.in.skip(paramLong);
    this.mCount = (int)(l + this.mCount);
    return l;
  }

  public void skipOrThrow(long paramLong)
    throws IOException
  {
    if (skip(paramLong) == paramLong)
      return;
    throw new EOFException();
  }

  public void skipTo(long paramLong)
    throws IOException
  {
    long l = paramLong - this.mCount;
    assert (l >= 0L);
    skipOrThrow(l);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.exif.CountedDataInputStream
 * JD-Core Version:    0.5.4
 */