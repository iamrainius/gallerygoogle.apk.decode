package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class TrackHeaderBox extends AbstractFullBox
{
  private int alternateGroup;
  private long creationTime;
  private long duration;
  private double height;
  private int layer;
  private long[] matrix = { 65536L, 0L, 0L, 0L, 65536L, 0L, 0L, 0L, 1073741824L };
  private long modificationTime;
  private long trackId;
  private float volume;
  private double width;

  public TrackHeaderBox()
  {
    super("tkhd");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1)
    {
      this.creationTime = IsoTypeReader.readUInt64(paramByteBuffer);
      this.modificationTime = IsoTypeReader.readUInt64(paramByteBuffer);
      this.trackId = IsoTypeReader.readUInt32(paramByteBuffer);
      IsoTypeReader.readUInt32(paramByteBuffer);
    }
    for (this.duration = IsoTypeReader.readUInt64(paramByteBuffer); ; this.duration = IsoTypeReader.readUInt32(paramByteBuffer))
    {
      IsoTypeReader.readUInt32(paramByteBuffer);
      IsoTypeReader.readUInt32(paramByteBuffer);
      this.layer = IsoTypeReader.readUInt16(paramByteBuffer);
      this.alternateGroup = IsoTypeReader.readUInt16(paramByteBuffer);
      this.volume = IsoTypeReader.readFixedPoint88(paramByteBuffer);
      IsoTypeReader.readUInt16(paramByteBuffer);
      this.matrix = new long[9];
      for (int i = 0; ; ++i)
      {
        if (i >= 9)
          break label165;
        this.matrix[i] = IsoTypeReader.readUInt32(paramByteBuffer);
      }
      this.creationTime = IsoTypeReader.readUInt32(paramByteBuffer);
      this.modificationTime = IsoTypeReader.readUInt32(paramByteBuffer);
      this.trackId = IsoTypeReader.readUInt32(paramByteBuffer);
      IsoTypeReader.readUInt32(paramByteBuffer);
    }
    label165: this.width = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
    this.height = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
  }

  public int getAlternateGroup()
  {
    return this.alternateGroup;
  }

  public void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1)
    {
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.creationTime);
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.modificationTime);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.trackId);
      IsoTypeWriter.writeUInt32(paramByteBuffer, 0L);
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.duration);
    }
    while (true)
    {
      IsoTypeWriter.writeUInt32(paramByteBuffer, 0L);
      IsoTypeWriter.writeUInt32(paramByteBuffer, 0L);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.layer);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.alternateGroup);
      IsoTypeWriter.writeFixedPont88(paramByteBuffer, this.volume);
      IsoTypeWriter.writeUInt16(paramByteBuffer, 0);
      for (int i = 0; ; ++i)
      {
        if (i >= 9)
          break label154;
        IsoTypeWriter.writeUInt32(paramByteBuffer, this.matrix[i]);
      }
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.creationTime);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.modificationTime);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.trackId);
      IsoTypeWriter.writeUInt32(paramByteBuffer, 0L);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.duration);
    }
    label154: IsoTypeWriter.writeFixedPont1616(paramByteBuffer, this.width);
    IsoTypeWriter.writeFixedPont1616(paramByteBuffer, this.height);
  }

  protected long getContentSize()
  {
    long l;
    if (getVersion() == 1)
      l = 4L + 32L;
    while (true)
    {
      return l + 60L;
      l = 4L + 20L;
    }
  }

  public long getCreationTime()
  {
    return this.creationTime;
  }

  public long getDuration()
  {
    return this.duration;
  }

  public double getHeight()
  {
    return this.height;
  }

  public int getLayer()
  {
    return this.layer;
  }

  public long[] getMatrix()
  {
    return this.matrix;
  }

  public long getModificationTime()
  {
    return this.modificationTime;
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

  public boolean isEnabled()
  {
    return (0x1 & getFlags()) > 0;
  }

  public boolean isInMovie()
  {
    return (0x2 & getFlags()) > 0;
  }

  public boolean isInPoster()
  {
    return (0x8 & getFlags()) > 0;
  }

  public boolean isInPreview()
  {
    return (0x4 & getFlags()) > 0;
  }

  public void setAlternateGroup(int paramInt)
  {
    this.alternateGroup = paramInt;
  }

  public void setCreationTime(long paramLong)
  {
    this.creationTime = paramLong;
  }

  public void setDuration(long paramLong)
  {
    this.duration = paramLong;
  }

  public void setHeight(double paramDouble)
  {
    this.height = paramDouble;
  }

  public void setLayer(int paramInt)
  {
    this.layer = paramInt;
  }

  public void setMatrix(long[] paramArrayOfLong)
  {
    this.matrix = paramArrayOfLong;
  }

  public void setModificationTime(long paramLong)
  {
    this.modificationTime = paramLong;
  }

  public void setTrackId(long paramLong)
  {
    this.trackId = paramLong;
  }

  public void setVolume(float paramFloat)
  {
    this.volume = paramFloat;
  }

  public void setWidth(double paramDouble)
  {
    this.width = paramDouble;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("TrackHeaderBox[");
    localStringBuilder.append("creationTime=").append(getCreationTime());
    localStringBuilder.append(";");
    localStringBuilder.append("modificationTime=").append(getModificationTime());
    localStringBuilder.append(";");
    localStringBuilder.append("trackId=").append(getTrackId());
    localStringBuilder.append(";");
    localStringBuilder.append("duration=").append(getDuration());
    localStringBuilder.append(";");
    localStringBuilder.append("layer=").append(getLayer());
    localStringBuilder.append(";");
    localStringBuilder.append("alternateGroup=").append(getAlternateGroup());
    localStringBuilder.append(";");
    localStringBuilder.append("volume=").append(getVolume());
    for (int i = 0; i < this.matrix.length; ++i)
    {
      localStringBuilder.append(";");
      localStringBuilder.append("matrix").append(i).append("=").append(this.matrix[i]);
    }
    localStringBuilder.append(";");
    localStringBuilder.append("width=").append(getWidth());
    localStringBuilder.append(";");
    localStringBuilder.append("height=").append(getHeight());
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.TrackHeaderBox
 * JD-Core Version:    0.5.4
 */