package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class XmlBox extends AbstractFullBox
{
  String xml = "";

  public XmlBox()
  {
    super("xml ");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.xml = IsoTypeReader.readString(paramByteBuffer, paramByteBuffer.remaining());
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(Utf8.convert(this.xml));
  }

  protected long getContentSize()
  {
    return 4 + Utf8.utf8StringLengthInBytes(this.xml);
  }

  public String toString()
  {
    return "XmlBox{xml='" + this.xml + '\'' + '}';
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.XmlBox
 * JD-Core Version:    0.5.4
 */