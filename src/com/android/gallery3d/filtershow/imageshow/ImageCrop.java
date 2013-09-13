package com.android.gallery3d.filtershow.imageshow;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class ImageCrop extends ImageGeometry
{
  private static final Paint gPaint;
  private static int mTouchTolerance = 45;
  private final Paint borderPaint;
  private final Drawable cropIndicator;
  private final int indicatorSize;
  private float mAspectHeight = 1.0F;
  private float mAspectWidth = 1.0F;
  private final int mBorderColor = Color.argb(128, 255, 255, 255);
  private boolean mFirstDraw = true;
  private boolean mFixAspectRatio = false;
  private float mLastRot = 0.0F;
  private int movingEdges;

  static
  {
    gPaint = new Paint();
  }

  public ImageCrop(Context paramContext)
  {
    super(paramContext);
    Resources localResources = paramContext.getResources();
    this.cropIndicator = localResources.getDrawable(2130837567);
    this.indicatorSize = (int)localResources.getDimension(2131624049);
    this.borderPaint = new Paint();
    this.borderPaint.setStyle(Paint.Style.STROKE);
    this.borderPaint.setColor(this.mBorderColor);
    this.borderPaint.setStrokeWidth(2.0F);
  }

  public ImageCrop(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    Resources localResources = paramContext.getResources();
    this.cropIndicator = localResources.getDrawable(2130837567);
    this.indicatorSize = (int)localResources.getDimension(2131624049);
    this.borderPaint = new Paint();
    this.borderPaint.setStyle(Paint.Style.STROKE);
    this.borderPaint.setColor(this.mBorderColor);
    this.borderPaint.setStrokeWidth(2.0F);
  }

  private int bitCycleLeft(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = -1 + (1 << paramInt3);
    int j = paramInt1 & i;
    int k = paramInt2 % paramInt3;
    return j >> paramInt3 - k | (i & j << k | paramInt1 & (i ^ 0xFFFFFFFF));
  }

  private void cropSetup()
  {
    if (this.mFixAspectRatio)
    {
      RectF localRectF = getRotatedCropBounds();
      fixAspectRatio(localRectF, this.mAspectWidth, this.mAspectHeight);
      setCropBounds(getUnrotatedCropBounds(localRectF));
      return;
    }
    setCropBounds(getLocalCropBounds());
  }

  private void detectMovingEdges(float paramFloat1, float paramFloat2)
  {
    int i = 1;
    RectF localRectF = getCropBoundsDisplayed();
    this.movingEdges = 0;
    float f1 = Math.abs(paramFloat1 - localRectF.left);
    float f2 = Math.abs(paramFloat1 - localRectF.right);
    label65: float f4;
    int j;
    if ((f1 <= mTouchTolerance) && (f1 < f2))
    {
      this.movingEdges = (0x1 | this.movingEdges);
      float f3 = Math.abs(paramFloat2 - localRectF.top);
      f4 = Math.abs(paramFloat2 - localRectF.bottom);
      if (f3 > mTouchTolerance)
        break label206;
      j = i;
      label102: if (f3 >= f4)
        break label212;
      label110: if ((i & j) == 0)
        break label217;
    }
    for (this.movingEdges = (0x2 | this.movingEdges); ; this.movingEdges = (0x8 | this.movingEdges))
      do
      {
        if ((localRectF.contains(paramFloat1, paramFloat2)) && (this.movingEdges == 0))
          this.movingEdges = 16;
        if ((this.mFixAspectRatio) && (this.movingEdges != 16))
          this.movingEdges = fixEdgeToCorner(this.movingEdges);
        invalidate();
        return;
        if (f2 <= mTouchTolerance);
        this.movingEdges = (0x4 | this.movingEdges);
        break label65:
        label206: j = 0;
        break label102:
        label212: i = 0;
        label217: break label110:
      }
      while (f4 > mTouchTolerance);
  }

  private void drawIndicator(Canvas paramCanvas, Drawable paramDrawable, float paramFloat1, float paramFloat2)
  {
    int i = (int)paramFloat1 - this.indicatorSize / 2;
    int j = (int)paramFloat2 - this.indicatorSize / 2;
    paramDrawable.setBounds(i, j, i + this.indicatorSize, j + this.indicatorSize);
    paramDrawable.draw(paramCanvas);
  }

  private void drawRuleOfThird(Canvas paramCanvas, RectF paramRectF, Paint paramPaint)
  {
    float f1 = paramRectF.width() / 3.0F;
    float f2 = paramRectF.height() / 3.0F;
    float f3 = f1 + paramRectF.left;
    float f4 = f2 + paramRectF.top;
    for (int i = 0; i < 2; ++i)
    {
      float f6 = paramRectF.top;
      float f7 = paramRectF.bottom;
      paramCanvas.drawLine(f3, f6, f3, f7, paramPaint);
      f3 += f1;
    }
    int j = 0;
    float f5 = f4;
    while (j < 2)
    {
      paramCanvas.drawLine(paramRectF.left, f5, paramRectF.right, f5, paramPaint);
      f5 += f2;
      ++j;
    }
  }

  private int fixEdgeToCorner(int paramInt)
  {
    if (paramInt == 1)
      paramInt |= 2;
    if (paramInt == 2)
      paramInt |= 1;
    if (paramInt == 4)
      paramInt |= 8;
    if (paramInt == 8)
      paramInt |= 4;
    return paramInt;
  }

  private RectF fixedCornerResize(RectF paramRectF, int paramInt, float paramFloat1, float paramFloat2)
  {
    RectF localRectF;
    if (paramInt == 12)
      localRectF = new RectF(paramRectF.left, paramRectF.top, paramFloat1 + (paramRectF.left + paramRectF.width()), paramFloat2 + (paramRectF.top + paramRectF.height()));
    do
    {
      return localRectF;
      if (paramInt == 9)
        return new RectF(paramFloat1 + (paramRectF.right - paramRectF.width()), paramRectF.top, paramRectF.right, paramFloat2 + (paramRectF.top + paramRectF.height()));
      if (paramInt == 3)
        return new RectF(paramFloat1 + (paramRectF.right - paramRectF.width()), paramFloat2 + (paramRectF.bottom - paramRectF.height()), paramRectF.right, paramRectF.bottom);
      localRectF = null;
    }
    while (paramInt != 6);
    return new RectF(paramRectF.left, paramFloat2 + (paramRectF.bottom - paramRectF.height()), paramFloat1 + (paramRectF.left + paramRectF.width()), paramRectF.bottom);
  }

  private RectF getRotatedCropBounds()
  {
    RectF localRectF = new RectF(getLocalCropBounds());
    Matrix localMatrix = getCropRotationMatrix(getLocalRotation(), getLocalPhotoBounds());
    if (localMatrix == null)
      return null;
    localMatrix.mapRect(localRectF);
    return localRectF;
  }

  private RectF getRotatedStraightenBounds()
  {
    RectF localRectF = getUntranslatedStraightenCropBounds(getLocalPhotoBounds(), getLocalStraighten());
    Matrix localMatrix = getCropRotationMatrix(getLocalRotation(), getLocalPhotoBounds());
    if (localMatrix == null)
      return null;
    localMatrix.mapRect(localRectF);
    return localRectF;
  }

  private float getScaledMinWidthHeight()
  {
    RectF localRectF = new RectF(0.0F, 0.0F, getWidth(), getHeight());
    return 0.1F * Math.min(localRectF.width(), localRectF.height()) / computeScale(getWidth(), getHeight());
  }

  private RectF getUnrotatedCropBounds(RectF paramRectF)
  {
    Matrix localMatrix1 = getCropRotationMatrix(getLocalRotation(), getLocalPhotoBounds());
    RectF localRectF;
    if (localMatrix1 == null)
      localRectF = null;
    Matrix localMatrix2;
    do
    {
      return localRectF;
      localMatrix2 = new Matrix();
      if (!localMatrix1.invert(localMatrix2))
        return null;
      localRectF = new RectF(paramRectF);
    }
    while (localMatrix2.mapRect(localRectF));
    return null;
  }

  private void moveEdges(float paramFloat1, float paramFloat2)
  {
    RectF localRectF1 = getRotatedCropBounds();
    float f1 = getScaledMinWidthHeight();
    float f2 = computeScale(getWidth(), getHeight());
    float f3 = paramFloat1 / f2;
    float f4 = paramFloat2 / f2;
    int i = this.movingEdges;
    if ((this.mFixAspectRatio) && (i != 16))
    {
      if (i == 1)
        i |= 2;
      if (i == 2)
        i |= 1;
      if (i == 4)
        i |= 8;
      if (i == 8)
        i |= 4;
    }
    RectF localRectF4;
    float f12;
    label146: float f13;
    if (i == 16)
    {
      localRectF4 = getRotatedStraightenBounds();
      if (f3 > 0.0F)
      {
        f12 = Math.min(localRectF4.right - localRectF1.right, f3);
        if (f4 <= 0.0F)
          break label252;
        f13 = Math.min(localRectF4.bottom - localRectF1.bottom, f4);
        label170: localRectF1.offset(f12, f13);
      }
    }
    while (true)
    {
      this.movingEdges = i;
      Matrix localMatrix1 = getCropRotationMatrix(getLocalRotation(), getLocalPhotoBounds());
      Matrix localMatrix2 = new Matrix();
      if ((localMatrix1.invert(localMatrix2)) || (!localMatrix2.mapRect(localRectF1)));
      setCropBounds(localRectF1);
      label252: float f5;
      float f6;
      RectF localRectF3;
      Matrix localMatrix3;
      Matrix localMatrix4;
      do
      {
        return;
        f12 = Math.max(localRectF4.left - localRectF1.left, f3);
        break label146:
        f13 = Math.max(localRectF4.top - localRectF1.top, f4);
        break label170:
        int j = i & 0x1;
        f5 = 0.0F;
        if (j != 0)
          f5 = Math.min(f3 + localRectF1.left, localRectF1.right - f1) - localRectF1.left;
        int k = i & 0x2;
        f6 = 0.0F;
        if (k != 0)
          f6 = Math.min(f4 + localRectF1.top, localRectF1.bottom - f1) - localRectF1.top;
        if ((i & 0x4) != 0)
          f5 = Math.max(f3 + localRectF1.right, f1 + localRectF1.left) - localRectF1.right;
        if ((i & 0x8) != 0)
          f6 = Math.max(f4 + localRectF1.bottom, f1 + localRectF1.top) - localRectF1.bottom;
        if (!this.mFixAspectRatio)
          break label655;
        RectF localRectF2 = getCropBoundsDisplayed();
        float[] arrayOfFloat1 = new float[2];
        arrayOfFloat1[0] = localRectF2.left;
        arrayOfFloat1[1] = localRectF2.bottom;
        float[] arrayOfFloat2 = new float[2];
        arrayOfFloat2[0] = localRectF2.right;
        arrayOfFloat2[1] = localRectF2.top;
        if ((this.movingEdges == 3) || (this.movingEdges == 12))
        {
          arrayOfFloat1[1] = localRectF2.top;
          arrayOfFloat2[1] = localRectF2.bottom;
        }
        float[] arrayOfFloat3 = new float[2];
        arrayOfFloat3[0] = (arrayOfFloat1[0] - arrayOfFloat2[0]);
        arrayOfFloat3[1] = (arrayOfFloat1[1] - arrayOfFloat2[1]);
        float[] arrayOfFloat4 = { f5, f6 };
        float[] arrayOfFloat5 = GeometryMath.normalize(arrayOfFloat3);
        float f7 = GeometryMath.scalarProjection(arrayOfFloat4, arrayOfFloat5);
        float f8 = f7 * arrayOfFloat5[0];
        float f9 = f7 * arrayOfFloat5[1];
        float f10 = f8 * f2;
        float f11 = f9 * f2;
        localRectF3 = fixedCornerResize(localRectF2, i, f10, f11);
        localMatrix3 = getCropBoundDisplayMatrix();
        localMatrix4 = new Matrix();
      }
      while ((!localMatrix3.invert(localMatrix4)) || (!localMatrix4.mapRect(localRectF3)));
      setCropBounds(localRectF3);
      return;
      if ((i & 0x1) != 0)
        label655: localRectF1.left = (f5 + localRectF1.left);
      if ((i & 0x2) != 0)
        localRectF1.top = (f6 + localRectF1.top);
      if ((i & 0x4) != 0)
        localRectF1.right = (f5 + localRectF1.right);
      if ((i & 0x8) == 0)
        continue;
      localRectF1.bottom = (f6 + localRectF1.bottom);
    }
  }

  public static void setTouchTolerance(int paramInt)
  {
    mTouchTolerance = paramInt;
  }

  private void swapAspect()
  {
    float f = this.mAspectWidth;
    this.mAspectWidth = this.mAspectHeight;
    this.mAspectHeight = f;
  }

  public void apply(float paramFloat1, float paramFloat2)
  {
    this.mFixAspectRatio = true;
    this.mAspectWidth = paramFloat1;
    this.mAspectHeight = paramFloat2;
    setLocalCropBounds(getUntranslatedStraightenCropBounds(getLocalPhotoBounds(), getLocalStraighten()));
    cropSetup();
    saveAndSetPreset();
    invalidate();
  }

  public void applyClear()
  {
    this.mFixAspectRatio = false;
    setLocalCropBounds(getUntranslatedStraightenCropBounds(getLocalPhotoBounds(), getLocalStraighten()));
    cropSetup();
    saveAndSetPreset();
    invalidate();
  }

  public void applyOriginal()
  {
    this.mFixAspectRatio = true;
    RectF localRectF = getLocalPhotoBounds();
    float f1 = localRectF.width();
    float f2 = localRectF.height();
    float f3 = Math.min(f1, f2);
    this.mAspectWidth = (f1 / f3);
    this.mAspectHeight = (f2 / f3);
    setLocalCropBounds(getUntranslatedStraightenCropBounds(localRectF, getLocalStraighten()));
    cropSetup();
    saveAndSetPreset();
    invalidate();
  }

  protected int decoder(int paramInt, float paramFloat)
  {
    switch (constrainedRotation(paramFloat))
    {
    default:
      return paramInt;
    case 90:
      return bitCycleLeft(paramInt, 3, 4);
    case 180:
      return bitCycleLeft(paramInt, 2, 4);
    case 270:
    }
    return bitCycleLeft(paramInt, 1, 4);
  }

  protected void drawShape(Canvas paramCanvas, Bitmap paramBitmap)
  {
    boolean bool = true;
    gPaint.setAntiAlias(bool);
    gPaint.setFilterBitmap(bool);
    gPaint.setDither(bool);
    gPaint.setARGB(255, 255, 255, 255);
    if (this.mFirstDraw)
    {
      cropSetup();
      this.mFirstDraw = false;
    }
    float f = getLocalRotation();
    RectF localRectF1 = drawTransformed(paramCanvas, paramBitmap, gPaint);
    gPaint.setColor(this.mBorderColor);
    gPaint.setStrokeWidth(3.0F);
    gPaint.setStyle(Paint.Style.STROKE);
    drawRuleOfThird(paramCanvas, localRectF1, gPaint);
    gPaint.setColor(this.mBorderColor);
    gPaint.setStrokeWidth(3.0F);
    gPaint.setStyle(Paint.Style.STROKE);
    drawStraighten(paramCanvas, gPaint);
    int i = decoder(this.movingEdges, f);
    paramCanvas.save();
    paramCanvas.rotate(f, this.mCenterX, this.mCenterY);
    RectF localRectF2 = unrotatedCropBounds();
    if (i == 0);
    while (true)
    {
      if (((i & 0x2) != 0) || (bool))
        drawIndicator(paramCanvas, this.cropIndicator, localRectF2.centerX(), localRectF2.top);
      if (((i & 0x8) != 0) || (bool))
        drawIndicator(paramCanvas, this.cropIndicator, localRectF2.centerX(), localRectF2.bottom);
      if (((i & 0x1) != 0) || (bool))
        drawIndicator(paramCanvas, this.cropIndicator, localRectF2.left, localRectF2.centerY());
      if (((i & 0x4) != 0) || (bool))
        drawIndicator(paramCanvas, this.cropIndicator, localRectF2.right, localRectF2.centerY());
      paramCanvas.restore();
      return;
      bool = false;
    }
  }

  protected void gainedVisibility()
  {
    if ((int)((getLocalRotation() - this.mLastRot) / 90.0F) % 2 != 0)
      swapAspect();
    cropSetup();
    this.mFirstDraw = true;
  }

  protected Matrix getCropBoundDisplayMatrix()
  {
    Matrix localMatrix = getCropRotationMatrix(getLocalRotation(), getLocalPhotoBounds());
    if (localMatrix == null)
      localMatrix = new Matrix();
    float f = computeScale(getWidth(), getHeight());
    localMatrix.postTranslate(this.mXOffset, this.mYOffset);
    localMatrix.postScale(f, f, this.mCenterX, this.mCenterY);
    return localMatrix;
  }

  protected RectF getCropBoundsDisplayed()
  {
    RectF localRectF = new RectF(getLocalCropBounds());
    Matrix localMatrix1 = getCropRotationMatrix(getLocalRotation(), getLocalPhotoBounds());
    if (localMatrix1 == null)
      new Matrix();
    while (true)
    {
      Matrix localMatrix2 = new Matrix();
      float f = computeScale(getWidth(), getHeight());
      localMatrix2.setScale(f, f, this.mCenterX, this.mCenterY);
      localMatrix2.preTranslate(this.mXOffset, this.mYOffset);
      localMatrix2.mapRect(localRectF);
      return localRectF;
      localMatrix1.mapRect(localRectF);
    }
  }

  protected Matrix getCropRotationMatrix(float paramFloat, RectF paramRectF)
  {
    Matrix localMatrix = getLocalGeoFlipMatrix(paramRectF.width(), paramRectF.height());
    localMatrix.postRotate(paramFloat, paramRectF.centerX(), paramRectF.centerY());
    if (!localMatrix.rectStaysRect())
      localMatrix = null;
    return localMatrix;
  }

  public String getName()
  {
    return getContext().getString(2131362131);
  }

  public void imageLoaded()
  {
    super.imageLoaded();
    syncLocalToMasterGeometry();
    applyOriginal();
    invalidate();
  }

  protected void lostVisibility()
  {
    this.mLastRot = getLocalRotation();
  }

  public void resetParameter()
  {
    super.resetParameter();
    cropSetup();
  }

  protected void setActionDown(float paramFloat1, float paramFloat2)
  {
    super.setActionDown(paramFloat1, paramFloat2);
    detectMovingEdges(paramFloat1, paramFloat2);
  }

  protected void setActionMove(float paramFloat1, float paramFloat2)
  {
    if (this.movingEdges != 0)
      moveEdges(paramFloat1 - this.mCurrentX, paramFloat2 - this.mCurrentY);
    super.setActionMove(paramFloat1, paramFloat2);
  }

  protected void setActionUp()
  {
    super.setActionUp();
    this.movingEdges = 0;
  }

  public void setCropBounds(RectF paramRectF)
  {
    RectF localRectF1 = new RectF(paramRectF);
    float f1 = getScaledMinWidthHeight();
    float f2 = this.mAspectWidth;
    float f3 = this.mAspectHeight;
    if (this.mFixAspectRatio)
    {
      f1 /= f2 * f3;
      if ((int)(getLocalRotation() / 90.0F) % 2 != 0)
      {
        float f6 = f2;
        f2 = f3;
        f3 = f6;
      }
    }
    float f4 = localRectF1.width();
    float f5 = localRectF1.height();
    if (this.mFixAspectRatio)
      if ((f4 < f1 * f2) || (f5 < f1 * f3))
      {
        f4 = f1 * f2;
        f5 = f1 * f3;
      }
    while (true)
    {
      RectF localRectF2 = getLocalPhotoBounds();
      if (localRectF2.width() < f1)
        f4 = localRectF2.width();
      if (localRectF2.height() < f1)
        f5 = localRectF2.height();
      localRectF1.set(localRectF1.left, localRectF1.top, f4 + localRectF1.left, f5 + localRectF1.top);
      localRectF1.intersect(getUntranslatedStraightenCropBounds(getLocalPhotoBounds(), getLocalStraighten()));
      if (this.mFixAspectRatio)
        fixAspectRatio(localRectF1, f2, f3);
      setLocalCropBounds(localRectF1);
      invalidate();
      return;
      if (f4 < f1)
        f4 = f1;
      if (f5 >= f1)
        continue;
      f5 = f1;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.imageshow.ImageCrop
 * JD-Core Version:    0.5.4
 */