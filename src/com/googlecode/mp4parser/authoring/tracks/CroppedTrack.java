package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox.Entry;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class CroppedTrack extends AbstractTrack
{
  private int fromSample;
  Track origTrack;
  private long[] syncSampleArray;
  private int toSample;

  static
  {
    if (!CroppedTrack.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public CroppedTrack(Track paramTrack, long paramLong1, long paramLong2)
  {
    this.origTrack = paramTrack;
    assert (paramLong1 <= 2147483647L);
    assert (paramLong2 <= 2147483647L);
    this.fromSample = (int)paramLong1;
    this.toSample = (int)paramLong2;
  }

  public List<CompositionTimeToSample.Entry> getCompositionTimeEntries()
  {
    if ((this.origTrack.getCompositionTimeEntries() != null) && (!this.origTrack.getCompositionTimeEntries().isEmpty()))
    {
      int[] arrayOfInt1 = CompositionTimeToSample.blowupCompositionTimes(this.origTrack.getCompositionTimeEntries());
      int[] arrayOfInt2 = new int[this.toSample - this.fromSample];
      System.arraycopy(arrayOfInt1, this.fromSample, arrayOfInt2, 0, this.toSample - this.fromSample);
      localLinkedList = new LinkedList();
      int i = arrayOfInt2.length;
      int j = 0;
      label88: if (j >= i)
        break label170;
      int k = arrayOfInt2[j];
      if ((localLinkedList.isEmpty()) || (((CompositionTimeToSample.Entry)localLinkedList.getLast()).getOffset() != k))
        localLinkedList.add(new CompositionTimeToSample.Entry(1, k));
      while (true)
      {
        ++j;
        break label88:
        CompositionTimeToSample.Entry localEntry = (CompositionTimeToSample.Entry)localLinkedList.getLast();
        localEntry.setCount(1 + localEntry.getCount());
      }
    }
    LinkedList localLinkedList = null;
    label170: return localLinkedList;
  }

  public List<TimeToSampleBox.Entry> getDecodingTimeEntries()
  {
    if ((this.origTrack.getDecodingTimeEntries() != null) && (!this.origTrack.getDecodingTimeEntries().isEmpty()))
    {
      long[] arrayOfLong1 = TimeToSampleBox.blowupTimeToSamples(this.origTrack.getDecodingTimeEntries());
      long[] arrayOfLong2 = new long[this.toSample - this.fromSample];
      System.arraycopy(arrayOfLong1, this.fromSample, arrayOfLong2, 0, this.toSample - this.fromSample);
      localLinkedList = new LinkedList();
      int i = arrayOfLong2.length;
      int j = 0;
      label88: if (j >= i)
        break label171;
      long l = arrayOfLong2[j];
      if ((localLinkedList.isEmpty()) || (((TimeToSampleBox.Entry)localLinkedList.getLast()).getDelta() != l))
        localLinkedList.add(new TimeToSampleBox.Entry(1L, l));
      while (true)
      {
        ++j;
        break label88:
        TimeToSampleBox.Entry localEntry = (TimeToSampleBox.Entry)localLinkedList.getLast();
        localEntry.setCount(1L + localEntry.getCount());
      }
    }
    LinkedList localLinkedList = null;
    label171: return localLinkedList;
  }

  public String getHandler()
  {
    return this.origTrack.getHandler();
  }

  public Box getMediaHeaderBox()
  {
    return this.origTrack.getMediaHeaderBox();
  }

  public List<SampleDependencyTypeBox.Entry> getSampleDependencies()
  {
    if ((this.origTrack.getSampleDependencies() != null) && (!this.origTrack.getSampleDependencies().isEmpty()))
      return this.origTrack.getSampleDependencies().subList(this.fromSample, this.toSample);
    return null;
  }

  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.origTrack.getSampleDescriptionBox();
  }

  public List<ByteBuffer> getSamples()
  {
    return this.origTrack.getSamples().subList(this.fromSample, this.toSample);
  }

  public long[] getSyncSamples()
  {
    monitorenter;
    while (true)
    {
      int j;
      long[] arrayOfLong1;
      try
      {
        if (this.syncSampleArray == null)
        {
          if ((this.origTrack.getSyncSamples() != null) && (this.origTrack.getSyncSamples().length > 0))
          {
            LinkedList localLinkedList = new LinkedList();
            long[] arrayOfLong2 = this.origTrack.getSyncSamples();
            int i = arrayOfLong2.length;
            j = 0;
            if (j < i)
            {
              long l = arrayOfLong2[j];
              if ((l < this.fromSample) || (l >= this.toSample))
                break label197;
              localLinkedList.add(Long.valueOf(l - this.fromSample));
              break label197:
            }
            this.syncSampleArray = new long[localLinkedList.size()];
            for (int k = 0; k < this.syncSampleArray.length; ++k)
              this.syncSampleArray[k] = ((Long)localLinkedList.get(k)).longValue();
          }
          for (arrayOfLong1 = this.syncSampleArray; ; arrayOfLong1 = null)
            return arrayOfLong1;
        }
      }
      finally
      {
        monitorexit;
      }
      label197: ++j;
    }
  }

  public TrackMetaData getTrackMetaData()
  {
    return this.origTrack.getTrackMetaData();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.authoring.tracks.CroppedTrack
 * JD-Core Version:    0.5.4
 */