package com.google.android.picasastore;

import android.database.AbstractCursor;
import android.database.CursorIndexOutOfBoundsException;

public class PicasaMatrixCursor extends AbstractCursor
{
  private final int columnCount;
  private final String[] columnNames;
  private Object[] data;
  private int rowCount = 0;

  public PicasaMatrixCursor(String[] paramArrayOfString)
  {
    this(paramArrayOfString, 16);
  }

  public PicasaMatrixCursor(String[] paramArrayOfString, int paramInt)
  {
    this.columnNames = paramArrayOfString;
    this.columnCount = paramArrayOfString.length;
    if (paramInt < 1)
      paramInt = 1;
    this.data = new Object[paramInt * this.columnCount];
  }

  private void ensureCapacity(int paramInt)
  {
    if (paramInt <= this.data.length)
      return;
    Object[] arrayOfObject = this.data;
    int i = 2 * this.data.length;
    if (i < paramInt)
      i = paramInt;
    this.data = new Object[i];
    System.arraycopy(arrayOfObject, 0, this.data, 0, arrayOfObject.length);
  }

  private Object get(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.columnCount))
      throw new CursorIndexOutOfBoundsException("Requested column: " + paramInt + ", # of columns: " + this.columnCount);
    if (this.mPos < 0)
      throw new CursorIndexOutOfBoundsException("Before first row.");
    if (this.mPos >= this.rowCount)
      throw new CursorIndexOutOfBoundsException("After last row.");
    return this.data[(paramInt + this.mPos * this.columnCount)];
  }

  public void addRow(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject.length != this.columnCount)
      throw new IllegalArgumentException("columnNames.length = " + this.columnCount + ", columnValues.length = " + paramArrayOfObject.length);
    int i = this.rowCount;
    this.rowCount = (i + 1);
    int j = i * this.columnCount;
    ensureCapacity(j + this.columnCount);
    System.arraycopy(paramArrayOfObject, 0, this.data, j, this.columnCount);
  }

  public byte[] getBlob(int paramInt)
  {
    return (byte[])(byte[])get(paramInt);
  }

  public String[] getColumnNames()
  {
    return this.columnNames;
  }

  public int getCount()
  {
    return this.rowCount;
  }

  public double getDouble(int paramInt)
  {
    Object localObject = get(paramInt);
    if (localObject == null)
      return 0.0D;
    if (localObject instanceof Number)
      return ((Number)localObject).doubleValue();
    return Double.parseDouble(localObject.toString());
  }

  public float getFloat(int paramInt)
  {
    Object localObject = get(paramInt);
    if (localObject == null)
      return 0.0F;
    if (localObject instanceof Number)
      return ((Number)localObject).floatValue();
    return Float.parseFloat(localObject.toString());
  }

  public int getInt(int paramInt)
  {
    Object localObject = get(paramInt);
    if (localObject == null)
      return 0;
    if (localObject instanceof Number)
      return ((Number)localObject).intValue();
    return Integer.parseInt(localObject.toString());
  }

  public long getLong(int paramInt)
  {
    Object localObject = get(paramInt);
    if (localObject == null)
      return 0L;
    if (localObject instanceof Number)
      return ((Number)localObject).longValue();
    return Long.parseLong(localObject.toString());
  }

  public short getShort(int paramInt)
  {
    Object localObject = get(paramInt);
    if (localObject == null)
      return 0;
    if (localObject instanceof Number)
      return ((Number)localObject).shortValue();
    return Short.parseShort(localObject.toString());
  }

  public String getString(int paramInt)
  {
    Object localObject = get(paramInt);
    if (localObject == null)
      return null;
    return localObject.toString();
  }

  public int getType(int paramInt)
  {
    Object localObject = get(paramInt);
    if (localObject == null)
      return 0;
    if (localObject instanceof byte[])
      return 4;
    if ((localObject instanceof Float) || (localObject instanceof Double))
      return 2;
    if ((localObject instanceof Long) || (localObject instanceof Integer))
      return 1;
    return 3;
  }

  public boolean isNull(int paramInt)
  {
    return get(paramInt) == null;
  }

  public RowBuilder newRow()
  {
    this.rowCount = (1 + this.rowCount);
    int i = this.rowCount * this.columnCount;
    ensureCapacity(i);
    return new RowBuilder(i - this.columnCount, i);
  }

  public class RowBuilder
  {
    private final int endIndex;
    private int index;

    RowBuilder(int paramInt1, int arg3)
    {
      this.index = paramInt1;
      int i;
      this.endIndex = i;
    }

    public RowBuilder add(Object paramObject)
    {
      if (this.index == this.endIndex)
        throw new CursorIndexOutOfBoundsException("No more columns left.");
      Object[] arrayOfObject = PicasaMatrixCursor.this.data;
      int i = this.index;
      this.index = (i + 1);
      arrayOfObject[i] = paramObject;
      return this;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasastore.PicasaMatrixCursor
 * JD-Core Version:    0.5.4
 */