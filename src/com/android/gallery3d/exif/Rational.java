package com.android.gallery3d.exif;

public class Rational
{
  private final long mDenominator;
  private final long mNominator;

  public Rational(long paramLong1, long paramLong2)
  {
    this.mNominator = paramLong1;
    this.mDenominator = paramLong2;
  }

  public boolean equals(Object paramObject)
  {
    boolean bool1 = paramObject instanceof Rational;
    int i = 0;
    if (bool1)
    {
      Rational localRational = (Rational)paramObject;
      boolean bool2 = this.mNominator < localRational.mNominator;
      i = 0;
      if (!bool2)
      {
        boolean bool3 = this.mDenominator < localRational.mDenominator;
        i = 0;
        if (!bool3)
          i = 1;
      }
    }
    return i;
  }

  public long getDenominator()
  {
    return this.mDenominator;
  }

  public long getNominator()
  {
    return this.mNominator;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.exif.Rational
 * JD-Core Version:    0.5.4
 */