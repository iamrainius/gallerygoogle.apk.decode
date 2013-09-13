package com.googlecode.mp4parser.util;

public class CastUtils
{
  public static int l2i(long paramLong)
  {
    if ((paramLong > 2147483647L) || (paramLong < -2147483648L))
      throw new RuntimeException("A cast to int has gone wrong. Please contact the mp4parser discussion group (" + paramLong + ")");
    return (int)paramLong;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.util.CastUtils
 * JD-Core Version:    0.5.4
 */