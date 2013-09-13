package com.google.android.apps.lightcycle.storage;

import android.location.Location;

public class PhotoMetadata
{
  public String filePath;
  public Location location;
  public int poseHeading;
  public long timestamp;

  public PhotoMetadata(long paramLong, String paramString, Location paramLocation, int paramInt)
  {
    this.timestamp = paramLong;
    this.filePath = paramString;
    this.location = paramLocation;
    this.poseHeading = paramInt;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.storage.PhotoMetadata
 * JD-Core Version:    0.5.4
 */