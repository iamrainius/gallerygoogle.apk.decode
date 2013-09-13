package com.adobe.xmp;

import com.adobe.xmp.properties.XMPAliasInfo;

public abstract interface XMPSchemaRegistry
{
  public abstract XMPAliasInfo findAlias(String paramString);

  public abstract String getNamespacePrefix(String paramString);

  public abstract String getNamespaceURI(String paramString);

  public abstract String registerNamespace(String paramString1, String paramString2)
    throws XMPException;
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.XMPSchemaRegistry
 * JD-Core Version:    0.5.4
 */