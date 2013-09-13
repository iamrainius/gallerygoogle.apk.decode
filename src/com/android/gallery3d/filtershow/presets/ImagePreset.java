package com.android.gallery3d.filtershow.presets;

import android.graphics.Bitmap;
import android.util.Log;
import com.android.gallery3d.filtershow.ImageStateAdapter;
import com.android.gallery3d.filtershow.cache.ImageLoader;
import com.android.gallery3d.filtershow.filters.ImageFilter;
import com.android.gallery3d.filtershow.imageshow.GeometryMetadata;
import com.android.gallery3d.filtershow.imageshow.ImageShow;
import java.util.Iterator;
import java.util.Vector;

public class ImagePreset
{
  private boolean mDoApplyFilters = true;
  private boolean mDoApplyGeometry = true;
  private ImageShow mEndPoint = null;
  protected Vector<ImageFilter> mFilters = new Vector();
  public final GeometryMetadata mGeoData = new GeometryMetadata();
  private String mHistoryName = "Original";
  private ImageFilter mImageBorder = null;
  private ImageLoader mImageLoader = null;
  protected boolean mIsFxPreset = false;
  private boolean mIsHighQuality = false;
  protected String mName = "Original";
  private float mScaleFactor = 1.0F;

  public ImagePreset()
  {
    setup();
  }

  public ImagePreset(ImagePreset paramImagePreset)
  {
    while (true)
    {
      try
      {
        if (paramImagePreset.mImageBorder == null)
          break label216;
        this.mImageBorder = paramImagePreset.mImageBorder.clone();
        break label216:
        if (i >= paramImagePreset.mFilters.size())
          break label172;
        ImageFilter localImageFilter = ((ImageFilter)paramImagePreset.mFilters.elementAt(i)).clone();
        localImageFilter.setImagePreset(this);
        add(localImageFilter);
        label172: ++i;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        Log.v("ImagePreset", "Exception trying to clone: " + localCloneNotSupportedException);
        this.mName = paramImagePreset.name();
        this.mHistoryName = paramImagePreset.name();
        this.mIsFxPreset = paramImagePreset.isFx();
        this.mImageLoader = paramImagePreset.getImageLoader();
        this.mGeoData.set(paramImagePreset.mGeoData);
        return;
      }
      label216: int i = 0;
    }
  }

  public ImagePreset(String paramString)
  {
    setHistoryName(paramString);
    setup();
  }

  private void setBorder(ImageFilter paramImageFilter)
  {
    this.mImageBorder = paramImageFilter;
  }

  public void add(ImageFilter paramImageFilter)
  {
    if (paramImageFilter.getFilterType() == 1)
    {
      setHistoryName(paramImageFilter.getName());
      setBorder(paramImageFilter);
    }
    while (true)
    {
      paramImageFilter.setImagePreset(this);
      return;
      if (paramImageFilter.getFilterType() == 2)
      {
        int i = 0;
        int j = 0;
        if (j < this.mFilters.size())
        {
          label40: int k = ((ImageFilter)this.mFilters.get(j)).getFilterType();
          if ((i != 0) && (k != 4))
            this.mFilters.remove(j);
          while (true)
          {
            ++j;
            break label40:
            if (k != 2)
              continue;
            this.mFilters.remove(j);
            this.mFilters.add(j, paramImageFilter);
            setHistoryName(paramImageFilter.getName());
            i = 1;
          }
        }
        if (i != 0)
          continue;
        this.mFilters.add(paramImageFilter);
        setHistoryName(paramImageFilter.getName());
      }
      this.mFilters.add(paramImageFilter);
      setHistoryName(paramImageFilter.getName());
    }
  }

  public Bitmap apply(Bitmap paramBitmap)
  {
    Bitmap localBitmap = paramBitmap;
    if (this.mDoApplyGeometry)
      localBitmap = this.mGeoData.apply(paramBitmap, this.mScaleFactor, this.mIsHighQuality);
    if (this.mDoApplyFilters)
      for (int i = 0; i < this.mFilters.size(); ++i)
        localBitmap = ((ImageFilter)this.mFilters.elementAt(i)).apply(localBitmap, this.mScaleFactor, this.mIsHighQuality);
    if (this.mImageBorder != null)
      localBitmap = this.mImageBorder.apply(localBitmap, this.mScaleFactor, this.mIsHighQuality);
    if (this.mEndPoint != null)
      this.mEndPoint.updateFilteredImage(localBitmap);
    return localBitmap;
  }

