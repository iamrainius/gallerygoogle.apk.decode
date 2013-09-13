package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.nio.ByteBuffer;

public class SoundMediaHeaderBox extends AbstractMediaHeaderBox
{
  private float balance;

  public SoundMediaHeaderBox()
  {
    super("smhd");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.balance = IsoTypeReader.readFixedPoint88(paramByteBuffer);
    IsoTypeReader.readUInt16(paramByteBuffer);
  }

  public float getBalance()
  {
    return this.balance;
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeFixedPont88(paramByteBuffer, this.balance);
    IsoTypeWriter.writeUInt16(paramByteBuffer, 0);
  }

  protected long getContentSize()
  {
    return 8L;
  }

  public String toString()
  {
    return "SoundMediaHeaderBox[balance=" + getBalance() + "]";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.SoundMediaHeaderBox
 * JD-Core Version:    0.5.4
 */