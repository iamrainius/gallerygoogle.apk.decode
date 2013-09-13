package com.android.gallery3d.app;

import java.util.HashMap;

public class TransitionStore
{
  private HashMap<Object, Object> mStorage = new HashMap();

  public void clear()
  {
    this.mStorage.clear();
  }

  public <T> T get(Object paramObject)
  {
    return this.mStorage.get(paramObject);
  }

  public <T> T get(Object paramObject, T paramT)
  {
    Object localObject = this.mStorage.get(paramObject);
    if (localObject == null)
      return paramT;
    return localObject;
  }

  public void put(Object paramObject1, Object paramObject2)
  {
    this.mStorage.put(paramObject1, paramObject2);
  }

  public <T> void putIfNotPresent(Object paramObject, T paramT)
  {
    this.mStorage.put(paramObject, get(paramObject, paramT));
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.TransitionStore
 * JD-Core Version:    0.5.4
 */