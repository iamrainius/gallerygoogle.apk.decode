package com.adobe.xmp.properties;

import com.adobe.xmp.options.AliasOptions;

public abstract interface XMPAliasInfo
{
  public abstract AliasOptions getAliasForm();

  public abstract String getNamespace();

  public abstract String getPrefix();

  public abstract String getPropName();
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.properties.XMPAliasInfo
 * JD-Core Version:    0.5.4
 */