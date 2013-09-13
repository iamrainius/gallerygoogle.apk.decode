package com.android.gallery3d.ui;

import android.graphics.Bitmap;
import android.graphics.PointF;
import com.android.gallery3d.anim.CanvasAnimation;
import com.android.gallery3d.anim.FloatAnimation;
import java.util.Random;
import javax.microedition.khronos.opengles.GL11;

public class SlideshowView extends GLView
{
  private SlideshowAnimation mCurrentAnimation;
  private int mCurrentRotation;
  private BitmapTexture mCurrentTexture;
  private SlideshowAnimation mPrevAnimation;
  private int mPrevRotation;
  private BitmapTexture mPrevTexture;
  private Random mRandom = new Random();
  private final FloatAnimation mTransitionAnimation = new FloatAnimation(0.0F, 1.0F, 1000);

  public void next(Bitmap paramBitmap, int paramInt)
  {
    this.mTransitionAnimation.start();
    if (this.mPrevTexture != null)
    {
      this.mPrevTexture.getBitmap().recycle();
      this.mPrevTexture.recycle();
    }
    this.mPrevTexture = this.mCurrentTexture;
    this.mPrevAnimation = this.mCurrentAnimation;
    this.mPrevRotation = this.mCurrentRotation;
    this.mCurrentRotation = paramInt;
    this.mCurrentTexture = new BitmapTexture(paramBitmap);
    if ((0x1 & paramInt / 90) == 0);
    for (this.mCurrentAnimation = new SlideshowAnimation(this.mCurrentTexture.getWidth(), this.mCurrentTexture.getHeight(), this.mRandom); ; this.mCurrentAnimation = new SlideshowAnimation(this.mCurrentTexture.getHeight(), this.mCurrentTexture.getWidth(), this.mRandom))
    {
      this.mCurrentAnimation.start();
      invalidate();
      return;
    }
  }

  public void release()
  {
    if (this.mPrevTexture != null)
    {
      this.mPrevTexture.recycle();
      this.mPrevTexture = null;
    }
    if (this.mCurrentTexture == null)
      return;
    this.mCurrentTexture.recycle();
    this.mCurrentTexture = null;
  }

  protected void render(GLCanvas paramGLCanvas)
  {
    long l = AnimationTime.get();
    boolean bool = this.mTransitionAnimation.calculate(l);
    GL11 localGL11 = paramGLCanvas.getGLInstance();
    localGL11.glBlendFunc(1, 1);
    float f;
    if (this.mPrevTexture == null)
      f = 1.0F;
    while (true)
    {
      if ((this.mPrevTexture != null) && (f != 1.0F))
      {
        bool |= this.mPrevAnimation.calculate(l);
        paramGLCanvas.save(3);
        paramGLCanvas.setAlpha(1.0F - f);
        this.mPrevAnimation.apply(paramGLCanvas);
        paramGLCanvas.rotate(this.mPrevRotation, 0.0F, 0.0F, 1.0F);
        this.mPrevTexture.draw(paramGLCanvas, -this.mPrevTexture.getWidth() / 2, -this.mPrevTexture.getHeight() / 2);
        paramGLCanvas.restore();
      }
      if (this.mCurrentTexture != null)
      {
        bool |= this.mCurrentAnimation.calculate(l);
        paramGLCanvas.save(3);
        paramGLCanvas.setAlpha(f);
        this.mCurrentAnimation.apply(paramGLCanvas);
        paramGLCanvas.rotate(this.mCurrentRotation, 0.0F, 0.0F, 1.0F);
        this.mCurrentTexture.draw(paramGLCanvas, -this.mCurrentTexture.getWidth() / 2, -this.mCurrentTexture.getHeight() / 2);
        paramGLCanvas.restore();
      }
      if (bool)
        invalidate();
      localGL11.glBlendFunc(1, 771);
      return;
      f = this.mTransitionAnimation.get();
    }
  }

  private class SlideshowAnimation extends CanvasAnimation
  {
    private final int mHeight;
    private final PointF mMovingVector;
    private float mProgress;
    private final int mWidth;

    public SlideshowAnimation(int paramInt1, int paramRandom, Random arg4)
    {
      this.mWidth = paramInt1;
      this.mHeight = paramRandom;
      Object localObject;
      this.mMovingVector = new PointF(0.2F * this.mWidth * (localObject.nextFloat() - 0.5F), 0.2F * this.mHeight * (localObject.nextFloat() - 0.5F));
      setDuration(3500);
    }

    public void apply(GLCanvas paramGLCanvas)
    {
      int i = SlideshowView.this.getWidth();
      int j = SlideshowView.this.getHeight();
      float f = Math.min(i / this.mWidth, j / this.mHeight) * (1.0F + 0.2F * this.mProgress);
      paramGLCanvas.translate(i / 2 + this.mMovingVector.x * this.mProgress, j / 2 + this.mMovingVector.y * this.mProgress);
      paramGLCanvas.scale(f, f, 0.0F);
    }

    public int getCanvasSaveFlags()
    {
      return 2;
    }

    protected void onCalculate(float paramFloat)
    {
      this.mProgress = paramFloat;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.SlideshowView
 * JD-Core Version:    0.5.4
 */