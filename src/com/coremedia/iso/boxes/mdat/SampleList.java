package com.coremedia.iso.boxes.mdat;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ChunkOffsetBox;
import com.coremedia.iso.boxes.ContainerBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackExtendsBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox.Entry;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SampleList extends AbstractList<ByteBuffer>
{
  IsoFile isoFile;
  HashMap<MediaDataBox, Long> mdatEndCache = new HashMap();
  HashMap<MediaDataBox, Long> mdatStartCache = new HashMap();
  MediaDataBox[] mdats;
  long[] offsets;
  long[] sizes;

  public SampleList(TrackBox paramTrackBox)
  {
    initIsoFile(paramTrackBox.getIsoFile());
    SampleSizeBox localSampleSizeBox = paramTrackBox.getSampleTableBox().getSampleSizeBox();
    ChunkOffsetBox localChunkOffsetBox = paramTrackBox.getSampleTableBox().getChunkOffsetBox();
    SampleToChunkBox localSampleToChunkBox = paramTrackBox.getSampleTableBox().getSampleToChunkBox();
    long[] arrayOfLong1;
    label69: long[] arrayOfLong2;
    int i;
    if (localChunkOffsetBox != null)
    {
      arrayOfLong1 = localChunkOffsetBox.getChunkOffsets();
      if ((localSampleToChunkBox == null) || (localSampleToChunkBox.getEntries().size() <= 0) || (arrayOfLong1.length <= 0) || (localSampleSizeBox == null) || (localSampleSizeBox.getSampleCount() <= 0L))
        break label266;
      arrayOfLong2 = localSampleToChunkBox.blowup(arrayOfLong1.length);
      i = 0;
      if (localSampleSizeBox.getSampleSize() <= 0L)
        break label249;
      this.sizes = new long[CastUtils.l2i(localSampleSizeBox.getSampleCount())];
      Arrays.fill(this.sizes, localSampleSizeBox.getSampleSize());
      label152: this.offsets = new long[this.sizes.length];
    }
    for (int j = 0; j < arrayOfLong2.length; ++j)
    {
      long l1 = arrayOfLong2[j];
      long l2 = arrayOfLong1[j];
      int k = 0;
      while (true)
      {
        if (k >= l1)
          break label260;
        long l3 = this.sizes[i];
        this.offsets[i] = l2;
        l2 += l3;
        int l = i + 1;
        ++k;
        i = l;
      }
      arrayOfLong1 = new long[0];
      break label69:
      label249: this.sizes = localSampleSizeBox.getSampleSizes();
      label260: break label152:
    }
    label266: List localList = paramTrackBox.getParent().getBoxes(MovieExtendsBox.class);
    if (localList.size() <= 0)
      return;
    HashMap localHashMap = new HashMap();
    Iterator localIterator1 = ((MovieExtendsBox)localList.get(0)).getBoxes(TrackExtendsBox.class).iterator();
    TrackExtendsBox localTrackExtendsBox;
    do
    {
      if (!localIterator1.hasNext())
        break label418;
      localTrackExtendsBox = (TrackExtendsBox)localIterator1.next();
    }
    while (localTrackExtendsBox.getTrackId() != paramTrackBox.getTrackHeaderBox().getTrackId());
    Iterator localIterator2 = paramTrackBox.getIsoFile().getBoxes(MovieFragmentBox.class).iterator();
    while (true)
    {
      if (localIterator2.hasNext());
      localHashMap.putAll(getOffsets((MovieFragmentBox)localIterator2.next(), paramTrackBox.getTrackHeaderBox().getTrackId(), localTrackExtendsBox));
    }
    if ((this.sizes == null) || (this.offsets == null))
    {
      label418: this.sizes = new long[0];
      this.offsets = new long[0];
    }
    splitToArrays(localHashMap);
  }

  private void initIsoFile(IsoFile paramIsoFile)
  {
    this.isoFile = paramIsoFile;
    long l1 = 0L;
    LinkedList localLinkedList = new LinkedList();
    Iterator localIterator = this.isoFile.getBoxes().iterator();
    while (true)
    {
      if (!localIterator.hasNext())
        break label168;
      Box localBox = (Box)localIterator.next();
      long l2 = localBox.getSize();
      if ("mdat".equals(localBox.getType()))
      {
        if (!localBox instanceof MediaDataBox)
          break;
        long l3 = l1 + ((MediaDataBox)localBox).getHeader().limit();
        this.mdatStartCache.put((MediaDataBox)localBox, Long.valueOf(l3));
        this.mdatEndCache.put((MediaDataBox)localBox, Long.valueOf(l3 + l2));
        localLinkedList.add((MediaDataBox)localBox);
      }
      l1 += l2;
    }
    throw new RuntimeException("Sample need to be in mdats and mdats need to be instanceof MediaDataBox");
    label168: this.mdats = ((MediaDataBox[])localLinkedList.toArray(new MediaDataBox[localLinkedList.size()]));
  }

  private void splitToArrays(Map<Long, Long> paramMap)
  {
    ArrayList localArrayList = new ArrayList(paramMap.keySet());
    Collections.sort(localArrayList);
    long[] arrayOfLong1 = new long[this.sizes.length + localArrayList.size()];
    System.arraycopy(this.sizes, 0, arrayOfLong1, 0, this.sizes.length);
    long[] arrayOfLong2 = new long[this.offsets.length + localArrayList.size()];
    System.arraycopy(this.offsets, 0, arrayOfLong2, 0, this.offsets.length);
    for (int i = 0; i < localArrayList.size(); ++i)
    {
      arrayOfLong2[(i + this.offsets.length)] = ((Long)localArrayList.get(i)).longValue();
      arrayOfLong1[(i + this.sizes.length)] = ((Long)paramMap.get(localArrayList.get(i))).longValue();
    }
    this.sizes = arrayOfLong1;
    this.offsets = arrayOfLong2;
  }

  public ByteBuffer get(int paramInt)
  {
    long l1 = this.offsets[paramInt];
    int i = CastUtils.l2i(this.sizes[paramInt]);
    for (MediaDataBox localMediaDataBox : this.mdats)
    {
      long l2 = ((Long)this.mdatStartCache.get(localMediaDataBox)).longValue();
      long l3 = ((Long)this.mdatEndCache.get(localMediaDataBox)).longValue();
      if ((l2 <= l1) && (l1 + i <= l3))
        return localMediaDataBox.getContent(l1 - l2, i);
    }
    throw new RuntimeException("The sample with offset " + l1 + " and size " + i + " is NOT located within an mdat");
  }

  Map<Long, Long> getOffsets(MovieFragmentBox paramMovieFragmentBox, long paramLong, TrackExtendsBox paramTrackExtendsBox)
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator1 = paramMovieFragmentBox.getBoxes(TrackFragmentBox.class).iterator();
    long l1;
    Iterator localIterator2;
    do
    {
      TrackFragmentBox localTrackFragmentBox;
      do
      {
        if (!localIterator1.hasNext())
          break label332;
        localTrackFragmentBox = (TrackFragmentBox)localIterator1.next();
      }
      while (localTrackFragmentBox.getTrackFragmentHeaderBox().getTrackId() != paramLong);
      if (!localTrackFragmentBox.getTrackFragmentHeaderBox().hasBaseDataOffset())
        break label223;
      l1 = localTrackFragmentBox.getTrackFragmentHeaderBox().getBaseDataOffset();
      localIterator2 = localTrackFragmentBox.getBoxes(TrackRunBox.class).iterator();
    }
    while (!localIterator2.hasNext());
    TrackRunBox localTrackRunBox = (TrackRunBox)localIterator2.next();
    long l2 = l1 + localTrackRunBox.getDataOffset();
    TrackFragmentHeaderBox localTrackFragmentHeaderBox = ((TrackFragmentBox)localTrackRunBox.getParent()).getTrackFragmentHeaderBox();
    long l3 = 0L;
    Iterator localIterator3 = localTrackRunBox.getEntries().iterator();
    while (true)
    {
      if (localIterator3.hasNext());
      TrackRunBox.Entry localEntry = (TrackRunBox.Entry)localIterator3.next();
      if (localTrackRunBox.isSampleSizePresent())
      {
        long l6 = localEntry.getSampleSize();
        localHashMap.put(Long.valueOf(l3 + l2), Long.valueOf(l6));
        l3 += l6;
        continue;
        label223: l1 = paramMovieFragmentBox.getOffset();
      }
      if (localTrackFragmentHeaderBox.hasDefaultSampleSize())
      {
        long l5 = localTrackFragmentHeaderBox.getDefaultSampleSize();
        localHashMap.put(Long.valueOf(l3 + l2), Long.valueOf(l5));
        l3 += l5;
      }
      if (paramTrackExtendsBox == null)
        throw new RuntimeException("File doesn't contain trex box but track fragments aren't fully self contained. Cannot determine sample size.");
      long l4 = paramTrackExtendsBox.getDefaultSampleSize();
      localHashMap.put(Long.valueOf(l3 + l2), Long.valueOf(l4));
      l3 += l4;
    }
    label332: return localHashMap;
  }

  public int size()
  {
    return this.sizes.length;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.mdat.SampleList
 * JD-Core Version:    0.5.4
 */