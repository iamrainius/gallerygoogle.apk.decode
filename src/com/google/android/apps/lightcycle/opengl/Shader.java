package com.google.android.apps.lightcycle.opengl;

import android.opengl.GLES20;
import java.nio.FloatBuffer;

public abstract class Shader
{
  protected int mProgram;
  protected int mSamplerIndex = -1;
  protected int mTextureCoordIndex = -1;
  protected int mTransformIndex = -1;
  protected int mVertexIndex = -1;

  public static int createProgram(String paramString1, String paramString2)
    throws OpenGLException
  {
    int i = loadShader(35633, paramString1);
    int j = loadShader(35632, paramString2);
    int k = GLES20.glCreateProgram();
    if (k == 0)
      throw new OpenGLException("Unable to create program");
    GLES20.glAttachShader(k, i);
    OpenGLException.logError("glAttachShader");
    GLES20.glAttachShader(k, j);
    OpenGLException.logError("glAttachShader");
    GLES20.glLinkProgram(k);
    int[] arrayOfInt = new int[1];
    GLES20.glGetProgramiv(k, 35714, arrayOfInt, 0);
    if (arrayOfInt[0] != 1)
    {
      GLES20.glDeleteProgram(k);
      throw new OpenGLException("Could not link program", GLES20.glGetProgramInfoLog(k));
    }
    return k;
  }

  protected static int loadShader(int paramInt, String paramString)
    throws OpenGLException
  {
    int i = GLES20.glCreateShader(paramInt);
    if (i == 0)
      throw new OpenGLException("Unable to create shader");
    GLES20.glShaderSource(i, paramString);
    GLES20.glCompileShader(i);
    int[] arrayOfInt = new int[1];
    GLES20.glGetShaderiv(i, 35713, arrayOfInt, 0);
    if (arrayOfInt[0] == 0)
    {
      String str = GLES20.glGetShaderInfoLog(i);
      GLES20.glDeleteShader(i);
      throw new OpenGLException("Unable to compile shader " + paramInt, str);
    }
    return i;
  }

  public void bind()
  {
    GLES20.glUseProgram(this.mProgram);
  }

  protected int getAttribute(int paramInt, String paramString)
    throws OpenGLException
  {
    int i = GLES20.glGetAttribLocation(paramInt, paramString);
    if (i == -1)
      throw new OpenGLException("Unable to find " + paramString + " in shader");
    OpenGLException.logError("glGetAttribLocation " + paramString);
    return i;
  }

  protected int getUniform(int paramInt, String paramString)
    throws OpenGLException
  {
    int i = GLES20.glGetUniformLocation(paramInt, paramString);
    if (i == -1)
      throw new OpenGLException("Unable to find " + paramString + " in shader");
    OpenGLException.logError("glGetUniformLocation " + paramString);
    return i;
  }

  public void setTexCoords(FloatBuffer paramFloatBuffer)
  {
    if (this.mTextureCoordIndex < 0)
      return;
    GLES20.glVertexAttribPointer(this.mTextureCoordIndex, 2, 5126, false, 0, paramFloatBuffer);
    GLES20.glEnableVertexAttribArray(this.mTextureCoordIndex);
  }

  public void setTransform(float[] paramArrayOfFloat)
  {
    if (this.mTransformIndex < 0)
      return;
    GLES20.glUniformMatrix4fv(this.mTransformIndex, 1, false, paramArrayOfFloat, 0);
  }

  public void setVertices(FloatBuffer paramFloatBuffer)
  {
    if (this.mVertexIndex < 0)
      return;
    GLES20.glVertexAttribPointer(this.mVertexIndex, 3, 5126, false, 12, paramFloatBuffer);
    GLES20.glEnableVertexAttribArray(this.mVertexIndex);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.opengl.Shader
 * JD-Core Version:    0.5.4
 */