  public void fillImageStateAdapter(ImageStateAdapter paramImageStateAdapter)
  {
    if (paramImageStateAdapter == null)
      return;
    paramImageStateAdapter.clear();
    paramImageStateAdapter.addAll(this.mFilters);
    paramImageStateAdapter.notifyDataSetChanged();
  }

  public ImageFilter getFilter(String paramString)
  {
    for (int i = 0; i < this.mFilters.size(); ++i)
    {
      ImageFilter localImageFilter = (ImageFilter)this.mFilters.elementAt(i);
      if (localImageFilter.getName().equalsIgnoreCase(paramString))
        return localImageFilter;
    }
    return null;
  }

  public ImageLoader getImageLoader()
  {
    return this.mImageLoader;
  }

  public float getScaleFactor()
  {
    return this.mScaleFactor;
  }

  public boolean hasModifications()
  {
    if ((this.mImageBorder != null) && (!this.mImageBorder.isNil()));
    do
      return true;
    while (this.mGeoData.hasModifications());
    for (int i = 0; i < this.mFilters.size(); ++i)
      if (!((ImageFilter)this.mFilters.elementAt(i)).isNil());
    return false;
  }

  public String historyName()
  {
    return this.mHistoryName;
  }

  public boolean isFx()
  {
    return this.mIsFxPreset;
  }

  public boolean isPanoramaSafe()
  {
    if ((this.mImageBorder != null) && (!this.mImageBorder.isNil()));
    do
      return false;
    while (this.mGeoData.hasModifications());
    Iterator localIterator = this.mFilters.iterator();
    while (localIterator.hasNext())
    {
      ImageFilter localImageFilter = (ImageFilter)localIterator.next();
      if ((localImageFilter.getFilterType() == 4) && (!localImageFilter.isNil()))
        return false;
    }
    return true;
  }

  public String name()
  {
    return this.mName;
  }

  public boolean same(ImagePreset paramImagePreset)
  {
    if (paramImagePreset.mFilters.size() != this.mFilters.size());
    do
      return false;
    while ((!this.mName.equalsIgnoreCase(paramImagePreset.name())) || (this.mDoApplyGeometry != paramImagePreset.mDoApplyGeometry) || ((this.mDoApplyGeometry) && (!this.mGeoData.equals(paramImagePreset.mGeoData))) || (this.mImageBorder != paramImagePreset.mImageBorder) || ((this.mImageBorder != null) && (!this.mImageBorder.same(paramImagePreset.mImageBorder))) || ((this.mDoApplyFilters != paramImagePreset.mDoApplyFilters) && (((this.mFilters.size() > 0) || (paramImagePreset.mFilters.size() > 0)))));
    if ((this.mDoApplyFilters) && (paramImagePreset.mDoApplyFilters))
      for (int i = 0; i < paramImagePreset.mFilters.size(); ++i)
        if (!((ImageFilter)paramImagePreset.mFilters.elementAt(i)).same((ImageFilter)this.mFilters.elementAt(i)));
    return true;
  }

  public void setDoApplyFilters(boolean paramBoolean)
  {
    this.mDoApplyFilters = paramBoolean;
  }

  public void setDoApplyGeometry(boolean paramBoolean)
  {
    this.mDoApplyGeometry = paramBoolean;
  }

  public void setEndpoint(ImageShow paramImageShow)
  {
    this.mEndPoint = paramImageShow;
  }

  public void setGeometry(GeometryMetadata paramGeometryMetadata)
  {
    this.mGeoData.set(paramGeometryMetadata);
  }

  public void setHistoryName(String paramString)
  {
    this.mHistoryName = paramString;
  }

  public void setImageLoader(ImageLoader paramImageLoader)
  {
    this.mImageLoader = paramImageLoader;
  }

  public void setIsFx(boolean paramBoolean)
  {
    this.mIsFxPreset = paramBoolean;
  }

  public void setIsHighQuality(boolean paramBoolean)
  {
    this.mIsHighQuality = paramBoolean;
  }

  public void setName(String paramString)
  {
    this.mName = paramString;
    this.mHistoryName = paramString;
  }

  public void setScaleFactor(float paramFloat)
  {
    this.mScaleFactor = paramFloat;
  }

  public void setup()
  {
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.presets.ImagePreset
 * JD-Core Version:    0.5.4
 */