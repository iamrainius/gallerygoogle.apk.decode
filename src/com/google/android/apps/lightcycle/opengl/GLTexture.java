package com.google.android.apps.lightcycle.opengl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class GLTexture
{
  private int mTextureIndex = -1;

  public GLTexture()
  {
  }

  public GLTexture(TextureType paramTextureType)
  {
    switch (1.$SwitchMap$com$google$android$apps$lightcycle$opengl$GLTexture$TextureType[paramTextureType.ordinal()])
    {
    default:
      this.mTextureIndex = createStandardTexture();
      return;
    case 1:
      this.mTextureIndex = createStandardTexture();
      return;
    case 2:
    }
    this.mTextureIndex = createNNTexture();
  }

  public static int createNNTexture()
  {
    int[] arrayOfInt = new int[1];
    GLES20.glGenTextures(1, arrayOfInt, 0);
    GLES20.glBindTexture(3553, arrayOfInt[0]);
    GLES20.glTexParameterf(3553, 10241, 9728.0F);
    GLES20.glTexParameterf(3553, 10240, 9728.0F);
    GLES20.glTexParameteri(3553, 10242, 33071);
    GLES20.glTexParameteri(3553, 10243, 33071);
    return arrayOfInt[0];
  }

  public static int createStandardTexture()
  {
    int[] arrayOfInt = new int[1];
    GLES20.glGenTextures(1, arrayOfInt, 0);
    GLES20.glBindTexture(3553, arrayOfInt[0]);
    GLES20.glTexParameterf(3553, 10241, 9728.0F);
    GLES20.glTexParameterf(3553, 10240, 9729.0F);
    GLES20.glTexParameteri(3553, 10242, 33071);
    GLES20.glTexParameteri(3553, 10243, 33071);
    return arrayOfInt[0];
  }

  public void bind(Shader paramShader)
    throws OpenGLException
  {
    if (this.mTextureIndex < 0)
      throw new OpenGLException("Trying to bind without a loaded texture");
    GLES20.glBindTexture(3553, this.mTextureIndex);
    OpenGLException.logError("glBindTexture");
  }

  public int getIndex()
  {
    return this.mTextureIndex;
  }

  public void loadBitmap(Bitmap paramBitmap)
    throws OpenGLException
  {
    int[] arrayOfInt = new int[1];
    GLES20.glGenTextures(1, arrayOfInt, 0);
    this.mTextureIndex = arrayOfInt[0];
    GLES20.glBindTexture(3553, this.mTextureIndex);
    GLES20.glTexParameterf(3553, 10241, 9728.0F);
    GLES20.glTexParameterf(3553, 10240, 9729.0F);
    GLES20.glTexParameteri(3553, 10242, 33071);
    GLES20.glTexParameteri(3553, 10243, 33071);
    GLUtils.texImage2D(3553, 0, paramBitmap, 0);
    OpenGLException.logError("Texture : loadBitmap");
    paramBitmap.recycle();
  }

  public void recycle()
  {
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = this.mTextureIndex;
    GLES20.glDeleteTextures(1, arrayOfInt, 0);
    this.mTextureIndex = -1;
  }

  public void setIndex(int paramInt)
  {
    this.mTextureIndex = paramInt;
  }

  public static enum TextureType
  {
    static
    {
      NearestNeighbor = new TextureType("NearestNeighbor", 1);
      TextureType[] arrayOfTextureType = new TextureType[2];
      arrayOfTextureType[0] = Standard;
      arrayOfTextureType[1] = NearestNeighbor;
      $VALUES = arrayOfTextureType;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.opengl.GLTexture
 * JD-Core Version:    0.5.4
 */