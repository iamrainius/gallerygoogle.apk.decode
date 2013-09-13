package com.google.android.apps.lightcycle.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.lang.ref.SoftReference;
import java.util.Map;

public class LoadThumbnailTask extends AsyncTask<Void, Integer, Bitmap>
{
  private final Map<String, SoftReference<Bitmap>> cache;
  private final String pathName;
  private final ImageView view;

  public LoadThumbnailTask(String paramString, ImageView paramImageView, Map<String, SoftReference<Bitmap>> paramMap)
  {
    this.pathName = paramString;
    this.view = paramImageView;
    this.cache = paramMap;
  }

  private static Bitmap loadThumbnailFromFile(String paramString)
  {
    monitorenter;
    try
    {
      Bitmap localBitmap = BitmapFactory.decodeFile(paramString);
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

  protected Bitmap doInBackground(Void[] paramArrayOfVoid)
  {
    if ((!this.cache.containsKey(this.pathName)) || (((SoftReference)this.cache.get(this.pathName)).get() == null))
    {
      Bitmap localBitmap = loadThumbnailFromFile(this.pathName);
      this.cache.put(this.pathName, new SoftReference(localBitmap));
      return localBitmap;
    }
    return (Bitmap)((SoftReference)this.cache.get(this.pathName)).get();
  }

  protected void onPostExecute(Bitmap paramBitmap)
  {
    if (!this.pathName.equals(this.view.getTag(2131558401)))
      return;
    if (paramBitmap == null)
    {
      this.view.setScaleType(ImageView.ScaleType.CENTER);
      this.view.setImageResource(17301597);
      return;
    }
    this.view.setScaleType(ImageView.ScaleType.CENTER_CROP);
    this.view.setImageBitmap(paramBitmap);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.gallery.LoadThumbnailTask
 * JD-Core Version:    0.5.4
 */