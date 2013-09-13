package com.android.gallery3d.ui;

import android.content.Context;
import android.graphics.Rect;
import android.widget.Scroller;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.RangeArray;
import com.android.gallery3d.util.RangeIntArray;

class PositionController
{
  private static final int[] ANIM_TIME = { 0, 0, 600, 400, 300, 300, 0, 0, 0, 700 };
  private static final int[] CENTER_OUT_INDEX = new int[7];
  private static final int HORIZONTAL_SLACK;
  private static final int IMAGE_GAP = GalleryUtils.dpToPixel(16);
  private int mBoundBottom;
  private int mBoundLeft;
  private int mBoundRight;
  private int mBoundTop;
  private RangeArray<Box> mBoxes = new RangeArray(-3, 3);
  private boolean mConstrained = true;
  private Rect mConstrainedFrame = new Rect();
  private boolean mExtraScalingRange = false;
  private boolean mFilmMode = false;
  private FilmRatio mFilmRatio = new FilmRatio(null);
  private Scroller mFilmScroller;
  private float mFocusX;
  private float mFocusY;
  private RangeArray<Gap> mGaps = new RangeArray(-3, 2);
  private boolean mHasNext;
  private boolean mHasPrev;
  private boolean mInScale;
  private Listener mListener;
  private volatile Rect mOpenAnimationRect;
  private FlingScroller mPageScroller;
  private Platform mPlatform = new Platform(null);
  boolean mPopFromTop;
  private RangeArray<Rect> mRects = new RangeArray(-3, 3);
  private RangeArray<Box> mTempBoxes = new RangeArray(-3, 3);
  private RangeArray<Gap> mTempGaps = new RangeArray(-3, 2);
  private int mViewH = 1200;
  private int mViewW = 1200;

  static
  {
    HORIZONTAL_SLACK = GalleryUtils.dpToPixel(12);
    for (int i = 0; i < CENTER_OUT_INDEX.length; ++i)
    {
      int j = (i + 1) / 2;
      if ((i & 0x1) == 0)
        j = -j;
      CENTER_OUT_INDEX[i] = j;
    }
  }

  public PositionController(Context paramContext, Listener paramListener)
  {
    this.mListener = paramListener;
    this.mPageScroller = new FlingScroller();
    this.mFilmScroller = new Scroller(paramContext, null, false);
    initPlatform();
    for (int i = -3; i <= 3; ++i)
    {
      this.mBoxes.put(i, new Box(null));
      initBox(i);
      this.mRects.put(i, new Rect());
    }
    for (int j = -3; j < 3; ++j)
    {
      this.mGaps.put(j, new Gap(null));
      initGap(j);
    }
  }

  private void calculateStableBound(float paramFloat)
  {
    calculateStableBound(paramFloat, 0);
  }

  private void calculateStableBound(float paramFloat, int paramInt)
  {
    Box localBox = (Box)this.mBoxes.get(0);
    int i = widthOf(localBox, paramFloat);
    int j = heightOf(localBox, paramFloat);
    this.mBoundLeft = ((1 + this.mViewW) / 2 - (i + 1) / 2 - paramInt);
    this.mBoundRight = (paramInt + (i / 2 - this.mViewW / 2));
    this.mBoundTop = ((1 + this.mViewH) / 2 - (j + 1) / 2);
    this.mBoundBottom = (j / 2 - this.mViewH / 2);
    if (viewTallerThanScaledImage(paramFloat))
    {
      this.mBoundBottom = 0;
      this.mBoundTop = 0;
    }
    if (!viewWiderThanScaledImage(paramFloat))
      return;
    int k = this.mPlatform.mDefaultX;
    this.mBoundRight = k;
    this.mBoundLeft = k;
  }

  private boolean canScroll()
  {
    Box localBox = (Box)this.mBoxes.get(0);
    if (localBox.mAnimationStartTime == -1L)
      return true;
    switch (localBox.mAnimationKind)
    {
    case 0:
    case 6:
    case 7:
    }
    return false;
  }

  private void convertBoxToRect(int paramInt)
  {
    Box localBox = (Box)this.mBoxes.get(paramInt);
    Rect localRect1 = (Rect)this.mRects.get(paramInt);
    int i = localBox.mCurrentY + this.mPlatform.mCurrentY + this.mViewH / 2;
    int j = widthOf(localBox);
    int k = heightOf(localBox);
    if (paramInt == 0)
    {
      localRect1.left = (this.mPlatform.mCurrentX + this.mViewW / 2 - j / 2);
      localRect1.right = (j + localRect1.left);
    }
    while (true)
    {
      localRect1.top = (i - k / 2);
      localRect1.bottom = (k + localRect1.top);
      return;
      if (paramInt > 0)
      {
        Rect localRect3 = (Rect)this.mRects.get(paramInt - 1);
        Gap localGap2 = (Gap)this.mGaps.get(paramInt - 1);
        localRect1.left = (localRect3.right + localGap2.mCurrentGap);
        localRect1.right = (j + localRect1.left);
      }
      Rect localRect2 = (Rect)this.mRects.get(paramInt + 1);
      Gap localGap1 = (Gap)this.mGaps.get(paramInt);
      localRect1.right = (localRect2.left - localGap1.mCurrentGap);
      localRect1.left = (localRect1.right - j);
    }
  }

  private int gapToSide(Box paramBox)
  {
    return (int)(0.5F + (this.mViewW - getMinimalScale(paramBox) * paramBox.mImageW) / 2.0F);
  }

  private int getDefaultGapSize(int paramInt)
  {
    if (this.mFilmMode)
      return IMAGE_GAP;
    Box localBox1 = (Box)this.mBoxes.get(paramInt);
    Box localBox2 = (Box)this.mBoxes.get(paramInt + 1);
    return IMAGE_GAP + Math.max(gapToSide(localBox1), gapToSide(localBox2));
  }

  private float getMaximalScale(Box paramBox)
  {
    if (this.mFilmMode)
      return getMinimalScale(paramBox);
    if ((this.mConstrained) && (!this.mConstrainedFrame.isEmpty()))
      return getMinimalScale(paramBox);
    return 4.0F;
  }

  private float getMinimalScale(Box paramBox)
  {
    float f1 = 1.0F;
    float f2 = 1.0F;
    int i;
    int j;
    if ((!this.mFilmMode) && (this.mConstrained) && (!this.mConstrainedFrame.isEmpty()) && (paramBox == this.mBoxes.get(0)))
    {
      i = this.mConstrainedFrame.width();
      j = this.mConstrainedFrame.height();
      if (this.mFilmMode)
      {
        label58: if (this.mViewH <= this.mViewW)
          break label131;
        f1 = 0.7F;
        f2 = 0.48F;
      }
    }
    while (true)
    {
      return Math.min(4.0F, Math.min(f1 * i / paramBox.mImageW, f2 * j / paramBox.mImageH));
      i = this.mViewW;
      j = this.mViewH;
      break label58:
      label131: f1 = 0.7F;
      f2 = 0.7F;
    }
  }

