package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.boxes.Box;
import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.Iterator;
import java.util.List;

public class TrackFragmentBox extends AbstractContainerBox
{
  public TrackFragmentBox()
  {
    super("traf");
  }

  public TrackFragmentHeaderBox getTrackFragmentHeaderBox()
  {
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof TrackFragmentHeaderBox)
        return (TrackFragmentHeaderBox)localBox;
    }
    return null;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.fragment.TrackFragmentBox
 * JD-Core Version:    0.5.4
 */