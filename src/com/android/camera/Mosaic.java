package com.android.camera;

public class Mosaic
{
  static
  {
    System.loadLibrary("jni_mosaic");
  }

  public native void allocateMosaicMemory(int paramInt1, int paramInt2);

  public native int createMosaic(boolean paramBoolean);

  public native void freeMosaicMemory();

  public native byte[] getFinalMosaicNV21();

  public native int reportProgress(boolean paramBoolean1, boolean paramBoolean2);

  public native void reset();

  public native float[] setSourceImageFromGPU();

  public native void setStripType(int paramInt);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.Mosaic
 * JD-Core Version:    0.5.4
 */