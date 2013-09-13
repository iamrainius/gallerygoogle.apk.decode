package com.android.gallery3d.filtershow.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageButton;
import com.android.gallery3d.R.styleable;

public class FramedTextButton extends ImageButton
{
  private static Paint gPaint;
  private static int mTextPadding;
  private static int mTextSize = 24;
  private Context mContext = null;
  private String mText = null;

  static
  {
    mTextPadding = 20;
    gPaint = new Paint();
  }

  public FramedTextButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    this.mText = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.ImageButtonTitle).getString(1);
  }

  public static void setTextSize(int paramInt)
  {
    mTextSize = paramInt;
  }

  public void onDraw(Canvas paramCanvas)
  {
    gPaint.setARGB(255, 255, 255, 255);
    gPaint.setStrokeWidth(2.0F);
    gPaint.setStyle(Paint.Style.STROKE);
    paramCanvas.drawRect(mTextPadding, mTextPadding, getWidth() - mTextPadding, getHeight() - mTextPadding, gPaint);
    if (this.mText == null)
      return;
    gPaint.reset();
    gPaint.setARGB(255, 255, 255, 255);
    gPaint.setTextSize(mTextSize);
    float f = gPaint.measureText(this.mText);
    Rect localRect = new Rect();
    gPaint.getTextBounds(this.mText, 0, this.mText.length(), localRect);
    int i = (int)((getWidth() - f) / 2.0F);
    int j = (getHeight() + localRect.height()) / 2;
    paramCanvas.drawText(this.mText, i, j, gPaint);
  }

  public void setText(String paramString)
  {
    this.mText = paramString;
    invalidate();
  }

  public void setTextFrom(int paramInt)
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
      setText(this.mContext.getString(2131362135));
      continue;
      setText(this.mContext.getString(2131362136));
      continue;
      setText(this.mContext.getString(2131362137));
      continue;
      setText(this.mContext.getString(2131362138));
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.ui.FramedTextButton
 * JD-Core Version:    0.5.4
 */