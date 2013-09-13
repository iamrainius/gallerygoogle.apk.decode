package com.android.gallery3d.data;

import android.database.Cursor;
import com.android.gallery3d.util.GalleryUtils;
import java.text.DateFormat;
import java.util.Date;

public abstract class LocalMediaItem extends MediaItem
{
  public int bucketId;
  public String caption;
  public long dateAddedInSec;
  public long dateModifiedInSec;
  public long dateTakenInMs;
  public String filePath;
  public long fileSize;
  public int height;
  public int id;
  public double latitude = 0.0D;
  public double longitude = 0.0D;
  public String mimeType;
  public int width;

  public LocalMediaItem(Path paramPath, long paramLong)
  {
    super(paramPath, paramLong);
  }

  public int getBucketId()
  {
    return this.bucketId;
  }

  public long getDateInMs()
  {
    return this.dateTakenInMs;
  }

  public MediaDetails getDetails()
  {
    MediaDetails localMediaDetails = super.getDetails();
    localMediaDetails.addDetail(200, this.filePath);
    localMediaDetails.addDetail(1, this.caption);
    localMediaDetails.addDetail(3, DateFormat.getDateTimeInstance().format(new Date(1000L * this.dateModifiedInSec)));
    localMediaDetails.addDetail(5, Integer.valueOf(this.width));
    localMediaDetails.addDetail(6, Integer.valueOf(this.height));
    if (GalleryUtils.isValidLocation(this.latitude, this.longitude))
    {
      double[] arrayOfDouble = new double[2];
      arrayOfDouble[0] = this.latitude;
      arrayOfDouble[1] = this.longitude;
      localMediaDetails.addDetail(4, arrayOfDouble);
    }
    if (this.fileSize > 0L)
      localMediaDetails.addDetail(10, Long.valueOf(this.fileSize));
    return localMediaDetails;
  }

  public void getLatLong(double[] paramArrayOfDouble)
  {
    paramArrayOfDouble[0] = this.latitude;
    paramArrayOfDouble[1] = this.longitude;
  }

  public String getMimeType()
  {
    return this.mimeType;
  }

  public String getName()
  {
    return this.caption;
  }

  public long getSize()
  {
    return this.fileSize;
  }

  protected void updateContent(Cursor paramCursor)
  {
    if (!updateFromCursor(paramCursor))
      return;
    this.mDataVersion = nextVersionNumber();
  }

  protected abstract boolean updateFromCursor(Cursor paramCursor);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.LocalMediaItem
 * JD-Core Version:    0.5.4
 */