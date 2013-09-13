package com.android.gallery3d.ui;

import com.android.gallery3d.common.Utils;

public abstract class FadeTexture
  implements Texture
{
  private final int mHeight;
  private boolean mIsAnimating;
  private final boolean mIsOpaque;
  private final long mStartTime;
  private final int mWidth;

  public FadeTexture(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    this.mIsOpaque = paramBoolean;
    this.mStartTime = now();
    this.mIsAnimating = true;
  }

  private long now()
  {
    return AnimationTime.get();
  }

  public void draw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2)
  {
    draw(paramGLCanvas, paramInt1, paramInt2, this.mWidth, this.mHeight);
  }

  public int getHeight()
  {
    return this.mHeight;
  }

  protected float getRatio()
  {
    return Utils.clamp(1.0F - (float)(now() - this.mStartTime) / 180.0F, 0.0F, 1.0F);
  }

  public int getWidth()
  {
    return this.mWidth;
  }

  public boolean isAnimating()
  {
    if ((this.mIsAnimating) && (now() - this.mStartTime >= 180L))
      this.mIsAnimating = false;
    return this.mIsAnimating;
  }

  public boolean isOpaque()
  {
    return this.mIsOpaque;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.FadeTexture
 * JD-Core Version:    0.5.4
 */