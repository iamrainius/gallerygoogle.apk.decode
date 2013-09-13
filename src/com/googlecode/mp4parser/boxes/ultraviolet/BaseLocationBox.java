package com.googlecode.mp4parser.boxes.ultraviolet;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class BaseLocationBox extends AbstractFullBox
{
  String baseLocation = "";
  String purchaseLocation = "";

  public BaseLocationBox()
  {
    super("bloc");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.baseLocation = IsoTypeReader.readString(paramByteBuffer);
    paramByteBuffer.get(new byte[-1 + (256 - Utf8.utf8StringLengthInBytes(this.baseLocation))]);
    this.purchaseLocation = IsoTypeReader.readString(paramByteBuffer);
    paramByteBuffer.get(new byte[-1 + (256 - Utf8.utf8StringLengthInBytes(this.purchaseLocation))]);
    paramByteBuffer.get(new byte[512]);
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    BaseLocationBox localBaseLocationBox;
    do
    {
      return true;
      if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
        return false;
      localBaseLocationBox = (BaseLocationBox)paramObject;
      if (this.baseLocation != null)
        if (this.baseLocation.equals(localBaseLocationBox.baseLocation))
          break label59;
      do
        return false;
      while (localBaseLocationBox.baseLocation != null);
      label59: if (this.purchaseLocation == null)
        break;
    }
    while (this.purchaseLocation.equals(localBaseLocationBox.purchaseLocation));
    while (true)
    {
      return false;
      if (localBaseLocationBox.purchaseLocation == null);
    }
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(Utf8.convert(this.baseLocation));
    paramByteBuffer.put(new byte[256 - Utf8.utf8StringLengthInBytes(this.baseLocation)]);
    paramByteBuffer.put(Utf8.convert(this.purchaseLocation));
    paramByteBuffer.put(new byte[256 - Utf8.utf8StringLengthInBytes(this.purchaseLocation)]);
    paramByteBuffer.put(new byte[512]);
  }

  protected long getContentSize()
  {
    return 1028L;
  }

  public int hashCode()
  {
    if (this.baseLocation != null);
    for (int i = this.baseLocation.hashCode(); ; i = 0)
    {
      int j = i * 31;
      String str = this.purchaseLocation;
      int k = 0;
      if (str != null)
        k = this.purchaseLocation.hashCode();
      return j + k;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.ultraviolet.BaseLocationBox
 * JD-Core Version:    0.5.4
 */