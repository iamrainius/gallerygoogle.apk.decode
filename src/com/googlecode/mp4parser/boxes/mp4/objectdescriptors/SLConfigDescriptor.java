package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import java.io.IOException;
import java.nio.ByteBuffer;

@Descriptor(tags={6})
public class SLConfigDescriptor extends BaseDescriptor
{
  int predefined;

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    SLConfigDescriptor localSLConfigDescriptor;
    do
    {
      return true;
      if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
        return false;
      localSLConfigDescriptor = (SLConfigDescriptor)paramObject;
    }
    while (this.predefined == localSLConfigDescriptor.predefined);
    return false;
  }

  public int hashCode()
  {
    return this.predefined;
  }

  public void parseDetail(ByteBuffer paramByteBuffer)
    throws IOException
  {
    this.predefined = IsoTypeReader.readUInt8(paramByteBuffer);
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("SLConfigDescriptor");
    localStringBuilder.append("{predefined=").append(this.predefined);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.objectdescriptors.SLConfigDescriptor
 * JD-Core Version:    0.5.4
 */