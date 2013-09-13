package com.google.android.apps.lightcycle.opengl;

import android.opengl.GLES20;
import android.util.FloatMath;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Vector;

public class Sphere extends DrawableGL
{
  private boolean mLineDrawing = false;
  private ShortBuffer mLineIndices;
  private int mNumLineIndices;
  private int mNumTriangleIndices;

  public Sphere(int paramInt1, int paramInt2, float paramFloat)
  {
    generateGeometry(paramInt1, paramInt2, paramFloat);
  }

  private void generateGeometry(int paramInt1, int paramInt2, float paramFloat)
  {
    int i = paramInt1 * paramInt2;
    int j = 6 * ((paramInt1 - 1) * (paramInt2 - 1));
    initGeometry(i, j, true);
    this.mLineIndices = ByteBuffer.allocateDirect(2 * (i * 2)).order(ByteOrder.nativeOrder()).asShortBuffer();
    float f1 = 3.141593F / (paramInt1 - 1);
    float f2 = 6.283186F / (paramInt2 - 1);
    int k = 0;
    int l = 0;
    for (int i1 = 0; i1 < paramInt1; ++i1)
    {
      float f3 = f1 * i1 - 1.570796F;
      float f4 = 1.0F - i1 / (paramInt1 - 1);
      for (int i29 = 0; i29 < paramInt2; ++i29)
      {
        float f5 = 1.570796F + f2 * i29;
        float f6 = FloatMath.sin(f3);
        float f7 = FloatMath.cos(f3);
        float f8 = paramFloat * (f7 * FloatMath.cos(f5));
        float f9 = f6 * paramFloat;
        float f10 = paramFloat * (f7 * FloatMath.sin(f5));
        this.mVertices.put(k, f8);
        this.mVertices.put(k + 1, f9);
        this.mVertices.put(k + 2, f10);
        k += 3;
        float f11 = i29 / (paramInt2 - 1);
        this.mTexCoords.put(l, f11);
        this.mTexCoords.put(l + 1, f4);
        l += 2;
      }
    }
    int i2 = 0;
    int i3 = 0;
    while (true)
    {
      int i4 = paramInt1 - 1;
      if (i3 >= i4)
        break;
      int i20 = i3 * paramInt2;
      int i21 = paramInt2 * (i3 + 1);
      int i22 = 0;
      int i23 = i2;
      while (i22 < paramInt2 - 1)
      {
        ShortBuffer localShortBuffer3 = this.mIndices;
        int i24 = i23 + 1;
        localShortBuffer3.put(i23, (short)i20);
        ShortBuffer localShortBuffer4 = this.mIndices;
        int i25 = i24 + 1;
        localShortBuffer4.put(i24, (short)(i21 + 1));
        ShortBuffer localShortBuffer5 = this.mIndices;
        int i26 = i25 + 1;
        localShortBuffer5.put(i25, (short)i21);
        ShortBuffer localShortBuffer6 = this.mIndices;
        int i27 = i26 + 1;
        localShortBuffer6.put(i26, (short)i20);
        ShortBuffer localShortBuffer7 = this.mIndices;
        int i28 = i27 + 1;
        localShortBuffer7.put(i27, (short)(i20 + 1));
        ShortBuffer localShortBuffer8 = this.mIndices;
        i23 = i28 + 1;
        localShortBuffer8.put(i28, (short)(i21 + 1));
        ++i20;
        ++i21;
        ++i22;
      }
      ++i3;
      i2 = i23;
    }
    this.mNumTriangleIndices = j;
    int i5 = 0;
    int i6 = 1;
    int i7 = 0;
    int i8 = 0;
    while (i8 < paramInt1)
    {
      int i16 = 0;
      int i19;
      for (int i17 = i5; i16 < paramInt2; i17 = i19)
      {
        ShortBuffer localShortBuffer2 = this.mLineIndices;
        i19 = i17 + 1;
        localShortBuffer2.put(i17, (short)i7);
        i7 += i6;
        ++i16;
      }
      int i18 = i7 - i6;
      i6 = -i6;
      i7 = i18 + paramInt2;
      ++i8;
      i5 = i17;
    }
    int i9 = -1;
    int i10 = i7 - paramInt2;
    int i11 = 0;
    while (i11 < paramInt2)
    {
      int i12 = 0;
      int i15;
      for (int i13 = i5; i12 < paramInt1; i13 = i15)
      {
        ShortBuffer localShortBuffer1 = this.mLineIndices;
        i15 = i13 + 1;
        localShortBuffer1.put(i13, (short)i10);
        i10 += i9 * paramInt2;
        ++i12;
      }
      int i14 = i10 - i9 * paramInt2;
      i9 = -i9;
      i10 = i14 + 1;
      ++i11;
      i5 = i13;
    }
    this.mNumLineIndices = (i5 - 1);
  }

  public void createTexture(int paramInt)
  {
    this.mTextures.clear();
    GLTexture localGLTexture = new GLTexture();
    localGLTexture.setIndex(paramInt);
    this.mTextures.add(0, localGLTexture);
  }

  public void drawObject(float[] paramArrayOfFloat)
    throws OpenGLException
  {
    this.mShader.bind();
    this.mVertices.position(0);
    this.mShader.setVertices(this.mVertices);
    if (!this.mLineDrawing)
    {
      this.mTexCoords.position(0);
      this.mShader.setTexCoords(this.mTexCoords);
      if (this.mTextures.size() > 0)
        ((GLTexture)this.mTextures.get(0)).bind(this.mShader);
    }
    this.mShader.setTransform(paramArrayOfFloat);
    this.mIndices.position(0);
    this.mLineIndices.position(0);
    if (this.mLineDrawing)
    {
      GLES20.glDrawElements(3, this.mNumLineIndices, 5123, this.mLineIndices);
      return;
    }
    GLES20.glDrawElements(4, this.mNumTriangleIndices, 5123, this.mIndices);
  }

  public int getTextureId()
  {
    return ((GLTexture)this.mTextures.get(0)).getIndex();
  }

  public void setLineDrawing(boolean paramBoolean)
  {
    this.mLineDrawing = paramBoolean;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.opengl.Sphere
 * JD-Core Version:    0.5.4
 */