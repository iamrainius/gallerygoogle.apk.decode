package com.coremedia.iso;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public final class IsoTypeReader
{
  public static int byte2int(byte paramByte)
  {
    if (paramByte < 0)
      paramByte += 256;
    return paramByte;
  }

  public static String read4cc(ByteBuffer paramByteBuffer)
  {
    byte[] arrayOfByte = new byte[4];
    paramByteBuffer.get(arrayOfByte);
    return IsoFile.bytesToFourCC(arrayOfByte);
  }

  public static double readFixedPoint1616(ByteBuffer paramByteBuffer)
  {
    byte[] arrayOfByte = new byte[4];
    paramByteBuffer.get(arrayOfByte);
    return (0x0 | 0xFF000000 & arrayOfByte[0] << 24 | 0xFF0000 & arrayOfByte[1] << 16 | 0xFF00 & arrayOfByte[2] << 8 | 0xFF & arrayOfByte[3]) / 65536.0D;
  }

  public static float readFixedPoint88(ByteBuffer paramByteBuffer)
  {
    byte[] arrayOfByte = new byte[2];
    paramByteBuffer.get(arrayOfByte);
    return (short)((short)(0x0 | 0xFF00 & arrayOfByte[0] << 8) | 0xFF & arrayOfByte[1]) / 256.0F;
  }

  public static String readIso639(ByteBuffer paramByteBuffer)
  {
    int i = readUInt16(paramByteBuffer);
    StringBuilder localStringBuilder = new StringBuilder();
    for (int j = 0; j < 3; ++j)
      localStringBuilder.append((char)(96 + (0x1F & i >> 5 * (2 - j))));
    return localStringBuilder.toString();
  }

  public static String readString(ByteBuffer paramByteBuffer)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    while (true)
    {
      int i = paramByteBuffer.get();
      if (i == 0)
        break;
      localByteArrayOutputStream.write(i);
    }
    return Utf8.convert(localByteArrayOutputStream.toByteArray());
  }

  public static String readString(ByteBuffer paramByteBuffer, int paramInt)
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramByteBuffer.get(arrayOfByte);
    return Utf8.convert(arrayOfByte);
  }

  public static int readUInt16(ByteBuffer paramByteBuffer)
  {
    return 0 + (byte2int(paramByteBuffer.get()) << 8) + byte2int(paramByteBuffer.get());
  }

  public static int readUInt16BE(ByteBuffer paramByteBuffer)
  {
    return 0 + byte2int(paramByteBuffer.get()) + (byte2int(paramByteBuffer.get()) << 8);
  }

  public static int readUInt24(ByteBuffer paramByteBuffer)
  {
    return 0 + (readUInt16(paramByteBuffer) << 8) + byte2int(paramByteBuffer.get());
  }

  public static long readUInt32(ByteBuffer paramByteBuffer)
  {
    long l = paramByteBuffer.getInt();
    if (l < 0L)
      l += 4294967296L;
    return l;
  }

  public static long readUInt32BE(ByteBuffer paramByteBuffer)
  {
    long l1 = readUInt8(paramByteBuffer);
    long l2 = readUInt8(paramByteBuffer);
    long l3 = readUInt8(paramByteBuffer);
    return (readUInt8(paramByteBuffer) << 24) + (l3 << 16) + (l2 << 8) + (l1 << 0);
  }

  public static long readUInt64(ByteBuffer paramByteBuffer)
  {
    long l = 0L + (readUInt32(paramByteBuffer) << 32);
    if (l < 0L)
      throw new RuntimeException("I don't know how to deal with UInt64! long is not sufficient and I don't want to use BigInt");
    return l + readUInt32(paramByteBuffer);
  }

  public static int readUInt8(ByteBuffer paramByteBuffer)
  {
    return byte2int(paramByteBuffer.get());
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.IsoTypeReader
 * JD-Core Version:    0.5.4
 */