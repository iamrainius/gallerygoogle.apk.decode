package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class AlbumBox extends AbstractFullBox
{
  private String albumTitle;
  private String language;
  private int trackNumber;

  public AlbumBox()
  {
    super("albm");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.language = IsoTypeReader.readIso639(paramByteBuffer);
    this.albumTitle = IsoTypeReader.readString(paramByteBuffer);
    if (paramByteBuffer.remaining() > 0)
    {
      this.trackNumber = IsoTypeReader.readUInt8(paramByteBuffer);
      return;
    }
    this.trackNumber = -1;
  }

  public String getAlbumTitle()
  {
    return this.albumTitle;
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeIso639(paramByteBuffer, this.language);
    paramByteBuffer.put(Utf8.convert(this.albumTitle));
    paramByteBuffer.put(0);
    if (this.trackNumber == -1)
      return;
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.trackNumber);
  }

  protected long getContentSize()
  {
    int i = 1 + (6 + Utf8.utf8StringLengthInBytes(this.albumTitle));
    if (this.trackNumber == -1);
    for (int j = 0; ; j = 1)
      return j + i;
  }

  public String getLanguage()
  {
    return this.language;
  }

  public int getTrackNumber()
  {
    return this.trackNumber;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("AlbumBox[language=").append(getLanguage()).append(";");
    localStringBuilder.append("albumTitle=").append(getAlbumTitle());
    if (this.trackNumber >= 0)
      localStringBuilder.append(";trackNumber=").append(getTrackNumber());
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.AlbumBox
 * JD-Core Version:    0.5.4
 */