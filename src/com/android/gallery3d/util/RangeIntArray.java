package com.android.gallery3d.util;

public class RangeIntArray
{
  private int[] mData;
  private int mOffset;

  public RangeIntArray(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    this.mData = paramArrayOfInt;
    this.mOffset = paramInt1;
  }

  public int get(int paramInt)
  {
    return this.mData[(paramInt - this.mOffset)];
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.RangeIntArray
 * JD-Core Version:    0.5.4
 */