package com.coremedia.iso.boxes.apple;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.Utf8;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.util.ByteBufferByteChannel;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractAppleMetaDataBox extends AbstractBox
  implements ContainerBox
{
  private static Logger LOG;
  AppleDataBox appleDataBox = new AppleDataBox();

  static
  {
    if (!AbstractAppleMetaDataBox.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      LOG = Logger.getLogger(AbstractAppleMetaDataBox.class.getName());
      return;
    }
  }

  public AbstractAppleMetaDataBox(String paramString)
  {
    super(paramString);
  }

  static long toLong(byte paramByte)
  {
    if (paramByte < 0)
      return paramByte + 256;
    return paramByte;
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    IsoTypeReader.readUInt32(paramByteBuffer);
    String str = IsoTypeReader.read4cc(paramByteBuffer);
    assert ("data".equals(str));
    this.appleDataBox = new AppleDataBox();
    try
    {
      this.appleDataBox.parse(new ByteBufferByteChannel(paramByteBuffer), null, paramByteBuffer.remaining(), null);
      this.appleDataBox.setParent(this);
      return;
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }

  public List<Box> getBoxes()
  {
    return Collections.singletonList(this.appleDataBox);
  }

  public <T extends Box> List<T> getBoxes(Class<T> paramClass)
  {
    return getBoxes(paramClass, false);
  }

  public <T extends Box> List<T> getBoxes(Class<T> paramClass, boolean paramBoolean)
  {
    if (paramClass.isAssignableFrom(this.appleDataBox.getClass()))
      return Collections.singletonList(this.appleDataBox);
    return null;
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    try
    {
      this.appleDataBox.getBox(new ByteBufferByteChannel(paramByteBuffer));
      return;
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException("The Channel is based on a ByteBuffer and therefore it shouldn't throw any exception");
    }
  }

  protected long getContentSize()
  {
    return this.appleDataBox.getSize();
  }

  public String getValue()
  {
    if (this.appleDataBox.getFlags() == 1)
      return Utf8.convert(this.appleDataBox.getData());
    if (this.appleDataBox.getFlags() == 21)
    {
      byte[] arrayOfByte = this.appleDataBox.getData();
      long l1 = 0L;
      int i = arrayOfByte.length;
      int j = arrayOfByte.length;
      int k = 0;
      int i1;
      for (int l = 1; k < j; l = i1)
      {
        long l2 = toLong(arrayOfByte[k]);
        i1 = l + 1;
        l1 += (l2 << 8 * (i - l));
        ++k;
      }
      return "" + l1;
    }
    if (this.appleDataBox.getFlags() == 0)
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = new BigInteger(this.appleDataBox.getData());
      return String.format("%x", arrayOfObject);
    }
    return "unknown";
  }

  public String toString()
  {
    return super.getClass().getSimpleName() + "{" + "appleDataBox=" + getValue() + '}';
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.apple.AbstractAppleMetaDataBox
 * JD-Core Version:    0.5.4
 */