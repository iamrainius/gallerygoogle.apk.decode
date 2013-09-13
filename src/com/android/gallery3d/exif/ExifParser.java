package com.android.gallery3d.exif;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ExifParser
{
  private boolean mContainExifData = false;
  private final TreeMap<Integer, Object> mCorrespondingEvent = new TreeMap();
  private int mIfdStartOffset = 0;
  private int mIfdType;
  private ImageEvent mImageEvent;
  private ExifTag mJpegSizeTag;
  private boolean mNeedToParseOffsetsInCurrentIfd;
  private int mNumOfTagInIfd = 0;
  private final int mOptions;
  private ExifTag mStripSizeTag;
  private ExifTag mTag;
  private final CountedDataInputStream mTiffStream;

  private ExifParser(InputStream paramInputStream, int paramInt)
    throws IOException, ExifInvalidFormatException
  {
    this.mTiffStream = new CountedDataInputStream(paramInputStream);
    this.mOptions = paramInt;
    if (!this.mContainExifData);
    do
      return;
    while (this.mTiffStream.getReadByteCount() != 0);
    parseTiffHeader();
    registerIfd(0, this.mTiffStream.readUnsignedInt());
  }

  private void checkOffsetOrImageTag(ExifTag paramExifTag)
  {
    switch (paramExifTag.getTagId())
    {
    default:
    case -30871:
    case -30683:
    case -24571:
    case 513:
    case 514:
    case 273:
    case 279:
    }
    do
    {
      int i;
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                    return;
                  while ((!isIfdRequested(2)) && (!isIfdRequested(3)));
                  registerIfd(2, paramExifTag.getUnsignedLong(0));
                  return;
                }
                while (!isIfdRequested(4));
                registerIfd(4, paramExifTag.getUnsignedLong(0));
                return;
              }
              while (!isIfdRequested(3));
              registerIfd(3, paramExifTag.getUnsignedLong(0));
              return;
            }
            while (!isThumbnailRequested());
            registerCompressedImage(paramExifTag.getUnsignedLong(0));
            return;
          }
          while (!isThumbnailRequested());
          this.mJpegSizeTag = paramExifTag;
          return;
        }
        while (!isThumbnailRequested());
        if (!paramExifTag.hasValue())
          break label230;
        label184: i = 0;
      }
      while (i >= paramExifTag.getComponentCount());
      if (paramExifTag.getDataType() == 3)
        registerUncompressedStrip(i, paramExifTag.getUnsignedShort(i));
      while (true)
      {
        ++i;
        break label184:
        registerUncompressedStrip(i, paramExifTag.getUnsignedLong(i));
      }
      label230: this.mCorrespondingEvent.put(Integer.valueOf(paramExifTag.getOffset()), new ExifTagEvent(paramExifTag, false));
      return;
    }
    while ((!isThumbnailRequested()) || (!paramExifTag.hasValue()));
    this.mStripSizeTag = paramExifTag;
  }

  private boolean isIfdRequested(int paramInt)
  {
    int i = 1;
    switch (paramInt)
    {
    default:
      i = 0;
    case 0:
    case 1:
    case 2:
    case 4:
    case 3:
    }
    do
    {
      do
      {
        do
        {
          do
          {
            do
              return i;
            while ((0x1 & this.mOptions) != 0);
            return false;
          }
          while ((0x2 & this.mOptions) != 0);
          return false;
        }
        while ((0x4 & this.mOptions) != 0);
        return false;
      }
      while ((0x8 & this.mOptions) != 0);
      return false;
    }
    while ((0x10 & this.mOptions) != 0);
    return false;
  }

  private boolean isThumbnailRequested()
  {
    return (0x20 & this.mOptions) != 0;
  }

  private boolean needToParseOffsetsInCurrentIfd()
  {
    switch (this.mIfdType)
    {
    default:
    case 0:
      while (true)
      {
        return false;
        if ((isIfdRequested(2)) || (isIfdRequested(4)) || (isIfdRequested(3)))
          return true;
      }
    case 1:
      return isThumbnailRequested();
    case 2:
    }
    return isIfdRequested(3);
  }

  public static ExifParser parse(InputStream paramInputStream)
    throws IOException, ExifInvalidFormatException
  {
    return new ExifParser(paramInputStream, 63);
  }

  private void parseTiffHeader()
    throws IOException, ExifInvalidFormatException
  {
    int i = this.mTiffStream.readShort();
    if (18761 == i)
      this.mTiffStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
    while (true)
    {
      if (this.mTiffStream.readShort() == 42)
        return;
      throw new ExifInvalidFormatException("Invalid TIFF header");
      if (19789 != i)
        break;
      this.mTiffStream.setByteOrder(ByteOrder.BIG_ENDIAN);
    }
    throw new ExifInvalidFormatException("Invalid TIFF header");
  }

  private void readFullTagValue(ExifTag paramExifTag)
    throws IOException
  {
    switch (paramExifTag.getDataType())
    {
    case 6:
    case 8:
    default:
      return;
    case 1:
    case 7:
      byte[] arrayOfByte = new byte[paramExifTag.getComponentCount()];
      read(arrayOfByte);
      paramExifTag.setValue(arrayOfByte);
      return;
    case 2:
      paramExifTag.setValue(readString(paramExifTag.getComponentCount()));
      return;
    case 4:
      long[] arrayOfLong = new long[paramExifTag.getComponentCount()];
      int i5 = 0;
      int i6 = arrayOfLong.length;
      while (i5 < i6)
      {
        arrayOfLong[i5] = readUnsignedLong();
        ++i5;
      }
      paramExifTag.setValue(arrayOfLong);
      return;
    case 5:
      Rational[] arrayOfRational2 = new Rational[paramExifTag.getComponentCount()];
      int i3 = 0;
      int i4 = arrayOfRational2.length;
      while (i3 < i4)
      {
        arrayOfRational2[i3] = readUnsignedRational();
        ++i3;
      }
      paramExifTag.setValue(arrayOfRational2);
      return;
    case 3:
      int[] arrayOfInt2 = new int[paramExifTag.getComponentCount()];
      int i1 = 0;
      int i2 = arrayOfInt2.length;
      while (i1 < i2)
      {
        arrayOfInt2[i1] = readUnsignedShort();
        ++i1;
      }
      paramExifTag.setValue(arrayOfInt2);
      return;
    case 9:
      int[] arrayOfInt1 = new int[paramExifTag.getComponentCount()];
      int k = 0;
      int l = arrayOfInt1.length;
      while (k < l)
      {
        arrayOfInt1[k] = readLong();
        ++k;
      }
      paramExifTag.setValue(arrayOfInt1);
      return;
    case 10:
    }
    Rational[] arrayOfRational1 = new Rational[paramExifTag.getComponentCount()];
    int i = 0;
    int j = arrayOfRational1.length;
    while (i < j)
    {
      arrayOfRational1[i] = readRational();
      ++i;
    }
    paramExifTag.setValue(arrayOfRational1);
  }

  private ExifTag readTag()
    throws IOException, ExifInvalidFormatException
  {
    short s1 = this.mTiffStream.readShort();
    short s2 = this.mTiffStream.readShort();
    long l1 = this.mTiffStream.readUnsignedInt();
    if (l1 > 2147483647L)
      throw new ExifInvalidFormatException("Number of component is larger then Integer.MAX_VALUE");
    ExifTag localExifTag = new ExifTag(s1, s2, (int)l1, this.mIfdType);
    int i = localExifTag.getDataSize();
    if (i > 4)
    {
      long l2 = this.mTiffStream.readUnsignedInt();
      if (l2 > 2147483647L)
        throw new ExifInvalidFormatException("offset is larger then Integer.MAX_VALUE");
      localExifTag.setOffset((int)l2);
      return localExifTag;
    }
    readFullTagValue(localExifTag);
    this.mTiffStream.skip(4 - i);
    return localExifTag;
  }

  private void registerCompressedImage(long paramLong)
  {
    this.mCorrespondingEvent.put(Integer.valueOf((int)paramLong), new ImageEvent(3));
  }

  private void registerIfd(int paramInt, long paramLong)
  {
    this.mCorrespondingEvent.put(Integer.valueOf((int)paramLong), new IfdEvent(paramInt, isIfdRequested(paramInt)));
  }

  private void registerUncompressedStrip(int paramInt, long paramLong)
  {
    this.mCorrespondingEvent.put(Integer.valueOf((int)paramLong), new ImageEvent(4, paramInt));
  }

  private boolean seekTiffData(InputStream paramInputStream)
    throws IOException, ExifInvalidFormatException
  {
    DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
    if (localDataInputStream.readShort() != -40)
      throw new ExifInvalidFormatException("Invalid JPEG format");
    for (short s = localDataInputStream.readShort(); (s != -31) && (s != -39) && (!JpegHeader.isSofMarker(s)); s = localDataInputStream.readShort())
    {
      int i = localDataInputStream.readUnsignedShort();
      if (i - 2 == localDataInputStream.skip(i - 2))
        continue;
      throw new EOFException();
    }
    if (s != -31);
    do
    {
      return false;
      localDataInputStream.readShort();
    }
    while ((localDataInputStream.readInt() != 1165519206) || (localDataInputStream.readShort() != 0));
    return true;
  }

  private void skipTo(int paramInt)
    throws IOException
  {
    this.mTiffStream.skipTo(paramInt);
    while ((!this.mCorrespondingEvent.isEmpty()) && (((Integer)this.mCorrespondingEvent.firstKey()).intValue() < paramInt))
      this.mCorrespondingEvent.pollFirstEntry();
  }

  public ByteOrder getByteOrder()
  {
    return this.mTiffStream.getByteOrder();
  }

  public int getCompressedImageSize()
  {
    if (this.mJpegSizeTag == null)
      return 0;
    return (int)this.mJpegSizeTag.getUnsignedLong(0);
  }

  public int getCurrentIfd()
  {
    return this.mIfdType;
  }

  public int getStripIndex()
  {
    return this.mImageEvent.stripIndex;
  }

  public int getStripSize()
  {
    if (this.mStripSizeTag == null)
      return 0;
    if (this.mStripSizeTag.getDataType() == 3)
      return this.mStripSizeTag.getUnsignedShort(this.mImageEvent.stripIndex);
    return (int)this.mStripSizeTag.getUnsignedLong(this.mImageEvent.stripIndex);
  }

  public ExifTag getTag()
  {
    return this.mTag;
  }

  public int next()
    throws IOException, ExifInvalidFormatException
  {
    int i = 1;
    if (!this.mContainExifData)
      i = 5;
    int j;
    int k;
    do
    {
      return i;
      j = this.mTiffStream.getReadByteCount();
      k = 2 + this.mIfdStartOffset + 12 * this.mNumOfTagInIfd;
      if (j >= k)
        break label66;
      this.mTag = readTag();
    }
    while (!this.mNeedToParseOffsetsInCurrentIfd);
    checkOffsetOrImageTag(this.mTag);
    return i;
    label66: long l;
    if (j == k)
    {
      l = readUnsignedLong();
      if (this.mIfdType != 0)
        break label227;
      if ((((isIfdRequested(i)) || (isThumbnailRequested()))) && (l != 0L))
        registerIfd(i, l);
    }
    while (this.mCorrespondingEvent.size() != 0)
    {
      Map.Entry localEntry = this.mCorrespondingEvent.pollFirstEntry();
      Object localObject = localEntry.getValue();
      skipTo(((Integer)localEntry.getKey()).intValue());
      if (localObject instanceof IfdEvent)
      {
        this.mIfdType = ((IfdEvent)localObject).ifd;
        this.mNumOfTagInIfd = this.mTiffStream.readUnsignedShort();
        this.mIfdStartOffset = ((Integer)localEntry.getKey()).intValue();
        this.mNeedToParseOffsetsInCurrentIfd = needToParseOffsetsInCurrentIfd();
        if (((IfdEvent)localObject).isRequested)
        {
          return 0;
          label227: if (l == 0L)
            continue;
          throw new ExifInvalidFormatException("Invalid link to next IFD");
        }
        skipRemainingTagsInCurrentIfd();
      }
      if (localObject instanceof ImageEvent)
      {
        this.mImageEvent = ((ImageEvent)localObject);
        return this.mImageEvent.type;
      }
      ExifTagEvent localExifTagEvent = (ExifTagEvent)localObject;
      this.mTag = localExifTagEvent.tag;
      if (this.mTag.getDataType() != 7)
      {
        readFullTagValue(this.mTag);
        checkOffsetOrImageTag(this.mTag);
      }
      if (localExifTagEvent.isRequested)
        return 2;
    }
    return 5;
  }

  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return this.mTiffStream.read(paramArrayOfByte);
  }

  public int readLong()
    throws IOException
  {
    return this.mTiffStream.readInt();
  }

  public Rational readRational()
    throws IOException
  {
    int i = readLong();
    int j = readLong();
    return new Rational(i, j);
  }

  public String readString(int paramInt)
    throws IOException
  {
    if (paramInt > 0)
    {
      byte[] arrayOfByte = new byte[paramInt];
      this.mTiffStream.readOrThrow(arrayOfByte);
      return new String(arrayOfByte, 0, paramInt - 1, "UTF8");
    }
    return "";
  }

  public long readUnsignedLong()
    throws IOException
  {
    return 0xFFFFFFFF & readLong();
  }

  public Rational readUnsignedRational()
    throws IOException
  {
    return new Rational(readUnsignedLong(), readUnsignedLong());
  }

  public int readUnsignedShort()
    throws IOException
  {
    return 0xFFFF & this.mTiffStream.readShort();
  }

  public void registerForTagValue(ExifTag paramExifTag)
  {
    this.mCorrespondingEvent.put(Integer.valueOf(paramExifTag.getOffset()), new ExifTagEvent(paramExifTag, true));
  }

  public void skipRemainingTagsInCurrentIfd()
    throws IOException, ExifInvalidFormatException
  {
    int i = 2 + this.mIfdStartOffset + 12 * this.mNumOfTagInIfd;
    int j = this.mTiffStream.getReadByteCount();
    if (j > i);
    label68: long l;
    do
    {
      return;
      if (this.mNeedToParseOffsetsInCurrentIfd)
        while (true)
        {
          if (j >= i)
            break label68;
          this.mTag = readTag();
          checkOffsetOrImageTag(this.mTag);
          j += 12;
        }
      skipTo(i);
      l = readUnsignedLong();
    }
    while ((this.mIfdType != 0) || ((!isIfdRequested(1)) && (!isThumbnailRequested())) || (l <= 0L));
    registerIfd(1, l);
  }

  private static class ExifTagEvent
  {
    boolean isRequested;
    ExifTag tag;

    ExifTagEvent(ExifTag paramExifTag, boolean paramBoolean)
    {
      this.tag = paramExifTag;
      this.isRequested = paramBoolean;
    }
  }

  private static class IfdEvent
  {
    int ifd;
    boolean isRequested;

    IfdEvent(int paramInt, boolean paramBoolean)
    {
      this.ifd = paramInt;
      this.isRequested = paramBoolean;
    }
  }

  private static class ImageEvent
  {
    int stripIndex;
    int type;

    ImageEvent(int paramInt)
    {
      this.stripIndex = 0;
      this.type = paramInt;
    }

    ImageEvent(int paramInt1, int paramInt2)
    {
      this.type = paramInt1;
      this.stripIndex = paramInt2;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.exif.ExifParser
 * JD-Core Version:    0.5.4
 */