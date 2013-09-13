package com.android.camera.ui;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import com.android.camera.Util;

public class RotateTextToast
{
  Handler mHandler;
  ViewGroup mLayoutRoot;
  private final Runnable mRunnable = new Runnable()
  {
    public void run()
    {
      Util.fadeOut(RotateTextToast.this.mToast);
      RotateTextToast.this.mLayoutRoot.removeView(RotateTextToast.this.mToast);
      RotateTextToast.this.mToast = null;
    }
  };
  RotateLayout mToast;

  public RotateTextToast(Activity paramActivity, int paramInt1, int paramInt2)
  {
    this.mLayoutRoot = ((ViewGroup)paramActivity.getWindow().getDecorView());
    this.mToast = ((RotateLayout)paramActivity.getLayoutInflater().inflate(2130968654, this.mLayoutRoot).findViewById(2131558608));
    ((TextView)this.mToast.findViewById(2131558531)).setText(paramInt1);
    this.mToast.setOrientation(paramInt2, false);
    this.mHandler = new Handler();
  }

  public void show()
  {
    this.mToast.setVisibility(0);
    this.mHandler.postDelayed(this.mRunnable, 5000L);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.RotateTextToast
 * JD-Core Version:    0.5.4
 */