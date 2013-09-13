package com.android.gallery3d.filtershow.imageshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.android.gallery3d.filtershow.cache.ImageLoader;
import com.android.gallery3d.filtershow.ui.SliderController;

public class ImageZoom extends ImageSlave
{
  private boolean mTouchDown = false;
  private Rect mZoomBounds = null;
  private boolean mZoomedIn = false;

  public ImageZoom(Context paramContext)
  {
    super(paramContext);
  }

  public ImageZoom(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public boolean onDoubleTap(MotionEvent paramMotionEvent)
  {
    if (!this.mZoomedIn)
    {
      onTouchDown(paramMotionEvent.getX(), paramMotionEvent.getY());
      label19: if (this.mZoomedIn)
        break label46;
    }
    for (int i = 1; ; i = 0)
    {
      this.mZoomedIn = i;
      invalidate();
      return false;
      onTouchUp();
      label46: break label19:
    }
  }

  public void onDraw(Canvas paramCanvas)
  {
    drawBackground(paramCanvas);
    if ((((this.mZoomedIn) || (this.mTouchDown))) && (this.mImageLoader != null));
    for (Bitmap localBitmap = this.mImageLoader.getScaleOneImageForPreset(this, getImagePreset(), this.mZoomBounds, false); ; localBitmap = getFilteredImage())
    {
      drawImage(paramCanvas, localBitmap);
      if (showControls())
        this.mSliderController.onDraw(paramCanvas);
      drawToast(paramCanvas);
      return;
      requestFilteredImages();
    }
  }

  public void onTouchDown(float paramFloat1, float paramFloat2)
  {
    super.onTouchDown(paramFloat1, paramFloat2);
    if ((this.mZoomedIn) || (this.mTouchDown))
      return;
    this.mTouchDown = true;
    Rect localRect1 = this.mImageLoader.getOriginalBounds();
    Rect localRect2 = getImageBounds();
    float f1 = paramFloat1 - localRect2.left;
    float f2 = paramFloat2 - localRect2.top;
    float f3 = localRect1.width();
    float f4 = localRect1.height();
    (f3 / f4);
    int i = getWidth() / 2;
    int j = getHeight() / 2;
    (int)(f3 / 2.0F);
    (int)(f4 / 2.0F);
    int k = (int)(f3 * (f1 / localRect2.width()));
    int l = (int)(f4 * (f2 / localRect2.height()));
    int i1 = k - i;
    int i2 = l - j;
    this.mZoomBounds = new Rect(i1, i2, i1 + i * 2, i2 + j * 2);
  }

  public void onTouchUp()
  {
    this.mTouchDown = false;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.imageshow.ImageZoom
 * JD-Core Version:    0.5.4
 */