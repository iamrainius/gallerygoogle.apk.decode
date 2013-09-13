package com.android.gallery3d.filtershow.ui;

public class ControlPoint
  implements Comparable
{
  public float x;
  public float y;

  public ControlPoint(float paramFloat1, float paramFloat2)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
  }

  public ControlPoint(ControlPoint paramControlPoint)
  {
    this.x = paramControlPoint.x;
    this.y = paramControlPoint.y;
  }

  public int compareTo(Object paramObject)
  {
    ControlPoint localControlPoint = (ControlPoint)paramObject;
    if (localControlPoint.x < this.x)
      return 1;
    if (localControlPoint.x > this.x)
      return -1;
    return 0;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.ui.ControlPoint
 * JD-Core Version:    0.5.4
 */