  private float getTargetScale(Box paramBox)
  {
    if (paramBox.mAnimationStartTime == -1L)
      return paramBox.mCurrentScale;
    return paramBox.mToScale;
  }

  private int heightOf(Box paramBox)
  {
    return (int)(0.5F + paramBox.mImageH * paramBox.mCurrentScale);
  }

  private int heightOf(Box paramBox, float paramFloat)
  {
    return (int)(0.5F + paramFloat * paramBox.mImageH);
  }

  private void initBox(int paramInt)
  {
    Box localBox = (Box)this.mBoxes.get(paramInt);
    localBox.mImageW = this.mViewW;
    localBox.mImageH = this.mViewH;
    localBox.mUseViewSize = true;
    localBox.mScaleMin = getMinimalScale(localBox);
    localBox.mScaleMax = getMaximalScale(localBox);
    localBox.mCurrentY = 0;
    localBox.mCurrentScale = localBox.mScaleMin;
    localBox.mAnimationStartTime = -1L;
    localBox.mAnimationKind = -1;
  }

  private void initBox(int paramInt, PhotoView.Size paramSize)
  {
    if ((paramSize.width == 0) || (paramSize.height == 0))
    {
      initBox(paramInt);
      return;
    }
    Box localBox = (Box)this.mBoxes.get(paramInt);
    localBox.mImageW = paramSize.width;
    localBox.mImageH = paramSize.height;
    localBox.mUseViewSize = false;
    localBox.mScaleMin = getMinimalScale(localBox);
    localBox.mScaleMax = getMaximalScale(localBox);
    localBox.mCurrentY = 0;
    localBox.mCurrentScale = localBox.mScaleMin;
    localBox.mAnimationStartTime = -1L;
    localBox.mAnimationKind = -1;
  }

  private void initGap(int paramInt)
  {
    Gap localGap = (Gap)this.mGaps.get(paramInt);
    localGap.mDefaultSize = getDefaultGapSize(paramInt);
    localGap.mCurrentGap = localGap.mDefaultSize;
    localGap.mAnimationStartTime = -1L;
  }

  private void initGap(int paramInt1, int paramInt2)
  {
    Gap localGap = (Gap)this.mGaps.get(paramInt1);
    localGap.mDefaultSize = getDefaultGapSize(paramInt1);
    localGap.mCurrentGap = paramInt2;
    localGap.mAnimationStartTime = -1L;
  }

  private void initPlatform()
  {
    this.mPlatform.updateDefaultXY();
    this.mPlatform.mCurrentX = this.mPlatform.mDefaultX;
    this.mPlatform.mCurrentY = this.mPlatform.mDefaultY;
    this.mPlatform.mAnimationStartTime = -1L;
  }

  private static boolean isAlmostEqual(float paramFloat1, float paramFloat2)
  {
    float f = paramFloat1 - paramFloat2;
    if (f < 0.0F)
      f = -f;
    return f < 0.02F;
  }

  private void layoutAndSetPosition()
  {
    for (int i = 0; i < 7; ++i)
      convertBoxToRect(CENTER_OUT_INDEX[i]);
  }

  private void redraw()
  {
    layoutAndSetPosition();
    this.mListener.invalidate();
  }

  private boolean setBoxSize(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    Box localBox = (Box)this.mBoxes.get(paramInt1);
    boolean bool = localBox.mUseViewSize;
    if ((!bool) && (paramBoolean));
    do
    {
      return false;
      localBox.mUseViewSize = paramBoolean;
    }
    while ((paramInt2 == localBox.mImageW) && (paramInt3 == localBox.mImageH));
    float f;
    if (paramInt2 > paramInt3)
    {
      f = localBox.mImageW / paramInt2;
      label73: localBox.mImageW = paramInt2;
      localBox.mImageH = paramInt3;
      if ((((!bool) || (paramBoolean))) && (this.mFilmMode))
        break label163;
      localBox.mCurrentScale = getMinimalScale(localBox);
      localBox.mAnimationStartTime = -1L;
    }
    while (true)
    {
      if (paramInt1 == 0)
      {
        this.mFocusX /= f;
        this.mFocusY /= f;
      }
      return true;
      f = localBox.mImageH / paramInt3;
      break label73:
      label163: localBox.mCurrentScale = (f * localBox.mCurrentScale);
      localBox.mFromScale = (f * localBox.mFromScale);
      localBox.mToScale = (f * localBox.mToScale);
    }
  }

  private void snapAndRedraw()
  {
    this.mPlatform.startSnapback();
    for (int i = -3; i <= 3; ++i)
      ((Box)this.mBoxes.get(i)).startSnapback();
    for (int j = -3; j < 3; ++j)
      ((Gap)this.mGaps.get(j)).startSnapback();
    this.mFilmRatio.startSnapback();
    redraw();
  }

  private boolean startAnimation(int paramInt1, int paramInt2, float paramFloat, int paramInt3)
  {
    boolean bool = false | this.mPlatform.doAnimation(paramInt1, this.mPlatform.mDefaultY, paramInt3) | ((Box)this.mBoxes.get(0)).doAnimation(paramInt2, paramFloat, paramInt3);
    if (bool)
      redraw();
    return bool;
  }

  private boolean startOpeningAnimationIfNeeded()
  {
    if (this.mOpenAnimationRect == null);
    Box localBox;
    do
    {
      return false;
      localBox = (Box)this.mBoxes.get(0);
    }
    while (localBox.mUseViewSize);
    Rect localRect = this.mOpenAnimationRect;
    this.mOpenAnimationRect = null;
    this.mPlatform.mCurrentX = (localRect.centerX() - this.mViewW / 2);
    localBox.mCurrentY = (localRect.centerY() - this.mViewH / 2);
    localBox.mCurrentScale = Math.max(localRect.width() / localBox.mImageW, localRect.height() / localBox.mImageH);
    startAnimation(this.mPlatform.mDefaultX, 0, localBox.mScaleMin, 5);
    for (int i = -1; i < 1; ++i)
    {
      Gap localGap = (Gap)this.mGaps.get(i);
      localGap.mCurrentGap = this.mViewW;
      localGap.doAnimation(localGap.mDefaultSize, 5);
    }
    return true;
  }

  private void updateScaleAndGapLimit()
  {
    for (int i = -3; i <= 3; ++i)
    {
      Box localBox = (Box)this.mBoxes.get(i);
      localBox.mScaleMin = getMinimalScale(localBox);
      localBox.mScaleMax = getMaximalScale(localBox);
    }
    for (int j = -3; j < 3; ++j)
      ((Gap)this.mGaps.get(j)).mDefaultSize = getDefaultGapSize(j);
  }

