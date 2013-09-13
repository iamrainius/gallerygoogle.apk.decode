package com.android.gallery3d.exif;

import android.util.SparseArray;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class ExifTag
{
  private static final SimpleDateFormat TIME_FORMAT;
  private static final int[] TYPE_TO_SIZE_MAP = new int[11];
  private static volatile SparseArray<Integer> sInteroperTagInfo;
  private static volatile SparseArray<Integer> sTagInfo;
  private int mComponentCount;
  private final boolean mComponentCountDefined;
  private final short mDataType;
  private final int mIfd;
  private int mOffset;
  private final short mTagId;
  private Object mValue;

  static
  {
    TYPE_TO_SIZE_MAP[1] = 1;
    TYPE_TO_SIZE_MAP[2] = 1;
    TYPE_TO_SIZE_MAP[3] = 2;
    TYPE_TO_SIZE_MAP[4] = 4;
    TYPE_TO_SIZE_MAP[5] = 8;
    TYPE_TO_SIZE_MAP[7] = 1;
    TYPE_TO_SIZE_MAP[9] = 4;
    TYPE_TO_SIZE_MAP[10] = 8;
    sTagInfo = null;
    sInteroperTagInfo = null;
    TIME_FORMAT = new SimpleDateFormat("yyyy:MM:dd kk:mm:ss");
  }

  ExifTag(short paramShort1, short paramShort2, int paramInt1, int paramInt2)
  {
    this.mTagId = paramShort1;
    this.mDataType = paramShort2;
    this.mComponentCount = paramInt1;
    this.mComponentCountDefined = getComponentCountDefined(paramShort1, paramInt2);
    this.mIfd = paramInt2;
  }

  public static ExifTag buildTag(short paramShort)
  {
    Integer localInteger = (Integer)getTagInfo().get(paramShort);
    if (localInteger == null)
      throw new IllegalArgumentException("Unknown Tag ID: " + paramShort);
    return new ExifTag(paramShort, getTypeFromInfo(localInteger.intValue()), getComponentCountFromInfo(localInteger.intValue()), getIfdIdFromInfo(localInteger.intValue()));
  }

  private void checkComponentCountOrThrow(int paramInt)
    throws IllegalArgumentException
  {
    if ((!this.mComponentCountDefined) || (this.mComponentCount == paramInt))
      return;
    throw new IllegalArgumentException("Tag " + this.mTagId + ": Required " + this.mComponentCount + " components but was given " + paramInt + " component(s)");
  }

  private void checkOverflowForRational(Rational[] paramArrayOfRational)
  {
    int i = paramArrayOfRational.length;
    for (int j = 0; j < i; ++j)
    {
      Rational localRational = paramArrayOfRational[j];
      if ((localRational.getNominator() >= -2147483648L) && (localRational.getDenominator() >= -2147483648L) && (localRational.getNominator() <= 2147483647L) && (localRational.getDenominator() <= 2147483647L))
        continue;
      throw new IllegalArgumentException("Tag " + this.mTagId + ": Value" + localRational + " is illegal for type RATIONAL");
    }
  }

  private void checkOverflowForUnsignedLong(int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt.length;
    for (int j = 0; j < i; ++j)
    {
      int k = paramArrayOfInt[j];
      if (k >= 0)
        continue;
      throw new IllegalArgumentException("Tag " + this.mTagId + ": Value" + k + " is illegal for type UNSIGNED_LONG");
    }
  }

  private void checkOverflowForUnsignedLong(long[] paramArrayOfLong)
  {
    int i = paramArrayOfLong.length;
    for (int j = 0; j < i; ++j)
    {
      long l = paramArrayOfLong[j];
      if ((l >= 0L) && (l <= 4294967295L))
        continue;
      throw new IllegalArgumentException("Tag " + this.mTagId + ": Value" + l + " is illegal for type UNSIGNED_LONG");
    }
  }

  private void checkOverflowForUnsignedRational(Rational[] paramArrayOfRational)
  {
    int i = paramArrayOfRational.length;
    for (int j = 0; j < i; ++j)
    {
      Rational localRational = paramArrayOfRational[j];
      if ((localRational.getNominator() >= 0L) && (localRational.getDenominator() >= 0L) && (localRational.getNominator() <= 4294967295L) && (localRational.getDenominator() <= 4294967295L))
        continue;
      throw new IllegalArgumentException("Tag " + this.mTagId + ": Value" + localRational + " is illegal for type UNSIGNED_RATIONAL");
    }
  }

  private void checkOverflowForUnsignedShort(int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt.length;
    for (int j = 0; j < i; ++j)
    {
      int k = paramArrayOfInt[j];
      if ((k <= 65535) && (k >= 0))
        continue;
      throw new IllegalArgumentException("Tag " + this.mTagId + ": Value" + k + " is illegal for type UNSIGNED_SHORT");
    }
  }

  private static String convertTypeToString(short paramShort)
  {
    switch (paramShort)
    {
    case 6:
    case 8:
    default:
      return "";
    case 1:
      return "UNSIGNED_BYTE";
    case 2:
      return "ASCII";
    case 3:
      return "UNSIGNED_SHORT";
    case 4:
      return "UNSIGNED_LONG";
    case 5:
      return "UNSIGNED_RATIONAL";
    case 7:
      return "UNDEFINED";
    case 9:
      return "LONG";
    case 10:
    }
    return "RATIONAL";
  }

  private static boolean getComponentCountDefined(short paramShort, int paramInt)
  {
    if (paramInt == 3);
    for (Integer localInteger = (Integer)getInteroperTagInfo().get(paramShort); localInteger == null; localInteger = (Integer)getTagInfo().get(paramShort))
      return false;
    if (getComponentCountFromInfo(localInteger.intValue()) != 0);
    for (int i = 1; ; i = 0)
      return i;
  }

  private static int getComponentCountFromInfo(int paramInt)
  {
    return 0xFFFF & paramInt;
  }

  public static int getElementSize(short paramShort)
  {
    return TYPE_TO_SIZE_MAP[paramShort];
  }

  private static int getIfdIdFromInfo(int paramInt)
  {
    return 0xFF & paramInt >> 24;
  }

  static int getIfdIdFromTagId(short paramShort)
  {
    Integer localInteger = (Integer)getTagInfo().get(paramShort);
    if (localInteger == null)
      throw new IllegalArgumentException("Unknown Tag ID: " + paramShort);
    return getIfdIdFromInfo(localInteger.intValue());
  }

  private static SparseArray<Integer> getInteroperTagInfo()
  {
    if (sInteroperTagInfo == null)
      monitorenter;
    try
    {
      if (sInteroperTagInfo == null)
      {
        sInteroperTagInfo = new SparseArray();
        sInteroperTagInfo.put(1, Integer.valueOf(50462720));
      }
      return sInteroperTagInfo;
    }
    finally
    {
      monitorexit;
    }
  }

  private static SparseArray<Integer> getTagInfo()
  {
    if (sTagInfo == null)
      monitorenter;
    try
    {
      if (sTagInfo == null)
      {
        sTagInfo = new SparseArray();
        initTagInfo();
      }
      return sTagInfo;
    }
    finally
    {
      monitorexit;
    }
  }

  private static short getTypeFromInfo(int paramInt)
  {
    return (short)(0xFF & paramInt >> 16);
  }

  private static void initTagInfo()
  {
    sTagInfo.put(271, Integer.valueOf(131072));
    sTagInfo.put(256, Integer.valueOf(262145));
    sTagInfo.put(257, Integer.valueOf(262145));
    sTagInfo.put(258, Integer.valueOf(196611));
    sTagInfo.put(259, Integer.valueOf(196609));
    sTagInfo.put(262, Integer.valueOf(196609));
    sTagInfo.put(274, Integer.valueOf(196609));
    sTagInfo.put(277, Integer.valueOf(196609));
    sTagInfo.put(284, Integer.valueOf(196609));
    sTagInfo.put(530, Integer.valueOf(196610));
    sTagInfo.put(531, Integer.valueOf(196609));
    sTagInfo.put(282, Integer.valueOf(327681));
    sTagInfo.put(283, Integer.valueOf(327681));
    sTagInfo.put(296, Integer.valueOf(196609));
    sTagInfo.put(273, Integer.valueOf(262144));
    sTagInfo.put(278, Integer.valueOf(262145));
    sTagInfo.put(279, Integer.valueOf(262144));
    sTagInfo.put(513, Integer.valueOf(262145));
    sTagInfo.put(514, Integer.valueOf(262145));
    sTagInfo.put(301, Integer.valueOf(197376));
    sTagInfo.put(318, Integer.valueOf(327682));
    sTagInfo.put(319, Integer.valueOf(327686));
    sTagInfo.put(529, Integer.valueOf(327683));
    sTagInfo.put(532, Integer.valueOf(327686));
    sTagInfo.put(306, Integer.valueOf(131092));
    sTagInfo.put(270, Integer.valueOf(131072));
    sTagInfo.put(271, Integer.valueOf(131072));
    sTagInfo.put(272, Integer.valueOf(131072));
    sTagInfo.put(305, Integer.valueOf(131072));
    sTagInfo.put(315, Integer.valueOf(131072));
    sTagInfo.put(-32104, Integer.valueOf(131072));
    sTagInfo.put(-30871, Integer.valueOf(262145));
    sTagInfo.put(-30683, Integer.valueOf(262145));
    sTagInfo.put(-28672, Integer.valueOf(34013188));
    sTagInfo.put(-24576, Integer.valueOf(34013188));
    sTagInfo.put(-24575, Integer.valueOf(33751041));
    sTagInfo.put(-28415, Integer.valueOf(34013188));
    sTagInfo.put(-28414, Integer.valueOf(33882113));
    sTagInfo.put(-24574, Integer.valueOf(33816577));
    sTagInfo.put(-24573, Integer.valueOf(33816577));
    sTagInfo.put(-28036, Integer.valueOf(34013184));
    sTagInfo.put(-28026, Integer.valueOf(34013184));
    sTagInfo.put(-24572, Integer.valueOf(33685517));
    sTagInfo.put(-28669, Integer.valueOf(33685524));
    sTagInfo.put(-28668, Integer.valueOf(33685524));
    sTagInfo.put(-28016, Integer.valueOf(33685504));
    sTagInfo.put(-28015, Integer.valueOf(33685504));
    sTagInfo.put(-28014, Integer.valueOf(33685504));
    sTagInfo.put(-23520, Integer.valueOf(33685537));
    sTagInfo.put(-32102, Integer.valueOf(33882113));
    sTagInfo.put(-32099, Integer.valueOf(33882113));
    sTagInfo.put(-30686, Integer.valueOf(33751041));
    sTagInfo.put(-30684, Integer.valueOf(33685504));
    sTagInfo.put(-30681, Integer.valueOf(33751040));
    sTagInfo.put(-30680, Integer.valueOf(34013184));
    sTagInfo.put(-28159, Integer.valueOf(34209793));
    sTagInfo.put(-28158, Integer.valueOf(33882113));
    sTagInfo.put(-28157, Integer.valueOf(34209793));
    sTagInfo.put(-28156, Integer.valueOf(34209793));
    sTagInfo.put(-28155, Integer.valueOf(33882113));
    sTagInfo.put(-28154, Integer.valueOf(33882113));
    sTagInfo.put(-28153, Integer.valueOf(33751041));
    sTagInfo.put(-28152, Integer.valueOf(33751041));
    sTagInfo.put(-28151, Integer.valueOf(33751041));
    sTagInfo.put(-28150, Integer.valueOf(33882113));
    sTagInfo.put(-28140, Integer.valueOf(33751040));
    sTagInfo.put(-24053, Integer.valueOf(33882113));
    sTagInfo.put(-24052, Integer.valueOf(34013184));
    sTagInfo.put(-24050, Integer.valueOf(33882113));
    sTagInfo.put(-24049, Integer.valueOf(33882113));
    sTagInfo.put(-24048, Integer.valueOf(33751041));
    sTagInfo.put(-24044, Integer.valueOf(33751042));
    sTagInfo.put(-24043, Integer.valueOf(33882113));
    sTagInfo.put(-24041, Integer.valueOf(33751041));
    sTagInfo.put(-23808, Integer.valueOf(34013185));
    sTagInfo.put(-23807, Integer.valueOf(34013185));
    sTagInfo.put(-23806, Integer.valueOf(34013184));
    sTagInfo.put(-23551, Integer.valueOf(33751041));
    sTagInfo.put(-23550, Integer.valueOf(33751041));
    sTagInfo.put(-23549, Integer.valueOf(33751041));
    sTagInfo.put(-23548, Integer.valueOf(33882113));
    sTagInfo.put(-23547, Integer.valueOf(33751041));
    sTagInfo.put(-23546, Integer.valueOf(33751041));
    sTagInfo.put(-23545, Integer.valueOf(33882113));
    sTagInfo.put(-23544, Integer.valueOf(33751041));
    sTagInfo.put(-23543, Integer.valueOf(33751041));
    sTagInfo.put(-23542, Integer.valueOf(33751041));
    sTagInfo.put(-23541, Integer.valueOf(34013184));
    sTagInfo.put(-23540, Integer.valueOf(33751041));
    sTagInfo.put(0, Integer.valueOf(67174404));
    sTagInfo.put(1, Integer.valueOf(67239938));
    sTagInfo.put(3, Integer.valueOf(67239938));
    sTagInfo.put(2, Integer.valueOf(67764227));
    sTagInfo.put(4, Integer.valueOf(67764227));
    sTagInfo.put(5, Integer.valueOf(67174401));
    sTagInfo.put(6, Integer.valueOf(67436545));
    sTagInfo.put(7, Integer.valueOf(67436547));
    sTagInfo.put(8, Integer.valueOf(67239936));
    sTagInfo.put(9, Integer.valueOf(67239938));
    sTagInfo.put(10, Integer.valueOf(67239938));
    sTagInfo.put(11, Integer.valueOf(67436545));
    sTagInfo.put(12, Integer.valueOf(67239938));
    sTagInfo.put(13, Integer.valueOf(67436545));
    sTagInfo.put(14, Integer.valueOf(67239938));
    sTagInfo.put(15, Integer.valueOf(67436545));
    sTagInfo.put(16, Integer.valueOf(67239938));
    sTagInfo.put(17, Integer.valueOf(67436545));
    sTagInfo.put(18, Integer.valueOf(67239936));
    sTagInfo.put(19, Integer.valueOf(67239938));
    sTagInfo.put(20, Integer.valueOf(67436545));
    sTagInfo.put(23, Integer.valueOf(67239938));
    sTagInfo.put(24, Integer.valueOf(67436545));
    sTagInfo.put(25, Integer.valueOf(67239938));
    sTagInfo.put(26, Integer.valueOf(67436545));
    sTagInfo.put(27, Integer.valueOf(67567616));
    sTagInfo.put(28, Integer.valueOf(67567616));
    sTagInfo.put(29, Integer.valueOf(67239947));
    sTagInfo.put(30, Integer.valueOf(67305483));
  }

  static boolean isOffsetTag(short paramShort)
  {
    return (paramShort == -30871) || (paramShort == -30683) || (paramShort == 513) || (paramShort == 273) || (paramShort == -24571);
  }

  private void throwTypeNotMatchedException(String paramString)
    throws IllegalArgumentException
  {
    throw new IllegalArgumentException("Tag " + this.mTagId + ": expect type " + convertTypeToString(this.mDataType) + " but got " + paramString);
  }

  public boolean equals(Object paramObject)
  {
    ExifTag localExifTag;
    if (paramObject instanceof ExifTag)
    {
      localExifTag = (ExifTag)paramObject;
      if (this.mValue == null)
        break label165;
      if (!this.mValue instanceof long[])
        break label65;
      if (localExifTag.mValue instanceof long[])
        break label41;
    }
    do
    {
      do
      {
        do
        {
          return false;
          label41: return Arrays.equals((long[])(long[])this.mValue, (long[])(long[])localExifTag.mValue);
          label65: if (!this.mValue instanceof Rational[])
            break label109;
        }
        while (!localExifTag.mValue instanceof Rational[]);
        return Arrays.equals((Rational[])(Rational[])this.mValue, (Rational[])(Rational[])localExifTag.mValue);
        label109: if (!this.mValue instanceof byte[])
          break label153;
      }
      while (!localExifTag.mValue instanceof byte[]);
      return Arrays.equals((byte[])(byte[])this.mValue, (byte[])(byte[])localExifTag.mValue);
      label153: label165: return this.mValue.equals(localExifTag.mValue);
    }
    while (localExifTag.mValue != null);
    return true;
  }

  public void getBytes(byte[] paramArrayOfByte)
  {
    getBytes(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public void getBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if ((this.mDataType != 7) && (this.mDataType != 1))
      throw new IllegalArgumentException("Cannot get BYTE value from " + convertTypeToString(this.mDataType));
    Object localObject = this.mValue;
    if (paramInt2 > this.mComponentCount)
      paramInt2 = this.mComponentCount;
    System.arraycopy(localObject, 0, paramArrayOfByte, paramInt1, paramInt2);
  }

  public int getComponentCount()
  {
    return this.mComponentCount;
  }

  public int getDataSize()
  {
    return getComponentCount() * getElementSize(getDataType());
  }

  public short getDataType()
  {
    return this.mDataType;
  }

  public int getIfd()
  {
    return this.mIfd;
  }

  public int getLong(int paramInt)
  {
    if (this.mDataType != 9)
      throw new IllegalArgumentException("Cannot get LONG value from " + convertTypeToString(this.mDataType));
    return (int)((long[])(long[])this.mValue)[paramInt];
  }

  public int getOffset()
  {
    return this.mOffset;
  }

  public Rational getRational(int paramInt)
  {
    if ((this.mDataType != 10) && (this.mDataType != 5))
      throw new IllegalArgumentException("Cannot get RATIONAL value from " + convertTypeToString(this.mDataType));
    return ((Rational[])(Rational[])this.mValue)[paramInt];
  }

  public String getString()
  {
    if (this.mDataType != 2)
      throw new IllegalArgumentException("Cannot get ASCII value from " + convertTypeToString(this.mDataType));
    return (String)this.mValue;
  }

  public short getTagId()
  {
    return this.mTagId;
  }

  public long getUnsignedLong(int paramInt)
  {
    if (this.mDataType != 4)
      throw new IllegalArgumentException("Cannot get UNSIGNED LONG value from " + convertTypeToString(this.mDataType));
    return ((long[])(long[])this.mValue)[paramInt];
  }

  public int getUnsignedShort(int paramInt)
  {
    if (this.mDataType != 3)
      throw new IllegalArgumentException("Cannot get UNSIGNED_SHORT value from " + convertTypeToString(this.mDataType));
    return (int)((long[])(long[])this.mValue)[paramInt];
  }

  public boolean hasValue()
  {
    return this.mValue != null;
  }

  void setOffset(int paramInt)
  {
    this.mOffset = paramInt;
  }

  public void setTimeValue(long paramLong)
  {
    synchronized (TIME_FORMAT)
    {
      setValue(TIME_FORMAT.format(new Date(paramLong)));
      return;
    }
  }

  public void setValue(int paramInt)
  {
    checkComponentCountOrThrow(1);
    setValue(new int[] { paramInt });
  }

  public void setValue(Rational paramRational)
  {
    setValue(new Rational[] { paramRational });
  }

  public void setValue(String paramString)
  {
    checkComponentCountOrThrow(1 + paramString.length());
    if (this.mDataType != 2)
      throwTypeNotMatchedException("String");
    this.mComponentCount = (1 + paramString.length());
    this.mValue = paramString;
  }

  public void setValue(byte[] paramArrayOfByte)
  {
    setValue(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public void setValue(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    checkComponentCountOrThrow(paramInt2);
    if ((this.mDataType != 1) && (this.mDataType != 7))
      throwTypeNotMatchedException("byte");
    this.mValue = new byte[paramInt2];
    System.arraycopy(paramArrayOfByte, paramInt1, this.mValue, 0, paramInt2);
    this.mComponentCount = paramInt2;
  }

  public void setValue(int[] paramArrayOfInt)
  {
    checkComponentCountOrThrow(paramArrayOfInt.length);
    if ((this.mDataType != 3) && (this.mDataType != 9) && (this.mDataType != 4))
      throwTypeNotMatchedException("int");
    if (this.mDataType == 3)
      checkOverflowForUnsignedShort(paramArrayOfInt);
    long[] arrayOfLong;
    while (true)
    {
      arrayOfLong = new long[paramArrayOfInt.length];
      for (int i = 0; ; ++i)
      {
        if (i >= paramArrayOfInt.length)
          break label93;
        arrayOfLong[i] = paramArrayOfInt[i];
      }
      if (this.mDataType != 4)
        continue;
      checkOverflowForUnsignedLong(paramArrayOfInt);
    }
    label93: this.mValue = arrayOfLong;
    this.mComponentCount = paramArrayOfInt.length;
  }

  public void setValue(long[] paramArrayOfLong)
  {
    checkComponentCountOrThrow(paramArrayOfLong.length);
    if (this.mDataType != 4)
      throwTypeNotMatchedException("long");
    checkOverflowForUnsignedLong(paramArrayOfLong);
    this.mValue = paramArrayOfLong;
    this.mComponentCount = paramArrayOfLong.length;
  }

  public void setValue(Rational[] paramArrayOfRational)
  {
    if (this.mDataType == 5)
      checkOverflowForUnsignedRational(paramArrayOfRational);
    while (true)
    {
      checkComponentCountOrThrow(paramArrayOfRational.length);
      this.mValue = paramArrayOfRational;
      this.mComponentCount = paramArrayOfRational.length;
      return;
      if (this.mDataType == 10)
        checkOverflowForRational(paramArrayOfRational);
      throwTypeNotMatchedException("Rational");
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.exif.ExifTag
 * JD-Core Version:    0.5.4
 */