package com.android.gallery3d.filtershow.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.android.gallery3d.filtershow.filters.ImageFilterCurves;
import com.android.gallery3d.filtershow.imageshow.ImageSlave;
import com.android.gallery3d.filtershow.presets.ImagePreset;

public class ImageCurves extends ImageSlave
{
  int[] blueHistogram = new int[256];
  Path gHistoPath = new Path();
  Paint gPaint = new Paint();
  Path gPathSpline = new Path();
  int[] greenHistogram = new int[256];
  private ControlPoint mCurrentControlPoint = null;
  private int mCurrentCurveIndex = 0;
  private boolean mDidAddPoint = false;
  private boolean mDidDelete = false;
  boolean mDoingTouchMove = false;
  private ImagePreset mLastPreset = null;
  int[] redHistogram = new int[256];

  public ImageCurves(Context paramContext)
  {
    super(paramContext);
    resetCurve();
  }

  public ImageCurves(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    resetCurve();
  }

  private ImageFilterCurves curves()
  {
    if (getMaster() != null)
    {
      String str = getFilterName();
      return (ImageFilterCurves)getImagePreset().getFilter(str);
    }
    return null;
  }

  private void drawHistogram(Canvas paramCanvas, int[] paramArrayOfInt, int paramInt, PorterDuff.Mode paramMode)
  {
    int i = 0;
    for (int j = 0; j < paramArrayOfInt.length; ++j)
    {
      if (paramArrayOfInt[j] <= i)
        continue;
      i = paramArrayOfInt[j];
    }
    float f1 = getWidth();
    float f2 = getHeight();
    float f3 = f1 / paramArrayOfInt.length;
    float f4 = 0.3F * f2 / i;
    Paint localPaint1 = new Paint();
    localPaint1.setARGB(100, 255, 255, 255);
    localPaint1.setStrokeWidth((int)Math.ceil(f3));
    Paint localPaint2 = new Paint();
    localPaint2.setColor(paramInt);
    localPaint2.setStrokeWidth(6.0F);
    PorterDuffXfermode localPorterDuffXfermode = new PorterDuffXfermode(paramMode);
    localPaint2.setXfermode(localPorterDuffXfermode);
    this.gHistoPath.reset();
    this.gHistoPath.moveTo(0.0F, f2);
    int k = 0;
    float f5 = 0.0F;
    float f6 = 0.0F;
    for (int l = 0; l < paramArrayOfInt.length; ++l)
    {
      float f7 = f3 * l;
      float f8 = f4 * paramArrayOfInt[l];
      if (f8 == 0.0F)
        continue;
      float f9 = f2 - (f8 + f5) / 2.0F;
      if (k == 0)
      {
        this.gHistoPath.lineTo(f7, f2);
        k = 1;
      }
      this.gHistoPath.lineTo(f7, f9);
      f5 = f8;
      f6 = f7;
    }
    this.gHistoPath.lineTo(f6, f2);
    this.gHistoPath.lineTo(f1, f2);
    this.gHistoPath.close();
    paramCanvas.drawPath(this.gHistoPath, localPaint2);
    localPaint2.setStrokeWidth(2.0F);
    localPaint2.setStyle(Paint.Style.STROKE);
    localPaint2.setARGB(255, 200, 200, 200);
    paramCanvas.drawPath(this.gHistoPath, localPaint2);
  }

  private String getFilterName()
  {
    return "Curves";
  }

  private Spline getSpline(int paramInt)
  {
    return curves().getSpline(paramInt);
  }

