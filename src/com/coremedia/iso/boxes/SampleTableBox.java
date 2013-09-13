package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.Iterator;
import java.util.List;

public class SampleTableBox extends AbstractContainerBox
{
  public SampleTableBox()
  {
    super("stbl");
  }

  public ChunkOffsetBox getChunkOffsetBox()
  {
    Iterator localIterator = this.boxes.iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof ChunkOffsetBox)
        return (ChunkOffsetBox)localBox;
    }
    return null;
  }

  public CompositionTimeToSample getCompositionTimeToSample()
  {
    Iterator localIterator = this.boxes.iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof CompositionTimeToSample)
        return (CompositionTimeToSample)localBox;
    }
    return null;
  }

  public SampleDependencyTypeBox getSampleDependencyTypeBox()
  {
    Iterator localIterator = this.boxes.iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof SampleDependencyTypeBox)
        return (SampleDependencyTypeBox)localBox;
    }
    return null;
  }

  public SampleDescriptionBox getSampleDescriptionBox()
  {
    Iterator localIterator = this.boxes.iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof SampleDescriptionBox)
        return (SampleDescriptionBox)localBox;
    }
    return null;
  }

  public SampleSizeBox getSampleSizeBox()
  {
    Iterator localIterator = this.boxes.iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof SampleSizeBox)
        return (SampleSizeBox)localBox;
    }
    return null;
  }

  public SampleToChunkBox getSampleToChunkBox()
  {
    Iterator localIterator = this.boxes.iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof SampleToChunkBox)
        return (SampleToChunkBox)localBox;
    }
    return null;
  }

  public SyncSampleBox getSyncSampleBox()
  {
    Iterator localIterator = this.boxes.iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof SyncSampleBox)
        return (SyncSampleBox)localBox;
    }
    return null;
  }

  public TimeToSampleBox getTimeToSampleBox()
  {
    Iterator localIterator = this.boxes.iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof TimeToSampleBox)
        return (TimeToSampleBox)localBox;
    }
    return null;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.SampleTableBox
 * JD-Core Version:    0.5.4
 */