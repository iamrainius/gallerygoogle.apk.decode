package com.android.gallery3d.filtershow.imageshow;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import com.android.gallery3d.filtershow.filters.ImageFilterGeometry;

public class GeometryMetadata
{
  private static final ImageFilterGeometry mImageFilter = new ImageFilterGeometry();
  private RectF mBounds = new RectF();
  private final RectF mCropBounds = new RectF();
  private FLIP mFlip = FLIP.NONE;
  private final RectF mPhotoBounds = new RectF();
  private float mRotation = 0.0F;
  private float mScaleFactor = 1.0F;
  private float mStraightenRotation = 0.0F;

  public GeometryMetadata()
  {
  }

  public GeometryMetadata(GeometryMetadata paramGeometryMetadata)
  {
    set(paramGeometryMetadata);
  }

  public static Matrix buildCenteredPhotoMatrix(RectF paramRectF1, RectF paramRectF2, float paramFloat1, float paramFloat2, FLIP paramFLIP, float[] paramArrayOfFloat)
  {
    Matrix localMatrix = buildPhotoMatrix(paramRectF1, paramRectF2, paramFloat1, paramFloat2, paramFLIP);
    float[] arrayOfFloat = new float[2];
    arrayOfFloat[0] = paramRectF1.centerX();
    arrayOfFloat[1] = paramRectF1.centerY();
    localMatrix.mapPoints(arrayOfFloat);
    concatRecenterMatrix(localMatrix, arrayOfFloat, paramArrayOfFloat);
    return localMatrix;
  }

  public static Matrix buildPhotoMatrix(RectF paramRectF1, RectF paramRectF2, float paramFloat1, float paramFloat2, FLIP paramFLIP)
  {
    Matrix localMatrix = new Matrix();
    localMatrix.setRotate(paramFloat2, paramRectF1.centerX(), paramRectF1.centerY());
    concatMirrorMatrix(localMatrix, paramRectF1.right, paramRectF1.bottom, paramFLIP);
    localMatrix.postRotate(paramFloat1, paramRectF2.centerX(), paramRectF2.centerY());
    return localMatrix;
  }

  public static Matrix buildWanderingCropMatrix(RectF paramRectF1, RectF paramRectF2, float paramFloat1, float paramFloat2, FLIP paramFLIP, float[] paramArrayOfFloat)
  {
    Matrix localMatrix = buildCenteredPhotoMatrix(paramRectF1, paramRectF2, paramFloat1, paramFloat2, paramFLIP, paramArrayOfFloat);
    localMatrix.preRotate(-paramFloat2, paramRectF1.centerX(), paramRectF1.centerY());
    return localMatrix;
  }

  protected static void concatHorizontalMatrix(Matrix paramMatrix, float paramFloat)
  {
    paramMatrix.postScale(-1.0F, 1.0F);
    paramMatrix.postTranslate(paramFloat, 0.0F);
  }

  public static void concatMirrorMatrix(Matrix paramMatrix, float paramFloat1, float paramFloat2, FLIP paramFLIP)
  {
    if (paramFLIP == FLIP.HORIZONTAL)
      concatHorizontalMatrix(paramMatrix, paramFloat1);
    do
    {
      return;
      if (paramFLIP != FLIP.VERTICAL)
        continue;
      concatVerticalMatrix(paramMatrix, paramFloat2);
      return;
    }
    while (paramFLIP != FLIP.BOTH);
    concatVerticalMatrix(paramMatrix, paramFloat2);
    concatHorizontalMatrix(paramMatrix, paramFloat1);
  }

  public static void concatRecenterMatrix(Matrix paramMatrix, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    paramMatrix.postTranslate(paramArrayOfFloat2[0] - paramArrayOfFloat1[0], paramArrayOfFloat2[1] - paramArrayOfFloat1[1]);
  }

  protected static void concatVerticalMatrix(Matrix paramMatrix, float paramFloat)
  {
    paramMatrix.postScale(1.0F, -1.0F);
    paramMatrix.postTranslate(0.0F, paramFloat);
  }

