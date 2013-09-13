package com.googlecode.mp4parser.boxes;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import java.nio.ByteBuffer;

public class AC3SpecificBox extends AbstractBox
{
  int acmod;
  int bitRateCode;
  int bsid;
  int bsmod;
  int fscod;
  int lfeon;
  int reserved;

  public AC3SpecificBox()
  {
    super("dac3");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    BitReaderBuffer localBitReaderBuffer = new BitReaderBuffer(paramByteBuffer);
    this.fscod = localBitReaderBuffer.readBits(2);
    this.bsid = localBitReaderBuffer.readBits(5);
    this.bsmod = localBitReaderBuffer.readBits(3);
    this.acmod = localBitReaderBuffer.readBits(3);
    this.lfeon = localBitReaderBuffer.readBits(1);
    this.bitRateCode = localBitReaderBuffer.readBits(5);
    this.reserved = localBitReaderBuffer.readBits(5);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    BitWriterBuffer localBitWriterBuffer = new BitWriterBuffer(paramByteBuffer);
    localBitWriterBuffer.writeBits(this.fscod, 2);
    localBitWriterBuffer.writeBits(this.bsid, 5);
    localBitWriterBuffer.writeBits(this.bsmod, 3);
    localBitWriterBuffer.writeBits(this.acmod, 3);
    localBitWriterBuffer.writeBits(this.lfeon, 1);
    localBitWriterBuffer.writeBits(this.bitRateCode, 5);
    localBitWriterBuffer.writeBits(this.reserved, 5);
  }

  protected long getContentSize()
  {
    return 3L;
  }

  public String toString()
  {
    return "AC3SpecificBox{fscod=" + this.fscod + ", bsid=" + this.bsid + ", bsmod=" + this.bsmod + ", acmod=" + this.acmod + ", lfeon=" + this.lfeon + ", bitRateCode=" + this.bitRateCode + ", reserved=" + this.reserved + '}';
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.AC3SpecificBox
 * JD-Core Version:    0.5.4
 */