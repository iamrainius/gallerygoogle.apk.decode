package com.android.gallery3d.data;

import android.net.Uri;
import com.android.gallery3d.app.GalleryApp;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChangeNotifier
{
  private AtomicBoolean mContentDirty = new AtomicBoolean(true);
  private MediaSet mMediaSet;

  public ChangeNotifier(MediaSet paramMediaSet, Uri paramUri, GalleryApp paramGalleryApp)
  {
    this.mMediaSet = paramMediaSet;
    paramGalleryApp.getDataManager().registerChangeNotifier(paramUri, this);
  }

  public ChangeNotifier(MediaSet paramMediaSet, Uri[] paramArrayOfUri, GalleryApp paramGalleryApp)
  {
    this.mMediaSet = paramMediaSet;
    for (int i = 0; i < paramArrayOfUri.length; ++i)
      paramGalleryApp.getDataManager().registerChangeNotifier(paramArrayOfUri[i], this);
  }

  public void fakeChange()
  {
    onChange(false);
  }

  public boolean isDirty()
  {
    return this.mContentDirty.compareAndSet(true, false);
  }

  protected void onChange(boolean paramBoolean)
  {
    if (!this.mContentDirty.compareAndSet(false, true))
      return;
    this.mMediaSet.notifyContentChanged();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.ChangeNotifier
 * JD-Core Version:    0.5.4
 */