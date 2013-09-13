package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.Iterator;
import java.util.List;

public class MediaBox extends AbstractContainerBox
{
  public MediaBox()
  {
    super("mdia");
  }

  public HandlerBox getHandlerBox()
  {
    Iterator localIterator = this.boxes.iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof HandlerBox)
        return (HandlerBox)localBox;
    }
    return null;
  }

  public MediaHeaderBox getMediaHeaderBox()
  {
    Iterator localIterator = this.boxes.iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof MediaHeaderBox)
        return (MediaHeaderBox)localBox;
    }
    return null;
  }

  public MediaInformationBox getMediaInformationBox()
  {
    Iterator localIterator = this.boxes.iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof MediaInformationBox)
        return (MediaInformationBox)localBox;
    }
    return null;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.MediaBox
 * JD-Core Version:    0.5.4
 */