package com.android.gallery3d.app;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.TimeToSampleBox.Entry;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TrimVideoUtils
{
  private static double correctTimeToSyncSample(Track paramTrack, double paramDouble, boolean paramBoolean)
  {
    double[] arrayOfDouble = new double[paramTrack.getSyncSamples().length];
    long l = 0L;
    double d1 = 0.0D;
    for (int i = 0; i < paramTrack.getDecodingTimeEntries().size(); ++i)
    {
      TimeToSampleBox.Entry localEntry = (TimeToSampleBox.Entry)paramTrack.getDecodingTimeEntries().get(i);
      for (int i1 = 0; i1 < localEntry.getCount(); ++i1)
      {
        if (Arrays.binarySearch(paramTrack.getSyncSamples(), 1L + l) >= 0)
          arrayOfDouble[Arrays.binarySearch(paramTrack.getSyncSamples(), 1L + l)] = d1;
        d1 += localEntry.getDelta() / paramTrack.getTrackMetaData().getTimescale();
        l += 1L;
      }
    }
    double d2 = 0.0D;
    int j = arrayOfDouble.length;
    for (int k = 0; k < j; ++k)
    {
      double d3 = arrayOfDouble[k];
      if (d3 > paramDouble)
      {
        if (paramBoolean)
          return d3;
        return d2;
      }
      d2 = d3;
    }
    return arrayOfDouble[(-1 + arrayOfDouble.length)];
  }

  public static void startTrim(File paramFile1, File paramFile2, int paramInt1, int paramInt2)
    throws IOException
  {
    RandomAccessFile localRandomAccessFile = new RandomAccessFile(paramFile1, "r");
    Movie localMovie = MovieCreator.build(localRandomAccessFile.getChannel());
    List localList = localMovie.getTracks();
    localMovie.setTracks(new LinkedList());
    double d1 = paramInt1 / 1000;
    double d2 = paramInt2 / 1000;
    int i = 0;
    Iterator localIterator1 = localList.iterator();
    while (localIterator1.hasNext())
    {
      Track localTrack2 = (Track)localIterator1.next();
      if ((localTrack2.getSyncSamples() == null) || (localTrack2.getSyncSamples().length <= 0))
        continue;
      if (i != 0)
        throw new RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.");
      d1 = correctTimeToSyncSample(localTrack2, d1, false);
      d2 = correctTimeToSyncSample(localTrack2, d2, true);
      i = 1;
    }
    Iterator localIterator2 = localList.iterator();
    while (localIterator2.hasNext())
    {
      Track localTrack1 = (Track)localIterator2.next();
      long l1 = 0L;
      double d3 = 0.0D;
      long l2 = -1L;
      long l3 = -1L;
      for (int j = 0; ; ++j)
      {
        int k = localTrack1.getDecodingTimeEntries().size();
        if (j >= k)
          break;
        TimeToSampleBox.Entry localEntry = (TimeToSampleBox.Entry)localTrack1.getDecodingTimeEntries().get(j);
        for (int l = 0; l < localEntry.getCount(); ++l)
        {
          if (d3 <= d1)
            l2 = l1;
          if (d3 > d2)
            break;
          l3 = l1;
          d3 += localEntry.getDelta() / localTrack1.getTrackMetaData().getTimescale();
          l1 += 1L;
        }
      }
      localMovie.addTrack(new CroppedTrack(localTrack1, l2, l3));
    }
    IsoFile localIsoFile = new DefaultMp4Builder().build(localMovie);
    if (!paramFile2.exists())
      paramFile2.createNewFile();
    FileOutputStream localFileOutputStream = new FileOutputStream(paramFile2);
    FileChannel localFileChannel = localFileOutputStream.getChannel();
    localIsoFile.getBox(localFileChannel);
    localFileChannel.close();
    localFileOutputStream.close();
    localRandomAccessFile.close();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.TrimVideoUtils
 * JD-Core Version:    0.5.4
 */