package com.android.gallery3d.data;

import android.graphics.Bitmap;
import com.android.gallery3d.common.Utils;
import java.util.ArrayList;

public class BitmapPool
{
  private final int mHeight;
  private final boolean mOneSize;
  private final ArrayList<Bitmap> mPool;
  private final int mPoolLimit;
  private final int mWidth;

  public BitmapPool(int paramInt)
  {
    this.mWidth = -1;
    this.mHeight = -1;
    this.mPoolLimit = paramInt;
    this.mPool = new ArrayList(paramInt);
    this.mOneSize = false;
  }

  public BitmapPool(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    this.mPoolLimit = paramInt3;
    this.mPool = new ArrayList(paramInt3);
    this.mOneSize = true;
  }

  public void clear()
  {
    monitorenter;
    try
    {
      this.mPool.clear();
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

  public Bitmap getBitmap()
  {
    monitorenter;
    Bitmap localBitmap;
    try
    {
      Utils.assertTrue(this.mOneSize);
      int i = this.mPool.size();
      if (i > 0)
      {
        localBitmap = (Bitmap)this.mPool.remove(i - 1);
        return localBitmap;
      }
    }
    finally
    {
      monitorexit;
    }
  }

  public Bitmap getBitmap(int paramInt1, int paramInt2)
  {
    monitorenter;
    label12: Bitmap localBitmap2;
    try
    {
      boolean bool;
      if (!this.mOneSize)
      {
        bool = true;
        Utils.assertTrue(bool);
      }
      for (int i = -1 + this.mPool.size(); i >= 0; --i)
      {
        Bitmap localBitmap1 = (Bitmap)this.mPool.get(i);
        if ((localBitmap1.getWidth() != paramInt1) || (localBitmap1.getHeight() != paramInt2))
          continue;
        localBitmap2 = (Bitmap)this.mPool.remove(i);
        return localBitmap2;
        bool = false;
        break label12:
      }
    }
    finally
    {
      monitorexit;
    }
  }

  public boolean isOneSize()
  {
    return this.mOneSize;
  }

  public void recycle(Bitmap paramBitmap)
  {
    if (paramBitmap == null)
      return;
    if ((this.mOneSize) && (((paramBitmap.getWidth() != this.mWidth) || (paramBitmap.getHeight() != this.mHeight))))
    {
      paramBitmap.recycle();
      return;
    }
    monitorenter;
    try
    {
      if (this.mPool.size() >= this.mPoolLimit)
        this.mPool.remove(0);
      this.mPool.add(paramBitmap);
      return;
    }
    finally
    {
      monitorexit;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.BitmapPool
 * JD-Core Version:    0.5.4
 */