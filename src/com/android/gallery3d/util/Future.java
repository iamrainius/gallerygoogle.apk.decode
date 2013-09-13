package com.android.gallery3d.util;

public abstract interface Future<T>
{
  public abstract void cancel();

  public abstract T get();

  public abstract boolean isCancelled();

  public abstract boolean isDone();

  public abstract void waitDone();
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.Future
 * JD-Core Version:    0.5.4
 */