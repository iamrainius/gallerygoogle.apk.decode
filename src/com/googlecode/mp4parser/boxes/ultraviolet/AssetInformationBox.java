package com.googlecode.mp4parser.boxes.ultraviolet;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class AssetInformationBox extends AbstractFullBox
{
  String apid = "";
  String profileVersion = "0000";

  static
  {
    if (!AssetInformationBox.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public AssetInformationBox()
  {
    super("ainf");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.profileVersion = IsoTypeReader.readString(paramByteBuffer, 4);
    this.apid = IsoTypeReader.readString(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(Utf8.convert(this.profileVersion), 0, 4);
    paramByteBuffer.put(Utf8.convert(this.apid));
    paramByteBuffer.put(0);
  }

  protected long getContentSize()
  {
    return 9 + Utf8.utf8StringLengthInBytes(this.apid);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.ultraviolet.AssetInformationBox
 * JD-Core Version:    0.5.4
 */