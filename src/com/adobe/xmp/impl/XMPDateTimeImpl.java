package com.adobe.xmp.impl;

import com.adobe.xmp.XMPDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class XMPDateTimeImpl
  implements XMPDateTime
{
  private int day = 0;
  private int hour = 0;
  private int minute = 0;
  private int month = 0;
  private int nanoSeconds;
  private int second = 0;
  private TimeZone timeZone = TimeZone.getTimeZone("UTC");
  private int year = 0;

  public XMPDateTimeImpl()
  {
  }

  public XMPDateTimeImpl(Calendar paramCalendar)
  {
    Date localDate = paramCalendar.getTime();
    TimeZone localTimeZone = paramCalendar.getTimeZone();
    GregorianCalendar localGregorianCalendar = (GregorianCalendar)Calendar.getInstance(Locale.US);
    localGregorianCalendar.setGregorianChange(new Date(-9223372036854775808L));
    localGregorianCalendar.setTimeZone(localTimeZone);
    localGregorianCalendar.setTime(localDate);
    this.year = localGregorianCalendar.get(1);
    this.month = (1 + localGregorianCalendar.get(2));
    this.day = localGregorianCalendar.get(5);
    this.hour = localGregorianCalendar.get(11);
    this.minute = localGregorianCalendar.get(12);
    this.second = localGregorianCalendar.get(13);
    this.nanoSeconds = (1000000 * localGregorianCalendar.get(14));
    this.timeZone = localGregorianCalendar.getTimeZone();
  }

  public XMPDateTimeImpl(Date paramDate, TimeZone paramTimeZone)
  {
    GregorianCalendar localGregorianCalendar = new GregorianCalendar(paramTimeZone);
    localGregorianCalendar.setTime(paramDate);
    this.year = localGregorianCalendar.get(1);
    this.month = (1 + localGregorianCalendar.get(2));
    this.day = localGregorianCalendar.get(5);
    this.hour = localGregorianCalendar.get(11);
    this.minute = localGregorianCalendar.get(12);
    this.second = localGregorianCalendar.get(13);
    this.nanoSeconds = (1000000 * localGregorianCalendar.get(14));
    this.timeZone = paramTimeZone;
  }

  public int compareTo(Object paramObject)
  {
    long l = getCalendar().getTimeInMillis() - ((XMPDateTime)paramObject).getCalendar().getTimeInMillis();
    if (l != 0L)
      return (int)(l % 2L);
    return (int)((this.nanoSeconds - ((XMPDateTime)paramObject).getNanoSecond()) % 2L);
  }

  public Calendar getCalendar()
  {
    GregorianCalendar localGregorianCalendar = (GregorianCalendar)Calendar.getInstance(Locale.US);
    localGregorianCalendar.setGregorianChange(new Date(-9223372036854775808L));
    localGregorianCalendar.setTimeZone(this.timeZone);
    localGregorianCalendar.set(1, this.year);
    localGregorianCalendar.set(2, -1 + this.month);
    localGregorianCalendar.set(5, this.day);
    localGregorianCalendar.set(11, this.hour);
    localGregorianCalendar.set(12, this.minute);
    localGregorianCalendar.set(13, this.second);
    localGregorianCalendar.set(14, this.nanoSeconds / 1000000);
    return localGregorianCalendar;
  }

  public int getDay()
  {
    return this.day;
  }

  public int getHour()
  {
    return this.hour;
  }

  public String getISO8601String()
  {
    return ISO8601Converter.render(this);
  }

  public int getMinute()
  {
    return this.minute;
  }

  public int getMonth()
  {
    return this.month;
  }

  public int getNanoSecond()
  {
    return this.nanoSeconds;
  }

  public int getSecond()
  {
    return this.second;
  }

  public TimeZone getTimeZone()
  {
    return this.timeZone;
  }

  public int getYear()
  {
    return this.year;
  }

  public void setDay(int paramInt)
  {
    if (paramInt < 1)
    {
      this.day = 1;
      return;
    }
    if (paramInt > 31)
    {
      this.day = 31;
      return;
    }
    this.day = paramInt;
  }

  public void setHour(int paramInt)
  {
    this.hour = Math.min(Math.abs(paramInt), 23);
  }

  public void setMinute(int paramInt)
  {
    this.minute = Math.min(Math.abs(paramInt), 59);
  }

  public void setMonth(int paramInt)
  {
    if (paramInt < 1)
    {
      this.month = 1;
      return;
    }
    if (paramInt > 12)
    {
      this.month = 12;
      return;
    }
    this.month = paramInt;
  }

  public void setNanoSecond(int paramInt)
  {
    this.nanoSeconds = paramInt;
  }

  public void setSecond(int paramInt)
  {
    this.second = Math.min(Math.abs(paramInt), 59);
  }

  public void setTimeZone(TimeZone paramTimeZone)
  {
    this.timeZone = paramTimeZone;
  }

  public void setYear(int paramInt)
  {
    this.year = Math.min(Math.abs(paramInt), 9999);
  }

  public String toString()
  {
    return getISO8601String();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.XMPDateTimeImpl
 * JD-Core Version:    0.5.4
 */