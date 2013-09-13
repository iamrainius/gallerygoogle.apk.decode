package com.android.camera;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PanoUtil
{
  public static String createName(String paramString, long paramLong)
  {
    Date localDate = new Date(paramLong);
    return new SimpleDateFormat(paramString).format(localDate);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.PanoUtil
 * JD-Core Version:    0.5.4
 */