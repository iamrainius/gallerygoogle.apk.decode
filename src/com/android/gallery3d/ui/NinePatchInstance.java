package com.android.gallery3d.ui;

import com.android.gallery3d.common.Utils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL11;

class NinePatchInstance
{
  private int[] mBufferNames;
  private int mIdxCount;
  private ByteBuffer mIndexBuffer;
  private FloatBuffer mUvBuffer;
  private FloatBuffer mXyBuffer;

  public NinePatchInstance(NinePatchTexture paramNinePatchTexture, int paramInt1, int paramInt2)
  {
    NinePatchChunk localNinePatchChunk = paramNinePatchTexture.getNinePatchChunk();
    if ((paramInt1 <= 0) || (paramInt2 <= 0))
      throw new RuntimeException("invalid dimension");
    if ((localNinePatchChunk.mDivX.length != 2) || (localNinePatchChunk.mDivY.length != 2))
      throw new RuntimeException("unsupported nine patch");
    float[] arrayOfFloat1 = new float[4];
    float[] arrayOfFloat2 = new float[4];
    float[] arrayOfFloat3 = new float[4];
    float[] arrayOfFloat4 = new float[4];
    prepareVertexData(arrayOfFloat1, arrayOfFloat2, arrayOfFloat3, arrayOfFloat4, stretch(arrayOfFloat1, arrayOfFloat3, localNinePatchChunk.mDivX, paramNinePatchTexture.getWidth(), paramInt1), stretch(arrayOfFloat2, arrayOfFloat4, localNinePatchChunk.mDivY, paramNinePatchTexture.getHeight(), paramInt2), localNinePatchChunk.mColor);
  }

  private static ByteBuffer allocateDirectNativeOrderBuffer(int paramInt)
  {
    return ByteBuffer.allocateDirect(paramInt).order(ByteOrder.nativeOrder());
  }

  private void prepareBuffers(GLCanvas paramGLCanvas)
  {
    this.mBufferNames = new int[3];
    GL11 localGL11 = paramGLCanvas.getGLInstance();
    GLId.glGenBuffers(3, this.mBufferNames, 0);
    localGL11.glBindBuffer(34962, this.mBufferNames[0]);
    localGL11.glBufferData(34962, 4 * this.mXyBuffer.capacity(), this.mXyBuffer, 35044);
    localGL11.glBindBuffer(34962, this.mBufferNames[1]);
    localGL11.glBufferData(34962, 4 * this.mUvBuffer.capacity(), this.mUvBuffer, 35044);
    localGL11.glBindBuffer(34963, this.mBufferNames[2]);
    localGL11.glBufferData(34963, this.mIndexBuffer.capacity(), this.mIndexBuffer, 35044);
    this.mXyBuffer = null;
    this.mUvBuffer = null;
    this.mIndexBuffer = null;
  }

  private void prepareVertexData(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4, int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    int i = 0;
    float[] arrayOfFloat1 = new float[32];
    float[] arrayOfFloat2 = new float[32];
    int j = 0;
    while (j < paramInt2)
    {
      int i13 = 0;
      int i15;
      for (int i14 = i; i13 < paramInt1; i14 = i15)
      {
        i15 = i14 + 1;
        int i16 = i14 << 1;
        int i17 = i16 + 1;
        arrayOfFloat1[i16] = paramArrayOfFloat1[i13];
        arrayOfFloat1[i17] = paramArrayOfFloat2[j];
        arrayOfFloat2[i16] = paramArrayOfFloat3[i13];
        arrayOfFloat2[i17] = paramArrayOfFloat4[j];
        ++i13;
      }
      ++j;
      i = i14;
    }
    int k = 1;
    int l = 0;
    byte[] arrayOfByte = new byte[24];
    for (int i1 = 0; ; ++i1)
    {
      int i2 = paramInt2 - 1;
      if (i1 >= i2)
        break;
      --k;
      label153: int i4;
      int i5;
      if (l == 0)
      {
        l = 1;
        if (l == 0)
          break label307;
        i4 = 0;
        i5 = paramInt1;
      }
      for (int i6 = 1; ; i6 = -1)
      {
        int i7 = i4;
        while (true)
        {
          if (i7 == i5)
            break label322;
          int i8 = i7 + i1 * paramInt1;
          if (i7 != i4)
          {
            int i10 = i7 + i1 * (paramInt1 - 1);
            if ((l == 0) || (paramArrayOfInt[(--i10)] == 0))
            {
              arrayOfByte[k] = arrayOfByte[(k - 1)];
              int i11 = k + 1;
              int i12 = i11 + 1;
              arrayOfByte[i11] = (byte)i8;
              k = i12;
            }
          }
          int i9 = k + 1;
          arrayOfByte[k] = (byte)i8;
          k = i9 + 1;
          arrayOfByte[i9] = (byte)(i8 + paramInt1);
          i7 += i6;
        }
        l = 0;
        break label153:
        label307: i4 = paramInt1 - 1;
        label322: i5 = -1;
      }
    }
    this.mIdxCount = k;
    int i3 = 4 * (i * 2);
    this.mXyBuffer = allocateDirectNativeOrderBuffer(i3).asFloatBuffer();
    this.mUvBuffer = allocateDirectNativeOrderBuffer(i3).asFloatBuffer();
    this.mIndexBuffer = allocateDirectNativeOrderBuffer(this.mIdxCount);
    this.mXyBuffer.put(arrayOfFloat1, 0, i * 2).position(0);
    this.mUvBuffer.put(arrayOfFloat2, 0, i * 2).position(0);
    this.mIndexBuffer.put(arrayOfByte, 0, k).position(0);
  }

