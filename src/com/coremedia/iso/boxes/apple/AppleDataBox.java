package com.coremedia.iso.boxes.apple;

import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public final class AppleDataBox extends AbstractFullBox
{
  private byte[] data;
  private byte[] fourBytes = new byte[4];

  public AppleDataBox()
  {
    super("data");
  }

  private static AppleDataBox getEmpty()
  {
    AppleDataBox localAppleDataBox = new AppleDataBox();
    localAppleDataBox.setVersion(0);
    localAppleDataBox.setFourBytes(new byte[4]);
    return localAppleDataBox;
  }

  public static AppleDataBox getStringAppleDataBox()
  {
    AppleDataBox localAppleDataBox = getEmpty();
    localAppleDataBox.setFlags(1);
    localAppleDataBox.setData(new byte[] { 0 });
    return localAppleDataBox;
  }

  public static AppleDataBox getUint16AppleDataBox()
  {
    AppleDataBox localAppleDataBox = new AppleDataBox();
    localAppleDataBox.setFlags(21);
    localAppleDataBox.setData(new byte[] { 0, 0 });
    return localAppleDataBox;
  }

  public static AppleDataBox getUint32AppleDataBox()
  {
    AppleDataBox localAppleDataBox = new AppleDataBox();
    localAppleDataBox.setFlags(21);
    localAppleDataBox.setData(new byte[] { 0, 0, 0, 0 });
    return localAppleDataBox;
  }

  public static AppleDataBox getUint8AppleDataBox()
  {
    AppleDataBox localAppleDataBox = new AppleDataBox();
    localAppleDataBox.setFlags(21);
    localAppleDataBox.setData(new byte[] { 0 });
    return localAppleDataBox;
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.fourBytes = new byte[4];
    paramByteBuffer.get(this.fourBytes);
    this.data = new byte[paramByteBuffer.remaining()];
    paramByteBuffer.get(this.data);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(this.fourBytes, 0, 4);
    paramByteBuffer.put(this.data);
  }

  protected long getContentSize()
  {
    return 8 + this.data.length;
  }

  public byte[] getData()
  {
    return this.data;
  }

  public void setData(byte[] paramArrayOfByte)
  {
    this.data = new byte[paramArrayOfByte.length];
    System.arraycopy(paramArrayOfByte, 0, this.data, 0, paramArrayOfByte.length);
  }

  public void setFourBytes(byte[] paramArrayOfByte)
  {
    System.arraycopy(paramArrayOfByte, 0, this.fourBytes, 0, 4);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.apple.AppleDataBox
 * JD-Core Version:    0.5.4
 */