package com.google.android.apps.lightcycle.opengl;

import android.opengl.Matrix;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public abstract class DrawableGL
{
  protected HashSet<DrawableGL> mChildren = null;
  protected float[] mGlobalMatrix = new float[16];
  protected ShortBuffer mIndices = null;
  protected float[] mLocalMatrix = new float[16];
  protected final DrawableGL mParent;
  protected Shader mShader = null;
  protected FloatBuffer mTexCoords = null;
  protected Vector<GLTexture> mTextures = new Vector();
  protected FloatBuffer mVertices = null;

  public DrawableGL()
  {
    Matrix.setIdentityM(this.mLocalMatrix, 0);
    this.mParent = null;
  }

  public void draw(float[] paramArrayOfFloat)
    throws OpenGLException
  {
    Matrix.multiplyMM(this.mGlobalMatrix, 0, paramArrayOfFloat, 0, this.mLocalMatrix, 0);
    if (this.mChildren != null)
    {
      Iterator localIterator = this.mChildren.iterator();
      while (localIterator.hasNext())
        ((DrawableGL)localIterator.next()).draw(this.mGlobalMatrix);
    }
    drawObject(this.mGlobalMatrix);
  }

  public abstract void drawObject(float[] paramArrayOfFloat)
    throws OpenGLException;

  public Shader getShader()
  {
    return this.mShader;
  }

  protected void initGeometry(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.mVertices = ByteBuffer.allocateDirect(4 * (paramInt1 * 3)).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.mIndices = ByteBuffer.allocateDirect(paramInt2 * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
    if (!paramBoolean)
      return;
    this.mTexCoords = ByteBuffer.allocateDirect(4 * (paramInt1 * 2)).order(ByteOrder.nativeOrder()).asFloatBuffer();
  }

  protected void putIndex(int paramInt, short paramShort)
  {
    this.mIndices.put(paramInt, paramShort);
  }

  protected void putVertex(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    int i = paramInt * 3;
    FloatBuffer localFloatBuffer1 = this.mVertices;
    int j = i + 1;
    localFloatBuffer1.put(i, paramFloat1);
    FloatBuffer localFloatBuffer2 = this.mVertices;
    int k = j + 1;
    localFloatBuffer2.put(j, paramFloat2);
    FloatBuffer localFloatBuffer3 = this.mVertices;
    (k + 1);
    localFloatBuffer3.put(k, paramFloat3);
  }

  public void setShader(Shader paramShader)
  {
    this.mShader = paramShader;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.opengl.DrawableGL
 * JD-Core Version:    0.5.4
 */