package com.adobe.xmp.options;

import com.adobe.xmp.XMPException;

public final class SerializeOptions extends Options
{
  private int baseIndent = 0;
  private String indent = "  ";
  private String newline = "\n";
  private boolean omitVersionAttribute = false;
  private int padding = 2048;

  public SerializeOptions()
  {
  }

  public SerializeOptions(int paramInt)
    throws XMPException
  {
    super(paramInt);
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    try
    {
      SerializeOptions localSerializeOptions = new SerializeOptions(getOptions());
      localSerializeOptions.setBaseIndent(this.baseIndent);
      localSerializeOptions.setIndent(this.indent);
      localSerializeOptions.setNewline(this.newline);
      localSerializeOptions.setPadding(this.padding);
      return localSerializeOptions;
    }
    catch (XMPException localXMPException)
    {
    }
    return null;
  }

  public int getBaseIndent()
  {
    return this.baseIndent;
  }

  public boolean getEncodeUTF16BE()
  {
    return (0x3 & getOptions()) == 2;
  }

  public boolean getEncodeUTF16LE()
  {
    return (0x3 & getOptions()) == 3;
  }

  public String getEncoding()
  {
    if (getEncodeUTF16BE())
      return "UTF-16BE";
    if (getEncodeUTF16LE())
      return "UTF-16LE";
    return "UTF-8";
  }

  public boolean getExactPacketLength()
  {
    return getOption(512);
  }

  public boolean getIncludeThumbnailPad()
  {
    return getOption(256);
  }

  public String getIndent()
  {
    return this.indent;
  }

  public String getNewline()
  {
    return this.newline;
  }

  public boolean getOmitPacketWrapper()
  {
    return getOption(16);
  }

  public boolean getOmitVersionAttribute()
  {
    return this.omitVersionAttribute;
  }

  public int getPadding()
  {
    return this.padding;
  }

  public boolean getReadOnlyPacket()
  {
    return getOption(32);
  }

  public boolean getSort()
  {
    return getOption(4096);
  }

  public boolean getUseCompactFormat()
  {
    return getOption(64);
  }

  protected int getValidOptions()
  {
    return 4976;
  }

  public SerializeOptions setBaseIndent(int paramInt)
  {
    this.baseIndent = paramInt;
    return this;
  }

  public SerializeOptions setIndent(String paramString)
  {
    this.indent = paramString;
    return this;
  }

  public SerializeOptions setNewline(String paramString)
  {
    this.newline = paramString;
    return this;
  }

  public SerializeOptions setOmitPacketWrapper(boolean paramBoolean)
  {
    setOption(16, paramBoolean);
    return this;
  }

  public SerializeOptions setPadding(int paramInt)
  {
    this.padding = paramInt;
    return this;
  }

  public SerializeOptions setUseCompactFormat(boolean paramBoolean)
  {
    setOption(64, paramBoolean);
    return this;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.options.SerializeOptions
 * JD-Core Version:    0.5.4
 */