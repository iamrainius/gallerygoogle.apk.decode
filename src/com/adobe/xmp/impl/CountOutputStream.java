package com.adobe.xmp.impl;

import java.io.IOException;
import java.io.OutputStream;

public final class CountOutputStream extends OutputStream
{
  private int bytesWritten = 0;
  private final OutputStream out;

  CountOutputStream(OutputStream paramOutputStream)
  {
    this.out = paramOutputStream;
  }

  public int getBytesWritten()
  {
    return this.bytesWritten;
  }

  public void write(int paramInt)
    throws IOException
  {
    this.out.write(paramInt);
    this.bytesWritten = (1 + this.bytesWritten);
  }

  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    this.out.write(paramArrayOfByte);
    this.bytesWritten += paramArrayOfByte.length;
  }

  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    this.out.write(paramArrayOfByte, paramInt1, paramInt2);
    this.bytesWritten = (paramInt2 + this.bytesWritten);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.CountOutputStream
 * JD-Core Version:    0.5.4
 */