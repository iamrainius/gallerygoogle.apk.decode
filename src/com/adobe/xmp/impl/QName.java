package com.adobe.xmp.impl;

public class QName
{
  private String localName;
  private String prefix;

  public QName(String paramString)
  {
    int i = paramString.indexOf(':');
    if (i >= 0)
    {
      this.prefix = paramString.substring(0, i);
      this.localName = paramString.substring(i + 1);
      return;
    }
    this.prefix = "";
    this.localName = paramString;
  }

  public String getPrefix()
  {
    return this.prefix;
  }

  public boolean hasPrefix()
  {
    return (this.prefix != null) && (this.prefix.length() > 0);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.QName
 * JD-Core Version:    0.5.4
 */