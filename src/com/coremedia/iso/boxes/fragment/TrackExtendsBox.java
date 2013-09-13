package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class TrackExtendsBox extends AbstractFullBox
{
  private long defaultSampleDescriptionIndex;
  private long defaultSampleDuration;
  private SampleFlags defaultSampleFlags;
  private long defaultSampleSize;
  private long trackId;

  public TrackExtendsBox()
  {
    super("trex");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.trackId = IsoTypeReader.readUInt32(paramByteBuffer);
    this.defaultSampleDescriptionIndex = IsoTypeReader.readUInt32(paramByteBuffer);
    this.defaultSampleDuration = IsoTypeReader.readUInt32(paramByteBuffer);
    this.defaultSampleSize = IsoTypeReader.readUInt32(paramByteBuffer);
    this.defaultSampleFlags = new SampleFlags(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.trackId);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.defaultSampleDescriptionIndex);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.defaultSampleDuration);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.defaultSampleSize);
    this.defaultSampleFlags.getContent(paramByteBuffer);
  }

  protected long getContentSize()
  {
    return 24L;
  }

  public long getDefaultSampleDuration()
  {
    return this.defaultSampleDuration;
  }

  public SampleFlags getDefaultSampleFlags()
  {
    return this.defaultSampleFlags;
  }

  public long getDefaultSampleSize()
  {
    return this.defaultSampleSize;
  }

  public long getTrackId()
  {
    return this.trackId;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.fragment.TrackExtendsBox
 * JD-Core Version:    0.5.4
 */