package com.adobe.xmp.impl;

public class Base64
{
  private static byte[] ascii;
  private static byte[] base64 = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };

  static
  {
    ascii = new byte['ÿ'];
    for (int i = 0; i < 255; ++i)
      ascii[i] = -1;
    for (int j = 0; j < base64.length; ++j)
      ascii[base64[j]] = (byte)j;
    ascii[9] = -2;
    ascii[10] = -2;
    ascii[13] = -2;
    ascii[32] = -2;
    ascii[61] = -3;
  }

  public static final byte[] decode(byte[] paramArrayOfByte)
    throws IllegalArgumentException
  {
    int i = 0;
    int j = 0;
    if (j < paramArrayOfByte.length)
    {
      label4: int i2 = ascii[paramArrayOfByte[j]];
      if (i2 >= 0)
      {
        int i3 = i + 1;
        paramArrayOfByte[i] = i2;
        i = i3;
      }
      do
      {
        ++j;
        break label4:
      }
      while (i2 != -1);
      throw new IllegalArgumentException("Invalid base 64 string");
    }
    while ((i > 0) && (paramArrayOfByte[(i - 1)] == -3))
      --i;
    byte[] arrayOfByte = new byte[i * 3 / 4];
    int k = 0;
    for (int l = 0; l < -2 + arrayOfByte.length; l += 3)
    {
      arrayOfByte[l] = (byte)(0xFF & paramArrayOfByte[k] << 2 | 0x3 & paramArrayOfByte[(k + 1)] >>> 4);
      arrayOfByte[(l + 1)] = (byte)(0xFF & paramArrayOfByte[(k + 1)] << 4 | 0xF & paramArrayOfByte[(k + 2)] >>> 2);
      arrayOfByte[(l + 2)] = (byte)(0xFF & paramArrayOfByte[(k + 2)] << 6 | 0x3F & paramArrayOfByte[(k + 3)]);
      k += 4;
    }
    if (l < arrayOfByte.length)
      arrayOfByte[l] = (byte)(0xFF & paramArrayOfByte[k] << 2 | 0x3 & paramArrayOfByte[(k + 1)] >>> 4);
    int i1 = l + 1;
    if (i1 < arrayOfByte.length)
      arrayOfByte[i1] = (byte)(0xFF & paramArrayOfByte[(k + 1)] << 4 | 0xF & paramArrayOfByte[(k + 2)] >>> 2);
    return arrayOfByte;
  }

  public static final byte[] encode(byte[] paramArrayOfByte)
  {
    return encode(paramArrayOfByte, 0);
  }

  public static final byte[] encode(byte[] paramArrayOfByte, int paramInt)
  {
    int i = 4 * (paramInt / 4);
    if (i < 0)
      i = 0;
    int j = 4 * ((2 + paramArrayOfByte.length) / 3);
    if (i > 0)
      j += (j - 1) / i;
    byte[] arrayOfByte = new byte[j];
    int k = 0;
    int l = 0;
    int i1 = 0;
    while (true)
    {
      int i19;
      if (l + 3 <= paramArrayOfByte.length)
      {
        int i15 = l + 1;
        int i16 = (0xFF & paramArrayOfByte[l]) << 16;
        int i17 = i15 + 1;
        int i18 = i16 | (0xFF & paramArrayOfByte[i15]) << 8;
        i19 = i17 + 1;
        int i20 = i18 | (0xFF & paramArrayOfByte[i17]) << 0;
        int i21 = (i20 & 0xFC0000) >> 18;
        int i22 = k + 1;
        arrayOfByte[k] = base64[i21];
        int i23 = (i20 & 0x3F000) >> 12;
        int i24 = i22 + 1;
        arrayOfByte[i22] = base64[i23];
        int i25 = (i20 & 0xFC0) >> 6;
        int i26 = i24 + 1;
        arrayOfByte[i24] = base64[i25];
        int i27 = i20 & 0x3F;
        k = i26 + 1;
        arrayOfByte[i26] = base64[i27];
        i1 += 4;
        if ((k >= j) || (i <= 0) || (i1 % i != 0))
          break label505;
        int i28 = k + 1;
        arrayOfByte[k] = 10;
        l = i19;
        k = i28;
      }
      if (paramArrayOfByte.length - l == 2)
      {
        int i8 = (0xFF & paramArrayOfByte[l]) << 16 | (0xFF & paramArrayOfByte[(l + 1)]) << 8;
        int i9 = (i8 & 0xFC0000) >> 18;
        int i10 = k + 1;
        arrayOfByte[k] = base64[i9];
        int i11 = (i8 & 0x3F000) >> 12;
        int i12 = i10 + 1;
        arrayOfByte[i10] = base64[i11];
        int i13 = (i8 & 0xFC0) >> 6;
        int i14 = i12 + 1;
        arrayOfByte[i12] = base64[i13];
        (i14 + 1);
        arrayOfByte[i14] = 61;
      }
      do
        return arrayOfByte;
      while (paramArrayOfByte.length - l != 1);
      int i2 = (0xFF & paramArrayOfByte[l]) << 16;
      int i3 = (i2 & 0xFC0000) >> 18;
      int i4 = k + 1;
      arrayOfByte[k] = base64[i3];
      int i5 = (i2 & 0x3F000) >> 12;
      int i6 = i4 + 1;
      arrayOfByte[i4] = base64[i5];
      int i7 = i6 + 1;
      arrayOfByte[i6] = 61;
      (i7 + 1);
      arrayOfByte[i7] = 61;
      return arrayOfByte;
      label505: l = i19;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.Base64
 * JD-Core Version:    0.5.4
 */