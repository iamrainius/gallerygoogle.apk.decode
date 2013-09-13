package com.android.camera.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.List;

public class PieItem
{
  private float animate;
  private int inner;
  private int level;
  private float mAlpha;
  private float mCenter;
  private boolean mChangeAlphaWhenDisabled = true;
  private Drawable mDrawable;
  private boolean mEnabled;
  private List<PieItem> mItems;
  private OnClickListener mOnClickListener;
  private Path mPath;
  private boolean mSelected;
  private int outer;
  private float start;
  private float sweep;

  public PieItem(Drawable paramDrawable, int paramInt)
  {
    this.mDrawable = paramDrawable;
    this.level = paramInt;
    setAlpha(1.0F);
    this.mEnabled = true;
    setAnimationAngle(getAnimationAngle());
    this.start = -1.0F;
    this.mCenter = -1.0F;
  }

  public void addItem(PieItem paramPieItem)
  {
    if (this.mItems == null)
      this.mItems = new ArrayList();
    this.mItems.add(paramPieItem);
  }

  public void draw(Canvas paramCanvas)
  {
    this.mDrawable.draw(paramCanvas);
  }

  public float getAnimationAngle()
  {
    return this.animate;
  }

  public float getCenter()
  {
    return this.mCenter;
  }

  public int getInnerRadius()
  {
    return this.inner;
  }

  public int getIntrinsicHeight()
  {
    return this.mDrawable.getIntrinsicHeight();
  }

  public int getIntrinsicWidth()
  {
    return this.mDrawable.getIntrinsicWidth();
  }

  public List<PieItem> getItems()
  {
    return this.mItems;
  }

  public int getOuterRadius()
  {
    return this.outer;
  }

  public Path getPath()
  {
    return this.mPath;
  }

  public float getStartAngle()
  {
    return this.start + this.animate;
  }

  public float getSweep()
  {
    return this.sweep;
  }

  public boolean hasItems()
  {
    return this.mItems != null;
  }

  public boolean isEnabled()
  {
    return this.mEnabled;
  }

  public boolean isSelected()
  {
    return this.mSelected;
  }

  public void performClick()
  {
    if (this.mOnClickListener == null)
      return;
    this.mOnClickListener.onClick(this);
  }

  public void setAlpha(float paramFloat)
  {
    this.mAlpha = paramFloat;
    this.mDrawable.setAlpha((int)(255.0F * paramFloat));
  }

  public void setAnimationAngle(float paramFloat)
  {
    this.animate = paramFloat;
  }

  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mDrawable.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public void setEnabled(boolean paramBoolean)
  {
    this.mEnabled = paramBoolean;
    if (this.mChangeAlphaWhenDisabled)
    {
      if (!this.mEnabled)
        break label25;
      setAlpha(1.0F);
    }
    return;
    label25: setAlpha(0.3F);
  }

  public void setFixedSlice(float paramFloat1, float paramFloat2)
  {
    this.mCenter = paramFloat1;
    this.sweep = paramFloat2;
  }

  public void setGeometry(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2)
  {
    this.start = paramFloat1;
    this.sweep = paramFloat2;
    this.inner = paramInt1;
    this.outer = paramInt2;
  }

  public void setImageResource(Context paramContext, int paramInt)
  {
    Drawable localDrawable = paramContext.getResources().getDrawable(paramInt).mutate();
    localDrawable.setBounds(this.mDrawable.getBounds());
    this.mDrawable = localDrawable;
    setAlpha(this.mAlpha);
  }

  public void setOnClickListener(OnClickListener paramOnClickListener)
  {
    this.mOnClickListener = paramOnClickListener;
  }

  public void setPath(Path paramPath)
  {
    this.mPath = paramPath;
  }

  public void setSelected(boolean paramBoolean)
  {
    this.mSelected = paramBoolean;
  }

  public static abstract interface OnClickListener
  {
    public abstract void onClick(PieItem paramPieItem);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.PieItem
 * JD-Core Version:    0.5.4
 */