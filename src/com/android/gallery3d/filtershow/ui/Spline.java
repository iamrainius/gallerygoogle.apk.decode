package com.android.gallery3d.filtershow.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Vector;

public class Spline
{
  private static Drawable mCurveHandle;
  private static int mCurveHandleSize;
  private static int mCurveWidth;
  private final Paint gPaint = new Paint();
  private ControlPoint mCurrentControlPoint = null;
  private final Vector<ControlPoint> mPoints = new Vector();

  public Spline()
  {
  }

  public Spline(Spline paramSpline)
  {
    for (int i = 0; i < paramSpline.mPoints.size(); ++i)
    {
      ControlPoint localControlPoint = (ControlPoint)paramSpline.mPoints.elementAt(i);
      this.mPoints.add(new ControlPoint(localControlPoint));
    }
    Collections.sort(this.mPoints);
  }

  public static int colorForCurve(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return -1;
    case 1:
      return -65536;
    case 2:
      return -16711936;
    case 3:
    }
    return -16776961;
  }

  public static int curveHandleSize()
  {
    return mCurveHandleSize;
  }

  private void drawGrid(Canvas paramCanvas, float paramFloat1, float paramFloat2)
  {
    this.gPaint.setARGB(128, 150, 150, 150);
    this.gPaint.setStrokeWidth(1.0F);
    (paramFloat2 / 9.0F);
    (paramFloat1 / 9.0F);
    this.gPaint.setARGB(255, 100, 100, 100);
    this.gPaint.setStrokeWidth(2.0F);
    paramCanvas.drawLine(0.0F, paramFloat2, paramFloat1, 0.0F, this.gPaint);
    this.gPaint.setARGB(128, 200, 200, 200);
    this.gPaint.setStrokeWidth(4.0F);
    float f1 = paramFloat2 / 3.0F;
    float f2 = paramFloat1 / 3.0F;
    for (int i = 1; i < 3; ++i)
    {
      paramCanvas.drawLine(0.0F, f1 * i, paramFloat1, f1 * i, this.gPaint);
      paramCanvas.drawLine(f2 * i, 0.0F, f2 * i, paramFloat2, this.gPaint);
    }
    paramCanvas.drawLine(0.0F, 0.0F, 0.0F, paramFloat2, this.gPaint);
    paramCanvas.drawLine(paramFloat1, 0.0F, paramFloat1, paramFloat2, this.gPaint);
    paramCanvas.drawLine(0.0F, 0.0F, paramFloat1, 0.0F, this.gPaint);
    paramCanvas.drawLine(0.0F, paramFloat2, paramFloat1, paramFloat2, this.gPaint);
  }

  private void drawHandles(Canvas paramCanvas, Drawable paramDrawable, float paramFloat1, float paramFloat2)
  {
    int i = (int)paramFloat1 - mCurveHandleSize / 2;
    int j = (int)paramFloat2 - mCurveHandleSize / 2;
    paramDrawable.setBounds(i, j, i + mCurveHandleSize, j + mCurveHandleSize);
    paramDrawable.draw(paramCanvas);
  }

  public static void setCurveHandle(Drawable paramDrawable, int paramInt)
  {
    mCurveHandle = paramDrawable;
    mCurveHandleSize = paramInt;
  }

  public static void setCurveWidth(int paramInt)
  {
    mCurveWidth = paramInt;
  }

  public int addPoint(float paramFloat1, float paramFloat2)
  {
    return addPoint(new ControlPoint(paramFloat1, paramFloat2));
  }

  public int addPoint(ControlPoint paramControlPoint)
  {
    this.mPoints.add(paramControlPoint);
    Collections.sort(this.mPoints);
    return this.mPoints.indexOf(paramControlPoint);
  }

  public void deletePoint(int paramInt)
  {
    this.mPoints.remove(paramInt);
    Collections.sort(this.mPoints);
  }

  public void didMovePoint(ControlPoint paramControlPoint)
  {
    this.mCurrentControlPoint = paramControlPoint;
  }

  public void draw(Canvas paramCanvas, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2)
  {
    float f1 = paramInt2 - mCurveHandleSize;
    float f2 = paramInt3 - mCurveHandleSize;
    float f3 = mCurveHandleSize / 2;
    float f4 = mCurveHandleSize / 2;
    ControlPoint[] arrayOfControlPoint = new ControlPoint[this.mPoints.size()];
    for (int i = 0; ; ++i)
    {
      int j = this.mPoints.size();
      if (i >= j)
        break;
      ControlPoint localControlPoint2 = (ControlPoint)this.mPoints.get(i);
      arrayOfControlPoint[i] = new ControlPoint(f1 * localControlPoint2.x, f2 * localControlPoint2.y);
    }
    double[] arrayOfDouble = solveSystem(arrayOfControlPoint);
    Path localPath = new Path();
    localPath.moveTo(0.0F, arrayOfControlPoint[0].y);
    for (int k = 0; ; ++k)
    {
      int l = -1 + arrayOfControlPoint.length;
      if (k >= l)
        break;
      double d1 = arrayOfControlPoint[k].x;
      double d2 = arrayOfControlPoint[(k + 1)].x;
      double d3 = arrayOfControlPoint[k].y;
      double d4 = arrayOfControlPoint[(k + 1)].y;
      double d5 = d1;
      while (d5 < d2)
      {
        double d6 = d2 - d1;
        double d7 = d6 * d6;
        double d8 = (d5 - d1) / d6;
        double d9 = 1.0D - d8;
        double d10 = d9 * d3;
        double d11 = d8 * d4;
        double d12 = (d9 * (d9 * d9) - d9) * arrayOfDouble[k];
        double d13 = (d8 * (d8 * d8) - d8) * arrayOfDouble[(k + 1)];
        double d14 = d10 + d11 + d7 / 6.0D * (d12 + d13);
        if (d14 > f2)
          d14 = f2;
        if (d14 < 0.0D)
          d14 = 0.0D;
        localPath.lineTo((float)d5, (float)d14);
        d5 += 20.0D;
      }
    }
    paramCanvas.save();
    paramCanvas.translate(f3, f4);
    drawGrid(paramCanvas, f1, f2);
    ControlPoint localControlPoint1 = arrayOfControlPoint[(-1 + arrayOfControlPoint.length)];
    localPath.lineTo(localControlPoint1.x, localControlPoint1.y);
    localPath.lineTo(f1, localControlPoint1.y);
    Paint localPaint = new Paint();
    localPaint.setAntiAlias(true);
    localPaint.setFilterBitmap(true);
    localPaint.setDither(true);
    localPaint.setStyle(Paint.Style.STROKE);
    int i1 = mCurveWidth;
    if (paramBoolean1)
      i1 = (int)(1.5D * i1);
    localPaint.setStrokeWidth(i1 + 2);
    localPaint.setColor(-16777216);
    paramCanvas.drawPath(localPath, localPaint);
    if ((paramBoolean2) && (this.mCurrentControlPoint != null))
    {
      float f7 = f1 * this.mCurrentControlPoint.x;
      float f8 = f2 * this.mCurrentControlPoint.y;
      localPaint.setStrokeWidth(3.0F);
      localPaint.setColor(-16777216);
      paramCanvas.drawLine(f7, f8, f7, f2, localPaint);
      paramCanvas.drawLine(0.0F, f8, f7, f8, localPaint);
      localPaint.setStrokeWidth(1.0F);
      localPaint.setColor(paramInt1);
      paramCanvas.drawLine(f7, f8, f7, f2, localPaint);
      paramCanvas.drawLine(0.0F, f8, f7, f8, localPaint);
    }
    localPaint.setStrokeWidth(i1);
    localPaint.setColor(paramInt1);
    paramCanvas.drawPath(localPath, localPaint);
    if (paramBoolean1)
      for (int i2 = 0; ; ++i2)
      {
        int i3 = arrayOfControlPoint.length;
        if (i2 >= i3)
          break;
        float f5 = arrayOfControlPoint[i2].x;
        float f6 = arrayOfControlPoint[i2].y;
        drawHandles(paramCanvas, mCurveHandle, f5, f6);
      }
    paramCanvas.restore();
  }

  public float[] getAppliedCurve()
  {
    float[] arrayOfFloat = new float[256];
    ControlPoint[] arrayOfControlPoint = new ControlPoint[this.mPoints.size()];
    for (int i = 0; i < this.mPoints.size(); ++i)
    {
      ControlPoint localControlPoint3 = (ControlPoint)this.mPoints.get(i);
      arrayOfControlPoint[i] = new ControlPoint(localControlPoint3.x, localControlPoint3.y);
    }
    double[] arrayOfDouble = solveSystem(arrayOfControlPoint);
    boolean bool = arrayOfControlPoint[0].x < 0.0F;
    int j = 0;
    if (bool)
      j = (int)(256.0F * arrayOfControlPoint[0].x);
    for (int k = 0; k < j; ++k)
      arrayOfFloat[k] = (1.0F - arrayOfControlPoint[0].y);
    int l = j;
    if (l < 256)
    {
      label138: double d1 = l / 256.0D;
      int i1 = 0;
      for (int i2 = 0; i2 < -1 + arrayOfControlPoint.length; ++i2)
      {
        if ((d1 < arrayOfControlPoint[i2].x) || (d1 > arrayOfControlPoint[(i2 + 1)].x))
          continue;
        i1 = i2;
      }
      ControlPoint localControlPoint1 = arrayOfControlPoint[i1];
      ControlPoint localControlPoint2 = arrayOfControlPoint[(i1 + 1)];
      if (d1 <= localControlPoint2.x)
      {
        double d2 = localControlPoint1.x;
        double d3 = localControlPoint2.x;
        double d4 = localControlPoint1.y;
        double d5 = localControlPoint2.y;
        double d6 = d3 - d2;
        double d7 = d6 * d6;
        double d8 = (d1 - d2) / d6;
        double d9 = 1.0D - d8;
        double d10 = d9 * d4;
        double d11 = d8 * d5;
        double d12 = (d9 * (d9 * d9) - d9) * arrayOfDouble[i1];
        double d13 = (d8 * (d8 * d8) - d8) * arrayOfDouble[(i1 + 1)];
        double d14 = d10 + d11 + d7 / 6.0D * (d12 + d13);
        if (d14 > 1.0D)
          d14 = 1.0D;
        if (d14 < 0.0D)
          d14 = 0.0D;
        arrayOfFloat[l] = (float)(1.0D - d14);
      }
      while (true)
      {
        ++l;
        break label138:
        arrayOfFloat[l] = (1.0F - localControlPoint2.y);
      }
    }
    return arrayOfFloat;
  }

  public int getNbPoints()
  {
    return this.mPoints.size();
  }

  public ControlPoint getPoint(int paramInt)
  {
    return (ControlPoint)this.mPoints.elementAt(paramInt);
  }

  public boolean isOriginal()
  {
    if (getNbPoints() > 2)
      return false;
    if ((((ControlPoint)this.mPoints.elementAt(0)).x != 0.0F) || (((ControlPoint)this.mPoints.elementAt(0)).y != 1.0F))
      return false;
    return (((ControlPoint)this.mPoints.elementAt(1)).x == 1.0F) && (((ControlPoint)this.mPoints.elementAt(1)).y == 0.0F);
  }

  public boolean isPointContained(float paramFloat, int paramInt)
  {
    for (int i = 0; i < paramInt; ++i)
      if (((ControlPoint)this.mPoints.elementAt(i)).x > paramFloat)
        return false;
    for (int j = paramInt + 1; j < this.mPoints.size(); ++j)
      if (((ControlPoint)this.mPoints.elementAt(j)).x < paramFloat);
    return true;
  }

  public void movePoint(int paramInt, float paramFloat1, float paramFloat2)
  {
    if ((paramInt < 0) || (paramInt > -1 + this.mPoints.size()))
      return;
    ControlPoint localControlPoint = (ControlPoint)this.mPoints.elementAt(paramInt);
    localControlPoint.x = paramFloat1;
    localControlPoint.y = paramFloat2;
  }

  double[] solveSystem(ControlPoint[] paramArrayOfControlPoint)
  {
    int i = paramArrayOfControlPoint.length;
    int[] arrayOfInt = { i, 3 };
    double[][] arrayOfDouble = (double[][])Array.newInstance(Double.TYPE, arrayOfInt);
    double[] arrayOfDouble1 = new double[i];
    double[] arrayOfDouble2 = new double[i];
    arrayOfDouble[0][1] = 1.0D;
    arrayOfDouble[(i - 1)][1] = 1.0D;
    for (int j = 1; ; ++j)
    {
      int k = i - 1;
      if (j >= k)
        break;
      double d2 = paramArrayOfControlPoint[j].x - paramArrayOfControlPoint[(j - 1)].x;
      double d3 = paramArrayOfControlPoint[(j + 1)].x - paramArrayOfControlPoint[(j - 1)].x;
      double d4 = paramArrayOfControlPoint[(j + 1)].x - paramArrayOfControlPoint[j].x;
      double d5 = paramArrayOfControlPoint[(j + 1)].y - paramArrayOfControlPoint[j].y;
      double d6 = paramArrayOfControlPoint[j].y - paramArrayOfControlPoint[(j - 1)].y;
      arrayOfDouble[j][0] = (0.1666666666666667D * d2);
      arrayOfDouble[j][1] = (0.3333333333333333D * d3);
      arrayOfDouble[j][2] = (0.1666666666666667D * d4);
      arrayOfDouble1[j] = (d5 / d4 - d6 / d2);
    }
    for (int l = 1; l < i; ++l)
    {
      double d1 = arrayOfDouble[l][0] / arrayOfDouble[(l - 1)][1];
      arrayOfDouble[l][1] -= d1 * arrayOfDouble[(l - 1)][2];
      arrayOfDouble1[l] -= d1 * arrayOfDouble1[(l - 1)];
    }
    arrayOfDouble2[(i - 1)] = (arrayOfDouble1[(i - 1)] / arrayOfDouble[(i - 1)][1]);
    for (int i1 = i - 2; i1 >= 0; --i1)
      arrayOfDouble2[i1] = ((arrayOfDouble1[i1] - arrayOfDouble[i1][2] * arrayOfDouble2[(i1 + 1)]) / arrayOfDouble[i1][1]);
    return arrayOfDouble2;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.ui.Spline
 * JD-Core Version:    0.5.4
 */