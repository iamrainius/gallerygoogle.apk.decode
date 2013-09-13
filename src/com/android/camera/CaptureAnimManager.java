package com.android.camera;

import android.graphics.Color;
import android.os.SystemClock;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.gallery3d.ui.GLCanvas;
import com.android.gallery3d.ui.RawTexture;

public class CaptureAnimManager
{
  private int mAnimOrientation;
  private long mAnimStartTime;
  private int mAnimType;
  private float mDelta;
  private int mDrawHeight;
  private int mDrawWidth;
  private final Interpolator mSlideInterpolator = new DecelerateInterpolator();
  private float mX;
  private float mY;

  public void animateFlash()
  {
    this.mAnimType = 1;
  }

  public void animateFlashAndSlide()
  {
    this.mAnimType = 0;
  }

  public void animateSlide()
  {
    if (this.mAnimType != 1)
      return;
    this.mAnimType = 2;
    this.mAnimStartTime = SystemClock.uptimeMillis();
  }

  public boolean drawAnimation(GLCanvas paramGLCanvas, CameraScreenNail paramCameraScreenNail, RawTexture paramRawTexture)
  {
    long l = SystemClock.uptimeMillis() - this.mAnimStartTime;
    if ((this.mAnimType == 2) && (l > 400L))
      return false;
    if ((this.mAnimType == 0) && (l > 800L))
      return false;
    int i = this.mAnimType;
    if (this.mAnimType == 0)
      if (l >= 400L)
        break label182;
    for (i = 1; ; i = 2)
    {
      if (i == 2)
        l -= 400L;
      if (i != 1)
        break;
      paramRawTexture.draw(paramGLCanvas, (int)this.mX, (int)this.mY, this.mDrawWidth, this.mDrawHeight);
      if (l < 200L)
      {
        int j = Color.argb((int)(255.0F * (0.3F - 0.3F * (float)l / 200.0F)), 255, 255, 255);
        paramGLCanvas.fillRect(this.mX, this.mY, this.mDrawWidth, this.mDrawHeight, j);
      }
      label180: label182: return true;
    }
    if (i == 2)
    {
      float f1 = (float)l / 400.0F;
      float f2 = this.mX;
      float f3 = this.mY;
      if ((this.mAnimOrientation == 0) || (this.mAnimOrientation == 180))
        f2 += this.mDelta * this.mSlideInterpolator.getInterpolation(f1);
      while (true)
      {
        paramCameraScreenNail.directDraw(paramGLCanvas, (int)this.mX, (int)this.mY, this.mDrawWidth, this.mDrawHeight);
        paramRawTexture.draw(paramGLCanvas, (int)f2, (int)f3, this.mDrawWidth, this.mDrawHeight);
        break label180:
        f3 += this.mDelta * this.mSlideInterpolator.getInterpolation(f1);
      }
    }
    return false;
  }

  public void setOrientation(int paramInt)
  {
    this.mAnimOrientation = ((360 - paramInt) % 360);
  }

  public void startAnimation(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mAnimStartTime = SystemClock.uptimeMillis();
    this.mDrawWidth = paramInt3;
    this.mDrawHeight = paramInt4;
    this.mX = paramInt1;
    this.mY = paramInt2;
    switch (this.mAnimOrientation)
    {
    default:
      return;
    case 0:
      this.mDelta = paramInt3;
      return;
    case 90:
      this.mDelta = (-paramInt4);
      return;
    case 180:
      this.mDelta = (-paramInt3);
      return;
    case 270:
    }
    this.mDelta = paramInt4;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.CaptureAnimManager
 * JD-Core Version:    0.5.4
 */