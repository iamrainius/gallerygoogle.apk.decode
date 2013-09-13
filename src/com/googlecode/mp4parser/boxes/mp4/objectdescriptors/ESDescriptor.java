package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Descriptor(tags={3})
public class ESDescriptor extends BaseDescriptor
{
  private static Logger log = Logger.getLogger(ESDescriptor.class.getName());
  int URLFlag;
  int URLLength = 0;
  String URLString;
  DecoderConfigDescriptor decoderConfigDescriptor;
  int dependsOnEsId;
  int esId;
  int oCREsId;
  int oCRstreamFlag;
  List<BaseDescriptor> otherDescriptors = new ArrayList();
  int remoteODFlag;
  SLConfigDescriptor slConfigDescriptor;
  int streamDependenceFlag;
  int streamPriority;

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    ESDescriptor localESDescriptor;
    do
    {
      return true;
      if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
        return false;
      localESDescriptor = (ESDescriptor)paramObject;
      if (this.URLFlag != localESDescriptor.URLFlag)
        return false;
      if (this.URLLength != localESDescriptor.URLLength)
        return false;
      if (this.dependsOnEsId != localESDescriptor.dependsOnEsId)
        return false;
      if (this.esId != localESDescriptor.esId)
        return false;
      if (this.oCREsId != localESDescriptor.oCREsId)
        return false;
      if (this.oCRstreamFlag != localESDescriptor.oCRstreamFlag)
        return false;
      if (this.remoteODFlag != localESDescriptor.remoteODFlag)
        return false;
      if (this.streamDependenceFlag != localESDescriptor.streamDependenceFlag)
        return false;
      if (this.streamPriority != localESDescriptor.streamPriority)
        return false;
      if (this.URLString != null)
        if (this.URLString.equals(localESDescriptor.URLString))
          break label176;
      do
        return false;
      while (localESDescriptor.URLString != null);
      if (this.decoderConfigDescriptor != null)
        label176: if (this.decoderConfigDescriptor.equals(localESDescriptor.decoderConfigDescriptor))
          break label206;
      do
        return false;
      while (localESDescriptor.decoderConfigDescriptor != null);
      if (this.otherDescriptors != null)
        label206: if (this.otherDescriptors.equals(localESDescriptor.otherDescriptors))
          break label236;
      do
        return false;
      while (localESDescriptor.otherDescriptors != null);
      label236: if (this.slConfigDescriptor == null)
        break;
    }
    while (this.slConfigDescriptor.equals(localESDescriptor.slConfigDescriptor));
    while (true)
    {
      return false;
      if (localESDescriptor.slConfigDescriptor == null);
    }
  }

  public int hashCode()
  {
    int i = 31 * (31 * (31 * (31 * (31 * (31 * this.esId + this.streamDependenceFlag) + this.URLFlag) + this.oCRstreamFlag) + this.streamPriority) + this.URLLength);
    int j;
    label63: int l;
    label110: int i1;
    if (this.URLString != null)
    {
      j = this.URLString.hashCode();
      int k = 31 * (31 * (31 * (31 * (i + j) + this.remoteODFlag) + this.dependsOnEsId) + this.oCREsId);
      if (this.decoderConfigDescriptor == null)
        break label179;
      l = this.decoderConfigDescriptor.hashCode();
      i1 = 31 * (k + l);
      if (this.slConfigDescriptor == null)
        break label185;
    }
    for (int i2 = this.slConfigDescriptor.hashCode(); ; i2 = 0)
    {
      int i3 = 31 * (i1 + i2);
      List localList = this.otherDescriptors;
      int i4 = 0;
      if (localList != null)
        i4 = this.otherDescriptors.hashCode();
      return i3 + i4;
      j = 0;
      break label63:
      label179: l = 0;
      label185: break label110:
    }
  }

  public void parseDetail(ByteBuffer paramByteBuffer)
    throws IOException
  {
    this.esId = IsoTypeReader.readUInt16(paramByteBuffer);
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    this.streamDependenceFlag = (i >>> 7);
    this.URLFlag = (0x1 & i >>> 6);
    this.oCRstreamFlag = (0x1 & i >>> 5);
    this.streamPriority = (i & 0x1F);
    if (this.streamDependenceFlag == 1)
      this.dependsOnEsId = IsoTypeReader.readUInt16(paramByteBuffer);
    if (this.URLFlag == 1)
    {
      this.URLLength = IsoTypeReader.readUInt8(paramByteBuffer);
      this.URLString = IsoTypeReader.readString(paramByteBuffer, this.URLLength);
    }
    if (this.oCRstreamFlag == 1)
      this.oCREsId = IsoTypeReader.readUInt16(paramByteBuffer);
    int j = 1 + (2 + (1 + getSizeBytes()));
    int k;
    label130: int i1;
    label152: int i5;
    long l3;
    Integer localInteger3;
    label267: label311: long l2;
    Integer localInteger2;
    label411: label455: label472: BaseDescriptor localBaseDescriptor1;
    long l1;
    Integer localInteger1;
    if (this.streamDependenceFlag == 1)
    {
      k = 2;
      int l = j + k;
      if (this.URLFlag != 1)
        break label620;
      i1 = 1 + this.URLLength;
      int i2 = i1 + l;
      int i3 = this.oCRstreamFlag;
      int i4 = 0;
      if (i3 == 1)
        i4 = 2;
      i5 = i2 + i4;
      int i6 = paramByteBuffer.position();
      if (getSize() > i5 + 2)
      {
        BaseDescriptor localBaseDescriptor3 = ObjectDescriptorFactory.createFrom(-1, paramByteBuffer);
        l3 = paramByteBuffer.position() - i6;
        Logger localLogger3 = log;
        StringBuilder localStringBuilder3 = new StringBuilder().append(localBaseDescriptor3).append(" - ESDescriptor1 read: ").append(l3).append(", size: ");
        if (localBaseDescriptor3 == null)
          break label626;
        localInteger3 = Integer.valueOf(localBaseDescriptor3.getSize());
        localLogger3.finer(localInteger3);
        if (localBaseDescriptor3 == null)
          break label632;
        int i11 = localBaseDescriptor3.getSize();
        paramByteBuffer.position(i6 + i11);
        i5 += i11;
        if (localBaseDescriptor3 instanceof DecoderConfigDescriptor)
          this.decoderConfigDescriptor = ((DecoderConfigDescriptor)localBaseDescriptor3);
      }
      int i7 = paramByteBuffer.position();
      if (getSize() <= i5 + 2)
        break label662;
      BaseDescriptor localBaseDescriptor2 = ObjectDescriptorFactory.createFrom(-1, paramByteBuffer);
      l2 = paramByteBuffer.position() - i7;
      Logger localLogger2 = log;
      StringBuilder localStringBuilder2 = new StringBuilder().append(localBaseDescriptor2).append(" - ESDescriptor2 read: ").append(l2).append(", size: ");
      if (localBaseDescriptor2 == null)
        break label644;
      localInteger2 = Integer.valueOf(localBaseDescriptor2.getSize());
      localLogger2.finer(localInteger2);
      if (localBaseDescriptor2 == null)
        break label650;
      int i10 = localBaseDescriptor2.getSize();
      paramByteBuffer.position(i7 + i10);
      i5 += i10;
      if (localBaseDescriptor2 instanceof SLConfigDescriptor)
        this.slConfigDescriptor = ((SLConfigDescriptor)localBaseDescriptor2);
      if (getSize() - i5 <= 2)
        return;
      int i8 = paramByteBuffer.position();
      localBaseDescriptor1 = ObjectDescriptorFactory.createFrom(-1, paramByteBuffer);
      l1 = paramByteBuffer.position() - i8;
      Logger localLogger1 = log;
      StringBuilder localStringBuilder1 = new StringBuilder().append(localBaseDescriptor1).append(" - ESDescriptor3 read: ").append(l1).append(", size: ");
      if (localBaseDescriptor1 == null)
        break label673;
      localInteger1 = Integer.valueOf(localBaseDescriptor1.getSize());
      label555: localLogger1.finer(localInteger1);
      if (localBaseDescriptor1 == null)
        break label679;
      int i9 = localBaseDescriptor1.getSize();
      paramByteBuffer.position(i8 + i9);
      i5 += i9;
    }
    while (true)
    {
      this.otherDescriptors.add(localBaseDescriptor1);
      break label472:
      k = 0;
      break label130:
      label620: i1 = 0;
      break label152:
      label626: localInteger3 = null;
      break label267:
      label632: i5 = (int)(l3 + i5);
      break label311:
      label644: localInteger2 = null;
      break label411:
      label650: i5 = (int)(l2 + i5);
      break label455:
      label662: log.warning("SLConfigDescriptor is missing!");
      break label472:
      label673: localInteger1 = null;
      break label555:
      label679: i5 = (int)(l1 + i5);
    }
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ESDescriptor");
    localStringBuilder.append("{esId=").append(this.esId);
    localStringBuilder.append(", streamDependenceFlag=").append(this.streamDependenceFlag);
    localStringBuilder.append(", URLFlag=").append(this.URLFlag);
    localStringBuilder.append(", oCRstreamFlag=").append(this.oCRstreamFlag);
    localStringBuilder.append(", streamPriority=").append(this.streamPriority);
    localStringBuilder.append(", URLLength=").append(this.URLLength);
    localStringBuilder.append(", URLString='").append(this.URLString).append('\'');
    localStringBuilder.append(", remoteODFlag=").append(this.remoteODFlag);
    localStringBuilder.append(", dependsOnEsId=").append(this.dependsOnEsId);
    localStringBuilder.append(", oCREsId=").append(this.oCREsId);
    localStringBuilder.append(", decoderConfigDescriptor=").append(this.decoderConfigDescriptor);
    localStringBuilder.append(", slConfigDescriptor=").append(this.slConfigDescriptor);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor
 * JD-Core Version:    0.5.4
 */