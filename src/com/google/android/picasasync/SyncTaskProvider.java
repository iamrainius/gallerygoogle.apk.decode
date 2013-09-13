package com.google.android.picasasync;

import java.util.Collection;

public abstract interface SyncTaskProvider
{
  public abstract void collectTasks(Collection<SyncTask> paramCollection);

  public abstract void onSyncStart();

  public abstract void resetSyncStates();
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.SyncTaskProvider
 * JD-Core Version:    0.5.4
 */