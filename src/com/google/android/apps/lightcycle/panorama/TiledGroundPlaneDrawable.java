package com.google.android.apps.lightcycle.panorama;

import android.opengl.GLES20;
import com.google.android.apps.lightcycle.Constants;
import com.google.android.apps.lightcycle.opengl.DrawableGL;
import com.google.android.apps.lightcycle.opengl.OpenGLException;
import com.google.android.apps.lightcycle.shaders.GroundPlaneShader;
import java.nio.ShortBuffer;

public class TiledGroundPlaneDrawable extends DrawableGL
{
  private final float gapSize = 0.5F;
  private int numIndices = 0;
  private final float planeHeight = -10.0F;
  private GroundPlaneShader shader;
  private final float tileDim = 4.0F;
  private final int tilesPerSide = 15;

  public TiledGroundPlaneDrawable()
  {
    try
    {
      this.shader = new GroundPlaneShader();
      generateGeometry();
      return;
    }
    catch (OpenGLException localOpenGLException)
    {
      localOpenGLException.printStackTrace();
    }
  }

  private void generateGeometry()
  {
    super.getClass();
    super.getClass();
    int i = 15 * 15;
    initGeometry(i * 4, i * 6, false);
    super.getClass();
    float f1 = 4.0F / 2.0F;
    float f2 = 7.0F * -4.5F;
    float f3 = f2;
    int j = 0;
    int k = 0;
    short[] arrayOfShort = { 0, 1, 2, 0, 2, 3 };
    for (int l = 0; ; ++l)
    {
      super.getClass();
      if (l >= 15)
        break;
      float f4 = f2;
      int i1 = 0;
      while (true)
      {
        super.getClass();
        if (i1 >= 15)
          break;
        int i2 = j;
        int i3 = (short)(j + 1);
        float f5 = f3 - f1;
        super.getClass();
        float f6 = f4 - f1;
        putVertex(j, f5, -10.0F, f6);
        int i4 = (short)(i3 + 1);
        float f7 = f3 + f1;
        super.getClass();
        putVertex(i3, f7, -10.0F, f4 - f1);
        int i5 = (short)(i4 + 1);
        float f8 = f3 + f1;
        super.getClass();
        putVertex(i4, f8, -10.0F, f4 + f1);
        j = (short)(i5 + 1);
        float f9 = f3 - f1;
        super.getClass();
        putVertex(i5, f9, -10.0F, f4 + f1);
        int i6 = 0;
        int i8;
        for (int i7 = k; i6 < 6; i7 = i8)
        {
          i8 = (short)(i7 + 1);
          putIndex(i7, (short)(i2 + arrayOfShort[i6]));
          ++i6;
        }
        f4 += 4.5F;
        ++i1;
        k = i7;
      }
      f3 += 4.5F;
    }
    this.numIndices = k;
  }

  public void drawObject(float[] paramArrayOfFloat)
    throws OpenGLException
  {
    this.shader.bind();
    this.shader.setColor(Constants.GROUND_PLANE_COLOR);
    this.shader.setVertices(this.mVertices);
    this.shader.setTransform(paramArrayOfFloat);
    this.mIndices.position(0);
    GLES20.glDrawElements(4, this.numIndices, 5123, this.mIndices);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.TiledGroundPlaneDrawable
 * JD-Core Version:    0.5.4
 */