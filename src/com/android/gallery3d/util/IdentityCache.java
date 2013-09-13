package com.android.gallery3d.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class IdentityCache<K, V>
{
  private ReferenceQueue<V> mQueue = new ReferenceQueue();
  private final HashMap<K, Entry<K, V>> mWeakMap = new HashMap();

  private void cleanUpWeakMap()
  {
    for (Entry localEntry = (Entry)this.mQueue.poll(); localEntry != null; localEntry = (Entry)this.mQueue.poll())
      this.mWeakMap.remove(localEntry.mKey);
  }

  public V get(K paramK)
  {
    monitorenter;
    Object localObject3;
    Object localObject2;
    try
    {
      cleanUpWeakMap();
      Entry localEntry = (Entry)this.mWeakMap.get(paramK);
      if (localEntry == null)
      {
        localObject3 = null;
        return localObject3;
      }
      localObject2 = localEntry.get();
    }
    finally
    {
      monitorexit;
    }
  }

  public V put(K paramK, V paramV)
  {
    monitorenter;
    Object localObject3;
    Object localObject2;
    try
    {
      cleanUpWeakMap();
      Entry localEntry = (Entry)this.mWeakMap.put(paramK, new Entry(paramK, paramV, this.mQueue));
      if (localEntry == null)
      {
        localObject3 = null;
        return localObject3;
      }
      localObject2 = localEntry.get();
    }
    finally
    {
      monitorexit;
    }
  }

  private static class Entry<K, V> extends WeakReference<V>
  {
    K mKey;

    public Entry(K paramK, V paramV, ReferenceQueue<V> paramReferenceQueue)
    {
      super(paramV, paramReferenceQueue);
      this.mKey = paramK;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.IdentityCache
 * JD-Core Version:    0.5.4
 */