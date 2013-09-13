package com.android.gallery3d.exif;

class JpegHeader
{
  public static final boolean isSofMarker(short paramShort)
  {
    return (paramShort >= -64) && (paramShort <= -49) && (paramShort != -60) && (paramShort != -56) && (paramShort != -52);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.exif.JpegHeader
 * JD-Core Version:    0.5.4
 */