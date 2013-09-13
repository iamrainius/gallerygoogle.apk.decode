package com.google.android.apps.lightcycle;

public class Constants
{
  public static final float[] ANDROID_BLUE;
  public static final float[] ANDROID_GRAY;
  public static final float[] BACKGROUND_BLACK;
  public static final float[] GRAY = { 0.4196F, 0.4196F, 0.4196F, 1.0F };
  public static final float[] GREEN;
  public static final float[] GROUND_PLANE_COLOR;
  public static final float[] TRANSPARENT_GRAY = { 0.4196F, 0.4196F, 0.4196F, 0.5F };
  public static final float[] TRANSPARENT_WHITE;
  public static final float[] WHITE;

  static
  {
    GREEN = new float[] { 0.0F, 1.0F, 0.0F, 1.0F };
    ANDROID_BLUE = new float[] { 0.2F, 0.71F, 0.898F, 1.0F };
    ANDROID_GRAY = new float[] { 0.8F, 0.8F, 0.8F, 1.0F };
    BACKGROUND_BLACK = new float[] { 0.0F, 0.0F, 0.0F, 1.0F };
    WHITE = new float[] { 1.0F, 1.0F, 1.0F, 1.0F };
    TRANSPARENT_WHITE = new float[] { 1.0F, 1.0F, 1.0F, 0.6F };
    GROUND_PLANE_COLOR = new float[] { 1.0F, 1.0F, 1.0F, 0.1F };
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.Constants
 * JD-Core Version:    0.5.4
 */