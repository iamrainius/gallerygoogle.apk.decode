package com.google.android.apps.lightcycle.shaders;

import android.opengl.GLES20;
import com.google.android.apps.lightcycle.opengl.OpenGLException;
import com.google.android.apps.lightcycle.opengl.Shader;

public class TransparencyShader extends Shader
{
  private int mAlphaFactorIndex = 0;
  private String mFragmentShader = "precision highp float;                            \nuniform float uAlphaFactor;                         \nvarying vec2 vTexCoord;                             \nuniform sampler2D sTexture;                         \nvoid main()                                         \n{                                                   \n  vec4 texcolor;                                    \n  texcolor = texture2D( sTexture, vTexCoord );      \n  texcolor.a = uAlphaFactor;                        \n  gl_FragColor = texcolor;                          \n}                                                   \n";
  private final String mVertexShader = "uniform mat4 uMvpMatrix;                   \nattribute vec4 aPosition;                   \nattribute vec2 aTextureCoord;               \nvarying vec2 vTexCoord;                     \nvoid main()                                 \n{                                           \n   gl_Position = uMvpMatrix * aPosition;    \n   vTexCoord = aTextureCoord;               \n}                                           \n";

  public TransparencyShader()
    throws OpenGLException
  {
    this.mProgram = createProgram("uniform mat4 uMvpMatrix;                   \nattribute vec4 aPosition;                   \nattribute vec2 aTextureCoord;               \nvarying vec2 vTexCoord;                     \nvoid main()                                 \n{                                           \n   gl_Position = uMvpMatrix * aPosition;    \n   vTexCoord = aTextureCoord;               \n}                                           \n", this.mFragmentShader);
    this.mVertexIndex = getAttribute(this.mProgram, "aPosition");
    this.mTextureCoordIndex = getAttribute(this.mProgram, "aTextureCoord");
    this.mTransformIndex = getUniform(this.mProgram, "uMvpMatrix");
    this.mAlphaFactorIndex = getUniform(this.mProgram, "uAlphaFactor");
    bind();
    GLES20.glUniform1f(this.mAlphaFactorIndex, 0.9F);
  }

  public void setAlpha(float paramFloat)
  {
    GLES20.glUniform1f(this.mAlphaFactorIndex, paramFloat);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.shaders.TransparencyShader
 * JD-Core Version:    0.5.4
 */