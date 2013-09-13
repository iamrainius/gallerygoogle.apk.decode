package com.android.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ShutterButton extends ImageView
{
  private OnShutterButtonListener mListener;
  private boolean mOldPressed;
  private boolean mTouchEnabled = true;

  public ShutterButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  private void callShutterButtonFocus(boolean paramBoolean)
  {
    if (this.mListener == null)
      return;
    this.mListener.onShutterButtonFocus(paramBoolean);
  }

  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mTouchEnabled)
      return super.dispatchTouchEvent(paramMotionEvent);
    return false;
  }

  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    boolean bool = isPressed();
    if (bool != this.mOldPressed)
    {
      if (bool)
        break label41;
      post(new Runnable(bool)
      {
        public void run()
        {
          ShutterButton.this.callShutterButtonFocus(this.val$pressed);
        }
      });
    }
    while (true)
    {
      this.mOldPressed = bool;
      return;
      label41: callShutterButtonFocus(bool);
    }
  }

  public boolean performClick()
  {
    boolean bool = super.performClick();
    if (this.mListener != null)
      this.mListener.onShutterButtonClick();
    return bool;
  }

  public void setOnShutterButtonListener(OnShutterButtonListener paramOnShutterButtonListener)
  {
    this.mListener = paramOnShutterButtonListener;
  }

  public static abstract interface OnShutterButtonListener
  {
    public abstract void onShutterButtonClick();

    public abstract void onShutterButtonFocus(boolean paramBoolean);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ShutterButton
 * JD-Core Version:    0.5.4
 */