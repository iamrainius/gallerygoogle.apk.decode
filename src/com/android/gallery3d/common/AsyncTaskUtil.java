package com.android.gallery3d.common;

import android.os.AsyncTask;
import android.os.Build.VERSION;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

public class AsyncTaskUtil
{
  private static Executor sExecutor;
  private static Method sMethodExecuteOnExecutor;

  static
  {
    if (Build.VERSION.SDK_INT >= 11);
    try
    {
      sExecutor = (Executor)AsyncTask.class.getField("THREAD_POOL_EXECUTOR").get(null);
      sMethodExecuteOnExecutor = AsyncTask.class.getMethod("executeOnExecutor", new Class[] { Executor.class, [Ljava.lang.Object.class });
      return;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      throw new RuntimeException(localNoSuchFieldException);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new RuntimeException(localNoSuchMethodException);
    }
  }

  public static <Param> void executeInParallel(AsyncTask<Param, ?, ?> paramAsyncTask, Param[] paramArrayOfParam)
  {
    if (Build.VERSION.SDK_INT < 11)
    {
      paramAsyncTask.execute(paramArrayOfParam);
      return;
    }
    try
    {
      Method localMethod = sMethodExecuteOnExecutor;
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = sExecutor;
      arrayOfObject[1] = paramArrayOfParam;
      localMethod.invoke(paramAsyncTask, arrayOfObject);
      return;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new RuntimeException(localInvocationTargetException);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.common.AsyncTaskUtil
 * JD-Core Version:    0.5.4
 */