  private static int stretch(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = Utils.nextPowerOf2(paramInt1);
    float f1 = paramInt1 / i;
    float f2 = 0.0F;
    int j = 0;
    int k = paramArrayOfInt.length;
    while (j < k)
    {
      f2 += paramArrayOfInt[(j + 1)] - paramArrayOfInt[j];
      j += 2;
    }
    float f3 = f2 + (paramInt2 - paramInt1);
    float f4 = 0.0F;
    float f5 = 0.0F;
    paramArrayOfFloat1[0] = 0.0F;
    paramArrayOfFloat2[0] = 0.0F;
    int l = 0;
    int i1 = paramArrayOfInt.length;
    while (l < i1)
    {
      paramArrayOfFloat1[(l + 1)] = (0.5F + (f4 + (paramArrayOfInt[l] - f5)));
      paramArrayOfFloat2[(l + 1)] = Math.min((0.5F + paramArrayOfInt[l]) / i, f1);
      float f6 = paramArrayOfInt[(l + 1)] - paramArrayOfInt[l];
      float f7 = f3 * f6 / f2;
      f3 -= f7;
      f2 -= f6;
      f4 = f7 + paramArrayOfFloat1[(l + 1)];
      f5 = paramArrayOfInt[(l + 1)];
      paramArrayOfFloat1[(l + 2)] = (f4 - 0.5F);
      paramArrayOfFloat2[(l + 2)] = Math.min((f5 - 0.5F) / i, f1);
      l += 2;
    }
    paramArrayOfFloat1[(1 + paramArrayOfInt.length)] = paramInt2;
    paramArrayOfFloat2[(1 + paramArrayOfInt.length)] = f1;
    int i2 = 0;
    int i3 = 1;
    int i4 = 2 + paramArrayOfInt.length;
    if (i3 < i4)
    {
      label259: if (paramArrayOfFloat1[i3] - paramArrayOfFloat1[i2] < 1.0F);
      while (true)
      {
        ++i3;
        break label259:
        paramArrayOfFloat1[(++i2)] = paramArrayOfFloat1[i3];
        paramArrayOfFloat2[i2] = paramArrayOfFloat2[i3];
      }
    }
    return i2 + 1;
  }

  public void draw(GLCanvas paramGLCanvas, NinePatchTexture paramNinePatchTexture, int paramInt1, int paramInt2)
  {
    if (this.mBufferNames == null)
      prepareBuffers(paramGLCanvas);
    paramGLCanvas.drawMesh(paramNinePatchTexture, paramInt1, paramInt2, this.mBufferNames[0], this.mBufferNames[1], this.mBufferNames[2], this.mIdxCount);
  }

  public void recycle(GLCanvas paramGLCanvas)
  {
    if (this.mBufferNames == null)
      return;
    paramGLCanvas.deleteBuffer(this.mBufferNames[0]);
    paramGLCanvas.deleteBuffer(this.mBufferNames[1]);
    paramGLCanvas.deleteBuffer(this.mBufferNames[2]);
    this.mBufferNames = null;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.NinePatchInstance
 * JD-Core Version:    0.5.4
 */