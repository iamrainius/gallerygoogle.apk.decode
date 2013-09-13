package com.google.android.apps.lightcycle.shaders;

import android.opengl.GLES20;
import com.google.android.apps.lightcycle.opengl.OpenGLException;
import com.google.android.apps.lightcycle.opengl.Shader;

public class GroundPlaneShader extends Shader
{
  private int mColorIndex;
  private String mFragmentShader = "precision mediump float;                       \nuniform vec4 uDrawColor;                       \nvoid main()                                    \n{                                              \n  gl_FragColor = uDrawColor;                   \n}                                              \n";
  private final String mVertexShader = "uniform mat4 uMvpMatrix;                   \nattribute vec4 aPosition;                   \nvoid main()                                 \n{                                           \n   gl_Position = uMvpMatrix * aPosition;    \n}                                           \n";

  public GroundPlaneShader()
    throws OpenGLException
  {
    this.mProgram = createProgram("uniform mat4 uMvpMatrix;                   \nattribute vec4 aPosition;                   \nvoid main()                                 \n{                                           \n   gl_Position = uMvpMatrix * aPosition;    \n}                                           \n", this.mFragmentShader);
    this.mVertexIndex = getAttribute(this.mProgram, "aPosition");
    this.mTransformIndex = getUniform(this.mProgram, "uMvpMatrix");
    this.mColorIndex = getUniform(this.mProgram, "uDrawColor");
  }

  public void setColor(float[] paramArrayOfFloat)
  {
    bind();
    GLES20.glUniform4f(this.mColorIndex, paramArrayOfFloat[0], paramArrayOfFloat[1], paramArrayOfFloat[2], paramArrayOfFloat[3]);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.shaders.GroundPlaneShader
 * JD-Core Version:    0.5.4
 */