package com.googlecode.mp4parser.authoring.builder;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.ContainerBox;
import com.coremedia.iso.boxes.DataEntryUrlBox;
import com.coremedia.iso.boxes.DataInformationBox;
import com.coremedia.iso.boxes.DataReferenceBox;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.SampleToChunkBox.Entry;
import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.coremedia.iso.boxes.SyncSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox.Entry;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.authoring.DateHelper;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultMp4Builder
{
  private static Logger LOG;
  public int STEPSIZE = 64;
  Set<StaticChunkOffsetBox> chunkOffsetBoxes = new HashSet();
  private FragmentIntersectionFinder intersectionFinder = new TwoSecondIntersectionFinder();
  HashMap<Track, List<ByteBuffer>> track2Sample = new HashMap();
  HashMap<Track, long[]> track2SampleSizes = new HashMap();

  static
  {
    if (!DefaultMp4Builder.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      LOG = Logger.getLogger(DefaultMp4Builder.class.getName());
      return;
    }
  }

  private MovieBox createMovieBox(Movie paramMovie)
  {
    MovieBox localMovieBox = new MovieBox();
    MovieHeaderBox localMovieHeaderBox = new MovieHeaderBox();
    localMovieHeaderBox.setCreationTime(DateHelper.convert(new Date()));
    localMovieHeaderBox.setModificationTime(DateHelper.convert(new Date()));
    long l1 = getTimescale(paramMovie);
    long l2 = 0L;
    Iterator localIterator1 = paramMovie.getTracks().iterator();
    while (localIterator1.hasNext())
    {
      Track localTrack2 = (Track)localIterator1.next();
      long l4 = l1 * getDuration(localTrack2) / localTrack2.getTrackMetaData().getTimescale();
      if (l4 <= l2)
        continue;
      l2 = l4;
    }
    localMovieHeaderBox.setDuration(l2);
    localMovieHeaderBox.setTimescale(l1);
    long l3 = 0L;
    Iterator localIterator2 = paramMovie.getTracks().iterator();
    while (localIterator2.hasNext())
    {
      Track localTrack1 = (Track)localIterator2.next();
      if (l3 < localTrack1.getTrackMetaData().getTrackId())
        l3 = localTrack1.getTrackMetaData().getTrackId();
    }
    localMovieHeaderBox.setNextTrackId(l3 + 1L);
    if ((localMovieHeaderBox.getCreationTime() >= 4294967296L) || (localMovieHeaderBox.getModificationTime() >= 4294967296L) || (localMovieHeaderBox.getDuration() >= 4294967296L))
      localMovieHeaderBox.setVersion(1);
    localMovieBox.addBox(localMovieHeaderBox);
    Iterator localIterator3 = paramMovie.getTracks().iterator();
    while (localIterator3.hasNext())
      localMovieBox.addBox(createTrackBox((Track)localIterator3.next(), paramMovie));
    Box localBox = createUdta(paramMovie);
    if (localBox != null)
      localMovieBox.addBox(localBox);
    return localMovieBox;
  }

  private TrackBox createTrackBox(Track paramTrack, Movie paramMovie)
  {
    LOG.info("Creating Mp4TrackImpl " + paramTrack);
    TrackBox localTrackBox = new TrackBox();
    TrackHeaderBox localTrackHeaderBox = new TrackHeaderBox();
    boolean bool = paramTrack.isEnabled();
    int i = 0;
    if (bool)
      i = 0 + 1;
    if (paramTrack.isInMovie())
      i += 2;
    if (paramTrack.isInPreview())
      i += 4;
    if (paramTrack.isInPoster());
    localTrackHeaderBox.setFlags(i += 8);
    localTrackHeaderBox.setAlternateGroup(paramTrack.getTrackMetaData().getGroup());
    localTrackHeaderBox.setCreationTime(DateHelper.convert(paramTrack.getTrackMetaData().getCreationTime()));
    localTrackHeaderBox.setDuration(getDuration(paramTrack) * getTimescale(paramMovie) / paramTrack.getTrackMetaData().getTimescale());
    localTrackHeaderBox.setHeight(paramTrack.getTrackMetaData().getHeight());
    localTrackHeaderBox.setWidth(paramTrack.getTrackMetaData().getWidth());
    localTrackHeaderBox.setLayer(paramTrack.getTrackMetaData().getLayer());
    localTrackHeaderBox.setModificationTime(DateHelper.convert(new Date()));
    localTrackHeaderBox.setTrackId(paramTrack.getTrackMetaData().getTrackId());
    localTrackHeaderBox.setVolume(paramTrack.getTrackMetaData().getVolume());
    localTrackHeaderBox.setMatrix(paramTrack.getTrackMetaData().getMatrix());
    if ((localTrackHeaderBox.getCreationTime() >= 4294967296L) || (localTrackHeaderBox.getModificationTime() >= 4294967296L) || (localTrackHeaderBox.getDuration() >= 4294967296L))
      localTrackHeaderBox.setVersion(1);
    localTrackBox.addBox(localTrackHeaderBox);
    MediaBox localMediaBox = new MediaBox();
    localTrackBox.addBox(localMediaBox);
    MediaHeaderBox localMediaHeaderBox = new MediaHeaderBox();
    localMediaHeaderBox.setCreationTime(DateHelper.convert(paramTrack.getTrackMetaData().getCreationTime()));
    localMediaHeaderBox.setDuration(getDuration(paramTrack));
    localMediaHeaderBox.setTimescale(paramTrack.getTrackMetaData().getTimescale());
    localMediaHeaderBox.setLanguage(paramTrack.getTrackMetaData().getLanguage());
    localMediaBox.addBox(localMediaHeaderBox);
    HandlerBox localHandlerBox = new HandlerBox();
    localMediaBox.addBox(localHandlerBox);
    localHandlerBox.setHandlerType(paramTrack.getHandler());
    MediaInformationBox localMediaInformationBox = new MediaInformationBox();
    localMediaInformationBox.addBox(paramTrack.getMediaHeaderBox());
    DataInformationBox localDataInformationBox = new DataInformationBox();
    DataReferenceBox localDataReferenceBox = new DataReferenceBox();
    localDataInformationBox.addBox(localDataReferenceBox);
    DataEntryUrlBox localDataEntryUrlBox = new DataEntryUrlBox();
    localDataEntryUrlBox.setFlags(1);
    localDataReferenceBox.addBox(localDataEntryUrlBox);
    localMediaInformationBox.addBox(localDataInformationBox);
    SampleTableBox localSampleTableBox = new SampleTableBox();
    localSampleTableBox.addBox(paramTrack.getSampleDescriptionBox());
    if ((paramTrack.getDecodingTimeEntries() != null) && (!paramTrack.getDecodingTimeEntries().isEmpty()))
    {
      TimeToSampleBox localTimeToSampleBox = new TimeToSampleBox();
      localTimeToSampleBox.setEntries(paramTrack.getDecodingTimeEntries());
      localSampleTableBox.addBox(localTimeToSampleBox);
    }
    List localList = paramTrack.getCompositionTimeEntries();
    if ((localList != null) && (!localList.isEmpty()))
    {
      CompositionTimeToSample localCompositionTimeToSample = new CompositionTimeToSample();
      localCompositionTimeToSample.setEntries(localList);
      localSampleTableBox.addBox(localCompositionTimeToSample);
    }
    long[] arrayOfLong1 = paramTrack.getSyncSamples();
    if ((arrayOfLong1 != null) && (arrayOfLong1.length > 0))
    {
      SyncSampleBox localSyncSampleBox = new SyncSampleBox();
      localSyncSampleBox.setSampleNumber(arrayOfLong1);
      localSampleTableBox.addBox(localSyncSampleBox);
    }
    if ((paramTrack.getSampleDependencies() != null) && (!paramTrack.getSampleDependencies().isEmpty()))
    {
      SampleDependencyTypeBox localSampleDependencyTypeBox = new SampleDependencyTypeBox();
      localSampleDependencyTypeBox.setEntries(paramTrack.getSampleDependencies());
      localSampleTableBox.addBox(localSampleDependencyTypeBox);
    }
    HashMap localHashMap = new HashMap();
    Iterator localIterator1 = paramMovie.getTracks().iterator();
    while (localIterator1.hasNext())
    {
      Track localTrack2 = (Track)localIterator1.next();
      localHashMap.put(localTrack2, getChunkSizes(localTrack2, paramMovie));
    }
    int[] arrayOfInt1 = (int[])localHashMap.get(paramTrack);
    SampleToChunkBox localSampleToChunkBox = new SampleToChunkBox();
    localSampleToChunkBox.setEntries(new LinkedList());
    long l1 = -2147483648L;
    for (int j = 0; ; ++j)
    {
      int k = arrayOfInt1.length;
      if (j >= k)
        break;
      if (l1 == arrayOfInt1[j])
        continue;
      localSampleToChunkBox.getEntries().add(new SampleToChunkBox.Entry(j + 1, arrayOfInt1[j], 1L));
      l1 = arrayOfInt1[j];
    }
    localSampleTableBox.addBox(localSampleToChunkBox);
    SampleSizeBox localSampleSizeBox = new SampleSizeBox();
    localSampleSizeBox.setSampleSizes((long[])this.track2SampleSizes.get(paramTrack));
    localSampleTableBox.addBox(localSampleSizeBox);
    StaticChunkOffsetBox localStaticChunkOffsetBox = new StaticChunkOffsetBox();
    this.chunkOffsetBoxes.add(localStaticChunkOffsetBox);
    long l2 = 0L;
    long[] arrayOfLong2 = new long[arrayOfInt1.length];
    if (LOG.isLoggable(Level.FINE))
      LOG.fine("Calculating chunk offsets for track_" + paramTrack.getTrackMetaData().getTrackId());
    for (int l = 0; ; ++l)
    {
      int i1 = arrayOfInt1.length;
      if (l >= i1)
        break;
      if (LOG.isLoggable(Level.FINER))
        LOG.finer("Calculating chunk offsets for track_" + paramTrack.getTrackMetaData().getTrackId() + " chunk " + l);
      Iterator localIterator2 = paramMovie.getTracks().iterator();
      if (!localIterator2.hasNext())
        continue;
      Track localTrack1 = (Track)localIterator2.next();
      if (LOG.isLoggable(Level.FINEST))
        LOG.finest("Adding offsets of track_" + localTrack1.getTrackMetaData().getTrackId());
      int[] arrayOfInt2 = (int[])localHashMap.get(localTrack1);
      long l3 = 0L;
      for (int i2 = 0; i2 < l; ++i2)
        l3 += arrayOfInt2[i2];
      if (localTrack1 == paramTrack)
        arrayOfLong2[l] = l2;
      for (int i3 = CastUtils.l2i(l3); ; ++i3)
      {
        if (i3 < l3 + arrayOfInt2[l]);
        l2 += ((long[])this.track2SampleSizes.get(localTrack1))[i3];
      }
    }
    localStaticChunkOffsetBox.setChunkOffsets(arrayOfLong2);
    localSampleTableBox.addBox(localStaticChunkOffsetBox);
    localMediaInformationBox.addBox(localSampleTableBox);
    localMediaBox.addBox(localMediaInformationBox);
    return localTrackBox;
  }

  public static long gcd(long paramLong1, long paramLong2)
  {
    if (paramLong2 == 0L)
      return paramLong1;
    return gcd(paramLong2, paramLong1 % paramLong2);
  }

  protected static long getDuration(Track paramTrack)
  {
    long l = 0L;
    Iterator localIterator = paramTrack.getDecodingTimeEntries().iterator();
    while (localIterator.hasNext())
    {
      TimeToSampleBox.Entry localEntry = (TimeToSampleBox.Entry)localIterator.next();
      l += localEntry.getCount() * localEntry.getDelta();
    }
    return l;
  }

  private static long sum(int[] paramArrayOfInt)
  {
    long l = 0L;
    int i = paramArrayOfInt.length;
    for (int j = 0; j < i; ++j)
      l += paramArrayOfInt[j];
    return l;
  }

  public IsoFile build(Movie paramMovie)
  {
    LOG.fine("Creating movie " + paramMovie);
    Iterator localIterator1 = paramMovie.getTracks().iterator();
    while (localIterator1.hasNext())
    {
      Track localTrack = (Track)localIterator1.next();
      List localList = localTrack.getSamples();
      putSamples(localTrack, localList);
      long[] arrayOfLong2 = new long[localList.size()];
      for (int j = 0; j < arrayOfLong2.length; ++j)
        arrayOfLong2[j] = ((ByteBuffer)localList.get(j)).limit();
      putSampleSizes(localTrack, arrayOfLong2);
    }
    IsoFile localIsoFile = new IsoFile();
    LinkedList localLinkedList = new LinkedList();
    localLinkedList.add("isom");
    localLinkedList.add("iso2");
    localLinkedList.add("avc1");
    localIsoFile.addBox(new FileTypeBox("isom", 0L, localLinkedList));
    localIsoFile.addBox(createMovieBox(paramMovie));
    InterleaveChunkMdat localInterleaveChunkMdat = new InterleaveChunkMdat(paramMovie, null);
    localIsoFile.addBox(localInterleaveChunkMdat);
    long l = localInterleaveChunkMdat.getDataOffset();
    Iterator localIterator2 = this.chunkOffsetBoxes.iterator();
    if (localIterator2.hasNext())
    {
      long[] arrayOfLong1 = ((StaticChunkOffsetBox)localIterator2.next()).getChunkOffsets();
      for (int i = 0; ; ++i)
      {
        if (i < arrayOfLong1.length);
        arrayOfLong1[i] = (l + arrayOfLong1[i]);
      }
    }
    return localIsoFile;
  }

  protected Box createUdta(Movie paramMovie)
  {
    return null;
  }

  int[] getChunkSizes(Track paramTrack, Movie paramMovie)
  {
    long[] arrayOfLong = this.intersectionFinder.sampleNumbers(paramTrack, paramMovie);
    int[] arrayOfInt = new int[arrayOfLong.length];
    int i = 0;
    if (i < arrayOfLong.length)
    {
      label21: long l1 = arrayOfLong[i] - 1L;
      long l2;
      if (arrayOfLong.length == i + 1)
        l2 = paramTrack.getSamples().size();
      while (true)
      {
        arrayOfInt[i] = CastUtils.l2i(l2 - l1);
        ++i;
        break label21:
        l2 = arrayOfLong[(i + 1)] - 1L;
      }
    }
    assert (((List)this.track2Sample.get(paramTrack)).size() == sum(arrayOfInt)) : "The number of samples and the sum of all chunk lengths must be equal";
    return arrayOfInt;
  }

  public long getTimescale(Movie paramMovie)
  {
    long l = ((Track)paramMovie.getTracks().iterator().next()).getTrackMetaData().getTimescale();
    Iterator localIterator = paramMovie.getTracks().iterator();
    while (localIterator.hasNext())
      l = gcd(((Track)localIterator.next()).getTrackMetaData().getTimescale(), l);
    return l;
  }

  protected long[] putSampleSizes(Track paramTrack, long[] paramArrayOfLong)
  {
    return (long[])this.track2SampleSizes.put(paramTrack, paramArrayOfLong);
  }

  protected List<ByteBuffer> putSamples(Track paramTrack, List<ByteBuffer> paramList)
  {
    return (List)this.track2Sample.put(paramTrack, paramList);
  }

  public List<ByteBuffer> unifyAdjacentBuffers(List<ByteBuffer> paramList)
  {
    ArrayList localArrayList = new ArrayList(paramList.size());
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      ByteBuffer localByteBuffer1 = (ByteBuffer)localIterator.next();
      int i = -1 + localArrayList.size();
      if ((i >= 0) && (localByteBuffer1.hasArray()) && (((ByteBuffer)localArrayList.get(i)).hasArray()) && (localByteBuffer1.array() == ((ByteBuffer)localArrayList.get(i)).array()) && (((ByteBuffer)localArrayList.get(i)).arrayOffset() + ((ByteBuffer)localArrayList.get(i)).limit() == localByteBuffer1.arrayOffset()))
      {
        ByteBuffer localByteBuffer3 = (ByteBuffer)localArrayList.remove(i);
        localArrayList.add(ByteBuffer.wrap(localByteBuffer1.array(), localByteBuffer3.arrayOffset(), localByteBuffer3.limit() + localByteBuffer1.limit()).slice());
      }
      if ((i >= 0) && (localByteBuffer1 instanceof MappedByteBuffer) && (localArrayList.get(i) instanceof MappedByteBuffer) && (((ByteBuffer)localArrayList.get(i)).limit() == ((ByteBuffer)localArrayList.get(i)).capacity() - localByteBuffer1.capacity()))
      {
        ByteBuffer localByteBuffer2 = (ByteBuffer)localArrayList.get(i);
        localByteBuffer2.limit(localByteBuffer1.limit() + localByteBuffer2.limit());
      }
      localArrayList.add(localByteBuffer1);
    }
    return localArrayList;
  }

  private class InterleaveChunkMdat
    implements Box
  {
    long contentSize = 0L;
    ContainerBox parent;
    List<ByteBuffer> samples = new ArrayList();
    List<Track> tracks;

    private InterleaveChunkMdat(Movie arg2)
    {
      Movie localMovie;
      this.tracks = localMovie.getTracks();
      HashMap localHashMap = new HashMap();
      Iterator localIterator1 = localMovie.getTracks().iterator();
      while (localIterator1.hasNext())
      {
        Track localTrack2 = (Track)localIterator1.next();
        localHashMap.put(localTrack2, DefaultMp4Builder.this.getChunkSizes(localTrack2, localMovie));
      }
      for (int i = 0; i < ((int[])localHashMap.values().iterator().next()).length; ++i)
      {
        Iterator localIterator2 = this.tracks.iterator();
        if (!localIterator2.hasNext())
          continue;
        Track localTrack1 = (Track)localIterator2.next();
        int[] arrayOfInt = (int[])localHashMap.get(localTrack1);
        long l = 0L;
        for (int j = 0; j < i; ++j)
          l += arrayOfInt[j];
        for (int k = CastUtils.l2i(l); ; ++k)
        {
          if (k < l + arrayOfInt[i]);
          ByteBuffer localByteBuffer = (ByteBuffer)((List)DefaultMp4Builder.this.track2Sample.get(localTrack1)).get(k);
          this.contentSize += localByteBuffer.limit();
          this.samples.add((ByteBuffer)localByteBuffer.rewind());
        }
      }
    }

    private boolean isSmallBox(long paramLong)
    {
      return 8L + paramLong < 4294967296L;
    }

    public void getBox(WritableByteChannel paramWritableByteChannel)
      throws IOException
    {
      ByteBuffer localByteBuffer1 = ByteBuffer.allocate(16);
      long l = getSize();
      label24: label51: List localList1;
      int i;
      label87: int j;
      if (isSmallBox(l))
      {
        IsoTypeWriter.writeUInt32(localByteBuffer1, l);
        localByteBuffer1.put(IsoFile.fourCCtoBytes("mdat"));
        if (!isSmallBox(l))
          break label237;
        localByteBuffer1.put(new byte[8]);
        localByteBuffer1.rewind();
        paramWritableByteChannel.write(localByteBuffer1);
        if (!paramWritableByteChannel instanceof GatheringByteChannel)
          break label257;
        localList1 = DefaultMp4Builder.this.unifyAdjacentBuffers(this.samples);
        i = 0;
        if (i >= Math.ceil(localList1.size() / DefaultMp4Builder.this.STEPSIZE))
          return;
        j = i * DefaultMp4Builder.this.STEPSIZE;
        if ((i + 1) * DefaultMp4Builder.this.STEPSIZE >= localList1.size())
          break label245;
      }
      for (int k = (i + 1) * DefaultMp4Builder.this.STEPSIZE; ; k = localList1.size())
      {
        List localList2 = localList1.subList(j, k);
        ByteBuffer[] arrayOfByteBuffer = (ByteBuffer[])localList2.toArray(new ByteBuffer[localList2.size()]);
        do
          ((GatheringByteChannel)paramWritableByteChannel).write(arrayOfByteBuffer);
        while (arrayOfByteBuffer[(-1 + arrayOfByteBuffer.length)].remaining() > 0);
        ++i;
        break label87:
        IsoTypeWriter.writeUInt32(localByteBuffer1, 1L);
        break label24:
        label237: IsoTypeWriter.writeUInt64(localByteBuffer1, l);
        label245: break label51:
      }
      label257: Iterator localIterator = this.samples.iterator();
      while (localIterator.hasNext())
      {
        ByteBuffer localByteBuffer2 = (ByteBuffer)localIterator.next();
        localByteBuffer2.rewind();
        paramWritableByteChannel.write(localByteBuffer2);
      }
    }

    public long getDataOffset()
    {
      Object localObject = this;
      long l = 16L;
      if (((Box)localObject).getParent() != null)
      {
        label6: Iterator localIterator = ((Box)localObject).getParent().getBoxes().iterator();
        while (true)
        {
          Box localBox;
          if (localIterator.hasNext())
          {
            localBox = (Box)localIterator.next();
            if (localObject != localBox)
              break label71;
          }
          localObject = ((Box)localObject).getParent();
          break label6:
          label71: l += localBox.getSize();
        }
      }
      return l;
    }

    public ContainerBox getParent()
    {
      return this.parent;
    }

    public long getSize()
    {
      return 16L + this.contentSize;
    }

    public String getType()
    {
      return "mdat";
    }

    public void parse(ReadableByteChannel paramReadableByteChannel, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
      throws IOException
    {
    }

    public void setParent(ContainerBox paramContainerBox)
    {
      this.parent = paramContainerBox;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
 * JD-Core Version:    0.5.4
 */