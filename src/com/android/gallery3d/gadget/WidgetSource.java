package com.android.gallery3d.gadget;

import android.graphics.Bitmap;
import android.net.Uri;
import com.android.gallery3d.data.ContentListener;

public abstract interface WidgetSource
{
  public abstract void close();

  public abstract Uri getContentUri(int paramInt);

  public abstract Bitmap getImage(int paramInt);

  public abstract void reload();

  public abstract void setContentListener(ContentListener paramContentListener);

  public abstract int size();
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.gadget.WidgetSource
 * JD-Core Version:    0.5.4
 */