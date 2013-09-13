package com.coremedia.iso.boxes.vodafone;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class AlbumArtistBox extends AbstractFullBox
{
  private String albumArtist;
  private String language;

  public AlbumArtistBox()
  {
    super("albr");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.language = IsoTypeReader.readIso639(paramByteBuffer);
    this.albumArtist = IsoTypeReader.readString(paramByteBuffer);
  }

  public String getAlbumArtist()
  {
    return this.albumArtist;
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeIso639(paramByteBuffer, this.language);
    paramByteBuffer.put(Utf8.convert(this.albumArtist));
    paramByteBuffer.put(0);
  }

  protected long getContentSize()
  {
    return 1 + (6 + Utf8.utf8StringLengthInBytes(this.albumArtist));
  }

  public String getLanguage()
  {
    return this.language;
  }

  public String toString()
  {
    return "AlbumArtistBox[language=" + getLanguage() + ";albumArtist=" + getAlbumArtist() + "]";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.vodafone.AlbumArtistBox
 * JD-Core Version:    0.5.4
 */