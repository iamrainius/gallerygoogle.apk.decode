package com.android.gallery3d.ui;

import android.graphics.Rect;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class NinePatchChunk
{
  public int[] mColor;
  public int[] mDivX;
  public int[] mDivY;
  public Rect mPaddings = new Rect();

  private static void checkDivCount(int paramInt)
  {
    if ((paramInt != 0) && ((paramInt & 0x1) == 0))
      return;
    throw new RuntimeException("invalid nine-patch: " + paramInt);
  }

  public static NinePatchChunk deserialize(byte[] paramArrayOfByte)
  {
    ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte).order(ByteOrder.nativeOrder());
    if (localByteBuffer.get() == 0)
      return null;
    NinePatchChunk localNinePatchChunk = new NinePatchChunk();
    localNinePatchChunk.mDivX = new int[localByteBuffer.get()];
    localNinePatchChunk.mDivY = new int[localByteBuffer.get()];
    localNinePatchChunk.mColor = new int[localByteBuffer.get()];
    checkDivCount(localNinePatchChunk.mDivX.length);
    checkDivCount(localNinePatchChunk.mDivY.length);
    localByteBuffer.getInt();
    localByteBuffer.getInt();
    localNinePatchChunk.mPaddings.left = localByteBuffer.getInt();
    localNinePatchChunk.mPaddings.right = localByteBuffer.getInt();
    localNinePatchChunk.mPaddings.top = localByteBuffer.getInt();
    localNinePatchChunk.mPaddings.bottom = localByteBuffer.getInt();
    localByteBuffer.getInt();
    readIntArray(localNinePatchChunk.mDivX, localByteBuffer);
    readIntArray(localNinePatchChunk.mDivY, localByteBuffer);
    readIntArray(localNinePatchChunk.mColor, localByteBuffer);
    return localNinePatchChunk;
  }

  private static void readIntArray(int[] paramArrayOfInt, ByteBuffer paramByteBuffer)
  {
    int i = 0;
    int j = paramArrayOfInt.length;
    while (i < j)
    {
      paramArrayOfInt[i] = paramByteBuffer.getInt();
      ++i;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.NinePatchChunk
 * JD-Core Version:    0.5.4
 */