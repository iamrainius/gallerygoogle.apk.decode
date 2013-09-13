package com.android.gallery3d.data;

import java.util.concurrent.atomic.AtomicBoolean;

public class SnailAlbum extends SingleItemAlbum
{
  private AtomicBoolean mDirty = new AtomicBoolean(false);

  public SnailAlbum(Path paramPath, SnailItem paramSnailItem)
  {
    super(paramPath, paramSnailItem);
  }

  public void notifyChange()
  {
    this.mDirty.set(true);
    notifyContentChanged();
  }

  public long reload()
  {
    if (this.mDirty.compareAndSet(true, false))
    {
      ((SnailItem)getItem()).updateVersion();
      this.mDataVersion = nextVersionNumber();
    }
    return this.mDataVersion;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.SnailAlbum
 * JD-Core Version:    0.5.4
 */