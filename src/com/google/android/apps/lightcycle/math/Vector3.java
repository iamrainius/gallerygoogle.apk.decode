package com.google.android.apps.lightcycle.math;

public final class Vector3
{
  public static final Vector3 ZERO = new Vector3(0.0F, 0.0F, 0.0F);
  public float x;
  public float y;
  public float z;

  public Vector3()
  {
  }

  public Vector3(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    set(paramFloat1, paramFloat2, paramFloat3);
  }

  public final float dot(Vector3 paramVector3)
  {
    return this.x * paramVector3.x + this.y * paramVector3.y + this.z * paramVector3.z;
  }

  public final float length()
  {
    return (float)Math.sqrt(length2());
  }

  public final float length2()
  {
    return this.x * this.x + this.y * this.y + this.z * this.z;
  }

  public final float normalize()
  {
    float f = length();
    if (f != 0.0F)
    {
      this.x /= f;
      this.y /= f;
      this.z /= f;
    }
    return f;
  }

  public final void set(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.z = paramFloat3;
  }

  public float[] toFloatArray()
  {
    float[] arrayOfFloat = new float[3];
    arrayOfFloat[0] = this.x;
    arrayOfFloat[1] = this.y;
    arrayOfFloat[2] = this.z;
    return arrayOfFloat;
  }

  public String toString()
  {
    return "" + this.x + ", " + this.y + ", " + this.z;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.math.Vector3
 * JD-Core Version:    0.5.4
 */