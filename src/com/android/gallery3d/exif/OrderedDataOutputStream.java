package com.android.gallery3d.exif;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class OrderedDataOutputStream extends FilterOutputStream
{
  private final ByteBuffer mByteBuffer = ByteBuffer.allocate(4);

  public OrderedDataOutputStream(OutputStream paramOutputStream)
  {
    super(paramOutputStream);
  }

  public void setByteOrder(ByteOrder paramByteOrder)
  {
    this.mByteBuffer.order(paramByteOrder);
  }

  public void writeInt(int paramInt)
    throws IOException
  {
    this.mByteBuffer.rewind();
    this.mByteBuffer.putInt(paramInt);
    this.out.write(this.mByteBuffer.array());
  }

  public void writeRational(Rational paramRational)
    throws IOException
  {
    writeInt((int)paramRational.getNominator());
    writeInt((int)paramRational.getDenominator());
  }

  public void writeShort(short paramShort)
    throws IOException
  {
    this.mByteBuffer.rewind();
    this.mByteBuffer.putShort(paramShort);
    this.out.write(this.mByteBuffer.array(), 0, 2);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.exif.OrderedDataOutputStream
 * JD-Core Version:    0.5.4
 */