package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.Iterator;
import java.util.List;

public class TrackBox extends AbstractContainerBox
{
  public TrackBox()
  {
    super("trak");
  }

  public MediaBox getMediaBox()
  {
    Iterator localIterator = this.boxes.iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof MediaBox)
        return (MediaBox)localBox;
    }
    return null;
  }

  public SampleTableBox getSampleTableBox()
  {
    MediaBox localMediaBox = getMediaBox();
    if (localMediaBox != null)
    {
      MediaInformationBox localMediaInformationBox = localMediaBox.getMediaInformationBox();
      if (localMediaInformationBox != null)
        return localMediaInformationBox.getSampleTableBox();
    }
    return null;
  }

  public TrackHeaderBox getTrackHeaderBox()
  {
    Iterator localIterator = this.boxes.iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof TrackHeaderBox)
        return (TrackHeaderBox)localBox;
    }
    return null;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.TrackBox
 * JD-Core Version:    0.5.4
 */