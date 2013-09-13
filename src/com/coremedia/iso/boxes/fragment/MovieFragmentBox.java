package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.Iterator;
import java.util.List;

public class MovieFragmentBox extends AbstractContainerBox
{
  public MovieFragmentBox()
  {
    super("moof");
  }

  public long getOffset()
  {
    Object localObject = this;
    long l = 0L;
    if (((Box)localObject).getParent() != null)
    {
      label4: Iterator localIterator = ((Box)localObject).getParent().getBoxes().iterator();
      while (true)
      {
        Box localBox;
        if (localIterator.hasNext())
        {
          localBox = (Box)localIterator.next();
          if (localObject != localBox)
            break label69;
        }
        localObject = ((Box)localObject).getParent();
        break label4:
        label69: l += localBox.getSize();
      }
    }
    return l;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.fragment.MovieFragmentBox
 * JD-Core Version:    0.5.4
 */