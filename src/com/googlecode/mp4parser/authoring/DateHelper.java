package com.googlecode.mp4parser.authoring;

import java.util.Date;

public class DateHelper
{
  public static long convert(Date paramDate)
  {
    return 2082844800L + paramDate.getTime() / 1000L;
  }

  public static Date convert(long paramLong)
  {
    return new Date(1000L * (paramLong - 2082844800L));
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.authoring.DateHelper
 * JD-Core Version:    0.5.4
 */