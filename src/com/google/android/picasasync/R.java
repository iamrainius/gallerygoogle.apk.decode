package com.google.android.picasasync;

import android.util.Log;
import com.android.gallery3d.common.Utils;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public final class R
{
  private static void copyStaticMembers(Class<?> paramClass1, Class<?> paramClass2)
  {
    Field[] arrayOfField = paramClass1.getDeclaredFields();
    int i = arrayOfField.length;
    int j = 0;
    while (j < i)
    {
      Field localField1 = arrayOfField[j];
      try
      {
        Field localField2 = paramClass2.getDeclaredField(localField1.getName());
        Utils.assertTrue(Modifier.isStatic(localField2.getModifiers()));
        localField1.set(null, localField2.get(null));
        ++j;
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        throw new AssertionError("resource not found: " + localField1.getName());
      }
      catch (Exception localException)
      {
        Log.w("PicasaSync.R", "fail to set resource", localException);
        throw new AssertionError("cannot set resource : " + localField1.getName());
      }
    }
  }

  public static void init(Class<?> paramClass)
  {
    HashMap localHashMap = new HashMap();
    for (Class localClass3 : paramClass.getDeclaredClasses())
      localHashMap.put(localClass3.getSimpleName(), localClass3);
    for (Class localClass1 : R.class.getDeclaredClasses())
    {
      String str = localClass1.getSimpleName();
      Class localClass2 = (Class)localHashMap.get(str);
      if (localClass2 == null)
        throw new AssertionError("resource not found: " + str);
      copyStaticMembers(localClass1, localClass2);
    }
  }

  public static final class id
  {
    public static int icon;
    public static int ps_progress;
    public static int ps_status;
    public static int ps_title;
  }

  public static final class layout
  {
    public static int ps_cache_notification;
  }

  public static final class string
  {
    public static int ps_cache_done;
    public static int ps_cache_done_title;
    public static int ps_cache_status;
    public static int ps_cache_status_title;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.R
 * JD-Core Version:    0.5.4
 */