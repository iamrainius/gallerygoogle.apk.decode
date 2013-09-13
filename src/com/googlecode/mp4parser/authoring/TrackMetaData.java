package com.googlecode.mp4parser.authoring;

import java.util.Date;

public class TrackMetaData
  implements Cloneable
{
  private Date creationTime = new Date();
  private int group = 0;
  private double height;
  private String language;
  int layer;
  private long[] matrix = { 65536L, 0L, 0L, 0L, 65536L, 0L, 0L, 0L, 1073741824L };
  private Date modificationTime = new Date();
  private long timescale;
  private long trackId = 1L;
  private float volume;
  private double width;

  public Object clone()
  {
    try
    {
      Object localObject = super.clone();
      return localObject;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
    }
    return null;
  }

  public Date getCreationTime()
  {
    return this.creationTime;
  }

  public int getGroup()
  {
    return this.group;
  }

  public double getHeight()
  {
    return this.height;
  }

  public String getLanguage()
  {
    return this.language;
  }

  public int getLayer()
  {
    return this.layer;
  }

  public long[] getMatrix()
  {
    return this.matrix;
  }

  public long getTimescale()
  {
    return this.timescale;
  }

  public long getTrackId()
  {
    return this.trackId;
  }

  public float getVolume()
  {
    return this.volume;
  }

  public double getWidth()
  {
    return this.width;
  }

  public void setCreationTime(Date paramDate)
  {
    this.creationTime = paramDate;
  }

  public void setHeight(double paramDouble)
  {
    this.height = paramDouble;
  }

  public void setLanguage(String paramString)
  {
    this.language = paramString;
  }

  public void setLayer(int paramInt)
  {
    this.layer = paramInt;
  }

  public void setMatrix(long[] paramArrayOfLong)
  {
    this.matrix = paramArrayOfLong;
  }

  public void setModificationTime(Date paramDate)
  {
    this.modificationTime = paramDate;
  }

  public void setTimescale(long paramLong)
  {
    this.timescale = paramLong;
  }

  public void setTrackId(long paramLong)
  {
    this.trackId = paramLong;
  }

  public void setWidth(double paramDouble)
  {
    this.width = paramDouble;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.authoring.TrackMetaData
 * JD-Core Version:    0.5.4
 */