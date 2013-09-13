package com.googlecode.mp4parser.boxes.basemediaformat;

import com.coremedia.iso.boxes.h264.AvcConfigurationBox.AVCDecoderConfigurationRecord;
import com.googlecode.mp4parser.AbstractBox;
import java.nio.ByteBuffer;

public class AvcNalUnitStorageBox extends AbstractBox
{
  AvcConfigurationBox.AVCDecoderConfigurationRecord avcDecoderConfigurationRecord;

  public AvcNalUnitStorageBox()
  {
    super("avcn");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.avcDecoderConfigurationRecord = new AvcConfigurationBox.AVCDecoderConfigurationRecord(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    this.avcDecoderConfigurationRecord.getContent(paramByteBuffer);
  }

  protected long getContentSize()
  {
    return this.avcDecoderConfigurationRecord.getContentSize();
  }

  public String toString()
  {
    return "AvcNalUnitStorageBox{SPS=" + this.avcDecoderConfigurationRecord.getSequenceParameterSetsAsStrings() + ",PPS=" + this.avcDecoderConfigurationRecord.getPictureParameterSetsAsStrings() + ",lengthSize=" + (1 + this.avcDecoderConfigurationRecord.lengthSizeMinusOne) + '}';
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.basemediaformat.AvcNalUnitStorageBox
 * JD-Core Version:    0.5.4
 */