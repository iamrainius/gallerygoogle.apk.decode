package com.android.camera;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class OnScreenHint
{
  int mGravity = 81;
  private final Handler mHandler = new Handler();
  private final Runnable mHide = new Runnable()
  {
    public void run()
    {
      OnScreenHint.this.handleHide();
    }
  };
  float mHorizontalMargin;
  View mNextView;
  private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
  private final Runnable mShow = new Runnable()
  {
    public void run()
    {
      OnScreenHint.this.handleShow();
    }
  };
  float mVerticalMargin;
  View mView;
  private final WindowManager mWM;
  int mX;
  int mY;

  private OnScreenHint(Context paramContext)
  {
    this.mWM = ((WindowManager)paramContext.getSystemService("window"));
    this.mY = paramContext.getResources().getDimensionPixelSize(2131623936);
    this.mParams.height = -2;
    this.mParams.width = -2;
    this.mParams.flags = 24;
    this.mParams.format = -3;
    this.mParams.windowAnimations = 2131492873;
    this.mParams.type = 1000;
    this.mParams.setTitle("OnScreenHint");
  }

  private void handleHide()
  {
    monitorenter;
    try
    {
      if (this.mView != null)
      {
        if (this.mView.getParent() != null)
          this.mWM.removeView(this.mView);
        this.mView = null;
      }
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  private void handleShow()
  {
    monitorenter;
    try
    {
      if (this.mView != this.mNextView)
      {
        handleHide();
        this.mView = this.mNextView;
        int i = this.mGravity;
        this.mParams.gravity = i;
        if ((i & 0x7) == 7)
          this.mParams.horizontalWeight = 1.0F;
        if ((i & 0x70) == 112)
          this.mParams.verticalWeight = 1.0F;
        this.mParams.x = this.mX;
        this.mParams.y = this.mY;
        this.mParams.verticalMargin = this.mVerticalMargin;
        this.mParams.horizontalMargin = this.mHorizontalMargin;
        if (this.mView.getParent() != null)
          this.mWM.removeView(this.mView);
        this.mWM.addView(this.mView, this.mParams);
      }
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  public static OnScreenHint makeText(Context paramContext, CharSequence paramCharSequence)
  {
    OnScreenHint localOnScreenHint = new OnScreenHint(paramContext);
    View localView = ((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(2130968619, null);
    ((TextView)localView.findViewById(2131558531)).setText(paramCharSequence);
    localOnScreenHint.mNextView = localView;
    return localOnScreenHint;
  }

  public void cancel()
  {
    this.mHandler.post(this.mHide);
  }

  public void setText(CharSequence paramCharSequence)
  {
    if (this.mNextView == null)
      throw new RuntimeException("This OnScreenHint was not created with OnScreenHint.makeText()");
    TextView localTextView = (TextView)this.mNextView.findViewById(2131558531);
    if (localTextView == null)
      throw new RuntimeException("This OnScreenHint was not created with OnScreenHint.makeText()");
    localTextView.setText(paramCharSequence);
  }

  public void show()
  {
    if (this.mNextView == null)
      throw new RuntimeException("View is not initialized");
    this.mHandler.post(this.mShow);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.OnScreenHint
 * JD-Core Version:    0.5.4
 */