package com.android.gallery3d.ui;

import android.graphics.Bitmap;
import android.graphics.RectF;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.BitmapPool;
import com.android.gallery3d.data.MediaItem;

public class TiledScreenNail
  implements ScreenNail
{
  private static boolean mDrawPlaceholder;
  private static int mPlaceholderColor;
  private static int sMaxSide = 640;
  private long mAnimationStartTime = -1L;
  private Bitmap mBitmap;
  private int mHeight;
  private TiledTexture mTexture;
  private int mWidth;

  static
  {
    mPlaceholderColor = -14540254;
    mDrawPlaceholder = true;
  }

  public TiledScreenNail(int paramInt1, int paramInt2)
  {
    setSize(paramInt1, paramInt2);
  }

  public TiledScreenNail(Bitmap paramBitmap)
  {
    this.mWidth = paramBitmap.getWidth();
    this.mHeight = paramBitmap.getHeight();
    this.mBitmap = paramBitmap;
    this.mTexture = new TiledTexture(paramBitmap);
  }

  public static void disableDrawPlaceholder()
  {
    mDrawPlaceholder = false;
  }

  public static void enableDrawPlaceholder()
  {
    mDrawPlaceholder = true;
  }

  private float getRatio()
  {
    return Utils.clamp(1.0F - (float)(AnimationTime.get() - this.mAnimationStartTime) / 180.0F, 0.0F, 1.0F);
  }

  private static void recycleBitmap(BitmapPool paramBitmapPool, Bitmap paramBitmap)
  {
    if ((paramBitmapPool == null) || (paramBitmap == null))
      return;
    paramBitmapPool.recycle(paramBitmap);
  }

  public static void setMaxSide(int paramInt)
  {
    sMaxSide = paramInt;
  }

  public static void setPlaceholderColor(int paramInt)
  {
    mPlaceholderColor = paramInt;
  }

  private void setSize(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) || (paramInt2 == 0))
    {
      paramInt1 = sMaxSide;
      paramInt2 = 3 * sMaxSide / 4;
    }
    float f = Math.min(1.0F, sMaxSide / Math.max(paramInt1, paramInt2));
    this.mWidth = Math.round(f * paramInt1);
    this.mHeight = Math.round(f * paramInt2);
  }

  public ScreenNail combine(ScreenNail paramScreenNail)
  {
    if (paramScreenNail == null)
      return this;
    if (!paramScreenNail instanceof TiledScreenNail)
    {
      recycle();
      return paramScreenNail;
    }
    TiledScreenNail localTiledScreenNail = (TiledScreenNail)paramScreenNail;
    this.mWidth = localTiledScreenNail.mWidth;
    this.mHeight = localTiledScreenNail.mHeight;
    if (localTiledScreenNail.mTexture != null)
    {
      recycleBitmap(MediaItem.getThumbPool(), this.mBitmap);
      if (this.mTexture != null)
        this.mTexture.recycle();
      this.mBitmap = localTiledScreenNail.mBitmap;
      this.mTexture = localTiledScreenNail.mTexture;
      localTiledScreenNail.mBitmap = null;
      localTiledScreenNail.mTexture = null;
    }
    localTiledScreenNail.recycle();
    return this;
  }

  public void draw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((this.mTexture == null) || (!this.mTexture.isReady()))
    {
      if (this.mAnimationStartTime == -1L)
        this.mAnimationStartTime = -2L;
      if (mDrawPlaceholder)
        paramGLCanvas.fillRect(paramInt1, paramInt2, paramInt3, paramInt4, mPlaceholderColor);
      return;
    }
    if (this.mAnimationStartTime == -2L)
      this.mAnimationStartTime = AnimationTime.get();
    if (isAnimating())
    {
      this.mTexture.drawMixed(paramGLCanvas, mPlaceholderColor, getRatio(), paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    this.mTexture.draw(paramGLCanvas, paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public void draw(GLCanvas paramGLCanvas, RectF paramRectF1, RectF paramRectF2)
  {
    if ((this.mTexture == null) || (!this.mTexture.isReady()))
    {
      paramGLCanvas.fillRect(paramRectF2.left, paramRectF2.top, paramRectF2.width(), paramRectF2.height(), mPlaceholderColor);
      return;
    }
    this.mTexture.draw(paramGLCanvas, paramRectF1, paramRectF2);
  }

  public int getHeight()
  {
    return this.mHeight;
  }

  public TiledTexture getTexture()
  {
    return this.mTexture;
  }

  public int getWidth()
  {
    return this.mWidth;
  }

  public boolean isAnimating()
  {
    if (this.mAnimationStartTime < 0L)
      return false;
    if (AnimationTime.get() - this.mAnimationStartTime >= 180L)
    {
      this.mAnimationStartTime = -3L;
      return false;
    }
    return true;
  }

  public void noDraw()
  {
  }

  public void recycle()
  {
    if (this.mTexture != null)
    {
      this.mTexture.recycle();
      this.mTexture = null;
    }
    recycleBitmap(MediaItem.getThumbPool(), this.mBitmap);
    this.mBitmap = null;
  }

  public void updatePlaceholderSize(int paramInt1, int paramInt2)
  {
    if (this.mBitmap != null);
    do
      return;
    while ((paramInt1 == 0) || (paramInt2 == 0));
    setSize(paramInt1, paramInt2);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.TiledScreenNail
 * JD-Core Version:    0.5.4
 */