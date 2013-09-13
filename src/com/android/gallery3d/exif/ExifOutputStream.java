package com.android.gallery3d.exif;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ExifOutputStream extends FilterOutputStream
{
  private ByteBuffer mBuffer = ByteBuffer.allocate(4);
  private int mByteToCopy;
  private int mByteToSkip;
  private ExifData mExifData;
  private int mState = 0;

  static
  {
    if (!ExifOutputStream.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public ExifOutputStream(OutputStream paramOutputStream)
  {
    super(paramOutputStream);
  }

  private int calculateAllOffset()
  {
    IfdData localIfdData1 = this.mExifData.getIfdData(0);
    int i = calculateOffsetOfIfd(localIfdData1, 8);
    localIfdData1.getTag(-30871).setValue(i);
    IfdData localIfdData2 = this.mExifData.getIfdData(2);
    int j = calculateOffsetOfIfd(localIfdData2, i);
    IfdData localIfdData3 = this.mExifData.getIfdData(3);
    if (localIfdData3 != null)
    {
      localIfdData2.getTag(-24571).setValue(j);
      j = calculateOffsetOfIfd(localIfdData3, j);
    }
    IfdData localIfdData4 = this.mExifData.getIfdData(4);
    if (localIfdData4 != null)
    {
      localIfdData1.getTag(-30683).setValue(j);
      j = calculateOffsetOfIfd(localIfdData4, j);
    }
    IfdData localIfdData5 = this.mExifData.getIfdData(1);
    if (localIfdData5 != null)
    {
      localIfdData1.setOffsetToNextIfd(j);
      j = calculateOffsetOfIfd(localIfdData5, j);
    }
    if (this.mExifData.hasCompressedThumbnail())
    {
      localIfdData5.getTag(513).setValue(j);
      j += this.mExifData.getCompressedThumbnail().length;
    }
    do
      return j;
    while (!this.mExifData.hasUncompressedStrip());
    long[] arrayOfLong = new long[this.mExifData.getStripCount()];
    for (int k = 0; k < this.mExifData.getStripCount(); ++k)
    {
      arrayOfLong[k] = j;
      j += this.mExifData.getStrip(k).length;
    }
    localIfdData5.getTag(273).setValue(arrayOfLong);
    return j;
  }

  private int calculateOffsetOfIfd(IfdData paramIfdData, int paramInt)
  {
    int i = paramInt + (4 + (2 + 12 * paramIfdData.getTagCount()));
    for (ExifTag localExifTag : paramIfdData.getAllTags())
    {
      if (localExifTag.getDataSize() <= 4)
        continue;
      localExifTag.setOffset(i);
      i += localExifTag.getDataSize();
    }
    return i;
  }

  private void createRequiredIfdAndTag()
  {
    IfdData localIfdData1 = this.mExifData.getIfdData(0);
    if (localIfdData1 == null)
    {
      localIfdData1 = new IfdData(0);
      this.mExifData.addIfdData(localIfdData1);
    }
    localIfdData1.setTag(new ExifTag(-30871, 4, 1, 0));
    IfdData localIfdData2 = this.mExifData.getIfdData(2);
    if (localIfdData2 == null)
    {
      localIfdData2 = new IfdData(2);
      this.mExifData.addIfdData(localIfdData2);
    }
    if (this.mExifData.getIfdData(4) != null)
      localIfdData1.setTag(new ExifTag(-30683, 4, 1, 0));
    if (this.mExifData.getIfdData(3) != null)
      localIfdData2.setTag(new ExifTag(-24571, 4, 1, 2));
    IfdData localIfdData3 = this.mExifData.getIfdData(1);
    if (this.mExifData.hasCompressedThumbnail())
    {
      if (localIfdData3 == null)
      {
        localIfdData3 = new IfdData(1);
        this.mExifData.addIfdData(localIfdData3);
      }
      localIfdData3.setTag(new ExifTag(513, 4, 1, 1));
      ExifTag localExifTag3 = new ExifTag(514, 4, 1, 1);
      localExifTag3.setValue(this.mExifData.getCompressedThumbnail().length);
      localIfdData3.setTag(localExifTag3);
    }
    do
      return;
    while (!this.mExifData.hasUncompressedStrip());
    if (localIfdData3 == null)
    {
      localIfdData3 = new IfdData(1);
      this.mExifData.addIfdData(localIfdData3);
    }
    int i = this.mExifData.getStripCount();
    ExifTag localExifTag1 = new ExifTag(273, 4, i, 1);
    ExifTag localExifTag2 = new ExifTag(279, 4, i, 1);
    long[] arrayOfLong = new long[i];
    for (int j = 0; j < this.mExifData.getStripCount(); ++j)
      arrayOfLong[j] = this.mExifData.getStrip(j).length;
    localExifTag2.setValue(arrayOfLong);
    localIfdData3.setTag(localExifTag1);
    localIfdData3.setTag(localExifTag2);
  }

  private int requestByteToBuffer(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    int i = paramInt1 - this.mBuffer.position();
    if (paramInt3 > i);
    for (int j = i; ; j = paramInt3)
    {
      this.mBuffer.put(paramArrayOfByte, paramInt2, j);
      return j;
    }
  }

  private void writeAllTags(OrderedDataOutputStream paramOrderedDataOutputStream)
    throws IOException
  {
    writeIfd(this.mExifData.getIfdData(0), paramOrderedDataOutputStream);
    writeIfd(this.mExifData.getIfdData(2), paramOrderedDataOutputStream);
    IfdData localIfdData1 = this.mExifData.getIfdData(3);
    if (localIfdData1 != null)
      writeIfd(localIfdData1, paramOrderedDataOutputStream);
    IfdData localIfdData2 = this.mExifData.getIfdData(4);
    if (localIfdData2 != null)
      writeIfd(localIfdData2, paramOrderedDataOutputStream);
    if (this.mExifData.getIfdData(1) == null)
      return;
    writeIfd(this.mExifData.getIfdData(1), paramOrderedDataOutputStream);
  }

  private void writeExifData()
    throws IOException
  {
    createRequiredIfdAndTag();
    int i = calculateAllOffset();
    OrderedDataOutputStream localOrderedDataOutputStream = new OrderedDataOutputStream(this.out);
    localOrderedDataOutputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
    localOrderedDataOutputStream.writeShort(-31);
    localOrderedDataOutputStream.writeShort((short)(i + 8));
    localOrderedDataOutputStream.writeInt(1165519206);
    localOrderedDataOutputStream.writeShort(0);
    if (this.mExifData.getByteOrder() == ByteOrder.BIG_ENDIAN)
      localOrderedDataOutputStream.writeShort(19789);
    while (true)
    {
      localOrderedDataOutputStream.setByteOrder(this.mExifData.getByteOrder());
      localOrderedDataOutputStream.writeShort(42);
      localOrderedDataOutputStream.writeInt(8);
      writeAllTags(localOrderedDataOutputStream);
      writeThumbnail(localOrderedDataOutputStream);
      return;
      localOrderedDataOutputStream.writeShort(18761);
    }
  }

  private void writeIfd(IfdData paramIfdData, OrderedDataOutputStream paramOrderedDataOutputStream)
    throws IOException
  {
    ExifTag[] arrayOfExifTag = paramIfdData.getAllTags();
    paramOrderedDataOutputStream.writeShort((short)arrayOfExifTag.length);
    int i = arrayOfExifTag.length;
    ExifTag localExifTag2;
    for (int j = 0; ; ++j)
    {
      if (j >= i)
        break label120;
      localExifTag2 = arrayOfExifTag[j];
      paramOrderedDataOutputStream.writeShort(localExifTag2.getTagId());
      paramOrderedDataOutputStream.writeShort(localExifTag2.getDataType());
      paramOrderedDataOutputStream.writeInt(localExifTag2.getComponentCount());
      if (localExifTag2.getDataSize() <= 4)
        break;
      paramOrderedDataOutputStream.writeInt(localExifTag2.getOffset());
    }
    writeTagValue(localExifTag2, paramOrderedDataOutputStream);
    int i1 = 0;
    int i2 = 4 - localExifTag2.getDataSize();
    while (true)
    {
      if (i1 < i2);
      paramOrderedDataOutputStream.write(0);
      ++i1;
    }
    label120: paramOrderedDataOutputStream.writeInt(paramIfdData.getOffsetToNextIfd());
    int k = arrayOfExifTag.length;
    for (int l = 0; l < k; ++l)
    {
      ExifTag localExifTag1 = arrayOfExifTag[l];
      if (localExifTag1.getDataSize() <= 4)
        continue;
      writeTagValue(localExifTag1, paramOrderedDataOutputStream);
    }
  }

  private void writeTagValue(ExifTag paramExifTag, OrderedDataOutputStream paramOrderedDataOutputStream)
    throws IOException
  {
    switch (paramExifTag.getDataType())
    {
    case 6:
    case 8:
    default:
      return;
    case 2:
      paramOrderedDataOutputStream.write(paramExifTag.getString().getBytes());
      int i5 = paramExifTag.getComponentCount() - paramExifTag.getString().length();
      for (int i6 = 0; ; ++i6)
      {
        if (i6 < i5);
        paramOrderedDataOutputStream.write(0);
      }
    case 9:
      int i3 = 0;
      int i4 = paramExifTag.getComponentCount();
      while (true)
      {
        if (i3 < i4);
        paramOrderedDataOutputStream.writeInt(paramExifTag.getLong(i3));
        ++i3;
      }
    case 5:
    case 10:
      int i1 = 0;
      int i2 = paramExifTag.getComponentCount();
      while (true)
      {
        if (i1 < i2);
        paramOrderedDataOutputStream.writeRational(paramExifTag.getRational(i1));
        ++i1;
      }
    case 1:
    case 7:
      byte[] arrayOfByte = new byte[paramExifTag.getComponentCount()];
      paramExifTag.getBytes(arrayOfByte);
      paramOrderedDataOutputStream.write(arrayOfByte);
      return;
    case 4:
      int k = 0;
      int l = paramExifTag.getComponentCount();
      while (true)
      {
        if (k < l);
        paramOrderedDataOutputStream.writeInt((int)paramExifTag.getUnsignedLong(k));
        ++k;
      }
    case 3:
    }
    int i = 0;
    int j = paramExifTag.getComponentCount();
    while (true)
    {
      if (i < j);
      paramOrderedDataOutputStream.writeShort((short)paramExifTag.getUnsignedShort(i));
      ++i;
    }
  }

  private void writeThumbnail(OrderedDataOutputStream paramOrderedDataOutputStream)
    throws IOException
  {
    if (this.mExifData.hasCompressedThumbnail())
      paramOrderedDataOutputStream.write(this.mExifData.getCompressedThumbnail());
    do
      return;
    while (!this.mExifData.hasUncompressedStrip());
    for (int i = 0; ; ++i)
    {
      if (i < this.mExifData.getStripCount());
      paramOrderedDataOutputStream.write(this.mExifData.getStrip(i));
    }
  }

  public void setExifData(ExifData paramExifData)
  {
    this.mExifData = paramExifData;
  }

  public void write(int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[1];
    arrayOfByte[0] = (byte)(paramInt & 0xFF);
    write(arrayOfByte);
  }

  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    label0: int l;
    label47: int k;
    if ((((this.mByteToSkip > 0) || (this.mByteToCopy > 0) || (this.mState != 2))) && (paramInt2 > 0))
    {
      if (this.mByteToSkip > 0)
      {
        if (paramInt2 <= this.mByteToSkip)
          break label126;
        l = this.mByteToSkip;
        paramInt2 -= l;
        this.mByteToSkip -= l;
        paramInt1 += l;
      }
      if (this.mByteToCopy > 0)
      {
        if (paramInt2 <= this.mByteToCopy)
          break label132;
        k = this.mByteToCopy;
        label89: this.out.write(paramArrayOfByte, paramInt1, k);
        paramInt2 -= k;
        this.mByteToCopy -= k;
        paramInt1 += k;
      }
      if (paramInt2 != 0);
    }
    do
    {
      do
      {
        do
        {
          return;
          label126: l = paramInt2;
          break label47:
          label132: k = paramInt2;
          break label89:
          switch (this.mState)
          {
          default:
            break;
          case 0:
            int j = requestByteToBuffer(2, paramArrayOfByte, paramInt1, paramInt2);
            paramInt1 += j;
            paramInt2 -= j;
          case 1:
          }
        }
        while (this.mBuffer.position() < 2);
        this.mBuffer.rewind();
        assert (this.mBuffer.getShort() == -40);
        this.out.write(this.mBuffer.array(), 0, 2);
        this.mState = 1;
        this.mBuffer.rewind();
        writeExifData();
        break label0:
        int i = requestByteToBuffer(4, paramArrayOfByte, paramInt1, paramInt2);
        paramInt1 += i;
        paramInt2 -= i;
        if ((this.mBuffer.position() != 2) || (this.mBuffer.getShort() != -39))
          continue;
        this.out.write(this.mBuffer.array(), 0, 2);
        this.mBuffer.rewind();
      }
      while (this.mBuffer.position() < 4);
      this.mBuffer.rewind();
      short s = this.mBuffer.getShort();
      if (s == -31)
        this.mByteToSkip = (-2 + (0xFF & this.mBuffer.getShort()));
      for (this.mState = 2; ; this.mState = 2)
      {
        while (true)
        {
          this.mBuffer.rewind();
          break label0:
          if (JpegHeader.isSofMarker(s))
            break;
          this.out.write(this.mBuffer.array(), 0, 4);
          this.mByteToCopy = (-2 + (0xFF & this.mBuffer.getShort()));
        }
        this.out.write(this.mBuffer.array(), 0, 4);
      }
    }
    while (paramInt2 <= 0);
    this.out.write(paramArrayOfByte, paramInt1, paramInt2);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.exif.ExifOutputStream
 * JD-Core Version:    0.5.4
 */