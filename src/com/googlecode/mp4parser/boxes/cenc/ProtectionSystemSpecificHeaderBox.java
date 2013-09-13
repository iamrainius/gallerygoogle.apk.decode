package com.googlecode.mp4parser.boxes.cenc;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.util.UUIDConverter;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ProtectionSystemSpecificHeaderBox extends AbstractFullBox
{
  public static byte[] OMA2_SYSTEM_ID;
  public static byte[] PLAYREADY_SYSTEM_ID;
  byte[] content;
  byte[] systemId;

  static
  {
    if (!ProtectionSystemSpecificHeaderBox.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      OMA2_SYSTEM_ID = UUIDConverter.convert(UUID.fromString("A2B55680-6F43-11E0-9A3F-0002A5D5C51B"));
      PLAYREADY_SYSTEM_ID = UUIDConverter.convert(UUID.fromString("9A04F079-9840-4286-AB92-E65BE0885F95"));
      return;
    }
  }

  public ProtectionSystemSpecificHeaderBox()
  {
    super("pssh");
  }

  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.systemId = new byte[16];
    paramByteBuffer.get(this.systemId);
    long l = IsoTypeReader.readUInt32(paramByteBuffer);
    this.content = new byte[paramByteBuffer.remaining()];
    paramByteBuffer.get(this.content);
    assert (l == this.content.length);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    assert (this.systemId.length == 16);
    paramByteBuffer.put(this.systemId, 0, 16);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.content.length);
    paramByteBuffer.put(this.content);
  }

  protected long getContentSize()
  {
    return 24 + this.content.length;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.cenc.ProtectionSystemSpecificHeaderBox
 * JD-Core Version:    0.5.4
 */