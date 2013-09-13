package com.coremedia.iso.boxes.h264;

import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class AvcConfigurationBox extends AbstractBox
{
  public AVCDecoderConfigurationRecord avcDecoderConfigurationRecord = new AVCDecoderConfigurationRecord();

  public AvcConfigurationBox()
  {
    super("avcC");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.avcDecoderConfigurationRecord = new AVCDecoderConfigurationRecord(paramByteBuffer);
  }

  public void getContent(ByteBuffer paramByteBuffer)
  {
    this.avcDecoderConfigurationRecord.getContent(paramByteBuffer);
  }

  public long getContentSize()
  {
    return this.avcDecoderConfigurationRecord.getContentSize();
  }

  public static class AVCDecoderConfigurationRecord
  {
    public int avcLevelIndication;
    public int avcProfileIndication;
    public int bitDepthChromaMinus8 = 0;
    public int bitDepthChromaMinus8PaddingBits = 31;
    public int bitDepthLumaMinus8 = 0;
    public int bitDepthLumaMinus8PaddingBits = 31;
    public int chromaFormat = 1;
    public int chromaFormatPaddingBits = 31;
    public int configurationVersion;
    public boolean hasExts = true;
    public int lengthSizeMinusOne;
    public int lengthSizeMinusOnePaddingBits = 60;
    public int numberOfSequenceParameterSetsPaddingBits = 7;
    public List<byte[]> pictureParameterSets = new ArrayList();
    public int profileCompatibility;
    public List<byte[]> sequenceParameterSetExts = new ArrayList();
    public List<byte[]> sequenceParameterSets = new ArrayList();

    public AVCDecoderConfigurationRecord()
    {
    }

    public AVCDecoderConfigurationRecord(ByteBuffer paramByteBuffer)
    {
      this.configurationVersion = IsoTypeReader.readUInt8(paramByteBuffer);
      this.avcProfileIndication = IsoTypeReader.readUInt8(paramByteBuffer);
      this.profileCompatibility = IsoTypeReader.readUInt8(paramByteBuffer);
      this.avcLevelIndication = IsoTypeReader.readUInt8(paramByteBuffer);
      BitReaderBuffer localBitReaderBuffer1 = new BitReaderBuffer(paramByteBuffer);
      this.lengthSizeMinusOnePaddingBits = localBitReaderBuffer1.readBits(6);
      this.lengthSizeMinusOne = localBitReaderBuffer1.readBits(2);
      this.numberOfSequenceParameterSetsPaddingBits = localBitReaderBuffer1.readBits(3);
      int i = localBitReaderBuffer1.readBits(5);
      for (int j = 0; j < i; ++j)
      {
        byte[] arrayOfByte3 = new byte[IsoTypeReader.readUInt16(paramByteBuffer)];
        paramByteBuffer.get(arrayOfByte3);
        this.sequenceParameterSets.add(arrayOfByte3);
      }
      long l1 = IsoTypeReader.readUInt8(paramByteBuffer);
      for (int k = 0; k < l1; ++k)
      {
        byte[] arrayOfByte2 = new byte[IsoTypeReader.readUInt16(paramByteBuffer)];
        paramByteBuffer.get(arrayOfByte2);
        this.pictureParameterSets.add(arrayOfByte2);
      }
      if (paramByteBuffer.remaining() < 4)
        this.hasExts = false;
      if ((this.hasExts) && (((this.avcProfileIndication == 100) || (this.avcProfileIndication == 110) || (this.avcProfileIndication == 122) || (this.avcProfileIndication == 144))))
      {
        BitReaderBuffer localBitReaderBuffer2 = new BitReaderBuffer(paramByteBuffer);
        this.chromaFormatPaddingBits = localBitReaderBuffer2.readBits(6);
        this.chromaFormat = localBitReaderBuffer2.readBits(2);
        this.bitDepthLumaMinus8PaddingBits = localBitReaderBuffer2.readBits(5);
        this.bitDepthLumaMinus8 = localBitReaderBuffer2.readBits(3);
        this.bitDepthChromaMinus8PaddingBits = localBitReaderBuffer2.readBits(5);
        this.bitDepthChromaMinus8 = localBitReaderBuffer2.readBits(3);
        long l2 = IsoTypeReader.readUInt8(paramByteBuffer);
        for (int l = 0; ; ++l)
        {
          if (l >= l2)
            return;
          byte[] arrayOfByte1 = new byte[IsoTypeReader.readUInt16(paramByteBuffer)];
          paramByteBuffer.get(arrayOfByte1);
          this.sequenceParameterSetExts.add(arrayOfByte1);
        }
      }
      this.chromaFormat = -1;
      this.bitDepthLumaMinus8 = -1;
      this.bitDepthChromaMinus8 = -1;
    }

    public void getContent(ByteBuffer paramByteBuffer)
    {
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.configurationVersion);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.avcProfileIndication);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.profileCompatibility);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.avcLevelIndication);
      BitWriterBuffer localBitWriterBuffer1 = new BitWriterBuffer(paramByteBuffer);
      localBitWriterBuffer1.writeBits(this.lengthSizeMinusOnePaddingBits, 6);
      localBitWriterBuffer1.writeBits(this.lengthSizeMinusOne, 2);
      localBitWriterBuffer1.writeBits(this.numberOfSequenceParameterSetsPaddingBits, 3);
      localBitWriterBuffer1.writeBits(this.pictureParameterSets.size(), 5);
      Iterator localIterator1 = this.sequenceParameterSets.iterator();
      while (localIterator1.hasNext())
      {
        byte[] arrayOfByte3 = (byte[])localIterator1.next();
        IsoTypeWriter.writeUInt16(paramByteBuffer, arrayOfByte3.length);
        paramByteBuffer.put(arrayOfByte3);
      }
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.pictureParameterSets.size());
      Iterator localIterator2 = this.pictureParameterSets.iterator();
      while (localIterator2.hasNext())
      {
        byte[] arrayOfByte2 = (byte[])localIterator2.next();
        IsoTypeWriter.writeUInt16(paramByteBuffer, arrayOfByte2.length);
        paramByteBuffer.put(arrayOfByte2);
      }
      if ((!this.hasExts) || ((this.avcProfileIndication != 100) && (this.avcProfileIndication != 110) && (this.avcProfileIndication != 122) && (this.avcProfileIndication != 144)))
        return;
      BitWriterBuffer localBitWriterBuffer2 = new BitWriterBuffer(paramByteBuffer);
      localBitWriterBuffer2.writeBits(this.chromaFormatPaddingBits, 6);
      localBitWriterBuffer2.writeBits(this.chromaFormat, 2);
      localBitWriterBuffer2.writeBits(this.bitDepthLumaMinus8PaddingBits, 5);
      localBitWriterBuffer2.writeBits(this.bitDepthLumaMinus8, 3);
      localBitWriterBuffer2.writeBits(this.bitDepthChromaMinus8PaddingBits, 5);
      localBitWriterBuffer2.writeBits(this.bitDepthChromaMinus8, 3);
      Iterator localIterator3 = this.sequenceParameterSetExts.iterator();
      while (localIterator3.hasNext())
      {
        byte[] arrayOfByte1 = (byte[])localIterator3.next();
        IsoTypeWriter.writeUInt16(paramByteBuffer, arrayOfByte1.length);
        paramByteBuffer.put(arrayOfByte1);
      }
    }

    public long getContentSize()
    {
      long l1 = 5L + 1L;
      Iterator localIterator1 = this.sequenceParameterSets.iterator();
      while (localIterator1.hasNext())
      {
        byte[] arrayOfByte3 = (byte[])localIterator1.next();
        l1 = l1 + 2L + arrayOfByte3.length;
      }
      long l2 = l1 + 1L;
      Iterator localIterator2 = this.pictureParameterSets.iterator();
      while (localIterator2.hasNext())
      {
        byte[] arrayOfByte2 = (byte[])localIterator2.next();
        l2 = l2 + 2L + arrayOfByte2.length;
      }
      if ((this.hasExts) && (((this.avcProfileIndication == 100) || (this.avcProfileIndication == 110) || (this.avcProfileIndication == 122) || (this.avcProfileIndication == 144))))
      {
        l2 += 4L;
        Iterator localIterator3 = this.sequenceParameterSetExts.iterator();
        while (localIterator3.hasNext())
        {
          byte[] arrayOfByte1 = (byte[])localIterator3.next();
          l2 = l2 + 2L + arrayOfByte1.length;
        }
      }
      return l2;
    }

    public List<String> getPictureParameterSetsAsStrings()
    {
      ArrayList localArrayList = new ArrayList(this.pictureParameterSets.size());
      Iterator localIterator = this.pictureParameterSets.iterator();
      while (localIterator.hasNext())
        localArrayList.add(Hex.encodeHex((byte[])localIterator.next()));
      return localArrayList;
    }

    public List<String> getSequenceParameterSetsAsStrings()
    {
      ArrayList localArrayList = new ArrayList(this.sequenceParameterSets.size());
      Iterator localIterator = this.sequenceParameterSets.iterator();
      while (localIterator.hasNext())
        localArrayList.add(Hex.encodeHex((byte[])localIterator.next()));
      return localArrayList;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.h264.AvcConfigurationBox
 * JD-Core Version:    0.5.4
 */