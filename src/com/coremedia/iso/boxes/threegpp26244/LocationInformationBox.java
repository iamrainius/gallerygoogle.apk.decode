package com.coremedia.iso.boxes.threegpp26244;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class LocationInformationBox extends AbstractFullBox
{
  private String additionalNotes = "";
  private double altitude;
  private String astronomicalBody = "";
  private String language;
  private double latitude;
  private double longitude;
  private String name = "";
  private int role;

  public LocationInformationBox()
  {
    super("loci");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.language = IsoTypeReader.readIso639(paramByteBuffer);
    this.name = IsoTypeReader.readString(paramByteBuffer);
    this.role = IsoTypeReader.readUInt8(paramByteBuffer);
    this.longitude = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
    this.latitude = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
    this.altitude = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
    this.astronomicalBody = IsoTypeReader.readString(paramByteBuffer);
    this.additionalNotes = IsoTypeReader.readString(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeIso639(paramByteBuffer, this.language);
    paramByteBuffer.put(Utf8.convert(this.name));
    paramByteBuffer.put(0);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.role);
    IsoTypeWriter.writeFixedPont1616(paramByteBuffer, this.longitude);
    IsoTypeWriter.writeFixedPont1616(paramByteBuffer, this.latitude);
    IsoTypeWriter.writeFixedPont1616(paramByteBuffer, this.altitude);
    paramByteBuffer.put(Utf8.convert(this.astronomicalBody));
    paramByteBuffer.put(0);
    paramByteBuffer.put(Utf8.convert(this.additionalNotes));
    paramByteBuffer.put(0);
  }

  protected long getContentSize()
  {
    return 22 + Utf8.convert(this.name).length + Utf8.convert(this.astronomicalBody).length + Utf8.convert(this.additionalNotes).length;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.threegpp26244.LocationInformationBox
 * JD-Core Version:    0.5.4
 */