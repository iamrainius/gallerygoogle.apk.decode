package android.support.v4.content;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build.VERSION;

public class IntentCompat
{
  private static final IntentCompatImpl IMPL;

  static
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 15)
    {
      IMPL = new IntentCompatImplIcsMr1();
      return;
    }
    if (i >= 11)
    {
      IMPL = new IntentCompatImplHC();
      return;
    }
    IMPL = new IntentCompatImplBase();
  }

  public static Intent makeMainActivity(ComponentName paramComponentName)
  {
    return IMPL.makeMainActivity(paramComponentName);
  }

  static abstract interface IntentCompatImpl
  {
    public abstract Intent makeMainActivity(ComponentName paramComponentName);
  }

  static class IntentCompatImplBase
    implements IntentCompat.IntentCompatImpl
  {
    public Intent makeMainActivity(ComponentName paramComponentName)
    {
      Intent localIntent = new Intent("android.intent.action.MAIN");
      localIntent.setComponent(paramComponentName);
      localIntent.addCategory("android.intent.category.LAUNCHER");
      return localIntent;
    }
  }

  static class IntentCompatImplHC extends IntentCompat.IntentCompatImplBase
  {
    public Intent makeMainActivity(ComponentName paramComponentName)
    {
      return IntentCompatHoneycomb.makeMainActivity(paramComponentName);
    }
  }

  static class IntentCompatImplIcsMr1 extends IntentCompat.IntentCompatImplHC
  {
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     android.support.v4.content.IntentCompat
 * JD-Core Version:    0.5.4
 */