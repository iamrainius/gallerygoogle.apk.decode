package com.android.gallery3d.filtershow.filters;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Allocation.MipmapControl;
import android.renderscript.RSIllegalArgumentException;
import android.renderscript.RSRuntimeException;
import android.renderscript.RenderScript;
import android.util.Log;

public class ImageFilterRS extends ImageFilter
{
  protected static Allocation mInPixelsAllocation;
  protected static Allocation mOutPixelsAllocation;
  private static RenderScript mRS = null;
  private static Resources mResources = null;
  private final String LOGTAG = "ImageFilterRS";

  public static RenderScript getRenderScriptContext()
  {
    return mRS;
  }

  public static void setRenderScriptContext(Activity paramActivity)
  {
    mRS = RenderScript.create(paramActivity);
    mResources = paramActivity.getResources();
  }

  public Bitmap apply(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    if (paramBitmap == null)
      return paramBitmap;
    try
    {
      prepare(paramBitmap);
      createFilter(mResources, paramFloat, paramBoolean);
      runFilter();
      update(paramBitmap);
      return paramBitmap;
    }
    catch (RSIllegalArgumentException localRSIllegalArgumentException)
    {
      Log.e("ImageFilterRS", "Illegal argument? " + localRSIllegalArgumentException);
      return paramBitmap;
    }
    catch (RSRuntimeException localRSRuntimeException)
    {
      Log.e("ImageFilterRS", "RS runtime exception ? " + localRSRuntimeException);
    }
    return paramBitmap;
  }

  public void createFilter(Resources paramResources, float paramFloat, boolean paramBoolean)
  {
  }

  public void prepare(Bitmap paramBitmap)
  {
    mInPixelsAllocation = Allocation.createFromBitmap(mRS, paramBitmap, Allocation.MipmapControl.MIPMAP_NONE, 1);
    mOutPixelsAllocation = Allocation.createTyped(mRS, mInPixelsAllocation.getType());
  }

  public void runFilter()
  {
  }

  public void update(Bitmap paramBitmap)
  {
    mOutPixelsAllocation.copyTo(paramBitmap);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ImageFilterRS
 * JD-Core Version:    0.5.4
 */