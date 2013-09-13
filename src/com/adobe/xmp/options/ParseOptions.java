package com.adobe.xmp.options;

public final class ParseOptions extends Options
{
  public ParseOptions()
  {
    setOption(24, true);
  }

  public boolean getAcceptLatin1()
  {
    return getOption(16);
  }

  public boolean getFixControlChars()
  {
    return getOption(8);
  }

  public boolean getOmitNormalization()
  {
    return getOption(32);
  }

  public boolean getRequireXMPMeta()
  {
    return getOption(1);
  }

  public boolean getStrictAliasing()
  {
    return getOption(4);
  }

  protected int getValidOptions()
  {
    return 61;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.options.ParseOptions
 * JD-Core Version:    0.5.4
 */