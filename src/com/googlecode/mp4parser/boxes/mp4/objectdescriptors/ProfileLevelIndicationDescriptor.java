package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import java.io.IOException;
import java.nio.ByteBuffer;

@Descriptor(tags={20})
public class ProfileLevelIndicationDescriptor extends BaseDescriptor
{
  int profileLevelIndicationIndex;

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    ProfileLevelIndicationDescriptor localProfileLevelIndicationDescriptor;
    do
    {
      return true;
      if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
        return false;
      localProfileLevelIndicationDescriptor = (ProfileLevelIndicationDescriptor)paramObject;
    }
    while (this.profileLevelIndicationIndex == localProfileLevelIndicationDescriptor.profileLevelIndicationIndex);
    return false;
  }

  public int hashCode()
  {
    return this.profileLevelIndicationIndex;
  }

  public void parseDetail(ByteBuffer paramByteBuffer)
    throws IOException
  {
    this.profileLevelIndicationIndex = IsoTypeReader.readUInt8(paramByteBuffer);
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ProfileLevelIndicationDescriptor");
    localStringBuilder.append("{profileLevelIndicationIndex=").append(Integer.toHexString(this.profileLevelIndicationIndex));
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ProfileLevelIndicationDescriptor
 * JD-Core Version:    0.5.4
 */