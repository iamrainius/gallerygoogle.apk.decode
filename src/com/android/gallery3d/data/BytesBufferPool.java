package com.android.gallery3d.data;

import com.android.gallery3d.util.ThreadPool.JobContext;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BytesBufferPool
{
  private final int mBufferSize;
  private final ArrayList<BytesBuffer> mList;
  private final int mPoolSize;

  public BytesBufferPool(int paramInt1, int paramInt2)
  {
    this.mList = new ArrayList(paramInt1);
    this.mPoolSize = paramInt1;
    this.mBufferSize = paramInt2;
  }

  public void clear()
  {
    monitorenter;
    try
    {
      this.mList.clear();
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public BytesBuffer get()
  {
    monitorenter;
    BytesBuffer localBytesBuffer;
    try
    {
      int i = this.mList.size();
      if (i > 0)
      {
        localBytesBuffer = (BytesBuffer)this.mList.remove(i - 1);
        return localBytesBuffer;
      }
    }
    finally
    {
      monitorexit;
    }
  }

  public void recycle(BytesBuffer paramBytesBuffer)
  {
    monitorenter;
    try
    {
      int i = paramBytesBuffer.data.length;
      int j = this.mBufferSize;
      if (i != j);
      do
        return;
      while (this.mList.size() >= this.mPoolSize);
      paramBytesBuffer.offset = 0;
      paramBytesBuffer.length = 0;
    }
    finally
    {
      monitorexit;
    }
  }

  public static class BytesBuffer
  {
    public byte[] data;
    public int length;
    public int offset;

    private BytesBuffer(int paramInt)
    {
      this.data = new byte[paramInt];
    }

    public void readFrom(ThreadPool.JobContext paramJobContext, FileDescriptor paramFileDescriptor)
      throws IOException
    {
      FileInputStream localFileInputStream = new FileInputStream(paramFileDescriptor);
      this.length = 0;
      int i;
      try
      {
        i = this.data.length;
        do
        {
          int j = Math.min(4096, i - this.length);
          int k = localFileInputStream.read(this.data, this.length, j);
          if (k >= 0)
          {
            boolean bool = paramJobContext.isCancelled();
            if (!bool)
              break label75;
          }
          return;
          label75: this.length = (k + this.length);
        }
        while (this.length != i);
        byte[] arrayOfByte = new byte[2 * this.data.length];
        System.arraycopy(this.data, 0, arrayOfByte, 0, this.data.length);
        this.data = arrayOfByte;
      }
      finally
      {
        localFileInputStream.close();
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.BytesBufferPool
 * JD-Core Version:    0.5.4
 */