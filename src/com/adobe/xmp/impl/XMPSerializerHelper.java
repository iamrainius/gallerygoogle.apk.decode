package com.adobe.xmp.impl;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.options.SerializeOptions;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class XMPSerializerHelper
{
  public static void serialize(XMPMetaImpl paramXMPMetaImpl, OutputStream paramOutputStream, SerializeOptions paramSerializeOptions)
    throws XMPException
  {
    if (paramSerializeOptions != null);
    while (true)
    {
      if (paramSerializeOptions.getSort())
        paramXMPMetaImpl.sort();
      new XMPSerializerRDF().serialize(paramXMPMetaImpl, paramOutputStream, paramSerializeOptions);
      return;
      paramSerializeOptions = new SerializeOptions();
    }
  }

  public static byte[] serializeToBuffer(XMPMetaImpl paramXMPMetaImpl, SerializeOptions paramSerializeOptions)
    throws XMPException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(2048);
    serialize(paramXMPMetaImpl, localByteArrayOutputStream, paramSerializeOptions);
    return localByteArrayOutputStream.toByteArray();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.XMPSerializerHelper
 * JD-Core Version:    0.5.4
 */