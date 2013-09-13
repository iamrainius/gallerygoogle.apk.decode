package com.android.gallery3d.common;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class LruCache<K, V>
{
  private final HashMap<K, V> mLruMap;
  private ReferenceQueue<V> mQueue = new ReferenceQueue();
  private final HashMap<K, Entry<K, V>> mWeakMap = new HashMap();

  public LruCache(int paramInt)
  {
    this.mLruMap = new LinkedHashMap(16, 0.75F, true, paramInt)
    {
      protected boolean removeEldestEntry(Map.Entry<K, V> paramEntry)
      {
        return size() > this.val$capacity;
      }
    };
  }

  private void cleanUpWeakMap()
  {
    for (Entry localEntry = (Entry)this.mQueue.poll(); localEntry != null; localEntry = (Entry)this.mQueue.poll())
      this.mWeakMap.remove(localEntry.mKey);
  }

  public boolean containsKey(K paramK)
  {
    monitorenter;
    try
    {
      cleanUpWeakMap();
      boolean bool = this.mWeakMap.containsKey(paramK);
      monitorexit;
      return bool;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public V get(K paramK)
  {
    monitorenter;
    while (true)
    {
      Object localObject4;
      Object localObject5;
      try
      {
        cleanUpWeakMap();
        Object localObject2 = this.mLruMap.get(paramK);
        localObject3 = localObject2;
        if (localObject3 != null)
          return localObject3;
        Entry localEntry = (Entry)this.mWeakMap.get(paramK);
        if (localEntry == null)
          localObject4 = null;
        else
          localObject5 = localEntry.get();
      }
      finally
      {
        monitorexit;
      }
      Object localObject3 = localObject4;
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
      this.mLruMap.put(paramK, paramV);
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
 * Qualified Name:     com.android.gallery3d.common.LruCache
 * JD-Core Version:    0.5.4
 */