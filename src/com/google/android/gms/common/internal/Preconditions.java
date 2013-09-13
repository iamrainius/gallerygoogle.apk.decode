package com.google.android.gms.common.internal;

public final class Preconditions
{
  private Preconditions()
  {
    throw new AssertionError("Uninstantiable");
  }

  public static <T> T checkNotNull(T paramT)
  {
    if (paramT == null)
      throw new NullPointerException("null reference");
    return paramT;
  }

  public static void checkState(boolean paramBoolean)
  {
    if (paramBoolean)
      return;
    throw new IllegalStateException();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.gms.common.internal.Preconditions
 * JD-Core Version:    0.5.4
 */