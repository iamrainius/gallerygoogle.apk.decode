package com.android.gallery3d.util;

import com.android.gallery3d.common.Utils;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;

public class InterruptableOutputStream extends OutputStream
{
  private volatile boolean mIsInterrupted = false;
  private OutputStream mOutputStream;

  public InterruptableOutputStream(OutputStream paramOutputStream)
  {
    this.mOutputStream = ((OutputStream)Utils.checkNotNull(paramOutputStream));
  }

  public void close()
    throws IOException
  {
    this.mOutputStream.close();
  }

  public void flush()
    throws IOException
  {
    if (this.mIsInterrupted)
      throw new InterruptedIOException();
    this.mOutputStream.flush();
  }

  public void interrupt()
  {
    this.mIsInterrupted = true;
  }

  public void write(int paramInt)
    throws IOException
  {
    if (this.mIsInterrupted)
      throw new InterruptedIOException();
    this.mOutputStream.write(paramInt);
  }

  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = paramInt1 + paramInt2;
    while (paramInt1 < i)
    {
      if (this.mIsInterrupted)
        throw new InterruptedIOException();
      int j = Math.min(4096, i - paramInt1);
      this.mOutputStream.write(paramArrayOfByte, paramInt1, j);
      paramInt1 += j;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.InterruptableOutputStream
 * JD-Core Version:    0.5.4
 */