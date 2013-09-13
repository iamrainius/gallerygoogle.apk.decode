package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import java.io.IOException;
import java.nio.ByteBuffer;

@Descriptor(tags={0})
public abstract class BaseDescriptor
{
  int sizeBytes;
  int sizeOfInstance;
  int tag;

  static
  {
    if (!BaseDescriptor.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public int getSize()
  {
    return 1 + this.sizeOfInstance + this.sizeBytes;
  }

  public int getSizeBytes()
  {
    return this.sizeBytes;
  }

  public int getSizeOfInstance()
  {
    return this.sizeOfInstance;
  }

  public final void parse(int paramInt, ByteBuffer paramByteBuffer)
    throws IOException
  {
    this.tag = paramInt;
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    int j = 0 + 1;
    for (this.sizeOfInstance = (i & 0x7F); i >>> 7 == 1; this.sizeOfInstance = (this.sizeOfInstance << 7 | i & 0x7F))
    {
      i = IsoTypeReader.readUInt8(paramByteBuffer);
      ++j;
    }
    this.sizeBytes = j;
    ByteBuffer localByteBuffer = paramByteBuffer.slice();
    localByteBuffer.limit(this.sizeOfInstance);
    parseDetail(localByteBuffer);
    assert (localByteBuffer.remaining() == 0) : (super.getClass().getSimpleName() + " has not been fully parsed");
    paramByteBuffer.position(paramByteBuffer.position() + this.sizeOfInstance);
  }

  public abstract void parseDetail(ByteBuffer paramByteBuffer)
    throws IOException;

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("BaseDescriptor");
    localStringBuilder.append("{tag=").append(this.tag);
    localStringBuilder.append(", sizeOfInstance=").append(this.sizeOfInstance);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor
 * JD-Core Version:    0.5.4
 */