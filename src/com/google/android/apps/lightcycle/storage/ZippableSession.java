package com.google.android.apps.lightcycle.storage;

import com.google.android.apps.lightcycle.util.Callback;
import java.io.File;

public abstract interface ZippableSession
{
  public abstract void saveAs(File paramFile, Callback<Boolean> paramCallback);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.storage.ZippableSession
 * JD-Core Version:    0.5.4
 */