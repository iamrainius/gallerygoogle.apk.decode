package com.android.camera;

import android.util.Log;

public class MosaicFrameProcessor
{
  private static MosaicFrameProcessor sMosaicFrameProcessor;
  private int mCurrProcessFrameIdx = -1;
  private float[] mDeltaX = new float[3];
  private float[] mDeltaY = new float[3];
  private int mFillIn = 0;
  private boolean mFirstRun;
  private boolean mIsMosaicMemoryAllocated = false;
  private int mLastProcessFrameIdx = -1;
  private Mosaic mMosaicer = new Mosaic();
  private int mOldestIdx = 0;
  private float mPanningRateX;
  private float mPanningRateY;
  private int mPreviewBufferSize;
  private int mPreviewHeight;
  private int mPreviewWidth;
  private ProgressListener mProgressListener;
  private int mTotalFrameCount = 0;
  private float mTotalTranslationX = 0.0F;
  private float mTotalTranslationY = 0.0F;
  private float mTranslationLastX;
  private float mTranslationLastY;

  public static MosaicFrameProcessor getInstance()
  {
    if (sMosaicFrameProcessor == null)
      sMosaicFrameProcessor = new MosaicFrameProcessor();
    return sMosaicFrameProcessor;
  }

  private void setupMosaicer(int paramInt1, int paramInt2, int paramInt3)
  {
    Log.v("MosaicFrameProcessor", "setupMosaicer w, h=" + paramInt1 + ',' + paramInt2 + ',' + paramInt3);
    if (this.mIsMosaicMemoryAllocated)
      throw new RuntimeException("MosaicFrameProcessor in use!");
    this.mIsMosaicMemoryAllocated = true;
    this.mMosaicer.allocateMosaicMemory(paramInt1, paramInt2);
  }

  public void calculateTranslationRate()
  {
    float[] arrayOfFloat = this.mMosaicer.setSourceImageFromGPU();
    (int)arrayOfFloat[10];
    this.mTotalFrameCount = (int)arrayOfFloat[9];
    float f1 = arrayOfFloat[2];
    float f2 = arrayOfFloat[5];
    if (this.mFirstRun)
    {
      this.mTranslationLastX = f1;
      this.mTranslationLastY = f2;
      this.mFirstRun = false;
      return;
    }
    int i = this.mOldestIdx;
    this.mTotalTranslationX -= this.mDeltaX[i];
    this.mTotalTranslationY -= this.mDeltaY[i];
    this.mDeltaX[i] = Math.abs(f1 - this.mTranslationLastX);
    this.mDeltaY[i] = Math.abs(f2 - this.mTranslationLastY);
    this.mTotalTranslationX += this.mDeltaX[i];
    this.mTotalTranslationY += this.mDeltaY[i];
    this.mPanningRateX = (this.mTotalTranslationX / (this.mPreviewWidth / 4) / 3.0F);
    this.mPanningRateY = (this.mTotalTranslationY / (this.mPreviewHeight / 4) / 3.0F);
    this.mTranslationLastX = f1;
    this.mTranslationLastY = f2;
    this.mOldestIdx = ((1 + this.mOldestIdx) % 3);
  }

  public void clear()
  {
    if (this.mIsMosaicMemoryAllocated)
    {
      this.mMosaicer.freeMosaicMemory();
      this.mIsMosaicMemoryAllocated = false;
    }
    monitorenter;
    try
    {
      super.notify();
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  public int createMosaic(boolean paramBoolean)
  {
    return this.mMosaicer.createMosaic(paramBoolean);
  }

  public byte[] getFinalMosaicNV21()
  {
    return this.mMosaicer.getFinalMosaicNV21();
  }

  public void initialize(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mPreviewWidth = paramInt1;
    this.mPreviewHeight = paramInt2;
    this.mPreviewBufferSize = paramInt3;
    setupMosaicer(this.mPreviewWidth, this.mPreviewHeight, this.mPreviewBufferSize);
    setStripType(1);
    reset();
  }

  public boolean isMosaicMemoryAllocated()
  {
    return this.mIsMosaicMemoryAllocated;
  }

  public void processFrame()
  {
    if (!this.mIsMosaicMemoryAllocated);
    do
    {
      do
      {
        do
        {
          return;
          this.mCurrProcessFrameIdx = this.mFillIn;
          this.mFillIn = ((1 + this.mFillIn) % 2);
        }
        while (this.mCurrProcessFrameIdx == this.mLastProcessFrameIdx);
        this.mLastProcessFrameIdx = this.mCurrProcessFrameIdx;
        if (this.mTotalFrameCount >= 100)
          break label112;
        calculateTranslationRate();
      }
      while (this.mProgressListener == null);
      this.mProgressListener.onProgress(false, this.mPanningRateX, this.mPanningRateY, 4.0F * this.mTranslationLastX / this.mPreviewWidth, 4.0F * this.mTranslationLastY / this.mPreviewHeight);
      label112: return;
    }
    while (this.mProgressListener == null);
    this.mProgressListener.onProgress(true, this.mPanningRateX, this.mPanningRateY, 4.0F * this.mTranslationLastX / this.mPreviewWidth, 4.0F * this.mTranslationLastY / this.mPreviewHeight);
  }

  public int reportProgress(boolean paramBoolean1, boolean paramBoolean2)
  {
    return this.mMosaicer.reportProgress(paramBoolean1, paramBoolean2);
  }

  public void reset()
  {
    this.mFirstRun = true;
    this.mTotalFrameCount = 0;
    this.mFillIn = 0;
    this.mTotalTranslationX = 0.0F;
    this.mTranslationLastX = 0.0F;
    this.mTotalTranslationY = 0.0F;
    this.mTranslationLastY = 0.0F;
    this.mPanningRateX = 0.0F;
    this.mPanningRateY = 0.0F;
    this.mLastProcessFrameIdx = -1;
    this.mCurrProcessFrameIdx = -1;
    for (int i = 0; i < 3; ++i)
    {
      this.mDeltaX[i] = 0.0F;
      this.mDeltaY[i] = 0.0F;
    }
    this.mMosaicer.reset();
  }

  public void setProgressListener(ProgressListener paramProgressListener)
  {
    this.mProgressListener = paramProgressListener;
  }

  public void setStripType(int paramInt)
  {
    this.mMosaicer.setStripType(paramInt);
  }

  public static abstract interface ProgressListener
  {
    public abstract void onProgress(boolean paramBoolean, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.MosaicFrameProcessor
 * JD-Core Version:    0.5.4
 */