  private int pickControlPoint(float paramFloat1, float paramFloat2)
  {
    int i = 0;
    Spline localSpline = getSpline(this.mCurrentCurveIndex);
    float f1 = localSpline.getPoint(0).x;
    float f2 = localSpline.getPoint(0).y;
    double d1 = Math.sqrt((f1 - paramFloat1) * (f1 - paramFloat1) + (f2 - paramFloat2) * (f2 - paramFloat2));
    for (int j = 1; j < localSpline.getNbPoints(); ++j)
    {
      float f3 = localSpline.getPoint(j).x;
      float f4 = localSpline.getPoint(j).y;
      double d2 = Math.sqrt((f3 - paramFloat1) * (f3 - paramFloat1) + (f4 - paramFloat2) * (f4 - paramFloat2));
      if (d2 >= d1)
        continue;
      d1 = d2;
      i = j;
    }
    if ((!this.mDidAddPoint) && (d1 * getWidth() > 100.0D) && (localSpline.getNbPoints() < 10))
      i = -1;
    return i;
  }

  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    this.gPaint.setAntiAlias(true);
    if ((getImagePreset() != this.mLastPreset) && (getFilteredImage() != null))
    {
      ComputeHistogramTask localComputeHistogramTask = new ComputeHistogramTask();
      Bitmap[] arrayOfBitmap = new Bitmap[1];
      arrayOfBitmap[0] = getFilteredImage();
      localComputeHistogramTask.execute(arrayOfBitmap);
      this.mLastPreset = getImagePreset();
    }
    if (curves() == null)
      return;
    if ((this.mCurrentCurveIndex == 0) || (this.mCurrentCurveIndex == 1))
      drawHistogram(paramCanvas, this.redHistogram, -65536, PorterDuff.Mode.SCREEN);
    if ((this.mCurrentCurveIndex == 0) || (this.mCurrentCurveIndex == 2))
      drawHistogram(paramCanvas, this.greenHistogram, -16711936, PorterDuff.Mode.SCREEN);
    if ((this.mCurrentCurveIndex == 0) || (this.mCurrentCurveIndex == 3))
      drawHistogram(paramCanvas, this.blueHistogram, -16776961, PorterDuff.Mode.SCREEN);
    if (this.mCurrentCurveIndex == 0)
      for (int i = 0; i < 4; ++i)
      {
        Spline localSpline = getSpline(i);
        if ((i == this.mCurrentCurveIndex) || (localSpline.isOriginal()))
          continue;
        localSpline.draw(paramCanvas, Spline.colorForCurve(i), getWidth(), getHeight(), false, this.mDoingTouchMove);
      }
    getSpline(this.mCurrentCurveIndex).draw(paramCanvas, Spline.colorForCurve(this.mCurrentCurveIndex), getWidth(), getHeight(), true, this.mDoingTouchMove);
    drawToast(paramCanvas);
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    monitorenter;
    while (true)
    {
      Spline localSpline;
      int i;
      try
      {
        float f1 = paramMotionEvent.getX() / getWidth();
        float f2 = paramMotionEvent.getY();
        float f3 = Spline.curveHandleSize() / 2;
        if (f2 < f3)
          f2 = f3;
        if (f2 > getHeight() - f3)
          f2 = getHeight() - f3;
        float f4 = (f2 - f3) / (getHeight() - 2.0F * f3);
        if (paramMotionEvent.getActionMasked() == 1)
        {
          this.mCurrentControlPoint = null;
          updateCachedImage();
          this.mDidAddPoint = false;
          if (this.mDidDelete)
            this.mDidDelete = false;
          this.mDoingTouchMove = false;
        }
        do
        {
          return true;
          this.mDoingTouchMove = true;
        }
        while ((this.mDidDelete) || (curves() == null));
        localSpline = getSpline(this.mCurrentCurveIndex);
        i = pickControlPoint(f1, f4);
        if (this.mCurrentControlPoint == null)
        {
          if (i != -1)
            break label251;
          this.mCurrentControlPoint = new ControlPoint(f1, f4);
          i = localSpline.addPoint(this.mCurrentControlPoint);
          this.mDidAddPoint = true;
        }
        if (!localSpline.isPointContained(f1, i))
          break label265;
        localSpline.didMovePoint(this.mCurrentControlPoint);
        localSpline.movePoint(i, f1, f4);
        updateCachedImage();
      }
      finally
      {
        monitorexit;
      }
      label251: this.mCurrentControlPoint = localSpline.getPoint(i);
      continue;
      label265: if ((i == -1) || (localSpline.getNbPoints() <= 2))
        continue;
      localSpline.deletePoint(i);
      this.mDidDelete = true;
    }
  }

  public void resetCurve()
  {
    if ((getMaster() == null) || (curves() == null))
      return;
    curves().reset();
    updateCachedImage();
  }

  public void resetParameter()
  {
    super.resetParameter();
    resetCurve();
    this.mLastPreset = null;
    invalidate();
  }

  public void setChannel(int paramInt)
  {
    switch (paramInt)
    {
    default:
    case 2131558652:
    case 2131558653:
    case 2131558654:
    case 2131558655:
    }
    while (true)
    {
      invalidate();
      return;
      this.mCurrentCurveIndex = 0;
      continue;
      this.mCurrentCurveIndex = 1;
      continue;
      this.mCurrentCurveIndex = 2;
      continue;
      this.mCurrentCurveIndex = 3;
    }
  }

  public boolean showTitle()
  {
    return false;
  }

  public void updateCachedImage()
  {
    monitorenter;
    try
    {
      if (getImagePreset() != null)
      {
        resetImageCaches(this);
        invalidate();
      }
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  class ComputeHistogramTask extends AsyncTask<Bitmap, Void, int[]>
  {
    ComputeHistogramTask()
    {
    }

    protected int[] doInBackground(Bitmap[] paramArrayOfBitmap)
    {
      int[] arrayOfInt1 = new int[768];
      Bitmap localBitmap = paramArrayOfBitmap[0];
      int i = localBitmap.getWidth();
      int j = localBitmap.getHeight();
      int[] arrayOfInt2 = new int[i * j];
      localBitmap.getPixels(arrayOfInt2, 0, i, 0, 0, i, j);
      for (int k = 0; k < i; ++k)
        for (int l = 0; l < j; ++l)
        {
          int i1 = k + l * i;
          int i2 = Color.red(arrayOfInt2[i1]);
          int i3 = Color.green(arrayOfInt2[i1]);
          int i4 = Color.blue(arrayOfInt2[i1]);
          arrayOfInt1[i2] = (1 + arrayOfInt1[i2]);
          int i5 = i3 + 256;
          arrayOfInt1[i5] = (1 + arrayOfInt1[i5]);
          int i6 = i4 + 512;
          arrayOfInt1[i6] = (1 + arrayOfInt1[i6]);
        }
      return arrayOfInt1;
    }

    protected void onPostExecute(int[] paramArrayOfInt)
    {
      System.arraycopy(paramArrayOfInt, 0, ImageCurves.this.redHistogram, 0, 256);
      System.arraycopy(paramArrayOfInt, 256, ImageCurves.this.greenHistogram, 0, 256);
      System.arraycopy(paramArrayOfInt, 512, ImageCurves.this.blueHistogram, 0, 256);
      ImageCurves.this.invalidate();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.ui.ImageCurves
 * JD-Core Version:    0.5.4
 */