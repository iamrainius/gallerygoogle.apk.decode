package com.android.gallery3d.ui;

import android.graphics.Bitmap;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;

public abstract class BitmapLoader
  implements FutureListener<Bitmap>
{
  private Bitmap mBitmap;
  private int mState = 0;
  private Future<Bitmap> mTask;

  public void cancelLoad()
  {
    monitorenter;
    try
    {
      if (this.mState == 1)
      {
        this.mState = 0;
        if (this.mTask != null)
          this.mTask.cancel();
      }
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
    try
    {
      Bitmap localBitmap = this.mBitmap;
      monitorexit;
      return localBitmap;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public boolean isRequestInProgress()
  {
    int i = 1;
    monitorenter;
    try
    {
      int j = this.mState;
      if (j == i)
        return i;
      i = 0;
    }
    finally
    {
      monitorexit;
    }
  }

  public void onFutureDone(Future<Bitmap> paramFuture)
  {
    monitorenter;
    try
    {
      this.mTask = null;
      this.mBitmap = ((Bitmap)paramFuture.get());
      if (this.mState == 4)
      {
        if (this.mBitmap != null)
        {
          recycleBitmap(this.mBitmap);
          this.mBitmap = null;
        }
        return;
      }
      if ((paramFuture.isCancelled()) && (this.mBitmap == null))
      {
        if (this.mState == 1)
          this.mTask = submitBitmapTask(this);
        return;
      }
    }
    finally
    {
      monitorexit;
    }
    if (this.mBitmap == null);
    for (int i = 3; ; i = 2)
    {
      this.mState = i;
      monitorexit;
      onLoadComplete(this.mBitmap);
      return;
    }
  }

  protected abstract void onLoadComplete(Bitmap paramBitmap);

  public void recycle()
  {
    monitorenter;
    try
    {
      this.mState = 4;
      if (this.mBitmap != null)
      {
        recycleBitmap(this.mBitmap);
        this.mBitmap = null;
      }
      if (this.mTask != null)
        this.mTask.cancel();
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  protected abstract void recycleBitmap(Bitmap paramBitmap);

  public void startLoad()
  {
    monitorenter;
    try
    {
      if (this.mState == 0)
      {
        this.mState = 1;
        if (this.mTask == null)
          this.mTask = submitBitmapTask(this);
      }
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

  protected abstract Future<Bitmap> submitBitmapTask(FutureListener<Bitmap> paramFutureListener);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.BitmapLoader
 * JD-Core Version:    0.5.4
 */