package com.adobe.xmp.options;

import com.adobe.xmp.XMPException;

public final class AliasOptions extends Options
{
  public AliasOptions()
  {
  }

  public AliasOptions(int paramInt)
    throws XMPException
  {
    super(paramInt);
  }

  protected int getValidOptions()
  {
    return 7680;
  }

  public boolean isArray()
  {
    return getOption(512);
  }

  public boolean isArrayAltText()
  {
    return getOption(4096);
  }

  public boolean isSimple()
  {
    return getOptions() == 0;
  }

  public AliasOptions setArrayAltText(boolean paramBoolean)
  {
    setOption(7680, paramBoolean);
    return this;
  }

  public AliasOptions setArrayOrdered(boolean paramBoolean)
  {
    setOption(1536, paramBoolean);
    return this;
  }

  public PropertyOptions toPropertyOptions()
    throws XMPException
  {
    return new PropertyOptions(getOptions());
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.options.AliasOptions
 * JD-Core Version:    0.5.4
 */