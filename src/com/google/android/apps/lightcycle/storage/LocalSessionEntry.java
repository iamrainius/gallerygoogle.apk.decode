package com.google.android.apps.lightcycle.storage;

public class LocalSessionEntry
{
  public String captureDirectory;
  public String creatorVersion;
  public String id;
  public String metadataFile;
  public boolean stitchedExists;
  public String stitchedFile;
  public boolean thumbnailExists;
  public String thumbnailFile;

  public String toString()
  {
    return "ID : " + this.id + " \n Stitched exists : " + String.valueOf(this.stitchedExists) + "\n Thumbnail exists : " + String.valueOf(this.thumbnailExists) + "\n Stitched file : " + this.stitchedFile + "\n Metadata file " + this.metadataFile + "\n Capture directory : " + this.captureDirectory + "\n Creator version : " + this.creatorVersion;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.storage.LocalSessionEntry
 * JD-Core Version:    0.5.4
 */