package com.googlecode.mp4parser.authoring.builder;

import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;

public abstract interface FragmentIntersectionFinder
{
  public abstract long[] sampleNumbers(Track paramTrack, Movie paramMovie);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.authoring.builder.FragmentIntersectionFinder
 * JD-Core Version:    0.5.4
 */