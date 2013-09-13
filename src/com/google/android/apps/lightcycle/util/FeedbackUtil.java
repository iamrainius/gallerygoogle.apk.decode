package com.google.android.apps.lightcycle.util;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class FeedbackUtil
{
  private static final String TAG = FeedbackUtil.class.getSimpleName();

  public void startFeedback(Activity paramActivity, View paramView)
  {
    try
    {
      Class localClass1 = Class.forName("com.google.userfeedback.android.api.UserFeedbackSpec");
      Object localObject = localClass1.getConstructor(new Class[] { Activity.class, View.class, String.class, String.class }).newInstance(new Object[] { paramActivity, paramView, "AndroidRuntime:V LightCycle:* *:S", "com.google.android.apps.lightcycle.USER_INITIATED_FEEDBACK_REPORT" });
      Class localClass2 = Class.forName("com.google.userfeedback.android.api.UserFeedback");
      Constructor localConstructor = localClass2.getConstructor(new Class[0]);
      localClass2.getMethod("startFeedback", new Class[] { localClass1 }).invoke(localConstructor.newInstance(new Object[0]), new Object[] { localObject });
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.e(TAG, "Unable to launch feedback activity.", localThrowable);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.util.FeedbackUtil
 * JD-Core Version:    0.5.4
 */