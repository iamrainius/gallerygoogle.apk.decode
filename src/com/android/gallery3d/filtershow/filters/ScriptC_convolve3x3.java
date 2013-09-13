package com.android.gallery3d.filtershow.filters;

import android.content.res.Resources;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.FieldPacker;
import android.renderscript.RSRuntimeException;
import android.renderscript.RenderScript;
import android.renderscript.ScriptC;
import android.renderscript.Type;

public class ScriptC_convolve3x3 extends ScriptC
{
  private Element __ALLOCATION;
  private Element __F32;
  private Element __I32;
  private Element __U8_4;
  private float[] mExportVar_gCoeffs;
  private int mExportVar_gHeight;
  private Allocation mExportVar_gIn;
  private Allocation mExportVar_gPixels;
  private int mExportVar_gWidth;

  public ScriptC_convolve3x3(RenderScript paramRenderScript, Resources paramResources, int paramInt)
  {
    super(paramRenderScript, paramResources, paramInt);
    this.__I32 = Element.I32(paramRenderScript);
    this.__ALLOCATION = Element.ALLOCATION(paramRenderScript);
    this.__F32 = Element.F32(paramRenderScript);
    this.__U8_4 = Element.U8_4(paramRenderScript);
  }

  public void bind_gPixels(Allocation paramAllocation)
  {
    this.mExportVar_gPixels = paramAllocation;
    if (paramAllocation == null)
    {
      bindAllocation(null, 2);
      return;
    }
    bindAllocation(paramAllocation, 2);
  }

  public void forEach_root(Allocation paramAllocation1, Allocation paramAllocation2)
  {
    if (!paramAllocation1.getType().getElement().isCompatible(this.__U8_4))
      throw new RSRuntimeException("Type mismatch with U8_4!");
    if (!paramAllocation2.getType().getElement().isCompatible(this.__U8_4))
      throw new RSRuntimeException("Type mismatch with U8_4!");
    Type localType1 = paramAllocation1.getType();
    Type localType2 = paramAllocation2.getType();
    if ((localType1.getCount() != localType2.getCount()) || (localType1.getX() != localType2.getX()) || (localType1.getY() != localType2.getY()) || (localType1.getZ() != localType2.getZ()) || (localType1.hasFaces() != localType2.hasFaces()) || (localType1.hasMipmaps() != localType2.hasMipmaps()))
      throw new RSRuntimeException("Dimension mismatch between input and output parameters!");
    forEach(0, paramAllocation1, paramAllocation2, null);
  }

  public void set_gCoeffs(float[] paramArrayOfFloat)
  {
    monitorenter;
    try
    {
      this.mExportVar_gCoeffs = paramArrayOfFloat;
      FieldPacker localFieldPacker = new FieldPacker(36);
      for (int i = 0; i < 9; ++i)
        localFieldPacker.addF32(paramArrayOfFloat[i]);
      int[] arrayOfInt = { 9 };
      setVar(4, localFieldPacker, this.__F32, arrayOfInt);
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  public void set_gHeight(int paramInt)
  {
    monitorenter;
    try
    {
      setVar(1, paramInt);
      this.mExportVar_gHeight = paramInt;
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public void set_gIn(Allocation paramAllocation)
  {
    monitorenter;
    try
    {
      setVar(3, paramAllocation);
      this.mExportVar_gIn = paramAllocation;
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public void set_gWidth(int paramInt)
  {
    monitorenter;
    try
    {
      setVar(0, paramInt);
      this.mExportVar_gWidth = paramInt;
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ScriptC_convolve3x3
 * JD-Core Version:    0.5.4
 */