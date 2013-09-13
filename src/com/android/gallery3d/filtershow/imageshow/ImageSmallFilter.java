package com.android.gallery3d.filtershow.imageshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import com.android.gallery3d.filtershow.FilterShowActivity;
import com.android.gallery3d.filtershow.filters.ImageFilter;
import com.android.gallery3d.filtershow.presets.ImagePreset;

public class ImageSmallFilter extends ImageShow
  implements View.OnClickListener
{
  protected static int mBackgroundColor;
  protected static int mMargin = 12;
  protected static int mTextMargin = 8;
  private FilterShowActivity mController = null;
  protected ImageFilter mImageFilter = null;
  protected boolean mIsSelected = false;
  protected final Paint mPaint = new Paint();
  protected final int mSelectedBackgroundColor = -1;
  private boolean mSetBorder = false;
  private boolean mShowTitle = true;
  protected final int mTextColor = -1;

  static
  {
    mBackgroundColor = -16776961;
  }

  public ImageSmallFilter(Context paramContext)
  {
    super(paramContext);
    setOnClickListener(this);
  }

  public ImageSmallFilter(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setOnClickListener(this);
  }

  public static void setDefaultBackgroundColor(int paramInt)
  {
    mBackgroundColor = paramInt;
  }

  public static void setMargin(int paramInt)
  {
    mMargin = paramInt;
  }

  public static void setTextMargin(int paramInt)
  {
    mTextMargin = paramInt;
  }

  public void drawImage(Canvas paramCanvas, Bitmap paramBitmap, Rect paramRect)
  {
    int i;
    int j;
    int k;
    int i1;
    int l;
    if (paramBitmap != null)
    {
      i = paramBitmap.getWidth();
      j = paramBitmap.getHeight();
      if (i <= j)
        break label73;
      k = j;
      i1 = (int)((i - k) / 2.0F);
      l = 0;
    }
    while (true)
    {
      paramCanvas.drawBitmap(paramBitmap, new Rect(i1, l, i1 + k, l + k), paramRect, this.mPaint);
      return;
      label73: k = i;
      l = (int)((j - k) / 2.0F);
      i1 = 0;
    }
  }

  public ImagePreset getImagePreset()
  {
    return this.mImagePreset;
  }

  public void onClick(View paramView)
  {
    if (this.mController != null)
    {
      if (this.mImageFilter == null)
        break label31;
      this.mController.useImageFilter(this, this.mImageFilter, this.mSetBorder);
    }
    label31: 
    do
      return;
    while (this.mImagePreset == null);
    this.mController.useImagePreset(this, this.mImagePreset);
  }

  public void onDraw(Canvas paramCanvas)
  {
    requestFilteredImages();
    paramCanvas.drawColor(mBackgroundColor);
    float f = this.mPaint.measureText(this.mImageFilter.getName());
    (mTextSize + 2 * mTextPadding);
    int i = (int)((getWidth() - f) / 2.0F);
    int j = getHeight();
    if (this.mIsSelected)
    {
      this.mPaint.setColor(-1);
      paramCanvas.drawRect(0.0F, mMargin, getWidth(), getWidth() + mMargin, this.mPaint);
    }
    Rect localRect = new Rect(mMargin, 2 * mMargin, getWidth() - mMargin, getWidth());
    drawImage(paramCanvas, getFilteredImage(), localRect);
    this.mPaint.setTextSize(mTextSize);
    this.mPaint.setColor(-1);
    paramCanvas.drawText(this.mImageFilter.getName(), i, j - mTextMargin, this.mPaint);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    View.MeasureSpec.getSize(paramInt1);
    int i = View.MeasureSpec.getSize(paramInt2);
    setMeasuredDimension(i - (mTextSize + mTextPadding), i);
  }

  public void setBorder(boolean paramBoolean)
  {
    this.mSetBorder = paramBoolean;
  }

  public void setController(FilterShowActivity paramFilterShowActivity)
  {
    this.mController = paramFilterShowActivity;
  }

  public void setImageFilter(ImageFilter paramImageFilter)
  {
    this.mImageFilter = paramImageFilter;
    this.mImagePreset = new ImagePreset();
    this.mImagePreset.setName(paramImageFilter.getName());
    paramImageFilter.setImagePreset(this.mImagePreset);
    this.mImagePreset.add(this.mImageFilter);
  }

  public void setSelected(boolean paramBoolean)
  {
    if (this.mIsSelected != paramBoolean)
      invalidate();
    this.mIsSelected = paramBoolean;
  }

  public void setShowTitle(boolean paramBoolean)
  {
    this.mShowTitle = paramBoolean;
    invalidate();
  }

  public boolean showControls()
  {
    return false;
  }

  public boolean showHires()
  {
    return false;
  }

  public boolean showTitle()
  {
    return this.mShowTitle;
  }

  public boolean updateGeometryFlags()
  {
    return false;
  }

  public void updateImagePresets(boolean paramBoolean)
  {
    if (getImagePreset() != null)
      return;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.imageshow.ImageSmallFilter
 * JD-Core Version:    0.5.4
 */