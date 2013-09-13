package com.android.gallery3d.common;

import J;

public class LongSparseArray<E>
  implements Cloneable
{
  private static final Object DELETED = new Object();
  private boolean mGarbage = false;
  private long[] mKeys;
  private int mSize;
  private Object[] mValues;

  public LongSparseArray()
  {
    this(10);
  }

  public LongSparseArray(int paramInt)
  {
    int i = idealLongArraySize(paramInt);
    this.mKeys = new long[i];
    this.mValues = new Object[i];
    this.mSize = 0;
  }

  private static int binarySearch(long[] paramArrayOfLong, int paramInt1, int paramInt2, long paramLong)
  {
    int i = paramInt1 + paramInt2;
    int j = paramInt1 - 1;
    while (i - j > 1)
    {
      int k = (i + j) / 2;
      if (paramArrayOfLong[k] < paramLong)
        j = k;
      i = k;
    }
    if (i == paramInt1 + paramInt2)
      i = 0xFFFFFFFF ^ paramInt1 + paramInt2;
    do
      return i;
    while (paramArrayOfLong[i] == paramLong);
    return i ^ 0xFFFFFFFF;
  }

  private void gc()
  {
    int i = this.mSize;
    int j = 0;
    long[] arrayOfLong = this.mKeys;
    Object[] arrayOfObject = this.mValues;
    for (int k = 0; k < i; ++k)
    {
      Object localObject = arrayOfObject[k];
      if (localObject == DELETED)
        continue;
      if (k != j)
      {
        arrayOfLong[j] = arrayOfLong[k];
        arrayOfObject[j] = localObject;
        arrayOfObject[k] = null;
      }
      ++j;
    }
    this.mGarbage = false;
    this.mSize = j;
  }

  private static int idealByteArraySize(int paramInt)
  {
    for (int i = 4; ; ++i)
    {
      if (i < 32)
      {
        if (paramInt > -12 + (1 << i))
          continue;
        paramInt = -12 + (1 << i);
      }
      return paramInt;
    }
  }

  public static int idealLongArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 8) / 8;
  }

  public void clear()
  {
    int i = this.mSize;
    Object[] arrayOfObject = this.mValues;
    for (int j = 0; j < i; ++j)
      arrayOfObject[j] = null;
    this.mSize = 0;
    this.mGarbage = false;
  }

  public LongSparseArray<E> clone()
  {
    LongSparseArray localLongSparseArray = null;
    try
    {
      localLongSparseArray = (LongSparseArray)super.clone();
      localLongSparseArray.mKeys = ((long[])this.mKeys.clone());
      localLongSparseArray.mValues = ((Object[])this.mValues.clone());
      return localLongSparseArray;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
    }
    return localLongSparseArray;
  }

  public E get(long paramLong)
  {
    return get(paramLong, null);
  }

  public E get(long paramLong, E paramE)
  {
    int i = binarySearch(this.mKeys, 0, this.mSize, paramLong);
    if ((i < 0) || (this.mValues[i] == DELETED))
      return paramE;
    return this.mValues[i];
  }

  public void put(long paramLong, E paramE)
  {
    int i = binarySearch(this.mKeys, 0, this.mSize, paramLong);
    if (i >= 0)
    {
      this.mValues[i] = paramE;
      return;
    }
    int j = i ^ 0xFFFFFFFF;
    if ((j < this.mSize) && (this.mValues[j] == DELETED))
    {
      this.mKeys[j] = paramLong;
      this.mValues[j] = paramE;
      return;
    }
    if ((this.mGarbage) && (this.mSize >= this.mKeys.length))
    {
      gc();
      j = 0xFFFFFFFF ^ binarySearch(this.mKeys, 0, this.mSize, paramLong);
    }
    if (this.mSize >= this.mKeys.length)
    {
      int k = idealLongArraySize(1 + this.mSize);
      long[] arrayOfLong = new long[k];
      Object[] arrayOfObject = new Object[k];
      System.arraycopy(this.mKeys, 0, arrayOfLong, 0, this.mKeys.length);
      System.arraycopy(this.mValues, 0, arrayOfObject, 0, this.mValues.length);
      this.mKeys = arrayOfLong;
      this.mValues = arrayOfObject;
    }
    if (this.mSize - j != 0)
    {
      System.arraycopy(this.mKeys, j, this.mKeys, j + 1, this.mSize - j);
      System.arraycopy(this.mValues, j, this.mValues, j + 1, this.mSize - j);
    }
    this.mKeys[j] = paramLong;
    this.mValues[j] = paramE;
    this.mSize = (1 + this.mSize);
  }

  public void removeAt(int paramInt)
  {
    if (this.mValues[paramInt] == DELETED)
      return;
    this.mValues[paramInt] = DELETED;
    this.mGarbage = true;
  }

  public int size()
  {
    if (this.mGarbage)
      gc();
    return this.mSize;
  }

  public E valueAt(int paramInt)
  {
    if (this.mGarbage)
      gc();
    return this.mValues[paramInt];
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.common.LongSparseArray
 * JD-Core Version:    0.5.4
 */