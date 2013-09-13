package com.android.gallery3d.util;

public class IntArray
{
  private int[] mData = new int[8];
  private int mSize = 0;

  public void add(int paramInt)
  {
    if (this.mData.length == this.mSize)
    {
      int[] arrayOfInt2 = new int[this.mSize + this.mSize];
      System.arraycopy(this.mData, 0, arrayOfInt2, 0, this.mSize);
      this.mData = arrayOfInt2;
    }
    int[] arrayOfInt1 = this.mData;
    int i = this.mSize;
    this.mSize = (i + 1);
    arrayOfInt1[i] = paramInt;
  }

  public void clear()
  {
    this.mSize = 0;
    if (this.mData.length == 8)
      return;
    this.mData = new int[8];
  }

  public int[] getInternalArray()
  {
    return this.mData;
  }

  public int size()
  {
    return this.mSize;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.IntArray
 * JD-Core Version:    0.5.4
 */