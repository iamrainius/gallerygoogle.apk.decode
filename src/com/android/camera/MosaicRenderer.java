package com.android.camera;

public class MosaicRenderer
{
  static
  {
    System.loadLibrary("jni_mosaic");
  }

  public static native int init();

  public static native void preprocess(float[] paramArrayOfFloat);

  public static native void reset(int paramInt1, int paramInt2, boolean paramBoolean);

  public static native void setWarping(boolean paramBoolean);

  public static native void step();

  public static native void transferGPUtoCPU();

  public static native void updateMatrix();
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.MosaicRenderer
 * JD-Core Version:    0.5.4
 */