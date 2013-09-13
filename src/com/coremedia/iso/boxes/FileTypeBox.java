package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractBox;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FileTypeBox extends AbstractBox
{
  private List<String> compatibleBrands = Collections.emptyList();
  private String majorBrand;
  private long minorVersion;

  public FileTypeBox()
  {
    super("ftyp");
  }

  public FileTypeBox(String paramString, long paramLong, List<String> paramList)
  {
    super("ftyp");
    this.majorBrand = paramString;
    this.minorVersion = paramLong;
    this.compatibleBrands = paramList;
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.majorBrand = IsoTypeReader.read4cc(paramByteBuffer);
    this.minorVersion = IsoTypeReader.readUInt32(paramByteBuffer);
    int i = paramByteBuffer.remaining() / 4;
    this.compatibleBrands = new LinkedList();
    for (int j = 0; j < i; ++j)
      this.compatibleBrands.add(IsoTypeReader.read4cc(paramByteBuffer));
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.majorBrand));
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.minorVersion);
    Iterator localIterator = this.compatibleBrands.iterator();
    while (localIterator.hasNext())
      paramByteBuffer.put(IsoFile.fourCCtoBytes((String)localIterator.next()));
  }

  protected long getContentSize()
  {
    return 8 + 4 * this.compatibleBrands.size();
  }

  public String getMajorBrand()
  {
    return this.majorBrand;
  }

  public long getMinorVersion()
  {
    return this.minorVersion;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("FileTypeBox[");
    localStringBuilder.append("majorBrand=").append(getMajorBrand());
    localStringBuilder.append(";");
    localStringBuilder.append("minorVersion=").append(getMinorVersion());
    Iterator localIterator = this.compatibleBrands.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localStringBuilder.append(";");
      localStringBuilder.append("compatibleBrand=").append(str);
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.FileTypeBox
 * JD-Core Version:    0.5.4
 */