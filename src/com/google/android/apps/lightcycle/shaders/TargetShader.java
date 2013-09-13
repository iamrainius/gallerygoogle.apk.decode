package com.google.android.apps.lightcycle.shaders;

import android.opengl.GLES20;
import com.google.android.apps.lightcycle.opengl.OpenGLException;
import com.google.android.apps.lightcycle.opengl.Shader;

public class TargetShader extends Shader
{
  private int mAlphaIndex;
  private int mContrastFactorIndex;
  private String mFragmentShader = "precision mediump float;                            \nuniform float uBrightness;                          \nuniform float uAlpha;                               \nvarying vec2 vTexCoord;                             \nuniform sampler2D sTexture;                         \nvoid main()                                         \n{                                                   \n  gl_FragColor = texture2D( sTexture, vTexCoord);   \n  gl_FragColor.rgb *= uBrightness * uAlpha;         \n  gl_FragColor.a = gl_FragColor.a * uAlpha;         \n}                                                   \n";
  private final String mVertexShader = "uniform mat4 uMvpMatrix;                   \nattribute vec4 aPosition;                   \nattribute vec2 aTextureCoord;               \nvarying vec2 vTexCoord;                     \nvoid main()                                 \n{                                           \n   gl_Position = uMvpMatrix * aPosition;    \n   vTexCoord = aTextureCoord;               \n}                                           \n";

  public TargetShader()
    throws OpenGLException
  {
    this.mProgram = createProgram("uniform mat4 uMvpMatrix;                   \nattribute vec4 aPosition;                   \nattribute vec2 aTextureCoord;               \nvarying vec2 vTexCoord;                     \nvoid main()                                 \n{                                           \n   gl_Position = uMvpMatrix * aPosition;    \n   vTexCoord = aTextureCoord;               \n}                                           \n", this.mFragmentShader);
    this.mVertexIndex = getAttribute(this.mProgram, "aPosition");
    this.mTextureCoordIndex = getAttribute(this.mProgram, "aTextureCoord");
    this.mTransformIndex = getUniform(this.mProgram, "uMvpMatrix");
    this.mContrastFactorIndex = getUniform(this.mProgram, "uBrightness");
    this.mAlphaIndex = getUniform(this.mProgram, "uAlpha");
    bind();
    GLES20.glUniform1f(this.mContrastFactorIndex, 0.5F);
    GLES20.glUniform1f(this.mAlphaIndex, 0.5F);
  }

  public void setAlpha(float paramFloat)
  {
    GLES20.glUniform1f(this.mAlphaIndex, paramFloat);
  }

  public void setContrastFactor(float paramFloat)
  {
    GLES20.glUniform1f(this.mContrastFactorIndex, paramFloat);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.shaders.TargetShader
 * JD-Core Version:    0.5.4
 */