package com.googlecode.mp4parser.authoring.container.mp4;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.TrackBox;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Mp4TrackImpl;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.List;

public class MovieCreator
{
  public static Movie build(ReadableByteChannel paramReadableByteChannel)
    throws IOException
  {
    IsoFile localIsoFile = new IsoFile(paramReadableByteChannel);
    Movie localMovie = new Movie();
    Iterator localIterator = localIsoFile.getMovieBox().getBoxes(TrackBox.class).iterator();
    while (localIterator.hasNext())
      localMovie.addTrack(new Mp4TrackImpl((TrackBox)localIterator.next()));
    return localMovie;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
 * JD-Core Version:    0.5.4
 */