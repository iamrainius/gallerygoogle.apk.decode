package com.android.gallery3d.filtershow.imageshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class ImageWithIcon extends ImageSmallFilter
{
  private Bitmap bitmap;

  public ImageWithIcon(Context paramContext)
  {
    super(paramContext);
  }

  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (this.bitmap == null)
      return;
    Rect localRect = new Rect(0, mMargin, getWidth() - mMargin, getWidth());
    drawImage(paramCanvas, this.bitmap, localRect);
  }

  public void setIcon(Bitmap paramBitmap)
  {
    this.bitmap = paramBitmap;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.imageshow.ImageWithIcon
 * JD-Core Version:    0.5.4
 */