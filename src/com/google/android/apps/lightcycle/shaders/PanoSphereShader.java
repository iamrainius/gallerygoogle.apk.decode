package com.google.android.apps.lightcycle.shaders;

import com.google.android.apps.lightcycle.opengl.OpenGLException;
import com.google.android.apps.lightcycle.opengl.Shader;

public class PanoSphereShader extends Shader
{
  private String mFragmentShader = "precision mediump float;                            \nvarying vec2 vTexCoord;                             \nuniform sampler2D sTexture;                         \nvoid main()                                         \n{                                                   \n  vec4 texcolor;                                    \n  texcolor = texture2D( sTexture, vTexCoord );      \n  texcolor.a = 0.85;                                \n  if (texcolor.r < .0001) texcolor.a = 0.0;         \n  gl_FragColor = texcolor;                          \n}                                                   \n";
  private final String mVertexShader = "uniform mat4 uMvpMatrix;                   \nattribute vec4 aPosition;                   \nattribute vec2 aTextureCoord;               \nvarying vec2 vTexCoord;                     \nvoid main()                                 \n{                                           \n   gl_Position = uMvpMatrix * aPosition;    \n   vTexCoord = aTextureCoord;               \n}                                           \n";

  public PanoSphereShader()
    throws OpenGLException
  {
    this.mProgram = createProgram("uniform mat4 uMvpMatrix;                   \nattribute vec4 aPosition;                   \nattribute vec2 aTextureCoord;               \nvarying vec2 vTexCoord;                     \nvoid main()                                 \n{                                           \n   gl_Position = uMvpMatrix * aPosition;    \n   vTexCoord = aTextureCoord;               \n}                                           \n", this.mFragmentShader);
    this.mVertexIndex = getAttribute(this.mProgram, "aPosition");
    this.mTextureCoordIndex = getAttribute(this.mProgram, "aTextureCoord");
    this.mTransformIndex = getUniform(this.mProgram, "uMvpMatrix");
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.shaders.PanoSphereShader
 * JD-Core Version:    0.5.4
 */