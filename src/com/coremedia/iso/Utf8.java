package com.coremedia.iso;

import java.io.UnsupportedEncodingException;

public final class Utf8
{
  public static String convert(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null);
    try
    {
      String str = new String(paramArrayOfByte, "UTF-8");
      return str;
      return null;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new Error(localUnsupportedEncodingException);
    }
  }

  public static byte[] convert(String paramString)
  {
    if (paramString != null);
    try
    {
      byte[] arrayOfByte = paramString.getBytes("UTF-8");
      return arrayOfByte;
      return null;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new Error(localUnsupportedEncodingException);
    }
  }

  public static int utf8StringLengthInBytes(String paramString)
  {
    if (paramString != null);
    try
    {
      int i = paramString.getBytes("UTF-8").length;
      return i;
      return 0;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new RuntimeException();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.Utf8
 * JD-Core Version:    0.5.4
 */