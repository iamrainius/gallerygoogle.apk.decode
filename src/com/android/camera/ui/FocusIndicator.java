package com.android.camera.ui;

public abstract interface FocusIndicator
{
  public abstract void clear();

  public abstract void showFail(boolean paramBoolean);

  public abstract void showStart();

  public abstract void showSuccess(boolean paramBoolean);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.FocusIndicator
 * JD-Core Version:    0.5.4
 */