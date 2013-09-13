package com.adobe.xmp.impl;

import com.adobe.xmp.XMPException;

class ParseState
{
  private int pos = 0;
  private String str;

  public ParseState(String paramString)
  {
    this.str = paramString;
  }

  public char ch()
  {
    if (this.pos < this.str.length())
      return this.str.charAt(this.pos);
    return '\000';
  }

  public char ch(int paramInt)
  {
    if (paramInt < this.str.length())
      return this.str.charAt(paramInt);
    return '\000';
  }

  public int gatherInt(String paramString, int paramInt)
    throws XMPException
  {
    int i = 0;
    int j = 0;
    for (int k = ch(this.pos); (48 <= k) && (k <= 57); k = ch(this.pos))
    {
      i = i * 10 + (k - 48);
      j = 1;
      this.pos = (1 + this.pos);
    }
    if (j != 0)
    {
      if (i > paramInt)
        return paramInt;
      if (i < 0)
        return 0;
      return i;
    }
    throw new XMPException(paramString, 5);
  }

  public boolean hasNext()
  {
    return this.pos < this.str.length();
  }

  public int length()
  {
    return this.str.length();
  }

  public int pos()
  {
    return this.pos;
  }

  public void skip()
  {
    this.pos = (1 + this.pos);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.ParseState
 * JD-Core Version:    0.5.4
 */