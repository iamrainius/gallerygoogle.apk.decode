package com.android.gallery3d.filtershow.imageshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.util.AttributeSet;

public class ImageSmallBorder extends ImageSmallFilter
{
  protected final float mImageScaleFactor = 3.5F;
  protected final int mInnerBorderColor = -16777216;
  protected final int mInnerBorderWidth = 2;
  protected final int mSelectedBackgroundColor = -1;

  public ImageSmallBorder(Context paramContext)
  {
    super(paramContext);
  }

  public ImageSmallBorder(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public void drawImage(Canvas paramCanvas, Bitmap paramBitmap, Rect paramRect)
  {
    if (paramBitmap == null)
      return;
    int i = paramBitmap.getWidth();
    paramBitmap.getHeight();
    paramCanvas.drawBitmap(paramBitmap, new Rect(0, 0, i, i), paramRect, this.mPaint);
  }

  public void onDraw(Canvas paramCanvas)
  {
    requestFilteredImages();
    paramCanvas.drawColor(mBackgroundColor);
    RectF localRectF = new RectF(mMargin, 2 * mMargin, -1 + (getWidth() - mMargin), getWidth());
    if (this.mIsSelected)
    {
      this.mPaint.setColor(-1);
      paramCanvas.drawRect(0.0F, mMargin, getWidth(), getWidth() + mMargin, this.mPaint);
    }
    this.mPaint.setColor(-16777216);
    this.mPaint.setStrokeWidth(2.0F);
    Path localPath = new Path();
    localPath.addRect(localRectF, Path.Direction.CCW);
    this.mPaint.setStyle(Paint.Style.STROKE);
    paramCanvas.drawPath(localPath, this.mPaint);
    this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    paramCanvas.save();
    paramCanvas.clipRect(1 + mMargin, 2 * mMargin, -2 + (getWidth() - mMargin), -1 + getWidth(), Region.Op.INTERSECT);
    paramCanvas.translate(1 + mMargin, 1 + 2 * mMargin);
    paramCanvas.scale(3.5F, 3.5F);
    Rect localRect = new Rect(0, 0, getWidth(), getWidth());
    drawImage(paramCanvas, getFilteredImage(), localRect);
    paramCanvas.restore();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.imageshow.ImageSmallBorder
 * JD-Core Version:    0.5.4
 */