package com.android.gallery3d.filtershow.imageshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.android.gallery3d.filtershow.HistoryAdapter;
import com.android.gallery3d.filtershow.PanelController;
import com.android.gallery3d.filtershow.presets.ImagePreset;

public abstract class ImageGeometry extends ImageSlave
{
  protected float mCenterX;
  protected float mCenterY;
  protected float mCurrentX;
  protected float mCurrentY;
  private boolean mHasDrawn = false;
  private RectF mLocalDisplayBounds = null;
  private GeometryMetadata mLocalGeometry = null;
  protected MODES mMode = MODES.NONE;
  protected float mTouchCenterX;
  protected float mTouchCenterY;
  private boolean mVisibilityGained = false;
  protected float mXOffset = 0.0F;
  protected float mYOffset = 0.0F;

  public ImageGeometry(Context paramContext)
  {
    super(paramContext);
  }

  public ImageGeometry(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  protected static float angleFor(float paramFloat1, float paramFloat2)
  {
    return (float)(180.0D * Math.atan2(paramFloat1, paramFloat2) / 3.141592653589793D);
  }

  private void calculateLocalScalingFactorAndOffset()
  {
    if ((this.mLocalGeometry == null) || (this.mLocalDisplayBounds == null))
      return;
    RectF localRectF = this.mLocalGeometry.getPhotoBounds();
    float f1 = localRectF.width();
    float f2 = localRectF.height();
    float f3 = this.mLocalDisplayBounds.width();
    float f4 = this.mLocalDisplayBounds.height();
    this.mCenterX = (f3 / 2.0F);
    this.mCenterY = (f4 / 2.0F);
    this.mYOffset = ((f4 - f2) / 2.0F);
    this.mXOffset = ((f3 - f1) / 2.0F);
    updateScale();
  }

  protected static void drawShadows(Canvas paramCanvas, Paint paramPaint, RectF paramRectF1, RectF paramRectF2, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    paramCanvas.save();
    paramCanvas.rotate(paramFloat1, paramFloat2, paramFloat3);
    float f1 = paramRectF2.left - paramRectF2.right;
    float f2 = paramRectF2.top - paramRectF2.bottom;
    float f3 = (float)Math.sqrt(f1 * f1 + f2 * f2) / 2.0F;
    float f4 = paramFloat2 - f3;
    float f5 = paramFloat2 + f3;
    float f6 = paramFloat3 - f3;
    float f7 = paramFloat3 + f3;
    paramCanvas.drawRect(f4, f6, paramRectF1.right, paramRectF1.top, paramPaint);
    paramCanvas.drawRect(f4, paramRectF1.top, paramRectF1.left, f7, paramPaint);
    paramCanvas.drawRect(paramRectF1.left, paramRectF1.bottom, f5, f7, paramPaint);
    paramCanvas.drawRect(paramRectF1.right, f6, f5, paramRectF1.bottom, paramPaint);
    paramCanvas.rotate(-paramFloat1, paramFloat2, paramFloat3);
    paramCanvas.restore();
  }

  protected static void fixAspectRatio(RectF paramRectF, float paramFloat1, float paramFloat2)
  {
    float f1 = Math.min(paramRectF.width() / paramFloat1, paramRectF.height() / paramFloat2);
    float f2 = paramRectF.centerX();
    float f3 = paramRectF.centerY();
    float f4 = f1 * paramFloat1 / 2.0F;
    float f5 = f1 * paramFloat2 / 2.0F;
    paramRectF.set(f2 - f4, f3 - f5, f2 + f4, f3 + f5);
  }

  public static RectF getUntranslatedStraightenCropBounds(RectF paramRectF, float paramFloat)
  {
    float f1 = paramFloat;
    if (f1 < 0.0F)
      f1 = -f1;
    double d1 = Math.toRadians(f1);
    double d2 = Math.sin(d1);
    double d3 = Math.cos(d1);
    double d4 = paramRectF.width();
    double d5 = paramRectF.height();
    double d6 = Math.min(d5 * d5 / (d4 * d2 + d5 * d3), d5 * d4 / (d4 * d3 + d5 * d2));
    double d7 = d6 * d4 / d5;
    float f2 = (float)(0.5D * (d4 - d7));
    float f3 = (float)(0.5D * (d5 - d6));
    float f4 = (float)(d7 + f2);
    float f5 = (float)(d6 + f3);
    RectF localRectF = new RectF(f2, f3, f4, f5);
    return localRectF;
  }

  private void setupLocalDisplayBounds(RectF paramRectF)
  {
    this.mLocalDisplayBounds = paramRectF;
    calculateLocalScalingFactorAndOffset();
  }

  protected static int snappedAngle(float paramFloat)
  {
    float f = paramFloat % 90.0F;
    int i = (int)(paramFloat / 90.0F);
    if (f < -45.0F)
      --i;
    while (true)
    {
      return i * 90;
      if (f <= 45.0F)
        continue;
      ++i;
    }
  }

  protected float computeScale(float paramFloat1, float paramFloat2)
  {
    return GeometryMath.scale(this.mLocalGeometry.getPhotoBounds().width(), this.mLocalGeometry.getPhotoBounds().height(), paramFloat1, paramFloat2);
  }

  protected int constrainedRotation(float paramFloat)
  {
    int i = (int)(paramFloat % 360.0F / 90.0F);
    if (i < 0)
      i += 4;
    return i * 90;
  }

  protected void drawShadows(Canvas paramCanvas, Paint paramPaint, RectF paramRectF)
  {
    drawShadows(paramCanvas, paramPaint, paramRectF, new RectF(0.0F, 0.0F, getWidth(), getHeight()), getLocalRotation(), getWidth() / 2, getHeight() / 2);
  }

  protected void drawShape(Canvas paramCanvas, Bitmap paramBitmap)
  {
  }

  protected void drawStraighten(Canvas paramCanvas, Paint paramPaint)
  {
    RectF localRectF = straightenBounds();
    paramCanvas.save();
    paramCanvas.drawRect(localRectF, paramPaint);
    paramCanvas.restore();
  }

  protected RectF drawTransformed(Canvas paramCanvas, Bitmap paramBitmap, Paint paramPaint)
  {
    paramPaint.setARGB(255, 0, 0, 0);
    RectF localRectF1 = getLocalPhotoBounds();
    RectF localRectF2 = getLocalCropBounds();
    float f = computeScale(getWidth(), getHeight());
    if ((int)(getLocalRotation() / 90.0F) % 2 != 0)
      f = computeScale(getHeight(), getWidth());
    RectF localRectF3 = GeometryMath.scaleRect(localRectF2, f);
    RectF localRectF4 = GeometryMath.scaleRect(localRectF1, f);
    float[] arrayOfFloat = new float[2];
    arrayOfFloat[0] = (getWidth() / 2.0F);
    arrayOfFloat[1] = (getHeight() / 2.0F);
    Matrix localMatrix = GeometryMetadata.buildCenteredPhotoMatrix(localRectF4, localRectF3, getLocalRotation(), getLocalStraighten(), getLocalFlip(), arrayOfFloat);
    GeometryMetadata.buildWanderingCropMatrix(localRectF4, localRectF3, getLocalRotation(), getLocalStraighten(), getLocalFlip(), arrayOfFloat).mapRect(localRectF3);
    Path localPath = new Path();
    localPath.addRect(localRectF3, Path.Direction.CCW);
    localMatrix.preScale(f, f);
    paramCanvas.save();
    paramCanvas.drawBitmap(paramBitmap, localMatrix, paramPaint);
    paramCanvas.restore();
    paramPaint.setColor(-1);
    paramPaint.setStyle(Paint.Style.STROKE);
    paramPaint.setStrokeWidth(2.0F);
    paramCanvas.drawPath(localPath, paramPaint);
    return localRectF3;
  }

  protected void drawTransformedCropped(Canvas paramCanvas, Bitmap paramBitmap, Paint paramPaint)
  {
    RectF localRectF1 = getLocalPhotoBounds();
    RectF localRectF2 = getLocalCropBounds();
    float f1 = localRectF2.width();
    float f2 = localRectF2.height();
    float f3 = GeometryMath.scale(f1, f2, getWidth(), getHeight());
    if ((int)(getLocalRotation() / 90.0F) % 2 != 0)
      f3 = GeometryMath.scale(f1, f2, getHeight(), getWidth());
    RectF localRectF3 = GeometryMath.scaleRect(localRectF2, f3);
    RectF localRectF4 = GeometryMath.scaleRect(localRectF1, f3);
    float[] arrayOfFloat1 = new float[2];
    arrayOfFloat1[0] = (getWidth() / 2.0F);
    arrayOfFloat1[1] = (getHeight() / 2.0F);
    Matrix localMatrix = GeometryMetadata.buildWanderingCropMatrix(localRectF4, localRectF3, getLocalRotation(), getLocalStraighten(), getLocalFlip(), arrayOfFloat1);
    float[] arrayOfFloat2 = new float[2];
    arrayOfFloat2[0] = localRectF3.centerX();
    arrayOfFloat2[1] = localRectF3.centerY();
    localMatrix.mapPoints(arrayOfFloat2);
    GeometryMetadata.concatRecenterMatrix(localMatrix, arrayOfFloat2, arrayOfFloat1);
    localMatrix.preRotate(getLocalStraighten(), localRectF4.centerX(), localRectF4.centerY());
    localMatrix.preScale(f3, f3);
    paramPaint.setARGB(255, 0, 0, 0);
    paramCanvas.save();
    paramCanvas.drawBitmap(paramBitmap, localMatrix, paramPaint);
    paramCanvas.restore();
    paramPaint.setARGB(255, 0, 0, 0);
    paramPaint.setStyle(Paint.Style.FILL);
    localRectF3.offset(arrayOfFloat1[0] - localRectF3.centerX(), arrayOfFloat1[1] - localRectF3.centerY());
    drawShadows(paramCanvas, paramPaint, localRectF3);
  }

  protected void gainedVisibility()
  {
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
    return (angleFor(f3, f4) - f5) % 360.0F;
  }

  protected Matrix getGeoMatrix(RectF paramRectF, boolean paramBoolean)
  {
    RectF localRectF = getLocalPhotoBounds();
    float f1 = GeometryMath.scale(localRectF.width(), localRectF.height(), getWidth(), getHeight());
    if ((int)(getLocalRotation() / 90.0F) % 2 != 0)
      f1 = GeometryMath.scale(localRectF.width(), localRectF.height(), getHeight(), getWidth());
    float f2 = getHeight() / 2;
    float f3 = getWidth() / 2;
    float f4 = 2.0F * paramRectF.left + paramRectF.width();
    float f5 = 2.0F * paramRectF.top + paramRectF.height();
    return this.mLocalGeometry.buildGeometryMatrix(f4, f5, f1, f3, f2, paramBoolean);
  }

  protected RectF getLocalCropBounds()
  {
    return this.mLocalGeometry.getPreviewCropBounds();
  }

  protected RectF getLocalDisplayBounds()
  {
    return new RectF(this.mLocalDisplayBounds);
  }

  protected GeometryMetadata.FLIP getLocalFlip()
  {
    return this.mLocalGeometry.getFlipType();
  }

  protected Matrix getLocalGeoFlipMatrix(float paramFloat1, float paramFloat2)
  {
    return this.mLocalGeometry.getFlipMatrix(paramFloat1, paramFloat2);
  }

  protected RectF getLocalPhotoBounds()
  {
    return this.mLocalGeometry.getPhotoBounds();
  }

  protected float getLocalRotation()
  {
    return this.mLocalGeometry.getRotation();
  }

  protected float getLocalScale()
  {
    return this.mLocalGeometry.getScaleFactor();
  }

  protected float getLocalStraighten()
  {
    return this.mLocalGeometry.getStraightenRotation();
  }

  protected int getLocalValue()
  {
    return 0;
  }

  public String getName()
  {
    return "Geometry";
  }

  protected void lostVisibility()
  {
  }

  public void onDraw(Canvas paramCanvas)
  {
    if (getDirtyGeometryFlag())
    {
      syncLocalToMasterGeometry();
      clearDirtyGeometryFlag();
    }
    requestFilteredImages();
    Bitmap localBitmap = getMaster().getFiltersOnlyImage();
    if (localBitmap == null)
    {
      invalidate();
      return;
    }
    this.mHasDrawn = true;
    drawShape(paramCanvas, localBitmap);
  }

  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    setupLocalDisplayBounds(new RectF(0.0F, 0.0F, paramInt1, paramInt2));
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getActionMasked())
    {
    default:
      setNoAction();
    case 0:
    case 1:
    case 2:
    }
    while (true)
    {
      if (getPanelController() != null)
        getPanelController().onNewValue(getLocalValue());
      invalidate();
      return true;
      setActionDown(paramMotionEvent.getX(), paramMotionEvent.getY());
      continue;
      setActionUp();
      saveAndSetPreset();
      continue;
      setActionMove(paramMotionEvent.getX(), paramMotionEvent.getY());
    }
  }

  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    if (paramInt == 0)
    {
      this.mVisibilityGained = true;
      syncLocalToMasterGeometry();
      updateScale();
      gainedVisibility();
      return;
    }
    if ((this.mVisibilityGained == true) && (this.mHasDrawn == true))
      lostVisibility();
    this.mVisibilityGained = false;
    this.mHasDrawn = false;
  }

  public void resetParameter()
  {
    super.resetParameter();
    setLocalRotation(0.0F);
    setLocalStraighten(0.0F);
    setLocalCropBounds(getLocalPhotoBounds());
    setLocalFlip(GeometryMetadata.FLIP.NONE);
    saveAndSetPreset();
    invalidate();
  }

  protected void saveAndSetPreset()
  {
    ImagePreset localImagePreset1 = getHistory().getLast();
    if ((localImagePreset1 != null) && (localImagePreset1.historyName().equalsIgnoreCase(getName())))
    {
      getImagePreset().setGeometry(this.mLocalGeometry);
      resetImageCaches(this);
    }
    while (true)
    {
      invalidate();
      return;
      if (!this.mLocalGeometry.hasModifications())
        continue;
      ImagePreset localImagePreset2 = new ImagePreset(getImagePreset());
      localImagePreset2.setGeometry(this.mLocalGeometry);
      localImagePreset2.setHistoryName(getName());
      localImagePreset2.setIsFx(false);
      setImagePreset(localImagePreset2, true);
    }
  }

  protected void setActionDown(float paramFloat1, float paramFloat2)
  {
    this.mTouchCenterX = paramFloat1;
    this.mTouchCenterY = paramFloat2;
    this.mCurrentX = paramFloat1;
    this.mCurrentY = paramFloat2;
    this.mMode = MODES.DOWN;
  }

  protected void setActionMove(float paramFloat1, float paramFloat2)
  {
    this.mCurrentX = paramFloat1;
    this.mCurrentY = paramFloat2;
    this.mMode = MODES.MOVE;
  }

  protected void setActionUp()
  {
    this.mMode = MODES.UP;
  }

  protected void setLocalCropBounds(RectF paramRectF)
  {
    this.mLocalGeometry.setCropBounds(paramRectF);
    updateScale();
  }

  protected void setLocalFlip(GeometryMetadata.FLIP paramFLIP)
  {
    this.mLocalGeometry.setFlipType(paramFLIP);
  }

  protected void setLocalRotation(float paramFloat)
  {
    this.mLocalGeometry.setRotation(paramFloat);
    updateScale();
  }

  protected void setLocalScale(float paramFloat)
  {
    this.mLocalGeometry.setScaleFactor(paramFloat);
  }

  protected void setLocalStraighten(float paramFloat)
  {
    this.mLocalGeometry.setStraightenRotation(paramFloat);
    updateScale();
  }

  protected void setNoAction()
  {
    this.mMode = MODES.NONE;
  }

  public boolean showTitle()
  {
    return false;
  }

  protected RectF straightenBounds()
  {
    RectF localRectF = getUntranslatedStraightenCropBounds(getLocalPhotoBounds(), getLocalStraighten());
    getGeoMatrix(localRectF, true).mapRect(localRectF);
    return localRectF;
  }

  protected void syncLocalToMasterGeometry()
  {
    this.mLocalGeometry = getMaster().getGeometry();
    calculateLocalScalingFactorAndOffset();
  }

  protected RectF unrotatedCropBounds()
  {
    RectF localRectF1 = getLocalCropBounds();
    RectF localRectF2 = getLocalPhotoBounds();
    float f1 = computeScale(getWidth(), getHeight());
    float f2 = getHeight() / 2;
    float f3 = getWidth() / 2;
    this.mLocalGeometry.buildGeometryMatrix(localRectF2.width(), localRectF2.height(), f1, f3, f2, 0.0F).mapRect(localRectF1);
    return localRectF1;
  }

  protected void updateScale()
  {
    RectF localRectF = getUntranslatedStraightenCropBounds(this.mLocalGeometry.getPhotoBounds(), getLocalStraighten());
    setLocalScale(computeScale(localRectF.width(), localRectF.height()));
  }

  protected static enum MODES
  {
    static
    {
      DOWN = new MODES("DOWN", 1);
      UP = new MODES("UP", 2);
      MOVE = new MODES("MOVE", 3);
      MODES[] arrayOfMODES = new MODES[4];
      arrayOfMODES[0] = NONE;
      arrayOfMODES[1] = DOWN;
      arrayOfMODES[2] = UP;
      arrayOfMODES[3] = MOVE;
      $VALUES = arrayOfMODES;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.imageshow.ImageGeometry
 * JD-Core Version:    0.5.4
 */