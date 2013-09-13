package com.android.gallery3d.ui;

import javax.microedition.khronos.opengles.GL11;

public class GLId
{
  static int sNextId = 1;

  public static void glDeleteBuffers(GL11 paramGL11, int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    monitorenter;
    try
    {
      paramGL11.glDeleteBuffers(paramInt1, paramArrayOfInt, paramInt2);
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public static void glDeleteTextures(GL11 paramGL11, int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    monitorenter;
    try
    {
      paramGL11.glDeleteTextures(paramInt1, paramArrayOfInt, paramInt2);
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public static void glGenBuffers(int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    monitorenter;
    int i = paramInt1;
    label5: int j = i - 1;
    int k;
    if (i > 0)
      k = paramInt2 + j;
    try
    {
      int l = sNextId;
      sNextId = l + 1;
      paramArrayOfInt[k] = l;
      i = j;
      break label5:
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public static void glGenTextures(int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    monitorenter;
    int i = paramInt1;
    label5: int j = i - 1;
    int k;
    if (i > 0)
      k = paramInt2 + j;
    try
    {
      int l = sNextId;
      sNextId = l + 1;
      paramArrayOfInt[k] = l;
      i = j;
      break label5:
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.GLId
 * JD-Core Version:    0.5.4
 */