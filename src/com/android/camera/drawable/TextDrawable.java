package com.android.camera.drawable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

public class TextDrawable extends Drawable
{
  private int mIntrinsicHeight;
  private int mIntrinsicWidth;
  private Paint mPaint;
  private CharSequence mText;

  public TextDrawable(Resources paramResources, CharSequence paramCharSequence)
  {
    this.mText = paramCharSequence;
    this.mPaint = new Paint(1);
    this.mPaint.setColor(-1);
    this.mPaint.setTextAlign(Paint.Align.CENTER);
    float f = TypedValue.applyDimension(2, 15.0F, paramResources.getDisplayMetrics());
    this.mPaint.setTextSize(f);
    this.mIntrinsicWidth = (int)(0.5D + this.mPaint.measureText(this.mText, 0, this.mText.length()));
    this.mIntrinsicHeight = this.mPaint.getFontMetricsInt(null);
  }

  public void draw(Canvas paramCanvas)
  {
    Rect localRect = getBounds();
    paramCanvas.drawText(this.mText, 0, this.mText.length(), localRect.centerX(), localRect.centerY(), this.mPaint);
  }

  public int getIntrinsicHeight()
  {
    return this.mIntrinsicHeight;
  }

  public int getIntrinsicWidth()
  {
    return this.mIntrinsicWidth;
  }

  public int getOpacity()
  {
    return this.mPaint.getAlpha();
  }

  public void setAlpha(int paramInt)
  {
    this.mPaint.setAlpha(paramInt);
  }

  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.mPaint.setColorFilter(paramColorFilter);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.drawable.TextDrawable
 * JD-Core Version:    0.5.4
 */