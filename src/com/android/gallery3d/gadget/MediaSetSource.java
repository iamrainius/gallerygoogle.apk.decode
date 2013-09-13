package com.android.gallery3d.gadget;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.ContentListener;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaSet;
import java.util.ArrayList;
import java.util.Arrays;

public class MediaSetSource
  implements ContentListener, WidgetSource
{
  private MediaItem[] mCache = new MediaItem[32];
  private int mCacheEnd;
  private int mCacheStart;
  private ContentListener mContentListener;
  private MediaSet mSource;
  private long mSourceVersion = -1L;

  public MediaSetSource(MediaSet paramMediaSet)
  {
    this.mSource = ((MediaSet)Utils.checkNotNull(paramMediaSet));
    this.mSource.addContentListener(this);
  }

  private void ensureCacheRange(int paramInt)
  {
    if ((paramInt >= this.mCacheStart) && (paramInt < this.mCacheEnd))
      return;
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mCacheStart = paramInt;
      ArrayList localArrayList = this.mSource.getMediaItem(this.mCacheStart, 32);
      this.mCacheEnd = (this.mCacheStart + localArrayList.size());
      localArrayList.toArray(this.mCache);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }

  public void close()
  {
    this.mSource.removeContentListener(this);
  }

  public Uri getContentUri(int paramInt)
  {
    monitorenter;
    Object localObject2;
    label33: Uri localUri;
    try
    {
      ensureCacheRange(paramInt);
      if (paramInt >= this.mCacheStart)
      {
        int i = this.mCacheEnd;
        if (paramInt < i)
          break label33;
      }
      localObject2 = null;
      return localObject2;
      localUri = this.mCache[(paramInt - this.mCacheStart)].getContentUri();
    }
    finally
    {
      monitorexit;
    }
  }

  public Bitmap getImage(int paramInt)
  {
    monitorenter;
    Object localObject2;
    label33: Bitmap localBitmap;
    try
    {
      ensureCacheRange(paramInt);
      if (paramInt >= this.mCacheStart)
      {
        int i = this.mCacheEnd;
        if (paramInt < i)
          break label33;
      }
      localObject2 = null;
      return localObject2;
      localBitmap = WidgetUtils.createWidgetBitmap(this.mCache[(paramInt - this.mCacheStart)]);
    }
    finally
    {
      monitorexit;
    }
  }

  public void onContentDirty()
  {
    if (this.mContentListener == null)
      return;
    this.mContentListener.onContentDirty();
  }

  public void reload()
  {
    long l = this.mSource.reload();
    if (this.mSourceVersion == l)
      return;
    this.mSourceVersion = l;
    this.mCacheStart = 0;
    this.mCacheEnd = 0;
    Arrays.fill(this.mCache, null);
  }

  public void setContentListener(ContentListener paramContentListener)
  {
    this.mContentListener = paramContentListener;
  }

  public int size()
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      int i = this.mSource.getMediaItemCount();
      return i;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.gadget.MediaSetSource
 * JD-Core Version:    0.5.4
 */