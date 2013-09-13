package com.android.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewStub;
import android.widget.RelativeLayout;
import com.android.camera.ui.LayoutChangeHelper;
import com.android.camera.ui.LayoutChangeNotifier;
import com.android.camera.ui.LayoutChangeNotifier.Listener;
import com.android.gallery3d.common.ApiHelper;

public class PreviewFrameLayout extends RelativeLayout
  implements LayoutChangeNotifier
{
  private double mAspectRatio;
  private View mBorder;
  private LayoutChangeHelper mLayoutChangeHelper;
  private OnSizeChangedListener mListener;

  public PreviewFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setAspectRatio(1.333333333333333D);
    this.mLayoutChangeHelper = new LayoutChangeHelper(this);
  }

  protected void onFinishInflate()
  {
    this.mBorder = findViewById(2131558586);
    if (!ApiHelper.HAS_FACE_DETECTION)
      return;
    ViewStub localViewStub = (ViewStub)findViewById(2131558582);
    if (localViewStub == null)
      return;
    localViewStub.inflate();
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.mLayoutChangeHelper.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt2);
    int k = getPaddingLeft() + getPaddingRight();
    int l = getPaddingTop() + getPaddingBottom();
    int i1 = i - k;
    int i2 = j - l;
    int i3 = i1 + k;
    int i4 = i2 + l;
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(i3, 1073741824), View.MeasureSpec.makeMeasureSpec(i4, 1073741824));
  }

  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mListener == null)
      return;
    this.mListener.onSizeChanged(paramInt1, paramInt2);
  }

  public void setAspectRatio(double paramDouble)
  {
    if (paramDouble <= 0.0D)
      throw new IllegalArgumentException();
    if (getResources().getConfiguration().orientation == 1)
      paramDouble = 1.0D / paramDouble;
    if (this.mAspectRatio == paramDouble)
      return;
    this.mAspectRatio = paramDouble;
    requestLayout();
  }

  public void setOnLayoutChangeListener(LayoutChangeNotifier.Listener paramListener)
  {
    this.mLayoutChangeHelper.setOnLayoutChangeListener(paramListener);
  }

  public void setOnSizeChangedListener(OnSizeChangedListener paramOnSizeChangedListener)
  {
    this.mListener = paramOnSizeChangedListener;
  }

  public void showBorder(boolean paramBoolean)
  {
    View localView = this.mBorder;
    if (paramBoolean);
    for (int i = 0; ; i = 4)
    {
      localView.setVisibility(i);
      return;
    }
  }

  public static abstract interface OnSizeChangedListener
  {
    public abstract void onSizeChanged(int paramInt1, int paramInt2);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.PreviewFrameLayout
 * JD-Core Version:    0.5.4
 */