package com.googlecode.mp4parser.authoring;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Movie
{
  List<Track> tracks = new LinkedList();

  public void addTrack(Track paramTrack)
  {
    if (getTrackByTrackId(paramTrack.getTrackMetaData().getTrackId()) != null)
      paramTrack.getTrackMetaData().setTrackId(getNextTrackId());
    this.tracks.add(paramTrack);
  }

  public long getNextTrackId()
  {
    long l = 0L;
    Iterator localIterator = this.tracks.iterator();
    while (localIterator.hasNext())
    {
      Track localTrack = (Track)localIterator.next();
      if (l < localTrack.getTrackMetaData().getTrackId())
        l = localTrack.getTrackMetaData().getTrackId();
    }
    return l + 1L;
  }

  public Track getTrackByTrackId(long paramLong)
  {
    Iterator localIterator = this.tracks.iterator();
    Track localTrack;
    while (localIterator.hasNext())
    {
      localTrack = (Track)localIterator.next();
      if (localTrack.getTrackMetaData().getTrackId() == paramLong)
        return localTrack;
    }
    return null;
  }

  public List<Track> getTracks()
  {
    return this.tracks;
  }

  public void setTracks(List<Track> paramList)
  {
    this.tracks = paramList;
  }

  public String toString()
  {
    String str = "Movie{ ";
    Iterator localIterator = this.tracks.iterator();
    while (localIterator.hasNext())
    {
      Track localTrack = (Track)localIterator.next();
      str = str + "track_" + localTrack.getTrackMetaData().getTrackId() + " (" + localTrack.getHandler() + ") ";
    }
    return str + '}';
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.authoring.Movie
 * JD-Core Version:    0.5.4
 */