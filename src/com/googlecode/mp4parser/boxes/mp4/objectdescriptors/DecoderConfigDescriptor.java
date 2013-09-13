package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoTypeReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Descriptor(tags={4})
public class DecoderConfigDescriptor extends BaseDescriptor
{
  private static Logger log = Logger.getLogger(DecoderConfigDescriptor.class.getName());
  AudioSpecificConfig audioSpecificInfo;
  long avgBitRate;
  int bufferSizeDB;
  byte[] configDescriptorDeadBytes;
  DecoderSpecificInfo decoderSpecificInfo;
  long maxBitRate;
  int objectTypeIndication;
  List<ProfileLevelIndicationDescriptor> profileLevelIndicationDescriptors = new ArrayList();
  int streamType;
  int upStream;

  public void parseDetail(ByteBuffer paramByteBuffer)
    throws IOException
  {
    this.objectTypeIndication = IsoTypeReader.readUInt8(paramByteBuffer);
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    this.streamType = (i >>> 2);
    this.upStream = (0x1 & i >> 1);
    this.bufferSizeDB = IsoTypeReader.readUInt24(paramByteBuffer);
    this.maxBitRate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.avgBitRate = IsoTypeReader.readUInt32(paramByteBuffer);
    Integer localInteger2;
    if (paramByteBuffer.remaining() > 2)
    {
      int j = paramByteBuffer.position();
      BaseDescriptor localBaseDescriptor2 = ObjectDescriptorFactory.createFrom(this.objectTypeIndication, paramByteBuffer);
      int k = paramByteBuffer.position() - j;
      Logger localLogger2 = log;
      StringBuilder localStringBuilder2 = new StringBuilder().append(localBaseDescriptor2).append(" - DecoderConfigDescr1 read: ").append(k).append(", size: ");
      if (localBaseDescriptor2 == null)
        break label346;
      localInteger2 = Integer.valueOf(localBaseDescriptor2.getSize());
      label135: localLogger2.finer(localInteger2);
      if (localBaseDescriptor2 != null)
      {
        int l = localBaseDescriptor2.getSize();
        if (k < l)
        {
          this.configDescriptorDeadBytes = new byte[l - k];
          paramByteBuffer.get(this.configDescriptorDeadBytes);
        }
      }
      if (localBaseDescriptor2 instanceof DecoderSpecificInfo)
        this.decoderSpecificInfo = ((DecoderSpecificInfo)localBaseDescriptor2);
      if (localBaseDescriptor2 instanceof AudioSpecificConfig)
        this.audioSpecificInfo = ((AudioSpecificConfig)localBaseDescriptor2);
    }
    if (paramByteBuffer.remaining() <= 2)
      label223: return;
    long l1 = paramByteBuffer.position();
    BaseDescriptor localBaseDescriptor1 = ObjectDescriptorFactory.createFrom(this.objectTypeIndication, paramByteBuffer);
    long l2 = paramByteBuffer.position() - l1;
    Logger localLogger1 = log;
    StringBuilder localStringBuilder1 = new StringBuilder().append(localBaseDescriptor1).append(" - DecoderConfigDescr2 read: ").append(l2).append(", size: ");
    if (localBaseDescriptor1 != null);
    for (Integer localInteger1 = Integer.valueOf(localBaseDescriptor1.getSize()); ; localInteger1 = null)
    {
      localLogger1.finer(localInteger1);
      if (localBaseDescriptor1 instanceof ProfileLevelIndicationDescriptor);
      this.profileLevelIndicationDescriptors.add((ProfileLevelIndicationDescriptor)localBaseDescriptor1);
      break label223:
      label346: localInteger2 = null;
      break label135:
    }
  }

  public String toString()
  {
    StringBuilder localStringBuilder1 = new StringBuilder();
    localStringBuilder1.append("DecoderConfigDescriptor");
    localStringBuilder1.append("{objectTypeIndication=").append(this.objectTypeIndication);
    localStringBuilder1.append(", streamType=").append(this.streamType);
    localStringBuilder1.append(", upStream=").append(this.upStream);
    localStringBuilder1.append(", bufferSizeDB=").append(this.bufferSizeDB);
    localStringBuilder1.append(", maxBitRate=").append(this.maxBitRate);
    localStringBuilder1.append(", avgBitRate=").append(this.avgBitRate);
    localStringBuilder1.append(", decoderSpecificInfo=").append(this.decoderSpecificInfo);
    localStringBuilder1.append(", audioSpecificInfo=").append(this.audioSpecificInfo);
    StringBuilder localStringBuilder2 = localStringBuilder1.append(", configDescriptorDeadBytes=");
    byte[] arrayOfByte;
    label148: StringBuilder localStringBuilder3;
    if (this.configDescriptorDeadBytes != null)
    {
      arrayOfByte = this.configDescriptorDeadBytes;
      localStringBuilder2.append(Hex.encodeHex(arrayOfByte));
      localStringBuilder3 = localStringBuilder1.append(", profileLevelIndicationDescriptors=");
      if (this.profileLevelIndicationDescriptors != null)
        break label206;
    }
    label206: List[] arrayOfList;
    for (String str = "null"; ; str = Arrays.asList(arrayOfList).toString())
    {
      localStringBuilder3.append(str);
      localStringBuilder1.append('}');
      return localStringBuilder1.toString();
      arrayOfByte = new byte[0];
      break label148:
      arrayOfList = new List[1];
      arrayOfList[0] = this.profileLevelIndicationDescriptors;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderConfigDescriptor
 * JD-Core Version:    0.5.4
 */