  private boolean viewTallerThanScaledImage(float paramFloat)
  {
    return this.mViewH >= heightOf((Box)this.mBoxes.get(0), paramFloat);
  }

  private boolean viewWiderThanScaledImage(float paramFloat)
  {
    return this.mViewW >= widthOf((Box)this.mBoxes.get(0), paramFloat);
  }

  private int widthOf(Box paramBox)
  {
    return (int)(0.5F + paramBox.mImageW * paramBox.mCurrentScale);
  }

  private int widthOf(Box paramBox, float paramFloat)
  {
    return (int)(0.5F + paramFloat * paramBox.mImageW);
  }

  public void advanceAnimation()
  {
    boolean bool = false | this.mPlatform.advanceAnimation();
    for (int i = -3; i <= 3; ++i)
      bool |= ((Box)this.mBoxes.get(i)).advanceAnimation();
    for (int j = -3; j < 3; ++j)
      bool |= ((Gap)this.mGaps.get(j)).advanceAnimation();
    if (!((bool | this.mFilmRatio.advanceAnimation())))
      return;
    redraw();
  }

  public void beginScale(float paramFloat1, float paramFloat2)
  {
    float f1 = paramFloat1 - this.mViewW / 2;
    float f2 = paramFloat2 - this.mViewH / 2;
    Box localBox = (Box)this.mBoxes.get(0);
    Platform localPlatform = this.mPlatform;
    this.mInScale = true;
    this.mFocusX = (int)(0.5F + (f1 - localPlatform.mCurrentX) / localBox.mCurrentScale);
    this.mFocusY = (int)(0.5F + (f2 - localBox.mCurrentY) / localBox.mCurrentScale);
  }

  public void endScale()
  {
    this.mInScale = false;
    snapAndRedraw();
  }

  public boolean flingFilmX(int paramInt)
  {
    if (paramInt == 0);
    Box localBox;
    Platform localPlatform;
    int i;
    do
    {
      return false;
      localBox = (Box)this.mBoxes.get(0);
      localPlatform = this.mPlatform;
      i = localPlatform.mDefaultX;
    }
    while (((!this.mHasPrev) && (localPlatform.mCurrentX >= i)) || ((!this.mHasNext) && (localPlatform.mCurrentX <= i)));
    this.mFilmScroller.fling(localPlatform.mCurrentX, 0, paramInt, 0, -2147483648, 2147483647, 0, 0);
    return startAnimation(this.mFilmScroller.getFinalX(), localBox.mCurrentY, localBox.mCurrentScale, 7);
  }

  public int flingFilmY(int paramInt1, int paramInt2)
  {
    Box localBox = (Box)this.mBoxes.get(paramInt1);
    int i = heightOf(localBox);
    int j;
    if ((paramInt2 < 0) || ((paramInt2 == 0) && (localBox.mCurrentY <= 0)))
    {
      j = -this.mViewH / 2 - (i + 1) / 2 - 3;
      label52: if (paramInt2 == 0)
        break label136;
    }
    for (int k = Math.min(400, (int)(1000.0F * Math.abs(j - localBox.mCurrentY) / Math.abs(paramInt2))); ; k = 200)
    {
      ANIM_TIME[8] = k;
      if (localBox.doAnimation(j, localBox.mCurrentScale, 8) == 0)
        break;
      redraw();
      return k;
      j = 3 + ((1 + this.mViewH) / 2 + i / 2);
      label136: break label52:
    }
    return -1;
  }

  public boolean flingPage(int paramInt1, int paramInt2)
  {
    Box localBox = (Box)this.mBoxes.get(0);
    Platform localPlatform = this.mPlatform;
    if ((viewWiderThanScaledImage(localBox.mCurrentScale)) && (viewTallerThanScaledImage(localBox.mCurrentScale)))
      return false;
    int i = getImageAtEdges();
    if (((paramInt1 > 0) && ((i & 0x1) != 0)) || ((paramInt1 < 0) && ((i & 0x2) != 0)))
      paramInt1 = 0;
    if (((paramInt2 > 0) && ((i & 0x4) != 0)) || ((paramInt2 < 0) && ((i & 0x8) != 0)))
      paramInt2 = 0;
    if ((paramInt1 == 0) && (paramInt2 == 0))
      return false;
    FlingScroller localFlingScroller = this.mPageScroller;
    int j = localPlatform.mCurrentX;
    int k = localBox.mCurrentY;
    int l = this.mBoundLeft;
    int i1 = this.mBoundRight;
    int i2 = this.mBoundTop;
    int i3 = this.mBoundBottom;
    localFlingScroller.fling(j, k, paramInt1, paramInt2, l, i1, i2, i3);
    int i4 = this.mPageScroller.getFinalX();
    int i5 = this.mPageScroller.getFinalY();
    ANIM_TIME[6] = this.mPageScroller.getDuration();
    return startAnimation(i4, i5, localBox.mCurrentScale, 6);
  }

  public void forceImageSize(int paramInt, PhotoView.Size paramSize)
  {
    if ((paramSize.width == 0) || (paramSize.height == 0))
      return;
    Box localBox = (Box)this.mBoxes.get(paramInt);
    localBox.mImageW = paramSize.width;
    localBox.mImageH = paramSize.height;
  }

  public float getFilmRatio()
  {
    return this.mFilmRatio.mCurrentRatio;
  }

  public int getImageAtEdges()
  {
    Box localBox = (Box)this.mBoxes.get(0);
    Platform localPlatform = this.mPlatform;
    calculateStableBound(localBox.mCurrentScale);
    int i = localPlatform.mCurrentX;
    int j = this.mBoundLeft;
    int k = 0;
    if (i <= j)
      k = 0x0 | 0x2;
    if (localPlatform.mCurrentX >= this.mBoundRight)
      k |= 1;
    if (localBox.mCurrentY <= this.mBoundTop)
      k |= 8;
    if (localBox.mCurrentY >= this.mBoundBottom)
      k |= 4;
    return k;
  }

  public int getImageHeight()
  {
    return ((Box)this.mBoxes.get(0)).mImageH;
  }

  public float getImageScale()
  {
    return ((Box)this.mBoxes.get(0)).mCurrentScale;
  }

  public int getImageWidth()
  {
    return ((Box)this.mBoxes.get(0)).mImageW;
  }

  public Rect getPosition(int paramInt)
  {
    return (Rect)this.mRects.get(paramInt);
  }

  public boolean hasDeletingBox()
  {
    for (int i = -3; i <= 3; ++i)
      if (((Box)this.mBoxes.get(i)).mAnimationKind == 8)
        return true;
    return false;
  }

  public int hitTest(int paramInt1, int paramInt2)
  {
    for (int i = 0; i < 7; ++i)
    {
      int j = CENTER_OUT_INDEX[i];
      if (((Rect)this.mRects.get(j)).contains(paramInt1, paramInt2))
        return j;
    }
    return 2147483647;
  }

