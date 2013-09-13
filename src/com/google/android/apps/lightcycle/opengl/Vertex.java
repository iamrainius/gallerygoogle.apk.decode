package com.google.android.apps.lightcycle.opengl;

import java.nio.FloatBuffer;

public class Vertex
{
  public final float x;
  public final float y;
  public final float z;

  public Vertex(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.z = paramFloat3;
  }

  public void addToBuffer(FloatBuffer paramFloatBuffer, int paramInt)
  {
    paramFloatBuffer.put(paramInt, this.x);
    paramFloatBuffer.put(paramInt + 1, this.y);
    paramFloatBuffer.put(paramInt + 2, this.z);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.opengl.Vertex
 * JD-Core Version:    0.5.4
 */