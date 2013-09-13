package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.Hex;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

@Descriptor(tags={5})
public class DecoderSpecificInfo extends BaseDescriptor
{
  byte[] bytes;

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    DecoderSpecificInfo localDecoderSpecificInfo;
    do
    {
      return true;
      if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
        return false;
      localDecoderSpecificInfo = (DecoderSpecificInfo)paramObject;
    }
    while (Arrays.equals(this.bytes, localDecoderSpecificInfo.bytes));
    return false;
  }

  public int hashCode()
  {
    if (this.bytes != null)
      return Arrays.hashCode(this.bytes);
    return 0;
  }

  public void parseDetail(ByteBuffer paramByteBuffer)
    throws IOException
  {
    if (this.sizeOfInstance <= 0)
      return;
    this.bytes = new byte[this.sizeOfInstance];
    paramByteBuffer.get(this.bytes);
  }

  public String toString()
  {
    StringBuilder localStringBuilder1 = new StringBuilder();
    localStringBuilder1.append("DecoderSpecificInfo");
    StringBuilder localStringBuilder2 = localStringBuilder1.append("{bytes=");
    if (this.bytes == null);
    for (String str = "null"; ; str = Hex.encodeHex(this.bytes))
    {
      localStringBuilder2.append(str);
      localStringBuilder1.append('}');
      return localStringBuilder1.toString();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderSpecificInfo
 * JD-Core Version:    0.5.4
 */