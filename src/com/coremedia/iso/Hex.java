package com.coremedia.iso;

public class Hex
{
  private static final char[] DIGITS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };

  public static String encodeHex(byte[] paramArrayOfByte)
  {
    return encodeHex(paramArrayOfByte, 0);
  }

  public static String encodeHex(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramArrayOfByte.length;
    int j = i << 1;
    int k;
    label16: char[] arrayOfChar;
    int l;
    int i1;
    label30: int i2;
    if (paramInt > 0)
    {
      k = i / paramInt;
      arrayOfChar = new char[k + j];
      l = 0;
      i1 = 0;
      if (l >= i)
        break label128;
      if ((paramInt <= 0) || (l % paramInt != 0) || (i1 <= 0))
        break label138;
      i2 = i1 + 1;
      arrayOfChar[i1] = '-';
    }
    while (true)
    {
      int i3 = i2 + 1;
      arrayOfChar[i2] = DIGITS[((0xF0 & paramArrayOfByte[l]) >>> 4)];
      int i4 = i3 + 1;
      arrayOfChar[i3] = DIGITS[(0xF & paramArrayOfByte[l])];
      ++l;
      i1 = i4;
      break label30:
      k = 0;
      break label16:
      label128: return new String(arrayOfChar);
      label138: i2 = i1;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.Hex
 * JD-Core Version:    0.5.4
 */