package com.google.android.apps.lightcycle.panorama;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;
import com.google.android.apps.lightcycle.opengl.DeviceOrientedSprite;
import com.google.android.apps.lightcycle.opengl.DrawableGL;
import com.google.android.apps.lightcycle.opengl.GLTexture;
import com.google.android.apps.lightcycle.opengl.OpenGLException;
import com.google.android.apps.lightcycle.opengl.SingleColorShader;
import com.google.android.apps.lightcycle.sensor.DeviceOrientationDetector;
import com.google.android.apps.lightcycle.sensor.DeviceOrientationDetector.DeviceOrientation;
import com.google.android.apps.lightcycle.util.LG;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Vector;

public class Pano2dPreviewOverlay extends DrawableGL
{
  private DeviceOrientedSprite mBackgroundSprite;
  private boolean mDisplayFlatMode = false;
  private float mHeight;
  private boolean mInitialized = false;
  private PointF mLandscapeCenter;
  private SingleColorShader mLineShader = null;
  private DeviceOrientationDetector mOrientationDetector = null;
  private float[] mOutlineCoords = new float[40];
  private ShortBuffer mOutlineIndices = ByteBuffer.allocateDirect(40).order(ByteOrder.nativeOrder()).asShortBuffer();
  private FloatBuffer mOutlineVertices = ByteBuffer.allocateDirect(240).order(ByteOrder.nativeOrder()).asFloatBuffer();
  private PointF mPortraitCenter;
  private DeviceOrientedSprite mPreviewSprite;
  private boolean mValidEstimate = false;
  private float mWidth;

  private void updateCurrentFrameOutlineLandscape()
  {
    this.mOutlineCoords = LightCycleNative.GetFramePanoOutline(5, 5);
    float f1 = this.mLandscapeCenter.x - this.mWidth / 2.0F;
    float f2 = this.mLandscapeCenter.y - this.mHeight / 2.0F;
    int i = 0;
    int j = 0;
    int i3;
    for (int k = 0; i < 20; k = i3)
    {
      float[] arrayOfFloat1 = this.mOutlineCoords;
      int l = j + 1;
      float f3 = f1 + arrayOfFloat1[j] * this.mWidth;
      float[] arrayOfFloat2 = this.mOutlineCoords;
      j = l + 1;
      float f4 = f2 + arrayOfFloat2[l] * this.mHeight;
      FloatBuffer localFloatBuffer1 = this.mOutlineVertices;
      int i1 = k + 1;
      localFloatBuffer1.put(k, f3);
      FloatBuffer localFloatBuffer2 = this.mOutlineVertices;
      int i2 = i1 + 1;
      localFloatBuffer2.put(i1, f4);
      FloatBuffer localFloatBuffer3 = this.mOutlineVertices;
      i3 = i2 + 1;
      localFloatBuffer3.put(i2, -0.5F);
      this.mOutlineIndices.put(i, (short)i);
      ++i;
    }
  }

  private void updateCurrentFrameOutlinePortrait()
  {
    this.mOutlineCoords = LightCycleNative.GetFramePanoOutline(5, 5);
    (this.mPortraitCenter.x - this.mHeight / 2.0F);
    float f1 = this.mPortraitCenter.x + this.mHeight / 2.0F;
    float f2 = this.mPortraitCenter.y - this.mWidth / 2.0F;
    int i = 0;
    int j = 0;
    int i3;
    for (int k = 0; i < 20; k = i3)
    {
      float[] arrayOfFloat1 = this.mOutlineCoords;
      int l = j + 1;
      float f3 = f2 + arrayOfFloat1[j] * this.mWidth;
      float[] arrayOfFloat2 = this.mOutlineCoords;
      j = l + 1;
      float f4 = f1 - arrayOfFloat2[l] * this.mHeight;
      FloatBuffer localFloatBuffer1 = this.mOutlineVertices;
      int i1 = k + 1;
      localFloatBuffer1.put(k, f4);
      FloatBuffer localFloatBuffer2 = this.mOutlineVertices;
      int i2 = i1 + 1;
      localFloatBuffer2.put(i1, f3);
      FloatBuffer localFloatBuffer3 = this.mOutlineVertices;
      i3 = i2 + 1;
      localFloatBuffer3.put(i2, -0.5F);
      this.mOutlineIndices.put(i, (short)i);
      ++i;
    }
  }

