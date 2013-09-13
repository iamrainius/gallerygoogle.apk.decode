package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class UnknownDescriptor extends BaseDescriptor
{
  private static Logger log = Logger.getLogger(UnknownDescriptor.class.getName());
  private ByteBuffer data;

  public void parseDetail(ByteBuffer paramByteBuffer)
    throws IOException
  {
    this.data = ((ByteBuffer)paramByteBuffer.slice().limit(getSizeOfInstance()));
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UnknownDescriptor");
    localStringBuilder.append("{tag=").append(this.tag);
    localStringBuilder.append(", sizeOfInstance=").append(this.sizeOfInstance);
    localStringBuilder.append(", data=").append(this.data);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.objectdescriptors.UnknownDescriptor
 * JD-Core Version:    0.5.4
 */