package com.adobe.xmp.impl.xpath;

import java.util.ArrayList;
import java.util.List;

public class XMPPath
{
  private List segments = new ArrayList(5);

  public void add(XMPPathSegment paramXMPPathSegment)
  {
    this.segments.add(paramXMPPathSegment);
  }

  public XMPPathSegment getSegment(int paramInt)
  {
    return (XMPPathSegment)this.segments.get(paramInt);
  }

  public int size()
  {
    return this.segments.size();
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 1; i < size(); ++i)
    {
      localStringBuffer.append(getSegment(i));
      if (i >= -1 + size())
        continue;
      int j = getSegment(i + 1).getKind();
      if ((j != 1) && (j != 2))
        continue;
      localStringBuffer.append('/');
    }
    return localStringBuffer.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.xpath.XMPPath
 * JD-Core Version:    0.5.4
 */