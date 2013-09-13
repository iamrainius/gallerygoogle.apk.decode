package com.google.android.apps.lightcycle.panorama;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.util.DisplayMetrics;
import com.google.android.apps.lightcycle.Constants;
import com.google.android.apps.lightcycle.opengl.DeviceOrientedSprite;
import com.google.android.apps.lightcycle.opengl.DrawableGL;
import com.google.android.apps.lightcycle.opengl.OpenGLException;
import com.google.android.apps.lightcycle.opengl.Sprite;
import com.google.android.apps.lightcycle.sensor.DeviceOrientationDetector;
import com.google.android.apps.lightcycle.shaders.ScaledTransparencyShader;
import com.google.android.apps.lightcycle.util.LG;

public class MessageDisplay extends DrawableGL
{
  private Sprite mAlignmentLost;
  private Sprite mConfidential;
  private Context mContext;
  FadeableMessage mHitToStartMessage;
  FadeableMessage mHoldStillMessage;
  private DeviceOrientationDetector mOrientationDetector;
  private DeviceOrientedSprite mRotateDeviceCcw;
  private DeviceOrientedSprite mRotateDeviceCw;
  private int mSurfaceHeight;
  private int mSurfaceWidth;
  private ScaledTransparencyShader mTransparencyShader;

  private Sprite createOrientedSprite(Context paramContext, int paramInt, float paramFloat1, float paramFloat2)
  {
    DeviceOrientedSprite localDeviceOrientedSprite = new DeviceOrientedSprite(this.mOrientationDetector);
    localDeviceOrientedSprite.initCentered(paramContext, paramInt, -1.0F, paramFloat2, paramFloat1, paramFloat1, this.mSurfaceWidth, this.mSurfaceHeight);
    return localDeviceOrientedSprite;
  }

  private Sprite createOrientedSprite(Bitmap paramBitmap, float paramFloat1, float paramFloat2)
  {
    DeviceOrientedSprite localDeviceOrientedSprite = new DeviceOrientedSprite(this.mOrientationDetector);
    localDeviceOrientedSprite.initCentered(paramBitmap, -1.0F, paramFloat2, paramFloat1, paramFloat1, this.mSurfaceWidth, this.mSurfaceHeight);
    return localDeviceOrientedSprite;
  }

