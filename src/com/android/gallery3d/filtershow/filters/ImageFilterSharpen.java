package com.android.gallery3d.filtershow.filters;

import android.content.res.Resources;
import android.renderscript.Allocation;
import android.renderscript.Type;

public class ImageFilterSharpen extends ImageFilterRS
{
  private ScriptC_convolve3x3 mScript;

  public ImageFilterSharpen()
  {
    this.mName = "Sharpen";
  }

  public void createFilter(Resources paramResources, float paramFloat, boolean paramBoolean)
  {
    int i = mInPixelsAllocation.getType().getX();
    int j = mInPixelsAllocation.getType().getY();
    float f = paramFloat * this.mParameter / 100.0F;
    float[] arrayOfFloat = new float[9];
    arrayOfFloat[0] = (-f);
    arrayOfFloat[1] = (-f);
    arrayOfFloat[2] = (-f);
    arrayOfFloat[3] = (-f);
    arrayOfFloat[4] = (1.0F + 8.0F * f);
    arrayOfFloat[5] = (-f);
    arrayOfFloat[6] = (-f);
    arrayOfFloat[7] = (-f);
    arrayOfFloat[8] = (-f);
    if (this.mScript == null)
      this.mScript = new ScriptC_convolve3x3(getRenderScriptContext(), paramResources, 2131230722);
    this.mScript.set_gCoeffs(arrayOfFloat);
    this.mScript.set_gWidth(i);
    this.mScript.set_gHeight(j);
  }

  public void runFilter()
  {
    this.mScript.set_gIn(mInPixelsAllocation);
    this.mScript.bind_gPixels(mInPixelsAllocation);
    this.mScript.forEach_root(mInPixelsAllocation, mOutPixelsAllocation);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ImageFilterSharpen
 * JD-Core Version:    0.5.4
 */