package com.google.android.picasasync;

import com.android.gallery3d.common.Utils;
import java.util.HashSet;

class SyncLockManager
{
  private final HashSet<SyncLock> mLocks = new HashSet();

  public SyncLock acquireLock(int paramInt, Object paramObject)
    throws InterruptedException
  {
    SyncLock localSyncLock;
    synchronized (this.mLocks)
    {
      localSyncLock = new SyncLock(paramInt, paramObject, null);
      if (!this.mLocks.add(localSyncLock))
        this.mLocks.wait();
    }
    monitorexit;
    return localSyncLock;
  }

  public class SyncLock
  {
    private Object mKey;
    private int mType;

    private SyncLock(int paramObject, Object arg3)
    {
      this.mType = paramObject;
      Object localObject;
      this.mKey = localObject;
    }

    public boolean equals(Object paramObject)
    {
      if ((paramObject == null) || (!paramObject instanceof SyncLock));
      SyncLock localSyncLock;
      do
      {
        return false;
        localSyncLock = (SyncLock)paramObject;
      }
      while ((localSyncLock.mType != this.mType) || (!localSyncLock.mKey.equals(this.mKey)));
      return true;
    }

    public int hashCode()
    {
      return this.mType ^ this.mKey.hashCode();
    }

    public void unlock()
    {
      synchronized (SyncLockManager.this.mLocks)
      {
        Utils.assertTrue(SyncLockManager.this.mLocks.remove(this));
        SyncLockManager.this.mLocks.notifyAll();
        return;
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.SyncLockManager
 * JD-Core Version:    0.5.4
 */