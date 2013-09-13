package com.googlecode.mp4parser.boxes;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import java.nio.ByteBuffer;

public class MLPSpecificBox extends AbstractBox
{
  int format_info;
  int peak_data_rate;
  int reserved;
  int reserved2;

  public MLPSpecificBox()
  {
    super("dmlp");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    BitReaderBuffer localBitReaderBuffer = new BitReaderBuffer(paramByteBuffer);
    this.format_info = localBitReaderBuffer.readBits(32);
    this.peak_data_rate = localBitReaderBuffer.readBits(15);
    this.reserved = localBitReaderBuffer.readBits(1);
    this.reserved2 = localBitReaderBuffer.readBits(32);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    BitWriterBuffer localBitWriterBuffer = new BitWriterBuffer(paramByteBuffer);
    localBitWriterBuffer.writeBits(this.format_info, 32);
    localBitWriterBuffer.writeBits(this.peak_data_rate, 15);
    localBitWriterBuffer.writeBits(this.reserved, 1);
    localBitWriterBuffer.writeBits(this.reserved2, 32);
  }

  protected long getContentSize()
  {
    return 10L;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.MLPSpecificBox
 * JD-Core Version:    0.5.4
 */