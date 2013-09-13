package com.google.android.apps.lightcycle.opengl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import com.google.android.apps.lightcycle.shaders.TransparencyShader;
import java.nio.FloatBuffer;

public class TexturedCube extends DrawableGL
{
  private int mNumIndices = 0;
  private TransparencyShader shader;
  private GLTexture texture;

  public TexturedCube(Bitmap paramBitmap, float paramFloat)
  {
    try
    {
      this.shader = new TransparencyShader();
      this.texture = new GLTexture();
      generateGeometry(paramBitmap, paramFloat);
      return;
    }
    catch (OpenGLException localOpenGLException)
    {
      localOpenGLException.printStackTrace();
    }
  }

  private void generateGeometry(Bitmap paramBitmap, float paramFloat)
  {
    this.mNumIndices = 36;
    initGeometry(36, this.mNumIndices, true);
    float[] arrayOfFloat = { -1.0F, -1.0F, 1.0F, 1.0F, -1.0F, 1.0F, 1.0F, 1.0F, 1.0F, -1.0F, 1.0F, 1.0F, -1.0F, -1.0F, -1.0F, 1.0F, -1.0F, -1.0F, 1.0F, 1.0F, -1.0F, -1.0F, 1.0F, -1.0F };
    short[] arrayOfShort = { 0, 7, 3, 0, 4, 7, 4, 6, 7, 4, 5, 6, 5, 2, 6, 5, 1, 2, 1, 3, 2, 1, 0, 3, 3, 6, 7, 3, 2, 6, 4, 1, 0, 4, 5, 1 };
    for (int i = 0; i < 36; ++i)
    {
      int l = 3 * arrayOfShort[i];
      float f4 = paramFloat * arrayOfFloat[l];
      float f5 = paramFloat * arrayOfFloat[(l + 1)];
      float f6 = paramFloat * arrayOfFloat[(l + 2)];
      putVertex(i, f4, f5, f6);
      short s = (short)i;
      putIndex(i, s);
    }
    float f1 = 0.3333333F + 0.3333333F;
    int j = 0;
    for (int k = 0; k < 4; ++k)
    {
      float f3 = 0.25F * k;
      j = putFaceTexCooords(j, f3, 0.3333333F, f3 + 0.25F, f1);
    }
    float f2 = 0.25F * 2.0F;
    putFaceTexCooords(putFaceTexCooords(j, 0.25F, 0.3333333F, f2, 0.0F), 0.25F, 3.0F * 0.3333333F, 0.25F * 2.0F, 2.0F * 0.3333333F);
    try
    {
      this.texture.loadBitmap(paramBitmap);
      return;
    }
    catch (OpenGLException localOpenGLException)
    {
      localOpenGLException.printStackTrace();
    }
  }

  private int putFaceTexCooords(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    putTexCoord(paramInt, paramFloat1, paramFloat4);
    int i = paramInt + 2;
    putTexCoord(i, paramFloat3, paramFloat2);
    int j = i + 2;
    putTexCoord(j, paramFloat1, paramFloat2);
    int k = j + 2;
    putTexCoord(k, paramFloat1, paramFloat4);
    int l = k + 2;
    putTexCoord(l, paramFloat3, paramFloat4);
    int i1 = l + 2;
    putTexCoord(i1, paramFloat3, paramFloat2);
    return i1 + 2;
  }

  private void putTexCoord(int paramInt, float paramFloat1, float paramFloat2)
  {
    this.mTexCoords.put(paramInt, paramFloat1);
    this.mTexCoords.put(paramInt + 1, paramFloat2);
  }

  public void drawObject(float[] paramArrayOfFloat)
    throws OpenGLException
  {
    this.shader.bind();
    this.shader.setAlpha(0.8F);
    this.mVertices.position(0);
    this.shader.setVertices(this.mVertices);
    this.mTexCoords.position(0);
    this.shader.setTexCoords(this.mTexCoords);
    this.texture.bind(this.shader);
    this.shader.setTransform(paramArrayOfFloat);
    GLES20.glDrawElements(4, this.mNumIndices, 5123, this.mIndices);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.opengl.TexturedCube
 * JD-Core Version:    0.5.4
 */