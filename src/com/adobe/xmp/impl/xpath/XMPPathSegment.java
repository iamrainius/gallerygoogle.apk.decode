package com.adobe.xmp.impl.xpath;

public class XMPPathSegment
{
  private boolean alias;
  private int aliasForm;
  private int kind;
  private String name;

  public XMPPathSegment(String paramString, int paramInt)
  {
    this.name = paramString;
    this.kind = paramInt;
  }

  public int getAliasForm()
  {
    return this.aliasForm;
  }

  public int getKind()
  {
    return this.kind;
  }

  public String getName()
  {
    return this.name;
  }

  public boolean isAlias()
  {
    return this.alias;
  }

  public void setAlias(boolean paramBoolean)
  {
    this.alias = paramBoolean;
  }

  public void setAliasForm(int paramInt)
  {
    this.aliasForm = paramInt;
  }

  public void setKind(int paramInt)
  {
    this.kind = paramInt;
  }

  public void setName(String paramString)
  {
    this.name = paramString;
  }

  public String toString()
  {
    switch (this.kind)
    {
    default:
      return this.name;
    case 1:
    case 2:
    case 3:
    case 4:
      return this.name;
    case 5:
    case 6:
    }
    return this.name;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.xpath.XMPPathSegment
 * JD-Core Version:    0.5.4
 */