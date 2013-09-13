package com.android.gallery3d.exif;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

public class ExifData
{
  private final ByteOrder mByteOrder;
  private final IfdData[] mIfdDatas = new IfdData[5];
  private ArrayList<byte[]> mStripBytes = new ArrayList();
  private byte[] mThumbnail;

  public ExifData(ByteOrder paramByteOrder)
  {
    this.mByteOrder = paramByteOrder;
  }

  private IfdData getOrCreateIfdData(int paramInt)
  {
    IfdData localIfdData = this.mIfdDatas[paramInt];
    if (localIfdData == null)
    {
      localIfdData = new IfdData(paramInt);
      this.mIfdDatas[paramInt] = localIfdData;
    }
    return localIfdData;
  }

  private static Rational[] toExifLatLong(double paramDouble)
  {
    double d1 = Math.abs(paramDouble);
    int i = (int)d1;
    double d2 = 60.0D * (d1 - i);
    int j = (int)d2;
    int k = (int)(6000.0D * (d2 - j));
    Rational[] arrayOfRational = new Rational[3];
    arrayOfRational[0] = new Rational(i, 1L);
    arrayOfRational[1] = new Rational(j, 1L);
    arrayOfRational[2] = new Rational(k, 100L);
    return arrayOfRational;
  }

  public void addGpsTags(double paramDouble1, double paramDouble2)
  {
    IfdData localIfdData = getIfdData(4);
    if (localIfdData == null)
    {
      localIfdData = new IfdData(4);
      addIfdData(localIfdData);
    }
    ExifTag localExifTag1 = new ExifTag(2, 10, 3, 4);
    ExifTag localExifTag2 = new ExifTag(4, 10, 3, 4);
    ExifTag localExifTag3 = new ExifTag(1, 2, 2, 4);
    ExifTag localExifTag4 = new ExifTag(3, 2, 2, 4);
    localExifTag1.setValue(toExifLatLong(paramDouble1));
    localExifTag2.setValue(toExifLatLong(paramDouble2));
    String str1;
    if (paramDouble1 >= 0.0D)
    {
      str1 = "N";
      label110: localExifTag3.setValue(str1);
      if (paramDouble2 < 0.0D)
        break label170;
    }
    for (String str2 = "E"; ; str2 = "W")
    {
      localExifTag4.setValue(str2);
      localIfdData.setTag(localExifTag1);
      localIfdData.setTag(localExifTag2);
      localIfdData.setTag(localExifTag3);
      localIfdData.setTag(localExifTag4);
      return;
      str1 = "S";
      label170: break label110:
    }
  }

  void addIfdData(IfdData paramIfdData)
  {
    this.mIfdDatas[paramIfdData.getId()] = paramIfdData;
  }

  public ExifTag addTag(short paramShort)
  {
    IfdData localIfdData = getOrCreateIfdData(ExifTag.getIfdIdFromTagId(paramShort));
    ExifTag localExifTag = ExifTag.buildTag(paramShort);
    localIfdData.setTag(localExifTag);
    return localExifTag;
  }

  public boolean equals(Object paramObject)
  {
    if (paramObject instanceof ExifData)
    {
      ExifData localExifData = (ExifData)paramObject;
      if ((localExifData.mByteOrder != this.mByteOrder) || (!Arrays.equals(localExifData.mThumbnail, this.mThumbnail)) || (localExifData.mStripBytes.size() != this.mStripBytes.size()))
        return false;
      for (int i = 0; i < this.mStripBytes.size(); ++i)
        if (!Arrays.equals((byte[])localExifData.mStripBytes.get(i), (byte[])this.mStripBytes.get(i)))
          return false;
      for (int j = 0; j < 5; ++j)
        if (!Util.equals(localExifData.getIfdData(j), getIfdData(j)))
          return false;
      return true;
    }
    return false;
  }

  public ByteOrder getByteOrder()
  {
    return this.mByteOrder;
  }

  public byte[] getCompressedThumbnail()
  {
    return this.mThumbnail;
  }

  IfdData getIfdData(int paramInt)
  {
    return this.mIfdDatas[paramInt];
  }

  public byte[] getStrip(int paramInt)
  {
    return (byte[])this.mStripBytes.get(paramInt);
  }

  public int getStripCount()
  {
    return this.mStripBytes.size();
  }

  public boolean hasCompressedThumbnail()
  {
    return this.mThumbnail != null;
  }

  public boolean hasUncompressedStrip()
  {
    return this.mStripBytes.size() != 0;
  }

  public void removeThumbnailData()
  {
    this.mThumbnail = null;
    this.mStripBytes.clear();
    this.mIfdDatas[1] = null;
  }

  public void setCompressedThumbnail(byte[] paramArrayOfByte)
  {
    this.mThumbnail = paramArrayOfByte;
  }

  public void setStripBytes(int paramInt, byte[] paramArrayOfByte)
  {
    if (paramInt < this.mStripBytes.size())
    {
      this.mStripBytes.set(paramInt, paramArrayOfByte);
      return;
    }
    for (int i = this.mStripBytes.size(); i < paramInt; ++i)
      this.mStripBytes.add(null);
    this.mStripBytes.add(paramArrayOfByte);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.exif.ExifData
 * JD-Core Version:    0.5.4
 */