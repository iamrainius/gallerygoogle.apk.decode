package com.adobe.xmp.impl;

import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public final class ISO8601Converter
{
  public static XMPDateTime parse(String paramString)
    throws XMPException
  {
    return parse(paramString, new XMPDateTimeImpl());
  }

  public static XMPDateTime parse(String paramString, XMPDateTime paramXMPDateTime)
    throws XMPException
  {
    ParameterAsserts.assertNotNull(paramString);
    ParseState localParseState = new ParseState(paramString);
    if ((localParseState.ch(0) == 'T') || ((localParseState.length() >= 2) && (localParseState.ch(1) == ':')) || ((localParseState.length() >= 3) && (localParseState.ch(2) == ':')));
    int i7;
    for (int i = 1; ; i = 0)
    {
      if (i != 0)
        break label309;
      if (localParseState.ch(0) == '-')
        localParseState.skip();
      i7 = localParseState.gatherInt("Invalid year in date string", 9999);
      if ((!localParseState.hasNext()) || (localParseState.ch() == '-'))
        break;
      throw new XMPException("Invalid date string, after year", 5);
    }
    if (localParseState.ch(0) == '-')
      i7 = -i7;
    paramXMPDateTime.setYear(i7);
    if (!localParseState.hasNext());
    do
    {
      do
      {
        return paramXMPDateTime;
        localParseState.skip();
        int i8 = localParseState.gatherInt("Invalid month in date string", 12);
        if ((localParseState.hasNext()) && (localParseState.ch() != '-'))
          throw new XMPException("Invalid date string, after month", 5);
        paramXMPDateTime.setMonth(i8);
      }
      while (!localParseState.hasNext());
      localParseState.skip();
      int i9 = localParseState.gatherInt("Invalid day in date string", 31);
      if ((localParseState.hasNext()) && (localParseState.ch() != 'T'))
        throw new XMPException("Invalid date string, after day", 5);
      paramXMPDateTime.setDay(i9);
    }
    while (!localParseState.hasNext());
    if (localParseState.ch() == 'T')
      label266: localParseState.skip();
    int j;
    do
    {
      j = localParseState.gatherInt("Invalid hour in date string", 23);
      if (localParseState.ch() == ':')
        break label341;
      throw new XMPException("Invalid date string, after hour", 5);
      label309: paramXMPDateTime.setMonth(1);
      paramXMPDateTime.setDay(1);
      break label266:
    }
    while (i != 0);
    throw new XMPException("Invalid date string, missing 'T' after date", 5);
    label341: paramXMPDateTime.setHour(j);
    localParseState.skip();
    int k = localParseState.gatherInt("Invalid minute in date string", 59);
    if ((localParseState.hasNext()) && (localParseState.ch() != ':') && (localParseState.ch() != 'Z') && (localParseState.ch() != '+') && (localParseState.ch() != '-'))
      throw new XMPException("Invalid date string, after minute", 5);
    paramXMPDateTime.setMinute(k);
    if (localParseState.ch() == ':')
    {
      localParseState.skip();
      int i3 = localParseState.gatherInt("Invalid whole seconds in date string", 59);
      if ((localParseState.hasNext()) && (localParseState.ch() != '.') && (localParseState.ch() != 'Z') && (localParseState.ch() != '+') && (localParseState.ch() != '-'))
        throw new XMPException("Invalid date string, after whole seconds", 5);
      paramXMPDateTime.setSecond(i3);
      if (localParseState.ch() == '.')
      {
        localParseState.skip();
        int i4 = localParseState.pos();
        int i5 = localParseState.gatherInt("Invalid fractional seconds in date string", 999999999);
        if ((localParseState.ch() != 'Z') && (localParseState.ch() != '+') && (localParseState.ch() != '-'))
          throw new XMPException("Invalid date string, after fractional second", 5);
        for (int i6 = localParseState.pos() - i4; i6 > 9; --i6)
          i5 /= 10;
        while (i6 < 9)
        {
          i5 *= 10;
          ++i6;
        }
        paramXMPDateTime.setNanoSecond(i5);
      }
    }
    int l = 0;
    int i1 = 0;
    int i2 = 0;
    if (localParseState.ch() == 'Z')
      localParseState.skip();
    while (true)
    {
      paramXMPDateTime.setTimeZone(new SimpleTimeZone(l * (1000 * (i1 * 3600) + 1000 * (i2 * 60)), ""));
      if (localParseState.hasNext());
      throw new XMPException("Invalid date string, extra chars at end", 5);
      boolean bool = localParseState.hasNext();
      i1 = 0;
      i2 = 0;
      l = 0;
      if (!bool)
        continue;
      if (localParseState.ch() == '+');
      for (l = 1; ; l = -1)
      {
        localParseState.skip();
        i1 = localParseState.gatherInt("Invalid time zone hour in date string", 23);
        if (localParseState.ch() == ':')
          break label804;
        throw new XMPException("Invalid date string, after time zone hour", 5);
        if (localParseState.ch() != '-')
          break;
      }
      throw new XMPException("Time zone must begin with 'Z', '+', or '-'", 5);
      label804: localParseState.skip();
      i2 = localParseState.gatherInt("Invalid time zone minute in date string", 59);
    }
  }

  public static String render(XMPDateTime paramXMPDateTime)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    DecimalFormat localDecimalFormat = new DecimalFormat("0000", new DecimalFormatSymbols(Locale.ENGLISH));
    localStringBuffer.append(localDecimalFormat.format(paramXMPDateTime.getYear()));
    if (paramXMPDateTime.getMonth() == 0)
      return localStringBuffer.toString();
    localDecimalFormat.applyPattern("'-'00");
    localStringBuffer.append(localDecimalFormat.format(paramXMPDateTime.getMonth()));
    if (paramXMPDateTime.getDay() == 0)
      return localStringBuffer.toString();
    localStringBuffer.append(localDecimalFormat.format(paramXMPDateTime.getDay()));
    int i;
    if ((paramXMPDateTime.getHour() != 0) || (paramXMPDateTime.getMinute() != 0) || (paramXMPDateTime.getSecond() != 0) || (paramXMPDateTime.getNanoSecond() != 0) || ((paramXMPDateTime.getTimeZone() != null) && (paramXMPDateTime.getTimeZone().getRawOffset() != 0)))
    {
      localStringBuffer.append('T');
      localDecimalFormat.applyPattern("00");
      localStringBuffer.append(localDecimalFormat.format(paramXMPDateTime.getHour()));
      localStringBuffer.append(':');
      localStringBuffer.append(localDecimalFormat.format(paramXMPDateTime.getMinute()));
      if ((paramXMPDateTime.getSecond() != 0) || (paramXMPDateTime.getNanoSecond() != 0))
      {
        double d = paramXMPDateTime.getSecond() + paramXMPDateTime.getNanoSecond() / 1000000000.0D;
        localDecimalFormat.applyPattern(":00.#########");
        localStringBuffer.append(localDecimalFormat.format(d));
      }
      if (paramXMPDateTime.getTimeZone() != null)
      {
        long l = paramXMPDateTime.getCalendar().getTimeInMillis();
        i = paramXMPDateTime.getTimeZone().getOffset(l);
        if (i != 0)
          break label325;
        localStringBuffer.append('Z');
      }
    }
    while (true)
    {
      return localStringBuffer.toString();
      label325: int j = i / 3600000;
      int k = Math.abs(i % 3600000 / 60000);
      localDecimalFormat.applyPattern("+00;-00");
      localStringBuffer.append(localDecimalFormat.format(j));
      localDecimalFormat.applyPattern(":00");
      localStringBuffer.append(localDecimalFormat.format(k));
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.ISO8601Converter
 * JD-Core Version:    0.5.4
 */