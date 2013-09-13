package com.google.android.apps.lightcycle.panorama;

import android.opengl.GLES20;
import com.google.android.apps.lightcycle.opengl.DrawableGL;
import com.google.android.apps.lightcycle.opengl.GLTexture;
import com.google.android.apps.lightcycle.opengl.OpenGLException;
import com.google.android.apps.lightcycle.opengl.Shader;
import com.google.android.apps.lightcycle.util.LG;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Vector;

public class PanoramaFrameOverlay extends DrawableGL
{
  private boolean mDrawOutlineOnly = false;
  private boolean mInitialized = false;
  private int mNumIndices = 0;
  private int mNumOutlineIndices = 0;
  private ShortBuffer mOutlineIndices;
  private Shader mOutlineShader = null;

  public void createTexture(int paramInt)
  {
    this.mTextures.clear();
    GLTexture localGLTexture = new GLTexture();
    this.mTextures.add(0, localGLTexture);
    ((GLTexture)this.mTextures.get(0)).setIndex(paramInt);
  }

  public void drawObject(float[] paramArrayOfFloat)
    throws OpenGLException
  {
    if (!this.mInitialized);
    do
    {
      return;
      if (this.mDrawOutlineOnly)
        continue;
      this.mShader.bind();
      this.mShader.setVertices(this.mVertices);
      this.mShader.setTexCoords(this.mTexCoords);
      this.mShader.setTransform(paramArrayOfFloat);
      if (this.mTextures.size() > 0)
        ((GLTexture)this.mTextures.get(0)).bind(this.mShader);
      this.mIndices.position(0);
      GLES20.glDrawElements(4, this.mNumIndices, 5123, this.mIndices);
    }
    while (this.mOutlineShader == null);
    this.mOutlineShader.bind();
    this.mOutlineShader.setVertices(this.mVertices);
    this.mOutlineShader.setTransform(paramArrayOfFloat);
    this.mOutlineIndices.position(0);
    GLES20.glLineWidth(3.0F);
    GLES20.glDrawElements(2, this.mNumOutlineIndices, 5123, this.mOutlineIndices);
  }

  public void generateGeometry(float[] paramArrayOfFloat, int paramInt1, int paramInt2, float paramFloat)
  {
    int i = paramInt1 * paramInt2;
    this.mNumIndices = (6 * ((paramInt2 - 1) * (paramInt1 - 1)));
    this.mVertices = ByteBuffer.allocateDirect(4 * (i * 3)).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.mTexCoords = ByteBuffer.allocateDirect(4 * (i * 2)).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.mIndices = ByteBuffer.allocateDirect(2 * this.mNumIndices).order(ByteOrder.nativeOrder()).asShortBuffer();
    this.mOutlineIndices = ByteBuffer.allocateDirect(2 * (paramInt2 * 2 + paramInt1 * 2)).order(ByteOrder.nativeOrder()).asShortBuffer();
    for (int j = 0; j < i * 3; ++j)
      this.mVertices.put(j, paramFloat * paramArrayOfFloat[j]);
    int k = 0;
    for (int l = 0; l < paramInt2; ++l)
    {
      float f1 = l / (paramInt2 - 1);
      for (int i22 = 0; i22 < paramInt1; ++i22)
      {
        float f2 = i22 / (paramInt1 - 1);
        this.mTexCoords.put(k, f2);
        this.mTexCoords.put(k + 1, f1);
        k += 2;
      }
    }
    int i1 = 0;
    int i2 = 0;
    while (i2 < paramInt2 - 1)
    {
      int i13 = i2 * paramInt1;
      int i14 = paramInt1 * (i2 + 1);
      int i15 = 0;
      int i16 = i1;
      while (i15 < paramInt1 - 1)
      {
        ShortBuffer localShortBuffer5 = this.mIndices;
        int i17 = i16 + 1;
        localShortBuffer5.put(i16, (short)i13);
        ShortBuffer localShortBuffer6 = this.mIndices;
        int i18 = i17 + 1;
        localShortBuffer6.put(i17, (short)(i14 + 1));
        ShortBuffer localShortBuffer7 = this.mIndices;
        int i19 = i18 + 1;
        localShortBuffer7.put(i18, (short)i14);
        ShortBuffer localShortBuffer8 = this.mIndices;
        int i20 = i19 + 1;
        localShortBuffer8.put(i19, (short)i13);
        ShortBuffer localShortBuffer9 = this.mIndices;
        int i21 = i20 + 1;
        localShortBuffer9.put(i20, (short)(i13 + 1));
        ShortBuffer localShortBuffer10 = this.mIndices;
        i16 = i21 + 1;
        localShortBuffer10.put(i21, (short)(i14 + 1));
        ++i13;
        ++i14;
        ++i15;
      }
      ++i2;
      i1 = i16;
    }
    int i3 = 0;
    int i12;
    for (int i4 = 0; i3 < paramInt1; i4 = i12)
    {
      ShortBuffer localShortBuffer4 = this.mOutlineIndices;
      i12 = i4 + 1;
      localShortBuffer4.put(i4, (short)i3);
      ++i3;
    }
    int i5 = 0;
    while (i5 < paramInt2)
    {
      ShortBuffer localShortBuffer3 = this.mOutlineIndices;
      int i11 = i4 + 1;
      localShortBuffer3.put(i4, (short)(paramInt1 - 1 + i5 * paramInt1));
      ++i5;
      i4 = i11;
    }
    int i6 = paramInt1 * (paramInt2 - 1);
    int i7 = paramInt1 - 1;
    while (i7 >= 0)
    {
      ShortBuffer localShortBuffer2 = this.mOutlineIndices;
      int i10 = i4 + 1;
      localShortBuffer2.put(i4, (short)(i6 + i7));
      --i7;
      i4 = i10;
    }
    int i8 = paramInt2 - 1;
    while (i8 >= 0)
    {
      ShortBuffer localShortBuffer1 = this.mOutlineIndices;
      int i9 = i4 + 1;
      localShortBuffer1.put(i4, (short)(i8 * paramInt1));
      --i8;
      i4 = i9;
    }
    this.mNumOutlineIndices = (i4 - 1);
    this.mInitialized = true;
  }

  public boolean getDrawOutlineOnly()
  {
    return this.mDrawOutlineOnly;
  }

  public int getTextureId()
  {
    return ((GLTexture)this.mTextures.get(0)).getIndex();
  }

  public void setDrawOutlineOnly(boolean paramBoolean)
  {
    this.mDrawOutlineOnly = paramBoolean;
  }

  public void setOutlineShader(Shader paramShader)
  {
    this.mOutlineShader = paramShader;
  }

  public void setTextureId(int paramInt)
  {
    if (this.mTextures.size() == 0)
    {
      LG.d("PanoramaFrameOverlay Texture does not exist.");
      return;
    }
    ((GLTexture)this.mTextures.get(0)).setIndex(paramInt);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.PanoramaFrameOverlay
 * JD-Core Version:    0.5.4
 */