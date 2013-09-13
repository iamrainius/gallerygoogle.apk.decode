package com.google.android.apps.lightcycle.panorama;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.PointF;
import android.view.MotionEvent;
import com.google.android.apps.lightcycle.opengl.Shader;
import com.google.android.apps.lightcycle.sensor.DeviceOrientationDetector;
import com.google.android.apps.lightcycle.util.Callback;

public class RenderedGui extends MessageSender
{
  private Button doneButton = null;
  private Callback<Boolean> doneButtonVisibilityListener = null;
  private boolean enabledUndoButton = true;
  private GuiManager guiManager = new GuiManager();
  private DeviceOrientationDetector orientationDetector;
  private boolean showOwnDoneButton = true;
  private boolean showOwnUndoButton = true;
  private Button undoButton = null;
  private Callback<Boolean> undoButtonStatusListener = null;
  private Callback<Boolean> undoButtonVisibilityListener = null;

  private void notifyDone()
  {
    notifyAll(1, 0.0F, "");
  }

  public void draw(float[] paramArrayOfFloat)
  {
    this.guiManager.draw(paramArrayOfFloat);
  }

  public boolean handleEvent(MotionEvent paramMotionEvent)
  {
    return this.guiManager.handleEvent(paramMotionEvent);
  }

  public void init(Context paramContext, Shader paramShader, int paramInt1, int paramInt2, DeviceOrientationDetector paramDeviceOrientationDetector)
  {
    this.orientationDetector = paramDeviceOrientationDetector;
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inScaled = false;
    Bitmap localBitmap1 = BitmapFactory.decodeResource(paramContext.getResources(), 2130837577, localOptions);
    float f1 = 0.129F * paramInt1 / localBitmap1.getWidth();
    localBitmap1.recycle();
    this.doneButton = new Button(this.orientationDetector);
    this.doneButton.init(paramContext, 2130837577, 2130837578, new PointF(0.0F, 0.0F), f1, paramShader, paramInt1, paramInt2);
    this.doneButton.setPosition(new PointF(0.85F, 0.1125F));
    this.doneButton.setVisible(false);
    this.guiManager.addElement(this.doneButton);
    this.doneButton.subscribe(new MessageSender.MessageSubscriber()
    {
      public void message(int paramInt, float paramFloat, String paramString)
      {
        RenderedGui.this.doneButton.setVisible(false);
        RenderedGui.this.notifyDone();
      }
    });
    Bitmap localBitmap2 = BitmapFactory.decodeResource(paramContext.getResources(), 2130837900, localOptions);
    float f2 = 0.129F * paramInt1 / localBitmap2.getWidth();
    localBitmap2.recycle();
    this.undoButton = new Button(this.orientationDetector);
    this.undoButton.init(paramContext, 2130837900, 2130837901, new PointF(0.0F, 0.0F), f2, paramShader, paramInt1, paramInt2);
    this.undoButton.setPosition(new PointF(0.87F, 0.94F));
    this.undoButton.setVisible(false);
    this.guiManager.addElement(this.undoButton);
    this.undoButton.subscribe(new MessageSender.MessageSubscriber()
    {
      public void message(int paramInt, float paramFloat, String paramString)
      {
        RenderedGui.this.notifyUndo();
      }
    });
  }

  public void notifyUndo()
  {
    notifyAll(2, 0.0F, "");
  }

  public void setDoneButtonVisibilityListener(Callback<Boolean> paramCallback)
  {
    this.doneButtonVisibilityListener = paramCallback;
  }

  public void setDoneButtonVisible(boolean paramBoolean)
  {
    Button localButton;
    if (this.doneButton != null)
    {
      localButton = this.doneButton;
      if ((!paramBoolean) || (!this.showOwnDoneButton))
        break label51;
    }
    for (boolean bool = true; ; bool = false)
    {
      localButton.setVisible(bool);
      if (this.doneButtonVisibilityListener != null)
        this.doneButtonVisibilityListener.onCallback(Boolean.valueOf(paramBoolean));
      label51: return;
    }
  }

  public void setShowOwnDoneButton(boolean paramBoolean)
  {
    this.showOwnDoneButton = paramBoolean;
    if ((this.doneButton == null) || (paramBoolean))
      return;
    this.doneButton.setVisible(false);
  }

  public void setShowOwnUndoButton(boolean paramBoolean)
  {
    this.showOwnUndoButton = paramBoolean;
    if ((this.undoButton == null) || (paramBoolean))
      return;
    this.undoButton.setVisible(false);
  }

  public void setUndoButtonEnabled(boolean paramBoolean)
  {
    if (paramBoolean == this.enabledUndoButton);
    do
    {
      return;
      this.enabledUndoButton = paramBoolean;
      if (this.undoButton == null)
        continue;
      this.undoButton.setEnabled(paramBoolean);
    }
    while (this.undoButtonStatusListener == null);
    this.undoButtonStatusListener.onCallback(Boolean.valueOf(paramBoolean));
  }

  public void setUndoButtonStatusListener(Callback<Boolean> paramCallback)
  {
    this.undoButtonStatusListener = paramCallback;
  }

  public void setUndoButtonVisibilityListener(Callback<Boolean> paramCallback)
  {
    this.undoButtonVisibilityListener = paramCallback;
  }

  public void setUndoButtonVisible(boolean paramBoolean)
  {
    Button localButton;
    if (this.undoButton != null)
    {
      localButton = this.undoButton;
      if ((!paramBoolean) || (!this.showOwnUndoButton))
        break label51;
    }
    for (boolean bool = true; ; bool = false)
    {
      localButton.setVisible(bool);
      if (this.undoButtonVisibilityListener != null)
        this.undoButtonVisibilityListener.onCallback(Boolean.valueOf(paramBoolean));
      label51: return;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.RenderedGui
 * JD-Core Version:    0.5.4
 */