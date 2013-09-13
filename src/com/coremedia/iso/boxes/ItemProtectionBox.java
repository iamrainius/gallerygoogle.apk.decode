package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.FullContainerBox;
import java.nio.ByteBuffer;
import java.util.List;

public class ItemProtectionBox extends FullContainerBox
{
  public ItemProtectionBox()
  {
    super("ipro");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    IsoTypeReader.readUInt16(paramByteBuffer);
    parseChildBoxes(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt16(paramByteBuffer, getBoxes().size());
    writeChildBoxes(paramByteBuffer);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.ItemProtectionBox
 * JD-Core Version:    0.5.4
 */