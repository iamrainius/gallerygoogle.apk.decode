package com.android.gallery3d.ui;

class FlingScroller
{
  private double mCosAngle;
  private double mCurrV;
  private int mCurrX;
  private int mCurrY;
  private int mDistance;
  private int mDuration;
  private int mFinalX;
  private int mFinalY;
  private int mMaxX;
  private int mMaxY;
  private int mMinX;
  private int mMinY;
  private double mSinAngle;
  private int mStartX;
  private int mStartY;

  private double getV(float paramFloat)
  {
    return 1000 * (4 * this.mDistance) * Math.pow(1.0F - paramFloat, 3.0D) / this.mDuration;
  }

  private int getX(float paramFloat)
  {
    int i = (int)Math.round(this.mStartX + paramFloat * this.mDistance * this.mCosAngle);
    if ((this.mCosAngle > 0.0D) && (this.mStartX <= this.mMaxX))
      i = Math.min(i, this.mMaxX);
    do
      return i;
    while ((this.mCosAngle >= 0.0D) || (this.mStartX < this.mMinX));
    return Math.max(i, this.mMinX);
  }

  private int getY(float paramFloat)
  {
    int i = (int)Math.round(this.mStartY + paramFloat * this.mDistance * this.mSinAngle);
    if ((this.mSinAngle > 0.0D) && (this.mStartY <= this.mMaxY))
      i = Math.min(i, this.mMaxY);
    do
      return i;
    while ((this.mSinAngle >= 0.0D) || (this.mStartY < this.mMinY));
    return Math.max(i, this.mMinY);
  }

  public void computeScrollOffset(float paramFloat)
  {
    float f1 = Math.min(paramFloat, 1.0F);
    float f2 = 1.0F - (float)Math.pow(1.0F - f1, 4.0D);
    this.mCurrX = getX(f2);
    this.mCurrY = getY(f2);
    this.mCurrV = getV(f1);
  }

  public void fling(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
  {
    this.mStartX = paramInt1;
    this.mStartY = paramInt2;
    this.mMinX = paramInt5;
    this.mMinY = paramInt7;
    this.mMaxX = paramInt6;
    this.mMaxY = paramInt8;
    double d = Math.hypot(paramInt3, paramInt4);
    this.mSinAngle = (paramInt4 / d);
    this.mCosAngle = (paramInt3 / d);
    this.mDuration = (int)Math.round(50.0D * Math.pow(Math.abs(d), 0.3333333333333333D));
    this.mDistance = (int)Math.round(d * this.mDuration / 4.0D / 1000.0D);
    this.mFinalX = getX(1.0F);
    this.mFinalY = getY(1.0F);
  }

  public int getCurrVelocityX()
  {
    return (int)Math.round(this.mCurrV * this.mCosAngle);
  }

  public int getCurrVelocityY()
  {
    return (int)Math.round(this.mCurrV * this.mSinAngle);
  }

  public int getCurrX()
  {
    return this.mCurrX;
  }

  public int getCurrY()
  {
    return this.mCurrY;
  }

  public int getDuration()
  {
    return this.mDuration;
  }

  public int getFinalX()
  {
    return this.mFinalX;
  }

  public int getFinalY()
  {
    return this.mFinalY;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.FlingScroller
 * JD-Core Version:    0.5.4
 */