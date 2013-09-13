package com.android.camera.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

public class PreviewSurfaceView extends SurfaceView
{
  public PreviewSurfaceView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setZOrderMediaOverlay(true);
    getHolder().setType(3);
  }

  private void setLayoutSize(int paramInt)
  {
    ViewGroup.LayoutParams localLayoutParams = getLayoutParams();
    if ((localLayoutParams.width == paramInt) && (localLayoutParams.height == paramInt))
      return;
    localLayoutParams.width = paramInt;
    localLayoutParams.height = paramInt;
    setLayoutParams(localLayoutParams);
  }

  public void expand()
  {
    setLayoutSize(-1);
  }

  public void shrink()
  {
    setLayoutSize(1);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.PreviewSurfaceView
 * JD-Core Version:    0.5.4
 */