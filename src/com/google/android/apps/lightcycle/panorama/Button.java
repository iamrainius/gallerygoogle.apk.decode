package com.google.android.apps.lightcycle.panorama;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.view.MotionEvent;
import com.google.android.apps.lightcycle.opengl.DeviceOrientedSprite;
import com.google.android.apps.lightcycle.opengl.OpenGLException;
import com.google.android.apps.lightcycle.opengl.Shader;
import com.google.android.apps.lightcycle.sensor.DeviceOrientationDetector;

public class Button extends MessageSender
  implements GuiManager.GuiElement
{
  private boolean mEnabled = true;
  private DeviceOrientationDetector mOrientationDetector;
  private boolean mSelected = false;
  private DeviceOrientedSprite mSprite;
  private DeviceOrientedSprite mSpriteSelected;
  private int mSurfaceHeight;
  private int mSurfaceWidth;
  private boolean mVisible = true;

  public Button(DeviceOrientationDetector paramDeviceOrientationDetector)
  {
    this.mOrientationDetector = paramDeviceOrientationDetector;
  }

  private boolean inRect(MotionEvent paramMotionEvent)
  {
    PointF localPointF = this.mSprite.getPosition();
    int i = this.mSprite.getWidth() / 2;
    int j = this.mSprite.getHeight() / 2;
    return new RectF(localPointF.x - i, this.mSurfaceHeight - localPointF.y - j, localPointF.x + i, this.mSurfaceHeight - localPointF.y + j).contains(paramMotionEvent.getX(), paramMotionEvent.getY());
  }

  public void draw(float[] paramArrayOfFloat)
  {
    if (!this.mVisible)
      return;
    GLES20.glBlendFunc(1, 771);
    while (true)
      try
      {
        if (this.mSelected)
        {
          this.mSpriteSelected.draw(paramArrayOfFloat);
          GLES20.glBlendFunc(770, 771);
          return;
        }
        this.mSprite.draw(paramArrayOfFloat);
      }
      catch (OpenGLException localOpenGLException)
      {
        localOpenGLException.printStackTrace();
      }
  }

  public boolean handleEvent(MotionEvent paramMotionEvent)
  {
    switch (0xFF & paramMotionEvent.getAction())
    {
    default:
    case 0:
      do
        return false;
      while (!inRect(paramMotionEvent));
      this.mSelected = true;
      return true;
    case 1:
      boolean bool1 = inRect(paramMotionEvent);
      int i = 0;
      if (bool1)
      {
        boolean bool2 = this.mVisible;
        i = 0;
        if (bool2)
        {
          boolean bool3 = this.mEnabled;
          i = 0;
          if (bool3)
          {
            notifyAll(1, 1.0F, "");
            i = 1;
          }
        }
      }
      this.mSelected = false;
      return i;
    case 2:
    }
    this.mSelected = inRect(paramMotionEvent);
    return this.mSelected;
  }

  public void init(Context paramContext, int paramInt1, int paramInt2, PointF paramPointF, float paramFloat, Shader paramShader, int paramInt3, int paramInt4)
  {
    if (paramInt3 < paramInt4)
      paramFloat *= paramInt4 / paramInt3;
    this.mSprite = new DeviceOrientedSprite(this.mOrientationDetector);
    this.mSpriteSelected = new DeviceOrientedSprite(this.mOrientationDetector);
    this.mSprite.init2D(paramContext, paramInt1, -1.0F, paramFloat);
    this.mSpriteSelected.init2D(paramContext, paramInt2, -1.0F, paramFloat);
    this.mSprite.setShader(paramShader);
    this.mSpriteSelected.setShader(paramShader);
    this.mSurfaceWidth = paramInt3;
    this.mSurfaceHeight = paramInt4;
    setPosition(paramPointF);
  }

  public void setEnabled(boolean paramBoolean)
  {
    this.mEnabled = paramBoolean;
  }

  public void setPosition(PointF paramPointF)
  {
    PointF localPointF1 = new PointF((int)(this.mSurfaceWidth * paramPointF.x), (int)(this.mSurfaceHeight * paramPointF.y));
    PointF localPointF2 = new PointF((int)(this.mSurfaceWidth * paramPointF.y), (int)(this.mSurfaceHeight * (1.0F - paramPointF.x)));
    this.mSprite.setPositions(localPointF2, localPointF1, this.mSurfaceWidth, this.mSurfaceHeight);
    this.mSpriteSelected.setPositions(localPointF2, localPointF1, this.mSurfaceWidth, this.mSurfaceHeight);
  }

  public void setVisible(boolean paramBoolean)
  {
    this.mVisible = paramBoolean;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.Button
 * JD-Core Version:    0.5.4
 */