  public boolean inOpeningAnimation()
  {
    return ((this.mPlatform.mAnimationKind == 5) && (this.mPlatform.mAnimationStartTime != -1L)) || ((((Box)this.mBoxes.get(0)).mAnimationKind == 5) && (((Box)this.mBoxes.get(0)).mAnimationStartTime != -1L));
  }

  public boolean isAtMinimalScale()
  {
    Box localBox = (Box)this.mBoxes.get(0);
    return isAlmostEqual(localBox.mCurrentScale, localBox.mScaleMin);
  }

  public boolean isCenter()
  {
    Box localBox = (Box)this.mBoxes.get(0);
    int i = this.mPlatform.mCurrentX;
    int j = this.mPlatform.mDefaultX;
    int k = 0;
    if (i == j)
    {
      int l = localBox.mCurrentY;
      k = 0;
      if (l == 0)
        k = 1;
    }
    return k;
  }

  public boolean isScrolling()
  {
    return (this.mPlatform.mAnimationStartTime != -1L) && (this.mPlatform.mCurrentX != this.mPlatform.mToX);
  }

  public void moveBox(int[] paramArrayOfInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PhotoView.Size[] paramArrayOfSize)
  {
    this.mHasPrev = paramBoolean1;
    this.mHasNext = paramBoolean2;
    RangeIntArray localRangeIntArray = new RangeIntArray(paramArrayOfInt, -3, 3);
    layoutAndSetPosition();
    for (int i = -3; i <= 3; ++i)
      ((Box)this.mBoxes.get(i)).mAbsoluteX = (((Rect)this.mRects.get(i)).centerX() - this.mViewW / 2);
    for (int j = -3; j <= 3; ++j)
    {
      this.mTempBoxes.put(j, this.mBoxes.get(j));
      this.mBoxes.put(j, null);
    }
    for (int k = -3; k < 3; ++k)
    {
      this.mTempGaps.put(k, this.mGaps.get(k));
      this.mGaps.put(k, null);
    }
    int l = -3;
    if (l <= 3)
    {
      label172: int i27 = localRangeIntArray.get(l);
      if (i27 == 2147483647);
      while (true)
      {
        ++l;
        break label172:
        this.mBoxes.put(l, this.mTempBoxes.get(i27));
        this.mTempBoxes.put(i27, null);
      }
    }
    int i1 = -3;
    if (i1 < 3)
    {
      label236: int i25 = localRangeIntArray.get(i1);
      if (i25 == 2147483647);
      while (true)
      {
        ++i1;
        break label236:
        int i26 = localRangeIntArray.get(i1 + 1);
        if ((i26 == 2147483647) || (i25 + 1 != i26))
          continue;
        this.mGaps.put(i1, this.mTempGaps.get(i25));
        this.mTempGaps.put(i25, null);
      }
    }
    int i2 = -3;
    int i3 = -3;
    if (i3 <= 3)
    {
      label332: if (this.mBoxes.get(i3) != null);
      while (true)
      {
        ++i3;
        break label332:
        while (this.mTempBoxes.get(i2) == null)
          ++i2;
        RangeArray localRangeArray3 = this.mBoxes;
        RangeArray localRangeArray4 = this.mTempBoxes;
        int i24 = i2 + 1;
        localRangeArray3.put(i3, localRangeArray4.get(i2));
        initBox(i3, paramArrayOfSize[(i3 + 3)]);
        i2 = i24;
      }
    }
    int i4 = -3;
    label430: int i5;
    label452: int i6;
    if ((i4 > 3) || (localRangeIntArray.get(i4) != 2147483647))
    {
      i5 = 3;
      if ((i5 >= -3) && (localRangeIntArray.get(i5) == 2147483647))
        break label547;
      if (i4 > 3)
      {
        ((Box)this.mBoxes.get(0)).mAbsoluteX = this.mPlatform.mCurrentX;
        i5 = 0;
        i4 = 0;
      }
      i6 = Math.max(0, i4 + 1);
      label515: if (i6 >= i5)
        break label683;
      if (localRangeIntArray.get(i6) == 2147483647)
        break label553;
    }
    while (true)
    {
      ++i6;
      break label515:
      ++i4;
      break label430:
      label547: --i5;
      break label452:
      label553: Box localBox9 = (Box)this.mBoxes.get(i6 - 1);
      Box localBox10 = (Box)this.mBoxes.get(i6);
      int i22 = widthOf(localBox9);
      int i23 = widthOf(localBox10);
      localBox10.mAbsoluteX = (localBox9.mAbsoluteX + (i22 - i22 / 2) + i23 / 2 + getDefaultGapSize(i6));
      if (this.mPopFromTop)
        localBox10.mCurrentY = (-(this.mViewH / 2 + heightOf(localBox10) / 2));
      localBox10.mCurrentY = (this.mViewH / 2 + heightOf(localBox10) / 2);
    }
    label683: int i7 = Math.min(-1, i5 - 1);
    if (i7 > i4)
    {
      label693: if (localRangeIntArray.get(i7) != 2147483647);
      while (true)
      {
        --i7;
        break label693:
        Box localBox7 = (Box)this.mBoxes.get(i7 + 1);
        Box localBox8 = (Box)this.mBoxes.get(i7);
        int i20 = widthOf(localBox7);
        int i21 = widthOf(localBox8);
        localBox8.mAbsoluteX = (localBox7.mAbsoluteX - i20 / 2 - (i21 - i21 / 2) - getDefaultGapSize(i7));
        if (this.mPopFromTop)
          localBox8.mCurrentY = (-(this.mViewH / 2 + heightOf(localBox8) / 2));
        localBox8.mCurrentY = (this.mViewH / 2 + heightOf(localBox8) / 2);
      }
    }
    int i8 = -3;
    int i9 = -3;
    if (i9 < 3)
    {
      label857: if (this.mGaps.get(i9) != null);
      while (true)
      {
        ++i9;
        break label857:
        while (this.mTempGaps.get(i8) == null)
          ++i8;
        RangeArray localRangeArray1 = this.mGaps;
        RangeArray localRangeArray2 = this.mTempGaps;
        int i17 = i8 + 1;
        localRangeArray1.put(i9, localRangeArray2.get(i8));
        Box localBox5 = (Box)this.mBoxes.get(i9);
        Box localBox6 = (Box)this.mBoxes.get(i9 + 1);
        int i18 = widthOf(localBox5);
        int i19 = widthOf(localBox6);
        if ((i9 >= i4) && (i9 < i5))
        {
          initGap(i9, localBox6.mAbsoluteX - localBox5.mAbsoluteX - i19 / 2 - (i18 - i18 / 2));
          i8 = i17;
        }
        initGap(i9);
        i8 = i17;
      }
    }
    for (int i10 = i4 - 1; i10 >= -3; --i10)
    {
      Box localBox3 = (Box)this.mBoxes.get(i10 + 1);
      Box localBox4 = (Box)this.mBoxes.get(i10);
      int i15 = widthOf(localBox3);
      int i16 = widthOf(localBox4);
      Gap localGap2 = (Gap)this.mGaps.get(i10);
      localBox4.mAbsoluteX = (localBox3.mAbsoluteX - i15 / 2 - (i16 - i16 / 2) - localGap2.mCurrentGap);
    }
    for (int i11 = i5 + 1; i11 <= 3; ++i11)
    {
      Box localBox1 = (Box)this.mBoxes.get(i11 - 1);
      Box localBox2 = (Box)this.mBoxes.get(i11);
      int i13 = widthOf(localBox1);
      int i14 = widthOf(localBox2);
      Gap localGap1 = (Gap)this.mGaps.get(i11 - 1);
      localBox2.mAbsoluteX = (localBox1.mAbsoluteX + (i13 - i13 / 2) + i14 / 2 + localGap1.mCurrentGap);
    }
    int i12 = ((Box)this.mBoxes.get(0)).mAbsoluteX - this.mPlatform.mCurrentX;
    Platform localPlatform1 = this.mPlatform;
    localPlatform1.mCurrentX = (i12 + localPlatform1.mCurrentX);
    Platform localPlatform2 = this.mPlatform;
    localPlatform2.mFromX = (i12 + localPlatform2.mFromX);
    Platform localPlatform3 = this.mPlatform;
    localPlatform3.mToX = (i12 + localPlatform3.mToX);
    Platform localPlatform4 = this.mPlatform;
    localPlatform4.mFlingOffset = (i12 + localPlatform4.mFlingOffset);
    if (this.mConstrained != paramBoolean3)
    {
      this.mConstrained = paramBoolean3;
      this.mPlatform.updateDefaultXY();
      updateScaleAndGapLimit();
    }
    snapAndRedraw();
  }

