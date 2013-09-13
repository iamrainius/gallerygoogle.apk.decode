package com.googlecode.mp4parser.boxes.apple;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class BaseMediaInfoAtom extends AbstractFullBox
{
  short balance;
  short graphicsMode = 64;
  int opColorB = 32768;
  int opColorG = 32768;
  int opColorR = 32768;
  short reserved;

  public BaseMediaInfoAtom()
  {
    super("gmin");
  }

  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.graphicsMode = paramByteBuffer.getShort();
    this.opColorR = IsoTypeReader.readUInt16(paramByteBuffer);
    this.opColorG = IsoTypeReader.readUInt16(paramByteBuffer);
    this.opColorB = IsoTypeReader.readUInt16(paramByteBuffer);
    this.balance = paramByteBuffer.getShort();
    this.reserved = paramByteBuffer.getShort();
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.putShort(this.graphicsMode);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.opColorR);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.opColorG);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.opColorB);
    paramByteBuffer.putShort(this.balance);
    paramByteBuffer.putShort(this.reserved);
  }

  protected long getContentSize()
  {
    return 16L;
  }

  public String toString()
  {
    return "BaseMediaInfoAtom{graphicsMode=" + this.graphicsMode + ", opColorR=" + this.opColorR + ", opColorG=" + this.opColorG + ", opColorB=" + this.opColorB + ", balance=" + this.balance + ", reserved=" + this.reserved + '}';
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.apple.BaseMediaInfoAtom
 * JD-Core Version:    0.5.4
 */