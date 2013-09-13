package com.google.android.apps.lightcycle.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import com.google.android.apps.lightcycle.util.LG;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Vector;

public class Sprite extends DrawableGL
{
  private float mDepth = 4.0F;
  private float mHalfX;
  private float mHalfY;
  private Point mImageDim = new Point();
  private boolean mInitialized = false;
  private int mNumIndices;
  private int mNumVertices;
  private float[] mObjectTransform = new float[16];
  private float[] mTransform = new float[16];

  private void createRenderData()
  {
    this.mNumIndices = 6;
    this.mNumVertices = 4;
    this.mVertices = ByteBuffer.allocateDirect(4 * (3 * this.mNumVertices)).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.mTexCoords = ByteBuffer.allocateDirect(4 * (2 * this.mNumVertices)).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.mIndices = ByteBuffer.allocateDirect(2 * this.mNumIndices).order(ByteOrder.nativeOrder()).asShortBuffer();
    this.mVertices.clear();
    this.mTexCoords.clear();
    this.mIndices.clear();
    this.mHalfX = (this.mImageDim.x / 2.0F);
    this.mHalfY = (this.mImageDim.y / 2.0F);
    float[] arrayOfFloat = { 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F };
    for (int i = 0; i < arrayOfFloat.length; ++i)
      this.mTexCoords.put(i, arrayOfFloat[i]);
    short[] arrayOfShort = { 0, 1, 2, 0, 2, 3 };
    for (int j = 0; j < arrayOfShort.length; ++j)
      this.mIndices.put(j, arrayOfShort[j]);
    Matrix.setIdentityM(this.mObjectTransform, 0);
  }

  private boolean initFromBitmap(Bitmap paramBitmap)
  {
    GLTexture localGLTexture = new GLTexture();
    this.mTextures.add(0, localGLTexture);
    this.mImageDim.set(paramBitmap.getWidth(), paramBitmap.getHeight());
    try
    {
      ((GLTexture)this.mTextures.get(0)).loadBitmap(paramBitmap);
      createRenderData();
      return true;
    }
    catch (OpenGLException localOpenGLException)
    {
      localOpenGLException.printStackTrace();
    }
    return false;
  }

  private boolean initFromDrawable(Context paramContext, int paramInt)
  {
    GLTexture localGLTexture = new GLTexture();
    this.mTextures.add(0, localGLTexture);
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inScaled = false;
    Bitmap localBitmap = BitmapFactory.decodeResource(paramContext.getResources(), paramInt, localOptions);
    if (localBitmap == null)
      return false;
    this.mImageDim.set(localBitmap.getWidth(), localBitmap.getHeight());
    try
    {
      ((GLTexture)this.mTextures.get(0)).loadBitmap(localBitmap);
      localBitmap.recycle();
      createRenderData();
      return true;
    }
    catch (OpenGLException localOpenGLException)
    {
      localOpenGLException.printStackTrace();
    }
  }

  public void drawObject(float[] paramArrayOfFloat)
    throws OpenGLException
  {
  }

