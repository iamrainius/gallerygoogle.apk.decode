package com.android.gallery3d.anim;

public class FloatAnimation extends Animation
{
  private float mCurrent;
  private final float mFrom;
  private final float mTo;

  public FloatAnimation(float paramFloat1, float paramFloat2, int paramInt)
  {
    this.mFrom = paramFloat1;
    this.mTo = paramFloat2;
    this.mCurrent = paramFloat1;
    setDuration(paramInt);
  }

  public float get()
  {
    return this.mCurrent;
  }

  protected void onCalculate(float paramFloat)
  {
    this.mCurrent = (this.mFrom + paramFloat * (this.mTo - this.mFrom));
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.anim.FloatAnimation
 * JD-Core Version:    0.5.4
 */