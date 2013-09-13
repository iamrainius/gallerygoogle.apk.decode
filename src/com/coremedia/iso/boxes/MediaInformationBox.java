package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.Iterator;
import java.util.List;

public class MediaInformationBox extends AbstractContainerBox
{
  public MediaInformationBox()
  {
    super("minf");
  }

  public AbstractMediaHeaderBox getMediaHeaderBox()
  {
    Iterator localIterator = this.boxes.iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof AbstractMediaHeaderBox)
        return (AbstractMediaHeaderBox)localBox;
    }
    return null;
  }

  public SampleTableBox getSampleTableBox()
  {
    Iterator localIterator = this.boxes.iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof SampleTableBox)
        return (SampleTableBox)localBox;
    }
    return null;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.MediaInformationBox
 * JD-Core Version:    0.5.4
 */