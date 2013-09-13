package com.adobe.xmp.options;

import com.adobe.xmp.XMPException;
import java.util.Map;

public abstract class Options
{
  private Map optionNames = null;
  private int options = 0;

  public Options()
  {
  }

  public Options(int paramInt)
    throws XMPException
  {
    assertOptionsValid(paramInt);
    setOptions(paramInt);
  }

  private void assertOptionsValid(int paramInt)
    throws XMPException
  {
    int i = paramInt & (0xFFFFFFFF ^ getValidOptions());
    if (i == 0)
    {
      assertConsistency(paramInt);
      return;
    }
    throw new XMPException("The option bit(s) 0x" + Integer.toHexString(i) + " are invalid!", 103);
  }

  protected void assertConsistency(int paramInt)
    throws XMPException
  {
  }

  public boolean equals(Object paramObject)
  {
    return getOptions() == ((Options)paramObject).getOptions();
  }

  protected boolean getOption(int paramInt)
  {
    return (paramInt & this.options) != 0;
  }

  public int getOptions()
  {
    return this.options;
  }

  protected abstract int getValidOptions();

  public int hashCode()
  {
    return getOptions();
  }

  public void setOption(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean);
    for (int i = paramInt | this.options; ; i = this.options & (paramInt ^ 0xFFFFFFFF))
    {
      this.options = i;
      return;
    }
  }

  public void setOptions(int paramInt)
    throws XMPException
  {
    assertOptionsValid(paramInt);
    this.options = paramInt;
  }

  public String toString()
  {
    return "0x" + Integer.toHexString(this.options);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.options.Options
 * JD-Core Version:    0.5.4
 */