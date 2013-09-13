package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.nio.ByteBuffer;

public class VideoMediaHeaderBox extends AbstractMediaHeaderBox
{
  private int graphicsmode = 0;
  private int[] opcolor = { 0, 0, 0 };

  public VideoMediaHeaderBox()
  {
    super("vmhd");
    setFlags(1);
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.graphicsmode = IsoTypeReader.readUInt16(paramByteBuffer);
    this.opcolor = new int[3];
    for (int i = 0; i < 3; ++i)
      this.opcolor[i] = IsoTypeReader.readUInt16(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.graphicsmode);
    int[] arrayOfInt = this.opcolor;
    int i = arrayOfInt.length;
    for (int j = 0; j < i; ++j)
      IsoTypeWriter.writeUInt16(paramByteBuffer, arrayOfInt[j]);
  }

  protected long getContentSize()
  {
    return 12L;
  }

  public int getGraphicsmode()
  {
    return this.graphicsmode;
  }

  public int[] getOpcolor()
  {
    return this.opcolor;
  }

  public String toString()
  {
    return "VideoMediaHeaderBox[graphicsmode=" + getGraphicsmode() + ";opcolor0=" + getOpcolor()[0] + ";opcolor1=" + getOpcolor()[1] + ";opcolor2=" + getOpcolor()[2] + "]";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.VideoMediaHeaderBox
 * JD-Core Version:    0.5.4
 */