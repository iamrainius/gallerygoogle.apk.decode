package com.android.gallery3d.ui;

import com.android.gallery3d.common.Utils;

public class GLPaint
{
  private int mColor = 0;
  private float mLineWidth = 1.0F;

  public int getColor()
  {
    return this.mColor;
  }

  public float getLineWidth()
  {
    return this.mLineWidth;
  }

  public void setColor(int paramInt)
  {
    this.mColor = paramInt;
  }

  public void setLineWidth(float paramFloat)
  {
    if (paramFloat >= 0.0F);
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      this.mLineWidth = paramFloat;
      return;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.GLPaint
 * JD-Core Version:    0.5.4
 */