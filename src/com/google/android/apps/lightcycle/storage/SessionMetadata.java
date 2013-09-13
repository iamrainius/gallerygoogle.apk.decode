package com.google.android.apps.lightcycle.storage;

public class SessionMetadata
{
  public String creatorVersion;
  public int numPhotos;
  public boolean stitchedPanoramaExists;
  public boolean thumbnailExists;

  public String toString()
  {
    return "Number of Photos : " + String.valueOf(this.numPhotos) + "\n Panorama exists : " + String.valueOf(this.stitchedPanoramaExists) + "\n thumbnail exists : " + String.valueOf(this.thumbnailExists) + "\n creator version " + this.creatorVersion;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.storage.SessionMetadata
 * JD-Core Version:    0.5.4
 */