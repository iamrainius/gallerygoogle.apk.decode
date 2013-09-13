package com.googlecode.mp4parser.authoring;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.AbstractMediaHeaderBox;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.ContainerBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SyncSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox.Entry;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.SampleFlags;
import com.coremedia.iso.boxes.fragment.TrackExtendsBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox.Entry;
import com.coremedia.iso.boxes.mdat.SampleList;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Mp4TrackImpl extends AbstractTrack
{
  private List<CompositionTimeToSample.Entry> compositionTimeEntries;
  private List<TimeToSampleBox.Entry> decodingTimeEntries;
  private String handler;
  private AbstractMediaHeaderBox mihd;
  private List<SampleDependencyTypeBox.Entry> sampleDependencies;
  private SampleDescriptionBox sampleDescriptionBox;
  private List<ByteBuffer> samples;
  private long[] syncSamples = new long[0];
  private TrackMetaData trackMetaData = new TrackMetaData();

  public Mp4TrackImpl(TrackBox paramTrackBox)
  {
    long l1 = paramTrackBox.getTrackHeaderBox().getTrackId();
    this.samples = new SampleList(paramTrackBox);
    SampleTableBox localSampleTableBox = paramTrackBox.getMediaBox().getMediaInformationBox().getSampleTableBox();
    this.handler = paramTrackBox.getMediaBox().getHandlerBox().getHandlerType();
    this.mihd = paramTrackBox.getMediaBox().getMediaInformationBox().getMediaHeaderBox();
    this.decodingTimeEntries = new LinkedList();
    this.compositionTimeEntries = new LinkedList();
    this.sampleDependencies = new LinkedList();
    this.decodingTimeEntries.addAll(localSampleTableBox.getTimeToSampleBox().getEntries());
    if (localSampleTableBox.getCompositionTimeToSample() != null)
      this.compositionTimeEntries.addAll(localSampleTableBox.getCompositionTimeToSample().getEntries());
    if (localSampleTableBox.getSampleDependencyTypeBox() != null)
      this.sampleDependencies.addAll(localSampleTableBox.getSampleDependencyTypeBox().getEntries());
    if (localSampleTableBox.getSyncSampleBox() != null)
      this.syncSamples = localSampleTableBox.getSyncSampleBox().getSampleNumber();
    this.sampleDescriptionBox = localSampleTableBox.getSampleDescriptionBox();
    List localList = paramTrackBox.getParent().getBoxes(MovieExtendsBox.class);
    TrackExtendsBox localTrackExtendsBox;
    LinkedList localLinkedList;
    long l2;
    Iterator localIterator5;
    if (localList.size() > 0)
    {
      Iterator localIterator1 = localList.iterator();
      if (localIterator1.hasNext())
      {
        Iterator localIterator2 = ((MovieExtendsBox)localIterator1.next()).getBoxes(TrackExtendsBox.class).iterator();
        do
        {
          if (localIterator2.hasNext());
          localTrackExtendsBox = (TrackExtendsBox)localIterator2.next();
        }
        while (localTrackExtendsBox.getTrackId() != l1);
        localLinkedList = new LinkedList();
        Iterator localIterator3 = paramTrackBox.getIsoFile().getBoxes(MovieFragmentBox.class).iterator();
        l2 = 1L;
        if (localIterator3.hasNext())
          localIterator5 = ((MovieFragmentBox)localIterator3.next()).getBoxes(TrackFragmentBox.class).iterator();
      }
    }
    while (true)
    {
      if (localIterator5.hasNext());
      TrackFragmentBox localTrackFragmentBox = (TrackFragmentBox)localIterator5.next();
      if (localTrackFragmentBox.getTrackFragmentHeaderBox().getTrackId() != l1)
        continue;
      Iterator localIterator6 = localTrackFragmentBox.getBoxes(TrackRunBox.class).iterator();
      long l3 = l2;
      while (true)
      {
        if (!localIterator6.hasNext())
          break label1179;
        TrackRunBox localTrackRunBox = (TrackRunBox)localIterator6.next();
        TrackFragmentHeaderBox localTrackFragmentHeaderBox = ((TrackFragmentBox)localTrackRunBox.getParent()).getTrackFragmentHeaderBox();
        Iterator localIterator7 = localTrackRunBox.getEntries().iterator();
        int k = 1;
        long l4 = l3;
        if (localIterator7.hasNext())
        {
          label477: TrackRunBox.Entry localEntry = (TrackRunBox.Entry)localIterator7.next();
          if (localTrackRunBox.isSampleDurationPresent())
            if ((this.decodingTimeEntries.size() == 0) || (((TimeToSampleBox.Entry)this.decodingTimeEntries.get(-1 + this.decodingTimeEntries.size())).getDelta() != localEntry.getSampleDuration()))
            {
              this.decodingTimeEntries.add(new TimeToSampleBox.Entry(1L, localEntry.getSampleDuration()));
              if (localTrackRunBox.isSampleCompositionTimeOffsetPresent())
              {
                label577: if ((this.compositionTimeEntries.size() != 0) && (((CompositionTimeToSample.Entry)this.compositionTimeEntries.get(-1 + this.compositionTimeEntries.size())).getOffset() == localEntry.getSampleCompositionTimeOffset()))
                  break label811;
                this.compositionTimeEntries.add(new CompositionTimeToSample.Entry(1, CastUtils.l2i(localEntry.getSampleCompositionTimeOffset())));
              }
              label658: if (!localTrackRunBox.isSampleFlagsPresent())
                break label851;
            }
          for (SampleFlags localSampleFlags = localEntry.getSampleFlags(); ; localSampleFlags = localTrackExtendsBox.getDefaultSampleFlags())
            while (true)
            {
              if ((localSampleFlags != null) && (!localSampleFlags.isSampleIsDifferenceSample()))
                localLinkedList.add(Long.valueOf(l4));
              l4 += 1L;
              k = 0;
              break label477:
              TimeToSampleBox.Entry localEntry2 = (TimeToSampleBox.Entry)this.decodingTimeEntries.get(-1 + this.decodingTimeEntries.size());
              localEntry2.setCount(1L + localEntry2.getCount());
              break label577:
              if (localTrackFragmentHeaderBox.hasDefaultSampleDuration())
                this.decodingTimeEntries.add(new TimeToSampleBox.Entry(1L, localTrackFragmentHeaderBox.getDefaultSampleDuration()));
              this.decodingTimeEntries.add(new TimeToSampleBox.Entry(1L, localTrackExtendsBox.getDefaultSampleDuration()));
              break label577:
              label811: CompositionTimeToSample.Entry localEntry1 = (CompositionTimeToSample.Entry)this.compositionTimeEntries.get(-1 + this.compositionTimeEntries.size());
              localEntry1.setCount(1 + localEntry1.getCount());
              break label658:
              if ((k != 0) && (localTrackRunBox.isFirstSampleFlagsPresent()))
                label851: localSampleFlags = localTrackRunBox.getFirstSampleFlags();
              if (!localTrackFragmentHeaderBox.hasDefaultSampleFlags())
                break;
              localSampleFlags = localTrackFragmentHeaderBox.getDefaultSampleFlags();
            }
        }
        l3 = l4;
      }
      long[] arrayOfLong1 = this.syncSamples;
      this.syncSamples = new long[this.syncSamples.length + localLinkedList.size()];
      System.arraycopy(arrayOfLong1, 0, this.syncSamples, 0, arrayOfLong1.length);
      Iterator localIterator4 = localLinkedList.iterator();
      int j;
      for (int i = arrayOfLong1.length; ; i = j)
      {
        if (localIterator4.hasNext());
        Long localLong = (Long)localIterator4.next();
        long[] arrayOfLong2 = this.syncSamples;
        j = i + 1;
        arrayOfLong2[i] = localLong.longValue();
      }
      MediaHeaderBox localMediaHeaderBox = paramTrackBox.getMediaBox().getMediaHeaderBox();
      TrackHeaderBox localTrackHeaderBox = paramTrackBox.getTrackHeaderBox();
      setEnabled(localTrackHeaderBox.isEnabled());
      setInMovie(localTrackHeaderBox.isInMovie());
      setInPoster(localTrackHeaderBox.isInPoster());
      setInPreview(localTrackHeaderBox.isInPreview());
      this.trackMetaData.setTrackId(localTrackHeaderBox.getTrackId());
      this.trackMetaData.setCreationTime(DateHelper.convert(localMediaHeaderBox.getCreationTime()));
      this.trackMetaData.setLanguage(localMediaHeaderBox.getLanguage());
      this.trackMetaData.setModificationTime(DateHelper.convert(localMediaHeaderBox.getModificationTime()));
      this.trackMetaData.setTimescale(localMediaHeaderBox.getTimescale());
      this.trackMetaData.setHeight(localTrackHeaderBox.getHeight());
      this.trackMetaData.setWidth(localTrackHeaderBox.getWidth());
      this.trackMetaData.setLayer(localTrackHeaderBox.getLayer());
      this.trackMetaData.setMatrix(localTrackHeaderBox.getMatrix());
      return;
      label1179: l2 = l3;
    }
  }

  public List<CompositionTimeToSample.Entry> getCompositionTimeEntries()
  {
    return this.compositionTimeEntries;
  }

  public List<TimeToSampleBox.Entry> getDecodingTimeEntries()
  {
    return this.decodingTimeEntries;
  }

  public String getHandler()
  {
    return this.handler;
  }

  public AbstractMediaHeaderBox getMediaHeaderBox()
  {
    return this.mihd;
  }

  public List<SampleDependencyTypeBox.Entry> getSampleDependencies()
  {
    return this.sampleDependencies;
  }

  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.sampleDescriptionBox;
  }

  public List<ByteBuffer> getSamples()
  {
    return this.samples;
  }

  public long[] getSyncSamples()
  {
    return this.syncSamples;
  }

  public TrackMetaData getTrackMetaData()
  {
    return this.trackMetaData;
  }

  public String toString()
  {
    return "Mp4TrackImpl{handler='" + this.handler + '\'' + '}';
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.authoring.Mp4TrackImpl
 * JD-Core Version:    0.5.4
 */