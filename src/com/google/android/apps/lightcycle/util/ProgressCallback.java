package com.google.android.apps.lightcycle.util;

public abstract interface ProgressCallback<T>
{
  public abstract void onDone(T paramT);

  public abstract void onNewProgressMessage(String paramString);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.util.ProgressCallback
 * JD-Core Version:    0.5.4
 */