  private Bitmap createTextBitmap(String paramString, int paramInt, Typeface paramTypeface, float[] paramArrayOfFloat)
  {
    int i = (int)(0.5F + this.mContext.getResources().getDisplayMetrics().density * paramInt);
    Paint localPaint = new Paint();
    localPaint.setTypeface(paramTypeface);
    localPaint.setTextSize(i);
    Bitmap localBitmap = Bitmap.createBitmap(10 + (int)localPaint.measureText(paramString), (int)(1.5F * i), Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    localCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    localPaint.setARGB((int)(255.0F * paramArrayOfFloat[0]), (int)(255.0F * paramArrayOfFloat[1]), (int)(255.0F * paramArrayOfFloat[2]), (int)(255.0F * paramArrayOfFloat[3]));
    localCanvas.drawText(paramString, 5.0F, i + 5, localPaint);
    return localBitmap;
  }

  private void drawCenteredSprite(Sprite paramSprite, float[] paramArrayOfFloat)
  {
    paramSprite.setShader(this.mTransparencyShader);
    try
    {
      paramSprite.draw(paramArrayOfFloat);
      return;
    }
    catch (OpenGLException localOpenGLException)
    {
      LG.d("Draw sprite failed.");
    }
  }

  public void activateMessage(Message paramMessage, double paramDouble)
  {
    if (paramMessage == Message.HOLDSTILL)
      this.mHoldStillMessage.activate(paramDouble);
    do
      return;
    while (paramMessage != Message.HITTOSTART);
    this.mHitToStartMessage.activate(paramDouble);
  }

  public void drawMessage(float[] paramArrayOfFloat, Message paramMessage)
    throws OpenGLException
  {
    if (paramMessage == Message.ALIGNMENTLOST)
      drawCenteredSprite(this.mAlignmentLost, paramArrayOfFloat);
    do
    {
      return;
      if (paramMessage != Message.HOLDSTILL)
        continue;
      drawCenteredSprite(this.mHoldStillMessage.getSprite(), paramArrayOfFloat);
      return;
    }
    while (paramMessage != Message.HITTOSTART);
    drawCenteredSprite(this.mHitToStartMessage.getSprite(), paramArrayOfFloat);
  }

  public void drawMessages(float[] paramArrayOfFloat)
  {
    this.mHoldStillMessage.drawAndUpdate(paramArrayOfFloat);
    this.mHitToStartMessage.drawAndUpdate(paramArrayOfFloat);
  }

  public void drawObject(float[] paramArrayOfFloat)
    throws OpenGLException
  {
  }

  public void drawRotateDevice(float[] paramArrayOfFloat, boolean paramBoolean)
  {
    GLES20.glBlendFunc(1, 771);
    if (paramBoolean)
      drawCenteredSprite(this.mRotateDeviceCw, paramArrayOfFloat);
    while (true)
    {
      GLES20.glBlendFunc(770, 771);
      return;
      drawCenteredSprite(this.mRotateDeviceCcw, paramArrayOfFloat);
    }
  }

  public void init(Context paramContext, int paramInt1, int paramInt2, DeviceOrientationDetector paramDeviceOrientationDetector)
  {
    this.mContext = paramContext;
    this.mSurfaceWidth = paramInt1;
    this.mSurfaceHeight = paramInt2;
    this.mOrientationDetector = paramDeviceOrientationDetector;
    try
    {
      this.mTransparencyShader = new ScaledTransparencyShader();
      this.mAlignmentLost = createOrientedSprite(paramContext, 2130837505, 0.82F, 1.0F);
      this.mConfidential = createOrientedSprite(paramContext, 2130837571, 0.95F, 1.0F);
      this.mRotateDeviceCw = new DeviceOrientedSprite(this.mOrientationDetector);
      this.mRotateDeviceCw.initCentered(paramContext, 2130837736, -1.0F, 1.0F, 0.85F, 0.85F, paramInt1, paramInt2);
      this.mRotateDeviceCcw = new DeviceOrientedSprite(this.mOrientationDetector);
      this.mRotateDeviceCcw.initCentered(paramContext, 2130837735, -1.0F, 1.0F, 0.85F, 0.85F, paramInt1, paramInt2);
      this.mHitToStartMessage = new FadeableMessage(2131361826);
      this.mHoldStillMessage = new FadeableMessage(2131361825);
      return;
    }
    catch (OpenGLException localOpenGLException)
    {
      localOpenGLException.printStackTrace();
    }
  }

  private class FadeableMessage
  {
    private float mAlpha = 0.0F;
    private double mDelay = 0.0D;
    private Sprite mSprite = null;
    private boolean mTimerFinished = true;
    private long mTimerStart = 0L;

    public FadeableMessage(int arg2)
    {
      int i;
      Bitmap localBitmap = MessageDisplay.this.createTextBitmap(MessageDisplay.this.mContext.getResources().getString(i), 18, Typeface.SANS_SERIF, Constants.WHITE);
      this.mSprite = MessageDisplay.this.createOrientedSprite(localBitmap, 0.82F, 1.0F);
      localBitmap.recycle();
    }

    private float updateAlpha(float paramFloat)
    {
      float f = paramFloat - 0.1F * paramFloat;
      if (f < 0.005F)
        f = 0.0F;
      return f;
    }

    public void activate(double paramDouble)
    {
      this.mDelay = paramDouble;
      this.mAlpha = 1.0F;
      this.mTimerStart = System.nanoTime();
      this.mTimerFinished = false;
    }

    public void drawAndUpdate(float[] paramArrayOfFloat)
    {
      if (this.mAlpha == 0.0F);
      do
      {
        return;
        if ((!this.mTimerFinished) && ((System.nanoTime() - this.mTimerStart) / 1000000000.0D > this.mDelay))
          this.mTimerFinished = true;
        MessageDisplay.this.mTransparencyShader.bind();
        MessageDisplay.this.mTransparencyShader.setAlpha(this.mAlpha);
        MessageDisplay.this.drawCenteredSprite(this.mSprite, paramArrayOfFloat);
      }
      while (!this.mTimerFinished);
      this.mAlpha = updateAlpha(this.mAlpha);
    }

    public Sprite getSprite()
    {
      return this.mSprite;
    }
  }

  public static enum Message
  {
    static
    {
      HITTOSTART = new Message("HITTOSTART", 2);
      Message[] arrayOfMessage = new Message[3];
      arrayOfMessage[0] = ALIGNMENTLOST;
      arrayOfMessage[1] = HOLDSTILL;
      arrayOfMessage[2] = HITTOSTART;
      $VALUES = arrayOfMessage;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.MessageDisplay
 * JD-Core Version:    0.5.4
 */