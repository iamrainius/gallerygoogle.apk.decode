package com.android.gallery3d.data;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;

public class Exif
{
  public static int getOrientation(InputStream paramInputStream)
  {
    if (paramInputStream == null)
      return 0;
    byte[] arrayOfByte1 = new byte[8];
    int i;
    byte[] arrayOfByte2;
    while (true)
    {
      boolean bool1 = read(paramInputStream, arrayOfByte1, 2);
      i = 0;
      if (bool1)
      {
        int i4 = 0xFF & arrayOfByte1[0];
        i = 0;
        if (i4 == 255)
        {
          int i5 = 0xFF & arrayOfByte1[1];
          if ((i5 == 255) || (i5 == 216) || (i5 == 1))
            continue;
          if ((i5 == 217) || (i5 == 218))
            return 0;
          if (!read(paramInputStream, arrayOfByte1, 2))
            return 0;
          int i6 = pack(arrayOfByte1, 0, 2, false);
          if (i6 < 2)
          {
            Log.e("CameraExif", "Invalid length");
            return 0;
          }
          i = i6 - 2;
          if ((i5 != 225) || (i < 6))
            break label207;
          if (!read(paramInputStream, arrayOfByte1, 6))
            return 0;
          i -= 6;
          if ((pack(arrayOfByte1, 0, 4, false) != 1165519206) || (pack(arrayOfByte1, 4, 2, false) != 0))
            break label207;
        }
      }
      if (i <= 8)
        break label468;
      arrayOfByte2 = new byte[i];
      if (read(paramInputStream, arrayOfByte2, i))
        break;
      return 0;
      label207: long l1 = i;
      try
      {
        paramInputStream.skip(l1);
      }
      catch (IOException localIOException)
      {
        return 0;
      }
    }
    int j = pack(arrayOfByte2, 0, 4, false);
    if ((j != 1229531648) && (j != 1296891946))
    {
      Log.e("CameraExif", "Invalid byte order");
      return 0;
    }
    if (j == 1229531648);
    int k;
    for (boolean bool2 = true; ; bool2 = false)
    {
      k = 2 + pack(arrayOfByte2, 4, 4, bool2);
      if ((k >= 10) && (k <= i))
        break;
      Log.e("CameraExif", "Invalid offset");
      return 0;
    }
    int l = 0 + k;
    int i1 = i - k;
    int i3;
    for (int i2 = pack(arrayOfByte2, l - 2, 2, bool2); ; i2 = i3)
    {
      i3 = i2 - 1;
      if ((i2 <= 0) || (i1 < 12))
        break;
      if (pack(arrayOfByte2, l, 2, bool2) == 274)
      {
        switch (pack(arrayOfByte2, l + 8, 2, bool2))
        {
        case 2:
        case 4:
        case 5:
        case 7:
        default:
          Log.i("CameraExif", "Unsupported orientation");
          return 0;
        case 1:
          return 0;
        case 3:
          return 180;
        case 6:
          return 90;
        case 8:
        }
        return 270;
      }
      l += 12;
      i1 -= 12;
    }
    label468: Log.i("CameraExif", "Orientation not found");
    return 0;
  }

  private static int pack(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = 1;
    if (paramBoolean)
    {
      paramInt1 += paramInt2 - 1;
      i = -1;
    }
    int j = 0;
    int l;
    for (int k = paramInt2; ; k = l)
    {
      l = k - 1;
      if (k <= 0)
        break;
      j = j << 8 | 0xFF & paramArrayOfByte[paramInt1];
      paramInt1 += i;
    }
    return j;
  }

  private static boolean read(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt)
  {
    try
    {
      int i = paramInputStream.read(paramArrayOfByte, 0, paramInt);
      int j = 0;
      if (i == paramInt)
        j = 1;
      return j;
    }
    catch (IOException localIOException)
    {
    }
    return false;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.Exif
 * JD-Core Version:    0.5.4
 */