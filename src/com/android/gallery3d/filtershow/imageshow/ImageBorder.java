package com.android.gallery3d.filtershow.imageshow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

public class ImageBorder extends ImageSlave
{
  Paint gPaint = new Paint();

  public ImageBorder(Context paramContext)
  {
    super(paramContext);
  }

  public ImageBorder(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
  }

  public boolean showTitle()
  {
    return false;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.imageshow.ImageBorder
 * JD-Core Version:    0.5.4
 */