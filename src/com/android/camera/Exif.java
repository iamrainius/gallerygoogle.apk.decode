package com.android.camera;

import android.util.Log;

public class Exif
{
  public static int getOrientation(byte[] paramArrayOfByte)
  {
    int i = 1;
    if (paramArrayOfByte == null)
      return 0;
    int j = 0;
    int i8;
    int i9;
    do
    {
      while (true)
      {
        int k = j + 3;
        int l = paramArrayOfByte.length;
        i1 = 0;
        if (k >= l)
          break label110;
        i8 = j + 1;
        if ((0xFF & paramArrayOfByte[j]) != 255)
          break label456;
        i9 = 0xFF & paramArrayOfByte[i8];
        if (i9 != 255)
          break;
        j = i8;
      }
      j = i8 + 1;
    }
    while ((i9 == 216) || (i9 == i));
    int i1 = 0;
    if (i9 != 217)
    {
      i1 = 0;
      if (i9 != 218)
        break label150;
    }
    while (true)
    {
      if (i1 > 8)
      {
        label110: int i2 = pack(paramArrayOfByte, j, 4, false);
        if ((i2 != 1229531648) && (i2 != 1296891946))
        {
          Log.e("CameraExif", "Invalid byte order");
          return 0;
          label150: int i10 = pack(paramArrayOfByte, j, 2, false);
          if ((i10 < 2) || (j + i10 > paramArrayOfByte.length))
          {
            Log.e("CameraExif", "Invalid length");
            return 0;
          }
          if ((i9 == 225) && (i10 >= 8) && (pack(paramArrayOfByte, j + 2, 4, false) == 1165519206) && (pack(paramArrayOfByte, j + 6, 2, false) == 0))
          {
            j += 8;
            i1 = i10 - 8;
          }
          j += i10;
        }
        if (i2 == 1229531648);
        int i3;
        while (true)
        {
          i3 = 2 + pack(paramArrayOfByte, j + 4, 4, i);
          if ((i3 >= 10) && (i3 <= i1))
            break;
          Log.e("CameraExif", "Invalid offset");
          return 0;
          i = 0;
        }
        int i4 = j + i3;
        int i5 = i1 - i3;
        int i7;
        for (int i6 = pack(paramArrayOfByte, i4 - 2, 2, i); ; i6 = i7)
        {
          i7 = i6 - 1;
          if ((i6 <= 0) || (i5 < 12))
            break;
          if (pack(paramArrayOfByte, i4, 2, i) == 274)
          {
            switch (pack(paramArrayOfByte, i4 + 8, 2, i))
            {
            case 1:
            case 2:
            case 4:
            case 5:
            case 7:
            default:
              Log.i("CameraExif", "Unsupported orientation");
              return 0;
            case 3:
              return 180;
            case 6:
              return 90;
            case 8:
            }
            return 270;
          }
          i4 += 12;
          i5 -= 12;
        }
      }
      Log.i("CameraExif", "Orientation not found");
      return 0;
      label456: j = i8;
      i1 = 0;
    }
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
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.Exif
 * JD-Core Version:    0.5.4
 */