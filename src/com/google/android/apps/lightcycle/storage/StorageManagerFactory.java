package com.google.android.apps.lightcycle.storage;

public class StorageManagerFactory
{
  public static StorageManager getStorageManager()
  {
    return new LocalFileStorageManager();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.storage.StorageManagerFactory
 * JD-Core Version:    0.5.4
 */