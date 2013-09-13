package com.googlecode.mp4parser.authoring;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.TimeToSampleBox.Entry;
import java.nio.ByteBuffer;
import java.util.List;

public abstract interface Track
{
  public abstract List<CompositionTimeToSample.Entry> getCompositionTimeEntries();

  public abstract List<TimeToSampleBox.Entry> getDecodingTimeEntries();

  public abstract String getHandler();

  public abstract Box getMediaHeaderBox();

  public abstract List<SampleDependencyTypeBox.Entry> getSampleDependencies();

  public abstract SampleDescriptionBox getSampleDescriptionBox();

  public abstract List<ByteBuffer> getSamples();

  public abstract long[] getSyncSamples();

  public abstract TrackMetaData getTrackMetaData();

  public abstract boolean isEnabled();

  public abstract boolean isInMovie();

  public abstract boolean isInPoster();

  public abstract boolean isInPreview();
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.authoring.Track
 * JD-Core Version:    0.5.4
 */