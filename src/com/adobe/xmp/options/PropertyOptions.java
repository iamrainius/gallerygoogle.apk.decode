package com.adobe.xmp.options;

import com.adobe.xmp.XMPException;

public final class PropertyOptions extends Options
{
  public PropertyOptions()
  {
  }

  public PropertyOptions(int paramInt)
    throws XMPException
  {
    super(paramInt);
  }

  public void assertConsistency(int paramInt)
    throws XMPException
  {
    if (((paramInt & 0x100) > 0) && ((paramInt & 0x200) > 0))
      throw new XMPException("IsStruct and IsArray options are mutually exclusive", 103);
    if (((paramInt & 0x2) <= 0) || ((paramInt & 0x300) <= 0))
      return;
    throw new XMPException("Structs and arrays can't have \"value\" options", 103);
  }

  public boolean getHasLanguage()
  {
    return getOption(64);
  }

  protected int getValidOptions()
  {
    return -2147475470;
  }

  public boolean isArray()
  {
    return getOption(512);
  }

  public boolean isArrayAltText()
  {
    return getOption(4096);
  }

  public boolean isArrayAlternate()
  {
    return getOption(2048);
  }

  public boolean isArrayOrdered()
  {
    return getOption(1024);
  }

  public boolean isCompositeProperty()
  {
    return (0x300 & getOptions()) > 0;
  }

  public boolean isQualifier()
  {
    return getOption(32);
  }

  public boolean isSchemaNode()
  {
    return getOption(-2147483648);
  }

  public boolean isSimple()
  {
    return (0x300 & getOptions()) == 0;
  }

  public boolean isStruct()
  {
    return getOption(256);
  }

  public boolean isURI()
  {
    return getOption(2);
  }

  public void mergeWith(PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    if (paramPropertyOptions == null)
      return;
    setOptions(getOptions() | paramPropertyOptions.getOptions());
  }

  public PropertyOptions setArray(boolean paramBoolean)
  {
    setOption(512, paramBoolean);
    return this;
  }

  public PropertyOptions setArrayAltText(boolean paramBoolean)
  {
    setOption(4096, paramBoolean);
    return this;
  }

  public PropertyOptions setArrayAlternate(boolean paramBoolean)
  {
    setOption(2048, paramBoolean);
    return this;
  }

  public PropertyOptions setArrayOrdered(boolean paramBoolean)
  {
    setOption(1024, paramBoolean);
    return this;
  }

  public PropertyOptions setHasLanguage(boolean paramBoolean)
  {
    setOption(64, paramBoolean);
    return this;
  }

  public PropertyOptions setHasQualifiers(boolean paramBoolean)
  {
    setOption(16, paramBoolean);
    return this;
  }

  public PropertyOptions setHasType(boolean paramBoolean)
  {
    setOption(128, paramBoolean);
    return this;
  }

  public PropertyOptions setQualifier(boolean paramBoolean)
  {
    setOption(32, paramBoolean);
    return this;
  }

  public PropertyOptions setSchemaNode(boolean paramBoolean)
  {
    setOption(-2147483648, paramBoolean);
    return this;
  }

  public PropertyOptions setStruct(boolean paramBoolean)
  {
    setOption(256, paramBoolean);
    return this;
  }

  public PropertyOptions setURI(boolean paramBoolean)
  {
    setOption(2, paramBoolean);
    return this;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.options.PropertyOptions
 * JD-Core Version:    0.5.4
 */