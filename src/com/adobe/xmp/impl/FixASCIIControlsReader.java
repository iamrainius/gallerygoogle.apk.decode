package com.adobe.xmp.impl;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

public class FixASCIIControlsReader extends PushbackReader
{
  private int control = 0;
  private int digits = 0;
  private int state = 0;

  public FixASCIIControlsReader(Reader paramReader)
  {
    super(paramReader, 8);
  }

  private char processChar(char paramChar)
  {
    switch (this.state)
    {
    default:
    case 0:
      do
        return paramChar;
      while (paramChar != '&');
      this.state = 1;
      return paramChar;
    case 1:
      if (paramChar == '#')
      {
        this.state = 2;
        return paramChar;
      }
      this.state = 5;
      return paramChar;
    case 2:
      if (paramChar == 'x')
      {
        this.control = 0;
        this.digits = 0;
        this.state = 3;
        return paramChar;
      }
      if (('0' <= paramChar) && (paramChar <= '9'))
      {
        this.control = Character.digit(paramChar, 10);
        this.digits = 1;
        this.state = 4;
        return paramChar;
      }
      this.state = 5;
      return paramChar;
    case 4:
      if (('0' <= paramChar) && (paramChar <= '9'))
      {
        this.control = (10 * this.control + Character.digit(paramChar, 10));
        this.digits = (1 + this.digits);
        if (this.digits <= 5)
        {
          this.state = 4;
          return paramChar;
        }
        this.state = 5;
        return paramChar;
      }
      if ((paramChar == ';') && (Utils.isControlChar((char)this.control)))
      {
        this.state = 0;
        return (char)this.control;
      }
      this.state = 5;
      return paramChar;
    case 3:
      if ((('0' <= paramChar) && (paramChar <= '9')) || (('a' <= paramChar) && (paramChar <= 'f')) || (('A' <= paramChar) && (paramChar <= 'F')))
      {
        this.control = (16 * this.control + Character.digit(paramChar, 16));
        this.digits = (1 + this.digits);
        if (this.digits <= 4)
        {
          this.state = 3;
          return paramChar;
        }
        this.state = 5;
        return paramChar;
      }
      if ((paramChar == ';') && (Utils.isControlChar((char)this.control)))
      {
        this.state = 0;
        return (char)this.control;
      }
      this.state = 5;
      return paramChar;
    case 5:
    }
    this.state = 0;
    return paramChar;
  }

  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 0;
    int j = 0;
    char[] arrayOfChar = new char[8];
    int k = 1;
    int l = paramInt1;
    while ((k != 0) && (j < paramInt2))
    {
      label18: label45: int i1;
      if (super.read(arrayOfChar, i, 1) == 1)
      {
        k = 1;
        if (k == 0)
          break label150;
        char c = processChar(arrayOfChar[i]);
        if (this.state != 0)
          break label111;
        if (Utils.isControlChar(c))
          c = ' ';
        i1 = l + 1;
        paramArrayOfChar[l] = c;
        i = 0;
        ++j;
      }
      while (true)
      {
        l = i1;
        break label18:
        k = 0;
        break label45:
        if (this.state == 5)
        {
          label111: unread(arrayOfChar, 0, i + 1);
          i1 = l;
          i = 0;
        }
        ++i;
        i1 = l;
      }
      label150: if (i <= 0)
        continue;
      unread(arrayOfChar, 0, i);
      this.state = 5;
      k = 1;
      i = 0;
    }
    if ((j > 0) || (k != 0))
      return j;
    return -1;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.FixASCIIControlsReader
 * JD-Core Version:    0.5.4
 */