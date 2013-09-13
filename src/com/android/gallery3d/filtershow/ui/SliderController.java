package com.android.gallery3d.filtershow.ui;

import android.graphics.Canvas;
import android.graphics.Paint;

public class SliderController
{
  private static int mTextSize = 128;
  private int mHeight = 0;
  private SliderListener mListener = null;
  private MODES mMode = MODES.NONE;
  int mOriginalValue = 0;
  private final Paint mPaint = new Paint();
  private String mToast = null;
  private int mValue = 100;
  private int mWidth = 0;

  public void onDraw(Canvas paramCanvas)
  {
    if ((this.mMode == MODES.NONE) || (this.mMode != MODES.UP))
      return;
  }

  private static enum MODES
  {
    static
    {
      DOWN = new MODES("DOWN", 1);
      UP = new MODES("UP", 2);
      MOVE = new MODES("MOVE", 3);
      MODES[] arrayOfMODES = new MODES[4];
      arrayOfMODES[0] = NONE;
      arrayOfMODES[1] = DOWN;
      arrayOfMODES[2] = UP;
      arrayOfMODES[3] = MOVE;
      $VALUES = arrayOfMODES;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.ui.SliderController
 * JD-Core Version:    0.5.4
 */