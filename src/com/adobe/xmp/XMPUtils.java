package com.adobe.xmp;

import com.adobe.xmp.impl.Base64;
import com.adobe.xmp.impl.ISO8601Converter;

public class XMPUtils
{
  public static String convertFromBoolean(boolean paramBoolean)
  {
    if (paramBoolean)
      return "True";
    return "False";
  }

  public static String convertFromDate(XMPDateTime paramXMPDateTime)
  {
    return ISO8601Converter.render(paramXMPDateTime);
  }

  public static String convertFromDouble(double paramDouble)
  {
    return String.valueOf(paramDouble);
  }

  public static String convertFromInteger(int paramInt)
  {
    return String.valueOf(paramInt);
  }

  public static String convertFromLong(long paramLong)
  {
    return String.valueOf(paramLong);
  }

  public static boolean convertToBoolean(String paramString)
    throws XMPException
  {
    if ((paramString == null) || (paramString.length() == 0))
      throw new XMPException("Empty convert-string", 5);
    String str = paramString.toLowerCase();
    int i;
    try
    {
      int j = Integer.parseInt(str);
      return j != 0;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      if ((!"true".equals(str)) && (!"t".equals(str)) && (!"on".equals(str)))
      {
        boolean bool = "yes".equals(str);
        i = 0;
        if (!bool);
      }
      i = 1;
    }
    return i;
  }

  public static XMPDateTime convertToDate(String paramString)
    throws XMPException
  {
    if ((paramString == null) || (paramString.length() == 0))
      throw new XMPException("Empty convert-string", 5);
    return ISO8601Converter.parse(paramString);
  }

  public static double convertToDouble(String paramString)
    throws XMPException
  {
    if (paramString != null);
    try
    {
      if (paramString.length() == 0)
        throw new XMPException("Empty convert-string", 5);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new XMPException("Invalid double string", 5);
    }
    double d = Double.parseDouble(paramString);
    return d;
  }

  public static int convertToInteger(String paramString)
    throws XMPException
  {
    if (paramString != null);
    try
    {
      if (paramString.length() == 0)
        throw new XMPException("Empty convert-string", 5);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new XMPException("Invalid integer string", 5);
    }
    if (paramString.startsWith("0x"))
      return Integer.parseInt(paramString.substring(2), 16);
    int i = Integer.parseInt(paramString);
    return i;
  }

  public static long convertToLong(String paramString)
    throws XMPException
  {
    if (paramString != null);
    try
    {
      if (paramString.length() == 0)
        throw new XMPException("Empty convert-string", 5);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new XMPException("Invalid long string", 5);
    }
    if (paramString.startsWith("0x"))
      return Long.parseLong(paramString.substring(2), 16);
    long l = Long.parseLong(paramString);
    return l;
  }

  public static byte[] decodeBase64(String paramString)
    throws XMPException
  {
    try
    {
      byte[] arrayOfByte = Base64.decode(paramString.getBytes());
      return arrayOfByte;
    }
    catch (Throwable localThrowable)
    {
      throw new XMPException("Invalid base64 string", 5, localThrowable);
    }
  }

  public static String encodeBase64(byte[] paramArrayOfByte)
  {
    return new String(Base64.encode(paramArrayOfByte));
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.XMPUtils
 * JD-Core Version:    0.5.4
 */