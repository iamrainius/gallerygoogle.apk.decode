package com.android.gallery3d.filtershow.filters;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.RectF;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.android.gallery3d.filtershow.cache.ImageLoader;
import com.android.gallery3d.filtershow.presets.ImagePreset;

public class ImageFilterTinyPlanet extends ImageFilter
{
  private static final String TAG = ImageFilterTinyPlanet.class.getSimpleName();
  private float mAngle = 0.0F;

  public ImageFilterTinyPlanet()
  {
    setFilterType(6);
    this.mName = "TinyPlanet";
    this.mMinParameter = 10;
    this.mMaxParameter = 60;
    this.mDefaultParameter = 20;
    this.mPreviewParameter = 20;
    this.mParameter = 20;
    this.mAngle = 0.0F;
  }

  private Bitmap applyXmp(Bitmap paramBitmap, ImagePreset paramImagePreset, int paramInt)
  {
    try
    {
      XMPMeta localXMPMeta = paramImagePreset.getImageLoader().getXmpObject();
      if (localXMPMeta == null)
        return paramBitmap;
      int i = getInt(localXMPMeta, "CroppedAreaImageWidthPixels");
      int j = getInt(localXMPMeta, "CroppedAreaImageHeightPixels");
      int k = getInt(localXMPMeta, "FullPanoWidthPixels");
      int l = getInt(localXMPMeta, "FullPanoHeightPixels");
      int i1 = getInt(localXMPMeta, "CroppedAreaLeftPixels");
      int i2 = getInt(localXMPMeta, "CroppedAreaTopPixels");
      float f = paramInt / k;
      Bitmap localBitmap = Bitmap.createBitmap((int)(f * k), (int)(f * l), Bitmap.Config.ARGB_8888);
      Canvas localCanvas = new Canvas(localBitmap);
      int i3 = i1 + i;
      int i4 = i2 + j;
      RectF localRectF = new RectF(f * i1, f * i2, f * i3, f * i4);
      localCanvas.drawBitmap(paramBitmap, null, localRectF, null);
      paramBitmap = localBitmap;
      return paramBitmap;
    }
    catch (XMPException localXMPException)
    {
    }
  }

  private static int getInt(XMPMeta paramXMPMeta, String paramString)
    throws XMPException
  {
    if (paramXMPMeta.doesPropertyExist("http://ns.google.com/photos/1.0/panorama/", paramString))
      return paramXMPMeta.getPropertyInteger("http://ns.google.com/photos/1.0/panorama/", paramString).intValue();
    return 0;
  }

  public Bitmap apply(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    int i = paramBitmap.getWidth();
    paramBitmap.getHeight();
    int j = (int)(i / 2.0F);
    ImagePreset localImagePreset = getImagePreset();
    if ((localImagePreset != null) && (localImagePreset.isPanoramaSafe()))
      paramBitmap = applyXmp(paramBitmap, localImagePreset, i);
    Bitmap localBitmap = Bitmap.createBitmap(j, j, Bitmap.Config.ARGB_8888);
    int k = paramBitmap.getWidth();
    int l = paramBitmap.getHeight();
    float f1 = this.mParameter / 100.0F;
    float f2 = this.mAngle;
    nativeApplyFilter(paramBitmap, k, l, localBitmap, j, f1, f2);
    return localBitmap;
  }

  public float getAngle()
  {
    return this.mAngle;
  }

  public boolean isNil()
  {
    return false;
  }

  protected native void nativeApplyFilter(Bitmap paramBitmap1, int paramInt1, int paramInt2, Bitmap paramBitmap2, int paramInt3, float paramFloat1, float paramFloat2);

  public void setAngle(float paramFloat)
  {
    this.mAngle = paramFloat;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ImageFilterTinyPlanet
 * JD-Core Version:    0.5.4
 */