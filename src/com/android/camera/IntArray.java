package com.android.camera;

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

  public int size()
  {
    return this.mSize;
  }

  public int[] toArray(int[] paramArrayOfInt)
  {
    if ((paramArrayOfInt == null) || (paramArrayOfInt.length < this.mSize))
      paramArrayOfInt = new int[this.mSize];
    System.arraycopy(this.mData, 0, paramArrayOfInt, 0, this.mSize);
    return paramArrayOfInt;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.IntArray
 * JD-Core Version:    0.5.4
 */