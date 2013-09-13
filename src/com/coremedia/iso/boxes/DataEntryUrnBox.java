package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class DataEntryUrnBox extends AbstractFullBox
{
  private String location;
  private String name;

  public DataEntryUrnBox()
  {
    super("urn ");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.name = IsoTypeReader.readString(paramByteBuffer);
    this.location = IsoTypeReader.readString(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(Utf8.convert(this.name));
    paramByteBuffer.put(0);
    paramByteBuffer.put(Utf8.convert(this.location));
    paramByteBuffer.put(0);
  }

  protected long getContentSize()
  {
    return 1 + (1 + Utf8.utf8StringLengthInBytes(this.name) + Utf8.utf8StringLengthInBytes(this.location));
  }

  public String getLocation()
  {
    return this.location;
  }

  public String getName()
  {
    return this.name;
  }

  public String toString()
  {
    return "DataEntryUrlBox[name=" + getName() + ";location=" + getLocation() + "]";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.DataEntryUrnBox
 * JD-Core Version:    0.5.4
 */