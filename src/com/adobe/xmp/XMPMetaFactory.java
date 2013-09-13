package com.adobe.xmp;

import com.adobe.xmp.impl.XMPMetaImpl;
import com.adobe.xmp.impl.XMPMetaParser;
import com.adobe.xmp.impl.XMPSchemaRegistryImpl;
import com.adobe.xmp.impl.XMPSerializerHelper;
import com.adobe.xmp.options.ParseOptions;
import com.adobe.xmp.options.SerializeOptions;
import java.io.PrintStream;

public final class XMPMetaFactory
{
  private static XMPSchemaRegistry schema = new XMPSchemaRegistryImpl();
  private static XMPVersionInfo versionInfo = null;

  private static void assertImplementation(XMPMeta paramXMPMeta)
  {
    if (paramXMPMeta instanceof XMPMetaImpl)
      return;
    throw new UnsupportedOperationException("The serializing service works onlywith the XMPMeta implementation of this library");
  }

  public static XMPMeta create()
  {
    return new XMPMetaImpl();
  }

  public static XMPSchemaRegistry getSchemaRegistry()
  {
    return schema;
  }

  public static XMPVersionInfo getVersionInfo()
  {
    monitorenter;
    try
    {
      XMPVersionInfo localXMPVersionInfo1;
      label21: if (localXMPVersionInfo1 != null);
    }
    finally
    {
      try
      {
        versionInfo = new XMPVersionInfo()
        {
          public String getMessage()
          {
            return "Adobe XMP Core 5.1.0-jc003";
          }

          public String toString()
          {
            return "Adobe XMP Core 5.1.0-jc003";
          }
        };
        XMPVersionInfo localXMPVersionInfo2 = versionInfo;
        monitorexit;
        return localXMPVersionInfo2;
      }
      catch (Throwable localThrowable)
      {
        System.out.println(localThrowable);
        break label21:
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }
  }

  public static XMPMeta parseFromBuffer(byte[] paramArrayOfByte)
    throws XMPException
  {
    return parseFromBuffer(paramArrayOfByte, null);
  }

  public static XMPMeta parseFromBuffer(byte[] paramArrayOfByte, ParseOptions paramParseOptions)
    throws XMPException
  {
    return XMPMetaParser.parse(paramArrayOfByte, paramParseOptions);
  }

  public static byte[] serializeToBuffer(XMPMeta paramXMPMeta, SerializeOptions paramSerializeOptions)
    throws XMPException
  {
    assertImplementation(paramXMPMeta);
    return XMPSerializerHelper.serializeToBuffer((XMPMetaImpl)paramXMPMeta, paramSerializeOptions);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.XMPMetaFactory
 * JD-Core Version:    0.5.4
 */