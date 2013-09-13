package com.google.android.picasastore;

import android.util.Log;
import java.lang.reflect.Method;

public final class SystemProperties
{
  private static final Method sGetLongMethod;

  static
  {
    Method localMethod1;
    try
    {
      Class localClass = Class.forName("android.os.SystemProperties");
      Class[] arrayOfClass = new Class[2];
      arrayOfClass[0] = String.class;
      arrayOfClass[1] = Long.TYPE;
      Method localMethod2 = localClass.getMethod("getLong", arrayOfClass);
      localMethod1 = localMethod2;
      sGetLongMethod = localMethod1;
      return;
    }
    catch (Exception localException)
    {
      Log.e("SystemProperties", "initialize error", localException);
      localMethod1 = null;
    }
  }

  public static long getLong(String paramString, long paramLong)
  {
    try
    {
      if (sGetLongMethod != null)
      {
        Method localMethod = sGetLongMethod;
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = paramString;
        arrayOfObject[1] = Long.valueOf(paramLong);
        long l = ((Long)localMethod.invoke(null, arrayOfObject)).longValue();
        paramLong = l;
      }
      return paramLong;
    }
    catch (Exception localException)
    {
      Log.e("SystemProperties", "get error", localException);
    }
    return paramLong;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasastore.SystemProperties
 * JD-Core Version:    0.5.4
 */