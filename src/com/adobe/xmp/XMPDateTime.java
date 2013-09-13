package com.adobe.xmp;

import java.util.Calendar;
import java.util.TimeZone;

public abstract interface XMPDateTime extends Comparable
{
  public abstract Calendar getCalendar();

  public abstract int getDay();

  public abstract int getHour();

  public abstract int getMinute();

  public abstract int getMonth();

  public abstract int getNanoSecond();

  public abstract int getSecond();

  public abstract TimeZone getTimeZone();

  public abstract int getYear();

  public abstract void setDay(int paramInt);

  public abstract void setHour(int paramInt);

  public abstract void setMinute(int paramInt);

  public abstract void setMonth(int paramInt);

  public abstract void setNanoSecond(int paramInt);

  public abstract void setSecond(int paramInt);

  public abstract void setTimeZone(TimeZone paramTimeZone);

  public abstract void setYear(int paramInt);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.XMPDateTime
 * JD-Core Version:    0.5.4
 */