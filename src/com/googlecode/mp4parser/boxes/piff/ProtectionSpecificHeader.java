package com.googlecode.mp4parser.boxes.piff;

import com.coremedia.iso.Hex;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProtectionSpecificHeader
{
  protected static Map<UUID, Class<? extends ProtectionSpecificHeader>> uuidRegistry = new HashMap();
  ByteBuffer data;

  static
  {
    uuidRegistry.put(UUID.fromString("9A04F079-9840-4286-AB92-E65BE0885F95"), PlayReadyHeader.class);
  }

  public static ProtectionSpecificHeader createFor(UUID paramUUID, ByteBuffer paramByteBuffer)
  {
    Class localClass = (Class)uuidRegistry.get(paramUUID);
    ProtectionSpecificHeader localProtectionSpecificHeader = new ProtectionSpecificHeader();
    if (localClass != null);
    try
    {
      localProtectionSpecificHeader = (ProtectionSpecificHeader)localClass.newInstance();
      localProtectionSpecificHeader.parse(paramByteBuffer);
      return localProtectionSpecificHeader;
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new RuntimeException(localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
  }

  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ProtectionSpecificHeader) && (super.getClass().equals(paramObject.getClass())))
      return this.data.equals(((ProtectionSpecificHeader)paramObject).data);
    return false;
  }

  public ByteBuffer getData()
  {
    return this.data;
  }

  public void parse(ByteBuffer paramByteBuffer)
  {
    this.data = paramByteBuffer;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ProtectionSpecificHeader");
    localStringBuilder.append("{data=");
    ByteBuffer localByteBuffer = getData().duplicate();
    localByteBuffer.rewind();
    byte[] arrayOfByte = new byte[localByteBuffer.limit()];
    localByteBuffer.get(arrayOfByte);
    localStringBuilder.append(Hex.encodeHex(arrayOfByte));
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.piff.ProtectionSpecificHeader
 * JD-Core Version:    0.5.4
 */