  public void resetToFullView()
  {
    Box localBox = (Box)this.mBoxes.get(0);
    startAnimation(this.mPlatform.mDefaultX, 0, localBox.mScaleMin, 4);
  }

  public int scaleBy(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    int i = 1;
    float f1 = paramFloat2 - this.mViewW / 2;
    float f2 = paramFloat3 - this.mViewH / 2;
    Box localBox = (Box)this.mBoxes.get(0);
    Platform localPlatform = this.mPlatform;
    float f3 = localBox.clampScale(paramFloat1 * getTargetScale(localBox));
    int j;
    label73: int k;
    if (this.mFilmMode)
    {
      j = localPlatform.mCurrentX;
      if (!this.mFilmMode)
        break label137;
      k = localBox.mCurrentY;
      label87: startAnimation(j, k, f3, i);
      if (f3 >= localBox.mScaleMin)
        break label157;
      i = -1;
    }
    do
    {
      return i;
      j = (int)(0.5F + (f1 - f3 * this.mFocusX));
      break label73:
      label137: k = (int)(0.5F + (f2 - f3 * this.mFocusY));
      label157: break label87:
    }
    while (f3 > localBox.mScaleMax);
    return 0;
  }

  public void scrollFilmX(int paramInt)
  {
    if (!canScroll())
      return;
    Box localBox = (Box)this.mBoxes.get(0);
    Platform localPlatform = this.mPlatform;
    if (localBox.mAnimationStartTime != -1L)
      switch (localBox.mAnimationKind)
      {
      default:
        return;
      case 0:
      case 6:
      case 7:
      }
    int i = paramInt + localPlatform.mCurrentX - this.mPlatform.mDefaultX;
    if ((!this.mHasPrev) && (i > 0))
      this.mListener.onPull(i, 1);
    for (i = 0; ; i = 0)
    {
      do
      {
        startAnimation(i + this.mPlatform.mDefaultX, localBox.mCurrentY, localBox.mCurrentScale, 0);
        return;
      }
      while ((this.mHasNext) || (i >= 0));
      this.mListener.onPull(-i, 3);
    }
  }

  public void scrollFilmY(int paramInt1, int paramInt2)
  {
    if (!canScroll())
      return;
    Box localBox = (Box)this.mBoxes.get(paramInt1);
    localBox.doAnimation(paramInt2 + localBox.mCurrentY, localBox.mCurrentScale, 0);
    redraw();
  }

  public void scrollPage(int paramInt1, int paramInt2)
  {
    if (!canScroll())
      return;
    Box localBox = (Box)this.mBoxes.get(0);
    Platform localPlatform = this.mPlatform;
    calculateStableBound(localBox.mCurrentScale);
    int i = paramInt1 + localPlatform.mCurrentX;
    int j = paramInt2 + localBox.mCurrentY;
    if (this.mBoundTop != this.mBoundBottom)
    {
      if (j >= this.mBoundTop)
        break label161;
      this.mListener.onPull(this.mBoundTop - j, 2);
    }
    label88: int k = Utils.clamp(j, this.mBoundTop, this.mBoundBottom);
    if ((!this.mHasPrev) && (i > this.mBoundRight))
    {
      int i1 = i - this.mBoundRight;
      this.mListener.onPull(i1, 1);
    }
    for (i = this.mBoundRight; ; i = this.mBoundLeft)
    {
      do
      {
        startAnimation(i, k, localBox.mCurrentScale, 0);
        return;
        label161: if (j > this.mBoundBottom);
        this.mListener.onPull(j - this.mBoundBottom, 0);
        break label88:
      }
      while ((this.mHasNext) || (i >= this.mBoundLeft));
      int l = this.mBoundLeft - i;
      this.mListener.onPull(l, 3);
    }
  }

  public void setConstrainedFrame(Rect paramRect)
  {
    if (this.mConstrainedFrame.equals(paramRect))
      return;
    this.mConstrainedFrame.set(paramRect);
    this.mPlatform.updateDefaultXY();
    updateScaleAndGapLimit();
    snapAndRedraw();
  }

  public void setExtraScalingRange(boolean paramBoolean)
  {
    if (this.mExtraScalingRange == paramBoolean);
    do
    {
      return;
      this.mExtraScalingRange = paramBoolean;
    }
    while (paramBoolean);
    snapAndRedraw();
  }

  public void setFilmMode(boolean paramBoolean)
  {
    if (paramBoolean == this.mFilmMode)
      return;
    this.mFilmMode = paramBoolean;
    this.mPlatform.updateDefaultXY();
    updateScaleAndGapLimit();
    stopAnimation();
    snapAndRedraw();
  }

