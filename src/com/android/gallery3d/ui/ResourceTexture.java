package com.android.gallery3d.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import com.android.gallery3d.common.Utils;

public class ResourceTexture extends UploadedTexture
{
  protected final Context mContext;
  protected final int mResId;

  public ResourceTexture(Context paramContext, int paramInt)
  {
    this.mContext = ((Context)Utils.checkNotNull(paramContext));
    this.mResId = paramInt;
    setOpaque(false);
  }

  protected void onFreeBitmap(Bitmap paramBitmap)
  {
    if (inFinalizer())
      return;
    paramBitmap.recycle();
  }

  protected Bitmap onGetBitmap()
  {
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
    return BitmapFactory.decodeResource(this.mContext.getResources(), this.mResId, localOptions);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.ResourceTexture
 * JD-Core Version:    0.5.4
 */