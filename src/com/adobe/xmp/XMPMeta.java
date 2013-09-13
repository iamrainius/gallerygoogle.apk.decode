package com.adobe.xmp;

import com.adobe.xmp.options.PropertyOptions;
import java.util.Calendar;

public abstract interface XMPMeta extends Cloneable
{
  public abstract boolean doesPropertyExist(String paramString1, String paramString2);

  public abstract String getPacketHeader();

  public abstract Boolean getPropertyBoolean(String paramString1, String paramString2)
    throws XMPException;

  public abstract Calendar getPropertyCalendar(String paramString1, String paramString2)
    throws XMPException;

  public abstract Integer getPropertyInteger(String paramString1, String paramString2)
    throws XMPException;

  public abstract String getPropertyString(String paramString1, String paramString2)
    throws XMPException;

  public abstract void setLocalizedText(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, PropertyOptions paramPropertyOptions)
    throws XMPException;

  public abstract void setProperty(String paramString1, String paramString2, Object paramObject)
    throws XMPException;

  public abstract void setPropertyBoolean(String paramString1, String paramString2, boolean paramBoolean)
    throws XMPException;

  public abstract void setPropertyDate(String paramString1, String paramString2, XMPDateTime paramXMPDateTime)
    throws XMPException;

  public abstract void setPropertyDouble(String paramString1, String paramString2, double paramDouble)
    throws XMPException;

  public abstract void setPropertyInteger(String paramString1, String paramString2, int paramInt)
    throws XMPException;
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.XMPMeta
 * JD-Core Version:    0.5.4
 */