package com.android.gallery3d.filtershow.filters;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.android.gallery3d.filtershow.imageshow.GeometryMetadata;

public class ImageFilterGeometry extends ImageFilter
{
  private final Bitmap.Config mConfig = Bitmap.Config.ARGB_8888;
  private GeometryMetadata mGeometry = null;

  public ImageFilterGeometry()
  {
    this.mName = "Geometry";
  }

  public Bitmap apply(Bitmap paramBitmap, float paramFloat, boolean paramBoolean)
  {
    Rect localRect = new Rect(0, 0, paramBitmap.getWidth(), paramBitmap.getHeight());
    RectF localRectF = this.mGeometry.getCropBounds(paramBitmap);
    if ((localRectF.width() > 0.0F) && (localRectF.height() > 0.0F))
      localRectF.roundOut(localRect);
    if (this.mGeometry.hasSwitchedWidthHeight());
    for (Bitmap localBitmap = Bitmap.createBitmap(localRect.height(), localRect.width(), this.mConfig); ; localBitmap = Bitmap.createBitmap(localRect.width(), localRect.height(), this.mConfig))
    {
      float[] arrayOfFloat = new float[2];
      arrayOfFloat[0] = (localBitmap.getWidth() / 2.0F);
      arrayOfFloat[1] = (localBitmap.getHeight() / 2.0F);
      Matrix localMatrix = this.mGeometry.buildTotalXform(paramBitmap.getWidth(), paramBitmap.getHeight(), arrayOfFloat);
      Canvas localCanvas = new Canvas(localBitmap);
      Paint localPaint = new Paint();
      localPaint.setAntiAlias(true);
      localPaint.setFilterBitmap(true);
      localPaint.setDither(true);
      localCanvas.drawBitmap(paramBitmap, localMatrix, localPaint);
      return localBitmap;
    }
  }

  public ImageFilter clone()
    throws CloneNotSupportedException
  {
    return (ImageFilterGeometry)super.clone();
  }

  public void setGeometryMetadata(GeometryMetadata paramGeometryMetadata)
  {
    this.mGeometry = paramGeometryMetadata;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.filters.ImageFilterGeometry
 * JD-Core Version:    0.5.4
 */