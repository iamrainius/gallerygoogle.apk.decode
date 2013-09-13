package com.google.android.apps.lightcycle.util;

import android.content.Context;

public class Utils
{
  public static boolean isDogfoodApp(Context paramContext)
  {
    return paramContext.getPackageName().equals("com.google.android.apps.lightcycle");
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.util.Utils
 * JD-Core Version:    0.5.4
 */