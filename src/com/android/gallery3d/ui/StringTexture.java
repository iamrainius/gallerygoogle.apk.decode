package com.android.gallery3d.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.FloatMath;

class StringTexture extends CanvasTexture
{
  private final Paint.FontMetricsInt mMetrics;
  private final TextPaint mPaint;
  private final String mText;

  private StringTexture(String paramString, TextPaint paramTextPaint, Paint.FontMetricsInt paramFontMetricsInt, int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
    this.mText = paramString;
    this.mPaint = paramTextPaint;
    this.mMetrics = paramFontMetricsInt;
  }

  public static TextPaint getDefaultPaint(float paramFloat, int paramInt)
  {
    TextPaint localTextPaint = new TextPaint();
    localTextPaint.setTextSize(paramFloat);
    localTextPaint.setAntiAlias(true);
    localTextPaint.setColor(paramInt);
    localTextPaint.setShadowLayer(2.0F, 0.0F, 0.0F, -16777216);
    return localTextPaint;
  }

  public static StringTexture newInstance(String paramString, float paramFloat, int paramInt)
  {
    return newInstance(paramString, getDefaultPaint(paramFloat, paramInt));
  }

  public static StringTexture newInstance(String paramString, float paramFloat1, int paramInt, float paramFloat2, boolean paramBoolean)
  {
    TextPaint localTextPaint = getDefaultPaint(paramFloat1, paramInt);
    if (paramBoolean)
      localTextPaint.setTypeface(Typeface.defaultFromStyle(1));
    if (paramFloat2 > 0.0F)
      paramString = TextUtils.ellipsize(paramString, localTextPaint, paramFloat2, TextUtils.TruncateAt.END).toString();
    return newInstance(paramString, localTextPaint);
  }

  private static StringTexture newInstance(String paramString, TextPaint paramTextPaint)
  {
    Paint.FontMetricsInt localFontMetricsInt = paramTextPaint.getFontMetricsInt();
    int i = (int)FloatMath.ceil(paramTextPaint.measureText(paramString));
    int j = localFontMetricsInt.bottom - localFontMetricsInt.top;
    if (i <= 0)
      i = 1;
    if (j <= 0)
      j = 1;
    return new StringTexture(paramString, paramTextPaint, localFontMetricsInt, i, j);
  }

  protected void onDraw(Canvas paramCanvas, Bitmap paramBitmap)
  {
    paramCanvas.translate(0.0F, -this.mMetrics.ascent);
    paramCanvas.drawText(this.mText, 0.0F, 0.0F, this.mPaint);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.StringTexture
 * JD-Core Version:    0.5.4
 */