  public static Matrix getFlipMatrix(float paramFloat1, float paramFloat2, FLIP paramFLIP)
  {
    if (paramFLIP == FLIP.HORIZONTAL)
      return getHorizontalMatrix(paramFloat1);
    if (paramFLIP == FLIP.VERTICAL)
      return getVerticalMatrix(paramFloat2);
    if (paramFLIP == FLIP.BOTH)
    {
      Matrix localMatrix2 = getVerticalMatrix(paramFloat2);
      localMatrix2.postConcat(getHorizontalMatrix(paramFloat1));
      return localMatrix2;
    }
    Matrix localMatrix1 = new Matrix();
    localMatrix1.reset();
    return localMatrix1;
  }

  protected static Matrix getHorizontalMatrix(float paramFloat)
  {
    Matrix localMatrix = new Matrix();
    localMatrix.setScale(-1.0F, 1.0F);
    localMatrix.postTranslate(paramFloat, 0.0F);
    return localMatrix;
  }

  protected static Matrix getVerticalMatrix(float paramFloat)
  {
    Matrix localMatrix = new Matrix();
    localMatrix.setScale(1.0F, -1.0F);
    localMatrix.postTranslate(0.0F, paramFloat);
    return localMatrix;
  }

  public Bitmap apply(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    if (!hasModifications())
      return paramBitmap;
    mImageFilter.setGeometryMetadata(this);
    return mImageFilter.apply(paramBitmap, paramFloat, paramBoolean);
  }

  public Matrix buildGeometryMatrix(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    float f1 = paramFloat1 / 2.0F;
    float f2 = paramFloat2 / 2.0F;
    Matrix localMatrix = getFlipMatrix(paramFloat1, paramFloat2);
    localMatrix.postTranslate(-f1, -f2);
    localMatrix.postRotate(paramFloat6);
    localMatrix.postScale(paramFloat3, paramFloat3);
    localMatrix.postTranslate(paramFloat4, paramFloat5);
    return localMatrix;
  }

  public Matrix buildGeometryMatrix(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, boolean paramBoolean)
  {
    float f = this.mRotation;
    if (!paramBoolean)
      f += this.mStraightenRotation;
    return buildGeometryMatrix(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, f);
  }

  public Matrix buildTotalXform(float paramFloat1, float paramFloat2, float[] paramArrayOfFloat)
  {
    RectF localRectF1 = getPhotoBounds();
    RectF localRectF2 = getPreviewCropBounds();
    float f = GeometryMath.scale(localRectF1.width(), localRectF1.height(), paramFloat1, paramFloat2);
    RectF localRectF3 = GeometryMath.scaleRect(localRectF2, f);
    RectF localRectF4 = GeometryMath.scaleRect(localRectF1, f);
    Matrix localMatrix = buildWanderingCropMatrix(localRectF4, localRectF3, getRotation(), getStraightenRotation(), getFlipType(), paramArrayOfFloat);
    float[] arrayOfFloat = new float[2];
    arrayOfFloat[0] = localRectF3.centerX();
    arrayOfFloat[1] = localRectF3.centerY();
    localMatrix.mapPoints(arrayOfFloat);
    concatRecenterMatrix(localMatrix, arrayOfFloat, paramArrayOfFloat);
    localMatrix.preRotate(getStraightenRotation(), localRectF4.centerX(), localRectF4.centerY());
    return localMatrix;
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    GeometryMetadata localGeometryMetadata;
    do
    {
      return true;
      if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
        return false;
      localGeometryMetadata = (GeometryMetadata)paramObject;
    }
    while ((this.mScaleFactor == localGeometryMetadata.mScaleFactor) && (this.mRotation == localGeometryMetadata.mRotation) && (this.mStraightenRotation == localGeometryMetadata.mStraightenRotation) && (this.mFlip == localGeometryMetadata.mFlip) && (this.mCropBounds.equals(localGeometryMetadata.mCropBounds)) && (this.mPhotoBounds.equals(localGeometryMetadata.mPhotoBounds)));
    return false;
  }

