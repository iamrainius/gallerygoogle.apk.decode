package com.google.android.apps.lightcycle.opengl;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class CurvedTile
{
  private ShortBuffer indices;
  private final int numIndices;
  private final int numVertices;
  private FloatBuffer texCoords;
  public final int textureId;
  private int vertIndex = 0;
  private FloatBuffer vertices;

  public CurvedTile(int paramInt1, int paramInt2)
  {
    this.textureId = paramInt1;
    int i = paramInt2 + 1;
    this.numVertices = (i * i);
    this.numIndices = (2 * (3 * ((i - 1) * (i - 1))));
    initBuffers();
    int j = 0;
    int k = 0;
    while (k < i - 1)
    {
      int i5 = k * i;
      int i6 = 0;
      int i7 = j;
      while (i6 < i - 1)
      {
        int i8 = i5 + i6;
        ShortBuffer localShortBuffer1 = this.indices;
        int i9 = i7 + 1;
        localShortBuffer1.put(i7, (short)(i8 + 0));
        ShortBuffer localShortBuffer2 = this.indices;
        int i10 = i9 + 1;
        localShortBuffer2.put(i9, (short)(i8 + 1));
        ShortBuffer localShortBuffer3 = this.indices;
        int i11 = i10 + 1;
        localShortBuffer3.put(i10, (short)(i8 + i));
        ShortBuffer localShortBuffer4 = this.indices;
        int i12 = i11 + 1;
        localShortBuffer4.put(i11, (short)(i8 + i));
        ShortBuffer localShortBuffer5 = this.indices;
        int i13 = i12 + 1;
        localShortBuffer5.put(i12, (short)(i8 + 1));
        ShortBuffer localShortBuffer6 = this.indices;
        i7 = i13 + 1;
        localShortBuffer6.put(i13, (short)(1 + (i8 + i)));
        ++i6;
      }
      ++k;
      j = i7;
    }
    int l = 0;
    int i1 = 0;
    while (i1 < i)
    {
      int i2 = 0;
      int i3 = l;
      while (i2 < i)
      {
        float f1 = i2 / (i - 1);
        float f2 = 1.0F - i1 / (i - 1);
        FloatBuffer localFloatBuffer1 = this.texCoords;
        int i4 = i3 + 1;
        localFloatBuffer1.put(i3, f1);
        FloatBuffer localFloatBuffer2 = this.texCoords;
        i3 = i4 + 1;
        localFloatBuffer2.put(i4, f2);
        ++i2;
      }
      ++i1;
      l = i3;
    }
  }

  private void initBuffers()
  {
    this.vertices = ByteBuffer.allocateDirect(4 * (3 * this.numVertices)).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.texCoords = ByteBuffer.allocateDirect(4 * (2 * this.numVertices)).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.indices = ByteBuffer.allocateDirect(2 * this.numIndices).order(ByteOrder.nativeOrder()).asShortBuffer();
  }

  public void draw(Shader paramShader)
  {
    this.vertices.position(0);
    paramShader.setVertices(this.vertices);
    this.texCoords.position(0);
    paramShader.setTexCoords(this.texCoords);
    this.indices.position(0);
    GLES20.glDrawElements(4, this.numIndices, 5123, this.indices);
  }

  public void putVertex(Vertex paramVertex)
  {
    paramVertex.addToBuffer(this.vertices, this.vertIndex);
    this.vertIndex = (3 + this.vertIndex);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.opengl.CurvedTile
 * JD-Core Version:    0.5.4
 */