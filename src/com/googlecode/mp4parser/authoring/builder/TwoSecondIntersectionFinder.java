package com.googlecode.mp4parser.authoring.builder;

import com.coremedia.iso.boxes.TimeToSampleBox.Entry;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TwoSecondIntersectionFinder
  implements FragmentIntersectionFinder
{
  protected long getDuration(Track paramTrack)
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

  public long[] sampleNumbers(Track paramTrack, Movie paramMovie)
  {
    List localList = paramTrack.getDecodingTimeEntries();
    double d1 = 0.0D;
    Iterator localIterator1 = paramMovie.getTracks().iterator();
    while (localIterator1.hasNext())
    {
      Track localTrack = (Track)localIterator1.next();
      double d2 = getDuration(localTrack) / localTrack.getTrackMetaData().getTimescale();
      if (d1 >= d2)
        continue;
      d1 = d2;
    }
    int i = -1 + (int)Math.ceil(d1 / 2.0D);
    if (i < 1)
      i = 1;
    long[] arrayOfLong = new long[i];
    Arrays.fill(arrayOfLong, -1L);
    arrayOfLong[0] = 1L;
    long l1 = 0L;
    int j = 0;
    Iterator localIterator2 = localList.iterator();
    if (localIterator2.hasNext())
    {
      TimeToSampleBox.Entry localEntry = (TimeToSampleBox.Entry)localIterator2.next();
      int l = 0;
      while (true)
      {
        if (l < localEntry.getCount());
        int i1 = 1 + (int)(l1 / paramTrack.getTrackMetaData().getTimescale() / 2L);
        if (i1 < arrayOfLong.length);
        int i2 = j + 1;
        arrayOfLong[i1] = (j + 1);
        l1 += localEntry.getDelta();
        ++l;
        j = i2;
      }
    }
    long l2 = j + 1;
    for (int k = -1 + arrayOfLong.length; k >= 0; --k)
    {
      if (arrayOfLong[k] == -1L)
        arrayOfLong[k] = l2;
      l2 = arrayOfLong[k];
    }
    return arrayOfLong;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.authoring.builder.TwoSecondIntersectionFinder
 * JD-Core Version:    0.5.4
 */