  public RectF getCropBounds(Bitmap paramBitmap)
  {
    float f = GeometryMath.scale(this.mPhotoBounds.width(), this.mPhotoBounds.height(), paramBitmap.getWidth(), paramBitmap.getHeight());
    return new RectF(f * this.mCropBounds.left, f * this.mCropBounds.top, f * this.mCropBounds.right, f * this.mCropBounds.bottom);
  }

  public Matrix getFlipMatrix(float paramFloat1, float paramFloat2)
  {
    return getFlipMatrix(paramFloat1, paramFloat2, getFlipType());
  }

  public FLIP getFlipType()
  {
    return this.mFlip;
  }

  public RectF getPhotoBounds()
  {
    return new RectF(this.mPhotoBounds);
  }

  public RectF getPreviewCropBounds()
  {
    return new RectF(this.mCropBounds);
  }

  public float getRotation()
  {
    return this.mRotation;
  }

  public float getScaleFactor()
  {
    return this.mScaleFactor;
  }

  public float getStraightenRotation()
  {
    return this.mStraightenRotation;
  }

  public boolean hasModifications()
  {
    if (this.mScaleFactor != 1.0F);
    do
      return true;
    while ((this.mRotation != 0.0F) || (this.mStraightenRotation != 0.0F) || (!this.mCropBounds.equals(this.mPhotoBounds)) || (!this.mFlip.equals(FLIP.NONE)));
    return false;
  }

  public boolean hasSwitchedWidthHeight()
  {
    return (int)(this.mRotation / 90.0F) % 2 != 0;
  }

  public int hashCode()
  {
    return 31 * (31 * (31 * (31 * (31 * (713 + Float.floatToIntBits(this.mRotation)) + Float.floatToIntBits(this.mStraightenRotation)) + Float.floatToIntBits(this.mScaleFactor)) + this.mFlip.hashCode()) + this.mCropBounds.hashCode()) + this.mPhotoBounds.hashCode();
  }

  public void set(GeometryMetadata paramGeometryMetadata)
  {
    this.mScaleFactor = paramGeometryMetadata.mScaleFactor;
    this.mRotation = paramGeometryMetadata.mRotation;
    this.mStraightenRotation = paramGeometryMetadata.mStraightenRotation;
    this.mCropBounds.set(paramGeometryMetadata.mCropBounds);
    this.mPhotoBounds.set(paramGeometryMetadata.mPhotoBounds);
    this.mFlip = paramGeometryMetadata.mFlip;
    this.mBounds = paramGeometryMetadata.mBounds;
  }

  public void setCropBounds(RectF paramRectF)
  {
    this.mCropBounds.set(paramRectF);
  }

  public void setFlipType(FLIP paramFLIP)
  {
    this.mFlip = paramFLIP;
  }

  public void setPhotoBounds(RectF paramRectF)
  {
    this.mPhotoBounds.set(paramRectF);
  }

  public void setRotation(float paramFloat)
  {
    this.mRotation = paramFloat;
  }

  public void setScaleFactor(float paramFloat)
  {
    this.mScaleFactor = paramFloat;
  }

  public void setStraightenRotation(float paramFloat)
  {
    this.mStraightenRotation = paramFloat;
  }

  public String toString()
  {
    return super.getClass().getName() + "[" + "scale=" + this.mScaleFactor + ",rotation=" + this.mRotation + ",flip=" + this.mFlip + ",straighten=" + this.mStraightenRotation + ",cropRect=" + this.mCropBounds.toShortString() + ",photoRect=" + this.mPhotoBounds.toShortString() + "]";
  }

  public static enum FLIP
  {
    static
    {
      HORIZONTAL = new FLIP("HORIZONTAL", 2);
      BOTH = new FLIP("BOTH", 3);
      FLIP[] arrayOfFLIP = new FLIP[4];
      arrayOfFLIP[0] = NONE;
      arrayOfFLIP[1] = VERTICAL;
      arrayOfFLIP[2] = HORIZONTAL;
      arrayOfFLIP[3] = BOTH;
      $VALUES = arrayOfFLIP;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.imageshow.GeometryMetadata
 * JD-Core Version:    0.5.4
 */