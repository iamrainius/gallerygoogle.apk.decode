package com.coremedia.iso.boxes.fragment;

import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import java.nio.ByteBuffer;

public class SampleFlags
{
  private int reserved;
  private int sampleDegradationPriority;
  private int sampleDependsOn;
  private int sampleHasRedundancy;
  private int sampleIsDependedOn;
  private boolean sampleIsDifferenceSample;
  private int samplePaddingValue;

  public SampleFlags()
  {
  }

  public SampleFlags(ByteBuffer paramByteBuffer)
  {
    BitReaderBuffer localBitReaderBuffer = new BitReaderBuffer(paramByteBuffer);
    this.reserved = localBitReaderBuffer.readBits(6);
    this.sampleDependsOn = localBitReaderBuffer.readBits(2);
    this.sampleIsDependedOn = localBitReaderBuffer.readBits(2);
    this.sampleHasRedundancy = localBitReaderBuffer.readBits(2);
    this.samplePaddingValue = localBitReaderBuffer.readBits(3);
    if (localBitReaderBuffer.readBits(i) == i);
    while (true)
    {
      this.sampleIsDifferenceSample = i;
      this.sampleDegradationPriority = localBitReaderBuffer.readBits(16);
      return;
      i = 0;
    }
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    SampleFlags localSampleFlags;
    do
    {
      return true;
      if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
        return false;
      localSampleFlags = (SampleFlags)paramObject;
      if (this.reserved != localSampleFlags.reserved)
        return false;
      if (this.sampleDegradationPriority != localSampleFlags.sampleDegradationPriority)
        return false;
      if (this.sampleDependsOn != localSampleFlags.sampleDependsOn)
        return false;
      if (this.sampleHasRedundancy != localSampleFlags.sampleHasRedundancy)
        return false;
      if (this.sampleIsDependedOn != localSampleFlags.sampleIsDependedOn)
        return false;
      if (this.sampleIsDifferenceSample != localSampleFlags.sampleIsDifferenceSample)
        return false;
    }
    while (this.samplePaddingValue == localSampleFlags.samplePaddingValue);
    return false;
  }

  public void getContent(ByteBuffer paramByteBuffer)
  {
    BitWriterBuffer localBitWriterBuffer = new BitWriterBuffer(paramByteBuffer);
    localBitWriterBuffer.writeBits(this.reserved, 6);
    localBitWriterBuffer.writeBits(this.sampleDependsOn, 2);
    localBitWriterBuffer.writeBits(this.sampleIsDependedOn, 2);
    localBitWriterBuffer.writeBits(this.sampleHasRedundancy, 2);
    localBitWriterBuffer.writeBits(this.samplePaddingValue, 3);
    if (this.sampleIsDifferenceSample);
    for (int i = 1; ; i = 0)
    {
      localBitWriterBuffer.writeBits(i, 1);
      localBitWriterBuffer.writeBits(this.sampleDegradationPriority, 16);
      return;
    }
  }

  public int hashCode()
  {
    int i = 31 * (31 * (31 * (31 * (31 * this.reserved + this.sampleDependsOn) + this.sampleIsDependedOn) + this.sampleHasRedundancy) + this.samplePaddingValue);
    if (this.sampleIsDifferenceSample);
    for (int j = 1; ; j = 0)
      return 31 * (i + j) + this.sampleDegradationPriority;
  }

  public boolean isSampleIsDifferenceSample()
  {
    return this.sampleIsDifferenceSample;
  }

  public String toString()
  {
    return "SampleFlags{reserved=" + this.reserved + ", sampleDependsOn=" + this.sampleDependsOn + ", sampleHasRedundancy=" + this.sampleHasRedundancy + ", samplePaddingValue=" + this.samplePaddingValue + ", sampleIsDifferenceSample=" + this.sampleIsDifferenceSample + ", sampleDegradationPriority=" + this.sampleDegradationPriority + '}';
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.fragment.SampleFlags
 * JD-Core Version:    0.5.4
 */