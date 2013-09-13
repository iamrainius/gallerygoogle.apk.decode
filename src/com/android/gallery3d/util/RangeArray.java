package com.android.gallery3d.util;

public class RangeArray<T>
{
  private T[] mData;
  private int mOffset;

  public RangeArray(int paramInt1, int paramInt2)
  {
    this.mData = ((Object[])new Object[1 + (paramInt2 - paramInt1)]);
    this.mOffset = paramInt1;
  }

  public T get(int paramInt)
  {
    return this.mData[(paramInt - this.mOffset)];
  }

  public void put(int paramInt, T paramT)
  {
    this.mData[(paramInt - this.mOffset)] = paramT;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.RangeArray
 * JD-Core Version:    0.5.4
 */