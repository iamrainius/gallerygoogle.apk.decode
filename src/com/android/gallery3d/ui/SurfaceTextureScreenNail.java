package com.android.gallery3d.ui;

import android.annotation.TargetApi;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import com.android.gallery3d.common.ApiHelper;

@TargetApi(11)
public abstract class SurfaceTextureScreenNail
  implements SurfaceTexture.OnFrameAvailableListener, ScreenNail
{
  protected ExtTexture mExtTexture;
  private boolean mHasTexture = false;
  private int mHeight;
  private SurfaceTexture mSurfaceTexture;
  private float[] mTransform = new float[16];
  private int mWidth;

  @TargetApi(14)
  private static void releaseSurfaceTexture(SurfaceTexture paramSurfaceTexture)
  {
    paramSurfaceTexture.setOnFrameAvailableListener(null);
    if (!ApiHelper.HAS_RELEASE_SURFACE_TEXTURE)
      return;
    paramSurfaceTexture.release();
  }

  @TargetApi(15)
  private static void setDefaultBufferSize(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
  {
    if (!ApiHelper.HAS_SET_DEFALT_BUFFER_SIZE)
      return;
    paramSurfaceTexture.setDefaultBufferSize(paramInt1, paramInt2);
  }

  public void acquireSurfaceTexture()
  {
    this.mExtTexture = new ExtTexture(36197);
    this.mExtTexture.setSize(this.mWidth, this.mHeight);
    this.mSurfaceTexture = new SurfaceTexture(this.mExtTexture.getId());
    setDefaultBufferSize(this.mSurfaceTexture, this.mWidth, this.mHeight);
    this.mSurfaceTexture.setOnFrameAvailableListener(this);
    monitorenter;
    try
    {
      this.mHasTexture = true;
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  public void draw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    monitorenter;
    try
    {
      if (!this.mHasTexture)
        return;
      this.mSurfaceTexture.updateTexImage();
      this.mSurfaceTexture.getTransformMatrix(this.mTransform);
      paramGLCanvas.save(2);
      int i = paramInt1 + paramInt3 / 2;
      int j = paramInt2 + paramInt4 / 2;
      paramGLCanvas.translate(i, j);
      paramGLCanvas.scale(1.0F, -1.0F, 1.0F);
      paramGLCanvas.translate(-i, -j);
      updateTransformMatrix(this.mTransform);
      paramGLCanvas.drawTexture(this.mExtTexture, this.mTransform, paramInt1, paramInt2, paramInt3, paramInt4);
      paramGLCanvas.restore();
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  public void draw(GLCanvas paramGLCanvas, RectF paramRectF1, RectF paramRectF2)
  {
    throw new UnsupportedOperationException();
  }

  public int getHeight()
  {
    return this.mHeight;
  }

  public SurfaceTexture getSurfaceTexture()
  {
    return this.mSurfaceTexture;
  }

  public int getWidth()
  {
    return this.mWidth;
  }

  public void releaseSurfaceTexture()
  {
    monitorenter;
    try
    {
      this.mHasTexture = false;
      monitorexit;
      this.mExtTexture.recycle();
      this.mExtTexture = null;
      releaseSurfaceTexture(this.mSurfaceTexture);
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  public void setSize(int paramInt1, int paramInt2)
  {
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
  }

  protected void updateTransformMatrix(float[] paramArrayOfFloat)
  {
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.SurfaceTextureScreenNail
 * JD-Core Version:    0.5.4
 */