  public void drawObject(float[] paramArrayOfFloat)
    throws OpenGLException
  {
    if (!this.mInitialized);
    do
    {
      return;
      if (this.mTextures.size() == 0)
      {
        LG.d("No textures defined!");
        return;
      }
      this.mBackgroundSprite.setShader(this.mLineShader);
      this.mBackgroundSprite.setTexture((GLTexture)this.mTextures.get(0));
      this.mBackgroundSprite.draw(paramArrayOfFloat);
      this.mPreviewSprite.setTexture((GLTexture)this.mTextures.get(0));
      this.mPreviewSprite.setShader(this.mShader);
      this.mPreviewSprite.draw(paramArrayOfFloat);
    }
    while (!this.mValidEstimate);
    GLES20.glDisable(2929);
    updateCurrentFrameOutline();
    this.mLineShader.bind();
    this.mLineShader.setVertices(this.mOutlineVertices);
    this.mLineShader.setTransform(paramArrayOfFloat);
    this.mOutlineIndices.position(0);
    this.mOutlineVertices.position(0);
    GLES20.glLineWidth(2.0F);
    GLES20.glDrawElements(2, 20, 5123, this.mOutlineIndices);
    GLES20.glEnable(2929);
  }

  public void init(Context paramContext, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, DeviceOrientationDetector paramDeviceOrientationDetector)
  {
    this.mWidth = (paramFloat4 * paramFloat2);
    this.mHeight = (this.mWidth / 2.5F);
    this.mOrientationDetector = paramDeviceOrientationDetector;
    this.mPreviewSprite = new DeviceOrientedSprite(paramDeviceOrientationDetector);
    this.mPreviewSprite.init2D((int)this.mWidth, (int)this.mHeight, -1.0F);
    this.mBackgroundSprite = new DeviceOrientedSprite(paramDeviceOrientationDetector);
    this.mBackgroundSprite.init2D(2 + (int)this.mWidth, 2 + (int)this.mHeight, -1.0F);
    float f = 0.05F * paramFloat2;
    this.mPortraitCenter = new PointF(f + this.mWidth / 2.0F, f + this.mHeight / 2.0F);
    this.mLandscapeCenter = new PointF(f + this.mHeight / 2.0F, paramFloat3 - this.mWidth / 2.0F - f);
    this.mPreviewSprite.setPositions(this.mLandscapeCenter, this.mPortraitCenter, (int)paramFloat2, (int)paramFloat3);
    this.mBackgroundSprite.setPositions(this.mLandscapeCenter, this.mPortraitCenter, (int)paramFloat2, (int)paramFloat3);
    this.mInitialized = true;
  }

  public boolean initialized()
  {
    return this.mInitialized;
  }

  public boolean pointInside(float paramFloat1, float paramFloat2)
  {
    return this.mDisplayFlatMode;
  }

  public void setLineShader(SingleColorShader paramSingleColorShader)
  {
    this.mLineShader = paramSingleColorShader;
  }

  public void setTextureId(int paramInt)
  {
    this.mTextures.clear();
    GLTexture localGLTexture = new GLTexture();
    this.mTextures.add(0, localGLTexture);
    ((GLTexture)this.mTextures.get(0)).setIndex(paramInt);
  }

  public void updateCurrentFrameOutline()
  {
    if (this.mOrientationDetector == null)
      return;
    if (this.mOrientationDetector.getOrientation().isOrientationChanged90())
    {
      updateCurrentFrameOutlineLandscape();
      return;
    }
    updateCurrentFrameOutlinePortrait();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.Pano2dPreviewOverlay
 * JD-Core Version:    0.5.4
 */