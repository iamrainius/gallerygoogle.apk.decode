package com.android.gallery3d.filtershow.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class ImageButtonTitle extends ImageButton
{
  private static Paint gPaint;
  private static int mTextPadding;
  private static int mTextSize = 24;
  private String mText = null;

  static
  {
    mTextPadding = 20;
    gPaint = new Paint();
  }

  public ImageButtonTitle(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public static void setTextPadding(int paramInt)
  {
    mTextPadding = paramInt;
  }

  public static void setTextSize(int paramInt)
  {
    mTextSize = paramInt;
  }

  public String getText()
  {
    return this.mText;
  }

  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (this.mText == null)
      return;
    gPaint.setARGB(255, 255, 255, 255);
    gPaint.setTextSize(mTextSize);
    float f = gPaint.measureText(this.mText);
    int i = (int)((getWidth() - f) / 2.0F);
    int j = getHeight() - mTextPadding;
    paramCanvas.drawText(this.mText, i, j, gPaint);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.ui.ImageButtonTitle
 * JD-Core Version:    0.5.4
 */