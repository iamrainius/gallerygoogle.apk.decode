package com.android.camera.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TwoStateImageView extends ImageView
{
  private boolean mFilterEnabled = true;

  public TwoStateImageView(Context paramContext)
  {
    this(paramContext, null);
  }

  public TwoStateImageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public void enableFilter(boolean paramBoolean)
  {
    this.mFilterEnabled = paramBoolean;
  }

  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    if (this.mFilterEnabled)
    {
      if (!paramBoolean)
        break label24;
      setAlpha(255);
    }
    return;
    label24: setAlpha(102);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.TwoStateImageView
 * JD-Core Version:    0.5.4
 */