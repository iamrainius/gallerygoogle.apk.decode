package com.googlecode.mp4parser.boxes.mp4;

import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ObjectDescriptorFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AbstractDescriptorBox extends AbstractFullBox
{
  private static Logger log = Logger.getLogger(AbstractDescriptorBox.class.getName());
  public ByteBuffer data;
  public BaseDescriptor descriptor;

  public AbstractDescriptorBox(String paramString)
  {
    super(paramString);
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.data = paramByteBuffer.slice();
    paramByteBuffer.position(paramByteBuffer.position() + paramByteBuffer.remaining());
    try
    {
      this.data.rewind();
      this.descriptor = ObjectDescriptorFactory.createFrom(-1, this.data);
      return;
    }
    catch (IOException localIOException)
    {
      log.log(Level.WARNING, "Error parsing ObjectDescriptor", localIOException);
      return;
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      log.log(Level.WARNING, "Error parsing ObjectDescriptor", localIndexOutOfBoundsException);
    }
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    this.data.rewind();
    paramByteBuffer.put(this.data);
  }

  protected long getContentSize()
  {
    return 4 + this.data.limit();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.AbstractDescriptorBox
 * JD-Core Version:    0.5.4
 */