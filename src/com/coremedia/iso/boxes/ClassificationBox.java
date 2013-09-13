package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class ClassificationBox extends AbstractFullBox
{
  private String classificationEntity;
  private String classificationInfo;
  private int classificationTableIndex;
  private String language;

  public ClassificationBox()
  {
    super("clsf");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    byte[] arrayOfByte = new byte[4];
    paramByteBuffer.get(arrayOfByte);
    this.classificationEntity = IsoFile.bytesToFourCC(arrayOfByte);
    this.classificationTableIndex = IsoTypeReader.readUInt16(paramByteBuffer);
    this.language = IsoTypeReader.readIso639(paramByteBuffer);
    this.classificationInfo = IsoTypeReader.readString(paramByteBuffer);
  }

  public String getClassificationEntity()
  {
    return this.classificationEntity;
  }

  public String getClassificationInfo()
  {
    return this.classificationInfo;
  }

  public int getClassificationTableIndex()
  {
    return this.classificationTableIndex;
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.classificationEntity));
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.classificationTableIndex);
    IsoTypeWriter.writeIso639(paramByteBuffer, this.language);
    paramByteBuffer.put(Utf8.convert(this.classificationInfo));
    paramByteBuffer.put(0);
  }

  protected long getContentSize()
  {
    return 1 + (8 + Utf8.utf8StringLengthInBytes(this.classificationInfo));
  }

  public String getLanguage()
  {
    return this.language;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ClassificationBox[language=").append(getLanguage());
    localStringBuilder.append("classificationEntity=").append(getClassificationEntity());
    localStringBuilder.append(";classificationTableIndex=").append(getClassificationTableIndex());
    localStringBuilder.append(";language=").append(getLanguage());
    localStringBuilder.append(";classificationInfo=").append(getClassificationInfo());
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.ClassificationBox
 * JD-Core Version:    0.5.4
 */