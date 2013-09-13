package com.android.camera;

import android.os.SystemClock;
import android.util.Log;
import com.android.gallery3d.ui.GLCanvas;
import com.android.gallery3d.ui.RawTexture;

public class SwitchAnimManager
{
  private long mAnimStartTime;
  private int mPreviewFrameLayoutWidth;
  private int mReviewDrawingHeight;
  private int mReviewDrawingWidth;

  public boolean drawAnimation(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4, CameraScreenNail paramCameraScreenNail, RawTexture paramRawTexture)
  {
    long l = SystemClock.uptimeMillis() - this.mAnimStartTime;
    if ((float)l > 400.0F)
      return false;
    float f1 = (float)l / 400.0F;
    float f2 = paramInt1 + paramInt3 / 2.0F;
    float f3 = paramInt2 + paramInt4 / 2.0F;
    float f4 = 1.0F - 0.2F * (1.0F - f1);
    float f5 = f4 * paramInt3;
    float f6 = f4 * paramInt4;
    int i = Math.round(f2 - f5 / 2.0F);
    int j = Math.round(f3 - f6 / 2.0F);
    float f7 = 1.0F + 0.5F * f1;
    float f8 = 1.0F;
    if (this.mPreviewFrameLayoutWidth != 0)
      f8 = paramInt3 / this.mPreviewFrameLayoutWidth;
    while (true)
    {
      float f9 = f8 * (f7 * this.mReviewDrawingWidth);
      float f10 = f8 * (f7 * this.mReviewDrawingHeight);
      int k = Math.round(f2 - f9 / 2.0F);
      int i1 = Math.round(f3 - f10 / 2.0F);
      float f11 = paramGLCanvas.getAlpha();
      paramGLCanvas.setAlpha(f1);
      paramCameraScreenNail.directDraw(paramGLCanvas, i, j, Math.round(f5), Math.round(f6));
      paramGLCanvas.setAlpha(0.8F * (1.0F - f1));
      paramRawTexture.draw(paramGLCanvas, k, i1, Math.round(f9), Math.round(f10));
      paramGLCanvas.setAlpha(f11);
      return true;
      Log.e("SwitchAnimManager", "mPreviewFrameLayoutWidth is 0.");
    }
  }

  public boolean drawDarkPreview(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4, RawTexture paramRawTexture)
  {
    float f1 = paramInt1 + paramInt3 / 2.0F;
    float f2 = paramInt2 + paramInt4 / 2.0F;
    float f3 = 1.0F;
    if (this.mPreviewFrameLayoutWidth != 0)
      f3 = paramInt3 / this.mPreviewFrameLayoutWidth;
    while (true)
    {
      float f4 = f3 * this.mReviewDrawingWidth;
      float f5 = f3 * this.mReviewDrawingHeight;
      int i = Math.round(f1 - f4 / 2.0F);
      int j = Math.round(f2 - f5 / 2.0F);
      float f6 = paramGLCanvas.getAlpha();
      paramGLCanvas.setAlpha(0.8F);
      paramRawTexture.draw(paramGLCanvas, i, j, Math.round(f4), Math.round(f5));
      paramGLCanvas.setAlpha(f6);
      return true;
      Log.e("SwitchAnimManager", "mPreviewFrameLayoutWidth is 0.");
    }
  }

  public void setPreviewFrameLayoutSize(int paramInt1, int paramInt2)
  {
    this.mPreviewFrameLayoutWidth = paramInt1;
  }

  public void setReviewDrawingSize(int paramInt1, int paramInt2)
  {
    this.mReviewDrawingWidth = paramInt1;
    this.mReviewDrawingHeight = paramInt2;
  }

  public void startAnimation()
  {
    this.mAnimStartTime = SystemClock.uptimeMillis();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.SwitchAnimManager
 * JD-Core Version:    0.5.4
 */