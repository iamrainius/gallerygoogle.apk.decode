package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class SchemeTypeBox extends AbstractFullBox
{
  String schemeType = "    ";
  String schemeUri = null;
  long schemeVersion;

  static
  {
    if (!SchemeTypeBox.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public SchemeTypeBox()
  {
    super("schm");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.schemeType = IsoTypeReader.read4cc(paramByteBuffer);
    this.schemeVersion = IsoTypeReader.readUInt32(paramByteBuffer);
    if ((0x1 & getFlags()) != 1)
      return;
    this.schemeUri = IsoTypeReader.readString(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.schemeType));
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.schemeVersion);
    if ((0x1 & getFlags()) != 1)
      return;
    paramByteBuffer.put(Utf8.convert(this.schemeUri));
  }

  protected long getContentSize()
  {
    if ((0x1 & getFlags()) == 1);
    for (int i = 1 + Utf8.utf8StringLengthInBytes(this.schemeUri); ; i = 0)
      return i + 12;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Schema Type Box[");
    localStringBuilder.append("schemeUri=").append(this.schemeUri).append("; ");
    localStringBuilder.append("schemeType=").append(this.schemeType).append("; ");
    localStringBuilder.append("schemeVersion=").append(this.schemeUri).append("; ");
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.SchemeTypeBox
 * JD-Core Version:    0.5.4
 */