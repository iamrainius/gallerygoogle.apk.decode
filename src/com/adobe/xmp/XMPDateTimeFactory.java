package com.adobe.xmp;

import com.adobe.xmp.impl.XMPDateTimeImpl;
import java.util.Calendar;
import java.util.TimeZone;

public final class XMPDateTimeFactory
{
  private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

  public static XMPDateTime createFromCalendar(Calendar paramCalendar)
  {
    return new XMPDateTimeImpl(paramCalendar);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.XMPDateTimeFactory
 * JD-Core Version:    0.5.4
 */