  public void drawRotated(float[] paramArrayOfFloat, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    throws OpenGLException
  {
    if (!this.mInitialized)
    {
      Log.e("LightCycle", "Sprite not initialized.");
      return;
    }
    if (this.mShader == null)
    {
      LG.d("The shader does not exist.");
      return;
    }
    this.mShader.bind();
    this.mVertices.position(0);
    this.mTexCoords.position(0);
    this.mShader.setVertices(this.mVertices);
    this.mShader.setTexCoords(this.mTexCoords);
    Matrix.translateM(this.mTransform, 0, paramArrayOfFloat, 0, paramFloat1, paramFloat2, 0.0F);
    Matrix.rotateM(this.mTransform, 0, paramFloat3, 0.0F, 0.0F, 1.0F);
    if (paramFloat4 != 1.0F)
      Matrix.scaleM(this.mTransform, 0, paramFloat4, paramFloat4, paramFloat4);
    this.mShader.setTransform(this.mTransform);
    if (this.mTextures.size() == 0)
    {
      LG.d("Error : no textures defined for Sprite");
      return;
    }
    ((GLTexture)this.mTextures.get(0)).bind(this.mShader);
    this.mIndices.position(0);
    GLES20.glDrawElements(4, this.mNumIndices, 5123, this.mIndices);
  }

  public void drawRotatedCentered(float[] paramArrayOfFloat, float paramFloat1, float paramFloat2, float paramFloat3)
    throws OpenGLException
  {
    if (!this.mInitialized)
    {
      Log.e("LightCycle", "Sprite not initialized.");
      return;
    }
    if (this.mShader == null)
    {
      LG.d("The shader does not exist.");
      return;
    }
    this.mShader.bind();
    this.mVertices.position(0);
    this.mTexCoords.position(0);
    this.mShader.setVertices(this.mVertices);
    this.mShader.setTexCoords(this.mTexCoords);
    Matrix.translateM(this.mTransform, 0, paramArrayOfFloat, 0, paramFloat1 + this.mHalfX, paramFloat2 + this.mHalfY, 0.0F);
    Matrix.rotateM(this.mTransform, 0, paramFloat3, 0.0F, 0.0F, 1.0F);
    this.mShader.setTransform(this.mTransform);
    if (this.mTextures.size() == 0)
    {
      LG.d("Error : no textures defined for Sprite");
      return;
    }
    ((GLTexture)this.mTextures.get(0)).bind(this.mShader);
    this.mIndices.position(0);
    GLES20.glDrawElements(4, this.mNumIndices, 5123, this.mIndices);
  }

  public int getHeight()
  {
    return this.mImageDim.y;
  }

  public int getWidth()
  {
    return this.mImageDim.x;
  }

  public void init2D(int paramInt1, int paramInt2, float paramFloat)
  {
    this.mImageDim.set(paramInt1, paramInt2);
    createRenderData();
    this.mHalfX = (paramInt1 / 2.0F);
    this.mHalfY = (paramInt2 / 2.0F);
    this.mDepth = paramFloat;
    float[] arrayOfFloat = new float[12];
    arrayOfFloat[0] = (-this.mHalfX);
    arrayOfFloat[1] = this.mHalfY;
    arrayOfFloat[2] = this.mDepth;
    arrayOfFloat[3] = this.mHalfX;
    arrayOfFloat[4] = this.mHalfY;
    arrayOfFloat[5] = this.mDepth;
    arrayOfFloat[6] = this.mHalfX;
    arrayOfFloat[7] = (-this.mHalfY);
    arrayOfFloat[8] = this.mDepth;
    arrayOfFloat[9] = (-this.mHalfX);
    arrayOfFloat[10] = (-this.mHalfY);
    arrayOfFloat[11] = this.mDepth;
    for (int i = 0; i < arrayOfFloat.length; ++i)
      this.mVertices.put(i, arrayOfFloat[i]);
    this.mInitialized = true;
  }

  public boolean init2D(Context paramContext, int paramInt, float paramFloat1, float paramFloat2)
  {
    if (!initFromDrawable(paramContext, paramInt))
      return false;
    this.mDepth = paramFloat1;
    this.mHalfX = (paramFloat2 * this.mHalfX);
    this.mHalfY = (paramFloat2 * this.mHalfY);
    float[] arrayOfFloat = new float[12];
    arrayOfFloat[0] = (-this.mHalfX);
    arrayOfFloat[1] = this.mHalfY;
    arrayOfFloat[2] = this.mDepth;
    arrayOfFloat[3] = this.mHalfX;
    arrayOfFloat[4] = this.mHalfY;
    arrayOfFloat[5] = this.mDepth;
    arrayOfFloat[6] = this.mHalfX;
    arrayOfFloat[7] = (-this.mHalfY);
    arrayOfFloat[8] = this.mDepth;
    arrayOfFloat[9] = (-this.mHalfX);
    arrayOfFloat[10] = (-this.mHalfY);
    arrayOfFloat[11] = this.mDepth;
    for (int i = 0; i < arrayOfFloat.length; ++i)
      this.mVertices.put(i, arrayOfFloat[i]);
    this.mInitialized = true;
    return true;
  }

  public boolean init2D(Bitmap paramBitmap, float paramFloat1, float paramFloat2)
  {
    initFromBitmap(paramBitmap);
    this.mDepth = paramFloat1;
    this.mHalfX = (paramFloat2 * this.mHalfX);
    this.mHalfY = (paramFloat2 * this.mHalfY);
    float[] arrayOfFloat = new float[12];
    arrayOfFloat[0] = (-this.mHalfX);
    arrayOfFloat[1] = this.mHalfY;
    arrayOfFloat[2] = this.mDepth;
    arrayOfFloat[3] = this.mHalfX;
    arrayOfFloat[4] = this.mHalfY;
    arrayOfFloat[5] = this.mDepth;
    arrayOfFloat[6] = this.mHalfX;
    arrayOfFloat[7] = (-this.mHalfY);
    arrayOfFloat[8] = this.mDepth;
    arrayOfFloat[9] = (-this.mHalfX);
    arrayOfFloat[10] = (-this.mHalfY);
    arrayOfFloat[11] = this.mDepth;
    for (int i = 0; i < arrayOfFloat.length; ++i)
      this.mVertices.put(i, arrayOfFloat[i]);
    this.mInitialized = true;
    return true;
  }

  public void setTexture(GLTexture paramGLTexture)
  {
    if (this.mTextures.size() == 0)
    {
      this.mTextures.add(0, paramGLTexture);
      return;
    }
    this.mTextures.set(0, paramGLTexture);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.opengl.Sprite
 * JD-Core Version:    0.5.4
 */