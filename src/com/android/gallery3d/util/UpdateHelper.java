package com.android.gallery3d.util;

import com.android.gallery3d.common.Utils;

public class UpdateHelper
{
  private boolean mUpdated = false;

  public boolean isUpdated()
  {
    return this.mUpdated;
  }

  public double update(double paramDouble1, double paramDouble2)
  {
    if (paramDouble1 != paramDouble2)
    {
      this.mUpdated = true;
      paramDouble1 = paramDouble2;
    }
    return paramDouble1;
  }

  public int update(int paramInt1, int paramInt2)
  {
    if (paramInt1 != paramInt2)
    {
      this.mUpdated = true;
      paramInt1 = paramInt2;
    }
    return paramInt1;
  }

  public long update(long paramLong1, long paramLong2)
  {
    if (paramLong1 != paramLong2)
    {
      this.mUpdated = true;
      paramLong1 = paramLong2;
    }
    return paramLong1;
  }

  public <T> T update(T paramT1, T paramT2)
  {
    if (!Utils.equals(paramT1, paramT2))
    {
      this.mUpdated = true;
      paramT1 = paramT2;
    }
    return paramT1;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.UpdateHelper
 * JD-Core Version:    0.5.4
 */