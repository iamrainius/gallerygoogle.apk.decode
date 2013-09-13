package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class MediaHeaderBox extends AbstractFullBox
{
  private long creationTime;
  private long duration;
  private String language;
  private long modificationTime;
  private long timescale;

  public MediaHeaderBox()
  {
    super("mdhd");
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
      this.language = IsoTypeReader.readIso639(paramByteBuffer);
      IsoTypeReader.readUInt16(paramByteBuffer);
      return;
      this.creationTime = IsoTypeReader.readUInt32(paramByteBuffer);
      this.modificationTime = IsoTypeReader.readUInt32(paramByteBuffer);
      this.timescale = IsoTypeReader.readUInt32(paramByteBuffer);
    }
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
      IsoTypeWriter.writeIso639(paramByteBuffer, this.language);
      IsoTypeWriter.writeUInt16(paramByteBuffer, 0);
      return;
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.creationTime);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.modificationTime);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.timescale);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.duration);
    }
  }

  protected long getContentSize()
  {
    long l;
    if (getVersion() == 1)
      l = 4L + 28L;
    while (true)
    {
      return 2L + (l + 2L);
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

  public String getLanguage()
  {
    return this.language;
  }

  public long getModificationTime()
  {
    return this.modificationTime;
  }

  public long getTimescale()
  {
    return this.timescale;
  }

  public void setCreationTime(long paramLong)
  {
    this.creationTime = paramLong;
  }

  public void setDuration(long paramLong)
  {
    this.duration = paramLong;
  }

  public void setLanguage(String paramString)
  {
    this.language = paramString;
  }

  public void setTimescale(long paramLong)
  {
    this.timescale = paramLong;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("MediaHeaderBox[");
    localStringBuilder.append("creationTime=").append(getCreationTime());
    localStringBuilder.append(";");
    localStringBuilder.append("modificationTime=").append(getModificationTime());
    localStringBuilder.append(";");
    localStringBuilder.append("timescale=").append(getTimescale());
    localStringBuilder.append(";");
    localStringBuilder.append("duration=").append(getDuration());
    localStringBuilder.append(";");
    localStringBuilder.append("language=").append(getLanguage());
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.MediaHeaderBox
 * JD-Core Version:    0.5.4
 */