package com.android.gallery3d.ui;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class CaptureAnimation
{
  private static final Interpolator sSlideInterpolator;
  private static final Interpolator sZoomInInterpolator;
  private static final Interpolator sZoomOutInterpolator = new DecelerateInterpolator();

  static
  {
    sZoomInInterpolator = new AccelerateInterpolator();
    sSlideInterpolator = new AccelerateDecelerateInterpolator();
  }

  public static float calculateScale(float paramFloat)
  {
    if (paramFloat <= 0.5F)
      return 1.0F - 0.2F * sZoomOutInterpolator.getInterpolation(paramFloat * 2.0F);
    return 0.8F + 0.2F * sZoomInInterpolator.getInterpolation(2.0F * (paramFloat - 0.5F));
  }

  public static float calculateSlide(float paramFloat)
  {
    return sSlideInterpolator.getInterpolation(paramFloat);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.CaptureAnimation
 * JD-Core Version:    0.5.4
 */