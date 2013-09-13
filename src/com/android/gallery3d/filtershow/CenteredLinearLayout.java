package com.android.gallery3d.filtershow;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import com.android.gallery3d.R.styleable;

public class CenteredLinearLayout extends LinearLayout
{
  private final int mMaxWidth = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.CenteredLinearLayout).getDimensionPixelSize(0, 0);

  public CenteredLinearLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt1);
    View.MeasureSpec.getSize(paramInt2);
    Resources localResources = getContext().getResources();
    TypedValue.applyDimension(1, i, localResources.getDisplayMetrics());
    if ((this.mMaxWidth > 0) && (i > this.mMaxWidth))
    {
      int j = View.MeasureSpec.getMode(paramInt1);
      paramInt1 = View.MeasureSpec.makeMeasureSpec(this.mMaxWidth, j);
    }
    super.onMeasure(paramInt1, paramInt2);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.CenteredLinearLayout
 * JD-Core Version:    0.5.4
 */