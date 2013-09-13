package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class MovieHeaderBox extends AbstractFullBox
{
  private long creationTime;
  private int currentTime;
  private long duration;
  private long[] matrix = { 65536L, 0L, 0L, 0L, 65536L, 0L, 0L, 0L, 1073741824L };
  private long modificationTime;
  private long nextTrackId;
  private int posterTime;
  private int previewDuration;
  private int previewTime;
  private double rate = 1.0D;
  private int selectionDuration;
  private int selectionTime;
  private long timescale;
  private float volume = 1.0F;

  public MovieHeaderBox()
  {
    super("mvhd");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1)
    {
      this.creationTime = IsoTypeReader.readUInt64(paramByteBuffer);
      this.modificationTime = IsoTypeReader.readUInt64(paramByteBuffer);
      this.timescale = IsoTypeReader.readUInt32(paramByteBuffer);
    }
    for (this.duration = IsoTypeReader.readUInt64(paramByteBuffer); ; this.duration = IsoTypeReader.readUInt32(paramByteBuffer))
    {
      this.rate = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
      this.volume = IsoTypeReader.readFixedPoint88(paramByteBuffer);
      IsoTypeReader.readUInt16(paramByteBuffer);
      IsoTypeReader.readUInt32(paramByteBuffer);
      IsoTypeReader.readUInt32(paramByteBuffer);
      this.matrix = new long[9];
      for (int i = 0; ; ++i)
      {
        if (i >= 9)
          break label147;
        this.matrix[i] = IsoTypeReader.readUInt32(paramByteBuffer);
      }
      this.creationTime = IsoTypeReader.readUInt32(paramByteBuffer);
      this.modificationTime = IsoTypeReader.readUInt32(paramByteBuffer);
      this.timescale = IsoTypeReader.readUInt32(paramByteBuffer);
    }
    label147: this.previewTime = paramByteBuffer.getInt();
    this.previewDuration = paramByteBuffer.getInt();
    this.posterTime = paramByteBuffer.getInt();
    this.selectionTime = paramByteBuffer.getInt();
    this.selectionDuration = paramByteBuffer.getInt();
    this.currentTime = paramByteBuffer.getInt();
    this.nextTrackId = IsoTypeReader.readUInt32(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1)
    {
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.creationTime);
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.modificationTime);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.timescale);
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.duration);
    }
    while (true)
    {
      IsoTypeWriter.writeFixedPont1616(paramByteBuffer, this.rate);
      IsoTypeWriter.writeFixedPont88(paramByteBuffer, this.volume);
      IsoTypeWriter.writeUInt16(paramByteBuffer, 0);
      IsoTypeWriter.writeUInt32(paramByteBuffer, 0L);
      IsoTypeWriter.writeUInt32(paramByteBuffer, 0L);
      for (int i = 0; ; ++i)
      {
        if (i >= 9)
          break label136;
        IsoTypeWriter.writeUInt32(paramByteBuffer, this.matrix[i]);
      }
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.creationTime);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.modificationTime);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.timescale);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.duration);
    }
    label136: paramByteBuffer.putInt(this.previewTime);
    paramByteBuffer.putInt(this.previewDuration);
    paramByteBuffer.putInt(this.posterTime);
    paramByteBuffer.putInt(this.selectionTime);
    paramByteBuffer.putInt(this.selectionDuration);
    paramByteBuffer.putInt(this.currentTime);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.nextTrackId);
  }

  protected long getContentSize()
  {
    long l;
    if (getVersion() == 1)
      l = 4L + 28L;
    while (true)
    {
      return l + 80L;
      l = 4L + 16L;
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

  public long getModificationTime()
  {
    return this.modificationTime;
  }

  public long getNextTrackId()
  {
    return this.nextTrackId;
  }

  public double getRate()
  {
    return this.rate;
  }

  public long getTimescale()
  {
    return this.timescale;
  }

  public float getVolume()
  {
    return this.volume;
  }

  public void setCreationTime(long paramLong)
  {
    this.creationTime = paramLong;
  }

  public void setDuration(long paramLong)
  {
    this.duration = paramLong;
  }

  public void setModificationTime(long paramLong)
  {
    this.modificationTime = paramLong;
  }

  public void setNextTrackId(long paramLong)
  {
    this.nextTrackId = paramLong;
  }

  public void setTimescale(long paramLong)
  {
    this.timescale = paramLong;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("MovieHeaderBox[");
    localStringBuilder.append("creationTime=").append(getCreationTime());
    localStringBuilder.append(";");
    localStringBuilder.append("modificationTime=").append(getModificationTime());
    localStringBuilder.append(";");
    localStringBuilder.append("timescale=").append(getTimescale());
    localStringBuilder.append(";");
    localStringBuilder.append("duration=").append(getDuration());
    localStringBuilder.append(";");
    localStringBuilder.append("rate=").append(getRate());
    localStringBuilder.append(";");
    localStringBuilder.append("volume=").append(getVolume());
    for (int i = 0; i < this.matrix.length; ++i)
    {
      localStringBuilder.append(";");
      localStringBuilder.append("matrix").append(i).append("=").append(this.matrix[i]);
    }
    localStringBuilder.append(";");
    localStringBuilder.append("nextTrackId=").append(getNextTrackId());
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.MovieHeaderBox
 * JD-Core Version:    0.5.4
 */