  public void setImageSize(int paramInt, PhotoView.Size paramSize, Rect paramRect)
  {
    if ((paramSize.width == 0) || (paramSize.height == 0));
    int i;
    do
    {
      return;
      i = 0;
      if (paramRect == null)
        continue;
      boolean bool = this.mConstrainedFrame.equals(paramRect);
      i = 0;
      if (bool)
        continue;
      this.mConstrainedFrame.set(paramRect);
      this.mPlatform.updateDefaultXY();
      i = 1;
    }
    while ((i | setBoxSize(paramInt, paramSize.width, paramSize.height, false)) == 0);
    updateScaleAndGapLimit();
    snapAndRedraw();
  }

  public void setPopFromTop(boolean paramBoolean)
  {
    this.mPopFromTop = paramBoolean;
  }

  public void setViewSize(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == this.mViewW) && (paramInt2 == this.mViewH));
    do
    {
      return;
      boolean bool = isAtMinimalScale();
      this.mViewW = paramInt1;
      this.mViewH = paramInt2;
      initPlatform();
      for (int i = -3; i <= 3; ++i)
        setBoxSize(i, paramInt1, paramInt2, true);
      updateScaleAndGapLimit();
      if (!bool)
        continue;
      Box localBox = (Box)this.mBoxes.get(0);
      localBox.mCurrentScale = localBox.mScaleMin;
    }
    while (startOpeningAnimationIfNeeded());
    skipToFinalPosition();
  }

  public void skipAnimation()
  {
    if (this.mPlatform.mAnimationStartTime != -1L)
    {
      this.mPlatform.mCurrentX = this.mPlatform.mToX;
      this.mPlatform.mCurrentY = this.mPlatform.mToY;
      this.mPlatform.mAnimationStartTime = -1L;
    }
    int i = -3;
    if (i <= 3)
    {
      label55: Box localBox = (Box)this.mBoxes.get(i);
      if (localBox.mAnimationStartTime == -1L);
      while (true)
      {
        ++i;
        break label55:
        localBox.mCurrentY = localBox.mToY;
        localBox.mCurrentScale = localBox.mToScale;
        localBox.mAnimationStartTime = -1L;
      }
    }
    int j = -3;
    if (j < 3)
    {
      label125: Gap localGap = (Gap)this.mGaps.get(j);
      if (localGap.mAnimationStartTime == -1L);
      while (true)
      {
        ++j;
        break label125:
        localGap.mCurrentGap = localGap.mToGap;
        localGap.mAnimationStartTime = -1L;
      }
    }
    redraw();
  }

  public void skipToFinalPosition()
  {
    stopAnimation();
    snapAndRedraw();
    skipAnimation();
  }

  public void snapback()
  {
    snapAndRedraw();
  }

  public void startCaptureAnimationSlide(int paramInt)
  {
    Box localBox1 = (Box)this.mBoxes.get(0);
    Box localBox2 = (Box)this.mBoxes.get(paramInt);
    Gap localGap = (Gap)this.mGaps.get(paramInt);
    this.mPlatform.doAnimation(this.mPlatform.mDefaultX, this.mPlatform.mDefaultY, 9);
    localBox1.doAnimation(0, localBox1.mScaleMin, 9);
    localBox2.doAnimation(0, localBox2.mScaleMin, 9);
    localGap.doAnimation(localGap.mDefaultSize, 9);
    redraw();
  }

  public void startHorizontalSlide()
  {
    Box localBox = (Box)this.mBoxes.get(0);
    startAnimation(this.mPlatform.mDefaultX, 0, localBox.mScaleMin, 3);
  }

  public void stopAnimation()
  {
    this.mPlatform.mAnimationStartTime = -1L;
    for (int i = -3; i <= 3; ++i)
      ((Box)this.mBoxes.get(i)).mAnimationStartTime = -1L;
    for (int j = -3; j < 3; ++j)
      ((Gap)this.mGaps.get(j)).mAnimationStartTime = -1L;
  }

  public void stopScrolling()
  {
    if (this.mPlatform.mAnimationStartTime == -1L)
      return;
    if (this.mFilmMode)
      this.mFilmScroller.forceFinished(true);
    Platform localPlatform1 = this.mPlatform;
    Platform localPlatform2 = this.mPlatform;
    int i = this.mPlatform.mCurrentX;
    localPlatform2.mToX = i;
    localPlatform1.mFromX = i;
  }

  public void zoomIn(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    float f1 = paramFloat1 - this.mViewW / 2;
    float f2 = paramFloat2 - this.mViewH / 2;
    Box localBox = (Box)this.mBoxes.get(0);
    float f3 = (f1 - this.mPlatform.mCurrentX) / localBox.mCurrentScale;
    float f4 = (f2 - localBox.mCurrentY) / localBox.mCurrentScale;
    int i = (int)(0.5F + paramFloat3 * -f3);
    int j = (int)(0.5F + paramFloat3 * -f4);
    calculateStableBound(paramFloat3);
    startAnimation(Utils.clamp(i, this.mBoundLeft, this.mBoundRight), Utils.clamp(j, this.mBoundTop, this.mBoundBottom), Utils.clamp(paramFloat3, localBox.mScaleMin, localBox.mScaleMax), 4);
  }

  private static abstract class Animatable
  {
    public int mAnimationDuration;
    public int mAnimationKind;
    public long mAnimationStartTime;

    private static float applyInterpolationCurve(int paramInt, float paramFloat)
    {
      float f = 1.0F - paramFloat;
      switch (paramInt)
      {
      default:
        return paramFloat;
      case 0:
      case 6:
      case 7:
      case 8:
      case 9:
        return 1.0F - f;
      case 1:
      case 5:
        return 1.0F - f * f;
      case 2:
      case 3:
      case 4:
      }
      return 1.0F - f * (f * (f * (f * f)));
    }

    public boolean advanceAnimation()
    {
      if (this.mAnimationStartTime == -1L)
        return false;
      if (this.mAnimationStartTime == -2L)
      {
        this.mAnimationStartTime = -1L;
        return startSnapback();
      }
      float f1;
      label45: float f2;
      if (this.mAnimationDuration == 0)
      {
        f1 = 1.0F;
        if (f1 < 1.0F)
          break label89;
        f2 = 1.0F;
      }
      while (true)
      {
        if (interpolate(f2))
          this.mAnimationStartTime = -2L;
        return true;
        f1 = (float)(AnimationTime.get() - this.mAnimationStartTime) / this.mAnimationDuration;
        break label45:
        label89: f2 = applyInterpolationCurve(this.mAnimationKind, f1);
      }
    }

    protected abstract boolean interpolate(float paramFloat);

    public abstract boolean startSnapback();
  }

  private class Box extends PositionController.Animatable
  {
    public int mAbsoluteX;
    public float mCurrentScale;
    public int mCurrentY;
    public float mFromScale;
    public int mFromY;
    public int mImageH;
    public int mImageW;
    public float mScaleMax;
    public float mScaleMin;
    public float mToScale;
    public int mToY;
    public boolean mUseViewSize;

    private Box()
    {
      super(null);
    }

    private boolean doAnimation(int paramInt1, float paramFloat, int paramInt2)
    {
      float f = clampScale(paramFloat);
      if ((this.mCurrentY == paramInt1) && (this.mCurrentScale == f) && (paramInt2 != 9))
        return false;
      this.mAnimationKind = paramInt2;
      this.mFromY = this.mCurrentY;
      this.mFromScale = this.mCurrentScale;
      this.mToY = paramInt1;
      this.mToScale = f;
      this.mAnimationStartTime = AnimationTime.startTime();
      this.mAnimationDuration = PositionController.ANIM_TIME[paramInt2];
      advanceAnimation();
      return true;
    }

    private boolean interpolateFlingPage(float paramFloat)
    {
      PositionController.this.mPageScroller.computeScrollOffset(paramFloat);
      PositionController.this.calculateStableBound(this.mCurrentScale);
      int i = this.mCurrentY;
      this.mCurrentY = PositionController.this.mPageScroller.getCurrY();
      if ((i > PositionController.this.mBoundTop) && (this.mCurrentY == PositionController.this.mBoundTop))
      {
        int l = (int)(0.5F + -PositionController.this.mPageScroller.getCurrVelocityY());
        PositionController.this.mListener.onAbsorb(l, 2);
      }
      while (true)
      {
        boolean bool = paramFloat < 1.0F;
        int k = 0;
        if (!bool)
          k = 1;
        return k;
        if ((i >= PositionController.this.mBoundBottom) || (this.mCurrentY != PositionController.this.mBoundBottom))
          continue;
        int j = (int)(0.5F + PositionController.this.mPageScroller.getCurrVelocityY());
        PositionController.this.mListener.onAbsorb(j, 0);
      }
    }

    private boolean interpolateLinear(float paramFloat)
    {
      if (paramFloat >= 1.0F)
      {
        this.mCurrentY = this.mToY;
        this.mCurrentScale = this.mToScale;
      }
      do
      {
        return true;
        this.mCurrentY = (int)(this.mFromY + paramFloat * (this.mToY - this.mFromY));
        this.mCurrentScale = (this.mFromScale + paramFloat * (this.mToScale - this.mFromScale));
        if (this.mAnimationKind != 9)
          continue;
        this.mCurrentScale = (CaptureAnimation.calculateScale(paramFloat) * this.mCurrentScale);
        return false;
      }
      while ((this.mCurrentY == this.mToY) && (this.mCurrentScale == this.mToScale));
      return false;
    }

    public float clampScale(float paramFloat)
    {
      return Utils.clamp(paramFloat, 0.7F * this.mScaleMin, 1.4F * this.mScaleMax);
    }

    protected boolean interpolate(float paramFloat)
    {
      if (this.mAnimationKind == 6)
        return interpolateFlingPage(paramFloat);
      return interpolateLinear(paramFloat);
    }

    public boolean startSnapback()
    {
      if (this.mAnimationStartTime != -1L);
      do
        return false;
      while (((this.mAnimationKind == 0) && (PositionController.this.mListener.isHoldingDown())) || ((this.mAnimationKind == 8) && (PositionController.this.mListener.isHoldingDelete())) || ((PositionController.this.mInScale) && (this == PositionController.this.mBoxes.get(0))));
      int i = this.mCurrentY;
      float f2;
      label123: float f3;
      label142: float f1;
      if (this == PositionController.this.mBoxes.get(0))
        if (PositionController.this.mExtraScalingRange)
        {
          f2 = 0.7F * this.mScaleMin;
          if (!PositionController.this.mExtraScalingRange)
            break label200;
          f3 = 1.4F * this.mScaleMax;
          f1 = Utils.clamp(this.mCurrentScale, f2, f3);
          if (!PositionController.this.mFilmMode)
            break label209;
        }
      for (int j = 0; ; j = 0)
      {
        while (true)
        {
          if ((this.mCurrentY != j) || (this.mCurrentScale != f1));
          return doAnimation(j, f1, 2);
          f2 = this.mScaleMin;
          break label123:
          label200: f3 = this.mScaleMax;
          break label142:
          label209: PositionController.this.calculateStableBound(f1, PositionController.HORIZONTAL_SLACK);
          if (PositionController.this.viewTallerThanScaledImage(f1) == 0)
            i += (int)(0.5F + (this.mCurrentScale - f1) * PositionController.this.mFocusY);
          j = Utils.clamp(i, PositionController.this.mBoundTop, PositionController.this.mBoundBottom);
        }
        f1 = this.mScaleMin;
      }
    }
  }

  private class FilmRatio extends PositionController.Animatable
  {
    public float mCurrentRatio;
    public float mFromRatio;
    public float mToRatio;

    private FilmRatio()
    {
      super(null);
    }

    private boolean doAnimation(float paramFloat, int paramInt)
    {
      this.mAnimationKind = paramInt;
      this.mFromRatio = this.mCurrentRatio;
      this.mToRatio = paramFloat;
      this.mAnimationStartTime = AnimationTime.startTime();
      this.mAnimationDuration = PositionController.ANIM_TIME[this.mAnimationKind];
      advanceAnimation();
      return true;
    }

    protected boolean interpolate(float paramFloat)
    {
      if (paramFloat >= 1.0F)
        this.mCurrentRatio = this.mToRatio;
      do
      {
        return true;
        this.mCurrentRatio = (this.mFromRatio + paramFloat * (this.mToRatio - this.mFromRatio));
      }
      while (this.mCurrentRatio == this.mToRatio);
      return false;
    }

    public boolean startSnapback()
    {
      float f;
      if (PositionController.this.mFilmMode)
        f = 1.0F;
      while (f == this.mToRatio)
      {
        return false;
        f = 0.0F;
      }
      return doAnimation(f, 2);
    }
  }

  private class Gap extends PositionController.Animatable
  {
    public int mCurrentGap;
    public int mDefaultSize;
    public int mFromGap;
    public int mToGap;

    private Gap()
    {
      super(null);
    }

    public boolean doAnimation(int paramInt1, int paramInt2)
    {
      if ((this.mCurrentGap == paramInt1) && (paramInt2 != 9))
        return false;
      this.mAnimationKind = paramInt2;
      this.mFromGap = this.mCurrentGap;
      this.mToGap = paramInt1;
      this.mAnimationStartTime = AnimationTime.startTime();
      this.mAnimationDuration = PositionController.ANIM_TIME[this.mAnimationKind];
      advanceAnimation();
      return true;
    }

    protected boolean interpolate(float paramFloat)
    {
      if (paramFloat >= 1.0F)
        this.mCurrentGap = this.mToGap;
      do
      {
        return true;
        this.mCurrentGap = (int)(this.mFromGap + paramFloat * (this.mToGap - this.mFromGap));
        if (this.mAnimationKind != 9)
          continue;
        this.mCurrentGap = (int)(CaptureAnimation.calculateScale(paramFloat) * this.mCurrentGap);
        return false;
      }
      while (this.mCurrentGap == this.mToGap);
      return false;
    }

    public boolean startSnapback()
    {
      if (this.mAnimationStartTime != -1L)
        return false;
      return doAnimation(this.mDefaultSize, 2);
    }
  }

  public static abstract interface Listener
  {
    public abstract void invalidate();

    public abstract boolean isHoldingDelete();

    public abstract boolean isHoldingDown();

    public abstract void onAbsorb(int paramInt1, int paramInt2);

    public abstract void onPull(int paramInt1, int paramInt2);
  }

  private class Platform extends PositionController.Animatable
  {
    public int mCurrentX;
    public int mCurrentY;
    public int mDefaultX;
    public int mDefaultY;
    public int mFlingOffset;
    public int mFromX;
    public int mFromY;
    public int mToX;
    public int mToY;

    private Platform()
    {
      super(null);
    }

    private boolean doAnimation(int paramInt1, int paramInt2, int paramInt3)
    {
      if ((this.mCurrentX == paramInt1) && (this.mCurrentY == paramInt2))
        return false;
      this.mAnimationKind = paramInt3;
      this.mFromX = this.mCurrentX;
      this.mFromY = this.mCurrentY;
      this.mToX = paramInt1;
      this.mToY = paramInt2;
      this.mAnimationStartTime = AnimationTime.startTime();
      this.mAnimationDuration = PositionController.ANIM_TIME[paramInt3];
      this.mFlingOffset = 0;
      advanceAnimation();
      return true;
    }

    private boolean interpolateFlingFilm(float paramFloat)
    {
      PositionController.this.mFilmScroller.computeScrollOffset();
      this.mCurrentX = (PositionController.this.mFilmScroller.getCurrX() + this.mFlingOffset);
      int i = -1;
      if (this.mCurrentX < this.mDefaultX)
        if (PositionController.this.mHasNext);
      for (i = 3; ; i = 1)
        do
        {
          if (i != -1)
          {
            PositionController.this.mFilmScroller.forceFinished(true);
            this.mCurrentX = this.mDefaultX;
          }
          return PositionController.this.mFilmScroller.isFinished();
        }
        while ((this.mCurrentX <= this.mDefaultX) || (PositionController.this.mHasPrev));
    }

    private boolean interpolateFlingPage(float paramFloat)
    {
      PositionController.this.mPageScroller.computeScrollOffset(paramFloat);
      PositionController.Box localBox = (PositionController.Box)PositionController.this.mBoxes.get(0);
      PositionController.this.calculateStableBound(localBox.mCurrentScale);
      int i = this.mCurrentX;
      this.mCurrentX = PositionController.this.mPageScroller.getCurrX();
      if ((i > PositionController.this.mBoundLeft) && (this.mCurrentX == PositionController.this.mBoundLeft))
      {
        int k = (int)(0.5F + -PositionController.this.mPageScroller.getCurrVelocityX());
        PositionController.this.mListener.onAbsorb(k, 3);
      }
      while (paramFloat >= 1.0F)
      {
        return true;
        if ((i >= PositionController.this.mBoundRight) || (this.mCurrentX != PositionController.this.mBoundRight))
          continue;
        int j = (int)(0.5F + PositionController.this.mPageScroller.getCurrVelocityX());
        PositionController.this.mListener.onAbsorb(j, 1);
      }
      return false;
    }

    private boolean interpolateLinear(float paramFloat)
    {
      if (paramFloat >= 1.0F)
      {
        this.mCurrentX = this.mToX;
        this.mCurrentY = this.mToY;
      }
      do
      {
        return true;
        if (this.mAnimationKind == 9)
          paramFloat = CaptureAnimation.calculateSlide(paramFloat);
        this.mCurrentX = (int)(this.mFromX + paramFloat * (this.mToX - this.mFromX));
        this.mCurrentY = (int)(this.mFromY + paramFloat * (this.mToY - this.mFromY));
        if (this.mAnimationKind == 9)
          return false;
      }
      while ((this.mCurrentX == this.mToX) && (this.mCurrentY == this.mToY));
      return false;
    }

    protected boolean interpolate(float paramFloat)
    {
      if (this.mAnimationKind == 6)
        return interpolateFlingPage(paramFloat);
      if (this.mAnimationKind == 7)
        return interpolateFlingFilm(paramFloat);
      return interpolateLinear(paramFloat);
    }

    public boolean startSnapback()
    {
      if (this.mAnimationStartTime != -1L);
      do
        return false;
      while (((this.mAnimationKind == 0) && (PositionController.this.mListener.isHoldingDown())) || (PositionController.this.mInScale));
      PositionController.Box localBox = (PositionController.Box)PositionController.this.mBoxes.get(0);
      float f1;
      label78: float f2;
      label96: float f3;
      int i;
      int j;
      if (PositionController.this.mExtraScalingRange)
      {
        f1 = 0.7F * localBox.mScaleMin;
        if (!PositionController.this.mExtraScalingRange)
          break label171;
        f2 = 1.4F * localBox.mScaleMax;
        f3 = Utils.clamp(localBox.mCurrentScale, f1, f2);
        i = this.mCurrentX;
        j = this.mDefaultY;
        if (!PositionController.this.mFilmMode)
          break label179;
      }
      for (int k = this.mDefaultX; ; k = Utils.clamp(i, PositionController.this.mBoundLeft, PositionController.this.mBoundRight))
      {
        if ((this.mCurrentX != k) || (this.mCurrentY != j));
        return doAnimation(k, j, 2);
        f1 = localBox.mScaleMin;
        break label78:
        label171: f2 = localBox.mScaleMax;
        break label96:
        label179: PositionController.this.calculateStableBound(f3, PositionController.HORIZONTAL_SLACK);
        if (PositionController.this.viewWiderThanScaledImage(f3) != 0)
          continue;
        i += (int)(0.5F + (localBox.mCurrentScale - f3) * PositionController.this.mFocusX);
      }
    }

    public void updateDefaultXY()
    {
      if ((PositionController.this.mConstrained) && (!PositionController.this.mConstrainedFrame.isEmpty()))
      {
        this.mDefaultX = (PositionController.this.mConstrainedFrame.centerX() - PositionController.this.mViewW / 2);
        boolean bool = PositionController.this.mFilmMode;
        int i = 0;
        if (bool);
        while (true)
        {
          this.mDefaultY = i;
          return;
          i = PositionController.this.mConstrainedFrame.centerY() - PositionController.this.mViewH / 2;
        }
      }
      this.mDefaultX = 0;
      this.mDefaultY = 0;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.PositionController
 * JD-Core Version:    0.5.4
 */