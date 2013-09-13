package com.coremedia.iso.boxes.apple;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;

public class AppleDataReferenceBox extends AbstractFullBox
{
  private String dataReference;
  private int dataReferenceSize;
  private String dataReferenceType;

  public AppleDataReferenceBox()
  {
    super("rdrf");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.dataReferenceType = IsoTypeReader.read4cc(paramByteBuffer);
    this.dataReferenceSize = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.dataReference = IsoTypeReader.readString(paramByteBuffer, this.dataReferenceSize);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.dataReferenceType));
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.dataReferenceSize);
    paramByteBuffer.put(Utf8.convert(this.dataReference));
  }

  protected long getContentSize()
  {
    return 12 + this.dataReferenceSize;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.apple.AppleDataReferenceBox
 * JD-Core Version:    0.5.4
 */