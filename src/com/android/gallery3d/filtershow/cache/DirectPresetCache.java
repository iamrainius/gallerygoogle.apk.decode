package com.android.gallery3d.filtershow.cache;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import com.android.gallery3d.filtershow.FilterShowActivity;
import com.android.gallery3d.filtershow.imageshow.ImageShow;
import com.android.gallery3d.filtershow.presets.ImagePreset;
import java.util.Vector;

public class DirectPresetCache
  implements Cache
{
  private final Bitmap.Config mBitmapConfig = Bitmap.Config.ARGB_8888;
  private final Vector<CachedPreset> mCache = new Vector();
  private int mCacheSize = 1;
  private long mGlobalAge = 0L;
  private ImageLoader mLoader = null;
  private final Runnable mNotifyObserversRunnable = new Runnable()
  {
    public void run()
    {
      for (int i = 0; i < DirectPresetCache.this.mObservers.size(); ++i)
      {
        ImageShow localImageShow = (ImageShow)DirectPresetCache.this.mObservers.elementAt(i);
        localImageShow.invalidate();
        localImageShow.updateImage();
      }
    }
  };
  private final Vector<ImageShow> mObservers = new Vector();
  private Bitmap mOriginalBitmap = null;

  public DirectPresetCache(ImageLoader paramImageLoader, int paramInt)
  {
    this.mLoader = paramImageLoader;
    this.mCacheSize = paramInt;
  }

  private CachedPreset getCachedPreset(ImagePreset paramImagePreset)
  {
    for (int i = 0; i < this.mCache.size(); ++i)
    {
      CachedPreset localCachedPreset = (CachedPreset)this.mCache.elementAt(i);
      if (localCachedPreset.mPreset == paramImagePreset)
        return localCachedPreset;
    }
    return null;
  }

  private CachedPreset getOldestCachedPreset()
  {
    Object localObject = null;
    int i = 0;
    if (i < this.mCache.size())
    {
      label4: CachedPreset localCachedPreset = (CachedPreset)this.mCache.elementAt(i);
      if (localCachedPreset.mBusy);
      while (true)
      {
        ++i;
        break label4:
        if (localObject == null)
          localObject = localCachedPreset;
        if (localObject.mAge <= localCachedPreset.mAge)
          continue;
        localObject = localCachedPreset;
      }
    }
    return localObject;
  }

  public void addObserver(ImageShow paramImageShow)
  {
    if (this.mObservers.contains(paramImageShow))
      return;
    this.mObservers.add(paramImageShow);
  }

  protected void compute(CachedPreset paramCachedPreset)
  {
    CachedPreset.access$302(paramCachedPreset, null);
    CachedPreset.access$302(paramCachedPreset, this.mOriginalBitmap.copy(this.mBitmapConfig, true));
    float f = paramCachedPreset.mBitmap.getWidth() / this.mLoader.getOriginalBounds().width();
    if (f < 1.0F)
      paramCachedPreset.mPreset.setIsHighQuality(false);
    paramCachedPreset.mPreset.setScaleFactor(f);
    CachedPreset.access$302(paramCachedPreset, paramCachedPreset.mPreset.apply(paramCachedPreset.mBitmap));
    long l = this.mGlobalAge;
    this.mGlobalAge = (1L + l);
    CachedPreset.access$402(paramCachedPreset, l);
  }

  protected void didCompute(CachedPreset paramCachedPreset)
  {
    CachedPreset.access$202(paramCachedPreset, false);
    notifyObservers();
  }

  public Bitmap get(ImagePreset paramImagePreset)
  {
    CachedPreset localCachedPreset = getCachedPreset(paramImagePreset);
    if ((localCachedPreset != null) && (!localCachedPreset.mBusy))
      return localCachedPreset.mBitmap;
    return null;
  }

  public void notifyObservers()
  {
    this.mLoader.getActivity().runOnUiThread(this.mNotifyObserversRunnable);
  }

  public void prepare(ImagePreset paramImagePreset)
  {
    CachedPreset localCachedPreset = getCachedPreset(paramImagePreset);
    if ((localCachedPreset == null) || ((localCachedPreset.mBitmap == null) && (!localCachedPreset.mBusy)))
      if (localCachedPreset == null)
      {
        if (this.mCache.size() >= this.mCacheSize)
          break label76;
        localCachedPreset = new CachedPreset();
        this.mCache.add(localCachedPreset);
      }
    while (true)
    {
      if (localCachedPreset != null)
      {
        CachedPreset.access$102(localCachedPreset, paramImagePreset);
        willCompute(localCachedPreset);
      }
      return;
      label76: localCachedPreset = getOldestCachedPreset();
    }
  }

  public void reset(ImagePreset paramImagePreset)
  {
    CachedPreset localCachedPreset = getCachedPreset(paramImagePreset);
    if ((localCachedPreset == null) || (localCachedPreset.mBusy))
      return;
    CachedPreset.access$302(localCachedPreset, null);
    willCompute(localCachedPreset);
  }

  public void setOriginalBitmap(Bitmap paramBitmap)
  {
    this.mOriginalBitmap = paramBitmap;
    notifyObservers();
  }

  protected void willCompute(CachedPreset paramCachedPreset)
  {
    if (paramCachedPreset == null)
      return;
    CachedPreset.access$202(paramCachedPreset, true);
    compute(paramCachedPreset);
    didCompute(paramCachedPreset);
  }

  protected class CachedPreset
  {
    private long mAge = 0L;
    private Bitmap mBitmap = null;
    private boolean mBusy = false;
    private ImagePreset mPreset = null;

    protected CachedPreset()
    {
    }

    public void setBusy(boolean paramBoolean)
    {
      this.mBusy = paramBoolean;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.cache.DirectPresetCache
 * JD-Core Version:    0.5.4
 */