package com.google.android.apps.lightcycle.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import java.lang.reflect.Method;

public class UiUtil
{
  public static void lockCurrentScreenOrientation(Activity paramActivity)
  {
    Configuration localConfiguration = paramActivity.getResources().getConfiguration();
    Display localDisplay = paramActivity.getWindowManager().getDefaultDisplay();
    if (localConfiguration.orientation == 2)
      switch (localDisplay.getRotation())
      {
      default:
        paramActivity.setRequestedOrientation(0);
      case 2:
      case 3:
      }
    do
    {
      return;
      paramActivity.setRequestedOrientation(8);
      return;
    }
    while (localConfiguration.orientation != 1);
    switch (localDisplay.getRotation())
    {
    default:
      paramActivity.setRequestedOrientation(1);
      return;
    case 1:
    case 2:
    }
    paramActivity.setRequestedOrientation(9);
  }

  public static void setDisplayHomeAsUpEnabled(Activity paramActivity, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT >= 11);
    try
    {
      Method localMethod1 = Activity.class.getMethod("getActionBar", new Class[0]);
      Object localObject = localMethod1.invoke(paramActivity, new Object[0]);
      Class localClass = localMethod1.getReturnType();
      Class[] arrayOfClass = new Class[1];
      arrayOfClass[0] = Boolean.TYPE;
      Method localMethod2 = localClass.getMethod("setDisplayHomeAsUpEnabled", arrayOfClass);
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Boolean.valueOf(paramBoolean);
      localMethod2.invoke(localObject, arrayOfObject);
      return;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
  }

  @SuppressLint({"NewApi"})
  public static void switchSystemUiToLightsOut(Window paramWindow)
  {
    if (Build.VERSION.SDK_INT < 11)
      return;
    WindowManager.LayoutParams localLayoutParams = paramWindow.getAttributes();
    localLayoutParams.systemUiVisibility = 1;
    paramWindow.setAttributes(localLayoutParams);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.util.UiUtil
 * JD-Core Version:    0.5.4
 */