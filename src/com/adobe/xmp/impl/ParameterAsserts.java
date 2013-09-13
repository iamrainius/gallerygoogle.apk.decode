package com.adobe.xmp.impl;

import com.adobe.xmp.XMPException;

class ParameterAsserts
{
  public static void assertArrayName(String paramString)
    throws XMPException
  {
    if ((paramString != null) && (paramString.length() != 0))
      return;
    throw new XMPException("Empty array name", 4);
  }

  public static void assertNotNull(Object paramObject)
    throws XMPException
  {
    if (paramObject == null)
      throw new XMPException("Parameter must not be null", 4);
    if ((!paramObject instanceof String) || (((String)paramObject).length() != 0))
      return;
    throw new XMPException("Parameter must not be null or empty", 4);
  }

  public static void assertPrefix(String paramString)
    throws XMPException
  {
    if ((paramString != null) && (paramString.length() != 0))
      return;
    throw new XMPException("Empty prefix", 4);
  }

  public static void assertPropName(String paramString)
    throws XMPException
  {
    if ((paramString != null) && (paramString.length() != 0))
      return;
    throw new XMPException("Empty property name", 4);
  }

  public static void assertSchemaNS(String paramString)
    throws XMPException
  {
    if ((paramString != null) && (paramString.length() != 0))
      return;
    throw new XMPException("Empty schema namespace URI", 4);
  }

  public static void assertSpecificLang(String paramString)
    throws XMPException
  {
    if ((paramString != null) && (paramString.length() != 0))
      return;
    throw new XMPException("Empty specific language", 4);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.ParameterAsserts
 * JD-Core Version:    0.5.4
 */