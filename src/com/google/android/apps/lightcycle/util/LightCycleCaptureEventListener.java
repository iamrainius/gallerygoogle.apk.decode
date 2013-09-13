package com.google.android.apps.lightcycle.util;

public abstract interface LightCycleCaptureEventListener
{
  public abstract void onDoneButtonVisibilityChanged(boolean paramBoolean);

  public abstract void onPhotoTaken();

  public abstract void onUndoButtonStatusChanged(boolean paramBoolean);

  public abstract void onUndoButtonVisibilityChanged(boolean paramBoolean);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.util.LightCycleCaptureEventListener
 * JD-Core Version:    0.5.4
 */