package com.android.gallery3d.filtershow.imageshow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.android.gallery3d.filtershow.filters.ImageFilterTinyPlanet;

public class ImageTinyPlanet extends ImageSlave
{
  private float mCenterX = 0.0F;
  private float mCenterY = 0.0F;
  private float mCurrentX = 0.0F;
  private float mCurrentY = 0.0F;
  private float mStartAngle = 0.0F;
  private float mTouchCenterX = 0.0F;
  private float mTouchCenterY = 0.0F;

  public ImageTinyPlanet(Context paramContext)
  {
    super(paramContext);
  }

  public ImageTinyPlanet(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  protected static float angleFor(float paramFloat1, float paramFloat2)
  {
    return (float)(180.0D * Math.atan2(paramFloat1, paramFloat2) / 3.141592653589793D);
  }

  protected float getCurrentTouchAngle()
  {
    if ((this.mCurrentX == this.mTouchCenterX) && (this.mCurrentY == this.mTouchCenterY))
      return 0.0F;
    float f1 = this.mTouchCenterX - this.mCenterX;
    float f2 = this.mTouchCenterY - this.mCenterY;
    float f3 = this.mCurrentX - this.mCenterX;
    float f4 = this.mCurrentY - this.mCenterY;
    float f5 = angleFor(f1, f2);
    return (float)(3.141592653589793D * ((angleFor(f3, f4) - f5) % 360.0F) / 180.0D);
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    ImageFilterTinyPlanet localImageFilterTinyPlanet = (ImageFilterTinyPlanet)getCurrentFilter();
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    this.mCurrentX = f1;
    this.mCurrentY = f2;
    this.mCenterX = (getWidth() / 2);
    this.mCenterY = (getHeight() / 2);
    switch (paramMotionEvent.getActionMasked())
    {
    default:
    case 0:
    case 1:
    case 2:
    }
    while (true)
    {
      resetImageCaches(this);
      invalidate();
      return true;
      this.mTouchCenterX = f1;
      this.mTouchCenterY = f2;
      this.mStartAngle = localImageFilterTinyPlanet.getAngle();
      continue;
      localImageFilterTinyPlanet.setAngle(this.mStartAngle + getCurrentTouchAngle());
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.imageshow.ImageTinyPlanet
 * JD-Core Version:    0.5.4
 */