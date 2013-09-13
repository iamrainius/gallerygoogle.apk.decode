package com.android.gallery3d.data;

import android.graphics.Rect;
import com.android.gallery3d.common.Utils;
import java.util.StringTokenizer;

public class Face
  implements Comparable<Face>
{
  private String mName;
  private String mPersonId;
  private Rect mPosition;

  public Face(String paramString1, String paramString2, String paramString3)
  {
    this.mName = paramString1;
    this.mPersonId = paramString2;
    if ((this.mName != null) && (this.mPersonId != null) && (paramString3 != null));
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString3);
      this.mPosition = new Rect();
      while (true)
      {
        if (!localStringTokenizer.hasMoreElements())
          return;
        this.mPosition.left = Integer.parseInt(localStringTokenizer.nextToken());
        this.mPosition.top = Integer.parseInt(localStringTokenizer.nextToken());
        this.mPosition.right = Integer.parseInt(localStringTokenizer.nextToken());
        this.mPosition.bottom = Integer.parseInt(localStringTokenizer.nextToken());
      }
    }
  }

  public int compareTo(Face paramFace)
  {
    return this.mName.compareTo(paramFace.mName);
  }

  public boolean equals(Object paramObject)
  {
    if (paramObject instanceof Face)
    {
      Face localFace = (Face)paramObject;
      return this.mPersonId.equals(localFace.mPersonId);
    }
    return false;
  }

  public String getName()
  {
    return this.mName;
  }

  public Rect getPosition()
  {
    return this.mPosition;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.Face
 * JD-Core Version:    0.5.4
 */